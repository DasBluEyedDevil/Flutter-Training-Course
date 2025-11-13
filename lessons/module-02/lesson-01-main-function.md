# Module 2, Lesson 1: What Happens When You Run an App?

## Welcome to Flutter!

Congratulations on completing Module 1! You now understand:
- How to write basic code (instructions)
- How to store information (variables)
- How to make decisions (if/else)

Now we're ready to start building actual Flutter apps!

But first, we need to understand: **What happens when you run a Flutter app?**

---

## The Starting Point

Remember when we created our first app and saw all that code in `main.dart`? Let's simplify it and understand what's actually happening.

Every Flutter app starts with this:

```dart
void main() {
  runApp(MyApp());
}
```

That's it! This is the **entry point** of every Flutter app.

---

## The "main()" Function - Your App's Starting Point

Think of `main()` like the "Start" button on a video game.

When you press "Run" in VS Code:
1. Flutter looks for the `main()` function
2. Executes whatever code is inside it
3. Your app comes to life!

**Every Dart and Flutter program must have a `main()` function.** Without it, the program doesn't know where to begin.

---

## The "runApp()" Function - Showing Something on Screen

Now look at what's *inside* the `main()` function:

```dart
runApp(MyApp());
```

**Conceptual Explanation**:
- `runApp()` is a special Flutter function that says "Put this on the screen"
- `MyApp()` is what we want to show
- Together they mean: "Take MyApp and display it"

**The Technical Term**: `runApp()` is the function that tells Flutter to inflate your app's widget tree and attach it to the screen.

(Don't worry about "widget tree" yet - we'll get there!)

---

## Your First Minimal Flutter App

Let's create the simplest possible Flutter app. Create a new file called `minimal_app.dart`:

```dart
import 'package:flutter/material.dart';

void main() {
  runApp(
    MaterialApp(
      home: Center(
        child: Text('Hello, Flutter!'),
      ),
    ),
  );
}
```

Let's run this! You should see a screen with "Hello, Flutter!" in the middle.

---

## Breaking It Down

Let's understand each piece:

### 1. The Import Statement

```dart
import 'package:flutter/material.dart';
```

**Conceptual**: Think of this like adding tools to your toolbox. The `material.dart` package contains all the visual components (buttons, text, etc.) that Flutter provides.

**Technical**: This imports Flutter's Material Design widgets, which give us access to ready-made UI components.

### 2. The Main Function

```dart
void main() {
  ...
}
```

We know this one! It's the starting point.

### 3. MaterialApp

```dart
MaterialApp(
  home: Center(
    child: Text('Hello, Flutter!'),
  ),
)
```

**Conceptual**: `MaterialApp` is like the foundation of a house. It provides the basic structure that all Flutter apps need.

**Technical**: `MaterialApp` is a widget that wraps your entire app and provides Material Design styling, navigation, and theme support.

### 4. The Home

```dart
home: Center(...)
```

**Conceptual**: The `home` is the first screen the user sees - like the homepage of a website.

**Technical**: `home` is a property that takes a widget. This widget becomes the default route (screen) of your app.

### 5. Center

```dart
Center(
  child: Text('Hello, Flutter!'),
)
```

**Conceptual**: `Center` is like putting something in the middle of a page. Whatever is inside it gets centered on the screen.

**Technical**: `Center` is a layout widget that positions its child in the center of the available space.

### 6. Text

```dart
Text('Hello, Flutter!')
```

**Conceptual**: This displays text on the screen, just like `print()` displays text in the terminal!

**Technical**: `Text` is a widget that displays a string of text with styling.

---

## Introducing: Widgets

You've now seen your first **widgets**!

**Conceptual First**: Think of widgets as LEGO pieces. Each piece is a building block:
- A `Text` widget is like a LEGO piece with letters on it
- A `Center` widget is like a LEGO baseplate that centers other pieces
- A `MaterialApp` widget is like the LEGO box that holds everything together

You snap these pieces together to build your app!

**Now the Technical Term**: Widgets are the building blocks of Flutter apps. Everything you see on the screen is a widget - text, buttons, images, layouts, everything.

---

## The Widget Tree (Simplified)

Look at how our widgets are nested:

```
MaterialApp
  └─ Center
      └─ Text
```

This is called a **widget tree**. Each widget can have children (widgets inside it), creating a tree structure.

Think of it like:
- **MaterialApp** is the trunk
- **Center** is a branch
- **Text** is a leaf

---

## Customizing Your First App

Let's make changes to see how widgets work!

### Change 1: Bigger Text

```dart
void main() {
  runApp(
    MaterialApp(
      home: Center(
        child: Text(
          'Hello, Flutter!',
          style: TextStyle(fontSize: 48),
        ),
      ),
    ),
  );
}
```

Save and see the text get bigger!

### Change 2: Add Color

```dart
Text(
  'Hello, Flutter!',
  style: TextStyle(
    fontSize: 48,
    color: Colors.blue,
  ),
),
```

The text is now blue!

### Change 3: Multiple Style Properties

```dart
Text(
  'Hello, Flutter!',
  style: TextStyle(
    fontSize: 48,
    color: Colors.blue,
    fontWeight: FontWeight.bold,
  ),
),
```

Now it's big, blue, and bold!

---

## Understanding the Pattern

Notice the pattern:

1. Every widget has **properties** (like `home`, `child`, `style`)
2. Properties are set using a **colon** (`:`)
3. Some properties take other widgets (like `child`)
4. Some properties take values (like `fontSize`)

This is how all Flutter code is structured!

---

## ✅ YOUR CHALLENGE: Customize Your App

**Goal**: Create a Flutter app that displays a personalized greeting.

**Requirements**:

Create a file called `my_greeting_app.dart` that:

1. Displays your name in large text
2. Centers it on the screen
3. Makes the text a color of your choice
4. Makes the text bold

**Example**: If your name is "Alex", the screen should show "Hello, Alex!" in large, bold, colored text in the center.

**Hints**:
- Start with the minimal app code
- Change the text to include your name
- Use `TextStyle` with `fontSize`, `color`, and `fontWeight`
- Available colors: `Colors.red`, `Colors.blue`, `Colors.green`, `Colors.purple`, etc.

**Success Condition**: Your app runs and displays your customized greeting. ✅

---

## Bonus Challenge: Add Background Color

Try adding a background color to your app:

```dart
MaterialApp(
  home: Container(
    color: Colors.lightBlue,
    child: Center(
      child: Text(
        'Your text here',
        style: TextStyle(...),
      ),
    ),
  ),
)
```

This introduces the `Container` widget - another LEGO piece that can have a background color!

---

## Common Beginner Questions

**Q: Why do we need both `MaterialApp` and `Center`?**
A: `MaterialApp` sets up the app foundation. `Center` positions the content. They serve different purposes!

**Q: What if I forget the `import` statement?**
A: You'll get errors like "Undefined name 'MaterialApp'". The import brings in the tools you need.

**Q: Can I have multiple `main()` functions?**
A: No! Each program has exactly one `main()` function as the entry point.

**Q: Why all the commas?**
A: Commas separate properties and parameters. It's how Dart knows where one thing ends and another begins.

---

## What Did We Learn?

Let's recap:
- ✅ Every Flutter app starts with `main()`
- ✅ `runApp()` puts your app on the screen
- ✅ Widgets are LEGO-like building blocks
- ✅ Everything in Flutter is a widget
- ✅ Widgets nest inside each other (widget tree)
- ✅ We can customize widgets with properties
- ✅ `import` statements bring in the tools we need

---

## What's Next?

Right now, we can display text. But real apps need layouts - multiple pieces of content arranged on screen.

In the next lesson, we'll learn about **layout widgets**:
- How to stack things vertically (like a to-do list)
- How to arrange things horizontally (like a row of buttons)
- How to create complex arrangements

Get ready to build real app screens! 🚀
