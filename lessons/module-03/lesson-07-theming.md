# Lesson 3.7: App Theming with Material 3

## Learning Objectives
By the end of this lesson, you will be able to:
- Understand Flutter's theming system with Material 3
- Create a custom theme using ColorScheme.fromSeed
- Implement light and dark themes
- Customize TextTheme for consistent typography
- Apply component-specific themes
- Switch between themes dynamically
- Use theme data throughout your app

---

## Introduction

### What is App Theming?

**Concept First:**
Imagine you're decorating a house. Without a theme, each room has random colors, different furniture styles, and mismatched lighting. It looks chaotic and unprofessional.

With a design theme, every room follows consistent colors, matching furniture styles, and coordinated lighting. The house feels cohesive and well-designed.

**App theming** is the same idea: defining a consistent visual style (colors, fonts, button styles, etc.) that applies automatically throughout your entire app.

**Real-world analogy:** Starbucks has a consistent theme—green colors, sans-serif fonts, rounded corners. You recognize it instantly. Your app needs the same consistency!

**Jargon:**
- **ThemeData**: Flutter's object containing all theme information
- **ColorScheme**: A set of 30+ colors defining your app's color palette
- **TextTheme**: A set of text styles for different purposes (headlines, body, captions)
- **Material 3**: Google's latest design system (default in Flutter 3.16+)
- **Seed Color**: A single color that generates an entire color palette

### Why This Matters

**Without theming:**
```dart
// Every button needs manual styling
ElevatedButton(
  style: ElevatedButton.styleFrom(
    backgroundColor: Color(0xFF6750A4),
    foregroundColor: Colors.white,
    textStyle: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
  ),
  child: Text('Submit'),
  onPressed: () {},
)

// Repeated 50 times across your app 😱
// Change the color? Update 50 places!
```

**With theming:**
```dart
// Styled automatically from theme!
ElevatedButton(
  child: Text('Submit'),
  onPressed: () {},
)

// Change your theme's primary color once → all buttons update! ✨
```

---

## Section 1: Understanding Material 3 Theming

### Material 3 Color System

Material 3 generates a complete color palette from a **single seed color**:

```
Seed Color (e.g., Blue #2196F3)
    ↓
Generates:
- Primary (Main brand color)
- Secondary (Accent color)
- Tertiary (Complementary color)
- Error (Error states)
- Surface (Backgrounds)
- OnPrimary (Text on primary color)
- OnSecondary (Text on secondary color)
... (30+ colors total!)
```

**Analogy:** Give an interior designer your favorite color. They create an entire palette—wall colors, furniture, accents—all coordinated automatically!

### Material 3 is Default (Flutter 3.16+)

As of Flutter 3.16, Material 3 is enabled by default. You don't need to set `useMaterial3: true` anymore!

---

## Section 2: Creating Your First Theme

### Basic Theme Setup

```dart
// main.dart
import 'package:flutter/material.dart';

void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Themed App',

      // Define your theme here
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          seedColor: Colors.deepPurple,
        ),
      ),

      home: const HomeScreen(),
    );
  }
}

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('My Themed App'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            // These widgets automatically use theme colors!
            ElevatedButton(
              onPressed: () {},
              child: const Text('Primary Button'),
            ),
            const SizedBox(height: 16),
            FilledButton(
              onPressed: () {},
              child: const Text('Filled Button'),
            ),
            const SizedBox(height: 16),
            OutlinedButton(
              onPressed: () {},
              child: const Text('Outlined Button'),
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {},
        child: const Icon(Icons.add),
      ),
    );
  }
}
```

**What happens:**
1. `ColorScheme.fromSeed` generates 30+ coordinated colors from `Colors.deepPurple`
2. All Material widgets (buttons, app bars, cards) use these colors automatically
3. Change `seedColor` to `Colors.teal` → entire app changes instantly!

---

## Section 3: Light and Dark Themes

### Implementing Theme Switching

```dart
// main.dart
void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Light & Dark Theme',

      // Light theme
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          seedColor: Colors.indigo,
          brightness: Brightness.light,
        ),
      ),

      // Dark theme
      darkTheme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          seedColor: Colors.indigo,
          brightness: Brightness.dark,
        ),
      ),

      // Flutter automatically switches based on system setting
      themeMode: ThemeMode.system,

      home: const HomeScreen(),
    );
  }
}
```

**ThemeMode options:**
- `ThemeMode.light` - Always use light theme
- `ThemeMode.dark` - Always use dark theme
- `ThemeMode.system` - Follow device setting (recommended)

### Manual Theme Switching

```dart
class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  ThemeMode _themeMode = ThemeMode.system;

  void _toggleTheme() {
    setState(() {
      _themeMode = _themeMode == ThemeMode.light
          ? ThemeMode.dark
          : ThemeMode.light;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          seedColor: Colors.blue,
          brightness: Brightness.light,
        ),
      ),
      darkTheme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          seedColor: Colors.blue,
          brightness: Brightness.dark,
        ),
      ),
      themeMode: _themeMode,
      home: HomeScreen(onToggleTheme: _toggleTheme),
    );
  }
}

class HomeScreen extends StatelessWidget {
  final VoidCallback onToggleTheme;

  const HomeScreen({super.key, required this.onToggleTheme});

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Theme Switcher'),
        actions: [
          IconButton(
            icon: Icon(isDark ? Icons.light_mode : Icons.dark_mode),
            onPressed: onToggleTheme,
          ),
        ],
      ),
      body: const Center(
        child: Text('Toggle theme with the icon button'),
      ),
    );
  }
}
```

---

## Section 4: Customizing TextTheme

### Understanding TextTheme Styles

TextTheme provides predefined styles for different text purposes:

| Style | Purpose | Example |
|-------|---------|---------|
| `displayLarge` | Very large headlines | "Welcome" on splash screen |
| `headlineLarge` | Section headers | "Settings" title |
| `titleLarge` | Card titles | "New Message" dialog title |
| `bodyLarge` | Main content | Article text |
| `bodyMedium` | Default body text | Paragraph text |
| `labelLarge` | Button text | "SUBMIT" button |

### Custom TextTheme Example

```dart
ThemeData(
  colorScheme: ColorScheme.fromSeed(seedColor: Colors.teal),

  textTheme: const TextTheme(
    // Large display text
    displayLarge: TextStyle(
      fontSize: 96,
      fontWeight: FontWeight.bold,
      letterSpacing: -1.5,
    ),

    // Page headlines
    headlineLarge: TextStyle(
      fontSize: 32,
      fontWeight: FontWeight.w600,
      letterSpacing: 0.25,
    ),

    // Card/dialog titles
    titleLarge: TextStyle(
      fontSize: 22,
      fontWeight: FontWeight.w500,
    ),

    // Body text
    bodyLarge: TextStyle(
      fontSize: 16,
      fontWeight: FontWeight.normal,
      letterSpacing: 0.5,
    ),

    // Captions and hints
    bodySmall: TextStyle(
      fontSize: 12,
      fontWeight: FontWeight.normal,
      letterSpacing: 0.4,
    ),
  ),
)
```

### Using Custom Fonts

```yaml
# pubspec.yaml
flutter:
  fonts:
    - family: Poppins
      fonts:
        - asset: fonts/Poppins-Regular.ttf
        - asset: fonts/Poppins-Bold.ttf
          weight: 700
```

```dart
ThemeData(
  colorScheme: ColorScheme.fromSeed(seedColor: Colors.purple),

  textTheme: const TextTheme(
    headlineLarge: TextStyle(
      fontFamily: 'Poppins',
      fontSize: 32,
      fontWeight: FontWeight.bold,
    ),
    bodyLarge: TextStyle(
      fontFamily: 'Poppins',
      fontSize: 16,
    ),
  ),
)
```

---

## Section 5: Component-Specific Theming

### Customizing Button Themes

```dart
ThemeData(
  colorScheme: ColorScheme.fromSeed(seedColor: Colors.orange),

  elevatedButtonTheme: ElevatedButtonThemeData(
    style: ElevatedButton.styleFrom(
      padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 16),
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
      ),
      textStyle: const TextStyle(
        fontSize: 18,
        fontWeight: FontWeight.bold,
      ),
    ),
  ),

  outlinedButtonTheme: OutlinedButtonThemeData(
    style: OutlinedButton.styleFrom(
      padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 16),
      side: const BorderSide(width: 2),
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
      ),
    ),
  ),
)
```

### Customizing AppBar Theme

```dart
ThemeData(
  colorScheme: ColorScheme.fromSeed(seedColor: Colors.blue),

  appBarTheme: const AppBarTheme(
    centerTitle: true,
    elevation: 4,
    backgroundColor: Colors.white,
    foregroundColor: Colors.black,
    titleTextStyle: TextStyle(
      fontSize: 20,
      fontWeight: FontWeight.bold,
      color: Colors.black,
    ),
  ),
)
```

### Customizing Input Decoration Theme

```dart
ThemeData(
  colorScheme: ColorScheme.fromSeed(seedColor: Colors.green),

  inputDecorationTheme: InputDecorationTheme(
    filled: true,
    fillColor: Colors.grey[100],
    border: OutlineInputBorder(
      borderRadius: BorderRadius.circular(12),
      borderSide: BorderSide.none,
    ),
    focusedBorder: OutlineInputBorder(
      borderRadius: BorderRadius.circular(12),
      borderSide: const BorderSide(color: Colors.green, width: 2),
    ),
    contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
  ),
)
```

### Customizing Card Theme

```dart
ThemeData(
  colorScheme: ColorScheme.fromSeed(seedColor: Colors.purple),

  cardTheme: CardTheme(
    elevation: 4,
    shape: RoundedRectangleBorder(
      borderRadius: BorderRadius.circular(16),
    ),
    margin: const EdgeInsets.all(8),
  ),
)
```

---

## Section 6: Accessing Theme Data

### Using Theme.of(context)

```dart
class MyWidget extends StatelessWidget {
  const MyWidget({super.key});

  @override
  Widget build(BuildContext context) {
    // Access current theme
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final textTheme = theme.textTheme;

    return Container(
      // Use theme colors
      color: colorScheme.surface,
      padding: const EdgeInsets.all(16),
      child: Column(
        children: [
          Text(
            'Headline',
            style: textTheme.headlineLarge,
          ),
          const SizedBox(height: 8),
          Text(
            'Body text that uses theme styling',
            style: textTheme.bodyLarge?.copyWith(
              color: colorScheme.onSurface,
            ),
          ),
        ],
      ),
    );
  }
}
```

### Common Theme Properties

```dart
// Colors
Theme.of(context).colorScheme.primary
Theme.of(context).colorScheme.secondary
Theme.of(context).colorScheme.surface
Theme.of(context).colorScheme.error
Theme.of(context).colorScheme.onPrimary  // Text color on primary background

// Text styles
Theme.of(context).textTheme.headlineLarge
Theme.of(context).textTheme.bodyLarge
Theme.of(context).textTheme.labelLarge

// Check if dark mode
Theme.of(context).brightness == Brightness.dark
```

---

## Section 7: Complete Theming Example

### Comprehensive App Theme

```dart
// lib/theme/app_theme.dart
import 'package:flutter/material.dart';

class AppTheme {
  // Brand colors
  static const seedColor = Color(0xFF6750A4);

  // Light theme
  static ThemeData lightTheme = ThemeData(
    colorScheme: ColorScheme.fromSeed(
      seedColor: seedColor,
      brightness: Brightness.light,
    ),

    // Typography
    textTheme: const TextTheme(
      displayLarge: TextStyle(
        fontSize: 96,
        fontWeight: FontWeight.bold,
      ),
      headlineLarge: TextStyle(
        fontSize: 32,
        fontWeight: FontWeight.w600,
      ),
      titleLarge: TextStyle(
        fontSize: 22,
        fontWeight: FontWeight.w500,
      ),
      bodyLarge: TextStyle(
        fontSize: 16,
        height: 1.5,
      ),
    ),

    // AppBar
    appBarTheme: const AppBarTheme(
      centerTitle: true,
      elevation: 2,
    ),

    // Buttons
    elevatedButtonTheme: ElevatedButtonThemeData(
      style: ElevatedButton.styleFrom(
        padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(8),
        ),
      ),
    ),

    // Input fields
    inputDecorationTheme: InputDecorationTheme(
      filled: true,
      border: OutlineInputBorder(
        borderRadius: BorderRadius.circular(8),
      ),
      contentPadding: const EdgeInsets.symmetric(
        horizontal: 16,
        vertical: 12,
      ),
    ),

    // Cards
    cardTheme: CardTheme(
      elevation: 2,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
      ),
    ),
  );

  // Dark theme
  static ThemeData darkTheme = ThemeData(
    colorScheme: ColorScheme.fromSeed(
      seedColor: seedColor,
      brightness: Brightness.dark,
    ),

    // Same text theme as light
    textTheme: lightTheme.textTheme,

    // Dark-specific customizations
    appBarTheme: const AppBarTheme(
      centerTitle: true,
      elevation: 0,
    ),

    elevatedButtonTheme: lightTheme.elevatedButtonTheme,
    inputDecorationTheme: lightTheme.inputDecorationTheme,
    cardTheme: lightTheme.cardTheme,
  );
}
```

### Using the Theme

```dart
// lib/main.dart
import 'package:flutter/material.dart';
import 'theme/app_theme.dart';

void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Themed App',
      theme: AppTheme.lightTheme,
      darkTheme: AppTheme.darkTheme,
      themeMode: ThemeMode.system,
      home: const HomeScreen(),
    );
  }
}

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Material 3 Theme'),
      ),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          // Headline
          Text(
            'Welcome!',
            style: Theme.of(context).textTheme.headlineLarge,
          ),
          const SizedBox(height: 16),

          // Body text
          Text(
            'This app uses a custom Material 3 theme with consistent colors, typography, and component styling.',
            style: Theme.of(context).textTheme.bodyLarge,
          ),
          const SizedBox(height: 24),

          // Buttons
          ElevatedButton(
            onPressed: () {},
            child: const Text('Elevated Button'),
          ),
          const SizedBox(height: 16),

          FilledButton(
            onPressed: () {},
            child: const Text('Filled Button'),
          ),
          const SizedBox(height: 16),

          OutlinedButton(
            onPressed: () {},
            child: const Text('Outlined Button'),
          ),
          const SizedBox(height: 24),

          // Card
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'Themed Card',
                    style: Theme.of(context).textTheme.titleLarge,
                  ),
                  const SizedBox(height: 8),
                  Text(
                    'This card automatically uses the theme\'s card styling.',
                    style: Theme.of(context).textTheme.bodyMedium,
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 24),

          // Input field
          const TextField(
            decoration: InputDecoration(
              labelText: 'Username',
              hintText: 'Enter your username',
              prefixIcon: Icon(Icons.person),
            ),
          ),
        ],
      ),
    );
  }
}
```

---

## Section 8: Best Practices

### 1. Use One Seed Color for Consistency

**Good:**
```dart
// Same seed color for light and dark
theme: ThemeData(
  colorScheme: ColorScheme.fromSeed(
    seedColor: brandColor,
    brightness: Brightness.light,
  ),
),
darkTheme: ThemeData(
  colorScheme: ColorScheme.fromSeed(
    seedColor: brandColor,  // Same color!
    brightness: Brightness.dark,
  ),
),
```

### 2. Extract Theme to Separate File

```
lib/
├── theme/
│   ├── app_theme.dart      # Theme definitions
│   └── app_colors.dart     # Color constants
├── main.dart
└── ...
```

### 3. Always Use Theme Colors, Never Hardcode

**Bad:**
```dart
Container(
  color: Color(0xFF6750A4),  // Hardcoded!
  child: Text(
    'Hello',
    style: TextStyle(color: Colors.white),  // Hardcoded!
  ),
)
```

**Good:**
```dart
Container(
  color: Theme.of(context).colorScheme.primary,  // From theme
  child: Text(
    'Hello',
    style: Theme.of(context).textTheme.bodyLarge?.copyWith(
      color: Theme.of(context).colorScheme.onPrimary,  // From theme
    ),
  ),
)
```

### 4. Use Material 3 Color Roles

Material 3 provides semantic color roles:
- `primary` - Main brand actions
- `secondary` - Less prominent actions
- `tertiary` - Complementary accents
- `error` - Errors and warnings
- `surface` - Card and sheet backgrounds
- `onPrimary`, `onSecondary`, etc. - Text on those colors

Use these instead of arbitrary colors!

### 5. Test Both Light and Dark Themes

Always test your app in both themes:
```dart
// In main.dart, temporarily force dark mode for testing
themeMode: ThemeMode.dark,  // Change to test
```

---

## Quiz

Test your understanding of app theming:

### Question 1
What is the recommended way to create a color scheme in Material 3?

A) Define 30 colors manually
B) Use `ColorScheme.fromSeed()` with a single seed color
C) Use hexadecimal color codes throughout your app
D) Copy colors from another app

### Question 2
What does `Theme.of(context)` do?

A) Creates a new theme
B) Returns the current theme applied to the widget tree
C) Switches between light and dark themes
D) Deletes the current theme

### Question 3
Which TextTheme style should you use for button text?

A) `displayLarge`
B) `headlineLarge`
C) `bodyLarge`
D) `labelLarge`

### Question 4
What is the correct way to make an app support both light and dark themes?

A) Create two separate apps
B) Define both `theme` and `darkTheme` in MaterialApp
C) Use only dark colors
D) Themes can't be switched

### Question 5
When should you hardcode colors like `Colors.red` in your widgets?

A) Always, for precision
B) For important elements only
C) Rarely—use theme colors instead for consistency
D) Never, even for semantic colors like error states

---

## Answer Key

**Question 1: B** - `ColorScheme.fromSeed()` is the Material 3 way. It generates a complete, harmonious 30-color palette from a single seed color automatically.

**Question 2: B** - `Theme.of(context)` returns the nearest ThemeData in the widget tree, allowing you to access theme colors, text styles, and other styling information.

**Question 3: D** - `labelLarge` is specifically designed for button text in Material 3. It has appropriate sizing and weight for button labels.

**Question 4: B** - Define both `theme` (light) and `darkTheme` (dark) in MaterialApp, then use `themeMode` to control which is active. Flutter handles the switching automatically.

**Question 5: C** - Use theme colors for consistency. Hardcoded colors should be rare exceptions. For error states, use `Theme.of(context).colorScheme.error` instead of `Colors.red`.

---

## Summary

In this lesson, you learned:

✅ **ThemeData** defines your app's visual style in one place
✅ **ColorScheme.fromSeed()** generates a complete color palette from one seed color
✅ **Material 3** is the default in Flutter 3.16+ with 30+ coordinated colors
✅ **Light and dark themes** can be implemented with `theme` and `darkTheme`
✅ **TextTheme** provides predefined styles for different text purposes
✅ **Component themes** customize specific widgets (buttons, cards, inputs)
✅ **Theme.of(context)** accesses theme data anywhere in your widget tree
✅ Using theme colors ensures **consistency** across your entire app

**Key Takeaway:** Theming is essential for professional apps. Define your theme once, and every widget automatically follows your design system. Change your seed color, and your entire app updates instantly. This saves time, ensures consistency, and makes your app look polished!

---

## What's Next?

In **Lesson 3.8: Mini-Project - Instagram-Style Feed** (previously Lesson 3.7), you'll apply everything you've learned about layouts AND theming to build a beautiful, themed social media feed with custom styling!
