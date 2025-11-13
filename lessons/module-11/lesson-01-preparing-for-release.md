# Lesson 1: Preparing for Release

## What You'll Learn
- Release checklist for production apps
- App icons and splash screens
- Version numbers and build numbers
- App signing and certificates
- Release vs debug builds
- Performance optimization for production

## Concept First: Why Preparation Matters

### Real-World Analogy
Releasing an app is like **launching a new product**:
- ✅ Quality control (testing)
- ✅ Packaging (build configuration)
- ✅ Branding (icons, splash screens)
- ✅ Documentation (store listings)
- ✅ Legal (privacy policy, terms)

Just like you wouldn't ship a product in a plain brown box, you shouldn't release an app without proper preparation!

### Why This Matters
Poor preparation leads to:
- ❌ App store rejections
- ❌ Bad first impressions
- ❌ Security vulnerabilities
- ❌ Poor performance
- ❌ Legal issues

**73% of apps** get rejected on first submission! Proper preparation increases approval chances.

---

## Release Checklist

### ✅ Pre-Release Checklist

**1. Testing**
- [ ] All features work correctly
- [ ] Tested on multiple devices (small & large screens)
- [ ] Tested on different OS versions
- [ ] No crashes or ANRs (Android) / crashes (iOS)
- [ ] Performance is acceptable (no lag)
- [ ] Battery usage is reasonable

**2. Content**
- [ ] App name is finalized
- [ ] App description is written
- [ ] Screenshots prepared (all required sizes)
- [ ] App icon designed (all required sizes)
- [ ] Splash screen configured
- [ ] Privacy policy published
- [ ] Terms of service (if applicable)

**3. Legal & Compliance**
- [ ] Privacy policy covers all data collection
- [ ] Permissions are justified
- [ ] COPPA compliance (if targeting children)
- [ ] GDPR compliance (if serving EU users)
- [ ] Age rating determined

**4. Technical**
- [ ] Version number set correctly
- [ ] Build number incremented
- [ ] API keys secured (not in code)
- [ ] Error logging configured
- [ ] Analytics integrated
- [ ] App signing configured

**5. Store Listings**
- [ ] Google Play Console account active
- [ ] Apple Developer account active (if iOS)
- [ ] App created in console
- [ ] Store listing filled out
- [ ] Pricing and countries selected

---

## App Icons

### Icon Requirements

**Android (Play Store):**
- **512×512px**: High-res icon (PNG, 32-bit, max 1024 KB)
- **Adaptive icon**: Foreground + background layers
- **Various sizes**: 48dp, 72dp, 96dp, 144dp, 192dp

**iOS (App Store):**
- **1024×1024px**: App Store icon (PNG, no transparency, no rounded corners)
- **Various sizes**: @1x, @2x, @3x for different devices

### Creating App Icons

**Using flutter_launcher_icons Package:**

1. **Install:**
```yaml
dev_dependencies:
  flutter_launcher_icons: ^0.14.1
```

2. **Configure** (pubspec.yaml):
```yaml
flutter_launcher_icons:
  android: true
  ios: true
  image_path: "assets/icon/app_icon.png"  # 1024x1024 source image
  adaptive_icon_foreground: "assets/icon/foreground.png"
  adaptive_icon_background: "#FFFFFF"  # Or use an image

  # iOS-specific
  remove_alpha_ios: true

  # Android-specific
  min_sdk_android: 21
```

3. **Generate icons:**
```bash
flutter pub get
flutter pub run flutter_launcher_icons
```

**Best Practices:**
- ✅ Use simple, recognizable designs
- ✅ Avoid text (hard to read at small sizes)
- ✅ Test on various backgrounds (dark/light)
- ✅ Follow platform guidelines (Material Design / Human Interface Guidelines)
- ❌ Don't use screenshots as icons
- ❌ Don't violate trademarks

---

## Splash Screens

### Native Splash Screens

**Using flutter_native_splash Package:**

1. **Install:**
```yaml
dev_dependencies:
  flutter_native_splash: ^2.4.3
```

2. **Configure** (pubspec.yaml):
```yaml
flutter_native_splash:
  color: "#FFFFFF"  # Background color
  image: assets/splash/splash_logo.png  # Logo image
  android_12:
    image: assets/splash/splash_logo_android12.png  # Android 12+
    color: "#FFFFFF"
  ios: true
  android: true
  web: false
```

3. **Generate:**
```bash
flutter pub get
flutter pub run flutter_native_splash:create
```

**Platform-Specific Notes:**
- **Android 12+**: Uses new splash screen API (animated)
- **iOS**: Static launch screen (no animations allowed)

---

## Version Numbers

### Semantic Versioning

Format: `MAJOR.MINOR.PATCH+BUILD`

**Example:** `1.2.3+45`
- **1**: Major version (breaking changes)
- **2**: Minor version (new features, backward compatible)
- **3**: Patch version (bug fixes)
- **45**: Build number (internal tracking)

### Setting Versions

**pubspec.yaml:**
```yaml
version: 1.2.3+45
```

**When to Increment:**
- **Major (1.x.x)**: Complete rewrite, breaking changes
- **Minor (x.1.x)**: New features added
- **Patch (x.x.1)**: Bug fixes only
- **Build (x.x.x+1)**: Every build/release

**Examples:**
- `1.0.0+1`: Initial release
- `1.0.1+2`: Bug fix
- `1.1.0+3`: Added new feature
- `2.0.0+4`: Major overhaul

---

## Release vs Debug Builds

### Debug Build (Development)
```bash
flutter run
```

**Characteristics:**
- Includes debugging info
- Larger file size (~20-40 MB larger)
- Hot reload enabled
- Performance overhead
- Console logging enabled

### Release Build (Production)
```bash
# Android
flutter build apk --release
flutter build appbundle --release  # Preferred

# iOS
flutter build ioS --release
```

**Characteristics:**
- Optimized code (tree-shaking, minification)
- Smaller file size
- No debugging symbols
- Maximum performance
- Logging disabled (unless explicitly configured)

**Performance Comparison:**
| Metric | Debug | Release |
|--------|-------|---------|
| File Size | ~40 MB | ~15 MB |
| Startup Time | 3-5 sec | 1-2 sec |
| Frame Rate | 50-55 FPS | 60 FPS |
| Memory Usage | Higher | Lower |

---

## App Signing

### Android App Signing

**1. Create a Keystore:**
```bash
keytool -genkey -v -keystore ~/my-release-key.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias my-key-alias
```

**Important:** Save the keystore file and password securely! If lost, you cannot update your app!

**2. Reference Keystore** (android/key.properties):
```properties
storePassword=myStorePassword
keyPassword=myKeyPassword
keyAlias=my-key-alias
storeFile=/Users/username/my-release-key.jks
```

**3. Configure Gradle** (android/app/build.gradle):
```gradle
def keystoreProperties = new Properties()
def keystorePropertiesFile = rootProject.file('key.properties')
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(new FileInputStream(keystorePropertiesFile))
}

android {
    ...
    signingConfigs {
        release {
            keyAlias keystoreProperties['keyAlias']
            keyPassword keystoreProperties['keyPassword']
            storeFile keystoreProperties['storeFile'] ? file(keystoreProperties['storeFile']) : null
            storePassword keystoreProperties['storePassword']
        }
    }

    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled true
            shrinkResources true
        }
    }
}
```

**4. Add to .gitignore:**
```
key.properties
*.jks
*.keystore
```

### iOS Code Signing

iOS signing is handled through Xcode:

1. **Automatic Signing** (recommended for beginners):
   - Open `ios/Runner.xcworkspace` in Xcode
   - Select "Runner" project
   - General → Signing → Enable "Automatically manage signing"
   - Select your Apple Developer Team

2. **Manual Signing** (advanced):
   - Create App ID in Apple Developer Portal
   - Create Distribution Certificate
   - Create Provisioning Profile
   - Configure in Xcode

---

## Build Configuration

### Android Configuration

**android/app/build.gradle:**
```gradle
android {
    namespace = "com.example.myapp"
    compileSdk = 35  // Latest Android SDK
    ndkVersion = "27.0.12077973"

    defaultConfig {
        applicationId = "com.example.myapp"
        minSdk = 21  // Minimum Android 5.0
        targetSdk = 35  // Latest
        versionCode = flutter.versionCode  // From pubspec.yaml
        versionName = flutter.versionName

        multiDexEnabled true  // If app is large
    }

    buildTypes {
        release {
            // Obfuscate code
            minifyEnabled true
            shrinkResources true

            // Use ProGuard
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'

            signingConfig signingConfigs.release
        }
    }
}
```

### iOS Configuration

**ios/Runner/Info.plist:**
```xml
<dict>
    <!-- App Name -->
    <key>CFBundleName</key>
    <string>$(PRODUCT_NAME)</string>

    <!-- Display Name (shown on home screen) -->
    <key>CFBundleDisplayName</key>
    <string>My App</string>

    <!-- Bundle Identifier -->
    <key>CFBundleIdentifier</key>
    <string>$(PRODUCT_BUNDLE_IDENTIFIER)</string>

    <!-- Version -->
    <key>CFBundleShortVersionString</key>
    <string>$(FLUTTER_BUILD_NAME)</string>

    <!-- Build Number -->
    <key>CFBundleVersion</key>
    <string>$(FLUTTER_BUILD_NUMBER)</string>

    <!-- Minimum iOS Version -->
    <key>MinimumOSVersion</key>
    <string>12.0</string>

    <!-- Supported Devices -->
    <key>UIRequiredDeviceCapabilities</key>
    <array>
        <string>arm64</string>
    </array>

    <!-- Orientations -->
    <key>UISupportedInterfaceOrientations</key>
    <array>
        <string>UIInterfaceOrientationPortrait</string>
        <string>UIInterfaceOrientationLandscapeLeft</string>
        <string>UIInterfaceOrientationLandscapeRight</string>
    </array>
</dict>
```

---

## Environment Variables & Secrets

### Managing API Keys Securely

**❌ DON'T:**
```dart
const API_KEY = 'sk_live_abc123';  // Hardcoded = BAD!
```

**✅ DO:**
```dart
// Use flutter_dotenv
import 'package:flutter_dotenv/flutter_dotenv.dart';

await dotenv.load();
final apiKey = dotenv.env['API_KEY'];
```

**.env file:**
```
API_KEY=sk_live_abc123
STRIPE_KEY=pk_live_xyz789
```

**.gitignore:**
```
.env
.env.local
.env.production
```

### Build Flavors (Dev vs Prod)

Create separate configurations for development and production.

---

## Performance Optimization

### Before Release

1. **Profile the App:**
```bash
flutter run --profile
```

2. **Analyze Build Size:**
```bash
flutter build apk --analyze-size
flutter build ios --analyze-size
```

3. **Optimize Images:**
   - Use WebP format
   - Compress PNG/JPEG (TinyPNG, ImageOptim)
   - Use appropriate resolutions (@1x, @2x, @3x)

4. **Enable Obfuscation:**
```bash
flutter build apk --obfuscate --split-debug-info=build/app/outputs/symbols
```

5. **Remove Unused Resources:**
   - Delete unused assets
   - Remove unused packages

---

## Quiz

**Question 1:** What's the difference between version number and build number?
A) They're the same thing
B) Version is user-facing; build is internal tracking
C) Version is for Android; build is for iOS
D) Build number must always be 1

**Question 2:** Why should you never commit your keystore file to Git?
A) It's too large
B) If stolen, attackers can impersonate your app
C) It will break the build
D) Google prohibits it

**Question 3:** What does "minifyEnabled true" do?
A) Makes the app smaller by removing unused code
B) Minimizes battery usage
C) Reduces network calls
D) Compresses images

---

## Exercise: Prepare Your App for Release

Take any Flutter app you've built and:
1. Set version to 1.0.0+1
2. Create app icon (1024x1024)
3. Generate launcher icons with flutter_launcher_icons
4. Add splash screen
5. Create keystore for Android
6. Build release APK
7. Check file size (should be < 20 MB)

---

## Summary

You've learned how to prepare a Flutter app for production! Here's what we covered:

- **Release Checklist**: Testing, legal, technical requirements
- **App Icons**: Generating icons for all platforms
- **Splash Screens**: Native splash configuration
- **Version Numbers**: Semantic versioning (MAJOR.MINOR.PATCH+BUILD)
- **App Signing**: Keystore creation and configuration
- **Build Configuration**: Release vs debug builds
- **Secrets Management**: Protecting API keys
- **Performance**: Optimization techniques

Next lesson: Publishing to Google Play Store!

---

## Answer Key

**Answer 1:** B) Version is user-facing; build is internal tracking

Version number (1.2.3) is what users see in the app store. Build number (+45) is for internal tracking and must increment with each submission, even if the version doesn't change.

**Answer 2:** B) If stolen, attackers can impersonate your app

Your keystore is how you prove ownership of your app. If someone steals it and your passwords, they can sign updates pretending to be you. Always keep it secure and never commit to version control!

**Answer 3:** A) Makes the app smaller by removing unused code

Minification (minifyEnabled true) removes unused code and shortens identifiers, reducing the final APK/IPA size. Combined with shrinkResources, it can reduce app size by 30-50%.
