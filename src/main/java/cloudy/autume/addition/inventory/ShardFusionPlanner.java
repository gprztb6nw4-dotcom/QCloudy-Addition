package cloudy.autume.addition.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Pure-Java, offline multi-step Shard route planner. */
public final class ShardFusionPlanner {
    private static final int MAX_DEPTH = 28;
    private static final double EPSILON = 1.0E-10;

    private final ShardFusionCatalog catalog;

    public ShardFusionPlanner(ShardFusionCatalog catalog) {
        this.catalog = catalog;
    }

    public Plan plan(String targetId, int quantity, Parameters parameters) {
        ShardFusionCatalog.Shard target = catalog.byId(targetId).orElse(null);
        if (target == null || quantity < 1) return Plan.impossible(target, quantity, "Invalid target");
        if (parameters.objective() == Objective.CHEAPEST && parameters.prices().isEmpty()) {
            return Plan.impossible(target, quantity, "Bazaar price provider unavailable");
        }

        int count = catalog.shards().size();
        double[][] costs = new double[MAX_DEPTH + 1][count];
        Choice[][] choices = new Choice[MAX_DEPTH + 1][count];
        for (double[] row : costs) Arrays.fill(row, Double.POSITIVE_INFINITY);

        for (ShardFusionCatalog.Shard shard : catalog.shards()) {
            Direct direct = direct(shard, parameters);
            costs[0][shard.ordinal()] = direct.cost();
            choices[0][shard.ordinal()] = new Choice(null, 0, direct.method(), 0);
        }

        double craftCost = craftCost(parameters);
        for (int depth = 1; depth <= MAX_DEPTH; depth++) {
            for (ShardFusionCatalog.Shard shard : catalog.shards()) {
                Direct direct = direct(shard, parameters);
                double best = direct.cost();
                Choice bestChoice = new Choice(null, 0, direct.method(), depth);
                for (ShardFusionCatalog.Recipe recipe : catalog.recipesForOutput(shard.id())) {
                    int output = outputCount(recipe, shard.id());
                    if (output <= 0) continue;
                    double left = costs[depth - 1][recipe.left().ordinal()];
                    double right = costs[depth - 1][recipe.right().ordinal()];
                    if (!Double.isFinite(left) || !Double.isFinite(right)) continue;
                    double expectedOutput = output * expectedReptileMultiplier(recipe, parameters);
                    double candidate = (recipe.inputCount() * (left + right) + craftCost) / expectedOutput;
                    if (candidate + EPSILON < best) {
                        best = candidate;
                        bestChoice = new Choice(recipe, output, Acquisition.FUSION, depth);
                    }
                }
                costs[depth][shard.ordinal()] = best;
                choices[depth][shard.ordinal()] = bestChoice;
            }
        }

        if (!Double.isFinite(costs[MAX_DEPTH][target.ordinal()])) {
            return Plan.impossible(target, quantity, "No complete route with the current rates and mode");
        }

        Map<String, Integer> available = new LinkedHashMap<>();
        if (parameters.useWarehouse()) available.putAll(parameters.warehouse());
        Accumulator totals = new Accumulator();
        Node root = build(target, quantity, MAX_DEPTH, parameters, costs, choices, available, totals,
                java.util.Set.of());
        if (containsUnavailable(root)) {
            return Plan.impossible(target, quantity,
                    "No acyclic complete route with the current rates and mode");
        }
        return new Plan(target, quantity, root, Map.copyOf(totals.hunt), Map.copyOf(totals.buy),
                Map.copyOf(totals.inventory), totals.crafts, totals.cost, true, "");
    }

    private Node build(ShardFusionCatalog.Shard shard, int requested, int depth, Parameters parameters,
                       double[][] costs, Choice[][] choices, Map<String, Integer> available,
                       Accumulator totals, java.util.Set<String> path) {
        totals.nodes++;
        int owned = Math.min(requested, available.getOrDefault(shard.id(), 0));
        if (owned > 0) {
            available.put(shard.id(), available.get(shard.id()) - owned);
            totals.inventory.merge(shard.id(), owned, Integer::sum);
        }
        int remaining = requested - owned;
        Choice choice = choices[Math.max(0, depth)][shard.ordinal()];
        boolean cycleOrLimit = path.contains(shard.id()) || totals.nodes > 4_096;
        if (remaining == 0) {
            return new Node(shard, requested, owned, Acquisition.INVENTORY, 0, 0,
                    null, List.of(), 0.0, List.of());
        }
        if (choice == null || choice.recipe() == null || depth <= 0 || cycleOrLimit) {
            Direct direct = direct(shard, parameters);
            if (!Double.isFinite(direct.cost())) {
                return new Node(shard, requested, owned, Acquisition.UNAVAILABLE, 0, 0,
                        null, List.of(), Double.POSITIVE_INFINITY, List.of());
            }
            Map<String, Integer> bucket = direct.method() == Acquisition.BUY ? totals.buy : totals.hunt;
            bucket.merge(shard.id(), remaining, Integer::sum);
            double cost = remaining * direct.cost();
            totals.cost += cost;
            return new Node(shard, requested, owned, direct.method(), 0, 0,
                    null, List.of(), cost, alternatives(shard, depth, parameters, costs));
        }

        ShardFusionCatalog.Recipe recipe = choice.recipe();
        int output = Math.max(1, choice.outputCount());
        int crafts = ceilDiv(remaining, output);
        int inputQuantity = safeMultiply(crafts, recipe.inputCount());
        java.util.Set<String> nextPath = new java.util.HashSet<>(path);
        nextPath.add(shard.id());
        nextPath = java.util.Set.copyOf(nextPath);
        Node left = build(recipe.left(), inputQuantity, depth - 1, parameters, costs, choices,
                available, totals, nextPath);
        Node right = build(recipe.right(), inputQuantity, depth - 1, parameters, costs, choices,
                available, totals, nextPath);
        totals.crafts += crafts;
        double ownCost = crafts * craftCost(parameters);
        totals.cost += ownCost;
        return new Node(shard, requested, owned, Acquisition.FUSION, crafts, output,
                recipe, List.of(left, right), ownCost + left.estimatedCost() + right.estimatedCost(),
                alternatives(shard, depth, parameters, costs));
    }

    private List<Alternative> alternatives(ShardFusionCatalog.Shard shard, int depth,
                                           Parameters parameters, double[][] costs) {
        List<Alternative> result = new ArrayList<>();
        Direct direct = direct(shard, parameters);
        if (Double.isFinite(direct.cost())) result.add(new Alternative(direct.method(), null, 1, direct.cost()));
        int inputDepth = Math.max(0, depth - 1);
        for (ShardFusionCatalog.Recipe recipe : catalog.recipesForOutput(shard.id())) {
            int output = outputCount(recipe, shard.id());
            if (output <= 0) continue;
            double left = costs[inputDepth][recipe.left().ordinal()];
            double right = costs[inputDepth][recipe.right().ordinal()];
            if (!Double.isFinite(left) || !Double.isFinite(right)) continue;
            double unit = (recipe.inputCount() * (left + right) + craftCost(parameters))
                    / (output * expectedReptileMultiplier(recipe, parameters));
            result.add(new Alternative(Acquisition.FUSION, recipe, output, unit));
        }
        result.sort(Comparator.comparingDouble(Alternative::unitCost));
        return List.copyOf(result.subList(0, Math.min(12, result.size())));
    }

    private Direct direct(ShardFusionCatalog.Shard shard, Parameters parameters) {
        double rate = parameters.customRates().getOrDefault(shard.id(),
                ShardAcquisitionRates.instance().rate(shard.id()));
        if ("L15".equals(shard.id()) && rate <= 0.0) {
            rate = kuudraRate(parameters.kuudraTier(), parameters.coinsPerHour(), parameters.kuudraSeconds());
        }
        if (rate > 0.0) rate *= 1.0 + Math.max(0, parameters.hunterFortune()) / 100.0;
        double huntHours = rate > 0.0 ? 1.0 / rate : Double.POSITIVE_INFINITY;

        if (parameters.mode() == Mode.IRONMAN) {
            return new Direct(huntHours, Acquisition.HUNT);
        }
        double price = parameters.prices().getOrDefault(shard.id(), Double.POSITIVE_INFINITY);
        if (parameters.objective() == Objective.CHEAPEST) {
            return new Direct(price, Acquisition.BUY);
        }
        double purchaseHours = parameters.coinsPerHour() > 0.0 && Double.isFinite(price)
                ? price / parameters.coinsPerHour() : Double.POSITIVE_INFINITY;
        return purchaseHours < huntHours
                ? new Direct(purchaseHours, Acquisition.BUY)
                : new Direct(huntHours, Acquisition.HUNT);
    }

    private static double craftCost(Parameters parameters) {
        double hours = Math.max(0, parameters.craftSeconds()) / 3600.0;
        if (parameters.objective() == Objective.CHEAPEST) {
            return parameters.coinsPerHour() > 0.0 ? hours * parameters.coinsPerHour() : 0.0;
        }
        return hours;
    }

    private static double expectedReptileMultiplier(ShardFusionCatalog.Recipe recipe, Parameters parameters) {
        if (!recipe.pureReptilePossible()) return 1.0;
        return 1.0 + 0.02 * Math.clamp(parameters.crocodileLevel(), 0, 10);
    }

    /** SkyShards' current Kuudra opportunity-time model for Kraken (L15). */
    public static double kuudraRate(KuudraTier tier, double coinsPerHour, int completionSeconds) {
        if (tier == null || tier == KuudraTier.NONE) return 0.0;
        int cost = switch (tier) {
            case T1 -> 155_000;
            case T2 -> 310_000;
            case T3 -> 582_000;
            case T4 -> 1_164_000;
            case T5 -> 2_328_000;
            case NONE -> 0;
        };
        double multiplier = switch (tier) {
            case T4 -> 1.25;
            case T5 -> 1.5;
            default -> 1.0;
        };
        int fallback = tier == KuudraTier.T5 ? 100 : 60;
        double runTime = completionSeconds > 0 ? completionSeconds : fallback;
        double keyTime = coinsPerHour > 0.0 ? cost / coinsPerHour * 3600.0 : 0.0;
        return multiplier * 3600.0 / (runTime + 25.0 + keyTime);
    }

    private static int outputCount(ShardFusionCatalog.Recipe recipe, String shardId) {
        int count = 0;
        for (ShardFusionCatalog.Output output : recipe.outputs()) {
            if (output.shard().id().equals(shardId)) count = Math.max(count, output.count());
        }
        return count;
    }

    private static int ceilDiv(int numerator, int denominator) {
        return (numerator + denominator - 1) / denominator;
    }

    private static int safeMultiply(int left, int right) {
        long value = (long) left * right;
        return (int) Math.min(1_000_000_000L, value);
    }

    private static boolean containsUnavailable(Node node) {
        if (node.method() == Acquisition.UNAVAILABLE) return true;
        return node.inputs().stream().anyMatch(ShardFusionPlanner::containsUnavailable);
    }

    public enum Mode { IRONMAN, NORMAL }

    public enum Objective { FASTEST, CHEAPEST }

    public enum Acquisition { INVENTORY, HUNT, BUY, FUSION, UNAVAILABLE }

    public enum KuudraTier { NONE, T1, T2, T3, T4, T5 }

    public record Parameters(Mode mode, Objective objective, Map<String, Double> customRates,
                             Map<String, Double> prices, Map<String, Integer> warehouse,
                             boolean useWarehouse, int hunterFortune, int crocodileLevel,
                             double coinsPerHour, int craftSeconds, KuudraTier kuudraTier,
                             int kuudraSeconds) {
        public Parameters {
            mode = mode == null ? Mode.IRONMAN : mode;
            objective = objective == null ? Objective.FASTEST : objective;
            if (mode == Mode.IRONMAN && objective == Objective.CHEAPEST) {
                objective = Objective.FASTEST;
            }
            customRates = customRates == null ? Map.of() : Map.copyOf(customRates);
            prices = prices == null ? Map.of() : Map.copyOf(prices);
            warehouse = warehouse == null ? Map.of() : Map.copyOf(warehouse);
            kuudraTier = kuudraTier == null ? KuudraTier.NONE : kuudraTier;
        }
    }

    public record Node(ShardFusionCatalog.Shard shard, int requested, int inventoryUsed,
                       Acquisition method, int crafts, int outputPerCraft,
                       ShardFusionCatalog.Recipe recipe, List<Node> inputs,
                       double estimatedCost, List<Alternative> alternatives) {
        public Node {
            inputs = List.copyOf(inputs);
            alternatives = List.copyOf(alternatives);
        }
    }

    public record Alternative(Acquisition method, ShardFusionCatalog.Recipe recipe,
                              int outputPerCraft, double unitCost) {
    }

    public record Plan(ShardFusionCatalog.Shard target, int requested, Node root,
                       Map<String, Integer> huntMaterials, Map<String, Integer> buyMaterials,
                       Map<String, Integer> inventoryUsed, int crafts, double estimatedCost,
                       boolean possible, String problem) {
        private static Plan impossible(ShardFusionCatalog.Shard target, int requested, String problem) {
            return new Plan(target, requested, null, Map.of(), Map.of(), Map.of(), 0,
                    Double.POSITIVE_INFINITY, false, problem);
        }
    }

    private record Choice(ShardFusionCatalog.Recipe recipe, int outputCount,
                          Acquisition method, int depth) {
    }

    private record Direct(double cost, Acquisition method) {
    }

    private static final class Accumulator {
        final Map<String, Integer> hunt = new LinkedHashMap<>();
        final Map<String, Integer> buy = new LinkedHashMap<>();
        final Map<String, Integer> inventory = new LinkedHashMap<>();
        int crafts;
        int nodes;
        double cost;
    }
}
