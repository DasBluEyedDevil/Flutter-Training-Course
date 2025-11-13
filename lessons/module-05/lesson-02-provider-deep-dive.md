# Module 5, Lesson 2: Provider Deep Dive

## Beyond the Basics

You learned Provider basics. Now let's master it with real-world patterns!

**This lesson covers:**
- Multiple providers
- ProxyProvider (providers that depend on others)
- FutureProvider & StreamProvider
- Best practices and patterns

---

## Multiple Providers Pattern

Real apps need multiple state objects:

```dart
void main() {
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => CartModel()),
        ChangeNotifierProvider(create: (_) => UserModel()),
        ChangeNotifierProvider(create: (_) => ThemeModel()),
        ChangeNotifierProvider(create: (_) => NotificationModel()),
      ],
      child: MyApp(),
    ),
  );
}
```

---

## Real Shopping Cart Example

```dart
// models/product.dart
class Product {
  final String id;
  final String name;
  final double price;
  final String imageUrl;

  Product({
    required this.id,
    required this.name,
    required this.price,
    required this.imageUrl,
  });
}

// models/cart_item.dart
class CartItem {
  final Product product;
  int quantity;

  CartItem({required this.product, this.quantity = 1});

  double get totalPrice => product.price * quantity;
}

// providers/cart_provider.dart
import 'package:flutter/foundation.dart';

class CartProvider with ChangeNotifier {
  Map<String, CartItem> _items = {};

  Map<String, CartItem> get items => _items;

  int get itemCount => _items.length;

  int get totalQuantity {
    return _items.values.fold(0, (sum, item) => sum + item.quantity);
  }

  double get totalAmount {
    return _items.values.fold(0.0, (sum, item) => sum + item.totalPrice);
  }

  void addItem(Product product) {
    if (_items.containsKey(product.id)) {
      // Increase quantity
      _items[product.id]!.quantity++;
    } else {
      // Add new item
      _items[product.id] = CartItem(product: product);
    }
    notifyListeners();
  }

  void removeItem(String productId) {
    _items.remove(productId);
    notifyListeners();
  }

  void increaseQuantity(String productId) {
    if (_items.containsKey(productId)) {
      _items[productId]!.quantity++;
      notifyListeners();
    }
  }

  void decreaseQuantity(String productId) {
    if (!_items.containsKey(productId)) return;

    if (_items[productId]!.quantity > 1) {
      _items[productId]!.quantity--;
    } else {
      _items.remove(productId);
    }
    notifyListeners();
  }

  void clear() {
    _items = {};
    notifyListeners();
  }
}
```

---

## Using the Cart Provider

```dart
// screens/product_list_screen.dart
class ProductListScreen extends StatelessWidget {
  final List<Product> products = [
    Product(id: '1', name: 'Laptop', price: 999.99, imageUrl: 'url1'),
    Product(id: '2', name: 'Mouse', price: 29.99, imageUrl: 'url2'),
    Product(id: '3', name: 'Keyboard', price: 79.99, imageUrl: 'url3'),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Products'),
        actions: [
          // Cart badge
          Consumer<CartProvider>(
            builder: (context, cart, child) {
              return Stack(
                alignment: Alignment.center,
                children: [
                  IconButton(
                    icon: Icon(Icons.shopping_cart),
                    onPressed: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(builder: (_) => CartScreen()),
                      );
                    },
                  ),
                  if (cart.totalQuantity > 0)
                    Positioned(
                      right: 8,
                      top: 8,
                      child: Container(
                        padding: EdgeInsets.all(4),
                        decoration: BoxDecoration(
                          color: Colors.red,
                          shape: BoxShape.circle,
                        ),
                        child: Text(
                          '${cart.totalQuantity}',
                          style: TextStyle(
                            color: Colors.white,
                            fontSize: 12,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ),
                    ),
                ],
              );
            },
          ),
        ],
      ),
      body: GridView.builder(
        padding: EdgeInsets.all(10),
        gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 2,
          childAspectRatio: 0.7,
          crossAxisSpacing: 10,
          mainAxisSpacing: 10,
        ),
        itemCount: products.length,
        itemBuilder: (context, index) {
          final product = products[index];
          return ProductCard(product: product);
        },
      ),
    );
  }
}

// widgets/product_card.dart
class ProductCard extends StatelessWidget {
  final Product product;

  ProductCard({required this.product});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Expanded(
            child: Image.network(
              product.imageUrl,
              fit: BoxFit.cover,
              width: double.infinity,
            ),
          ),
          Padding(
            padding: EdgeInsets.all(8),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  product.name,
                  style: TextStyle(fontWeight: FontWeight.bold),
                ),
                SizedBox(height: 4),
                Text(
                  '\$${product.price.toStringAsFixed(2)}',
                  style: TextStyle(
                    color: Colors.green,
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                SizedBox(height: 8),
                SizedBox(
                  width: double.infinity,
                  child: ElevatedButton.icon(
                    onPressed: () {
                      context.read<CartProvider>().addItem(product);
                      ScaffoldMessenger.of(context).showSnackBar(
                        SnackBar(
                          content: Text('Added to cart'),
                          duration: Duration(seconds: 1),
                        ),
                      );
                    },
                    icon: Icon(Icons.add_shopping_cart),
                    label: Text('Add'),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
```

---

## Cart Screen

```dart
// screens/cart_screen.dart
class CartScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Shopping Cart'),
      ),
      body: Consumer<CartProvider>(
        builder: (context, cart, child) {
          if (cart.itemCount == 0) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.shopping_cart_outlined, size: 100, color: Colors.grey),
                  SizedBox(height: 16),
                  Text(
                    'Your cart is empty',
                    style: TextStyle(fontSize: 18, color: Colors.grey),
                  ),
                ],
              ),
            );
          }

          return Column(
            children: [
              Expanded(
                child: ListView.builder(
                  itemCount: cart.items.length,
                  itemBuilder: (context, index) {
                    final item = cart.items.values.toList()[index];
                    return CartItemWidget(item: item);
                  },
                ),
              ),
              // Total bar
              Container(
                padding: EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: Colors.grey[100],
                  boxShadow: [
                    BoxShadow(
                      color: Colors.black12,
                      blurRadius: 4,
                      offset: Offset(0, -2),
                    ),
                  ],
                ),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Text('Total', style: TextStyle(color: Colors.grey)),
                        Text(
                          '\$${cart.totalAmount.toStringAsFixed(2)}',
                          style: TextStyle(
                            fontSize: 24,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ],
                    ),
                    ElevatedButton(
                      onPressed: () {
                        // Checkout logic
                        showDialog(
                          context: context,
                          builder: (context) => AlertDialog(
                            title: Text('Checkout'),
                            content: Text('Order placed successfully!'),
                            actions: [
                              TextButton(
                                onPressed: () {
                                  cart.clear();
                                  Navigator.pop(context);
                                  Navigator.pop(context);
                                },
                                child: Text('OK'),
                              ),
                            ],
                          ),
                        );
                      },
                      child: Text('Checkout'),
                      style: ElevatedButton.styleFrom(
                        padding: EdgeInsets.symmetric(horizontal: 32, vertical: 16),
                      ),
                    ),
                  ],
                ),
              ),
            ],
          );
        },
      ),
    );
  }
}

// widgets/cart_item_widget.dart
class CartItemWidget extends StatelessWidget {
  final CartItem item;

  CartItemWidget({required this.item});

  @override
  Widget build(BuildContext context) {
    final cart = context.read<CartProvider>();

    return Card(
      margin: EdgeInsets.symmetric(horizontal: 10, vertical: 5),
      child: Padding(
        padding: EdgeInsets.all(8),
        child: Row(
          children: [
            Image.network(
              item.product.imageUrl,
              width: 80,
              height: 80,
              fit: BoxFit.cover,
            ),
            SizedBox(width: 16),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    item.product.name,
                    style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
                  ),
                  SizedBox(height: 4),
                  Text(
                    '\$${item.product.price.toStringAsFixed(2)}',
                    style: TextStyle(color: Colors.green),
                  ),
                  SizedBox(height: 8),
                  Row(
                    children: [
                      IconButton(
                        icon: Icon(Icons.remove_circle_outline),
                        onPressed: () {
                          cart.decreaseQuantity(item.product.id);
                        },
                        padding: EdgeInsets.zero,
                        constraints: BoxConstraints(),
                      ),
                      Padding(
                        padding: EdgeInsets.symmetric(horizontal: 16),
                        child: Text(
                          '${item.quantity}',
                          style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                        ),
                      ),
                      IconButton(
                        icon: Icon(Icons.add_circle_outline),
                        onPressed: () {
                          cart.increaseQuantity(item.product.id);
                        },
                        padding: EdgeInsets.zero,
                        constraints: BoxConstraints(),
                      ),
                    ],
                  ),
                ],
              ),
            ),
            Column(
              children: [
                Text(
                  '\$${item.totalPrice.toStringAsFixed(2)}',
                  style: TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                SizedBox(height: 8),
                IconButton(
                  icon: Icon(Icons.delete, color: Colors.red),
                  onPressed: () {
                    cart.removeItem(item.product.id);
                  },
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

## FutureProvider for Async Data

Load data once and provide it:

```dart
// providers/products_provider.dart
Future<List<Product>> fetchProducts() async {
  await Future.delayed(Duration(seconds: 2));  // Simulate API call
  return [
    Product(id: '1', name: 'Laptop', price: 999.99, imageUrl: 'url1'),
    Product(id: '2', name: 'Mouse', price: 29.99, imageUrl: 'url2'),
  ];
}

// In main.dart
void main() {
  runApp(
    MultiProvider(
      providers: [
        FutureProvider<List<Product>>(
          create: (_) => fetchProducts(),
          initialData: [],
        ),
        ChangeNotifierProvider(create: (_) => CartProvider()),
      ],
      child: MyApp(),
    ),
  );
}

// Usage in widget
class ProductList extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final products = context.watch<List<Product>>();

    if (products.isEmpty) {
      return Center(child: CircularProgressIndicator());
    }

    return ListView.builder(
      itemCount: products.length,
      itemBuilder: (context, index) => ProductCard(product: products[index]),
    );
  }
}
```

---

## StreamProvider for Real-Time Data

```dart
// Example: Real-time user count
Stream<int> userCountStream() async* {
  int count = 0;
  while (true) {
    await Future.delayed(Duration(seconds: 1));
    count++;
    yield count;
  }
}

// In main.dart
StreamProvider<int>(
  create: (_) => userCountStream(),
  initialData: 0,
  child: MyApp(),
)

// Usage
Consumer<int>(
  builder: (context, userCount, child) {
    return Text('Active users: $userCount');
  },
)
```

---

## ProxyProvider (Provider that depends on another)

```dart
// Auth provider
class AuthProvider with ChangeNotifier {
  String? _userId;

  String? get userId => _userId;
  bool get isAuthenticated => _userId != null;

  void login(String id) {
    _userId = id;
    notifyListeners();
  }

  void logout() {
    _userId = null;
    notifyListeners();
  }
}

// Cart provider that needs user ID
class UserCartProvider with ChangeNotifier {
  final String userId;
  List<CartItem> _items = [];

  UserCartProvider(this.userId) {
    _loadUserCart();
  }

  void _loadUserCart() {
    // Load cart for specific user
    print('Loading cart for user: $userId');
  }

  List<CartItem> get items => _items;
}

// Setup with ProxyProvider
MultiProvider(
  providers: [
    ChangeNotifierProvider(create: (_) => AuthProvider()),
    ProxyProvider<AuthProvider, UserCartProvider?>(
      update: (context, auth, previous) {
        if (auth.userId == null) return null;
        return UserCartProvider(auth.userId!);
      },
    ),
  ],
  child: MyApp(),
)
```

---

## Best Practices

### 1. Keep Providers Focused
❌ **Bad**: One giant provider for everything
```dart
class AppProvider with ChangeNotifier {
  List<Product> products = [];
  List<CartItem> cart = [];
  User? user;
  String theme = 'light';
  // Too much!
}
```

✅ **Good**: Separate concerns
```dart
class ProductsProvider with ChangeNotifier { ... }
class CartProvider with ChangeNotifier { ... }
class UserProvider with ChangeNotifier { ... }
class ThemeProvider with ChangeNotifier { ... }
```

### 2. Use listen: false for Actions
```dart
// Reading value - needs updates
Text('${context.watch<Counter>().count}')

// Calling method - doesn't need updates
ElevatedButton(
  onPressed: () {
    context.read<Counter>().increment();  // listen: false
  },
  child: Text('Increment'),
)
```

### 3. Consumer for Partial Rebuilds
```dart
// Only rebuilds the Text, not entire Column
Column(
  children: [
    Text('Static text'),
    Consumer<Counter>(
      builder: (context, counter, child) {
        return Text('${counter.count}');
      },
    ),
    Text('More static text'),
  ],
)
```

---

## ✅ YOUR CHALLENGE: E-Commerce App

Build a complete e-commerce app with:
1. **ProductsProvider**: Manages product list
2. **CartProvider**: Shopping cart with add/remove/quantity
3. **FavoritesProvider**: Save favorite products
4. **AuthProvider**: Simple login/logout

Screens:
- Product grid
- Product detail
- Cart
- Favorites
- Profile

**Success Condition**: Multi-provider app with state shared across screens! ✅

---

## What Did We Learn?

- ✅ MultiProvider for multiple state objects
- ✅ FutureProvider for async data
- ✅ StreamProvider for real-time data
- ✅ ProxyProvider for dependent providers
- ✅ Production-ready shopping cart
- ✅ Best practices and patterns

---

## What's Next?

Provider is powerful, but there's a newer, better way: **Riverpod**!

Next lesson: **Introduction to Riverpod** - Provider's successor with compile-time safety!
