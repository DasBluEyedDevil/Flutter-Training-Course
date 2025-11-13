# Module 4, Lesson 3: Gestures and Touch Interactions

## Beyond Buttons

Buttons are great, but apps need richer interactions:
- **Swipe** to delete items
- **Long press** for context menus
- **Drag** items around
- **Pinch** to zoom
- **Double tap** to like

**Flutter makes this easy with GestureDetector!**

---

## The GestureDetector Widget

Wrap ANY widget to make it detect gestures:

```dart
GestureDetector(
  onTap: () {
    print('Tapped!');
  },
  child: Container(
    width: 200,
    height: 200,
    color: Colors.blue,
    child: Center(child: Text('Tap Me')),
  ),
)
```

---

## All Available Gestures

```dart
GestureDetector(
  onTap: () => print('Single tap'),
  onDoubleTap: () => print('Double tap'),
  onLongPress: () => print('Long press'),
  onPanUpdate: (details) => print('Dragging: ${details.delta}'),
  onScaleUpdate: (details) => print('Pinching: ${details.scale}'),
  child: YourWidget(),
)
```

---

## Tap vs InkWell

**GestureDetector**: No visual feedback
**InkWell**: Material ripple effect

```dart
// No visual feedback
GestureDetector(
  onTap: () => print('Tap'),
  child: Container(
    color: Colors.blue,
    padding: EdgeInsets.all(20),
    child: Text('Tap Me'),
  ),
)

// With ripple effect
InkWell(
  onTap: () => print('Tap'),
  child: Container(
    padding: EdgeInsets.all(20),
    child: Text('Tap Me'),
  ),
)
```

**Best Practice**: Use InkWell for Material Design apps!

---

## Double Tap Example (Like Button)

```dart
class LikeableImage extends StatefulWidget {
  @override
  _LikeableImageState createState() => _LikeableImageState();
}

class _LikeableImageState extends State<LikeableImage> {
  bool isLiked = false;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onDoubleTap: () {
        setState(() {
          isLiked = !isLiked;
        });
      },
      child: Stack(
        alignment: Alignment.center,
        children: [
          Image.network(
            'https://picsum.photos/400',
            width: 400,
            height: 400,
            fit: BoxFit.cover,
          ),
          if (isLiked)
            Icon(
              Icons.favorite,
              size: 100,
              color: Colors.red.withOpacity(0.7),
            ),
        ],
      ),
    );
  }
}
```

**Instagram-style double-tap to like!**

---

## Long Press for Context Menu

```dart
class LongPressExample extends StatelessWidget {
  void _showContextMenu(BuildContext context) {
    showModalBottomSheet(
      context: context,
      builder: (context) => Container(
        padding: EdgeInsets.all(20),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            ListTile(
              leading: Icon(Icons.share),
              title: Text('Share'),
              onTap: () => Navigator.pop(context),
            ),
            ListTile(
              leading: Icon(Icons.edit),
              title: Text('Edit'),
              onTap: () => Navigator.pop(context),
            ),
            ListTile(
              leading: Icon(Icons.delete, color: Colors.red),
              title: Text('Delete', style: TextStyle(color: Colors.red)),
              onTap: () => Navigator.pop(context),
            ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onLongPress: () => _showContextMenu(context),
      child: Card(
        child: ListTile(
          leading: CircleAvatar(child: Icon(Icons.person)),
          title: Text('John Doe'),
          subtitle: Text('Long press for options'),
        ),
      ),
    );
  }
}
```

---

## Drag to Reorder

```dart
class DraggableBox extends StatefulWidget {
  @override
  _DraggableBoxState createState() => _DraggableBoxState();
}

class _DraggableBoxState extends State<DraggableBox> {
  Offset position = Offset(100, 100);

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        Positioned(
          left: position.dx,
          top: position.dy,
          child: GestureDetector(
            onPanUpdate: (details) {
              setState(() {
                position = Offset(
                  position.dx + details.delta.dx,
                  position.dy + details.delta.dy,
                );
              });
            },
            child: Container(
              width: 100,
              height: 100,
              decoration: BoxDecoration(
                color: Colors.blue,
                borderRadius: BorderRadius.circular(8),
                boxShadow: [BoxShadow(color: Colors.black26, blurRadius: 8)],
              ),
              child: Center(
                child: Text(
                  'Drag Me',
                  style: TextStyle(color: Colors.white),
                ),
              ),
            ),
          ),
        ),
      ],
    );
  }
}
```

---

## Swipe to Dismiss

```dart
class SwipeableTodo extends StatelessWidget {
  final List<String> todos = ['Buy milk', 'Walk dog', 'Code Flutter'];

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      itemCount: todos.length,
      itemBuilder: (context, index) {
        return Dismissible(
          key: Key(todos[index]),
          background: Container(
            color: Colors.red,
            alignment: Alignment.centerRight,
            padding: EdgeInsets.only(right: 20),
            child: Icon(Icons.delete, color: Colors.white),
          ),
          direction: DismissDirection.endToStart,
          onDismissed: (direction) {
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(content: Text('${todos[index]} deleted')),
            );
          },
          child: ListTile(
            leading: Icon(Icons.check_box_outline_blank),
            title: Text(todos[index]),
          ),
        );
      },
    );
  }
}
```

**Swipe left to delete - like iOS Mail!**

---

## Haptic Feedback

Add tactile feedback for better UX:

```dart
import 'package:flutter/services.dart';

GestureDetector(
  onTap: () {
    HapticFeedback.lightImpact();  // Subtle vibration
    print('Tapped!');
  },
  onLongPress: () {
    HapticFeedback.heavyImpact();  // Stronger vibration
    print('Long pressed!');
  },
  child: YourWidget(),
)
```

---

## Complete Interactive Card

```dart
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class InteractiveCard extends StatefulWidget {
  @override
  _InteractiveCardState createState() => _InteractiveCardState();
}

class _InteractiveCardState extends State<InteractiveCard> {
  bool isLiked = false;
  bool isBookmarked = false;

  void _showOptions(BuildContext context) {
    showModalBottomSheet(
      context: context,
      builder: (context) => Container(
        padding: EdgeInsets.all(20),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            ListTile(
              leading: Icon(Icons.share),
              title: Text('Share'),
              onTap: () {
                Navigator.pop(context);
                HapticFeedback.lightImpact();
              },
            ),
            ListTile(
              leading: Icon(Icons.report),
              title: Text('Report'),
              onTap: () {
                Navigator.pop(context);
                HapticFeedback.lightImpact();
              },
            ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Header with long press
          GestureDetector(
            onLongPress: () {
              HapticFeedback.mediumImpact();
              _showOptions(context);
            },
            child: ListTile(
              leading: CircleAvatar(
                backgroundImage: NetworkImage('https://i.pravatar.cc/150?img=1'),
              ),
              title: Text('Jane Smith'),
              subtitle: Text('2 hours ago'),
              trailing: Icon(Icons.more_vert),
            ),
          ),

          // Double tap to like image
          GestureDetector(
            onDoubleTap: () {
              HapticFeedback.lightImpact();
              setState(() {
                isLiked = true;
              });
            },
            child: Stack(
              alignment: Alignment.center,
              children: [
                Image.network(
                  'https://picsum.photos/400/300',
                  width: double.infinity,
                  height: 300,
                  fit: BoxFit.cover,
                ),
                if (isLiked)
                  TweenAnimationBuilder(
                    duration: Duration(milliseconds: 300),
                    tween: Tween<double>(begin: 0, end: 1),
                    builder: (context, double value, child) {
                      return Transform.scale(
                        scale: value,
                        child: Icon(
                          Icons.favorite,
                          size: 100,
                          color: Colors.red.withOpacity(1 - value),
                        ),
                      );
                    },
                    onEnd: () {
                      setState(() {
                        isLiked = isLiked;  // Keep liked state
                      });
                    },
                  ),
              ],
            ),
          ),

          // Action buttons
          Padding(
            padding: EdgeInsets.all(8),
            child: Row(
              children: [
                IconButton(
                  icon: Icon(
                    isLiked ? Icons.favorite : Icons.favorite_border,
                    color: isLiked ? Colors.red : null,
                  ),
                  onPressed: () {
                    HapticFeedback.lightImpact();
                    setState(() {
                      isLiked = !isLiked;
                    });
                  },
                ),
                IconButton(
                  icon: Icon(Icons.comment_outlined),
                  onPressed: () {},
                ),
                IconButton(
                  icon: Icon(Icons.share_outlined),
                  onPressed: () {},
                ),
                Spacer(),
                IconButton(
                  icon: Icon(
                    isBookmarked ? Icons.bookmark : Icons.bookmark_border,
                  ),
                  onPressed: () {
                    HapticFeedback.lightImpact();
                    setState(() {
                      isBookmarked = !isBookmarked;
                    });
                  },
                ),
              ],
            ),
          ),

          Padding(
            padding: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'Beautiful sunset!',
                  style: TextStyle(fontWeight: FontWeight.bold),
                ),
                Text(
                  'What an amazing view from the mountains today.',
                  style: TextStyle(color: Colors.grey[600]),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
```

---

## Gesture Priority

**Problem**: What if you have overlapping gestures?

```dart
GestureDetector(
  onTap: () => print('Parent tap'),
  child: Container(
    color: Colors.blue,
    padding: EdgeInsets.all(50),
    child: GestureDetector(
      onTap: () => print('Child tap'),
      child: Container(
        color: Colors.red,
        width: 100,
        height: 100,
      ),
    ),
  ),
)
```

**Result**: Only "Child tap" prints (child wins)

To allow parent to handle: Use `behavior: HitTestBehavior.translucent`

---

## ✅ YOUR CHALLENGES

### Challenge 1: Like Animation
Create a widget that shows a heart animation when double-tapped (like Instagram).

### Challenge 2: Swipeable Cards
Create a Tinder-style swipeable card stack.

### Challenge 3: Context Menu
Add long-press context menus to list items with edit/delete options.

### Challenge 4: Draggable Puzzle
Create a 3x3 grid where tiles can be dragged to reorder.

**Success Condition**: Rich, interactive gestures! ✅

---

## Common Mistakes

❌ **Mistake 1**: No visual feedback
```dart
GestureDetector(
  onTap: () => doSomething(),
  child: Text('Tap me'),  // No indication it's tappable!
)
```

✅ **Fix**: Use InkWell or change colors on tap
```dart
InkWell(
  onTap: () => doSomething(),
  child: Container(
    padding: EdgeInsets.all(16),
    child: Text('Tap me'),
  ),
)
```

❌ **Mistake 2**: Forgetting setState in gesture handlers
```dart
onTap: () {
  isLiked = !isLiked;  // Won't update UI!
}
```

✅ **Fix**: Always use setState
```dart
onTap: () {
  setState(() {
    isLiked = !isLiked;
  });
}
```

---

## What Did We Learn?

- ✅ GestureDetector for custom touch handling
- ✅ Tap, double tap, long press, drag gestures
- ✅ InkWell for Material ripple effects
- ✅ Dismissible for swipe-to-delete
- ✅ Haptic feedback for better UX
- ✅ Building Instagram-style interactions

---

## What's Next?

You've mastered buttons, forms, state, and gestures! Next up: **Navigation and Routing** - how to build multi-screen apps!
