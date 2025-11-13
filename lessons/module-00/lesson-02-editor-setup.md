# Module 0, Lesson 2: Setting Up Your Editor

## What's an Editor?

Think of writing code like writing a book. You *could* use Notepad or TextEdit, but professional writers use Microsoft Word or Google Docs because they have spell-check, grammar suggestions, and formatting tools.

For programming, we use a special kind of text editor called an **IDE** (Integrated Development Environment) or **code editor**. These tools:
- Highlight your code with colors (making it easier to read)
- Catch mistakes as you type (like spell-check)
- Auto-complete your code (like text predictions on your phone)
- Let you run and test your app with one click

---

## The Technical Names

For Flutter development, we recommend **Visual Studio Code** (VS Code for short). Don't confuse this with "Visual Studio" - they're different programs!

**Why VS Code?**
- It's **free** and works on Windows, Mac, and Linux
- It's **lightweight** (doesn't slow down your computer)
- It has **amazing Flutter support** through extensions
- It's what most Flutter developers use

---

## Installation Steps

### Step 1: Download VS Code

1. Go to: `https://code.visualstudio.com`
2. Click the big download button (it auto-detects your operating system)
3. Install it like any other program:
   - **Windows**: Run the `.exe` file
   - **Mac**: Drag the `.app` to your Applications folder
   - **Linux**: Follow the instructions for your distribution

### Step 2: Install the Flutter Extension

Once VS Code is installed:

1. **Open VS Code**
2. Click the **Extensions** icon on the left sidebar (it looks like four squares)
3. In the search bar, type: `Flutter`
4. Find the extension called **"Flutter"** by Dart Code
5. Click **Install**

This will automatically install two extensions:
- **Flutter**: Adds Flutter-specific features
- **Dart**: Adds support for the Dart language

---

## Step 3: Verify Everything Works

Let's make sure VS Code can talk to Flutter!

1. **Open the Command Palette**:
   - Windows/Linux: Press `Ctrl + Shift + P`
   - Mac: Press `Cmd + Shift + P`

2. Type: `Flutter: Run Flutter Doctor`

3. Press Enter

4. You should see a terminal open showing the `flutter doctor` output

If you see green checkmarks for Flutter and Dart, you're all set! ✅

---

## Understanding the VS Code Interface

Here's a quick tour of what you'll see:

```
┌─────────────────────────────────────────┐
│ Menu Bar                                │
├──────┬──────────────────────────────────┤
│      │                                  │
│ Side │    Main Editor                   │
│ Bar  │    (Your code goes here)         │
│      │                                  │
│      │                                  │
├──────┴──────────────────────────────────┤
│ Terminal / Debug Console                │
└─────────────────────────────────────────┘
```

**Key Parts:**
- **Side Bar** (left): File explorer, search, source control, extensions
- **Main Editor** (center): Where you write code
- **Terminal** (bottom): Where you run commands and see output

---

## Useful VS Code Shortcuts

Learn these - they'll save you tons of time:

| Shortcut | What It Does |
|----------|--------------|
| `Ctrl/Cmd + P` | Quick file search |
| `Ctrl/Cmd + Shift + P` | Command palette |
| `Ctrl/Cmd + B` | Toggle sidebar |
| `Ctrl/Cmd + J` | Toggle terminal |
| `Ctrl/Cmd + S` | Save file |
| `Ctrl/Cmd + /` | Comment/uncomment code |

---

## ✅ YOUR CHALLENGE: Customize Your Editor

**Goal**: Make VS Code comfortable for you to use.

**Tasks**:

1. **Change the theme** (optional, but fun!):
   - Press `Ctrl/Cmd + K`, then `Ctrl/Cmd + T`
   - Browse through themes and pick one you like
   - Popular choices: "Dark+ (default)", "Monokai", "Dracula"

2. **Adjust font size**:
   - Press `Ctrl/Cmd + ,` to open Settings
   - Search for "font size"
   - Try different sizes until it's comfortable (14-16 is common)

3. **Test the Flutter extension**:
   - Press `Ctrl/Cmd + Shift + P`
   - Type "Flutter: New Project"
   - See if the command appears (don't run it yet!)

**Success Condition**: You've installed VS Code, the Flutter extension, and customized at least one setting. ✅

---

## What's Next?

In the next lesson, we'll actually create and run your very first Flutter app! We'll see "Hello World" running on a simulated phone right on your computer.

You're making great progress! 🚀
