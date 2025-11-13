# Module 0, Lesson 5: Troubleshooting Common Setup Issues

## When Things Go Wrong

Even experienced developers encounter setup issues. The good news? Most problems have simple solutions, and you're not alone!

Think of troubleshooting like being a detective:
- **The crime**: Your app won't run
- **The clues**: Error messages
- **The solution**: Following the evidence

This lesson teaches you how to solve the most common problems you'll face.

---

## The #1 Troubleshooting Tool: flutter doctor

This command checks your entire setup and tells you what's wrong.

```bash
flutter doctor
```

**What it checks**:
- ✅ Is Flutter installed?
- ✅ Is Dart available?
- ✅ Are Android tools installed?
- ✅ Is Xcode available? (Mac)
- ✅ Are there any missing dependencies?

**How to read the output**:

```
[✓] Flutter (Channel stable, 3.24.0)
[✗] Android toolchain - develop for Android devices
    ✗ Android SDK not found
[!] Xcode - develop for iOS and macOS (Xcode 15.0)
    ✗ CocoaPods not installed
[✓] Chrome - develop for the web
[✓] VS Code (version 1.85.0)
```

- **[✓]**: Working perfectly
- **[!]**: Working but with warnings
- **[✗]**: Not working, needs fixing

---

## Problem 1: "flutter: command not found"

### What it means:
Your computer doesn't know where Flutter is installed.

### Solution (Windows):

```powershell
# Verify Flutter location
dir C:\src\flutter\bin\flutter.bat

# If it exists, add to PATH
[System.Environment]::SetEnvironmentVariable(
    "Path",
    [System.Environment]::GetEnvironmentVariable("Path", "User") + ";C:\src\flutter\bin",
    "User"
)

# Restart PowerShell and test
flutter --version
```

### Solution (Mac/Linux):

```bash
# Find where you installed Flutter
ls ~/flutter/bin/flutter

# Add to PATH (Mac with zsh)
echo 'export PATH="$PATH:$HOME/flutter/bin"' >> ~/.zshrc
source ~/.zshrc

# Or for bash
echo 'export PATH="$PATH:$HOME/flutter/bin"' >> ~/.bashrc
source ~/.bashrc

# Test
flutter --version
```

---

## Problem 2: Android License Not Accepted

### Error message:
```
Android sdkmanager not found. Update to the latest Android SDK
and ensure that the cmdline-tools are installed
```

### Solution:

```bash
# Accept all Android licenses
flutter doctor --android-licenses

# Type 'y' and press Enter for each license
```

If this doesn't work:

1. Open Android Studio
2. Go to **Settings** → **Appearance & Behavior** → **System Settings** → **Android SDK**
3. Click **SDK Tools** tab
4. Check **Android SDK Command-line Tools**
5. Click **Apply**

Then run `flutter doctor --android-licenses` again.

---

## Problem 3: "Waiting for another flutter command..."

### Error message:
```
Waiting for another flutter command to release the startup lock...
```

### What happened:
A previous Flutter command didn't finish properly and left a lock file.

### Solution:

```bash
# Kill the lock file
cd <your-flutter-installation>
rm -f bin/cache/lockfile

# Windows PowerShell:
Remove-Item -Force bin/cache/lockfile

# Or just restart your computer (easiest)
```

---

## Problem 4: Emulator Won't Start

### Symptom:
Emulator starts but shows a black screen or crashes.

### Solution 1: Enable Hardware Acceleration

**Windows**:
1. Open **Task Manager** → **Performance**
2. Check if "Virtualization" is enabled
3. If not, enable Intel VT-x or AMD-V in BIOS

**Mac**:
Hardware acceleration is enabled by default.

**Linux**:
```bash
# Install KVM
sudo apt-get install qemu-kvm libvirt-daemon-system libvirt-clients bridge-utils

# Add yourself to the kvm group
sudo adduser $USER kvm
```

### Solution 2: Allocate More RAM

1. Open Android Studio
2. **Tools** → **Device Manager**
3. Click the pencil icon (Edit) on your emulator
4. Click **Show Advanced Settings**
5. Increase RAM to at least 2048 MB
6. Click **Finish**

### Solution 3: Use a Different System Image

Some system images work better than others:
- Try **API 33** (Android 13) instead of the latest
- Use **x86_64** images (faster than ARM)

---

## Problem 5: App Builds But Crashes Immediately

### Check 1: Clean and Rebuild

```bash
# Clean all build files
flutter clean

# Get dependencies again
flutter pub get

# Try running again
flutter run
```

### Check 2: Check for Errors in Code

Look at the terminal output. Common errors:

```
Error: Expected ';' after this
```
↳ Missing semicolon

```
Error: The method 'Text' isn't defined
```
↳ Missing import: `import 'package:flutter/material.dart';`

```
Error: The argument type 'int' can't be assigned to 'String'
```
↳ Type mismatch - check your variables

---

## Problem 6: Hot Reload Doesn't Work

### Symptoms:
- You save changes but nothing updates
- App needs full restart every time

### Solutions:

**1. Make sure you're editing the right file**
- Are you editing `lib/main.dart`?
- Not a file in `android/` or `ios/`?

**2. Try Hot Restart instead**
- Press `Ctrl/Cmd + Shift + F5`
- Or click the circular arrow icon

**3. Check for errors**
- Look at the terminal for error messages
- Fix any syntax errors

**4. Full restart**
```bash
# Stop the app
q (in terminal)

# Clean
flutter clean

# Run again
flutter run
```

---

## Problem 7: VS Code Not Finding Flutter

### Symptoms:
- "Dart" or "Flutter" commands not available
- No syntax highlighting
- Can't run apps from VS Code

### Solution:

1. **Install Flutter Extension**:
   - Press `Ctrl/Cmd + Shift + X`
   - Search "Flutter"
   - Install the official Flutter extension

2. **Set Flutter SDK Path**:
   - Press `Ctrl/Cmd + Shift + P`
   - Type "Flutter: Change SDK"
   - Select your Flutter installation path

3. **Restart VS Code**:
   - Close and reopen VS Code
   - Check if Flutter commands work

---

## Problem 8: Gradle Build Fails (Android)

### Error message:
```
FAILURE: Build failed with an exception.
* What went wrong:
Could not determine the dependencies of task ':app:compileDebugJavaWithJavac'
```

### Solution 1: Update Gradle

Edit `android/build.gradle`:

```gradle
// Find this line:
classpath 'com.android.tools.build:gradle:7.x.x'

// Update to:
classpath 'com.android.tools.build:gradle:8.1.0'
```

### Solution 2: Clear Gradle Cache

```bash
cd android
./gradlew clean

# Or on Windows:
gradlew.bat clean
```

### Solution 3: Update Java Version

Flutter requires Java 11 or higher:

```bash
# Check Java version
java -version

# If it's older than 11, download from:
# https://adoptium.net/
```

---

## Problem 9: Pod Install Fails (iOS/Mac)

### Error message:
```
CocoaPods not installed or not in valid state
```

### Solution:

```bash
# Install CocoaPods
sudo gem install cocoapods

# Set up pods
pod setup

# Then from your project:
cd ios
pod install
cd ..

# Try running again
flutter run
```

---

## Problem 10: "Version Solving Failed"

### Error message:
```
Because every version of flutter_test from sdk depends on...
version solving failed
```

### Solution:

```bash
# Update all packages
flutter pub upgrade

# If that doesn't work, delete lock file
rm pubspec.lock

# Get fresh dependencies
flutter pub get
```

---

## The Nuclear Option: Complete Reset

If nothing else works, start fresh:

```bash
# 1. Clean everything
flutter clean

# 2. Delete build files
rm -rf build/
rm -rf .dart_tool/

# 3. Reset pub cache
flutter pub cache repair

# 4. Get dependencies
flutter pub get

# 5. Run
flutter run
```

---

## ✅ YOUR CHALLENGE: Practice Troubleshooting

**Goal**: Become familiar with debugging tools.

**Tasks**:

1. **Run flutter doctor**:
   ```bash
   flutter doctor -v
   ```
   The `-v` flag shows more details. Take a screenshot!

2. **Check Flutter version**:
   ```bash
   flutter --version
   ```

3. **List connected devices**:
   ```bash
   flutter devices
   ```

4. **Intentionally break something**:
   - Open your hello_world app
   - Delete a semicolon `;` somewhere
   - Try to run it
   - Read the error message
   - Fix it!

**Success Condition**: You understand how to read error messages and know where to look for solutions. ✅

---

## Getting Help When Stuck

### Official Resources:
- **Flutter Docs**: https://docs.flutter.dev
- **Flutter GitHub Issues**: https://github.com/flutter/flutter/issues
- **Stack Overflow**: Tag your question with `[flutter]`

### Search Strategy:
1. Copy the exact error message
2. Google: "flutter [your error message]"
3. Look for recent results (last 1-2 years)
4. Try the top 3 solutions

### Ask for Help:
When asking questions, include:
- Exact error message (full output)
- Output of `flutter doctor -v`
- What you've already tried
- Code snippet (if relevant)

---

## Common Error Patterns

| If you see... | It usually means... |
|---------------|---------------------|
| `command not found` | PATH not set correctly |
| `licenses not accepted` | Run `flutter doctor --android-licenses` |
| `version solving failed` | Package conflict - run `flutter pub upgrade` |
| `gradle build failed` | Android build issue - clean and rebuild |
| `pod install failed` | iOS/Mac dependency issue - reinstall CocoaPods |
| `waiting for lock` | Previous Flutter command stuck - restart |

---

## What Did We Learn?

Let's recap:
- ✅ `flutter doctor` is your best friend
- ✅ Most errors have simple solutions
- ✅ Clean and rebuild fixes many issues
- ✅ Error messages tell you what's wrong
- ✅ Google is your ally
- ✅ The Flutter community is helpful!

---

## What's Next?

**Congratulations!** You've completed Module 0! Your development environment is set up, and you know how to troubleshoot problems.

In **Module 1**, we'll dive into the Dart programming language. You'll learn:
- How to store information (variables)
- How to make decisions (if/else)
- How to repeat actions (loops)
- How to organize code (functions)

All taught interactively with lots of hands-on practice!

Ready to start coding? Let's go! 🚀
