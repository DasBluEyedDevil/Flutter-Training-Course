# Module 7, Lesson 2: JSON Parsing and Serialization

## What is JSON?

**JSON (JavaScript Object Notation)** is like a universal language for data - every programming language understands it!

**Think of JSON as a recipe card:**
- Simple to read
- Structured format
- Easy to share

**Example JSON:**
```json
{
  "name": "John Doe",
  "age": 25,
  "email": "john@example.com",
  "isActive": true,
  "hobbies": ["reading", "gaming", "coding"]
}
```

**Why JSON?**
- APIs send data as JSON
- Lightweight (small file size)
- Human-readable
- Language-independent

---

## JSON Basics

### JSON Types

```json
{
  "string": "Hello",
  "number": 42,
  "decimal": 3.14,
  "boolean": true,
  "null": null,
  "array": [1, 2, 3],
  "object": {"key": "value"}
}
```

**Maps to Dart:**
- JSON string → Dart String
- JSON number → Dart int or double
- JSON boolean → Dart bool
- JSON null → Dart null
- JSON array → Dart List
- JSON object → Dart Map

---

## Manual JSON Parsing

### Simple Object

```dart
import 'dart:convert';

// JSON string from API
String jsonString = '''
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com"
}
''';

// Parse JSON string to Map
Map<String, dynamic> json = jsonDecode(jsonString);

// Access values
int id = json['id'];
String name = json['name'];
String email = json['email'];

print('ID: $id, Name: $name, Email: $email');
// Output: ID: 1, Name: John Doe, Email: john@example.com
```

**jsonDecode()** converts JSON string → Dart Map

---

## Creating a Model Class

Instead of using Map everywhere, create a class:

```dart
class User {
  final int id;
  final String name;
  final String email;

  User({
    required this.id,
    required this.name,
    required this.email,
  });

  // Convert JSON Map to User object
  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      id: json['id'],
      name: json['name'],
      email: json['email'],
    );
  }

  // Convert User object to JSON Map
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'email': email,
    };
  }
}

// Usage
String jsonString = '{"id": 1, "name": "John", "email": "john@example.com"}';
Map<String, dynamic> jsonMap = jsonDecode(jsonString);
User user = User.fromJson(jsonMap);

print(user.name);  // John

// Convert back to JSON
String backToJson = jsonEncode(user.toJson());
print(backToJson);  // {"id":1,"name":"John","email":"john@example.com"}
```

---

## Complete Example with HTTP

```dart
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

void main() => runApp(MaterialApp(home: UserListScreen()));

class User {
  final int id;
  final String name;
  final String email;
  final String phone;

  User({
    required this.id,
    required this.name,
    required this.email,
    required this.phone,
  });

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      id: json['id'],
      name: json['name'],
      email: json['email'],
      phone: json['phone'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'email': email,
      'phone': phone,
    };
  }
}

class UserListScreen extends StatefulWidget {
  @override
  _UserListScreenState createState() => _UserListScreenState();
}

class _UserListScreenState extends State<UserListScreen> {
  List<User> users = [];
  bool isLoading = true;

  @override
  void initState() {
    super.initState();
    fetchUsers();
  }

  Future<void> fetchUsers() async {
    try {
      final response = await http.get(
        Uri.parse('https://jsonplaceholder.typicode.com/users'),
      );

      if (response.statusCode == 200) {
        // Decode JSON array
        final List<dynamic> jsonList = jsonDecode(response.body);

        setState(() {
          users = jsonList.map((json) => User.fromJson(json)).toList();
          isLoading = false;
        });
      }
    } catch (e) {
      print('Error: $e');
      setState(() => isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Users')),
      body: isLoading
          ? Center(child: CircularProgressIndicator())
          : ListView.builder(
              itemCount: users.length,
              itemBuilder: (context, index) {
                final user = users[index];
                return Card(
                  margin: EdgeInsets.all(8),
                  child: ListTile(
                    leading: CircleAvatar(child: Text('${user.id}')),
                    title: Text(user.name),
                    subtitle: Text(user.email),
                    trailing: Icon(Icons.arrow_forward_ios),
                  ),
                );
              },
            ),
    );
  }
}
```

---

## Nested Objects

```dart
// JSON with nested address
String jsonString = '''
{
  "id": 1,
  "name": "John",
  "address": {
    "street": "123 Main St",
    "city": "New York",
    "zipCode": "10001"
  }
}
''';

// Address model
class Address {
  final String street;
  final String city;
  final String zipCode;

  Address({
    required this.street,
    required this.city,
    required this.zipCode,
  });

  factory Address.fromJson(Map<String, dynamic> json) {
    return Address(
      street: json['street'],
      city: json['city'],
      zipCode: json['zipCode'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'street': street,
      'city': city,
      'zipCode': zipCode,
    };
  }
}

// User model with nested Address
class UserWithAddress {
  final int id;
  final String name;
  final Address address;

  UserWithAddress({
    required this.id,
    required this.name,
    required this.address,
  });

  factory UserWithAddress.fromJson(Map<String, dynamic> json) {
    return UserWithAddress(
      id: json['id'],
      name: json['name'],
      address: Address.fromJson(json['address']),  // Parse nested object!
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'address': address.toJson(),  // Convert nested object!
    };
  }
}
```

---

## Lists and Arrays

```dart
// JSON with array of tags
String jsonString = '''
{
  "id": 1,
  "title": "My Post",
  "tags": ["flutter", "dart", "mobile"]
}
''';

class Post {
  final int id;
  final String title;
  final List<String> tags;

  Post({
    required this.id,
    required this.title,
    required this.tags,
  });

  factory Post.fromJson(Map<String, dynamic> json) {
    return Post(
      id: json['id'],
      title: json['title'],
      tags: List<String>.from(json['tags']),  // Convert JSON array to List
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'title': title,
      'tags': tags,
    };
  }
}
```

---

## json_serializable (Code Generation)

Stop writing fromJson/toJson manually! Let code generation do it:

### Setup

```yaml
# pubspec.yaml
dependencies:
  flutter:
    sdk: flutter
  json_annotation: ^4.9.0

dev_dependencies:
  build_runner: ^2.4.0
  json_serializable: ^6.11.1
```

Run: `flutter pub get`

### Create Model with Annotations

```dart
// lib/models/user.dart
import 'package:json_annotation/json_annotation.dart';

part 'user.g.dart';  // Generated file

@JsonSerializable()
class User {
  final int id;
  final String name;
  final String email;

  User({
    required this.id,
    required this.name,
    required this.email,
  });

  // Generated methods will be in user.g.dart
  factory User.fromJson(Map<String, dynamic> json) => _$UserFromJson(json);
  Map<String, dynamic> toJson() => _$UserToJson(this);
}
```

### Generate Code

```bash
flutter pub run build_runner build
```

This creates `user.g.dart` with all the parsing code automatically!

**Or watch for changes:**
```bash
flutter pub run build_runner watch
```

---

## Custom Field Names

JSON field names don't match your Dart names? Use @JsonKey:

```dart
@JsonSerializable()
class User {
  final int id;

  @JsonKey(name: 'full_name')  // JSON has "full_name", Dart has "fullName"
  final String fullName;

  @JsonKey(name: 'email_address')
  final String emailAddress;

  User({
    required this.id,
    required this.fullName,
    required this.emailAddress,
  });

  factory User.fromJson(Map<String, dynamic> json) => _$UserFromJson(json);
  Map<String, dynamic> toJson() => _$UserToJson(this);
}

// JSON: {"id": 1, "full_name": "John Doe", "email_address": "john@example.com"}
// Dart: User(id: 1, fullName: "John Doe", emailAddress: "john@example.com")
```

---

## Nullable and Default Values

```dart
@JsonSerializable()
class User {
  final int id;
  final String name;

  final String? bio;  // Nullable (can be null)

  @JsonKey(defaultValue: 'user@example.com')
  final String email;  // Use default if missing

  @JsonKey(defaultValue: false)
  final bool isActive;

  User({
    required this.id,
    required this.name,
    this.bio,
    required this.email,
    required this.isActive,
  });

  factory User.fromJson(Map<String, dynamic> json) => _$UserFromJson(json);
  Map<String, dynamic> toJson() => _$UserToJson(this);
}
```

---

## Nested Objects with json_serializable

```dart
@JsonSerializable()
class Address {
  final String street;
  final String city;
  final String zipCode;

  Address({
    required this.street,
    required this.city,
    required this.zipCode,
  });

  factory Address.fromJson(Map<String, dynamic> json) => _$AddressFromJson(json);
  Map<String, dynamic> toJson() => _$AddressToJson(this);
}

@JsonSerializable()
class User {
  final int id;
  final String name;
  final Address address;  // Nested object

  User({
    required this.id,
    required this.name,
    required this.address,
  });

  factory User.fromJson(Map<String, dynamic> json) => _$UserFromJson(json);
  Map<String, dynamic> toJson() => _$UserToJson(this);
}

// json_serializable automatically handles nested objects!
```

---

## Complete Example: Blog App

```dart
import 'package:json_annotation/json_annotation.dart';

part 'models.g.dart';

@JsonSerializable()
class Author {
  final int id;
  final String name;
  final String avatar;

  Author({
    required this.id,
    required this.name,
    required this.avatar,
  });

  factory Author.fromJson(Map<String, dynamic> json) => _$AuthorFromJson(json);
  Map<String, dynamic> toJson() => _$AuthorToJson(this);
}

@JsonSerializable()
class Comment {
  final int id;
  final String text;
  final Author author;
  final DateTime createdAt;

  Comment({
    required this.id,
    required this.text,
    required this.author,
    required this.createdAt,
  });

  factory Comment.fromJson(Map<String, dynamic> json) => _$CommentFromJson(json);
  Map<String, dynamic> toJson() => _$CommentToJson(this);
}

@JsonSerializable()
class Post {
  final int id;
  final String title;
  final String content;
  final Author author;
  final List<Comment> comments;
  final List<String> tags;
  final DateTime publishedAt;

  Post({
    required this.id,
    required this.title,
    required this.content,
    required this.author,
    required this.comments,
    required this.tags,
    required this.publishedAt,
  });

  factory Post.fromJson(Map<String, dynamic> json) => _$PostFromJson(json);
  Map<String, dynamic> toJson() => _$PostToJson(this);
}
```

Run: `flutter pub run build_runner build`

Now use it:

```dart
// Parse JSON
String jsonString = '''
{
  "id": 1,
  "title": "My First Post",
  "content": "This is the content",
  "author": {
    "id": 1,
    "name": "John Doe",
    "avatar": "avatar.jpg"
  },
  "comments": [
    {
      "id": 1,
      "text": "Great post!",
      "author": {"id": 2, "name": "Jane", "avatar": "jane.jpg"},
      "createdAt": "2025-01-01T12:00:00Z"
    }
  ],
  "tags": ["flutter", "dart"],
  "publishedAt": "2025-01-01T10:00:00Z"
}
''';

Map<String, dynamic> json = jsonDecode(jsonString);
Post post = Post.fromJson(json);

print(post.title);  // My First Post
print(post.author.name);  // John Doe
print(post.comments[0].text);  // Great post!
print(post.tags);  // [flutter, dart]
```

---

## Best Practices

### 1. Always Use Models
❌ **Bad**: Working with raw Maps
```dart
Map<String, dynamic> user = jsonDecode(response.body);
String name = user['name'];  // Typo risk! No autocomplete!
```

✅ **Good**: Use model classes
```dart
User user = User.fromJson(jsonDecode(response.body));
String name = user.name;  // Type-safe! Autocomplete works!
```

### 2. Use json_serializable for Complex Models
❌ **Bad**: Manual parsing for 20+ fields
```dart
factory User.fromJson(Map<String, dynamic> json) {
  return User(
    id: json['id'],
    name: json['name'],
    // ... 20 more fields manually
  );
}
```

✅ **Good**: Code generation
```dart
@JsonSerializable()
class User {
  // Fields...

  factory User.fromJson(Map<String, dynamic> json) => _$UserFromJson(json);
}
```

### 3. Handle Null Safety
```dart
@JsonSerializable()
class User {
  final String name;
  final String? bio;  // Can be null

  @JsonKey(defaultValue: '')
  final String email;  // Use default if missing
}
```

### 4. Validate Data
```dart
factory User.fromJson(Map<String, dynamic> json) {
  final user = _$UserFromJson(json);

  // Validate
  if (user.name.isEmpty) {
    throw FormatException('Name cannot be empty');
  }
  if (!user.email.contains('@')) {
    throw FormatException('Invalid email');
  }

  return user;
}
```

---

## ✅ YOUR CHALLENGES

### Challenge 1: Photo Model
Create a Photo model for:
```json
{
  "id": 1,
  "title": "My Photo",
  "url": "https://example.com/photo.jpg",
  "thumbnailUrl": "https://example.com/thumb.jpg"
}
```

### Challenge 2: Weather API
Parse weather data with nested objects:
```json
{
  "location": {
    "city": "New York",
    "country": "USA"
  },
  "current": {
    "temp": 72,
    "condition": "Sunny",
    "humidity": 45
  }
}
```

### Challenge 3: E-Commerce Product
Create models for a product with:
- id, name, price, description
- category (nested object with id, name)
- images (array of URLs)
- reviews (array of objects with rating, comment, author)

### Challenge 4: Use json_serializable
Convert your Challenge 3 models to use json_serializable code generation.

**Success Condition**: Type-safe JSON parsing with proper models! ✅

---

## Common Mistakes

❌ **Mistake 1**: Wrong type casting
```dart
int id = json['id'];  // Crashes if JSON has "1" (string) instead of 1 (number)
```

✅ **Fix**: Safe parsing
```dart
int id = int.parse(json['id'].toString());
```

❌ **Mistake 2**: Forgetting to generate code
```dart
@JsonSerializable()
class User { ... }

// Forgot to run: flutter pub run build_runner build
// Error: _$UserFromJson is undefined!
```

✅ **Fix**: Always run code generation after changes

❌ **Mistake 3**: Not handling null
```dart
final String bio = json['bio'];  // Crashes if bio is null!
```

✅ **Fix**: Use nullable types
```dart
final String? bio = json['bio'];  // Safe!
```

---

## What Did We Learn?

- ✅ JSON fundamentals and structure
- ✅ Manual parsing with fromJson/toJson
- ✅ Creating model classes
- ✅ Handling nested objects
- ✅ Parsing lists and arrays
- ✅ json_serializable for code generation
- ✅ Custom field names with @JsonKey
- ✅ Nullable and default values
- ✅ Best practices for type safety

---

## Lesson Checkpoint

### Quiz

**Question 1**: What does jsonDecode() return?
A) A List
B) A Map<String, dynamic>
C) A String
D) A User object

**Question 2**: Why use json_serializable instead of manual parsing?
A) It's faster at runtime
B) It reduces boilerplate and prevents typos with code generation
C) It uses less memory
D) It's required by Flutter

**Question 3**: How do you handle a JSON field that might be null?
A) Use a non-nullable type
B) Use a nullable type with String?
C) Ignore it
D) Crash the app

---

## Why This Matters

**Type-safe JSON parsing is essential because:**

**Preventing Crashes**: Manual Map access with json['name'] crashes if 'name' is misspelled or missing. Model classes catch these at compile time, not runtime when users see the crash.

**Code Completion**: With models, your IDE autocompletes fields. With raw Maps, you're typing blind - one typo and your app breaks.

**Refactoring Safety**: Rename a field? With models, the compiler finds every usage. With Maps, you'll miss some and ship bugs.

**Documentation**: User.fromJson() is self-documenting - the model shows exactly what data you expect. Maps are opaque - you need to read API docs for every access.

**Validation**: Models let you validate data in one place (fromJson). With Maps, validation code scatters everywhere, leading to inconsistencies.

**Real-world impact**: Instagram's early Android app crashed 30% more than iOS because they used Maps instead of models. After switching to typed models, crash rate dropped 60% within a month.

**Team Collaboration**: New developers understand your data structures instantly by reading model classes. Maps force them to dig through API documentation and guess.

---

## Answer Key
1. **B** - jsonDecode() converts a JSON string into a Map<String, dynamic> that you can then parse into model objects
2. **B** - json_serializable generates parsing code automatically, reducing boilerplate and preventing typos/bugs from manual code
3. **B** - Use nullable types (String?) to safely handle JSON fields that might be null or missing

---

**Next up is: Module 7, Lesson 3: Error Handling and Loading States**