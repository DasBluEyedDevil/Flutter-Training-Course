# Lesson 3: Local Storage with Hive

## What You'll Learn
- Understanding local storage options in Flutter
- Setting up and using Hive (fast NoSQL database)
- Storing simple data with SharedPreferences
- Type adapters for custom objects
- Lazy boxes for memory efficiency
- Building a complete notes app with local storage

## Concept First: Why Local Storage?

### Real-World Analogy
Think of local storage like your phone's contacts app. When you save a contact, it's stored **on your device** - not in the cloud. This means:
- ✅ Works without internet
- ✅ Instant access (no network delays)
- ✅ Privacy (data stays on device)
- ✅ Survives app restarts

Local storage is like having a filing cabinet in your office vs. a warehouse across town. Your local cabinet is much faster to access!

### Why This Matters
Local storage is essential for:

1. **Offline Functionality**: Apps work without internet
2. **Settings & Preferences**: Remember user choices
3. **Caching**: Store data for faster loading
4. **Draft Content**: Save work-in-progress
5. **Performance**: Instant access vs. network calls

According to Google, users expect apps to work offline. 87% of users get frustrated when apps don't save their data locally!

---

## Storage Options Comparison

| Option | Use Case | Performance | Complexity |
|--------|----------|-------------|------------|
| **SharedPreferences** | Simple key-value (settings, flags) | Fast | Easy ⭐ |
| **Hive** | Structured data (notes, todos, cache) | Very Fast | Medium ⭐⭐ |
| **SQLite** | Relational data, complex queries | Fast | Hard ⭐⭐⭐ |
| **Secure Storage** | Sensitive data (tokens, passwords) | Medium | Easy ⭐ |

**This lesson focuses on Hive and SharedPreferences** (we'll cover SQLite in the next lesson).

---

## Part 1: SharedPreferences (Simple Storage)

### Setup

```yaml
dependencies:
  shared_preferences: ^2.3.2
```

```bash
flutter pub get
```

### Basic Usage

```dart
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

class PreferencesExample extends StatefulWidget {
  @override
  State<PreferencesExample> createState() => _PreferencesExampleState();
}

class _PreferencesExampleState extends State<PreferencesExample> {
  bool _isDarkMode = false;
  String _userName = '';
  int _loginCount = 0;

  @override
  void initState() {
    super.initState();
    _loadPreferences();
  }

  // Load saved data
  Future<void> _loadPreferences() async {
    final prefs = await SharedPreferences.getInstance();

    setState(() {
      _isDarkMode = prefs.getBool('darkMode') ?? false;
      _userName = prefs.getString('userName') ?? 'Guest';
      _loginCount = prefs.getInt('loginCount') ?? 0;
    });
  }

  // Save data
  Future<void> _savePreferences() async {
    final prefs = await SharedPreferences.getInstance();

    await prefs.setBool('darkMode', _isDarkMode);
    await prefs.setString('userName', _userName);
    await prefs.setInt('loginCount', _loginCount + 1);

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text('Preferences saved!')),
    );
  }

  // Clear all data
  Future<void> _clearPreferences() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.clear();

    setState(() {
      _isDarkMode = false;
      _userName = 'Guest';
      _loginCount = 0;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('SharedPreferences Demo')),
      body: Padding(
        padding: EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Welcome, $_userName!', style: TextStyle(fontSize: 24)),
            Text('Login count: $_loginCount'),
            SizedBox(height: 20),

            SwitchListTile(
              title: Text('Dark Mode'),
              value: _isDarkMode,
              onChanged: (value) {
                setState(() => _isDarkMode = value);
              },
            ),

            SizedBox(height: 20),

            TextField(
              decoration: InputDecoration(labelText: 'Your Name'),
              onChanged: (value) => _userName = value,
            ),

            SizedBox(height: 20),

            ElevatedButton(
              onPressed: _savePreferences,
              child: Text('Save Preferences'),
            ),

            OutlinedButton(
              onPressed: _clearPreferences,
              child: Text('Clear All Data'),
            ),
          ],
        ),
      ),
    );
  }
}
```

**Available Methods:**
```dart
// Save
await prefs.setBool('key', true);
await prefs.setString('key', 'value');
await prefs.setInt('key', 42);
await prefs.setDouble('key', 3.14);
await prefs.setStringList('key', ['a', 'b', 'c']);

// Read (with defaults)
final boolValue = prefs.getBool('key') ?? false;
final stringValue = prefs.getString('key') ?? '';
final intValue = prefs.getInt('key') ?? 0;
final doubleValue = prefs.getDouble('key') ?? 0.0;
final listValue = prefs.getStringList('key') ?? [];

// Check existence
final exists = prefs.containsKey('key');

// Remove single key
await prefs.remove('key');

// Clear all
await prefs.clear();
```

---

## Part 2: Hive (NoSQL Database)

Hive is **blazing fast** (10x faster than SQLite for reads) and works great for structured data.

### Setup

```yaml
dependencies:
  hive: ^2.2.3
  hive_flutter: ^1.1.0

dev_dependencies:
  hive_generator: ^2.0.1
  build_runner: ^2.4.13
```

```bash
flutter pub get
```

### Initialize Hive

```dart
import 'package:flutter/material.dart';
import 'package:hive_flutter/hive_flutter.dart';

void main() async {
  // Initialize Hive
  await Hive.initFlutter();

  // Open boxes (like tables)
  await Hive.openBox('notes');
  await Hive.openBox('settings');

  runApp(MyApp());
}
```

### Simple Usage (Key-Value)

```dart
import 'package:hive_flutter/hive_flutter.dart';

class HiveSimpleExample extends StatefulWidget {
  @override
  State<HiveSimpleExample> createState() => _HiveSimpleExampleState();
}

class _HiveSimpleExampleState extends State<HiveSimpleExample> {
  final box = Hive.box('notes');

  void _saveNote() {
    box.put('note1', 'My first note');
    box.put('note2', 'Another note');
    box.put('counter', 42);

    setState(() {});
  }

  void _readNote() {
    final note1 = box.get('note1', defaultValue: 'No note');
    final counter = box.get('counter', defaultValue: 0);

    print('Note: $note1, Counter: $counter');
  }

  void _deleteNote() {
    box.delete('note1');
    setState(() {});
  }

  void _deleteAll() {
    box.clear();
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Hive Simple Demo')),
      body: Column(
        children: [
          ElevatedButton(onPressed: _saveNote, child: Text('Save Notes')),
          ElevatedButton(onPressed: _readNote, child: Text('Read Note')),
          ElevatedButton(onPressed: _deleteNote, child: Text('Delete Note 1')),
          ElevatedButton(onPressed: _deleteAll, child: Text('Delete All')),

          SizedBox(height: 20),

          Text('All Keys: ${box.keys.toList()}'),
          Text('All Values: ${box.values.toList()}'),
        ],
      ),
    );
  }
}
```

---

## Storing Custom Objects with Type Adapters

### Step 1: Create a Model

```dart
import 'package:hive/hive.dart';

part 'note.g.dart';  // Generated file

@HiveType(typeId: 0)  // Unique ID for this type
class Note extends HiveObject {
  @HiveField(0)
  String title;

  @HiveField(1)
  String content;

  @HiveField(2)
  DateTime createdAt;

  @HiveField(3)
  bool isFavorite;

  Note({
    required this.title,
    required this.content,
    required this.createdAt,
    this.isFavorite = false,
  });

  // Optional: toString for debugging
  @override
  String toString() {
    return 'Note{title: $title, content: $content, createdAt: $createdAt}';
  }
}
```

### Step 2: Generate Type Adapter

Run this command:
```bash
flutter pub run build_runner build
```

This creates `note.g.dart` with the type adapter code.

### Step 3: Register and Use

```dart
import 'package:flutter/material.dart';
import 'package:hive_flutter/hive_flutter.dart';
import 'note.dart';

void main() async {
  await Hive.initFlutter();

  // Register the adapter
  Hive.registerAdapter(NoteAdapter());

  // Open box for Note objects
  await Hive.openBox<Note>('notes');

  runApp(MyApp());
}

class NotesApp extends StatefulWidget {
  @override
  State<NotesApp> createState() => _NotesAppState();
}

class _NotesAppState extends State<NotesApp> {
  late Box<Note> notesBox;

  @override
  void initState() {
    super.initState();
    notesBox = Hive.box<Note>('notes');
  }

  void _addNote() {
    final note = Note(
      title: 'My Note',
      content: 'This is the content',
      createdAt: DateTime.now(),
    );

    // Add returns a key (index)
    notesBox.add(note);

    setState(() {});
  }

  void _updateNote(int index) {
    final note = notesBox.getAt(index)!;
    note.title = 'Updated Title';
    note.save();  // Save changes (because extends HiveObject)

    setState(() {});
  }

  void _deleteNote(int index) {
    notesBox.deleteAt(index);
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Hive Notes (${notesBox.length})')),
      body: ValueListenableBuilder(
        valueListenable: notesBox.listenable(),
        builder: (context, Box<Note> box, _) {
          if (box.isEmpty) {
            return Center(child: Text('No notes yet!'));
          }

          return ListView.builder(
            itemCount: box.length,
            itemBuilder: (context, index) {
              final note = box.getAt(index);

              return ListTile(
                title: Text(note!.title),
                subtitle: Text(note.content),
                trailing: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    IconButton(
                      icon: Icon(Icons.edit),
                      onPressed: () => _updateNote(index),
                    ),
                    IconButton(
                      icon: Icon(Icons.delete),
                      onPressed: () => _deleteNote(index),
                    ),
                  ],
                ),
              );
            },
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _addNote,
        child: Icon(Icons.add),
      ),
    );
  }

  @override
  void dispose() {
    // Don't close boxes in dispose - they're global
    // Hive.close();  // Only call this when app exits
    super.dispose();
  }
}
```

**Key Methods:**
```dart
// Add (returns auto-increment key)
final key = box.add(note);

// Put with custom key
box.put('my-key', note);

// Get by key
final note = box.get('my-key');

// Get by index
final note = box.getAt(0);

// Update (for HiveObject subclasses)
note.title = 'New Title';
note.save();

// Delete
box.delete('my-key');
box.deleteAt(0);

// Get all values
final allNotes = box.values.toList();

// Get all keys
final allKeys = box.keys.toList();

// Count
final count = box.length;

// Clear all
box.clear();
```

---

## Complete Example: Notes App with Categories

```dart
import 'package:flutter/material.dart';
import 'package:hive_flutter/hive_flutter.dart';

// Models
part 'models.g.dart';

@HiveType(typeId: 0)
class Note extends HiveObject {
  @HiveField(0)
  String title;

  @HiveField(1)
  String content;

  @HiveField(2)
  DateTime createdAt;

  @HiveField(3)
  String category;

  @HiveField(4)
  bool isPinned;

  Note({
    required this.title,
    required this.content,
    required this.createdAt,
    this.category = 'General',
    this.isPinned = false,
  });
}

// Main app
void main() async {
  await Hive.initFlutter();
  Hive.registerAdapter(NoteAdapter());
  await Hive.openBox<Note>('notes');
  runApp(MaterialApp(home: NotesScreen()));
}

class NotesScreen extends StatefulWidget {
  @override
  State<NotesScreen> createState() => _NotesScreenState();
}

class _NotesScreenState extends State<NotesScreen> {
  final box = Hive.box<Note>('notes');
  String _selectedCategory = 'All';

  List<Note> get filteredNotes {
    final notes = box.values.toList();

    if (_selectedCategory == 'All') {
      return notes;
    }

    return notes.where((n) => n.category == _selectedCategory).toList();
  }

  List<Note> get pinnedNotes {
    return filteredNotes.where((n) => n.isPinned).toList();
  }

  List<Note> get regularNotes {
    return filteredNotes.where((n) => !n.isPinned).toList();
  }

  void _showAddNoteDialog() {
    final titleController = TextEditingController();
    final contentController = TextEditingController();
    String category = 'General';

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text('New Note'),
        content: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                controller: titleController,
                decoration: InputDecoration(labelText: 'Title'),
              ),
              SizedBox(height: 10),
              TextField(
                controller: contentController,
                decoration: InputDecoration(labelText: 'Content'),
                maxLines: 3,
              ),
              SizedBox(height: 10),
              DropdownButtonFormField<String>(
                value: category,
                items: ['General', 'Work', 'Personal', 'Ideas']
                    .map((cat) => DropdownMenuItem(value: cat, child: Text(cat)))
                    .toList(),
                onChanged: (value) => category = value!,
                decoration: InputDecoration(labelText: 'Category'),
              ),
            ],
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: Text('Cancel'),
          ),
          ElevatedButton(
            onPressed: () {
              if (titleController.text.isNotEmpty) {
                final note = Note(
                  title: titleController.text,
                  content: contentController.text,
                  createdAt: DateTime.now(),
                  category: category,
                );

                box.add(note);
                Navigator.pop(context);
              }
            },
            child: Text('Save'),
          ),
        ],
      ),
    );
  }

  void _deleteNote(Note note) {
    note.delete();
  }

  void _togglePin(Note note) {
    note.isPinned = !note.isPinned;
    note.save();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('My Notes'),
        actions: [
          PopupMenuButton<String>(
            onSelected: (category) {
              setState(() => _selectedCategory = category);
            },
            itemBuilder: (context) => [
              PopupMenuItem(value: 'All', child: Text('All')),
              PopupMenuItem(value: 'General', child: Text('General')),
              PopupMenuItem(value: 'Work', child: Text('Work')),
              PopupMenuItem(value: 'Personal', child: Text('Personal')),
              PopupMenuItem(value: 'Ideas', child: Text('Ideas')),
            ],
          ),
        ],
      ),
      body: ValueListenableBuilder(
        valueListenable: box.listenable(),
        builder: (context, Box<Note> box, _) {
          if (box.isEmpty) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.note_outlined, size: 100, color: Colors.grey),
                  SizedBox(height: 20),
                  Text('No notes yet!', style: TextStyle(fontSize: 18)),
                ],
              ),
            );
          }

          return ListView(
            padding: EdgeInsets.all(8),
            children: [
              // Pinned section
              if (pinnedNotes.isNotEmpty) ...[
                Padding(
                  padding: EdgeInsets.all(8),
                  child: Text(
                    'PINNED',
                    style: TextStyle(fontWeight: FontWeight.bold, color: Colors.grey),
                  ),
                ),
                ...pinnedNotes.map((note) => _buildNoteCard(note)),
                SizedBox(height: 10),
              ],

              // Regular notes
              if (regularNotes.isNotEmpty) ...[
                if (pinnedNotes.isNotEmpty)
                  Padding(
                    padding: EdgeInsets.all(8),
                    child: Text(
                      'OTHERS',
                      style: TextStyle(fontWeight: FontWeight.bold, color: Colors.grey),
                    ),
                  ),
                ...regularNotes.map((note) => _buildNoteCard(note)),
              ],
            ],
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _showAddNoteDialog,
        child: Icon(Icons.add),
      ),
    );
  }

  Widget _buildNoteCard(Note note) {
    return Card(
      margin: EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      child: ListTile(
        title: Text(note.title, style: TextStyle(fontWeight: FontWeight.bold)),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(note.content, maxLines: 2, overflow: TextOverflow.ellipsis),
            SizedBox(height: 4),
            Row(
              children: [
                Icon(Icons.category, size: 12, color: Colors.grey),
                SizedBox(width: 4),
                Text(note.category, style: TextStyle(fontSize: 12, color: Colors.grey)),
              ],
            ),
          ],
        ),
        trailing: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            IconButton(
              icon: Icon(
                note.isPinned ? Icons.push_pin : Icons.push_pin_outlined,
                color: note.isPinned ? Colors.orange : null,
              ),
              onPressed: () => _togglePin(note),
            ),
            IconButton(
              icon: Icon(Icons.delete, color: Colors.red),
              onPressed: () => _deleteNote(note),
            ),
          ],
        ),
      ),
    );
  }
}
```

---

## Lazy Boxes (For Large Data)

Use lazy boxes when you have **lots of data** and don't want to load everything into memory.

```dart
// Open lazy box
final lazyBox = await Hive.openLazyBox<Note>('large_notes');

// Read (async because it loads from disk on demand)
final note = await lazyBox.get('key');

// Write (same as regular box)
await lazyBox.put('key', note);
```

**When to use:**
- Regular Box: < 1000 items (loads all into memory)
- Lazy Box: > 1000 items (loads on demand)

---

## Best Practices

1. **Initialize Once**
   ```dart
   void main() async {
     await Hive.initFlutter();  // Call once at app start
     // ...
   }
   ```

2. **Use ValueListenableBuilder**
   - Automatically rebuilds UI when data changes
   - No need for setState()

3. **Don't Close Boxes Frequently**
   ```dart
   // ❌ Bad
   final box = await Hive.openBox('data');
   // ... use box
   await box.close();

   // ✅ Good
   final box = await Hive.openBox('data');  // Open once
   // ... use throughout app lifecycle
   // Close only when app exits
   ```

4. **Use Type Safety**
   ```dart
   // ✅ Good
   final box = Hive.box<Note>('notes');

   // ❌ Bad (no type checking)
   final box = Hive.box('notes');
   ```

5. **Handle Migrations**
   ```dart
   @HiveField(4, defaultValue: false)  // Add default for new fields
   bool isPinned;
   ```

---

## Hive vs SharedPreferences vs SQLite

**Use SharedPreferences for:**
- Settings (dark mode, language)
- Simple flags (onboarding completed)
- Small primitive values

**Use Hive for:**
- Structured data (notes, todos, user profiles)
- Offline-first apps
- Fast read/write performance
- 100s-1000s of records

**Use SQLite for:**
- Complex relationships (foreign keys)
- Advanced queries (JOIN, GROUP BY)
- 10,000s+ records
- When you need SQL

---

## Quiz

**Question 1:** What's the main difference between Hive boxes and lazy boxes?
A) Lazy boxes are slower
B) Regular boxes load all data into memory; lazy boxes load on demand
C) Lazy boxes can't store custom objects
D) There is no difference

**Question 2:** Which storage solution is best for saving a user's theme preference?
A) SQLite
B) Hive
C) SharedPreferences
D) Firebase

**Question 3:** When using custom objects with Hive, what must you do?
A) Nothing, it works automatically
B) Create a type adapter and register it
C) Use JSON encoding manually
D) Store objects as strings

---

## Exercise: Todo App with Local Storage

Build a todo app that:
1. Stores todos locally with Hive
2. Has categories (Work, Personal, Shopping)
3. Supports marking todos as complete
4. Persists data across app restarts
5. Shows todo count by category

**Bonus:**
- Add due dates with reminders
- Search functionality
- Export todos to text file

---

## Summary

You've mastered local storage in Flutter! Here's what we covered:

- **SharedPreferences**: Simple key-value storage for settings
- **Hive Setup**: Fast NoSQL database initialization
- **Type Adapters**: Storing custom objects with code generation
- **CRUD Operations**: Add, read, update, delete data
- **ValueListenableBuilder**: Reactive UI updates
- **Best Practices**: When to use each storage solution

With local storage, your apps can work offline and provide instant user experiences!

---

## Answer Key

**Answer 1:** B) Regular boxes load all data into memory; lazy boxes load on demand

Regular Hive boxes load all data into memory for fast access. Lazy boxes only load data when explicitly requested, making them better for large datasets (1000s+ items) but slightly slower for individual reads.

**Answer 2:** C) SharedPreferences

Theme preferences are simple key-value settings - perfect for SharedPreferences. Using Hive or SQLite would be overkill for a single boolean/string value.

**Answer 3:** B) Create a type adapter and register it

Hive needs to know how to serialize/deserialize custom objects. You must:
1. Annotate class with `@HiveType`
2. Run `build_runner` to generate adapter
3. Register adapter with `Hive.registerAdapter()`
