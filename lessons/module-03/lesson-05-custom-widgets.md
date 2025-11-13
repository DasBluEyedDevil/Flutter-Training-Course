# Module 3, Lesson 5: Creating Custom Widgets

## Why Custom Widgets?

You keep writing the same code over and over:
- Product cards
- List items
- Buttons with icons

**Solution**: Create **custom widgets** - reusable components!

---

## Extracting a Widget

**Before**: Messy code with repetition

```dart
// Repeated 3 times!
Container(
  padding: EdgeInsets.all(16),
  decoration: BoxDecoration(
    color: Colors.white,
    borderRadius: BorderRadius.circular(8),
    boxShadow: [BoxShadow(color: Colors.grey, blurRadius: 4)],
  ),
  child: Text(data),
)
```

**After**: Clean custom widget

```dart
// Define once
class CustomCard extends StatelessWidget {
  final String text;
  
  CustomCard({required this.text});
  
  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(8),
        boxShadow: [BoxShadow(color: Colors.grey, blurRadius: 4)],
      ),
      child: Text(text),
    );
  }
}

// Use many times
CustomCard(text: 'Hello'),
CustomCard(text: 'World'),
```

---

## Product Card Example

```dart
class ProductCard extends StatelessWidget {
  final String name;
  final double price;
  final String imageUrl;
  
  ProductCard({
    required this.name,
    required this.price,
    required this.imageUrl,
  });
  
  @override
  Widget build(BuildContext context) {
    return Card(
      child: Column(
        children: [
          Image.network(imageUrl, height: 150, fit: BoxFit.cover),
          Padding(
            padding: EdgeInsets.all(8),
            child: Column(
              children: [
                Text(name, style: TextStyle(fontWeight: FontWeight.bold)),
                SizedBox(height: 4),
                Text('\$$price', style: TextStyle(color: Colors.green)),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

// Usage:
GridView.count(
  crossAxisCount: 2,
  children: [
    ProductCard(name: 'Laptop', price: 999.99, imageUrl: 'url1'),
    ProductCard(name: 'Mouse', price: 29.99, imageUrl: 'url2'),
    ProductCard(name: 'Keyboard', price: 79.99, imageUrl: 'url3'),
  ],
)
```

---

## Passing Callbacks

Make widgets interactive:

```dart
class CustomButton extends StatelessWidget {
  final String label;
  final VoidCallback onPressed;  // Function parameter!
  
  CustomButton({required this.label, required this.onPressed});
  
  @override
  Widget build(BuildContext context) {
    return ElevatedButton(
      onPressed: onPressed,
      child: Text(label),
    );
  }
}

// Usage:
CustomButton(
  label: 'Click Me',
  onPressed: () {
    print('Button clicked!');
  },
)
```

---

## Optional Parameters

```dart
class UserAvatar extends StatelessWidget {
  final String imageUrl;
  final double size;
  final Color borderColor;
  
  UserAvatar({
    required this.imageUrl,
    this.size = 50,  // Default value
    this.borderColor = Colors.blue,  // Default
  });
  
  @override
  Widget build(BuildContext context) {
    return Container(
      width: size,
      height: size,
      decoration: BoxDecoration(
        shape: BoxShape.circle,
        border: Border.all(color: borderColor, width: 2),
      ),
      child: ClipOval(
        child: Image.network(imageUrl, fit: BoxFit.cover),
      ),
    );
  }
}

// Usage:
UserAvatar(imageUrl: 'url'),  // Uses defaults
UserAvatar(imageUrl: 'url', size: 80),  // Custom size
UserAvatar(imageUrl: 'url', size: 100, borderColor: Colors.red),
```

---

## Widget Organization

**Project structure:**

```
lib/
  main.dart
  widgets/
    custom_button.dart
    product_card.dart
    user_avatar.dart
```

**Import and use:**

```dart
import 'widgets/product_card.dart';

// Now use ProductCard anywhere
```

---

## ✅ YOUR CHALLENGE: Build a Comment Widget

Create a `CommentWidget` with:
- User avatar
- Username
- Comment text
- Timestamp

Use it to display 5 comments in a ListView!

**Success Condition**: Reusable comment widget! ✅

---

## What's Next?

You can now build and organize custom widgets! In the final Module 3 lessons, we'll cover **scrolling techniques** and build a complete **mini-project** combining everything!
