# Module 1, Lesson 2: Storing Information (Variables)

## The Box Analogy

Imagine you're organizing your room. You have different boxes:
- A box labeled "TOYS" containing your toys
- A box labeled "BOOKS" containing your books
- A box labeled "CLOTHES" containing your clothes

Each box has:
1. A **label** (so you know what's inside)
2. **Contents** (the actual stuff)

In programming, we call these boxes **variables**. They let us store information and give it a name so we can use it later.

---

## Why Do We Need Variables?

Look at this code:

```dart
void main() {
  print('Hello, Sarah!');
  print('Welcome back, Sarah!');
  print('Sarah, you have 3 new messages.');
}
```

What if we want to change the name from "Sarah" to "John"? We'd have to change it in 3 places!

Now look at this:

```dart
void main() {
  var name = 'Sarah';
  print('Hello, $name!');
  print('Welcome back, $name!');
  print('$name, you have 3 new messages.');
}
```

Now if we want to use a different name, we only change it in **one place**! That's the power of variables.

---

## Creating Your First Variable

The basic syntax is:

```dart
var name = 'Sarah';
```

Let's break this down:

**Conceptual Explanation**:
- We're creating a box
- The box is labeled `name`
- We're putting the text `'Sarah'` inside it

**Technical Terms**:
- `var`: This keyword tells Dart "I'm about to create a variable"
- `name`: This is the variable name (the label on the box)
- `=`: This is the assignment operator (putting something in the box)
- `'Sarah'`: This is the value (the contents of the box)

---

## Different Types of Boxes

Just like in real life, we need different types of containers for different things. You wouldn't store milk in a cardboard box!

In Dart, variables have **types**:

### 1. Text (Strings)

For storing words and sentences:

```dart
var name = 'Sarah';
var greeting = 'Hello, World!';
var email = 'sarah@example.com';
```

### 2. Numbers (Integers)

For storing whole numbers:

```dart
var age = 25;
var messageCount = 3;
var score = 100;
```

### 3. Decimal Numbers (Doubles)

For storing numbers with decimals:

```dart
var price = 19.99;
var temperature = 72.5;
var rating = 4.8;
```

### 4. True/False (Booleans)

For storing yes/no, true/false values:

```dart
var isLoggedIn = true;
var hasNewMessages = false;
var isWeekend = true;
```

---

## Using Variables

Once you create a variable, you can use it anywhere in your code:

```dart
void main() {
  var name = 'Alex';
  var age = 28;
  var city = 'New York';

  print('My name is $name');
  print('I am $age years old');
  print('I live in $city');
}
```

**Output**:
```
My name is Alex
I am 28 years old
I live in New York
```

Notice the `$` symbol? That's how we insert variables into strings. It's called **string interpolation**.

---

## Changing Variable Contents

Variables aren't permanent - you can change what's inside the box:

```dart
void main() {
  var mood = 'happy';
  print('I am feeling $mood');  // Output: I am feeling happy

  mood = 'excited';  // Change the contents
  print('Now I am feeling $mood');  // Output: Now I am feeling excited
}
```

Notice: The second time, we don't use `var` - we already created the variable!

---

## Variable Naming Rules

You can't just name variables anything you want. There are rules:

**✅ Valid Names**:
```dart
var name = 'Alex';
var firstName = 'Alex';
var first_name = 'Alex';
var age2 = 25;
var myAge = 25;
```

**❌ Invalid Names**:
```dart
var 2age = 25;           // Can't start with a number
var first-name = 'Alex'; // Can't use hyphens
var my age = 25;         // Can't have spaces
var class = 'Math';      // 'class' is a reserved word
```

**Naming Convention (Best Practice)**:
- Use camelCase: `firstName`, `myAge`, `isLoggedIn`
- First word lowercase, subsequent words capitalized
- Be descriptive: `userName` is better than `un`

---

## Explicit Types (Being More Specific)

Instead of using `var` (where Dart guesses the type), you can be explicit:

```dart
void main() {
  String name = 'Sarah';      // This box only holds text
  int age = 25;               // This box only holds integers
  double price = 19.99;       // This box only holds decimals
  bool isActive = true;       // This box only holds true/false
}
```

**When to use `var` vs explicit types?**
- `var`: When the type is obvious from the value
- Explicit (`String`, `int`, etc.): When you want to be extra clear

Both work! It's mostly personal preference.

---

## Math with Variables

You can do math with number variables:

```dart
void main() {
  var apples = 5;
  var oranges = 3;
  var totalFruit = apples + oranges;

  print('Total fruit: $totalFruit');  // Output: Total fruit: 8

  var price = 10.50;
  var tax = 2.15;
  var total = price + tax;

  print('Total with tax: \$${total}');  // Output: Total with tax: $12.65
}
```

**Note**: To print an actual `$` symbol, you need to escape it with `\$`.

---

## Common Beginner Mistakes

| Mistake | What Happens |
|---------|--------------|
| `var Name = 'Alex';` | Works, but should be `name` (lowercase first letter) |
| `name = 'Alex';` without `var` first | Error: Variable not declared |
| `var age = '25';` then trying to do math | Wrong! '25' is text, not a number |
| `var age = 25; var age = 30;` | Error: Variable already declared |
| Using a variable before creating it | Error: Undefined name |

---

## ✅ YOUR CHALLENGE: Build a Profile

**Goal**: Create variables to store information about yourself, then display it.

**Requirements**:

Create a file called `my_profile.dart` with:

1. Variables for:
   - Your full name (String)
   - Your age (int)
   - Your favorite food (String)
   - Whether you like programming (bool - true or false)
   - Your height in feet (double)

2. Print out all this information in a nice format

**Example output**:

```
=== My Profile ===
Name: Alex Johnson
Age: 28
Favorite Food: Pizza
Likes Programming: true
Height: 5.9 feet
```

**Hints**:
- Use `String`, `int`, `double`, and `bool` types
- Use `$variableName` to insert variables into print statements
- You'll need 5-6 `print()` statements

**Success Condition**: Your program displays all your profile information using variables. ✅

---

## Bonus Challenge: Do Some Math

Add these variables and calculations:

```dart
var currentYear = 2024;
var birthYear = 1996;  // Use your actual birth year
var calculatedAge = currentYear - birthYear;

print('Calculated age: $calculatedAge');
```

Does it match your age variable?

---

## What Did We Learn?

Let's recap:
- ✅ Variables are like labeled boxes that store information
- ✅ Use `var` or explicit types (`String`, `int`, `double`, `bool`)
- ✅ Use `$variableName` to insert variables into strings
- ✅ Variables can be changed after creation
- ✅ We can do math with number variables
- ✅ Variable names follow specific rules

---

## What's Next?

Now we can store information. But what if we want our program to make decisions?

"If the user is logged in, show the dashboard. Otherwise, show the login page."

"If the age is under 18, show 'You're a minor'. Otherwise, show 'You're an adult'."

In the next lesson, we'll learn about **conditionals** (if/else statements) - how to make your program smart enough to make decisions!

See you there! 🚀
