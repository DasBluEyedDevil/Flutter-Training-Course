# Module 2, Lesson 5: The Container Widget

## The Swiss Army Knife of Widgets

If widgets were tools, `Container` would be a Swiss Army knife - it does MANY things:
- Acts as a **box** to hold other widgets
- Adds **padding** (breathing room inside)
- Adds **margin** (spacing outside)
- Sets **background color**
- Adds **borders**
- Makes **rounded corners**
- Sets **width and height**

**Container is the most versatile widget you'll use!**

---

## The Empty Container

The simplest container:

```dart
Container()
```

You can't see it because it's invisible and empty! Let's give it color:

```dart
Container(
  color: Colors.blue,
  width: 100,
  height: 100,
)
```

Now you have a blue square!

---

## Container with a Child

Containers can hold other widgets:

```dart
Container(
  color: Colors.blue,
  padding: EdgeInsets.all(20),
  child: Text(
    'Hello!',
    style: TextStyle(color: Colors.white),
  ),
)
```

---

## Padding - Space Inside

**Conceptual**: Padding is like bubble wrap inside a box.

```dart
// Padding on all sides
Container(
  color: Colors.blue,
  padding: EdgeInsets.all(20),
  child: Text('Padded'),
)

// Different padding per side
Container(
  padding: EdgeInsets.only(
    left: 10,
    right: 10,
    top: 20,
    bottom: 20,
  ),
  child: Text('Custom Padding'),
)

// Symmetric padding
Container(
  padding: EdgeInsets.symmetric(
    horizontal: 20,  // left and right
    vertical: 10,    // top and bottom
  ),
  child: Text('Symmetric'),
)
```

---

## Margin - Space Outside

**Conceptual**: Margin is like the space between boxes on a shelf.

```dart
Container(
  margin: EdgeInsets.all(20),
  color: Colors.red,
  child: Text('Has margin'),
)
```

**Margin vs Padding**:
- **Padding**: Space between container edge and its child (inside)
- **Margin**: Space between container and other widgets (outside)

---

## Width and Height

```dart
Container(
  width: 200,
  height: 100,
  color: Colors.green,
)
```

**Special sizes**:
```dart
// Take up all available width
Container(
  width: double.infinity,
  height: 100,
  color: Colors.orange,
)

// Take up all available height
Container(
  width: 100,
  height: double.infinity,
  color: Colors.purple,
)
```

---

## BoxDecoration - Advanced Styling

For more complex styling, use `decoration`:

```dart
Container(
  width: 200,
  height: 100,
  decoration: BoxDecoration(
    color: Colors.blue,
    borderRadius: BorderRadius.circular(20),  // Rounded corners
  ),
)
```

**Note**: When using `decoration`, put `color` inside `BoxDecoration`, not directly on Container!

---

## Rounded Corners

```dart
Container(
  width: 200,
  height: 100,
  decoration: BoxDecoration(
    color: Colors.purple,
    borderRadius: BorderRadius.circular(15),
  ),
)
```

**Different corner radii**:
```dart
decoration: BoxDecoration(
  color: Colors.blue,
  borderRadius: BorderRadius.only(
    topLeft: Radius.circular(20),
    topRight: Radius.circular(20),
    bottomLeft: Radius.circular(0),
    bottomRight: Radius.circular(0),
  ),
)
```

---

## Borders

```dart
Container(
  width: 200,
  height: 100,
  decoration: BoxDecoration(
    color: Colors.white,
    border: Border.all(
      color: Colors.blue,
      width: 3,
    ),
    borderRadius: BorderRadius.circular(10),
  ),
  child: Center(child: Text('Bordered')),
)
```

---

## Shadows

```dart
Container(
  width: 200,
  height: 100,
  decoration: BoxDecoration(
    color: Colors.white,
    borderRadius: BorderRadius.circular(15),
    boxShadow: [
      BoxShadow(
        color: Colors.grey,
        blurRadius: 10,
        offset: Offset(0, 5),  // x, y
      ),
    ],
  ),
  child: Center(child: Text('Has shadow')),
)
```

---

## Gradients

```dart
Container(
  width: 200,
  height: 100,
  decoration: BoxDecoration(
    gradient: LinearGradient(
      colors: [Colors.blue, Colors.purple],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
    ),
    borderRadius: BorderRadius.circular(15),
  ),
)
```

---

## Complete Card Example

Let's combine everything to create a nice card:

```dart
Container(
  margin: EdgeInsets.all(20),
  padding: EdgeInsets.all(20),
  decoration: BoxDecoration(
    color: Colors.white,
    borderRadius: BorderRadius.circular(15),
    boxShadow: [
      BoxShadow(
        color: Colors.grey.withOpacity(0.5),
        blurRadius: 10,
        offset: Offset(0, 3),
      ),
    ],
  ),
  child: Column(
    mainAxisSize: MainAxisSize.min,
    children: [
      Text(
        'Card Title',
        style: TextStyle(
          fontSize: 20,
          fontWeight: FontWeight.bold,
        ),
      ),
      SizedBox(height: 10),
      Text(
        'This is a nice card with shadow and rounded corners!',
        textAlign: TextAlign.center,
      ),
    ],
  ),
)
```

---

## Alignment Inside Container

```dart
Container(
  width: 200,
  height: 200,
  color: Colors.blue,
  alignment: Alignment.center,  // Center the child
  child: Text('Centered'),
)
```

**Alignment options**:
- `Alignment.topLeft`
- `Alignment.topCenter`
- `Alignment.topRight`
- `Alignment.centerLeft`
- `Alignment.center`
- `Alignment.centerRight`
- `Alignment.bottomLeft`
- `Alignment.bottomCenter`
- `Alignment.bottomRight`

---

## ✅ YOUR CHALLENGE: Build a Business Card

Create a business card using Container with:
1. Rounded corners
2. A nice color or gradient
3. Shadow
4. Padding
5. Your name and title

**Example layout**:
```
┌──────────────────────────┐
│                          │
│      Your Name           │
│      Your Title          │
│      your@email.com      │
│                          │
└──────────────────────────┘
```

**Success Condition**: You've created a styled container that looks like a business card! ✅

---

## What Did We Learn?

- ✅ Container is the Swiss Army knife widget
- ✅ `padding` adds space inside
- ✅ `margin` adds space outside
- ✅ `width` and `height` control size
- ✅ `BoxDecoration` for advanced styling
- ✅ Borders, shadows, gradients, rounded corners
- ✅ `alignment` positions child inside

---

## What's Next?

You've learned individual widgets (Text, Image, Container). Now it's time to learn how to **arrange multiple widgets** on screen! In the next lesson, we'll explore **Column and Row** - the building blocks of layouts.
