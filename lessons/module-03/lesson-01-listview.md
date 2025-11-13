# Module 3, Lesson 1: Scrollable Lists (ListView)

## The Scrolling Problem

Imagine a news app with 100 articles. You can't fit them all on one screen! You need **scrolling**.

**Column** doesn't scroll by default. If content is too long, it overflows and you get an error.

**ListView** solves this - it creates a scrollable list of widgets!

---

## When to Use ListView

Use ListView when you have:
- A list of items (emails, messages, products)
- Content that might be longer than the screen
- Repeated items with similar structure

**Think**: Instagram feed, WhatsApp chat list, shopping cart

---

## Your First ListView

```dart
import 'package:flutter/material.dart';

void main() {
  runApp(
    MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: Text('ListView Demo')),
        body: ListView(
          children: [
            ListTile(title: Text('Item 1')),
            ListTile(title: Text('Item 2')),
            ListTile(title: Text('Item 3')),
            ListTile(title: Text('Item 4')),
            ListTile(title: Text('Item 5')),
          ],
        ),
      ),
    ),
  );
}
```

---

## ListTile - The Perfect List Item

`ListTile` is a pre-built widget perfect for lists:

```dart
ListTile(
  leading: Icon(Icons.person),  // Left side
  title: Text('John Doe'),      // Main text
  subtitle: Text('Software Engineer'),  // Secondary text
  trailing: Icon(Icons.arrow_forward),  // Right side
)
```

---

## ListView.builder - For Dynamic Lists

When you have many items (especially from data), use `ListView.builder`:

```dart
ListView.builder(
  itemCount: 100,  // Number of items
  itemBuilder: (context, index) {
    return ListTile(
      title: Text('Item $index'),
    );
  },
)
```

**Why builder?** It only creates widgets for visible items - much more efficient!

---

## Complete Example with Data

```dart
class ContactsScreen extends StatelessWidget {
  final List<String> contacts = [
    'Alice',
    'Bob',
    'Carol',
    'David',
    'Eve',
    'Frank',
    'Grace',
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Contacts')),
      body: ListView.builder(
        itemCount: contacts.length,
        itemBuilder: (context, index) {
          return ListTile(
            leading: CircleAvatar(
              child: Text(contacts[index][0]),
            ),
            title: Text(contacts[index]),
            subtitle: Text('Contact #${index + 1}'),
            trailing: Icon(Icons.phone),
          );
        },
      ),
    );
  }
}
```

---

## ListView.separated - With Dividers

Add dividers between items:

```dart
ListView.separated(
  itemCount: contacts.length,
  itemBuilder: (context, index) {
    return ListTile(title: Text(contacts[index]));
  },
  separatorBuilder: (context, index) {
    return Divider();
  },
)
```

---

## Horizontal ListView

Lists can scroll horizontally too:

```dart
ListView(
  scrollDirection: Axis.horizontal,
  children: [
    Container(width: 160, color: Colors.red),
    Container(width: 160, color: Colors.blue),
    Container(width: 160, color: Colors.green),
  ],
)
```

---

## ✅ YOUR CHALLENGE: Todo List

Create a todo list with:
1. At least 5 todos
2. Use ListView.builder
3. Each todo has a checkbox icon
4. Bonus: Add dividers

**Success Condition**: Scrollable todo list! ✅

---

## What's Next?

Lists are one way to show multiple items. What about **grids** like a photo gallery? That's next!
