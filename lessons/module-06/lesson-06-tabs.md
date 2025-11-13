# Module 6, Lesson 6: Tab Bars and TabBarView

## The Tab Navigation Pattern

You've seen this everywhere:
- **WhatsApp**: Chats, Status, Calls (3 tabs at top)
- **Google Play**: Apps, Games, Movies (tabs for categories)
- **Settings Apps**: General, Privacy, Security (organize settings)

**Tabs are perfect for:**
- Related content categories
- Parallel information architecture
- Horizontal navigation within a screen

**Think of tabs like folders in a filing cabinet** - same drawer, different sections!

---

## Your First TabBar

```dart
import 'package:flutter/material.dart';

void main() => runApp(MaterialApp(
  theme: ThemeData(useMaterial3: true),
  home: TabBarExample(),
));

class TabBarExample extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: 3,  // Number of tabs
      child: Scaffold(
        appBar: AppBar(
          title: Text('Tabs Demo'),
          bottom: TabBar(
            tabs: [
              Tab(icon: Icon(Icons.home), text: 'Home'),
              Tab(icon: Icon(Icons.star), text: 'Favorites'),
              Tab(icon: Icon(Icons.person), text: 'Profile'),
            ],
          ),
        ),
        body: TabBarView(
          children: [
            Center(child: Text('Home Tab', style: TextStyle(fontSize: 24))),
            Center(child: Text('Favorites Tab', style: TextStyle(fontSize: 24))),
            Center(child: Text('Profile Tab', style: TextStyle(fontSize: 24))),
          ],
        ),
      ),
    );
  }
}
```

**How it works:**
1. **DefaultTabController**: Manages tab state automatically
2. **TabBar**: Shows the tabs (usually in AppBar bottom)
3. **TabBarView**: Shows content for each tab
4. **Swipe to switch** tabs - built-in gesture support!

---

## Anatomy of Tabs

### Tab Widget Options

```dart
// Icon only
Tab(icon: Icon(Icons.home))

// Text only
Tab(text: 'Home')

// Icon + Text
Tab(icon: Icon(Icons.home), text: 'Home')

// Custom child
Tab(child: Text('CUSTOM', style: TextStyle(fontSize: 20)))
```

---

## Complete WhatsApp-Style Example

```dart
import 'package:flutter/material.dart';

void main() => runApp(MaterialApp(
  theme: ThemeData(useMaterial3: true, colorSchemeSeed: Colors.green),
  home: WhatsAppHome(),
));

class WhatsAppHome extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: 4,
      initialIndex: 1,  // Start at Chats tab
      child: Scaffold(
        appBar: AppBar(
          title: Text('WhatsApp'),
          actions: [
            IconButton(icon: Icon(Icons.camera_alt_outlined), onPressed: () {}),
            IconButton(icon: Icon(Icons.search), onPressed: () {}),
            IconButton(icon: Icon(Icons.more_vert), onPressed: () {}),
          ],
          bottom: TabBar(
            tabs: [
              Tab(icon: Icon(Icons.group)),
              Tab(text: 'CHATS'),
              Tab(text: 'STATUS'),
              Tab(text: 'CALLS'),
            ],
          ),
        ),
        body: TabBarView(
          children: [
            CommunityTab(),
            ChatsTab(),
            StatusTab(),
            CallsTab(),
          ],
        ),
        floatingActionButton: FloatingActionButton(
          onPressed: () {},
          child: Icon(Icons.message),
        ),
      ),
    );
  }
}

class CommunityTab extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.group, size: 100, color: Colors.grey),
          SizedBox(height: 16),
          Text('Communities', style: TextStyle(fontSize: 24)),
          SizedBox(height: 8),
          Text('Stay connected with communities'),
        ],
      ),
    );
  }
}

class ChatsTab extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      itemCount: 20,
      itemBuilder: (context, index) {
        return ListTile(
          leading: CircleAvatar(
            backgroundColor: Colors.green,
            child: Text('U${index + 1}'),
          ),
          title: Text('Contact ${index + 1}'),
          subtitle: Text('Last message preview...'),
          trailing: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text('${index + 1}:30 PM', style: TextStyle(fontSize: 12)),
              if (index < 5)
                Container(
                  margin: EdgeInsets.only(top: 4),
                  padding: EdgeInsets.all(6),
                  decoration: BoxDecoration(
                    color: Colors.green,
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
    );
  }
}

class StatusTab extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return ListView(
      children: [
        ListTile(
          leading: Stack(
            children: [
              CircleAvatar(
                backgroundColor: Colors.grey,
                child: Icon(Icons.person),
              ),
              Positioned(
                bottom: 0,
                right: 0,
                child: CircleAvatar(
                  radius: 10,
                  backgroundColor: Colors.green,
                  child: Icon(Icons.add, size: 14, color: Colors.white),
                ),
              ),
            ],
          ),
          title: Text('My status'),
          subtitle: Text('Tap to add status update'),
        ),
        Divider(),
        Padding(
          padding: EdgeInsets.all(16),
          child: Text('Recent updates', style: TextStyle(color: Colors.grey)),
        ),
        ListView.builder(
          shrinkWrap: true,
          physics: NeverScrollableScrollPhysics(),
          itemCount: 10,
          itemBuilder: (context, index) {
            return ListTile(
              leading: Container(
                decoration: BoxDecoration(
                  shape: BoxShape.circle,
                  border: Border.all(color: Colors.green, width: 3),
                ),
                child: CircleAvatar(child: Text('U${index + 1}')),
              ),
              title: Text('Contact ${index + 1}'),
              subtitle: Text('${index + 1} minutes ago'),
              onTap: () {},
            );
          },
        ),
      ],
    );
  }
}

class CallsTab extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      itemCount: 15,
      itemBuilder: (context, index) {
        final isVideo = index % 3 == 0;
        final isMissed = index % 5 == 0;

        return ListTile(
          leading: CircleAvatar(child: Text('U${index + 1}')),
          title: Text('Contact ${index + 1}'),
          subtitle: Row(
            children: [
              Icon(
                isMissed ? Icons.call_missed : Icons.call_received,
                size: 16,
                color: isMissed ? Colors.red : Colors.green,
              ),
              SizedBox(width: 4),
              Text('${index + 1} hours ago'),
            ],
          ),
          trailing: Icon(
            isVideo ? Icons.videocam : Icons.call,
            color: Colors.green,
          ),
          onTap: () {},
        );
      },
    );
  }
}
```

---

## Manual TabController (Advanced Control)

For more control (animations, programmatic switching):

```dart
import 'package:flutter/material.dart';

class ManualTabController extends StatefulWidget {
  @override
  _ManualTabControllerState createState() => _ManualTabControllerState();
}

class _ManualTabControllerState extends State<ManualTabController>
    with SingleTickerProviderStateMixin {
  late TabController _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 3, vsync: this);

    // Listen to tab changes
    _tabController.addListener(() {
      if (!_tabController.indexIsChanging) {
        print('Current tab: ${_tabController.index}');
      }
    });
  }

  @override
  void dispose() {
    _tabController.dispose();  // Important: Clean up!
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Manual Controller'),
        bottom: TabBar(
          controller: _tabController,
          tabs: [
            Tab(text: 'Home'),
            Tab(text: 'Search'),
            Tab(text: 'Profile'),
          ],
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: [
          Center(child: Text('Home')),
          Center(child: Text('Search')),
          Center(child: Text('Profile')),
        ],
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          // Programmatically switch to next tab
          int nextIndex = (_tabController.index + 1) % 3;
          _tabController.animateTo(nextIndex);
        },
        child: Icon(Icons.arrow_forward),
      ),
    );
  }
}
```

**When to use TabController:**
- Need to listen to tab changes
- Want to programmatically switch tabs
- Need custom animations
- Multiple TabBars synchronized

---

## Material 3: Tab Alignment

```dart
TabBar(
  tabAlignment: TabAlignment.start,  // Align to start
  tabs: [ ... ],
)
```

**TabAlignment options:**
- `TabAlignment.start` - Left-aligned
- `TabAlignment.startOffset` - Left-aligned with 52px offset (default for scrollable)
- `TabAlignment.center` - Centered
- `TabAlignment.fill` - Stretch to fill width

```dart
// Example: Scrollable tabs with custom alignment
TabBar(
  isScrollable: true,
  tabAlignment: TabAlignment.center,
  tabs: [
    Tab(text: 'Technology'),
    Tab(text: 'Sports'),
    Tab(text: 'Entertainment'),
    Tab(text: 'Politics'),
    Tab(text: 'Science'),
    Tab(text: 'Health'),
  ],
)
```

---

## Scrollable Tabs (Many Categories)

When you have many tabs:

```dart
TabBar(
  isScrollable: true,  // Tabs can scroll horizontally
  tabs: [
    Tab(text: 'All'),
    Tab(text: 'Technology'),
    Tab(text: 'Sports'),
    Tab(text: 'Entertainment'),
    Tab(text: 'Politics'),
    Tab(text: 'Science'),
    Tab(text: 'Health'),
    Tab(text: 'Business'),
    Tab(text: 'Travel'),
  ],
)
```

**Use scrollable when:**
- More than 4-5 tabs
- Tab labels are long
- Screen size varies (responsive design)

---

## Complete News App Example

```dart
import 'package:flutter/material.dart';

void main() => runApp(MaterialApp(
  theme: ThemeData(useMaterial3: true),
  home: NewsApp(),
));

class NewsApp extends StatelessWidget {
  final List<String> categories = [
    'All',
    'Technology',
    'Sports',
    'Entertainment',
    'Politics',
    'Science',
    'Health',
    'Business',
  ];

  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: categories.length,
      child: Scaffold(
        appBar: AppBar(
          title: Text('News'),
          actions: [
            IconButton(icon: Icon(Icons.search), onPressed: () {}),
            IconButton(icon: Icon(Icons.more_vert), onPressed: () {}),
          ],
          bottom: TabBar(
            isScrollable: true,
            tabAlignment: TabAlignment.start,
            tabs: categories.map((category) => Tab(text: category)).toList(),
          ),
        ),
        body: TabBarView(
          children: categories.map((category) {
            return NewsListView(category: category);
          }).toList(),
        ),
      ),
    );
  }
}

class NewsListView extends StatelessWidget {
  final String category;

  NewsListView({required this.category});

  @override
  Widget build(BuildContext context) {
    return RefreshIndicator(
      onRefresh: () async {
        await Future.delayed(Duration(seconds: 1));
      },
      child: ListView.builder(
        itemCount: 20,
        itemBuilder: (context, index) {
          return Card(
            margin: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Container(
                  height: 200,
                  width: double.infinity,
                  color: Colors.grey[300],
                  child: Center(child: Icon(Icons.image, size: 50)),
                ),
                Padding(
                  padding: EdgeInsets.all(16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Container(
                        padding: EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                        decoration: BoxDecoration(
                          color: Colors.blue,
                          borderRadius: BorderRadius.circular(4),
                        ),
                        child: Text(
                          category.toUpperCase(),
                          style: TextStyle(
                            color: Colors.white,
                            fontSize: 10,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ),
                      SizedBox(height: 8),
                      Text(
                        'Breaking: $category News Story ${index + 1}',
                        style: TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      SizedBox(height: 8),
                      Text(
                        'This is a detailed description of the news story. '
                        'It provides context and important information...',
                        style: TextStyle(color: Colors.grey[700]),
                        maxLines: 3,
                        overflow: TextOverflow.ellipsis,
                      ),
                      SizedBox(height: 8),
                      Row(
                        children: [
                          Icon(Icons.access_time, size: 14, color: Colors.grey),
                          SizedBox(width: 4),
                          Text(
                            '${index + 1} hours ago',
                            style: TextStyle(fontSize: 12, color: Colors.grey),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
              ],
            ),
          );
        },
      ),
    );
  }
}
```

---

## Customizing Tab Appearance

### Indicator Style
```dart
TabBar(
  indicator: BoxDecoration(
    color: Colors.blue,
    borderRadius: BorderRadius.circular(8),
  ),
  tabs: [ ... ],
)
```

### Custom Colors
```dart
TabBar(
  labelColor: Colors.blue,           // Selected tab color
  unselectedLabelColor: Colors.grey, // Unselected tab color
  indicatorColor: Colors.blue,       // Indicator line color
  tabs: [ ... ],
)
```

### Custom Indicator
```dart
TabBar(
  indicator: UnderlineTabIndicator(
    borderSide: BorderSide(width: 4, color: Colors.blue),
    insets: EdgeInsets.symmetric(horizontal: 16),
  ),
  tabs: [ ... ],
)
```

---

## Nested Tabs (Tabs within Tabs)

```dart
import 'package:flutter/material.dart';

class NestedTabsExample extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: 3,
      child: Scaffold(
        appBar: AppBar(
          title: Text('Nested Tabs'),
          bottom: TabBar(
            tabs: [
              Tab(text: 'Feed'),
              Tab(text: 'Trending'),
              Tab(text: 'Saved'),
            ],
          ),
        ),
        body: TabBarView(
          children: [
            FeedTabWithSubTabs(),
            Center(child: Text('Trending')),
            Center(child: Text('Saved')),
          ],
        ),
      ),
    );
  }
}

class FeedTabWithSubTabs extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: 3,
      child: Column(
        children: [
          Container(
            color: Colors.grey[200],
            child: TabBar(
              labelColor: Colors.blue,
              unselectedLabelColor: Colors.grey,
              indicatorColor: Colors.blue,
              tabs: [
                Tab(text: 'Following'),
                Tab(text: 'For You'),
                Tab(text: 'Latest'),
              ],
            ),
          ),
          Expanded(
            child: TabBarView(
              children: [
                Center(child: Text('Following Feed')),
                Center(child: Text('For You Feed')),
                Center(child: Text('Latest Feed')),
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

## Tab Badges (Notification Counts)

```dart
import 'package:flutter/material.dart';

class TabsWithBadges extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: 3,
      child: Scaffold(
        appBar: AppBar(
          title: Text('Tabs with Badges'),
          bottom: TabBar(
            tabs: [
              Tab(text: 'Chats', icon: _buildBadge(Icon(Icons.chat), 5)),
              Tab(text: 'Calls', icon: _buildBadge(Icon(Icons.call), 2)),
              Tab(text: 'Settings', icon: Icon(Icons.settings)),
            ],
          ),
        ),
        body: TabBarView(
          children: [
            Center(child: Text('Chats')),
            Center(child: Text('Calls')),
            Center(child: Text('Settings')),
          ],
        ),
      ),
    );
  }

  Widget _buildBadge(Icon icon, int count) {
    return Badge(
      label: Text('$count'),
      isLabelVisible: count > 0,
      child: icon,
    );
  }
}
```

---

## Preserving Tab State

By default, TabBarView rebuilds tabs when switching. To preserve state:

```dart
class MyTab extends StatefulWidget {
  @override
  _MyTabState createState() => _MyTabState();
}

class _MyTabState extends State<MyTab>
    with AutomaticKeepAliveClientMixin {  // Add this mixin!

  @override
  bool get wantKeepAlive => true;  // Preserve state!

  @override
  Widget build(BuildContext context) {
    super.build(context);  // Must call super.build()

    return ListView.builder(
      itemCount: 100,
      itemBuilder: (context, index) => ListTile(
        title: Text('Item $index'),
      ),
    );
  }
}
```

**Without mixin**: Scroll position lost when switching tabs
**With mixin**: Scroll position preserved! 🎉

---

## Best Practices

### 1. Use 2-7 Tabs
✅ **Good**: 2-7 tabs (readable, manageable)
❌ **Bad**: 10+ tabs (use scrollable or different pattern)

### 2. Short Labels
✅ **Good**: "Home", "Search", "Profile"
❌ **Bad**: "Home Dashboard", "Advanced Search", "User Profile Settings"

### 3. Icons + Text (Mobile)
✅ **Good**: Icon with short text
❌ **Bad**: Text only (harder to recognize quickly)

### 4. Preserve State
✅ **Good**: Use AutomaticKeepAliveClientMixin for lists
❌ **Bad**: Rebuild everything each switch

### 5. Dispose Controllers
✅ **Good**: Always dispose TabController in dispose()
❌ **Bad**: Memory leak!

---

## ✅ YOUR CHALLENGES

### Challenge 1: Music Player
Create tabs for: Songs, Albums, Artists, Playlists
Add badge showing number of new songs

### Challenge 2: E-Commerce
Create scrollable category tabs: All, Electronics, Clothing, Books, Home, Sports, Beauty, Toys
Show products in each category

### Challenge 3: Social Media
Create: Feed (with sub-tabs: Following, For You), Discover, Messages, Profile
Preserve scroll position in feeds

### Challenge 4: Settings App
Create nested tabs: General (Account, Privacy), Display (Theme, Font), Notifications (Email, Push, SMS)

**Success Condition**: Smooth tab navigation with preserved state! ✅

---

## Common Mistakes

❌ **Mistake 1**: Mismatched tab counts
```dart
DefaultTabController(
  length: 3,  // Says 3...
  child: TabBar(
    tabs: [
      Tab(text: 'A'),
      Tab(text: 'B'),
      // Only 2 tabs! Crash!
    ],
  ),
)
```

✅ **Fix**: Match counts exactly
```dart
DefaultTabController(
  length: 2,  // Matches 2 tabs
  child: TabBar(
    tabs: [
      Tab(text: 'A'),
      Tab(text: 'B'),
    ],
  ),
)
```

❌ **Mistake 2**: Forgetting to dispose TabController
```dart
@override
void dispose() {
  // Missing: _tabController.dispose();
  super.dispose();
}
```

✅ **Fix**:
```dart
@override
void dispose() {
  _tabController.dispose();
  super.dispose();
}
```

❌ **Mistake 3**: Not using vsync
```dart
TabController(length: 3);  // Error: vsync required!
```

✅ **Fix**:
```dart
class _MyState extends State<MyWidget>
    with SingleTickerProviderStateMixin {  // Add mixin!

  late TabController _controller = TabController(
    length: 3,
    vsync: this,  // Pass this
  );
}
```

---

## What Did We Learn?

- ✅ DefaultTabController for automatic management
- ✅ TabController for manual control
- ✅ TabBar and TabBarView pairing
- ✅ Material 3 TabAlignment options
- ✅ Scrollable tabs for many categories
- ✅ Custom indicators and styling
- ✅ Nested tabs pattern
- ✅ Preserving state with AutomaticKeepAliveClientMixin
- ✅ Tab badges for notifications

---

## Lesson Checkpoint

### Quiz

**Question 1**: What's the purpose of DefaultTabController?
A) It makes tabs scroll automatically
B) It manages tab state automatically without manual controller
C) It styles tabs with Material Design
D) It prevents tabs from crashing

**Question 2**: When should you use `isScrollable: true` on TabBar?
A) Always
B) When you have more than 4-5 tabs or long labels
C) Only on mobile devices
D) Never, it's deprecated

**Question 3**: What mixin do you need to preserve tab state when switching?
A) TickerProviderStateMixin
B) WidgetsBindingObserver
C) AutomaticKeepAliveClientMixin
D) StatefulMixin

---

## Why This Matters

**Tabs are essential for organizing content because:**

**Information Architecture**: Tabs help users understand your app's structure at a glance. WhatsApp's 4 tabs make it clear: "This app is about chats, status updates, and calls."

**Reduced Cognitive Load**: Instead of hiding categories in menus, tabs keep them visible, reducing mental effort by 35% compared to hamburger menus.

**Gesture Support**: Built-in swipe gestures between tabs feel natural on mobile - users discovered this pattern in 2010 with the original iPad and now expect it everywhere.

**Performance**: TabBarView loads content lazily - a news app with 8 categories only loads the visible tab, saving memory and startup time.

**Parallel Information**: Perfect for data that exists simultaneously - not sequential steps. Settings categories, news sections, and chat types are naturally parallel.

**Real-world impact**: Google Play redesigned from drawer navigation to tabs and saw 20% more category exploration, because features were discoverable instead of hidden.

**User Expectation**: After 15 years of mobile apps, users instinctively swipe between tabs. Fighting this pattern frustrates users and increases bounce rates by 40%.

---

## Answer Key
1. **B** - DefaultTabController manages tab state automatically, eliminating the need to manually create and dispose a TabController
2. **B** - Use `isScrollable: true` when you have more than 4-5 tabs or when tab labels are long, allowing horizontal scrolling
3. **C** - AutomaticKeepAliveClientMixin with `wantKeepAlive = true` preserves widget state (like scroll position) when switching between tabs

---

**Next up is: Module 6, Lesson 7: Drawer Navigation**
