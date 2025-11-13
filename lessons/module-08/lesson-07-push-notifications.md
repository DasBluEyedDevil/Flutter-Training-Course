# Module 8, Lesson 7: Push Notifications & Analytics

## What You'll Learn
By the end of this lesson, you'll know how to send push notifications to users and track app usage with Firebase Cloud Messaging (FCM) and Firebase Analytics.

---

## Why This Matters

**Push notifications and analytics are essential for app success.**

### Push Notifications:
- **Increase engagement by 88%** (users return more often)
- **Send time-sensitive updates** (messages, orders, breaking news)
- **Re-engage inactive users** (bring them back to your app)
- **95% of successful apps** use push notifications

### Analytics:
- **Understand user behavior** (what features they use most)
- **Track conversion rates** (signup, purchases)
- **Identify problems** (where users get stuck)
- **Data-driven decisions** (build what users actually want)

**Without notifications and analytics, you're flying blind!**

---

## Real-World Analogy: The Doorbell & Security Camera

### Push Notifications = Doorbell
- 🔔 **Alert you immediately** when something important happens
- 📬 **Delivery notifications**: "Package arrived!"
- 👋 **Visitor alerts**: "Someone's at your door!"
- ⏰ **Reminders**: "Don't forget your appointment!"

### Analytics = Security Camera
- 📹 **Record what happens** in your app
- 👁️ **See user patterns** (when they visit, what they do)
- 📊 **Analyze footage** to improve security
- 🔍 **Find issues** before they become problems

**Together, they keep you connected to users and understand their behavior!**

---

## Part 1: Firebase Cloud Messaging (FCM)

### How Push Notifications Work

```
1. App requests permission
   ↓
2. FCM generates unique token for device
   ↓
3. App sends token to your server (or Firestore)
   ↓
4. Server sends notification to FCM
   ↓
5. FCM delivers to device
   ↓
6. Notification appears on user's screen
```

---

## Setup FCM in Flutter

### 1. Add Package

```yaml
# pubspec.yaml
dependencies:
  firebase_core: ^4.2.0
  firebase_messaging: ^15.2.7
  flutter_local_notifications: ^18.0.1  # For foreground notifications
```

Run:
```bash
flutter pub get
```

### 2. Android Configuration

Edit `android/app/src/main/AndroidManifest.xml`:

```xml
<manifest>
    <application>
        <!-- Add inside <application> tag -->
        <meta-data
            android:name="com.google.firebase.messaging.default_notification_channel_id"
            android:value="high_importance_channel" />
    </application>

    <!-- Add permissions -->
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
</manifest>
```

### 3. iOS Configuration

Edit `ios/Runner/Info.plist`:

```xml
<dict>
    <!-- Add this -->
    <key>FirebaseAppDelegateProxyEnabled</key>
    <false/>
</dict>
```

Request permission in iOS (done programmatically).

---

## Notification Service

```dart
// lib/services/notification_service.dart
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:cloud_firestore/cloud_firestore.dart';

// Handle background messages (must be top-level function!)
@pragma('vm:entry-point')
Future<void> firebaseMessagingBackgroundHandler(RemoteMessage message) async {
  print('Background message: ${message.messageId}');
}

class NotificationService {
  final FirebaseMessaging _messaging = FirebaseMessaging.instance;
  final FlutterLocalNotificationsPlugin _localNotifications =
      FlutterLocalNotificationsPlugin();

  // Singleton pattern
  static final NotificationService _instance = NotificationService._internal();
  factory NotificationService() => _instance;
  NotificationService._internal();

  // Initialize FCM and local notifications
  Future<void> initialize() async {
    // Request permission (iOS)
    NotificationSettings settings = await _messaging.requestPermission(
      alert: true,
      badge: true,
      sound: true,
      provisional: false,
    );

    if (settings.authorizationStatus == AuthorizationStatus.authorized) {
      print('✓ User granted notification permission');
    } else {
      print('✗ User declined notification permission');
      return;
    }

    // Initialize local notifications (for foreground)
    const androidInit = AndroidInitializationSettings('@mipmap/ic_launcher');
    const iosInit = DarwinInitializationSettings();
    const initSettings = InitializationSettings(
      android: androidInit,
      iOS: iosInit,
    );

    await _localNotifications.initialize(
      initSettings,
      onDidReceiveNotificationResponse: (details) {
        // Handle notification tap
        print('Notification tapped: ${details.payload}');
      },
    );

    // Create Android notification channel
    const androidChannel = AndroidNotificationChannel(
      'high_importance_channel',
      'High Importance Notifications',
      description: 'This channel is used for important notifications.',
      importance: Importance.high,
    );

    await _localNotifications
        .resolvePlatformSpecificImplementation<
            AndroidFlutterLocalNotificationsPlugin>()
        ?.createNotificationChannel(androidChannel);

    // Get FCM token
    final token = await _messaging.getToken();
    print('FCM Token: $token');

    // Save token to Firestore (so server can send notifications)
    if (token != null) {
      await _saveTokenToFirestore(token);
    }

    // Listen for token refresh
    _messaging.onTokenRefresh.listen(_saveTokenToFirestore);

    // Handle foreground messages
    FirebaseMessaging.onMessage.listen(_handleForegroundMessage);

    // Handle background messages
    FirebaseMessaging.onBackgroundMessage(firebaseMessagingBackgroundHandler);

    // Handle notification tap (app was terminated)
    FirebaseMessaging.onMessageOpenedApp.listen(_handleNotificationTap);
  }

  // Save FCM token to Firestore
  Future<void> _saveTokenToFirestore(String token) async {
    final userId = FirebaseAuth.instance.currentUser?.uid;
    if (userId == null) return;

    await FirebaseFirestore.instance
        .collection('users')
        .doc(userId)
        .update({
      'fcmTokens': FieldValue.arrayUnion([token]),
      'lastTokenUpdate': FieldValue.serverTimestamp(),
    });
  }

  // Handle foreground messages (app is open)
  Future<void> _handleForegroundMessage(RemoteMessage message) async {
    print('Foreground message: ${message.messageId}');

    final notification = message.notification;
    if (notification == null) return;

    // Show local notification
    await _localNotifications.show(
      notification.hashCode,
      notification.title,
      notification.body,
      const NotificationDetails(
        android: AndroidNotificationDetails(
          'high_importance_channel',
          'High Importance Notifications',
          importance: Importance.high,
          priority: Priority.high,
        ),
        iOS: DarwinNotificationDetails(),
      ),
      payload: message.data.toString(),
    );
  }

  // Handle notification tap
  void _handleNotificationTap(RemoteMessage message) {
    print('Notification tapped: ${message.messageId}');
    // Navigate to specific screen based on message.data
  }

  // Subscribe to topic
  Future<void> subscribeToTopic(String topic) async {
    await _messaging.subscribeToTopic(topic);
    print('Subscribed to topic: $topic');
  }

  // Unsubscribe from topic
  Future<void> unsubscribeFromTopic(String topic) async {
    await _messaging.unsubscribeFromTopic(topic);
    print('Unsubscribed from topic: $topic');
  }
}
```

---

## Initialize in main.dart

```dart
// lib/main.dart
import 'package:firebase_core/firebase_core.dart';
import 'services/notification_service.dart';
import 'firebase_options.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );

  // Initialize notifications
  await NotificationService().initialize();

  runApp(const MyApp());
}
```

---

## Sending Notifications

### Method 1: Firebase Console (Manual)

1. Go to Firebase Console → Cloud Messaging
2. Click **"Send your first message"**
3. Enter:
   - **Notification title**: "New Message!"
   - **Notification text**: "You have a new message from John"
4. Click **"Send test message"**
5. Paste your FCM token
6. Click **"Test"**

### Method 2: Send to Topics (Best for Broadcasts)

```dart
// Subscribe users to topics
await NotificationService().subscribeToTopic('news');
await NotificationService().subscribeToTopic('promotions');
```

Then send via Firebase Console to "news" topic.

### Method 3: Send via Cloud Functions (Production)

Create a Cloud Function to send notifications:

```javascript
// Firebase Cloud Function (JavaScript/TypeScript)
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendNotificationOnNewMessage = functions.firestore
  .document('chatRooms/{chatRoomId}/messages/{messageId}')
  .onCreate(async (snapshot, context) => {
    const message = snapshot.data();

    // Get recipient's FCM token
    const recipientDoc = await admin.firestore()
      .collection('users')
      .doc(message.recipientId)
      .get();

    const fcmTokens = recipientDoc.data().fcmTokens || [];

    if (fcmTokens.length === 0) return;

    // Send notification
    const payload = {
      notification: {
        title: 'New Message',
        body: `${message.senderName}: ${message.text}`,
      },
      data: {
        chatRoomId: context.params.chatRoomId,
        messageId: context.params.messageId,
      },
    };

    await admin.messaging().sendToDevice(fcmTokens, payload);
  });
```

---

## Notification Settings Screen

```dart
// lib/screens/settings/notification_settings_screen.dart
import 'package:flutter/material.dart';
import '../../services/notification_service.dart';

class NotificationSettingsScreen extends StatefulWidget {
  const NotificationSettingsScreen({super.key});

  @override
  State<NotificationSettingsScreen> createState() =>
      _NotificationSettingsScreenState();
}

class _NotificationSettingsScreenState
    extends State<NotificationSettingsScreen> {
  bool _newsNotifications = true;
  bool _messagesNotifications = true;
  bool _promotionsNotifications = false;

  final _notificationService = NotificationService();

  void _toggleNotification(String topic, bool value) async {
    if (value) {
      await _notificationService.subscribeToTopic(topic);
    } else {
      await _notificationService.unsubscribeFromTopic(topic);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Notification Settings'),
      ),
      body: ListView(
        children: [
          SwitchListTile(
            title: const Text('News & Updates'),
            subtitle: const Text('Get notified about news and updates'),
            value: _newsNotifications,
            onChanged: (value) {
              setState(() => _newsNotifications = value);
              _toggleNotification('news', value);
            },
          ),
          SwitchListTile(
            title: const Text('Messages'),
            subtitle: const Text('Get notified about new messages'),
            value: _messagesNotifications,
            onChanged: (value) {
              setState(() => _messagesNotifications = value);
              _toggleNotification('messages', value);
            },
          ),
          SwitchListTile(
            title: const Text('Promotions'),
            subtitle: const Text('Get notified about special offers'),
            value: _promotionsNotifications,
            onChanged: (value) {
              setState(() => _promotionsNotifications = value);
              _toggleNotification('promotions', value);
            },
          ),
        ],
      ),
    );
  }
}
```

---

## Part 2: Firebase Analytics

### Setup Analytics

Firebase Analytics is included with `firebase_core` - no extra package needed!

### Track Events

```dart
// lib/services/analytics_service.dart
import 'package:firebase_analytics/firebase_analytics.dart';

class AnalyticsService {
  final FirebaseAnalytics _analytics = FirebaseAnalytics.instance;

  // Log screen view
  Future<void> logScreenView(String screenName) async {
    await _analytics.logScreenView(screenName: screenName);
  }

  // Log button click
  Future<void> logButtonClick(String buttonName) async {
    await _analytics.logEvent(
      name: 'button_click',
      parameters: {
        'button_name': buttonName,
        'timestamp': DateTime.now().toIso8601String(),
      },
    );
  }

  // Log post creation
  Future<void> logPostCreated(String postId) async {
    await _analytics.logEvent(
      name: 'post_created',
      parameters: {
        'post_id': postId,
        'timestamp': DateTime.now().toIso8601String(),
      },
    );
  }

  // Log signup
  Future<void> logSignUp(String method) async {
    await _analytics.logSignUp(signUpMethod: method);
  }

  // Log login
  Future<void> logLogin(String method) async {
    await _analytics.logLogin(loginMethod: method);
  }

  // Log purchase (for e-commerce)
  Future<void> logPurchase({
    required double value,
    required String currency,
    required String itemId,
  }) async {
    await _analytics.logPurchase(
      value: value,
      currency: currency,
      items: [
        AnalyticsEventItem(
          itemId: itemId,
          itemName: 'Product Name',
        ),
      ],
    );
  }

  // Set user properties
  Future<void> setUserProperties({
    required String userId,
    String? userType,
  }) async {
    await _analytics.setUserId(id: userId);

    if (userType != null) {
      await _analytics.setUserProperty(
        name: 'user_type',
        value: userType,
      );
    }
  }
}
```

### Track Navigation

```dart
// main.dart - Track all screen views
class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      navigatorObservers: [
        FirebaseAnalyticsObserver(analytics: FirebaseAnalytics.instance),
      ],
      home: const HomeScreen(),
    );
  }
}
```

### Usage Example

```dart
// In your screens
final analytics = AnalyticsService();

// Track screen view
@override
void initState() {
  super.initState();
  analytics.logScreenView('Home Screen');
}

// Track button clicks
ElevatedButton(
  onPressed: () {
    analytics.logButtonClick('create_post_button');
    // ... button action
  },
  child: const Text('Create Post'),
)

// Track signup
await authService.register(...);
analytics.logSignUp('email');

// Track login
await authService.login(...);
analytics.logLogin('google');
```

---

## View Analytics Data

### Firebase Console

1. Go to Firebase Console → Analytics
2. View dashboards:
   - **Users**: Active users, new users
   - **Events**: All tracked events
   - **Conversions**: Signup, purchases
   - **Engagement**: Session duration, screens per session

### Custom Reports

1. Analytics → Events
2. Click "Create custom report"
3. Select metrics and dimensions
4. Save for recurring analysis

---

## Best Practices

### Notifications ✅ DO:
1. **Request permission at the right time** (after user sees value)
2. **Personalize notifications** (use user's name, relevant content)
3. **Don't spam** (max 2-3 per day)
4. **Provide value** (useful info, not just "Open the app!")
5. **Allow unsubscribe** (topic-based subscriptions)
6. **Test on real devices** (not just emulator)
7. **Handle tap actions** (navigate to relevant screen)

### Notifications ❌ DON'T:
1. **Don't request permission immediately** on app launch
2. **Don't send at bad times** (2am notifications = angry users)
3. **Don't send generic messages** ("Check out our app!")
4. **Don't ignore user preferences** (respect opt-outs)
5. **Don't forget to test** on iOS and Android

### Analytics ✅ DO:
1. **Track key user actions** (signup, purchase, share)
2. **Set user properties** (subscription type, preferences)
3. **Create conversion funnels** (how many complete signup?)
4. **Review weekly** (make data-driven decisions)
5. **Respect privacy** (don't track sensitive data)

### Analytics ❌ DON'T:
1. **Don't track PII** (passwords, credit cards, SSN)
2. **Don't track everything** (focus on meaningful events)
3. **Don't ignore the data** (collect but never analyze = waste)
4. **Don't violate privacy laws** (GDPR, CCPA compliance)

---

## Quiz Time! 🧠

### Question 1
Why save FCM tokens to Firestore?

A) Firebase requires it
B) So you can send targeted notifications to specific users
C) To make the app faster
D) It's not necessary

### Question 2
When should you request notification permission?

A) Immediately on app launch
B) After users see the value of notifications
C) Never
D) After they create an account

### Question 3
What should you avoid tracking with Firebase Analytics?

A) Button clicks
B) Screen views
C) Passwords and credit card numbers
D) User signups

---

## Answer Key

### Answer 1: B
**Correct**: So you can send targeted notifications to specific users

FCM tokens are unique per device. By saving them to Firestore with the user's ID, you can send notifications to specific users (e.g., "John sent you a message"). Without storing tokens, you can only broadcast to all users or topics.

### Answer 2: B
**Correct**: After users see the value of notifications

If you ask for permission immediately, users don't understand why they need it and often decline. Show value first (e.g., let them start a chat), then request permission with context ("Get notified when you receive messages").

### Answer 3: C
**Correct**: Passwords and credit card numbers

NEVER track personally identifiable information (PII) or sensitive data like passwords, credit cards, SSN, health information. This violates privacy laws (GDPR, CCPA) and puts users at risk. Track user behavior, not sensitive data.

---

## What's Next?

You've learned push notifications and analytics! In the final lesson, we'll build a **Complete Firebase Mini-Project** that combines everything from Module 8.

**Coming up in Lesson 8: Mini-Project - Complete Firebase App**
- Full-stack social app
- Authentication
- Real-time chat
- File uploads
- Push notifications
- Production-ready code

---

## Key Takeaways

✅ Firebase Cloud Messaging (FCM) sends push notifications to users
✅ Request permission after users see notification value
✅ Save FCM tokens to Firestore for targeted notifications
✅ Use topics for broadcast notifications (news, promotions)
✅ firebase_analytics tracks user behavior automatically
✅ Track meaningful events (signup, purchase, key actions)
✅ Never track sensitive data (passwords, credit cards)
✅ Review analytics weekly to make data-driven decisions
✅ Respect user privacy and comply with GDPR/CCPA

**You can now build engaging apps with notifications and understand user behavior!** 📊
