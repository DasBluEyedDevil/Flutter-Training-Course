# Module 7, Lesson 4: Authentication and Headers

## What You'll Learn
By the end of this lesson, you'll understand how to authenticate users with APIs, securely store authentication tokens, and send authenticated requests with custom headers.

---

## Why This Matters

**Every time you log into an app, this is what's happening behind the scenes.**

- **95% of modern apps** require user authentication
- **Poor authentication** leads to security breaches (costly and damaging)
- **Proper token storage** protects user accounts from theft
- **Understanding headers** is essential for working with real-world APIs

In this lesson, you'll learn how professional apps handle authentication - the same techniques used by apps like Instagram, Twitter, and banking apps.

---

## Real-World Analogy: The VIP Wristband

Imagine going to a music festival:

### Without Authentication
You try to enter, but the security guard stops you. "Do you have a ticket?" Without proof that you paid, you can't get in.

### With Authentication
1. **Login (Get the Wristband)**: You show your ticket at the entrance. They give you a wristband that proves you're allowed in.
2. **Access Everything (Send the Wristband)**: Now you can go to different stages, food stalls, etc. Just flash your wristband each time.
3. **No Need to Show Ticket Again**: Your wristband is proof enough. You don't need to show your original ticket every time.
4. **Wristband Expires**: At the end of the day, the wristband becomes invalid.

### In the Digital World
- **Ticket** = Username and Password
- **Wristband** = Authentication Token (JWT)
- **Showing Wristband** = Sending Token in Request Headers
- **Wristband Expires** = Token Expiration

---

## What Are HTTP Headers?

Think of HTTP headers as **the envelope information** on a letter:

### The Envelope (Headers)
```
From: You
To: API Server
Type: JSON Letter
Language: English
Authorization: VIP Member #12345
```

### The Letter (Body)
```json
{
  "message": "Please give me my profile information"
}
```

**Headers provide metadata** (information about the request) without being part of the actual data.

### Common Headers

| Header | Purpose | Example |
|--------|---------|---------|
| `Authorization` | Proves who you are | `Bearer abc123token` |
| `Content-Type` | What format you're sending | `application/json` |
| `Accept` | What format you want back | `application/json` |
| `User-Agent` | What app/device you're using | `MyApp/1.0.0` |

---

## What is a JWT Token?

**JWT** (JSON Web Token) is like a **tamper-proof ID card** that proves who you are.

### Structure of a JWT
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NSIsIm5hbWUiOiJBbGljZSJ9.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

It has three parts (separated by dots):
1. **Header**: Type of token and algorithm used
2. **Payload**: Your user information (user ID, name, etc.)
3. **Signature**: Proof that the server created it (can't be forged)

**You don't need to create JWTs** - the backend server creates them when you log in. Your Flutter app just **stores and sends** them.

---

## How Authentication Works: The Flow

```
┌─────────────┐                    ┌─────────────┐
│             │   1. Login         │             │
│  Flutter    │   (username +      │     API     │
│    App      │    password)       │   Server    │
│             │─────────────────>  │             │
│             │                    │             │
│             │  2. Here's your    │             │
│             │     JWT token!     │             │
│             │<─────────────────  │             │
│             │                    │             │
│   Store     │                    │             │
│   Token     │                    │             │
│  Securely   │                    │             │
│             │                    │             │
│             │  3. Get my data    │             │
│             │   (+ JWT token     │             │
│             │    in header)      │             │
│             │─────────────────>  │             │
│             │                    │  Verify     │
│             │                    │  Token      │
│             │  4. Here's your    │             │
│             │     data!          │             │
│             │<─────────────────  │             │
└─────────────┘                    └─────────────┘
```

---

## Setting Up Secure Storage

First, add the `flutter_secure_storage` package to store tokens safely:

```yaml
# pubspec.yaml
dependencies:
  flutter:
    sdk: flutter
  http: ^1.6.0
  flutter_secure_storage: ^9.2.4
```

Run:
```bash
flutter pub get
```

### Why Secure Storage?

**DON'T** store tokens in:
- ❌ Regular variables (lost when app closes)
- ❌ SharedPreferences (not encrypted, easily read)
- ❌ Plain text files (very insecure)

**DO** store tokens in:
- ✅ flutter_secure_storage (encrypted, platform-specific keychain)

---

## Bad vs Good: Handling Authentication

### ❌ Bad Approach: No Authentication

```dart
// This will get rejected by the API!
Future<Map<String, dynamic>> getProfile() async {
  final response = await http.get(
    Uri.parse('https://api.example.com/profile'),
  );

  if (response.statusCode == 200) {
    return jsonDecode(response.body);
  } else {
    throw Exception('Failed'); // Why? No token!
  }
}
```

**Problem**: No authentication token sent. Server returns 401 Unauthorized.

### ❌ Bad Approach: Insecure Storage

```dart
// DON'T DO THIS!
String? _token;

Future<void> login(String username, String password) async {
  final response = await http.post(
    Uri.parse('https://api.example.com/login'),
    body: jsonEncode({'username': username, 'password': password}),
  );

  if (response.statusCode == 200) {
    final data = jsonDecode(response.body);
    _token = data['token']; // Lost when app closes!
  }
}
```

**Problem**: Token stored in memory only. Lost when app closes or crashes.

### ✅ Good Approach: Secure Authentication

```dart
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class AuthService {
  final storage = const FlutterSecureStorage();
  final String baseUrl = 'https://api.example.com';

  // Login and store token securely
  Future<bool> login(String username, String password) async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/login'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'username': username,
          'password': password,
        }),
      ).timeout(const Duration(seconds: 10));

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        final token = data['token'];

        // Store token securely
        await storage.write(key: 'auth_token', value: token);
        return true;
      } else {
        return false;
      }
    } catch (e) {
      print('Login error: $e');
      return false;
    }
  }

  // Get authenticated user profile
  Future<Map<String, dynamic>> getProfile() async {
    // Retrieve token from secure storage
    final token = await storage.read(key: 'auth_token');

    if (token == null) {
      throw Exception('Not logged in');
    }

    final response = await http.get(
      Uri.parse('$baseUrl/profile'),
      headers: {
        'Authorization': 'Bearer $token', // THE MAGIC LINE!
        'Content-Type': 'application/json',
      },
    ).timeout(const Duration(seconds: 10));

    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    } else if (response.statusCode == 401) {
      // Token expired or invalid
      await logout();
      throw Exception('Session expired. Please login again.');
    } else {
      throw Exception('Failed to get profile');
    }
  }

  // Logout and clear token
  Future<void> logout() async {
    await storage.delete(key: 'auth_token');
  }

  // Check if user is logged in
  Future<bool> isLoggedIn() async {
    final token = await storage.read(key: 'auth_token');
    return token != null;
  }
}
```

**Why This is Good**:
- ✅ Token stored securely with encryption
- ✅ Persists across app restarts
- ✅ Handles 401 (unauthorized) by logging out
- ✅ Easy to check login status
- ✅ Clean separation of concerns

---

## Complete Example: Login Screen with Authentication

Let's build a complete login flow with profile display:

### 1. Create the Auth Service (as shown above)

```dart
// lib/services/auth_service.dart
// (Use the complete AuthService class from above)
```

### 2. Create a User Model

```dart
// lib/models/user.dart
class User {
  final String id;
  final String name;
  final String email;
  final String avatarUrl;

  User({
    required this.id,
    required this.name,
    required this.email,
    required this.avatarUrl,
  });

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      id: json['id'].toString(),
      name: json['name'],
      email: json['email'],
      avatarUrl: json['avatar'] ?? 'https://via.placeholder.com/150',
    );
  }
}
```

### 3. Create the Login Screen

```dart
// lib/screens/login_screen.dart
import 'package:flutter/material.dart';
import '../services/auth_service.dart';
import 'profile_screen.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _authService = AuthService();
  final _usernameController = TextEditingController();
  final _passwordController = TextEditingController();
  bool _isLoading = false;
  String? _errorMessage;

  @override
  void dispose() {
    _usernameController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  Future<void> _handleLogin() async {
    if (_usernameController.text.isEmpty || _passwordController.text.isEmpty) {
      setState(() {
        _errorMessage = 'Please enter both username and password';
      });
      return;
    }

    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    final success = await _authService.login(
      _usernameController.text,
      _passwordController.text,
    );

    setState(() {
      _isLoading = false;
    });

    if (success && mounted) {
      // Navigate to profile screen
      Navigator.of(context).pushReplacement(
        MaterialPageRoute(builder: (_) => const ProfileScreen()),
      );
    } else {
      setState(() {
        _errorMessage = 'Invalid username or password';
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(24.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              // Logo or Title
              const Icon(
                Icons.lock_person,
                size: 80,
                color: Colors.blue,
              ),
              const SizedBox(height: 24),
              Text(
                'Welcome Back',
                style: Theme.of(context).textTheme.headlineMedium,
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 8),
              Text(
                'Sign in to continue',
                style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                  color: Colors.grey,
                ),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 48),

              // Username field
              TextField(
                controller: _usernameController,
                decoration: const InputDecoration(
                  labelText: 'Username',
                  prefixIcon: Icon(Icons.person),
                  border: OutlineInputBorder(),
                ),
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
                  decoration: BoxDecoration(
                    color: Colors.red.shade50,
                    borderRadius: BorderRadius.circular(8),
                    border: Border.all(color: Colors.red.shade200),
                  ),
                  child: Row(
                    children: [
                      Icon(Icons.error_outline, color: Colors.red.shade700),
                      const SizedBox(width: 12),
                      Expanded(
                        child: Text(
                          _errorMessage!,
                          style: TextStyle(color: Colors.red.shade700),
                        ),
                      ),
                    ],
                  ),
                ),
              if (_errorMessage != null) const SizedBox(height: 16),

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
                  : const Text('Login', style: TextStyle(fontSize: 16)),
              ),

              const SizedBox(height: 16),

              // Demo credentials hint
              Container(
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: Colors.blue.shade50,
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Column(
                  children: [
                    Text(
                      'Demo Credentials',
                      style: TextStyle(
                        fontWeight: FontWeight.bold,
                        color: Colors.blue.shade900,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      'Try any username/password for demo',
                      style: TextStyle(
                        fontSize: 12,
                        color: Colors.blue.shade700,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
```

### 4. Create the Profile Screen

```dart
// lib/screens/profile_screen.dart
import 'package:flutter/material.dart';
import '../services/auth_service.dart';
import '../models/user.dart';
import 'login_screen.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  final _authService = AuthService();
  late Future<Map<String, dynamic>> _profileFuture;

  @override
  void initState() {
    super.initState();
    _profileFuture = _authService.getProfile();
  }

  void _retryLoadProfile() {
    setState(() {
      _profileFuture = _authService.getProfile();
    });
  }

  Future<void> _handleLogout() async {
    await _authService.logout();

    if (mounted) {
      Navigator.of(context).pushReplacement(
        MaterialPageRoute(builder: (_) => const LoginScreen()),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('My Profile'),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            tooltip: 'Logout',
            onPressed: _handleLogout,
          ),
        ],
      ),
      body: FutureBuilder<Map<String, dynamic>>(
        future: _profileFuture,
        builder: (context, snapshot) {
          // Loading state
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  CircularProgressIndicator(),
                  SizedBox(height: 16),
                  Text('Loading your profile...'),
                ],
              ),
            );
          }

          // Error state
          if (snapshot.hasError) {
            final errorMessage = snapshot.error.toString();
            final isSessionExpired = errorMessage.contains('Session expired');

            if (isSessionExpired) {
              // Auto-navigate to login
              WidgetsBinding.instance.addPostFrameCallback((_) {
                Navigator.of(context).pushReplacement(
                  MaterialPageRoute(builder: (_) => const LoginScreen()),
                );
              });
              return const Center(child: CircularProgressIndicator());
            }

            return Center(
              child: Padding(
                padding: const EdgeInsets.all(24.0),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    const Icon(
                      Icons.error_outline,
                      color: Colors.red,
                      size: 64,
                    ),
                    const SizedBox(height: 16),
                    const Text(
                      'Failed to load profile',
                      style: TextStyle(
                        fontSize: 20,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 8),
                    Text(
                      errorMessage,
                      textAlign: TextAlign.center,
                      style: const TextStyle(color: Colors.grey),
                    ),
                    const SizedBox(height: 24),
                    FilledButton.icon(
                      onPressed: _retryLoadProfile,
                      icon: const Icon(Icons.refresh),
                      label: const Text('Try Again'),
                    ),
                  ],
                ),
              ),
            );
          }

          // Success state
          if (snapshot.hasData) {
            final user = User.fromJson(snapshot.data!);

            return SingleChildScrollView(
              padding: const EdgeInsets.all(24.0),
              child: Column(
                children: [
                  // Avatar
                  CircleAvatar(
                    radius: 60,
                    backgroundImage: NetworkImage(user.avatarUrl),
                  ),
                  const SizedBox(height: 24),

                  // Name
                  Text(
                    user.name,
                    style: Theme.of(context).textTheme.headlineMedium,
                  ),
                  const SizedBox(height: 8),

                  // Email
                  Text(
                    user.email,
                    style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                      color: Colors.grey,
                    ),
                  ),
                  const SizedBox(height: 32),

                  // Info cards
                  Card(
                    child: ListTile(
                      leading: const Icon(Icons.badge),
                      title: const Text('User ID'),
                      subtitle: Text(user.id),
                    ),
                  ),
                  Card(
                    child: ListTile(
                      leading: const Icon(Icons.verified_user),
                      title: const Text('Account Status'),
                      subtitle: const Text('Active'),
                      trailing: Icon(
                        Icons.check_circle,
                        color: Colors.green.shade600,
                      ),
                    ),
                  ),
                  Card(
                    child: ListTile(
                      leading: const Icon(Icons.security),
                      title: const Text('Authentication'),
                      subtitle: const Text('JWT Token Active'),
                      trailing: Icon(
                        Icons.lock,
                        color: Colors.blue.shade600,
                      ),
                    ),
                  ),

                  const SizedBox(height: 32),

                  // Logout button
                  OutlinedButton.icon(
                    onPressed: _handleLogout,
                    icon: const Icon(Icons.logout),
                    label: const Text('Logout'),
                    style: OutlinedButton.styleFrom(
                      foregroundColor: Colors.red,
                      side: const BorderSide(color: Colors.red),
                      padding: const EdgeInsets.symmetric(
                        horizontal: 32,
                        vertical: 12,
                      ),
                    ),
                  ),
                ],
              ),
            );
          }

          return const Center(child: Text('No data'));
        },
      ),
    );
  }
}
```

### 5. Update Main App with Auth Check

```dart
// lib/main.dart
import 'package:flutter/material.dart';
import 'services/auth_service.dart';
import 'screens/login_screen.dart';
import 'screens/profile_screen.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Auth Demo',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.blue),
        useMaterial3: true,
      ),
      home: const AuthCheck(),
    );
  }
}

// Check if user is already logged in
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
    _checkLoginStatus();
  }

  Future<void> _checkLoginStatus() async {
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
        body: Center(
          child: CircularProgressIndicator(),
        ),
      );
    }

    return _isLoggedIn ? const ProfileScreen() : const LoginScreen();
  }
}
```

---

## Advanced: Attaching Custom Headers to All Requests

For a real app, you'll want to create a **custom HTTP client** that automatically adds headers to every request:

```dart
// lib/services/api_client.dart
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'dart:convert';

class ApiClient {
  final String baseUrl;
  final storage = const FlutterSecureStorage();

  ApiClient({required this.baseUrl});

  // Helper method to get headers with auth token
  Future<Map<String, String>> _getHeaders() async {
    final token = await storage.read(key: 'auth_token');

    final headers = {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    };

    if (token != null) {
      headers['Authorization'] = 'Bearer $token';
    }

    return headers;
  }

  // GET request
  Future<http.Response> get(String endpoint) async {
    final headers = await _getHeaders();
    return http.get(
      Uri.parse('$baseUrl$endpoint'),
      headers: headers,
    ).timeout(const Duration(seconds: 10));
  }

  // POST request
  Future<http.Response> post(String endpoint, Map<String, dynamic> body) async {
    final headers = await _getHeaders();
    return http.post(
      Uri.parse('$baseUrl$endpoint'),
      headers: headers,
      body: jsonEncode(body),
    ).timeout(const Duration(seconds: 10));
  }

  // PUT request
  Future<http.Response> put(String endpoint, Map<String, dynamic> body) async {
    final headers = await _getHeaders();
    return http.put(
      Uri.parse('$baseUrl$endpoint'),
      headers: headers,
      body: jsonEncode(body),
    ).timeout(const Duration(seconds: 10));
  }

  // DELETE request
  Future<http.Response> delete(String endpoint) async {
    final headers = await _getHeaders();
    return http.delete(
      Uri.parse('$baseUrl$endpoint'),
      headers: headers,
    ).timeout(const Duration(seconds: 10));
  }
}

// Usage:
// final apiClient = ApiClient(baseUrl: 'https://api.example.com');
// final response = await apiClient.get('/profile');
// No need to manually add auth headers every time!
```

---

## Handling Token Expiration

JWT tokens expire after a certain time (usually 15 minutes to 24 hours). Here's how to handle expiration gracefully:

```dart
class AuthService {
  // ... previous code ...

  Future<T> makeAuthenticatedRequest<T>(
    Future<http.Response> Function() request,
    T Function(Map<String, dynamic>) fromJson,
  ) async {
    try {
      final response = await request();

      if (response.statusCode == 200) {
        return fromJson(jsonDecode(response.body));
      } else if (response.statusCode == 401) {
        // Token expired or invalid
        await logout();
        throw Exception('Your session has expired. Please login again.');
      } else {
        throw Exception('Request failed with status ${response.statusCode}');
      }
    } catch (e) {
      rethrow;
    }
  }

  // Usage:
  Future<User> getProfile() async {
    final token = await storage.read(key: 'auth_token');
    if (token == null) throw Exception('Not logged in');

    return makeAuthenticatedRequest(
      () => http.get(
        Uri.parse('$baseUrl/profile'),
        headers: {
          'Authorization': 'Bearer $token',
          'Content-Type': 'application/json',
        },
      ),
      (json) => User.fromJson(json),
    );
  }
}
```

---

## Security Best Practices

### ✅ DO:
1. **Always use HTTPS** (not HTTP) for authentication endpoints
2. **Store tokens in flutter_secure_storage** (not SharedPreferences)
3. **Clear tokens on logout**
4. **Handle 401 responses** by logging user out
5. **Set appropriate timeouts** (10 seconds is reasonable)
6. **Validate token format** before sending (basic check)

### ❌ DON'T:
1. **Never log tokens** to console in production
2. **Never store passwords** (only store tokens)
3. **Never send tokens in URL query parameters** (use headers)
4. **Never share tokens** between different users
5. **Never ignore HTTPS certificate errors** in production

```dart
// ✅ Good: Token in header
headers: {'Authorization': 'Bearer $token'}

// ❌ Bad: Token in URL (visible in logs!)
Uri.parse('https://api.example.com/profile?token=$token')
```

---

## Testing with a Real API

You can test authentication with **JSONPlaceholder's** auth simulation or **ReqRes**:

### ReqRes (https://reqres.in)

```dart
// Login endpoint
POST https://reqres.in/api/login
Body: {"email": "eve.holt@reqres.in", "password": "cityslicka"}
Response: {"token": "QpwL5tke4Pnpja7X4"}

// Then use token for authenticated requests
GET https://reqres.in/api/users/2
Headers: {"Authorization": "Bearer QpwL5tke4Pnpja7X4"}
```

Try building a login flow with ReqRes to practice!

---

## Common Errors and Solutions

### Error: "401 Unauthorized"
**Cause**: Token missing, expired, or invalid
**Solution**: Check if token exists, verify format, handle by re-authenticating

### Error: "Invalid token format"
**Cause**: Token not prefixed with "Bearer " or malformed
**Solution**: Ensure format is exactly `'Bearer $token'` (with space!)

### Error: "Failed to parse header value"
**Cause**: Special characters or newlines in token
**Solution**: Trim token: `token.trim()`

### Error: "Token null after storage"
**Cause**: Forgot to await storage.write()
**Solution**: Always use `await` when storing/reading tokens

---

## Quiz Time! 🧠

Test your understanding:

### Question 1
What is the correct format for sending a JWT token in the Authorization header?

A) `'Authorization': 'JWT $token'`
B) `'Authorization': '$token'`
C) `'Authorization': 'Bearer $token'`
D) `'Bearer': '$token'`

### Question 2
Where should you store authentication tokens in Flutter?

A) In a regular String variable
B) In SharedPreferences
C) In flutter_secure_storage
D) In a text file

### Question 3
What HTTP status code indicates that your token is expired or invalid?

A) 200 (OK)
B) 400 (Bad Request)
C) 401 (Unauthorized)
D) 404 (Not Found)

---

## Answer Key

### Answer 1: C
**Correct**: `'Authorization': 'Bearer $token'`

The standard format is "Bearer" followed by a space and then the token. "Bearer" must be capitalized.

### Answer 2: C
**Correct**: In flutter_secure_storage

flutter_secure_storage uses platform-specific encryption (Keychain on iOS, KeyStore on Android) to securely store sensitive data like tokens. SharedPreferences is not encrypted and regular variables don't persist.

### Answer 3: C
**Correct**: 401 (Unauthorized)

401 Unauthorized means the server couldn't verify your identity (invalid/expired token). This is your cue to log the user out and ask them to login again.

---

## What's Next?

You've learned how to authenticate users and send secure requests with custom headers. In the next lesson, we'll explore the **Dio package** - a powerful alternative to the http package with built-in interceptors, automatic token refresh, and better error handling!

**Coming up in Lesson 5: Dio Package (Advanced HTTP Client)**
- Why Dio is better for complex apps
- Interceptors (automatic header injection)
- Automatic retry logic
- Download/upload progress tracking
- Much more!

---

## Key Takeaways

✅ Authentication tokens are like VIP wristbands - prove who you are once, then show the wristband each time
✅ Use flutter_secure_storage to store tokens securely (encrypted)
✅ Send tokens in the Authorization header: `'Authorization': 'Bearer $token'`
✅ Always handle 401 (Unauthorized) by logging user out
✅ JWT tokens have three parts (header.payload.signature) but you don't need to create them
✅ Use HTTPS for all authentication endpoints
✅ Create a custom ApiClient to automatically add headers to all requests

**You're now ready to build secure, authenticated apps!** 🎉
