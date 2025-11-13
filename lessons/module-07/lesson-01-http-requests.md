# Module 7, Lesson 1: HTTP Requests and APIs

## What is an API?

Imagine you're at a restaurant:
- **You (App)**: Want food
- **Kitchen (Server)**: Has food
- **Waiter (API)**: Takes your order to kitchen, brings food back

**API (Application Programming Interface)** is the waiter - it takes your app's requests to a server and brings back data!

**Real-world examples:**
- Weather app → Weather API → Gets current temperature
- Instagram → Instagram API → Gets your feed
- Google Maps → Maps API → Gets directions

---

## HTTP Methods (Restaurant Menu Actions)

Think of HTTP methods like different ways to interact with a menu:

| Method | Restaurant Analogy | What it Does |
|--------|-------------------|--------------|
| **GET** | Read the menu | Get/read data |
| **POST** | Place new order | Create new data |
| **PUT** | Change entire order | Update/replace data |
| **DELETE** | Cancel order | Delete data |

---

## Installation

```yaml
# pubspec.yaml
dependencies:
  flutter:
    sdk: flutter
  http: ^1.6.0
```

Run: `flutter pub get`

---

## Your First GET Request

```dart
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

void main() => runApp(MaterialApp(home: SimpleGetExample()));

class SimpleGetExample extends StatefulWidget {
  @override
  _SimpleGetExampleState createState() => _SimpleGetExampleState();
}

class _SimpleGetExampleState extends State<SimpleGetExample> {
  String result = 'No data yet';

  Future<void> fetchData() async {
    final response = await http.get(
      Uri.parse('https://jsonplaceholder.typicode.com/posts/1'),
    );

    if (response.statusCode == 200) {
      setState(() {
        result = response.body;
      });
    } else {
      setState(() {
        result = 'Error: ${response.statusCode}';
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('GET Request')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            ElevatedButton(
              onPressed: fetchData,
              child: Text('Fetch Data'),
            ),
            SizedBox(height: 20),
            Expanded(
              child: SingleChildScrollView(
                padding: EdgeInsets.all(16),
                child: Text(result),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
```

**Try it!** Tap "Fetch Data" → See JSON data appear!

---

## Understanding the Code

### 1. Import http package
```dart
import 'package:http/http.dart' as http;
```

### 2. Create URI
```dart
Uri.parse('https://jsonplaceholder.typicode.com/posts/1')
```

### 3. Make async request
```dart
final response = await http.get(uri);
```

**await** = "Wait for this to finish before continuing"

### 4. Check status code
```dart
if (response.statusCode == 200) {  // 200 = Success!
  // Use response.body
}
```

**Status codes:**
- 200: Success
- 404: Not found
- 500: Server error

---

## HTTP Status Codes (The Restaurant Story)

| Code | Restaurant Analogy | Meaning |
|------|-------------------|----------|
| **200** | Order successful! | Request succeeded |
| **201** | Order created! | Resource created |
| **400** | Invalid order | Bad request |
| **401** | Not allowed to order | Unauthorized |
| **404** | Dish not on menu | Not found |
| **500** | Kitchen on fire! | Server error |

---

## Complete Example with JSON Parsing

```dart
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';  // For jsonDecode

void main() => runApp(MaterialApp(home: PostListScreen()));

class Post {
  final int id;
  final String title;
  final String body;

  Post({required this.id, required this.title, required this.body});

  factory Post.fromJson(Map<String, dynamic> json) {
    return Post(
      id: json['id'],
      title: json['title'],
      body: json['body'],
    );
  }
}

class PostListScreen extends StatefulWidget {
  @override
  _PostListScreenState createState() => _PostListScreenState();
}

class _PostListScreenState extends State<PostListScreen> {
  List<Post> posts = [];
  bool isLoading = false;
  String? error;

  @override
  void initState() {
    super.initState();
    fetchPosts();
  }

  Future<void> fetchPosts() async {
    setState(() {
      isLoading = true;
      error = null;
    });

    try {
      final response = await http.get(
        Uri.parse('https://jsonplaceholder.typicode.com/posts'),
      );

      if (response.statusCode == 200) {
        final List<dynamic> jsonData = jsonDecode(response.body);
        setState(() {
          posts = jsonData.map((json) => Post.fromJson(json)).toList();
          isLoading = false;
        });
      } else {
        setState(() {
          error = 'Error: ${response.statusCode}';
          isLoading = false;
        });
      }
    } catch (e) {
      setState(() {
        error = 'Failed to load posts: $e';
        isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Posts')),
      body: _buildBody(),
      floatingActionButton: FloatingActionButton(
        onPressed: fetchPosts,
        child: Icon(Icons.refresh),
      ),
    );
  }

  Widget _buildBody() {
    if (isLoading) {
      return Center(child: CircularProgressIndicator());
    }

    if (error != null) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.error_outline, size: 60, color: Colors.red),
            SizedBox(height: 16),
            Text(error!, style: TextStyle(color: Colors.red)),
            SizedBox(height: 16),
            ElevatedButton(
              onPressed: fetchPosts,
              child: Text('Retry'),
            ),
          ],
        ),
      );
    }

    if (posts.isEmpty) {
      return Center(child: Text('No posts found'));
    }

    return RefreshIndicator(
      onRefresh: fetchPosts,
      child: ListView.builder(
        itemCount: posts.length,
        itemBuilder: (context, index) {
          final post = posts[index];
          return Card(
            margin: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            child: ListTile(
              leading: CircleAvatar(child: Text('${post.id}')),
              title: Text(
                post.title,
                style: TextStyle(fontWeight: FontWeight.bold),
              ),
              subtitle: Text(
                post.body,
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
              ),
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => PostDetailScreen(post: post),
                  ),
                );
              },
            ),
          );
        },
      ),
    );
  }
}

class PostDetailScreen extends StatelessWidget {
  final Post post;

  PostDetailScreen({required this.post});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Post ${post.id}')),
      body: Padding(
        padding: EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              post.title,
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
            ),
            SizedBox(height: 16),
            Text(post.body, style: TextStyle(fontSize: 16)),
          ],
        ),
      ),
    );
  }
}
```

---

## POST Request (Creating Data)

```dart
Future<void> createPost() async {
  final response = await http.post(
    Uri.parse('https://jsonplaceholder.typicode.com/posts'),
    headers: {
      'Content-Type': 'application/json; charset=UTF-8',
    },
    body: jsonEncode({
      'title': 'My New Post',
      'body': 'This is the content of my post',
      'userId': 1,
    }),
  );

  if (response.statusCode == 201) {  // 201 = Created!
    print('Post created successfully!');
    final newPost = Post.fromJson(jsonDecode(response.body));
    print('New post ID: ${newPost.id}');
  } else {
    print('Failed to create post');
  }
}
```

**Key differences from GET:**
- Use `http.post()` instead of `http.get()`
- Add `headers` to specify JSON content
- Add `body` with data to send
- Expect status code 201 (Created)

---

## PUT Request (Updating Data)

```dart
Future<void> updatePost(int postId) async {
  final response = await http.put(
    Uri.parse('https://jsonplaceholder.typicode.com/posts/$postId'),
    headers: {
      'Content-Type': 'application/json; charset=UTF-8',
    },
    body: jsonEncode({
      'id': postId,
      'title': 'Updated Title',
      'body': 'Updated content',
      'userId': 1,
    }),
  );

  if (response.statusCode == 200) {
    print('Post updated successfully!');
  } else {
    print('Failed to update post');
  }
}
```

---

## DELETE Request (Removing Data)

```dart
Future<void> deletePost(int postId) async {
  final response = await http.delete(
    Uri.parse('https://jsonplaceholder.typicode.com/posts/$postId'),
  );

  if (response.statusCode == 200) {
    print('Post deleted successfully!');
  } else {
    print('Failed to delete post');
  }
}
```

---

## Error Handling Best Practices

```dart
Future<List<Post>> fetchPosts() async {
  try {
    final response = await http.get(
      Uri.parse('https://jsonplaceholder.typicode.com/posts'),
    ).timeout(Duration(seconds: 10));  // Add timeout!

    if (response.statusCode == 200) {
      final List<dynamic> jsonData = jsonDecode(response.body);
      return jsonData.map((json) => Post.fromJson(json)).toList();
    } else {
      throw Exception('Failed to load posts: ${response.statusCode}');
    }
  } on http.ClientException catch (e) {
    throw Exception('Network error: $e');
  } on TimeoutException catch (e) {
    throw Exception('Request timeout: $e');
  } catch (e) {
    throw Exception('Unexpected error: $e');
  }
}
```

**Always handle:**
- Network errors (no connection)
- Timeout errors (too slow)
- Server errors (500)
- Parse errors (invalid JSON)

---

## Using http.Client (Persistent Connection)

For multiple requests, use Client:

```dart
class ApiService {
  final http.Client client = http.Client();

  Future<List<Post>> getPosts() async {
    final response = await client.get(
      Uri.parse('https://jsonplaceholder.typicode.com/posts'),
    );

    if (response.statusCode == 200) {
      final List<dynamic> jsonData = jsonDecode(response.body);
      return jsonData.map((json) => Post.fromJson(json)).toList();
    } else {
      throw Exception('Failed to load posts');
    }
  }

  Future<Post> getPost(int id) async {
    final response = await client.get(
      Uri.parse('https://jsonplaceholder.typicode.com/posts/$id'),
    );

    if (response.statusCode == 200) {
      return Post.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Failed to load post');
    }
  }

  void dispose() {
    client.close();  // Important: Close when done!
  }
}
```

**Benefits:**
- Reuses connection (faster)
- Better performance for multiple requests
- Proper resource management

---

## Complete CRUD Example

```dart
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class CrudExample extends StatefulWidget {
  @override
  _CrudExampleState createState() => _CrudExampleState();
}

class _CrudExampleState extends State<CrudExample> {
  final baseUrl = 'https://jsonplaceholder.typicode.com/posts';
  List<Post> posts = [];
  bool isLoading = false;

  @override
  void initState() {
    super.initState();
    _fetchPosts();
  }

  // CREATE
  Future<void> _createPost() async {
    final response = await http.post(
      Uri.parse(baseUrl),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'title': 'New Post ${posts.length + 1}',
        'body': 'This is a new post',
        'userId': 1,
      }),
    );

    if (response.statusCode == 201) {
      final newPost = Post.fromJson(jsonDecode(response.body));
      setState(() {
        posts.insert(0, newPost);
      });
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Post created!')),
      );
    }
  }

  // READ
  Future<void> _fetchPosts() async {
    setState(() => isLoading = true);

    final response = await http.get(Uri.parse(baseUrl));

    if (response.statusCode == 200) {
      final List<dynamic> jsonData = jsonDecode(response.body);
      setState(() {
        posts = jsonData.take(10).map((json) => Post.fromJson(json)).toList();
        isLoading = false;
      });
    }
  }

  // UPDATE
  Future<void> _updatePost(Post post) async {
    final response = await http.put(
      Uri.parse('$baseUrl/${post.id}'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'id': post.id,
        'title': '${post.title} (Updated)',
        'body': post.body,
        'userId': 1,
      }),
    );

    if (response.statusCode == 200) {
      final updatedPost = Post.fromJson(jsonDecode(response.body));
      setState(() {
        final index = posts.indexWhere((p) => p.id == post.id);
        if (index != -1) {
          posts[index] = updatedPost;
        }
      });
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Post updated!')),
      );
    }
  }

  // DELETE
  Future<void> _deletePost(Post post) async {
    final response = await http.delete(
      Uri.parse('$baseUrl/${post.id}'),
    );

    if (response.statusCode == 200) {
      setState(() {
        posts.removeWhere((p) => p.id == post.id);
      });
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Post deleted!')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('CRUD Operations')),
      body: isLoading
          ? Center(child: CircularProgressIndicator())
          : ListView.builder(
              itemCount: posts.length,
              itemBuilder: (context, index) {
                final post = posts[index];
                return Card(
                  margin: EdgeInsets.all(8),
                  child: ListTile(
                    title: Text(post.title),
                    subtitle: Text(post.body, maxLines: 2),
                    trailing: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        IconButton(
                          icon: Icon(Icons.edit, color: Colors.blue),
                          onPressed: () => _updatePost(post),
                        ),
                        IconButton(
                          icon: Icon(Icons.delete, color: Colors.red),
                          onPressed: () => _deletePost(post),
                        ),
                      ],
                    ),
                  ),
                );
              },
            ),
      floatingActionButton: FloatingActionButton(
        onPressed: _createPost,
        child: Icon(Icons.add),
      ),
    );
  }
}
```

---

## ✅ YOUR CHALLENGES

### Challenge 1: User List
Fetch users from: `https://jsonplaceholder.typicode.com/users`
Display in a list with name, email, and phone.

### Challenge 2: Photo Gallery
Fetch photos from: `https://jsonplaceholder.typicode.com/photos?_limit=20`
Display in a grid with images and titles.

### Challenge 3: Todo App
Create a todo app using: `https://jsonplaceholder.typicode.com/todos`
Implement GET, POST, PUT, DELETE operations.

### Challenge 4: Comments System
Fetch and display comments for posts using:
`https://jsonplaceholder.typicode.com/posts/1/comments`

**Success Condition**: Working CRUD operations with proper error handling! ✅

---

## Common Mistakes

❌ **Mistake 1**: Forgetting async/await
```dart
void fetchData() {  // Missing async!
  final response = await http.get(...);  // Error!
}
```

✅ **Fix**:
```dart
Future<void> fetchData() async {  // Add async!
  final response = await http.get(...);
}
```

❌ **Mistake 2**: Not checking status code
```dart
final response = await http.get(uri);
final data = jsonDecode(response.body);  // Might fail!
```

✅ **Fix**:
```dart
final response = await http.get(uri);
if (response.statusCode == 200) {
  final data = jsonDecode(response.body);
}
```

❌ **Mistake 3**: No error handling
```dart
Future<void> fetchData() async {
  final response = await http.get(uri);  // What if network fails?
}
```

✅ **Fix**:
```dart
Future<void> fetchData() async {
  try {
    final response = await http.get(uri);
    // Handle response
  } catch (e) {
    print('Error: $e');
  }
}
```

---

## What Did We Learn?

- ✅ What APIs are and why they're important
- ✅ HTTP methods: GET, POST, PUT, DELETE
- ✅ http package (1.6.0) for making requests
- ✅ Status codes and their meanings
- ✅ async/await for asynchronous operations
- ✅ JSON parsing with jsonDecode()
- ✅ Error handling and timeouts
- ✅ http.Client for persistent connections
- ✅ Complete CRUD operations

---

## Lesson Checkpoint

### Quiz

**Question 1**: What does the HTTP GET method do?
A) Create new data
B) Retrieve/read data from a server
C) Update existing data
D) Delete data

**Question 2**: What status code indicates a successful request?
A) 404
B) 500
C) 200
D) 401

**Question 3**: Why do we need async/await with HTTP requests?
A) To make the app faster
B) To wait for the server response without blocking the UI
C) To save memory
D) To prevent crashes

---

## Why This Matters

**Connecting to the internet transforms your app:**

**Real Data**: Instead of fake placeholder data, your app displays real, up-to-date information from servers. A weather app without an API is just a static interface.

**Dynamic Content**: Social media feeds, news articles, product catalogs - all come from APIs. Without HTTP requests, these apps couldn't function.

**User Interaction**: POST/PUT/DELETE let users create content, update profiles, and interact with your app. Read-only apps are boring - users want to contribute!

**Collaboration**: Multiple users can share data through a common server. Think multiplayer games, chat apps, collaborative documents.

**Separation of Concerns**: Your app focuses on UI, the server handles data storage and business logic. This makes apps more maintainable and scalable.

**Real-world impact**: When Instagram added API support for third-party apps, engagement increased 200% because users could post from anywhere. APIs unlock your app's potential!

**Career Essential**: Every professional app connects to APIs. This isn't optional - it's fundamental to modern app development.

---

## Answer Key
1. **B** - HTTP GET retrieves/reads data from a server without modifying it
2. **C** - Status code 200 indicates a successful HTTP request
3. **B** - async/await allows waiting for server responses without blocking the UI thread, keeping the app responsive

---

**Next up is: Module 7, Lesson 2: JSON Parsing and Serialization**