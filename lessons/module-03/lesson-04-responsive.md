# Module 3, Lesson 4: Responsive Layouts

## The Multi-Screen Challenge

Your app runs on:
- Small phones (320px wide)
- Large phones (400px+)  
- Tablets (600px+)
- Desktop (1200px+)

**One layout doesn't fit all!** You need **responsive** design.

---

## MediaQuery - Screen Information

Get device screen info:

```dart
double screenWidth = MediaQuery.of(context).size.width;
double screenHeight = MediaQuery.of(context).size.height;
```

---

## Conditional Layouts

```dart
class ResponsiveLayout extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    double width = MediaQuery.of(context).size.width;
    
    if (width < 600) {
      return MobileLayout();  // Phone
    } else if (width < 1200) {
      return TabletLayout();  // Tablet
    } else {
      return DesktopLayout(); // Desktop
    }
  }
}
```

---

## LayoutBuilder - Responsive to Parent

```dart
LayoutBuilder(
  builder: (context, constraints) {
    if (constraints.maxWidth < 600) {
      // Mobile: single column
      return Column(children: items);
    } else {
      // Tablet+: two columns
      return Row(
        children: [
          Expanded(child: Column(children: items.take(half))),
          Expanded(child: Column(children: items.skip(half))),
        ],
      );
    }
  },
)
```

---

## Flexible Columns with GridView

```dart
GridView.extent(
  maxCrossAxisExtent: 200,  // Adjusts columns automatically!
  children: items,
)
```

On 400px screen: 2 columns
On 800px screen: 4 columns
Auto-responsive!

---

## Expanded and Flexible

```dart
Row(
  children: [
    Flexible(
      flex: 1,
      child: Container(color: Colors.red),
    ),
    Flexible(
      flex: 2,  // Takes 2x the space
      child: Container(color: Colors.blue),
    ),
  ],
)
```

---

## SafeArea - Avoid Notches

```dart
Scaffold(
  body: SafeArea(  // Avoids notches, status bar
    child: YourContent(),
  ),
)
```

---

## Orientation Aware

```dart
OrientationBuilder(
  builder: (context, orientation) {
    if (orientation == Orientation.portrait) {
      return PortraitLayout();
    } else {
      return LandscapeLayout();
    }
  },
)
```

---

## ✅ YOUR CHALLENGE: Responsive Grid

Create an app that shows:
- 2 columns on phones (<600px)
- 3 columns on tablets (600-1200px)
- 4 columns on desktop (>1200px)

Use MediaQuery or GridView.extent!

**Success Condition**: Grid adjusts to screen width! ✅

---

## What's Next?

You can now build responsive layouts! Next: creating **custom, reusable widgets** to organize your code!
