# Module 2, Lesson 2: Building Blocks (Widgets)

## The LEGO Analogy

Remember playing with LEGO bricks? Each brick is a simple piece, but when you combine them, you can build amazing things - houses, cars, spaceships!

**Flutter widgets work exactly the same way!**
- Each widget is a building block
- You snap widgets together
- Complex UIs are made from simple widgets combined

---

## What Exactly is a Widget?

**Conceptual First**: A widget is anything you see on screen:
- Text? That's a widget.
- Button? That's a widget.
- Image? That's a widget.
- The layout that arranges them? Also a widget!

**Technical Term**: In Flutter, **everything is a widget**. Widgets are the building blocks of your app's user interface.

---

## Two Types of Widgets

### 1. StatelessWidget - Doesn't Change

Think of a street sign - it always shows the same information.

```dart
import 'package:flutter/material.dart';

class WelcomeScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        body: Center(
          child: Text('Welcome!'),
        ),
      ),
    );
  }
}
```

**When to use**: Static content that doesn't change.

### 2. StatefulWidget - Can Change

Think of a digital clock - it updates every second.

```dart
class Counter extends StatefulWidget {
  @override
  _CounterState createState() => _CounterState();
}

class _CounterState extends State<Counter> {
  int count = 0;

  @override
  Widget build(BuildContext context) {
    return Text('Count: $count');
  }
}
```

**When to use**: Dynamic content that changes (we'll cover this in detail later).

---

## Common Built-in Widgets

Flutter provides many ready-to-use widgets:

| Widget | Purpose |
|--------|---------|
| `Text` | Display text |
| `Image` | Show images |
| `Icon` | Display icons |
| `Container` | Box for layout and styling |
| `Row` | Arrange widgets horizontally |
| `Column` | Arrange widgets vertically |
| `Stack` | Overlay widgets |
| `ListView` | Scrollable list |
| `Button` | Clickable button |

---

## The Widget Tree

Widgets nest inside each other, forming a tree:

```dart
MaterialApp
 └─ Scaffold
     └─ Center
         └─ Text
```

**Think of it like nesting dolls** - each widget contains other widgets.

---

## ✅ YOUR CHALLENGE

Create a simple app with at least 3 different widgets nested together.

**Success Condition**: You understand that everything in Flutter is a widget! ✅

---

## What's Next?

Now let's explore the most common widget - **Text** - and learn how to style it!
