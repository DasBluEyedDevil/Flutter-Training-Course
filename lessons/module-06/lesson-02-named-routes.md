# Module 6, Lesson 2: Named Routes

## The Problem with Basic Navigation

With basic navigation, you write this EVERYWHERE:

```dart
Navigator.push(
  context,
  MaterialPageRoute(builder: (context) => ProductDetail(product: product)),
);

Navigator.push(
  context,
  MaterialPageRoute(builder: (context) => UserProfile(userId: userId)),
);

Navigator.push(
  context,
  MaterialPageRoute(builder: (context) => SettingsScreen()),
);
```

**Problems:**
- Repetitive code
- Hard to change transitions
- No central route management
- Typos cause runtime errors

**Solution: Named Routes!**

---

## What are Named Routes?

Instead of creating MaterialPageRoute everywhere, define routes with string names:

```dart
'/': HomeScreen
'/detail': DetailScreen
'/profile': ProfileScreen
'/settings': SettingsScreen
```

Then navigate with strings:

```dart
Navigator.pushNamed(context, '/detail');
Navigator.pushNamed(context, '/profile');
```

---

## Setting Up Named Routes

```dart
import 'package:flutter/material.dart';

void main() {
  runApp(MaterialApp(
    // Define initial route
    initialRoute: '/',

    // Define all routes
    routes: {
      '/': (context) => HomeScreen(),
      '/detail': (context) => DetailScreen(),
      '/profile': (context) => ProfileScreen(),
      '/settings': (context) => SettingsScreen(),
    },
  ));
}
```

---

## Navigating with Named Routes

```dart
// Go to route
Navigator.pushNamed(context, '/detail');

// Go back (same as before)
Navigator.pop(context);

// Replace current route
Navigator.pushReplacementNamed(context, '/home');

// Clear stack and go to route
Navigator.pushNamedAndRemoveUntil(context, '/home', (route) => false);
```

---

## Passing Arguments

### Method 1: Via Navigator

```dart
// Navigate with arguments
Navigator.pushNamed(
  context,
  '/detail',
  arguments: {'productId': 123, 'name': 'Laptop'},
);

// Receive arguments in destination screen
class DetailScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final args = ModalRoute.of(context)!.settings.arguments as Map<String, dynamic>;
    final productId = args['productId'];
    final name = args['name'];

    return Scaffold(
      appBar: AppBar(title: Text(name)),
      body: Center(child: Text('Product ID: $productId')),
    );
  }
}
```

### Method 2: Type-Safe Arguments

```dart
// Define argument class
class ProductDetailArguments {
  final int productId;
  final String name;

  ProductDetailArguments({required this.productId, required this.name});
}

// Navigate
Navigator.pushNamed(
  context,
  '/detail',
  arguments: ProductDetailArguments(productId: 123, name: 'Laptop'),
);

// Receive
class DetailScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final args = ModalRoute.of(context)!.settings.arguments as ProductDetailArguments;

    return Scaffold(
      appBar: AppBar(title: Text(args.name)),
      body: Center(child: Text('Product ID: ${args.productId}')),
    );
  }
}
```

**Much safer!** Type errors caught at compile time.

---

## Complete E-Commerce Example

```dart
import 'package:flutter/material.dart';

void main() {
  runApp(MaterialApp(
    title: 'E-Commerce App',
    initialRoute: '/',
    routes: {
      '/': (context) => HomeScreen(),
      '/products': (context) => ProductsScreen(),
      '/product-detail': (context) => ProductDetailScreen(),
      '/cart': (context) => CartScreen(),
      '/checkout': (context) => CheckoutScreen(),
    },
  ));
}

// Argument classes
class ProductDetailArgs {
  final int productId;
  final String name;
  final double price;

  ProductDetailArgs({
    required this.productId,
    required this.name,
    required this.price,
  });
}

// Home Screen
class HomeScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('E-Commerce'),
        actions: [
          IconButton(
            icon: Icon(Icons.shopping_cart),
            onPressed: () => Navigator.pushNamed(context, '/cart'),
          ),
        ],
      ),
      body: GridView.count(
        crossAxisCount: 2,
        padding: EdgeInsets.all(16),
        mainAxisSpacing: 16,
        crossAxisSpacing: 16,
        children: [
          _CategoryCard(
            title: 'Electronics',
            icon: Icons.devices,
            onTap: () => Navigator.pushNamed(context, '/products'),
          ),
          _CategoryCard(
            title: 'Clothing',
            icon: Icons.checkroom,
            onTap: () => Navigator.pushNamed(context, '/products'),
          ),
          _CategoryCard(
            title: 'Books',
            icon: Icons.book,
            onTap: () => Navigator.pushNamed(context, '/products'),
          ),
          _CategoryCard(
            title: 'Home',
            icon: Icons.home,
            onTap: () => Navigator.pushNamed(context, '/products'),
          ),
        ],
      ),
    );
  }
}

class _CategoryCard extends StatelessWidget {
  final String title;
  final IconData icon;
  final VoidCallback onTap;

  _CategoryCard({required this.title, required this.icon, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: InkWell(
        onTap: onTap,
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(icon, size: 64, color: Colors.blue),
            SizedBox(height: 8),
            Text(title, style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
          ],
        ),
      ),
    );
  }
}

// Products Screen
class ProductsScreen extends StatelessWidget {
  final List<Map<String, dynamic>> products = [
    {'id': 1, 'name': 'Laptop', 'price': 999.99},
    {'id': 2, 'name': 'Mouse', 'price': 29.99},
    {'id': 3, 'name': 'Keyboard', 'price': 79.99},
    {'id': 4, 'name': 'Monitor', 'price': 299.99},
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Products'),
        actions: [
          IconButton(
            icon: Icon(Icons.shopping_cart),
            onPressed: () => Navigator.pushNamed(context, '/cart'),
          ),
        ],
      ),
      body: ListView.builder(
        itemCount: products.length,
        itemBuilder: (context, index) {
          final product = products[index];
          return ListTile(
            leading: CircleAvatar(child: Text('\$')),
            title: Text(product['name']),
            subtitle: Text('\$${product['price'].toStringAsFixed(2)}'),
            trailing: Icon(Icons.arrow_forward_ios),
            onTap: () {
              Navigator.pushNamed(
                context,
                '/product-detail',
                arguments: ProductDetailArgs(
                  productId: product['id'],
                  name: product['name'],
                  price: product['price'],
                ),
              );
            },
          );
        },
      ),
    );
  }
}

// Product Detail Screen
class ProductDetailScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final args = ModalRoute.of(context)!.settings.arguments as ProductDetailArgs;

    return Scaffold(
      appBar: AppBar(title: Text(args.name)),
      body: Padding(
        padding: EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              width: double.infinity,
              height: 250,
              color: Colors.grey[300],
              child: Icon(Icons.image, size: 100),
            ),
            SizedBox(height: 16),
            Text(
              args.name,
              style: TextStyle(fontSize: 28, fontWeight: FontWeight.bold),
            ),
            SizedBox(height: 8),
            Text(
              '\$${args.price.toStringAsFixed(2)}',
              style: TextStyle(fontSize: 24, color: Colors.green),
            ),
            SizedBox(height: 16),
            Text(
              'Product ID: ${args.productId}',
              style: TextStyle(color: Colors.grey[600]),
            ),
            Spacer(),
            Row(
              children: [
                Expanded(
                  child: ElevatedButton(
                    onPressed: () {
                      Navigator.pushNamed(context, '/cart');
                    },
                    child: Text('Add to Cart'),
                    style: ElevatedButton.styleFrom(
                      padding: EdgeInsets.all(16),
                    ),
                  ),
                ),
                SizedBox(width: 16),
                Expanded(
                  child: OutlinedButton(
                    onPressed: () {
                      Navigator.pop(context);
                    },
                    child: Text('Back'),
                    style: OutlinedButton.styleFrom(
                      padding: EdgeInsets.all(16),
                    ),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

// Cart Screen
class CartScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Shopping Cart')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.shopping_cart, size: 100, color: Colors.grey),
            SizedBox(height: 16),
            Text('Your cart is empty', style: TextStyle(fontSize: 18)),
            SizedBox(height: 24),
            ElevatedButton(
              onPressed: () {
                Navigator.pushNamed(context, '/checkout');
              },
              child: Text('Checkout'),
            ),
          ],
        ),
      ),
    );
  }
}

// Checkout Screen
class CheckoutScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Checkout')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.check_circle, size: 100, color: Colors.green),
            SizedBox(height: 16),
            Text('Order Confirmed!', style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold)),
            SizedBox(height: 24),
            ElevatedButton(
              onPressed: () {
                // Go back to home and clear navigation stack
                Navigator.pushNamedAndRemoveUntil(context, '/', (route) => false);
              },
              child: Text('Back to Home'),
            ),
          ],
        ),
      ),
    );
  }
}
```

---

## onGenerateRoute (Advanced)

For dynamic routes or custom logic:

```dart
MaterialApp(
  onGenerateRoute: (settings) {
    // Handle /product/:id
    if (settings.name?.startsWith('/product/') == true) {
      final productId = settings.name!.split('/').last;

      return MaterialPageRoute(
        builder: (context) => ProductDetailScreen(productId: productId),
      );
    }

    // Handle /user/:username
    if (settings.name?.startsWith('/user/') == true) {
      final username = settings.name!.split('/').last;

      return MaterialPageRoute(
        builder: (context) => UserProfileScreen(username: username),
      );
    }

    // Default route
    return MaterialPageRoute(builder: (context) => HomeScreen());
  },
);

// Navigate
Navigator.pushNamed(context, '/product/123');
Navigator.pushNamed(context, '/user/john_doe');
```

---

## onUnknownRoute (404 Handler)

Handle invalid routes gracefully:

```dart
MaterialApp(
  routes: {
    '/': (context) => HomeScreen(),
    '/about': (context) => AboutScreen(),
  },
  onUnknownRoute: (settings) {
    return MaterialPageRoute(
      builder: (context) => NotFoundScreen(routeName: settings.name),
    );
  },
);

class NotFoundScreen extends StatelessWidget {
  final String? routeName;

  NotFoundScreen({this.routeName});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('404')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.error_outline, size: 100, color: Colors.red),
            SizedBox(height: 16),
            Text('Page Not Found', style: TextStyle(fontSize: 24)),
            if (routeName != null)
              Text('Route: $routeName', style: TextStyle(color: Colors.grey)),
            SizedBox(height: 24),
            ElevatedButton(
              onPressed: () => Navigator.pushNamedAndRemoveUntil(context, '/', (route) => false),
              child: Text('Go Home'),
            ),
          ],
        ),
      ),
    );
  }
}
```

---

## Route Constants (Best Practice)

Avoid typos with constants:

```dart
// routes.dart
class AppRoutes {
  static const String home = '/';
  static const String products = '/products';
  static const String productDetail = '/product-detail';
  static const String cart = '/cart';
  static const String checkout = '/checkout';
  static const String profile = '/profile';
  static const String settings = '/settings';
}

// main.dart
MaterialApp(
  routes: {
    AppRoutes.home: (context) => HomeScreen(),
    AppRoutes.products: (context) => ProductsScreen(),
    AppRoutes.productDetail: (context) => ProductDetailScreen(),
    AppRoutes.cart: (context) => CartScreen(),
    AppRoutes.checkout: (context) => CheckoutScreen(),
  },
);

// Usage
Navigator.pushNamed(context, AppRoutes.productDetail);
Navigator.pushNamed(context, AppRoutes.cart);
```

**Benefits:**
- Autocomplete works
- Refactoring is easy
- Typos caught at compile time

---

## ✅ YOUR CHALLENGES

### Challenge 1: Blog App
Create named routes for: Home → Categories → Posts → Post Detail → Comments

### Challenge 2: Settings Flow
Create: Home → Settings → Account Settings → Change Password (with arguments for user data)

### Challenge 3: Authentication Flow
Create: Login → Home (clear stack), and Home → Logout → Login (clear stack)

### Challenge 4: Dynamic Routes
Use onGenerateRoute to handle /post/:id and /category/:slug

**Success Condition**: Organized navigation with named routes! ✅

---

## Named Routes vs Basic Navigation

| Feature | Basic Navigation | Named Routes |
|---------|------------------|--------------|
| **Setup** | None | Define routes upfront |
| **Navigate** | `Navigator.push(MaterialPageRoute(...))` | `Navigator.pushNamed('/route')` |
| **Arguments** | Constructor params | `arguments` parameter |
| **Type Safety** | ✓ Compile-time | Runtime (unless using constants) |
| **Centralized** | ✗ No | ✓ Yes |
| **Best For** | Small apps | Medium-large apps |

---

## What Did We Learn?

- ✅ Named routes for organized navigation
- ✅ Setting up routes in MaterialApp
- ✅ pushNamed, pushReplacementNamed, pushNamedAndRemoveUntil
- ✅ Passing arguments with named routes
- ✅ Type-safe argument classes
- ✅ onGenerateRoute for dynamic routes
- ✅ onUnknownRoute for 404 handling
- ✅ Route constants for safety

---

## What's Next?

Named routes are great, but there's an even more powerful way: **Navigation 2.0 (Router API)** - declarative navigation with deep linking support!
