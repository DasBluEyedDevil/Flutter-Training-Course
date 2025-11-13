# Module 3, Lesson 6: Advanced Scrolling Techniques

## Beyond Basic Lists

You've learned ListView for simple scrolling. But what about:
- Horizontal scrolling
- Mixing scrolling directions
- Scrolling only when needed

Let's master advanced scrolling!

---

## SingleChildScrollView

Makes ANY widget scrollable:

```dart
SingleChildScrollView(
  child: Column(
    children: [
      Container(height: 200, color: Colors.red),
      Container(height: 200, color: Colors.blue),
      Container(height: 200, color: Colors.green),
      Container(height: 200, color: Colors.yellow),
      // If total height > screen, it scrolls!
    ],
  ),
)
```

**Use case**: Forms, long content that might overflow.

---

## Horizontal Scrolling

```dart
SingleChildScrollView(
  scrollDirection: Axis.horizontal,
  child: Row(
    children: [
      Container(width: 200, color: Colors.red),
      Container(width: 200, color: Colors.blue),
      Container(width: 200, color: Colors.green),
    ],
  ),
)
```

**Use case**: Image galleries, category chips.

---

## PageView - Swipeable Pages

Like Instagram stories:

```dart
PageView(
  children: [
    Container(color: Colors.red, child: Center(child: Text('Page 1'))),
    Container(color: Colors.blue, child: Center(child: Text('Page 2'))),
    Container(color: Colors.green, child: Center(child: Text('Page 3'))),
  ],
)
```

Swipe to navigate!

---

## PageView with Indicator

```dart
class PageViewExample extends StatefulWidget {
  @override
  _PageViewExampleState createState() => _PageViewExampleState();
}

class _PageViewExampleState extends State<PageViewExample> {
  int currentPage = 0;
  
  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Expanded(
          child: PageView(
            onPageChanged: (index) {
              setState(() {
                currentPage = index;
              });
            },
            children: [
              Container(color: Colors.red),
              Container(color: Colors.blue),
              Container(color: Colors.green),
            ],
          ),
        ),
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: List.generate(3, (index) {
            return Container(
              margin: EdgeInsets.all(4),
              width: 8,
              height: 8,
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                color: currentPage == index ? Colors.blue : Colors.grey,
              ),
            );
          }),
        ),
      ],
    );
  }
}
```

---

## Wrap - Auto-wrapping

Like word wrap, but for widgets:

```dart
Wrap(
  spacing: 8,  // Horizontal spacing
  runSpacing: 8,  // Vertical spacing
  children: [
    Chip(label: Text('Flutter')),
    Chip(label: Text('Dart')),
    Chip(label: Text('Mobile')),
    Chip(label: Text('Development')),
    Chip(label: Text('UI')),
    // Auto-wraps to next line when needed!
  ],
)
```

**Use case**: Tags, filter chips, buttons that wrap.

---

## NestedScrollView

Scroll a header away:

```dart
NestedScrollView(
  headerSliverBuilder: (context, innerBoxIsScrolled) {
    return [
      SliverAppBar(
        expandedHeight: 200,
        floating: false,
        pinned: true,
        flexibleSpace: FlexibleSpaceBar(
          title: Text('My App'),
          background: Image.network('url', fit: BoxFit.cover),
        ),
      ),
    ];
  },
  body: ListView.builder(
    itemCount: 50,
    itemBuilder: (context, index) {
      return ListTile(title: Text('Item $index'));
    },
  ),
)
```

---

## RefreshIndicator - Pull to Refresh

```dart
RefreshIndicator(
  onRefresh: () async {
    // Load new data
    await Future.delayed(Duration(seconds: 2));
    print('Refreshed!');
  },
  child: ListView(
    children: items,
  ),
)
```

---

## ✅ YOUR CHALLENGE: Product Categories

Create:
1. Horizontal scrolling category chips (Wrap or Row)
2. Vertical product list
3. Pull-to-refresh functionality

**Success Condition**: Mixed scrolling directions! ✅

---

## What's Next?

Final Module 3 lesson: **Mini-project** combining all layout techniques!
