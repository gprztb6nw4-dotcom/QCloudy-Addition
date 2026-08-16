#!/usr/bin/env python3
"""Build QCA's offline Century Cake catalog from a reviewed NEU item snapshot."""

from __future__ import annotations

import argparse
import json
import re
from pathlib import Path


CAKES = {
    "EPOCH_CAKE_AQUA": ("Intelligence", "+5 Intelligence"),
    "EPOCH_CAKE_BLACK": ("Magic Find", "+1 Magic Find"),
    "EPOCH_CAKE_BLUE": ("Sea Creature Chance", "+1 Sea Creature Chance"),
    "EPOCH_CAKE_BROWN": ("Farming Fortune", "+5 Farming Fortune"),
    "EPOCH_CAKE_CYAN": ("Mining Fortune", "+5 Mining Fortune"),
    "EPOCH_CAKE_DARK_GREEN": ("Vitality", "+1 Vitality"),
    "EPOCH_CAKE_EXPIRED": ("Sweep", "+5 Sweep"),
    "EPOCH_CAKE_GRAY": ("True Defense", "+1 True Defense"),
    "EPOCH_CAKE_GREEN": ("Defense", "+3 Defense"),
    "EPOCH_CAKE_HEPHAESTUS": ("Tracking", "+1 Tracking"),
    "EPOCH_CAKE_MAGENTA": ("Rift Time", "+10 Rift Time"),
    "EPOCH_CAKE_ORANGE": ("Ferocity", "+2 Ferocity"),
    "EPOCH_CAKE_PINK": ("Health", "+10 Health"),
    "EPOCH_CAKE_PURPLE": ("Pet Luck", "+1 Pet Luck"),
    "EPOCH_CAKE_RED": ("Strength", "+2 Strength"),
    "EPOCH_CAKE_SEVEN_SEAS": ("Treasure Chance", "+1 Treasure Chance"),
    "EPOCH_CAKE_SILVER": ("Cold Resistance", "+1 Cold Resistance"),
    "EPOCH_CAKE_STARBORN": ("Hunter Fortune", "+1 Hunter Fortune"),
    "EPOCH_CAKE_WHITE": ("Foraging Fortune", "+5 Foraging Fortune"),
    "EPOCH_CAKE_YELLOW": ("Speed", "+10 Speed"),
}

FORMATTING = re.compile(r"§.")
TEXTURE = re.compile(r'Value:\"([^\"]+)\"')


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--neu-root", required=True, type=Path)
    parser.add_argument("--output", required=True, type=Path)
    args = parser.parse_args()

    rows = []
    for internal_id, (effect, bonus) in CAKES.items():
        path = args.neu_root / "repo" / "items" / f"{internal_id}.json"
        source = json.loads(path.read_text(encoding="utf-8"))
        match = TEXTURE.search(source.get("nbttag", ""))
        if match is None:
            raise SystemExit(f"missing texture profile: {internal_id}")
        name = FORMATTING.sub("", source["displayname"]).strip()
        rows.append({
            "internalId": internal_id,
            "name": name,
            "effect": effect,
            "bonus": bonus,
            "rarity": "UNCOMMON",
            "texture": match.group(1),
        })

    if len(rows) != 20 or len({row["effect"] for row in rows}) != 20:
        raise SystemExit("Century Cake catalog must contain 20 unique effects")
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps({"cakes": rows}, indent=2) + "\n", encoding="utf-8")


if __name__ == "__main__":
    main()
