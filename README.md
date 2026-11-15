# Cartography [WIP]

Cartography is an ultra-configurable, vanilla-friendly map mod for Minecraft 1.21.1 on NeoForge. It focuses on visual immersion, balanced navigation, and compatibility with moving contraptions.

## What Cartography does

Cartography scans world geometry into layered data and stores it in compressed NBT files. It is designed to run cleanly on servers and clients without bloat, giving server owners and players full control over navigation rules, map visuals, and radar behavior.

### Layered world scanning

The backend scans chunk columns from the highest filled block down to the minimum section Y coordinate. It classifies blocks into five distinct strata:

- Terrain: Solid, light-blocking blocks that define the landscape.
- Water: Fluid blocks with tracked depth values up to 255 levels.
- Vegetation: Leaves, crops, and flowers identified through block tags.
- Decorations: Non-occluding surface blocks and structures.
- Biomes: Biome identifiers sampled per column from noise data.

Each layer maintains its own palette and block index arrays, with optional height tracking for topographic rendering.

### Moving contraptions and sub-levels

Standard map mods assume a static world grid. Cartography includes native compatibility with Sable, Create, and Create Aeronautics to map moving structures:

- Scans plot chunks within active sub-levels.
- Tracks contraption position vectors and scale transforms in real time.
- Caches sub-level chunk data in dedicated world folders.
- Automatically flushes and removes temporary data when a sub-level container is deleted.

### Waypoints and pins

The waypoint engine stores markers per dimension with support for private, public, and player-shared visibility:

- Death waypoints: Automatically records the coordinates of a player death using a skeleton skull icon. The marker expires after a configurable duration.
- Mount waypoints: Drops a marker when mounting a mob, boat, or minecart, using the vehicle item as the icon. It includes a blacklist for entity types or tags, and removes the marker after a set timeout.
- Respawn waypoints: Creates a waypoint whenever a player sets their spawn at a bed or respawn anchor.
- Pins: A contextual ping tool to mark an item, block, or place in the world. New pins can be added and configured through datapacks.

### Dynamic map appearance

The visual engine is designed to adapt to the surrounding environment:

- Shifts map colors and brightness according to the time of day and ambient light.
- Reacts to weather states, including rain, snow, and thunderstorms.
- Fully themeable through resource packs for custom parchment textures, borders, icons, and color palettes.
- Client settings managed in-game using YetAnotherConfigLib (YACL).

### Optional F3 debug restrictions

For servers that want to keep navigation grounded in game items and landmarks, Cartography provides mixins and overlay handlers that restrict the F3 debug screen.

Each restriction supports three levels: `DISABLED` (no restriction), `NON_OPS` (restricted for regular players), or `ALL` (restricted for everyone).

Available toggles in `cartography-server.toml`:

- `hideF3Coordinates`: Strips the `XYZ:` line.
- `hideF3Block`: Strips the `Block:` line.
- `hideF3Chunk`: Strips the `Chunk:` line.
- `hideF3Facing`: Strips the `Facing:` direction line.
- `hideF3Biome`: Strips the `Biome:` line.
- `hideF3Light`: Strips the `Client Light:` line.
- `hideF3Targeted`: Removes targeted block, targeted fluid, and targeted entity panels on the right side of the screen.
- `disableF3C`: Blocks the `F3 + C` shortcut that copies coordinates to the clipboard.
- `disableF3G`: Blocks the `F3 + G` shortcut that toggles chunk borders.

## Building from source

### Requirements

- Java 21 JDK
- Git

### Build

```bash
git clone https://github.com/SirCesarium/cartography.git
cd cartography

./gradlew build
```

To launch a client for testing:

```bash
./gradlew runClient
```

To launch a dedicated test server:

```bash
./gradlew runServer
```

## License

[MIT](./LICENSE)
