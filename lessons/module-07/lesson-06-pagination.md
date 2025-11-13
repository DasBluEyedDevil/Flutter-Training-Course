# Module 7, Lesson 6: Pagination and Infinite Scroll

## What You'll Learn
By the end of this lesson, you'll understand how to efficiently load large datasets using pagination, implement infinite scroll, and create smooth loading experiences like Instagram, Twitter, and Reddit.

---

## Why This Matters

**Pagination is critical for app performance and user experience.**

- **Loading 10,000 items at once** would crash most phones
- **Apps with pagination feel 10x faster** than apps without
- **Instagram, Twitter, TikTok** - all use infinite scroll
- **Reduces server costs** by loading only what users need
- **Better battery life** (less data = less power consumption)

In this lesson, you'll learn the same techniques used by every major social media and content app.

---

## Real-World Analogy: The Library

### Without Pagination ❌ (The Bad Librarian)
Imagine asking a librarian for "books about cooking":
- 📚 They bring you **ALL 5,000 cooking books** at once
- 🏋️ You can't carry them all
- ⏰ It takes 30 minutes to bring them all
- 😵 You only wanted to browse a few books
- 💸 The library spent huge effort on books you won't read

**This is what happens when you load all data at once.**

### With Pagination ✅ (The Smart Librarian)
Instead, the smart librarian:
- 📖 Brings you **20 books** to start
- 👀 You browse them while sitting comfortably
- 🔄 When you're done, they bring **20 more**
- ⚡ Each delivery is fast (seconds, not minutes)
- 😊 You can stop whenever you want
- 💰 The library only moves books you actually look at

**This is pagination - loading data in small chunks.**

---

## Types of Pagination

### 1. Offset-Based Pagination (Simple)
"Give me items 0-19, then 20-39, then 40-59..."

```
Page 1: Items 0-19   (offset=0,  limit=20)
Page 2: Items 20-39  (offset=20, limit=20)
Page 3: Items 40-59  (offset=40, limit=20)
```

**API Request**:
```
GET /posts?page=1&limit=20
GET /posts?page=2&limit=20
GET /posts?offset=0&limit=20
GET /posts?offset=20&limit=20
```

**Pros**: Simple to implement
**Cons**: Can have issues with real-time data (items moving)

### 2. Cursor-Based Pagination (Advanced)
"Give me items after cursor ABC123..."

```
Page 1: Items 1-20    (cursor=null, limit=20)    → Returns cursor="ABC123"
Page 2: Items 21-40   (cursor="ABC123", limit=20) → Returns cursor="XYZ789"
Page 3: Items 41-60   (cursor="XYZ789", limit=20) → Returns cursor="DEF456"
```

**API Request**:
```
GET /posts?limit=20
GET /posts?cursor=ABC123&limit=20
GET /posts?cursor=XYZ789&limit=20
```

**Pros**: Works perfectly with real-time data
**Cons**: Slightly more complex

---

## Infinite Scroll Pattern

**Infinite Scroll** = Automatically load more data when user scrolls near the bottom.

### The Flow

```
1. User opens app
   ↓
2. Load first page (20 items)
   ↓
3. User scrolls down
   ↓
4. When near bottom → Load next page
   ↓
5. Add new items to list
   ↓
6. Repeat steps 3-5 infinitely
```

---

## Basic Pagination Example

Let's start with simple offset-based pagination:

```dart
// lib/models/post.dart
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
```

```dart
// lib/services/posts_service.dart
import 'package:dio/dio.dart';
import '../models/post.dart';

class PostsService {
  final Dio _dio = Dio(
    BaseOptions(baseUrl: 'https://jsonplaceholder.typicode.com'),
  );

  // Fetch posts with pagination
  Future<List<Post>> getPosts({required int page, int limit = 20}) async {
    try {
      // JSONPlaceholder uses _page and _limit
      final response = await _dio.get(
        '/posts',
        queryParameters: {
          '_page': page,
          '_limit': limit,
        },
      );

      final List<dynamic> data = response.data;
      return data.map((json) => Post.fromJson(json)).toList();
    } catch (e) {
      throw Exception('Failed to load posts: $e');
    }
  }
}
```

---

## Infinite Scroll Implementation

### Approach 1: ScrollController (Manual Detection)

```dart
// lib/screens/posts_screen.dart
import 'package:flutter/material.dart';
import '../services/posts_service.dart';
import '../models/post.dart';

class PostsScreen extends StatefulWidget {
  const PostsScreen({super.key});

  @override
  State<PostsScreen> createState() => _PostsScreenState();
}

class _PostsScreenState extends State<PostsScreen> {
  final _postsService = PostsService();
  final _scrollController = ScrollController();
  final List<Post> _posts = [];

  int _currentPage = 1;
  bool _isLoadingMore = false;
  bool _hasMoreData = true;

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
    final posts = await _postsService.getPosts(page: _currentPage);
    setState(() {
      _posts.addAll(posts);
    });
  }

  void _onScroll() {
    // Check if user scrolled near the bottom (within 200 pixels)
    if (_scrollController.position.pixels >=
        _scrollController.position.maxScrollExtent - 200) {
      _loadMorePosts();
    }
  }

  Future<void> _loadMorePosts() async {
    // Prevent multiple simultaneous loads
    if (_isLoadingMore || !_hasMoreData) return;

    setState(() {
      _isLoadingMore = true;
    });

    try {
      _currentPage++;
      final newPosts = await _postsService.getPosts(page: _currentPage);

      setState(() {
        if (newPosts.isEmpty) {
          _hasMoreData = false; // No more data available
        } else {
          _posts.addAll(newPosts);
        }
        _isLoadingMore = false;
      });
    } catch (e) {
      setState(() {
        _isLoadingMore = false;
        _currentPage--; // Revert page number on error
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Infinite Scroll Posts'),
      ),
      body: _posts.isEmpty
          ? const Center(child: CircularProgressIndicator())
          : ListView.builder(
              controller: _scrollController,
              padding: const EdgeInsets.all(16),
              itemCount: _posts.length + (_hasMoreData ? 1 : 0),
              itemBuilder: (context, index) {
                // Show loading indicator at the bottom
                if (index == _posts.length) {
                  return const Center(
                    child: Padding(
                      padding: EdgeInsets.all(16.0),
                      child: CircularProgressIndicator(),
                    ),
                  );
                }

                final post = _posts[index];
                return Card(
                  margin: const EdgeInsets.only(bottom: 12),
                  child: ListTile(
                    leading: CircleAvatar(
                      child: Text(post.id.toString()),
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
                  ),
                );
              },
            ),
    );
  }
}
```

---

## Enhanced Version with Pull-to-Refresh

Let's add the ability to refresh by pulling down:

```dart
// lib/screens/posts_screen_enhanced.dart
import 'package:flutter/material.dart';
import '../services/posts_service.dart';
import '../models/post.dart';

class PostsScreenEnhanced extends StatefulWidget {
  const PostsScreenEnhanced({super.key});

  @override
  State<PostsScreenEnhanced> createState() => _PostsScreenEnhancedState();
}

class _PostsScreenEnhancedState extends State<PostsScreenEnhanced> {
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
      final posts = await _postsService.getPosts(page: 1);
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
      final newPosts = await _postsService.getPosts(page: _currentPage);

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

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Failed to load more: $e')),
        );
      }
    }
  }

  Future<void> _onRefresh() async {
    await _loadInitialPosts();
  }

  @override
  Widget build(BuildContext context) {
    // Initial loading state
    if (_isLoadingInitial && _posts.isEmpty) {
      return Scaffold(
        appBar: AppBar(title: const Text('Posts')),
        body: const Center(child: CircularProgressIndicator()),
      );
    }

    // Error state
    if (_errorMessage != null && _posts.isEmpty) {
      return Scaffold(
        appBar: AppBar(title: const Text('Posts')),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Icon(Icons.error_outline, size: 64, color: Colors.red),
              const SizedBox(height: 16),
              Text(
                _errorMessage!,
                textAlign: TextAlign.center,
                style: const TextStyle(color: Colors.red),
              ),
              const SizedBox(height: 16),
              FilledButton.icon(
                onPressed: _loadInitialPosts,
                icon: const Icon(Icons.refresh),
                label: const Text('Try Again'),
              ),
            ],
          ),
        ),
      );
    }

    // Success state with data
    return Scaffold(
      appBar: AppBar(
        title: Text('Posts (${_posts.length})'),
      ),
      body: RefreshIndicator(
        onRefresh: _onRefresh,
        child: ListView.builder(
          controller: _scrollController,
          padding: const EdgeInsets.all(16),
          itemCount: _posts.length + 1, // +1 for loading indicator or end message
          itemBuilder: (context, index) {
            // Loading indicator or "End of list" message
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
                      '🎉 You\'ve reached the end!',
                      style: TextStyle(
                        color: Colors.grey.shade600,
                        fontSize: 16,
                      ),
                    ),
                  ),
                );
              } else {
                return const SizedBox.shrink();
              }
            }

            final post = _posts[index];
            return Card(
              margin: const EdgeInsets.only(bottom: 12),
              child: ListTile(
                leading: CircleAvatar(
                  backgroundColor: Colors.blue.shade100,
                  child: Text(
                    post.id.toString(),
                    style: TextStyle(color: Colors.blue.shade900),
                  ),
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
                trailing: const Icon(Icons.arrow_forward_ios, size: 16),
                onTap: () {
                  // Navigate to detail screen
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(content: Text('Tapped post ${post.id}')),
                  );
                },
              ),
            );
          },
        ),
      ),
    );
  }
}
```

**Features**:
- ✅ Initial loading state
- ✅ Error handling with retry
- ✅ Infinite scroll
- ✅ Pull-to-refresh
- ✅ Loading indicator at bottom
- ✅ "End of list" message
- ✅ Item count in app bar

---

## Approach 2: Using a Package (infinite_scroll_pagination)

For production apps, consider using a battle-tested package:

```yaml
# pubspec.yaml
dependencies:
  infinite_scroll_pagination: ^4.0.0
```

```dart
// lib/screens/posts_screen_package.dart
import 'package:flutter/material.dart';
import 'package:infinite_scroll_pagination/infinite_scroll_pagination.dart';
import '../services/posts_service.dart';
import '../models/post.dart';

class PostsScreenPackage extends StatefulWidget {
  const PostsScreenPackage({super.key});

  @override
  State<PostsScreenPackage> createState() => _PostsScreenPackageState();
}

class _PostsScreenPackageState extends State<PostsScreenPackage> {
  final _postsService = PostsService();
  final PagingController<int, Post> _pagingController =
      PagingController(firstPageKey: 1);

  @override
  void initState() {
    super.initState();
    _pagingController.addPageRequestListener((pageKey) {
      _fetchPage(pageKey);
    });
  }

  Future<void> _fetchPage(int pageKey) async {
    try {
      final newPosts = await _postsService.getPosts(page: pageKey);
      final isLastPage = newPosts.length < 20; // Assuming 20 items per page

      if (isLastPage) {
        _pagingController.appendLastPage(newPosts);
      } else {
        final nextPageKey = pageKey + 1;
        _pagingController.appendPage(newPosts, nextPageKey);
      }
    } catch (error) {
      _pagingController.error = error;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Posts (Package)')),
      body: RefreshIndicator(
        onRefresh: () => Future.sync(() => _pagingController.refresh()),
        child: PagedListView<int, Post>(
          pagingController: _pagingController,
          padding: const EdgeInsets.all(16),
          builderDelegate: PagedChildBuilderDelegate<Post>(
            itemBuilder: (context, post, index) => Card(
              margin: const EdgeInsets.only(bottom: 12),
              child: ListTile(
                leading: CircleAvatar(child: Text(post.id.toString())),
                title: Text(
                  post.title,
                  style: const TextStyle(fontWeight: FontWeight.bold),
                ),
                subtitle: Text(
                  post.body,
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                ),
              ),
            ),
            firstPageErrorIndicatorBuilder: (context) => Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Icon(Icons.error_outline, size: 64, color: Colors.red),
                  const SizedBox(height: 16),
                  Text(
                    'Error: ${_pagingController.error}',
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 16),
                  FilledButton.icon(
                    onPressed: () => _pagingController.refresh(),
                    icon: const Icon(Icons.refresh),
                    label: const Text('Try Again'),
                  ),
                ],
              ),
            ),
            newPageErrorIndicatorBuilder: (context) => Padding(
              padding: const EdgeInsets.all(16.0),
              child: Center(
                child: FilledButton.icon(
                  onPressed: () => _pagingController.retryLastFailedRequest(),
                  icon: const Icon(Icons.refresh),
                  label: const Text('Retry'),
                ),
              ),
            ),
            noItemsFoundIndicatorBuilder: (context) => const Center(
              child: Text('No posts found'),
            ),
          ),
        ),
      ),
    );
  }

  @override
  void dispose() {
    _pagingController.dispose();
    super.dispose();
  }
}
```

**Benefits of the package**:
- ✅ Less boilerplate code
- ✅ Built-in error handling UI
- ✅ Automatic state management
- ✅ Well-tested and maintained

---

## Cursor-Based Pagination Example

For APIs that use cursors (like Twitter, Instagram):

```dart
// lib/services/cursor_posts_service.dart
import 'package:dio/dio.dart';

class PaginatedResponse {
  final List<dynamic> data;
  final String? nextCursor;
  final bool hasMore;

  PaginatedResponse({
    required this.data,
    this.nextCursor,
    required this.hasMore,
  });

  factory PaginatedResponse.fromJson(Map<String, dynamic> json) {
    return PaginatedResponse(
      data: json['data'] as List<dynamic>,
      nextCursor: json['next_cursor'],
      hasMore: json['has_more'] ?? false,
    );
  }
}

class CursorPostsService {
  final Dio _dio = Dio(
    BaseOptions(baseUrl: 'https://api.example.com'),
  );

  Future<PaginatedResponse> getPosts({String? cursor, int limit = 20}) async {
    final queryParams = {
      'limit': limit,
      if (cursor != null) 'cursor': cursor,
    };

    final response = await _dio.get('/posts', queryParameters: queryParams);
    return PaginatedResponse.fromJson(response.data);
  }
}
```

```dart
// lib/screens/cursor_posts_screen.dart
class CursorPostsScreen extends StatefulWidget {
  const CursorPostsScreen({super.key});

  @override
  State<CursorPostsScreen> createState() => _CursorPostsScreenState();
}

class _CursorPostsScreenState extends State<CursorPostsScreen> {
  final _service = CursorPostsService();
  final _scrollController = ScrollController();
  final List<dynamic> _posts = [];

  String? _nextCursor;
  bool _isLoadingMore = false;
  bool _hasMoreData = true;

  @override
  void initState() {
    super.initState();
    _loadInitialPosts();
    _scrollController.addListener(_onScroll);
  }

  Future<void> _loadInitialPosts() async {
    final response = await _service.getPosts();
    setState(() {
      _posts.addAll(response.data);
      _nextCursor = response.nextCursor;
      _hasMoreData = response.hasMore;
    });
  }

  void _onScroll() {
    if (_scrollController.position.pixels >=
        _scrollController.position.maxScrollExtent - 200) {
      _loadMorePosts();
    }
  }

  Future<void> _loadMorePosts() async {
    if (_isLoadingMore || !_hasMoreData) return;

    setState(() {
      _isLoadingMore = true;
    });

    try {
      final response = await _service.getPosts(cursor: _nextCursor);

      setState(() {
        _posts.addAll(response.data);
        _nextCursor = response.nextCursor;
        _hasMoreData = response.hasMore;
        _isLoadingMore = false;
      });
    } catch (e) {
      setState(() {
        _isLoadingMore = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    // Similar to previous examples...
    return Scaffold(
      appBar: AppBar(title: const Text('Cursor Pagination')),
      body: ListView.builder(
        controller: _scrollController,
        itemCount: _posts.length + (_hasMoreData ? 1 : 0),
        itemBuilder: (context, index) {
          if (index == _posts.length) {
            return const Center(child: CircularProgressIndicator());
          }
          // Build your item widget
          return ListTile(title: Text(_posts[index].toString()));
        },
      ),
    );
  }

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }
}
```

---

## Performance Optimization Tips

### 1. Use `ListView.builder` (Not `ListView`)

```dart
// ✅ Good: Lazy loading (only builds visible items)
ListView.builder(
  itemCount: _posts.length,
  itemBuilder: (context, index) => PostCard(post: _posts[index]),
)

// ❌ Bad: Builds ALL items at once (memory intensive)
ListView(
  children: _posts.map((post) => PostCard(post: post)).toList(),
)
```

### 2. Cache Images

```dart
// Use cached_network_image package
CachedNetworkImage(
  imageUrl: post.imageUrl,
  placeholder: (context, url) => CircularProgressIndicator(),
  errorWidget: (context, url, error) => Icon(Icons.error),
)
```

### 3. Throttle Scroll Events

```dart
// Don't load on every pixel scrolled
Timer? _scrollTimer;

void _onScroll() {
  _scrollTimer?.cancel();
  _scrollTimer = Timer(Duration(milliseconds: 300), () {
    if (_scrollController.position.pixels >=
        _scrollController.position.maxScrollExtent - 200) {
      _loadMorePosts();
    }
  });
}
```

---

## Best Practices

### ✅ DO:
1. **Show loading indicators** at the bottom while loading more
2. **Show "End of list" message** when no more data
3. **Implement pull-to-refresh** for better UX
4. **Use ListView.builder** for performance
5. **Handle errors gracefully** with retry options
6. **Prevent duplicate loads** with `_isLoadingMore` flag
7. **Show item count** in app bar (e.g., "Posts (125)")

### ❌ DON'T:
1. **Don't load all data at once** (defeats the purpose!)
2. **Don't trigger loading on every scroll pixel** (use threshold)
3. **Don't forget to dispose controllers** (memory leaks)
4. **Don't show errors silently** (users need feedback)
5. **Don't make page size too small** (too many requests) or too large (slow)

**Recommended page size**: 20-50 items

---

## Common Pitfalls

### Problem 1: Loading Same Page Multiple Times

```dart
// ❌ Bad: No guard
Future<void> _loadMorePosts() async {
  _currentPage++;
  final posts = await _service.getPosts(page: _currentPage);
  _posts.addAll(posts);
}

// ✅ Good: Guard with flag
Future<void> _loadMorePosts() async {
  if (_isLoadingMore) return; // Prevent duplicate calls

  setState(() => _isLoadingMore = true);
  try {
    _currentPage++;
    final posts = await _service.getPosts(page: _currentPage);
    setState(() {
      _posts.addAll(posts);
      _isLoadingMore = false;
    });
  } catch (e) {
    setState(() {
      _isLoadingMore = false;
      _currentPage--; // Revert on error
    });
  }
}
```

### Problem 2: Not Disposing ScrollController

```dart
// ❌ Bad: Memory leak
@override
Widget build(BuildContext context) {
  final controller = ScrollController(); // Created every build!
  return ListView(controller: controller);
}

// ✅ Good: Create once, dispose properly
class _MyScreenState extends State<MyScreen> {
  late final ScrollController _controller;

  @override
  void initState() {
    super.initState();
    _controller = ScrollController();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }
}
```

---

## Quiz Time! 🧠

Test your understanding:

### Question 1
Why is pagination important for mobile apps?

A) It makes the code shorter
B) It prevents loading too much data at once, improving performance and reducing server costs
C) It's required by Flutter
D) It only works on Android

### Question 2
What is the difference between offset-based and cursor-based pagination?

A) They're the same thing
B) Offset uses page numbers, cursor uses unique identifiers for the last item
C) Cursor is faster
D) Offset only works with databases

### Question 3
When should you load the next page in infinite scroll?

A) As soon as the user starts scrolling
B) When the user reaches the exact bottom
C) When the user scrolls within 200-300 pixels of the bottom
D) Every 5 seconds

---

## Answer Key

### Answer 1: B
**Correct**: It prevents loading too much data at once, improving performance and reducing server costs

Loading thousands of items at once would consume excessive memory, slow down the app, waste bandwidth, drain battery, and put unnecessary load on your server. Pagination solves all these problems by loading small chunks.

### Answer 2: B
**Correct**: Offset uses page numbers, cursor uses unique identifiers for the last item

Offset-based pagination uses page numbers or offsets (`page=1, page=2` or `offset=0, offset=20`). Cursor-based pagination uses a unique cursor/token that points to where to continue (`cursor=ABC123`). Cursor-based is better for real-time data because items can be added/removed without breaking pagination.

### Answer 3: C
**Correct**: When the user scrolls within 200-300 pixels of the bottom

This provides the best UX - loading starts before reaching the actual bottom, so users don't see a jarring stop. Too early (option A) wastes resources, and exact bottom (option B) creates a noticeable pause.

---

## What's Next?

You've learned how to efficiently handle large datasets with pagination and infinite scroll. In the next lesson, we'll explore **File Upload and Download** with progress tracking!

**Coming up in Lesson 7: File Upload and Download**
- Uploading images, videos, and documents
- Progress tracking for uploads/downloads
- Image picker integration
- Multiple file selection
- Complete gallery app example

---

## Key Takeaways

✅ Pagination loads data in small chunks (20-50 items at a time)
✅ Two main types: offset-based (page numbers) and cursor-based (unique tokens)
✅ Infinite scroll automatically loads more data when user scrolls near bottom
✅ Always use ListView.builder for performance (lazy loading)
✅ Implement pull-to-refresh for better UX
✅ Show loading indicators and "end of list" messages
✅ Prevent duplicate loads with a boolean flag (_isLoadingMore)
✅ Always dispose ScrollController to prevent memory leaks

**You're now ready to build apps that handle thousands of items smoothly!** 🎉
