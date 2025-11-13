# Lesson 8: Mini-Project - Fitness Tracker App

## Project Overview

Build a comprehensive **Fitness Tracker App** that combines all advanced features from Module 9:

- 🎨 **Animations**: Smooth transitions and progress indicators
- 📸 **Camera**: Profile photos and workout photos
- 💾 **Local Storage**: Hive for settings, SQLite for workout history
- 🗺️ **Maps & Location**: Track running routes with GPS
- 📱 **Device Features**: Biometric lock, step counter with accelerometer
- ⏰ **Background Tasks**: Daily reminder notifications
- 📊 **Data Visualization**: Charts showing progress over time

This is a production-ready app that showcases everything you've learned!

---

## What You'll Build

### Core Features

1. **User Profile**
   - Profile photo with camera/gallery
   - Biometric authentication to protect data
   - Personal stats (height, weight, age)

2. **Workout Tracking**
   - Log workouts (running, cycling, gym)
   - GPS tracking for outdoor activities
   - Real-time map showing route
   - Duration, distance, calories burned

3. **Step Counter**
   - Use accelerometer to count steps
   - Daily step goal with progress bar
   - Animated step counter

4. **Workout History**
   - SQLite database for all workouts
   - Filter by type, date range
   - Statistics and charts

5. **Background Reminders**
   - Daily workout reminder notifications
   - Sync workout data periodically

---

## Project Structure

```
lib/
├── main.dart
├── models/
│   ├── user_profile.dart
│   ├── workout.dart
│   └── step_data.dart
├── services/
│   ├── database_service.dart    # SQLite
│   ├── storage_service.dart     # Hive
│   ├── location_service.dart    # GPS tracking
│   ├── auth_service.dart        # Biometrics
│   └── step_counter_service.dart # Accelerometer
├── screens/
│   ├── home_screen.dart
│   ├── profile_screen.dart
│   ├── workout_tracker_screen.dart
│   ├── workout_history_screen.dart
│   └── stats_screen.dart
└── widgets/
    ├── animated_progress_ring.dart
    ├── workout_card.dart
    └── stat_chart.dart
```

---

## Step 1: Dependencies

**pubspec.yaml:**
```yaml
name: fitness_tracker
description: Comprehensive fitness tracking app

dependencies:
  flutter:
    sdk: flutter

  # Local Storage
  hive: ^2.2.3
  hive_flutter: ^1.1.0
  sqflite: ^2.4.2
  path_provider: ^2.1.5
  shared_preferences: ^2.3.2

  # Location & Maps
  google_maps_flutter: ^2.13.1
  geolocator: ^14.0.2

  # Camera & Images
  image_picker: ^1.3.0
  permission_handler: ^11.3.1

  # Device Features
  local_auth: ^2.3.0
  sensors_plus: ^7.1.1

  # Background Tasks
  workmanager: ^0.9.0+3

  # UI & Charts
  fl_chart: ^0.69.0  # For statistics charts
  intl: ^0.20.1      # Date formatting

dev_dependencies:
  flutter_test:
    sdk: flutter
  hive_generator: ^2.0.1
  build_runner: ^2.4.13
```

Run:
```bash
flutter pub get
```

---

## Step 2: Models

### User Profile Model

**lib/models/user_profile.dart:**
```dart
import 'package:hive/hive.dart';

part 'user_profile.g.dart';

@HiveType(typeId: 0)
class UserProfile extends HiveObject {
  @HiveField(0)
  String name;

  @HiveField(1)
  int age;

  @HiveField(2)
  double height;  // in cm

  @HiveField(3)
  double weight;  // in kg

  @HiveField(4)
  String? photoPath;

  @HiveField(5)
  int dailyStepGoal;

  @HiveField(6)
  bool biometricEnabled;

  UserProfile({
    required this.name,
    required this.age,
    required this.height,
    required this.weight,
    this.photoPath,
    this.dailyStepGoal = 10000,
    this.biometricEnabled = false,
  });

  double get bmi => weight / ((height / 100) * (height / 100));

  String get bmiCategory {
    if (bmi < 18.5) return 'Underweight';
    if (bmi < 25) return 'Normal';
    if (bmi < 30) return 'Overweight';
    return 'Obese';
  }
}
```

### Workout Model

**lib/models/workout.dart:**
```dart
class Workout {
  final int? id;
  final String type;  // 'running', 'cycling', 'gym', 'walking'
  final DateTime startTime;
  final DateTime endTime;
  final double? distance;  // in km (null for gym workouts)
  final int calories;
  final String? notes;
  final String? routeJson;  // JSON string of LatLng points

  Workout({
    this.id,
    required this.type,
    required this.startTime,
    required this.endTime,
    this.distance,
    required this.calories,
    this.notes,
    this.routeJson,
  });

  Duration get duration => endTime.difference(startTime);

  double? get avgSpeed {
    if (distance == null || distance == 0) return null;
    final hours = duration.inMinutes / 60;
    return distance! / hours;  // km/h
  }

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'type': type,
      'start_time': startTime.millisecondsSinceEpoch,
      'end_time': endTime.millisecondsSinceEpoch,
      'distance': distance,
      'calories': calories,
      'notes': notes,
      'route_json': routeJson,
    };
  }

  factory Workout.fromMap(Map<String, dynamic> map) {
    return Workout(
      id: map['id'],
      type: map['type'],
      startTime: DateTime.fromMillisecondsSinceEpoch(map['start_time']),
      endTime: DateTime.fromMillisecondsSinceEpoch(map['end_time']),
      distance: map['distance'],
      calories: map['calories'],
      notes: map['notes'],
      routeJson: map['route_json'],
    );
  }
}
```

---

## Step 3: Database Service (SQLite)

**lib/services/database_service.dart:**
```dart
import 'package:sqflite/sqflite.dart';
import 'package:path/path.dart';
import '../models/workout.dart';

class DatabaseService {
  static final DatabaseService instance = DatabaseService._internal();
  factory DatabaseService() => instance;
  DatabaseService._internal();

  static Database? _database;

  Future<Database> get database async {
    if (_database != null) return _database!;
    _database = await _initDatabase();
    return _database!;
  }

  Future<Database> _initDatabase() async {
    final dbPath = await getDatabasesPath();
    final path = join(dbPath, 'fitness_tracker.db');

    return await openDatabase(
      path,
      version: 1,
      onCreate: _onCreate,
    );
  }

  Future<void> _onCreate(Database db, int version) async {
    await db.execute('''
      CREATE TABLE workouts (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        type TEXT NOT NULL,
        start_time INTEGER NOT NULL,
        end_time INTEGER NOT NULL,
        distance REAL,
        calories INTEGER NOT NULL,
        notes TEXT,
        route_json TEXT
      )
    ''');

    await db.execute('''
      CREATE TABLE daily_steps (
        date TEXT PRIMARY KEY,
        steps INTEGER NOT NULL
      )
    ''');
  }

  // Workout methods
  Future<int> insertWorkout(Workout workout) async {
    final db = await database;
    return await db.insert('workouts', workout.toMap());
  }

  Future<List<Workout>> getAllWorkouts() async {
    final db = await database;
    final maps = await db.query('workouts', orderBy: 'start_time DESC');
    return maps.map((map) => Workout.fromMap(map)).toList();
  }

  Future<List<Workout>> getWorkoutsByType(String type) async {
    final db = await database;
    final maps = await db.query(
      'workouts',
      where: 'type = ?',
      whereArgs: [type],
      orderBy: 'start_time DESC',
    );
    return maps.map((map) => Workout.fromMap(map)).toList();
  }

  Future<List<Workout>> getWorkoutsInDateRange(DateTime start, DateTime end) async {
    final db = await database;
    final maps = await db.query(
      'workouts',
      where: 'start_time >= ? AND start_time <= ?',
      whereArgs: [start.millisecondsSinceEpoch, end.millisecondsSinceEpoch],
      orderBy: 'start_time DESC',
    );
    return maps.map((map) => Workout.fromMap(map)).toList();
  }

  Future<int> deleteWorkout(int id) async {
    final db = await database;
    return await db.delete('workouts', where: 'id = ?', whereArgs: [id]);
  }

  // Step counter methods
  Future<void> saveDailySteps(String date, int steps) async {
    final db = await database;
    await db.insert(
      'daily_steps',
      {'date': date, 'steps': steps},
      conflictAlgorithm: ConflictAlgorithm.replace,
    );
  }

  Future<int?> getStepsForDate(String date) async {
    final db = await database;
    final results = await db.query(
      'daily_steps',
      where: 'date = ?',
      whereArgs: [date],
    );

    return results.isNotEmpty ? results.first['steps'] as int : null;
  }

  Future<Map<String, int>> getStepsForWeek() async {
    final db = await database;
    final now = DateTime.now();
    final weekAgo = now.subtract(Duration(days: 7));

    final results = await db.query(
      'daily_steps',
      where: 'date >= ?',
      whereArgs: [weekAgo.toIso8601String().split('T')[0]],
      orderBy: 'date ASC',
    );

    return {
      for (var row in results) row['date'] as String: row['steps'] as int
    };
  }

  // Statistics
  Future<Map<String, dynamic>> getWorkoutStats() async {
    final db = await database;

    final totalWorkouts = Sqflite.firstIntValue(
      await db.rawQuery('SELECT COUNT(*) FROM workouts'),
    ) ?? 0;

    final totalDistance = (await db.rawQuery(
      'SELECT SUM(distance) as total FROM workouts WHERE distance IS NOT NULL',
    ))[0]['total'] ?? 0.0;

    final totalCalories = Sqflite.firstIntValue(
      await db.rawQuery('SELECT SUM(calories) FROM workouts'),
    ) ?? 0;

    final workoutsByType = await db.rawQuery('''
      SELECT type, COUNT(*) as count
      FROM workouts
      GROUP BY type
      ORDER BY count DESC
    ''');

    return {
      'totalWorkouts': totalWorkouts,
      'totalDistance': totalDistance,
      'totalCalories': totalCalories,
      'workoutsByType': workoutsByType,
    };
  }
}
```

---

## Step 4: Main App Setup

**lib/main.dart:**
```dart
import 'package:flutter/material.dart';
import 'package:hive_flutter/hive_flutter.dart';
import 'package:workmanager/workmanager.dart';
import 'models/user_profile.dart';
import 'screens/home_screen.dart';
import 'screens/profile_screen.dart';
import 'services/database_service.dart';

// Background task callback
@pragma('vm:entry-point')
void callbackDispatcher() {
  Workmanager().executeTask((task, inputData) async {
    switch (task) {
      case 'dailyReminder':
        // In real app: trigger local notification
        print('⏰ Daily workout reminder!');
        break;
      case 'syncData':
        // In real app: sync to cloud
        print('☁️ Syncing workout data...');
        break;
    }

    return Future.value(true);
  });
}

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Initialize Hive
  await Hive.initFlutter();
  Hive.registerAdapter(UserProfileAdapter());
  await Hive.openBox<UserProfile>('profile');
  await Hive.openBox('settings');

  // Initialize SQLite
  await DatabaseService().database;

  // Initialize Workmanager
  await Workmanager().initialize(callbackDispatcher, isInDebugMode: true);

  // Register daily reminder (8 AM every day)
  await Workmanager().registerPeriodicTask(
    'daily-reminder',
    'dailyReminder',
    frequency: Duration(hours: 24),
    initialDelay: _calculateDelayUntil8AM(),
  );

  runApp(FitnessTrackerApp());
}

Duration _calculateDelayUntil8AM() {
  final now = DateTime.now();
  var next8AM = DateTime(now.year, now.month, now.day, 8, 0);

  if (now.isAfter(next8AM)) {
    next8AM = next8AM.add(Duration(days: 1));
  }

  return next8AM.difference(now);
}

class FitnessTrackerApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Fitness Tracker',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        primarySwatch: Colors.blue,
        useMaterial3: true,
      ),
      darkTheme: ThemeData.dark(useMaterial3: true),
      home: HomeScreen(),
    );
  }
}
```

---

## Step 5: Home Screen with Step Counter

**lib/screens/home_screen.dart:**
```dart
import 'package:flutter/material.dart';
import 'package:sensors_plus/sensors_plus.dart';
import 'package:intl/intl.dart';
import 'dart:async';
import '../services/database_service.dart';

class HomeScreen extends StatefulWidget {
  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> with TickerProviderStateMixin {
  int _todaySteps = 0;
  int _stepGoal = 10000;
  StreamSubscription? _accelerometerSubscription;
  List<double> _recentAcceleration = [];

  late AnimationController _progressController;
  late Animation<double> _progressAnimation;

  @override
  void initState() {
    super.initState();

    _progressController = AnimationController(
      vsync: this,
      duration: Duration(milliseconds: 1000),
    );

    _progressAnimation = Tween<double>(begin: 0, end: 0).animate(
      CurvedAnimation(parent: _progressController, curve: Curves.easeInOut),
    );

    _loadTodaySteps();
    _startStepCounter();
  }

  @override
  void dispose() {
    _accelerometerSubscription?.cancel();
    _progressController.dispose();
    super.dispose();
  }

  Future<void> _loadTodaySteps() async {
    final today = DateFormat('yyyy-MM-dd').format(DateTime.now());
    final steps = await DatabaseService().getStepsForDate(today);

    setState(() {
      _todaySteps = steps ?? 0;
      _updateProgress();
    });
  }

  void _updateProgress() {
    final progress = (_todaySteps / _stepGoal).clamp(0.0, 1.0);

    _progressAnimation = Tween<double>(
      begin: _progressAnimation.value,
      end: progress,
    ).animate(
      CurvedAnimation(parent: _progressController, curve: Curves.easeInOut),
    );

    _progressController.reset();
    _progressController.forward();
  }

  void _startStepCounter() {
    _accelerometerSubscription = accelerometerEventStream().listen((event) {
      final magnitude = (event.x * event.x + event.y * event.y + event.z * event.z);

      _recentAcceleration.add(magnitude);
      if (_recentAcceleration.length > 10) {
        _recentAcceleration.removeAt(0);
      }

      // Simple step detection: detect peaks in acceleration
      if (_recentAcceleration.length == 10) {
        final avg = _recentAcceleration.reduce((a, b) => a + b) / _recentAcceleration.length;

        if (magnitude > avg * 1.5 && magnitude > 150) {
          setState(() {
            _todaySteps++;
            _updateProgress();
          });

          _saveTodaySteps();
        }
      }
    });
  }

  Future<void> _saveTodaySteps() async {
    final today = DateFormat('yyyy-MM-dd').format(DateTime.now());
    await DatabaseService().saveDailySteps(today, _todaySteps);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Fitness Tracker'),
        actions: [
          IconButton(
            icon: Icon(Icons.person),
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(builder: (_) => ProfileScreen()),
              );
            },
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: EdgeInsets.all(16),
        child: Column(
          children: [
            // Animated Step Counter
            AnimatedBuilder(
              animation: _progressAnimation,
              builder: (context, child) {
                return CustomPaint(
                  size: Size(200, 200),
                  painter: CircularProgressPainter(
                    progress: _progressAnimation.value,
                    color: Theme.of(context).primaryColor,
                  ),
                  child: Container(
                    width: 200,
                    height: 200,
                    child: Center(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Text(
                            '$_todaySteps',
                            style: TextStyle(
                              fontSize: 48,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                          Text(
                            'steps',
                            style: TextStyle(
                              fontSize: 18,
                              color: Colors.grey,
                            ),
                          ),
                          SizedBox(height: 8),
                          Text(
                            'Goal: $_stepGoal',
                            style: TextStyle(fontSize: 14, color: Colors.grey),
                          ),
                        ],
                      ),
                    ),
                  ),
                );
              },
            ),

            SizedBox(height: 40),

            // Quick Actions
            _buildQuickActionButton(
              icon: Icons.directions_run,
              label: 'Start Running',
              color: Colors.blue,
              onTap: () {
                // Navigate to workout tracker
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text('Starting workout tracker...')),
                );
              },
            ),

            SizedBox(height: 12),

            _buildQuickActionButton(
              icon: Icons.history,
              label: 'Workout History',
              color: Colors.green,
              onTap: () {
                // Navigate to history
              },
            ),

            SizedBox(height: 12),

            _buildQuickActionButton(
              icon: Icons.bar_chart,
              label: 'Statistics',
              color: Colors.orange,
              onTap: () {
                // Navigate to stats
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildQuickActionButton({
    required IconData icon,
    required String label,
    required Color color,
    required VoidCallback onTap,
  }) {
    return Card(
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: EdgeInsets.all(16),
          child: Row(
            children: [
              Container(
                padding: EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: color.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Icon(icon, color: color, size: 32),
              ),
              SizedBox(width: 16),
              Text(
                label,
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
              Spacer(),
              Icon(Icons.arrow_forward_ios, size: 16, color: Colors.grey),
            ],
          ),
        ),
      ),
    );
  }
}

// Custom painter for circular progress
class CircularProgressPainter extends CustomPainter {
  final double progress;
  final Color color;

  CircularProgressPainter({required this.progress, required this.color});

  @override
  void paint(Canvas canvas, Size size) {
    final center = Offset(size.width / 2, size.height / 2);
    final radius = size.width / 2;

    // Background circle
    final bgPaint = Paint()
      ..color = color.withOpacity(0.1)
      ..strokeWidth = 20
      ..style = PaintingStyle.stroke;

    canvas.drawCircle(center, radius - 10, bgPaint);

    // Progress arc
    final progressPaint = Paint()
      ..color = color
      ..strokeWidth = 20
      ..style = PaintingStyle.stroke
      ..strokeCap = StrokeCap.round;

    canvas.drawArc(
      Rect.fromCircle(center: center, radius: radius - 10),
      -90 * (3.14159 / 180),  // Start at top
      progress * 360 * (3.14159 / 180),  // Sweep angle
      false,
      progressPaint,
    );
  }

  @override
  bool shouldRepaint(CircularProgressPainter oldDelegate) {
    return oldDelegate.progress != progress;
  }
}
```

---

## Key Features Implementation Summary

### ✅ Animations
- Circular progress ring with smooth animation
- Hero transitions between screens (implement in navigation)

### ✅ Camera & Gallery
- Profile photo picker (implement in ProfileScreen)
- Workout photo attachments

### ✅ Local Storage
- **Hive**: User profile and settings
- **SQLite**: Workout history and step data

### ✅ Maps & Location
- GPS route tracking during workouts
- Display route on Google Maps

### ✅ Device Features
- **Accelerometer**: Step counting algorithm
- **Biometric Auth**: Lock profile screen

### ✅ Background Tasks
- Daily workout reminders at 8 AM
- Periodic data sync

---

## Complete Implementation Checklist

Build the remaining screens:

1. ✅ **Home Screen** (completed above)
2. **Profile Screen**
   - Edit profile info
   - Change profile photo with camera/gallery
   - Enable biometric lock
   - BMI calculator

3. **Workout Tracker Screen**
   - Start/stop workout timer
   - Track GPS route in real-time
   - Calculate distance and calories
   - Save workout to database

4. **Workout History Screen**
   - List all workouts from SQLite
   - Filter by type and date
   - Delete workouts
   - View workout details with map

5. **Statistics Screen**
   - Charts showing progress over time
   - Total distance, calories, workouts
   - Weekly step counts

---

## Testing Your App

### Test Checklist

- [ ] Profile photo picker works (camera & gallery)
- [ ] Biometric lock activates correctly
- [ ] Step counter increments when walking
- [ ] SQLite stores workouts persistently
- [ ] GPS tracking shows route on map
- [ ] Background task runs at scheduled time
- [ ] App survives app restart (data persists)
- [ ] Animations are smooth (60 FPS)

---

## Summary

Congratulations! You've built a comprehensive **Fitness Tracker App** that demonstrates:

- 🎨 **Advanced animations** for engaging UX
- 📸 **Camera integration** for profile photos
- 💾 **Dual storage** with Hive and SQLite
- 🗺️ **GPS tracking** with real-time maps
- 📱 **Device sensors** for step counting
- 🔒 **Biometric security** for privacy
- ⏰ **Background tasks** for reminders

This capstone project showcases production-ready Flutter development skills!

---

## Module 9 Complete! 🎉

You've mastered **Advanced Flutter Features**:

1. ✅ Animations (implicit & explicit)
2. ✅ Camera & Gallery access
3. ✅ Local storage (Hive & SharedPreferences)
4. ✅ SQLite database
5. ✅ Maps & Location services
6. ✅ Device sensors & biometrics
7. ✅ Background tasks
8. ✅ Complete mini-project

**Next Steps:**
- Deploy your app to Google Play / App Store
- Add Firebase for cloud sync
- Implement social features (share workouts)
- Add widget support for home screen

You're now ready to build professional, feature-rich Flutter applications! 🚀
