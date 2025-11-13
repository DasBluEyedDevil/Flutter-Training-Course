# Module 3, Lesson 2: Photo Grids (GridView)

## When ListView Isn't Enough

Lists are great for vertical scrolling, but what about a **photo gallery** or **product catalog**? You need items arranged in a **grid** - multiple columns!

Think: Instagram explore page, Pinterest, app store icons.

**GridView** creates scrollable grids of widgets!

---

## Your First GridView

```dart
GridView.count(
  crossAxisCount: 2,  // 2 columns
  children: [
    Container(color: Colors.red),
    Container(color: Colors.blue),
    Container(color: Colors.green),
    Container(color: Colors.yellow),
  ],
)
```

Creates a 2-column grid!

---

## GridView.count - Fixed Number of Columns

```dart
import 'package:flutter/material.dart';

void main() {
  runApp(
    MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: Text('Photo Grid')),
        body: GridView.count(
          crossAxisCount: 3,
          children: List.generate(20, (index) {
            return Card(
              child: Center(
                child: Text('Item $index'),
              ),
            );
          }),
        ),
      ),
    ),
  );
}
```

---

## Spacing Between Items

```dart
GridView.count(
  crossAxisCount: 2,
  mainAxisSpacing: 10,    // Vertical spacing
  crossAxisSpacing: 10,   // Horizontal spacing
  padding: EdgeInsets.all(10),
  children: [
    // Items...
  ],
)
```

---

## GridView.builder - For Large/Dynamic Data

```dart
GridView.builder(
  gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
    crossAxisCount: 2,
    childAspectRatio: 1.0,  // Square items
  ),
  itemCount: 100,
  itemBuilder: (context, index) {
    return Card(
      child: Center(child: Text('$index')),
    );
  },
)
```

---

## Photo Gallery Example

```dart
class PhotoGallery extends StatelessWidget {
  final List<String> photos = [
    'https://picsum.photos/200/200?random=1',
    'https://picsum.photos/200/200?random=2',
    'https://picsum.photos/200/200?random=3',
    'https://picsum.photos/200/200?random=4',
    'https://picsum.photos/200/200?random=5',
    'https://picsum.photos/200/200?random=6',
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Gallery')),
      body: GridView.builder(
        gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 3,
          mainAxisSpacing: 4,
          crossAxisSpacing: 4,
        ),
        itemCount: photos.length,
        itemBuilder: (context, index) {
          return Image.network(
            photos[index],
            fit: BoxFit.cover,
          );
        },
      ),
    );
  }
}
```

---

## GridView.extent - Maximum Item Size

Instead of specifying columns, specify max width per item:

```dart
GridView.extent(
  maxCrossAxisExtent: 150,  // Max 150px per item
  children: [
    // Items adjust to fit screen width
  ],
)
```

Automatically adjusts columns based on screen size - **responsive**!

---

## ✅ YOUR CHALLENGE: Product Grid

Create a product grid with:
1. At least 9 items
2. 3 columns
3. Each item shows image and name
4. Add spacing between items

**Success Condition**: Working grid with products! ✅

---

## What's Next?

Grids arrange items in 2D. But what about **overlaying** widgets on top of each other? That's **Stack**!
