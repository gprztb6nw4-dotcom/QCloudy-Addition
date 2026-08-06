#!/usr/bin/env python3
"""Generate readable ACA maps from the graph bundled in the local SkyHanni reference JAR.

The Dwarven Mines output intentionally collapses Y into one original, blocky topological
overview. Its region order and approximate footprint are derived from the public graph,
while its labels use Minecraft's bundled bitmap fonts. Glacite keeps its three Y bands.
No hidden blocks, entities, or live server data are used.
"""

from __future__ import annotations

import io
import json
import math
import re
import tarfile
import zipfile
from pathlib import Path

from PIL import Image, ImageDraw, ImageFont


ROOT = Path(__file__).resolve().parents[1]
SOURCE_JAR = ROOT / "SkyHanni-7.41.0-mc26.1.jar"
OUTPUT = ROOT / "src/main/resources/assets/autumecloudyaddition/textures/gui"
OUTPUT_SIZE = 200
SUPERSAMPLE = 4
SIZE = OUTPUT_SIZE * SUPERSAMPLE
MARGIN = 12 * SUPERSAMPLE
MINECRAFT_JAR = ROOT / ".gradle-user-home/caches/fabric-loom/minecraftMaven/net/minecraft/minecraft-merged-deobf/26.1.2/minecraft-merged-deobf-26.1.2.jar"
UNIFONT_ZIP = ROOT / ".gradle-user-home/caches/fabric-loom/assets/objects/cc/ccd5ac4767ce0a9c71d1dd62f2dc25449789b5dd"


class MinecraftBitmapFont:
    """Small offline renderer for the exact bitmap glyphs Minecraft ships."""

    def __init__(self):
        with zipfile.ZipFile(MINECRAFT_JAR) as archive:
            self.ascii = Image.open(io.BytesIO(
                archive.read("assets/minecraft/textures/font/ascii.png"))).convert("RGBA").getchannel("A")
        with zipfile.ZipFile(UNIFONT_ZIP) as archive:
            name = next(item for item in archive.namelist() if item.endswith(".hex"))
            self.unifont = {}
            for raw_line in archive.read(name).decode("ascii").splitlines():
                codepoint, bitmap = raw_line.split(":", 1)
                self.unifont[int(codepoint, 16)] = bitmap

    def glyph(self, character: str) -> tuple[Image.Image, int]:
        codepoint = ord(character)
        if codepoint < 256:
            cell_x = (codepoint % 16) * 8
            cell_y = (codepoint // 16) * 8
            mask = self.ascii.crop((cell_x, cell_y, cell_x + 8, cell_y + 8))
            box = mask.getbbox()
            if box is None:
                return mask, 4
            return mask, min(8, box[2] + 1)

        bitmap = self.unifont.get(codepoint)
        if bitmap is None:
            return Image.new("L", (8, 8)), 8
        row_width = len(bitmap) * 4 // 16
        raw = bytes.fromhex(bitmap)
        mask = Image.new("1", (row_width, 16))
        pixels = mask.load()
        for y in range(16):
            row = int.from_bytes(raw[y * row_width // 8:(y + 1) * row_width // 8], "big")
            for x in range(row_width):
                pixels[x, y] = 255 if row & (1 << (row_width - x - 1)) else 0
        mask = mask.convert("L").resize((max(1, row_width // 2), 8), Image.Resampling.NEAREST)
        return mask, mask.width + 1

    def measure(self, value: str) -> tuple[int, int]:
        lines = value.split("\n")
        widths = [sum(self.glyph(character)[1] for character in line) for line in lines]
        return max(widths, default=0), max(8, len(lines) * 9 - 1)

    def draw_centered(self, image: Image.Image, center: tuple[int, int], value: str,
                      fill: tuple[int, int, int, int], shadow=(0, 0, 0, 210)):
        width, height = self.measure(value)
        origin_y = center[1] - height // 2
        for line_index, line in enumerate(value.split("\n")):
            line_width = sum(self.glyph(character)[1] for character in line)
            cursor = center[0] - line_width // 2
            y = origin_y + line_index * 9
            for character in line:
                glyph, advance = self.glyph(character)
                if character != " ":
                    shadow_layer = Image.new("RGBA", glyph.size, shadow)
                    color_layer = Image.new("RGBA", glyph.size, fill)
                    image.paste(shadow_layer, (cursor + 1, y + 1), glyph)
                    image.paste(color_layer, (cursor, y), glyph)
                cursor += advance


DWARVEN_REGIONS = (
    # key, polygon, text center, material-derived fill, light/dark label
    ("village", ((70, 10), (126, 9), (141, 22), (136, 43), (112, 52), (80, 47), (65, 31)), (103, 29), (55, 170, 190, 245), False),
    ("upper", ((20, 29), (65, 28), (78, 47), (64, 67), (31, 70), (17, 54)), (46, 48), (164, 73, 139, 245), False),
    ("rampart", ((31, 71), (78, 49), (97, 59), (90, 86), (71, 101), (35, 95), (22, 82)), (59, 78), (104, 86, 156, 245), False),
    ("forge", ((79, 50), (121, 49), (128, 71), (117, 85), (89, 84), (91, 65)), (105, 66), (96, 101, 106, 245), False),
    ("lava", ((123, 49), (154, 59), (151, 83), (122, 83), (117, 71)), (137, 67), (193, 72, 43, 245), False),
    ("cliffside", ((80, 87), (120, 84), (151, 88), (148, 107), (111, 117), (73, 105)), (112, 99), (53, 155, 102, 245), False),
    ("reserve", ((13, 91), (39, 95), (72, 102), (68, 126), (43, 139), (16, 128), (8, 108)), (38, 113), (183, 134, 42, 245), True),
    ("goblin", ((16, 130), (44, 140), (72, 127), (88, 144), (75, 166), (43, 174), (12, 160), (7, 145)), (46, 151), (36, 112, 58, 245), False),
    ("royal_mines", ((154, 73), (184, 74), (190, 90), (185, 121), (162, 129), (149, 107)), (171, 99), (205, 166, 52, 245), True),
    ("mist", ((76, 108), (111, 117), (148, 108), (161, 128), (148, 149), (112, 157), (77, 146), (66, 125)), (113, 132), (183, 190, 193, 245), True),
    ("ice", ((75, 148), (111, 158), (149, 150), (155, 164), (134, 178), (91, 177), (70, 165)), (112, 164), (67, 164, 199, 245), False),
    ("palace", ((151, 132), (177, 132), (177, 142), (184, 142), (184, 181), (171, 181), (171, 190), (146, 190), (146, 181), (136, 181), (136, 159), (151, 159)), (160, 167), (112, 82, 119, 245), False),
)


def load_graph(name: str) -> dict[str, dict]:
    with zipfile.ZipFile(SOURCE_JAR) as archive:
        compressed = archive.read("assets/skyhanni/repo.tar.gz")
    with tarfile.open(fileobj=io.BytesIO(compressed), mode="r:gz") as archive:
        suffix = f"/constants/island_graphs/{name}.json"
        member = next(item for item in archive.getmembers() if item.name.endswith(suffix))
        extracted = archive.extractfile(member)
        if extracted is None:
            raise RuntimeError(f"Could not extract {name}")
        return json.load(extracted)


def position(node: dict) -> tuple[float, float, float]:
    return tuple(map(float, node["Position"].split(":")))


def strip_color(value: str) -> str:
    return re.sub(r"§.", "", value)


def font(size: int, bold: bool = False, cjk: bool = False):
    if cjk:
        candidates = [
            Path("/System/Library/Fonts/Hiragino Sans GB.ttc"),
            Path("/System/Library/Fonts/STHeiti Medium.ttc" if bold else "/System/Library/Fonts/STHeiti Light.ttc"),
        ]
    else:
        candidates = [
            Path("/System/Library/Fonts/Supplemental/Arial Bold.ttf" if bold else "/System/Library/Fonts/Supplemental/Arial.ttf"),
            Path("/System/Library/Fonts/SFNS.ttf"),
        ]
    for candidate in candidates:
        if candidate.exists():
            return ImageFont.truetype(str(candidate), size=size * SUPERSAMPLE)
    return ImageFont.load_default()


def transform(x: float, z: float, bounds: tuple[float, float, float, float]) -> tuple[int, int]:
    min_x, max_x, min_z, max_z = bounds
    px = MARGIN + (x - min_x) / (max_x - min_x) * (SIZE - MARGIN * 2)
    py = MARGIN + (z - min_z) / (max_z - min_z) * (SIZE - MARGIN * 2)
    return round(px), round(py)


def base_image() -> tuple[Image.Image, ImageDraw.ImageDraw]:
    # Keep the texture background transparent so the runtime HUD opacity setting
    # controls the whole panel. Borders are also drawn at runtime.
    image = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
    return image, ImageDraw.Draw(image, "RGBA")


def save(image: Image.Image, path: Path):
    image.resize((OUTPUT_SIZE, OUTPUT_SIZE), Image.Resampling.LANCZOS).save(path, optimize=True)


def draw_edges(draw, graph, bounds, selected_y=None):
    seen = set()
    for key, node in graph.items():
        if "Position" not in node:
            continue
        x1, y1, z1 = position(node)
        for other_key in node.get("Neighbours", {}):
            edge = tuple(sorted((key, other_key)))
            if edge in seen or other_key not in graph or "Position" not in graph[other_key]:
                continue
            seen.add(edge)
            x2, y2, z2 = position(graph[other_key])
            if selected_y is None:
                brightness = int(115 + MathLike.clamp((y1 + y2) / 2, 80, 235) / 235 * 85)
                color = (48, brightness, 220, 95)
                width = SUPERSAMPLE
            else:
                low, high = selected_y
                midpoint = (y1 + y2) / 2
                active = low <= midpoint <= high
                color = (82, 221, 255, 190) if active else (52, 91, 112, 34)
                width = 2 * SUPERSAMPLE if active else SUPERSAMPLE
            draw.line((*transform(x1, z1, bounds), *transform(x2, z2, bounds)), fill=color, width=width)


class MathLike:
    @staticmethod
    def clamp(value, lower, upper):
        return max(lower, min(upper, value))


def area_points(graph, wanted):
    points = {name: [] for name in wanted}
    for node in graph.values():
        name = strip_color(node.get("Name", ""))
        if name in points and "Position" in node:
            points[name].append(position(node))
    return {name: values for name, values in points.items() if values}


def draw_labels(draw, graph, bounds, labels, cjk: bool = False):
    points = area_points(graph, labels)
    label_font = font(12 if cjk else 11, True, cjk)
    placed_boxes = []
    for source_name, display_name in labels.items():
        if source_name not in points:
            continue
        x = sum(point[0] for point in points[source_name]) / len(points[source_name])
        z = sum(point[2] for point in points[source_name]) / len(points[source_name])
        px, py = transform(x, z, bounds)
        box = draw.textbbox((0, 0), display_name, font=label_font, stroke_width=SUPERSAMPLE)
        width = box[2] - box[0]
        height = box[3] - box[1]
        inset = 3 * SUPERSAMPLE
        pad_x = 4 * SUPERSAMPLE
        pad_y = 2 * SUPERSAMPLE
        preferred_x = px - width / 2
        preferred_y = py - height / 2

        # Named Glacite locations are close together near the north entrance. Resolve
        # their label cards as rectangles instead of drawing every card at its centroid.
        # The deterministic nearest-free search preserves geographical placement while
        # guaranteeing a visible gap at the final 200 px texture size.
        candidates = [(0, 0)]
        step = 4 * SUPERSAMPLE
        for radius in range(1, 17):
            for dx in range(-radius, radius + 1):
                for dy in range(-radius, radius + 1):
                    if max(abs(dx), abs(dy)) == radius:
                        candidates.append((dx * step, dy * step))
        candidates.sort(key=lambda offset: (offset[0] * offset[0] + offset[1] * offset[1],
                                            abs(offset[1]), abs(offset[0])))

        chosen = None
        gap = 3 * SUPERSAMPLE
        for offset_x, offset_y in candidates:
            candidate_x = int(MathLike.clamp(preferred_x + offset_x,
                                             inset + pad_x, SIZE - width - inset - pad_x))
            candidate_y = int(MathLike.clamp(preferred_y + offset_y,
                                             inset + pad_y, SIZE - height - inset - pad_y))
            candidate_box = (candidate_x - pad_x, candidate_y - pad_y,
                             candidate_x + width + pad_x, candidate_y + height + pad_y)
            if all(candidate_box[2] + gap <= other[0]
                   or other[2] + gap <= candidate_box[0]
                   or candidate_box[3] + gap <= other[1]
                   or other[3] + gap <= candidate_box[1]
                   for other in placed_boxes):
                chosen = (candidate_x, candidate_y, candidate_box)
                break
        if chosen is None:
            candidate_x = int(MathLike.clamp(preferred_x, inset + pad_x,
                                             SIZE - width - inset - pad_x))
            candidate_y = int(MathLike.clamp(preferred_y, inset + pad_y,
                                             SIZE - height - inset - pad_y))
            chosen = (candidate_x, candidate_y,
                      (candidate_x - pad_x, candidate_y - pad_y,
                       candidate_x + width + pad_x, candidate_y + height + pad_y))
        px, py, placed_box = chosen
        placed_boxes.append(placed_box)
        draw.rounded_rectangle((px - pad_x, py - pad_y, px + width + pad_x, py + height + pad_y),
                               radius=4 * SUPERSAMPLE, fill=(2, 7, 12, 220),
                               outline=(100, 222, 255, 190), width=SUPERSAMPLE)
        draw.text((px, py), display_name, font=label_font, fill=(238, 246, 250, 235),
                  stroke_width=SUPERSAMPLE, stroke_fill=(0, 0, 0, 220))


def convex_hull(points):
    points = sorted(set(points))
    if len(points) <= 1:
        return points

    def cross(origin, a, b):
        return (a[0] - origin[0]) * (b[1] - origin[1]) - (a[1] - origin[1]) * (b[0] - origin[0])

    lower = []
    for point in points:
        while len(lower) >= 2 and cross(lower[-2], lower[-1], point) <= 0:
            lower.pop()
        lower.append(point)
    upper = []
    for point in reversed(points):
        while len(upper) >= 2 and cross(upper[-2], upper[-1], point) <= 0:
            upper.pop()
        upper.append(point)
    return lower[:-1] + upper[:-1]


def expanded_hull(points, minimum_width=24, minimum_height=20, padding=6):
    """Return a padded hull in texture coordinates, keeping its real X/Z footprint."""
    left = min(point[0] for point in points)
    right = max(point[0] for point in points)
    top = min(point[1] for point in points)
    bottom = max(point[1] for point in points)
    width = max(right - left, minimum_width * SUPERSAMPLE)
    height = max(bottom - top, minimum_height * SUPERSAMPLE)
    center_x = (left + right) / 2
    center_y = (top + bottom) / 2
    scale_x = (width + padding * 2 * SUPERSAMPLE) / max(1, right - left)
    scale_y = (height + padding * 2 * SUPERSAMPLE) / max(1, bottom - top)
    expanded = []
    for x, y in points:
        expanded.append((round(center_x + (x - center_x) * scale_x),
                         round(center_y + (y - center_y) * scale_y)))
    if len(expanded) < 3:
        half_w = width / 2 + padding * SUPERSAMPLE
        half_h = height / 2 + padding * SUPERSAMPLE
        return [(round(center_x - half_w), round(center_y - half_h)),
                (round(center_x + half_w), round(center_y - half_h)),
                (round(center_x + half_w), round(center_y + half_h)),
                (round(center_x - half_w), round(center_y + half_h))]
    return convex_hull(expanded)


def multiline_centered_text(draw, center, value, label_font, fill):
    spacing = 1 * SUPERSAMPLE
    box = draw.multiline_textbbox((0, 0), value, font=label_font, spacing=spacing,
                                  align="center", stroke_width=SUPERSAMPLE)
    width = box[2] - box[0]
    height = box[3] - box[1]
    left = center[0] - width / 2
    top = center[1] - height / 2
    pad_x = 3 * SUPERSAMPLE
    pad_y = 2 * SUPERSAMPLE
    draw.rounded_rectangle((left - pad_x, top - pad_y, left + width + pad_x, top + height + pad_y),
                           radius=3 * SUPERSAMPLE, fill=(3, 8, 13, 218))
    draw.multiline_text((left, top), value,
                        font=label_font, spacing=spacing, align="center", fill=fill,
                        stroke_width=SUPERSAMPLE, stroke_fill=(0, 0, 0, 230))


def draw_dwarven_regions(draw, graph, bounds, regions, cjk=False):
    label_font = font(9 if cjk else 8, True, cjk)
    palette = [
        ((32, 116, 147, 150), (104, 224, 255, 245)),
        ((50, 88, 142, 150), (122, 180, 255, 245)),
        ((61, 119, 82, 150), (123, 231, 158, 245)),
        ((139, 87, 35, 150), (255, 181, 91, 245)),
        ((104, 65, 135, 150), (213, 145, 255, 245)),
        ((125, 61, 77, 150), (255, 130, 153, 245)),
        ((54, 114, 123, 150), (113, 228, 231, 245)),
    ]
    all_points = area_points(graph, {name: name for region in regions for name in region[0]})
    for index, (source_names, display_name, label_offset) in enumerate(regions):
        world_points = [point for name in source_names for point in all_points.get(name, [])]
        if not world_points:
            continue
        map_points = [transform(x, z, bounds) for x, _, z in world_points]
        hull = expanded_hull(convex_hull(map_points))
        fill, outline = palette[index % len(palette)]
        draw.polygon(hull, fill=fill)
        draw.line(hull + [hull[0]], fill=outline, width=2 * SUPERSAMPLE, joint="curve")
        center = (sum(point[0] for point in map_points) / len(map_points),
                  sum(point[1] for point in map_points) / len(map_points))
        center = (center[0] + label_offset[0] * SUPERSAMPLE,
                  center[1] + label_offset[1] * SUPERSAMPLE)
        multiline_centered_text(draw, center, display_name, label_font, (247, 252, 255, 255))


def generate_dwarven(labels: dict[str, str], suffix: str = ""):
    """Draw a clean, single-layer schematic rather than a dense route graph."""
    image = Image.new("RGBA", (OUTPUT_SIZE, OUTPUT_SIZE), (0, 0, 0, 0))
    draw = ImageDraw.Draw(image, "RGBA")
    bitmap_font = MinecraftBitmapFont()

    # A few dark passages establish topology without pretending to show every tunnel.
    connections = (
        ((102, 39), (103, 57)), ((67, 55), (89, 65)), ((72, 88), (89, 76)),
        ((121, 70), (135, 70)), ((111, 85), (112, 94)), ((69, 113), (80, 126)),
        ((66, 139), (78, 142)), ((146, 101), (159, 101)), ((112, 148), (112, 158)),
        ((153, 126), (160, 145)),
    )
    for start, end in connections:
        draw.line((*start, *end), fill=(12, 16, 18, 255), width=8)
        draw.line((*start, *end), fill=(69, 75, 78, 255), width=3)

    for key, polygon, center, fill, dark_text in DWARVEN_REGIONS:
        draw.polygon(polygon, fill=fill)
        draw.line((*polygon, polygon[0]), fill=(9, 12, 14, 255), width=3, joint="curve")
        # A subtle inner edge gives the blocks depth while keeping them flat and readable.
        draw.line((polygon[0], polygon[1]), fill=(255, 255, 255, 45), width=1)
        label_color = (25, 30, 33, 255) if dark_text else (244, 248, 250, 255)
        bitmap_font.draw_centered(image, center, labels[key], label_color)

    image.save(OUTPUT / f"dwarven_mines{suffix}.png", optimize=True)


def generate_glacite(labels: dict[str, str], suffix: str = "", cjk: bool = False):
    graph = load_graph("GLACITE_TUNNELS")
    bounds = (-131, 130, 181, 485)
    layers = {
        "low": (-math.inf, 126),
        "middle": (126, 143),
        "high": (143, math.inf),
    }
    for name, y_range in layers.items():
        image, draw = base_image()
        draw_edges(draw, graph, bounds, y_range)
        draw_labels(draw, graph, bounds, labels, cjk)
        save(image, OUTPUT / f"glacite_tunnels_{name}{suffix}.png")


def generate_arrow():
    image = Image.new("RGBA", (12, 12), (0, 0, 0, 0))
    draw = ImageDraw.Draw(image, "RGBA")
    draw.polygon(((6, 0), (11, 11), (6, 8), (1, 11)), fill=(255, 44, 64, 255),
                 outline=(255, 235, 238, 255))
    image.save(OUTPUT / "player_arrow.png", optimize=True)


def generate_icon():
    image = Image.new("RGBA", (128, 128), (5, 13, 22, 255))
    draw = ImageDraw.Draw(image, "RGBA")
    draw.rounded_rectangle((7, 7, 121, 121), radius=24, fill=(10, 29, 45, 255),
                           outline=(86, 215, 255, 255), width=5)
    draw.ellipse((28, 49, 78, 91), fill=(207, 241, 255, 255))
    draw.ellipse((51, 30, 103, 91), fill=(232, 248, 255, 255))
    draw.rectangle((29, 68, 101, 91), fill=(222, 246, 255, 255))
    draw.polygon(((67, 49), (100, 102), (84, 102), (77, 88), (55, 88), (48, 102), (32, 102)),
                 fill=(255, 75, 100, 255))
    image.save(ROOT / "src/main/resources/assets/autumecloudyaddition/icon.png", optimize=True)


def generate_color_wheel():
    image = Image.new("RGBA", (160, 160), (0, 0, 0, 0))
    pixels = image.load()
    center = 79.5
    radius = 77.5
    import colorsys
    for y in range(160):
        for x in range(160):
            dx = x - center
            dy = y - center
            saturation = math.sqrt(dx * dx + dy * dy) / radius
            if saturation > 1.0:
                continue
            hue = (math.atan2(dy, dx) / (2 * math.pi)) % 1.0
            red, green, blue = colorsys.hsv_to_rgb(hue, saturation, 1.0)
            pixels[x, y] = (round(red * 255), round(green * 255), round(blue * 255), 255)
    image.save(OUTPUT / "color_wheel.png", optimize=True)


def main():
    OUTPUT.mkdir(parents=True, exist_ok=True)
    generate_dwarven({
        "village": "Village",
        "upper": "Upper\nMines",
        "rampart": "Rampart\nQuarry",
        "forge": "Forge",
        "lava": "Lava\nSprings",
        "cliffside": "Cliffside",
        "reserve": "Far\nReserve",
        "goblin": "Goblin\nBurrows",
        "royal_mines": "Royal\nMines",
        "mist": "The Mist",
        "ice": "Ice Wall",
        "palace": "Royal\nPalace",
    })
    generate_glacite({
        "Dwarven Base Camp": "Base Camp",
        "Great Glacite Lake": "Lake",
        "Fossil Research Center": "Fossil Center",
        "Rail System": "Rail",
    })
    generate_arrow()
    generate_icon()
    generate_color_wheel()


if __name__ == "__main__":
    main()
