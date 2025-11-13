# Module 3, Lesson 7: Mini-Project - Instagram-Style Feed

## Putting It All Together!

You've mastered layouts! Now build a complete Instagram-style feed combining:
- ListView for scrolling posts
- GridView for photo galleries
- Stack for overlays
- Custom widgets for posts
- Responsive design

---

## What We're Building

An Instagram-like feed with:
- Posts with images
- Like/comment/share buttons
- Profile avatars
- Stories section (horizontal scroll)
- Responsive grid for explore page

---

## Step 1: Project Structure

```
lib/
  main.dart
  widgets/
    post_card.dart
    story_circle.dart
    explore_grid.dart
```

---

## Step 2: Story Circle Widget

```dart
// widgets/story_circle.dart
import 'package:flutter/material.dart';

class StoryCircle extends StatelessWidget {
  final String username;
  final String imageUrl;
  final bool viewed;
  
  StoryCircle({
    required this.username,
    required this.imageUrl,
    this.viewed = false,
  });
  
  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsets.symmetric(horizontal: 8),
      child: Column(
        children: [
          Container(
            width: 70,
            height: 70,
            decoration: BoxDecoration(
              shape: BoxShape.circle,
              gradient: viewed 
                ? null 
                : LinearGradient(colors: [Colors.purple, Colors.orange]),
            ),
            padding: EdgeInsets.all(3),
            child: Container(
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                border: Border.all(color: Colors.white, width: 3),
              ),
              child: ClipOval(
                child: Image.network(imageUrl, fit: BoxFit.cover),
              ),
            ),
          ),
          SizedBox(height: 4),
          Text(
            username,
            style: TextStyle(fontSize: 12),
            overflow: TextOverflow.ellipsis,
          ),
        ],
      ),
    );
  }
}
```

---

## Step 3: Post Card Widget

```dart
// widgets/post_card.dart
import 'package:flutter/material.dart';

class PostCard extends StatelessWidget {
  final String username;
  final String userImage;
  final String postImage;
  final String caption;
  final int likes;
  
  PostCard({
    required this.username,
    required this.userImage,
    required this.postImage,
    required this.caption,
    required this.likes,
  });
  
  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Header
        Padding(
          padding: EdgeInsets.all(8),
          child: Row(
            children: [
              CircleAvatar(
                backgroundImage: NetworkImage(userImage),
                radius: 16,
              ),
              SizedBox(width: 8),
              Text(username, style: TextStyle(fontWeight: FontWeight.bold)),
              Spacer(),
              Icon(Icons.more_vert),
            ],
          ),
        ),
        
        // Image
        Image.network(postImage, width: double.infinity, fit: BoxFit.cover),
        
        // Actions
        Row(
          children: [
            IconButton(icon: Icon(Icons.favorite_border), onPressed: () {}),
            IconButton(icon: Icon(Icons.comment_outlined), onPressed: () {}),
            IconButton(icon: Icon(Icons.send_outlined), onPressed: () {}),
            Spacer(),
            IconButton(icon: Icon(Icons.bookmark_border), onPressed: () {}),
          ],
        ),
        
        // Likes
        Padding(
          padding: EdgeInsets.symmetric(horizontal: 16),
          child: Text('$likes likes', style: TextStyle(fontWeight: FontWeight.bold)),
        ),
        
        // Caption
        Padding(
          padding: EdgeInsets.all(16),
          child: RichText(
            text: TextSpan(
              style: TextStyle(color: Colors.black),
              children: [
                TextSpan(text: '$username ', style: TextStyle(fontWeight: FontWeight.bold)),
                TextSpan(text: caption),
              ],
            ),
          ),
        ),
      ],
    );
  }
}
```

---

## Step 4: Main Feed

```dart
// main.dart
import 'package:flutter/material.dart';
import 'widgets/story_circle.dart';
import 'widgets/post_card.dart';

void main() => runApp(InstagramClone());

class InstagramClone extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: FeedScreen(),
    );
  }
}

class FeedScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.white,
        elevation: 0,
        title: Text('Instagram', style: TextStyle(color: Colors.black)),
        actions: [
          IconButton(icon: Icon(Icons.favorite_border, color: Colors.black), onPressed: () {}),
          IconButton(icon: Icon(Icons.send_outlined, color: Colors.black), onPressed: () {}),
        ],
      ),
      body: ListView(
        children: [
          // Stories section
          Container(
            height: 100,
            child: ListView(
              scrollDirection: Axis.horizontal,
              children: [
                StoryCircle(username: 'Your Story', imageUrl: 'https://picsum.photos/100/100?random=1'),
                StoryCircle(username: 'Alice', imageUrl: 'https://picsum.photos/100/100?random=2'),
                StoryCircle(username: 'Bob', imageUrl: 'https://picsum.photos/100/100?random=3', viewed: true),
                StoryCircle(username: 'Carol', imageUrl: 'https://picsum.photos/100/100?random=4'),
                StoryCircle(username: 'Dave', imageUrl: 'https://picsum.photos/100/100?random=5'),
              ],
            ),
          ),
          Divider(),
          
          // Posts
          PostCard(
            username: 'travel_lover',
            userImage: 'https://picsum.photos/100/100?random=10',
            postImage: 'https://picsum.photos/400/400?random=20',
            caption: 'Amazing sunset! 🌅',
            likes: 1234,
          ),
          PostCard(
            username: 'food_enthusiast',
            userImage: 'https://picsum.photos/100/100?random=11',
            postImage: 'https://picsum.photos/400/400?random=21',
            caption: 'Best pizza in town! 🍕',
            likes: 567,
          ),
          PostCard(
            username: 'nature_photos',
            userImage: 'https://picsum.photos/100/100?random=12',
            postImage: 'https://picsum.photos/400/400?random=22',
            caption: 'Mountains calling 🏔️',
            likes: 2341,
          ),
        ],
      ),
      bottomNavigationBar: BottomNavigationBar(
        type: BottomNavigationBarType.fixed,
        items: [
          BottomNavigationBarItem(icon: Icon(Icons.home), label: ''),
          BottomNavigationBarItem(icon: Icon(Icons.search), label: ''),
          BottomNavigationBarItem(icon: Icon(Icons.add_box_outlined), label: ''),
          BottomNavigationBarItem(icon: Icon(Icons.movie_outlined), label: ''),
          BottomNavigationBarItem(icon: Icon(Icons.person_outline), label: ''),
        ],
      ),
    );
  }
}
```

---

## ✅ YOUR CHALLENGES

### Challenge 1: Add Explore Page
Create a grid of photos using GridView.count with 3 columns.

### Challenge 2: Make Posts Interactive
Add setState to toggle like button (filled/outlined).

### Challenge 3: Add Comments
Create a comment section below each post.

### Challenge 4: Add Stories Indicator
Show which stories are viewed vs new.

### Challenge 5: Make it Responsive
Adjust grid columns based on screen width.

**Success Condition**: Working Instagram-style feed! ✅

---

## What Did We Learn?

Module 3 complete! You can now:
- ✅ Create scrollable lists (ListView)
- ✅ Build grids (GridView)
- ✅ Layer widgets (Stack)
- ✅ Make responsive layouts (MediaQuery, LayoutBuilder)
- ✅ Create custom widgets
- ✅ Use advanced scrolling (PageView, Wrap)
- ✅ Build complex UI layouts

---

## What's Next?

**Module 4: User Interaction!**

You can build beautiful layouts, but they don't DO anything yet! Next, you'll learn:
- Handling button presses
- Getting user input from text fields
- Managing state (making your app interactive)
- Building forms

Get ready to make your apps come alive! 🚀
