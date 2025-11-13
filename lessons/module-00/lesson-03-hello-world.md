# Module 0, Lesson 3: Running Your First "Hello World"

## The Moment of Truth

Remember all that setup we did? The Flutter installation, the editor configuration? This is where it all comes together. You're about to create and run your very first Flutter app!

Think of this like turning on a new toy for the first time. We don't need to understand how all the wires and circuits work inside - we just want to see the lights turn on and know everything is working.

---

## What is "Hello World"?

In programming, "Hello World" is a tradition. It's the simplest possible program that just displays the text "Hello World" on the screen. It's used to verify that:
- Your tools are installed correctly
- You can create a new project
- You can run code
- You can see the result

Once you see "Hello World" running, you'll know your development environment is ready for real work!

---

## Creating Your First Project

### Step 1: Open VS Code

Launch Visual Studio Code (the editor we installed in the previous lesson).

### Step 2: Create a New Flutter Project

1. Press `Ctrl/Cmd + Shift + P` to open the command palette
2. Type: `Flutter: New Project`
3. Select **Application**
4. Choose a location on your computer (like a folder called "FlutterProjects")
5. Name your project: `hello_world` (must be lowercase with underscores, no spaces!)
6. Press Enter

VS Code will now create your project. This takes 30-60 seconds. You'll see a progress indicator.

### Step 3: Explore What Was Created

Look at the **Explorer** panel (left sidebar). You'll see a folder structure:

```
hello_world/
├── lib/
│   └── main.dart        ← This is YOUR code file
├── android/             ← Android-specific files
├── ios/                 ← iOS-specific files
├── web/                 ← Web-specific files
├── test/                ← Testing files
└── pubspec.yaml         ← Project configuration
```

**The only file you need to know about right now is `lib/main.dart`**. This is where your app's code lives.

---

## Running Your App

### Step 1: Choose a Device

At the bottom-right of VS Code, you should see a device selector. Click it and choose one of:
- **Chrome** (easiest for beginners - runs in a web browser)
- **Windows** / **macOS** / **Linux** (if available)
- **Android Emulator** (if you have one set up)
- **iOS Simulator** (Mac only, if set up)

For this lesson, **choose Chrome** - it's the simplest option.

### Step 2: Run the App

There are three ways to run your app:

**Option 1**: Press `F5`

**Option 2**: Press `Ctrl/Cmd + Shift + P`, type "Flutter: Run", press Enter

**Option 3**: Click the "Run" button in the top-right corner

Choose any method. You'll see:
1. A terminal opens at the bottom
2. Text scrolling by (this is Flutter building your app)
3. After 10-30 seconds, a window opens showing your app!

---

## What You're Seeing

Congratulations! You're running a Flutter app! 🎉

You should see:
- A blue app bar at the top with "hello_world" as the title
- A counter showing "0"
- A button with a "+" icon at the bottom-right

**Try clicking the + button!** The counter increases. You just interacted with a real, working app!

---

## Understanding the Magic (Just a Peek)

Open the file `lib/main.dart`. You'll see about 110 lines of code. Don't worry - we're not going to understand all of it yet.

But notice around line 94, you'll see:

```dart
Text(
  '$_counter',
  style: Theme.of(context).textTheme.headlineMedium,
),
```

This is the line that displays the counter! When you press the + button, the number `_counter` changes, and the screen updates.

**Don't try to understand this code yet.** We'll learn every single piece in the upcoming lessons. For now, just know: *this is what makes the number appear*.

---

## Making Your First Change

Let's personalize this app! We're going to change the title.

1. Find line 31 in `main.dart`. It looks like:
   ```dart
   title: 'Flutter Demo',
   ```

2. Change it to:
   ```dart
   title: 'My First App',
   ```

3. Save the file (`Ctrl/Cmd + S`)

4. Look at your running app - **it instantly updates!** The title bar now says "My First App"

This feature is called **Hot Reload** - it's one of Flutter's superpowers. You can make changes and see them instantly without restarting the app!

---

## ✅ YOUR CHALLENGE: Make It Yours

**Goal**: Customize the app to make it truly yours.

**Tasks**:

1. **Change the title** (you just did this!)

2. **Change the button text**:
   - Find line 96: `Text('You have pushed the button this many times:'),`
   - Change it to something fun like: `Text('Button presses:'),`
   - Save and watch it update!

3. **Click the button 10 times**
   - Get that counter to at least 10!

4. **Try Hot Restart**:
   - Notice the counter stays at 10
   - Press `Ctrl/Cmd + Shift + F5` (or click the circular arrow icon)
   - This is "Hot Restart" - the counter resets to 0!

**Success Condition**: You've run the app, clicked the button, and made at least one change that you can see on the screen. ✅

---

## Understanding Hot Reload vs Hot Restart

These are two important concepts:

- **Hot Reload** (`Ctrl/Cmd + S` or the lightning bolt icon):
  - Injects your code changes into the running app
  - Keeps the app's current state (counter stays at 10)
  - Takes 1-2 seconds
  - Use this 95% of the time

- **Hot Restart** (`Ctrl/Cmd + Shift + F5` or circular arrow icon):
  - Restarts the app from scratch
  - Resets all state (counter goes back to 0)
  - Takes a few seconds
  - Use this when something seems broken

---

## What Did We Learn?

Let's recap what you just did:
- ✅ Created a brand new Flutter project
- ✅ Ran the app on Chrome/your device
- ✅ Interacted with a real, working app
- ✅ Made code changes and saw them update instantly
- ✅ Experienced Hot Reload (Flutter's superpower!)

**This is a huge milestone!** You now have a working development environment and you've run your first app.

---

## What's Next?

In the next module, we're going to slow down and learn the Dart programming language from scratch. We'll start with the absolute basics:
- How to store information (variables)
- How to make decisions (if/else)
- How to repeat actions (loops)

All taught interactively, with lots of practice!

See you in Module 1! 🚀
