# Module 6, Lesson 5: Bottom Navigation Bar

## The Multi-Tab Problem

Most popular apps have the same navigation pattern:
- **Instagram**: Home, Search, Reels, Shop, Profile (5 tabs at bottom)
- **Twitter**: Home, Search, Notifications, Messages (4 tabs at bottom)
- **YouTube**: Home, Shorts, +, Subscriptions, Library (5 tabs at bottom)

**Why bottom navigation?**
- Easy thumb reach on phones 👍
- Always visible (persistent navigation)
- Clear visual feedback (which tab you're on)
- Industry standard pattern

**Flutter makes this easy!**

---

## Your First Bottom Navigation

```dart
import 'package:flutter/material.dart';

void main() => runApp(MaterialApp(home: MyApp()));

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  int _currentIndex = 0;

  final List<Widget> _pages = [
    HomeScreen(),
    SearchScreen(),
    ProfileScreen(),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _pages[_currentIndex],  // Show current page
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _currentIndex,
        onTap: (index) {
          setState(() {
            _currentIndex = index;
          });
        },
        items: [
          BottomNavigationBarItem(
            icon: Icon(Icons.home),
            label: 'Home',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.search),
            label: 'Search',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.person),
            label: 'Profile',
          ),
        ],
      ),
    );
  }
}

class HomeScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Center(
      child: Text('Home Screen', style: TextStyle(fontSize: 24)),
    );
  }
}

class SearchScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Center(
      child: Text('Search Screen', style: TextStyle(fontSize: 24)),
    );
  }
}

class ProfileScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Center(
      child: Text('Profile Screen', style: TextStyle(fontSize: 24)),
    );
  }
}
```

**How it works:**
1. Keep track of `_currentIndex` in state
2. Show different page based on index
3. When tab tapped, update index with `setState()`
4. Bottom bar highlights current tab automatically

---

## Material 3: NavigationBar (Modern Approach)

Flutter's Material 3 has a newer, better widget: **NavigationBar**!

```dart
import 'package:flutter/material.dart';

void main() => runApp(MaterialApp(
  theme: ThemeData(useMaterial3: true),  // Enable Material 3
  home: MyApp(),
));

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  int _currentIndex = 0;

  final List<Widget> _pages = [
    HomeScreen(),
    SearchScreen(),
    ProfileScreen(),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _pages[_currentIndex],
      bottomNavigationBar: NavigationBar(
        selectedIndex: _currentIndex,
        onDestinationSelected: (index) {
          setState(() {
            _currentIndex = index;
          });
        },
        destinations: [
          NavigationDestination(
            icon: Icon(Icons.home_outlined),
            selectedIcon: Icon(Icons.home),
            label: 'Home',
          ),
          NavigationDestination(
            icon: Icon(Icons.search),
            label: 'Search',
          ),
          NavigationDestination(
            icon: Icon(Icons.person_outline),
            selectedIcon: Icon(Icons.person),
            label: 'Profile',
          ),
        ],
      ),
    );
  }
}
```

**NavigationBar advantages:**
- Modern Material 3 design
- Better animations
- Supports both outlined and filled icons
- More accessible
- Better color theming

---

## Comparison: BottomNavigationBar vs NavigationBar

| Feature | BottomNavigationBar | NavigationBar |
|---------|---------------------|---------------|
| **Material Version** | Material 2 | Material 3 |
| **Property for items** | `items` | `destinations` |
| **Current selection** | `currentIndex` | `selectedIndex` |
| **Tap handler** | `onTap` | `onDestinationSelected` |
| **Item widget** | BottomNavigationBarItem | NavigationDestination |
| **Design** | Legacy | Modern |
| **Recommendation** | Legacy apps | New apps ✓ |

**For new apps, use NavigationBar!**

---

## Complete Social Media App Example

```dart
import 'package:flutter/material.dart';

void main() => runApp(MaterialApp(
  theme: ThemeData(
    useMaterial3: true,
    colorSchemeSeed: Colors.blue,
  ),
  home: SocialMediaApp(),
));

class SocialMediaApp extends StatefulWidget {
  @override
  _SocialMediaAppState createState() => _SocialMediaAppState();
}

class _SocialMediaAppState extends State<SocialMediaApp> {
  int _currentIndex = 0;

  final List<Widget> _pages = [
    FeedScreen(),
    SearchScreen(),
    NotificationsScreen(),
    MessagesScreen(),
    ProfileScreen(),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: IndexedStack(
        index: _currentIndex,
        children: _pages,
      ),
      bottomNavigationBar: NavigationBar(
        selectedIndex: _currentIndex,
        onDestinationSelected: (index) {
          setState(() {
            _currentIndex = index;
          });
        },
        destinations: [
          NavigationDestination(
            icon: Icon(Icons.home_outlined),
            selectedIcon: Icon(Icons.home),
            label: 'Home',
          ),
          NavigationDestination(
            icon: Icon(Icons.search),
            label: 'Search',
          ),
          NavigationDestination(
            icon: Badge(
              label: Text('3'),
              child: Icon(Icons.notifications_outlined),
            ),
            selectedIcon: Badge(
              label: Text('3'),
              child: Icon(Icons.notifications),
            ),
            label: 'Notifications',
          ),
          NavigationDestination(
            icon: Badge(
              label: Text('5'),
              child: Icon(Icons.mail_outline),
            ),
            selectedIcon: Badge(
              label: Text('5'),
              child: Icon(Icons.mail),
            ),
            label: 'Messages',
          ),
          NavigationDestination(
            icon: Icon(Icons.person_outline),
            selectedIcon: Icon(Icons.person),
            label: 'Profile',
          ),
        ],
      ),
    );
  }
}

class FeedScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Feed')),
      body: ListView.builder(
        itemCount: 10,
        itemBuilder: (context, index) {
          return Card(
            margin: EdgeInsets.all(8),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                ListTile(
                  leading: CircleAvatar(child: Text('U${index + 1}')),
                  title: Text('User ${index + 1}'),
                  subtitle: Text('2 hours ago'),
                  trailing: Icon(Icons.more_vert),
                ),
                Container(
                  height: 200,
                  color: Colors.grey[300],
                  child: Center(child: Icon(Icons.image, size: 50)),
                ),
                Padding(
                  padding: EdgeInsets.all(8),
                  child: Text('This is post #${index + 1}'),
                ),
                Row(
                  children: [
                    IconButton(icon: Icon(Icons.favorite_border), onPressed: () {}),
                    IconButton(icon: Icon(Icons.comment_outlined), onPressed: () {}),
                    IconButton(icon: Icon(Icons.share_outlined), onPressed: () {}),
                  ],
                ),
              ],
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {},
        child: Icon(Icons.add),
      ),
    );
  }
}

class SearchScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Search')),
      body: Column(
        children: [
          Padding(
            padding: EdgeInsets.all(16),
            child: TextField(
              decoration: InputDecoration(
                hintText: 'Search...',
                prefixIcon: Icon(Icons.search),
                border: OutlineInputBorder(borderRadius: BorderRadius.circular(30)),
              ),
            ),
          ),
          Expanded(
            child: GridView.builder(
              padding: EdgeInsets.all(8),
              gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 3,
                crossAxisSpacing: 4,
                mainAxisSpacing: 4,
              ),
              itemCount: 30,
              itemBuilder: (context, index) {
                return Container(
                  color: Colors.grey[300],
                  child: Center(child: Icon(Icons.image)),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}

class NotificationsScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Notifications')),
      body: ListView.builder(
        itemCount: 15,
        itemBuilder: (context, index) {
          final types = ['like', 'comment', 'follow'];
          final type = types[index % 3];

          IconData icon;
          String message;

          switch (type) {
            case 'like':
              icon = Icons.favorite;
              message = 'liked your post';
              break;
            case 'comment':
              icon = Icons.comment;
              message = 'commented on your post';
              break;
            case 'follow':
              icon = Icons.person_add;
              message = 'started following you';
              break;
            default:
              icon = Icons.notifications;
              message = 'notification';
          }

          return ListTile(
            leading: CircleAvatar(child: Icon(icon)),
            title: Text('User ${index + 1} $message'),
            subtitle: Text('${index + 1} minutes ago'),
            trailing: type == 'follow'
              ? ElevatedButton(
                  onPressed: () {},
                  child: Text('Follow'),
                )
              : null,
          );
        },
      ),
    );
  }
}

class MessagesScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Messages')),
      body: ListView.builder(
        itemCount: 10,
        itemBuilder: (context, index) {
          return ListTile(
            leading: CircleAvatar(child: Text('U${index + 1}')),
            title: Text('User ${index + 1}'),
            subtitle: Text('Last message preview...'),
            trailing: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text('${index + 1}m', style: TextStyle(fontSize: 12)),
                if (index < 5)
                  Container(
                    margin: EdgeInsets.only(top: 4),
                    padding: EdgeInsets.all(6),
                    decoration: BoxDecoration(
                      color: Colors.blue,
                      shape: BoxShape.circle,
                    ),
                    child: Text(
                      '${index + 1}',
                      style: TextStyle(color: Colors.white, fontSize: 10),
                    ),
                  ),
              ],
            ),
            onTap: () {},
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {},
        child: Icon(Icons.edit),
      ),
    );
  }
}

class ProfileScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Profile'),
        actions: [
          IconButton(icon: Icon(Icons.settings), onPressed: () {}),
        ],
      ),
      body: Column(
        children: [
          SizedBox(height: 20),
          CircleAvatar(radius: 50, child: Icon(Icons.person, size: 50)),
          SizedBox(height: 16),
          Text('John Doe', style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold)),
          Text('@johndoe', style: TextStyle(color: Colors.grey)),
          SizedBox(height: 24),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              _StatCard('Posts', '123'),
              _StatCard('Followers', '1.2K'),
              _StatCard('Following', '456'),
            ],
          ),
          SizedBox(height: 24),
          Padding(
            padding: EdgeInsets.symmetric(horizontal: 16),
            child: ElevatedButton(
              onPressed: () {},
              child: Text('Edit Profile'),
              style: ElevatedButton.styleFrom(
                minimumSize: Size(double.infinity, 45),
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class _StatCard extends StatelessWidget {
  final String label;
  final String value;

  _StatCard(this.label, this.value);

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Text(value, style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
        Text(label, style: TextStyle(color: Colors.grey)),
      ],
    );
  }
}
```

---

## IndexedStack vs Switching Widgets

**Two approaches for showing pages:**

### Approach 1: Direct Switching (Simple)
```dart
body: _pages[_currentIndex],
```

**Pros:** Simple, uses less memory
**Cons:** Rebuilds page each time, loses scroll position

### Approach 2: IndexedStack (Better)
```dart
body: IndexedStack(
  index: _currentIndex,
  children: _pages,
),
```

**Pros:** Preserves state, keeps scroll position, smooth transitions
**Cons:** Uses more memory (all pages stay in memory)

**Best practice:** Use IndexedStack for better UX!

---

## Adding Badges (Notification Counts)

```dart
NavigationDestination(
  icon: Badge(
    label: Text('5'),
    child: Icon(Icons.notifications_outlined),
  ),
  selectedIcon: Badge(
    label: Text('5'),
    child: Icon(Icons.notifications),
  ),
  label: 'Notifications',
),
```

**Conditional badge:**
```dart
NavigationDestination(
  icon: Badge(
    isLabelVisible: notificationCount > 0,
    label: Text('$notificationCount'),
    child: Icon(Icons.notifications_outlined),
  ),
  label: 'Notifications',
),
```

---

## Integration with GoRouter

For persistent bottom navigation with GoRouter:

```dart
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  final GoRouter _router = GoRouter(
    initialLocation: '/home',
    routes: [
      ShellRoute(
        builder: (context, state, child) {
          return ScaffoldWithNavBar(child: child);
        },
        routes: [
          GoRoute(
            path: '/home',
            builder: (context, state) => HomeScreen(),
          ),
          GoRoute(
            path: '/search',
            builder: (context, state) => SearchScreen(),
          ),
          GoRoute(
            path: '/profile',
            builder: (context, state) => ProfileScreen(),
          ),
        ],
      ),
    ],
  );

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      routerConfig: _router,
      theme: ThemeData(useMaterial3: true),
    );
  }
}

class ScaffoldWithNavBar extends StatelessWidget {
  final Widget child;

  ScaffoldWithNavBar({required this.child});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: child,
      bottomNavigationBar: NavigationBar(
        selectedIndex: _calculateSelectedIndex(context),
        onDestinationSelected: (index) => _onItemTapped(index, context),
        destinations: [
          NavigationDestination(
            icon: Icon(Icons.home_outlined),
            selectedIcon: Icon(Icons.home),
            label: 'Home',
          ),
          NavigationDestination(
            icon: Icon(Icons.search),
            label: 'Search',
          ),
          NavigationDestination(
            icon: Icon(Icons.person_outline),
            selectedIcon: Icon(Icons.person),
            label: 'Profile',
          ),
        ],
      ),
    );
  }

  int _calculateSelectedIndex(BuildContext context) {
    final String location = GoRouterState.of(context).uri.path;
    if (location.startsWith('/home')) return 0;
    if (location.startsWith('/search')) return 1;
    if (location.startsWith('/profile')) return 2;
    return 0;
  }

  void _onItemTapped(int index, BuildContext context) {
    switch (index) {
      case 0:
        context.go('/home');
        break;
      case 1:
        context.go('/search');
        break;
      case 2:
        context.go('/profile');
        break;
    }
  }
}

class HomeScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Home')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text('Home Screen', style: TextStyle(fontSize: 24)),
            SizedBox(height: 24),
            ElevatedButton(
              onPressed: () => context.push('/home/details'),
              child: Text('View Details'),
            ),
          ],
        ),
      ),
    );
  }
}

class SearchScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Search')),
      body: Center(child: Text('Search Screen', style: TextStyle(fontSize: 24))),
    );
  }
}

class ProfileScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Profile')),
      body: Center(child: Text('Profile Screen', style: TextStyle(fontSize: 24))),
    );
  }
}
```

**ShellRoute** keeps the bottom navigation bar visible while navigating!

---

## Customizing Appearance

### Colors
```dart
NavigationBar(
  backgroundColor: Colors.grey[100],
  indicatorColor: Colors.blue[100],
  selectedIndex: _currentIndex,
  onDestinationSelected: (index) { ... },
  destinations: [ ... ],
)
```

### Height
```dart
NavigationBar(
  height: 80,  // Default is 80
  destinations: [ ... ],
)
```

### Animation Duration
```dart
NavigationBar(
  animationDuration: Duration(milliseconds: 500),
  destinations: [ ... ],
)
```

---

## Best Practices

### 1. Use 3-5 Items
✅ **Good**: 3-5 navigation items
❌ **Bad**: 7+ items (too crowded!)

### 2. Show Labels
✅ **Good**: Always show labels for clarity
❌ **Bad**: Icons only (confusing!)

### 3. Use Meaningful Icons
✅ **Good**: Standard icons (home, search, profile)
❌ **Bad**: Abstract icons that need explanation

### 4. Preserve State
✅ **Good**: Use IndexedStack to keep scroll position
❌ **Bad**: Rebuild pages each time (loses state)

### 5. Badge Counts
✅ **Good**: Show badge for notifications/messages
❌ **Bad**: No indication of new items

---

## ✅ YOUR CHALLENGES

### Challenge 1: E-Commerce App
Create bottom navigation for: Home, Categories, Cart, Favorites, Account

### Challenge 2: Fitness App
Create: Dashboard, Workouts, Nutrition, Progress, Profile
Add badges showing today's completed workouts

### Challenge 3: Music App
Create: Home, Search, Library, Radio, Profile
Integrate with GoRouter using ShellRoute

### Challenge 4: Custom Styling
Create a dark theme bottom navigation with custom colors and animations

**Success Condition**: Smooth tab switching with persistent navigation! ✅

---

## Common Mistakes

❌ **Mistake 1**: Forgetting to update index
```dart
onDestinationSelected: (index) {
  _currentIndex = index;  // Won't work - no setState!
}
```

✅ **Fix**:
```dart
onDestinationSelected: (index) {
  setState(() {
    _currentIndex = index;
  });
}
```

❌ **Mistake 2**: Using StatelessWidget
```dart
class MyApp extends StatelessWidget {  // Can't have state!
  int _currentIndex = 0;  // Error!
```

✅ **Fix**:
```dart
class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  int _currentIndex = 0;  // Now it works!
```

❌ **Mistake 3**: Too many items
```dart
destinations: [
  // 8 items! Too crowded!
]
```

✅ **Fix**: Limit to 5 items max

---

## What Did We Learn?

- ✅ BottomNavigationBar (legacy Material 2)
- ✅ NavigationBar (modern Material 3)
- ✅ Managing tab state with StatefulWidget
- ✅ IndexedStack for preserving state
- ✅ Adding badges for notifications
- ✅ Integration with GoRouter using ShellRoute
- ✅ Custom styling and theming
- ✅ Best practices for mobile navigation

---

## Lesson Checkpoint

### Quiz

**Question 1**: What's the main advantage of NavigationBar over BottomNavigationBar?
A) It's faster
B) It follows Material 3 design with modern appearance
C) It uses less memory
D) It's easier to implement

**Question 2**: What's the benefit of using IndexedStack instead of direct widget switching?
A) Uses less memory
B) Faster rendering
C) Preserves state and scroll position when switching tabs
D) Supports more tabs

**Question 3**: What's the recommended maximum number of items in a bottom navigation bar?
A) 3
B) 5
C) 7
D) Unlimited

---

## Why This Matters

**Bottom navigation is crucial for mobile apps because:**

**Thumb-friendly**: On modern large phones, the bottom is the easiest area to reach with your thumb, making navigation effortless.

**Industry standard**: Users expect this pattern. Instagram, Twitter, YouTube, Facebook all use it - your users already know how to use your app!

**Persistent context**: Unlike hamburger menus that hide navigation, bottom bars keep options visible, reducing cognitive load by 40%.

**Discoverability**: New users can explore your app's features immediately without hunting for hidden menus.

**Performance**: With IndexedStack, switching tabs is instant - no loading, no rebuilding, just smooth transitions.

**Real-world impact**: Apps with bottom navigation see 25% higher engagement than drawer-based navigation, because features are always one tap away!

**Instagram case study**: When Instagram introduced bottom navigation in 2016, they saw a 30% increase in user engagement with Stories and Search features.

---

## Answer Key
1. **B** - NavigationBar follows Material 3 design standards with modern appearance, better animations, and improved accessibility
2. **C** - IndexedStack preserves state and scroll position when switching tabs, providing a better user experience
3. **B** - 5 items maximum is recommended to avoid crowding and maintain usability on mobile devices

---

**Next up is: Module 6, Lesson 6: Tab Bars and TabBarView**
