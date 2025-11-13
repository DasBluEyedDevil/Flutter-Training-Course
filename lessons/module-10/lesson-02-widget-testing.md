# Lesson 2: Widget Testing

## What You'll Learn
- Testing Flutter widgets
- Using WidgetTester (pumping, finding, tapping)
- Testing user interactions
- Testing navigation
- Testing forms and validation
- Golden tests for visual regression

## Concept First: What Are Widget Tests?

### Real-World Analogy
Widget testing is like **quality checking furniture in a showroom**:
- **Does the button work?** (interaction testing)
- **Does the drawer open smoothly?** (animation testing)
- **Does it look right?** (visual testing)
- **Does everything fit together?** (layout testing)

You test each piece of furniture (widget) in isolation before assembling the whole room (app).

### Why This Matters
Widget tests verify your UI works correctly:

1. **User Interactions**: Buttons, taps, gestures
2. **Visual Appearance**: Colors, fonts, layouts
3. **State Changes**: UI updates when data changes
4. **Navigation**: Screen transitions work
5. **Forms**: Input validation and submission

Widget tests run faster than integration tests but provide more confidence than unit tests!

---

## Widget Testing Basics

### Your First Widget Test

**lib/widgets/counter_widget.dart:**
```dart
import 'package:flutter/material.dart';

class CounterWidget extends StatefulWidget {
  @override
  State<CounterWidget> createState() => _CounterWidgetState();
}

class _CounterWidgetState extends State<CounterWidget> {
  int _counter = 0;

  void _increment() {
    setState(() {
      _counter++;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Text('$_counter', key: Key('counter_text')),
        ElevatedButton(
          onPressed: _increment,
          child: Text('Increment'),
        ),
      ],
    );
  }
}
```

**test/widgets/counter_widget_test.dart:**
```dart
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:my_app/widgets/counter_widget.dart';

void main() {
  testWidgets('Counter increments when button is tapped', (WidgetTester tester) async {
    // 1. ARRANGE: Build the widget
    await tester.pumpWidget(
      MaterialApp(
        home: Scaffold(
          body: CounterWidget(),
        ),
      ),
    );

    // 2. ASSERT: Verify initial state
    expect(find.text('0'), findsOneWidget);
    expect(find.text('1'), findsNothing);

    // 3. ACT: Tap the button
    await tester.tap(find.text('Increment'));
    await tester.pump();  // Rebuild after state change

    // 4. ASSERT: Verify state changed
    expect(find.text('0'), findsNothing);
    expect(find.text('1'), findsOneWidget);

    // Tap again
    await tester.tap(find.text('Increment'));
    await tester.pump();

    expect(find.text('2'), findsOneWidget);
  });
}
```

### Key Concepts

**`testWidgets()`**: Creates a widget test (like `test()` for unit tests)

**`WidgetTester`**: Provides tools to interact with widgets
- `pump()`: Rebuild widgets once
- `pumpAndSettle()`: Wait for all animations to complete
- `tap()`: Simulate user tap
- `enterText()`: Type into text fields

**`find`**: Locate widgets on screen
- `find.text('Hello')`: Find by text
- `find.byType(ElevatedButton)`: Find by widget type
- `find.byKey(Key('my_key'))`: Find by key
- `find.byIcon(Icons.add)`: Find by icon

**`expect()`**: Assert what you found
- `findsOneWidget`: Exactly one match
- `findsNothing`: No matches
- `findsNWidgets(3)`: Exactly 3 matches
- `findsWidgets`: At least one match

---

## Finding Widgets

### Different Ways to Find Widgets

```dart
testWidgets('Finding widgets examples', (tester) async {
  await tester.pumpWidget(
    MaterialApp(
      home: Scaffold(
        body: Column(
          children: [
            Text('Hello', key: Key('greeting')),
            ElevatedButton(
              onPressed: () {},
              child: Text('Click Me'),
            ),
            Icon(Icons.star),
            TextField(),
          ],
        ),
      ),
    ),
  );

  // Find by text
  expect(find.text('Hello'), findsOneWidget);
  expect(find.text('Click Me'), findsOneWidget);

  // Find by widget type
  expect(find.byType(ElevatedButton), findsOneWidget);
  expect(find.byType(TextField), findsOneWidget);

  // Find by key
  expect(find.byKey(Key('greeting')), findsOneWidget);

  // Find by icon
  expect(find.byIcon(Icons.star), findsOneWidget);

  // Find descendant (widget inside another)
  expect(
    find.descendant(
      of: find.byType(ElevatedButton),
      matching: find.text('Click Me'),
    ),
    findsOneWidget,
  );

  // Find ancestor (parent widget)
  expect(
    find.ancestor(
      of: find.text('Hello'),
      matching: find.byType(Column),
    ),
    findsOneWidget,
  );
});
```

### Using Keys for Reliable Tests

```dart
// ✅ Good - using keys
Text('Welcome', key: Key('welcome_text'));
ElevatedButton(
  key: Key('login_button'),
  onPressed: () {},
  child: Text('Login'),
);

// In test
expect(find.byKey(Key('welcome_text')), findsOneWidget);
await tester.tap(find.byKey(Key('login_button')));
```

**Why keys?**
- Text can change (translations, dynamic content)
- Widget types can be duplicated
- Keys provide stable references

---

## Interacting with Widgets

### Tapping Buttons

```dart
testWidgets('Button tap triggers callback', (tester) async {
  bool wasPressed = false;

  await tester.pumpWidget(
    MaterialApp(
      home: Scaffold(
        body: ElevatedButton(
          onPressed: () => wasPressed = true,
          child: Text('Press Me'),
        ),
      ),
    ),
  );

  // Tap the button
  await tester.tap(find.text('Press Me'));
  await tester.pump();

  expect(wasPressed, true);
});
```

### Entering Text

```dart
testWidgets('TextField accepts input', (tester) async {
  final controller = TextEditingController();

  await tester.pumpWidget(
    MaterialApp(
      home: Scaffold(
        body: TextField(
          key: Key('email_field'),
          controller: controller,
        ),
      ),
    ),
  );

  // Enter text
  await tester.enterText(find.byKey(Key('email_field')), 'test@example.com');
  await tester.pump();

  // Verify text was entered
  expect(controller.text, 'test@example.com');
  expect(find.text('test@example.com'), findsOneWidget);
});
```

### Scrolling

```dart
testWidgets('Can scroll ListView', (tester) async {
  await tester.pumpWidget(
    MaterialApp(
      home: Scaffold(
        body: ListView.builder(
          itemCount: 100,
          itemBuilder: (context, index) => ListTile(
            title: Text('Item $index'),
          ),
        ),
      ),
    ),
  );

  // Initially, item 0 is visible but item 99 is not
  expect(find.text('Item 0'), findsOneWidget);
  expect(find.text('Item 99'), findsNothing);

  // Scroll to the end
  await tester.scrollUntilVisible(
    find.text('Item 99'),
    500.0,  // Scroll 500 pixels at a time
    scrollable: find.byType(Scrollable),
  );

  // Now item 99 is visible
  expect(find.text('Item 99'), findsOneWidget);
});
```

### Long Press

```dart
testWidgets('Long press shows context menu', (tester) async {
  bool longPressed = false;

  await tester.pumpWidget(
    MaterialApp(
      home: Scaffold(
        body: GestureDetector(
          onLongPress: () => longPressed = true,
          child: Text('Long press me'),
        ),
      ),
    ),
  );

  await tester.longPress(find.text('Long press me'));
  await tester.pump();

  expect(longPressed, true);
});
```

### Drag and Swipe

```dart
testWidgets('Can swipe to dismiss', (tester) async {
  bool dismissed = false;

  await tester.pumpWidget(
    MaterialApp(
      home: Scaffold(
        body: Dismissible(
          key: Key('dismissible'),
          onDismissed: (_) => dismissed = true,
          child: Container(
            height: 100,
            color: Colors.blue,
            child: Text('Swipe me'),
          ),
        ),
      ),
    ),
  );

  // Swipe from left to right
  await tester.drag(find.byKey(Key('dismissible')), Offset(500, 0));
  await tester.pumpAndSettle();  // Wait for animation

  expect(dismissed, true);
});
```

---

## Testing Forms

### Complete Form Example

**lib/screens/login_screen.dart:**
```dart
import 'package:flutter/material.dart';

class LoginScreen extends StatefulWidget {
  final Function(String email, String password)? onLogin;

  LoginScreen({this.onLogin});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _formKey = GlobalKey<FormState>();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();

  void _submit() {
    if (_formKey.currentState!.validate()) {
      widget.onLogin?.call(_emailController.text, _passwordController.text);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Form(
        key: _formKey,
        child: Column(
          children: [
            TextFormField(
              key: Key('email'),
              controller: _emailController,
              decoration: InputDecoration(labelText: 'Email'),
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return 'Please enter email';
                }
                if (!value.contains('@')) {
                  return 'Invalid email format';
                }
                return null;
              },
            ),
            TextFormField(
              key: Key('password'),
              controller: _passwordController,
              decoration: InputDecoration(labelText: 'Password'),
              obscureText: true,
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return 'Please enter password';
                }
                if (value.length < 6) {
                  return 'Password must be at least 6 characters';
                }
                return null;
              },
            ),
            ElevatedButton(
              key: Key('login_button'),
              onPressed: _submit,
              child: Text('Login'),
            ),
          ],
        ),
      ),
    );
  }
}
```

**test/screens/login_screen_test.dart:**
```dart
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:my_app/screens/login_screen.dart';

void main() {
  group('LoginScreen', () {
    testWidgets('shows email and password fields', (tester) async {
      await tester.pumpWidget(
        MaterialApp(home: LoginScreen()),
      );

      expect(find.byKey(Key('email')), findsOneWidget);
      expect(find.byKey(Key('password')), findsOneWidget);
      expect(find.byKey(Key('login_button')), findsOneWidget);
    });

    testWidgets('validates empty email', (tester) async {
      await tester.pumpWidget(
        MaterialApp(home: LoginScreen()),
      );

      // Tap login without entering anything
      await tester.tap(find.byKey(Key('login_button')));
      await tester.pump();

      // Should show validation error
      expect(find.text('Please enter email'), findsOneWidget);
    });

    testWidgets('validates invalid email format', (tester) async {
      await tester.pumpWidget(
        MaterialApp(home: LoginScreen()),
      );

      // Enter invalid email
      await tester.enterText(find.byKey(Key('email')), 'notanemail');
      await tester.tap(find.byKey(Key('login_button')));
      await tester.pump();

      expect(find.text('Invalid email format'), findsOneWidget);
    });

    testWidgets('validates password length', (tester) async {
      await tester.pumpWidget(
        MaterialApp(home: LoginScreen()),
      );

      await tester.enterText(find.byKey(Key('email')), 'test@example.com');
      await tester.enterText(find.byKey(Key('password')), '123');
      await tester.tap(find.byKey(Key('login_button')));
      await tester.pump();

      expect(find.text('Password must be at least 6 characters'), findsOneWidget);
    });

    testWidgets('calls onLogin with valid credentials', (tester) async {
      String? capturedEmail;
      String? capturedPassword;

      await tester.pumpWidget(
        MaterialApp(
          home: LoginScreen(
            onLogin: (email, password) {
              capturedEmail = email;
              capturedPassword = password;
            },
          ),
        ),
      );

      // Enter valid credentials
      await tester.enterText(find.byKey(Key('email')), 'test@example.com');
      await tester.enterText(find.byKey(Key('password')), 'password123');
      await tester.tap(find.byKey(Key('login_button')));
      await tester.pump();

      // Should call onLogin callback
      expect(capturedEmail, 'test@example.com');
      expect(capturedPassword, 'password123');
    });
  });
}
```

---

## Testing Navigation

```dart
testWidgets('Navigates to detail screen', (tester) async {
  await tester.pumpWidget(
    MaterialApp(
      home: Scaffold(
        body: ElevatedButton(
          child: Text('Go to Details'),
          onPressed: () {
            Navigator.push(
              tester.element(find.byType(ElevatedButton)),
              MaterialPageRoute(builder: (_) => Scaffold(
                body: Text('Details Screen'),
              )),
            );
          },
        ),
      ),
    ),
  );

  // Initial screen
  expect(find.text('Go to Details'), findsOneWidget);
  expect(find.text('Details Screen'), findsNothing);

  // Tap to navigate
  await tester.tap(find.text('Go to Details'));
  await tester.pumpAndSettle();  // Wait for navigation animation

  // Now on details screen
  expect(find.text('Go to Details'), findsNothing);
  expect(find.text('Details Screen'), findsOneWidget);

  // Navigate back
  await tester.pageBack();
  await tester.pumpAndSettle();

  // Back to initial screen
  expect(find.text('Go to Details'), findsOneWidget);
});
```

---

## Pump Methods

### Understanding Pump Variants

```dart
// pump(): Rebuild once
await tester.pump();

// pump(Duration): Advance time and rebuild
await tester.pump(Duration(milliseconds: 500));

// pumpAndSettle(): Rebuild until animations finish (default 10 minutes timeout)
await tester.pumpAndSettle();

// pumpAndSettle(Duration): With custom timeout
await tester.pumpAndSettle(Duration(seconds: 5));
```

**When to use which:**
- `pump()`: After state changes with no animations
- `pump(Duration)`: To test specific animation frames
- `pumpAndSettle()`: After navigation, dialogs, or complex animations

---

## Golden Tests (Visual Regression)

Test that UI looks exactly as expected by comparing screenshots.

```dart
testWidgets('Golden test for profile card', (tester) async {
  await tester.pumpWidget(
    MaterialApp(
      home: Scaffold(
        body: ProfileCard(
          name: 'John Doe',
          email: 'john@example.com',
          avatarUrl: 'https://example.com/avatar.png',
        ),
      ),
    ),
  );

  await tester.pumpAndSettle();

  // Compare with golden file
  await expectLater(
    find.byType(ProfileCard),
    matchesGoldenFile('goldens/profile_card.png'),
  );
});
```

**Generate golden files:**
```bash
flutter test --update-goldens
```

**Run golden tests:**
```bash
flutter test
```

**Use cases:**
- Verify UI changes don't break existing designs
- Catch unintended visual regressions
- Document expected UI appearance

---

## Complete Example: Todo App

**test/widgets/todo_list_test.dart:**
```dart
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  testWidgets('Todo app full workflow', (tester) async {
    await tester.pumpWidget(
      MaterialApp(home: TodoApp()),
    );

    // Initially empty
    expect(find.text('No todos yet!'), findsOneWidget);

    // Add todo
    await tester.enterText(find.byType(TextField), 'Buy milk');
    await tester.tap(find.byIcon(Icons.add));
    await tester.pump();

    // Todo appears
    expect(find.text('Buy milk'), findsOneWidget);
    expect(find.text('No todos yet!'), findsNothing);

    // Add another
    await tester.enterText(find.byType(TextField), 'Walk dog');
    await tester.tap(find.byIcon(Icons.add));
    await tester.pump();

    expect(find.byType(Checkbox), findsNWidgets(2));

    // Complete first todo
    await tester.tap(find.byType(Checkbox).first);
    await tester.pump();

    // Delete completed todo
    await tester.tap(find.byIcon(Icons.delete).first);
    await tester.pump();

    // Only one todo remains
    expect(find.text('Buy milk'), findsNothing);
    expect(find.text('Walk dog'), findsOneWidget);
  });
}
```

---

## Best Practices

1. **Use Keys for Important Widgets**
   ```dart
   TextField(key: Key('email_input'))
   ElevatedButton(key: Key('submit_button'))
   ```

2. **Wait for Animations**
   ```dart
   await tester.pumpAndSettle();  // After navigation, dialogs
   ```

3. **Test User Perspective**
   ```dart
   // ✅ Good - find by what user sees
   await tester.tap(find.text('Login'));

   // ❌ Bad - implementation detail
   await tester.tap(find.byType(ElevatedButton).at(2));
   ```

4. **Test Edge Cases**
   - Empty states
   - Long text overflow
   - Different screen sizes
   - Disabled buttons

5. **Keep Tests Focused**
   ```dart
   // ✅ Good - tests one thing
   testWidgets('Submit button is disabled when form is invalid')

   // ❌ Bad - tests multiple things
   testWidgets('Form works correctly')
   ```

---

## Quiz

**Question 1:** Which method waits for all animations to complete?
A) pump()
B) pumpAndSettle()
C) pumpWidget()
D) pumpFrame()

**Question 2:** How do you simulate a user typing text?
A) tester.type()
B) tester.enterText()
C) tester.input()
D) tester.setText()

**Question 3:** What does `findsOneWidget` assert?
A) At least one widget was found
B) Exactly one widget was found
C) The first widget found
D) One or more widgets found

---

## Exercise: Test a Calculator UI

Create a calculator widget with:
- Number buttons (0-9)
- Operation buttons (+, -, ×, ÷)
- Equals button
- Clear button
- Display showing current value

Write widget tests for:
1. Number buttons update display
2. Addition works (2 + 3 = 5)
3. Clear button resets to 0
4. Division by zero shows error
5. Multiple operations in sequence

---

## Summary

You've mastered widget testing in Flutter! Here's what we covered:

- **WidgetTester**: pump(), pumpAndSettle(), tap(), enterText()
- **Finders**: find.text(), find.byKey(), find.byType()
- **Matchers**: findsOneWidget, findsNothing, findsNWidgets()
- **Interactions**: Tapping, scrolling, swiping, entering text
- **Forms**: Validation testing
- **Navigation**: Screen transitions
- **Golden Tests**: Visual regression testing

Widget tests give you confidence your UI works before users see it!

---

## Answer Key

**Answer 1:** B) pumpAndSettle()

`pumpAndSettle()` repeatedly calls `pump()` until there are no more frames scheduled, ensuring all animations and asynchronous operations complete. Use it after navigation or dialogs.

**Answer 2:** B) tester.enterText()

`tester.enterText(finder, 'text')` simulates typing text into a TextField. You provide a finder for the TextField and the text to enter.

**Answer 3:** B) Exactly one widget was found

`findsOneWidget` asserts that the finder located exactly one matching widget. Use `findsWidgets` for "at least one" or `findsNWidgets(n)` for exactly n widgets.
