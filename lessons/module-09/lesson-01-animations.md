# Lesson 1: Introduction to Animations

## What You'll Learn
- Understanding animations and their importance
- Implicit vs explicit animations
- AnimatedContainer and AnimatedOpacity
- Hero animations for screen transitions
- Custom animations with AnimationController

## Concept First: Why Animations Matter

### Real-World Analogy
Think of animations like the smooth movements in a well-choreographed dance. Without animations, your app would be like a slideshow of still photos. With animations, it becomes a fluid movie where each transition tells a story and guides the user's attention.

Just like how a door **gradually swings open** (not teleporting from closed to open), good UI animations help users understand what's happening and where things are going.

### Why This Matters
Animations are not just "eye candy" - they serve important purposes:

1. **Feedback**: Show that a button was pressed
2. **Guidance**: Direct attention to important elements
3. **Relationships**: Show how UI elements connect
4. **Continuity**: Smooth transitions prevent jarring experiences
5. **Polish**: Professional apps feel smooth and responsive

According to Material Design guidelines, animations should be:
- **Fast**: 200-300ms for most transitions
- **Natural**: Follow physics (easing curves, not linear)
- **Purposeful**: Every animation should have a reason

---

## Setting Up

No external packages needed for basic animations! Flutter has powerful built-in animation widgets.

For advanced animations (optional):
```yaml
dependencies:
  flutter_animate: ^4.5.0  # Easy-to-use animation effects
```

---

## The Animation Building Blocks

### 1. Implicit Animations (Easy Mode)

These are "smart" widgets that automatically animate when their properties change.

**Example: AnimatedContainer**

```dart
import 'package:flutter/material.dart';

class AnimatedBoxScreen extends StatefulWidget {
  @override
  State<AnimatedBoxScreen> createState() => _AnimatedBoxScreenState();
}

class _AnimatedBoxScreenState extends State<AnimatedBoxScreen> {
  // State variables that will animate
  bool _isExpanded = false;
  Color _boxColor = Colors.blue;

  void _toggle() {
    setState(() {
      _isExpanded = !_isExpanded;
      _boxColor = _isExpanded ? Colors.red : Colors.blue;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Animated Container')),
      body: Center(
        child: GestureDetector(
          onTap: _toggle,
          child: AnimatedContainer(
            // These properties will animate automatically!
            width: _isExpanded ? 300 : 100,
            height: _isExpanded ? 300 : 100,
            color: _boxColor,

            // Animation configuration
            duration: Duration(milliseconds: 300),
            curve: Curves.easeInOut,  // Natural motion

            // Optional: animate decoration too
            decoration: BoxDecoration(
              color: _boxColor,
              borderRadius: BorderRadius.circular(_isExpanded ? 50 : 10),
            ),

            child: Center(
              child: Text(
                _isExpanded ? 'Expanded!' : 'Tap Me',
                style: TextStyle(
                  color: Colors.white,
                  fontSize: _isExpanded ? 24 : 14,
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}
```

**How It Works:**
- Change the properties in `setState()`
- AnimatedContainer detects the changes
- Automatically animates from old → new values
- Magic! ✨

### 2. More Implicit Animation Widgets

```dart
// Animate opacity (fade in/out)
AnimatedOpacity(
  opacity: _isVisible ? 1.0 : 0.0,
  duration: Duration(milliseconds: 500),
  child: Text('Now you see me...'),
)

// Animate position
AnimatedPositioned(
  left: _isMoved ? 100 : 0,
  top: _isMoved ? 200 : 0,
  duration: Duration(milliseconds: 400),
  child: Container(width: 50, height: 50, color: Colors.green),
)

// Animate alignment
AnimatedAlign(
  alignment: _isTop ? Alignment.topCenter : Alignment.bottomCenter,
  duration: Duration(milliseconds: 300),
  child: Icon(Icons.star, size: 50),
)

// Animate between widgets
AnimatedSwitcher(
  duration: Duration(milliseconds: 300),
  child: _showFirst ? FirstWidget(key: ValueKey(1)) : SecondWidget(key: ValueKey(2)),
)

// Animate padding
AnimatedPadding(
  padding: EdgeInsets.all(_isPadded ? 40 : 0),
  duration: Duration(milliseconds: 250),
  child: Container(color: Colors.orange),
)
```

### 3. Hero Animations (Shared Element Transitions)

Hero animations create smooth transitions when navigating between screens.

**Screen 1 (List of Items):**
```dart
class ProductListScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Products')),
      body: ListView.builder(
        itemCount: 10,
        itemBuilder: (context, index) {
          return ListTile(
            leading: Hero(
              tag: 'product-$index',  // Unique identifier
              child: CircleAvatar(
                backgroundImage: NetworkImage(
                  'https://picsum.photos/seed/$index/200',
                ),
              ),
            ),
            title: Text('Product $index'),
            onTap: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (_) => ProductDetailScreen(productId: index),
                ),
              );
            },
          );
        },
      ),
    );
  }
}
```

**Screen 2 (Detail):**
```dart
class ProductDetailScreen extends StatelessWidget {
  final int productId;

  ProductDetailScreen({required this.productId});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Product Details')),
      body: Column(
        children: [
          // Same Hero tag = automatic animation!
          Hero(
            tag: 'product-$productId',
            child: Image.network(
              'https://picsum.photos/seed/$productId/400',
              height: 300,
              width: double.infinity,
              fit: BoxFit.cover,
            ),
          ),
          Padding(
            padding: EdgeInsets.all(16),
            child: Text(
              'Product $productId Details',
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
            ),
          ),
        ],
      ),
    );
  }
}
```

**How Hero Works:**
1. Both screens have a `Hero` widget with the **same tag**
2. When navigating, Flutter finds both Hero widgets
3. Automatically animates the transition between them
4. The image "flies" from list to detail screen!

---

## Explicit Animations (Full Control)

For complex custom animations, use `AnimationController`.

### Complete Example: Pulsing Heart

```dart
class PulsingHeartScreen extends StatefulWidget {
  @override
  State<PulsingHeartScreen> createState() => _PulsingHeartScreenState();
}

class _PulsingHeartScreenState extends State<PulsingHeartScreen>
    with SingleTickerProviderStateMixin {  // Required for animations

  late AnimationController _controller;
  late Animation<double> _scaleAnimation;
  late Animation<Color?> _colorAnimation;

  @override
  void initState() {
    super.initState();

    // 1. Create the controller (the "conductor")
    _controller = AnimationController(
      duration: Duration(milliseconds: 800),
      vsync: this,  // Sync with screen refresh
    );

    // 2. Create animations (the "dancers")
    _scaleAnimation = Tween<double>(
      begin: 1.0,
      end: 1.3,
    ).animate(CurvedAnimation(
      parent: _controller,
      curve: Curves.easeInOut,
    ));

    _colorAnimation = ColorTween(
      begin: Colors.red,
      end: Colors.pink,
    ).animate(_controller);

    // 3. Make it loop
    _controller.repeat(reverse: true);
  }

  @override
  void dispose() {
    _controller.dispose();  // Always clean up!
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Pulsing Heart')),
      body: Center(
        child: AnimatedBuilder(
          animation: _controller,
          builder: (context, child) {
            return Transform.scale(
              scale: _scaleAnimation.value,
              child: Icon(
                Icons.favorite,
                size: 100,
                color: _colorAnimation.value,
              ),
            );
          },
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          if (_controller.isAnimating) {
            _controller.stop();
          } else {
            _controller.repeat(reverse: true);
          }
          setState(() {});
        },
        child: Icon(_controller.isAnimating ? Icons.pause : Icons.play_arrow),
      ),
    );
  }
}
```

**Key Concepts:**
- **AnimationController**: Controls the animation (start, stop, reverse)
- **Tween**: Defines start and end values
- **CurvedAnimation**: Applies easing curves
- **AnimatedBuilder**: Rebuilds when animation changes
- **SingleTickerProviderStateMixin**: Optimizes animations

---

## Animation Curves

Curves make animations feel natural (not robotic).

```dart
// Common curves
Curves.easeInOut    // Slow → Fast → Slow (most natural)
Curves.easeIn       // Start slow, end fast
Curves.easeOut      // Start fast, end slow
Curves.linear       // Constant speed (robotic)
Curves.bounceOut    // Bouncy landing
Curves.elasticOut   // Spring effect
Curves.decelerate   // Gradually slow down
```

**Example Usage:**
```dart
AnimatedContainer(
  duration: Duration(milliseconds: 300),
  curve: Curves.bounceOut,  // Add personality!
  // ... properties
)
```

---

## Complete Example: Animated Login Screen

```dart
import 'package:flutter/material.dart';

class AnimatedLoginScreen extends StatefulWidget {
  @override
  State<AnimatedLoginScreen> createState() => _AnimatedLoginScreenState();
}

class _AnimatedLoginScreenState extends State<AnimatedLoginScreen>
    with SingleTickerProviderStateMixin {

  late AnimationController _controller;
  late Animation<double> _logoScale;
  late Animation<Offset> _formSlide;
  late Animation<double> _formFade;

  @override
  void initState() {
    super.initState();

    _controller = AnimationController(
      duration: Duration(milliseconds: 1500),
      vsync: this,
    );

    // Logo scales up
    _logoScale = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(
        parent: _controller,
        curve: Interval(0.0, 0.5, curve: Curves.elasticOut),
      ),
    );

    // Form slides up from bottom
    _formSlide = Tween<Offset>(
      begin: Offset(0, 1),
      end: Offset.zero,
    ).animate(
      CurvedAnimation(
        parent: _controller,
        curve: Interval(0.3, 0.8, curve: Curves.easeOut),
      ),
    );

    // Form fades in
    _formFade = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(
        parent: _controller,
        curve: Interval(0.5, 1.0, curve: Curves.easeIn),
      ),
    );

    // Start the animation
    _controller.forward();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
            colors: [Colors.blue.shade300, Colors.purple.shade300],
          ),
        ),
        child: SafeArea(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              // Animated Logo
              ScaleTransition(
                scale: _logoScale,
                child: Icon(
                  Icons.lock_outline,
                  size: 120,
                  color: Colors.white,
                ),
              ),

              SizedBox(height: 50),

              // Animated Form
              SlideTransition(
                position: _formSlide,
                child: FadeTransition(
                  opacity: _formFade,
                  child: Padding(
                    padding: EdgeInsets.all(20),
                    child: Card(
                      elevation: 8,
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(16),
                      ),
                      child: Padding(
                        padding: EdgeInsets.all(20),
                        child: Column(
                          children: [
                            Text(
                              'Welcome Back',
                              style: TextStyle(
                                fontSize: 28,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                            SizedBox(height: 20),
                            TextField(
                              decoration: InputDecoration(
                                labelText: 'Email',
                                prefixIcon: Icon(Icons.email),
                              ),
                            ),
                            SizedBox(height: 16),
                            TextField(
                              obscureText: true,
                              decoration: InputDecoration(
                                labelText: 'Password',
                                prefixIcon: Icon(Icons.lock),
                              ),
                            ),
                            SizedBox(height: 24),
                            ElevatedButton(
                              onPressed: () {},
                              style: ElevatedButton.styleFrom(
                                minimumSize: Size(double.infinity, 50),
                              ),
                              child: Text('Login', style: TextStyle(fontSize: 18)),
                            ),
                          ],
                        ),
                      ),
                    ),
                  ),
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

**Advanced Techniques Used:**
- **Interval**: Stagger animations (logo first, then form)
- **ScaleTransition**: Built-in widget for scaling
- **SlideTransition**: Built-in widget for sliding
- **FadeTransition**: Built-in widget for fading

---

## Quick Reference: Implicit Animation Widgets

| Widget | What It Animates | Use Case |
|--------|------------------|----------|
| AnimatedContainer | Size, color, padding, decoration | Most versatile |
| AnimatedOpacity | Transparency | Fade in/out |
| AnimatedPositioned | Position (in Stack) | Move elements |
| AnimatedAlign | Alignment | Snap to corners |
| AnimatedPadding | Padding | Spacing changes |
| AnimatedSwitcher | Widget replacement | Toggle content |
| AnimatedCrossFade | Fade between 2 widgets | A/B switches |
| AnimatedDefaultTextStyle | Text style | Text formatting |
| AnimatedPhysicalModel | Elevation, shadow | 3D effects |

---

## Best Practices

1. **Keep It Fast**: 200-400ms for most animations
2. **Be Consistent**: Use same durations/curves throughout app
3. **Don't Overdo It**: Not every element needs animation
4. **Test on Real Devices**: Animations look different on actual hardware
5. **Dispose Controllers**: Always call `dispose()` to prevent memory leaks
6. **Use Implicit First**: Only use explicit animations when needed

---

## Quiz

**Question 1:** What's the difference between implicit and explicit animations?
A) Implicit are faster than explicit
B) Implicit animate automatically when properties change; explicit require AnimationController
C) Explicit can only animate colors
D) There is no difference

**Question 2:** What does the `Hero` widget require to work across screens?
A) The same tag on both screens
B) The same size widget
C) An AnimationController
D) The flutter_animate package

**Question 3:** What is the recommended duration for most UI animations?
A) 50-100ms
B) 200-400ms
C) 1-2 seconds
D) As long as possible for dramatic effect

---

## Exercise: Bouncing Ball

Create a screen with a ball that:
1. Bounces up and down continuously
2. Changes color on each bounce
3. Has a pause/play button

**Hints:**
- Use `AnimationController` with `repeat(reverse: true)`
- Use `Tween<double>` for position
- Use `ColorTween` for color changes
- Use `Curves.bounceOut` for natural motion

---

## Summary

You've learned how to bring your Flutter apps to life with animations! Here's what we covered:

- **Implicit Animations**: Easy, automatic animations (AnimatedContainer, AnimatedOpacity, etc.)
- **Hero Animations**: Smooth shared element transitions between screens
- **Explicit Animations**: Full control with AnimationController
- **Animation Curves**: Make animations feel natural
- **Best Practices**: Keep it fast, consistent, and purposeful

Animations transform your app from functional to delightful. Users may not notice good animations, but they'll definitely feel the difference!

---

## Answer Key

**Answer 1:** B) Implicit animate automatically when properties change; explicit require AnimationController

Implicit animations (like AnimatedContainer) detect property changes and animate automatically. Explicit animations require manual setup with AnimationController for full control.

**Answer 2:** A) The same tag on both screens

Hero animations work by matching the `tag` property. Flutter finds Hero widgets with the same tag and animates between them during navigation.

**Answer 3:** B) 200-400ms

Material Design recommends 200-400ms for most transitions. This is fast enough to be responsive but slow enough to be noticeable and guide user attention.
