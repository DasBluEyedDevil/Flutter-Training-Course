# Module 1, Lesson 5: Reusable Instructions (Functions)

## The Recipe Analogy

Imagine you love making peanut butter sandwiches. Instead of remembering all the steps every time, you have a recipe card:

```
Recipe: Make PB&J Sandwich
1. Get two slices of bread
2. Spread peanut butter on one slice
3. Spread jelly on the other slice
4. Put the slices together
5. Cut in half
```

Now, whenever you want a sandwich, you just say "Make PB&J Sandwich" and follow the recipe!

**Functions are exactly like this** - they're named sets of instructions you can use over and over.

---

## Why Do We Need Functions?

Look at this repetitive code:

```dart
void main() {
  print('==========');
  print('Welcome!');
  print('==========');

  print('Processing...');

  print('==========');
  print('Done!');
  print('==========');
}
```

We're printing those equals signs multiple times. With a function:

```dart
void printBorder() {
  print('==========');
}

void main() {
  printBorder();
  print('Welcome!');
  printBorder();

  print('Processing...');

  printBorder();
  print('Done!');
  printBorder();
}
```

**Same output, cleaner code!**

---

## Creating Your First Function

**Conceptual Explanation**:
A function is like creating your own command. Once you define it, you can use it anywhere!

**The Pattern**:

```dart
void functionName() {
  // Code to run when function is called
}
```

**Real Example**:

```dart
void sayHello() {
  print('Hello!');
  print('Welcome to Flutter!');
  print('Have a great day!');
}

void main() {
  sayHello();  // Call the function
  sayHello();  // Call it again!
}
```

**Output**:
```
Hello!
Welcome to Flutter!
Have a great day!
Hello!
Welcome to Flutter!
Have a great day!
```

---

## Breaking Down a Function

```dart
void sayHello() {
  print('Hello!');
}
```

**The Parts**:

1. **`void`** - This means "doesn't give anything back" (we'll learn about returning values soon)
2. **`sayHello`** - The function name (use camelCase)
3. **`()`** - Parameters go here (empty for now)
4. **`{ }`** - The function body (code to run)

---

## Functions with Parameters - Making Them Flexible

What if you want to greet different people?

**Without parameters** (rigid):
```dart
void greetAlice() {
  print('Hello, Alice!');
}

void greetBob() {
  print('Hello, Bob!');
}
```

**With parameters** (flexible):
```dart
void greet(String name) {
  print('Hello, $name!');
}

void main() {
  greet('Alice');  // Output: Hello, Alice!
  greet('Bob');    // Output: Hello, Bob!
  greet('Charlie'); // Output: Hello, Charlie!
}
```

**Conceptual Explanation**:
Parameters are like **placeholders** or **blank spaces** in your recipe that you fill in when you use it.

---

## Multiple Parameters

You can have multiple parameters:

```dart
void introduce(String name, int age, String city) {
  print('Hi! My name is $name.');
  print('I am $age years old.');
  print('I live in $city.');
}

void main() {
  introduce('Sarah', 25, 'New York');
  introduce('Mike', 30, 'Los Angeles');
}
```

**Output**:
```
Hi! My name is Sarah.
I am 25 years old.
I live in New York.
Hi! My name is Mike.
I am 30 years old.
I live in Los Angeles.
```

**Order matters!** The values you pass must match the parameter order.

---

## Functions That Return Values

Sometimes you want a function to **give you back** a result.

**Conceptual Explanation**:
Think of a vending machine:
- You put in money and press a button (call the function)
- It **returns** a snack to you (the return value)

**The Pattern**:

```dart
ReturnType functionName(parameters) {
  // Do something
  return value;
}
```

**Real Example**:

```dart
int add(int a, int b) {
  return a + b;
}

void main() {
  var result = add(5, 3);
  print('5 + 3 = $result');  // Output: 5 + 3 = 8

  var another = add(10, 20);
  print('10 + 20 = $another'); // Output: 10 + 20 = 30
}
```

**Notice**:
- **`int`** instead of `void` - this function returns an integer
- **`return`** keyword sends the value back

---

## More Return Examples

### Calculate Area of Rectangle

```dart
double calculateArea(double width, double height) {
  return width * height;
}

void main() {
  var area = calculateArea(5.0, 3.0);
  print('Area: $area');  // Output: Area: 15.0
}
```

### Check if Adult

```dart
bool isAdult(int age) {
  return age >= 18;
}

void main() {
  print(isAdult(20));  // Output: true
  print(isAdult(15));  // Output: false

  if (isAdult(17)) {
    print('You can vote!');
  } else {
    print('Too young to vote.');
  }
}
```

### Get Greeting Based on Time

```dart
String getGreeting(int hour) {
  if (hour < 12) {
    return 'Good morning!';
  } else if (hour < 18) {
    return 'Good afternoon!';
  } else {
    return 'Good evening!';
  }
}

void main() {
  print(getGreeting(9));   // Output: Good morning!
  print(getGreeting(14));  // Output: Good afternoon!
  print(getGreeting(20));  // Output: Good evening!
}
```

---

## Optional Parameters

Sometimes you want parameters to be **optional**:

```dart
void greet(String name, [String greeting = 'Hello']) {
  print('$greeting, $name!');
}

void main() {
  greet('Alice');              // Output: Hello, Alice!
  greet('Bob', 'Hi');          // Output: Hi, Bob!
  greet('Charlie', 'Hey');     // Output: Hey, Charlie!
}
```

**Square brackets `[]`** make a parameter optional with a default value.

---

## Named Parameters

Named parameters make your code more readable:

```dart
void createUser({required String name, required int age, String country = 'USA'}) {
  print('Name: $name');
  print('Age: $age');
  print('Country: $country');
}

void main() {
  createUser(name: 'Alice', age: 25);
  createUser(name: 'Bob', age: 30, country: 'Canada');
  createUser(age: 28, name: 'Charlie');  // Order doesn't matter!
}
```

**Benefits**:
- **Clear**: You can see what each value is for
- **Flexible**: Order doesn't matter
- **`required`**: Makes sure important parameters aren't forgotten

---

## Arrow Functions (Shorthand)

For simple, one-line functions:

**Long way**:
```dart
int double(int x) {
  return x * 2;
}
```

**Short way** (arrow function):
```dart
int double(int x) => x * 2;
```

**More examples**:

```dart
String shout(String text) => text.toUpperCase();
bool isEven(int number) => number % 2 == 0;
int square(int x) => x * x;

void main() {
  print(shout('hello'));    // Output: HELLO
  print(isEven(4));         // Output: true
  print(square(5));         // Output: 25
}
```

---

## Real-World Examples

### Temperature Converter

```dart
double celsiusToFahrenheit(double celsius) {
  return (celsius * 9 / 5) + 32;
}

double fahrenheitToCelsius(double fahrenheit) {
  return (fahrenheit - 32) * 5 / 9;
}

void main() {
  print('25°C = ${celsiusToFahrenheit(25)}°F');
  print('77°F = ${fahrenheitToCelsius(77)}°C');
}
```

### Discount Calculator

```dart
double applyDiscount(double price, double discountPercent) {
  var discount = price * (discountPercent / 100);
  return price - discount;
}

void main() {
  var originalPrice = 100.0;
  var salePrice = applyDiscount(originalPrice, 20);

  print('Original: \$$originalPrice');
  print('After 20% discount: \$$salePrice');
  // Output: After 20% discount: $80.0
}
```

### Password Validator

```dart
bool isPasswordStrong(String password) {
  if (password.length < 8) {
    return false;
  }
  if (!password.contains(RegExp(r'[0-9]'))) {
    return false;  // Must have a number
  }
  return true;
}

void main() {
  print(isPasswordStrong('weak'));          // Output: false
  print(isPasswordStrong('strong123'));     // Output: true
}
```

---

## Function Scope - Variable Visibility

Variables inside a function can't be seen outside:

```dart
void myFunction() {
  var secret = 'Hidden!';
  print(secret);  // ✅ Works
}

void main() {
  myFunction();
  // print(secret);  // ❌ Error: secret is not defined
}
```

**Global vs Local**:

```dart
var globalVar = 'I am global';

void myFunction() {
  var localVar = 'I am local';
  print(globalVar);  // ✅ Can access global
  print(localVar);   // ✅ Can access local
}

void main() {
  print(globalVar);  // ✅ Can access global
  // print(localVar);  // ❌ Error: localVar only exists inside myFunction
}
```

---

## ✅ YOUR CHALLENGE: Build a Calculator

**Goal**: Create a calculator using functions.

Create a file called `calculator.dart`:

```dart
// TODO: Create these functions

int add(int a, int b) {
  // Your code here
}

int subtract(int a, int b) {
  // Your code here
}

int multiply(int a, int b) {
  // Your code here
}

double divide(int a, int b) {
  // Your code here
}

void main() {
  print('10 + 5 = ${add(10, 5)}');
  print('10 - 5 = ${subtract(10, 5)}');
  print('10 * 5 = ${multiply(10, 5)}');
  print('10 / 5 = ${divide(10, 5)}');
}
```

**Expected output**:
```
10 + 5 = 15
10 - 5 = 5
10 * 5 = 50
10 / 5 = 2.0
```

### Bonus Challenge 1: Make it Interactive

Add a function that takes an operation name:

```dart
double calculate(String operation, int a, int b) {
  // Use if/else to determine which operation
  // Return the result
}

void main() {
  print(calculate('add', 10, 5));       // 15
  print(calculate('multiply', 10, 5));  // 50
}
```

### Bonus Challenge 2: Add More Operations

Add these functions:
- `int power(int base, int exponent)` - Calculate base^exponent
- `double average(int a, int b, int c)` - Calculate average of 3 numbers
- `int max(int a, int b)` - Return the larger number

**Success Condition**: Your calculator works for all basic operations. ✅

---

## Common Beginner Mistakes

| Mistake | What Happens |
|---------|--------------|
| Forgetting `()` when calling | Function isn't called |
| Wrong number of arguments | Error: Expected X arguments |
| Wrong type of argument | Type error |
| Forgetting `return` | Function returns null |
| Returning from `void` function | Error: can't return value |
| Calling function before defining it | Error: function not found |

---

## What Did We Learn?

Let's recap:
- ✅ Functions organize code into reusable pieces
- ✅ Parameters make functions flexible
- ✅ `return` sends values back
- ✅ Return type must match what you return
- ✅ Named parameters improve readability
- ✅ Arrow functions are shorthand for simple functions
- ✅ Variables inside functions are local (scoped)

---

## What's Next?

Now we can:
- Store data (variables)
- Make decisions (if/else)
- Repeat actions (loops)
- Organize code (functions)

But what if we need to store **multiple related items**? Like a shopping list with many items?

In the next lesson, we'll learn about **Lists and Maps** - how to organize collections of data!

See you there! 🚀
