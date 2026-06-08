# Assignment 3 – Unit Testing the Columns Game

## Overview

This assignment focuses on creating a meaningful and reliable unit test suite for the refactored Columns game. The goal is to verify important game behavior and ensure that future changes do not break core functionality.

The project includes both the original game code (refactored version only) and a set of deterministic, behavior-focused unit tests.

---

## Test Framework

* **JUnit 5** is used for all unit tests.
* Tests are executed using **Maven**.

---

## Tested Components

### 1. Figure (Model)

Tested behaviors:

* Rotation up cycles colors in the correct order
* Rotation down cycles colors in the correct order
* Movement:

  * Moving left decreases `x`
  * Moving right increases `x`
  * Moving down increases `y`

---

### 2. Board (Model)

Test coverage includes:

* **Initialization**

  * `initBoard` clears the field, score, level, and match counter

* **Movement constraints**

  * Cannot move left at left boundary
  * Cannot move right at right boundary
  * Cannot move into occupied cells

* **Gravity**

  * `figureMayMoveDown` is false at bottom
  * `figureMayMoveDown` is false when blocked

* **Figure placement**

  * `pasteFigure` writes colors correctly
  * `dropFigure` places figure at lowest valid position

* **Match detection**

  * Vertical matches
  * Horizontal matches
  * Diagonal matches

* **Collapse behavior**

  * Cells fall downward after matches are removed

* **Scoring**

  * Score increases after matches

* **Level system**

  * Level increases after match threshold

* **Game over**

  * `isFieldFull` detects occupied row 3

---

### 3. GameController (Event Handling)

Tested using fake dependencies:

* `LEFT` moves figure left when allowed
* `LEFT` blocked by wall or occupied cell
* `RIGHT` moves figure right when allowed
* `UP` rotates figure upward
* `DOWN` rotates figure downward
* `DROP` drops figure to lowest position
* `LEVEL_UP` increases level (bounded)
* `LEVEL_DOWN` decreases level (bounded)

---

## Test Doubles (Fakes)

To ensure deterministic and fast tests, the following fake implementations were created:

### FakeRandom

* Provides predictable sequences of values
* Eliminates dependence on real randomness

### FakePlatform

* Controls time manually
* `delay()` does not sleep
* Allows deterministic controller testing

### FakeScreen

* Records drawing calls instead of rendering UI
* Enables verification of rendering behavior if needed

### FakeModelListener

* Records callbacks such as:

  * score updates
  * level changes
  * match notifications

---

## Test Design Principles

The tests were designed to be:

* **Deterministic** – no real randomness or time dependency
* **Fast** – no sleeping or UI rendering
* **Isolated** – each test verifies one behavior
* **Readable** – test names describe behavior clearly
* **Behavior-focused** – assertions check game rules, not implementation details

Weak tests such as constructor checks or “no exception thrown” were avoided.

---

## Challenges and Observations

### 1. Controller Complexity

The `GameController` was harder to test because it combines:

* input handling
* timing logic
* game loop behavior
* rendering

Solution:

* Introduced `FakePlatform` to control time
* Tested individual event behavior instead of full loop

---

### 2. Time Dependency

Real-time delays would make tests slow and unreliable.

Solution:

* Replaced real timing with controllable fake time

---

### 3. Randomness

Figure color generation depends on randomness.

Solution:

* Introduced `FakeRandom` to ensure predictable outputs

---

## Testability Improvements

Small changes were made to improve testability:

* Injected `RandomGenerator` into `Figure`
* Injected `Platform` into `GameController`
* Used package-private access instead of making classes public

No game logic or behavior was modified.

---

## Project Structure

```
src/
├── main/java/columns/        (game code)
└── test/java/columns/
    ├── model/
    │   ├── FigureTest.java
    │   ├── BoardTest.java
    │   ├── FakeRandom.java
    │   └── FakeModelListener.java
    ├── controller/
    │   ├── GameControllerTest.java
    │   └── FakePlatform.java
    └── view/
        └── FakeScreen.java
```

---

## How to Run Tests

Using Maven:

```
mvn test
```

All tests should run quickly and pass consistently.

---

## Optional Coverage (Bonus)

JaCoCo can be used to generate a coverage report:

```
mvn test
mvn jacoco:report
```

Coverage is used to support test completeness, not replace meaningful assertions.

---

## Conclusion

This test suite verifies the core mechanics of the Columns game, including movement, matching, scoring, and controller events. The use of deterministic test doubles ensures reliability and reproducibility. The tests are designed to catch regressions and provide confidence when modifying the code.
