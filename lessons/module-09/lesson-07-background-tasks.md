# Lesson 7: Background Tasks & Workmanager

## What You'll Learn
- Understanding background execution in mobile apps
- Using Workmanager for scheduled background tasks
- One-time vs periodic background work
- Handling constraints (network, battery, charging)
- Data sync and background uploads
- Best practices for battery efficiency

## Concept First: What Are Background Tasks?

### Real-World Analogy
Think of background tasks like a **scheduled cleaning service** for your house:
- **One-Time Task** = "Clean before the party tonight"
- **Periodic Task** = "Clean every Tuesday at 3 PM"
- **Constraints** = "Only clean when I'm not home and it's daylight"

Just like a cleaning service works when you're away, background tasks run when your app is closed or minimized!

### Why This Matters
Background tasks enable critical features:

1. **Data Sync**: Upload photos, sync notes (Google Photos, Evernote)
2. **Content Updates**: Fetch news, update widgets (News apps)
3. **Maintenance**: Clean cache, compress files
4. **Analytics**: Send usage data periodically
5. **Notifications**: Check for new messages (Email apps)

According to Google, proper background task management can reduce battery drain by 40% compared to naive implementations!

---

## Background Execution: The Challenges

### Platform Restrictions

Modern mobile OSes heavily restrict background work to save battery:

**iOS:**
- ❌ No continuous background execution (with exceptions)
- ✅ BGTaskScheduler for periodic tasks
- ⏰ Tasks run at OS discretion (not guaranteed timing)
- 🔋 Tasks killed if battery is low

**Android:**
- ✅ WorkManager for reliable scheduled work
- ⏰ Minimum 15-minute intervals for periodic work
- 🔋 Doze mode limits background tasks
- ✅ More flexibility than iOS

**Key Takeaway:** Background tasks are **not real-time**. Use them for deferrable work, not time-critical operations!

---

## Setting Up Workmanager

### Installation

**pubspec.yaml:**
```yaml
dependencies:
  flutter:
    sdk: flutter
  workmanager: ^0.9.0+3  # Latest version (Sept 2025)
```

```bash
flutter pub get
```

### Android Configuration

**android/app/src/main/AndroidManifest.xml:**
```xml
<manifest>
    <uses-permission android:name="android.permission.WAKE_LOCK"/>
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>
    <uses-permission android:name="android.permission.INTERNET"/>  <!-- If network needed -->

    <application>
        <receiver
            android:name="androidx.work.impl.background.systemalarm.RescheduleReceiver"
            android:enabled="true"
            android:exported="false"/>
    </application>
</manifest>
```

### iOS Configuration

**ios/Runner/Info.plist:**
```xml
<dict>
    <key>BGTaskSchedulerPermittedIdentifiers</key>
    <array>
        <string>com.yourcompany.app.refresh</string>
        <string>com.yourcompany.app.processing</string>
    </array>
</dict>
```

**ios/Runner/AppDelegate.swift:**
```swift
import UIKit
import Flutter
import workmanager

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
    GeneratedPluginRegistrant.register(with: self)

    WorkmanagerPlugin.setPluginRegistrantCallback { registry in
        GeneratedPluginRegistrant.register(with: registry)
    }

    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }
}
```

---

## Basic Workmanager Usage

### Step 1: Initialize Workmanager

```dart
import 'package:flutter/material.dart';
import 'package:workmanager/workmanager.dart';

// IMPORTANT: This callback MUST be a top-level function
@pragma('vm:entry-point')
void callbackDispatcher() {
  Workmanager().executeTask((task, inputData) {
    print('Background task started: $task');

    // Perform your background work here
    switch (task) {
      case 'syncData':
        _syncDataToServer();
        break;
      case 'cleanCache':
        _cleanOldCache();
        break;
      case 'updateWidgets':
        _updateHomeScreenWidgets();
        break;
      default:
        print('Unknown task: $task');
    }

    // Return true if task succeeded
    return Future.value(true);
  });
}

Future<void> _syncDataToServer() async {
  print('Syncing data...');
  // Simulate network call
  await Future.delayed(Duration(seconds: 2));
  print('Data synced!');
}

Future<void> _cleanOldCache() async {
  print('Cleaning cache...');
  await Future.delayed(Duration(seconds: 1));
  print('Cache cleaned!');
}

Future<void> _updateHomeScreenWidgets() async {
  print('Updating widgets...');
  await Future.delayed(Duration(seconds: 1));
  print('Widgets updated!');
}

void main() {
  WidgetsFlutterBinding.ensureInitialized();

  // Initialize Workmanager
  Workmanager().initialize(
    callbackDispatcher,
    isInDebugMode: true,  // Shows notifications on Android
  );

  runApp(MyApp());
}
```

### Step 2: Register One-Time Tasks

```dart
class BackgroundTasksScreen extends StatelessWidget {
  Future<void> _registerOneTimeTask() async {
    await Workmanager().registerOneOffTask(
      'unique-task-id',  // Unique identifier
      'syncData',        // Task name (matches switch case)
      inputData: {       // Optional data to pass
        'userId': '123',
        'timestamp': DateTime.now().toIso8601String(),
      },
    );

    print('One-time task registered!');
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Background Tasks')),
      body: Center(
        child: ElevatedButton(
          onPressed: _registerOneTimeTask,
          child: Text('Schedule One-Time Sync'),
        ),
      ),
    );
  }
}
```

### Step 3: Register Periodic Tasks

```dart
Future<void> _registerPeriodicTask() async {
  await Workmanager().registerPeriodicTask(
    'periodic-sync',  // Unique ID
    'syncData',       // Task name
    frequency: Duration(hours: 1),  // Run every hour (minimum 15 minutes)
    constraints: Constraints(
      networkType: NetworkType.connected,     // Require internet
      requiresBatteryNotLow: true,            // Don't run if battery low
      requiresCharging: false,                // Run even when not charging
      requiresDeviceIdle: false,              // Run even when device in use
      requiresStorageNotLow: true,            // Don't run if storage low
    ),
    inputData: {
      'periodic': true,
    },
    existingWorkPolicy: ExistingWorkPolicy.replace,  // Replace existing task
  );

  print('Periodic task registered!');
}
```

**Important:** Android minimum periodic interval is **15 minutes**. iOS is even less predictable!

---

## Complete Example: Photo Backup App

```dart
import 'package:flutter/material.dart';
import 'package:workmanager/workmanager.dart';
import 'package:shared_preferences/shared_preferences.dart';

// Background callback
@pragma('vm:entry-point')
void callbackDispatcher() {
  Workmanager().executeTask((task, inputData) async {
    print('🎯 Background task started: $task');
    print('📦 Input data: $inputData');

    try {
      switch (task) {
        case 'uploadPhotos':
          await _uploadPhotosToCloud(inputData);
          break;
        case 'cleanOldBackups':
          await _cleanOldBackups();
          break;
        default:
          print('❓ Unknown task: $task');
      }

      // Update last run time
      final prefs = await SharedPreferences.getInstance();
      await prefs.setString('lastBackupTime', DateTime.now().toIso8601String());

      print('✅ Task completed successfully');
      return Future.value(true);
    } catch (e) {
      print('❌ Task failed: $e');
      return Future.value(false);
    }
  });
}

Future<void> _uploadPhotosToCloud(Map<String, dynamic>? inputData) async {
  print('📤 Uploading photos to cloud...');

  // In real app:
  // 1. Get list of unsynced photos from local database
  // 2. Upload to cloud storage (Firebase, AWS S3, etc.)
  // 3. Mark as synced in database

  await Future.delayed(Duration(seconds: 3));  // Simulate upload

  final photoCount = inputData?['photoCount'] ?? 0;
  print('✅ Uploaded $photoCount photos');
}

Future<void> _cleanOldBackups() async {
  print('🧹 Cleaning old backups...');

  // In real app:
  // 1. Find backups older than X days
  // 2. Delete from cloud storage
  // 3. Update local database

  await Future.delayed(Duration(seconds: 2));  // Simulate cleanup

  print('✅ Old backups cleaned');
}

void main() {
  WidgetsFlutterBinding.ensureInitialized();

  Workmanager().initialize(
    callbackDispatcher,
    isInDebugMode: true,
  );

  runApp(MaterialApp(home: PhotoBackupApp()));
}

class PhotoBackupApp extends StatefulWidget {
  @override
  State<PhotoBackupApp> createState() => _PhotoBackupAppState();
}

class _PhotoBackupAppState extends State<PhotoBackupApp> {
  bool _autoBackupEnabled = false;
  String _lastBackupTime = 'Never';
  int _photosToBackup = 42;

  @override
  void initState() {
    super.initState();
    _loadSettings();
  }

  Future<void> _loadSettings() async {
    final prefs = await SharedPreferences.getInstance();
    setState(() {
      _autoBackupEnabled = prefs.getBool('autoBackup') ?? false;
      _lastBackupTime = prefs.getString('lastBackupTime') ?? 'Never';
    });
  }

  Future<void> _toggleAutoBackup(bool enabled) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('autoBackup', enabled);

    setState(() {
      _autoBackupEnabled = enabled;
    });

    if (enabled) {
      // Register periodic backup task
      await Workmanager().registerPeriodicTask(
        'photo-backup',
        'uploadPhotos',
        frequency: Duration(hours: 6),  // Every 6 hours
        constraints: Constraints(
          networkType: NetworkType.connected,
          requiresBatteryNotLow: true,
          requiresCharging: false,
        ),
        inputData: {
          'photoCount': _photosToBackup,
        },
        existingWorkPolicy: ExistingWorkPolicy.replace,
      );

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Auto-backup enabled! Will run every 6 hours.')),
      );
    } else {
      // Cancel periodic task
      await Workmanager().cancelByUniqueName('photo-backup');

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Auto-backup disabled')),
      );
    }
  }

  Future<void> _backupNow() async {
    // Register one-time task
    await Workmanager().registerOneOffTask(
      'backup-now-${DateTime.now().millisecondsSinceEpoch}',
      'uploadPhotos',
      inputData: {
        'photoCount': _photosToBackup,
        'manual': true,
      },
      constraints: Constraints(
        networkType: NetworkType.connected,
      ),
    );

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text('Backup started! Check notification.')),
    );
  }

  Future<void> _scheduleCleanup() async {
    await Workmanager().registerOneOffTask(
      'cleanup-${DateTime.now().millisecondsSinceEpoch}',
      'cleanOldBackups',
      initialDelay: Duration(minutes: 5),  // Run after 5 minutes
    );

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text('Cleanup scheduled in 5 minutes')),
    );
  }

  Future<void> _cancelAllTasks() async {
    await Workmanager().cancelAll();

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text('All background tasks cancelled')),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Photo Backup')),
      body: ListView(
        padding: EdgeInsets.all(16),
        children: [
          // Auto-backup toggle
          Card(
            child: SwitchListTile(
              title: Text('Auto Backup'),
              subtitle: Text(
                _autoBackupEnabled
                    ? 'Backs up every 6 hours when connected to WiFi'
                    : 'Disabled',
              ),
              value: _autoBackupEnabled,
              onChanged: _toggleAutoBackup,
              secondary: Icon(Icons.cloud_upload),
            ),
          ),

          SizedBox(height: 16),

          // Stats
          Card(
            child: Padding(
              padding: EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text('Backup Status', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                  SizedBox(height: 12),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text('Photos to backup:'),
                      Text('$_photosToBackup', style: TextStyle(fontWeight: FontWeight.bold)),
                    ],
                  ),
                  SizedBox(height: 8),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text('Last backup:'),
                      Text(_lastBackupTime == 'Never' ? 'Never' : _formatTime(_lastBackupTime)),
                    ],
                  ),
                ],
              ),
            ),
          ),

          SizedBox(height: 16),

          // Actions
          ElevatedButton.icon(
            onPressed: _backupNow,
            icon: Icon(Icons.backup),
            label: Text('Backup Now'),
            style: ElevatedButton.styleFrom(
              padding: EdgeInsets.all(16),
            ),
          ),

          SizedBox(height: 12),

          OutlinedButton.icon(
            onPressed: _scheduleCleanup,
            icon: Icon(Icons.cleaning_services),
            label: Text('Schedule Cleanup (5 min)'),
          ),

          SizedBox(height: 12),

          TextButton.icon(
            onPressed: _cancelAllTasks,
            icon: Icon(Icons.cancel, color: Colors.red),
            label: Text('Cancel All Tasks', style: TextStyle(color: Colors.red)),
          ),

          SizedBox(height: 20),

          // Info card
          Card(
            color: Colors.blue.shade50,
            child: Padding(
              padding: EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Icon(Icons.info, color: Colors.blue),
                      SizedBox(width: 8),
                      Text('How it works', style: TextStyle(fontWeight: FontWeight.bold)),
                    ],
                  ),
                  SizedBox(height: 8),
                  Text(
                    '• Background tasks run even when app is closed\n'
                    '• OS decides exact timing (not guaranteed)\n'
                    '• Tasks respect battery and network constraints\n'
                    '• Minimum interval: 15 minutes on Android',
                    style: TextStyle(fontSize: 12),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  String _formatTime(String isoString) {
    try {
      final dateTime = DateTime.parse(isoString);
      final now = DateTime.now();
      final diff = now.difference(dateTime);

      if (diff.inMinutes < 1) return 'Just now';
      if (diff.inHours < 1) return '${diff.inMinutes}m ago';
      if (diff.inDays < 1) return '${diff.inHours}h ago';
      return '${diff.inDays}d ago';
    } catch (e) {
      return isoString;
    }
  }
}
```

---

## Advanced Features

### 1. Task Constraints

```dart
await Workmanager().registerPeriodicTask(
  'battery-conscious-task',
  'syncData',
  constraints: Constraints(
    networkType: NetworkType.unmetered,  // Only on WiFi (not cellular)
    requiresBatteryNotLow: true,         // Battery > 15%
    requiresCharging: true,              // Device must be charging
    requiresDeviceIdle: true,            // Device not in use (Android only)
    requiresStorageNotLow: true,         // Storage not running out
  ),
);
```

### 2. Initial Delay

```dart
// Run task after 1 hour
await Workmanager().registerOneOffTask(
  'delayed-task',
  'cleanCache',
  initialDelay: Duration(hours: 1),
);
```

### 3. Backoff Policy

```dart
await Workmanager().registerOneOffTask(
  'retry-task',
  'uploadData',
  backoffPolicy: BackoffPolicy.exponential,  // exponential or linear
  backoffPolicyDelay: Duration(seconds: 30),  // Initial retry delay
);
```

**Backoff Example:**
- First retry: after 30 seconds
- Second retry: after 60 seconds (exponential)
- Third retry: after 120 seconds

### 4. Replacing vs Keeping Existing Tasks

```dart
await Workmanager().registerPeriodicTask(
  'my-task',
  'syncData',
  existingWorkPolicy: ExistingWorkPolicy.replace,  // or .keep, .append
);
```

- **replace**: Cancel old task, register new one
- **keep**: Keep old task, ignore new registration
- **append**: Run both (rarely used)

---

## Canceling Tasks

```dart
// Cancel specific task
await Workmanager().cancelByUniqueName('photo-backup');

// Cancel all tasks with same tag
await Workmanager().cancelByTag('sync-tasks');

// Cancel ALL tasks
await Workmanager().cancelAll();
```

---

## Best Practices

1. **Keep Background Work Short**
   - ❌ Don't run tasks for > 10 minutes
   - ✅ Break large work into smaller chunks

2. **Handle Task Failures Gracefully**
   ```dart
   try {
     await _performBackgroundWork();
     return Future.value(true);  // Success
   } catch (e) {
     print('Task failed: $e');
     return Future.value(false);  // Will retry with backoff
   }
   ```

3. **Use Constraints to Save Battery**
   ```dart
   // Good: Only sync when on WiFi and charging
   Constraints(
     networkType: NetworkType.unmetered,
     requiresCharging: true,
   )
   ```

4. **Don't Rely on Exact Timing**
   - OS decides when to run tasks
   - iOS is especially unpredictable
   - Use for deferrable work only

5. **Test on Real Devices**
   - Emulators don't accurately simulate background restrictions
   - Test with low battery, airplane mode, etc.

---

## Common Issues & Solutions

**Issue 1: Tasks not running on iOS**
- **Solution**: iOS is very restrictive. Tasks may not run for hours.
- BGTaskScheduler runs at system discretion
- Test with `e -l objc -- (void)[[BGTaskScheduler sharedScheduler] _simulateLaunchForTaskWithIdentifier:@"your.identifier"]` in Xcode debugger

**Issue 2: Tasks running too frequently**
- **Solution**: Set minimum `frequency: Duration(hours: 1)`
- Android minimum is 15 minutes, but OS may run less frequently

**Issue 3: Task crashes**
- **Solution**: Ensure callback is top-level function with `@pragma('vm:entry-point')`
- Don't access app state directly (use SharedPreferences, SQLite)

**Issue 4: Tasks not running after app force-quit (iOS)**
- **Solution**: This is expected iOS behavior
- iOS doesn't guarantee background execution after force-quit

---

## Quiz

**Question 1:** What's the minimum interval for periodic tasks on Android?
A) 1 minute
B) 5 minutes
C) 15 minutes
D) 1 hour

**Question 2:** When should you use `NetworkType.unmetered` constraint?
A) For all network tasks
B) For large uploads/downloads to save cellular data
C) Only on WiFi 6
D) Never, it's deprecated

**Question 3:** What does returning `false` from a background task do?
A) Cancels the task permanently
B) Causes the task to retry with backoff policy
C) Logs an error
D) Nothing

---

## Exercise: News App Background Sync

Build a news app that:
1. Fetches latest news every 2 hours in background
2. Only syncs on WiFi and when battery not low
3. Shows badge count for unread articles
4. Has manual "Refresh Now" button
5. Stores articles in SQLite

**Bonus Challenges:**
- Send notification when new articles available
- Clean old articles (older than 7 days) weekly
- Allow user to configure sync frequency
- Handle offline mode gracefully

---

## Summary

You've mastered background tasks in Flutter! Here's what we covered:

- **Workmanager Setup**: Initialize and configure for Android/iOS
- **One-Time Tasks**: Run background work once
- **Periodic Tasks**: Schedule recurring work with constraints
- **Task Management**: Register, cancel, and handle tasks
- **Best Practices**: Battery efficiency and platform limitations
- **Complete App**: Photo backup with auto-sync

With background tasks, your apps can sync data, perform maintenance, and stay up-to-date even when closed!

---

## Answer Key

**Answer 1:** C) 15 minutes

Android's WorkManager has a minimum periodic interval of **15 minutes**. You can request more frequent intervals, but the OS will enforce this minimum. This is to preserve battery life and prevent abuse.

**Answer 2:** B) For large uploads/downloads to save cellular data

`NetworkType.unmetered` means WiFi or unlimited data connections (not cellular metered data). Use this for large file operations to avoid expensive cellular data charges for users. For small API calls, `NetworkType.connected` (any connection) is fine.

**Answer 3:** B) Causes the task to retry with backoff policy

Returning `false` signals failure, and WorkManager will automatically retry the task according to the configured `backoffPolicy` (exponential or linear delay). Returning `true` means success - task won't retry.
