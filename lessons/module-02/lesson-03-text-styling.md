# Module 2, Lesson 3: Displaying and Styling Text

## Your First Widget Deep Dive

You've seen the `Text` widget briefly. Now let's master it! Text is the most common widget you'll use - every app shows text somewhere.

Think of the Text widget like a word processor:
- You can change the **font size**
- You can change the **color**
- You can make it **bold** or *italic*
- You can **align** it

All of this is possible with Flutter's Text widget!

---

## The Basic Text Widget

The simplest form:

```dart
import 'package:flutter/material.dart';

void main() {
  runApp(
    MaterialApp(
      home: Scaffold(
        body: Center(
          child: Text('Hello, Flutter!'),
        ),
      ),
    ),
  );
}
```

This displays plain text in the center of the screen.

---

## Introducing TextStyle

To style text, we use the `style` property with a `TextStyle`:

```dart
Text(
  'Hello, Flutter!',
  style: TextStyle(
    fontSize: 24,
    color: Colors.blue,
  ),
)
```

**Conceptual**: Think of `TextStyle` as the formatting toolbar in Word or Google Docs.

---

## Common Text Styling Options

### Font Size

```dart
Text(
  'Small Text',
  style: TextStyle(fontSize: 14),
)

Text(
  'Medium Text',
  style: TextStyle(fontSize: 20),
)

Text(
  'Large Text',
  style: TextStyle(fontSize: 32),
)
```

### Text Color

```dart
Text(
  'Red Text',
  style: TextStyle(color: Colors.red),
)

Text(
  'Blue Text',
  style: TextStyle(color: Colors.blue),
)

Text(
  'Custom Color',
  style: TextStyle(color: Color(0xFF6200EA)),
)
```

**Note**: `Color(0xFF6200EA)` is a hex color. `0xFF` means fully opaque.

### Font Weight (Bold)

```dart
Text(
  'Normal Text',
  style: TextStyle(fontWeight: FontWeight.normal),
)

Text(
  'Bold Text',
  style: TextStyle(fontWeight: FontWeight.bold),
)

Text(
  'Extra Bold',
  style: TextStyle(fontWeight: FontWeight.w900),
)
```

### Font Style (Italic)

```dart
Text(
  'Normal Text',
  style: TextStyle(fontStyle: FontStyle.normal),
)

Text(
  'Italic Text',
  style: TextStyle(fontStyle: FontStyle.italic),
)
```

### Combining Multiple Styles

```dart
Text(
  'Fancy Text!',
  style: TextStyle(
    fontSize: 28,
    color: Colors.purple,
    fontWeight: FontWeight.bold,
    fontStyle: FontStyle.italic,
  ),
)
```

---

## Text Alignment

Use the `textAlign` property:

```dart
Text(
  'Left Aligned',
  textAlign: TextAlign.left,
)

Text(
  'Center Aligned',
  textAlign: TextAlign.center,
)

Text(
  'Right Aligned',
  textAlign: TextAlign.right,
)
```

---

## Multi-line Text

### Line Breaks with \n

```dart
Text(
  'Line 1\nLine 2\nLine 3',
)
```

### Multi-line Strings

```dart
Text(
  '''This is a
  multi-line
  string''',
)
```

### Max Lines

Limit how many lines to show:

```dart
Text(
  'This is a very long text that might wrap to multiple lines',
  maxLines: 2,
  overflow: TextOverflow.ellipsis,  // Shows ... if text is cut off
)
```

---

## Letter and Word Spacing

```dart
Text(
  'Spaced Out',
  style: TextStyle(
    letterSpacing: 5.0,  // Space between letters
    wordSpacing: 10.0,   // Space between words
  ),
)
```

---

## Text Decoration

### Underline

```dart
Text(
  'Underlined Text',
  style: TextStyle(
    decoration: TextDecoration.underline,
  ),
)
```

### Strikethrough

```dart
Text(
  'Crossed Out',
  style: TextStyle(
    decoration: TextDecoration.lineThrough,
  ),
)
```

### Overline

```dart
Text(
  'Overlined Text',
  style: TextStyle(
    decoration: TextDecoration.overline,
  ),
)
```

---

## Custom Fonts

While we won't dive deep now, you can use custom fonts:

```dart
Text(
  'Custom Font',
  style: TextStyle(
    fontFamily: 'Roboto',
  ),
)
```

**Note**: You need to add font files to your project first.

---

## Complete Example

```dart
import 'package:flutter/material.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: Text('Text Styling Demo'),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(
                'Plain Text',
              ),
              SizedBox(height: 20),
              Text(
                'Large Bold Blue',
                style: TextStyle(
                  fontSize: 32,
                  fontWeight: FontWeight.bold,
                  color: Colors.blue,
                ),
              ),
              SizedBox(height: 20),
              Text(
                'Italic Purple',
                style: TextStyle(
                  fontSize: 24,
                  fontStyle: FontStyle.italic,
                  color: Colors.purple,
                ),
              ),
              SizedBox(height: 20),
              Text(
                'Underlined Red',
                style: TextStyle(
                  fontSize: 20,
                  decoration: TextDecoration.underline,
                  color: Colors.red,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
```

---

## ✅ YOUR CHALLENGE: Create a Styled Profile

Create an app that displays your profile with various text styles:

**Requirements**:
1. Your name in large, bold text
2. Your age in medium, colored text
3. Your favorite quote in italic text
4. A fun fact about you in underlined text
5. Use at least 4 different colors

**Success Condition**: Your app displays all text with different styles! ✅

---

## What Did We Learn?

- ✅ Text widget displays text
- ✅ TextStyle controls appearance
- ✅ fontSize, color, fontWeight are common properties
- ✅ textAlign controls alignment
- ✅ Can combine multiple styles
- ✅ Decorations add underlines, strikethrough

---

## What's Next?

Text is great, but apps need images too! In the next lesson, we'll learn how to display images from the internet and from your app's assets.
