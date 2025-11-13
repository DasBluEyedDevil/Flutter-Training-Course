# Module 3, Lesson 3: Layering Widgets (Stack)

## The Layer Cake Concept

Sometimes you need widgets **on top** of each other:
- Text on an image
- Badge on an icon
- Floating button over content

**Stack** lets you layer widgets like a cake!

---

## Your First Stack

```dart
Stack(
  children: [
    Container(
      width: 200,
      height: 200,
      color: Colors.blue,
    ),
    Positioned(
      top: 20,
      left: 20,
      child: Text(
        'On Top!',
        style: TextStyle(color: Colors.white, fontSize: 24),
      ),
    ),
  ],
)
```

---

## Positioned - Control Position

```dart
Stack(
  children: [
    Container(width: 300, height: 300, color: Colors.grey[300]),
    Positioned(
      top: 10,
      left: 10,
      child: Icon(Icons.star, color: Colors.yellow),
    ),
    Positioned(
      bottom: 10,
      right: 10,
      child: Icon(Icons.favorite, color: Colors.red),
    ),
  ],
)
```

---

## Image with Text Overlay

```dart
Stack(
  children: [
    Image.network(
      'https://picsum.photos/400/300',
      width: 400,
      height: 300,
      fit: BoxFit.cover,
    ),
    Positioned(
      bottom: 0,
      left: 0,
      right: 0,
      child: Container(
        color: Colors.black54,
        padding: EdgeInsets.all(10),
        child: Text(
          'Beautiful Photo',
          style: TextStyle(
            color: Colors.white,
            fontSize: 20,
          ),
        ),
      ),
    ),
  ],
)
```

---

## Centered Overlay

```dart
Stack(
  alignment: Alignment.center,  // Centers all children
  children: [
    Container(width: 200, height: 200, color: Colors.blue),
    Icon(Icons.play_circle_filled, size: 50, color: Colors.white),
  ],
)
```

---

## Badge Example

```dart
Stack(
  children: [
    Icon(Icons.shopping_cart, size: 40),
    Positioned(
      right: 0,
      top: 0,
      child: Container(
        padding: EdgeInsets.all(4),
        decoration: BoxDecoration(
          color: Colors.red,
          shape: BoxShape.circle,
        ),
        child: Text(
          '3',
          style: TextStyle(color: Colors.white, fontSize: 12),
        ),
      ),
    ),
  ],
)
```

---

## ✅ YOUR CHALLENGE: Profile Header

Create a profile header with:
1. Background image
2. Profile photo overlaid
3. Name overlay at bottom

**Success Condition**: Layered profile header! ✅

---

## What's Next?

You can now arrange widgets vertically (Column), horizontally (Row), in lists (ListView), grids (GridView), and layers (Stack)! Next: making layouts **responsive** to different screen sizes!
