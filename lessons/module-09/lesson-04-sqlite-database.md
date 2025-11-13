# Lesson 4: SQLite Database

## What You'll Learn
- Understanding relational databases
- Setting up and using SQLite with sqflite
- Creating tables and schemas
- CRUD operations (Create, Read, Update, Delete)
- Advanced queries with WHERE, ORDER BY, JOIN
- Database migrations and versioning
- Building a complete contacts app

## Concept First: What is SQLite?

### Real-World Analogy
Think of SQLite like an **Excel spreadsheet on steroids**:
- **Tables** = Spreadsheet tabs
- **Rows** = Individual records (like spreadsheet rows)
- **Columns** = Data fields (name, age, email)
- **Relationships** = Links between tables (like VLOOKUP)

But unlike Excel, SQLite:
- ✅ Handles millions of rows efficiently
- ✅ Enforces data types and rules
- ✅ Supports complex queries and joins
- ✅ Is transactional (changes are atomic)

### Why This Matters
SQLite is perfect for:

1. **Complex Data Structures**: Multiple related tables
2. **Large Datasets**: 10,000s+ records
3. **Advanced Queries**: Search, filter, sort, group
4. **Data Integrity**: Foreign keys, constraints, transactions
5. **Industry Standard**: Used by Android, iOS, Chrome, Firefox

According to SQLite.org, it's the **most deployed database engine** in the world - billions of copies in active use!

---

## When to Use SQLite vs Hive

| Feature | SQLite | Hive |
|---------|--------|------|
| **Data Structure** | Relational (tables with relationships) | Key-value, NoSQL |
| **Query Language** | SQL (SELECT, JOIN, GROUP BY) | Dart methods |
| **Performance** | Good for complex queries | 10x faster for simple reads |
| **Learning Curve** | Medium (need SQL knowledge) | Easy (pure Dart) |
| **Use Case** | Contact apps, inventory, analytics | Notes, settings, cache |
| **Record Count** | 10,000s+ | 100s-1000s |

**Rule of Thumb:**
- Simple data structure → Hive
- Need relationships or complex queries → SQLite

---

## Setting Up

### 1. Add Dependencies

**pubspec.yaml:**
```yaml
dependencies:
  flutter:
    sdk: flutter
  sqflite: ^2.4.2  # SQLite plugin
  path_provider: ^2.1.5  # For database path
  path: ^1.9.0  # Path utilities
```

```bash
flutter pub get
```

---

## Basic SQLite Example

### Step 1: Create a Database Helper

```dart
import 'package:sqflite/sqflite.dart';
import 'package:path/path.dart';

class DatabaseHelper {
  // Singleton pattern (only one instance)
  static final DatabaseHelper instance = DatabaseHelper._internal();
  factory DatabaseHelper() => instance;
  DatabaseHelper._internal();

  static Database? _database;

  // Get database instance
  Future<Database> get database async {
    if (_database != null) return _database!;

    _database = await _initDatabase();
    return _database!;
  }

  // Initialize database
  Future<Database> _initDatabase() async {
    // Get database path
    final dbPath = await getDatabasesPath();
    final path = join(dbPath, 'my_database.db');

    print('Database path: $path');

    // Open database (creates if doesn't exist)
    return await openDatabase(
      path,
      version: 1,  // Version for migrations
      onCreate: _onCreate,
    );
  }

  // Create tables
  Future<void> _onCreate(Database db, int version) async {
    await db.execute('''
      CREATE TABLE notes (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        title TEXT NOT NULL,
        content TEXT,
        created_at INTEGER NOT NULL
      )
    ''');

    print('Database tables created');
  }

  // CRUD: Create
  Future<int> insertNote(Map<String, dynamic> note) async {
    final db = await database;
    return await db.insert('notes', note);
  }

  // CRUD: Read all
  Future<List<Map<String, dynamic>>> getAllNotes() async {
    final db = await database;
    return await db.query('notes', orderBy: 'created_at DESC');
  }

  // CRUD: Read one
  Future<Map<String, dynamic>?> getNote(int id) async {
    final db = await database;
    final results = await db.query(
      'notes',
      where: 'id = ?',
      whereArgs: [id],
    );

    return results.isNotEmpty ? results.first : null;
  }

  // CRUD: Update
  Future<int> updateNote(int id, Map<String, dynamic> note) async {
    final db = await database;
    return await db.update(
      'notes',
      note,
      where: 'id = ?',
      whereArgs: [id],
    );
  }

  // CRUD: Delete
  Future<int> deleteNote(int id) async {
    final db = await database;
    return await db.delete(
      'notes',
      where: 'id = ?',
      whereArgs: [id],
    );
  }

  // Close database
  Future<void> close() async {
    final db = await database;
    db.close();
  }
}
```

### Step 2: Use in Your App

```dart
import 'package:flutter/material.dart';
import 'database_helper.dart';

class SQLiteDemo extends StatefulWidget {
  @override
  State<SQLiteDemo> createState() => _SQLiteDemoState();
}

class _SQLiteDemoState extends State<SQLiteDemo> {
  final DatabaseHelper _db = DatabaseHelper();
  List<Map<String, dynamic>> _notes = [];
  final _titleController = TextEditingController();
  final _contentController = TextEditingController();

  @override
  void initState() {
    super.initState();
    _loadNotes();
  }

  Future<void> _loadNotes() async {
    final notes = await _db.getAllNotes();
    setState(() {
      _notes = notes;
    });
  }

  Future<void> _addNote() async {
    if (_titleController.text.isEmpty) return;

    await _db.insertNote({
      'title': _titleController.text,
      'content': _contentController.text,
      'created_at': DateTime.now().millisecondsSinceEpoch,
    });

    _titleController.clear();
    _contentController.clear();
    _loadNotes();

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text('Note added!')),
    );
  }

  Future<void> _deleteNote(int id) async {
    await _db.deleteNote(id);
    _loadNotes();

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text('Note deleted')),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('SQLite Demo (${_notes.length} notes)')),
      body: Column(
        children: [
          // Input form
          Padding(
            padding: EdgeInsets.all(16),
            child: Column(
              children: [
                TextField(
                  controller: _titleController,
                  decoration: InputDecoration(labelText: 'Title'),
                ),
                TextField(
                  controller: _contentController,
                  decoration: InputDecoration(labelText: 'Content'),
                  maxLines: 3,
                ),
                SizedBox(height: 10),
                ElevatedButton(
                  onPressed: _addNote,
                  child: Text('Add Note'),
                ),
              ],
            ),
          ),

          Divider(),

          // Notes list
          Expanded(
            child: _notes.isEmpty
                ? Center(child: Text('No notes yet!'))
                : ListView.builder(
                    itemCount: _notes.length,
                    itemBuilder: (context, index) {
                      final note = _notes[index];
                      final createdAt = DateTime.fromMillisecondsSinceEpoch(
                        note['created_at'],
                      );

                      return ListTile(
                        title: Text(note['title']),
                        subtitle: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(note['content'] ?? ''),
                            SizedBox(height: 4),
                            Text(
                              'Created: ${createdAt.toString().split('.')[0]}',
                              style: TextStyle(fontSize: 12, color: Colors.grey),
                            ),
                          ],
                        ),
                        trailing: IconButton(
                          icon: Icon(Icons.delete, color: Colors.red),
                          onPressed: () => _deleteNote(note['id']),
                        ),
                      );
                    },
                  ),
          ),
        ],
      ),
    );
  }

  @override
  void dispose() {
    _titleController.dispose();
    _contentController.dispose();
    super.dispose();
  }
}
```

---

## Using Models (Type-Safe Approach)

### Step 1: Create a Model

```dart
class Note {
  final int? id;
  final String title;
  final String content;
  final DateTime createdAt;

  Note({
    this.id,
    required this.title,
    required this.content,
    required this.createdAt,
  });

  // Convert Note to Map (for database)
  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'title': title,
      'content': content,
      'created_at': createdAt.millisecondsSinceEpoch,
    };
  }

  // Convert Map to Note (from database)
  factory Note.fromMap(Map<String, dynamic> map) {
    return Note(
      id: map['id'],
      title: map['title'],
      content: map['content'],
      createdAt: DateTime.fromMillisecondsSinceEpoch(map['created_at']),
    );
  }

  @override
  String toString() {
    return 'Note{id: $id, title: $title, content: $content}';
  }
}
```

### Step 2: Update DatabaseHelper

```dart
class DatabaseHelper {
  // ... previous code ...

  // Type-safe methods
  Future<int> insertNoteObject(Note note) async {
    final db = await database;
    return await db.insert('notes', note.toMap());
  }

  Future<List<Note>> getAllNotesObjects() async {
    final db = await database;
    final maps = await db.query('notes', orderBy: 'created_at DESC');

    return maps.map((map) => Note.fromMap(map)).toList();
  }

  Future<Note?> getNoteObject(int id) async {
    final db = await database;
    final maps = await db.query(
      'notes',
      where: 'id = ?',
      whereArgs: [id],
    );

    return maps.isNotEmpty ? Note.fromMap(maps.first) : null;
  }

  Future<int> updateNoteObject(Note note) async {
    final db = await database;
    return await db.update(
      'notes',
      note.toMap(),
      where: 'id = ?',
      whereArgs: [note.id],
    );
  }
}
```

---

## Advanced Queries

### 1. WHERE Clauses (Filtering)

```dart
// Get notes with specific title
Future<List<Note>> searchNotes(String query) async {
  final db = await database;
  final maps = await db.query(
    'notes',
    where: 'title LIKE ?',
    whereArgs: ['%$query%'],  // % = wildcard
  );

  return maps.map((map) => Note.fromMap(map)).toList();
}

// Get recent notes (last 7 days)
Future<List<Note>> getRecentNotes() async {
  final db = await database;
  final sevenDaysAgo = DateTime.now().subtract(Duration(days: 7));

  final maps = await db.query(
    'notes',
    where: 'created_at > ?',
    whereArgs: [sevenDaysAgo.millisecondsSinceEpoch],
  );

  return maps.map((map) => Note.fromMap(map)).toList();
}
```

### 2. ORDER BY (Sorting)

```dart
// Get notes sorted by title
Future<List<Note>> getNotesSortedByTitle() async {
  final db = await database;
  final maps = await db.query(
    'notes',
    orderBy: 'title ASC',  // ASC = ascending, DESC = descending
  );

  return maps.map((map) => Note.fromMap(map)).toList();
}
```

### 3. LIMIT and OFFSET (Pagination)

```dart
// Get first 20 notes
Future<List<Note>> getNotesPage({int page = 0, int pageSize = 20}) async {
  final db = await database;
  final maps = await db.query(
    'notes',
    orderBy: 'created_at DESC',
    limit: pageSize,
    offset: page * pageSize,
  );

  return maps.map((map) => Note.fromMap(map)).toList();
}
```

### 4. COUNT and Aggregations

```dart
// Get total note count
Future<int> getTotalNoteCount() async {
  final db = await database;
  final result = await db.rawQuery('SELECT COUNT(*) as count FROM notes');

  return Sqflite.firstIntValue(result) ?? 0;
}

// Get notes grouped by date
Future<Map<String, int>> getNotesCountByDate() async {
  final db = await database;
  final results = await db.rawQuery('''
    SELECT DATE(created_at / 1000, 'unixepoch') as date, COUNT(*) as count
    FROM notes
    GROUP BY date
    ORDER BY date DESC
  ''');

  return {for (var row in results) row['date'] as String: row['count'] as int};
}
```

---

## Complete Example: Contacts App with Categories

```dart
import 'package:flutter/material.dart';
import 'package:sqflite/sqflite.dart';
import 'package:path/path.dart';

// Models
class Contact {
  final int? id;
  final String name;
  final String phone;
  final String? email;
  final int categoryId;

  Contact({
    this.id,
    required this.name,
    required this.phone,
    this.email,
    required this.categoryId,
  });

  Map<String, dynamic> toMap() => {
        'id': id,
        'name': name,
        'phone': phone,
        'email': email,
        'category_id': categoryId,
      };

  factory Contact.fromMap(Map<String, dynamic> map) => Contact(
        id: map['id'],
        name: map['name'],
        phone: map['phone'],
        email: map['email'],
        categoryId: map['category_id'],
      );
}

class Category {
  final int? id;
  final String name;
  final String color;

  Category({this.id, required this.name, required this.color});

  Map<String, dynamic> toMap() => {'id': id, 'name': name, 'color': color};

  factory Category.fromMap(Map<String, dynamic> map) => Category(
        id: map['id'],
        name: map['name'],
        color: map['color'],
      );
}

// Database Helper
class ContactsDatabase {
  static final ContactsDatabase instance = ContactsDatabase._internal();
  factory ContactsDatabase() => instance;
  ContactsDatabase._internal();

  static Database? _database;

  Future<Database> get database async {
    if (_database != null) return _database!;
    _database = await _initDatabase();
    return _database!;
  }

  Future<Database> _initDatabase() async {
    final dbPath = await getDatabasesPath();
    final path = join(dbPath, 'contacts.db');

    return await openDatabase(
      path,
      version: 1,
      onCreate: _onCreate,
    );
  }

  Future<void> _onCreate(Database db, int version) async {
    // Create categories table
    await db.execute('''
      CREATE TABLE categories (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL UNIQUE,
        color TEXT NOT NULL
      )
    ''');

    // Create contacts table with foreign key
    await db.execute('''
      CREATE TABLE contacts (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        phone TEXT NOT NULL,
        email TEXT,
        category_id INTEGER NOT NULL,
        FOREIGN KEY (category_id) REFERENCES categories (id)
      )
    ''');

    // Insert default categories
    await db.insert('categories', {'name': 'Friends', 'color': 'blue'});
    await db.insert('categories', {'name': 'Family', 'color': 'green'});
    await db.insert('categories', {'name': 'Work', 'color': 'orange'});
  }

  // Category methods
  Future<List<Category>> getCategories() async {
    final db = await database;
    final maps = await db.query('categories');
    return maps.map((map) => Category.fromMap(map)).toList();
  }

  Future<int> insertCategory(Category category) async {
    final db = await database;
    return await db.insert('categories', category.toMap());
  }

  // Contact methods
  Future<List<Contact>> getContacts({int? categoryId}) async {
    final db = await database;

    final maps = await db.query(
      'contacts',
      where: categoryId != null ? 'category_id = ?' : null,
      whereArgs: categoryId != null ? [categoryId] : null,
      orderBy: 'name ASC',
    );

    return maps.map((map) => Contact.fromMap(map)).toList();
  }

  Future<List<Map<String, dynamic>>> getContactsWithCategory() async {
    final db = await database;

    // JOIN query - combines contacts with category info
    final results = await db.rawQuery('''
      SELECT
        contacts.*,
        categories.name as category_name,
        categories.color as category_color
      FROM contacts
      INNER JOIN categories ON contacts.category_id = categories.id
      ORDER BY contacts.name ASC
    ''');

    return results;
  }

  Future<int> insertContact(Contact contact) async {
    final db = await database;
    return await db.insert('contacts', contact.toMap());
  }

  Future<int> updateContact(Contact contact) async {
    final db = await database;
    return await db.update(
      'contacts',
      contact.toMap(),
      where: 'id = ?',
      whereArgs: [contact.id],
    );
  }

  Future<int> deleteContact(int id) async {
    final db = await database;
    return await db.delete(
      'contacts',
      where: 'id = ?',
      whereArgs: [id],
    );
  }

  Future<List<Contact>> searchContacts(String query) async {
    final db = await database;

    final maps = await db.query(
      'contacts',
      where: 'name LIKE ? OR phone LIKE ?',
      whereArgs: ['%$query%', '%$query%'],
    );

    return maps.map((map) => Contact.fromMap(map)).toList();
  }
}

// UI
void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(MaterialApp(home: ContactsApp()));
}

class ContactsApp extends StatefulWidget {
  @override
  State<ContactsApp> createState() => _ContactsAppState();
}

class _ContactsAppState extends State<ContactsApp> {
  final ContactsDatabase _db = ContactsDatabase();
  List<Map<String, dynamic>> _contacts = [];
  List<Category> _categories = [];
  int? _selectedCategoryId;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    final contacts = await _db.getContactsWithCategory();
    final categories = await _db.getCategories();

    setState(() {
      _contacts = contacts;
      _categories = categories;
    });
  }

  void _showAddContactDialog() {
    final nameController = TextEditingController();
    final phoneController = TextEditingController();
    final emailController = TextEditingController();
    int? selectedCategoryId = _categories.isNotEmpty ? _categories.first.id : null;

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text('Add Contact'),
        content: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                controller: nameController,
                decoration: InputDecoration(labelText: 'Name'),
              ),
              TextField(
                controller: phoneController,
                decoration: InputDecoration(labelText: 'Phone'),
                keyboardType: TextInputType.phone,
              ),
              TextField(
                controller: emailController,
                decoration: InputDecoration(labelText: 'Email (optional)'),
                keyboardType: TextInputType.emailAddress,
              ),
              SizedBox(height: 10),
              DropdownButtonFormField<int>(
                value: selectedCategoryId,
                items: _categories
                    .map((cat) => DropdownMenuItem(
                          value: cat.id,
                          child: Text(cat.name),
                        ))
                    .toList(),
                onChanged: (value) => selectedCategoryId = value,
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
            onPressed: () async {
              if (nameController.text.isNotEmpty &&
                  phoneController.text.isNotEmpty &&
                  selectedCategoryId != null) {
                final contact = Contact(
                  name: nameController.text,
                  phone: phoneController.text,
                  email: emailController.text.isEmpty ? null : emailController.text,
                  categoryId: selectedCategoryId!,
                );

                await _db.insertContact(contact);
                _loadData();
                Navigator.pop(context);
              }
            },
            child: Text('Add'),
          ),
        ],
      ),
    );
  }

  Color _getCategoryColor(String colorName) {
    switch (colorName) {
      case 'blue':
        return Colors.blue;
      case 'green':
        return Colors.green;
      case 'orange':
        return Colors.orange;
      case 'red':
        return Colors.red;
      default:
        return Colors.grey;
    }
  }

  @override
  Widget build(BuildContext context) {
    final filteredContacts = _selectedCategoryId == null
        ? _contacts
        : _contacts.where((c) => c['category_id'] == _selectedCategoryId).toList();

    return Scaffold(
      appBar: AppBar(
        title: Text('Contacts (${filteredContacts.length})'),
        actions: [
          PopupMenuButton<int?>(
            icon: Icon(Icons.filter_list),
            onSelected: (categoryId) {
              setState(() => _selectedCategoryId = categoryId);
            },
            itemBuilder: (context) => [
              PopupMenuItem(value: null, child: Text('All')),
              ..._categories.map((cat) => PopupMenuItem(
                    value: cat.id,
                    child: Text(cat.name),
                  )),
            ],
          ),
        ],
      ),
      body: filteredContacts.isEmpty
          ? Center(child: Text('No contacts yet!'))
          : ListView.builder(
              itemCount: filteredContacts.length,
              itemBuilder: (context, index) {
                final contact = filteredContacts[index];
                final categoryColor = _getCategoryColor(contact['category_color']);

                return ListTile(
                  leading: CircleAvatar(
                    backgroundColor: categoryColor,
                    child: Text(
                      contact['name'][0].toUpperCase(),
                      style: TextStyle(color: Colors.white),
                    ),
                  ),
                  title: Text(contact['name']),
                  subtitle: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(contact['phone']),
                      if (contact['email'] != null)
                        Text(contact['email'], style: TextStyle(fontSize: 12)),
                      Text(
                        contact['category_name'],
                        style: TextStyle(fontSize: 12, color: categoryColor),
                      ),
                    ],
                  ),
                  trailing: IconButton(
                    icon: Icon(Icons.delete, color: Colors.red),
                    onPressed: () async {
                      await _db.deleteContact(contact['id']);
                      _loadData();
                    },
                  ),
                );
              },
            ),
      floatingActionButton: FloatingActionButton(
        onPressed: _showAddContactDialog,
        child: Icon(Icons.add),
      ),
    );
  }
}
```

---

## Database Migrations

When you need to change your database schema:

```dart
Future<Database> _initDatabase() async {
  final dbPath = await getDatabasesPath();
  final path = join(dbPath, 'my_database.db');

  return await openDatabase(
    path,
    version: 2,  // Increment version number
    onCreate: _onCreate,
    onUpgrade: _onUpgrade,  // Handle migration
  );
}

Future<void> _onUpgrade(Database db, int oldVersion, int newVersion) async {
  if (oldVersion < 2) {
    // Add new column to existing table
    await db.execute('ALTER TABLE notes ADD COLUMN is_favorite INTEGER DEFAULT 0');
  }

  // Add more migrations as needed
  // if (oldVersion < 3) { ... }
}
```

---

## Best Practices

1. **Use Transactions for Multiple Operations**
   ```dart
   Future<void> bulkInsert(List<Note> notes) async {
     final db = await database;

     await db.transaction((txn) async {
       for (var note in notes) {
         await txn.insert('notes', note.toMap());
       }
     });
   }
   ```

2. **Always Use Parameterized Queries (Prevent SQL Injection)**
   ```dart
   // ✅ Good - parameterized
   await db.query('notes', where: 'title = ?', whereArgs: [userInput]);

   // ❌ Bad - vulnerable to SQL injection
   await db.rawQuery("SELECT * FROM notes WHERE title = '$userInput'");
   ```

3. **Close Database When App Exits**
   ```dart
   @override
   void dispose() {
     DatabaseHelper().close();
     super.dispose();
   }
   ```

4. **Use Indexes for Frequently Queried Columns**
   ```dart
   await db.execute('CREATE INDEX idx_created_at ON notes(created_at)');
   ```

5. **Batch Operations for Performance**
   ```dart
   final batch = db.batch();
   for (var note in notes) {
     batch.insert('notes', note.toMap());
   }
   await batch.commit();
   ```

---

## Quiz

**Question 1:** What's the main advantage of SQLite over Hive?
A) It's faster for all operations
B) It supports complex queries and relational data
C) It's easier to use
D) It doesn't require setup

**Question 2:** What does the `?` placeholder do in SQLite queries?
A) It's a wildcard like `*`
B) It's replaced with values from `whereArgs` to prevent SQL injection
C) It marks optional parameters
D) It indicates null values

**Question 3:** How do you handle database schema changes in sqflite?
A) Delete the old database and create a new one
B) Use the `onUpgrade` callback with version numbers
C) Manually edit the database file
D) Schema changes are automatic

---

## Exercise: Expense Tracker

Build an expense tracker app with:
1. Categories table (Food, Transport, Entertainment, etc.)
2. Expenses table with amount, description, date, category_id
3. Display total expenses by category
4. Filter expenses by date range
5. Search expenses by description

**Bonus Challenges:**
- Add recurring expenses
- Export data to CSV
- Show expense trends with charts
- Budget limits per category

---

## Summary

You've mastered SQLite in Flutter! Here's what we covered:

- **Database Setup**: Singleton pattern with DatabaseHelper
- **CRUD Operations**: Create, Read, Update, Delete
- **Type-Safe Models**: Converting between objects and maps
- **Advanced Queries**: WHERE, ORDER BY, LIMIT, JOIN, COUNT
- **Relationships**: Foreign keys and JOIN queries
- **Migrations**: Handling schema changes with onUpgrade
- **Best Practices**: Transactions, parameterized queries, indexes

SQLite gives you the power of a full relational database right on the device!

---

## Answer Key

**Answer 1:** B) It supports complex queries and relational data

SQLite excels at relational data (multiple tables with relationships) and complex queries (JOIN, GROUP BY, aggregations). Hive is faster for simple key-value operations but lacks SQL query capabilities.

**Answer 2:** B) It's replaced with values from `whereArgs` to prevent SQL injection

The `?` placeholder is a parameter marker that gets safely replaced with values from the `whereArgs` array. This prevents SQL injection attacks by properly escaping user input. Never concatenate user input directly into SQL strings!

**Answer 3:** B) Use the `onUpgrade` callback with version numbers

When you change your database schema, increment the `version` number in `openDatabase()` and handle the migration in the `onUpgrade` callback. This safely updates the database structure while preserving existing data.
