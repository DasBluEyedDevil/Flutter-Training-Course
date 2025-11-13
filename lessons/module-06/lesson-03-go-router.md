# Module 6, Lesson 3: Modern Navigation with GoRouter

## The Evolution of Navigation

You've learned two navigation approaches:
1. **Basic Navigation**: `Navigator.push(MaterialPageRoute(...))`
2. **Named Routes**: `Navigator.pushNamed('/route')`

Both work, but they're **imperative** - you tell Flutter exactly what to do, step by step.

**Problem with imperative navigation:**
- Hard to handle deep links (`myapp://product/123`)
- Hard to sync URL bar on web
- Hard to manage complex navigation state
- Difficult to test

**Solution: Declarative Navigation with GoRouter!**

Think of it like building with LEGO blocks:
- **Imperative**: "Take this block, put it here, now take that block..."
- **Declarative**: "Here's the blueprint, you build it!"

---

## What is GoRouter?

**GoRouter** is Flutter's official modern routing solution:
- Built on Navigation 2.0 API
- URL-based navigation
- Deep linking support out of the box
- Type-safe routes
- Web-friendly (URL bar works!)
- Maintained by Flutter team

**Current version**: 17.0.0 (Flutter 3.29+, Dart 3.7+)

---

## Installation

```yaml
# pubspec.yaml
dependencies:
  flutter:
    sdk: flutter
  go_router: ^17.0.0
```

Run: `flutter pub get`

---

## Your First GoRouter

```dart
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  // Define router
  final GoRouter _router = GoRouter(
    routes: [
      GoRoute(
        path: '/',
        builder: (context, state) => HomeScreen(),
      ),
      GoRoute(
        path: '/details',
        builder: (context, state) => DetailsScreen(),
      ),
    ],
  );

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      routerConfig: _router,  // Use router config!
      title: 'GoRouter Demo',
    );
  }
}

class HomeScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Home')),
      body: Center(
        child: ElevatedButton(
          onPressed: () {
            // Navigate with go()
            context.go('/details');
          },
          child: Text('Go to Details'),
        ),
      ),
    );
  }
}

class DetailsScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Details')),
      body: Center(
        child: ElevatedButton(
          onPressed: () {
            // Go back
            context.go('/');
          },
          child: Text('Back to Home'),
        ),
      ),
    );
  }
}
```

**Key differences:**
- Use `MaterialApp.router` instead of `MaterialApp`
- Pass `routerConfig` instead of `routes`
- Navigate with `context.go('/path')` instead of `Navigator.pushNamed`

---

## Path Parameters (Dynamic Routes)

Handle URLs like `/user/123` or `/product/456`:

```dart
final router = GoRouter(
  routes: [
    GoRoute(
      path: '/',
      builder: (context, state) => HomeScreen(),
    ),
    GoRoute(
      path: '/user/:userId',  // :userId is a path parameter
      builder: (context, state) {
        final userId = state.pathParameters['userId']!;
        return UserScreen(userId: userId);
      },
    ),
    GoRoute(
      path: '/product/:productId',
      builder: (context, state) {
        final productId = state.pathParameters['productId']!;
        return ProductScreen(productId: productId);
      },
    ),
  ],
);

// Navigate
context.go('/user/42');
context.go('/product/laptop-123');
```

---

## Query Parameters

Handle URLs like `/search?q=flutter&sort=newest`:

```dart
GoRoute(
  path: '/search',
  builder: (context, state) {
    final query = state.uri.queryParameters['q'] ?? '';
    final sort = state.uri.queryParameters['sort'] ?? 'relevance';
    return SearchScreen(query: query, sort: sort);
  },
),

// Navigate
context.go('/search?q=flutter&sort=newest');
```

---

## Complete E-Commerce Example

```dart
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

void main() {
  runApp(MyApp());
}

class Product {
  final String id;
  final String name;
  final double price;

  Product({required this.id, required this.name, required this.price});
}

class MyApp extends StatelessWidget {
  final List<Product> products = [
    Product(id: 'laptop', name: 'Laptop', price: 999.99),
    Product(id: 'mouse', name: 'Mouse', price: 29.99),
    Product(id: 'keyboard', name: 'Keyboard', price: 79.99),
  ];

  late final GoRouter _router = GoRouter(
    routes: [
      GoRoute(
        path: '/',
        builder: (context, state) => HomeScreen(),
      ),
      GoRoute(
        path: '/products',
        builder: (context, state) => ProductsScreen(products: products),
      ),
      GoRoute(
        path: '/product/:productId',
        builder: (context, state) {
          final productId = state.pathParameters['productId']!;
          final product = products.firstWhere((p) => p.id == productId);
          return ProductDetailScreen(product: product);
        },
      ),
      GoRoute(
        path: '/cart',
        builder: (context, state) => CartScreen(),
      ),
    ],
  );

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      routerConfig: _router,
      title: 'E-Commerce',
    );
  }
}

class HomeScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('E-Commerce'),
        actions: [
          IconButton(
            icon: Icon(Icons.shopping_cart),
            onPressed: () => context.go('/cart'),
          ),
        ],
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text('Welcome to our store!', style: TextStyle(fontSize: 24)),
            SizedBox(height: 24),
            ElevatedButton(
              onPressed: () => context.go('/products'),
              child: Text('Browse Products'),
            ),
          ],
        ),
      ),
    );
  }
}

class ProductsScreen extends StatelessWidget {
  final List<Product> products;

  ProductsScreen({required this.products});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Products'),
        actions: [
          IconButton(
            icon: Icon(Icons.shopping_cart),
            onPressed: () => context.go('/cart'),
          ),
        ],
      ),
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
              context.go('/product/${product.id}');
            },
          );
        },
      ),
    );
  }
}

class ProductDetailScreen extends StatelessWidget {
  final Product product;

  ProductDetailScreen({required this.product});

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
              height: 250,
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
            Spacer(),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: () {
                  context.go('/cart');
                },
                child: Text('Add to Cart'),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class CartScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Cart')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.shopping_cart, size: 100, color: Colors.grey),
            SizedBox(height: 16),
            Text('Your cart is empty'),
            SizedBox(height: 24),
            ElevatedButton(
              onPressed: () => context.go('/products'),
              child: Text('Continue Shopping'),
            ),
          ],
        ),
      ),
    );
  }
}
```

---

## go() vs push()

GoRouter provides two navigation methods:

### context.go() - Replaces Current Route
```dart
context.go('/details');
// Stack: [Home] → [Details]

context.go('/settings');
// Stack: [Home] → [Settings]  (Details is REPLACED)
```

**Use for**: Main navigation where you want to replace the current screen

### context.push() - Adds to Stack
```dart
context.push('/details');
// Stack: [Home, Details]

context.push('/settings');
// Stack: [Home, Details, Settings]  (Details is KEPT)
```

**Use for**: Modal-style navigation where you want back button to work

**Best Practice**: Prefer `go()` for most cases, use `push()` for modals/overlays.

---

## Named Routes (Type-Safe)

Instead of string paths everywhere, use named routes:

```dart
final router = GoRouter(
  routes: [
    GoRoute(
      path: '/',
      name: 'home',  // Give it a name!
      builder: (context, state) => HomeScreen(),
    ),
    GoRoute(
      path: '/product/:id',
      name: 'product',
      builder: (context, state) {
        final id = state.pathParameters['id']!;
        return ProductScreen(productId: id);
      },
    ),
  ],
);

// Navigate by name
context.goNamed('home');
context.goNamed('product', pathParameters: {'id': '123'});

// With query parameters
context.goNamed('search', queryParameters: {'q': 'flutter', 'sort': 'newest'});
```

---

## Redirects (Route Guards)

Protect routes that require authentication:

```dart
class AuthService {
  bool isLoggedIn = false;
}

final authService = AuthService();

final router = GoRouter(
  redirect: (context, state) {
    final isLoggedIn = authService.isLoggedIn;
    final isGoingToLogin = state.matchedLocation == '/login';

    // Not logged in and not going to login? Redirect to login!
    if (!isLoggedIn && !isGoingToLogin) {
      return '/login';
    }

    // Logged in and going to login? Redirect to home!
    if (isLoggedIn && isGoingToLogin) {
      return '/';
    }

    // No redirect needed
    return null;
  },
  routes: [
    GoRoute(
      path: '/login',
      builder: (context, state) => LoginScreen(),
    ),
    GoRoute(
      path: '/',
      builder: (context, state) => HomeScreen(),
    ),
    GoRoute(
      path: '/profile',
      builder: (context, state) => ProfileScreen(),
    ),
  ],
);
```

**Automatic protection!** Try to access `/profile` without logging in → redirected to `/login`.

---

## Error Handling (404 Page)

```dart
final router = GoRouter(
  routes: [
    GoRoute(
      path: '/',
      builder: (context, state) => HomeScreen(),
    ),
  ],
  errorBuilder: (context, state) => Scaffold(
    appBar: AppBar(title: Text('404')),
    body: Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.error_outline, size: 100, color: Colors.red),
          SizedBox(height: 16),
          Text('Page not found: ${state.matchedLocation}'),
          SizedBox(height: 24),
          ElevatedButton(
            onPressed: () => context.go('/'),
            child: Text('Go Home'),
          ),
        ],
      ),
    ),
  ),
);
```

---

## Nested Navigation (Sub-routes)

Create child routes:

```dart
GoRoute(
  path: '/settings',
  builder: (context, state) => SettingsScreen(),
  routes: [
    // Child route: /settings/account
    GoRoute(
      path: 'account',
      builder: (context, state) => AccountSettingsScreen(),
    ),
    // Child route: /settings/notifications
    GoRoute(
      path: 'notifications',
      builder: (context, state) => NotificationSettingsScreen(),
    ),
  ],
),

// Navigate
context.go('/settings/account');
context.go('/settings/notifications');
```

---

## Refresh Listener (React to Changes)

```dart
class AuthNotifier extends ChangeNotifier {
  bool _isLoggedIn = false;

  bool get isLoggedIn => _isLoggedIn;

  void login() {
    _isLoggedIn = true;
    notifyListeners();  // GoRouter will refresh!
  }

  void logout() {
    _isLoggedIn = false;
    notifyListeners();
  }
}

final authNotifier = AuthNotifier();

final router = GoRouter(
  refreshListenable: authNotifier,  // Listen to auth changes!
  redirect: (context, state) {
    if (!authNotifier.isLoggedIn && state.matchedLocation != '/login') {
      return '/login';
    }
    return null;
  },
  routes: [...],
);
```

**When user logs out → GoRouter automatically redirects!**

---

## Complete Authentication Example

```dart
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class AuthService extends ChangeNotifier {
  bool _isLoggedIn = false;

  bool get isLoggedIn => _isLoggedIn;

  void login() {
    _isLoggedIn = true;
    notifyListeners();
  }

  void logout() {
    _isLoggedIn = false;
    notifyListeners();
  }
}

void main() {
  final authService = AuthService();

  final router = GoRouter(
    refreshListenable: authService,
    redirect: (context, state) {
      final isLoggedIn = authService.isLoggedIn;
      final isGoingToLogin = state.matchedLocation == '/login';

      if (!isLoggedIn && !isGoingToLogin) {
        return '/login';
      }

      if (isLoggedIn && isGoingToLogin) {
        return '/';
      }

      return null;
    },
    routes: [
      GoRoute(
        path: '/login',
        builder: (context, state) => LoginScreen(authService: authService),
      ),
      GoRoute(
        path: '/',
        builder: (context, state) => HomeScreen(authService: authService),
      ),
      GoRoute(
        path: '/profile',
        builder: (context, state) => ProfileScreen(authService: authService),
      ),
    ],
  );

  runApp(MaterialApp.router(routerConfig: router));
}

class LoginScreen extends StatelessWidget {
  final AuthService authService;

  LoginScreen({required this.authService});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Login')),
      body: Center(
        child: ElevatedButton(
          onPressed: () {
            authService.login();
            // GoRouter automatically redirects to home!
          },
          child: Text('Login'),
        ),
      ),
    );
  }
}

class HomeScreen extends StatelessWidget {
  final AuthService authService;

  HomeScreen({required this.authService});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Home'),
        actions: [
          IconButton(
            icon: Icon(Icons.logout),
            onPressed: () {
              authService.logout();
              // Automatically redirected to login!
            },
          ),
        ],
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text('Welcome! You are logged in.'),
            SizedBox(height: 24),
            ElevatedButton(
              onPressed: () => context.go('/profile'),
              child: Text('View Profile'),
            ),
          ],
        ),
      ),
    );
  }
}

class ProfileScreen extends StatelessWidget {
  final AuthService authService;

  ProfileScreen({required this.authService});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Profile')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            CircleAvatar(radius: 50, child: Icon(Icons.person, size: 50)),
            SizedBox(height: 16),
            Text('Your Profile', style: TextStyle(fontSize: 24)),
            SizedBox(height: 24),
            ElevatedButton(
              onPressed: () {
                authService.logout();
              },
              child: Text('Logout'),
              style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
            ),
          ],
        ),
      ),
    );
  }
}
```

**Try it:**
1. App starts → Not logged in → Redirects to `/login`
2. Click "Login" → Redirects to `/`
3. Try to access `/profile` → Works (you're logged in)
4. Click "Logout" → Redirects to `/login`

---

## Common Mistakes

❌ **Mistake 1**: Using `MaterialApp` instead of `MaterialApp.router`
```dart
MaterialApp(
  home: HomeScreen(),  // Won't work with GoRouter!
)
```

✅ **Fix**:
```dart
MaterialApp.router(
  routerConfig: router,
)
```

❌ **Mistake 2**: Forgetting slashes in paths
```dart
GoRoute(path: 'home', ...)  // Wrong!
```

✅ **Fix**:
```dart
GoRoute(path: '/home', ...)  // Must start with /
```

❌ **Mistake 3**: Using `Navigator.push` instead of `context.go`
```dart
Navigator.push(context, ...)  // Bypasses GoRouter!
```

✅ **Fix**:
```dart
context.go('/route')  // Use GoRouter methods
```

---

## What Did We Learn?

- ✅ GoRouter for modern, declarative navigation
- ✅ Path parameters for dynamic routes (`/user/:id`)
- ✅ Query parameters (`/search?q=flutter`)
- ✅ `context.go()` vs `context.push()`
- ✅ Named routes for type safety
- ✅ Redirects for authentication guards
- ✅ Error handling with errorBuilder
- ✅ Nested routes with sub-paths
- ✅ Refresh listener for reactive redirects

---

## Lesson Checkpoint

### Quiz

**Question 1**: What's the main advantage of GoRouter over basic Navigator?
A) It's faster at rendering widgets
B) It provides URL-based navigation and deep linking support
C) It uses less memory
D) It doesn't require any setup

**Question 2**: Which method should you prefer for most navigation cases?
A) context.push()
B) Navigator.pushNamed()
C) context.go()
D) Navigator.push()

**Question 3**: How do you access a path parameter in GoRouter?
A) `state.params['id']`
B) `state.pathParameters['id']`
C) `context.getParameter('id')`
D) `router.getParam('id')`

---

## Why This Matters

**URL-based navigation is crucial for:**
- **Web apps**: Users can bookmark and share specific pages
- **Deep linking**: Open your app directly to a product page from a marketing email
- **SEO**: Search engines can index your Flutter web app
- **State management**: The URL becomes your source of truth
- **Testing**: Easy to test specific routes without complex widget trees

**Real-world scenario**: You're building a social media app. A user shares a post link: `myapp://post/abc123`. With GoRouter, this automatically opens your app to that exact post - no complex routing logic needed!

---

## Answer Key
1. **B** - GoRouter provides URL-based navigation and deep linking support, making it ideal for web apps and mobile deep linking
2. **C** - context.go() is preferred because it replaces the current route and works well with deep links, while push() adds to the stack
3. **B** - Path parameters are accessed via `state.pathParameters['paramName']` in GoRouter

---

**Next up is: Module 6, Lesson 4: Deep Linking**
