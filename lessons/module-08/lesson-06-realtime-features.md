# Module 8, Lesson 6: Real-Time Features with Firebase

## What You'll Learn
By the end of this lesson, you'll know how to build real-time features like live chat, presence detection (online/offline status), and collaborative editing using Firebase's real-time capabilities.

---

## Why This Matters

**Real-time features make apps feel alive.**

- **WhatsApp**: Messages appear instantly
- **Google Docs**: See others typing in real-time
- **Instagram**: Live like counts and comments
- **Slack**: Online/offline status, typing indicators
- **75% of modern apps** have some real-time feature
- **User engagement increases 300%** with real-time updates

Firebase makes real-time features incredibly easy - no complex WebSocket servers needed!

---

## Real-World Analogy: The Walkie-Talkie

### Without Real-Time = Sending Letters
- ✉️ Write message → mail it → wait days → receive reply
- 📬 Check mailbox periodically for new letters
- ⏰ Slow, delayed communication
- ❌ Can't have natural conversations

### With Real-Time = Walkie-Talkie
- 📡 Speak → they hear instantly
- 🔊 Their response comes immediately
- 👥 Know when others are online/offline
- ✅ Natural, flowing conversation

**Firebase real-time updates are like having a walkie-talkie connection to your database!**

---

## Firebase Real-Time Capabilities

### 1. Firestore Snapshots (Real-Time Listeners)
```dart
// Listen to document changes
firestore.collection('chats').doc('room1').snapshots()

// Listen to collection changes
firestore.collection('messages').snapshots()
```

**When data changes**:
1. Firebase detects the change
2. Pushes update to all listening devices
3. Flutter rebuilds UI automatically (with StreamBuilder)

### 2. Firestore Realtime Database
- Legacy real-time database (still used for specific cases)
- Extremely low latency (< 100ms)
- JSON tree structure
- Good for: presence, typing indicators, live cursors

### 3. Firebase Cloud Messaging (FCM)
- Push notifications
- Background messaging
- Topic-based messaging

---

## Part 1: Real-Time Chat App

Let's build a complete chat app with Firebase!

### Chat Message Model

```dart
// lib/models/chat_message.dart
import 'package:cloud_firestore/cloud_firestore.dart';

class ChatMessage {
  final String id;
  final String text;
  final String senderId;
  final String senderName;
  final DateTime timestamp;
  final bool isRead;

  ChatMessage({
    required this.id,
    required this.text,
    required this.senderId,
    required this.senderName,
    required this.timestamp,
    this.isRead = false,
  });

  factory ChatMessage.fromFirestore(DocumentSnapshot doc) {
    final data = doc.data() as Map<String, dynamic>;
    return ChatMessage(
      id: doc.id,
      text: data['text'] ?? '',
      senderId: data['senderId'] ?? '',
      senderName: data['senderName'] ?? 'Unknown',
      timestamp: (data['timestamp'] as Timestamp).toDate(),
      isRead: data['isRead'] ?? false,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'text': text,
      'senderId': senderId,
      'senderName': senderName,
      'timestamp': Timestamp.fromDate(timestamp),
      'isRead': isRead,
    };
  }
}
```

### Chat Service

```dart
// lib/services/chat_service.dart
import 'package:cloud_firestore/cloud_firestore.dart';
import '../models/chat_message.dart';

class ChatService {
  final FirebaseFirestore _firestore = FirebaseFirestore.instance;

  // Get chat room ID for two users (consistent ordering)
  String getChatRoomId(String userId1, String userId2) {
    List<String> ids = [userId1, userId2]..sort();
    return '${ids[0]}_${ids[1]}';
  }

  // Send message
  Future<void> sendMessage({
    required String chatRoomId,
    required String text,
    required String senderId,
    required String senderName,
  }) async {
    final message = ChatMessage(
      id: '',
      text: text,
      senderId: senderId,
      senderName: senderName,
      timestamp: DateTime.now(),
    );

    await _firestore
        .collection('chatRooms')
        .doc(chatRoomId)
        .collection('messages')
        .add(message.toMap());

    // Update chat room's last message
    await _firestore.collection('chatRooms').doc(chatRoomId).set({
      'lastMessage': text,
      'lastMessageTime': Timestamp.now(),
      'participants': [senderId],  // Add other participant too
    }, SetOptions(merge: true));
  }

  // Get messages stream (real-time!)
  Stream<List<ChatMessage>> getMessagesStream(String chatRoomId) {
    return _firestore
        .collection('chatRooms')
        .doc(chatRoomId)
        .collection('messages')
        .orderBy('timestamp', descending: true)
        .snapshots()
        .map((snapshot) {
      return snapshot.docs
          .map((doc) => ChatMessage.fromFirestore(doc))
          .toList();
    });
  }

  // Mark messages as read
  Future<void> markAsRead(String chatRoomId, String messageId) async {
    await _firestore
        .collection('chatRooms')
        .doc(chatRoomId)
        .collection('messages')
        .doc(messageId)
        .update({'isRead': true});
  }

  // Delete message
  Future<void> deleteMessage(String chatRoomId, String messageId) async {
    await _firestore
        .collection('chatRooms')
        .doc(chatRoomId)
        .collection('messages')
        .doc(messageId)
        .delete();
  }
}
```

### Chat Screen

```dart
// lib/screens/chat/chat_screen.dart
import 'package:flutter/material.dart';
import '../../services/chat_service.dart';
import '../../services/auth_service.dart';
import '../../models/chat_message.dart';

class ChatScreen extends StatefulWidget {
  final String otherUserId;
  final String otherUserName;

  const ChatScreen({
    super.key,
    required this.otherUserId,
    required this.otherUserName,
  });

  @override
  State<ChatScreen> createState() => _ChatScreenState();
}

class _ChatScreenState extends State<ChatScreen> {
  final _chatService = ChatService();
  final _authService = AuthService();
  final _messageController = TextEditingController();
  final _scrollController = ScrollController();

  late String _chatRoomId;

  @override
  void initState() {
    super.initState();
    final currentUserId = _authService.currentUser!.uid;
    _chatRoomId = _chatService.getChatRoomId(currentUserId, widget.otherUserId);
  }

  @override
  void dispose() {
    _messageController.dispose();
    _scrollController.dispose();
    super.dispose();
  }

  Future<void> _sendMessage() async {
    final text = _messageController.text.trim();
    if (text.isEmpty) return;

    final currentUser = _authService.currentUser!;

    try {
      await _chatService.sendMessage(
        chatRoomId: _chatRoomId,
        text: text,
        senderId: currentUser.uid,
        senderName: currentUser.displayName ?? 'User',
      );

      _messageController.clear();

      // Scroll to bottom
      if (_scrollController.hasClients) {
        _scrollController.animateTo(
          0,
          duration: const Duration(milliseconds: 300),
          curve: Curves.easeOut,
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Failed to send: $e')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.otherUserName),
      ),
      body: Column(
        children: [
          // Messages list
          Expanded(
            child: StreamBuilder<List<ChatMessage>>(
              stream: _chatService.getMessagesStream(_chatRoomId),
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
                          Icons.chat_bubble_outline,
                          size: 64,
                          color: Colors.grey.shade300,
                        ),
                        const SizedBox(height: 16),
                        Text(
                          'No messages yet',
                          style: TextStyle(color: Colors.grey.shade600),
                        ),
                        const SizedBox(height: 8),
                        Text(
                          'Say hi to ${widget.otherUserName}!',
                          style: TextStyle(color: Colors.grey.shade500),
                        ),
                      ],
                    ),
                  );
                }

                final messages = snapshot.data!;
                final currentUserId = _authService.currentUser!.uid;

                return ListView.builder(
                  controller: _scrollController,
                  reverse: true,  // Latest at bottom
                  padding: const EdgeInsets.all(16),
                  itemCount: messages.length,
                  itemBuilder: (context, index) {
                    final message = messages[index];
                    final isMe = message.senderId == currentUserId;

                    return _buildMessageBubble(message, isMe);
                  },
                );
              },
            ),
          ),

          // Message input
          _buildMessageInput(),
        ],
      ),
    );
  }

  Widget _buildMessageBubble(ChatMessage message, bool isMe) {
    return Align(
      alignment: isMe ? Alignment.centerRight : Alignment.centerLeft,
      child: Container(
        margin: const EdgeInsets.only(bottom: 12),
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
        constraints: BoxConstraints(
          maxWidth: MediaQuery.of(context).size.width * 0.7,
        ),
        decoration: BoxDecoration(
          color: isMe ? Theme.of(context).primaryColor : Colors.grey.shade200,
          borderRadius: BorderRadius.only(
            topLeft: const Radius.circular(16),
            topRight: const Radius.circular(16),
            bottomLeft: Radius.circular(isMe ? 16 : 4),
            bottomRight: Radius.circular(isMe ? 4 : 16),
          ),
        ),
        child: Column(
          crossAxisAlignment:
              isMe ? CrossAxisAlignment.end : CrossAxisAlignment.start,
          children: [
            Text(
              message.text,
              style: TextStyle(
                color: isMe ? Colors.white : Colors.black87,
                fontSize: 16,
              ),
            ),
            const SizedBox(height: 4),
            Text(
              _formatTime(message.timestamp),
              style: TextStyle(
                color: isMe ? Colors.white70 : Colors.grey.shade600,
                fontSize: 12,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildMessageInput() {
    return Container(
      padding: const EdgeInsets.all(8.0),
      decoration: BoxDecoration(
        color: Colors.grey.shade100,
        border: Border(top: BorderSide(color: Colors.grey.shade300)),
      ),
      child: Row(
        children: [
          Expanded(
            child: TextField(
              controller: _messageController,
              decoration: InputDecoration(
                hintText: 'Type a message...',
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(24),
                  borderSide: BorderSide.none,
                ),
                filled: true,
                fillColor: Colors.white,
                contentPadding: const EdgeInsets.symmetric(
                  horizontal: 20,
                  vertical: 10,
                ),
              ),
              textCapitalization: TextCapitalization.sentences,
              onSubmitted: (_) => _sendMessage(),
            ),
          ),
          const SizedBox(width: 8),
          CircleAvatar(
            backgroundColor: Theme.of(context).primaryColor,
            child: IconButton(
              icon: const Icon(Icons.send, color: Colors.white),
              onPressed: _sendMessage,
            ),
          ),
        ],
      ),
    );
  }

  String _formatTime(DateTime dateTime) {
    final now = DateTime.now();
    final difference = now.difference(dateTime);

    if (difference.inDays > 0) {
      return '${dateTime.day}/${dateTime.month}/${dateTime.year}';
    } else if (difference.inHours > 0) {
      return '${difference.inHours}h ago';
    } else if (difference.inMinutes > 0) {
      return '${difference.inMinutes}m ago';
    } else {
      return 'Just now';
    }
  }
}
```

---

## Part 2: Online/Offline Presence

Track when users are online or offline!

### Presence Service

```dart
// lib/services/presence_service.dart
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_database/firebase_database.dart';

class PresenceService {
  final FirebaseFirestore _firestore = FirebaseFirestore.instance;
  final FirebaseDatabase _realtimeDb = FirebaseDatabase.instance;

  // Set user online
  Future<void> setOnline(String userId) async {
    final userStatusRef = _realtimeDb.ref('status/$userId');

    // Update Realtime Database
    await userStatusRef.set({
      'online': true,
      'lastSeen': ServerValue.timestamp,
    });

    // Set up disconnect handler (automatic offline when connection lost)
    await userStatusRef.onDisconnect().set({
      'online': false,
      'lastSeen': ServerValue.timestamp,
    });

    // Also update Firestore for queries
    await _firestore.collection('users').doc(userId).update({
      'online': true,
      'lastSeen': FieldValue.serverTimestamp(),
    });
  }

  // Set user offline
  Future<void> setOffline(String userId) async {
    await _realtimeDb.ref('status/$userId').set({
      'online': false,
      'lastSeen': ServerValue.timestamp,
    });

    await _firestore.collection('users').doc(userId).update({
      'online': false,
      'lastSeen': FieldValue.serverTimestamp(),
    });
  }

  // Listen to user's online status
  Stream<bool> getUserOnlineStatus(String userId) {
    return _realtimeDb
        .ref('status/$userId/online')
        .onValue
        .map((event) => event.snapshot.value as bool? ?? false);
  }

  // Get last seen time
  Future<DateTime?> getLastSeen(String userId) async {
    final doc = await _firestore.collection('users').doc(userId).get();
    final data = doc.data();

    if (data != null && data['lastSeen'] != null) {
      return (data['lastSeen'] as Timestamp).toDate();
    }
    return null;
  }
}
```

### Online Indicator Widget

```dart
// lib/widgets/online_indicator.dart
import 'package:flutter/material.dart';
import '../services/presence_service.dart';

class OnlineIndicator extends StatelessWidget {
  final String userId;
  final double size;

  const OnlineIndicator({
    super.key,
    required this.userId,
    this.size = 12,
  });

  @override
  Widget build(BuildContext context) {
    final presenceService = PresenceService();

    return StreamBuilder<bool>(
      stream: presenceService.getUserOnlineStatus(userId),
      builder: (context, snapshot) {
        final isOnline = snapshot.data ?? false;

        return Container(
          width: size,
          height: size,
          decoration: BoxDecoration(
            color: isOnline ? Colors.green : Colors.grey,
            shape: BoxShape.circle,
            border: Border.all(color: Colors.white, width: 2),
          ),
        );
      },
    );
  }
}
```

---

## Part 3: Typing Indicator

Show when someone is typing!

### Typing Service

```dart
// lib/services/typing_service.dart
import 'package:cloud_firestore/cloud_firestore.dart';

class TypingService {
  final FirebaseFirestore _firestore = FirebaseFirestore.instance;

  // Set typing status
  Future<void> setTyping({
    required String chatRoomId,
    required String userId,
    required bool isTyping,
  }) async {
    await _firestore.collection('chatRooms').doc(chatRoomId).set({
      'typing': {
        userId: isTyping,
      },
    }, SetOptions(merge: true));
  }

  // Get typing status stream
  Stream<bool> getTypingStatus({
    required String chatRoomId,
    required String otherUserId,
  }) {
    return _firestore
        .collection('chatRooms')
        .doc(chatRoomId)
        .snapshots()
        .map((snapshot) {
      final data = snapshot.data();
      if (data == null || data['typing'] == null) return false;

      final typing = data['typing'] as Map<String, dynamic>;
      return typing[otherUserId] == true;
    });
  }
}
```

### Add to Chat Screen

```dart
// In ChatScreen, add typing indicator
Widget _buildTypingIndicator() {
  return StreamBuilder<bool>(
    stream: _typingService.getTypingStatus(
      chatRoomId: _chatRoomId,
      otherUserId: widget.otherUserId,
    ),
    builder: (context, snapshot) {
      if (snapshot.data == true) {
        return Padding(
          padding: const EdgeInsets.all(8.0),
          child: Row(
            children: [
              const SizedBox(width: 16),
              ...List.generate(
                3,
                (index) => Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 2),
                  child: _AnimatedDot(delay: index * 200),
                ),
              ),
              const SizedBox(width: 8),
              Text(
                '${widget.otherUserName} is typing...',
                style: TextStyle(
                  color: Colors.grey.shade600,
                  fontStyle: FontStyle.italic,
                ),
              ),
            ],
          ),
        );
      }
      return const SizedBox.shrink();
    },
  );
}

// Animated dot widget
class _AnimatedDot extends StatefulWidget {
  final int delay;

  const _AnimatedDot({required this.delay});

  @override
  State<_AnimatedDot> createState() => _AnimatedDotState();
}

class _AnimatedDotState extends State<_AnimatedDot>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 600),
    )..repeat();

    Future.delayed(Duration(milliseconds: widget.delay), () {
      if (mounted) _controller.forward();
    });
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return FadeTransition(
      opacity: _controller,
      child: Container(
        width: 8,
        height: 8,
        decoration: BoxDecoration(
          color: Colors.grey.shade400,
          shape: BoxShape.circle,
        ),
      ),
    );
  }
}

// Update TextField to track typing
TextField(
  controller: _messageController,
  onChanged: (text) {
    _typingService.setTyping(
      chatRoomId: _chatRoomId,
      userId: _authService.currentUser!.uid,
      isTyping: text.isNotEmpty,
    );
  },
  // ... rest of TextField
)
```

---

## Part 4: Live Data Updates (Like Counter)

Build a live like counter that updates in real-time!

```dart
// Like button with real-time count
class LiveLikeButton extends StatelessWidget {
  final String postId;

  const LiveLikeButton({super.key, required this.postId});

  @override
  Widget build(BuildContext context) {
    final authService = AuthService();
    final currentUserId = authService.currentUser?.uid;

    return StreamBuilder<DocumentSnapshot>(
      stream: FirebaseFirestore.instance
          .collection('posts')
          .doc(postId)
          .snapshots(),
      builder: (context, snapshot) {
        if (!snapshot.hasData) {
          return const IconButton(
            icon: Icon(Icons.favorite_border),
            onPressed: null,
          );
        }

        final data = snapshot.data!.data() as Map<String, dynamic>?;
        final likes = data?['likes'] as List? ?? [];
        final hasLiked = currentUserId != null && likes.contains(currentUserId);
        final likeCount = likes.length;

        return Row(
          children: [
            IconButton(
              icon: Icon(
                hasLiked ? Icons.favorite : Icons.favorite_border,
                color: hasLiked ? Colors.red : null,
              ),
              onPressed: () async {
                if (currentUserId == null) return;

                if (hasLiked) {
                  await FirebaseFirestore.instance
                      .collection('posts')
                      .doc(postId)
                      .update({
                    'likes': FieldValue.arrayRemove([currentUserId]),
                  });
                } else {
                  await FirebaseFirestore.instance
                      .collection('posts')
                      .doc(postId)
                      .update({
                    'likes': FieldValue.arrayUnion([currentUserId]),
                  });
                }
              },
            ),
            Text('$likeCount'),
          ],
        );
      },
    );
  }
}
```

---

## Best Practices for Real-Time Features

### ✅ DO:
1. **Use StreamBuilder** for automatic UI updates
2. **Dispose streams** properly to prevent memory leaks
3. **Limit real-time listeners** (don't listen to huge collections)
4. **Debounce rapid updates** (typing indicators)
5. **Show loading states** while connecting
6. **Handle offline mode** gracefully
7. **Set up presence** on app start, clear on exit

### ❌ DON'T:
1. **Don't listen to entire collections** (use queries with limits)
2. **Don't forget to cancel listeners** (memory leaks!)
3. **Don't update on every keystroke** (use debounce)
4. **Don't rely solely on real-time** (handle offline)
5. **Don't leave presence "online" forever** (set onDisconnect)

---

## Quiz Time! 🧠

### Question 1
What's the main advantage of using StreamBuilder with Firestore snapshots()?

A) It's faster
B) It automatically rebuilds the UI when data changes
C) It uses less memory
D) It's required by Firebase

### Question 2
Why use onDisconnect() for presence detection?

A) It's faster
B) It automatically sets user offline when they lose connection
C) Firebase requires it
D) It saves battery

### Question 3
What should you do to prevent memory leaks with real-time listeners?

A) Use more listeners
B) Restart the app periodically
C) Properly dispose streams and controllers
D) Use HTTP instead

---

## Answer Key

### Answer 1: B
**Correct**: It automatically rebuilds the UI when data changes

StreamBuilder listens to Firestore snapshots (a Stream) and automatically rebuilds its child widget whenever new data arrives, providing seamless real-time updates without manual setState() calls.

### Answer 2: B
**Correct**: It automatically sets user offline when they lose connection

onDisconnect() is a Firebase Realtime Database feature that executes specified operations when a client disconnects (app closes, network lost, etc.), ensuring accurate presence status even if the app crashes.

### Answer 3: C
**Correct**: Properly dispose streams and controllers

Always cancel stream subscriptions and dispose controllers in dispose() method to prevent memory leaks. Unmanaged streams continue consuming resources even after widgets are destroyed.

---

## What's Next?

You've mastered real-time features! In the next lesson, we'll add **Push Notifications and Analytics** to make your app even more engaging.

**Coming up in Lesson 7: Push Notifications & Analytics**
- Firebase Cloud Messaging (FCM)
- Push notifications
- Analytics events
- User engagement tracking

---

## Key Takeaways

✅ Firebase snapshots() provide real-time data streams
✅ StreamBuilder automatically rebuilds UI when data changes
✅ Presence detection shows online/offline status
✅ Use Realtime Database for ultra-low latency features
✅ Always dispose streams to prevent memory leaks
✅ Typing indicators enhance chat UX
✅ Real-time like counters create engaging experiences
✅ onDisconnect() ensures accurate presence even after crashes

**You can now build real-time apps like WhatsApp!** 💬
