# Lesson 1: Introduction to Testing

## What You'll Learn
- Why testing matters in app development
- Types of tests (unit, widget, integration)
- Test-Driven Development (TDD) basics
- Setting up your testing environment
- Writing your first test
- The testing pyramid

## Concept First: Why Test Your Code?

### Real-World Analogy
Think of testing like a **safety inspection for a building**:
- **Unit Tests** = Checking individual bricks (do they meet quality standards?)
- **Widget Tests** = Testing rooms (do doors open, do lights work?)
- **Integration Tests** = Full building walkthrough (does everything work together?)

Just like you wouldn't move into a building without inspections, you shouldn't ship an app without tests!

### Why This Matters
Testing prevents disasters:

1. **Catch Bugs Early**: Find issues before users do
2. **Confidence to Refactor**: Change code without fear of breaking things
3. **Documentation**: Tests show how code should work
4. **Faster Development**: Automated tests are faster than manual testing
5. **Better Design**: Testable code is usually better code

According to Google, apps with >80% test coverage have **56% fewer production bugs** and **40% faster feature development** over time!

---

## The Testing Pyramid

```
         /\
        /  \  Few, Slow, Expensive
       /E2E \  (Integration Tests)
      /______\
     /        \
    / Widget  \ Some, Medium Speed
   /   Tests   \
  /____________\
 /              \
/  Unit Tests    \ Many, Fast, Cheap
/__________________\
```

**The Golden Rule:**
- **70%** Unit Tests (fast, isolated)
- **20%** Widget Tests (UI components)
- **10%** Integration Tests (full app flows)

---

## Types of Tests

### 1. Unit Tests
**What:** Test individual functions, methods, or classes in isolation

**Example:** Testing a function that calculates BMI
```dart
double calculateBMI(double weight, double height) {
  return weight / (height * height);
}

// Unit test
test('calculateBMI returns correct value', () {
  final bmi = calculateBMI(70, 1.75);  // 70kg, 1.75m
  expect(bmi, closeTo(22.86, 0.01));  // Should be ~22.86
});
```

**When to Use:**
- Business logic
- Calculations
- Data transformations
- Utility functions

### 2. Widget Tests
**What:** Test UI components (widgets) in isolation

**Example:** Testing if a button shows correct text
```dart
testWidgets('Counter increments when button pressed', (tester) async {
  await tester.pumpWidget(CounterWidget());

  // Verify initial state
  expect(find.text('0'), findsOneWidget);

  // Tap button
  await tester.tap(find.byIcon(Icons.add));
  await tester.pump();

  // Verify state changed
  expect(find.text('1'), findsOneWidget);
});
```

**When to Use:**
- Button interactions
- Form validation
- Navigation
- Widget rendering

### 3. Integration Tests
**What:** Test complete app flows on real devices/emulators

**Example:** Testing login → home → logout flow
```dart
testWidgets('Complete user journey', (tester) async {
  app.main();
  await tester.pumpAndSettle();

  // Login
  await tester.enterText(find.byKey(Key('email')), 'test@example.com');
  await tester.enterText(find.byKey(Key('password')), 'password123');
  await tester.tap(find.text('Login'));
  await tester.pumpAndSettle();

  // Verify home screen
  expect(find.text('Welcome'), findsOneWidget);

  // Logout
  await tester.tap(find.byIcon(Icons.logout));
  await tester.pumpAndSettle();

  // Verify back to login
  expect(find.text('Login'), findsOneWidget);
});
```

**When to Use:**
- Critical user flows
- End-to-end features
- Multi-screen interactions
- External dependencies (API, database)

---

## Setting Up Testing

### Default Test Setup

Flutter projects come with testing built-in! No extra setup needed for basic tests.

**pubspec.yaml** (already includes):
```yaml
dev_dependencies:
  flutter_test:
    sdk: flutter
```

### Test File Structure

```
my_app/
├── lib/
│   ├── main.dart
│   ├── models/
│   │   └── user.dart
│   └── services/
│       └── auth_service.dart
├── test/                    # Unit & Widget tests
│   ├── models/
│   │   └── user_test.dart   # Naming: <file>_test.dart
│   └── services/
│       └── auth_service_test.dart
└── integration_test/        # Integration tests
    └── app_test.dart
```

**Convention:** Test files mirror your `lib/` structure with `_test.dart` suffix.

---

## Your First Unit Test

### Example: Testing a Calculator

**lib/utils/calculator.dart:**
```dart
class Calculator {
  int add(int a, int b) => a + b;

  int subtract(int a, int b) => a - b;

  int multiply(int a, int b) => a * b;

  double divide(int a, int b) {
    if (b == 0) {
      throw ArgumentError('Cannot divide by zero');
    }
    return a / b;
  }
}
```

**test/utils/calculator_test.dart:**
```dart
import 'package:flutter_test/flutter_test.dart';
import 'package:my_app/utils/calculator.dart';

void main() {
  // Group related tests
  group('Calculator', () {
    // Create instance once for all tests
    late Calculator calculator;

    // setUp runs before EACH test
    setUp(() {
      calculator = Calculator();
    });

    // Individual test cases
    test('add returns sum of two numbers', () {
      final result = calculator.add(2, 3);
      expect(result, 5);
    });

    test('subtract returns difference', () {
      expect(calculator.subtract(10, 4), 6);
    });

    test('multiply returns product', () {
      expect(calculator.multiply(3, 7), 21);
    });

    test('divide returns quotient', () {
      expect(calculator.divide(10, 2), 5.0);
    });

    test('divide by zero throws ArgumentError', () {
      expect(
        () => calculator.divide(10, 0),
        throwsArgumentError,
      );
    });

    test('divide returns correct decimal', () {
      expect(calculator.divide(10, 3), closeTo(3.333, 0.001));
    });
  });
}
```

### Running Tests

```bash
# Run all tests
flutter test

# Run specific test file
flutter test test/utils/calculator_test.dart

# Run with coverage
flutter test --coverage

# Watch mode (re-run on file changes)
flutter test --watch
```

**Output:**
```
00:01 +6: All tests passed!
```

---

## Test Anatomy

### Basic Structure

```dart
void main() {
  test('description of what you are testing', () {
    // 1. ARRANGE: Set up test data
    final calculator = Calculator();
    final a = 5;
    final b = 3;

    // 2. ACT: Execute the code being tested
    final result = calculator.add(a, b);

    // 3. ASSERT: Verify the result
    expect(result, 8);
  });
}
```

**AAA Pattern:**
- **Arrange**: Set up test data and preconditions
- **Act**: Execute the code under test
- **Assert**: Verify the outcome

### Common Matchers

```dart
// Equality
expect(actual, equals(expected));
expect(actual, expected);  // Shorthand

// Numerical
expect(value, greaterThan(10));
expect(value, lessThan(100));
expect(value, inRange(10, 20));
expect(3.14159, closeTo(3.14, 0.01));  // For floats

// Types
expect(value, isA<String>());
expect(value, isNotNull);
expect(value, isNull);

// Collections
expect(list, contains(5));
expect(list, containsAll([1, 2, 3]));
expect(list, isEmpty);
expect(list, hasLength(3));

// Strings
expect(text, startsWith('Hello'));
expect(text, endsWith('World'));
expect(text, matches(RegExp(r'\d+')));  // Regex

// Exceptions
expect(() => throwsError(), throwsException);
expect(() => divideByZero(), throwsA(isA<ArgumentError>()));
```

---

## Test Organization

### Using `group()`

Group related tests together:

```dart
void main() {
  group('User Model', () {
    group('fromJson', () {
      test('parses valid JSON correctly', () { /* ... */ });
      test('throws on missing required field', () { /* ... */ });
      test('handles null optional fields', () { /* ... */ });
    });

    group('toJson', () {
      test('converts to valid JSON', () { /* ... */ });
      test('excludes null fields', () { /* ... */ });
    });

    group('validation', () {
      test('email must be valid format', () { /* ... */ });
      test('age must be positive', () { /* ... */ });
    });
  });
}
```

**Output:**
```
✓ User Model fromJson parses valid JSON correctly
✓ User Model fromJson throws on missing required field
✓ User Model toJson converts to valid JSON
...
```

### Setup and Teardown

```dart
void main() {
  late Database database;

  // Runs ONCE before all tests
  setUpAll(() {
    print('Setting up test suite...');
  });

  // Runs BEFORE EACH test
  setUp(() {
    database = Database.inMemory();
  });

  // Runs AFTER EACH test
  tearDown(() {
    database.close();
  });

  // Runs ONCE after all tests
  tearDownAll(() {
    print('Cleaning up test suite...');
  });

  test('insert adds record', () {
    database.insert('users', {'name': 'Alice'});
    expect(database.count('users'), 1);
  });

  test('delete removes record', () {
    database.insert('users', {'name': 'Bob'});
    database.delete('users', 1);
    expect(database.count('users'), 0);
  });
}
```

---

## Test-Driven Development (TDD)

### The Red-Green-Refactor Cycle

1. **Red**: Write a failing test first
2. **Green**: Write minimal code to make it pass
3. **Refactor**: Improve code while keeping tests green

**Example: Building a Todo List**

**Step 1: Red (Write failing test)**
```dart
test('new todo is not completed', () {
  final todo = Todo('Buy milk');
  expect(todo.isCompleted, false);
});
```

**Step 2: Green (Make it pass)**
```dart
class Todo {
  final String title;
  bool isCompleted;

  Todo(this.title) : isCompleted = false;
}
```

**Step 3: Refactor (Improve if needed)**
```dart
class Todo {
  final String title;
  final bool isCompleted;

  Todo(this.title, {this.isCompleted = false});
}
```

**Benefits of TDD:**
- ✅ Forces you to think about requirements first
- ✅ Ensures every feature has tests
- ✅ Prevents over-engineering
- ✅ Immediate feedback loop

---

## Complete Example: BMI Calculator

**lib/models/bmi_calculator.dart:**
```dart
class BMICalculator {
  double calculateBMI({required double weight, required double height}) {
    if (weight <= 0) throw ArgumentError('Weight must be positive');
    if (height <= 0) throw ArgumentError('Height must be positive');

    return weight / (height * height);
  }

  String getCategory(double bmi) {
    if (bmi < 18.5) return 'Underweight';
    if (bmi < 25) return 'Normal';
    if (bmi < 30) return 'Overweight';
    return 'Obese';
  }

  String getHealthAdvice(double bmi) {
    final category = getCategory(bmi);

    switch (category) {
      case 'Underweight':
        return 'Consider consulting a nutritionist to gain weight healthily.';
      case 'Normal':
        return 'Great! Maintain your healthy lifestyle.';
      case 'Overweight':
        return 'Consider a balanced diet and regular exercise.';
      case 'Obese':
        return 'Consult a healthcare professional for personalized advice.';
      default:
        return 'Unknown category';
    }
  }
}
```

**test/models/bmi_calculator_test.dart:**
```dart
import 'package:flutter_test/flutter_test.dart';
import 'package:my_app/models/bmi_calculator.dart';

void main() {
  group('BMICalculator', () {
    late BMICalculator calculator;

    setUp(() {
      calculator = BMICalculator();
    });

    group('calculateBMI', () {
      test('calculates correct BMI for normal values', () {
        final bmi = calculator.calculateBMI(weight: 70, height: 1.75);
        expect(bmi, closeTo(22.86, 0.01));
      });

      test('throws ArgumentError for zero weight', () {
        expect(
          () => calculator.calculateBMI(weight: 0, height: 1.75),
          throwsArgumentError,
        );
      });

      test('throws ArgumentError for negative height', () {
        expect(
          () => calculator.calculateBMI(weight: 70, height: -1.75),
          throwsArgumentError,
        );
      });

      test('handles very small values', () {
        final bmi = calculator.calculateBMI(weight: 0.1, height: 0.1);
        expect(bmi, 10.0);
      });
    });

    group('getCategory', () {
      test('returns Underweight for BMI < 18.5', () {
        expect(calculator.getCategory(18.0), 'Underweight');
      });

      test('returns Normal for BMI 18.5-24.9', () {
        expect(calculator.getCategory(22), 'Normal');
        expect(calculator.getCategory(18.5), 'Normal');
        expect(calculator.getCategory(24.9), 'Normal');
      });

      test('returns Overweight for BMI 25-29.9', () {
        expect(calculator.getCategory(27), 'Overweight');
      });

      test('returns Obese for BMI >= 30', () {
        expect(calculator.getCategory(35), 'Obese');
      });
    });

    group('getHealthAdvice', () {
      test('provides advice for each category', () {
        expect(
          calculator.getHealthAdvice(17),
          contains('nutritionist'),
        );
        expect(
          calculator.getHealthAdvice(22),
          contains('healthy lifestyle'),
        );
        expect(
          calculator.getHealthAdvice(27),
          contains('exercise'),
        );
        expect(
          calculator.getHealthAdvice(35),
          contains('healthcare professional'),
        );
      });
    });
  });
}
```

**Run the tests:**
```bash
flutter test test/models/bmi_calculator_test.dart
```

**Output:**
```
00:01 +12: All tests passed!
```

---

## Best Practices

1. **Test Names Should Be Descriptive**
   ```dart
   // ❌ Bad
   test('test1', () { /* ... */ });

   // ✅ Good
   test('calculateBMI returns correct value for normal inputs', () { /* ... */ });
   ```

2. **One Assertion Per Test (Usually)**
   ```dart
   // ❌ Bad - testing multiple things
   test('user validation', () {
     expect(user.isValidEmail(), true);
     expect(user.isValidAge(), true);
     expect(user.isValidName(), true);
   });

   // ✅ Good - separate tests
   test('validates email format correctly', () {
     expect(user.isValidEmail(), true);
   });

   test('validates age is positive', () {
     expect(user.isValidAge(), true);
   });
   ```

3. **Test Edge Cases**
   - Zero, negative, null values
   - Empty strings, empty lists
   - Boundary values (min, max)
   - Unexpected inputs

4. **Keep Tests Fast**
   - Unit tests should run in milliseconds
   - Avoid file I/O, network calls, delays
   - Use mocks for external dependencies

5. **Tests Should Be Independent**
   - No shared state between tests
   - Tests can run in any order
   - Each test sets up its own data

---

## Quiz

**Question 1:** In the testing pyramid, which type of test should you have the most of?
A) Integration tests
B) Widget tests
C) Unit tests
D) E2E tests

**Question 2:** What does the `setUp()` function do?
A) Runs once before all tests
B) Runs before each individual test
C) Runs after each test
D) Runs only when tests fail

**Question 3:** Which matcher would you use to test if a float is approximately equal?
A) equals()
B) closeTo()
C) approximately()
D) near()

---

## Exercise: Test a Shopping Cart

Create a `ShoppingCart` class with these methods:
- `addItem(String name, double price)`
- `removeItem(String name)`
- `getTotal()`
- `applyDiscount(double percentage)`

Write comprehensive unit tests covering:
1. Adding items increases total
2. Removing items decreases total
3. Discount is applied correctly
4. Edge cases (empty cart, removing non-existent item, 100% discount)

---

## Summary

You've learned the fundamentals of testing in Flutter! Here's what we covered:

- **Testing Pyramid**: 70% unit, 20% widget, 10% integration
- **Test Types**: Unit (functions), Widget (UI), Integration (full app)
- **Test Structure**: AAA pattern (Arrange, Act, Assert)
- **Test Organization**: `group()`, `setUp()`, `tearDown()`
- **TDD**: Red-Green-Refactor cycle
- **Best Practices**: Descriptive names, fast tests, edge cases

Testing may seem like extra work, but it **saves time** and **prevents bugs** in the long run!

---

## Answer Key

**Answer 1:** C) Unit tests

The testing pyramid recommends having the most unit tests (~70%) because they're fast, cheap, and test individual pieces of logic in isolation. Widget tests should be ~20% and integration tests ~10%.

**Answer 2:** B) Runs before each individual test

`setUp()` runs before EACH test in the group. Use `setUpAll()` to run once before all tests, `tearDown()` to run after each test, and `tearDownAll()` to run once after all tests.

**Answer 3:** B) closeTo()

`closeTo(expected, delta)` is used for floating-point comparisons because of precision issues. Example: `expect(3.14159, closeTo(3.14, 0.01))` checks if the value is within 0.01 of 3.14.
