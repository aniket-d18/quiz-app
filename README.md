# Java Swing MCQ Quiz Application

![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Maven](https://img.shields.io/badge/Build-Maven-blue.svg)
![Status](https://img.shields.io/badge/Status-Completed-success.svg)

A modern, highly interactive, kiosk-style desktop quiz application built using pure **Java 17** and **Swing**. This application was developed as a SY B.Tech Mini Project to demonstrate core Object-Oriented Programming (OOP) concepts, File I/O, and advanced Graphical User Interface (GUI) design.

## ✨ Features

- **Modern Glassmorphism UI:** A custom-built dark theme utilizing Java 2D Graphics gradients, translucent floating cards, and dynamic hover states—moving away from traditional grey Java interfaces.
- **Kiosk "Anti-Cheat" Mode:** The app launches as an undecorated maximal window with a `WindowFocusListener` that locks the user in. If they attempt to Alt-Tab, the window forces itself back to the front.
- **Dynamic File-Based Engine:** No expensive SQL databases required. Questions are parsed directly from text files at runtime, and scores are safely written to appending CSV logs.
- **Intelligent Result Breakdown:** The app dynamically extracts metadata topics from the loaded questions and automatically renders a category-wise performance breakdown after test submission.

## 🏗️ Architecture & OOP Implementation

This project strictly adheres to the **Model-View-Controller (MVC)** architectural pattern and leverages core OOP principles:

- **Models:** Self-contained POJOs representing core domains (`Student.java`, `Question.java`, `Result.java`, `Feedback.java`).
- **Data Layer:** `QuestionLoader.java` handles streaming `data/questions.txt`, throwing a custom `InvalidQuestionFormatException` if parsing fails. `ResultManager.java` securely logs final grades.
- **Controller:** The `QuizEngine.java` handles state-machining. It loads all questions, shuffles them uniformly, strictly serves the required subset size, and manages real-time answer states.
- **Views:** Independent UI logic components (`LoginPanel`, `QuizPanel`, `ResultPanel`) managed seamlessly by a `CardLayout` in the central `MainFrame`.

## ⚙️ How to Add Custom Questions

The entire quiz pool is dynamically loaded from `data/questions.txt`. The application reads the pool, shuffles it, and picks exactly 20 questions for the active test.

To insert your own questions, follow this exact pipe-delimited (`|`) format:
`ID | Category_Name | Question Text | Option A | Option B | Option C | Option D | Correct_Letter`

**Example:**
```text
1|Java Basics|What is the size of an int in Java?|2 bytes|4 bytes|8 bytes|1 byte|B
2|OOP|Which concept prevents data mutation?|Inheritance|Polymorphism|Encapsulation|Abstraction|C
```

## 🚀 How to Run

Ensure you have **Java 17** and **Maven** installed on your system.

1. Clone this repository.
2. Open your terminal in the project root directory.
3. Compile and execute using Maven:
   ```bash
   mvn compile exec:java
   ```

## ⌨️ Admin Hotkeys
* **Force Quit:** `Ctrl + Shift + Q` (To safely exit the locked Kiosk mode).
