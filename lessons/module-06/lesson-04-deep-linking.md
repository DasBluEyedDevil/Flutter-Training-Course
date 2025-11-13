# Module 6, Lesson 4: Deep Linking

## What is Deep Linking?

Imagine receiving an email: "Check out this product!" with a link. You tap it and:
- ❌ **Without deep linking**: Opens browser → App store → Download app → Open app → Search for product
- ✅ **With deep linking**: Opens app directly to that product!

**Deep linking** = Direct shortcuts to specific content in your app.

**Real-world examples:**
- Instagram post link → Opens Instagram app to that post
- Amazon product link → Opens Amazon app to product page
- YouTube video link → Opens YouTube app playing that video

---

## Types of Deep Links

### 1. Custom URL Schemes (Old Way)
```
myapp://product/123
instagram://user/john_doe
```

**Problems:**
- Any app can register the same scheme (security risk!)
- No fallback if app isn't installed
- Doesn't work on web

### 2. App Links (Android) & Universal Links (iOS) (Modern Way)
```
https://mycompany.com/product/123
```

**Benefits:**
- ✅ Secure (verified with your website)
- ✅ Fallback to website if app not installed
- ✅ Works on mobile, web, and desktop
- ✅ Better user experience

**We'll focus on the modern way!**

---

## How Deep Linking Works

```
User clicks link
    ↓
https://mycompany.com/product/123
    ↓
Operating System checks:
    - Is app installed?
    - Is app verified for this domain?
    ↓
If YES: Open app at /product/123
If NO:  Open browser to https://mycompany.com/product/123
```

---

## Installation

```yaml
# pubspec.yaml
dependencies:
  flutter:
    sdk: flutter
  go_router: ^17.0.0
  app_links: ^6.4.1
```

Run: `flutter pub get`

---

## Step 1: Android Configuration (App Links)

### A. Update AndroidManifest.xml

```xml
<!-- android/app/src/main/AndroidManifest.xml -->
<manifest ...>
  <application ...>
    <activity
      android:name=".MainActivity"
      ...>

      <!-- Add this intent filter -->
      <intent-filter android:autoVerify="true">
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />

        <!-- Replace with YOUR domain -->
        <data
          android:scheme="https"
          android:host="mycompany.com" />
      </intent-filter>

    </activity>
  </application>
</manifest>
```

**Key parts:**
- `android:autoVerify="true"` - Tells Android to verify ownership
- `android:scheme="https"` - Use HTTPS (secure!)
- `android:host="mycompany.com"` - Your website domain

### B. Create assetlinks.json

Host this file at: `https://mycompany.com/.well-known/assetlinks.json`

```json
[{
  "relation": ["delegate_permission/common.handle_all_urls"],
  "target": {
    "namespace": "android_app",
    "package_name": "com.mycompany.myapp",
    "sha256_cert_fingerprints": [
      "YOUR_SHA256_FINGERPRINT_HERE"
    ]
  }
}]
```

**To get SHA256 fingerprint:**

```bash
# Debug certificate (for testing)
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

# Release certificate (for production)
keytool -list -v -keystore /path/to/your/release.keystore -alias your-key-alias
```

Copy the SHA256 fingerprint from the output.

---

## Step 2: iOS Configuration (Universal Links)

### A. Update Info.plist

For Flutter 3.27+, deep linking is enabled by default. For earlier versions:

```xml
<!-- ios/Runner/Info.plist -->
<dict>
  <!-- Add these keys -->
  <key>FlutterDeepLinkingEnabled</key>
  <true/>
</dict>
```

### B. Enable Associated Domains in Xcode

1. Open `ios/Runner.xcworkspace` in Xcode
2. Select your project in the navigator
3. Go to "Signing & Capabilities" tab
4. Click "+ Capability"
5. Add "Associated Domains"
6. Add domain: `applinks:mycompany.com`

### C. Create apple-app-site-association

Host this file at: `https://mycompany.com/.well-known/apple-app-site-association`

```json
{
  "applinks": {
    "apps": [],
    "details": [
      {
        "appID": "TEAM_ID.com.mycompany.myapp",
        "paths": ["*"]
      }
    ]
  }
}
```

**To find your Team ID:**
1. Open Xcode
2. Go to project settings
3. Look at "Team" field (10-character string)

---

## Step 3: Basic Deep Link Handling

```dart
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:app_links/app_links.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  late final AppLinks _appLinks;
  final GoRouter _router = GoRouter(
    routes: [
      GoRoute(
        path: '/',
        builder: (context, state) => HomeScreen(),
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

  @override
  void initState() {
    super.initState();
    _initDeepLinks();
  }

  Future<void> _initDeepLinks() async {
    _appLinks = AppLinks();

    // Handle deep link when app is already running
    _appLinks.uriLinkStream.listen((uri) {
      print('Deep link received: $uri');
      _router.go(uri.path);
    });

    // Handle deep link that opened the app
    final initialUri = await _appLinks.getInitialLink();
    if (initialUri != null) {
      print('App opened with: $initialUri');
      _router.go(initialUri.path);
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      routerConfig: _router,
      title: 'Deep Linking Demo',
    );
  }
}

class HomeScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Home')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text('Home Screen', style: TextStyle(fontSize: 24)),
            SizedBox(height: 24),
            ElevatedButton(
              onPressed: () => context.go('/product/123'),
              child: Text('Go to Product 123'),
            ),
          ],
        ),
      ),
    );
  }
}

class ProductScreen extends StatelessWidget {
  final String productId;

  ProductScreen({required this.productId});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Product $productId')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.shopping_bag, size: 100),
            SizedBox(height: 16),
            Text(
              'Product ID: $productId',
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
            ),
            SizedBox(height: 16),
            Text('This screen was opened via deep link!'),
          ],
        ),
      ),
    );
  }
}
```

**Test it:**
1. Run the app
2. Send yourself a link: `https://mycompany.com/product/456`
3. Tap the link → App opens to Product 456!

---

## Complete E-Commerce Example with Deep Linking

```dart
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:app_links/app_links.dart';

void main() {
  runApp(ECommerceApp());
}

class Product {
  final String id;
  final String name;
  final double price;

  Product({required this.id, required this.name, required this.price});
}

class ECommerceApp extends StatefulWidget {
  @override
  _ECommerceAppState createState() => _ECommerceAppState();
}

class _ECommerceAppState extends State<ECommerceApp> {
  late final AppLinks _appLinks;

  final List<Product> products = [
    Product(id: 'laptop', name: 'Laptop', price: 999.99),
    Product(id: 'mouse', name: 'Mouse', price: 29.99),
    Product(id: 'keyboard', name: 'Keyboard', price: 79.99),
  ];

  late final GoRouter _router = GoRouter(
    routes: [
      GoRoute(
        path: '/',
        name: 'home',
        builder: (context, state) => HomeScreen(),
      ),
      GoRoute(
        path: '/products',
        name: 'products',
        builder: (context, state) => ProductListScreen(products: products),
      ),
      GoRoute(
        path: '/product/:productId',
        name: 'product',
        builder: (context, state) {
          final productId = state.pathParameters['productId']!;
          final product = products.firstWhere((p) => p.id == productId);
          return ProductDetailScreen(product: product);
        },
      ),
      GoRoute(
        path: '/cart',
        name: 'cart',
        builder: (context, state) => CartScreen(),
      ),
      GoRoute(
        path: '/category/:categoryName',
        name: 'category',
        builder: (context, state) {
          final categoryName = state.pathParameters['categoryName']!;
          return CategoryScreen(categoryName: categoryName);
        },
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
            Text('Page not found', style: TextStyle(fontSize: 24)),
            Text('Route: ${state.matchedLocation}'),
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

  @override
  void initState() {
    super.initState();
    _initDeepLinks();
  }

  Future<void> _initDeepLinks() async {
    _appLinks = AppLinks();

    // Listen for deep links when app is already open
    _appLinks.uriLinkStream.listen((uri) {
      _handleDeepLink(uri);
    });

    // Handle initial deep link that opened the app
    final initialUri = await _appLinks.getInitialLink();
    if (initialUri != null) {
      _handleDeepLink(initialUri);
    }
  }

  void _handleDeepLink(Uri uri) {
    print('Received deep link: $uri');

    // Show notification
    ScaffoldMessenger.of(_router.routerDelegate.navigatorKey.currentContext!)
        .showSnackBar(
      SnackBar(content: Text('Opened from: ${uri.toString()}')),
    );

    // Navigate to the path
    _router.go(uri.path);
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      routerConfig: _router,
      title: 'E-Commerce App',
      theme: ThemeData(primarySwatch: Colors.blue),
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
      body: GridView.count(
        crossAxisCount: 2,
        padding: EdgeInsets.all(16),
        mainAxisSpacing: 16,
        crossAxisSpacing: 16,
        children: [
          _CategoryCard(
            title: 'Electronics',
            icon: Icons.devices,
            onTap: () => context.go('/category/electronics'),
          ),
          _CategoryCard(
            title: 'Clothing',
            icon: Icons.checkroom,
            onTap: () => context.go('/category/clothing'),
          ),
          _CategoryCard(
            title: 'All Products',
            icon: Icons.list,
            onTap: () => context.go('/products'),
          ),
          _CategoryCard(
            title: 'Cart',
            icon: Icons.shopping_cart,
            onTap: () => context.go('/cart'),
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () => _showTestLinks(context),
        icon: Icon(Icons.link),
        label: Text('Test Links'),
      ),
    );
  }

  void _showTestLinks(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text('Test Deep Links'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Try these links:'),
            SizedBox(height: 8),
            SelectableText('https://mycompany.com/product/laptop'),
            SelectableText('https://mycompany.com/cart'),
            SelectableText('https://mycompany.com/category/electronics'),
            SizedBox(height: 16),
            Text(
              'Send these links via email/SMS to test deep linking!',
              style: TextStyle(fontSize: 12, fontStyle: FontStyle.italic),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: Text('Close'),
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
            Text(title, style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
          ],
        ),
      ),
    );
  }
}

class ProductListScreen extends StatelessWidget {
  final List<Product> products;

  ProductListScreen({required this.products});

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
            onTap: () => context.go('/product/${product.id}'),
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
            SizedBox(height: 16),
            Container(
              padding: EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: Colors.blue[50],
                borderRadius: BorderRadius.circular(8),
              ),
              child: Row(
                children: [
                  Icon(Icons.link, color: Colors.blue),
                  SizedBox(width: 8),
                  Expanded(
                    child: Text(
                      'Opened via deep link!',
                      style: TextStyle(color: Colors.blue[900]),
                    ),
                  ),
                ],
              ),
            ),
            Spacer(),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: () => context.go('/cart'),
                child: Text('Add to Cart'),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class CategoryScreen extends StatelessWidget {
  final String categoryName;

  CategoryScreen({required this.categoryName});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(categoryName.toUpperCase())),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.category, size: 100, color: Colors.grey),
            SizedBox(height: 16),
            Text(
              'Category: $categoryName',
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
            ),
            SizedBox(height: 8),
            Text('Products coming soon...'),
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
          ],
        ),
      ),
    );
  }
}
```

---

## Testing Deep Links

### On Android (using ADB)

```bash
# Test deep link
adb shell am start -W -a android.intent.action.VIEW -d "https://mycompany.com/product/laptop" com.mycompany.myapp

# Test another route
adb shell am start -W -a android.intent.action.VIEW -d "https://mycompany.com/cart" com.mycompany.myapp
```

### On iOS (using xcrun)

```bash
# Test deep link
xcrun simctl openurl booted "https://mycompany.com/product/laptop"

# Test another route
xcrun simctl openurl booted "https://mycompany.com/cart"
```

### Manual Testing

1. **Email yourself** the link: `https://mycompany.com/product/laptop`
2. Open email on your phone
3. Tap the link
4. App should open to product page!

---

## Verification Files Checklist

✅ **Android - assetlinks.json**
- Location: `https://mycompany.com/.well-known/assetlinks.json`
- Must be HTTPS (not HTTP)
- No redirects allowed
- Must return `Content-Type: application/json`

✅ **iOS - apple-app-site-association**
- Location: `https://mycompany.com/.well-known/apple-app-site-association`
- Must be HTTPS
- No `.json` extension!
- Must return `Content-Type: application/json`

**Test your files:**
```bash
# Test Android file
curl -I https://mycompany.com/.well-known/assetlinks.json

# Test iOS file
curl -I https://mycompany.com/.well-known/apple-app-site-association
```

Should return `200 OK` with `Content-Type: application/json`

---

## Handling Query Parameters

```dart
GoRoute(
  path: '/search',
  builder: (context, state) {
    final query = state.uri.queryParameters['q'] ?? '';
    final category = state.uri.queryParameters['category'] ?? 'all';
    return SearchScreen(query: query, category: category);
  },
),

// Deep link: https://mycompany.com/search?q=laptop&category=electronics
// Opens app to search screen with query pre-filled!
```

---

## Firebase Dynamic Links Alternative

For more advanced features (analytics, short links, campaign tracking):

```yaml
dependencies:
  firebase_dynamic_links: ^6.0.8
```

```dart
// Handle Firebase Dynamic Links
FirebaseDynamicLinks.instance.onLink.listen((dynamicLinkData) {
  final Uri deepLink = dynamicLinkData.link;
  // Handle the link
  _router.go(deepLink.path);
});
```

**Dynamic Links can:**
- Survive app installation (remember where user came from)
- Track campaign performance
- Create short links for sharing

---

## Common Issues and Solutions

### Issue 1: Link Opens Browser Instead of App

**Cause:** Verification files not accessible or incorrect

**Solution:**
```bash
# Verify your files are accessible
curl https://mycompany.com/.well-known/assetlinks.json
curl https://mycompany.com/.well-known/apple-app-site-association

# Check they return 200 OK
```

### Issue 2: Android App Not Verified

**Solution:**
```bash
# Check verification status
adb shell pm get-app-links com.mycompany.myapp

# Should show "verified" for your domain
```

### Issue 3: iOS Universal Links Not Working

**Solutions:**
- Make sure Associated Domains capability is added in Xcode
- Check Team ID is correct in apple-app-site-association
- Verify domain starts with `applinks:` in Xcode

---

## Security Best Practices

✅ **DO:**
- Use HTTPS for all deep links
- Verify domains with assetlinks.json / apple-app-site-association
- Validate incoming data from deep links
- Handle invalid/malicious links gracefully

❌ **DON'T:**
- Use HTTP (insecure!)
- Trust deep link data without validation
- Expose sensitive operations via deep links
- Use custom schemes for production (use App Links/Universal Links)

---

## ✅ YOUR CHALLENGES

### Challenge 1: Product Deep Links
Set up deep linking for a shopping app with product pages, categories, and search.

### Challenge 2: Social Media Links
Create deep links for user profiles, posts, and comments.

### Challenge 3: Authentication Flow
Handle deep links that require login (redirect to login, then to original destination).

### Challenge 4: Analytics Integration
Track which deep links are most popular using analytics.

**Success Condition**: Users can share links that open directly in your app! ✅

---

## Common Mistakes

❌ **Mistake 1**: Forgetting android:autoVerify="true"
```xml
<intent-filter>  <!-- Won't verify! -->
```

✅ **Fix**:
```xml
<intent-filter android:autoVerify="true">
```

❌ **Mistake 2**: Wrong file location
```
https://mycompany.com/assetlinks.json  ❌
```

✅ **Fix**:
```
https://mycompany.com/.well-known/assetlinks.json  ✓
```

❌ **Mistake 3**: Not handling initial link
```dart
// Only handles links when app is running!
_appLinks.uriLinkStream.listen((uri) { ... });
```

✅ **Fix**:
```dart
// Handle both cases
_appLinks.uriLinkStream.listen((uri) { ... });
final initialUri = await _appLinks.getInitialLink();
if (initialUri != null) { ... }
```

---

## What Did We Learn?

- ✅ Deep linking fundamentals and benefits
- ✅ App Links (Android) vs Universal Links (iOS)
- ✅ Setting up verification files (assetlinks.json, apple-app-site-association)
- ✅ Configuring AndroidManifest.xml and Info.plist
- ✅ Using app_links package with GoRouter
- ✅ Handling initial links and link streams
- ✅ Testing deep links with ADB and xcrun
- ✅ Security best practices

---

## Lesson Checkpoint

### Quiz

**Question 1**: What's the main advantage of App Links/Universal Links over custom URL schemes?
A) They're faster
B) They're verified with your website and have fallback to web
C) They use less battery
D) They're easier to implement

**Question 2**: Where should you host the assetlinks.json file for Android?
A) `https://example.com/assetlinks.json`
B) `https://example.com/.well-known/assetlinks.json`
C) In your app's assets folder
D) On Google Play Console

**Question 3**: Which two methods do you need to handle deep links in all scenarios?
A) `getInitialLink()` and `uriLinkStream.listen()`
B) `onDeepLink()` and `handleLink()`
C) `openUrl()` and `parseUrl()`
D) `Navigator.push()` and `Navigator.pop()`

---

## Why This Matters

**Deep linking is essential for:**

**Marketing**: Share product links via email/SMS that open directly in your app, increasing conversions by 2-3x compared to "open app → search" flows.

**User Experience**: User taps Instagram notification → Opens directly to that specific post, not the home feed. This seamless experience is expected in modern apps.

**Re-engagement**: Send push notification with deep link to abandoned cart → User taps → Opens app directly to checkout, recovering lost sales.

**Sharing**: User shares an interesting article from your news app → Friend taps link → Opens in app with content ready, creating viral growth loops.

**Cross-platform**: Same link works on iOS, Android, and Web, simplifying your marketing efforts.

**Real-world impact**: Airbnb saw 30% increase in bookings after implementing deep linking for shared listings!

---

## Answer Key
1. **B** - App Links and Universal Links are verified with your website, providing security and automatic fallback to web if app isn't installed
2. **B** - The assetlinks.json file must be hosted at `https://example.com/.well-known/assetlinks.json` for Android verification
3. **A** - You need `getInitialLink()` to handle the link that opened the app (cold start) and `uriLinkStream.listen()` to handle links while app is running

---

**Next up is: Module 6, Lesson 5: Bottom Navigation Bar**
