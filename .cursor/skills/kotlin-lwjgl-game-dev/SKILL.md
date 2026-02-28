---
name: kotlin-lwjgl-game-dev
description: Provides patterns and guidance for building 2D games in Kotlin using LWJGL 3, including project setup, game loop structure, rendering, input, and simple gameplay systems. Use when working in this Kotlin LWJGL game project or when the user asks about Kotlin, LWJGL, OpenGL, or 2D game architecture here.
---

# Kotlin LWJGL 2D Game Development

This skill provides focused guidance for building a 2D game in Kotlin using LWJGL 3 within this project.

## Project Setup

- Prefer **Gradle with Kotlin DSL** (`build.gradle.kts`) for:
  - Declaring **LWJGL 3** BOM and modules (core, glfw, opengl, stb, etc.).
  - Handling **natives per platform** (macos, windows, linux) via classifiers.
- When generating build files:
  - Use **clear version variables** for Kotlin, LWJGL, and JVM target.
  - Configure the application plugin (or equivalent) with a **main class** entry point.

### Minimal Dependencies Pattern

When proposing a minimal LWJGL dependency set, aim for:

- `lwjgl`
- `lwjgl-glfw`
- `lwjgl-opengl`
- `lwjgl-stb` (for image/font loading) if needed

Include `natives-*` variants for at least:

- `natives-macos`
- `natives-windows`

Add Linux natives if the user mentions Linux explicitly.

## Main Entry and Game Loop

When creating the entry point:

- Use a **single `main` function** (in Kotlin) that:
  - Initializes LWJGL/GLFW.
  - Creates a window with a configurable size and title.
  - Sets up the OpenGL context and basic state (clear color, vsync, blending).
- Structure the **game loop** as:

```kotlin
while (!glfwWindowShouldClose(window)) {
    val now = glfwGetTime()
    val delta = (now - lastTime).toFloat()
    lastTime = now

    // 1. Poll input
    glfwPollEvents()

    // 2. Update game state
    update(delta)

    // 3. Render
    render()

    // 4. Swap buffers
    glfwSwapBuffers(window)
}
```

- Keep `update` and `render` **separate functions** in the design, even if implemented inline initially.

## 2D Rendering Basics

For 2D games:

- Use an **orthographic projection** covering your world or screen coordinates.
- Start with:
  - A **simple shader** pair (vertex + fragment) for textured quads.
  - A basic **quad mesh** (two triangles) reused for sprites.
  - A **texture loader** that can load PNGs into OpenGL textures (optionally via STB).
- Encourage a **Sprite-like abstraction**:
  - Holds position, size, texture region, and optionally rotation.
  - Exposes a simple `draw(sprite)` API through a small **SpriteBatch** or renderer object.

If complexity grows, suggest:

- Batching sprites into a single vertex buffer where practical.
- Packing images into a **texture atlas**, but only after the basics work.

## Input Handling

- Use **GLFW callbacks** or polling for:
  - Keyboard input (movement, actions).
  - Mouse position and buttons (UI, aiming).
- Provide helpers that map:
  - **Raw keys** to semantic actions (e.g., `MOVE_LEFT`, `JUMP`).
  - Keep input-to-action mapping in one place to simplify remapping later.

## Game State and Scenes

- Recommend a **scene (screen/state) interface**, e.g.:

```kotlin
interface Scene {
    fun update(delta: Float)
    fun render()
    fun onEnter() {}
    fun onExit() {}
}
```

- Maintain a **current scene** reference in your game/application class.
- Use simple implementations such as:
  - `MenuScene`
  - `GameScene`
  - `PauseScene`

Switch scenes by calling `onExit` on the current one, then `onEnter` on the new scene.

## Entities and Simple Systems

- Begin with **plain Kotlin data classes** for entities:

```kotlin
data class Entity(
    var x: Float,
    var y: Float,
    var vx: Float = 0f,
    var vy: Float = 0f
)
```

- Add behavior through:
  - Small, focused **systems** (e.g., `MovementSystem`, `CollisionSystem`).
  - Functions that operate on collections of entities.
- Only introduce a **full ECS** framework if:
  - The number of entity types and interactions grows significantly.
  - The user explicitly asks for an ECS-style architecture.

## Debugging and Performance

- When performance issues appear, consider:
  - Reducing **state changes** and **draw calls** (favor batching).
  - Avoiding per-frame allocations in hot paths (reuse buffers/objects).
- For debugging:
  - Add simple **FPS logging** using frame time.
  - Optionally render **debug shapes** (e.g., colored rectangles) for collision bounds.

## Example Workflow

When the user asks for help:

1. **New project or major subsystem**:
   - Start with project structure and Gradle configuration.
   - Provide a minimal main window and loop that compiles and runs.
2. **Rendering task**:
   - Ensure there is a working OpenGL context.
   - Add or update a **sprite renderer** or shader as needed.
3. **Gameplay feature**:
   - Propose data structures and update logic.
   - Integrate with rendering and input layers, keeping responsibilities clear.
4. **Refinement**:
   - Suggest incremental refactors once a feature works (e.g., extracting a scene or system).

Use this skill whenever you need language- and framework-specific guidance for Kotlin + LWJGL 3 in this project.

