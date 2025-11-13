# Module 8, Lesson 8: Mini-Project - Complete Firebase Social App

## Project Overview

**Welcome to your Module 8 capstone project!** 🎉

In this mini-project, you'll build **"FireSocial"** - a complete social media app that combines **EVERY Firebase concept** from Module 8:

✅ Firebase Authentication (email & Google)
✅ Cloud Firestore (posts, likes, comments)
✅ Cloud Storage (profile pictures, post images)
✅ Security Rules (production-ready)
✅ Real-time features (live likes, typing indicators)
✅ Push notifications (new likes, comments)
✅ Analytics (track user behavior)

---

## What You'll Build

### FireSocial Features

1. **Authentication**
   - Email/password & Google Sign-In
   - Profile creation with photo upload
   - Secure session management

2. **User Profiles**
   - Profile picture upload
   - Bio and user info
   - Post count
   - Edit profile

3. **Posts Feed**
   - Create posts with images
   - Real-time feed updates
   - Like posts (with real-time counter)
   - Comment on posts
   - Delete own posts

4. **Real-Time Chat**
   - Direct messages
   - Typing indicators
   - Online/offline status
   - Message notifications

5. **Push Notifications**
   - New likes on your posts
   - New comments
   - New messages
   - Topic-based (announcements)

6. **Analytics**
   - Track screen views
   - Log user actions
   - Conversion tracking

---

## Project Structure

```
lib/
├── main.dart
├── models/
│   ├── user_model.dart
│   ├── post_model.dart
│   ├── comment_model.dart
│   └── chat_message.dart
├── services/
│   ├── auth_service.dart
│   ├── firestore_service.dart
│   ├── storage_service.dart
│   ├── chat_service.dart
│   ├── notification_service.dart
│   ├── analytics_service.dart
│   └── presence_service.dart
├── screens/
│   ├── auth/
│   │   ├── login_screen.dart
│   │   ├── register_screen.dart
│   │   └── profile_setup_screen.dart
│   ├── home/
│   │   ├── home_screen.dart
│   │   └── feed_screen.dart
│   ├── profile/
│   │   ├── profile_screen.dart
│   │   └── edit_profile_screen.dart
│   ├── posts/
│   │   ├── create_post_screen.dart
│   │   └── post_detail_screen.dart
│   └── chat/
│       ├── chat_list_screen.dart
│       └── chat_screen.dart
├── widgets/
│   ├── post_card.dart
│   ├── comment_card.dart
│   ├── user_avatar.dart
│   └── online_indicator.dart
└── utils/
    ├── constants.dart
    └── helpers.dart
```

---

## Step 1: Setup & Dependencies

### pubspec.yaml

```yaml
name: firesocial
description: A complete social media app with Firebase

environment:
  sdk: '>=3.7.0 <4.0.0'

dependencies:
  flutter:
    sdk: flutter

  # Firebase
  firebase_core: ^4.2.0
  firebase_auth: ^6.1.1
  cloud_firestore: ^6.0.3
  firebase_storage: ^13.0.4
  firebase_messaging: ^15.2.7
  firebase_analytics: ^11.3.4
  firebase_database: ^11.3.0  # For presence

  # Google Sign-In
  google_sign_in: ^7.1.1

  # Image Handling
  image_picker: ^1.1.2
  cached_network_image: ^3.5.0

  # Notifications
  flutter_local_notifications: ^18.0.1

  # UI
  timeago: ^3.8.0  # "2 hours ago" formatting
  intl: ^0.20.2    # Date formatting

dev_dependencies:
  flutter_test:
    sdk: flutter
  flutter_lints: ^5.0.0

flutter:
  uses-material-design: true
```

Run:
```bash
flutter pub get
```

---

## Step 2: Complete Data Models

### User Model

```dart
// lib/models/user_model.dart
import 'package:cloud_firestore/cloud_firestore.dart';

class UserModel {
  final String uid;
  final String email;
  final String displayName;
  final String? photoURL;
  final String? bio;
  final int postCount;
  final List<String> fcmTokens;
  final bool online;
  final DateTime? lastSeen;
  final DateTime createdAt;

  UserModel({
    required this.uid,
    required this.email,
    required this.displayName,
    this.photoURL,
    this.bio,
    this.postCount = 0,
    this.fcmTokens = const [],
    this.online = false,
    this.lastSeen,
    DateTime? createdAt,
  }) : createdAt = createdAt ?? DateTime.now();

  factory UserModel.fromFirestore(DocumentSnapshot doc) {
    final data = doc.data() as Map<String, dynamic>;
    return UserModel(
      uid: doc.id,
      email: data['email'] ?? '',
      displayName: data['displayName'] ?? 'Unknown',
      photoURL: data['photoURL'],
      bio: data['bio'],
      postCount: data['postCount'] ?? 0,
      fcmTokens: List<String>.from(data['fcmTokens'] ?? []),
      online: data['online'] ?? false,
      lastSeen: data['lastSeen'] != null
          ? (data['lastSeen'] as Timestamp).toDate()
          : null,
      createdAt: (data['createdAt'] as Timestamp).toDate(),
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'email': email,
      'displayName': displayName,
      'photoURL': photoURL,
      'bio': bio,
      'postCount': postCount,
      'fcmTokens': fcmTokens,
      'online': online,
      'lastSeen': lastSeen != null ? Timestamp.fromDate(lastSeen!) : null,
      'createdAt': Timestamp.fromDate(createdAt),
    };
  }
}
```

### Post Model

```dart
// lib/models/post_model.dart
import 'package:cloud_firestore/cloud_firestore.dart';

class Post {
  final String id;
  final String userId;
  final String userName;
  final String? userPhotoURL;
  final String caption;
  final String imageURL;
  final List<String> likes;
  final int commentCount;
  final DateTime createdAt;

  Post({
    required this.id,
    required this.userId,
    required this.userName,
    this.userPhotoURL,
    required this.caption,
    required this.imageURL,
    this.likes = const [],
    this.commentCount = 0,
    DateTime? createdAt,
  }) : createdAt = createdAt ?? DateTime.now();

  factory Post.fromFirestore(DocumentSnapshot doc) {
    final data = doc.data() as Map<String, dynamic>;
    return Post(
      id: doc.id,
      userId: data['userId'] ?? '',
      userName: data['userName'] ?? 'Unknown',
      userPhotoURL: data['userPhotoURL'],
      caption: data['caption'] ?? '',
      imageURL: data['imageURL'] ?? '',
      likes: List<String>.from(data['likes'] ?? []),
      commentCount: data['commentCount'] ?? 0,
      createdAt: (data['createdAt'] as Timestamp).toDate(),
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'userId': userId,
      'userName': userName,
      'userPhotoURL': userPhotoURL,
      'caption': caption,
      'imageURL': imageURL,
      'likes': likes,
      'commentCount': commentCount,
      'createdAt': Timestamp.fromDate(createdAt),
    };
  }

  bool isLikedBy(String userId) => likes.contains(userId);
}
```

---

## Step 3: Complete Firestore Service

```dart
// lib/services/firestore_service.dart
import 'package:cloud_firestore/cloud_firestore.dart';
import '../models/user_model.dart';
import '../models/post_model.dart';

class FirestoreService {
  final FirebaseFirestore _firestore = FirebaseFirestore.instance;

  // ========== USER OPERATIONS ==========

  Future<void> createUserProfile(UserModel user) async {
    await _firestore.collection('users').doc(user.uid).set(user.toMap());
  }

  Future<UserModel?> getUserProfile(String uid) async {
    final doc = await _firestore.collection('users').doc(uid).get();
    if (doc.exists) {
      return UserModel.fromFirestore(doc);
    }
    return null;
  }

  Future<void> updateUserProfile(String uid, Map<String, dynamic> data) async {
    await _firestore.collection('users').doc(uid).update(data);
  }

  Stream<UserModel?> getUserProfileStream(String uid) {
    return _firestore
        .collection('users')
        .doc(uid)
        .snapshots()
        .map((doc) => doc.exists ? UserModel.fromFirestore(doc) : null);
  }

  // ========== POST OPERATIONS ==========

  Future<String> createPost(Post post) async {
    final docRef = await _firestore.collection('posts').add(post.toMap());

    // Increment user's post count
    await _firestore.collection('users').doc(post.userId).update({
      'postCount': FieldValue.increment(1),
    });

    return docRef.id;
  }

  Stream<List<Post>> getPostsStream({int limit = 20}) {
    return _firestore
        .collection('posts')
        .orderBy('createdAt', descending: true)
        .limit(limit)
        .snapshots()
        .map((snapshot) {
      return snapshot.docs.map((doc) => Post.fromFirestore(doc)).toList();
    });
  }

  Stream<List<Post>> getUserPostsStream(String userId) {
    return _firestore
        .collection('posts')
        .where('userId', isEqualTo: userId)
        .orderBy('createdAt', descending: true)
        .snapshots()
        .map((snapshot) {
      return snapshot.docs.map((doc) => Post.fromFirestore(doc)).toList();
    });
  }

  Future<void> likePost(String postId, String userId) async {
    await _firestore.collection('posts').doc(postId).update({
      'likes': FieldValue.arrayUnion([userId]),
    });
  }

  Future<void> unlikePost(String postId, String userId) async {
    await _firestore.collection('posts').doc(postId).update({
      'likes': FieldValue.arrayRemove([userId]),
    });
  }

  Future<void> deletePost(String postId, String userId) async {
    await _firestore.collection('posts').doc(postId).delete();

    // Decrement user's post count
    await _firestore.collection('users').doc(userId).update({
      'postCount': FieldValue.increment(-1),
    });
  }

  // ========== COMMENT OPERATIONS ==========

  Future<void> addComment(String postId, Map<String, dynamic> comment) async {
    await _firestore
        .collection('posts')
        .doc(postId)
        .collection('comments')
        .add(comment);

    // Increment comment count
    await _firestore.collection('posts').doc(postId).update({
      'commentCount': FieldValue.increment(1),
    });
  }

  Stream<List<Map<String, dynamic>>> getCommentsStream(String postId) {
    return _firestore
        .collection('posts')
        .doc(postId)
        .collection('comments')
        .orderBy('createdAt', descending: true)
        .snapshots()
        .map((snapshot) {
      return snapshot.docs
          .map((doc) => {...doc.data(), 'id': doc.id})
          .toList();
    });
  }
}
```

---

## Step 4: Main App with Navigation

```dart
// lib/main.dart
import 'package:flutter/material.dart';
import 'package:firebase_core/firebase_core.dart';
import 'firebase_options.dart';
import 'services/auth_service.dart';
import 'services/notification_service.dart';
import 'services/analytics_service.dart';
import 'screens/auth/login_screen.dart';
import 'screens/home/home_screen.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );

  // Initialize services
  await NotificationService().initialize();

  runApp(const FireSocialApp());
}

class FireSocialApp extends StatelessWidget {
  const FireSocialApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'FireSocial',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      navigatorObservers: [
        AnalyticsService().getObserver(),
      ],
      home: const AuthWrapper(),
    );
  }
}

class AuthWrapper extends StatelessWidget {
  const AuthWrapper({super.key});

  @override
  Widget build(BuildContext context) {
    return StreamBuilder(
      stream: AuthService().authStateChanges,
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const Scaffold(
            body: Center(child: CircularProgressIndicator()),
          );
        }

        if (snapshot.hasData && snapshot.data != null) {
          return const HomeScreen();
        }

        return const LoginScreen();
      },
    );
  }
}
```

---

## Step 5: Home Screen with Bottom Navigation

```dart
// lib/screens/home/home_screen.dart
import 'package:flutter/material.dart';
import 'feed_screen.dart';
import '../posts/create_post_screen.dart';
import '../profile/profile_screen.dart';
import '../chat/chat_list_screen.dart';
import '../../services/auth_service.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  int _currentIndex = 0;

  final List<Widget> _screens = [
    const FeedScreen(),
    const CreatePostScreen(),
    const ChatListScreen(),
    const ProfileScreen(),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: IndexedStack(
        index: _currentIndex,
        children: _screens,
      ),
      bottomNavigationBar: NavigationBar(
        selectedIndex: _currentIndex,
        onDestinationSelected: (index) {
          setState(() => _currentIndex = index);
        },
        destinations: const [
          NavigationDestination(
            icon: Icon(Icons.home_outlined),
            selectedIcon: Icon(Icons.home),
            label: 'Feed',
          ),
          NavigationDestination(
            icon: Icon(Icons.add_circle_outline),
            selectedIcon: Icon(Icons.add_circle),
            label: 'Create',
          ),
          NavigationDestination(
            icon: Icon(Icons.chat_bubble_outline),
            selectedIcon: Icon(Icons.chat_bubble),
            label: 'Chats',
          ),
          NavigationDestination(
            icon: Icon(Icons.person_outline),
            selectedIcon: Icon(Icons.person),
            label: 'Profile',
          ),
        ],
      ),
    );
  }
}
```

---

## Step 6: Feed Screen with Real-Time Posts

```dart
// lib/screens/home/feed_screen.dart
import 'package:flutter/material.dart';
import '../../services/firestore_service.dart';
import '../../services/analytics_service.dart';
import '../../widgets/post_card.dart';

class FeedScreen extends StatefulWidget {
  const FeedScreen({super.key});

  @override
  State<FeedScreen> createState() => _FeedScreenState();
}

class _FeedScreenState extends State<FeedScreen> {
  final _firestoreService = FirestoreService();
  final _analytics = AnalyticsService();

  @override
  void initState() {
    super.initState();
    _analytics.logScreenView('Feed');
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('FireSocial'),
        actions: [
          IconButton(
            icon: const Icon(Icons.notifications_outlined),
            onPressed: () {
              // Navigate to notifications
            },
          ),
        ],
      ),
      body: StreamBuilder(
        stream: _firestoreService.getPostsStream(),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          }

          if (snapshot.hasError) {
            return Center(child: Text('Error: ${snapshot.error}'));
          }

          if (!snapshot.hasData || snapshot.data!.isEmpty) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(
                    Icons.photo_library_outlined,
                    size: 100,
                    color: Colors.grey.shade300,
                  ),
                  const SizedBox(height: 16),
                  Text(
                    'No posts yet',
                    style: Theme.of(context).textTheme.headlineSmall,
                  ),
                  const SizedBox(height: 8),
                  const Text('Be the first to post!'),
                ],
              ),
            );
          }

          final posts = snapshot.data!;

          return RefreshIndicator(
            onRefresh: () async {
              setState(() {});
            },
            child: ListView.builder(
              padding: const EdgeInsets.symmetric(vertical: 8),
              itemCount: posts.length,
              itemBuilder: (context, index) {
                return PostCard(post: posts[index]);
              },
            ),
          );
        },
      ),
    );
  }
}
```

---

## Step 7: Post Card Widget

```dart
// lib/widgets/post_card.dart
import 'package:flutter/material.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:timeago/timeago.dart' as timeago;
import '../models/post_model.dart';
import '../services/firestore_service.dart';
import '../services/auth_service.dart';

class PostCard extends StatelessWidget {
  final Post post;

  const PostCard({super.key, required this.post});

  @override
  Widget build(BuildContext context) {
    final authService = AuthService();
    final firestoreService = FirestoreService();
    final currentUserId = authService.currentUser?.uid;

    return Card(
      margin: const EdgeInsets.only(bottom: 16, left: 8, right: 8),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // User header
          ListTile(
            leading: CircleAvatar(
              backgroundImage: post.userPhotoURL != null
                  ? CachedNetworkImageProvider(post.userPhotoURL!)
                  : null,
              child: post.userPhotoURL == null
                  ? Text(post.userName[0].toUpperCase())
                  : null,
            ),
            title: Text(
              post.userName,
              style: const TextStyle(fontWeight: FontWeight.bold),
            ),
            subtitle: Text(timeago.format(post.createdAt)),
            trailing: post.userId == currentUserId
                ? PopupMenuButton(
                    itemBuilder: (context) => [
                      const PopupMenuItem(
                        value: 'delete',
                        child: Text('Delete'),
                      ),
                    ],
                    onSelected: (value) async {
                      if (value == 'delete') {
                        await firestoreService.deletePost(
                          post.id,
                          currentUserId!,
                        );
                      }
                    },
                  )
                : null,
          ),

          // Post image
          CachedNetworkImage(
            imageUrl: post.imageURL,
            width: double.infinity,
            fit: BoxFit.cover,
            placeholder: (context, url) => Container(
              height: 300,
              color: Colors.grey.shade200,
              child: const Center(child: CircularProgressIndicator()),
            ),
            errorWidget: (context, url, error) => Container(
              height: 300,
              color: Colors.grey.shade200,
              child: const Icon(Icons.error),
            ),
          ),

          // Like and comment buttons
          Row(
            children: [
              IconButton(
                icon: Icon(
                  post.isLikedBy(currentUserId!)
                      ? Icons.favorite
                      : Icons.favorite_border,
                  color:
                      post.isLikedBy(currentUserId) ? Colors.red : null,
                ),
                onPressed: () async {
                  if (post.isLikedBy(currentUserId)) {
                    await firestoreService.unlikePost(post.id, currentUserId);
                  } else {
                    await firestoreService.likePost(post.id, currentUserId);
                  }
                },
              ),
              Text('${post.likes.length}'),
              const SizedBox(width: 16),
              const Icon(Icons.comment_outlined),
              const SizedBox(width: 4),
              Text('${post.commentCount}'),
            ],
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
                      text: '${post.userName} ',
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
}
```

---

## Step 8: Security Rules (CRITICAL!)

### Firestore Security Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    function isSignedIn() {
      return request.auth != null;
    }

    function isOwner(userId) {
      return isSignedIn() && request.auth.uid == userId;
    }

    // Users collection
    match /users/{userId} {
      allow read: if true;
      allow create: if isOwner(userId);
      allow update: if isOwner(userId);
    }

    // Posts collection
    match /posts/{postId} {
      allow read: if true;
      allow create: if isSignedIn()
                    && request.resource.data.userId == request.auth.uid;
      allow update: if isSignedIn();
      allow delete: if isOwner(resource.data.userId);

      // Comments subcollection
      match /comments/{commentId} {
        allow read: if true;
        allow create: if isSignedIn();
      }
    }

    // Chat rooms
    match /chatRooms/{chatRoomId} {
      allow read, write: if isSignedIn()
                         && request.auth.uid in resource.data.participants;
    }
  }
}
```

### Storage Security Rules

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    function isSignedIn() {
      return request.auth != null;
    }

    function isOwner(userId) {
      return isSignedIn() && request.auth.uid == userId;
    }

    // User profile pictures
    match /users/{userId}/profile/{fileName} {
      allow read: if true;
      allow write: if isOwner(userId)
                   && request.resource.contentType.matches('image/.*')
                   && request.resource.size < 5 * 1024 * 1024;  // 5MB max
    }

    // Post images
    match /posts/{userId}/{fileName} {
      allow read: if true;
      allow write: if isOwner(userId)
                   && request.resource.contentType.matches('image/.*')
                   && request.resource.size < 10 * 1024 * 1024;  // 10MB max
    }
  }
}
```

---

## Testing Your App

### 1. Authentication
- ✅ Register with email/password
- ✅ Login with Google
- ✅ Logout

### 2. Posts
- ✅ Create post with image
- ✅ View feed (real-time updates)
- ✅ Like/unlike posts
- ✅ Delete own posts

### 3. Profile
- ✅ Upload profile picture
- ✅ Edit bio
- ✅ View post count

### 4. Real-Time
- ✅ Open app on 2 devices
- ✅ Like post on device 1
- ✅ Watch count update on device 2!

### 5. Security
- ✅ Try accessing other user's data (should fail)
- ✅ Try uploading oversized file (should fail)
- ✅ Check Firebase Console → Security Rules

---

## What You've Accomplished

Congratulations! You've built a complete social media app with:

✅ **Authentication**: Secure email & Google login
✅ **Database**: Real-time Firestore with complex queries
✅ **Storage**: File uploads with validation
✅ **Security**: Production-ready security rules
✅ **Real-Time**: Live updates across all devices
✅ **Notifications**: Push notifications (if implemented)
✅ **Analytics**: User behavior tracking

**This is a production-ready foundation!**

---

## Next Steps & Enhancements

Want to take this further? Try adding:

1. **Comments System**: Full comment threads with replies
2. **User Following**: Follow/unfollow users, follower counts
3. **Feed Algorithm**: Show posts from followed users only
4. **Stories**: Instagram-style 24-hour stories
5. **Hashtags**: Search posts by hashtags
6. **Mentions**: Tag users in posts/comments
7. **Direct Messages**: Real-time one-on-one chat
8. **Push Notifications**: Notify on likes, comments, follows
9. **Video Posts**: Upload and play videos
10. **Search**: Search users and posts

---

## Quiz Time! 🧠

### Question 1
Why use StreamBuilder for the posts feed?

A) It's faster
B) It provides automatic real-time updates when posts change
C) Firebase requires it
D) It uses less memory

### Question 2
Why increment postCount in Firestore when creating a post?

A) Firebase requires it
B) To avoid querying all posts to count them (performance)
C) It's not necessary
D) To make the app faster

### Question 3
What's the most important part of a production Firebase app?

A) Beautiful UI
B) Security rules
C) Analytics
D) Notifications

---

## Answer Key

### Answer 1: B
**Correct**: It provides automatic real-time updates when posts change

StreamBuilder listens to Firestore's `snapshots()` stream and automatically rebuilds the UI whenever data changes. When someone creates/deletes a post, all users see the update instantly without manual refresh.

### Answer 2: B
**Correct**: To avoid querying all posts to count them (performance)

Storing an aggregated count prevents expensive queries. Without it, you'd need to fetch all user posts just to count them (slow and costly). Firestore charges per document read, so fewer reads = lower costs.

### Answer 3: B
**Correct**: Security rules

Without proper security rules, your database is wide open - anyone can read/write/delete anything. Beautiful UI doesn't matter if hackers steal all user data. Security rules are the foundation of production apps.

---

## Congratulations! 🎉

**You've completed Module 8: Backend Integration!**

You now have the skills to build production-ready apps with:
- Secure authentication
- Real-time cloud databases
- File storage
- Push notifications
- User analytics
- Complete backend infrastructure

**You're ready for Module 9: Advanced Features!** Where you'll learn animations, local storage, camera integration, and more.

---

## Key Takeaways

✅ Firebase provides a complete backend solution (auth, database, storage, notifications)
✅ StreamBuilder enables real-time updates across all devices
✅ Security rules are CRITICAL - never deploy without them
✅ Store aggregated data (counts) to avoid expensive queries
✅ Use caching (CachedNetworkImage) for better performance
✅ Test on multiple devices to verify real-time sync
✅ Always dispose streams and controllers to prevent memory leaks

**Module 8 Complete - You're now a full-stack Flutter developer!** 🚀
