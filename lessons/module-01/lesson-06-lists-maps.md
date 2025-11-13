# Module 1, Lesson 6: Organizing Collections (Lists and Maps)

## The Shopping List Analogy

Imagine you're going grocery shopping. You could create separate variables:

```dart
var item1 = 'Milk';
var item2 = 'Bread';
var item3 = 'Eggs';
var item4 = 'Butter';
```

But that's clunky! What if you have 20 items? Or 100?

Instead, you'd write a **list**:
```
Shopping List:
1. Milk
2. Bread
3. Eggs
4. Butter
```

**Lists in programming work the same way** - they store multiple related items in one place.

---

## What is a List?

**Conceptual Explanation**:
A List is like a numbered container with multiple compartments, each holding one item.

```
List of fruits:
[0] Apple
[1] Banana
[2] Orange
[3] Mango
```

**Note**: Lists start counting at **0**, not 1! This is called "zero-indexing."

---

## Creating Your First List

```dart
void main() {
  var fruits = ['Apple', 'Banana', 'Orange'];

  print(fruits);  // Output: [Apple, Banana, Orange]
}
```

**The Parts**:
- **`var fruits`** - Variable name for the list
- **`[]`** - Square brackets indicate a list
- **`'Apple', 'Banana', 'Orange'`** - Items separated by commas

---

## Accessing List Items

Use the index (position number) to access items:

```dart
void main() {
  var fruits = ['Apple', 'Banana', 'Orange'];

  print(fruits[0]);  // Output: Apple (first item)
  print(fruits[1]);  // Output: Banana (second item)
  print(fruits[2]);  // Output: Orange (third item)
}
```

**Remember**: The first item is at index 0!

```
Index:  0        1         2
List:  [Apple | Banana | Orange]
```

---

## List Length

How many items are in a list?

```dart
void main() {
  var fruits = ['Apple', 'Banana', 'Orange'];

  print('Number of fruits: ${fruits.length}');  // Output: 3
}
```

**Useful pattern**: The last item is always at index `length - 1`:

```dart
void main() {
  var fruits = ['Apple', 'Banana', 'Orange'];

  var lastIndex = fruits.length - 1;
  print('Last fruit: ${fruits[lastIndex]}');  // Output: Orange
}
```

---

## Adding Items to a List

```dart
void main() {
  var fruits = ['Apple', 'Banana'];

  print(fruits);  // Output: [Apple, Banana]

  fruits.add('Orange');
  print(fruits);  // Output: [Apple, Banana, Orange]

  fruits.add('Mango');
  print(fruits);  // Output: [Apple, Banana, Orange, Mango]
}
```

**`.add()`** adds an item to the **end** of the list.

---

## Removing Items

```dart
void main() {
  var fruits = ['Apple', 'Banana', 'Orange'];

  fruits.remove('Banana');
  print(fruits);  // Output: [Apple, Orange]

  fruits.removeAt(0);  // Remove by index
  print(fruits);  // Output: [Orange]
}
```

**Two ways to remove**:
- **`remove('value')`** - Remove specific item
- **`removeAt(index)`** - Remove by position

---

## Changing List Items

```dart
void main() {
  var fruits = ['Apple', 'Banana', 'Orange'];

  print(fruits);  // Output: [Apple, Banana, Orange]

  fruits[1] = 'Grape';  // Change Banana to Grape
  print(fruits);  // Output: [Apple, Grape, Orange]
}
```

---

## Looping Through Lists

**This is super common!** You'll do this all the time in Flutter.

### Method 1: For-each Loop

```dart
void main() {
  var fruits = ['Apple', 'Banana', 'Orange'];

  for (var fruit in fruits) {
    print('I like $fruit');
  }
}
```

**Output**:
```
I like Apple
I like Banana
I like Orange
```

**Read as**: "For each fruit in fruits, print..."

### Method 2: Traditional For Loop

```dart
void main() {
  var fruits = ['Apple', 'Banana', 'Orange'];

  for (var i = 0; i < fruits.length; i++) {
    print('Fruit ${i + 1}: ${fruits[i]}');
  }
}
```

**Output**:
```
Fruit 1: Apple
Fruit 2: Banana
Fruit 3: Orange
```

---

## List Methods You'll Use Often

```dart
void main() {
  var numbers = [3, 1, 4, 1, 5, 9, 2, 6];

  // Check if list contains an item
  print(numbers.contains(5));  // Output: true

  // Find index of an item
  print(numbers.indexOf(4));   // Output: 2

  // Sort the list
  numbers.sort();
  print(numbers);  // Output: [1, 1, 2, 3, 4, 5, 6, 9]

  // Reverse the list
  var reversed = numbers.reversed.toList();
  print(reversed);  // Output: [9, 6, 5, 4, 3, 2, 1, 1]

  // Clear all items
  numbers.clear();
  print(numbers);  // Output: []
}
```

---

## Different Types of Lists

### List of Numbers

```dart
var scores = [85, 92, 78, 95, 88];
var prices = [19.99, 24.50, 15.75];
```

### List of Booleans

```dart
var answers = [true, false, true, true];
```

### Mixed Type List (not recommended)

```dart
var mixed = [1, 'hello', true, 3.14];  // Works, but confusing!
```

**Best Practice**: Keep lists to one type.

---

## Typed Lists (Recommended)

Be explicit about what type of items your list holds:

```dart
void main() {
  List<String> fruits = ['Apple', 'Banana'];
  List<int> numbers = [1, 2, 3];
  List<double> prices = [19.99, 24.50];

  // fruits.add(123);  // ❌ Error: can't add int to List<String>
  fruits.add('Orange');  // ✅ Works!
}
```

---

## Introduction to Maps

**Conceptual Explanation**:
Think of a dictionary - you look up a **word** (key) to find its **meaning** (value).

Maps work the same way: they store **key-value pairs**.

**Real-world example**: A phone book
- **Key**: Person's name
- **Value**: Phone number

```
"Alice" → "555-1234"
"Bob"   → "555-5678"
"Carol" → "555-9012"
```

---

## Creating Your First Map

```dart
void main() {
  var phoneBook = {
    'Alice': '555-1234',
    'Bob': '555-5678',
    'Carol': '555-9012'
  };

  print(phoneBook);
}
```

**The Parts**:
- **`{}`** - Curly braces indicate a Map
- **`'Alice':`** - The key
- **`'555-1234'`** - The value
- **`,`** - Separates pairs

---

## Accessing Map Values

Use the key to get the value:

```dart
void main() {
  var phoneBook = {
    'Alice': '555-1234',
    'Bob': '555-5678',
  };

  print(phoneBook['Alice']);  // Output: 555-1234
  print(phoneBook['Bob']);    // Output: 555-5678
}
```

---

## Adding and Changing Values

```dart
void main() {
  var ages = {
    'Alice': 25,
    'Bob': 30
  };

  // Add new entry
  ages['Carol'] = 28;
  print(ages);  // Output: {Alice: 25, Bob: 30, Carol: 28}

  // Change existing value
  ages['Alice'] = 26;
  print(ages);  // Output: {Alice: 26, Bob: 30, Carol: 28}
}
```

---

## Removing from Maps

```dart
void main() {
  var scores = {
    'Alice': 95,
    'Bob': 87,
    'Carol': 92
  };

  scores.remove('Bob');
  print(scores);  // Output: {Alice: 95, Carol: 92}
}
```

---

## Checking if Key Exists

```dart
void main() {
  var ages = {'Alice': 25, 'Bob': 30};

  print(ages.containsKey('Alice'));   // Output: true
  print(ages.containsKey('Charlie')); // Output: false

  print(ages.containsValue(25));      // Output: true
}
```

---

## Looping Through Maps

```dart
void main() {
  var scores = {
    'Alice': 95,
    'Bob': 87,
    'Carol': 92
  };

  // Loop through keys and values
  scores.forEach((name, score) {
    print('$name scored $score');
  });
}
```

**Output**:
```
Alice scored 95
Bob scored 87
Carol scored 92
```

**Or using entries**:

```dart
void main() {
  var scores = {'Alice': 95, 'Bob': 87};

  for (var entry in scores.entries) {
    print('${entry.key} scored ${entry.value}');
  }
}
```

---

## Typed Maps (Recommended)

```dart
void main() {
  Map<String, int> ages = {
    'Alice': 25,
    'Bob': 30
  };

  Map<String, double> prices = {
    'Apple': 1.99,
    'Banana': 0.59
  };
}
```

**`Map<KeyType, ValueType>`** specifies both types.

---

## Real-World Examples

### User Profile

```dart
void main() {
  var user = {
    'name': 'Alice Johnson',
    'email': 'alice@example.com',
    'age': 28,
    'city': 'New York'
  };

  print('Welcome, ${user['name']}!');
  print('Email: ${user['email']}');
}
```

### Product Inventory

```dart
void main() {
  var inventory = {
    'Laptop': 15,
    'Mouse': 50,
    'Keyboard': 30
  };

  print('Laptops in stock: ${inventory['Laptop']}');

  // Sell one laptop
  inventory['Laptop'] = inventory['Laptop']! - 1;
  print('Laptops now: ${inventory['Laptop']}');
}
```

### Shopping Cart

```dart
void main() {
  List<Map<String, dynamic>> cart = [
    {'name': 'Laptop', 'price': 999.99, 'quantity': 1},
    {'name': 'Mouse', 'price': 29.99, 'quantity': 2},
  ];

  var total = 0.0;
  for (var item in cart) {
    total += item['price'] * item['quantity'];
  }

  print('Total: \$$total');  // Output: Total: $1059.97
}
```

---

## ✅ YOUR CHALLENGE: Build a Contact Manager

**Goal**: Create a simple contact manager using Lists and Maps.

Create a file called `contacts.dart`:

```dart
void main() {
  // TODO: Create a list of contact maps
  List<Map<String, String>> contacts = [];

  // TODO: Add 3 contacts (each should have name, phone, email)

  // TODO: Print all contacts in a nice format

  // TODO: Find and print a specific contact by name

  // TODO: Remove one contact

  // TODO: Print remaining contacts
}
```

**Example output**:
```
=== All Contacts ===
Name: Alice
Phone: 555-1234
Email: alice@email.com

Name: Bob
Phone: 555-5678
Email: bob@email.com

=== Finding Alice ===
Found: Alice, 555-1234

=== After removing Bob ===
Remaining contacts: 1
```

**Hints**:
- Use a loop to print all contacts
- Use another loop to find a contact by name
- Use `removeAt()` or `remove()` to delete

**Success Condition**: Your contact manager can add, display, find, and remove contacts. ✅

---

## Bonus Challenge: Grade Calculator

Create a program that:

1. Stores student names and grades in a Map
2. Calculates the average grade
3. Finds the highest and lowest grades
4. Prints a report

```dart
void main() {
  Map<String, int> grades = {
    'Alice': 95,
    'Bob': 87,
    'Carol': 92,
    'David': 78,
    'Eve': 88
  };

  // Your code here to:
  // - Calculate average
  // - Find highest grade
  // - Find lowest grade
  // - Print report
}
```

---

## What Did We Learn?

Let's recap:
- ✅ Lists store multiple items in order
- ✅ Lists use zero-based indexing [0, 1, 2...]
- ✅ Use `add()`, `remove()`, `length` with Lists
- ✅ Maps store key-value pairs
- ✅ Use keys to access values: `map[key]`
- ✅ Loop through both Lists and Maps
- ✅ Type your collections: `List<String>`, `Map<String, int>`

---

## What's Next?

You've now learned all the **fundamental building blocks** of programming:
- ✅ Variables (storing data)
- ✅ Conditionals (making decisions)
- ✅ Loops (repeating actions)
- ✅ Functions (organizing code)
- ✅ Lists and Maps (managing collections)

In the next lessons, we'll do a **mini-project** to put it all together, and then we'll move into **Flutter** and start building actual user interfaces!

Get ready to build something cool! 🚀
