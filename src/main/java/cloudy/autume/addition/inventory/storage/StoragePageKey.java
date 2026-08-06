package cloudy.autume.addition.inventory.storage;

import net.minecraft.client.Minecraft;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record StoragePageKey(int index) implements Comparable<StoragePageKey> {
    private static final Pattern ENDER_CHEST = Pattern.compile("^Ender Chest (?:✦ )?\\(([1-9])/[1-9]\\)$");
    private static final Pattern BACKPACK = Pattern.compile("^.+Backpack (?:✦ )?\\(Slot #([0-9]+)\\)$");

    public StoragePageKey {
        if (index < 0 || index >= 27) throw new IllegalArgumentException("Storage page index out of range: " + index);
    }

    public boolean enderChest() {
        return index < 9;
    }

    public int number() {
        return enderChest() ? index + 1 : index - 9 + 1;
    }

    public String defaultName() {
        return enderChest() ? "Ender Chest #" + number() : "Backpack #" + number();
    }

    public void navigate() {
        var connection = Minecraft.getInstance().getConnection();
        if (connection != null) connection.sendCommand(enderChest()
                ? "enderchest " + number() : "backpack " + number());
    }

    public static StoragePageKey fromOverviewSlot(int slot) {
        if (slot >= 9 && slot < 18) return new StoragePageKey(slot - 9);
        if (slot >= 27 && slot < 45) return new StoragePageKey(slot - 27 + 9);
        return null;
    }

    public static StoragePageKey fromTitle(String title) {
        Matcher ender = ENDER_CHEST.matcher(title);
        if (ender.matches()) return new StoragePageKey(Integer.parseInt(ender.group(1)) - 1);
        Matcher backpack = BACKPACK.matcher(title);
        if (backpack.matches()) return new StoragePageKey(Integer.parseInt(backpack.group(1)) - 1 + 9);
        return null;
    }

    @Override
    public int compareTo(StoragePageKey other) {
        return Integer.compare(index, other.index);
    }
}
