# Module 1, Lesson 4: Repeating Actions (Loops)

## The Chore Analogy

Imagine your parent tells you: "Wash all 10 dishes in the sink."

You wouldn't write:
```
Wash dish 1
Wash dish 2
Wash dish 3
Wash dish 4
Wash dish 5
Wash dish 6
Wash dish 7
Wash dish 8
Wash dish 9
Wash dish 10
```

You'd think: "**Repeat** washing until all dishes are done."

That's exactly what **loops** do in programming - they repeat actions without you having to write the same code over and over.

---

## Why Do We Need Loops?

Look at this code:

```dart
void main() {
  print('Welcome user 1!');
  print('Welcome user 2!');
  print('Welcome user 3!');
  print('Welcome user 4!');
  print('Welcome user 5!');
}
```

What if you have 100 users? Or 1000? You can't write 1000 lines!

With a loop:

```dart
void main() {
  for (var i = 1; i <= 5; i++) {
    print('Welcome user $i!');
  }
}
```

**Output**:
```
Welcome user 1!
Welcome user 2!
Welcome user 3!
Welcome user 4!
Welcome user 5!
```

Same result, way less code!

---

## The "for" Loop - Counting Repetitions

When you know **exactly how many times** to repeat something, use a `for` loop.

**Conceptual Explanation**:
Think of it like counting:
- **Start** at 1
- **Keep going** while less than or equal to 5
- **Count up** by 1 each time

**The Pattern**:

```dart
for (start; condition; increment) {
  // Code to repeat
}
```

**Real Example**:

```dart
void main() {
  for (var i = 1; i <= 3; i++) {
    print('This is repetition number $i');
  }
}
```

**Output**:
```
This is repetition number 1
This is repetition number 2
This is repetition number 3
```

---

## Breaking Down the "for" Loop

```dart
for (var i = 1; i <= 3; i++) {
  print('Count: $i');
}
```

**The Three Parts**:

1. **`var i = 1`** - **Start**: Create a counter starting at 1
2. **`i <= 3`** - **Condition**: Keep looping while i is ≤ 3
3. **`i++`** - **Increment**: Add 1 to i after each loop

**`i++` is shorthand for `i = i + 1`**

**What Happens**:
- First time: i = 1, prints "Count: 1", then i becomes 2
- Second time: i = 2, prints "Count: 2", then i becomes 3
- Third time: i = 3, prints "Count: 3", then i becomes 4
- Fourth time: i = 4, but 4 is not ≤ 3, so STOP

---

## Different Counting Patterns

### Counting Down

```dart
void main() {
  for (var i = 5; i >= 1; i--) {
    print('$i...');
  }
  print('Blastoff! 🚀');
}
```

**Output**:
```
5...
4...
3...
2...
1...
Blastoff! 🚀
```

**Note**: `i--` means "subtract 1" (shorthand for `i = i - 1`)

### Counting by 2s

```dart
void main() {
  for (var i = 0; i <= 10; i += 2) {
    print(i);
  }
}
```

**Output**:
```
0
2
4
6
8
10
```

**Note**: `i += 2` means "add 2" (shorthand for `i = i + 2`)

### Starting from Any Number

```dart
void main() {
  for (var age = 18; age <= 21; age++) {
    print('At age $age, you can...');
  }
}
```

---

## The "while" Loop - Repeat Until...

When you **don't know how many times** you'll repeat, but you know **when to stop**, use a `while` loop.

**Conceptual Explanation**:
Think of it like: "**While** it's raining, stay inside."
- You don't know how long it will rain
- But you know the condition to check

**The Pattern**:

```dart
while (condition) {
  // Code to repeat
}
```

**Real Example**:

```dart
void main() {
  var count = 1;

  while (count <= 5) {
    print('Count is $count');
    count++;  // Important! Must change the condition
  }
}
```

**Output**:
```
Count is 1
Count is 2
Count is 3
Count is 4
Count is 5
```

**⚠️ Warning**: If you forget `count++`, the loop runs FOREVER (infinite loop)!

---

## Real-World Examples

### Example 1: Multiplication Table

```dart
void main() {
  var number = 7;

  print('Multiplication table for $number:');
  for (var i = 1; i <= 10; i++) {
    print('$number x $i = ${number * i}');
  }
}
```

**Output**:
```
Multiplication table for 7:
7 x 1 = 7
7 x 2 = 14
7 x 3 = 21
...
```

### Example 2: Password Attempts

```dart
void main() {
  var correctPassword = 'flutter123';
  var userPassword = '';
  var attempts = 0;

  while (userPassword != correctPassword && attempts < 3) {
    attempts++;
    print('Attempt $attempts: Enter password');
    // In a real app, we'd get user input here
    userPassword = 'wrong'; // Simulating wrong password
  }

  if (userPassword == correctPassword) {
    print('Access granted!');
  } else {
    print('Too many failed attempts. Locked out!');
  }
}
```

### Example 3: Sum of Numbers

```dart
void main() {
  var sum = 0;

  for (var i = 1; i <= 10; i++) {
    sum += i;  // Same as: sum = sum + i
  }

  print('Sum of 1 to 10 is: $sum');  // Output: 55
}
```

---

## The "break" Keyword - Exit Early

Sometimes you want to **stop a loop** before it naturally ends:

```dart
void main() {
  for (var i = 1; i <= 100; i++) {
    print(i);

    if (i == 5) {
      break;  // Stop the loop right now!
    }
  }
  print('Loop stopped');
}
```

**Output**:
```
1
2
3
4
5
Loop stopped
```

**Use case**: Searching for something - once you find it, stop looking!

---

## The "continue" Keyword - Skip to Next

Sometimes you want to **skip the current iteration** and continue with the next:

```dart
void main() {
  for (var i = 1; i <= 5; i++) {
    if (i == 3) {
      continue;  // Skip this iteration
    }
    print(i);
  }
}
```

**Output**:
```
1
2
4
5
```

**Notice**: 3 is missing because we skipped it!

**Use case**: Filtering - process items that match a condition, skip others.

---

## Nested Loops - Loops Inside Loops

You can put loops inside loops!

```dart
void main() {
  for (var row = 1; row <= 3; row++) {
    for (var col = 1; col <= 3; col++) {
      print('Row $row, Column $col');
    }
  }
}
```

**Output**:
```
Row 1, Column 1
Row 1, Column 2
Row 1, Column 3
Row 2, Column 1
Row 2, Column 2
Row 2, Column 3
Row 3, Column 1
Row 3, Column 2
Row 3, Column 3
```

**Use case**: Grid patterns, tables, 2D games (rows and columns).

---

## Common Beginner Mistakes

| Mistake | What Happens |
|---------|--------------|
| Forgetting `i++` in while loop | Infinite loop! |
| Using `=` instead of `==` in condition | Always true or syntax error |
| Starting at wrong number | Loop runs wrong number of times |
| Off-by-one error (`< 5` vs `<= 5`) | Loop runs one too few or too many times |
| Forgetting `var` before `i` | Error: i not defined |

---

## ✅ YOUR CHALLENGE: Loops Practice

**Goal**: Master loops by creating different patterns.

### Challenge 1: Print Numbers 1-20

Create a file called `loops_practice.dart`:

```dart
void main() {
  // Your code here: print numbers 1 to 20
}
```

**Expected output**: Numbers 1 through 20, each on its own line.

### Challenge 2: Even Numbers Only

Print only **even numbers** from 0 to 20.

**Hint**: Use `i += 2` or use an if statement with `i % 2 == 0`

**Expected output**:
```
0
2
4
6
...
20
```

### Challenge 3: Countdown Timer

Create a countdown from 10 to 1, then print "GO!".

**Expected output**:
```
10
9
8
...
2
1
GO!
```

### Challenge 4: Sum Calculator

Calculate the sum of all numbers from 1 to 50.

**Expected output**: `Sum: 1275`

### Challenge 5: Pattern Maker

Print this pattern using nested loops:

```
*
**
***
****
*****
```

**Hint**: Outer loop for rows, inner loop for stars in each row.

**Success Condition**: You've completed at least 3 of the 5 challenges. ✅

---

## Bonus Challenge: FizzBuzz

This is a classic programming interview question!

**Rules**:
- Print numbers 1 to 30
- If number is divisible by 3, print "Fizz" instead
- If divisible by 5, print "Buzz" instead
- If divisible by both 3 and 5, print "FizzBuzz"

**Expected output**:
```
1
2
Fizz
4
Buzz
Fizz
7
8
Fizz
Buzz
11
Fizz
13
14
FizzBuzz
...
```

**Hint**: Use `%` (modulo) operator:
- `i % 3 == 0` means divisible by 3
- `i % 5 == 0` means divisible by 5

---

## What Did We Learn?

Let's recap:
- ✅ Loops let us repeat code without copy-pasting
- ✅ `for` loops are for known repetitions
- ✅ `while` loops run until a condition is false
- ✅ `i++` increments, `i--` decrements
- ✅ `break` exits a loop early
- ✅ `continue` skips to the next iteration
- ✅ Nested loops create patterns and grids

---

## What's Next?

Now we can store data (variables), make decisions (if/else), and repeat actions (loops). But what if we want to **organize** our code into reusable pieces?

In the next lesson, we'll learn about **Functions** - how to create your own custom commands that you can use over and over!

Think of them as creating your own recipes that you can follow anytime.

See you there! 🚀
