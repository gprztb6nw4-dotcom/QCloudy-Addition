#!/usr/bin/env python3
"""Build QCloudy's offline Attribute Shard fusion catalog.

The generated catalog intentionally combines four independently inspectable
sources:

* the current Hypixel SkyBlock Wiki rarity tables (names/skills/list presence),
* NotEnoughUpdates-REPO's client item identifiers,
* SkyShards' compact fusion properties, used as a reviewed data input, and
* a reviewed response from Hypixel's official Bazaar API.

The current Wiki rarity tables list 321 rows.  The official Bazaar snapshot
contains exactly 320 ``SHARD_*`` products; the extra Wiki row is the legacy
Rainbug (L49) entry and is excluded.  The generated catalog also stores the
current Wiki effect and acquisition summaries so the client UI can explain a
Shard without any run-time network access.

This script performs no network access.  Callers must supply reviewed source
snapshots and commit only the generated JSON to the mod resources.
"""

from __future__ import annotations

import argparse
import json
import re
from pathlib import Path
from typing import Any


RARITIES = ("Common", "Uncommon", "Rare", "Epic", "Legendary")
RARITY_ORDER = {name.upper(): index for index, name in enumerate(RARITIES)}
EXPECTED_SHARD_COUNT = 320
VALID_CATEGORIES = {"FOREST", "WATER", "COMBAT"}
WIKI_REVISIONS = {
    "common": 811543,
    "uncommon": 811544,
    "rare": 811562,
    "epic": 811532,
    "legendary": 811546,
    "fusion_rules": 810162,
}
WIKI_EXCLUDED_NAMES = {"Rainbug"}
WIKI_ID_CORRECTIONS = {"Goldolot": ("R86", "R92")}
REQUIRED_IDENTITIES = {
    "Anteater": ("R70", "SHARD_ANTEATER", "ATTRIBUTE_SHARD_FLOOR_FORTUNE"),
    "Zombuddy": ("R84", "SHARD_ZOMBUDDY", "ATTRIBUTE_SHARD_MUTATION_SERENDIPITY"),
    "Troodon": ("R86", "SHARD_TROODON", "ATTRIBUTE_SHARD_ESSENCE_OF_FOSSILS"),
    "Goldolot": ("R92", "SHARD_GOLDOLOT", "ATTRIBUTE_SHARD_ECHO_OF_BUCKETS"),
    "Ghost Crab": ("L38", "SHARD_GHOST_CRAB", "ATTRIBUTE_SHARD_TREASURE_FISHER"),
}
CHAMELEON_EXCLUSIONS = {"Chameleon", "Molthorn", "Galaxy Fish", "Bitbug"}
FORBIDDEN_IDENTITIES = {"L49", "Rainbug", "SHARD_RAINBUG", "ATTRIBUTE_SHARD_MIRACLE_CHANCE"}

COLOR_TONES = {
    "Black": "BLACK", "Dark Blue": "DARK_BLUE", "Dark Green": "DARK_GREEN",
    "Dark Aqua": "DARK_AQUA", "Dark Red": "DARK_RED", "Dark Purple": "DARK_PURPLE",
    "DarkPurple": "DARK_PURPLE", "Gold": "GOLD", "Gray": "GRAY",
    "Dark Gray": "DARK_GRAY", "Blue": "BLUE", "Green": "GREEN", "Aqua": "AQUA",
    "Red": "RED", "Light Purple": "LIGHT_PURPLE", "Pink": "LIGHT_PURPLE",
    "Yellow": "YELLOW", "White": "WHITE", "Orange": "GOLD",
}

STAT_TONES = {
    "Health": "RED", "Intelligence": "AQUA", "Strength": "RED", "Sweep": "DARK_GREEN",
    "Defense": "GREEN", "Fig Fortune": "GOLD", "Mangrove Fortune": "GOLD",
    "Block Fortune": "GOLD", "Fishing Speed": "AQUA", "Farming Fortune": "GOLD",
    "Sea Creature Chance": "DARK_AQUA", "Trophy Chance": "GOLD",
    "Heat Resistance": "RED", "Hunter Fortune": "LIGHT_PURPLE", "Mining Speed": "GOLD",
    "Mining Fortune": "GOLD", "Tracking": "LIGHT_PURPLE", "Ore Fortune": "GOLD",
    "Speed": "WHITE", "Helix Fortune": "GOLD", "Pressure Resistance": "BLUE",
    "Overbloom": "YELLOW", "Damage": "RED", "Foraging Fortune": "GOLD",
    "Crit Damage": "BLUE", "Treasure Chance": "GOLD", "Bonus Pest Chance": "DARK_GREEN",
    "Magic Find": "AQUA", "Respiration": "DARK_AQUA", "Mining Spread": "YELLOW",
    "Foraging Wisdom": "DARK_AQUA", "Pristine": "DARK_PURPLE",
    "Fishing Wisdom": "DARK_AQUA", "True Defense": "WHITE",
    "Hunting Wisdom": "DARK_AQUA", "Mining Wisdom": "DARK_AQUA",
    "Farming Wisdom": "DARK_AQUA", "Gemstone Spread": "YELLOW",
    "Attack Speed": "YELLOW", "Social Wisdom": "DARK_AQUA", "Cold Resistance": "AQUA",
    "Double Hook Chance": "BLUE", "Charm Chance": "AQUA",
    "Enchanting Wisdom": "DARK_AQUA", "Vitality": "DARK_RED", "Health Regen": "RED",
    "Taming Wisdom": "DARK_AQUA", "Combat Wisdom": "DARK_AQUA",
}

MOB_TYPE_TONES = {
    "Airborne": "GRAY", "Animal": "GREEN", "Aquatic": "BLUE", "Arcane": "DARK_PURPLE",
    "Arthropod": "DARK_RED", "Construct": "GRAY", "Critter": "GREEN", "Cubic": "GREEN",
    "Elusive": "LIGHT_PURPLE", "Ender": "DARK_PURPLE", "Frozen": "WHITE",
    "Glacial": "AQUA", "Humanoid": "YELLOW", "Infernal": "DARK_RED", "Magmatic": "RED",
    "Mythological": "DARK_GREEN", "Pest": "DARK_GREEN", "Shielded": "YELLOW",
    "Skeletal": "GRAY", "Spooky": "GOLD", "Subterranean": "GOLD",
    "Undead": "DARK_GREEN", "Wither": "DARK_GRAY", "Woodland": "DARK_GREEN",
}


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser()
    parser.add_argument("--wiki-dir", type=Path, required=True)
    parser.add_argument("--neu", type=Path, required=True)
    parser.add_argument("--skyshards", type=Path, required=True)
    parser.add_argument("--bazaar", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    return parser.parse_args()


def wiki_entries(directory: Path) -> dict[str, dict[str, str]]:
    entries: dict[str, dict[str, str]] = {}
    for rarity in RARITIES:
        path = directory / f"qcloudy_Attributes_List_{rarity}.json"
        source = json.loads(path.read_text(encoding="utf-8"))
        text = source["parse"]["wikitext"]
        blocks = re.findall(
            r"\{\{Attribute Table Entry\s*(.*?)(?=\n\}\})",
            text,
            flags=re.DOTALL,
        )
        for block in blocks:
            fields: dict[str, str] = {}
            current: str | None = None
            for line in block.splitlines():
                match = re.match(r"\|\s*([A-Za-z0-9_]+)\s*=\s*(.*)", line)
                if match:
                    current = match.group(1)
                    fields[current] = match.group(2).strip()
                elif current:
                    fields[current] += "\n" + line.strip()
            shard_id = fields.get("id", "").strip()
            name = fields.get("shard", "").strip()
            if shard_id and name:
                if shard_id in entries:
                    raise ValueError(f"duplicate Wiki shard id: {shard_id}")
                entries[shard_id] = fields
    return entries


def normalized_sources(
    raw_properties: dict[str, dict[str, Any]],
    raw_neu: list[dict[str, Any]],
) -> tuple[dict[str, dict[str, Any]], dict[str, dict[str, Any]]]:
    properties = {key: dict(value) for key, value in raw_properties.items()}
    rainbug = properties.pop("L49", None)
    if rainbug is None or rainbug.get("name") != "Rainbug":
        raise ValueError("expected the reviewed fusion snapshot to contain legacy Rainbug at L49")
    for name, (shard_id, _bazaar_id, _internal_id) in REQUIRED_IDENTITIES.items():
        value = properties.get(shard_id)
        if value is None or value.get("name") != name:
            raise ValueError(f"expected reviewed fusion identity {shard_id} = {name}")

    neu_by_name: dict[str, dict[str, Any]] = {}
    removed_rainbug = False
    for original in raw_neu:
        item = dict(original)
        if item.get("displayName") == "Rainbug":
            removed_rainbug = True
            continue
        name = str(item.get("displayName", ""))
        if not name or name in neu_by_name:
            raise ValueError(f"invalid or duplicate NEU shard name: {name}")
        neu_by_name[name] = item
    if not removed_rainbug:
        raise ValueError("expected reviewed client snapshot to contain legacy Rainbug")
    if len(properties) != EXPECTED_SHARD_COUNT or len(neu_by_name) != EXPECTED_SHARD_COUNT:
        raise ValueError(
            "reviewed fusion/client snapshots must normalize to exactly "
            f"{EXPECTED_SHARD_COUNT} entries"
        )
    return properties, neu_by_name


def bazaar_snapshot(path: Path) -> tuple[set[str], int]:
    root = json.loads(path.read_text(encoding="utf-8"))
    if root.get("success") is not True or not isinstance(root.get("products"), dict):
        raise ValueError("invalid Hypixel Bazaar API snapshot")
    shard_products = {str(key) for key in root["products"] if str(key).startswith("SHARD_")}
    if len(shard_products) != EXPECTED_SHARD_COUNT:
        raise ValueError(
            f"expected {EXPECTED_SHARD_COUNT} official SHARD_* products, got {len(shard_products)}"
        )
    last_updated = root.get("lastUpdated")
    if not isinstance(last_updated, int) or last_updated <= 0:
        raise ValueError("Bazaar snapshot is missing a valid lastUpdated timestamp")
    return shard_products, last_updated


def shard_sort_key(shard: dict[str, Any]) -> tuple[int, int, int]:
    match = re.fullmatch(r"([CUREL])(\d+)(?:-(\d+))?", shard["id"])
    if not match:
        raise ValueError(f"invalid shard id: {shard['id']}")
    rarity = {"C": 0, "U": 1, "R": 2, "E": 3, "L": 4}[match.group(1)]
    return rarity, int(match.group(2)), int(match.group(3) or 0)


def build_catalog(
    wiki: dict[str, dict[str, str]],
    properties: dict[str, dict[str, Any]],
    neu_by_name: dict[str, dict[str, Any]],
    bazaar_ids: set[str],
    bazaar_last_updated: int,
) -> dict[str, Any]:
    name_to_id = {value["name"]: key for key, value in properties.items()}
    if len(name_to_id) != len(properties):
        raise ValueError("fusion properties contain duplicate Shard names")
    wiki_by_name = {value["shard"]: value for value in wiki.values()}
    if len(wiki_by_name) != len(wiki):
        raise ValueError("Wiki snapshot contains duplicate Shard names")
    shards: list[dict[str, Any]] = []
    for shard_id, fusion in properties.items():
        name = str(fusion["name"])
        item = neu_by_name.get(name)
        if item is None:
            raise ValueError(f"missing NEU item data for {name}")
        if item.get("shardId") != shard_id:
            raise ValueError(
                f"ID mismatch for {name}: fusion={shard_id}, item={item.get('shardId')}"
            )
        rarity = str(fusion["rarity"]).upper()
        families = sorted({str(value).strip() for value in fusion.get("family", []) if str(value).strip()})
        wiki_row = wiki_by_name.get(name)
        if wiki_row is not None and wiki_row.get("id") != shard_id:
            correction = WIKI_ID_CORRECTIONS.get(name)
            actual = (wiki_row.get("id", ""), shard_id)
            if correction != actual:
                raise ValueError(f"unreviewed Wiki ID correction for {name}: {actual}")

        id_result_name = str(fusion.get("id_result", "")).strip()
        id_result = name_to_id.get(id_result_name, "")
        if id_result_name and not id_result:
            raise ValueError(f"unknown ID Fusion target for {name}: {id_result_name}")

        internal_name = str(item.get("internalName", ""))
        if internal_name.endswith(";1"):
            internal_name = internal_name[:-2]
        shard = {
            "id": shard_id,
            "name": name,
            "attributeName": str(item.get("abilityName", "")),
            "effect": rich_text(wiki_row.get("effect", "")) if wiki_row else [],
            "acquisition": acquisition_methods(wiki_row.get("hunting", "")) if wiki_row else [],
            "mobTypes": mob_types(wiki_row.get("effect", "")) if wiki_row else [],
            "rarity": rarity,
            "category": str(fusion.get("category", item.get("alignment", ""))).upper(),
            "families": families,
            "skill": clean_wiki_text(wiki_row.get("skill", "")) if wiki_row else "",
            "bazaarId": str(item.get("bazaarName", "")),
            "internalId": internal_name,
            "inputCount": input_count(name, families),
            "idResult": id_result,
            "specialLeft": str(fusion.get("input1", "")),
            "specialRight": str(fusion.get("input2", "")),
            "wikiListed": wiki_row is not None,
        }
        if not shard["acquisition"]:
            shard["acquisition"] = [{
                "text": "Acquisition method is not documented in the current Attributes table.",
                "kind": "UNKNOWN",
            }]
        shards.append(shard)

    shards.sort(key=shard_sort_key)
    validate(shards, wiki, bazaar_ids)
    return {
        "schemaVersion": 1,
        "dataVersion": "2026-08-10-320-shards-details",
        "verifiedAt": "2026-08-10",
        "sources": {
            "wikiAttributeFusion": "https://hypixelskyblock.minecraft.wiki/w/Attribute_Fusion",
            "wikiAttributes": "https://hypixelskyblock.minecraft.wiki/w/Attributes",
            "wikiRevisions": WIKI_REVISIONS,
            "skyShardsCommit": "9688031dbc4e726168ffceb0f44884ff26e6e728",
            "officialBazaar": "https://api.hypixel.net/v2/skyblock/bazaar",
            "officialBazaarLastUpdated": bazaar_last_updated,
            "clientSupplements": [],
            "wikiIdCorrections": {},
            "excludedLegacyEntries": ["L49 Rainbug"],
            "note": "Current Wiki 321-row tables are filtered through the official Bazaar 320-product allow-list; Rainbug is excluded and all remaining identities are cross-validated.",
        },
        "shards": shards,
}


def split_template_arguments(value: str) -> list[str]:
    parts: list[str] = []
    start = 0
    template_depth = 0
    link_depth = 0
    index = 0
    while index < len(value):
        if value.startswith("{{", index):
            template_depth += 1
            index += 2
            continue
        if value.startswith("}}", index) and template_depth:
            template_depth -= 1
            index += 2
            continue
        if value.startswith("[[", index):
            link_depth += 1
            index += 2
            continue
        if value.startswith("]]", index) and link_depth:
            link_depth -= 1
            index += 2
            continue
        if value[index] == "|" and not template_depth and not link_depth:
            parts.append(value[start:index])
            start = index + 1
        index += 1
    parts.append(value[start:])
    return parts


def matching_end(value: str, start: int, opening: str, closing: str) -> int:
    depth = 0
    index = start
    while index < len(value) - 1:
        if value.startswith(opening, index):
            depth += 1
            index += 2
            continue
        if value.startswith(closing, index):
            depth -= 1
            index += 2
            if depth == 0:
                return index
            continue
        index += 1
    return -1


def append_span(spans: list[dict[str, str]], text: str, tone: str = "TEXT") -> None:
    text = text.replace("&nbsp;", " ").replace("&NoBreak;", "")
    text = re.sub(r"&#x[0-9A-Fa-f]+;", "", text)
    text = text.replace("'''", "").replace("''", "")
    if not text:
        return
    if spans and spans[-1]["tone"] == tone:
        spans[-1]["text"] += text
    else:
        spans.append({"text": text, "tone": tone})


def rich_text(value: str, inherited_tone: str = "TEXT") -> list[dict[str, str]]:
    spans: list[dict[str, str]] = []
    index = 0
    while index < len(value):
        if value.startswith("{{", index):
            end = matching_end(value, index, "{{", "}}")
            if end < 0:
                append_span(spans, value[index:], inherited_tone)
                break
            args = split_template_arguments(value[index + 2:end - 2])
            template = args[0].strip() if args else ""
            values = [part.strip() for part in args[1:]]
            if template.lower() == "stat" and len(values) >= 2:
                stat, amount = values[0], values[1]
                append_span(spans, amount + " " + stat, STAT_TONES.get(stat, inherited_tone))
            elif template.lower() in {"mt", "mobtype", "rmt"} and values:
                mob_type = clean_wiki_text(values[0])
                append_span(spans, mob_type, MOB_TYPE_TONES.get(mob_type, inherited_tone))
            elif template in COLOR_TONES and values:
                for span in rich_text(values[-1], COLOR_TONES[template]):
                    append_span(spans, span["text"], span["tone"])
            elif template.lower() == "zone" and values:
                append_span(spans, clean_wiki_text(values[-1]), "AQUA")
            else:
                visible = values[-1] if values else template
                for span in rich_text(visible, inherited_tone):
                    append_span(spans, span["text"], span["tone"])
            index = end
            continue
        if value.startswith("[[", index):
            end = value.find("]]", index + 2)
            if end < 0:
                append_span(spans, value[index:], inherited_tone)
                break
            link = value[index + 2:end]
            visible = link.split("|", 1)[1] if "|" in link else link.split("#")[-1]
            for span in rich_text(visible, inherited_tone):
                append_span(spans, span["text"], span["tone"])
            index = end + 2
            continue
        next_template = value.find("{{", index)
        next_link = value.find("[[", index)
        candidates = [position for position in (next_template, next_link) if position >= 0]
        end = min(candidates) if candidates else len(value)
        append_span(spans, value[index:end], inherited_tone)
        index = end

    normalized: list[dict[str, str]] = []
    for span in spans:
        text = re.sub(r"\s+", " ", span["text"])
        if not normalized:
            text = text.lstrip()
        if text:
            append_span(normalized, text, span["tone"])
    if normalized:
        normalized[-1]["text"] = normalized[-1]["text"].rstrip()
    return [span for span in normalized if span["text"]]


def acquisition_kind(value: str) -> str:
    lower = value.lower()
    if lower.startswith("fusing"):
        return "FUSION"
    if "critter capsule" in lower or "black hole" in lower or "lasso" in lower or lower.startswith("catch"):
        return "CAPTURE"
    if lower.startswith("kill") or "killing" in lower:
        return "KILL"
    if "hunting trap" in lower:
        return "TRAP"
    if "fishing" in lower:
        return "FISHING"
    if "tree gift" in lower or "honeycomb" in lower or "honeyhive" in lower:
        return "TREE_GIFT"
    if "purchased" in lower or "shop" in lower:
        return "SHOP"
    if "kuudra" in lower or "catacombs" in lower or "chest" in lower:
        return "CHEST"
    if "floor drop" in lower:
        return "FLOOR_DROP"
    if "hunted" in lower:
        return "HUNTING"
    return "OTHER"


def acquisition_methods(value: str) -> list[dict[str, str]]:
    methods: list[dict[str, str]] = []
    for raw in re.split(r"(?:^|\n)\s*\*\s*", value.strip()):
        text = clean_wiki_text(raw)
        if text:
            methods.append({"text": text, "kind": acquisition_kind(text)})
    return methods


def mob_types(value: str) -> list[str]:
    result: list[str] = []
    for match in re.finditer(r"\{\{(?:mt|MobType|Rmt)\|([^|}]+)", value, flags=re.IGNORECASE):
        name = clean_wiki_text(match.group(1))
        if name and name not in result:
            result.append(name)
    return result


def clean_wiki_text(value: str) -> str:
    value = re.sub(r"\[\[([^]|]+)\|([^]]+)]]", r"\2", value)
    value = re.sub(r"\[\[([^]]+)]]", r"\1", value)
    value = "".join(span["text"] for span in rich_text(value)) if "{{" in value else value
    return re.sub(r"\s+", " ", value).strip()


def input_count(name: str, families: list[str]) -> int:
    if name == "Chameleon":
        return 1
    if any(value in {"Reptile", "Amphibian", "Elemental"} for value in families):
        return 2
    return 5


def validate(
    shards: list[dict[str, Any]],
    wiki: dict[str, dict[str, str]],
    official_bazaar_ids: set[str],
) -> None:
    if len(shards) != EXPECTED_SHARD_COUNT:
        raise ValueError(f"expected {EXPECTED_SHARD_COUNT} normalized shards, got {len(shards)}")
    ids = {shard["id"] for shard in shards}
    names = {shard["name"] for shard in shards}
    bazaar_ids = {shard["bazaarId"] for shard in shards}
    internal_ids = {shard["internalId"] for shard in shards}
    if any(len(values) != EXPECTED_SHARD_COUNT for values in (ids, names, bazaar_ids, internal_ids)):
        raise ValueError("Shard IDs, names, Bazaar IDs, and internal IDs must all be unique")
    if bazaar_ids != official_bazaar_ids:
        missing = sorted(official_bazaar_ids - bazaar_ids)
        extra = sorted(bazaar_ids - official_bazaar_ids)
        raise ValueError(f"catalog/Bazaar identity mismatch; missing={missing}, extra={extra}")
    if len(wiki) != 321:
        raise ValueError(f"expected the current Wiki snapshot to contain 321 rows, got {len(wiki)}")
    wiki_names = {value["shard"] for value in wiki.values()}
    if names - wiki_names:
        raise ValueError(f"unexpected Shards missing from Wiki: {sorted(names - wiki_names)}")
    if wiki_names - names != WIKI_EXCLUDED_NAMES:
        raise ValueError(f"unexpected Wiki-only Shards: {sorted(wiki_names - names)}")

    by_name = {shard["name"]: shard for shard in shards}
    for name, (shard_id, bazaar_id, internal_id) in REQUIRED_IDENTITIES.items():
        shard = by_name.get(name)
        actual = None if shard is None else (shard["id"], shard["bazaarId"], shard["internalId"])
        if actual != (shard_id, bazaar_id, internal_id):
            raise ValueError(f"incorrect reviewed identity for {name}: {actual}")
    if FORBIDDEN_IDENTITIES & (ids | names | bazaar_ids | internal_ids):
        raise ValueError("legacy Rainbug identity must not remain in the official 320 catalog")

    all_families = {family for shard in shards for family in shard["families"]}
    for shard in shards:
        required_text = ("id", "name", "attributeName", "rarity", "category", "bazaarId", "internalId")
        if any(not isinstance(shard[field], str) or not shard[field] for field in required_text):
            raise ValueError(f"missing required Shard field in {shard.get('id', '<unknown>')}")
        if shard["rarity"] not in RARITY_ORDER:
            raise ValueError(f"invalid rarity for {shard['id']}: {shard['rarity']}")
        if shard["category"] not in VALID_CATEGORIES:
            raise ValueError(f"invalid category for {shard['id']}: {shard['category']}")
        expected_prefix = {"COMMON": "C", "UNCOMMON": "U", "RARE": "R", "EPIC": "E", "LEGENDARY": "L"}[shard["rarity"]]
        if not shard["id"].startswith(expected_prefix):
            raise ValueError(f"rarity/ID mismatch for {shard['id']}")
        if shard["families"] != sorted(set(shard["families"])):
            raise ValueError(f"families must be sorted and unique for {shard['id']}")
        if shard["inputCount"] != input_count(shard["name"], shard["families"]):
            raise ValueError(f"incorrect Chameleon input count for {shard['id']}")
        if not shard["effect"] or any(not span.get("text") for span in shard["effect"]):
            raise ValueError(f"missing Wiki effect details for {shard['id']}")
        if not shard["acquisition"] or any(not method.get("text") for method in shard["acquisition"]):
            raise ValueError(f"missing acquisition details for {shard['id']}")
        valid_tones = {"TEXT", *COLOR_TONES.values()}
        if any(span.get("tone") not in valid_tones for span in shard["effect"]):
            raise ValueError(f"invalid effect color tone for {shard['id']}")
        target = shard["idResult"]
        if target and target not in ids:
            raise ValueError(f"unresolved ID Fusion target: {shard['id']} -> {target}")
        if target == shard["id"]:
            raise ValueError(f"ID Fusion target cannot equal its input: {shard['id']}")
        validate_special_selector(shard["specialLeft"], shard, names, all_families)
        validate_special_selector(shard["specialRight"], shard, names, all_families)

    if by_name["Chameleon"]["inputCount"] != 1:
        raise ValueError("Chameleon must consume one copy of each input")
    missing_exclusions = CHAMELEON_EXCLUSIONS - names
    if missing_exclusions:
        raise ValueError(f"missing Chameleon exclusion Shards: {sorted(missing_exclusions)}")


def validate_special_selector(
    expression: str,
    output: dict[str, Any],
    shard_names: set[str],
    families: set[str],
) -> None:
    if not expression:
        return
    for alternative in expression.split("|"):
        for raw_member in alternative.split("&"):
            member = raw_member.strip()
            name = member[:-6] if member.endswith(" Shard") else member
            rarity = member[:-1] if member.endswith("+") else member
            valid = (
                member in {"Any", "Mining Shards"}
                or rarity.upper() in RARITY_ORDER
                or member.upper() in VALID_CATEGORIES
                or member in families
                or name in shard_names
            )
            if not valid:
                raise ValueError(
                    f"unresolved Special Fusion selector for {output['id']}: {member}"
                )


def main() -> None:
    args = parse_args()
    wiki = wiki_entries(args.wiki_dir)
    raw_properties = json.loads(args.skyshards.read_text(encoding="utf-8"))
    neu_root = json.loads(args.neu.read_text(encoding="utf-8"))
    bazaar_ids, bazaar_last_updated = bazaar_snapshot(args.bazaar)
    properties, neu_by_name = normalized_sources(raw_properties, neu_root["attributes"])
    catalog = build_catalog(wiki, properties, neu_by_name, bazaar_ids, bazaar_last_updated)
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(
        json.dumps(catalog, indent=2, ensure_ascii=False) + "\n",
        encoding="utf-8",
    )
    print(f"wrote {len(catalog['shards'])} shards to {args.output}")


if __name__ == "__main__":
    main()
