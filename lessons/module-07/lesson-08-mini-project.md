# Module 7, Lesson 8: Mini-Project - Social Media App

## Project Overview

**Welcome to your Module 7 capstone project!** 🎉

In this mini-project, you'll build a complete social media app called **"FlutterGram"** that combines **EVERY concept** you've learned in Module 7:

✅ HTTP requests (GET, POST, PUT, DELETE)
✅ JSON parsing and serialization
✅ Error handling and loading states
✅ Authentication with JWT tokens
✅ Dio package with interceptors
✅ Pagination and infinite scroll
✅ File upload (images)

---

## What You'll Build

### FlutterGram Features

1. **Authentication**
   - Login screen
   - Register screen
   - Secure token storage
   - Auto-logout on token expiration

2. **Feed**
   - Infinite scroll feed of posts
   - Pull-to-refresh
   - Loading states
   - Error handling with retry

3. **Create Post**
   - Image picker (camera/gallery)
   - Image preview
   - Upload with progress
   - Caption input

4. **User Profile**
   - View profile information
   - Post count
   - Logout functionality

5. **Production-Ready Code**
   - Clean architecture
   - Reusable widgets
   - Error handling
   - State management

---

## Project Structure

```
lib/
├── main.dart
├── models/
│   ├── user.dart
│   └── post.dart
├── services/
│   ├── api_client.dart
│   ├── auth_service.dart
│   └── posts_service.dart
├── screens/
│   ├── auth/
│   │   ├── login_screen.dart
│   │   └── register_screen.dart
│   ├── home/
│   │   ├── home_screen.dart
│   │   └── feed_screen.dart
│   ├── create/
│   │   └── create_post_screen.dart
│   └── profile/
│       └── profile_screen.dart
└── widgets/
    ├── post_card.dart
    ├── loading_indicator.dart
    └── error_view.dart
```

---

## Step 1: Setup and Dependencies

### pubspec.yaml

```yaml
name: fluttergram
description: A social media app demonstrating Module 7 concepts

environment:
  sdk: '>=3.7.0 <4.0.0'

dependencies:
  flutter:
    sdk: flutter

  # HTTP & Networking
  dio: ^5.7.0

  # Secure Storage
  flutter_secure_storage: ^9.2.4

  # Image Picking
  image_picker: ^1.1.2

  # Local Storage
  path_provider: ^2.1.5

  # JSON Serialization
  json_annotation: ^4.9.0

dev_dependencies:
  flutter_test:
    sdk: flutter
  build_runner: ^2.4.0
  json_serializable: ^6.11.1
  flutter_lints: ^5.0.0

flutter:
  uses-material-design: true
```

Run:
```bash
flutter pub get
```

---

## Step 2: Create Models

### User Model

```dart
// lib/models/user.dart
import 'package:json_annotation/json_annotation.dart';

part 'user.g.dart';

@JsonSerializable()
class User {
  final int id;
  final String name;
  final String email;
  final String? avatar;
  @JsonKey(name: 'created_at')
  final String? createdAt;

  User({
    required this.id,
    required this.name,
    required this.email,
    this.avatar,
    this.createdAt,
  });

  factory User.fromJson(Map<String, dynamic> json) => _$UserFromJson(json);
  Map<String, dynamic> toJson() => _$UserToJson(this);
}
```

### Post Model

```dart
// lib/models/post.dart
import 'package:json_annotation/json_annotation.dart';
import 'user.dart';

part 'post.g.dart';

@JsonSerializable()
class Post {
  final int id;
  @JsonKey(name: 'user_id')
  final int userId;
  final String caption;
  @JsonKey(name: 'image_url')
  final String imageUrl;
  @JsonKey(name: 'likes_count')
  final int likesCount;
  @JsonKey(name: 'comments_count')
  final int commentsCount;
  @JsonKey(name: 'created_at')
  final String createdAt;
  final User? user; // Optional, might be included in response

  Post({
    required this.id,
    required this.userId,
    required this.caption,
    required this.imageUrl,
    required this.likesCount,
    required this.commentsCount,
    required this.createdAt,
    this.user,
  });

  factory Post.fromJson(Map<String, dynamic> json) => _$PostFromJson(json);
  Map<String, dynamic> toJson() => _$PostToJson(this);
}
```

Generate the code:
```bash
flutter pub run build_runner build --delete-conflicting-outputs
```

---

## Step 3: Create API Client with Dio

```dart
// lib/services/api_client.dart
import 'package:dio/dio.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class ApiClient {
  static const String baseUrl = 'https://api.fluttergram.com';

  late final Dio _dio;
  final _storage = const FlutterSecureStorage();

  ApiClient() {
    _dio = Dio(
      BaseOptions(
        baseUrl: baseUrl,
        connectTimeout: const Duration(seconds: 30),
        receiveTimeout: const Duration(seconds: 30),
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
      ),
    );

    _dio.interceptors.add(_AuthInterceptor(_storage));
    _dio.interceptors.add(_LoggingInterceptor());
    _dio.interceptors.add(_ErrorInterceptor());
  }

  Dio get dio => _dio;
}

// Auto-inject auth token
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

// Log requests and responses
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

// Handle errors globally
class _ErrorInterceptor extends Interceptor {
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
        final statusCode = err.response?.statusCode;
        if (statusCode == 401) {
          message = 'Session expired. Please login again.';
        } else if (statusCode == 404) {
          message = 'Resource not found.';
        } else if (statusCode! >= 500) {
          message = 'Server error. Try again later.';
        } else {
          message = 'Request failed: $statusCode';
        }
        break;
      case DioExceptionType.connectionError:
        message = 'No internet connection.';
        break;
      default:
        message = 'Something went wrong.';
    }

    // Replace error with custom message
    handler.next(
      DioException(
        requestOptions: err.requestOptions,
        message: message,
        type: err.type,
      ),
    );
  }
}
```

---

## Step 4: Create Auth Service

```dart
// lib/services/auth_service.dart
import 'package:dio/dio.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import '../models/user.dart';
import 'api_client.dart';

class AuthService {
  final Dio _dio = ApiClient().dio;
  final _storage = const FlutterSecureStorage();

  // Login
  Future<User> login({
    required String email,
    required String password,
  }) async {
    try {
      final response = await _dio.post(
        '/auth/login',
        data: {'email': email, 'password': password},
      );

      final token = response.data['token'];
      final user = User.fromJson(response.data['user']);

      await _storage.write(key: 'auth_token', value: token);

      return user;
    } on DioException catch (e) {
      throw e.message ?? 'Login failed';
    }
  }

  // Register
  Future<User> register({
    required String name,
    required String email,
    required String password,
  }) async {
    try {
      final response = await _dio.post(
        '/auth/register',
        data: {
          'name': name,
          'email': email,
          'password': password,
        },
      );

      final token = response.data['token'];
      final user = User.fromJson(response.data['user']);

      await _storage.write(key: 'auth_token', value: token);

      return user;
    } on DioException catch (e) {
      throw e.message ?? 'Registration failed';
    }
  }

  // Get current user
  Future<User> getCurrentUser() async {
    try {
      final response = await _dio.get('/auth/me');
      return User.fromJson(response.data);
    } on DioException catch (e) {
      throw e.message ?? 'Failed to get user';
    }
  }

  // Logout
  Future<void> logout() async {
    await _storage.delete(key: 'auth_token');
  }

  // Check if logged in
  Future<bool> isLoggedIn() async {
    final token = await _storage.read(key: 'auth_token');
    return token != null;
  }
}
```

---

## Step 5: Create Posts Service

```dart
// lib/services/posts_service.dart
import 'package:dio/dio.dart';
import 'dart:io';
import '../models/post.dart';
import 'api_client.dart';

class PostsService {
  final Dio _dio = ApiClient().dio;

  // Get feed with pagination
  Future<List<Post>> getFeed({required int page, int limit = 20}) async {
    try {
      final response = await _dio.get(
        '/posts',
        queryParameters: {
          'page': page,
          'limit': limit,
        },
      );

      final List<dynamic> data = response.data['posts'];
      return data.map((json) => Post.fromJson(json)).toList();
    } on DioException catch (e) {
      throw e.message ?? 'Failed to load feed';
    }
  }

  // Create post with image
  Future<Post> createPost({
    required File imageFile,
    required String caption,
    Function(int sent, int total)? onProgress,
  }) async {
    try {
      final fileName = imageFile.path.split('/').last;
      final formData = FormData.fromMap({
        'image': await MultipartFile.fromFile(
          imageFile.path,
          filename: fileName,
        ),
        'caption': caption,
      });

      final response = await _dio.post(
        '/posts',
        data: formData,
        onSendProgress: onProgress,
      );

      return Post.fromJson(response.data);
    } on DioException catch (e) {
      throw e.message ?? 'Failed to create post';
    }
  }

  // Like post
  Future<void> likePost(int postId) async {
    try {
      await _dio.post('/posts/$postId/like');
    } on DioException catch (e) {
      throw e.message ?? 'Failed to like post';
    }
  }

  // Unlike post
  Future<void> unlikePost(int postId) async {
    try {
      await _dio.delete('/posts/$postId/like');
    } on DioException catch (e) {
      throw e.message ?? 'Failed to unlike post';
    }
  }
}
```

---

## Step 6: Create Reusable Widgets

### Post Card Widget

```dart
// lib/widgets/post_card.dart
import 'package:flutter/material.dart';
import '../models/post.dart';

class PostCard extends StatelessWidget {
  final Post post;
  final VoidCallback? onLike;

  const PostCard({
    super.key,
    required this.post,
    this.onLike,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.only(bottom: 16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // User header
          ListTile(
            leading: CircleAvatar(
              backgroundImage: post.user?.avatar != null
                  ? NetworkImage(post.user!.avatar!)
                  : null,
              child: post.user?.avatar == null
                  ? Text(post.user?.name[0] ?? 'U')
                  : null,
            ),
            title: Text(
              post.user?.name ?? 'Unknown User',
              style: const TextStyle(fontWeight: FontWeight.bold),
            ),
            subtitle: Text(_formatDate(post.createdAt)),
          ),

          // Post image
          AspectRatio(
            aspectRatio: 1.0,
            child: Image.network(
              post.imageUrl,
              fit: BoxFit.cover,
              loadingBuilder: (context, child, loadingProgress) {
                if (loadingProgress == null) return child;
                return Center(
                  child: CircularProgressIndicator(
                    value: loadingProgress.expectedTotalBytes != null
                        ? loadingProgress.cumulativeBytesLoaded /
                            loadingProgress.expectedTotalBytes!
                        : null,
                  ),
                );
              },
              errorBuilder: (context, error, stackTrace) {
                return Container(
                  color: Colors.grey.shade200,
                  child: const Center(
                    child: Icon(Icons.error_outline, size: 48),
                  ),
                );
              },
            ),
          ),

          // Action buttons
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 8),
            child: Row(
              children: [
                IconButton(
                  icon: const Icon(Icons.favorite_border),
                  onPressed: onLike,
                ),
                Text('${post.likesCount}'),
                const SizedBox(width: 16),
                const Icon(Icons.comment_outlined),
                const SizedBox(width: 4),
                Text('${post.commentsCount}'),
              ],
            ),
          ),

          // Caption
          if (post.caption.isNotEmpty)
            Padding(
              padding: const EdgeInsets.fromLTRB(16, 0, 16, 16),
              child: RichText(
                text: TextSpan(
                  style: DefaultTextStyle.of(context).style,
                  children: [
                    TextSpan(
                      text: '${post.user?.name ?? 'User'} ',
                      style: const TextStyle(fontWeight: FontWeight.bold),
                    ),
                    TextSpan(text: post.caption),
                  ],
                ),
              ),
            ),
        ],
      ),
    );
  }

  String _formatDate(String dateStr) {
    try {
      final date = DateTime.parse(dateStr);
      final now = DateTime.now();
      final difference = now.difference(date);

      if (difference.inDays > 7) {
        return '${date.day}/${date.month}/${date.year}';
      } else if (difference.inDays > 0) {
        return '${difference.inDays}d ago';
      } else if (difference.inHours > 0) {
        return '${difference.inHours}h ago';
      } else if (difference.inMinutes > 0) {
        return '${difference.inMinutes}m ago';
      } else {
        return 'Just now';
      }
    } catch (e) {
      return dateStr;
    }
  }
}
```

### Error View Widget

```dart
// lib/widgets/error_view.dart
import 'package:flutter/material.dart';

class ErrorView extends StatelessWidget {
  final String message;
  final VoidCallback onRetry;

  const ErrorView({
    super.key,
    required this.message,
    required this.onRetry,
  });

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.error_outline,
              size: 64,
              color: Colors.red.shade300,
            ),
            const SizedBox(height: 16),
            Text(
              'Oops!',
              style: Theme.of(context).textTheme.headlineSmall,
            ),
            const SizedBox(height: 8),
            Text(
              message,
              textAlign: TextAlign.center,
              style: TextStyle(color: Colors.grey.shade600),
            ),
            const SizedBox(height: 24),
            FilledButton.icon(
              onPressed: onRetry,
              icon: const Icon(Icons.refresh),
              label: const Text('Try Again'),
            ),
          ],
        ),
      ),
    );
  }
}
```

---

## Step 7: Create Screens

### Login Screen

```dart
// lib/screens/auth/login_screen.dart
import 'package:flutter/material.dart';
import '../../services/auth_service.dart';
import '../home/home_screen.dart';
import 'register_screen.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _authService = AuthService();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  bool _isLoading = false;
  String? _errorMessage;

  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  Future<void> _handleLogin() async {
    if (_emailController.text.isEmpty || _passwordController.text.isEmpty) {
      setState(() {
        _errorMessage = 'Please enter both email and password';
      });
      return;
    }

    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      await _authService.login(
        email: _emailController.text,
        password: _passwordController.text,
      );

      if (mounted) {
        Navigator.of(context).pushReplacement(
          MaterialPageRoute(builder: (_) => const HomeScreen()),
        );
      }
    } catch (e) {
      setState(() {
        _errorMessage = e.toString();
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              const SizedBox(height: 48),

              // Logo
              Icon(
                Icons.camera_alt,
                size: 80,
                color: Theme.of(context).primaryColor,
              ),
              const SizedBox(height: 16),
              Text(
                'FlutterGram',
                textAlign: TextAlign.center,
                style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 8),
              Text(
                'Share your moments',
                textAlign: TextAlign.center,
                style: TextStyle(color: Colors.grey.shade600),
              ),
              const SizedBox(height: 48),

              // Email field
              TextField(
                controller: _emailController,
                decoration: const InputDecoration(
                  labelText: 'Email',
                  prefixIcon: Icon(Icons.email),
                  border: OutlineInputBorder(),
                ),
                keyboardType: TextInputType.emailAddress,
                enabled: !_isLoading,
              ),
              const SizedBox(height: 16),

              // Password field
              TextField(
                controller: _passwordController,
                obscureText: true,
                decoration: const InputDecoration(
                  labelText: 'Password',
                  prefixIcon: Icon(Icons.lock),
                  border: OutlineInputBorder(),
                ),
                enabled: !_isLoading,
                onSubmitted: (_) => _handleLogin(),
              ),
              const SizedBox(height: 24),

              // Error message
              if (_errorMessage != null)
                Container(
                  padding: const EdgeInsets.all(12),
                  margin: const EdgeInsets.only(bottom: 16),
                  decoration: BoxDecoration(
                    color: Colors.red.shade50,
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Text(
                    _errorMessage!,
                    style: TextStyle(color: Colors.red.shade700),
                    textAlign: TextAlign.center,
                  ),
                ),

              // Login button
              FilledButton(
                onPressed: _isLoading ? null : _handleLogin,
                style: FilledButton.styleFrom(
                  padding: const EdgeInsets.symmetric(vertical: 16),
                ),
                child: _isLoading
                    ? const SizedBox(
                        height: 20,
                        width: 20,
                        child: CircularProgressIndicator(strokeWidth: 2),
                      )
                    : const Text('Login'),
              ),

              const SizedBox(height: 16),

              // Register link
              TextButton(
                onPressed: _isLoading
                    ? null
                    : () {
                        Navigator.of(context).push(
                          MaterialPageRoute(
                            builder: (_) => const RegisterScreen(),
                          ),
                        );
                      },
                child: const Text('Don\'t have an account? Register'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
```

### Feed Screen with Infinite Scroll

```dart
// lib/screens/home/feed_screen.dart
import 'package:flutter/material.dart';
import '../../services/posts_service.dart';
import '../../models/post.dart';
import '../../widgets/post_card.dart';
import '../../widgets/error_view.dart';

class FeedScreen extends StatefulWidget {
  const FeedScreen({super.key});

  @override
  State<FeedScreen> createState() => _FeedScreenState();
}

class _FeedScreenState extends State<FeedScreen> {
  final _postsService = PostsService();
  final _scrollController = ScrollController();
  final List<Post> _posts = [];

  int _currentPage = 1;
  bool _isLoadingInitial = true;
  bool _isLoadingMore = false;
  bool _hasMoreData = true;
  String? _errorMessage;

  @override
  void initState() {
    super.initState();
    _loadInitialPosts();
    _scrollController.addListener(_onScroll);
  }

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }

  Future<void> _loadInitialPosts() async {
    setState(() {
      _isLoadingInitial = true;
      _errorMessage = null;
    });

    try {
      final posts = await _postsService.getFeed(page: 1);
      setState(() {
        _posts.clear();
        _posts.addAll(posts);
        _currentPage = 1;
        _hasMoreData = posts.isNotEmpty;
        _isLoadingInitial = false;
      });
    } catch (e) {
      setState(() {
        _errorMessage = e.toString();
        _isLoadingInitial = false;
      });
    }
  }

  void _onScroll() {
    if (_scrollController.position.pixels >=
        _scrollController.position.maxScrollExtent - 200) {
      _loadMorePosts();
    }
  }

  Future<void> _loadMorePosts() async {
    if (_isLoadingMore || !_hasMoreData || _isLoadingInitial) return;

    setState(() {
      _isLoadingMore = true;
    });

    try {
      _currentPage++;
      final newPosts = await _postsService.getFeed(page: _currentPage);

      setState(() {
        if (newPosts.isEmpty) {
          _hasMoreData = false;
        } else {
          _posts.addAll(newPosts);
        }
        _isLoadingMore = false;
      });
    } catch (e) {
      setState(() {
        _isLoadingMore = false;
        _currentPage--;
      });
    }
  }

  Future<void> _handleLike(Post post) async {
    try {
      await _postsService.likePost(post.id);
      // Optionally update UI optimistically
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Failed to like post: $e')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoadingInitial && _posts.isEmpty) {
      return const Center(child: CircularProgressIndicator());
    }

    if (_errorMessage != null && _posts.isEmpty) {
      return ErrorView(
        message: _errorMessage!,
        onRetry: _loadInitialPosts,
      );
    }

    return RefreshIndicator(
      onRefresh: _loadInitialPosts,
      child: ListView.builder(
        controller: _scrollController,
        padding: const EdgeInsets.all(16),
        itemCount: _posts.length + 1,
        itemBuilder: (context, index) {
          if (index == _posts.length) {
            if (_isLoadingMore) {
              return const Center(
                child: Padding(
                  padding: EdgeInsets.all(16.0),
                  child: CircularProgressIndicator(),
                ),
              );
            } else if (!_hasMoreData) {
              return Center(
                child: Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: Text(
                    'You\'re all caught up! 🎉',
                    style: TextStyle(color: Colors.grey.shade600),
                  ),
                ),
              );
            } else {
              return const SizedBox.shrink();
            }
          }

          final post = _posts[index];
          return PostCard(
            post: post,
            onLike: () => _handleLike(post),
          );
        },
      ),
    );
  }
}
```

### Create Post Screen

```dart
// lib/screens/create/create_post_screen.dart
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'dart:io';
import '../../services/posts_service.dart';

class CreatePostScreen extends StatefulWidget {
  const CreatePostScreen({super.key});

  @override
  State<CreatePostScreen> createState() => _CreatePostScreenState();
}

class _CreatePostScreenState extends State<CreatePostScreen> {
  final _postsService = PostsService();
  final _imagePicker = ImagePicker();
  final _captionController = TextEditingController();

  File? _selectedImage;
  bool _isUploading = false;
  double _uploadProgress = 0.0;

  @override
  void dispose() {
    _captionController.dispose();
    super.dispose();
  }

  Future<void> _pickImage(ImageSource source) async {
    try {
      final XFile? image = await _imagePicker.pickImage(
        source: source,
        maxWidth: 1920,
        maxHeight: 1080,
        imageQuality: 85,
      );

      if (image != null) {
        setState(() {
          _selectedImage = File(image.path);
        });
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Failed to pick image: $e')),
        );
      }
    }
  }

  Future<void> _createPost() async {
    if (_selectedImage == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Please select an image')),
      );
      return;
    }

    setState(() {
      _isUploading = true;
      _uploadProgress = 0.0;
    });

    try {
      await _postsService.createPost(
        imageFile: _selectedImage!,
        caption: _captionController.text,
        onProgress: (sent, total) {
          setState(() {
            _uploadProgress = sent / total;
          });
        },
      );

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Post created successfully!')),
        );
        Navigator.of(context).pop(true); // Return true to refresh feed
      }
    } catch (e) {
      setState(() {
        _isUploading = false;
      });

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Failed to create post: $e')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Create Post'),
        actions: [
          TextButton(
            onPressed: (_selectedImage != null && !_isUploading)
                ? _createPost
                : null,
            child: const Text('Post'),
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            // Image preview
            if (_selectedImage != null)
              AspectRatio(
                aspectRatio: 1.0,
                child: ClipRRect(
                  borderRadius: BorderRadius.circular(12),
                  child: Image.file(
                    _selectedImage!,
                    fit: BoxFit.cover,
                  ),
                ),
              )
            else
              AspectRatio(
                aspectRatio: 1.0,
                child: Container(
                  decoration: BoxDecoration(
                    color: Colors.grey.shade100,
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Icon(
                        Icons.add_photo_alternate_outlined,
                        size: 64,
                        color: Colors.grey.shade400,
                      ),
                      const SizedBox(height: 16),
                      Text(
                        'Select a photo',
                        style: TextStyle(
                          color: Colors.grey.shade600,
                          fontSize: 16,
                        ),
                      ),
                    ],
                  ),
                ),
              ),

            const SizedBox(height: 16),

            // Image source buttons
            if (_selectedImage == null)
              Row(
                children: [
                  Expanded(
                    child: OutlinedButton.icon(
                      onPressed: () => _pickImage(ImageSource.gallery),
                      icon: const Icon(Icons.photo_library),
                      label: const Text('Gallery'),
                    ),
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: OutlinedButton.icon(
                      onPressed: () => _pickImage(ImageSource.camera),
                      icon: const Icon(Icons.camera_alt),
                      label: const Text('Camera'),
                    ),
                  ),
                ],
              ),

            const SizedBox(height: 24),

            // Caption input
            TextField(
              controller: _captionController,
              decoration: const InputDecoration(
                labelText: 'Caption',
                hintText: 'Write a caption...',
                border: OutlineInputBorder(),
              ),
              maxLines: 3,
              enabled: !_isUploading,
            ),

            // Upload progress
            if (_isUploading) ...[
              const SizedBox(height: 24),
              LinearProgressIndicator(value: _uploadProgress),
              const SizedBox(height: 8),
              Text(
                'Uploading... ${(_uploadProgress * 100).toStringAsFixed(0)}%',
                textAlign: TextAlign.center,
                style: TextStyle(color: Colors.grey.shade600),
              ),
            ],
          ],
        ),
      ),
    );
  }
}
```

---

## Step 8: Complete Main App

```dart
// lib/main.dart
import 'package:flutter/material.dart';
import 'services/auth_service.dart';
import 'screens/auth/login_screen.dart';
import 'screens/home/home_screen.dart';

void main() {
  runApp(const FlutterGramApp());
}

class FlutterGramApp extends StatelessWidget {
  const FlutterGramApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'FlutterGram',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.purple),
        useMaterial3: true,
      ),
      home: const AuthCheck(),
    );
  }
}

class AuthCheck extends StatefulWidget {
  const AuthCheck({super.key});

  @override
  State<AuthCheck> createState() => _AuthCheckState();
}

class _AuthCheckState extends State<AuthCheck> {
  final _authService = AuthService();
  bool _isChecking = true;
  bool _isLoggedIn = false;

  @override
  void initState() {
    super.initState();
    _checkAuth();
  }

  Future<void> _checkAuth() async {
    final loggedIn = await _authService.isLoggedIn();
    setState(() {
      _isLoggedIn = loggedIn;
      _isChecking = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    if (_isChecking) {
      return const Scaffold(
        body: Center(child: CircularProgressIndicator()),
      );
    }

    return _isLoggedIn ? const HomeScreen() : const LoginScreen();
  }
}
```

---

## What You've Accomplished

Congratulations! You've built a complete social media app that demonstrates:

✅ **HTTP Requests**: GET (feed), POST (create post, login), DELETE (logout)
✅ **JSON Parsing**: User and Post models with json_serializable
✅ **Error Handling**: User-friendly error messages, retry logic
✅ **Authentication**: Login, register, secure token storage, auto-logout
✅ **Dio with Interceptors**: Auto token injection, logging, global error handling
✅ **Pagination**: Infinite scroll with pull-to-refresh
✅ **File Upload**: Image picker, upload with progress tracking

---

## Next Steps & Enhancements

Want to take this further? Try adding:

1. **Comments System**: Add, view, delete comments on posts
2. **Like Animation**: Heart animation when double-tapping posts
3. **User Profiles**: View other users' profiles and posts
4. **Search**: Search for users and hashtags
5. **Notifications**: Push notifications for likes and comments
6. **Stories**: Instagram-style stories that disappear after 24 hours
7. **Direct Messages**: Chat feature between users

---

## Key Takeaways

✅ Clean architecture separates concerns (models, services, screens, widgets)
✅ Dio interceptors eliminate repetitive code (auth headers, logging)
✅ Reusable widgets (PostCard, ErrorView) improve maintainability
✅ Proper error handling creates professional user experience
✅ Progress tracking provides feedback for long operations
✅ Pagination prevents memory issues and improves performance

---

## Congratulations! 🎉

**You've completed Module 7: Networking and API Integration!**

You now have the skills to build production-ready apps that:
- Connect to real-world APIs
- Handle authentication securely
- Upload and download files
- Manage large datasets efficiently
- Provide excellent user experience with loading states and error handling

**You're ready for Module 8: Backend Integration** where you'll learn to connect Flutter apps to Firebase, Supabase, and other backend services!

---

## Quiz Time! 🧠

### Question 1
Why use interceptors in Dio?

A) To make requests faster
B) To automatically modify all requests/responses in one place (like adding auth headers)
C) To compress data
D) To cache responses

### Question 2
What's the benefit of separating services from screens?

A) It makes the code longer
B) It improves code organization, testability, and reusability
C) It's required by Flutter
D) It makes the app faster

### Question 3
Why implement pagination instead of loading all posts at once?

A) It looks better
B) It prevents memory issues, reduces server load, and improves performance
C) It's easier to code
D) APIs require it

---

## Answer Key

### Answer 1: B
**Correct**: To automatically modify all requests/responses in one place

Interceptors allow you to add headers (like auth tokens), log requests, handle errors, and retry failed requests in one central place. Without interceptors, you'd have to repeat this code for every single API call.

### Answer 2: B
**Correct**: It improves code organization, testability, and reusability

Separating services from UI makes your code modular. You can test services independently, reuse them across multiple screens, and easily swap implementations (e.g., switch from one API to another).

### Answer 3: B
**Correct**: It prevents memory issues, reduces server load, and improves performance

Loading 10,000 posts at once would consume excessive memory, slow down the app, and waste bandwidth. Pagination loads data in small chunks (20-50 items), providing a smooth experience while reducing resource usage.

---

**Module 7 Complete!** You're now a Flutter networking expert! 🚀
