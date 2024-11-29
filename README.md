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

---

## **Birds**
### **Red:**
- The standard bird with balanced properties.
- Has no special abilities but is reliable for destroying standard structures.

### **Chuck:**
- A speedster bird capable of moving extremely fast.
- Deals extra damage to wooden blocks and small structures due to its speed.
- Activated by tapping the screen after launching.
### **Bomb:**
- A heavy bird that explodes on impact.
- Effective against stone and metal blocks, causing widespread destruction.
- Explosion is triggered either on impact or by tapping the screen after launch.
## **Pigs**
### **Normal Pig:**
- The standard enemy that sits on or inside structures.
- Easily defeated when hit by a bird or when the structure collapses on it.
### **Magic Pig:**
- A tricky pig that teleports to another location when hit.
- Requires multiple hits to defeat as it relocates each time it’s attacked.
### **Zombie Pig:**
- A randomly appearing pig that enters the screen from the sides.
- Adds an element of surprise to gameplay, as it may interfere with planned shots.
- Can be defeated like normal pigs but requires strategic timing.



