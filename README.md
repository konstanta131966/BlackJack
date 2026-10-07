
# Blackjack 21 (Java Swing)

A simple, responsive desktop Blackjack game built in Java using Swing. Features dynamic rendering, full-deck card art, dealer AI, and real-time screen scaling.

  <img width="810" height="672" alt="cards" src="https://github.com/user-attachments/assets/e1e9f217-4ad8-4f18-a1d6-299bf74ce351" />
 

 ---

## Description

* **Rules**: Standard dealer AI (draws to 17 or higher), natural Blackjack checks, and dynamic Ace valuation (counts as 1 or 11 automatically).
* **Responsive UI**: Cards, fonts, and controls scale and center dynamically when resizing or maximizing the window.
* **Fullscreen Support**: Quick toggle via the control bar or by pressing `F11`.
* **Zero External Dependencies**: Pure Java Standard Library (AWT/Swing)[cite: 1]—runs out of the box on Windows, macOS, and Linux.

## Architecture Overview

  Main.java — Application bootstrap and entry point

  Blackjack.java — Core game state, deck shuffling, hand calculations, and dealer rules

  BlackjackGUI.java — Top-level window controller, user input handling, and layout scaling

  GamePanel.java — Custom double-buffered graphics surface rendering card sprites and centered text overlays

 ## Quick Start (No Compilation Required)

1. Ensure you have **Java 17 or higher** installed (`java -version`).
2. Download `Blackjack.jar` from the [Releases](https://github.com/YOUR_USERNAME/YOUR_REPO/releases/latest) section.
3. Run it via double-click, or launch from terminal:
   ```bash
   java -jar Blackjack.jar
   
## License

Distributed under the MIT License. Feel free to fork, modify, or use as a reference for your own Java desktop projects.
