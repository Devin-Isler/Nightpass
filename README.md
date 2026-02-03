# Nightpass: Survivor Battle Engine

## 🚀 Project Overview
**Nightpass** is a high-performance survival combat engine that simulates a nightly duel between a **Survivor (Hero)** and a mysterious **Stranger (Boss)**. While the premise is a card game, the system is a pure backend logic engine that manages complex hierarchical health states, deterministic card selection, and automated healing cycles through custom-implemented AVL trees and queues.

The core engineering challenge lies in the "Fire's Power"—a restorative mechanism that requires the engine to efficiently manage a deck and a discard pile to revive fallen warriors and maintain the Survivor's vitality night after night.

---

## 🛠 Under the Hood: The Engineering

The engine processes battle mechanics by synchronizing different data structures to ensure the simulation remains accurate and efficient, even during massive combat logs.

### 1. Hierarchical Health Management (Custom AVL Trees)
The core logic revolves around the **HealthTree**, a specialized AVL tree that maintains a balanced hierarchy of the Survivor's cards:
* **Structural Representation**: The tree organizes cards as nodes sorted by their current health (for the active deck) or missing health (for the discard pile).
* **Self-Balancing Logic**: To ensure $O(\log n)$ search, insertion, and deletion times, the tree performs rotations to maintain its height.
* **Recursive Stat Tracking**: Each node in the tree stores the `maxHp` and `minHp` of its entire subtree. This allows the engine to instantly prune branches that cannot satisfy a healing or attack requirement.



### 2. Multi-Level Combat Selection (The AttackTree)
To handle complex card interactions, the engine implements an **AttackTree** where each node represents a specific attack value.
* **Nested Complexity**: Each node in the `AttackTree` contains its own internal `HealthTree`. This creates a two-dimensional search space: find the best attack value first, then find the most suitable health profile within that value.
* **Priority-Based Selection**: The engine follows a strict 4-level priority system to find the optimal card to play against The Stranger, balancing raw power against survival needs.



### 3. Turn-Based Coordination (Custom Queues)
To maintain the correct order of combat operations and deterministic outcomes, the engine uses a custom **Queue** system:
* **FIFO Processing**: When multiple cards have identical attack and health stats, the engine uses a queue to ensure the card drawn first is played first.
* **Order of Operations**: Every combat effect and card revival is queued to ensure the simulation remains perfectly deterministic regardless of the number of turns.

### 4. The Fire's Power (Revival Logic)
The most critical part of the survival logic is the nightly healing phase:
* **The Healing Aura**: The engine identifies cards in the discard pile that can be fully or partially restored using the fire's remaining warmth.
* **Dynamic Stat Modification**: Revived cards undergo "Permanent Scarring"—their base attack is permanently reduced by 10% for a full revival or 5% for a partial revival, representing the toll of the duel.

---

## 📂 Project Architecture
* **`Main.java`**: The core simulation driver that handles the nightly battle loop and "Fire Phase" logic.
* **`HealthTree.java` / `HealthNode.java`**: The AVL tree system responsible for monitoring hierarchical health and missing health.
* **`AttackTree.java` / `AttackNode.java`**: The primary combat engine that manages multi-level card selection.
* **`Queue.java` / `QueueNode.java`**: The FIFO sequencer that ensures deterministic turn resolution.
* **`Card.java`**: The data model for Survivor cards, handling damage taken and revival penalties.

---

## 🚦 How to Run
The engine reads the Survivor's initial deck and the nightly encounters from input files to generate a survival log.

```bash
javac *.java
java Main <initial_deck_file> <encounter_log_file>
