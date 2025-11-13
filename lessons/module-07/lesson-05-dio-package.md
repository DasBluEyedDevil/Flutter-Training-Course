# Module 7, Lesson 5: Dio Package - Advanced HTTP Client

## What You'll Learn
By the end of this lesson, you'll understand how to use Dio - a powerful HTTP client that makes networking easier with built-in interceptors, automatic retries, and better error handling.

---

## Why This Matters

**Dio is the industry standard for professional Flutter apps.**

- **Used by 80%+ of production Flutter apps** that handle complex networking
- **Saves hours of development time** with built-in features (interceptors, retries, progress tracking)
- **Better error handling** out of the box
- **Automatic JSON parsing** with less boilerplate
- **Download/upload progress tracking** (critical for file transfers)

In this lesson, you'll learn the same HTTP client used by apps like Alibaba, Tencent, and thousands of other professional applications.

---

## Real-World Analogy: Personal Assistant vs Solo Worker

### Using the `http` Package (Solo Worker)
Imagine you run a small business and handle everything yourself:
- ✉️ You personally write every letter to suppliers
- 📞 You make every phone call
- 📝 You remember every detail yourself
- 🔄 If something fails, you have to manually retry
- 📊 You track everything on paper

**It works, but it's exhausting and error-prone.**

### Using Dio (Personal Assistant)
Now imagine hiring a smart personal assistant:
- ✉️ They automatically add your letterhead to every letter
- 📞 They retry calls if the line is busy
- 📝 They keep logs of all communications
- 🔄 They handle errors gracefully and report back
- 📊 They give you progress reports ("50% of the file uploaded...")

**Dio is your networking personal assistant.**

---

## http vs Dio: Side-by-Side Comparison

### Example: Fetch User Profile with Auth

#### With `http` Package ❌ (Manual Everything)

```dart
import 'package:http/http.dart' as http;
import 'dart:convert';

Future<Map<String, dynamic>> getProfile() async {
  // 1. Get token manually
  final token = await storage.read(key: 'auth_token');

  // 2. Build headers manually
  final headers = {
    'Authorization': 'Bearer $token',
    'Content-Type': 'application/json',
  };

  // 3. Make request with timeout
  final response = await http.get(
    Uri.parse('https://api.example.com/profile'),
    headers: headers,
  ).timeout(Duration(seconds: 10));

  // 4. Handle status codes manually
  if (response.statusCode == 200) {
    return jsonDecode(response.body);
  } else if (response.statusCode == 401) {
    throw Exception('Unauthorized');
  } else if (response.statusCode >= 500) {
    throw Exception('Server error');
  } else {
    throw Exception('Request failed');
  }
}
```

**Lines of code**: ~25 lines
**Problems**: Repetitive, error-prone, no retry logic, no logging

#### With Dio ✅ (Automatic Everything)

```dart
import 'package:dio/dio.dart';

Future<Map<String, dynamic>> getProfile() async {
  final dio = Dio();

  // That's it! Dio handles everything else automatically
  final response = await dio.get('https://api.example.com/profile');
  return response.data;
}
```

**Lines of code**: ~5 lines
**Benefits**: Auto JSON parsing, auto error handling, built-in timeout

---

## Setting Up Dio

### 1. Add Dio to Your Project

```yaml
# pubspec.yaml
dependencies:
  flutter:
    sdk: flutter
  dio: ^5.7.0
  flutter_secure_storage: ^9.2.4
```

Run:
```bash
flutter pub get
```

### 2. Basic Dio Instance

```dart
import 'package:dio/dio.dart';

final dio = Dio();

// That's it! You can now use dio.get(), dio.post(), etc.
```

---

## Making Requests with Dio

### GET Request

```dart
// Fetch posts
Future<List<dynamic>> getPosts() async {
  final response = await dio.get('https://jsonplaceholder.typicode.com/posts');
  return response.data; // Already parsed JSON!
}
```

### POST Request

```dart
// Create a new post
Future<Map<String, dynamic>> createPost({
  required String title,
  required String body,
}) async {
  final response = await dio.post(
    'https://jsonplaceholder.typicode.com/posts',
    data: {
      'title': title,
      'body': body,
      'userId': 1,
    },
  );
  return response.data;
}
```

### PUT Request

```dart
// Update a post
Future<Map<String, dynamic>> updatePost(int id, String newTitle) async {
  final response = await dio.put(
    'https://jsonplaceholder.typicode.com/posts/$id',
    data: {'title': newTitle},
  );
  return response.data;
}
```

### DELETE Request

```dart
// Delete a post
Future<void> deletePost(int id) async {
  await dio.delete('https://jsonplaceholder.typicode.com/posts/$id');
}
```

**Notice**: No `jsonEncode()` or `jsonDecode()` needed! Dio handles it automatically.

---

## Configuring Dio: Base Options

Instead of repeating the base URL everywhere, configure Dio once:

```dart
final dio = Dio(
  BaseOptions(
    baseUrl: 'https://api.example.com',
    connectTimeout: Duration(seconds: 10),
    receiveTimeout: Duration(seconds: 10),
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
  ),
);

// Now you can use relative paths
final response = await dio.get('/posts'); // https://api.example.com/posts
final user = await dio.get('/users/1');   // https://api.example.com/users/1
```

---

## Interceptors: The Magic of Dio 🪄

**Interceptors** are like **security checkpoints** at an airport:
- **Before you fly (Request Interceptor)**: Check your passport, add boarding pass
- **After you land (Response Interceptor)**: Stamp your passport, log arrival
- **If something goes wrong (Error Interceptor)**: Handle delays, rebook flights

### Request Interceptor (Add Auth Token Automatically)

```dart
class AuthInterceptor extends Interceptor {
  final FlutterSecureStorage storage;

  AuthInterceptor(this.storage);

  @override
  void onRequest(
    RequestOptions options,
    RequestInterceptorHandler handler,
  ) async {
    // Automatically add auth token to every request
    final token = await storage.read(key: 'auth_token');

    if (token != null) {
      options.headers['Authorization'] = 'Bearer $token';
      print('✅ Token added to ${options.path}');
    }

    handler.next(options); // Continue with the request
  }
}

// Add interceptor to Dio
dio.interceptors.add(AuthInterceptor(storage));

// Now ALL requests automatically include the auth token!
await dio.get('/profile'); // Token added automatically ✨
await dio.get('/posts');   // Token added automatically ✨
```

### Response Interceptor (Log All Responses)

```dart
class LoggingInterceptor extends Interceptor {
  @override
  void onResponse(Response response, ResponseInterceptorHandler handler) {
    print('✅ Response from ${response.requestOptions.path}');
    print('   Status: ${response.statusCode}');
    print('   Data: ${response.data}');

    handler.next(response); // Continue with the response
  }
}

dio.interceptors.add(LoggingInterceptor());
```

### Error Interceptor (Handle Errors Globally)

```dart
class ErrorInterceptor extends Interceptor {
  @override
  void onError(DioException err, ErrorInterceptorHandler handler) {
    String message;

    switch (err.type) {
      case DioExceptionType.connectionTimeout:
      case DioExceptionType.sendTimeout:
      case DioExceptionType.receiveTimeout:
        message = 'Connection timeout. Check your internet.';
        break;
      case DioExceptionType.badResponse:
        message = 'Server error: ${err.response?.statusCode}';
        break;
      case DioExceptionType.cancel:
        message = 'Request cancelled';
        break;
      default:
        message = 'Network error. Please try again.';
    }

    print('❌ Error: $message');
    handler.next(err); // Pass error to the caller
  }
}

dio.interceptors.add(ErrorInterceptor());
```

---

## Complete Dio Service with Interceptors

Here's a production-ready Dio service:

```dart
// lib/services/dio_service.dart
import 'package:dio/dio.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class DioService {
  late final Dio _dio;
  final storage = const FlutterSecureStorage();

  DioService() {
    _dio = Dio(
      BaseOptions(
        baseUrl: 'https://jsonplaceholder.typicode.com',
        connectTimeout: const Duration(seconds: 10),
        receiveTimeout: const Duration(seconds: 10),
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
      ),
    );

    // Add interceptors
    _dio.interceptors.add(_AuthInterceptor(storage));
    _dio.interceptors.add(_LoggingInterceptor());
    _dio.interceptors.add(_ErrorInterceptor());
  }

  Dio get dio => _dio;
}

// Auth Interceptor
class _AuthInterceptor extends Interceptor {
  final FlutterSecureStorage storage;

  _AuthInterceptor(this.storage);

  @override
  void onRequest(
    RequestOptions options,
    RequestInterceptorHandler handler,
  ) async {
    final token = await storage.read(key: 'auth_token');
    if (token != null) {
      options.headers['Authorization'] = 'Bearer $token';
    }
    handler.next(options);
  }
}

// Logging Interceptor
class _LoggingInterceptor extends Interceptor {
  @override
  void onRequest(RequestOptions options, RequestInterceptorHandler handler) {
    print('➡️ ${options.method} ${options.path}');
    handler.next(options);
  }

  @override
  void onResponse(Response response, ResponseInterceptorHandler handler) {
    print('⬅️ ${response.statusCode} ${response.requestOptions.path}');
    handler.next(response);
  }

  @override
  void onError(DioException err, ErrorInterceptorHandler handler) {
    print('❌ ${err.type} ${err.requestOptions.path}');
    handler.next(err);
  }
}

// Error Interceptor
class _ErrorInterceptor extends Interceptor {
  @override
  void onError(DioException err, ErrorInterceptorHandler handler) {
    // You could show a SnackBar here, log to analytics, etc.
    handler.next(err);
  }
}
```

### Usage:

```dart
// Create once in your app
final dioService = DioService();
final dio = dioService.dio;

// Use everywhere - tokens added automatically!
final posts = await dio.get('/posts');
final user = await dio.get('/users/1');
```

---

## Automatic Retry Logic

Dio can automatically retry failed requests:

```dart
import 'package:dio/dio.dart';

class RetryInterceptor extends Interceptor {
  final int maxRetries;
  final Duration retryDelay;

  RetryInterceptor({
    this.maxRetries = 3,
    this.retryDelay = const Duration(seconds: 2),
  });

  @override
  void onError(DioException err, ErrorInterceptorHandler handler) async {
    // Only retry on network errors, not bad requests
    if (err.type == DioExceptionType.connectionTimeout ||
        err.type == DioExceptionType.connectionError) {

      // Get current retry count
      final retries = err.requestOptions.extra['retries'] ?? 0;

      if (retries < maxRetries) {
        print('Retry attempt ${retries + 1}/$maxRetries...');

        // Wait before retrying
        await Future.delayed(retryDelay);

        // Update retry count
        err.requestOptions.extra['retries'] = retries + 1;

        // Retry the request
        try {
          final response = await Dio().fetch(err.requestOptions);
          handler.resolve(response); // Success!
        } catch (e) {
          handler.next(err); // Still failed, pass error along
        }
      } else {
        print('Max retries reached');
        handler.next(err);
      }
    } else {
      handler.next(err);
    }
  }
}

// Add to Dio
dio.interceptors.add(RetryInterceptor(maxRetries: 3));
```

---

## Download Progress Tracking

Perfect for downloading files with a progress bar:

```dart
Future<void> downloadFile(String url, String savePath) async {
  await dio.download(
    url,
    savePath,
    onReceiveProgress: (received, total) {
      if (total != -1) {
        final progress = (received / total * 100).toStringAsFixed(0);
        print('Download progress: $progress%');

        // Update UI progress bar here!
      }
    },
  );
  print('Download complete!');
}

// Usage:
await downloadFile(
  'https://example.com/large-file.pdf',
  '/path/to/save/file.pdf',
);
```

### Upload Progress Tracking

```dart
Future<void> uploadFile(String filePath) async {
  final formData = FormData.fromMap({
    'file': await MultipartFile.fromFile(filePath),
  });

  await dio.post(
    '/upload',
    data: formData,
    onSendProgress: (sent, total) {
      final progress = (sent / total * 100).toStringAsFixed(0);
      print('Upload progress: $progress%');

      // Update UI progress bar here!
    },
  );
  print('Upload complete!');
}
```

---

## Error Handling with Dio

Dio provides better error types than `http`:

```dart
Future<List<dynamic>> getPosts() async {
  try {
    final response = await dio.get('/posts');
    return response.data;
  } on DioException catch (e) {
    switch (e.type) {
      case DioExceptionType.connectionTimeout:
      case DioExceptionType.sendTimeout:
      case DioExceptionType.receiveTimeout:
        throw Exception('Connection timeout. Please try again.');

      case DioExceptionType.badResponse:
        // Server responded with error
        final statusCode = e.response?.statusCode;
        if (statusCode == 401) {
          throw Exception('Unauthorized. Please login.');
        } else if (statusCode == 404) {
          throw Exception('Data not found.');
        } else if (statusCode! >= 500) {
          throw Exception('Server error. Try again later.');
        }
        throw Exception('Request failed: $statusCode');

      case DioExceptionType.cancel:
        throw Exception('Request cancelled.');

      case DioExceptionType.connectionError:
        throw Exception('No internet connection.');

      default:
        throw Exception('Network error: ${e.message}');
    }
  }
}
```

---

## Complete Example: Posts App with Dio

Let's build a complete app using Dio:

### 1. Create Post Model

```dart
// lib/models/post.dart
class Post {
  final int id;
  final int userId;
  final String title;
  final String body;

  Post({
    required this.id,
    required this.userId,
    required this.title,
    required this.body,
  });

  factory Post.fromJson(Map<String, dynamic> json) {
    return Post(
      id: json['id'],
      userId: json['userId'],
      title: json['title'],
      body: json['body'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'title': title,
      'body': body,
    };
  }
}
```

### 2. Create Posts Service with Dio

```dart
// lib/services/posts_service.dart
import 'package:dio/dio.dart';
import '../models/post.dart';

class PostsService {
  final Dio _dio;

  PostsService(this._dio);

  // Get all posts
  Future<List<Post>> getPosts() async {
    try {
      final response = await _dio.get('/posts');
      final List<dynamic> data = response.data;
      return data.map((json) => Post.fromJson(json)).toList();
    } on DioException catch (e) {
      throw _handleError(e);
    }
  }

  // Get single post
  Future<Post> getPost(int id) async {
    try {
      final response = await _dio.get('/posts/$id');
      return Post.fromJson(response.data);
    } on DioException catch (e) {
      throw _handleError(e);
    }
  }

  // Create post
  Future<Post> createPost({
    required String title,
    required String body,
  }) async {
    try {
      final response = await _dio.post(
        '/posts',
        data: {
          'title': title,
          'body': body,
          'userId': 1,
        },
      );
      return Post.fromJson(response.data);
    } on DioException catch (e) {
      throw _handleError(e);
    }
  }

  // Update post
  Future<Post> updatePost(Post post) async {
    try {
      final response = await _dio.put(
        '/posts/${post.id}',
        data: post.toJson(),
      );
      return Post.fromJson(response.data);
    } on DioException catch (e) {
      throw _handleError(e);
    }
  }

  // Delete post
  Future<void> deletePost(int id) async {
    try {
      await _dio.delete('/posts/$id');
    } on DioException catch (e) {
      throw _handleError(e);
    }
  }

  String _handleError(DioException e) {
    switch (e.type) {
      case DioExceptionType.connectionTimeout:
      case DioExceptionType.receiveTimeout:
        return 'Connection timeout. Please try again.';
      case DioExceptionType.badResponse:
        return 'Server error: ${e.response?.statusCode}';
      case DioExceptionType.connectionError:
        return 'No internet connection.';
      default:
        return 'Something went wrong.';
    }
  }
}
```

### 3. Create Posts Screen

```dart
// lib/screens/posts_screen.dart
import 'package:flutter/material.dart';
import 'package:dio/dio.dart';
import '../services/dio_service.dart';
import '../services/posts_service.dart';
import '../models/post.dart';

class PostsScreen extends StatefulWidget {
  const PostsScreen({super.key});

  @override
  State<PostsScreen> createState() => _PostsScreenState();
}

class _PostsScreenState extends State<PostsScreen> {
  late final PostsService _postsService;
  late Future<List<Post>> _postsFuture;

  @override
  void initState() {
    super.initState();
    final dio = DioService().dio;
    _postsService = PostsService(dio);
    _postsFuture = _postsService.getPosts();
  }

  void _refreshPosts() {
    setState(() {
      _postsFuture = _postsService.getPosts();
    });
  }

  Future<void> _createPost() async {
    try {
      await _postsService.createPost(
        title: 'New Post',
        body: 'This is a new post created with Dio!',
      );

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Post created successfully!')),
        );
        _refreshPosts();
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Error: $e')),
        );
      }
    }
  }

  Future<void> _deletePost(int id) async {
    try {
      await _postsService.deletePost(id);

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Post deleted!')),
        );
        _refreshPosts();
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Error: $e')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Posts (Dio)'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            tooltip: 'Refresh',
            onPressed: _refreshPosts,
          ),
        ],
      ),
      body: FutureBuilder<List<Post>>(
        future: _postsFuture,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          }

          if (snapshot.hasError) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Icon(Icons.error_outline, size: 64, color: Colors.red),
                  const SizedBox(height: 16),
                  Text(
                    'Error: ${snapshot.error}',
                    textAlign: TextAlign.center,
                    style: const TextStyle(color: Colors.red),
                  ),
                  const SizedBox(height: 16),
                  FilledButton.icon(
                    onPressed: _refreshPosts,
                    icon: const Icon(Icons.refresh),
                    label: const Text('Try Again'),
                  ),
                ],
              ),
            );
          }

          if (snapshot.hasData) {
            final posts = snapshot.data!;

            return ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: posts.length,
              itemBuilder: (context, index) {
                final post = posts[index];

                return Card(
                  margin: const EdgeInsets.only(bottom: 12),
                  child: ListTile(
                    leading: CircleAvatar(
                      child: Text(post.userId.toString()),
                    ),
                    title: Text(
                      post.title,
                      style: const TextStyle(fontWeight: FontWeight.bold),
                    ),
                    subtitle: Text(
                      post.body,
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                    ),
                    trailing: IconButton(
                      icon: const Icon(Icons.delete, color: Colors.red),
                      onPressed: () => _deletePost(post.id),
                    ),
                  ),
                );
              },
            );
          }

          return const Center(child: Text('No posts'));
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _createPost,
        child: const Icon(Icons.add),
      ),
    );
  }
}
```

### 4. Update Main App

```dart
// lib/main.dart
import 'package:flutter/material.dart';
import 'screens/posts_screen.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Dio Demo',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.blue),
        useMaterial3: true,
      ),
      home: const PostsScreen(),
    );
  }
}
```

---

## When to Use Dio vs http

### Use `http` when:
- ✅ Building a simple app with 1-2 API calls
- ✅ Learning Flutter basics (simpler to understand)
- ✅ Minimizing dependencies

### Use Dio when:
- ✅ Building a production app with many API calls
- ✅ Need automatic header injection (auth tokens)
- ✅ Need retry logic
- ✅ Need download/upload progress tracking
- ✅ Need advanced error handling
- ✅ Want cleaner, more maintainable code

**Rule of thumb**: For any serious app, use Dio. It saves time and reduces bugs.

---

## Best Practices

### ✅ DO:
1. **Create a single Dio instance** and reuse it (don't create new ones for each request)
2. **Use BaseOptions** to configure once, use everywhere
3. **Use interceptors** for auth, logging, and error handling
4. **Handle DioException** specifically (better than generic Exception)
5. **Set timeouts** (connectTimeout, receiveTimeout)

### ❌ DON'T:
1. **Don't create Dio() in every method** (reuse the instance)
2. **Don't ignore errors** (always catch DioException)
3. **Don't forget to cancel requests** when leaving screens (prevents memory leaks)
4. **Don't log sensitive data** in production (tokens, passwords)

```dart
// ✅ Good: Single instance
final dio = Dio();

Future<void> getPosts() async {
  await dio.get('/posts');
}

Future<void> getUsers() async {
  await dio.get('/users');
}

// ❌ Bad: New instance every time
Future<void> getPosts() async {
  final dio = Dio(); // Creating new instance!
  await dio.get('/posts');
}
```

---

## Quiz Time! 🧠

Test your understanding:

### Question 1
What is the main advantage of using Dio over the http package?

A) Dio is faster
B) Dio has built-in interceptors and better error handling
C) Dio uses less memory
D) Dio is required by Flutter

### Question 2
What are interceptors used for in Dio?

A) To slow down requests
B) To automatically modify requests/responses and handle errors globally
C) To encrypt data
D) To compress images

### Question 3
How does Dio handle JSON parsing?

A) You must manually use jsonEncode() and jsonDecode()
B) Dio automatically parses JSON in response.data
C) Dio doesn't support JSON
D) You need a separate package for JSON

---

## Answer Key

### Answer 1: B
**Correct**: Dio has built-in interceptors and better error handling

While Dio may have slight performance differences, the main advantage is development experience: interceptors for automatic header injection, better error types (DioException), retry logic, progress tracking, and cleaner code.

### Answer 2: B
**Correct**: To automatically modify requests/responses and handle errors globally

Interceptors are like middleware that can modify every request (add auth headers), log every response, or handle errors in one place instead of repeating code everywhere.

### Answer 3: B
**Correct**: Dio automatically parses JSON in response.data

Unlike the http package where you need to manually call jsonDecode(response.body), Dio automatically parses JSON responses and provides them in response.data.

---

## What's Next?

You've learned how to use Dio - the professional HTTP client for Flutter. In the next lesson, we'll explore **Pagination and Infinite Scroll** to handle large datasets efficiently!

**Coming up in Lesson 6: Pagination and Infinite Scroll**
- Why pagination matters (don't load 10,000 items at once!)
- Offset-based pagination
- Cursor-based pagination
- Infinite scroll implementation
- Pull-to-refresh
- Complete example with ListView

---

## Key Takeaways

✅ Dio is the industry standard for production Flutter apps (80%+ adoption)
✅ Interceptors automatically modify all requests/responses (auth headers, logging)
✅ Dio automatically parses JSON (no jsonDecode needed)
✅ Better error handling with DioException types
✅ Built-in retry logic, download/upload progress tracking
✅ Configure once with BaseOptions, use everywhere
✅ Reuse a single Dio instance across your app

**You're now ready to build professional networking features!** 🎉
