# Module 4, Mini-Project: Interactive Notes App

## Project Overview

Build a complete **Notes App** with all Module 4 concepts:
- ✅ Buttons (FAB, IconButton, ElevatedButton)
- ✅ Forms and text input
- ✅ StatefulWidget and setState
- ✅ Gestures (swipe to delete, long press menu)

**You'll build a real, production-quality app!**

---

## Features

1. **Add notes** with title and content
2. **Edit existing notes**
3. **Delete notes** with swipe gesture
4. **Color-code notes**
5. **Long press** for quick actions
6. **Search notes**
7. **Persistent state** (data survives app restart)

---

## Step 1: Data Model

```dart
// lib/models/note.dart
class Note {
  String id;
  String title;
  String content;
  DateTime createdAt;
  DateTime updatedAt;
  int colorIndex;

  Note({
    required this.id,
    required this.title,
    required this.content,
    required this.createdAt,
    required this.updatedAt,
    this.colorIndex = 0,
  });

  // Convert to JSON for storage
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'title': title,
      'content': content,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
      'colorIndex': colorIndex,
    };
  }

  // Create from JSON
  factory Note.fromJson(Map<String, dynamic> json) {
    return Note(
      id: json['id'],
      title: json['title'],
      content: json['content'],
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: DateTime.parse(json['updatedAt']),
      colorIndex: json['colorIndex'] ?? 0,
    );
  }
}
```

---

## Step 2: Notes List Screen

```dart
// lib/screens/notes_list_screen.dart
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../models/note.dart';

class NotesListScreen extends StatefulWidget {
  @override
  _NotesListScreenState createState() => _NotesListScreenState();
}

class _NotesListScreenState extends State<NotesListScreen> {
  List<Note> notes = [];
  TextEditingController searchController = TextEditingController();
  List<Note> filteredNotes = [];

  final List<Color> noteColors = [
    Colors.white,
    Colors.red[100]!,
    Colors.orange[100]!,
    Colors.yellow[100]!,
    Colors.green[100]!,
    Colors.blue[100]!,
    Colors.purple[100]!,
  ];

  @override
  void initState() {
    super.initState();
    _loadNotes();
    searchController.addListener(_filterNotes);
  }

  void _loadNotes() {
    // In real app, load from SharedPreferences or database
    // For now, add sample data
    notes = [
      Note(
        id: '1',
        title: 'Welcome to Notes',
        content: 'This is your first note. Swipe left to delete, long press for options!',
        createdAt: DateTime.now(),
        updatedAt: DateTime.now(),
        colorIndex: 4,
      ),
    ];
    _filterNotes();
  }

  void _filterNotes() {
    setState(() {
      if (searchController.text.isEmpty) {
        filteredNotes = notes;
      } else {
        filteredNotes = notes.where((note) {
          return note.title.toLowerCase().contains(searchController.text.toLowerCase()) ||
                 note.content.toLowerCase().contains(searchController.text.toLowerCase());
        }).toList();
      }
    });
  }

  void _addOrEditNote({Note? note}) async {
    final result = await Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => NoteEditorScreen(note: note),
      ),
    );

    if (result != null && result is Note) {
      setState(() {
        if (note == null) {
          // Add new note
          notes.insert(0, result);
        } else {
          // Update existing note
          final index = notes.indexWhere((n) => n.id == result.id);
          if (index != -1) {
            notes[index] = result;
          }
        }
        _filterNotes();
      });
    }
  }

  void _deleteNote(Note note) {
    setState(() {
      notes.removeWhere((n) => n.id == note.id);
      _filterNotes();
    });

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text('Note deleted'),
        action: SnackBarAction(
          label: 'UNDO',
          onPressed: () {
            setState(() {
              notes.insert(0, note);
              _filterNotes();
            });
          },
        ),
      ),
    );
  }

  void _showNoteOptions(Note note) {
    HapticFeedback.mediumImpact();
    showModalBottomSheet(
      context: context,
      builder: (context) => Container(
        padding: EdgeInsets.symmetric(vertical: 20),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            ListTile(
              leading: Icon(Icons.edit),
              title: Text('Edit'),
              onTap: () {
                Navigator.pop(context);
                _addOrEditNote(note: note);
              },
            ),
            ListTile(
              leading: Icon(Icons.color_lens),
              title: Text('Change Color'),
              onTap: () {
                Navigator.pop(context);
                _showColorPicker(note);
              },
            ),
            ListTile(
              leading: Icon(Icons.share),
              title: Text('Share'),
              onTap: () {
                Navigator.pop(context);
                // Implement share functionality
              },
            ),
            ListTile(
              leading: Icon(Icons.delete, color: Colors.red),
              title: Text('Delete', style: TextStyle(color: Colors.red)),
              onTap: () {
                Navigator.pop(context);
                _deleteNote(note);
              },
            ),
          ],
        ),
      ),
    );
  }

  void _showColorPicker(Note note) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text('Choose Color'),
        content: Wrap(
          spacing: 8,
          runSpacing: 8,
          children: List.generate(noteColors.length, (index) {
            return GestureDetector(
              onTap: () {
                setState(() {
                  note.colorIndex = index;
                  note.updatedAt = DateTime.now();
                });
                Navigator.pop(context);
              },
              child: Container(
                width: 50,
                height: 50,
                decoration: BoxDecoration(
                  color: noteColors[index],
                  shape: BoxShape.circle,
                  border: Border.all(
                    color: note.colorIndex == index ? Colors.blue : Colors.grey,
                    width: note.colorIndex == index ? 3 : 1,
                  ),
                ),
              ),
            );
          }),
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('My Notes'),
        backgroundColor: Colors.blue,
        foregroundColor: Colors.white,
        bottom: PreferredSize(
          preferredSize: Size.fromHeight(60),
          child: Padding(
            padding: EdgeInsets.all(8),
            child: TextField(
              controller: searchController,
              decoration: InputDecoration(
                hintText: 'Search notes...',
                prefixIcon: Icon(Icons.search),
                filled: true,
                fillColor: Colors.white,
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(30),
                  borderSide: BorderSide.none,
                ),
              ),
            ),
          ),
        ),
      ),
      body: filteredNotes.isEmpty
          ? Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.note_add, size: 100, color: Colors.grey[300]),
                  SizedBox(height: 16),
                  Text(
                    searchController.text.isEmpty
                        ? 'No notes yet!\nTap + to create your first note'
                        : 'No notes found',
                    textAlign: TextAlign.center,
                    style: TextStyle(color: Colors.grey[600], fontSize: 16),
                  ),
                ],
              ),
            )
          : ListView.builder(
              padding: EdgeInsets.all(8),
              itemCount: filteredNotes.length,
              itemBuilder: (context, index) {
                final note = filteredNotes[index];
                return Dismissible(
                  key: Key(note.id),
                  background: Container(
                    color: Colors.red,
                    alignment: Alignment.centerRight,
                    padding: EdgeInsets.only(right: 20),
                    child: Icon(Icons.delete, color: Colors.white, size: 32),
                  ),
                  direction: DismissDirection.endToStart,
                  onDismissed: (direction) {
                    _deleteNote(note);
                  },
                  child: GestureDetector(
                    onTap: () => _addOrEditNote(note: note),
                    onLongPress: () => _showNoteOptions(note),
                    child: Card(
                      color: noteColors[note.colorIndex],
                      child: Padding(
                        padding: EdgeInsets.all(16),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              note.title,
                              style: TextStyle(
                                fontSize: 18,
                                fontWeight: FontWeight.bold,
                              ),
                              maxLines: 1,
                              overflow: TextOverflow.ellipsis,
                            ),
                            SizedBox(height: 8),
                            Text(
                              note.content,
                              style: TextStyle(
                                fontSize: 14,
                                color: Colors.grey[800],
                              ),
                              maxLines: 3,
                              overflow: TextOverflow.ellipsis,
                            ),
                            SizedBox(height: 8),
                            Text(
                              _formatDate(note.updatedAt),
                              style: TextStyle(
                                fontSize: 12,
                                color: Colors.grey[600],
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                  ),
                );
              },
            ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => _addOrEditNote(),
        child: Icon(Icons.add),
        tooltip: 'Add Note',
      ),
    );
  }

  String _formatDate(DateTime date) {
    final now = DateTime.now();
    final diff = now.difference(date);

    if (diff.inDays == 0) {
      return 'Today ${date.hour}:${date.minute.toString().padLeft(2, '0')}';
    } else if (diff.inDays == 1) {
      return 'Yesterday';
    } else if (diff.inDays < 7) {
      return '${diff.inDays} days ago';
    } else {
      return '${date.day}/${date.month}/${date.year}';
    }
  }

  @override
  void dispose() {
    searchController.dispose();
    super.dispose();
  }
}
```

---

## Step 3: Note Editor Screen

```dart
// lib/screens/note_editor_screen.dart
import 'package:flutter/material.dart';
import '../models/note.dart';

class NoteEditorScreen extends StatefulWidget {
  final Note? note;

  NoteEditorScreen({this.note});

  @override
  _NoteEditorScreenState createState() => _NoteEditorScreenState();
}

class _NoteEditorScreenState extends State<NoteEditorScreen> {
  late TextEditingController titleController;
  late TextEditingController contentController;
  bool isEditing = false;

  @override
  void initState() {
    super.initState();
    isEditing = widget.note != null;
    titleController = TextEditingController(text: widget.note?.title ?? '');
    contentController = TextEditingController(text: widget.note?.content ?? '');
  }

  void _saveNote() {
    if (titleController.text.isEmpty && contentController.text.isEmpty) {
      Navigator.pop(context);
      return;
    }

    final now = DateTime.now();
    final note = Note(
      id: widget.note?.id ?? DateTime.now().millisecondsSinceEpoch.toString(),
      title: titleController.text.isEmpty ? 'Untitled' : titleController.text,
      content: contentController.text,
      createdAt: widget.note?.createdAt ?? now,
      updatedAt: now,
      colorIndex: widget.note?.colorIndex ?? 0,
    );

    Navigator.pop(context, note);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        leading: IconButton(
          icon: Icon(Icons.arrow_back),
          onPressed: _saveNote,
        ),
        title: Text(isEditing ? 'Edit Note' : 'New Note'),
        actions: [
          IconButton(
            icon: Icon(Icons.check),
            onPressed: _saveNote,
          ),
        ],
      ),
      body: Padding(
        padding: EdgeInsets.all(16),
        child: Column(
          children: [
            TextField(
              controller: titleController,
              decoration: InputDecoration(
                hintText: 'Title',
                border: InputBorder.none,
              ),
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
              maxLines: 1,
            ),
            SizedBox(height: 16),
            Expanded(
              child: TextField(
                controller: contentController,
                decoration: InputDecoration(
                  hintText: 'Start typing...',
                  border: InputBorder.none,
                ),
                style: TextStyle(fontSize: 16),
                maxLines: null,
                expands: true,
                textAlignVertical: TextAlignVertical.top,
              ),
            ),
          ],
        ),
      ),
    );
  }

  @override
  void dispose() {
    titleController.dispose();
    contentController.dispose();
    super.dispose();
  }
}
```

---

## Step 4: Main App

```dart
// lib/main.dart
import 'package:flutter/material.dart';
import 'screens/notes_list_screen.dart';

void main() {
  runApp(NotesApp());
}

class NotesApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Notes App',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: NotesListScreen(),
      debugShowCheckedModeBanner: false,
    );
  }
}
```

---

## Features Walkthrough

### 1. Search Notes
- Real-time filtering as you type
- Searches both title and content

### 2. Swipe to Delete
- Swipe left on any note
- Shows red delete background
- Includes UNDO option

### 3. Long Press Menu
- Long press any note
- Shows bottom sheet with options:
  - Edit
  - Change Color
  - Share
  - Delete

### 4. Color Coding
- 7 different colors
- Visual organization
- Tap to change

### 5. Empty State
- Beautiful placeholder when no notes
- Clear call-to-action

---

## Enhancement Ideas

Want to make it even better? Add these:

### 1. Persistent Storage
```dart
import 'package:shared_preferences/shared_preferences.dart';

Future<void> _saveNotes() async {
  final prefs = await SharedPreferences.getInstance();
  final notesJson = notes.map((n) => n.toJson()).toList();
  await prefs.setString('notes', jsonEncode(notesJson));
}

Future<void> _loadNotes() async {
  final prefs = await SharedPreferences.getInstance();
  final notesString = prefs.getString('notes');
  if (notesString != null) {
    final List<dynamic> notesJson = jsonDecode(notesString);
    notes = notesJson.map((json) => Note.fromJson(json)).toList();
  }
}
```

### 2. Categories/Tags
Add a category field to Note model and filter by category.

### 3. Voice Input
Use speech_to_text package for voice notes.

### 4. Rich Text Formatting
Bold, italic, bullet points using a rich text editor package.

### 5. Pin Important Notes
Add a `isPinned` field and show pinned notes at top.

---

## ✅ YOUR CHALLENGES

### Challenge 1: Add Categories
Add a dropdown to assign categories (Work, Personal, Ideas) to notes.

### Challenge 2: Dark Theme
Implement a dark theme toggle.

### Challenge 3: Sort Options
Add sorting by date, title, or color.

### Challenge 4: Note Statistics
Show total notes count, character count, and most recent update.

**Success Condition**: A fully functional, beautiful notes app! ✅

---

## What Did We Learn?

This project combined EVERYTHING from Module 4:
- ✅ Multiple button types (FAB, IconButton)
- ✅ Text input with TextEditingController
- ✅ Forms and validation
- ✅ StatefulWidget with complex state
- ✅ Gestures (tap, long press, swipe)
- ✅ Navigation between screens
- ✅ Material Design components
- ✅ Real-world app architecture

---

## What's Next?

**Module 5: State Management**

Your notes app works, but what if you want to:
- Share data between screens more elegantly?
- Separate business logic from UI?
- Make state management scalable?

Next module: **Provider, Riverpod, and professional state management patterns!**
