# Lesson 10.5: End-to-End Testing with Firebase Test Lab

## Learning Objectives
By the end of this lesson, you will be able to:
- Understand what Firebase Test Lab is and why it's essential
- Set up Firebase Test Lab for your Flutter project
- Build and upload test APKs and iOS test bundles to Test Lab
- Run integration tests on hundreds of real devices in the cloud
- Analyze test results and device-specific issues
- Integrate Firebase Test Lab into your CI/CD pipeline

---

## Introduction

### What is Firebase Test Lab?

**Concept First:**
Imagine you're opening a restaurant chain in 50 different cities. You could personally visit each location to check if everything works (expensive and slow), or you could hire local inspectors in each city who all check at the same time and send you detailed reports (fast and comprehensive).

**Firebase Test Lab** is like having thousands of device testers working simultaneously. It's Google's cloud-based testing infrastructure that runs your app on hundreds of real Android and iOS devices, catching device-specific bugs before your users encounter them.

**Real-world scenario:** Your app works perfectly on your Samsung Galaxy S24 during development, but crashes on a Pixel 6 with Android 13, freezes on an iPhone 12 with iOS 16, and has layout issues on tablets. Test Lab would catch all these issues automatically.

**Jargon:**
- **Firebase Test Lab**: Google's cloud infrastructure for testing apps on real devices
- **Test Matrix**: A collection of test executions across multiple device configurations
- **Robo Test**: Automated UI testing that explores your app without written tests
- **Instrumentation Test**: Your integration tests packaged as Android instrumentation or iOS XCTests

### Why This Matters

**The Device Fragmentation Problem:**
- Android has **24,000+ different device models**
- iOS has 30+ iPhone models and 20+ iPad models
- Different screen sizes, OS versions, and hardware capabilities
- What works on one device might fail on another

**Without Test Lab:**
- Buy and maintain dozens of physical devices ($$$$)
- Manually test on each device (weeks of work)
- Still miss device-specific bugs
- Users discover issues after release

**With Test Lab:**
- Test on hundreds of devices in minutes
- Automatic screenshots and crash logs
- Pay only for testing time used
- Catch issues before users see them

---

## Section 1: Setting Up Firebase Test Lab

### Step 1: Create a Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project"
3. Enter project name (e.g., "my-flutter-app")
4. Click "Continue"
5. Disable Google Analytics (optional for testing)
6. Click "Create project"

### Step 2: Enable Firebase Test Lab

1. In Firebase Console, select your project
2. Click "Test Lab" in the left sidebar (under "Release & Monitor")
3. Test Lab is automatically enabled - no additional setup needed!

### Step 3: Install Firebase CLI

```bash
# Install Firebase CLI globally
npm install -g firebase-tools

# Or use curl (alternative)
curl -sL https://firebase.tools | bash

# Verify installation
firebase --version

# Login to Firebase
firebase login
```

### Step 4: Install gcloud SDK (for Advanced Usage)

```bash
# macOS (using Homebrew)
brew install --cask google-cloud-sdk

# Linux
curl https://sdk.cloud.google.com | bash
exec -l $SHELL

# Windows: Download installer from
# https://cloud.google.com/sdk/docs/install

# Initialize and login
gcloud init
gcloud auth login
```

---

## Section 2: Preparing Your Flutter App for Test Lab

### Add Integration Tests (if not already present)

Ensure you have integration tests in your project:

```
your_flutter_app/
├── integration_test/
│   ├── app_test.dart
│   └── login_flow_test.dart
├── lib/
├── test/
└── pubspec.yaml
```

### Sample Integration Test

```dart
// integration_test/app_test.dart
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:your_app/main.dart' as app;

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  testWidgets('Complete user flow test', (WidgetTester tester) async {
    app.main();
    await tester.pumpAndSettle();

    // Test your app's critical flows
    expect(find.text('Welcome'), findsOneWidget);

    // Add more test steps...
  });
}
```

---

## Section 3: Running Tests on Android Devices

### Step 1: Build the App and Test APKs

Firebase Test Lab requires two APK files:
1. **App APK**: Your app in debug mode
2. **Test APK**: Your integration tests packaged as instrumentation tests

```bash
# Build both APKs in one command
flutter build apk --debug

# Build the test APK
pushd android
./gradlew app:assembleDebug -Ptarget=$(pwd)/../integration_test/app_test.dart
./gradlew app:assembleDebugAndroidTest
popd
```

**Location of built APKs:**
- App APK: `build/app/outputs/flutter-apk/app-debug.apk`
- Test APK: `build/app/outputs/apk/androidTest/debug/app-debug-androidTest.apk`

### Step 2: Upload and Run Tests via Firebase Console (Easiest)

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project
3. Click "Test Lab" in the sidebar
4. Click "Run a test"
5. Select "Instrumentation"
6. Upload:
   - **App APK**: `build/app/outputs/flutter-apk/app-debug.apk`
   - **Test APK**: `build/app/outputs/apk/androidTest/debug/app-debug-androidTest.apk`
7. Select devices (you can choose from physical devices)
8. Click "Start tests"

**Free Tier:**
- 5 virtual device tests/day
- 10 physical device tests/day

### Step 3: Run Tests via Command Line (Advanced)

```bash
# Set your Firebase project ID
PROJECT_ID="your-firebase-project-id"

# Run tests on specific devices using gcloud
gcloud firebase test android run \
  --type instrumentation \
  --app build/app/outputs/flutter-apk/app-debug.apk \
  --test build/app/outputs/apk/androidTest/debug/app-debug-androidTest.apk \
  --device model=Pixel6,version=33,locale=en,orientation=portrait \
  --device model=Pixel4,version=30,locale=en,orientation=portrait \
  --project $PROJECT_ID
```

### Step 4: Run on Multiple Device Configurations

Create a device matrix to test different combinations:

```bash
# Test on multiple devices, OS versions, and orientations
gcloud firebase test android run \
  --type instrumentation \
  --app build/app/outputs/flutter-apk/app-debug.apk \
  --test build/app/outputs/apk/androidTest/debug/app-debug-androidTest.apk \
  --device model=Pixel6,version=33 \
  --device model=Pixel5,version=31 \
  --device model=SamsungGalaxyS21,version=30 \
  --device model=OnePlus8,version=29 \
  --project $PROJECT_ID
```

### Available Device Models

```bash
# List all available Android device models
gcloud firebase test android models list --project $PROJECT_ID

# List available Android versions
gcloud firebase test android versions list --project $PROJECT_ID
```

---

## Section 4: Running Tests on iOS Devices

### Step 1: Build iOS Test Bundle

iOS testing requires creating a `.zip` file containing your test build:

```bash
# Build iOS app and test runner
flutter build ios integration_test/app_test.dart --release

# Navigate to iOS directory
pushd ios

# Build for testing (creates .xcresult bundle)
xcodebuild build-for-testing \
  -workspace Runner.xcworkspace \
  -scheme Runner \
  -xcconfig Flutter/Release.xcconfig \
  -configuration Release \
  -derivedDataPath ../build/ios_integ \
  -sdk iphoneos

# Create the test zip file
pushd ../build/ios_integ/Build/Products
zip -r ios_tests.zip Release-iphoneos/*.app *.xctestrun
popd
popd
```

**Test bundle location:** `build/ios_integ/Build/Products/ios_tests.zip`

### Step 2: Upload and Run iOS Tests via Firebase Console

1. Go to Firebase Console → Test Lab
2. Click "Run a test"
3. Select "XCTest"
4. Upload `ios_tests.zip`
5. Select iOS devices
6. Click "Start tests"

### Step 3: Run iOS Tests via Command Line

```bash
# Run iOS tests on multiple devices
gcloud firebase test ios run \
  --test build/ios_integ/Build/Products/ios_tests.zip \
  --device model=iphone14pro,version=17.0,locale=en_US,orientation=portrait \
  --device model=iphone12,version=16.0,locale=en_US,orientation=portrait \
  --device model=ipadpro11,version=17.0,locale=en_US,orientation=landscape \
  --project $PROJECT_ID
```

### Available iOS Devices

```bash
# List available iOS device models
gcloud firebase test ios models list --project $PROJECT_ID

# List available iOS versions
gcloud firebase test ios versions list --project $PROJECT_ID
```

---

## Section 5: Analyzing Test Results

### Viewing Results in Firebase Console

After tests complete:

1. Go to Firebase Console → Test Lab
2. Click on your test run
3. View results for each device:
   - ✅ **Passed**: All tests passed
   - ❌ **Failed**: Tests failed or crashed
   - ⚠️ **Inconclusive**: Test didn't complete

### Detailed Device Results

Click on any device to see:
- **Video Recording**: Watch your app running on the device
- **Screenshots**: Automatic screenshots at key moments
- **Logs**: Complete logcat (Android) or syslog (iOS)
- **Performance Metrics**: CPU, memory, network usage
- **Test Artifacts**: Downloaded test outputs

### Understanding Common Failures

#### Failure Type 1: Timeout

```
Error: Test timed out after 5 minutes
```

**Cause:** Test takes too long or has infinite loop
**Fix:** Optimize slow operations or increase timeout

#### Failure Type 2: Widget Not Found

```
TestFailure: Expected to find widget with text "Login" but found none
```

**Cause:** Device-specific layout differences
**Fix:** Check screenshots to see actual layout, adjust test finders

#### Failure Type 3: Crash

```
Fatal Exception: java.lang.NullPointerException
```

**Cause:** Device-specific bug (OS version, screen size, etc.)
**Fix:** Review stack trace, fix null safety issues

### Downloading Test Artifacts

```bash
# Download all results for a test run
gcloud firebase test android run \
  --app build/app/outputs/flutter-apk/app-debug.apk \
  --test build/app/outputs/apk/androidTest/debug/app-debug-androidTest.apk \
  --results-dir=test_results/$(date +%Y%m%d_%H%M%S) \
  --project $PROJECT_ID

# Results will be downloaded to test_results/YYYYMMDD_HHMMSS/
```

---

## Section 6: Robo Tests (No Code Required)

### What are Robo Tests?

**Robo tests** are automated UI tests that explore your app without requiring written test code. Google's AI automatically:
- Taps buttons and UI elements
- Fills in text fields with sample data
- Navigates through screens
- Takes screenshots and videos
- Reports crashes and UI issues

**Analogy:** Like giving your app to a curious toddler who taps everything to see what happens—but with detailed logging!

### Running Robo Tests on Android

```bash
# Run Robo test (no test APK needed!)
gcloud firebase test android run \
  --type robo \
  --app build/app/outputs/flutter-apk/app-debug.apk \
  --device model=Pixel6,version=33 \
  --project $PROJECT_ID
```

### Robo Test via Firebase Console

1. Firebase Console → Test Lab → "Run a test"
2. Select "Robo"
3. Upload only the app APK (no test APK needed)
4. Select devices
5. Click "Start tests"

### When to Use Robo Tests

**Good for:**
- Quick smoke tests before release
- Discovering crashes in unexplored areas
- Testing without writing test code
- Exploring new UI flows

**Not good for:**
- Testing specific user flows (use integration tests instead)
- Testing login flows (Robo can't guess passwords)
- Complex multi-step scenarios

---

## Section 7: Integrating Test Lab into CI/CD

### GitHub Actions Integration

Create `.github/workflows/firebase-test-lab.yml`:

```yaml
name: Firebase Test Lab

on:
  pull_request:
    branches: [ main ]
  push:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up Flutter
        uses: subosito/flutter-action@v2
        with:
          flutter-version: '3.24.0'
          channel: 'stable'

      - name: Install dependencies
        run: flutter pub get

      - name: Build APKs
        run: |
          flutter build apk --debug
          cd android
          ./gradlew app:assembleDebugAndroidTest
          cd ..

      - name: Authenticate to Google Cloud
        uses: google-github-actions/auth@v2
        with:
          credentials_json: ${{ secrets.GOOGLE_CLOUD_CREDENTIALS }}

      - name: Set up gcloud CLI
        uses: google-github-actions/setup-gcloud@v2

      - name: Run tests on Firebase Test Lab
        run: |
          gcloud firebase test android run \
            --type instrumentation \
            --app build/app/outputs/flutter-apk/app-debug.apk \
            --test build/app/outputs/apk/androidTest/debug/app-debug-androidTest.apk \
            --device model=Pixel6,version=33 \
            --device model=Pixel5,version=31 \
            --project ${{ secrets.FIREBASE_PROJECT_ID }}

      - name: Upload test results
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: test-results
          path: test_results/
```

### Setting Up GitHub Secrets

1. Create a Google Cloud service account:
   ```bash
   gcloud iam service-accounts create github-actions \
     --display-name="GitHub Actions"

   gcloud projects add-iam-policy-binding YOUR_PROJECT_ID \
     --member="serviceAccount:github-actions@YOUR_PROJECT_ID.iam.gserviceaccount.com" \
     --role="roles/editor"

   gcloud iam service-accounts keys create credentials.json \
     --iam-account=github-actions@YOUR_PROJECT_ID.iam.gserviceaccount.com
   ```

2. In GitHub repository:
   - Go to Settings → Secrets and variables → Actions
   - Click "New repository secret"
   - Name: `GOOGLE_CLOUD_CREDENTIALS`
   - Value: Contents of `credentials.json`
   - Name: `FIREBASE_PROJECT_ID`
   - Value: Your Firebase project ID

### Codemagic Integration

Create `codemagic.yaml`:

```yaml
workflows:
  flutter-test-lab:
    name: Flutter Test Lab
    max_build_duration: 60
    environment:
      flutter: stable
      groups:
        - firebase_credentials
    scripts:
      - name: Install dependencies
        script: flutter pub get

      - name: Build APKs
        script: |
          flutter build apk --debug
          cd android
          ./gradlew app:assembleDebugAndroidTest
          cd ..

      - name: Run Firebase Test Lab
        script: |
          echo $FIREBASE_CREDENTIALS | base64 --decode > credentials.json
          gcloud auth activate-service-account --key-file=credentials.json
          gcloud --quiet config set project $FIREBASE_PROJECT_ID

          gcloud firebase test android run \
            --type instrumentation \
            --app build/app/outputs/flutter-apk/app-debug.apk \
            --test build/app/outputs/apk/androidTest/debug/app-debug-androidTest.apk \
            --device model=Pixel6,version=33 \
            --device model=SamsungGalaxyS21,version=30
```

---

## Section 8: Best Practices for Firebase Test Lab

### 1. Test on Representative Devices

**Don't test on everything** (expensive and slow). Choose devices that represent your user base:

```bash
# Good strategy: Test on popular devices from different manufacturers
gcloud firebase test android run \
  --app app-debug.apk \
  --test app-debug-androidTest.apk \
  --device model=Pixel6,version=33 \        # Latest Google device
  --device model=SamsungGalaxyS21,version=30 \  # Popular Samsung
  --device model=OnePlus8,version=29 \       # Different manufacturer
  --device model=Pixel4,version=28 \         # Older device
  --project $PROJECT_ID
```

### 2. Use Test Lab in Pre-Release Pipeline

**Test flow:**
```
Developer pushes code
    ↓
GitHub Actions runs unit tests (2 minutes)
    ↓
If passed: Build APKs (5 minutes)
    ↓
If passed: Firebase Test Lab on 5 devices (10 minutes)
    ↓
If all passed: Merge to main
    ↓
Deploy to production
```

### 3. Set Appropriate Timeouts

```bash
# Default timeout: 5 minutes
# Increase for slow tests
gcloud firebase test android run \
  --app app-debug.apk \
  --test app-debug-androidTest.apk \
  --timeout 10m \  # 10 minutes
  --project $PROJECT_ID
```

### 4. Run Robo Tests for Quick Checks

```bash
# Before full integration tests, run quick Robo test
gcloud firebase test android run \
  --type robo \
  --app build/app/outputs/flutter-apk/app-debug.apk \
  --device model=Pixel6,version=33 \
  --timeout 5m \
  --project $PROJECT_ID
```

### 5. Analyze Failure Patterns

If tests fail on specific devices:
- Check device-specific logs and screenshots
- Look for patterns (all Samsung devices fail? all Android 10 devices?)
- Add device-specific workarounds if needed

### 6. Monitor Test Lab Costs

**Free tier limits:**
- 5 virtual device tests/day
- 10 physical device tests/day

**Beyond free tier:**
- Virtual devices: $1/device-hour
- Physical devices: $5/device-hour

**Cost optimization:**
```bash
# Run on fewer devices during development
gcloud firebase test android run \
  --device model=Pixel6,version=33  # Just one device

# Run on full device matrix before release
gcloud firebase test android run \
  --device model=Pixel6,version=33 \
  --device model=Pixel5,version=31 \
  --device model=SamsungGalaxyS21,version=30  # Multiple devices
```

---

## Complete Example: Full Test Lab Workflow

### Project Structure

```
my_flutter_app/
├── integration_test/
│   ├── app_test.dart
│   └── login_flow_test.dart
├── lib/
│   └── main.dart
├── android/
├── ios/
├── test/
├── .github/
│   └── workflows/
│       └── firebase-test-lab.yml
└── scripts/
    ├── run_android_test_lab.sh
    └── run_ios_test_lab.sh
```

### Automated Test Script

```bash
#!/bin/bash
# scripts/run_android_test_lab.sh

set -e

echo "🏗️  Building Flutter app and test APKs..."
flutter build apk --debug

pushd android
./gradlew app:assembleDebugAndroidTest
popd

echo "📱 Running tests on Firebase Test Lab..."
gcloud firebase test android run \
  --type instrumentation \
  --app build/app/outputs/flutter-apk/app-debug.apk \
  --test build/app/outputs/apk/androidTest/debug/app-debug-androidTest.apk \
  --device model=Pixel6,version=33,locale=en,orientation=portrait \
  --device model=Pixel5,version=31,locale=en,orientation=portrait \
  --device model=SamsungGalaxyS21,version=30,locale=en,orientation=portrait \
  --results-dir=test_results/$(date +%Y%m%d_%H%M%S) \
  --timeout 15m \
  --project $FIREBASE_PROJECT_ID

echo "✅ Tests completed! Check Firebase Console for results."
```

Make it executable:
```bash
chmod +x scripts/run_android_test_lab.sh
```

Run it:
```bash
./scripts/run_android_test_lab.sh
```

---

## Quiz

Test your understanding of Firebase Test Lab:

### Question 1
What is the main advantage of Firebase Test Lab over local device testing?

A) It's completely free
B) Tests run faster
C) Tests run on hundreds of real devices simultaneously in the cloud
D) It doesn't require writing test code

### Question 2
What two files do you need to run Android integration tests on Test Lab?

A) Only the app APK
B) App APK and test APK
C) App APK and pubspec.yaml
D) Just the Dart test files

### Question 3
What are Robo tests?

A) Tests written in Robot Framework
B) Automated UI tests that explore your app without test code
C) Tests that run on robotic devices
D) A deprecated testing method

### Question 4
How many free physical device tests does Firebase Test Lab provide per day?

A) Unlimited
B) 5 tests per day
C) 10 tests per day
D) 100 tests per day

### Question 5
When should you run Firebase Test Lab tests in your CI/CD pipeline?

A) After every single commit
B) After unit tests pass and before merging to main
C) Only once per month
D) Never - only run manually

---

## Answer Key

**Question 1: C** - The main advantage is testing on hundreds of real devices simultaneously in Google's cloud infrastructure. This catches device-specific bugs impossible to find with just local testing.

**Question 2: B** - You need both the app APK (your app in debug mode) and the test APK (your integration tests packaged as instrumentation tests).

**Question 3: B** - Robo tests are automated UI tests where Google's AI explores your app by tapping buttons, filling forms, and navigating screens without requiring written test code.

**Question 4: C** - The free tier provides 10 physical device tests per day and 5 virtual device tests per day.

**Question 5: B** - Best practice is to run Test Lab after unit tests pass and before merging to main. This catches issues early while keeping costs reasonable.

---

## Summary

In this lesson, you learned:

✅ **Firebase Test Lab** runs your app on hundreds of real devices in the cloud
✅ Test on **physical Android and iOS devices** to catch device-specific bugs
✅ Build **app APKs and test APKs** for Android, **test bundles** for iOS
✅ Use **Robo tests** for quick automated exploration without test code
✅ Analyze **videos, screenshots, logs, and performance metrics** for each device
✅ Integrate Test Lab into **CI/CD pipelines** with GitHub Actions or Codemagic
✅ Free tier provides **10 physical device tests/day** for development
✅ Choose **representative devices** to balance coverage and cost

**Key Takeaway:** Firebase Test Lab is essential for production apps. It's the difference between "works on my device" and "works on 10,000+ device configurations worldwide." Integrate it into your release pipeline to catch device-specific bugs before users do.

---

## What's Next?

In **Lesson 10.6: Test Coverage and Reporting**, you'll learn how to measure which parts of your code are tested, generate coverage reports, and identify untested code that needs more tests.
