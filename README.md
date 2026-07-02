# Shulker Reader

Shulker Reader is a Fabric mod that turns empty shulker boxes into compact
redstone data. Place an empty shulker box in the reader and connect a comparator
to read its color. Power the reader to access two additional color values or
detect a custom-named shulker box.

## Requirements

- Minecraft 26.1.2
- Fabric Loader 0.19.3 or newer
- Fabric API
- Java 25

## Signals

Without power, the comparator outputs:

| Shulker box | Signal | Shulker box | Signal |
| --- | ---: | --- | ---: |
| Undyed | 1 | Blue | 9 |
| Red | 2 | Purple | 10 |
| Orange | 3 | Magenta | 11 |
| Yellow | 4 | Pink | 12 |
| Lime | 5 | Brown | 13 |
| Green | 6 | Black | 14 |
| Cyan | 7 | Gray | 15 |
| Light Blue | 8 |  |  |

When powered, Light Gray outputs 1 and White outputs 2. A custom-named shulker
box outputs 15 when powered and 0 when unpowered. All other powered inputs
output 0.

Only empty shulker boxes are accepted. Hoppers and other automation can insert
or remove them.

## Recipe

Craft a Shulker Reader with four quartz, four redstone dust, and one shulker
shell:

```text
Q R Q
R S R
Q R Q
```

## Installation

Install Fabric Loader and Fabric API for Minecraft 26.1.2, then place the
Shulker Reader JAR in the `mods` folder.

Licensed under [CC0 1.0](LICENSE).
