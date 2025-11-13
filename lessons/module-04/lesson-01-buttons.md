# Module 4, Lesson 1: Making Things Clickable (Buttons)

## From Static to Interactive!

Beautiful UIs are great, but **real apps respond** to user actions!

Think about every app you use:
- Tap to like a post
- Click to submit a form
- Press to send a message

**Buttons make apps interactive!**

---

## Your First Button

```dart
import 'package:flutter/material.dart';

void main() {
  runApp(
    MaterialApp(
      home: Scaffold(
        body: Center(
          child: ElevatedButton(
            onPressed: () {
              print('Button pressed!');
            },
            child: Text('Click Me'),
          ),
        ),
      ),
    ),
  );
}
```

Run this and click the button - check the console!

---

## Button Types in Flutter

### 1. ElevatedButton - Raised with shadow

```dart
ElevatedButton(
  onPressed: () {
    print('Elevated');
  },
  child: Text('Elevated Button'),
)
```

### 2. TextButton - Flat, no background

```dart
TextButton(
  onPressed: () {
    print('Text button');
  },
  child: Text('Text Button'),
)
```

### 3. OutlinedButton - Border, no fill

```dart
OutlinedButton(
  onPressed: () {
    print('Outlined');
  },
  child: Text('Outlined Button'),
)
```

### 4. IconButton - Just an icon

```dart
IconButton(
  icon: Icon(Icons.favorite),
  onPressed: () {
    print('Icon pressed');
  },
)
```

### 5. FloatingActionButton - Circular, floating

```dart
FloatingActionButton(
  onPressed: () {
    print('FAB pressed');
  },
  child: Icon(Icons.add),
)
```

---

## Styling Buttons

### Colors

```dart
ElevatedButton(
  style: ElevatedButton.styleFrom(
    backgroundColor: Colors.green,  // Flutter 3.27+
    foregroundColor: Colors.white,  // Text color
  ),
  onPressed: () {},
  child: Text('Green Button'),
)
```

### Size

```dart
ElevatedButton(
  style: ElevatedButton.styleFrom(
    minimumSize: Size(200, 50),  // Width x Height
  ),
  onPressed: () {},
  child: Text('Wide Button'),
)
```

### Rounded Corners

```dart
ElevatedButton(
  style: ElevatedButton.styleFrom(
    shape: RoundedRectangleBorder(
      borderRadius: BorderRadius.circular(30),
    ),
  ),
  onPressed: () {},
  child: Text('Rounded'),
)
```

---

## Disabled Buttons

```dart
ElevatedButton(
  onPressed: null,  // null = disabled
  child: Text('Disabled'),
)
```

The button appears grayed out and doesn't respond!

---

## Buttons with Icons

```dart
ElevatedButton.icon(
  onPressed: () {},
  icon: Icon(Icons.send),
  label: Text('Send Message'),
)
```

---

## InkWell - Make Anything Clickable

Turn ANY widget into a button:

```dart
InkWell(
  onTap: () {
    print('Container tapped!');
  },
  child: Container(
    padding: EdgeInsets.all(20),
    color: Colors.blue,
    child: Text('Tap me', style: TextStyle(color: Colors.white)),
  ),
)
```

---

## GestureDetector - Advanced Gestures

```dart
GestureDetector(
  onTap: () => print('Tapped'),
  onDoubleTap: () => print('Double tapped'),
  onLongPress: () => print('Long pressed'),
  child: Container(
    width: 200,
    height: 200,
    color: Colors.blue,
    child: Center(child: Text('Try different gestures')),
  ),
)
```

---

## Complete Example: Action Buttons

```dart
class ButtonDemo extends StatelessWidget {
  void showMessage(String message) {
    print(message);
  }
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Button Demo')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            ElevatedButton(
              onPressed: () => showMessage('Elevated'),
              child: Text('Elevated'),
            ),
            SizedBox(height: 10),
            TextButton(
              onPressed: () => showMessage('Text'),
              child: Text('Text Button'),
            ),
            SizedBox(height: 10),
            OutlinedButton(
              onPressed: () => showMessage('Outlined'),
              child: Text('Outlined'),
            ),
            SizedBox(height: 10),
            ElevatedButton.icon(
              onPressed: () => showMessage('Send'),
              icon: Icon(Icons.send),
              label: Text('Send'),
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => showMessage('FAB'),
        child: Icon(Icons.add),
      ),
    );
  }
}
```

---

## ✅ YOUR CHALLENGE: Button Playground

Create an app with:
1. At least 5 different button types
2. Each button with different styling
3. Buttons that print different messages
4. One disabled button

**Success Condition**: Working button collection! ✅

---

## What's Next?

Buttons let users trigger actions. But what about **getting INPUT** from users? Next: Text fields!
