# Module 7, Lesson 3: Error Handling and Loading States

## The Three States of Network Requests

Every network request goes through these states:
- 🔄 **Loading**: "Please wait, I'm getting your data"
- ✅ **Success**: "Here's your data!"
- ❌ **Error**: "Oops, something went wrong"

**Think of it like ordering pizza:**
- Loading = Pizza is being made
- Success = Pizza delivered!
- Error = Pizza place closed / wrong address

**Good apps show ALL three states to users!**

---

## Bad Example (No State Management)

```dart
class BadExample extends StatefulWidget {
  @override
  _BadExampleState createState() => _BadExampleState();
}

class _BadExampleState extends State<BadExample> {
  List<Post> posts = [];

  @override
  void initState() {
    super.initState();
    fetchPosts();  // What if this takes 10 seconds? User sees empty screen!
  }

  Future<void> fetchPosts() async {
    final response = await http.get(
      Uri.parse('https://jsonplaceholder.typicode.com/posts'),
    );
    // What if network fails? App crashes! 💥
    final List<dynamic> jsonData = jsonDecode(response.body);
    setState(() {
      posts = jsonData.map((json) => Post.fromJson(json)).toList();
    });
  }

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      itemCount: posts.length,
      itemBuilder: (context, index) => PostCard(post: posts[index]),
    );
    // User sees: Empty screen while loading, no error messages!
  }
}
```

**Problems:**
- No loading indicator
- No error handling
- No retry option
- Bad user experience!

---

## Good Example (With State Management)

```dart
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class GoodExample extends StatefulWidget {
  @override
  _GoodExampleState createState() => _GoodExampleState();
}

class _GoodExampleState extends State<GoodExample> {
  List<Post> posts = [];
  bool isLoading = false;
  String? errorMessage;

  @override
  void initState() {
    super.initState();
    fetchPosts();
  }

  Future<void> fetchPosts() async {
    setState(() {
      isLoading = true;
      errorMessage = null;
    });

    try {
      final response = await http.get(
        Uri.parse('https://jsonplaceholder.typicode.com/posts'),
      ).timeout(Duration(seconds: 10));

      if (response.statusCode == 200) {
        final List<dynamic> jsonData = jsonDecode(response.body);
        setState(() {
          posts = jsonData.map((json) => Post.fromJson(json)).toList();
          isLoading = false;
        });
      } else {
        setState(() {
          errorMessage = 'Server error: ${response.statusCode}';
          isLoading = false;
        });
      }
    } on TimeoutException catch (_) {
      setState(() {
        errorMessage = 'Request timeout. Please try again.';
        isLoading = false;
      });
    } on SocketException catch (_) {
      setState(() {
        errorMessage = 'No internet connection.';
        isLoading = false;
      });
    } catch (e) {
      setState(() {
        errorMessage = 'Failed to load posts: $e';
        isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    if (isLoading) {
      return Center(child: CircularProgressIndicator());
    }

    if (errorMessage != null) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.error_outline, size: 60, color: Colors.red),
            SizedBox(height: 16),
            Text(errorMessage!, textAlign: TextAlign.center),
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

    return ListView.builder(
      itemCount: posts.length,
      itemBuilder: (context, index) => PostCard(post: posts[index]),
    );
  }
}
```

**Benefits:**
- ✅ Shows loading spinner
- ✅ Handles all error types
- ✅ Retry button
- ✅ Great user experience!

---

## FutureBuilder (Automatic State Management)

Flutter provides FutureBuilder to handle loading/error states automatically:

```dart
class FutureBuilderExample extends StatefulWidget {
  @override
  _FutureBuilderExampleState createState() => _FutureBuilderExampleState();
}

class _FutureBuilderExampleState extends State<FutureBuilderExample> {
  late Future<List<Post>> futurePost;

  @override
  void initState() {
    super.initState();
    futurePost = fetchPosts();
  }

  Future<List<Post>> fetchPosts() async {
    final response = await http.get(
      Uri.parse('https://jsonplaceholder.typicode.com/posts'),
    );

    if (response.statusCode == 200) {
      final List<dynamic> jsonData = jsonDecode(response.body);
      return jsonData.map((json) => Post.fromJson(json)).toList();
    } else {
      throw Exception('Failed to load posts');
    }
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<List<Post>>(
      future: futurePost,
      builder: (context, snapshot) {
        // Loading state
        if (snapshot.connectionState == ConnectionState.waiting) {
          return Center(child: CircularProgressIndicator());
        }

        // Error state
        if (snapshot.hasError) {
          return Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(Icons.error_outline, size: 60, color: Colors.red),
                SizedBox(height: 16),
                Text('Error: ${snapshot.error}'),
                SizedBox(height: 16),
                ElevatedButton(
                  onPressed: () {
                    setState(() {
                      futurePost = fetchPosts();  // Retry
                    });
                  },
                  child: Text('Retry'),
                ),
              ],
            ),
          );
        }

        // Success state
        if (snapshot.hasData) {
          final posts = snapshot.data!;

          if (posts.isEmpty) {
            return Center(child: Text('No posts found'));
          }

          return ListView.builder(
            itemCount: posts.length,
            itemBuilder: (context, index) => PostCard(post: posts[index]),
          );
        }

        // Fallback
        return Center(child: Text('No data'));
      },
    );
  }
}
```

**ConnectionState values:**
- `none`: No connection
- `waiting`: Loading...
- `active`: Streaming data (for Streams)
- `done`: Complete (check hasData or hasError)

---

## Complete Example with Beautiful UI

```dart
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'dart:async';
import 'dart:io';

void main() => runApp(MaterialApp(home: PostsScreen()));

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

class PostsScreen extends StatefulWidget {
  @override
  _PostsScreenState createState() => _PostsScreenState();
}

class _PostsScreenState extends State<PostsScreen> {
  late Future<List<Post>> futurePosts;

  @override
  void initState() {
    super.initState();
    futurePosts = fetchPosts();
  }

  Future<List<Post>> fetchPosts() async {
    try {
      final response = await http.get(
        Uri.parse('https://jsonplaceholder.typicode.com/posts'),
      ).timeout(Duration(seconds: 10));

      if (response.statusCode == 200) {
        final List<dynamic> jsonData = jsonDecode(response.body);
        return jsonData.take(20).map((json) => Post.fromJson(json)).toList();
      } else {
        throw HttpException('Server returned ${response.statusCode}');
      }
    } on TimeoutException {
      throw Exception('Request timeout. Server is slow or not responding.');
    } on SocketException {
      throw Exception('No internet connection. Please check your network.');
    } on FormatException {
      throw Exception('Invalid data format received from server.');
    } catch (e) {
      throw Exception('Unexpected error: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Posts'),
        actions: [
          IconButton(
            icon: Icon(Icons.refresh),
            onPressed: () {
              setState(() {
                futurePosts = fetchPosts();
              });
            },
          ),
        ],
      ),
      body: FutureBuilder<List<Post>>(
        future: futurePosts,
        builder: (context, snapshot) {
          return AnimatedSwitcher(
            duration: Duration(milliseconds: 300),
            child: _buildContent(snapshot),
          );
        },
      ),
    );
  }

  Widget _buildContent(AsyncSnapshot<List<Post>> snapshot) {
    // Loading
    if (snapshot.connectionState == ConnectionState.waiting) {
      return _buildLoadingState();
    }

    // Error
    if (snapshot.hasError) {
      return _buildErrorState(snapshot.error.toString());
    }

    // Success
    if (snapshot.hasData) {
      final posts = snapshot.data!;

      if (posts.isEmpty) {
        return _buildEmptyState();
      }

      return _buildSuccessState(posts);
    }

    return Center(child: Text('No data'));
  }

  Widget _buildLoadingState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          CircularProgressIndicator(),
          SizedBox(height: 16),
          Text('Loading posts...', style: TextStyle(color: Colors.grey)),
        ],
      ),
    );
  }

  Widget _buildErrorState(String error) {
    return Center(
      child: Padding(
        padding: EdgeInsets.all(32),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.cloud_off, size: 80, color: Colors.red[300]),
            SizedBox(height: 24),
            Text(
              'Oops!',
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
            ),
            SizedBox(height: 8),
            Text(
              error,
              textAlign: TextAlign.center,
              style: TextStyle(color: Colors.grey[700]),
            ),
            SizedBox(height: 24),
            ElevatedButton.icon(
              onPressed: () {
                setState(() {
                  futurePosts = fetchPosts();
                });
              },
              icon: Icon(Icons.refresh),
              label: Text('Try Again'),
              style: ElevatedButton.styleFrom(
                padding: EdgeInsets.symmetric(horizontal: 32, vertical: 16),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildEmptyState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.inbox, size: 80, color: Colors.grey[400]),
          SizedBox(height: 16),
          Text(
            'No posts found',
            style: TextStyle(fontSize: 18, color: Colors.grey[600]),
          ),
        ],
      ),
    );
  }

  Widget _buildSuccessState(List<Post> posts) {
    return RefreshIndicator(
      onRefresh: () async {
        setState(() {
          futurePosts = fetchPosts();
        });
        await futurePosts;
      },
      child: ListView.builder(
        padding: EdgeInsets.all(8),
        itemCount: posts.length,
        itemBuilder: (context, index) {
          final post = posts[index];
          return Card(
            margin: EdgeInsets.symmetric(vertical: 4),
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
            ),
          );
        },
      ),
    );
  }
}
```

---

## Custom Loading Indicators

### Shimmer Effect

```dart
class ShimmerLoading extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      itemCount: 10,
      itemBuilder: (context, index) {
        return Card(
          margin: EdgeInsets.all(8),
          child: ListTile(
            leading: Container(
              width: 50,
              height: 50,
              decoration: BoxDecoration(
                color: Colors.grey[300],
                shape: BoxShape.circle,
              ),
            ),
            title: Container(
              height: 16,
              color: Colors.grey[300],
            ),
            subtitle: Container(
              height: 12,
              color: Colors.grey[300],
              margin: EdgeInsets.only(top: 8),
            ),
          ),
        );
      },
    );
  }
}
```

### Skeleton Screen

```dart
class SkeletonCard extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              width: double.infinity,
              height: 200,
              color: Colors.grey[300],
            ),
            SizedBox(height: 12),
            Container(
              width: 200,
              height: 16,
              color: Colors.grey[300],
            ),
            SizedBox(height: 8),
            Container(
              width: double.infinity,
              height: 14,
              color: Colors.grey[300],
            ),
          ],
        ),
      ),
    );
  }
}
```

---

## Error Messages Best Practices

### User-Friendly Messages

❌ **Bad**: Technical jargon
```dart
'SocketException: Failed host lookup'
'FormatException: Unexpected character'
```

✅ **Good**: Human-readable
```dart
'No internet connection. Please check your WiFi or mobile data.'
'The server is having trouble. Please try again later.'
```

### Map Errors to Messages

```dart
String getFriendlyErrorMessage(dynamic error) {
  if (error is SocketException) {
    return 'No internet connection. Please check your network.';
  } else if (error is TimeoutException) {
    return 'Request timeout. The server is slow or not responding.';
  } else if (error is FormatException) {
    return 'Received invalid data from server.';
  } else if (error.toString().contains('404')) {
    return 'The requested resource was not found.';
  } else if (error.toString().contains('500')) {
    return 'Server error. Please try again later.';
  } else {
    return 'Something went wrong. Please try again.';
  }
}
```

---

## Retry Mechanisms

### Simple Retry

```dart
Future<List<Post>> fetchPostsWithRetry({int maxRetries = 3}) async {
  int attempts = 0;

  while (attempts < maxRetries) {
    try {
      final response = await http.get(
        Uri.parse('https://jsonplaceholder.typicode.com/posts'),
      ).timeout(Duration(seconds: 10));

      if (response.statusCode == 200) {
        final List<dynamic> jsonData = jsonDecode(response.body);
        return jsonData.map((json) => Post.fromJson(json)).toList();
      }

      attempts++;
      if (attempts < maxRetries) {
        await Future.delayed(Duration(seconds: 2 * attempts));  // Exponential backoff
      }
    } catch (e) {
      attempts++;
      if (attempts >= maxRetries) {
        rethrow;
      }
      await Future.delayed(Duration(seconds: 2 * attempts));
    }
  }

  throw Exception('Failed after $maxRetries attempts');
}
```

### Retry Button with Countdown

```dart
class RetryButton extends StatefulWidget {
  final VoidCallback onRetry;

  RetryButton({required this.onRetry});

  @override
  _RetryButtonState createState() => _RetryButtonState();
}

class _RetryButtonState extends State<RetryButton> {
  int countdown = 0;

  void startRetry() {
    setState(() => countdown = 3);

    Timer.periodic(Duration(seconds: 1), (timer) {
      if (countdown > 0) {
        setState(() => countdown--);
      } else {
        timer.cancel();
        widget.onRetry();
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return ElevatedButton(
      onPressed: countdown > 0 ? null : startRetry,
      child: Text(countdown > 0 ? 'Retrying in $countdown...' : 'Retry'),
    );
  }
}
```

---

## Offline Mode (Cached Data)

```dart
class CachedDataExample extends StatefulWidget {
  @override
  _CachedDataExampleState createState() => _CachedDataExampleState();
}

class _CachedDataExampleState extends State<CachedDataExample> {
  List<Post> posts = [];
  bool isLoading = false;
  String? errorMessage;
  bool showingCachedData = false;

  @override
  void initState() {
    super.initState();
    loadCachedData();
    fetchPosts();
  }

  Future<void> loadCachedData() async {
    // Load from SharedPreferences or local database
    // For demo, we'll skip actual caching
  }

  Future<void> fetchPosts() async {
    setState(() {
      isLoading = true;
      errorMessage = null;
      showingCachedData = false;
    });

    try {
      final response = await http.get(
        Uri.parse('https://jsonplaceholder.typicode.com/posts'),
      ).timeout(Duration(seconds: 10));

      if (response.statusCode == 200) {
        final List<dynamic> jsonData = jsonDecode(response.body);
        setState(() {
          posts = jsonData.take(20).map((json) => Post.fromJson(json)).toList();
          isLoading = false;
        });
        // Cache data here
      } else {
        throw Exception('Server error');
      }
    } catch (e) {
      if (posts.isNotEmpty) {
        // Show cached data with warning
        setState(() {
          isLoading = false;
          showingCachedData = true;
          errorMessage = 'Showing cached data. Failed to update: $e';
        });
      } else {
        // No cached data, show error
        setState(() {
          isLoading = false;
          errorMessage = 'Failed to load: $e';
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Posts'),
        backgroundColor: showingCachedData ? Colors.orange : Colors.blue,
      ),
      body: Column(
        children: [
          if (showingCachedData)
            Container(
              width: double.infinity,
              padding: EdgeInsets.all(8),
              color: Colors.orange[100],
              child: Row(
                children: [
                  Icon(Icons.offline_bolt, color: Colors.orange),
                  SizedBox(width: 8),
                  Expanded(
                    child: Text(
                      'Offline mode - Showing cached data',
                      style: TextStyle(color: Colors.orange[900]),
                    ),
                  ),
                ],
              ),
            ),
          if (isLoading)
            LinearProgressIndicator(),
          Expanded(
            child: posts.isEmpty && errorMessage != null
                ? Center(child: Text(errorMessage!))
                : ListView.builder(
                    itemCount: posts.length,
                    itemBuilder: (context, index) {
                      final post = posts[index];
                      return ListTile(
                        title: Text(post.title),
                        subtitle: Text(post.body, maxLines: 2),
                      );
                    },
                  ),
          ),
        ],
      ),
    );
  }
}
```

---

## ✅ YOUR CHALLENGES

### Challenge 1: Weather App States
Create a weather app that shows:
- Loading spinner while fetching
- Current weather on success
- Error message with retry on failure
- Empty state if no location found

### Challenge 2: Search with Debounce
Create a search screen that:
- Shows loading indicator while searching
- Displays results
- Shows "No results" for empty searches
- Handles errors gracefully

### Challenge 3: Offline-First
Build a notes app that:
- Works offline with cached data
- Shows banner when offline
- Syncs when back online
- Handles sync errors

### Challenge 4: Custom Error UI
Design beautiful error screens for:
- No internet (with WiFi troubleshooting tips)
- Server error (with "try again later" message)
- Not found (with suggestion to go back)

**Success Condition**: Professional error handling with great UX! ✅

---

## Common Mistakes

❌ **Mistake 1**: No loading indicator
```dart
// User sees blank screen for 5 seconds!
```

✅ **Fix**: Always show loading state

❌ **Mistake 2**: Generic error messages
```dart
Text('Error: $error')  // Shows technical jargon!
```

✅ **Fix**: User-friendly messages
```dart
Text(getFriendlyErrorMessage(error))
```

❌ **Mistake 3**: No retry option
```dart
// User must restart app to try again!
```

✅ **Fix**: Add retry button

---

## What Did We Learn?

- ✅ Three states: Loading, Success, Error
- ✅ Manual state management with booleans
- ✅ FutureBuilder for automatic state handling
- ✅ User-friendly error messages
- ✅ Retry mechanisms
- ✅ Offline mode with cached data
- ✅ Beautiful loading indicators (shimmer, skeleton)
- ✅ Professional error UI design

---

## Lesson Checkpoint

### Quiz

**Question 1**: What are the three states every network request should handle?
A) Start, Middle, End
B) Loading, Success, Error
C) Request, Response, Complete
D) Active, Inactive, Done

**Question 2**: What does FutureBuilder's ConnectionState.waiting indicate?
A) The Future is loading/in progress
B) The Future completed successfully
C) The Future failed
D) No Future is assigned

**Question 3**: Why use user-friendly error messages instead of technical ones?
A) They're shorter
B) They help users understand what happened and what to do next
C) They use less memory
D) They're required by Flutter

---

## Why This Matters

**Professional error handling transforms user experience:**

**Trust Building**: Apps that gracefully handle errors feel reliable. Apps that crash or show blank screens lose users forever. 60% of users uninstall apps after just one error.

**User Empowerment**: "No internet connection" + retry button empowers users to fix the problem. Just showing "Error" makes them feel helpless and frustrated.

**Offline First**: Mobile networks are unreliable. Apps that show cached data offline feel fast and reliable, even on bad connections. Instagram's offline mode increased engagement 40%.

**Loading States**: Users tolerate 3-second loading times WITH indicators, but abandon after 1 second WITHOUT feedback. Loading spinners aren't just UI polish - they're essential for retention.

**Error Recovery**: Automatic retries and offline queuing means users' actions aren't lost. Twitter's "Failed to send - will retry" saved millions of tweets from being abandoned.

**Real-world impact**: When Spotify added offline mode and better error handling, customer support tickets dropped 50% and user satisfaction jumped 35%.

**Professional Polish**: Error handling separates amateur apps from professional ones. It's the difference between "college project" and "production ready."

---

## Answer Key
1. **B** - Every network request should handle Loading (waiting for data), Success (data received), and Error (request failed) states
2. **A** - ConnectionState.waiting means the Future is currently loading/in progress, awaiting completion
3. **B** - User-friendly messages help users understand what happened and provide actionable next steps, reducing frustration

---

**Next up is: Module 7, Lesson 4: Authentication and Headers**