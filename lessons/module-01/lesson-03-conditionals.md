# Module 1, Lesson 3: Making Decisions (if/else)

## Choose Your Own Adventure

Remember those "Choose Your Own Adventure" books?

> You're standing at a fork in the road.
> - If you go left, turn to page 42
> - If you go right, turn to page 67

Programs need to make decisions like this all the time:
- If the password is correct, log the user in. Otherwise, show an error.
- If it's raining, bring an umbrella. Otherwise, leave it home.
- If the score is above 90, show an "A". Otherwise, show a different grade.

This is what **conditionals** do - they let your program choose different paths based on conditions.

---

## The Basic Pattern: if

Here's the simplest decision:

```dart
void main() {
  var age = 20;

  if (age >= 18) {
    print('You are an adult!');
  }
}
```

**Conceptual Explanation**:
- We check a condition: "Is age greater than or equal to 18?"
- If the answer is YES (true), we run the code inside the `{ }`
- If the answer is NO (false), we skip that code

**Output**: `You are an adult!` (because 20 is >= 18)

---

## Now the Technical Terms

```dart
if (condition) {
  // Code to run if condition is true
}
```

- **`if`**: Keyword that starts a conditional
- **`(condition)`**: The test we're checking (must be true or false)
- **`{ }`**: The block of code to run if the condition is true

---

## Adding an "Otherwise": else

What if we want to do something when the condition is false?

```dart
void main() {
  var age = 15;

  if (age >= 18) {
    print('You are an adult!');
  } else {
    print('You are a minor.');
  }
}
```

**Output**: `You are a minor.` (because 15 is not >= 18)

Think of it like:
- **IF** the condition is true, do the first thing
- **OTHERWISE** (else), do the second thing

---

## Multiple Choices: else if

What if you have more than two options?

```dart
void main() {
  var score = 85;

  if (score >= 90) {
    print('Grade: A');
  } else if (score >= 80) {
    print('Grade: B');
  } else if (score >= 70) {
    print('Grade: C');
  } else if (score >= 60) {
    print('Grade: D');
  } else {
    print('Grade: F');
  }
}
```

**Output**: `Grade: B`

The program:
1. Checks if score >= 90 (NO, 85 is not >= 90)
2. Checks if score >= 80 (YES! → runs this block)
3. Stops checking (once one condition is true, it skips the rest)

---

## Comparison Operators

These are the symbols we use to compare things:

| Operator | Meaning | Example |
|----------|---------|---------|
| `==` | Equal to | `age == 18` |
| `!=` | Not equal to | `age != 18` |
| `>` | Greater than | `age > 18` |
| `<` | Less than | `age < 18` |
| `>=` | Greater than or equal | `age >= 18` |
| `<=` | Less than or equal | `age <= 18` |

**Common Mistake**: Using `=` instead of `==`
- `=` means "assign a value" (putting something in a box)
- `==` means "compare for equality" (checking if two things are equal)

```dart
var age = 18;      // ✅ Assignment (setting age to 18)
if (age == 18) {   // ✅ Comparison (checking if age equals 18)
  print('Age is 18');
}
```

---

## Real-World Examples

### Example 1: Login Check

```dart
void main() {
  var password = 'secret123';
  var userInput = 'secret123';

  if (userInput == password) {
    print('Login successful! Welcome!');
  } else {
    print('Incorrect password. Try again.');
  }
}
```

### Example 2: Weather Advice

```dart
void main() {
  var temperature = 75;

  if (temperature > 85) {
    print('It\'s hot! Drink lots of water.');
  } else if (temperature > 60) {
    print('Nice weather! Perfect for a walk.');
  } else {
    print('It\'s cold! Wear a jacket.');
  }
}
```

**Note**: `\'` lets you put an apostrophe inside a single-quoted string.

### Example 3: Shopping Cart

```dart
void main() {
  var itemPrice = 50.00;
  var walletMoney = 45.00;

  if (walletMoney >= itemPrice) {
    print('Purchase successful!');
  } else {
    var shortage = itemPrice - walletMoney;
    print('You need \$$shortage more.');
  }
}
```

**Output**: `You need $5.0 more.`

---

## Combining Conditions: AND / OR

Sometimes you need to check multiple things at once.

### AND (&&) - Both must be true

```dart
void main() {
  var age = 25;
  var hasLicense = true;

  if (age >= 16 && hasLicense) {
    print('You can drive!');
  } else {
    print('You cannot drive.');
  }
}
```

### OR (||) - At least one must be true

```dart
void main() {
  var isWeekend = true;
  var isHoliday = false;

  if (isWeekend || isHoliday) {
    print('No work today!');
  } else {
    print('Time to work.');
  }
}
```

### NOT (!) - Flips true/false

```dart
void main() {
  var isRaining = false;

  if (!isRaining) {
    print('It\'s not raining. Let\'s go outside!');
  }
}
```

---

## Common Patterns

### Pattern 1: Range Checking

```dart
var age = 25;

if (age >= 13 && age <= 19) {
  print('You are a teenager');
}
```

### Pattern 2: Eligibility Checking

```dart
var age = 67;

if (age >= 65) {
  print('You qualify for senior discount!');
} else {
  print('Regular pricing applies.');
}
```

### Pattern 3: Validation

```dart
var username = '';

if (username == '') {
  print('Error: Username cannot be empty');
} else {
  print('Username: $username');
}
```

---

## ✅ YOUR CHALLENGE: Build a Simple Program

**Goal**: Create a program that gives personalized advice based on age.

**Requirements**:

Create a file called `age_advice.dart` that:

1. Has a variable `age` set to your age (or any age)
2. Uses if/else if/else to print different messages:
   - If age < 13: "You're a child! Enjoy playing!"
   - If age >= 13 and < 20: "You're a teenager! Study hard!"
   - If age >= 20 and < 65: "You're an adult! Work hard, but enjoy life!"
   - If age >= 65: "You're a senior! Time to relax and enjoy retirement!"

**Example output** (if age = 25):

```
You're an adult! Work hard, but enjoy life!
```

**Hints**:
- You'll need at least 3 `else if` statements
- Use `&&` to check age ranges
- Test with different ages to make sure it works!

**Success Condition**: Your program gives the correct advice for any age you input. ✅

---

## Bonus Challenge: Grade Calculator

Create a program that takes a score (0-100) and:
- Prints the letter grade (A, B, C, D, F)
- Adds a "+" or "-" modifier:
  - 93-100: A
  - 90-92: A-
  - 87-89: B+
  - And so on...

This is tricky! You'll need nested conditions or multiple checks.

---

## Common Beginner Mistakes

| Mistake | What Happens |
|---------|--------------|
| `if (age = 18)` instead of `if (age == 18)` | Syntax error or unexpected behavior |
| Forgetting `{ }` around code blocks | Only the first line is conditional |
| `if (age > 18 && < 30)` | Syntax error - need `age < 30` |
| Not covering all cases with else | Some inputs might not do anything |
| Checking conditions in wrong order | Wrong condition might match first |

---

## What Did We Learn?

Let's recap:
- ✅ `if` lets programs make decisions
- ✅ `else` handles the "otherwise" case
- ✅ `else if` handles multiple options
- ✅ Comparison operators: `==`, `!=`, `>`, `<`, `>=`, `<=`
- ✅ Logical operators: `&&` (AND), `||` (OR), `!` (NOT)
- ✅ Conditions must evaluate to true or false

---

## What's Next?

Now we can store information (variables) and make decisions (if/else). But what if we need to do something many times?

For example:
- Print numbers 1 through 100
- Process every item in a shopping cart
- Repeat a game until the player wants to quit

In the next lesson, we'll learn about **loops** - how to repeat actions without copying and pasting code!

See you there! 🚀
