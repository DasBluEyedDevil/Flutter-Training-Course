# Module 2, Lesson 7: Mini-Project - Business Card App

## Putting It All Together!

Congratulations on making it through Module 2! You've learned:
- ✅ How Flutter apps start (main, runApp)
- ✅ Widgets are building blocks
- ✅ Styling text
- ✅ Displaying images
- ✅ Using containers for decoration
- ✅ Arranging widgets with Column and Row

Now let's combine EVERYTHING into a real project!

---

## What We're Building

A **digital business card app** that shows:
- Your name
- Your title/profession
- Your photo
- Contact information (email, phone)
- Social media icons
- A professional design with colors, shadows, and spacing

---

## The Final Result

Your app will look something like this:

```
┌─────────────────────────────────┐
│                                 │
│         ┌──────────┐            │
│         │  Photo   │            │
│         └──────────┘            │
│                                 │
│         Your Name               │
│         Your Title              │
│                                 │
│    ✉ email@example.com          │
│    ☎ +1 234 567 8900            │
│                                 │
│    🔗  💼  📷  🐦               │
│                                 │
└─────────────────────────────────┘
```

---

## Step 1: Create the Project

```bash
flutter create business_card
cd business_card
```

Open `lib/main.dart` and let's start coding!

---

## Step 2: Basic Structure

Replace everything in `main.dart`:

```dart
import 'package:flutter/material.dart';

void main() {
  runApp(BusinessCardApp());
}

class BusinessCardApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Business Card',
      home: BusinessCardScreen(),
    );
  }
}

class BusinessCardScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.teal,
      body: SafeArea(
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text('Your card will go here'),
            ],
          ),
        ),
      ),
    );
  }
}
```

Run it! You should see a teal screen with text.

---

## Step 3: Add the Profile Photo

```dart
class BusinessCardScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.teal,
      body: SafeArea(
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              CircleAvatar(
                radius: 50,
                backgroundColor: Colors.white,
                child: Icon(
                  Icons.person,
                  size: 60,
                  color: Colors.teal,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
```

**Tip**: Replace `Icon(Icons.person...)` with `backgroundImage: NetworkImage('YOUR_PHOTO_URL')` to use a real photo!

---

## Step 4: Add Name and Title

```dart
children: [
  CircleAvatar(
    radius: 50,
    backgroundColor: Colors.white,
    child: Icon(Icons.person, size: 60, color: Colors.teal),
  ),
  SizedBox(height: 20),
  Text(
    'Your Name',
    style: TextStyle(
      fontSize: 40,
      color: Colors.white,
      fontWeight: FontWeight.bold,
    ),
  ),
  Text(
    'FLUTTER DEVELOPER',
    style: TextStyle(
      fontSize: 20,
      color: Colors.teal[100],
      letterSpacing: 2.5,
      fontWeight: FontWeight.bold,
    ),
  ),
]
```

---

## Step 5: Add a Divider

```dart
SizedBox(
  width: 150,
  height: 20,
  child: Divider(
    color: Colors.teal[100],
  ),
)
```

---

## Step 6: Create Contact Info Cards

Let's create a reusable widget for contact info:

```dart
// Add this widget outside BusinessCardScreen class
class ContactCard extends StatelessWidget {
  final IconData icon;
  final String text;

  ContactCard({required this.icon, required this.text});

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: EdgeInsets.symmetric(vertical: 10, horizontal: 25),
      padding: EdgeInsets.all(10),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(5),
      ),
      child: Row(
        children: [
          Icon(icon, color: Colors.teal),
          SizedBox(width: 10),
          Text(
            text,
            style: TextStyle(
              color: Colors.teal[900],
              fontSize: 16,
            ),
          ),
        ],
      ),
    );
  }
}
```

---

## Step 7: Use Contact Cards

In your Column, after the divider:

```dart
ContactCard(
  icon: Icons.phone,
  text: '+1 234 567 8900',
),
ContactCard(
  icon: Icons.email,
  text: 'your.email@example.com',
),
```

---

## Step 8: Add Social Media Icons

```dart
SizedBox(height: 20),
Row(
  mainAxisAlignment: MainAxisAlignment.center,
  children: [
    IconButton(
      icon: Icon(Icons.link, color: Colors.white),
      onPressed: () {
        print('LinkedIn pressed');
      },
    ),
    IconButton(
      icon: Icon(Icons.code, color: Colors.white),
      onPressed: () {
        print('GitHub pressed');
      },
    ),
    IconButton(
      icon: Icon(Icons.camera_alt, color: Colors.white),
      onPressed: () {
        print('Instagram pressed');
      },
    ),
  ],
)
```

---

## Complete Code

```dart
import 'package:flutter/material.dart';

void main() {
  runApp(BusinessCardApp());
}

class BusinessCardApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Business Card',
      theme: ThemeData(
        primarySwatch: Colors.teal,
      ),
      home: BusinessCardScreen(),
    );
  }
}

class BusinessCardScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.teal,
      body: SafeArea(
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              CircleAvatar(
                radius: 50,
                backgroundColor: Colors.white,
                child: Icon(
                  Icons.person,
                  size: 60,
                  color: Colors.teal,
                ),
              ),
              SizedBox(height: 20),
              Text(
                'Your Name',
                style: TextStyle(
                  fontSize: 40,
                  color: Colors.white,
                  fontWeight: FontWeight.bold,
                  fontFamily: 'Pacifico',
                ),
              ),
              Text(
                'FLUTTER DEVELOPER',
                style: TextStyle(
                  fontSize: 20,
                  color: Colors.teal[100],
                  letterSpacing: 2.5,
                  fontWeight: FontWeight.bold,
                ),
              ),
              SizedBox(
                width: 150,
                height: 20,
                child: Divider(
                  color: Colors.teal[100],
                ),
              ),
              ContactCard(
                icon: Icons.phone,
                text: '+1 234 567 8900',
              ),
              ContactCard(
                icon: Icons.email,
                text: 'your.email@example.com',
              ),
              SizedBox(height: 20),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  IconButton(
                    icon: Icon(Icons.link, color: Colors.white, size: 30),
                    onPressed: () {},
                  ),
                  IconButton(
                    icon: Icon(Icons.code, color: Colors.white, size: 30),
                    onPressed: () {},
                  ),
                  IconButton(
                    icon: Icon(Icons.camera_alt, color: Colors.white, size: 30),
                    onPressed: () {},
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class ContactCard extends StatelessWidget {
  final IconData icon;
  final String text;

  ContactCard({required this.icon, required this.text});

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: EdgeInsets.symmetric(vertical: 10, horizontal: 25),
      padding: EdgeInsets.all(10),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(5),
      ),
      child: Row(
        children: [
          Icon(icon, color: Colors.teal),
          SizedBox(width: 10),
          Text(
            text,
            style: TextStyle(
              color: Colors.teal[900],
              fontSize: 16,
            ),
          ),
        ],
      ),
    );
  }
}
```

---

## ✅ YOUR CHALLENGES

### Challenge 1: Personalize It
Replace with your actual:
- Name
- Title
- Email
- Phone number
- Photo URL (or use an asset)

### Challenge 2: Change Colors
Try different color schemes:
- Blue theme: `Colors.blue`
- Purple theme: `Colors.deepPurple`
- Custom: `Color(0xFF6200EA)`

### Challenge 3: Add More Info
Add additional contact cards:
- Website
- Location
- Birthday

### Challenge 4: Add Animation
Make the card fade in when the app starts (we'll learn this in detail later):

```dart
opacity: _visible ? 1.0 : 0.0,
```

### Challenge 5: Make It Responsive
Test on different device sizes and adjust spacing.

**Success Condition**: You have a working, personalized business card app! ✅

---

## What Did We Learn?

Let's recap Module 2:
- ✅ Created a complete Flutter app from scratch
- ✅ Used multiple widgets together
- ✅ Created custom widgets (ContactCard)
- ✅ Applied styling (colors, fonts, spacing)
- ✅ Used Column for vertical layout
- ✅ Used Row for horizontal layout
- ✅ Added images, icons, and text
- ✅ Made a real, shareable project

---

## What's Next?

**Module 2 Complete!** 🎉

You can now build static Flutter apps with beautiful layouts!

In **Module 3**, we'll learn advanced layout techniques:
- ListView for scrollable lists
- GridView for grids
- Stack for overlaying widgets
- Responsive layouts

Get ready to build more complex UIs! 🚀
