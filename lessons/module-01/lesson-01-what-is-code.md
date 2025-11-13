# Module 1, Lesson 1: What is Code?

## Welcome to Programming!

Congratulations on completing Module 0! You've got Flutter installed, your editor set up, and you've even run your first app. Now it's time to understand what's actually happening when you write code.

This is where your journey as a *real* programmer begins.

---

## The Recipe Analogy

Imagine you're teaching a robot to make a sandwich. The robot is very literal - it only does *exactly* what you tell it to do.

You can't just say "make me a sandwich." You need to give step-by-step instructions:

```
1. Get two slices of bread
2. Open the peanut butter jar
3. Spread peanut butter on one slice
4. Open the jelly jar
5. Spread jelly on the other slice
6. Put the two slices together
7. Close both jars
```

**This is what code is**: A series of precise instructions that a computer follows, step by step.

---

## Code is Just Instructions

When you write code, you're writing instructions for a computer. The computer:
- Reads your instructions from top to bottom (usually)
- Executes them exactly as written
- Doesn't make assumptions or "guess" what you meant
- Will do exactly what you say, even if it's wrong!

This is both powerful and dangerous:
- **Powerful**: You have complete control
- **Dangerous**: Small mistakes (like forgetting a step) cause errors

---

## Your First Line of Code

Let's write the simplest possible code. Open VS Code and create a new file called `first_code.dart`.

Type this exactly:

```dart
void main() {
  print('Hello, World!');
}
```

Now run it:
1. Press `Ctrl/Cmd + Shift + P`
2. Type "Dart: Run"
3. Press Enter

You should see in the terminal:

```
Hello, World!
```

**Congratulations!** You just wrote and executed your first program! 🎉

---

## Breaking It Down (Conceptual First)

Let's understand what each part does, *in plain English first*:

```dart
void main() {
  print('Hello, World!');
}
```

Think of this like a play with actors on a stage:

1. **The stage**: `main()` is the main stage where your program starts. Every Dart program must have a `main()`. It's the starting point.

2. **The action**: Inside the curly braces `{ }` is what happens on that stage.

3. **The dialogue**: `print('Hello, World!');` is like an actor saying a line. It displays text.

---

## Now the Technical Terms

Now that you understand the *concept*, here are the official programming terms:

- **`void`**: This means "doesn't give back any information." Don't worry about this yet.

- **`main()`**: This is called a **function**. It's a container for instructions. The `main` function is special - it's where every program begins.

- **`{ }`**: These curly braces define the **body** of the function. Everything inside them is part of `main`.

- **`print()`**: This is also a function, but one that's already built into Dart. It displays text in the terminal.

- **`'Hello, World!'`**: This is a **string** - programmer-speak for "text." Strings always go in quotes.

- **`;`**: The semicolon tells Dart "this instruction is complete." It's like a period at the end of a sentence.

---

## The Golden Rule of Learning Code

**Don't memorize syntax. Understand concepts.**

You might forget whether to use `print()` or `display()`. That's okay! You can always look it up.

What matters is understanding:
- Programs run instructions in order
- You need a starting point (`main`)
- You can tell the computer to display text

The exact spelling and punctuation will become natural with practice.

---

## Let's Experiment!

### Experiment 1: Multiple Lines

Try this:

```dart
void main() {
  print('Hello, World!');
  print('My name is [Your Name]');
  print('I am learning Flutter!');
}
```

Run it. What do you see? Three lines of output!

**Takeaway**: Instructions execute one after another, from top to bottom.

### Experiment 2: What Happens If...?

Try this (intentionally wrong):

```dart
void main() {
  print('Forgot the semicolon')
  print('Oops!');
}
```

Run it. You get an error! Something like:

```
Error: Expected ';' after this.
```

**Takeaway**: Computers are picky. Every detail matters. Semicolons are required.

### Experiment 3: Inside the Quotes

Try this:

```dart
void main() {
  print('I can print numbers: 123');
  print('I can print symbols: !@#$%');
  print('I can even print emojis: 🎉🚀');
}
```

**Takeaway**: Anything inside quotes is treated as text - numbers, symbols, emojis, everything!

---

## Common Beginner Mistakes

Here are mistakes everyone makes at first:

| Mistake | What Happens |
|---------|--------------|
| Forgetting `;` | Error: "Expected ';'" |
| Mismatched quotes `'Hello"` | Error: "Unexpected character" |
| Forgetting `()` after `main` | Error: "Expected '('" |
| Typing `Main` instead of `main` | Error: "Expected 'main'" |
| Forgetting closing `}` | Error: "Expected '}'" |

**These are normal!** Every programmer makes these mistakes. The computer will always tell you exactly what's wrong.

---

## ✅ YOUR CHALLENGE: Write Your First Program

**Goal**: Write a program that introduces yourself.

**Requirements**:

Create a file called `introduction.dart` and write a program that prints:
- Your name
- Where you're from
- Why you're learning Flutter

**Example output**:

```
My name is Alex
I'm from New York
I'm learning Flutter because I want to build my own apps!
```

**Hints**:
- You'll need 3 `print()` statements
- Each needs to be on its own line
- Don't forget the semicolons!

**Success Condition**: Your program runs without errors and displays your introduction. ✅

---

## What Did We Learn?

Let's recap:
- ✅ Code is just step-by-step instructions
- ✅ Programs start at `main()`
- ✅ `print()` displays text
- ✅ Strings (text) go in quotes
- ✅ Semicolons end statements
- ✅ Computers are very literal and precise

---

## What's Next?

Right now, we can only display pre-written text. What if we want to store information and reuse it?

In the next lesson, we'll learn about **variables** - how to store and work with information in your programs. Think of them as labeled boxes that hold data!

See you in the next lesson! 🚀
