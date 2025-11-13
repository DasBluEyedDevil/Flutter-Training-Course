# Lesson 10.4: Integration Testing

## Learning Objectives
By the end of this lesson, you will be able to:
- Understand the difference between widget tests and integration tests
- Set up the integration_test package in your Flutter project
- Write integration tests that simulate real user interactions
- Run integration tests on physical devices and emulators
- Test navigation flows and multi-screen interactions
- Use IntegrationTestWidgetsFlutterBinding for device testing

---

## Introduction

### What is Integration Testing?

**Concept First:**
Imagine you're a restaurant inspector. Unit testing is like checking each ingredient separately (is the lettuce fresh? is the meat cooked?). Widget testing is like tasting individual dishes (does the burger taste good?). **Integration testing** is like experiencing the entire dining experience from start to finish—walking in, ordering, waiting, eating, and paying. You're making sure everything works together seamlessly.

In Flutter, integration testing verifies that your entire app works correctly from the user's perspective. It tests multiple widgets, screens, and services working together as a complete system.

**Jargon:**
- **Integration Test**: Tests that verify multiple parts of your app work together correctly
- **End-to-End (E2E) Test**: Tests that simulate complete user journeys through your app
- **IntegrationTestWidgetsFlutterBinding**: A test binding that allows tests to run on real devices

### Why This Matters

Integration tests catch issues that unit and widget tests miss:
- **Navigation bugs**: Does tapping "Login" actually take you to the home screen?
- **Data flow issues**: When you add an item to the cart, does it show up on the checkout screen?
- **Real device behavior**: Does your app work on actual Android and iOS devices?
- **User journey validation**: Can users complete critical tasks from start to finish?

**Real-world analogy:** Your app might have perfect individual features (like a car with a great engine, comfortable seats, and smooth steering), but integration tests ensure they work together (can you actually drive the car from point A to point B?).

---

## Section 1: Integration Testing vs Widget Testing

### Key Differences

| Aspect | Widget Testing | Integration Testing |
|--------|---------------|---------------------|
| **Scope** | Single widget/screen | Multiple screens and flows |
| **Speed** | Fast (milliseconds) | Slower (seconds) |
| **Runs on** | Host machine only | Real devices + emulators |
| **Dependencies** | Often mocked | Real services |
| **Purpose** | Verify UI components | Verify complete user flows |

### When to Use Each

**Use Widget Tests when:**
- Testing individual widgets or screens
- Verifying UI logic and interactions
- Running tests quickly during development
- Mocking external dependencies

**Use Integration Tests when:**
- Testing navigation between screens
- Verifying complete user workflows
- Testing on real devices before release
- Ensuring app works with real backend services

---

## Section 2: Setting Up Integration Testing

### Step 1: Create Integration Test Directory

In your Flutter project root, create a new directory:

```bash
mkdir integration_test
```

### Step 2: Add Integration Test Package

The `integration_test` package comes with Flutter SDK. No version needed!

```yaml
# pubspec.yaml
dev_dependencies:
  flutter_test:
    sdk: flutter
  integration_test:
    sdk: flutter  # No version number - it's part of Flutter SDK
  mocktail: ^1.0.4
```

Run:
```bash
flutter pub get
```

### Step 3: Understand the Test Structure

**integration_test/** directory structure:
```
integration_test/
├── app_test.dart              # Main integration test file
├── login_flow_test.dart       # Specific user flow tests
└── checkout_flow_test.dart    # Another flow test
```

---

## Section 3: Writing Your First Integration Test

### Example: Testing a Login Flow

Let's create a simple app and test the complete login journey.

#### Step 1: Create the App

```dart
// lib/main.dart
import 'package:flutter/material.dart';

void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Integration Test Demo',
      home: const LoginScreen(),
    );
  }
}

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  String _errorMessage = '';

  void _login() {
    final email = _emailController.text;
    final password = _passwordController.text;

    if (email.isEmpty || password.isEmpty) {
      setState(() {
        _errorMessage = 'Please fill in all fields';
      });
      return;
    }

    if (email == 'test@example.com' && password == 'password123') {
      Navigator.of(context).pushReplacement(
        MaterialPageRoute(builder: (_) => const HomeScreen()),
      );
    } else {
      setState(() {
        _errorMessage = 'Invalid credentials';
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Login')),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            TextField(
              key: const Key('emailField'),
              controller: _emailController,
              decoration: const InputDecoration(labelText: 'Email'),
            ),
            const SizedBox(height: 16),
            TextField(
              key: const Key('passwordField'),
              controller: _passwordController,
              decoration: const InputDecoration(labelText: 'Password'),
              obscureText: true,
            ),
            const SizedBox(height: 16),
            if (_errorMessage.isNotEmpty)
              Text(
                _errorMessage,
                key: const Key('errorText'),
                style: const TextStyle(color: Colors.red),
              ),
            const SizedBox(height: 16),
            ElevatedButton(
              key: const Key('loginButton'),
              onPressed: _login,
              child: const Text('Login'),
            ),
          ],
        ),
      ),
    );
  }

  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    super.dispose();
  }
}

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Home')),
      body: const Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              'Welcome!',
              key: Key('welcomeText'),
              style: TextStyle(fontSize: 24),
            ),
            SizedBox(height: 16),
            Text('You are now logged in'),
          ],
        ),
      ),
    );
  }
}
```

#### Step 2: Write the Integration Test

```dart
// integration_test/login_flow_test.dart
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:your_app/main.dart' as app;

void main() {
  // CRITICAL: Initialize the integration test binding
  // This allows tests to run on real devices
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  group('Login Flow Integration Tests', () {
    testWidgets('Complete login flow with valid credentials',
        (WidgetTester tester) async {
      // ARRANGE: Start the app
      app.main();
      await tester.pumpAndSettle();

      // Verify we're on the login screen
      expect(find.text('Login'), findsOneWidget);
      expect(find.byKey(const Key('emailField')), findsOneWidget);

      // ACT: Enter email
      await tester.enterText(
        find.byKey(const Key('emailField')),
        'test@example.com',
      );
      await tester.pumpAndSettle();

      // ACT: Enter password
      await tester.enterText(
        find.byKey(const Key('passwordField')),
        'password123',
      );
      await tester.pumpAndSettle();

      // ACT: Tap login button
      await tester.tap(find.byKey(const Key('loginButton')));
      await tester.pumpAndSettle();

      // ASSERT: Verify navigation to home screen
      expect(find.text('Welcome!'), findsOneWidget);
      expect(find.text('You are now logged in'), findsOneWidget);
      expect(find.text('Login'), findsNothing); // Login screen is gone
    });

    testWidgets('Login flow with invalid credentials shows error',
        (WidgetTester tester) async {
      // ARRANGE
      app.main();
      await tester.pumpAndSettle();

      // ACT: Enter wrong credentials
      await tester.enterText(
        find.byKey(const Key('emailField')),
        'wrong@example.com',
      );
      await tester.enterText(
        find.byKey(const Key('passwordField')),
        'wrongpassword',
      );
      await tester.pumpAndSettle();

      await tester.tap(find.byKey(const Key('loginButton')));
      await tester.pumpAndSettle();

      // ASSERT: Still on login screen with error
      expect(find.text('Login'), findsOneWidget);
      expect(find.text('Invalid credentials'), findsOneWidget);
      expect(find.text('Welcome!'), findsNothing); // Not on home screen
    });

    testWidgets('Login flow with empty fields shows validation error',
        (WidgetTester tester) async {
      // ARRANGE
      app.main();
      await tester.pumpAndSettle();

      // ACT: Tap login without entering anything
      await tester.tap(find.byKey(const Key('loginButton')));
      await tester.pumpAndSettle();

      // ASSERT: Error message appears
      expect(find.text('Please fill in all fields'), findsOneWidget);
      expect(find.text('Welcome!'), findsNothing);
    });
  });
}
```

### Key Differences from Widget Tests

1. **IntegrationTestWidgetsFlutterBinding.ensureInitialized()**
   - Required at the start of integration tests
   - Allows tests to run on real devices
   - Not needed in widget tests

2. **app.main() instead of pumping a widget**
   - Starts the entire app, not just a widget
   - Simulates launching the app like a user would

3. **More pumpAndSettle() calls**
   - Integration tests have more async operations
   - Navigation, animations, and API calls take time

---

## Section 4: Running Integration Tests

### On Desktop/Emulator (Fastest)

```bash
# Run all integration tests
flutter test integration_test

# Run a specific test file
flutter test integration_test/login_flow_test.dart

# Run with more verbose output
flutter test integration_test --verbose
```

### On Physical Device (Most Realistic)

**For Android:**
1. Connect your Android device via USB
2. Enable USB debugging in Developer Options
3. Run:
```bash
flutter devices  # Verify device is connected
flutter test integration_test --device-id=<your-device-id>
```

**For iOS:**
1. Connect your iPhone via USB
2. Trust the computer on your device
3. Run:
```bash
flutter devices
flutter test integration_test --device-id=<your-device-id>
```

### Running on Multiple Devices

```bash
# Run on all connected devices
flutter test integration_test --all
```

---

## Section 5: Advanced Integration Test Patterns

### Testing Multi-Screen Navigation

```dart
// integration_test/shopping_flow_test.dart
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:your_app/main.dart' as app;

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  testWidgets('Complete shopping flow: Browse -> Add to Cart -> Checkout',
      (WidgetTester tester) async {
    // Start app
    app.main();
    await tester.pumpAndSettle();

    // STEP 1: Browse products
    expect(find.text('Products'), findsOneWidget);

    // STEP 2: Tap on a product
    await tester.tap(find.byKey(const Key('product_1')));
    await tester.pumpAndSettle();

    // Verify product details screen
    expect(find.text('Product Details'), findsOneWidget);

    // STEP 3: Add to cart
    await tester.tap(find.byKey(const Key('addToCartButton')));
    await tester.pumpAndSettle();

    // Verify snackbar or confirmation
    expect(find.text('Added to cart'), findsOneWidget);

    // STEP 4: Navigate to cart
    await tester.tap(find.byIcon(Icons.shopping_cart));
    await tester.pumpAndSettle();

    // Verify item in cart
    expect(find.text('Shopping Cart'), findsOneWidget);
    expect(find.byKey(const Key('cartItem_1')), findsOneWidget);

    // STEP 5: Proceed to checkout
    await tester.tap(find.text('Checkout'));
    await tester.pumpAndSettle();

    // Verify checkout screen
    expect(find.text('Checkout'), findsOneWidget);

    // STEP 6: Fill checkout form
    await tester.enterText(find.byKey(const Key('nameField')), 'John Doe');
    await tester.enterText(find.byKey(const Key('addressField')), '123 Main St');
    await tester.pumpAndSettle();

    // STEP 7: Complete purchase
    await tester.tap(find.text('Complete Purchase'));
    await tester.pumpAndSettle();

    // ASSERT: Success screen
    expect(find.text('Order Confirmed'), findsOneWidget);
  });
}
```

### Testing Scrolling and List Interactions

```dart
testWidgets('Scroll through product list and tap item',
    (WidgetTester tester) async {
  app.main();
  await tester.pumpAndSettle();

  // Find the scrollable list
  final listFinder = find.byType(ListView);

  // Scroll down to find an item that's not initially visible
  await tester.scrollUntilVisible(
    find.byKey(const Key('product_20')),
    500.0, // Scroll amount in pixels
    scrollable: listFinder,
  );
  await tester.pumpAndSettle();

  // Verify item is now visible
  expect(find.byKey(const Key('product_20')), findsOneWidget);

  // Tap the item
  await tester.tap(find.byKey(const Key('product_20')));
  await tester.pumpAndSettle();

  // Verify navigation occurred
  expect(find.text('Product Details'), findsOneWidget);
});
```

### Testing Forms with Validation

```dart
testWidgets('Registration form validation flow',
    (WidgetTester tester) async {
  app.main();
  await tester.pumpAndSettle();

  // Navigate to registration screen
  await tester.tap(find.text('Register'));
  await tester.pumpAndSettle();

  // Test 1: Submit empty form
  await tester.tap(find.text('Submit'));
  await tester.pumpAndSettle();

  expect(find.text('Name is required'), findsOneWidget);
  expect(find.text('Email is required'), findsOneWidget);

  // Test 2: Invalid email format
  await tester.enterText(find.byKey(const Key('emailField')), 'invalidemail');
  await tester.tap(find.text('Submit'));
  await tester.pumpAndSettle();

  expect(find.text('Enter a valid email'), findsOneWidget);

  // Test 3: Valid submission
  await tester.enterText(find.byKey(const Key('nameField')), 'John Doe');
  await tester.enterText(
    find.byKey(const Key('emailField')),
    'john@example.com',
  );
  await tester.enterText(
    find.byKey(const Key('passwordField')),
    'securepass123',
  );
  await tester.pumpAndSettle();

  await tester.tap(find.text('Submit'));
  await tester.pumpAndSettle();

  // Verify success
  expect(find.text('Registration Successful'), findsOneWidget);
});
```

---

## Section 6: Best Practices for Integration Testing

### 1. Use Keys for Important Widgets

**Bad:**
```dart
ElevatedButton(
  onPressed: _login,
  child: const Text('Login'),
)
```

**Good:**
```dart
ElevatedButton(
  key: const Key('loginButton'),  // Easy to find in tests
  onPressed: _login,
  child: const Text('Login'),
)
```

### 2. Wait for Animations and Async Operations

```dart
// Bad: Might tap before widget is ready
await tester.tap(find.text('Login'));

// Good: Wait for all animations to finish
await tester.pumpAndSettle();
await tester.tap(find.text('Login'));
await tester.pumpAndSettle();
```

### 3. Test One User Flow Per Test

**Bad:** One massive test that tests everything
```dart
testWidgets('Test entire app', (tester) async {
  // 500 lines of test code testing login, shopping, checkout, profile...
});
```

**Good:** Separate tests for each flow
```dart
testWidgets('Login flow', (tester) async { /* ... */ });
testWidgets('Shopping flow', (tester) async { /* ... */ });
testWidgets('Checkout flow', (tester) async { /* ... */ });
```

### 4. Use Descriptive Test Names

**Bad:**
```dart
testWidgets('test1', (tester) async { /* ... */ });
```

**Good:**
```dart
testWidgets('User can complete purchase with valid credit card',
    (tester) async { /* ... */ });
```

### 5. Clean Up Between Tests

```dart
testWidgets('Login test', (WidgetTester tester) async {
  app.main();
  await tester.pumpAndSettle();

  // Test code...

  // No explicit cleanup needed - each test starts fresh
  // The next testWidgets call will restart the app
});
```

---

## Section 7: Debugging Integration Tests

### Common Issues and Solutions

#### Issue 1: "Unable to find widget"

**Error:**
```
The following TestFailure was thrown running a test:
Expected: exactly one matching node in the widget tree
Actual: _TextFinder:<zero widgets with text "Welcome">
```

**Solutions:**
```dart
// Solution 1: Check if you waited for animations
await tester.pumpAndSettle();

// Solution 2: Check if the text is actually there
await tester.pumpAndSettle();
print('Current widgets: ${find.text('Welcome').evaluate()}');

// Solution 3: Use a more flexible finder
find.textContaining('Welcome'); // Partial match
find.byType(Text); // Find by type instead
```

#### Issue 2: Test Times Out

**Error:**
```
TimeoutException after 0:00:30.000000: Test timed out
```

**Solutions:**
```dart
// Solution 1: Increase timeout
testWidgets('Slow test', (tester) async {
  // ... test code
}, timeout: const Timeout(Duration(seconds: 60)));

// Solution 2: Check for infinite loops in animations
// Make sure you're using pumpAndSettle(), not pump()
await tester.pumpAndSettle(); // Waits for animations to finish
```

#### Issue 3: Flaky Tests (Sometimes Pass, Sometimes Fail)

**Causes:**
- Network-dependent code
- Race conditions with async operations
- Animation timing issues

**Solutions:**
```dart
// Wait for specific conditions instead of arbitrary delays
await tester.pumpAndSettle();

// For network operations, consider mocking in integration tests
// Or use retry logic
for (int i = 0; i < 3; i++) {
  await tester.pumpAndSettle();
  if (find.text('Loaded').evaluate().isNotEmpty) break;
  await Future.delayed(const Duration(seconds: 1));
}
```

---

## Complete Integration Test Example

Here's a comprehensive example testing a todo app:

```dart
// integration_test/todo_app_test.dart
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:your_app/main.dart' as app;

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  group('Todo App Integration Tests', () {
    testWidgets('User can add, complete, and delete a todo',
        (WidgetTester tester) async {
      // ARRANGE: Start the app
      app.main();
      await tester.pumpAndSettle();

      // Verify empty state
      expect(find.text('No todos yet'), findsOneWidget);

      // ACT: Add a new todo
      await tester.tap(find.byKey(const Key('addButton')));
      await tester.pumpAndSettle();

      // Enter todo text
      await tester.enterText(
        find.byKey(const Key('todoInputField')),
        'Buy groceries',
      );
      await tester.pumpAndSettle();

      await tester.tap(find.text('Save'));
      await tester.pumpAndSettle();

      // ASSERT: Todo appears in list
      expect(find.text('Buy groceries'), findsOneWidget);
      expect(find.text('No todos yet'), findsNothing);

      // ACT: Mark todo as complete
      await tester.tap(find.byKey(const Key('todoCheckbox_0')));
      await tester.pumpAndSettle();

      // ASSERT: Todo is marked complete (strikethrough or different style)
      final todoWidget = tester.widget(find.text('Buy groceries'));
      // Add assertions based on your implementation

      // ACT: Delete the todo
      await tester.tap(find.byKey(const Key('deleteButton_0')));
      await tester.pumpAndSettle();

      // ASSERT: Todo is removed
      expect(find.text('Buy groceries'), findsNothing);
      expect(find.text('No todos yet'), findsOneWidget);
    });

    testWidgets('User can add multiple todos and filter them',
        (WidgetTester tester) async {
      app.main();
      await tester.pumpAndSettle();

      // Add first todo
      await tester.tap(find.byKey(const Key('addButton')));
      await tester.pumpAndSettle();
      await tester.enterText(
        find.byKey(const Key('todoInputField')),
        'Task 1',
      );
      await tester.tap(find.text('Save'));
      await tester.pumpAndSettle();

      // Add second todo
      await tester.tap(find.byKey(const Key('addButton')));
      await tester.pumpAndSettle();
      await tester.enterText(
        find.byKey(const Key('todoInputField')),
        'Task 2',
      );
      await tester.tap(find.text('Save'));
      await tester.pumpAndSettle();

      // Verify both todos exist
      expect(find.text('Task 1'), findsOneWidget);
      expect(find.text('Task 2'), findsOneWidget);

      // Complete first todo
      await tester.tap(find.byKey(const Key('todoCheckbox_0')));
      await tester.pumpAndSettle();

      // Filter to show only active todos
      await tester.tap(find.text('Active'));
      await tester.pumpAndSettle();

      // Verify only active todo is shown
      expect(find.text('Task 1'), findsNothing); // Completed, hidden
      expect(find.text('Task 2'), findsOneWidget); // Active, visible

      // Filter to show completed todos
      await tester.tap(find.text('Completed'));
      await tester.pumpAndSettle();

      // Verify only completed todo is shown
      expect(find.text('Task 1'), findsOneWidget);
      expect(find.text('Task 2'), findsNothing);
    });
  });
}
```

---

## Quiz

Test your understanding of integration testing:

### Question 1
What is the primary difference between widget tests and integration tests?

A) Widget tests are faster
B) Integration tests run on real devices
C) Integration tests test complete user flows across multiple screens
D) All of the above

### Question 2
What must you call at the beginning of an integration test file?

A) `WidgetTester.ensureInitialized()`
B) `IntegrationTestWidgetsFlutterBinding.ensureInitialized()`
C) `Flutter.initializeIntegrationTests()`
D) Nothing special is required

### Question 3
How do you run integration tests on a connected physical device?

A) `flutter run integration_test`
B) `flutter test integration_test --device-id=<id>`
C) `flutter device test`
D) Integration tests cannot run on physical devices

### Question 4
What is the purpose of `await tester.pumpAndSettle()` in integration tests?

A) To restart the test
B) To wait for all animations and async operations to complete
C) To take a screenshot
D) To clean up test resources

### Question 5
What is the recommended way to find widgets in integration tests?

A) By text content only
B) By widget type only
C) By using Key widgets assigned to important UI elements
D) Using index positions in the widget tree

---

## Answer Key

**Question 1: D** - All of the above. Integration tests differ from widget tests in that they're slower, run on real devices, and test complete workflows.

**Question 2: B** - `IntegrationTestWidgetsFlutterBinding.ensureInitialized()` must be called at the start. This binding allows tests to run on physical devices.

**Question 3: B** - Use `flutter test integration_test --device-id=<id>`. You first run `flutter devices` to get your device ID, then specify it in the test command.

**Question 4: B** - `pumpAndSettle()` waits for all animations, frame updates, and async operations to complete before proceeding. This ensures widgets are in their final state before assertions.

**Question 5: C** - Using `Key` widgets is the most reliable approach. Text and types can change, but keys provide stable, explicit identifiers for testing.

---

## Summary

In this lesson, you learned:

✅ **Integration tests** verify complete user workflows across multiple screens
✅ **IntegrationTestWidgetsFlutterBinding** enables testing on real devices
✅ Integration tests start with `app.main()` to launch the entire app
✅ Use `pumpAndSettle()` frequently to wait for async operations
✅ Add **Key widgets** to important UI elements for reliable test finders
✅ Run tests with `flutter test integration_test` or on devices
✅ Write separate tests for each user flow for maintainability
✅ Integration tests are slower but catch issues that unit/widget tests miss

**Key Takeaway:** Integration testing is your final defense before users encounter bugs. While slower than unit and widget tests, they verify that your entire app works as a cohesive system. Use them to test critical user journeys like login, checkout, and core features.

---

## What's Next?

In **Lesson 10.5: End-to-End Testing with Firebase Test Lab**, you'll learn how to run your integration tests on hundreds of real Android and iOS devices in the cloud, catching device-specific bugs before your users do.
