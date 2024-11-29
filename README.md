# Angry Birds Game

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

This project is a 2D physics-based game inspired by **Angry Birds**, where players launch projectiles (birds) to destroy structures and defeat enemies. The game implements core Object-Oriented Programming (OOP) concepts and leverages LibGDX's robust game development framework.

---

## Game Features

1. **Physics-Based Gameplay:**
    - Realistic trajectory and collision simulation using **Box2D** physics engine.
    - Drag-and-launch functionality for projectiles.

2. **Levels System:**
    - Multiple levels of increasing difficulty.
    - Levels unlock sequentially upon completion of the previous ones.
    - Locked levels displayed with a `lock.png` overlay.

3. **Dynamic Destruction:**
    - Structures react dynamically to hits based on physics calculations.

4. **Scoring System:**
    - Players score points by destroying structures and defeating enemies.

5. **Interactive UI:**
    - Hover effects and buttons for navigation.
    - Background music toggle functionality.

---

## OOPS Concepts Used

### 1. **Encapsulation**
- Each game component (e.g., Bird, Slingshot, Structures) is encapsulated within its own class.
- Private fields with getter and setter methods to control access.

### 2. **Inheritance**
- Game objects like birds, pigs, and blocks inherit from a base `GameObject` class.
- Levels inherit from a base `Level` class to maintain consistent behavior.

### 3. **Polymorphism**
- Methods like `render()` and `update()` are overridden for different game objects and levels.
- Dynamic behavior implemented for birds and structures depending on the type of projectile used.

### 4. **Abstraction**
- Complex functionalities like physics simulation, collision detection, and scoring are abstracted from the player.
- High-level interfaces like `Screen` for game states (menu, level selection, gameplay) make transitions seamless.

### 5. **Composition**
- Levels are composed of multiple `GameObject` instances such as birds, blocks, and enemies.
- Each level screen is composed of textures, buttons, and physics objects.

---

## Levels Description

### **Level 1:**
- Basic gameplay introduction.
- One bird to launch, simple structures, and one enemy.

### **Level 2:**
- Introduces larger structures with multiple enemies.
- More birds provided to complete the level.

### **Level 3:**
- Complex structures requiring strategic hits.
- Final boss pig added for an extra challenge.



