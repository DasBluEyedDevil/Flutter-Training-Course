# Module 6, Lesson 1: Basic Navigation

## The Multi-Screen Problem

So far, all your apps have been single-screen. But real apps need **multiple screens**:
- Home → Detail → Settings
- Login → Dashboard → Profile
- List → Edit → Confirm

**How do you move between screens in Flutter?**

**Navigator** is the answer!

---

## Think of Navigation as a Stack of Cards

Imagine a deck of cards:
- **Push**: Add a card on top (new screen covers current)
- **Pop**: Remove top card (go back to previous screen)

```
[Home Screen]
[Home Screen] → Push → [Home Screen, Detail Screen]
[Home Screen, Detail Screen] → Pop → [Home Screen]
```

This is called a **navigation stack**!

---

## Your First Navigation

```dart
import 'package:flutter/material.dart';

void main() => runApp(MaterialApp(home: HomeScreen()));

class HomeScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Home')),
      body: Center(
        child: ElevatedButton(
          onPressed: () {
            // Navigate to DetailScreen
            Navigator.push(
              context,
              MaterialPageRoute(builder: (context) => DetailScreen()),
            );
          },
          child: Text('Go to Detail'),
        ),
      ),
    );
  }
}

class DetailScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Detail')),
      body: Center(
        child: ElevatedButton(
          onPressed: () {
            // Go back
            Navigator.pop(context);
          },
          child: Text('Go Back'),
        ),
      ),
    );
  }
}
```

**Try it!** Tap "Go to Detail" → New screen slides in. Tap back button or "Go Back" → Returns to home.

---

## Understanding Navigator.push

```dart
Navigator.push(
  context,                                      // Where we are
  MaterialPageRoute(builder: (context) => DetailScreen()),  // Where we're going
);
```

**MaterialPageRoute** creates a platform-specific transition:
- **iOS**: Slide from right
- **Android**: Slide up

---

## Understanding Navigator.pop

```dart
Navigator.pop(context);
```

Removes the top screen from the stack and returns to the previous one.

**Automatic back button**: Android phones and iOS get a back arrow automatically! You only need `Navigator.pop()` for custom buttons.

---

## Passing Data to New Screen

Pass data via constructor:

```dart
class DetailScreen extends StatelessWidget {
  final String title;
  final int id;

  DetailScreen({required this.title, required this.id});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(title)),
      body: Center(
        child: Text('Item ID: $id', style: TextStyle(fontSize: 24)),
      ),
    );
  }
}

// Navigate with data
ElevatedButton(
  onPressed: () {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => DetailScreen(
          title: 'Product Detail',
          id: 42,
        ),
      ),
    );
  },
  child: Text('View Product'),
)
```

---

## Receiving Data Back from Screen

Use `await` with `Navigator.push`:

```dart
// Screen 1: Get result from Screen 2
class HomeScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Home')),
      body: Center(
        child: ElevatedButton(
          onPressed: () async {
            // Wait for result
            final result = await Navigator.push(
              context,
              MaterialPageRoute(builder: (context) => SelectColorScreen()),
            );

            if (result != null) {
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text('Selected: $result')),
              );
            }
          },
          child: Text('Select Color'),
        ),
      ),
    );
  }
}

// Screen 2: Return result
class SelectColorScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Select Color')),
      body: Column(
        children: [
          ListTile(
            leading: CircleAvatar(backgroundColor: Colors.red),
            title: Text('Red'),
            onTap: () {
              Navigator.pop(context, 'Red');  // Return data!
            },
          ),
          ListTile(
            leading: CircleAvatar(backgroundColor: Colors.blue),
            title: Text('Blue'),
            onTap: () {
              Navigator.pop(context, 'Blue');
            },
          ),
          ListTile(
            leading: CircleAvatar(backgroundColor: Colors.green),
            title: Text('Green'),
            onTap: () {
              Navigator.pop(context, 'Green');
            },
          ),
        ],
      ),
    );
  }
}
```

**Pattern**: `Navigator.pop(context, dataToReturn)`

---

## Complete Example: Product List & Detail

```dart
import 'package:flutter/material.dart';

void main() => runApp(MaterialApp(home: ProductList()));

class Product {
  final int id;
  final String name;
  final double price;
  final String description;

  Product({
    required this.id,
    required this.name,
    required this.price,
    required this.description,
  });
}

class ProductList extends StatelessWidget {
  final List<Product> products = [
    Product(id: 1, name: 'Laptop', price: 999.99, description: 'High-performance laptop'),
    Product(id: 2, name: 'Mouse', price: 29.99, description: 'Wireless mouse'),
    Product(id: 3, name: 'Keyboard', price: 79.99, description: 'Mechanical keyboard'),
    Product(id: 4, name: 'Monitor', price: 299.99, description: '27-inch display'),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Products')),
      body: ListView.builder(
        itemCount: products.length,
        itemBuilder: (context, index) {
          final product = products[index];
          return ListTile(
            leading: CircleAvatar(child: Text('\$')),
            title: Text(product.name),
            subtitle: Text('\$${product.price.toStringAsFixed(2)}'),
            trailing: Icon(Icons.arrow_forward_ios),
            onTap: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => ProductDetail(product: product),
                ),
              );
            },
          );
        },
      ),
    );
  }
}

class ProductDetail extends StatelessWidget {
  final Product product;

  ProductDetail({required this.product});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(product.name)),
      body: Padding(
        padding: EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              width: double.infinity,
              height: 200,
              color: Colors.grey[300],
              child: Icon(Icons.image, size: 100),
            ),
            SizedBox(height: 16),
            Text(
              product.name,
              style: TextStyle(fontSize: 28, fontWeight: FontWeight.bold),
            ),
            SizedBox(height: 8),
            Text(
              '\$${product.price.toStringAsFixed(2)}',
              style: TextStyle(fontSize: 24, color: Colors.green),
            ),
            SizedBox(height: 16),
            Text(
              'Description',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            SizedBox(height: 8),
            Text(
              product.description,
              style: TextStyle(fontSize: 16),
            ),
            Spacer(),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: () {
                  Navigator.pop(context);
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(content: Text('Added ${product.name} to cart')),
                  );
                },
                child: Text('Add to Cart'),
                style: ElevatedButton.styleFrom(
                  padding: EdgeInsets.all(16),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
```

---

## Custom Page Transitions

Change how screens transition:

```dart
// Fade transition
Navigator.push(
  context,
  PageRouteBuilder(
    pageBuilder: (context, animation, secondaryAnimation) => DetailScreen(),
    transitionsBuilder: (context, animation, secondaryAnimation, child) {
      return FadeTransition(
        opacity: animation,
        child: child,
      );
    },
  ),
);

// Scale transition
Navigator.push(
  context,
  PageRouteBuilder(
    pageBuilder: (context, animation, secondaryAnimation) => DetailScreen(),
    transitionsBuilder: (context, animation, secondaryAnimation, child) {
      return ScaleTransition(
        scale: animation,
        child: child,
      );
    },
  ),
);

// Slide from bottom
Navigator.push(
  context,
  PageRouteBuilder(
    pageBuilder: (context, animation, secondaryAnimation) => DetailScreen(),
    transitionsBuilder: (context, animation, secondaryAnimation, child) {
      const begin = Offset(0.0, 1.0);
      const end = Offset.zero;
      final tween = Tween(begin: begin, end: end);
      final offsetAnimation = animation.drive(tween);

      return SlideTransition(
        position: offsetAnimation,
        child: child,
      );
    },
  ),
);
```

---

## Replacing Current Screen

```dart
// Go to new screen and remove current from stack
Navigator.pushReplacement(
  context,
  MaterialPageRoute(builder: (context) => HomeScreen()),
);
```

**Use case**: Login → Home (don't want back button to go to login)

---

## Removing All Previous Screens

```dart
// Clear entire stack and go to new screen
Navigator.pushAndRemoveUntil(
  context,
  MaterialPageRoute(builder: (context) => HomeScreen()),
  (route) => false,  // Remove all previous routes
);
```

**Use case**: Logout → Login (clear all app screens)

---

## ✅ YOUR CHALLENGES

### Challenge 1: User Profile Flow
Create: Home → Profile → Edit Profile → Save (go back to home with snackbar)

### Challenge 2: Multi-Level Navigation
Create: Categories → Subcategories → Products → Product Detail (4 levels deep!)

### Challenge 3: Selection Flow
Create a screen that lets users select multiple items and returns the list to previous screen.

### Challenge 4: Custom Transition
Create a screen that slides in from the left instead of right.

**Success Condition**: Multi-screen navigation with data passing! ✅

---

## Common Mistakes

❌ **Mistake 1**: Forgetting `context`
```dart
Navigator.push(MaterialPageRoute(...));  // Error: Where's context?
```

✅ **Fix**: Always pass context
```dart
Navigator.push(context, MaterialPageRoute(...));
```

❌ **Mistake 2**: Not using `await` when expecting result
```dart
Navigator.push(context, MaterialPageRoute(...));
final result = ...;  // result is null!
```

✅ **Fix**: Use await
```dart
final result = await Navigator.push(context, MaterialPageRoute(...));
```

---

## What Did We Learn?

- ✅ Navigator as a stack of screens
- ✅ Navigator.push to go forward
- ✅ Navigator.pop to go back
- ✅ Passing data TO screens (constructor)
- ✅ Receiving data FROM screens (await + pop)
- ✅ Custom page transitions
- ✅ pushReplacement and pushAndRemoveUntil

---

## What's Next?

Basic navigation works, but gets messy for large apps. Next: **Named Routes** - organize navigation with string identifiers!
