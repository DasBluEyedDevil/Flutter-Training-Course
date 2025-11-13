# Module 1, Lesson 7: Mini-Project - Number Guessing Game

## Putting It All Together

Congratulations on making it this far! You've learned all the Dart fundamentals:
- ✅ Variables
- ✅ Conditionals (if/else)
- ✅ Loops
- ✅ Functions
- ✅ Lists and Maps

Now it's time to **combine everything** into a real project!

---

## What We're Building

A **Number Guessing Game** that:
- Picks a random number between 1 and 100
- Lets the player guess the number
- Gives hints ("too high" or "too low")
- Tracks the number of guesses
- Allows playing multiple rounds

**Skills you'll practice**:
- Using variables to track game state
- Using conditionals to check guesses
- Using loops for multiple attempts
- Using functions to organize code
- Using lists to track guess history

---

## Project Structure

We'll build this in steps:

1. **Version 1**: Basic game with hardcoded number
2. **Version 2**: Add random number generation
3. **Version 3**: Add attempt counter and guess history
4. **Version 4**: Add multi-round support
5. **Version 5**: Add difficulty levels

---

## Version 1: The Foundation

Let's start simple - player tries to guess a specific number.

Create a file called `guessing_game.dart`:

```dart
void main() {
  // The secret number
  var secretNumber = 42;

  // Simulate player guesses (we'll make this interactive later)
  var guesses = [50, 30, 40, 42];

  print('=== Number Guessing Game ===');
  print('I\'m thinking of a number between 1 and 100...');

  for (var guess in guesses) {
    print('\nYou guessed: $guess');

    if (guess == secretNumber) {
      print('🎉 Correct! You win!');
      break;  // Exit the loop
    } else if (guess > secretNumber) {
      print('📉 Too high! Try again.');
    } else {
      print('📈 Too low! Try again.');
    }
  }
}
```

**Run it!** You should see:

```
=== Number Guessing Game ===
I'm thinking of a number between 1 and 100...

You guessed: 50
📉 Too high! Try again.

You guessed: 30
📈 Too low! Try again.

You guessed: 40
📈 Too low! Try again.

You guessed: 42
🎉 Correct! You win!
```

**What's happening**:
- We have a secret number (42)
- We loop through guesses
- For each guess, we give feedback
- When correct, we celebrate and exit

---

## Version 2: Adding Random Numbers

Instead of always guessing 42, let's make it random!

**First, import the Random library** at the top of your file:

```dart
import 'dart:math';

void main() {
  // Generate random number between 1 and 100
  var random = Random();
  var secretNumber = random.nextInt(100) + 1;

  print('=== Number Guessing Game ===');
  print('I\'m thinking of a number between 1 and 100...');
  print('(Psst... it\'s $secretNumber - but pretend you don\'t know!)');

  // Rest of code...
}
```

**Understanding `random.nextInt(100) + 1`**:
- `random.nextInt(100)` gives 0-99
- `+ 1` shifts it to 1-100

**Try running it multiple times** - you'll get different numbers!

---

## Version 3: Tracking Attempts and History

Let's count how many guesses it takes and remember all guesses:

```dart
import 'dart:math';

void main() {
  var random = Random();
  var secretNumber = random.nextInt(100) + 1;
  var guesses = [50, 30, 40, 45, 42];  // Simulated guesses
  var attemptCount = 0;
  List<int> guessHistory = [];

  print('=== Number Guessing Game ===');
  print('I\'m thinking of a number between 1 and 100...');

  for (var guess in guesses) {
    attemptCount++;
    guessHistory.add(guess);

    print('\n--- Attempt $attemptCount ---');
    print('You guessed: $guess');

    if (guess == secretNumber) {
      print('🎉 Correct! You win!');
      print('It took you $attemptCount attempts.');
      print('Your guesses: $guessHistory');
      break;
    } else if (guess > secretNumber) {
      print('📉 Too high! Try again.');
    } else {
      print('📈 Too low! Try again.');
    }
  }
}
```

**New features**:
- `attemptCount` tracks number of tries
- `guessHistory` remembers all guesses
- We show a summary at the end

---

## Version 4: Organizing with Functions

Our code is getting messy. Let's use functions to organize it:

```dart
import 'dart:math';

// Function to generate random number
int generateSecretNumber() {
  var random = Random();
  return random.nextInt(100) + 1;
}

// Function to check a guess
String checkGuess(int guess, int secret) {
  if (guess == secret) {
    return 'correct';
  } else if (guess > secret) {
    return 'high';
  } else {
    return 'low';
  }
}

// Function to print game header
void printHeader() {
  print('=== Number Guessing Game ===');
  print('I\'m thinking of a number between 1 and 100...\n');
}

// Function to print game summary
void printSummary(int attempts, List<int> history) {
  print('\n🎉 You win!');
  print('It took you $attempts attempts.');
  print('Your guesses: $history');
}

void main() {
  var secretNumber = generateSecretNumber();
  var guesses = [50, 30, 70, 60, 55, 52, 51];  // Simulated
  var attemptCount = 0;
  List<int> guessHistory = [];

  printHeader();

  for (var guess in guesses) {
    attemptCount++;
    guessHistory.add(guess);

    print('Attempt $attemptCount: You guessed $guess');

    var result = checkGuess(guess, secretNumber);

    if (result == 'correct') {
      printSummary(attemptCount, guessHistory);
      break;
    } else if (result == 'high') {
      print('📉 Too high! Try again.\n');
    } else {
      print('📈 Too low! Try again.\n');
    }
  }
}
```

**Much better!** Each function has one job:
- `generateSecretNumber()` - creates random number
- `checkGuess()` - compares guess to secret
- `printHeader()` - shows game title
- `printSummary()` - shows final stats

---

## Version 5: Adding Difficulty Levels

Let's add difficulty levels with different ranges:

```dart
import 'dart:math';

int generateSecretNumber(String difficulty) {
  var random = Random();

  if (difficulty == 'easy') {
    return random.nextInt(50) + 1;  // 1-50
  } else if (difficulty == 'medium') {
    return random.nextInt(100) + 1;  // 1-100
  } else {  // hard
    return random.nextInt(500) + 1;  // 1-500
  }
}

void printHeader(String difficulty) {
  print('=== Number Guessing Game ===');
  print('Difficulty: ${difficulty.toUpperCase()}');

  if (difficulty == 'easy') {
    print('I\'m thinking of a number between 1 and 50...\n');
  } else if (difficulty == 'medium') {
    print('I\'m thinking of a number between 1 and 100...\n');
  } else {
    print('I\'m thinking of a number between 1 and 500...\n');
  }
}

void main() {
  var difficulty = 'easy';  // Try: 'easy', 'medium', 'hard'
  var secretNumber = generateSecretNumber(difficulty);

  printHeader(difficulty);

  // Rest of game logic...
}
```

---

## Full Game with All Features

Here's the complete, polished version:

```dart
import 'dart:math';

// ========== GAME CONFIGURATION ==========

class GameConfig {
  static const Map<String, int> ranges = {
    'easy': 50,
    'medium': 100,
    'hard': 500,
  };

  static const Map<String, int> maxAttempts = {
    'easy': 10,
    'medium': 7,
    'hard': 12,
  };
}

// ========== GAME FUNCTIONS ==========

int generateSecretNumber(String difficulty) {
  var random = Random();
  var range = GameConfig.ranges[difficulty] ?? 100;
  return random.nextInt(range) + 1;
}

void printHeader(String difficulty) {
  print('\n' + '=' * 40);
  print('   NUMBER GUESSING GAME');
  print('=' * 40);
  print('Difficulty: ${difficulty.toUpperCase()}');
  var range = GameConfig.ranges[difficulty] ?? 100;
  print('Guess a number between 1 and $range');
  var maxAttempts = GameConfig.maxAttempts[difficulty] ?? 7;
  print('You have $maxAttempts attempts. Good luck!\n');
}

String checkGuess(int guess, int secret) {
  if (guess == secret) return 'correct';
  if (guess > secret) return 'high';
  return 'low';
}

void printAttempt(int attemptNum, int guess, String result) {
  print('--- Attempt $attemptNum ---');
  print('You guessed: $guess');

  if (result == 'correct') {
    print('🎉 CORRECT! You found it!');
  } else if (result == 'high') {
    var diff = guess - (guess * 0.1).toInt();  // Give a hint
    print('📉 Too high! Try something lower...');
  } else {
    print('📈 Too low! Try something higher...');
  }
  print('');
}

void printWinSummary(int attempts, List<int> history, String difficulty) {
  print('=' * 40);
  print('   🎊 VICTORY! 🎊');
  print('=' * 40);
  print('Difficulty: ${difficulty.toUpperCase()}');
  print('Attempts used: $attempts');
  print('Your guessing strategy: $history');

  if (attempts <= 3) {
    print('Rating: ⭐⭐⭐ Amazing! Lucky or skilled?');
  } else if (attempts <= 5) {
    print('Rating: ⭐⭐ Great job!');
  } else {
    print('Rating: ⭐ You made it!');
  }
  print('=' * 40 + '\n');
}

void printLossSummary(int secret, List<int> history) {
  print('=' * 40);
  print('   😢 GAME OVER');
  print('=' * 40);
  print('The number was: $secret');
  print('Your guesses: $history');
  print('Better luck next time!');
  print('=' * 40 + '\n');
}

// ========== MAIN GAME LOGIC ==========

void playGame(String difficulty) {
  var secretNumber = generateSecretNumber(difficulty);
  var maxAttempts = GameConfig.maxAttempts[difficulty] ?? 7;
  var attemptCount = 0;
  List<int> guessHistory = [];

  printHeader(difficulty);

  // Simulate guesses (in real game, this would be user input)
  var simulatedGuesses = [50, 25, 37, 31, 28, 29, 30];

  for (var guess in simulatedGuesses) {
    if (attemptCount >= maxAttempts) {
      printLossSummary(secretNumber, guessHistory);
      return;
    }

    attemptCount++;
    guessHistory.add(guess);

    var result = checkGuess(guess, secretNumber);
    printAttempt(attemptCount, guess, result);

    if (result == 'correct') {
      printWinSummary(attemptCount, guessHistory, difficulty);
      return;
    }
  }

  // If loop ends without finding number
  printLossSummary(secretNumber, guessHistory);
}

void main() {
  print('\n🎮 Welcome to the Number Guessing Game! 🎮\n');

  // Play different difficulties
  playGame('easy');
  playGame('medium');
  playGame('hard');

  print('Thanks for playing! 👋');
}
```

---

## ✅ YOUR CHALLENGE: Enhance the Game

Now it's your turn! Take the code above and add these features:

### Challenge 1: Best Guess Tracking

Add a variable to track the closest guess:

```dart
var closestGuess = 0;
var closestDistance = 999;

// In the loop, check if current guess is closer than previous best
```

### Challenge 2: Hint System

After 3 failed attempts, give a hint:

```dart
if (attemptCount == 3 && result != 'correct') {
  if (secretNumber % 2 == 0) {
    print('💡 HINT: The number is even!');
  } else {
    print('💡 HINT: The number is odd!');
  }
}
```

### Challenge 3: Statistics Tracker

Create a Map to track statistics across multiple games:

```dart
Map<String, dynamic> stats = {
  'gamesPlayed': 0,
  'gamesWon': 0,
  'totalAttempts': 0,
  'bestScore': 999,
};
```

Update these stats after each game and print a summary at the end.

### Challenge 4: Range Narrowing

After each guess, show the narrowed range:

```
You guessed: 50
Too high! The number is between 1 and 49
```

**Success Condition**: You've added at least 2 of these enhancements. ✅

---

## What You've Accomplished

Look at what you just built:
- ✅ A complete, working game
- ✅ Multiple functions for organization
- ✅ Variables tracking state
- ✅ Conditionals for game logic
- ✅ Loops for gameplay
- ✅ Lists storing history
- ✅ Maps for configuration

**You're not a beginner anymore!** You can write real programs!

---

## What Did We Learn?

Let's recap this module:
- ✅ How to structure a complete program
- ✅ Breaking problems into functions
- ✅ Combining all Dart fundamentals
- ✅ Simulating game logic
- ✅ Organizing code for readability
- ✅ Using constants and configuration
- ✅ Providing good user feedback

---

## What's Next?

**Module 1 Complete!** 🎉

You now have a solid foundation in Dart programming. You can:
- Store and manipulate data
- Make decisions
- Create loops
- Write functions
- Manage collections
- Build complete programs

In **Module 2**, we'll take these skills and start building **actual Flutter apps** with visual interfaces!

You'll learn:
- How Flutter apps are structured
- What widgets are and how to use them
- How to display text, images, and buttons
- How to arrange elements on screen

Get ready to see your code come to life on screen! 🚀
