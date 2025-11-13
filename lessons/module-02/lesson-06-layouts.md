# Module 2, Lesson 6: Arranging Widgets (Column & Row)

## The Layout Problem

You know how to create individual widgets (Text, Image, Container). But real apps have MANY widgets on screen:
- A profile screen: photo + name + bio + buttons
- A login screen: logo + text fields + button
- A feed: many posts stacked vertically

**How do we arrange multiple widgets?** Enter `Column` and `Row`!

---

## The Stack of Pancakes (Column)

**Conceptual First**: Imagine stacking pancakes on a plate. Each pancake sits on top of the previous one.

**Column does the same** - it stacks widgets vertically (top to bottom).

```dart
Column(
  children: [
    Text('First widget'),
    Text('Second widget'),
    Text('Third widget'),
  ],
)
```

Output:
```
First widget
Second widget
Third widget
```

---

## Your First Column

```dart
import 'package:flutter/material.dart';

void main() {
  runApp(
    MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: Text('Column Demo')),
        body: Column(
          children: [
            Text('Line 1'),
            Text('Line 2'),
            Text('Line 3'),
          ],
        ),
      ),
    ),
  );
}
```

---

## Main Axis Alignment (Vertical)

Control how children are spaced vertically:

```dart
Column(
  mainAxisAlignment: MainAxisAlignment.start,  // Default: top
  children: [
    Text('Item 1'),
    Text('Item 2'),
    Text('Item 3'),
  ],
)
```

**Options**:
- `MainAxisAlignment.start` - At the top
- `MainAxisAlignment.center` - Centered vertically
- `MainAxisAlignment.end` - At the bottom
- `MainAxisAlignment.spaceBetween` - Space between items
- `MainAxisAlignment.spaceAround` - Space around items
- `MainAxisAlignment.spaceEvenly` - Equal spacing

---

## Cross Axis Alignment (Horizontal)

Control how children are aligned horizontally:

```dart
Column(
  crossAxisAlignment: CrossAxisAlignment.start,  // Left-aligned
  children: [
    Text('Short'),
    Text('Medium text'),
    Text('Very long text here'),
  ],
)
```

**Options**:
- `CrossAxisAlignment.start` - Left edge
- `CrossAxisAlignment.center` - Centered (default)
- `CrossAxisAlignment.end` - Right edge
- `CrossAxisAlignment.stretch` - Fill width

---

## Books on a Shelf (Row)

**Conceptual First**: Imagine books lined up on a shelf, side by side.

**Row does the same** - it arranges widgets horizontally (left to right).

```dart
Row(
  children: [
    Text('First'),
    Text('Second'),
    Text('Third'),
  ],
)
```

Output:
```
First  Second  Third
```

---

## Row Example

```dart
Row(
  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
  children: [
    Icon(Icons.home),
    Icon(Icons.search),
    Icon(Icons.person),
  ],
)
```

---

## Combining Column and Row

This is where it gets powerful!

```dart
Column(
  children: [
    Text('Header'),
    Row(
      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
      children: [
        Icon(Icons.favorite),
        Icon(Icons.star),
        Icon(Icons.share),
      ],
    ),
    Text('Footer'),
  ],
)
```

---

## Spacing Between Children

### Using SizedBox

```dart
Column(
  children: [
    Text('First'),
    SizedBox(height: 20),  // 20 pixels of space
    Text('Second'),
    SizedBox(height: 20),
    Text('Third'),
  ],
)
```

For Row:
```dart
Row(
  children: [
    Icon(Icons.home),
    SizedBox(width: 30),  // 30 pixels of space
    Icon(Icons.search),
  ],
)
```

---

## Complete Profile Example

```dart
import 'package:flutter/material.dart';

void main() {
  runApp(
    MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: Text('Profile')),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              CircleAvatar(
                radius: 50,
                backgroundColor: Colors.blue,
                child: Icon(Icons.person, size: 50, color: Colors.white),
              ),
              SizedBox(height: 20),
              Text(
                'John Doe',
                style: TextStyle(
                  fontSize: 24,
                  fontWeight: FontWeight.bold,
                ),
              ),
              SizedBox(height: 10),
              Text(
                'Flutter Developer',
                style: TextStyle(
                  fontSize: 16,
                  color: Colors.grey,
                ),
              ),
              SizedBox(height: 30),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.email, color: Colors.blue),
                  SizedBox(width: 10),
                  Text('john@example.com'),
                ],
              ),
            ],
          ),
        ),
      ),
    ),
  );
}
```

---

## Expanded - Taking Up Available Space

Sometimes you want a child to take up all remaining space:

```dart
Row(
  children: [
    Icon(Icons.menu),
    Expanded(
      child: Text('This takes up remaining space'),
    ),
    Icon(Icons.search),
  ],
)
```

---

## Flex - Controlling Proportions

```dart
Row(
  children: [
    Expanded(
      flex: 1,  // Takes 1/3 of space
      child: Container(color: Colors.red, height: 50),
    ),
    Expanded(
      flex: 2,  // Takes 2/3 of space
      child: Container(color: Colors.blue, height: 50),
    ),
  ],
)
```

---

## Common Mistakes

### 1. Column without Constrained Height

```dart
// ❌ This errors:
Column(
  children: [
    Expanded(child: Container(color: Colors.red)),
  ],
)

// ✅ This works:
SizedBox(
  height: 200,
  child: Column(
    children: [
      Expanded(child: Container(color: Colors.red)),
    ],
  ),
)
```

### 2. Row/Column Overflow

```dart
// If children are too wide/tall, wrap in SingleChildScrollView:
SingleChildScrollView(
  child: Column(
    children: [
      // Many children...
    ],
  ),
)
```

---

## ✅ YOUR CHALLENGE: Create a Social Media Post

Build a social media post layout:
1. Top row: profile photo + name
2. Middle: post text
3. Bottom row: like button + comment button + share button

Use Column for vertical arrangement, Row for horizontal.

**Success Condition**: You've created a complete post layout! ✅

---

## What Did We Learn?

- ✅ `Column` arranges widgets vertically
- ✅ `Row` arranges widgets horizontally
- ✅ `mainAxisAlignment` controls spacing along main axis
- ✅ `crossAxisAlignment` controls alignment on cross axis
- ✅ `SizedBox` creates spacing
- ✅ `Expanded` takes remaining space
- ✅ Combine Row and Column for complex layouts

---

## What's Next?

You now know the fundamentals of Flutter! You can display text, images, use containers, and arrange widgets in rows and columns.

In the next lesson, we'll build a **mini-project** that combines everything you've learned to create a complete app screen!
