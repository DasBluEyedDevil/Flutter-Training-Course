# Lesson 10.6: Test Coverage and Reporting

## Learning Objectives
By the end of this lesson, you will be able to:
- Understand what test coverage is and why it matters
- Generate test coverage reports for your Flutter project
- Interpret coverage metrics (line, function, and branch coverage)
- Exclude generated files from coverage reports
- Visualize coverage data in VSCode and HTML reports
- Set coverage targets and enforce them in CI/CD
- Identify untested code and improve coverage strategically

---

## Introduction

### What is Test Coverage?

**Concept First:**
Imagine you're a safety inspector testing all the fire exits in a building. You check each door one by one, marking them off on your floor plan. When you're done, you can see which exits you tested (highlighted in green) and which you didn't test yet (still red). That marked floor plan is your "coverage report."

**Test coverage** measures which parts of your code are executed when your tests run. It shows you:
- ✅ **Green (covered)**: Code that's tested
- ❌ **Red (uncovered)**: Code that's NOT tested
- ⚠️ **Yellow (partially covered)**: Code that's only sometimes tested

**Jargon:**
- **Line Coverage**: Percentage of code lines executed by tests
- **Function Coverage**: Percentage of functions/methods called by tests
- **Branch Coverage**: Percentage of decision paths (if/else, switch) tested
- **LCOV**: Linux Code Coverage tool, the standard format for coverage data
- **lcov.info**: The file containing raw coverage data

### Why This Matters

**Real-world scenario:** You have 10,000 lines of code. Your tests pass ✅, so everything's fine, right?

**Not necessarily!** Your tests might only execute 30% of your code. That means 7,000 lines are completely untested and could have bugs lurking.

**Coverage helps you:**
1. **Find blind spots**: Discover code paths never executed by tests
2. **Prioritize testing**: Focus on critical untested code
3. **Track progress**: Measure improvement over time
4. **Catch regressions**: Ensure new code is tested
5. **Build confidence**: Know what's actually verified

**Important note:** 100% coverage ≠ perfect code. But 30% coverage definitely means 70% is unverified!

---

## Section 1: Understanding Coverage Metrics

### Line Coverage

**What it measures:** Percentage of code lines executed during tests

```dart
// calculator.dart
int add(int a, int b) {
  return a + b;  // Line 1
}

int subtract(int a, int b) {
  return a - b;  // Line 2
}
```

```dart
// calculator_test.dart
test('add works', () {
  expect(add(2, 3), 5);  // Tests Line 1 only
});
```

**Line coverage: 50%** (1 out of 2 lines tested)

### Function Coverage

**What it measures:** Percentage of functions/methods called during tests

```dart
// math_utils.dart
int add(int a, int b) => a + b;
int subtract(int a, int b) => a - b;
int multiply(int a, int b) => a * b;
int divide(int a, int b) => a ~/ b;
```

```dart
// math_utils_test.dart
test('add works', () => expect(add(2, 3), 5));
test('multiply works', () => expect(multiply(2, 3), 6));
```

**Function coverage: 50%** (2 out of 4 functions tested)

### Branch Coverage

**What it measures:** Percentage of decision paths tested

```dart
// validator.dart
String validateAge(int age) {
  if (age < 0) {
    return 'Age cannot be negative';  // Branch 1
  } else if (age < 18) {
    return 'Must be 18 or older';     // Branch 2
  } else {
    return 'Valid';                   // Branch 3
  }
}
```

```dart
// validator_test.dart
test('valid age', () {
  expect(validateAge(25), 'Valid');  // Tests only Branch 3
});
```

**Branch coverage: 33%** (1 out of 3 branches tested)

**To reach 100% branch coverage:**
```dart
test('negative age', () {
  expect(validateAge(-5), 'Age cannot be negative');
});

test('underage', () {
  expect(validateAge(15), 'Must be 18 or older');
});

test('valid age', () {
  expect(validateAge(25), 'Valid');
});
```

**Branch coverage: 100%** ✅

---

## Section 2: Generating Coverage Reports

### Step 1: Run Tests with Coverage

```bash
# Generate coverage data
flutter test --coverage

# Output:
# 00:02 +10: All tests passed!
# Coverage data saved to coverage/lcov.info
```

This creates:
```
your_project/
├── coverage/
│   └── lcov.info  # Raw coverage data
├── test/
└── lib/
```

### Step 2: View Coverage in Terminal

Install `lcov` tools:

```bash
# macOS
brew install lcov

# Ubuntu/Debian
sudo apt-get install lcov

# Windows (using Chocolatey)
choco install lcov
```

Generate summary:
```bash
# View coverage summary
lcov --summary coverage/lcov.info

# Output:
# Summary coverage rate:
#   lines......: 67.5% (135 of 200 lines)
#   functions..: 70.0% (14 of 20 functions)
#   branches...: 50.0% (10 of 20 branches)
```

### Step 3: Generate HTML Report

```bash
# Generate beautiful HTML report
genhtml coverage/lcov.info -o coverage/html

# Open the report in your browser
open coverage/html/index.html  # macOS
xdg-open coverage/html/index.html  # Linux
start coverage/html/index.html  # Windows
```

**HTML report shows:**
- 📊 Overall coverage percentage
- 📁 Coverage by directory and file
- 📝 Line-by-line coverage with highlighting
- 🔴 Red lines = untested code
- 🟢 Green lines = tested code

---

## Section 3: Excluding Generated Files

### The Problem

Flutter generates files that pollute coverage reports:
- `*.g.dart` (code generation from build_runner)
- `*.freezed.dart` (freezed package)
- `*.gr.dart` (auto_route)
- `*.config.dart` (various packages)

### Solution: Remove Generated Files from Coverage

```bash
# Remove generated files from coverage
lcov --remove coverage/lcov.info \
  '*.g.dart' \
  '*.freezed.dart' \
  '*.gr.dart' \
  '*.config.dart' \
  -o coverage/lcov.info

# Generate HTML report with cleaned data
genhtml coverage/lcov.info -o coverage/html
```

### Automated Cleanup Script

Create `scripts/coverage.sh`:

```bash
#!/bin/bash
# scripts/coverage.sh

set -e

echo "🧪 Running tests with coverage..."
flutter test --coverage

echo "🧹 Cleaning up generated files from coverage..."
lcov --remove coverage/lcov.info \
  '*.g.dart' \
  '*.freezed.dart' \
  '*.gr.dart' \
  '*.config.dart' \
  '**/*.g.dart' \
  '**/*.freezed.dart' \
  '**/*.gr.dart' \
  '**/*.config.dart' \
  'lib/generated/**' \
  -o coverage/lcov_cleaned.info

echo "📊 Generating HTML report..."
genhtml coverage/lcov_cleaned.info -o coverage/html

echo "📈 Coverage summary:"
lcov --summary coverage/lcov_cleaned.info

echo ""
echo "✅ Done! Open coverage/html/index.html to view the report"
```

Make it executable:
```bash
chmod +x scripts/coverage.sh
```

Run it:
```bash
./scripts/coverage.sh
```

---

## Section 4: Visualizing Coverage in VSCode

### Option 1: Flutter Coverage Extension

1. Install **Flutter Coverage** extension in VSCode
2. Run: `flutter test --coverage`
3. Press `Cmd+Shift+P` (Mac) or `Ctrl+Shift+P` (Windows/Linux)
4. Type "Flutter Coverage: Toggle"
5. See coverage highlighting directly in your code!

**Color coding:**
- 🟢 Green highlight = tested line
- 🔴 Red highlight = untested line
- No highlight = not executable (comments, declarations)

### Option 2: Coverage Gutters Extension

1. Install **Coverage Gutters** extension in VSCode
2. Run: `flutter test --coverage`
3. Click "Watch" in the status bar
4. See coverage in the gutter (line numbers area)

**Gutter indicators:**
- ✅ Green dot = line covered
- ❌ Red dot = line not covered

### Option 3: Both Extensions Together

Use both for the best experience:
- **Flutter Coverage**: Highlights entire lines
- **Coverage Gutters**: Shows gutter indicators + branch coverage

---

## Section 5: Setting Coverage Targets

### Why Set Coverage Targets?

**Target = Minimum acceptable coverage percentage**

Examples:
- **Startups/Prototypes**: 50-60% (move fast, iterate)
- **Production apps**: 70-80% (balanced quality and speed)
- **Critical systems**: 90%+ (healthcare, finance, aviation)

### Enforcing Coverage in CI/CD

#### Method 1: Using lcov (Basic)

```bash
# scripts/check_coverage.sh
#!/bin/bash

COVERAGE_TARGET=70

flutter test --coverage

# Clean up generated files
lcov --remove coverage/lcov.info '*.g.dart' -o coverage/lcov.info

# Extract coverage percentage
COVERAGE=$(lcov --summary coverage/lcov.info 2>&1 | \
  grep 'lines......:' | \
  grep -oP '\d+\.\d+(?=%)')

echo "Coverage: ${COVERAGE}%"

# Check if coverage meets target
if (( $(echo "$COVERAGE < $COVERAGE_TARGET" | bc -l) )); then
  echo "❌ Coverage is below target of ${COVERAGE_TARGET}%"
  exit 1
else
  echo "✅ Coverage meets target of ${COVERAGE_TARGET}%"
  exit 0
fi
```

#### Method 2: Using test_coverage Package

```yaml
# pubspec.yaml
dev_dependencies:
  test_coverage: ^2.0.2
```

```bash
# Run tests and enforce 70% coverage
flutter pub run test_coverage --min-coverage 70

# Output:
# Coverage: 75.5%
# ✅ Coverage meets minimum requirement of 70%
```

### GitHub Actions with Coverage Check

```yaml
# .github/workflows/test-coverage.yml
name: Test Coverage

on:
  pull_request:
    branches: [ main ]

jobs:
  coverage:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - uses: subosito/flutter-action@v2
        with:
          flutter-version: '3.24.0'

      - name: Install dependencies
        run: flutter pub get

      - name: Run tests with coverage
        run: flutter test --coverage

      - name: Check coverage meets minimum
        run: |
          sudo apt-get install lcov
          lcov --remove coverage/lcov.info '*.g.dart' -o coverage/lcov.info

          COVERAGE=$(lcov --summary coverage/lcov.info 2>&1 | \
            grep 'lines......:' | \
            grep -oP '\d+\.\d+(?=%)')

          echo "Coverage: ${COVERAGE}%"

          if (( $(echo "$COVERAGE < 70" | bc -l) )); then
            echo "❌ Coverage below 70%"
            exit 1
          fi

      - name: Upload coverage to Codecov (optional)
        uses: codecov/codecov-action@v3
        with:
          files: ./coverage/lcov.info
```

---

## Section 6: Improving Coverage Strategically

### Step 1: Identify Untested Code

```bash
# Generate HTML report
./scripts/coverage.sh

# Open in browser and look for red lines
open coverage/html/index.html
```

### Step 2: Prioritize What to Test

**Not all code is equally important to test!**

**High priority (test first):**
- ✅ Business logic (calculations, validations, algorithms)
- ✅ Data transformations and processing
- ✅ Error handling and edge cases
- ✅ Public APIs and interfaces

**Medium priority:**
- ⚠️ UI logic (can use widget tests)
- ⚠️ Network layer (can mock)
- ⚠️ Database operations

**Low priority (okay to skip):**
- ⏩ Generated code (*.g.dart)
- ⏩ Simple getters/setters
- ⏩ Trivial constructors
- ⏩ UI widgets (covered by widget/integration tests)

### Step 3: Write Tests for Uncovered Code

**Example: Untested validator**

```dart
// lib/validators/email_validator.dart
class EmailValidator {
  static bool isValid(String email) {
    final regex = RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$');
    return regex.hasMatch(email);  // ❌ Currently untested
  }
}
```

**Coverage report shows:** 0% coverage for `isValid`

**Write tests:**

```dart
// test/validators/email_validator_test.dart
import 'package:flutter_test/flutter_test.dart';
import 'package:your_app/validators/email_validator.dart';

void main() {
  group('EmailValidator', () {
    test('valid email returns true', () {
      expect(EmailValidator.isValid('test@example.com'), true);
      expect(EmailValidator.isValid('user.name@company.co.uk'), true);
    });

    test('invalid email returns false', () {
      expect(EmailValidator.isValid('invalidemail'), false);
      expect(EmailValidator.isValid('@example.com'), false);
      expect(EmailValidator.isValid('test@'), false);
      expect(EmailValidator.isValid(''), false);
    });
  });
}
```

**New coverage:** 100% ✅

### Step 4: Track Coverage Over Time

Create a coverage badge for your README:

```bash
# Generate badge using shields.io
# Add to README.md:
```

```markdown
[![Coverage](https://img.shields.io/badge/coverage-75%25-brightgreen)](./coverage/html/index.html)
```

**Use Codecov or Coveralls for automatic tracking:**

```yaml
# .github/workflows/coverage.yml
- name: Upload to Codecov
  uses: codecov/codecov-action@v3
  with:
    files: ./coverage/lcov.info
    fail_ci_if_error: true
```

Visit [codecov.io](https://codecov.io) to see coverage trends over time.

---

## Section 7: Common Coverage Pitfalls

### Pitfall 1: Chasing 100% Coverage

**Bad mindset:**
```
"We must have 100% coverage no matter what!"
```

**Good mindset:**
```
"Let's aim for 70-80% coverage of meaningful code"
```

**Why:** Writing tests for trivial getters/setters wastes time. Focus on logic, not coverage percentage.

### Pitfall 2: Testing Implementation, Not Behavior

**Bad test (testing implementation):**
```dart
test('counter increments internal _value', () {
  final counter = Counter();
  counter.increment();
  expect(counter._value, 1); // Testing private implementation
});
```

**Good test (testing behavior):**
```dart
test('counter increments value', () {
  final counter = Counter();
  counter.increment();
  expect(counter.value, 1); // Testing public behavior
});
```

### Pitfall 3: Including Generated Files in Coverage

**Problem:** Generated files artificially lower coverage

**Solution:** Always exclude them:
```bash
lcov --remove coverage/lcov.info '*.g.dart' -o coverage/lcov.info
```

### Pitfall 4: Not Testing Edge Cases

**Weak test:**
```dart
test('divide works', () {
  expect(divide(10, 2), 5); // Only tests happy path
});
```

**Strong test:**
```dart
group('divide', () {
  test('works with positive numbers', () {
    expect(divide(10, 2), 5);
  });

  test('works with negative numbers', () {
    expect(divide(-10, 2), -5);
  });

  test('throws on division by zero', () {
    expect(() => divide(10, 0), throwsException);
  });
});
```

---

## Complete Example: Coverage Workflow

### Project Setup

```
my_flutter_app/
├── lib/
│   ├── models/
│   │   └── user.dart
│   ├── services/
│   │   └── auth_service.dart
│   └── utils/
│       └── validators.dart
├── test/
│   ├── models/
│   │   └── user_test.dart
│   ├── services/
│   │   └── auth_service_test.dart
│   └── utils/
│       └── validators_test.dart
├── scripts/
│   └── coverage.sh
└── .github/
    └── workflows/
        └── coverage.yml
```

### Script: scripts/coverage.sh

```bash
#!/bin/bash
set -e

echo "🧪 Running tests with coverage..."
flutter test --coverage --no-test-assets

echo "🧹 Removing generated files from coverage..."
lcov --remove coverage/lcov.info \
  '*.g.dart' \
  '*.freezed.dart' \
  '*.gr.dart' \
  'lib/generated/**' \
  -o coverage/lcov_cleaned.info

echo "📊 Generating HTML report..."
genhtml coverage/lcov_cleaned.info \
  -o coverage/html \
  --title "Flutter App Coverage" \
  --quiet

echo ""
echo "📈 Coverage Summary:"
lcov --summary coverage/lcov_cleaned.info

# Extract coverage percentage
COVERAGE=$(lcov --summary coverage/lcov_cleaned.info 2>&1 | \
  grep 'lines......:' | \
  grep -oP '\d+\.\d+(?=%)')

echo ""
echo "Coverage: ${COVERAGE}%"

# Check minimum coverage
MIN_COVERAGE=70
if (( $(echo "$COVERAGE < $MIN_COVERAGE" | bc -l) )); then
  echo "❌ Coverage is below minimum of ${MIN_COVERAGE}%"
  exit 1
else
  echo "✅ Coverage meets minimum of ${MIN_COVERAGE}%"
fi

echo ""
echo "Open coverage/html/index.html to view detailed report"
```

### GitHub Actions: .github/workflows/coverage.yml

```yaml
name: Coverage

on:
  pull_request:
    branches: [ main ]
  push:
    branches: [ main ]

jobs:
  coverage:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Setup Flutter
        uses: subosito/flutter-action@v2
        with:
          flutter-version: '3.24.0'
          channel: 'stable'

      - name: Install dependencies
        run: flutter pub get

      - name: Install lcov
        run: sudo apt-get install -y lcov

      - name: Run coverage script
        run: ./scripts/coverage.sh

      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          files: ./coverage/lcov_cleaned.info
          fail_ci_if_error: false

      - name: Upload coverage HTML as artifact
        uses: actions/upload-artifact@v4
        with:
          name: coverage-report
          path: coverage/html/
```

### Running Locally

```bash
# Make script executable (first time only)
chmod +x scripts/coverage.sh

# Run coverage
./scripts/coverage.sh

# View in browser
open coverage/html/index.html  # macOS
xdg-open coverage/html/index.html  # Linux
start coverage/html/index.html  # Windows
```

---

## Quiz

Test your understanding of test coverage:

### Question 1
What does 80% line coverage mean?

A) 80% of functions are tested
B) 80% of code lines are executed by tests
C) 80% of branches are tested
D) 80% of tests pass

### Question 2
Why should you exclude `*.g.dart` files from coverage reports?

A) They contain bugs
B) They're generated code that you don't write
C) They're too large
D) They're deprecated

### Question 3
What's the command to generate coverage data in Flutter?

A) `flutter test --coverage`
B) `flutter coverage`
C) `flutter test --cov`
D) `flutter analyze --coverage`

### Question 4
What's a reasonable coverage target for a production Flutter app?

A) 30-40%
B) 50-60%
C) 70-80%
D) 100%

### Question 5
Which code should you prioritize testing?

A) Generated files (*.g.dart)
B) Simple getters and setters
C) Business logic and data transformations
D) UI widget constructors

---

## Answer Key

**Question 1: B** - Line coverage measures the percentage of code lines that are executed when tests run. 80% means 80 out of 100 lines were executed.

**Question 2: B** - Generated files (*.g.dart, *.freezed.dart) are created by code generation tools. You don't write them, so testing coverage of generated code isn't meaningful.

**Question 3: A** - Use `flutter test --coverage` to run tests and generate coverage data in the `coverage/lcov.info` file.

**Question 4: C** - For production apps, 70-80% is a balanced target. It ensures critical code is tested without spending excessive time on trivial code. Critical systems (healthcare, finance) may require 90%+.

**Question 5: C** - Prioritize testing business logic, algorithms, calculations, validations, and data transformations. These are where bugs have the most impact. Skip trivial getters, setters, and generated code.

---

## Summary

In this lesson, you learned:

✅ **Test coverage** measures which code is executed by tests
✅ Three metrics: **line, function, and branch coverage**
✅ Generate coverage with `flutter test --coverage`
✅ Create HTML reports with `genhtml coverage/lcov.info -o coverage/html`
✅ **Exclude generated files** (*.g.dart) from coverage reports
✅ Visualize coverage in VSCode with **Flutter Coverage** and **Coverage Gutters**
✅ Set **coverage targets** (70-80% for production apps)
✅ Enforce coverage in CI/CD to prevent regressions
✅ Prioritize testing **business logic** over trivial code
✅ Track coverage over time with Codecov or Coveralls

**Key Takeaway:** Test coverage is a powerful tool to identify untested code, but it's not the goal itself. Focus on testing meaningful business logic and critical paths. A project with 70% coverage of the right code is better than 95% coverage of trivial code.

---

## What's Next?

In **Lesson 10.7: CI/CD for Flutter Apps**, you'll learn how to automate your entire testing pipeline—running unit tests, widget tests, integration tests, and coverage checks automatically on every commit using GitHub Actions, Codemagic, and other CI/CD platforms.
