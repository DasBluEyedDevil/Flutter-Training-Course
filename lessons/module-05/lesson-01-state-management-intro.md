# Module 5, Lesson 1: Understanding State Management

## The setState Limitation

setState() works great for simple apps. But imagine:
- Shopping cart visible on multiple screens
- User profile needed everywhere
- Theme that affects entire app

**Passing data through many widgets = nightmare!**

This is called "**prop drilling**":

```
HomeScreen
  └─ ProductList
      └─ ProductCard
          └─ AddToCartButton
              // Need cart data from way up top!
```

**Solution**: **State Management** - making data available app-wide!

---

## What is State Management?

**Concept**: A central place to store and manage app data.

Think of it like:
- **setState**: Your wallet (keep money on you)
- **State Management**: Your bank account (access from anywhere)

---

## Popular Solutions

1. **Provider** - Flutter team's recommendation, simple
2. **Riverpod** - Newer, more powerful
3. **Bloc** - Enterprise apps
4. **GetX** - All-in-one solution

**We'll learn Provider (easiest to start)!**

---

## When to Use State Management?

### Use setState when:
- Data used in ONE widget
- Simple toggles/counters
- Temporary UI state

### Use Provider when:
- Data shared across multiple widgets
- App-wide state (theme, auth)
- Shopping carts, favorites, etc.

---

## Provider Basics

**Step 1**: Add to `pubspec.yaml`:

```yaml
dependencies:
  flutter:
    sdk: flutter
  provider: ^6.1.1
```

Run: `flutter pub get`

**Step 2**: Create a model:

```dart
import 'package:flutter/foundation.dart';

class Counter with ChangeNotifier {
  int _count = 0;
  
  int get count => _count;
  
  void increment() {
    _count++;
    notifyListeners();  // Tells widgets to rebuild!
  }
}
```

**Step 3**: Provide it:

```dart
void main() {
  runApp(
    ChangeNotifierProvider(
      create: (context) => Counter(),
      child: MyApp(),
    ),
  );
}
```

**Step 4**: Consume it:

```dart
class CounterDisplay extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final counter = Provider.of<Counter>(context);
    
    return Text('${counter.count}');
  }
}
```

---

## Complete Counter with Provider

```dart
import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart';
import 'package:provider/provider.dart';

// 1. Model
class Counter with ChangeNotifier {
  int _count = 0;
  
  int get count => _count;
  
  void increment() {
    _count++;
    notifyListeners();
  }
  
  void decrement() {
    _count--;
    notifyListeners();
  }
}

// 2. Main - Provide
void main() {
  runApp(
    ChangeNotifierProvider(
      create: (context) => Counter(),
      child: MyApp(),
    ),
  );
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(home: CounterScreen());
  }
}

// 3. Consume
class CounterScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Provider Counter')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            // Reading value
            Consumer<Counter>(
              builder: (context, counter, child) {
                return Text(
                  '${counter.count}',
                  style: TextStyle(fontSize: 48),
                );
              },
            ),
            SizedBox(height: 20),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                FloatingActionButton(
                  onPressed: () {
                    // Calling method
                    Provider.of<Counter>(context, listen: false).decrement();
                  },
                  child: Icon(Icons.remove),
                ),
                SizedBox(width: 20),
                FloatingActionButton(
                  onPressed: () {
                    context.read<Counter>().increment();  // Alternative syntax
                  },
                  child: Icon(Icons.add),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
```

---

## Provider Syntax Options

```dart
// Option 1: Provider.of
Provider.of<Counter>(context)  // Rebuilds when changes
Provider.of<Counter>(context, listen: false)  // Doesn't rebuild

// Option 2: Consumer (best for partial rebuilds)
Consumer<Counter>(
  builder: (context, counter, child) {
    return Text('${counter.count}');
  },
)

// Option 3: context extension (Flutter 3.0+)
context.watch<Counter>()  // Rebuilds
context.read<Counter>()   // Doesn't rebuild
```

---

## Shopping Cart Example

```dart
class CartModel with ChangeNotifier {
  List<String> _items = [];
  
  List<String> get items => _items;
  int get itemCount => _items.length;
  
  void addItem(String item) {
    _items.add(item);
    notifyListeners();
  }
  
  void removeItem(String item) {
    _items.remove(item);
    notifyListeners();
  }
  
  void clear() {
    _items.clear();
    notifyListeners();
  }
}

// Usage:
class ProductCard extends StatelessWidget {
  final String productName;
  
  ProductCard({required this.productName});
  
  @override
  Widget build(BuildContext context) {
    return Card(
      child: ListTile(
        title: Text(productName),
        trailing: IconButton(
          icon: Icon(Icons.add_shopping_cart),
          onPressed: () {
            context.read<CartModel>().addItem(productName);
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(content: Text('Added to cart')),
            );
          },
        ),
      ),
    );
  }
}

class CartBadge extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Consumer<CartModel>(
      builder: (context, cart, child) {
        return Stack(
          children: [
            Icon(Icons.shopping_cart),
            if (cart.itemCount > 0)
              Positioned(
                right: 0,
                top: 0,
                child: Container(
                  padding: EdgeInsets.all(2),
                  decoration: BoxDecoration(
                    color: Colors.red,
                    shape: BoxShape.circle,
                  ),
                  child: Text(
                    '${cart.itemCount}',
                    style: TextStyle(fontSize: 10, color: Colors.white),
                  ),
                ),
              ),
          ],
        );
      },
    );
  }
}
```

---

## Multiple Providers

```dart
void main() {
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => CartModel()),
        ChangeNotifierProvider(create: (_) => UserModel()),
        ChangeNotifierProvider(create: (_) => ThemeModel()),
      ],
      child: MyApp(),
    ),
  );
}
```

---

## ✅ YOUR CHALLENGE: Todo App with Provider

Create a todo app using Provider:
1. TodoModel with add/remove/toggle methods
2. Multiple screens showing same data
3. Persist count across screens

**Success Condition**: Shared state across screens! ✅

---

## What's Next?

You now understand state management basics! Next lessons cover:
- Complex state patterns
- Riverpod (modern alternative)
- Best practices
- Real-world patterns
