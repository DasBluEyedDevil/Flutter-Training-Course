# Lesson 6: Device Features (Sensors & Biometrics)

## What You'll Learn
- Biometric authentication (fingerprint, Face ID)
- Accelerometer and gyroscope sensors
- Shake detection
- Vibration and haptic feedback
- Battery status and device info
- Building secure and interactive apps

## Concept First: Why Device Features?

### Real-World Analogy
Think of your phone's hardware features like the **five senses** of your app:
- **Biometrics** = Identity verification (like a bouncer checking ID)
- **Accelerometer** = Motion sensing (like your inner ear for balance)
- **Vibration** = Touch feedback (like feeling a tap on your shoulder)
- **Battery** = Energy awareness (like checking your car's fuel gauge)

Just like humans use multiple senses to interact with the world, apps use device features to create richer, more secure experiences!

### Why This Matters
Device features enable unique experiences:

1. **Security**: Biometric login (banking apps, password managers)
2. **Fitness**: Step tracking, workout monitoring (Fitbit, Strava)
3. **Gaming**: Motion controls (racing games, AR games)
4. **Productivity**: Shake to undo, vibrate on notifications
5. **Accessibility**: Haptic feedback for visually impaired users

According to Apple, Face ID is 20x more secure than Touch ID, and biometric authentication increases user engagement by 45%!

---

## Part 1: Biometric Authentication

### Setup

**pubspec.yaml:**
```yaml
dependencies:
  flutter:
    sdk: flutter
  local_auth: ^2.3.0  # Biometric authentication
  permission_handler: ^11.3.1
```

**Android Configuration (`android/app/src/main/AndroidManifest.xml`):**
```xml
<manifest>
    <uses-permission android:name="android.permission.USE_BIOMETRIC"/>
</manifest>
```

**iOS Configuration (`ios/Runner/Info.plist`):**
```xml
<dict>
    <key>NSFaceIDUsageDescription</key>
    <string>We need Face ID to secure your account</string>
</dict>
```

### Basic Biometric Authentication

```dart
import 'package:flutter/material.dart';
import 'package:local_auth/local_auth.dart';
import 'package:local_auth/error_codes.dart' as auth_error;

class BiometricAuthScreen extends StatefulWidget {
  @override
  State<BiometricAuthScreen> createState() => _BiometricAuthScreenState();
}

class _BiometricAuthScreenState extends State<BiometricAuthScreen> {
  final LocalAuthentication _localAuth = LocalAuthentication();
  bool _isAuthenticated = false;
  List<BiometricType> _availableBiometrics = [];

  @override
  void initState() {
    super.initState();
    _checkBiometrics();
  }

  // Check what biometrics are available
  Future<void> _checkBiometrics() async {
    try {
      // Check if device supports biometrics
      final canCheckBiometrics = await _localAuth.canCheckBiometrics;
      final isDeviceSupported = await _localAuth.isDeviceSupported();

      if (canCheckBiometrics && isDeviceSupported) {
        // Get list of available biometrics
        final availableBiometrics = await _localAuth.getAvailableBiometrics();

        setState(() {
          _availableBiometrics = availableBiometrics;
        });

        print('Available biometrics: $_availableBiometrics');
        // Possible values:
        // - BiometricType.face (Face ID on iOS, face unlock on Android)
        // - BiometricType.fingerprint (Touch ID on iOS, fingerprint on Android)
        // - BiometricType.iris (Iris scanner on Samsung devices)
      }
    } catch (e) {
      print('Error checking biometrics: $e');
    }
  }

  // Authenticate with biometrics
  Future<void> _authenticate() async {
    try {
      final authenticated = await _localAuth.authenticate(
        localizedReason: 'Please authenticate to access your account',
        options: AuthenticationOptions(
          stickyAuth: true,  // Show auth dialog until user interacts
          biometricOnly: false,  // Allow PIN/password fallback
        ),
      );

      setState(() {
        _isAuthenticated = authenticated;
      });

      if (authenticated) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Authentication successful!'),
            backgroundColor: Colors.green,
          ),
        );
      }
    } on PlatformException catch (e) {
      // Handle errors
      if (e.code == auth_error.notAvailable) {
        print('Biometrics not available');
      } else if (e.code == auth_error.notEnrolled) {
        print('No biometrics enrolled');
      } else if (e.code == auth_error.lockedOut) {
        print('Too many failed attempts');
      } else if (e.code == auth_error.permanentlyLockedOut) {
        print('Permanently locked out');
      }

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Authentication failed: ${e.message}')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Biometric Authentication')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              _isAuthenticated ? Icons.lock_open : Icons.lock,
              size: 100,
              color: _isAuthenticated ? Colors.green : Colors.red,
            ),

            SizedBox(height: 20),

            Text(
              _isAuthenticated ? 'Authenticated ✓' : 'Not Authenticated ✗',
              style: TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
                color: _isAuthenticated ? Colors.green : Colors.red,
              ),
            ),

            SizedBox(height: 40),

            // Available biometrics
            if (_availableBiometrics.isNotEmpty) ...[
              Text('Available biometrics:', style: TextStyle(fontSize: 16)),
              SizedBox(height: 10),
              ..._availableBiometrics.map((biometric) => Chip(
                    label: Text(biometric.toString().split('.').last),
                  )),
            ],

            SizedBox(height: 40),

            ElevatedButton.icon(
              onPressed: _authenticate,
              icon: Icon(Icons.fingerprint),
              label: Text('Authenticate'),
              style: ElevatedButton.styleFrom(
                padding: EdgeInsets.symmetric(horizontal: 32, vertical: 16),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
```

---

## Part 2: Motion Sensors

### Setup

**pubspec.yaml:**
```yaml
dependencies:
  sensors_plus: ^7.1.1  # Latest as of Sept 2025
```

**iOS Configuration (`ios/Runner/Info.plist`):**
```xml
<dict>
    <key>NSMotionUsageDescription</key>
    <string>We need motion sensors for activity tracking</string>
</dict>
```

### Accelerometer (Detects Device Motion)

```dart
import 'package:flutter/material.dart';
import 'package:sensors_plus/sensors_plus.dart';
import 'dart:async';

class AccelerometerScreen extends StatefulWidget {
  @override
  State<AccelerometerScreen> createState() => _AccelerometerScreenState();
}

class _AccelerometerScreenState extends State<AccelerometerScreen> {
  double _x = 0.0, _y = 0.0, _z = 0.0;
  StreamSubscription? _accelerometerSubscription;

  @override
  void initState() {
    super.initState();

    // Listen to accelerometer events
    _accelerometerSubscription = accelerometerEventStream().listen((AccelerometerEvent event) {
      setState(() {
        _x = event.x;  // Left/right tilt
        _y = event.y;  // Forward/backward tilt
        _z = event.z;  // Up/down (gravity)
      });
    });
  }

  @override
  void dispose() {
    _accelerometerSubscription?.cancel();
    super.dispose();
  }

  String _getOrientation() {
    if (_z.abs() > 9) {
      return _z > 0 ? 'Face Up' : 'Face Down';
    } else if (_x.abs() > _y.abs()) {
      return _x > 0 ? 'Tilted Right' : 'Tilted Left';
    } else {
      return _y > 0 ? 'Tilted Backward' : 'Tilted Forward';
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Accelerometer')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text('Accelerometer Values', style: TextStyle(fontSize: 24)),
            SizedBox(height: 40),

            // X axis (left/right)
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(Icons.arrow_back, color: _x < 0 ? Colors.blue : Colors.grey),
                SizedBox(width: 20),
                Text('X: ${_x.toStringAsFixed(2)} m/s²', style: TextStyle(fontSize: 18)),
                SizedBox(width: 20),
                Icon(Icons.arrow_forward, color: _x > 0 ? Colors.blue : Colors.grey),
              ],
            ),

            SizedBox(height: 20),

            // Y axis (forward/backward)
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(Icons.arrow_upward, color: _y < 0 ? Colors.green : Colors.grey),
                SizedBox(width: 20),
                Text('Y: ${_y.toStringAsFixed(2)} m/s²', style: TextStyle(fontSize: 18)),
                SizedBox(width: 20),
                Icon(Icons.arrow_downward, color: _y > 0 ? Colors.green : Colors.grey),
              ],
            ),

            SizedBox(height: 20),

            // Z axis (up/down)
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text('Z: ${_z.toStringAsFixed(2)} m/s²', style: TextStyle(fontSize: 18)),
              ],
            ),

            SizedBox(height: 40),

            Container(
              padding: EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: Colors.blue.withOpacity(0.1),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Text(
                'Orientation: ${_getOrientation()}',
                style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
```

### Gyroscope (Detects Rotation)

```dart
class GyroscopeScreen extends StatefulWidget {
  @override
  State<GyroscopeScreen> createState() => _GyroscopeScreenState();
}

class _GyroscopeScreenState extends State<GyroscopeScreen> {
  double _rotationX = 0.0, _rotationY = 0.0, _rotationZ = 0.0;
  StreamSubscription? _gyroscopeSubscription;

  @override
  void initState() {
    super.initState();

    _gyroscopeSubscription = gyroscopeEventStream().listen((GyroscopeEvent event) {
      setState(() {
        _rotationX = event.x;  // Pitch (nose up/down)
        _rotationY = event.y;  // Roll (wing up/down)
        _rotationZ = event.z;  // Yaw (turn left/right)
      });
    });
  }

  @override
  void dispose() {
    _gyroscopeSubscription?.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Gyroscope')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text('Rotation Rate (radians/second)', style: TextStyle(fontSize: 20)),
            SizedBox(height: 40),

            _buildRotationIndicator('Pitch (X)', _rotationX, Colors.red),
            SizedBox(height: 20),
            _buildRotationIndicator('Roll (Y)', _rotationY, Colors.green),
            SizedBox(height: 20),
            _buildRotationIndicator('Yaw (Z)', _rotationZ, Colors.blue),

            SizedBox(height: 40),

            Text(
              'Tilt your phone to see rotation values',
              style: TextStyle(color: Colors.grey),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildRotationIndicator(String label, double value, Color color) {
    return Column(
      children: [
        Text(label, style: TextStyle(fontSize: 16)),
        SizedBox(height: 8),
        Container(
          width: 300,
          height: 40,
          decoration: BoxDecoration(
            border: Border.all(color: Colors.grey),
            borderRadius: BorderRadius.circular(8),
          ),
          child: Stack(
            children: [
              // Center line
              Center(
                child: Container(
                  width: 2,
                  height: 40,
                  color: Colors.grey,
                ),
              ),
              // Indicator
              Align(
                alignment: Alignment(value.clamp(-1.0, 1.0), 0),
                child: Container(
                  width: 20,
                  height: 40,
                  color: color,
                ),
              ),
            ],
          ),
        ),
        SizedBox(height: 4),
        Text('${value.toStringAsFixed(2)} rad/s'),
      ],
    );
  }
}
```

---

## Part 3: Shake Detection

### Setup

**pubspec.yaml:**
```yaml
dependencies:
  shake: ^3.0.0
```

### Shake to Undo Example

```dart
import 'package:flutter/material.dart';
import 'package:shake/shake.dart';

class ShakeToUndoScreen extends StatefulWidget {
  @override
  State<ShakeToUndoScreen> createState() => _ShakeToUndoScreenState();
}

class _ShakeToUndoScreenState extends State<ShakeToUndoScreen> {
  ShakeDetector? _shakeDetector;
  List<String> _actions = [];
  int _counter = 0;

  @override
  void initState() {
    super.initState();

    // Initialize shake detector
    _shakeDetector = ShakeDetector.autoStart(
      onPhoneShake: (ShakeEvent event) {
        // Called when phone is shaken
        _undoLastAction();

        // Optional: Show shake details
        print('Shake detected!');
        print('Direction: ${event.direction}');  // X, Y, or Z axis
        print('Force: ${event.force}');
        print('Time: ${event.timestamp}');
      },
      minimumShakeCount: 1,
      shakeSlopTimeMS: 500,
      shakeCountResetTime: 3000,
      shakeThresholdGravity: 2.7,
    );
  }

  @override
  void dispose() {
    _shakeDetector?.stopListening();
    super.dispose();
  }

  void _addAction() {
    setState(() {
      _counter++;
      _actions.add('Action $_counter');
    });
  }

  void _undoLastAction() {
    if (_actions.isEmpty) return;

    setState(() {
      final lastAction = _actions.removeLast();
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Undid: $lastAction'),
          duration: Duration(seconds: 1),
        ),
      );
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Shake to Undo')),
      body: Column(
        children: [
          Padding(
            padding: EdgeInsets.all(16),
            child: Text(
              'Shake your phone to undo!',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
          ),

          Expanded(
            child: _actions.isEmpty
                ? Center(child: Text('No actions yet'))
                : ListView.builder(
                    itemCount: _actions.length,
                    itemBuilder: (context, index) {
                      return ListTile(
                        leading: CircleAvatar(child: Text('${index + 1}')),
                        title: Text(_actions[index]),
                        trailing: IconButton(
                          icon: Icon(Icons.delete),
                          onPressed: () {
                            setState(() => _actions.removeAt(index));
                          },
                        ),
                      );
                    },
                  ),
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _addAction,
        child: Icon(Icons.add),
        tooltip: 'Add Action',
      ),
    );
  }
}
```

---

## Part 4: Vibration & Haptic Feedback

### Setup

**pubspec.yaml:**
```yaml
dependencies:
  vibration: ^2.0.0
```

**Android Configuration (`android/app/src/main/AndroidManifest.xml`):**
```xml
<uses-permission android:name="android.permission.VIBRATE"/>
```

### Vibration Examples

```dart
import 'package:flutter/material.dart';
import 'package:vibration/vibration.dart';

class VibrationScreen extends StatefulWidget {
  @override
  State<VibrationScreen> createState() => _VibrationScreenState();
}

class _VibrationScreenState extends State<VibrationScreen> {
  bool _hasVibrator = false;
  bool _hasAmplitudeControl = false;

  @override
  void initState() {
    super.initState();
    _checkVibration();
  }

  Future<void> _checkVibration() async {
    final hasVibrator = await Vibration.hasVibrator() ?? false;
    final hasAmplitudeControl = await Vibration.hasAmplitudeControl() ?? false;

    setState(() {
      _hasVibrator = hasVibrator;
      _hasAmplitudeControl = hasAmplitudeControl;
    });
  }

  Future<void> _vibrateShort() async {
    if (_hasVibrator) {
      await Vibration.vibrate(duration: 100);  // 100ms
    }
  }

  Future<void> _vibrateMedium() async {
    if (_hasVibrator) {
      await Vibration.vibrate(duration: 500);  // 500ms
    }
  }

  Future<void> _vibratePattern() async {
    if (_hasVibrator) {
      // Pattern: [wait, vibrate, wait, vibrate, ...]
      await Vibration.vibrate(
        pattern: [0, 200, 100, 200, 100, 400],  // SOS pattern
      );
    }
  }

  Future<void> _vibrateWithAmplitude() async {
    if (_hasAmplitudeControl) {
      // Amplitude: 1-255 (iOS doesn't support this)
      await Vibration.vibrate(
        duration: 1000,
        amplitude: 128,  // Medium intensity
      );
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Amplitude control not supported')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Vibration & Haptics')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            if (!_hasVibrator)
              Text('No vibrator available', style: TextStyle(color: Colors.red)),

            if (_hasVibrator) ...[
              Text('Vibration Available ✓', style: TextStyle(color: Colors.green)),
              if (_hasAmplitudeControl)
                Text('Amplitude Control ✓', style: TextStyle(fontSize: 12)),
            ],

            SizedBox(height: 40),

            ElevatedButton(
              onPressed: _vibrateShort,
              child: Text('Short Vibration (100ms)'),
            ),

            SizedBox(height: 16),

            ElevatedButton(
              onPressed: _vibrateMedium,
              child: Text('Medium Vibration (500ms)'),
            ),

            SizedBox(height: 16),

            ElevatedButton(
              onPressed: _vibratePattern,
              child: Text('Pattern Vibration (SOS)'),
            ),

            SizedBox(height: 16),

            if (_hasAmplitudeControl)
              ElevatedButton(
                onPressed: _vibrateWithAmplitude,
                child: Text('With Amplitude (Android only)'),
              ),
          ],
        ),
      ),
    );
  }
}
```

### Haptic Feedback (Alternative)

Flutter has built-in haptic feedback:

```dart
import 'package:flutter/services.dart';

// Light impact (like tapping a button)
HapticFeedback.lightImpact();

// Medium impact
HapticFeedback.mediumImpact();

// Heavy impact (like error)
HapticFeedback.heavyImpact();

// Selection changed (like scrolling through picker)
HapticFeedback.selectionClick();

// Vibrate (long press effect)
HapticFeedback.vibrate();
```

**Example in Button:**
```dart
ElevatedButton(
  onPressed: () {
    HapticFeedback.lightImpact();  // Provide feedback
    // ... do action
  },
  child: Text('Tap Me'),
)
```

---

## Complete Example: Secure Notes App

```dart
import 'package:flutter/material.dart';
import 'package:local_auth/local_auth.dart';
import 'package:flutter/services.dart';

class SecureNotesApp extends StatefulWidget {
  @override
  State<SecureNotesApp> createState() => _SecureNotesAppState();
}

class _SecureNotesAppState extends State<SecureNotesApp> {
  final LocalAuthentication _localAuth = LocalAuthentication();
  bool _isUnlocked = false;
  List<String> _notes = ['Secret note 1', 'Secret note 2', 'Secret note 3'];

  Future<void> _authenticate() async {
    try {
      final authenticated = await _localAuth.authenticate(
        localizedReason: 'Please authenticate to view your secure notes',
        options: AuthenticationOptions(stickyAuth: true),
      );

      if (authenticated) {
        HapticFeedback.mediumImpact();  // Success feedback
        setState(() => _isUnlocked = true);
      }
    } catch (e) {
      HapticFeedback.heavyImpact();  // Error feedback
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Authentication failed')),
      );
    }
  }

  void _lock() {
    HapticFeedback.lightImpact();
    setState(() => _isUnlocked = false);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Secure Notes'),
        actions: [
          if (_isUnlocked)
            IconButton(
              icon: Icon(Icons.lock),
              onPressed: _lock,
            ),
        ],
      ),
      body: _isUnlocked
          ? ListView.builder(
              itemCount: _notes.length,
              itemBuilder: (context, index) {
                return ListTile(
                  leading: Icon(Icons.note),
                  title: Text(_notes[index]),
                );
              },
            )
          : Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.lock, size: 100, color: Colors.red),
                  SizedBox(height: 20),
                  Text('Notes are locked', style: TextStyle(fontSize: 24)),
                  SizedBox(height: 40),
                  ElevatedButton.icon(
                    onPressed: _authenticate,
                    icon: Icon(Icons.fingerprint),
                    label: Text('Unlock with Biometrics'),
                  ),
                ],
              ),
            ),
    );
  }
}
```

---

## Best Practices

1. **Always Check Availability**
   ```dart
   final hasVibrator = await Vibration.hasVibrator();
   final canAuth = await _localAuth.canCheckBiometrics;
   ```

2. **Provide Fallback Options**
   - If biometrics fail, offer PIN/password
   - If sensors unavailable, provide manual input

3. **Don't Overuse Haptics**
   - Only for important actions (button taps, errors)
   - Too much vibration annoys users

4. **Cancel Streams**
   ```dart
   @override
   void dispose() {
     _sensorSubscription?.cancel();
     super.dispose();
   }
   ```

5. **Handle Permissions Gracefully**
   - Explain why you need sensor access
   - Provide option to skip if not critical

---

## Quiz

**Question 1:** What's the difference between `accelerometerEvents` and `userAccelerometerEvents`?
A) They're the same
B) User accelerometer filters out gravity
C) Accelerometer is more accurate
D) User accelerometer is iOS only

**Question 2:** When should you use `HapticFeedback.heavyImpact()`?
A) For every button tap
B) For important actions like errors or deletions
C) Only on Android
D) Never, it's deprecated

**Question 3:** What does `stickyAuth: true` do in biometric authentication?
A) Makes authentication faster
B) Keeps showing the dialog until user interacts
C) Automatically retries on failure
D) Uses only fingerprint, not face

---

## Exercise: Motion-Controlled Game

Build a simple game that:
1. Has a ball that moves based on device tilt (accelerometer)
2. Vibrates when ball hits walls
3. Requires biometric authentication to start
4. Shake to reset ball position

**Bonus Challenges:**
- Add obstacles that ball must avoid
- Track high scores securely
- Use gyroscope for rotation effects
- Add haptic feedback for different events

---

## Summary

You've mastered device features in Flutter! Here's what we covered:

- **Biometric Authentication**: Secure login with fingerprint/Face ID
- **Accelerometer**: Detect device motion and tilt
- **Gyroscope**: Measure rotation and orientation
- **Shake Detection**: Respond to device shaking
- **Vibration & Haptics**: Provide tactile feedback
- **Complete App**: Secure notes with biometric lock

With these skills, you can build apps that feel native, secure, and interactive!

---

## Answer Key

**Answer 1:** B) User accelerometer filters out gravity

`accelerometerEvents` includes gravity (so when device is still, z-axis shows ~9.8 m/s²). `userAccelerometerEvents` filters out gravity, showing only user-caused motion. Use user accelerometer for gesture detection, regular accelerometer for orientation.

**Answer 2:** B) For important actions like errors or deletions

Heavy impact should be reserved for significant moments like errors, destructive actions (delete), or important confirmations. Overusing strong haptics reduces their effectiveness and annoys users. Light impact is for normal taps.

**Answer 3:** B) Keeps showing the dialog until user interacts

`stickyAuth: true` prevents the authentication dialog from dismissing automatically. It stays visible until the user successfully authenticates or explicitly cancels. This prevents accidental dismissals on Android.
