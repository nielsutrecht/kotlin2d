# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

```bash
./gradlew run          # Build and launch the game (macOS requires -XstartOnFirstThread, already configured)
./gradlew build        # Compile and package
./gradlew classes      # Compile only (fast check)
```

Requires Java 17 (see `.java-version`). No test suite exists yet.

**Note:** On macOS the JVM must start on the first thread (`-XstartOnFirstThread`), which is set in `build.gradle.kts` via `applicationDefaultJvmArgs`.

## Project Overview

A 2D dungeon game written in **Kotlin** using **LWJGL 3** (OpenGL + GLFW + STB + OpenAL). Single Gradle module, Kotlin DSL. All source is in `src/main/kotlin/kotlin2d/`.

## Architecture

**Game loop:** `Main.kt` → `GameApp` → game loop with delta time. Owns GLFW window and OpenGL context. Runs `update`/`render` on the current `Scene`, calls `Audio.update()`, and supports scene switching via `pendingScene`.

**Scene system:** `Scene` interface (`update`, `render`, `onEnter`, `onExit`). `DungeonScene` is the main gameplay scene — grid-based player movement (WASD/arrows), item pickup, door/key interaction, chest opening with BFS loot scatter, coin collection, and stair-based multi-level transitions.

**Rendering:** `SimpleTileRenderer` draws textured quads (OpenGL fixed-function `GL_QUADS`). `Camera` provides scroll/follow and visible-range culling. `Hud` draws inventory icons and gold counter in screen space.

**Tileset:** `DungeonTileset` defines `TileDef` entries with atlas coordinates into a 32×32 sprite sheet. `TileKind` enum classifies tiles for gameplay (WALL, FLOOR, DOOR, STAIRS, etc.). Includes enemy and item tiles.

**Map generation:** `DungeonGenerator` — procedural rooms + L-shaped corridors, door/key placement, enemy/chest/item spawning scaled by dungeon level.

**Entities:** Data-class approach — `Enemy` (5 types, static), `Item` (KEY/POTION/SWORD), `Chest` (openable, loot scatter), `CoinPile`. All placed per-level by the generator.

**State:** `GameState` singleton — inventory, gold, player stats, per-level dungeon cache for revisiting floors.

**Audio:** `Audio` singleton — OpenAL backend. Background music (shuffled OGG tracks from `music/`). SFX pool (8 sources, round-robin) with sound groups (random variant selection, auto-detected by `-N` suffix from `sounds/`).

**Input/Textures:** `Input` global object for GLFW key state. `Texture.load()` reads classpath resources via STB.

## Project Structure

- `src/main/kotlin/kotlin2d/` — all source code
- `src/main/resources/textures/` — sprite atlas
- `sounds/` — SFX (OGG, loaded from filesystem)
- `music/` — background music (OGG, loaded from filesystem)
- `openspec/` — spec-driven workflow

## OpenSpec Workflow

The project uses a spec-driven workflow via `openspec/`. Living specs live in `openspec/specs/<feature>/spec.md`. Completed changes are archived in `openspec/changes/archive/`.

## Key Conventions

- Idiomatic Kotlin; simple composable designs over premature abstractions
- Keep game loop, rendering, and input concerns separated
- LWJGL 3 with simple/approachable OpenGL patterns (textured quads, sprite sheets)
- Cross-platform: macOS and Windows (native libraries selected automatically in `build.gradle.kts`)
- Incremental improvements over large rewrites
