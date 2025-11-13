# Module 8, Lesson 3: Cloud Firestore - Database Operations

## What You'll Learn
By the end of this lesson, you'll know how to store, retrieve, update, and delete data using Cloud Firestore - Firebase's powerful NoSQL cloud database with real-time synchronization.

---

## Why This Matters

**Every app needs to store data.**

- **Instagram**: Stores billions of posts, comments, likes
- **Twitter**: Real-time tweets synced across devices
- **Spotify**: Playlists, listening history, preferences
- **Without a database**, your app loses all data when closed
- **99% of apps** use a database to persist user data

Cloud Firestore is Google's modern database that automatically syncs data across all devices in real-time.

---

## Real-World Analogy: The Filing Cabinet System

### Traditional SQL Database = Spreadsheet
Data stored in rigid tables with rows and columns:
```
Users Table:
| ID | Name    | Email           | Age |
|----|---------|-----------------|-----|
| 1  | Alice   | alice@mail.com  | 25  |
| 2  | Bob     | bob@mail.com    | 30  |
```

**Problem**: Adding a new field (e.g., "Phone Number") requires updating the entire table structure.

### NoSQL Database (Firestore) = Filing Cabinet
Data stored as flexible documents in folders:
```
users/ (Collection = Folder)
  ├── alice123/ (Document = File)
  │   ├── name: "Alice"
  │   ├── email: "alice@mail.com"
  │   ├── age: 25
  │   └── favoriteColor: "blue"  ← Can add unique fields!
  │
  └── bob456/ (Document = File)
      ├── name: "Bob"
      ├── email: "bob@mail.com"
      └── age: 30
```

**Benefits**:
- ✅ Each document can have different fields
- ✅ Easy to add new data without restructuring
- ✅ Hierarchical organization (like folders and subfolders)

---

## Firestore Structure

### Collections and Documents

```
firestore_database/
├── users/ (Collection)
│   ├── user123/ (Document)
│   │   ├── name: "Alice"
│   │   ├── email: "alice@example.com"
│   │   └── posts/ (Subcollection)
│   │       ├── post1/ (Document)
│   │       │   ├── title: "My First Post"
│   │       │   └── content: "Hello world!"
│   │       └── post2/ (Document)
│   │           ├── title: "Second Post"
│   │           └── content: "Still learning!"
│   │
│   └── user456/ (Document)
│       ├── name: "Bob"
│       └── email: "bob@example.com"
│
└── posts/ (Collection)
    ├── post123/ (Document)
    │   ├── title: "Flutter is Amazing"
    │   ├── authorId: "user123"
    │   └── likes: 42
    └── post456/ (Document)
        ├── title: "Learning Firestore"
        ├── authorId: "user456"
        └── likes: 15
```

**Key Concepts**:
- **Collection**: Container for documents (like a folder)
- **Document**: Individual record with key-value pairs (like a file)
- **Documents must be inside collections** (alternating structure)
- **Documents can contain subcollections**

---

## Setting Up Firestore

### 1. Enable Firestore in Firebase Console

1. Go to https://console.firebase.google.com
2. Select your project
3. Click **"Firestore Database"** in left sidebar
4. Click **"Create database"**
5. **Select mode**:
   - **Test mode** (for learning): Anyone can read/write (insecure!)
   - **Production mode**: Requires security rules (recommended)
6. Choose location (select closest to your users)
7. Click **"Enable"**

### 2. Verify Package in pubspec.yaml

```yaml
dependencies:
  firebase_core: ^4.2.0
  firebase_auth: ^6.1.1
  cloud_firestore: ^6.0.3  # ← This one!
```

Run:
```bash
flutter pub get
```

---

## CRUD Operations (Create, Read, Update, Delete)

### Create a Model Class

```dart
// lib/models/task.dart
import 'package:cloud_firestore/cloud_firestore.dart';

class Task {
  final String? id; // Firestore document ID
  final String title;
  final String description;
  final bool isCompleted;
  final DateTime createdAt;
  final String userId;

  Task({
    this.id,
    required this.title,
    required this.description,
    this.isCompleted = false,
    DateTime? createdAt,
    required this.userId,
  }) : createdAt = createdAt ?? DateTime.now();

  // Convert Task to Map (for Firestore)
  Map<String, dynamic> toMap() {
    return {
      'title': title,
      'description': description,
      'isCompleted': isCompleted,
      'createdAt': Timestamp.fromDate(createdAt),
      'userId': userId,
    };
  }

  // Create Task from Firestore DocumentSnapshot
  factory Task.fromFirestore(DocumentSnapshot doc) {
    final data = doc.data() as Map<String, dynamic>;
    return Task(
      id: doc.id,
      title: data['title'] ?? '',
      description: data['description'] ?? '',
      isCompleted: data['isCompleted'] ?? false,
      createdAt: (data['createdAt'] as Timestamp).toDate(),
      userId: data['userId'] ?? '',
    );
  }

  // Create Task from Map
  factory Task.fromMap(Map<String, dynamic> map, String id) {
    return Task(
      id: id,
      title: map['title'] ?? '',
      description: map['description'] ?? '',
      isCompleted: map['isCompleted'] ?? false,
      createdAt: (map['createdAt'] as Timestamp).toDate(),
      userId: map['userId'] ?? '',
    );
  }

  // Copy with method (useful for updates)
  Task copyWith({
    String? id,
    String? title,
    String? description,
    bool? isCompleted,
    DateTime? createdAt,
    String? userId,
  }) {
    return Task(
      id: id ?? this.id,
      title: title ?? this.title,
      description: description ?? this.description,
      isCompleted: isCompleted ?? this.isCompleted,
      createdAt: createdAt ?? this.createdAt,
      userId: userId ?? this.userId,
    );
  }
}
```

---

### Create Firestore Service

```dart
// lib/services/firestore_service.dart
import 'package:cloud_firestore/cloud_firestore.dart';
import '../models/task.dart';

class FirestoreService {
  final FirebaseFirestore _firestore = FirebaseFirestore.instance;

  // Reference to tasks collection
  CollectionReference get _tasksCollection => _firestore.collection('tasks');

  // ========== CREATE ==========

  // Add a new task
  Future<String> createTask(Task task) async {
    try {
      final docRef = await _tasksCollection.add(task.toMap());
      return docRef.id; // Return the generated document ID
    } catch (e) {
      throw 'Failed to create task: $e';
    }
  }

  // Add task with custom ID
  Future<void> createTaskWithId(String id, Task task) async {
    try {
      await _tasksCollection.doc(id).set(task.toMap());
    } catch (e) {
      throw 'Failed to create task: $e';
    }
  }

  // ========== READ ==========

  // Get single task by ID
  Future<Task?> getTask(String taskId) async {
    try {
      final doc = await _tasksCollection.doc(taskId).get();

      if (doc.exists) {
        return Task.fromFirestore(doc);
      }
      return null;
    } catch (e) {
      throw 'Failed to get task: $e';
    }
  }

  // Get all tasks for a user (returns Future)
  Future<List<Task>> getUserTasks(String userId) async {
    try {
      final querySnapshot = await _tasksCollection
          .where('userId', isEqualTo: userId)
          .orderBy('createdAt', descending: true)
          .get();

      return querySnapshot.docs
          .map((doc) => Task.fromFirestore(doc))
          .toList();
    } catch (e) {
      throw 'Failed to get tasks: $e';
    }
  }

  // Get tasks as a Stream (real-time updates!)
  Stream<List<Task>> getUserTasksStream(String userId) {
    return _tasksCollection
        .where('userId', isEqualTo: userId)
        .orderBy('createdAt', descending: true)
        .snapshots()
        .map((snapshot) {
      return snapshot.docs.map((doc) => Task.fromFirestore(doc)).toList();
    });
  }

  // Get completed tasks only
  Future<List<Task>> getCompletedTasks(String userId) async {
    try {
      final querySnapshot = await _tasksCollection
          .where('userId', isEqualTo: userId)
          .where('isCompleted', isEqualTo: true)
          .orderBy('createdAt', descending: true)
          .get();

      return querySnapshot.docs
          .map((doc) => Task.fromFirestore(doc))
          .toList();
    } catch (e) {
      throw 'Failed to get completed tasks: $e';
    }
  }

  // ========== UPDATE ==========

  // Update entire task
  Future<void> updateTask(String taskId, Task task) async {
    try {
      await _tasksCollection.doc(taskId).update(task.toMap());
    } catch (e) {
      throw 'Failed to update task: $e';
    }
  }

  // Update specific fields only
  Future<void> updateTaskFields(String taskId, Map<String, dynamic> fields) async {
    try {
      await _tasksCollection.doc(taskId).update(fields);
    } catch (e) {
      throw 'Failed to update task fields: $e';
    }
  }

  // Toggle task completion
  Future<void> toggleTaskCompletion(String taskId, bool isCompleted) async {
    try {
      await _tasksCollection.doc(taskId).update({
        'isCompleted': !isCompleted,
      });
    } catch (e) {
      throw 'Failed to toggle task: $e';
    }
  }

  // ========== DELETE ==========

  // Delete a task
  Future<void> deleteTask(String taskId) async {
    try {
      await _tasksCollection.doc(taskId).delete();
    } catch (e) {
      throw 'Failed to delete task: $e';
    }
  }

  // Delete all completed tasks for a user
  Future<void> deleteCompletedTasks(String userId) async {
    try {
      final querySnapshot = await _tasksCollection
          .where('userId', isEqualTo: userId)
          .where('isCompleted', isEqualTo: true)
          .get();

      // Batch delete for efficiency
      final batch = _firestore.batch();
      for (var doc in querySnapshot.docs) {
        batch.delete(doc.reference);
      }
      await batch.commit();
    } catch (e) {
      throw 'Failed to delete completed tasks: $e';
    }
  }

  // ========== ADVANCED QUERIES ==========

  // Search tasks by title
  Future<List<Task>> searchTasks(String userId, String searchTerm) async {
    try {
      final querySnapshot = await _tasksCollection
          .where('userId', isEqualTo: userId)
          .where('title', isGreaterThanOrEqualTo: searchTerm)
          .where('title', isLessThanOrEqualTo: '$searchTerm\uf8ff')
          .get();

      return querySnapshot.docs
          .map((doc) => Task.fromFirestore(doc))
          .toList();
    } catch (e) {
      throw 'Failed to search tasks: $e';
    }
  }

  // Get task count for a user
  Future<int> getTaskCount(String userId) async {
    try {
      final querySnapshot = await _tasksCollection
          .where('userId', isEqualTo: userId)
          .count()
          .get();

      return querySnapshot.count ?? 0;
    } catch (e) {
      throw 'Failed to get task count: $e';
    }
  }
}
```

---

## Building a Task Manager App

### Tasks Screen with StreamBuilder

```dart
// lib/screens/tasks/tasks_screen.dart
import 'package:flutter/material.dart';
import '../../services/firestore_service.dart';
import '../../services/auth_service.dart';
import '../../models/task.dart';
import 'add_task_screen.dart';

class TasksScreen extends StatefulWidget {
  const TasksScreen({super.key});

  @override
  State<TasksScreen> createState() => _TasksScreenState();
}

class _TasksScreenState extends State<TasksScreen> {
  final _firestoreService = FirestoreService();
  final _authService = AuthService();

  @override
  Widget build(BuildContext context) {
    final userId = _authService.currentUser?.uid;

    if (userId == null) {
      return const Scaffold(
        body: Center(child: Text('Please login first')),
      );
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text('My Tasks'),
        actions: [
          IconButton(
            icon: const Icon(Icons.delete_sweep),
            tooltip: 'Clear completed',
            onPressed: () => _clearCompleted(userId),
          ),
        ],
      ),
      body: StreamBuilder<List<Task>>(
        stream: _firestoreService.getUserTasksStream(userId),
        builder: (context, snapshot) {
          // Loading state
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          }

          // Error state
          if (snapshot.hasError) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Icon(Icons.error_outline, size: 64, color: Colors.red),
                  const SizedBox(height: 16),
                  Text('Error: ${snapshot.error}'),
                  const SizedBox(height: 16),
                  FilledButton(
                    onPressed: () => setState(() {}),
                    child: const Text('Retry'),
                  ),
                ],
              ),
            );
          }

          // Empty state
          if (!snapshot.hasData || snapshot.data!.isEmpty) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(
                    Icons.task_alt,
                    size: 100,
                    color: Colors.grey.shade300,
                  ),
                  const SizedBox(height: 16),
                  Text(
                    'No tasks yet',
                    style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                      color: Colors.grey.shade600,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    'Tap + to create your first task',
                    style: TextStyle(color: Colors.grey.shade500),
                  ),
                ],
              ),
            );
          }

          // Success state with data
          final tasks = snapshot.data!;

          return ListView.builder(
            padding: const EdgeInsets.all(16),
            itemCount: tasks.length,
            itemBuilder: (context, index) {
              final task = tasks[index];
              return _buildTaskCard(task);
            },
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => _navigateToAddTask(context),
        child: const Icon(Icons.add),
      ),
    );
  }

  Widget _buildTaskCard(Task task) {
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      child: ListTile(
        leading: Checkbox(
          value: task.isCompleted,
          onChanged: (_) => _toggleTask(task),
        ),
        title: Text(
          task.title,
          style: TextStyle(
            decoration: task.isCompleted ? TextDecoration.lineThrough : null,
            color: task.isCompleted ? Colors.grey : null,
          ),
        ),
        subtitle: task.description.isNotEmpty
            ? Text(
                task.description,
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
              )
            : null,
        trailing: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            // Edit button
            IconButton(
              icon: const Icon(Icons.edit),
              onPressed: () => _editTask(task),
            ),
            // Delete button
            IconButton(
              icon: const Icon(Icons.delete, color: Colors.red),
              onPressed: () => _deleteTask(task),
            ),
          ],
        ),
        onTap: () => _showTaskDetails(task),
      ),
    );
  }

  Future<void> _toggleTask(Task task) async {
    try {
      await _firestoreService.toggleTaskCompletion(task.id!, task.isCompleted);
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Failed to update task: $e')),
        );
      }
    }
  }

  Future<void> _deleteTask(Task task) async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Delete Task'),
        content: Text('Delete "${task.title}"?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('Cancel'),
          ),
          FilledButton(
            onPressed: () => Navigator.pop(context, true),
            style: FilledButton.styleFrom(backgroundColor: Colors.red),
            child: const Text('Delete'),
          ),
        ],
      ),
    );

    if (confirm == true) {
      try {
        await _firestoreService.deleteTask(task.id!);
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Task deleted')),
          );
        }
      } catch (e) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('Failed to delete: $e')),
          );
        }
      }
    }
  }

  Future<void> _clearCompleted(String userId) async {
    try {
      await _firestoreService.deleteCompletedTasks(userId);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Completed tasks cleared')),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Failed to clear: $e')),
        );
      }
    }
  }

  void _navigateToAddTask(BuildContext context) {
    Navigator.of(context).push(
      MaterialPageRoute(builder: (_) => const AddTaskScreen()),
    );
  }

  void _editTask(Task task) {
    Navigator.of(context).push(
      MaterialPageRoute(builder: (_) => AddTaskScreen(task: task)),
    );
  }

  void _showTaskDetails(Task task) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(task.title),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            if (task.description.isNotEmpty) ...[
              Text(task.description),
              const SizedBox(height: 16),
            ],
            Text(
              'Status: ${task.isCompleted ? "Completed" : "Pending"}',
              style: const TextStyle(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 8),
            Text(
              'Created: ${_formatDate(task.createdAt)}',
              style: TextStyle(color: Colors.grey.shade600),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Close'),
          ),
        ],
      ),
    );
  }

  String _formatDate(DateTime date) {
    return '${date.day}/${date.month}/${date.year} ${date.hour}:${date.minute.toString().padLeft(2, '0')}';
  }
}
```

---

### Add Task Screen

```dart
// lib/screens/tasks/add_task_screen.dart
import 'package:flutter/material.dart';
import '../../services/firestore_service.dart';
import '../../services/auth_service.dart';
import '../../models/task.dart';

class AddTaskScreen extends StatefulWidget {
  final Task? task; // If editing, pass existing task

  const AddTaskScreen({super.key, this.task});

  @override
  State<AddTaskScreen> createState() => _AddTaskScreenState();
}

class _AddTaskScreenState extends State<AddTaskScreen> {
  final _firestoreService = FirestoreService();
  final _authService = AuthService();
  final _formKey = GlobalKey<FormState>();
  final _titleController = TextEditingController();
  final _descriptionController = TextEditingController();

  bool _isLoading = false;
  bool get _isEditing => widget.task != null;

  @override
  void initState() {
    super.initState();
    if (_isEditing) {
      _titleController.text = widget.task!.title;
      _descriptionController.text = widget.task!.description;
    }
  }

  @override
  void dispose() {
    _titleController.dispose();
    _descriptionController.dispose();
    super.dispose();
  }

  Future<void> _saveTask() async {
    if (!_formKey.currentState!.validate()) return;

    final userId = _authService.currentUser?.uid;
    if (userId == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Please login first')),
      );
      return;
    }

    setState(() => _isLoading = true);

    try {
      final task = Task(
        id: widget.task?.id,
        title: _titleController.text.trim(),
        description: _descriptionController.text.trim(),
        userId: userId,
        isCompleted: widget.task?.isCompleted ?? false,
        createdAt: widget.task?.createdAt,
      );

      if (_isEditing) {
        await _firestoreService.updateTask(task.id!, task);
      } else {
        await _firestoreService.createTask(task);
      }

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(_isEditing ? 'Task updated!' : 'Task created!'),
          ),
        );
        Navigator.of(context).pop();
      }
    } catch (e) {
      setState(() => _isLoading = false);

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Error: $e')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(_isEditing ? 'Edit Task' : 'Add Task'),
      ),
      body: Form(
        key: _formKey,
        child: ListView(
          padding: const EdgeInsets.all(24.0),
          children: [
            TextFormField(
              controller: _titleController,
              decoration: const InputDecoration(
                labelText: 'Title',
                border: OutlineInputBorder(),
              ),
              enabled: !_isLoading,
              validator: (value) {
                if (value == null || value.trim().isEmpty) {
                  return 'Please enter a title';
                }
                return null;
              },
            ),
            const SizedBox(height: 16),
            TextFormField(
              controller: _descriptionController,
              decoration: const InputDecoration(
                labelText: 'Description (optional)',
                border: OutlineInputBorder(),
                alignLabelWithHint: true,
              ),
              maxLines: 5,
              enabled: !_isLoading,
            ),
            const SizedBox(height: 24),
            FilledButton(
              onPressed: _isLoading ? null : _saveTask,
              style: FilledButton.styleFrom(
                padding: const EdgeInsets.symmetric(vertical: 16),
              ),
              child: _isLoading
                  ? const SizedBox(
                      height: 20,
                      width: 20,
                      child: CircularProgressIndicator(strokeWidth: 2),
                    )
                  : Text(_isEditing ? 'Update Task' : 'Create Task'),
            ),
          ],
        ),
      ),
    );
  }
}
```

---

## Testing Your Firestore App

1. **Run the app**: `flutter run`
2. **Create tasks**: Add several tasks
3. **Check Firebase Console**: Firestore Database → View your data
4. **Real-time sync test**:
   - Open app on 2 devices/emulators
   - Create task on device 1
   - Watch it appear instantly on device 2!
5. **Test CRUD**: Create, read, update, delete tasks

---

## Firestore Query Operators

### Comparison Operators

```dart
// Equal to
.where('status', isEqualTo: 'active')

// Not equal to
.where('status', isNotEqualTo: 'deleted')

// Greater than
.where('age', isGreaterThan: 18)

// Greater than or equal
.where('price', isGreaterThanOrEqualTo: 100)

// Less than
.where('age', isLessThan: 65)

// Less than or equal
.where('score', isLessThanOrEqualTo: 100)

// Array contains
.where('tags', arrayContains: 'flutter')

// In (matches any value in list)
.where('category', whereIn: ['tech', 'science'])

// Not in
.where('status', whereNotIn: ['deleted', 'archived'])
```

### Ordering and Limiting

```dart
// Order by field (ascending)
.orderBy('createdAt')

// Order descending
.orderBy('createdAt', descending: true)

// Multiple orderBy
.orderBy('priority', descending: true)
.orderBy('createdAt')

// Limit results
.limit(10)

// Start after document (pagination)
.startAfterDocument(lastDocument)
```

---

## Real-Time Updates with Streams

**Streams automatically update when data changes!**

### Single Document Stream

```dart
Stream<Task?> getTaskStream(String taskId) {
  return _tasksCollection
      .doc(taskId)
      .snapshots()
      .map((doc) {
        if (doc.exists) {
          return Task.fromFirestore(doc);
        }
        return null;
      });
}
```

### Collection Stream

```dart
Stream<List<Task>> getTasksStream() {
  return _tasksCollection
      .snapshots()
      .map((snapshot) {
        return snapshot.docs
            .map((doc) => Task.fromFirestore(doc))
            .toList();
      });
}
```

**Use with StreamBuilder** for automatic UI updates!

---

## Batch Operations (Multiple Writes)

For performance, batch multiple writes:

```dart
Future<void> batchUpdateTasks(List<Task> tasks) async {
  final batch = _firestore.batch();

  for (var task in tasks) {
    final docRef = _tasksCollection.doc(task.id);
    batch.update(docRef, task.toMap());
  }

  await batch.commit(); // Execute all updates at once
}
```

**Benefits**:
- ✅ Atomic (all succeed or all fail)
- ✅ More efficient (single network call)
- ✅ Up to 500 operations per batch

---

## Best Practices

### ✅ DO:
1. **Use StreamBuilder** for real-time data
2. **Index frequently queried fields** (Firebase Console → Indexes)
3. **Denormalize data** when needed (duplicate for read performance)
4. **Use batch writes** for multiple updates
5. **Paginate large datasets** (use `.limit()` and `.startAfter()`)
6. **Handle offline mode** (Firestore caches automatically)
7. **Use Timestamps** for dates (not Strings)

### ❌ DON'T:
1. **Don't fetch entire collections** (use queries with filters)
2. **Don't nest data too deeply** (max 3-4 levels)
3. **Don't use client-side filtering** (use Firestore queries)
4. **Don't store large files** in documents (use Cloud Storage)
5. **Don't forget security rules** (covered in next lesson)

---

## Common Patterns

### User-Specific Data

```dart
// Always filter by userId!
.where('userId', isEqualTo: currentUserId)
```

### Subcollections

```dart
// Access subcollection
final comments = _firestore
    .collection('posts')
    .doc(postId)
    .collection('comments');
```

### Array Fields

```dart
// Add to array
.update({
  'tags': FieldValue.arrayUnion(['flutter', 'mobile'])
});

// Remove from array
.update({
  'tags': FieldValue.arrayRemove(['outdated'])
});
```

### Increment/Decrement

```dart
// Increment likes count
.update({
  'likes': FieldValue.increment(1)
});

// Decrement
.update({
  'stock': FieldValue.increment(-1)
});
```

---

## Quiz Time! 🧠

### Question 1
What's the difference between `.get()` and `.snapshots()`?

A) They're the same
B) `.get()` fetches once, `.snapshots()` provides real-time updates via Stream
C) `.snapshots()` is faster
D) `.get()` is for collections only

### Question 2
Why use batch writes instead of individual updates?

A) They're required by Firestore
B) They're atomic (all-or-nothing) and more efficient
C) They're easier to write
D) They're only for deletions

### Question 3
What's the maximum nesting depth recommended for Firestore documents?

A) 1 level
B) 3-4 levels
C) 10 levels
D) Unlimited

---

## Answer Key

### Answer 1: B
**Correct**: `.get()` fetches once, `.snapshots()` provides real-time updates via Stream

`.get()` returns a Future that fetches data once. `.snapshots()` returns a Stream that continuously listens for changes and automatically updates your UI via StreamBuilder.

### Answer 2: B
**Correct**: They're atomic (all-or-nothing) and more efficient

Batch writes ensure all operations succeed or fail together (atomicity), prevent partial updates, and reduce network calls by bundling multiple operations into one request.

### Answer 3: B
**Correct**: 3-4 levels

While Firestore technically allows deeper nesting, 3-4 levels is the practical recommendation. Deeper nesting makes queries complex and can impact performance. Consider denormalizing or using subcollections instead.

---

## What's Next?

You've mastered Firestore CRUD operations! In the next lesson, we'll learn **Cloud Storage** to upload and store images, videos, and files.

**Coming up in Lesson 4: Firebase Cloud Storage**
- Upload images and files
- Download URLs
- Progress tracking
- Delete files
- Complete image gallery app

---

## Key Takeaways

✅ Firestore is a NoSQL database with collections and documents
✅ Use StreamBuilder for real-time data synchronization
✅ CRUD operations: add(), get(), update(), delete()
✅ Queries support filtering (.where), ordering (.orderBy), and limiting (.limit)
✅ Batch operations improve performance for multiple writes
✅ Always filter by userId to ensure users only see their data
✅ Firestore automatically handles offline caching

**You can now build apps with real-time cloud databases!** 🎉
