# Lesson 10.8: Testing Best Practices Mini-Project

## Project Overview

**Project Name:** TaskMaster Pro - A production-ready task management app

**What You'll Build:**
A complete Flutter task management application with:
- ✅ Comprehensive test suite (unit, widget, integration)
- ✅ 80%+ test coverage
- ✅ CI/CD pipeline with GitHub Actions
- ✅ Firebase Test Lab integration
- ✅ Automated coverage reporting
- ✅ Production-ready code quality

**Duration:** 4-6 hours

**Learning Objectives:**
By completing this project, you will:
- Apply all testing concepts from Module 10
- Build a fully tested production-ready app
- Set up complete CI/CD pipeline
- Implement best practices for maintainable tests
- Understand when to use each type of test
- Create a portfolio project demonstrating testing expertise

---

## Section 1: Project Requirements

### Functional Requirements

**TaskMaster Pro** should allow users to:

1. **Task Management**
   - Create tasks with title, description, due date
   - Mark tasks as complete/incomplete
   - Delete tasks
   - Edit existing tasks

2. **Task Organization**
   - Filter tasks (All, Active, Completed)
   - Sort tasks (by date, by priority, by title)
   - Search tasks by title

3. **Data Persistence**
   - Save tasks locally using Hive
   - Load tasks on app startup
   - Maintain state across app restarts

4. **Statistics**
   - Show total tasks count
   - Show completed tasks percentage
   - Show overdue tasks count

### Testing Requirements

**You must implement:**

1. **Unit Tests** (70% of total tests)
   - Task model validation
   - Date utilities
   - Filtering and sorting logic
   - Statistics calculations
   - Repository operations

2. **Widget Tests** (20% of total tests)
   - Task list widget
   - Task item widget
   - Filter buttons
   - Add task form
   - Statistics widget

3. **Integration Tests** (10% of total tests)
   - Complete task creation flow
   - Complete task editing flow
   - Filter and search flow
   - Delete task flow

4. **Quality Requirements**
   - Minimum 80% code coverage
   - All tests must pass
   - Linting with no warnings
   - Formatted code (dart format)

---

## Section 2: Project Setup

### Step 1: Create Flutter Project

```bash
# Create project
flutter create task_master_pro
cd task_master_pro

# Create folder structure
mkdir -p lib/models lib/services lib/widgets lib/screens lib/utils
mkdir -p test/models test/services test/widgets test/screens test/utils
mkdir -p integration_test
mkdir -p scripts
mkdir -p .github/workflows
```

### Step 2: Add Dependencies

```yaml
# pubspec.yaml
name: task_master_pro
description: A production-ready task management app with comprehensive testing

publish_to: 'none'
version: 1.0.0+1

environment:
  sdk: '>=3.0.0 <4.0.0'

dependencies:
  flutter:
    sdk: flutter

  # State management
  flutter_bloc: ^8.1.6
  equatable: ^2.0.7

  # Local storage
  hive: ^2.2.3
  hive_flutter: ^1.1.0

  # Utilities
  intl: ^0.19.0  # Date formatting
  uuid: ^4.5.1   # Generate unique IDs

dev_dependencies:
  flutter_test:
    sdk: flutter
  integration_test:
    sdk: flutter

  # Testing
  mocktail: ^1.0.4
  bloc_test: ^9.1.7

  # Code generation
  hive_generator: ^2.0.1
  build_runner: ^2.4.13

  # Linting
  flutter_lints: ^4.0.0
```

Run:
```bash
flutter pub get
```

### Step 3: Create Project Structure

```
task_master_pro/
├── lib/
│   ├── main.dart
│   ├── models/
│   │   ├── task.dart
│   │   └── task.g.dart (generated)
│   ├── repositories/
│   │   └── task_repository.dart
│   ├── bloc/
│   │   ├── task_bloc.dart
│   │   ├── task_event.dart
│   │   └── task_state.dart
│   ├── screens/
│   │   ├── home_screen.dart
│   │   └── add_edit_task_screen.dart
│   ├── widgets/
│   │   ├── task_list.dart
│   │   ├── task_item.dart
│   │   ├── filter_buttons.dart
│   │   └── statistics_widget.dart
│   └── utils/
│       └── date_utils.dart
├── test/
│   ├── models/
│   │   └── task_test.dart
│   ├── repositories/
│   │   └── task_repository_test.dart
│   ├── bloc/
│   │   └── task_bloc_test.dart
│   ├── widgets/
│   │   ├── task_list_test.dart
│   │   ├── task_item_test.dart
│   │   └── filter_buttons_test.dart
│   └── utils/
│       └── date_utils_test.dart
├── integration_test/
│   ├── app_test.dart
│   └── task_flow_test.dart
├── scripts/
│   ├── coverage.sh
│   └── run_all_tests.sh
└── .github/
    └── workflows/
        ├── ci.yml
        └── integration.yml
```

---

## Section 3: Implementation - Models and Tests

### Task Model

```dart
// lib/models/task.dart
import 'package:equatable/equatable.dart';
import 'package:hive/hive.dart';
import 'package:uuid/uuid.dart';

part 'task.g.dart';

@HiveType(typeId: 0)
class Task extends Equatable {
  @HiveField(0)
  final String id;

  @HiveField(1)
  final String title;

  @HiveField(2)
  final String description;

  @HiveField(3)
  final DateTime dueDate;

  @HiveField(4)
  final bool isCompleted;

  @HiveField(5)
  final int priority; // 1 = low, 2 = medium, 3 = high

  const Task({
    required this.id,
    required this.title,
    required this.description,
    required this.dueDate,
    this.isCompleted = false,
    this.priority = 2,
  });

  // Factory constructor with auto-generated ID
  factory Task.create({
    required String title,
    required String description,
    required DateTime dueDate,
    int priority = 2,
  }) {
    return Task(
      id: const Uuid().v4(),
      title: title,
      description: description,
      dueDate: dueDate,
      priority: priority,
    );
  }

  // Validation
  String? validate() {
    if (title.trim().isEmpty) {
      return 'Title cannot be empty';
    }
    if (title.length > 100) {
      return 'Title must be less than 100 characters';
    }
    if (priority < 1 || priority > 3) {
      return 'Priority must be between 1 and 3';
    }
    return null; // Valid
  }

  // Check if task is overdue
  bool get isOverdue {
    if (isCompleted) return false;
    return DateTime.now().isAfter(dueDate);
  }

  // Copy with
  Task copyWith({
    String? id,
    String? title,
    String? description,
    DateTime? dueDate,
    bool? isCompleted,
    int? priority,
  }) {
    return Task(
      id: id ?? this.id,
      title: title ?? this.title,
      description: description ?? this.description,
      dueDate: dueDate ?? this.dueDate,
      isCompleted: isCompleted ?? this.isCompleted,
      priority: priority ?? this.priority,
    );
  }

  @override
  List<Object?> get props =>
      [id, title, description, dueDate, isCompleted, priority];
}
```

### Unit Tests for Task Model

```dart
// test/models/task_test.dart
import 'package:flutter_test/flutter_test.dart';
import 'package:task_master_pro/models/task.dart';

void main() {
  group('Task Model', () {
    test('creates task with all properties', () {
      // Arrange
      final dueDate = DateTime(2025, 12, 31);

      // Act
      final task = Task(
        id: '123',
        title: 'Test Task',
        description: 'Test Description',
        dueDate: dueDate,
        priority: 3,
      );

      // Assert
      expect(task.id, '123');
      expect(task.title, 'Test Task');
      expect(task.description, 'Test Description');
      expect(task.dueDate, dueDate);
      expect(task.isCompleted, false);
      expect(task.priority, 3);
    });

    test('Task.create generates unique ID', () {
      // Act
      final task1 = Task.create(
        title: 'Task 1',
        description: 'Desc 1',
        dueDate: DateTime.now(),
      );

      final task2 = Task.create(
        title: 'Task 2',
        description: 'Desc 2',
        dueDate: DateTime.now(),
      );

      // Assert
      expect(task1.id, isNot(equals(task2.id)));
      expect(task1.id.isNotEmpty, true);
    });

    group('validate()', () {
      test('returns null for valid task', () {
        final task = Task.create(
          title: 'Valid Task',
          description: 'Valid Description',
          dueDate: DateTime.now(),
        );

        expect(task.validate(), null);
      });

      test('returns error for empty title', () {
        final task = Task.create(
          title: '',
          description: 'Description',
          dueDate: DateTime.now(),
        );

        expect(task.validate(), 'Title cannot be empty');
      });

      test('returns error for whitespace-only title', () {
        final task = Task.create(
          title: '   ',
          description: 'Description',
          dueDate: DateTime.now(),
        );

        expect(task.validate(), 'Title cannot be empty');
      });

      test('returns error for title over 100 characters', () {
        final task = Task.create(
          title: 'a' * 101,
          description: 'Description',
          dueDate: DateTime.now(),
        );

        expect(task.validate(), 'Title must be less than 100 characters');
      });

      test('returns error for invalid priority', () {
        final task = Task(
          id: '123',
          title: 'Task',
          description: 'Desc',
          dueDate: DateTime.now(),
          priority: 0, // Invalid
        );

        expect(task.validate(), 'Priority must be between 1 and 3');
      });
    });

    group('isOverdue', () {
      test('returns false for completed task', () {
        final task = Task(
          id: '123',
          title: 'Task',
          description: 'Desc',
          dueDate: DateTime.now().subtract(const Duration(days: 1)),
          isCompleted: true,
        );

        expect(task.isOverdue, false);
      });

      test('returns true for overdue incomplete task', () {
        final task = Task(
          id: '123',
          title: 'Task',
          description: 'Desc',
          dueDate: DateTime.now().subtract(const Duration(days: 1)),
          isCompleted: false,
        );

        expect(task.isOverdue, true);
      });

      test('returns false for future task', () {
        final task = Task(
          id: '123',
          title: 'Task',
          description: 'Desc',
          dueDate: DateTime.now().add(const Duration(days: 1)),
        );

        expect(task.isOverdue, false);
      });
    });

    group('copyWith()', () {
      test('creates copy with updated title', () {
        final task = Task.create(
          title: 'Original',
          description: 'Desc',
          dueDate: DateTime.now(),
        );

        final updated = task.copyWith(title: 'Updated');

        expect(updated.title, 'Updated');
        expect(updated.id, task.id); // ID unchanged
        expect(updated.description, task.description); // Other fields unchanged
      });

      test('creates copy with updated completion status', () {
        final task = Task.create(
          title: 'Task',
          description: 'Desc',
          dueDate: DateTime.now(),
        );

        final completed = task.copyWith(isCompleted: true);

        expect(completed.isCompleted, true);
        expect(completed.id, task.id);
      });
    });

    group('Equatable', () {
      test('tasks with same properties are equal', () {
        final dueDate = DateTime(2025, 12, 31);

        final task1 = Task(
          id: '123',
          title: 'Task',
          description: 'Desc',
          dueDate: dueDate,
        );

        final task2 = Task(
          id: '123',
          title: 'Task',
          description: 'Desc',
          dueDate: dueDate,
        );

        expect(task1, equals(task2));
      });

      test('tasks with different IDs are not equal', () {
        final dueDate = DateTime(2025, 12, 31);

        final task1 = Task(
          id: '123',
          title: 'Task',
          description: 'Desc',
          dueDate: dueDate,
        );

        final task2 = Task(
          id: '456',
          title: 'Task',
          description: 'Desc',
          dueDate: dueDate,
        );

        expect(task1, isNot(equals(task2)));
      });
    });
  });
}
```

**Run tests:**
```bash
flutter test test/models/task_test.dart
```

**Expected output:**
```
00:02 +22: All tests passed!
```

---

## Section 4: Repository and Tests

### Task Repository

```dart
// lib/repositories/task_repository.dart
import 'package:hive/hive.dart';
import 'package:task_master_pro/models/task.dart';

abstract class TaskRepository {
  Future<List<Task>> getTasks();
  Future<void> addTask(Task task);
  Future<void> updateTask(Task task);
  Future<void> deleteTask(String id);
  Future<void> deleteAllCompletedTasks();
}

class HiveTaskRepository implements TaskRepository {
  static const String boxName = 'tasks';
  late Box<Task> _box;

  Future<void> init() async {
    _box = await Hive.openBox<Task>(boxName);
  }

  @override
  Future<List<Task>> getTasks() async {
    return _box.values.toList();
  }

  @override
  Future<void> addTask(Task task) async {
    await _box.put(task.id, task);
  }

  @override
  Future<void> updateTask(Task task) async {
    await _box.put(task.id, task);
  }

  @override
  Future<void> deleteTask(String id) async {
    await _box.delete(id);
  }

  @override
  Future<void> deleteAllCompletedTasks() async {
    final completedIds = _box.values
        .where((task) => task.isCompleted)
        .map((task) => task.id)
        .toList();

    for (final id in completedIds) {
      await _box.delete(id);
    }
  }
}
```

### Unit Tests for Repository (with Mocking)

```dart
// test/repositories/task_repository_test.dart
import 'package:flutter_test/flutter_test.dart';
import 'package:task_master_pro/models/task.dart';
import 'package:task_master_pro/repositories/task_repository.dart';
import 'package:hive_flutter/hive_flutter.dart';

void main() {
  group('HiveTaskRepository', () {
    late HiveTaskRepository repository;

    setUpAll(() async {
      // Initialize Hive for testing (in-memory)
      await Hive.initFlutter();
      Hive.registerAdapter(TaskAdapter());
    });

    setUp(() async {
      repository = HiveTaskRepository();
      await repository.init();
    });

    tearDown() async {
      // Clear all tasks after each test
      final box = await Hive.openBox<Task>(HiveTaskRepository.boxName);
      await box.clear();
    });

    tearDownAll() async {
      await Hive.close();
    });

    test('getTasks returns empty list initially', () async {
      // Act
      final tasks = await repository.getTasks();

      // Assert
      expect(tasks, isEmpty);
    });

    test('addTask adds task to repository', () async {
      // Arrange
      final task = Task.create(
        title: 'Test Task',
        description: 'Test Desc',
        dueDate: DateTime.now(),
      );

      // Act
      await repository.addTask(task);
      final tasks = await repository.getTasks();

      // Assert
      expect(tasks.length, 1);
      expect(tasks.first.id, task.id);
      expect(tasks.first.title, task.title);
    });

    test('updateTask updates existing task', () async {
      // Arrange
      final task = Task.create(
        title: 'Original',
        description: 'Desc',
        dueDate: DateTime.now(),
      );
      await repository.addTask(task);

      // Act
      final updated = task.copyWith(title: 'Updated');
      await repository.updateTask(updated);
      final tasks = await repository.getTasks();

      // Assert
      expect(tasks.length, 1);
      expect(tasks.first.title, 'Updated');
    });

    test('deleteTask removes task from repository', () async {
      // Arrange
      final task = Task.create(
        title: 'Task to Delete',
        description: 'Desc',
        dueDate: DateTime.now(),
      );
      await repository.addTask(task);

      // Act
      await repository.deleteTask(task.id);
      final tasks = await repository.getTasks();

      // Assert
      expect(tasks, isEmpty);
    });

    test('deleteAllCompletedTasks removes only completed tasks', () async {
      // Arrange
      final task1 = Task.create(
        title: 'Task 1',
        description: 'Desc',
        dueDate: DateTime.now(),
      ).copyWith(isCompleted: true);

      final task2 = Task.create(
        title: 'Task 2',
        description: 'Desc',
        dueDate: DateTime.now(),
      ); // Not completed

      final task3 = Task.create(
        title: 'Task 3',
        description: 'Desc',
        dueDate: DateTime.now(),
      ).copyWith(isCompleted: true);

      await repository.addTask(task1);
      await repository.addTask(task2);
      await repository.addTask(task3);

      // Act
      await repository.deleteAllCompletedTasks();
      final tasks = await repository.getTasks();

      // Assert
      expect(tasks.length, 1);
      expect(tasks.first.id, task2.id); // Only incomplete task remains
    });
  });
}
```

---

## Section 5: Widget Tests

### Task Item Widget

```dart
// lib/widgets/task_item.dart
import 'package:flutter/material.dart';
import 'package:task_master_pro/models/task.dart';
import 'package:intl/intl.dart';

class TaskItem extends StatelessWidget {
  final Task task;
  final VoidCallback onTap;
  final ValueChanged<bool?> onToggleComplete;
  final VoidCallback onDelete;

  const TaskItem({
    super.key,
    required this.task,
    required this.onTap,
    required this.onToggleComplete,
    required this.onDelete,
  });

  @override
  Widget build(BuildContext context) {
    final dateFormat = DateFormat('MMM dd, yyyy');

    return Card(
      child: ListTile(
        leading: Checkbox(
          key: Key('checkbox_${task.id}'),
          value: task.isCompleted,
          onChanged: onToggleComplete,
        ),
        title: Text(
          task.title,
          style: TextStyle(
            decoration: task.isCompleted
                ? TextDecoration.lineThrough
                : TextDecoration.none,
          ),
        ),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(task.description),
            const SizedBox(height: 4),
            Text(
              'Due: ${dateFormat.format(task.dueDate)}',
              style: TextStyle(
                color: task.isOverdue ? Colors.red : Colors.grey,
                fontWeight: task.isOverdue ? FontWeight.bold : FontWeight.normal,
              ),
            ),
          ],
        ),
        trailing: IconButton(
          key: Key('delete_${task.id}'),
          icon: const Icon(Icons.delete, color: Colors.red),
          onPressed: onDelete,
        ),
        onTap: onTap,
      ),
    );
  }
}
```

### Widget Test for Task Item

```dart
// test/widgets/task_item_test.dart
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:task_master_pro/models/task.dart';
import 'package:task_master_pro/widgets/task_item.dart';

void main() {
  group('TaskItem Widget', () {
    late Task testTask;

    setUp(() {
      testTask = Task(
        id: '123',
        title: 'Test Task',
        description: 'Test Description',
        dueDate: DateTime(2025, 12, 31),
      );
    });

    Widget createWidgetUnderTest(Task task) {
      return MaterialApp(
        home: Scaffold(
          body: TaskItem(
            task: task,
            onTap: () {},
            onToggleComplete: (_) {},
            onDelete: () {},
          ),
        ),
      );
    }

    testWidgets('displays task title and description', (tester) async {
      // Act
      await tester.pumpWidget(createWidgetUnderTest(testTask));

      // Assert
      expect(find.text('Test Task'), findsOneWidget);
      expect(find.text('Test Description'), findsOneWidget);
    });

    testWidgets('displays formatted due date', (tester) async {
      // Act
      await tester.pumpWidget(createWidgetUnderTest(testTask));

      // Assert
      expect(find.textContaining('Due: Dec 31, 2025'), findsOneWidget);
    });

    testWidgets('checkbox reflects completion status', (tester) async {
      // Arrange
      final completedTask = testTask.copyWith(isCompleted: true);

      // Act
      await tester.pumpWidget(createWidgetUnderTest(completedTask));
      final checkbox = tester.widget<Checkbox>(
        find.byKey(Key('checkbox_${completedTask.id}')),
      );

      // Assert
      expect(checkbox.value, true);
    });

    testWidgets('completed task has strikethrough text', (tester) async {
      // Arrange
      final completedTask = testTask.copyWith(isCompleted: true);

      // Act
      await tester.pumpWidget(createWidgetUnderTest(completedTask));
      final textWidget = tester.widget<Text>(find.text('Test Task'));

      // Assert
      expect(
        textWidget.style?.decoration,
        TextDecoration.lineThrough,
      );
    });

    testWidgets('tapping checkbox calls onToggleComplete', (tester) async {
      // Arrange
      bool toggleCalled = false;
      bool? toggledValue;

      final widget = MaterialApp(
        home: Scaffold(
          body: TaskItem(
            task: testTask,
            onTap: () {},
            onToggleComplete: (value) {
              toggleCalled = true;
              toggledValue = value;
            },
            onDelete: () {},
          ),
        ),
      );

      // Act
      await tester.pumpWidget(widget);
      await tester.tap(find.byKey(Key('checkbox_${testTask.id}')));

      // Assert
      expect(toggleCalled, true);
      expect(toggledValue, true); // Toggled from false to true
    });

    testWidgets('tapping delete button calls onDelete', (tester) async {
      // Arrange
      bool deleteCalled = false;

      final widget = MaterialApp(
        home: Scaffold(
          body: TaskItem(
            task: testTask,
            onTap: () {},
            onToggleComplete: (_) {},
            onDelete: () {
              deleteCalled = true;
            },
          ),
        ),
      );

      // Act
      await tester.pumpWidget(widget);
      await tester.tap(find.byKey(Key('delete_${testTask.id}')));

      // Assert
      expect(deleteCalled, true);
    });

    testWidgets('overdue task shows due date in red', (tester) async {
      // Arrange
      final overdueTask = testTask.copyWith(
        dueDate: DateTime.now().subtract(const Duration(days: 1)),
      );

      // Act
      await tester.pumpWidget(createWidgetUnderTest(overdueTask));

      // Find the due date text
      final dueDateFinder = find.textContaining('Due:');
      final textWidget = tester.widget<Text>(dueDateFinder);

      // Assert
      expect(textWidget.style?.color, Colors.red);
      expect(textWidget.style?.fontWeight, FontWeight.bold);
    });
  });
}
```

---

## Section 6: Integration Tests

```dart
// integration_test/task_flow_test.dart
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:task_master_pro/main.dart' as app;

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  group('Task Flow Integration Tests', () {
    testWidgets('Complete task lifecycle: Create, Complete, Delete',
        (WidgetTester tester) async {
      // ARRANGE: Start the app
      app.main();
      await tester.pumpAndSettle();

      // STEP 1: Verify empty state
      expect(find.text('No tasks yet'), findsOneWidget);

      // STEP 2: Create a new task
      await tester.tap(find.byKey(const Key('addTaskButton')));
      await tester.pumpAndSettle();

      // Fill in task details
      await tester.enterText(
        find.byKey(const Key('titleField')),
        'Buy groceries',
      );
      await tester.enterText(
        find.byKey(const Key('descriptionField')),
        'Milk, eggs, bread',
      );
      await tester.pumpAndSettle();

      // Select due date (tap date picker)
      await tester.tap(find.byKey(const Key('dueDatePicker')));
      await tester.pumpAndSettle();
      await tester.tap(find.text('15')); // Select 15th of month
      await tester.tap(find.text('OK'));
      await tester.pumpAndSettle();

      // Save task
      await tester.tap(find.byKey(const Key('saveTaskButton')));
      await tester.pumpAndSettle();

      // ASSERT: Task appears in list
      expect(find.text('Buy groceries'), findsOneWidget);
      expect(find.text('Milk, eggs, bread'), findsOneWidget);
      expect(find.text('No tasks yet'), findsNothing);

      // STEP 3: Mark task as complete
      await tester.tap(find.byKey(const Key('checkbox_0')));
      await tester.pumpAndSettle();

      // ASSERT: Task shows as completed
      final titleText = tester.widget<Text>(find.text('Buy groceries'));
      expect(titleText.style?.decoration, TextDecoration.lineThrough);

      // STEP 4: Delete task
      await tester.tap(find.byKey(const Key('delete_0')));
      await tester.pumpAndSettle();

      // Confirm deletion in dialog
      await tester.tap(find.text('Delete'));
      await tester.pumpAndSettle();

      // ASSERT: Back to empty state
      expect(find.text('Buy groceries'), findsNothing);
      expect(find.text('No tasks yet'), findsOneWidget);
    });

    testWidgets('Filter tasks by status', (WidgetTester tester) async {
      app.main();
      await tester.pumpAndSettle();

      // Create completed task
      await createTask(tester, 'Completed Task', 'Done', true);

      // Create incomplete task
      await createTask(tester, 'Incomplete Task', 'Not done', false);

      // Verify both tasks are visible in "All" filter
      expect(find.text('Completed Task'), findsOneWidget);
      expect(find.text('Incomplete Task'), findsOneWidget);

      // Filter to show only active tasks
      await tester.tap(find.text('Active'));
      await tester.pumpAndSettle();

      // ASSERT: Only incomplete task shown
      expect(find.text('Completed Task'), findsNothing);
      expect(find.text('Incomplete Task'), findsOneWidget);

      // Filter to show only completed tasks
      await tester.tap(find.text('Completed'));
      await tester.pumpAndSettle();

      // ASSERT: Only completed task shown
      expect(find.text('Completed Task'), findsOneWidget);
      expect(find.text('Incomplete Task'), findsNothing);

      // Show all tasks again
      await tester.tap(find.text('All'));
      await tester.pumpAndSettle();

      // ASSERT: Both tasks visible
      expect(find.text('Completed Task'), findsOneWidget);
      expect(find.text('Incomplete Task'), findsOneWidget);
    });
  });
}

// Helper function to create a task
Future<void> createTask(
  WidgetTester tester,
  String title,
  String description,
  bool markComplete,
) async {
  await tester.tap(find.byKey(const Key('addTaskButton')));
  await tester.pumpAndSettle();

  await tester.enterText(find.byKey(const Key('titleField')), title);
  await tester.enterText(
    find.byKey(const Key('descriptionField')),
    description,
  );
  await tester.pumpAndSettle();

  // Select tomorrow as due date
  await tester.tap(find.byKey(const Key('dueDatePicker')));
  await tester.pumpAndSettle();
  await tester.tap(find.text('15'));
  await tester.tap(find.text('OK'));
  await tester.pumpAndSettle();

  await tester.tap(find.byKey(const Key('saveTaskButton')));
  await tester.pumpAndSettle();

  if (markComplete) {
    // Find the task's checkbox and tap it
    final checkboxFinder = find.descendant(
      of: find.text(title),
      matching: find.byType(Checkbox),
    );
    await tester.tap(checkboxFinder);
    await tester.pumpAndSettle();
  }
}
```

---

## Section 7: CI/CD Setup

### GitHub Actions Workflow

Create `.github/workflows/ci.yml`:

```yaml
name: TaskMaster Pro CI

on:
  pull_request:
    branches: [ main ]
  push:
    branches: [ main ]

jobs:
  test:
    name: Test and Coverage
    runs-on: ubuntu-latest
    timeout-minutes: 20

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Setup Flutter
        uses: subosito/flutter-action@v2
        with:
          flutter-version: '3.24.0'
          channel: 'stable'
          cache: true

      - name: Install dependencies
        run: flutter pub get

      - name: Generate Hive adapters
        run: flutter pub run build_runner build --delete-conflicting-outputs

      - name: Verify code formatting
        run: dart format --set-exit-if-changed .

      - name: Analyze code
        run: flutter analyze --fatal-infos

      - name: Run unit and widget tests with coverage
        run: flutter test --coverage --no-test-assets

      - name: Install lcov
        run: sudo apt-get install -y lcov

      - name: Clean coverage data
        run: |
          lcov --remove coverage/lcov.info \
            '*.g.dart' \
            '*.freezed.dart' \
            -o coverage/lcov_cleaned.info

      - name: Check coverage threshold (80%)
        run: |
          COVERAGE=$(lcov --summary coverage/lcov_cleaned.info 2>&1 | \
            grep 'lines......:' | \
            grep -oP '\d+\.\d+(?=%)')

          echo "Coverage: ${COVERAGE}%"

          if (( $(echo "$COVERAGE < 80" | bc -l) )); then
            echo "❌ Coverage ${COVERAGE}% is below 80% threshold"
            exit 1
          else
            echo "✅ Coverage meets 80% threshold"
          fi

      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          files: ./coverage/lcov_cleaned.info
          fail_ci_if_error: false

      - name: Generate HTML coverage report
        run: |
          genhtml coverage/lcov_cleaned.info -o coverage/html

      - name: Upload coverage HTML as artifact
        uses: actions/upload-artifact@v4
        with:
          name: coverage-report
          path: coverage/html/

      - name: Build APK
        run: flutter build apk --debug
```

---

## Section 8: Running and Verifying Tests

### Script: scripts/run_all_tests.sh

```bash
#!/bin/bash
set -e

echo "🧪 TaskMaster Pro - Running All Tests"
echo "======================================"

echo ""
echo "📋 Step 1: Running unit and widget tests..."
flutter test --no-test-assets

echo ""
echo "📊 Step 2: Running tests with coverage..."
flutter test --coverage --no-test-assets

echo ""
echo "🧹 Step 3: Cleaning coverage data..."
lcov --remove coverage/lcov.info \
  '*.g.dart' \
  -o coverage/lcov_cleaned.info

echo ""
echo "📈 Step 4: Coverage summary:"
lcov --summary coverage/lcov_cleaned.info

COVERAGE=$(lcov --summary coverage/lcov_cleaned.info 2>&1 | \
  grep 'lines......:' | \
  grep -oP '\d+\.\d+(?=%)')

echo ""
echo "Coverage: ${COVERAGE}%"

if (( $(echo "$COVERAGE < 80" | bc -l) )); then
  echo "❌ Coverage is below 80% threshold"
  exit 1
else
  echo "✅ Coverage meets 80% threshold"
fi

echo ""
echo "🔗 Step 5: Generating HTML report..."
genhtml coverage/lcov_cleaned.info -o coverage/html

echo ""
echo "🚀 Step 6: Running integration tests..."
flutter test integration_test/

echo ""
echo "✅ All tests passed!"
echo "📄 View coverage report: open coverage/html/index.html"
```

Make it executable:
```bash
chmod +x scripts/run_all_tests.sh
```

Run it:
```bash
./scripts/run_all_tests.sh
```

---

## Section 9: Evaluation Criteria

### Grading Rubric (100 points)

**Implementation (40 points)**
- ✅ All functional requirements met (20 pts)
- ✅ Code follows Flutter best practices (10 pts)
- ✅ No linting warnings (5 pts)
- ✅ Code properly formatted (5 pts)

**Unit Tests (25 points)**
- ✅ Task model fully tested (10 pts)
- ✅ Repository fully tested (10 pts)
- ✅ Tests follow AAA pattern (5 pts)

**Widget Tests (15 points)**
- ✅ TaskItem widget fully tested (8 pts)
- ✅ Filter buttons tested (7 pts)

**Integration Tests (10 points)**
- ✅ Complete task lifecycle tested (5 pts)
- ✅ Filter flow tested (5 pts)

**Coverage (10 points)**
- ✅ 80%+ coverage (10 pts)
- ✅ 70-79% coverage (7 pts)
- ✅ 60-69% coverage (5 pts)
- ❌ <60% coverage (0 pts)

---

## Summary

In this mini-project, you built:

✅ **Production-ready task management app** with full CRUD operations
✅ **Comprehensive test suite** covering unit, widget, and integration tests
✅ **80%+ test coverage** with automated coverage reporting
✅ **CI/CD pipeline** with GitHub Actions
✅ **Quality gates** enforcing code standards
✅ **Automated testing** on every push and PR
✅ **Best practices** demonstrated throughout

**Key Takeaways:**

1. **Test Pyramid**: 70% unit tests, 20% widget tests, 10% integration tests
2. **TDD mindset**: Write tests as you develop, not after
3. **Quality gates**: Prevent bad code from merging
4. **Coverage ≠ Quality**: 80% coverage of meaningful code beats 100% of trivial code
5. **CI/CD**: Automate everything to catch issues early

**Portfolio Value:**

This project demonstrates professional-level testing practices employers look for:
- Comprehensive test coverage
- Clean, maintainable test code
- CI/CD pipeline setup
- Quality-first development approach

**Congratulations!** You've completed Module 10 and built a fully tested, production-ready Flutter application with industry-standard testing practices. 🎉

---

## What's Next?

In **Module 11: Deployment & Publishing**, you'll learn how to prepare this app for release, build signed APKs and IPAs, and publish it to the Google Play Store and Apple App Store!
