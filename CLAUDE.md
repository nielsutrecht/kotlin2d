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

A 2D dungeon game written in **Kotlin** using **LWJGL 3** (OpenGL + GLFW + STB). Single Gradle module, Kotlin DSL. All source is in `src/main/kotlin/kotlin2d/`.

## Architecture

**Game loop:** `Main.kt` → `GameApp` → game loop with delta time. `GameApp` owns the GLFW window, OpenGL context, and runs update/render on the current `Scene`.

**Scene system:** `Scene` interface with `update(delta)`, `render()`, `onEnter()`, `onExit()`. `FirstScene` is the only scene — a tile-based dungeon room with a controllable player character (WASD/arrow keys, grid-based movement with cooldown timer).

**Rendering:** `SimpleTileRenderer` draws textured quads using OpenGL fixed-function pipeline (immediate mode `GL_QUADS`). Converts grid coordinates → NDC via manual pixel-to-NDC math. No projection matrix or camera yet.

**Tileset:** `DungeonTileset` object defines tile definitions (`TileDef`) with atlas coordinates into a 32×32 sprite sheet (`dungeon-tileset.png`). `TileKind` enum classifies tiles for gameplay logic (e.g., `WALL` blocks movement).

**Texture loading:** `Texture.load()` reads from classpath resources via STB, returns a data class with OpenGL texture ID and dimensions.

**Input:** `Input` is a global object holding the GLFW window handle. Scenes query key state directly via `glfwGetKey`.

## Key Conventions

- Idiomatic Kotlin; simple composable designs over premature abstractions
- Keep game loop, rendering, and input concerns separated
- LWJGL 3 with simple/approachable OpenGL patterns (textured quads, sprite sheets)
- Cross-platform: macOS and Windows (native libraries selected automatically in `build.gradle.kts`)
- Incremental improvements over large rewrites
