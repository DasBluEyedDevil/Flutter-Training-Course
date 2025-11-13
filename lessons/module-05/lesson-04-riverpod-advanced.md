# Module 5, Lesson 4: Advanced Riverpod Patterns

## Beyond the Basics

You know Riverpod fundamentals. Now let's master advanced patterns for production apps!

**This lesson covers:**
- Family modifiers (parameterized providers)
- AutoDispose for memory management
- Combining providers
- AsyncValue handling
- Code generation (Riverpod 2.0+)

---

## Family Modifier - Parameterized Providers

**Problem**: You need a provider for EACH user/post/item

**Solution**: `.family` modifier!

```dart
// Without family - need separate provider for each user
final user1Provider = FutureProvider<User>((ref) => fetchUser('1'));
final user2Provider = FutureProvider<User>((ref) => fetchUser('2'));
// This doesn't scale!

// With family - ONE provider, multiple instances
final userProvider = FutureProvider.family<User, String>((ref, userId) async {
  final response = await http.get('https://api.example.com/users/$userId');
  return User.fromJson(jsonDecode(response.body));
});

// Usage
class UserProfile extends ConsumerWidget {
  final String userId;

  UserProfile({required this.userId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final userAsync = ref.watch(userProvider(userId));  // Pass parameter!

    return userAsync.when(
      data: (user) => Text(user.name),
      loading: () => CircularProgressIndicator(),
      error: (err, stack) => Text('Error: $err'),
    );
  }
}

// Different parameters = different instances
UserProfile(userId: '1'),  // Fetches user 1
UserProfile(userId: '2'),  // Fetches user 2
```

---

## Real Example: Comments Per Post

```dart
// Provider that fetches comments for a specific post
final commentsProvider = FutureProvider.family<List<Comment>, int>((ref, postId) async {
  await Future.delayed(Duration(seconds: 1));  // Simulate API call
  return [
    Comment(id: '1', postId: postId, text: 'Great post!', author: 'Alice'),
    Comment(id: '2', postId: postId, text: 'Thanks for sharing', author: 'Bob'),
  ];
});

// Usage
class PostDetail extends ConsumerWidget {
  final int postId;

  PostDetail({required this.postId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final commentsAsync = ref.watch(commentsProvider(postId));

    return Scaffold(
      appBar: AppBar(title: Text('Post $postId')),
      body: Column(
        children: [
          Text('Post content here...'),
          SizedBox(height: 20),
          Text('Comments:', style: TextStyle(fontWeight: FontWeight.bold)),
          Expanded(
            child: commentsAsync.when(
              data: (comments) => ListView.builder(
                itemCount: comments.length,
                itemBuilder: (context, index) {
                  final comment = comments[index];
                  return ListTile(
                    leading: CircleAvatar(child: Text(comment.author[0])),
                    title: Text(comment.author),
                    subtitle: Text(comment.text),
                  );
                },
              ),
              loading: () => Center(child: CircularProgressIndicator()),
              error: (err, stack) => Text('Error loading comments'),
            ),
          ),
        ],
      ),
    );
  }
}

class Comment {
  final String id;
  final int postId;
  final String text;
  final String author;

  Comment({
    required this.id,
    required this.postId,
    required this.text,
    required this.author,
  });
}
```

---

## AutoDispose - Automatic Cleanup

**Problem**: Providers stay in memory even when not used

**Solution**: `.autoDispose` modifier!

```dart
// Without autoDispose - stays in memory forever
final userProvider = FutureProvider<User>((ref) => fetchUser());

// With autoDispose - disposed when no longer watched
final userProvider = FutureProvider.autoDispose<User>((ref) => fetchUser());
```

**When to use:**
- ✅ Data that's screen-specific
- ✅ Temporary states
- ✅ API calls for detail views

**When NOT to use:**
- ❌ Global app state (theme, auth)
- ❌ Data shared across app
- ❌ Expensive computations you want to cache

---

## Combining autoDispose and family

```dart
final postProvider = FutureProvider.autoDispose.family<Post, int>((ref, postId) async {
  print('Fetching post $postId');
  final response = await http.get('https://api.example.com/posts/$postId');
  return Post.fromJson(jsonDecode(response.body));
});

// When you navigate AWAY from PostDetail, provider is automatically disposed
// When you navigate BACK, it fetches fresh data!
```

---

## Keeping Alive When Needed

Sometimes you want autoDispose but with exceptions:

```dart
final cacheProvider = FutureProvider.autoDispose.family<Data, String>((ref, id) async {
  // Keep alive for 5 minutes even if no one is watching
  final keepAlive = ref.keepAlive();

  Timer(Duration(minutes: 5), () {
    keepAlive.close();  // Now it can be disposed
  });

  return await fetchData(id);
});
```

---

## Combining Providers

Providers can watch OTHER providers!

```dart
// User authentication
final authProvider = StateProvider<String?>((ref) => null);

// User profile (depends on auth)
final userProfileProvider = FutureProvider<UserProfile?>((ref) async {
  final userId = ref.watch(authProvider);

  if (userId == null) return null;

  final response = await http.get('https://api.example.com/profile/$userId');
  return UserProfile.fromJson(jsonDecode(response.body));
});

// User posts (depends on auth)
final userPostsProvider = FutureProvider<List<Post>>((ref) async {
  final userId = ref.watch(authProvider);

  if (userId == null) return [];

  final response = await http.get('https://api.example.com/users/$userId/posts');
  return (jsonDecode(response.body) as List)
      .map((json) => Post.fromJson(json))
      .toList();
});

// When authProvider changes, both userProfileProvider and userPostsProvider
// automatically refetch!
```

---

## Complete Example: Multi-Provider Dependencies

```dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

// 1. Auth state
final authProvider = StateProvider<String?>((ref) => null);

// 2. User profile depends on auth
final userProfileProvider = FutureProvider<UserProfile?>((ref) async {
  final userId = ref.watch(authProvider);
  if (userId == null) return null;

  await Future.delayed(Duration(seconds: 1));
  return UserProfile(id: userId, name: 'User $userId', email: 'user$userId@example.com');
});

// 3. User settings depends on auth
final userSettingsProvider = StateNotifierProvider<UserSettingsNotifier, UserSettings?>((ref) {
  final userId = ref.watch(authProvider);
  return UserSettingsNotifier(userId);
});

class UserSettings {
  final bool darkMode;
  final bool notifications;

  UserSettings({required this.darkMode, required this.notifications});
}

class UserSettingsNotifier extends StateNotifier<UserSettings?> {
  final String? userId;

  UserSettingsNotifier(this.userId) : super(null) {
    if (userId != null) {
      _loadSettings();
    }
  }

  void _loadSettings() {
    // Simulate loading
    state = UserSettings(darkMode: false, notifications: true);
  }

  void toggleDarkMode() {
    if (state == null) return;
    state = UserSettings(darkMode: !state!.darkMode, notifications: state!.notifications);
  }

  void toggleNotifications() {
    if (state == null) return;
    state = UserSettings(darkMode: state!.darkMode, notifications: !state!.notifications);
  }
}

class UserProfile {
  final String id;
  final String name;
  final String email;

  UserProfile({required this.id, required this.name, required this.email});
}

// UI
void main() {
  runApp(
    ProviderScope(
      child: MaterialApp(home: HomeScreen()),
    ),
  );
}

class HomeScreen extends ConsumerWidget {
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final userId = ref.watch(authProvider);

    return Scaffold(
      appBar: AppBar(title: Text('Riverpod Advanced')),
      body: userId == null
          ? Center(
              child: ElevatedButton(
                onPressed: () {
                  ref.read(authProvider.notifier).state = 'user123';
                },
                child: Text('Login'),
              ),
            )
          : UserDashboard(),
    );
  }
}

class UserDashboard extends ConsumerWidget {
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final profileAsync = ref.watch(userProfileProvider);
    final settings = ref.watch(userSettingsProvider);

    return Padding(
      padding: EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text('Profile', style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold)),
          SizedBox(height: 16),
          profileAsync.when(
            data: (profile) => profile == null
                ? Text('No profile')
                : Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text('Name: ${profile.name}', style: TextStyle(fontSize: 18)),
                      Text('Email: ${profile.email}', style: TextStyle(fontSize: 18)),
                    ],
                  ),
            loading: () => CircularProgressIndicator(),
            error: (err, stack) => Text('Error: $err'),
          ),
          SizedBox(height: 32),
          Text('Settings', style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold)),
          if (settings != null) ...[
            SwitchListTile(
              title: Text('Dark Mode'),
              value: settings.darkMode,
              onChanged: (_) {
                ref.read(userSettingsProvider.notifier).toggleDarkMode();
              },
            ),
            SwitchListTile(
              title: Text('Notifications'),
              value: settings.notifications,
              onChanged: (_) {
                ref.read(userSettingsProvider.notifier).toggleNotifications();
              },
            ),
          ],
          SizedBox(height: 32),
          ElevatedButton(
            onPressed: () {
              ref.read(authProvider.notifier).state = null;
            },
            child: Text('Logout'),
          ),
        ],
      ),
    );
  }
}
```

---

## AsyncValue - Handling Loading/Error/Data

When working with async providers, use `.when()`:

```dart
final dataAsync = ref.watch(dataProvider);

// Option 1: when (rebuild for all states)
dataAsync.when(
  data: (data) => Text('Data: $data'),
  loading: () => CircularProgressIndicator(),
  error: (error, stack) => Text('Error: $error'),
);

// Option 2: maybeWhen (default for unhandled states)
dataAsync.maybeWhen(
  data: (data) => Text('Data: $data'),
  orElse: () => Text('Loading or error'),
);

// Option 3: map (more control)
dataAsync.map(
  data: (data) => Text('Success: ${data.value}'),
  loading: (_) => CircularProgressIndicator(),
  error: (error) => Text('Error: ${error.error}'),
);

// Option 4: Direct access (careful!)
if (dataAsync.hasValue) {
  final data = dataAsync.value!;
  return Text('$data');
}
```

---

## Refreshing Data

```dart
// Manual refresh
ElevatedButton(
  onPressed: () {
    ref.refresh(userProvider);  // Refetch data
  },
  child: Text('Refresh'),
)

// Invalidate and rebuild
ref.invalidate(userProvider);
```

---

## Code Generation (Riverpod 2.0+)

Modern Riverpod uses annotations and code generation:

```dart
// Instead of:
final counterProvider = StateProvider<int>((ref) => 0);

// Use annotations:
import 'package:riverpod_annotation/riverpod_annotation.dart';

part 'counter.g.dart';  // Generated file

@riverpod
class Counter extends _$Counter {
  @override
  int build() => 0;

  void increment() => state++;
  void decrement() => state--;
}

// Generated provider: counterProvider
```

**Benefits:**
- Type-safe
- Less boilerplate
- Better autocomplete
- Compile-time errors

**Setup:**
```yaml
dev_dependencies:
  build_runner: ^2.4.0
  riverpod_generator: ^2.4.0
```

Run: `flutter pub run build_runner watch`

---

## Best Practices Summary

### 1. Use autoDispose for Screen-Specific Data
```dart
final postDetailProvider = FutureProvider.autoDispose.family<Post, int>(
  (ref, postId) => fetchPost(postId),
);
```

### 2. Combine Providers for Derived State
```dart
final filteredTodosProvider = Provider<List<Todo>>((ref) {
  final todos = ref.watch(todosProvider);
  final filter = ref.watch(filterProvider);
  return todos.where((todo) => matchesFilter(todo, filter)).toList();
});
```

### 3. Use ref.listen for Side Effects
```dart
ref.listen<AsyncValue<Data>>(dataProvider, (previous, next) {
  next.whenData((data) {
    if (data.hasError) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Error occurred!')),
      );
    }
  });
});
```

### 4. Family for Parameterized Providers
```dart
final itemProvider = Provider.family<Item, String>((ref, itemId) {
  final items = ref.watch(itemsProvider);
  return items.firstWhere((item) => item.id == itemId);
});
```

---

## ✅ YOUR CHALLENGE: Social Media Feed

Build a social media feed with:
1. **Auth provider** - User login state
2. **Posts provider (.family)** - Fetch posts by user
3. **Comments provider (.family + .autoDispose)** - Comments per post
4. **Like provider (.family)** - Like status per post
5. **Combined provider** - Feed with liked posts highlighted

Features:
- Login/logout
- View user's posts
- Expand post to see comments
- Like/unlike posts
- Pull to refresh

**Success Condition**: Multi-level provider dependencies with family and autoDispose! ✅

---

## Common Patterns

### Pattern 1: Search with Debounce
```dart
final searchQueryProvider = StateProvider<String>((ref) => '');

final searchResultsProvider = FutureProvider.autoDispose<List<Item>>((ref) async {
  final query = ref.watch(searchQueryProvider);

  if (query.isEmpty) return [];

  // Debounce
  await Future.delayed(Duration(milliseconds: 500));

  // Check if query changed during delay
  if (query != ref.read(searchQueryProvider)) {
    return [];
  }

  return searchApi(query);
});
```

### Pattern 2: Pagination
```dart
final pageProvider = StateProvider<int>((ref) => 1);

final itemsProvider = FutureProvider<List<Item>>((ref) async {
  final page = ref.watch(pageProvider);
  return fetchItems(page);
});

// Load more
ref.read(pageProvider.notifier).state++;
```

---

## What Did We Learn?

- ✅ Family modifier for parameterized providers
- ✅ AutoDispose for automatic cleanup
- ✅ Combining providers for derived state
- ✅ AsyncValue handling with when/map
- ✅ Refreshing and invalidating providers
- ✅ Code generation (modern approach)
- ✅ Real-world patterns (search, pagination, auth)

---

## What's Next?

You've mastered Riverpod! Next: **State Management Best Practices** - architecture patterns, testing, and choosing the right solution for your app!
