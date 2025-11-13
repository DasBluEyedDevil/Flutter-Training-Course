# Module 0, Lesson 1: Installing Flutter & Dart SDK

## What Are We Doing Here?

Imagine you want to build a treehouse. Before you start nailing boards together, you need tools: a hammer, nails, wood, a saw. You can't build anything without your tools ready.

Building phone apps is the same. Before we write a single line of code, we need to install our "toolbox" on your computer. This toolbox contains everything we need to:
- Write instructions for your app (the code)
- Turn those instructions into something a phone can understand
- Test your app on a fake phone on your computer (before putting it on a real phone)

**The Big Picture**: Right now, your computer doesn't know how to build phone apps. We're going to teach it. Once we're done with this lesson, your computer will be ready to build apps for Android phones, iPhones, and even websites—all from the same code.

---

## The Technical Names

The toolbox we're installing has two main parts:

1. **Flutter**: This is the main toolkit. It's like the instruction manual and the assembly line for building apps. Flutter was created by Google and lets you build apps for phones, tablets, computers, and websites—all at once.

2. **Dart SDK**: SDK stands for "Software Development Kit" (don't worry about memorizing that). Dart is the *language* we'll use to write our instructions. Think of it like this: if Flutter is the kitchen, Dart is the language of the recipes.

When we "install Flutter," we're actually getting both Flutter and Dart together. They're best friends and always come as a package.

---

## Installation Instructions

### **FOR WINDOWS USERS:**

**Step 1: Download Flutter**
1. Open your web browser and go to: `https://docs.flutter.dev/get-started/install/windows`
2. Click the blue "Download Flutter SDK" button
3. A file called something like `flutter_windows_3.x.x-stable.zip` will download

**Step 2: Extract the Files**
1. Once downloaded, find the ZIP file (probably in your Downloads folder)
2. Right-click on it → Choose "Extract All"
3. Extract it to a simple location like `C:\src\flutter`
   - ⚠️ **Important**: Do NOT put it in a folder like `C:\Program Files`

**Step 3: Add Flutter to Your PATH**

*What's PATH? It's like your computer's phonebook. When you type "flutter" in a terminal, your computer looks through its PATH to find where the flutter program lives. We're adding Flutter's location to that phonebook.*

Open PowerShell (search for "PowerShell" in the Start menu) and run:

```powershell
# Add Flutter to your PATH permanently (replace C:\src\flutter with your actual path)
[System.Environment]::SetEnvironmentVariable(
    "Path",
    [System.Environment]::GetEnvironmentVariable("Path", "User") + ";C:\src\flutter\bin",
    "User"
)
```

**Step 4: Verify Installation**

Close and reopen PowerShell, then run:

```powershell
flutter doctor
```

---

### **FOR MAC USERS:**

**Step 1: Download Flutter**
1. Open Safari and go to: `https://docs.flutter.dev/get-started/install/macos`
2. Choose your Mac type:
   - **Intel Mac**: Download "Intel Chip" version
   - **Apple Silicon (M1/M2/M3)**: Download "Apple Silicon" version

**Step 2: Extract and Move the Files**

Open Terminal (press `Cmd + Space`, type "Terminal", press Enter):

```bash
# Go to your Downloads folder
cd ~/Downloads

# Extract the file
unzip flutter_macos_*-stable.zip

# Move Flutter to a permanent home
sudo mkdir -p /usr/local/
sudo mv flutter /usr/local/
sudo chown -R $(whoami) /usr/local/flutter
```

**Step 3: Add Flutter to Your PATH**

```bash
# Add Flutter to your PATH permanently
echo 'export PATH="$PATH:/usr/local/flutter/bin"' >> ~/.zshrc

# Reload your settings
source ~/.zshrc
```

**Step 4: Verify Installation**

```bash
flutter doctor
```

---

### **FOR LINUX USERS:**

Open Terminal:

```bash
# Download and extract Flutter
cd ~
wget https://storage.googleapis.com/flutter_infra_release/releases/stable/linux/flutter_linux_3.24.0-stable.tar.xz
tar xf flutter_linux_*-stable.tar.xz

# Add to PATH
echo 'export PATH="$PATH:$HOME/flutter/bin"' >> ~/.bashrc
source ~/.bashrc
```

**Verify Installation**:

```bash
flutter doctor
```

---

## ✅ YOUR CHALLENGE: Verify Your Installation

**Goal**: Confirm that Flutter is installed correctly.

**Instructions**:

1. Open your terminal (PowerShell on Windows, Terminal on Mac/Linux)

2. Run this command:
   ```bash
   flutter --version
   ```

3. You should see something like:
   ```
   Flutter 3.24.0 • channel stable
   Tools • Dart 3.5.0
   ```

4. Now run:
   ```bash
   flutter doctor
   ```

5. You'll probably see:
   - ✅ Green checkmarks (Flutter, Dart)
   - ❌ Red X marks (Android Studio, Chrome, Xcode)

**Success Condition**: If you see `flutter --version` print out version numbers, you're done! ✅

**Don't worry about the red marks** - we'll fix those in upcoming lessons.

---

## Next Steps

Once you've successfully installed Flutter, you're ready to move on to **Lesson 2: Setting Up Your Editor**. In the next lesson, we'll install VS Code and configure it to work perfectly with Flutter.

Great job completing your first lesson! 🎉
