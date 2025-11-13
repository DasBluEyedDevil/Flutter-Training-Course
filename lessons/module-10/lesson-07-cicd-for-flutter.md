# Lesson 10.7: CI/CD for Flutter Apps

## Learning Objectives
By the end of this lesson, you will be able to:
- Understand what CI/CD is and why it's essential for modern development
- Set up GitHub Actions for automated Flutter testing and building
- Configure Codemagic for Flutter CI/CD with minimal setup
- Automate testing, building, and deployment workflows
- Run tests automatically on every pull request
- Deploy apps to TestFlight and Google Play automatically
- Implement quality gates (linting, testing, coverage)

---

## Introduction

### What is CI/CD?

**Concept First:**
Imagine you're running a bakery. Without automation, you:
1. Manually mix ingredients for every bread loaf
2. Check each loaf by hand to ensure quality
3. Drive each delivery to customers yourself
4. Work 20 hours a day, exhausted

With automation (CI/CD), you:
1. Machines mix ingredients consistently
2. Quality sensors check each loaf automatically
3. Delivery trucks automatically route to customers
4. You oversee the process, focus on new recipes
5. Run 24/7 without exhaustion

**CI/CD** brings the same automation to software development.

**Jargon:**
- **CI (Continuous Integration)**: Automatically test and integrate code changes
- **CD (Continuous Deployment)**: Automatically deploy tested code to users
- **Pipeline**: A series of automated steps (test → build → deploy)
- **Workflow**: Configuration file defining what CI/CD should do
- **Runner**: Server that executes your CI/CD pipeline
- **Artifact**: Build output (APK, IPA, test reports)

### Why This Matters

**Without CI/CD:**
- Developer pushes code → manually run tests → might forget → bugs slip through
- Building APKs/IPAs locally → "works on my machine" syndrome
- Manual deployment → error-prone, time-consuming
- No consistent quality checks

**With CI/CD:**
- Every code push → automatic tests ✅
- Pull requests blocked if tests fail 🚫
- Builds created automatically on consistent machines
- Deploy to stores with one click or automatically
- Catch bugs before they reach users

**Real-world impact:**
- **Faster releases**: Deploy multiple times per day instead of per month
- **Higher quality**: Every change is tested automatically
- **Less stress**: No manual deployment at 2 AM
- **Team scalability**: 10 developers can work together safely

---

## Section 1: Understanding CI/CD Pipelines

### The CI/CD Workflow

```
Developer pushes code
    ↓
1. CODE ANALYSIS (2 min)
   - Linting (flutter analyze)
   - Code formatting check
    ↓
2. TESTING (5 min)
   - Unit tests
   - Widget tests
   - Test coverage check
    ↓
3. BUILD (10 min)
   - Build Android APK
   - Build iOS IPA
    ↓
4. INTEGRATION TESTING (15 min)
   - Firebase Test Lab
   - Multiple devices
    ↓
5. DEPLOYMENT (automatic if all pass)
   - Deploy to TestFlight (iOS)
   - Deploy to Google Play Internal Track (Android)
    ↓
6. NOTIFICATION
   - Slack/email notification
   - GitHub status check ✅
```

### Popular CI/CD Platforms for Flutter (2025)

| Platform | Best For | Free Tier | Flutter Support |
|----------|----------|-----------|----------------|
| **GitHub Actions** | GitHub projects | 2000 min/month | Excellent |
| **Codemagic** | Flutter-first | 500 min/month | Native |
| **CircleCI** | Docker workflows | 6000 min/month | Good |
| **GitLab CI** | GitLab projects | 400 min/month | Good |
| **Bitrise** | Mobile apps | 90 min/month | Excellent |

**Recommendation for beginners:** Start with GitHub Actions (most projects use GitHub) or Codemagic (easiest for Flutter).

---

## Section 2: Setting Up GitHub Actions

### Step 1: Create Workflow File

GitHub Actions workflows live in `.github/workflows/`.

```bash
# Create workflow directory
mkdir -p .github/workflows
```

### Step 2: Basic Flutter CI Workflow

Create `.github/workflows/flutter_ci.yml`:

```yaml
name: Flutter CI

# Run on push to main and all pull requests
on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    name: Test and Build
    runs-on: ubuntu-latest

    steps:
      # Step 1: Checkout code
      - name: Checkout repository
        uses: actions/checkout@v4

      # Step 2: Setup Flutter
      - name: Setup Flutter
        uses: subosito/flutter-action@v2
        with:
          flutter-version: '3.24.0'
          channel: 'stable'
          cache: true  # Cache Flutter SDK for faster builds

      # Step 3: Install dependencies
      - name: Install dependencies
        run: flutter pub get

      # Step 4: Verify formatting
      - name: Verify code formatting
        run: dart format --set-exit-if-changed .

      # Step 5: Analyze code
      - name: Analyze code
        run: flutter analyze

      # Step 6: Run unit tests
      - name: Run tests
        run: flutter test

      # Step 7: Build Android APK
      - name: Build Android APK
        run: flutter build apk --debug

      # Step 8: Upload APK as artifact
      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: app-debug-apk
          path: build/app/outputs/flutter-apk/app-debug.apk
```

### Step 3: Commit and Push

```bash
git add .github/workflows/flutter_ci.yml
git commit -m "Add GitHub Actions CI workflow"
git push
```

### Step 4: View Results

1. Go to your GitHub repository
2. Click "Actions" tab
3. See your workflow running!
4. ✅ Green checkmark = all passed
5. ❌ Red X = something failed

### Advanced: Multi-Platform CI

Test on Linux, macOS, and Windows:

```yaml
name: Flutter CI (Multi-Platform)

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    name: Test on ${{ matrix.os }}
    runs-on: ${{ matrix.os }}
    strategy:
      matrix:
        os: [ubuntu-latest, macos-latest, windows-latest]

    steps:
      - uses: actions/checkout@v4

      - uses: subosito/flutter-action@v2
        with:
          flutter-version: '3.24.0'
          channel: 'stable'

      - name: Install dependencies
        run: flutter pub get

      - name: Run tests
        run: flutter test

      - name: Build
        run: |
          if [ "$RUNNER_OS" == "Linux" ]; then
            flutter build apk --debug
          elif [ "$RUNNER_OS" == "macOS" ]; then
            flutter build ios --no-codesign
          elif [ "$RUNNER_OS" == "Windows" ]; then
            flutter build windows
          fi
        shell: bash
```

---

## Section 3: Advanced GitHub Actions Workflows

### Workflow with Test Coverage Enforcement

```yaml
name: Flutter CI with Coverage

on:
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - uses: subosito/flutter-action@v2
        with:
          flutter-version: '3.24.0'

      - name: Install dependencies
        run: flutter pub get

      - name: Run tests with coverage
        run: flutter test --coverage

      - name: Install lcov
        run: sudo apt-get install -y lcov

      - name: Remove generated files from coverage
        run: |
          lcov --remove coverage/lcov.info \
            '*.g.dart' \
            '*.freezed.dart' \
            '*.gr.dart' \
            -o coverage/lcov_cleaned.info

      - name: Check coverage threshold
        run: |
          COVERAGE=$(lcov --summary coverage/lcov_cleaned.info 2>&1 | \
            grep 'lines......:' | \
            grep -oP '\d+\.\d+(?=%)')

          echo "Coverage: ${COVERAGE}%"

          if (( $(echo "$COVERAGE < 70" | bc -l) )); then
            echo "❌ Coverage below 70%"
            exit 1
          else
            echo "✅ Coverage meets 70% requirement"
          fi

      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          files: ./coverage/lcov_cleaned.info
          fail_ci_if_error: false
```

### Workflow with Firebase Test Lab

```yaml
name: Flutter Integration Tests

on:
  pull_request:
    branches: [ main ]

jobs:
  integration-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - uses: subosito/flutter-action@v2
        with:
          flutter-version: '3.24.0'

      - name: Install dependencies
        run: flutter pub get

      - name: Build APKs for testing
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
            --project ${{ secrets.FIREBASE_PROJECT_ID }}
```

### Workflow for Automatic Deployment to Stores

```yaml
name: Deploy to Stores

on:
  push:
    tags:
      - 'v*'  # Trigger on version tags like v1.0.0

jobs:
  deploy-android:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - uses: subosito/flutter-action@v2
        with:
          flutter-version: '3.24.0'

      - uses: actions/setup-java@v3
        with:
          distribution: 'zulu'
          java-version: '17'

      - name: Build Android App Bundle
        run: flutter build appbundle --release

      - name: Sign APK
        uses: r0adkll/sign-android-release@v1
        with:
          releaseDirectory: build/app/outputs/bundle/release
          signingKeyBase64: ${{ secrets.SIGNING_KEY }}
          alias: ${{ secrets.KEY_ALIAS }}
          keyStorePassword: ${{ secrets.KEY_STORE_PASSWORD }}
          keyPassword: ${{ secrets.KEY_PASSWORD }}

      - name: Deploy to Google Play
        uses: r0adkll/upload-google-play@v1
        with:
          serviceAccountJsonPlainText: ${{ secrets.GOOGLE_PLAY_SERVICE_ACCOUNT }}
          packageName: com.yourcompany.yourapp
          releaseFiles: build/app/outputs/bundle/release/*.aab
          track: internal  # or: alpha, beta, production

  deploy-ios:
    runs-on: macos-latest
    steps:
      - uses: actions/checkout@v4

      - uses: subosito/flutter-action@v2
        with:
          flutter-version: '3.24.0'

      - name: Build iOS app
        run: flutter build ios --release --no-codesign

      - name: Build and sign with Xcode
        run: |
          cd ios
          xcodebuild -workspace Runner.xcworkspace \
            -scheme Runner \
            -configuration Release \
            -archivePath $PWD/build/Runner.xcarchive \
            archive

          xcodebuild -exportArchive \
            -archivePath $PWD/build/Runner.xcarchive \
            -exportPath $PWD/build \
            -exportOptionsPlist ExportOptions.plist

      - name: Upload to TestFlight
        uses: apple-actions/upload-testflight-build@v1
        with:
          app-path: 'ios/build/Runner.ipa'
          issuer-id: ${{ secrets.APPSTORE_ISSUER_ID }}
          api-key-id: ${{ secrets.APPSTORE_API_KEY_ID }}
          api-private-key: ${{ secrets.APPSTORE_API_PRIVATE_KEY }}
```

---

## Section 4: Setting Up Codemagic

Codemagic is Flutter-first and easier to set up than GitHub Actions.

### Step 1: Sign Up for Codemagic

1. Go to [codemagic.io](https://codemagic.io)
2. Sign up with GitHub, GitLab, or Bitbucket
3. Grant access to your repositories

### Step 2: Add Your Flutter App

1. Click "Add application"
2. Select your repository
3. Codemagic auto-detects it's a Flutter project ✅

### Step 3: Configure Workflow (UI Method)

**Easiest way: Use the workflow editor**

1. Click "Start your first build"
2. Codemagic automatically:
   - ✅ Installs Flutter
   - ✅ Runs `flutter pub get`
   - ✅ Runs `flutter test`
   - ✅ Builds Android APK
3. Click "Start new build"

**That's it!** Codemagic handles everything.

### Step 4: Configure Workflow (YAML Method)

For more control, create `codemagic.yaml` in your repository root:

```yaml
workflows:
  flutter-workflow:
    name: Flutter CI/CD
    max_build_duration: 60
    instance_type: mac_mini_m1  # Fast Apple Silicon machine

    environment:
      flutter: stable
      xcode: latest
      cocoapods: default

    scripts:
      - name: Get dependencies
        script: flutter pub get

      - name: Analyze code
        script: flutter analyze

      - name: Run tests
        script: flutter test

      - name: Build Android APK
        script: |
          flutter build apk --release

      - name: Build iOS (no codesign)
        script: |
          flutter build ios --release --no-codesign

    artifacts:
      - build/app/outputs/flutter-apk/app-release.apk
      - build/ios/iphoneos/*.app

    publishing:
      email:
        recipients:
          - your-email@example.com
        notify:
          success: true
          failure: true
```

### Step 5: Automatic Deployment with Codemagic

```yaml
workflows:
  deploy-workflow:
    name: Deploy to Stores
    max_build_duration: 60
    instance_type: mac_mini_m1

    environment:
      groups:
        - google_play  # Credentials stored in Codemagic
        - app_store

    scripts:
      - name: Get dependencies
        script: flutter pub get

      - name: Run tests
        script: flutter test

      - name: Build Android App Bundle
        script: flutter build appbundle --release

      - name: Build iOS
        script: |
          flutter build ipa --release \
            --export-options-plist=ios/ExportOptions.plist

    artifacts:
      - build/app/outputs/bundle/release/*.aab
      - build/ios/ipa/*.ipa

    publishing:
      google_play:
        credentials: $GOOGLE_PLAY_CREDENTIALS
        track: internal  # or: alpha, beta, production
        in_app_update_priority: 3

      app_store_connect:
        api_key: $APP_STORE_CONNECT_API_KEY
        key_id: $APP_STORE_CONNECT_KEY_ID
        issuer_id: $APP_STORE_CONNECT_ISSUER_ID
        submit_to_testflight: true

      email:
        recipients:
          - team@example.com
        notify:
          success: true
```

### Codemagic Features

✅ **Pre-installed Flutter** - No setup needed
✅ **Apple M1 machines** - Super fast iOS builds
✅ **Automatic code signing** - Handles certificates for you
✅ **Store publishing built-in** - One-click deployment
✅ **Visual workflow editor** - No YAML knowledge needed
✅ **Free tier** - 500 minutes/month

---

## Section 5: Quality Gates and Best Practices

### What are Quality Gates?

**Quality gates** are checks that must pass before code is merged or deployed.

### Essential Quality Gates

1. **Linting** - Code must follow style guidelines
2. **Unit Tests** - All tests must pass
3. **Widget Tests** - UI tests must pass
4. **Coverage** - Minimum coverage threshold
5. **Integration Tests** - Critical flows work
6. **Build Success** - App must build without errors

### Implementing Quality Gates

```yaml
# .github/workflows/quality-gates.yml
name: Quality Gates

on:
  pull_request:
    branches: [ main ]

jobs:
  quality-check:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - uses: subosito/flutter-action@v2
        with:
          flutter-version: '3.24.0'

      - name: Install dependencies
        run: flutter pub get

      # Gate 1: Formatting
      - name: Check formatting
        run: dart format --set-exit-if-changed .

      # Gate 2: Linting
      - name: Analyze code
        run: flutter analyze --fatal-infos

      # Gate 3: Unit tests
      - name: Run unit tests
        run: flutter test --exclude-tags=integration

      # Gate 4: Coverage threshold
      - name: Check test coverage
        run: |
          flutter test --coverage
          sudo apt-get install -y lcov
          lcov --remove coverage/lcov.info '*.g.dart' -o coverage/lcov.info

          COVERAGE=$(lcov --summary coverage/lcov.info 2>&1 | \
            grep 'lines......:' | \
            grep -oP '\d+\.\d+(?=%)')

          if (( $(echo "$COVERAGE < 70" | bc -l) )); then
            echo "❌ Coverage ${COVERAGE}% below 70%"
            exit 1
          fi

      # Gate 5: Build success
      - name: Build APK
        run: flutter build apk --debug
```

### Branch Protection Rules

Enforce quality gates in GitHub:

1. Go to **Settings** → **Branches**
2. Add rule for `main` branch
3. Enable:
   - ☑️ Require a pull request before merging
   - ☑️ Require status checks to pass before merging
   - ☑️ Require branches to be up to date before merging
4. Select required checks:
   - ✅ Analyze code
   - ✅ Run tests
   - ✅ Check coverage

Now PRs can't be merged until all checks pass!

---

## Section 6: Common CI/CD Patterns

### Pattern 1: Separate Workflows by Purpose

```
.github/workflows/
├── ci.yml              # Quick checks on every push
├── integration.yml     # Slow integration tests
└── deploy.yml          # Deployment to stores
```

**ci.yml** (fast, runs always):
```yaml
on:
  push:
    branches: [ main ]
  pull_request:

jobs:
  quick-check:
    steps:
      - Lint
      - Unit tests
      - Build
```

**integration.yml** (slow, runs on main only):
```yaml
on:
  push:
    branches: [ main ]

jobs:
  integration-tests:
    steps:
      - Build APKs
      - Firebase Test Lab
      - Performance tests
```

**deploy.yml** (manual trigger):
```yaml
on:
  workflow_dispatch:  # Manual trigger
    inputs:
      environment:
        description: 'Deployment environment'
        required: true
        type: choice
        options:
          - internal
          - beta
          - production

jobs:
  deploy:
    steps:
      - Build release
      - Deploy to ${{ inputs.environment }}
```

### Pattern 2: Caching for Faster Builds

```yaml
jobs:
  test:
    steps:
      - uses: actions/checkout@v4

      # Cache Flutter SDK
      - uses: subosito/flutter-action@v2
        with:
          flutter-version: '3.24.0'
          cache: true

      # Cache pub dependencies
      - name: Cache pub dependencies
        uses: actions/cache@v3
        with:
          path: |
            ~/.pub-cache
          key: ${{ runner.os }}-pub-${{ hashFiles('pubspec.yaml') }}
          restore-keys: |
            ${{ runner.os }}-pub-

      - name: Install dependencies
        run: flutter pub get
```

**Result:** Builds go from 10 minutes → 2 minutes! ⚡

### Pattern 3: Matrix Testing

Test multiple Flutter versions:

```yaml
jobs:
  test:
    strategy:
      matrix:
        flutter-version: ['3.22.0', '3.24.0']

    steps:
      - uses: actions/checkout@v4

      - uses: subosito/flutter-action@v2
        with:
          flutter-version: ${{ matrix.flutter-version }}

      - run: flutter test
```

---

## Section 7: Monitoring and Notifications

### Slack Notifications

```yaml
jobs:
  test:
    steps:
      # ... your build steps ...

      - name: Notify Slack on success
        if: success()
        uses: slackapi/slack-github-action@v1
        with:
          payload: |
            {
              "text": "✅ Build succeeded for ${{ github.repository }}"
            }
        env:
          SLACK_WEBHOOK_URL: ${{ secrets.SLACK_WEBHOOK_URL }}

      - name: Notify Slack on failure
        if: failure()
        uses: slackapi/slack-github-action@v1
        with:
          payload: |
            {
              "text": "❌ Build failed for ${{ github.repository }}"
            }
        env:
          SLACK_WEBHOOK_URL: ${{ secrets.SLACK_WEBHOOK_URL }}
```

### Email Notifications (Codemagic)

```yaml
# codemagic.yaml
publishing:
  email:
    recipients:
      - dev-team@company.com
      - qa-team@company.com
    notify:
      success: true
      failure: true
```

---

## Complete Example: Production-Ready CI/CD

### Project Structure

```
flutter_app/
├── .github/
│   └── workflows/
│       ├── ci.yml                  # Quick checks
│       ├── integration.yml         # Full integration tests
│       └── deploy.yml              # Store deployment
├── codemagic.yaml                  # Alternative: Codemagic config
├── lib/
├── test/
└── integration_test/
```

### ci.yml (Runs on every PR)

```yaml
name: CI

on:
  pull_request:
    branches: [ main ]
  push:
    branches: [ main ]

jobs:
  ci:
    runs-on: ubuntu-latest
    timeout-minutes: 20
    steps:
      - uses: actions/checkout@v4

      - uses: subosito/flutter-action@v2
        with:
          flutter-version: '3.24.0'
          cache: true

      - name: Install dependencies
        run: flutter pub get

      - name: Check formatting
        run: dart format --set-exit-if-changed .

      - name: Analyze code
        run: flutter analyze --fatal-infos

      - name: Run tests with coverage
        run: flutter test --coverage --no-test-assets

      - name: Check coverage
        run: |
          sudo apt-get install -y lcov
          lcov --remove coverage/lcov.info '*.g.dart' -o coverage/lcov.info
          COVERAGE=$(lcov --summary coverage/lcov.info 2>&1 | grep 'lines' | grep -oP '\d+\.\d+(?=%)')
          echo "Coverage: ${COVERAGE}%"
          if (( $(echo "$COVERAGE < 70" | bc -l) )); then exit 1; fi

      - name: Build APK
        run: flutter build apk --debug
```

---

## Quiz

Test your understanding of CI/CD for Flutter:

### Question 1
What does CI stand for?

A) Code Integration
B) Continuous Integration
C) Computer Interaction
D) Centralized Installation

### Question 2
What's the main benefit of CI/CD?

A) Writes code for you
B) Automatically tests and deploys code on every change
C) Makes your app run faster
D) Reduces app size

### Question 3
Where do GitHub Actions workflows live in your project?

A) `workflows/`
B) `.ci/workflows/`
C) `.github/workflows/`
D) `github/actions/`

### Question 4
What's a "quality gate"?

A) A firewall for your code
B) A check that must pass before code can be merged
C) A premium GitHub feature
D) An iOS app submission requirement

### Question 5
Which CI/CD platform is Flutter-first with native support?

A) Jenkins
B) Travis CI
C) Codemagic
D) CircleCI

---

## Answer Key

**Question 1: B** - CI stands for **Continuous Integration**, the practice of automatically integrating and testing code changes as they're made.

**Question 2: B** - The main benefit is **automation**: CI/CD automatically runs tests, builds, and deployments on every code change, catching issues early and enabling rapid releases.

**Question 3: C** - GitHub Actions workflows are stored in the **`.github/workflows/`** directory as YAML files.

**Question 4: B** - A quality gate is a **check that must pass** before code can be merged (e.g., tests passing, coverage above threshold, no linting errors).

**Question 5: C** - **Codemagic** is built specifically for Flutter with native support, pre-installed Flutter, automatic iOS code signing, and one-click store publishing.

---

## Summary

In this lesson, you learned:

✅ **CI/CD automates** testing, building, and deployment workflows
✅ **GitHub Actions** uses `.github/workflows/*.yml` files
✅ **Codemagic** provides Flutter-first CI/CD with minimal setup
✅ **Quality gates** enforce code standards before merging
✅ Run tests automatically on every **pull request**
✅ **Cache dependencies** to speed up builds (10 min → 2 min)
✅ Deploy to stores automatically with **one click or on every tag**
✅ Monitor builds with **Slack/email notifications**
✅ Use **branch protection** to block PRs until checks pass

**Key Takeaway:** CI/CD transforms development from manual, error-prone processes to automated, reliable pipelines. Set it up once, and every code change is automatically tested and validated. This lets you deploy confidently multiple times per day instead of dreading monthly releases.

---

## What's Next?

In **Lesson 10.8: Testing Best Practices Mini-Project**, you'll apply everything you've learned by building a complete Flutter app with a full testing suite—unit tests, widget tests, integration tests, coverage reporting, and CI/CD automation all working together.
