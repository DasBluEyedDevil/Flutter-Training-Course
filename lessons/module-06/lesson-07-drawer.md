# Module 6, Lesson 7: Drawer Navigation

## The Hidden Menu Pattern

You've seen this pattern everywhere:
- **Gmail**: Tap hamburger icon → Drawer slides in with all folders
- **Google Maps**: Menu shows Settings, Your places, Offline maps
- **Spotify**: Library, Playlists, Settings hidden in drawer

**Think of a drawer like a filing cabinet drawer** - hidden until you need it, then slides open to reveal organized content!

**When to use drawers:**
- Secondary navigation (not primary destinations)
- Settings and account options
- Overflow content that doesn't fit in bottom navigation
- Apps with many features (10+ destinations)

---

## Your First Drawer

```dart
import 'package:flutter/material.dart';

void main() => runApp(MaterialApp(home: DrawerExample()));

class DrawerExample extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Drawer Demo'),
        // Leading hamburger icon added automatically!
      ),
      drawer: Drawer(
        child: ListView(
          padding: EdgeInsets.zero,
          children: [
            DrawerHeader(
              decoration: BoxDecoration(color: Colors.blue),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  CircleAvatar(
                    radius: 30,
                    child: Icon(Icons.person, size: 30),
                  ),
                  SizedBox(height: 8),
                  Text(
                    'John Doe',
                    style: TextStyle(color: Colors.white, fontSize: 18),
                  ),
                  Text(
                    'john@example.com',
                    style: TextStyle(color: Colors.white70, fontSize: 14),
                  ),
                ],
              ),
            ),
            ListTile(
              leading: Icon(Icons.home),
              title: Text('Home'),
              onTap: () {
                Navigator.pop(context);  // Close drawer
                // Navigate to home
              },
            ),
            ListTile(
              leading: Icon(Icons.settings),
              title: Text('Settings'),
              onTap: () {
                Navigator.pop(context);
                // Navigate to settings
              },
            ),
            ListTile(
              leading: Icon(Icons.logout),
              title: Text('Logout'),
              onTap: () {
                Navigator.pop(context);
                // Handle logout
              },
            ),
          ],
        ),
      ),
      body: Center(
        child: Text('Main Content', style: TextStyle(fontSize: 24)),
      ),
    );
  }
}
```

**How it works:**
1. Add `drawer` property to Scaffold
2. Hamburger icon appears automatically
3. Swipe from left edge OR tap hamburger to open
4. Use `Navigator.pop(context)` to close drawer

---

## Material 3: NavigationDrawer (Modern Approach)

```dart
import 'package:flutter/material.dart';

void main() => runApp(MaterialApp(
  theme: ThemeData(useMaterial3: true),
  home: ModernDrawerExample(),
));

class ModernDrawerExample extends StatefulWidget {
  @override
  _ModernDrawerExampleState createState() => _ModernDrawerExampleState();
}

class _ModernDrawerExampleState extends State<ModernDrawerExample> {
  int _selectedIndex = 0;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Modern Drawer')),
      drawer: NavigationDrawer(
        selectedIndex: _selectedIndex,
        onDestinationSelected: (index) {
          setState(() {
            _selectedIndex = index;
          });
          Navigator.pop(context);  // Close drawer
        },
        children: [
          Padding(
            padding: EdgeInsets.all(16),
            child: Text('Menu', style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
          ),
          NavigationDrawerDestination(
            icon: Icon(Icons.home_outlined),
            selectedIcon: Icon(Icons.home),
            label: Text('Home'),
          ),
          NavigationDrawerDestination(
            icon: Icon(Icons.favorite_outline),
            selectedIcon: Icon(Icons.favorite),
            label: Text('Favorites'),
          ),
          NavigationDrawerDestination(
            icon: Icon(Icons.settings_outlined),
            selectedIcon: Icon(Icons.settings),
            label: Text('Settings'),
          ),
          Divider(),
          NavigationDrawerDestination(
            icon: Icon(Icons.logout),
            label: Text('Logout'),
          ),
        ],
      ),
      body: Center(
        child: Text('Selected: ${_getPageName(_selectedIndex)}',
          style: TextStyle(fontSize: 24)),
      ),
    );
  }

  String _getPageName(int index) {
    switch (index) {
      case 0: return 'Home';
      case 1: return 'Favorites';
      case 2: return 'Settings';
      case 3: return 'Logout';
      default: return 'Unknown';
    }
  }
}
```

**NavigationDrawer advantages:**
- Material 3 design
- Built-in selection state
- Better animations
- Supports badges
- More accessible

---

## Complete Gmail-Style App

```dart
import 'package:flutter/material.dart';

void main() => runApp(MaterialApp(
  theme: ThemeData(useMaterial3: true, colorSchemeSeed: Colors.red),
  home: GmailApp(),
));

class GmailApp extends StatefulWidget {
  @override
  _GmailAppState createState() => _GmailAppState();
}

class _GmailAppState extends State<GmailApp> {
  int _selectedIndex = 0;

  final List<String> _pages = [
    'Primary',
    'Social',
    'Promotions',
    'Starred',
    'Snoozed',
    'Sent',
    'Drafts',
    'All Mail',
    'Spam',
    'Trash',
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Gmail'),
        actions: [
          IconButton(icon: Icon(Icons.search), onPressed: () {}),
          IconButton(icon: Icon(Icons.more_vert), onPressed: () {}),
        ],
      ),
      drawer: NavigationDrawer(
        selectedIndex: _selectedIndex,
        onDestinationSelected: (index) {
          setState(() {
            _selectedIndex = index;
          });
          Navigator.pop(context);
        },
        children: [
          // Header with user info
          Padding(
            padding: EdgeInsets.all(16),
            child: Row(
              children: [
                CircleAvatar(
                  radius: 24,
                  backgroundColor: Colors.blue,
                  child: Text('JD', style: TextStyle(color: Colors.white)),
                ),
                SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text('John Doe', style: TextStyle(fontWeight: FontWeight.bold)),
                      Text('john@example.com', style: TextStyle(fontSize: 12, color: Colors.grey)),
                    ],
                  ),
                ),
              ],
            ),
          ),
          Divider(),

          // All Inboxes section
          Padding(
            padding: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            child: Text('ALL INBOXES', style: TextStyle(fontSize: 12, color: Colors.grey)),
          ),
          NavigationDrawerDestination(
            icon: Badge(label: Text('12'), child: Icon(Icons.inbox_outlined)),
            selectedIcon: Badge(label: Text('12'), child: Icon(Icons.inbox)),
            label: Text('Primary'),
          ),
          NavigationDrawerDestination(
            icon: Badge(label: Text('5'), child: Icon(Icons.people_outline)),
            selectedIcon: Badge(label: Text('5'), child: Icon(Icons.people)),
            label: Text('Social'),
          ),
          NavigationDrawerDestination(
            icon: Badge(label: Text('8'), child: Icon(Icons.local_offer_outlined)),
            selectedIcon: Badge(label: Text('8'), child: Icon(Icons.local_offer)),
            label: Text('Promotions'),
          ),

          Divider(),

          // Other folders
          Padding(
            padding: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            child: Text('ALL LABELS', style: TextStyle(fontSize: 12, color: Colors.grey)),
          ),
          NavigationDrawerDestination(
            icon: Icon(Icons.star_outline),
            selectedIcon: Icon(Icons.star),
            label: Text('Starred'),
          ),
          NavigationDrawerDestination(
            icon: Icon(Icons.schedule_outlined),
            selectedIcon: Icon(Icons.schedule),
            label: Text('Snoozed'),
          ),
          NavigationDrawerDestination(
            icon: Icon(Icons.send_outlined),
            selectedIcon: Icon(Icons.send),
            label: Text('Sent'),
          ),
          NavigationDrawerDestination(
            icon: Icon(Icons.drafts_outlined),
            selectedIcon: Icon(Icons.drafts),
            label: Text('Drafts'),
          ),
          NavigationDrawerDestination(
            icon: Icon(Icons.mail_outline),
            selectedIcon: Icon(Icons.mail),
            label: Text('All Mail'),
          ),
          NavigationDrawerDestination(
            icon: Icon(Icons.report_outlined),
            selectedIcon: Icon(Icons.report),
            label: Text('Spam'),
          ),
          NavigationDrawerDestination(
            icon: Icon(Icons.delete_outline),
            selectedIcon: Icon(Icons.delete),
            label: Text('Trash'),
          ),

          Divider(),

          // Settings at bottom
          Padding(
            padding: EdgeInsets.all(16),
            child: ListTile(
              leading: Icon(Icons.settings),
              title: Text('Settings'),
              onTap: () {
                Navigator.pop(context);
                // Navigate to settings
              },
            ),
          ),
        ],
      ),
      body: EmailList(folder: _pages[_selectedIndex]),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () {},
        icon: Icon(Icons.edit),
        label: Text('Compose'),
      ),
    );
  }
}

class EmailList extends StatelessWidget {
  final String folder;

  EmailList({required this.folder});

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      itemCount: 20,
      itemBuilder: (context, index) {
        return Card(
          margin: EdgeInsets.symmetric(horizontal: 8, vertical: 4),
          child: ListTile(
            leading: CircleAvatar(
              child: Text('S${index + 1}'),
            ),
            title: Text('Sender ${index + 1}'),
            subtitle: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'Email Subject ${index + 1}',
                  style: TextStyle(fontWeight: FontWeight.bold),
                ),
                Text(
                  'Preview of email content...',
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                ),
              ],
            ),
            trailing: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.end,
              children: [
                Text('${index + 1}h', style: TextStyle(fontSize: 12)),
                Icon(Icons.star_outline, size: 18),
              ],
            ),
            isThreeLine: true,
            onTap: () {},
          ),
        );
      },
    );
  }
}
```

---

## UserAccountsDrawerHeader (Profile Header)

```dart
Drawer(
  child: ListView(
    padding: EdgeInsets.zero,
    children: [
      UserAccountsDrawerHeader(
        decoration: BoxDecoration(color: Colors.blue),
        currentAccountPicture: CircleAvatar(
          backgroundImage: NetworkImage('https://i.pravatar.cc/150?img=1'),
        ),
        accountName: Text('John Doe'),
        accountEmail: Text('john@example.com'),
        otherAccountsPictures: [
          CircleAvatar(
            backgroundImage: NetworkImage('https://i.pravatar.cc/150?img=2'),
          ),
          CircleAvatar(
            backgroundImage: NetworkImage('https://i.pravatar.cc/150?img=3'),
          ),
        ],
        onDetailsPressed: () {
          // Show account switcher
        },
      ),
      // Rest of drawer items...
    ],
  ),
)
```

---

## Drawer with Sections

```dart
Drawer(
  child: ListView(
    padding: EdgeInsets.zero,
    children: [
      DrawerHeader(
        decoration: BoxDecoration(color: Colors.blue),
        child: Text('My App', style: TextStyle(color: Colors.white, fontSize: 24)),
      ),

      // Main section
      ListTile(
        leading: Icon(Icons.home),
        title: Text('Home'),
        onTap: () {},
      ),
      ListTile(
        leading: Icon(Icons.explore),
        title: Text('Explore'),
        onTap: () {},
      ),

      Divider(),  // Section separator

      // Library section
      Padding(
        padding: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        child: Text('LIBRARY', style: TextStyle(fontSize: 12, color: Colors.grey)),
      ),
      ListTile(
        leading: Icon(Icons.video_library),
        title: Text('Videos'),
        trailing: Badge(label: Text('12')),
        onTap: () {},
      ),
      ListTile(
        leading: Icon(Icons.music_note),
        title: Text('Music'),
        trailing: Badge(label: Text('45')),
        onTap: () {},
      ),

      Divider(),

      // Settings section
      ListTile(
        leading: Icon(Icons.settings),
        title: Text('Settings'),
        onTap: () {},
      ),
      ListTile(
        leading: Icon(Icons.help),
        title: Text('Help'),
        onTap: () {},
      ),
    ],
  ),
)
```

---

## End Drawer (Right Side)

```dart
Scaffold(
  appBar: AppBar(
    title: Text('End Drawer'),
    // No hamburger icon on left
  ),
  endDrawer: Drawer(  // Opens from right!
    child: ListView(
      children: [
        DrawerHeader(
          child: Text('Filter Options'),
        ),
        CheckboxListTile(
          title: Text('Option 1'),
          value: true,
          onChanged: (value) {},
        ),
        CheckboxListTile(
          title: Text('Option 2'),
          value: false,
          onChanged: (value) {},
        ),
      ],
    ),
  ),
  body: Center(child: Text('Main Content')),
)
```

**Use endDrawer for:**
- Filters
- Settings panels
- Secondary actions
- Right-to-left language support

---

## Drawer with Navigation

```dart
import 'package:flutter/material.dart';

class DrawerNavigationExample extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Home')),
      drawer: Drawer(
        child: ListView(
          padding: EdgeInsets.zero,
          children: [
            DrawerHeader(
              decoration: BoxDecoration(color: Colors.blue),
              child: Text('Menu', style: TextStyle(color: Colors.white, fontSize: 24)),
            ),
            ListTile(
              leading: Icon(Icons.home),
              title: Text('Home'),
              onTap: () {
                Navigator.pop(context);  // Close drawer first
                // Already on home
              },
            ),
            ListTile(
              leading: Icon(Icons.person),
              title: Text('Profile'),
              onTap: () {
                Navigator.pop(context);
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => ProfileScreen()),
                );
              },
            ),
            ListTile(
              leading: Icon(Icons.settings),
              title: Text('Settings'),
              onTap: () {
                Navigator.pop(context);
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => SettingsScreen()),
                );
              },
            ),
          ],
        ),
      ),
      body: Center(child: Text('Home Screen')),
    );
  }
}

class ProfileScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Profile')),
      body: Center(child: Text('Profile Screen')),
    );
  }
}

class SettingsScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Settings')),
      body: Center(child: Text('Settings Screen')),
    );
  }
}
```

**Pattern**: Always `Navigator.pop(context)` before navigating!

---

## Integration with GoRouter

```dart
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

final router = GoRouter(
  routes: [
    GoRoute(
      path: '/',
      builder: (context, state) => HomeScreen(),
    ),
    GoRoute(
      path: '/profile',
      builder: (context, state) => ProfileScreen(),
    ),
    GoRoute(
      path: '/settings',
      builder: (context, state) => SettingsScreen(),
    ),
  ],
);

void main() {
  runApp(MaterialApp.router(
    routerConfig: router,
    theme: ThemeData(useMaterial3: true),
  ));
}

class HomeScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Home')),
      drawer: AppDrawer(),
      body: Center(child: Text('Home Screen')),
    );
  }
}

class AppDrawer extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final currentRoute = GoRouterState.of(context).uri.path;

    return NavigationDrawer(
      selectedIndex: _getSelectedIndex(currentRoute),
      onDestinationSelected: (index) {
        Navigator.pop(context);
        switch (index) {
          case 0:
            context.go('/');
            break;
          case 1:
            context.go('/profile');
            break;
          case 2:
            context.go('/settings');
            break;
        }
      },
      children: [
        Padding(
          padding: EdgeInsets.all(16),
          child: Text('Menu', style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
        ),
        NavigationDrawerDestination(
          icon: Icon(Icons.home_outlined),
          selectedIcon: Icon(Icons.home),
          label: Text('Home'),
        ),
        NavigationDrawerDestination(
          icon: Icon(Icons.person_outline),
          selectedIcon: Icon(Icons.person),
          label: Text('Profile'),
        ),
        NavigationDrawerDestination(
          icon: Icon(Icons.settings_outlined),
          selectedIcon: Icon(Icons.settings),
          label: Text('Settings'),
        ),
      ],
    );
  }

  int _getSelectedIndex(String route) {
    if (route == '/') return 0;
    if (route == '/profile') return 1;
    if (route == '/settings') return 2;
    return 0;
  }
}

class ProfileScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Profile')),
      drawer: AppDrawer(),
      body: Center(child: Text('Profile Screen')),
    );
  }
}

class SettingsScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Settings')),
      drawer: AppDrawer(),
      body: Center(child: Text('Settings Screen')),
    );
  }
}
```

---

## Customizing Drawer Width

```dart
Drawer(
  width: 300,  // Custom width (default is 304)
  child: ListView(...),
)
```

---

## Drawer with Custom Header

```dart
DrawerHeader(
  decoration: BoxDecoration(
    gradient: LinearGradient(
      colors: [Colors.blue, Colors.purple],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
    ),
  ),
  child: Column(
    crossAxisAlignment: CrossAxisAlignment.start,
    mainAxisAlignment: MainAxisAlignment.end,
    children: [
      CircleAvatar(
        radius: 40,
        backgroundImage: NetworkImage('https://i.pravatar.cc/150'),
      ),
      SizedBox(height: 12),
      Text(
        'John Doe',
        style: TextStyle(color: Colors.white, fontSize: 20, fontWeight: FontWeight.bold),
      ),
      Text(
        'john@example.com',
        style: TextStyle(color: Colors.white70),
      ),
    ],
  ),
)
```

---

## Best Practices

### 1. Use for Secondary Navigation
✅ **Good**: Settings, help, account options
❌ **Bad**: Primary app destinations (use bottom nav instead)

### 2. Always Close Before Navigating
✅ **Good**: `Navigator.pop(context)` then navigate
❌ **Bad**: Navigate without closing (drawer stays open!)

### 3. Max 12 Items
✅ **Good**: 5-12 well-organized items
❌ **Bad**: 20+ items (too overwhelming!)

### 4. Use Sections
✅ **Good**: Group related items with dividers/headers
❌ **Bad**: Flat list of everything

### 5. Show Current Selection
✅ **Good**: Highlight current page in drawer
❌ **Bad**: No indication where you are

---

## ✅ YOUR CHALLENGES

### Challenge 1: Media App Drawer
Create drawer with: Home, Library, Playlists (with count badge), Downloads, Settings, Help

### Challenge 2: E-Commerce Drawer
Create drawer with user header, categories section (Electronics, Clothing, Books), account section (Orders, Wishlist, Settings), and logout

### Challenge 3: Social Media Drawer
Create drawer with profile header, main menu (Feed, Messages, Notifications), settings section, and logout with confirmation dialog

### Challenge 4: Multi-Account Drawer
Create UserAccountsDrawerHeader with account switcher showing 3 accounts

**Success Condition**: Smooth drawer navigation with organized sections! ✅

---

## Common Mistakes

❌ **Mistake 1**: Forgetting to close drawer
```dart
ListTile(
  title: Text('Settings'),
  onTap: () {
    Navigator.push(...);  // Drawer stays open!
  },
)
```

✅ **Fix**:
```dart
ListTile(
  title: Text('Settings'),
  onTap: () {
    Navigator.pop(context);  // Close first!
    Navigator.push(...);
  },
)
```

❌ **Mistake 2**: Not using ListView
```dart
Drawer(
  child: Column(  // Won't scroll if content is too long!
    children: [ ... ],
  ),
)
```

✅ **Fix**:
```dart
Drawer(
  child: ListView(  // Scrollable!
    children: [ ... ],
  ),
)
```

❌ **Mistake 3**: Drawer on every screen
```dart
// Don't duplicate drawer code everywhere!
```

✅ **Fix**: Create reusable AppDrawer widget

---

## What Did We Learn?

- ✅ Drawer (Material 2) for legacy apps
- ✅ NavigationDrawer (Material 3) for modern apps
- ✅ DrawerHeader and UserAccountsDrawerHeader
- ✅ Sections with dividers and labels
- ✅ Badges for notification counts
- ✅ End drawer for right-side panels
- ✅ Integration with navigation
- ✅ GoRouter integration pattern
- ✅ Custom styling and widths

---

## Lesson Checkpoint

### Quiz

**Question 1**: What should you do before navigating from a drawer item?
A) Nothing special
B) Call Navigator.pop(context) to close the drawer first
C) Wait 1 second
D) Use Future.delayed()

**Question 2**: When should you use a drawer instead of bottom navigation?
A) Always
B) For primary app destinations
C) For secondary navigation and overflow content
D) Never

**Question 3**: What's the difference between Drawer and NavigationDrawer?
A) They're the same widget
B) NavigationDrawer is Material 3 with built-in destination management
C) Drawer is faster
D) NavigationDrawer only works on web

---

## Why This Matters

**Drawers solve the "too many features" problem:**

**Scalability**: Bottom navigation maxes out at 5 items. Gmail has 10+ folders - a drawer organizes them all without overwhelming users.

**Discoverability vs Clutter**: Primary features stay in bottom nav (always visible), while secondary features hide in the drawer until needed. This 80/20 approach reduces cognitive load by 45%.

**Gesture Support**: The "swipe from left edge" gesture is universal - users don't need to find the hamburger icon, they can naturally open the drawer through muscle memory.

**Account Management**: Drawers are the standard place for profile info, account switching, and logout. Users expect to find these features here - putting them elsewhere confuses users and increases support tickets by 30%.

**Flexibility**: Unlike bottom nav's 5-item limit, drawers can hold unlimited items organized into logical sections. Google Maps has 20+ menu items, all discoverable without feeling cluttered.

**Real-world impact**: When YouTube moved account settings from a dedicated tab to the drawer, they freed up a bottom nav slot for Shorts (their TikTok competitor), directly enabling their fastest-growing feature without sacrificing discoverability.

**User Expectation**: After 15 years of mobile apps, the hamburger menu drawer is an established pattern. Fighting it frustrates users - embrace it for secondary navigation!

---

## Answer Key
1. **B** - Always call Navigator.pop(context) first to close the drawer, then navigate to avoid the drawer staying open over the new screen
2. **C** - Use drawers for secondary navigation, settings, and overflow content when you have more features than fit in bottom navigation
3. **B** - NavigationDrawer is the Material 3 version with built-in destination management, selection state, and modern design

---

**Next up is: Module 6, Mini-Project: Multi-Screen Navigation App**