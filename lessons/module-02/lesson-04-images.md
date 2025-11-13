# Module 2, Lesson 4: Displaying Images

## Why Images Matter

A picture is worth a thousand words! Images make apps come alive:
- **Icons** for buttons and navigation
- **Photos** for social media
- **Logos** for branding
- **Illustrations** for instructions

Flutter makes it easy to display images from different sources.

---

## Two Types of Images

### 1. Network Images (from the internet)

Like linking to a photo on the web.

### 2. Asset Images (bundled with your app)

Like photos you pack in your suitcase - they're always with you.

---

## Network Images - The Easy Way

Display an image from a URL:

```dart
import 'package:flutter/material.dart';

void main() {
  runApp(
    MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: Text('Network Image')),
        body: Center(
          child: Image.network(
            'https://picsum.photos/200/300',
          ),
        ),
      ),
    ),
  );
}
```

**That's it!** The image loads from the internet and displays.

---

## Controlling Image Size

### Fixed Width and Height

```dart
Image.network(
  'https://picsum.photos/200/300',
  width: 200,
  height: 200,
)
```

### Using fit Property

```dart
Image.network(
  'https://picsum.photos/200/300',
  width: 300,
  height: 200,
  fit: BoxFit.cover,  // Fills the space, may crop
)
```

**Common fit values**:
- `BoxFit.cover` - Fill space, may crop
- `BoxFit.contain` - Fit entirely, may have empty space
- `BoxFit.fill` - Stretch to fill (may distort)
- `BoxFit.fitWidth` - Fit width, height adjusts
- `BoxFit.fitHeight` - Fit height, width adjusts

---

## Asset Images - Images in Your App

### Step 1: Create an assets folder

```bash
mkdir assets
mkdir assets/images
```

### Step 2: Add images

Put your image files (like `logo.png`) in `assets/images/`

### Step 3: Register in pubspec.yaml

Edit `pubspec.yaml`:

```yaml
flutter:
  assets:
    - assets/images/logo.png
    - assets/images/  # Or include whole folder
```

**Important**: Indentation matters in YAML!

### Step 4: Use in code

```dart
Image.asset('assets/images/logo.png')
```

---

## Complete Asset Image Example

```dart
import 'package:flutter/material.dart';

void main() {
  runApp(
    MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: Text('Asset Image')),
        body: Center(
          child: Image.asset(
            'assets/images/logo.png',
            width: 200,
            height: 200,
          ),
        ),
      ),
    ),
  );
}
```

---

## Circular Images

Use `CircleAvatar`:

```dart
CircleAvatar(
  radius: 50,
  backgroundImage: NetworkImage(
    'https://picsum.photos/200',
  ),
)
```

Or use `ClipOval`:

```dart
ClipOval(
  child: Image.network(
    'https://picsum.photos/200',
    width: 100,
    height: 100,
    fit: BoxFit.cover,
  ),
)
```

---

## Loading Indicator

Show a loading spinner while image loads:

```dart
Image.network(
  'https://picsum.photos/200/300',
  loadingBuilder: (context, child, progress) {
    if (progress == null) return child;
    return CircularProgressIndicator();
  },
)
```

---

## Error Handling

What if the image fails to load?

```dart
Image.network(
  'https://invalid-url.com/image.jpg',
  errorBuilder: (context, error, stackTrace) {
    return Text('Failed to load image');
  },
)
```

---

## Icons - Special Images

Flutter has tons of built-in icons:

```dart
Icon(
  Icons.favorite,
  color: Colors.red,
  size: 50,
)

Icon(Icons.star)
Icon(Icons.home)
Icon(Icons.settings)
Icon(Icons.person)
Icon(Icons.search)
```

**Explore all icons**: https://api.flutter.dev/flutter/material/Icons-class.html

---

## Combining Images and Text

```dart
import 'package:flutter/material.dart';

void main() {
  runApp(
    MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: Text('Profile')),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              CircleAvatar(
                radius: 60,
                backgroundImage: NetworkImage(
                  'https://picsum.photos/200',
                ),
              ),
              SizedBox(height: 20),
              Text(
                'John Doe',
                style: TextStyle(
                  fontSize: 24,
                  fontWeight: FontWeight.bold,
                ),
              ),
              SizedBox(height: 10),
              Text(
                'Flutter Developer',
                style: TextStyle(
                  fontSize: 16,
                  color: Colors.grey,
                ),
              ),
            ],
          ),
        ),
      ),
    ),
  );
}
```

---

## ✅ YOUR CHALLENGE: Create a Photo Gallery

Create an app with:
1. At least 3 network images
2. Different sizes
3. At least one circular image
4. Text labels under each image

**Bonus**: Add a loading indicator!

**Success Condition**: Your app displays a gallery of images! ✅

---

## What Did We Learn?

- ✅ `Image.network()` loads from URLs
- ✅ `Image.asset()` loads bundled images
- ✅ `BoxFit` controls how images fill space
- ✅ `CircleAvatar` creates circular images
- ✅ `Icon` widget for built-in icons
- ✅ Loading and error handling

---

## What's Next?

You can display text and images! Next, we'll learn about **Container** - the Swiss Army knife widget for layout and decoration. It's like a box you can style however you want!
