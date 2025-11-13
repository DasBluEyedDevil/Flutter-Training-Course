# Module 5, Lesson 3: Introduction to Riverpod

## Why Riverpod?

Provider is great, but has limitations:
- Runtime errors (easy to forget providers)
- Hard to test
- Context required everywhere
- No compile-time safety

**Riverpod solves all of these!**

Think of it as "Provider 2.0" - same author, better design.

---

## Key Advantages

1. **No BuildContext needed** - access state from anywhere
2. **Compile-time safe** - errors caught before runtime
3. **Easy testing** - no widget tree needed
4. **Better performance** - automatic disposal
5. **DevTools integration** - amazing debugging

---

## Installation

```yaml
# pubspec.yaml
dependencies:
  flutter:
    sdk: flutter
  flutter_riverpod: ^2.5.1
```

Run: `flutter pub get`

---

## Setup Difference

**Provider:**
```dart
void main() {
  runApp(
    ChangeNotifierProvider(
      create: (_) => Counter(),
      child: MyApp(),
    ),
  );
}
```

**Riverpod:**
```dart
void main() {
  runApp(
    ProviderScope(  // One wrapper for ALL providers!
      child: MyApp(),
    ),
  );
}
```

**Much cleaner!** One `ProviderScope` at the root, done.

---

## Your First Riverpod Provider

```dart
import 'package:flutter_riverpod/flutter_riverpod.dart';

// Define provider OUTSIDE the widget tree
final counterProvider = StateProvider<int>((ref) => 0);

// Now ANY widget can access it!
```

**Key difference**: Providers are global constants, not widget tree dependencies.

---

## Reading State

**Provider way:**
```dart
class CounterDisplay extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final counter = Provider.of<Counter>(context);
    return Text('${counter.count}');
  }
}
```

**Riverpod way:**
```dart
class CounterDisplay extends ConsumerWidget {  // Note: ConsumerWidget
  @override
  Widget build(BuildContext context, WidgetRef ref) {  // Note: WidgetRef
    final count = ref.watch(counterProvider);
    return Text('$count');
  }
}
```

---

## Complete Counter Example

```dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

// 1. Define provider
final counterProvider = StateProvider<int>((ref) => 0);

// 2. Main with ProviderScope
void main() {
  runApp(
    ProviderScope(
      child: MyApp(),
    ),
  );
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: CounterScreen(),
    );
  }
}

// 3. ConsumerWidget to access state
class CounterScreen extends ConsumerWidget {
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final count = ref.watch(counterProvider);

    return Scaffold(
      appBar: AppBar(title: Text('Riverpod Counter')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text('Count:', style: TextStyle(fontSize: 24)),
            Text(
              '$count',
              style: TextStyle(fontSize: 72, fontWeight: FontWeight.bold),
            ),
            SizedBox(height: 20),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                FloatingActionButton(
                  onPressed: () {
                    ref.read(counterProvider.notifier).state--;
                  },
                  child: Icon(Icons.remove),
                ),
                SizedBox(width: 20),
                FloatingActionButton(
                  onPressed: () {
                    ref.read(counterProvider.notifier).state++;
                  },
                  child: Icon(Icons.add),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
```

---

## Provider Types in Riverpod

### 1. StateProvider (Simple Values)

```dart
// For simple state (int, String, bool)
final nameProvider = StateProvider<String>((ref) => 'John');

// Read
final name = ref.watch(nameProvider);

// Update
ref.read(nameProvider.notifier).state = 'Jane';
```

### 2. StateNotifierProvider (Complex State)

```dart
// State class
class Counter extends StateNotifier<int> {
  Counter() : super(0);  // Initial state

  void increment() => state++;
  void decrement() => state--;
  void reset() => state = 0;
}

// Provider
final counterProvider = StateNotifierProvider<Counter, int>((ref) {
  return Counter();
});

// Usage
final count = ref.watch(counterProvider);
ref.read(counterProvider.notifier).increment();
```

### 3. FutureProvider (Async Data)

```dart
// Fetch data from API
final userProvider = FutureProvider<User>((ref) async {
  final response = await http.get('https://api.example.com/user');
  return User.fromJson(jsonDecode(response.body));
});

// Usage
class UserProfile extends ConsumerWidget {
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final userAsync = ref.watch(userProvider);

    return userAsync.when(
      data: (user) => Text('Hello ${user.name}'),
      loading: () => CircularProgressIndicator(),
      error: (error, stack) => Text('Error: $error'),
    );
  }
}
```

### 4. StreamProvider (Real-Time Data)

```dart
// Real-time updates
final messagesProvider = StreamProvider<List<Message>>((ref) {
  return FirebaseFirestore.instance
      .collection('messages')
      .snapshots()
      .map((snapshot) => snapshot.docs.map((doc) => Message.fromDoc(doc)).toList());
});

// Usage
final messagesAsync = ref.watch(messagesProvider);

messagesAsync.when(
  data: (messages) => ListView.builder(
    itemCount: messages.length,
    itemBuilder: (context, index) => MessageTile(messages[index]),
  ),
  loading: () => CircularProgressIndicator(),
  error: (error, stack) => Text('Error: $error'),
);
```

---

## Real Todo App with Riverpod

```dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

// Models
class Todo {
  final String id;
  final String title;
  final bool completed;

  Todo({
    required this.id,
    required this.title,
    this.completed = false,
  });

  Todo copyWith({String? id, String? title, bool? completed}) {
    return Todo(
      id: id ?? this.id,
      title: title ?? this.title,
      completed: completed ?? this.completed,
    );
  }
}

// State Notifier
class TodosNotifier extends StateNotifier<List<Todo>> {
  TodosNotifier() : super([]);

  void addTodo(String title) {
    state = [
      ...state,
      Todo(
        id: DateTime.now().toString(),
        title: title,
      ),
    ];
  }

  void toggleTodo(String id) {
    state = [
      for (final todo in state)
        if (todo.id == id)
          todo.copyWith(completed: !todo.completed)
        else
          todo,
    ];
  }

  void removeTodo(String id) {
    state = state.where((todo) => todo.id != id).toList();
  }
}

// Provider
final todosProvider = StateNotifierProvider<TodosNotifier, List<Todo>>((ref) {
  return TodosNotifier();
});

// Filtered todos (computed state!)
enum TodoFilter { all, active, completed }

final todoFilterProvider = StateProvider<TodoFilter>((ref) => TodoFilter.all);

final filteredTodosProvider = Provider<List<Todo>>((ref) {
  final filter = ref.watch(todoFilterProvider);
  final todos = ref.watch(todosProvider);

  switch (filter) {
    case TodoFilter.active:
      return todos.where((todo) => !todo.completed).toList();
    case TodoFilter.completed:
      return todos.where((todo) => todo.completed).toList();
    case TodoFilter.all:
    default:
      return todos;
  }
});

// Main
void main() {
  runApp(
    ProviderScope(
      child: MaterialApp(home: TodoApp()),
    ),
  );
}

// UI
class TodoApp extends ConsumerWidget {
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final todos = ref.watch(filteredTodosProvider);
    final filter = ref.watch(todoFilterProvider);

    return Scaffold(
      appBar: AppBar(
        title: Text('Riverpod Todos'),
        actions: [
          DropdownButton<TodoFilter>(
            value: filter,
            items: [
              DropdownMenuItem(value: TodoFilter.all, child: Text('All')),
              DropdownMenuItem(value: TodoFilter.active, child: Text('Active')),
              DropdownMenuItem(value: TodoFilter.completed, child: Text('Completed')),
            ],
            onChanged: (value) {
              if (value != null) {
                ref.read(todoFilterProvider.notifier).state = value;
              }
            },
          ),
        ],
      ),
      body: Column(
        children: [
          AddTodoField(),
          Expanded(
            child: todos.isEmpty
                ? Center(child: Text('No todos'))
                : ListView.builder(
                    itemCount: todos.length,
                    itemBuilder: (context, index) {
                      final todo = todos[index];
                      return TodoTile(todo: todo);
                    },
                  ),
          ),
        ],
      ),
    );
  }
}

class AddTodoField extends ConsumerStatefulWidget {
  @override
  _AddTodoFieldState createState() => _AddTodoFieldState();
}

class _AddTodoFieldState extends ConsumerState<AddTodoField> {
  final controller = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsets.all(16),
      child: Row(
        children: [
          Expanded(
            child: TextField(
              controller: controller,
              decoration: InputDecoration(
                hintText: 'Enter todo',
                border: OutlineInputBorder(),
              ),
              onSubmitted: (value) {
                if (value.isNotEmpty) {
                  ref.read(todosProvider.notifier).addTodo(value);
                  controller.clear();
                }
              },
            ),
          ),
          SizedBox(width: 8),
          ElevatedButton(
            onPressed: () {
              if (controller.text.isNotEmpty) {
                ref.read(todosProvider.notifier).addTodo(controller.text);
                controller.clear();
              }
            },
            child: Text('Add'),
          ),
        ],
      ),
    );
  }

  @override
  void dispose() {
    controller.dispose();
    super.dispose();
  }
}

class TodoTile extends ConsumerWidget {
  final Todo todo;

  TodoTile({required this.todo});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return ListTile(
      leading: Checkbox(
        value: todo.completed,
        onChanged: (_) {
          ref.read(todosProvider.notifier).toggleTodo(todo.id);
        },
      ),
      title: Text(
        todo.title,
        style: TextStyle(
          decoration: todo.completed ? TextDecoration.lineThrough : null,
        ),
      ),
      trailing: IconButton(
        icon: Icon(Icons.delete, color: Colors.red),
        onPressed: () {
          ref.read(todosProvider.notifier).removeTodo(todo.id);
        },
      ),
    );
  }
}
```

---

## Key Differences: Provider vs Riverpod

| Feature | Provider | Riverpod |
|---------|----------|----------|
| **Setup** | Wrap with providers | One ProviderScope |
| **Context** | Required | Not required |
| **Widget Type** | StatelessWidget | ConsumerWidget |
| **Read State** | `context.watch()` | `ref.watch()` |
| **Update State** | `context.read()` | `ref.read()` |
| **Safety** | Runtime errors | Compile-time safe |
| **Testing** | Need widget tree | Direct access |
| **Provider Location** | Widget tree | Global constants |

---

## ref.watch vs ref.read vs ref.listen

```dart
// ref.watch - Rebuilds when state changes
final count = ref.watch(counterProvider);

// ref.read - Get current value, doesn't rebuild
final count = ref.read(counterProvider);

// ref.listen - Execute callback when state changes
ref.listen<int>(counterProvider, (previous, next) {
  print('Count changed from $previous to $next');
  if (next > 10) {
    showDialog(...);
  }
});
```

---

## ConsumerWidget vs Consumer

```dart
// ConsumerWidget - Entire widget rebuilds
class MyWidget extends ConsumerWidget {
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final count = ref.watch(counterProvider);
    return Text('$count');
  }
}

// Consumer - Only wrapped part rebuilds
class MyWidget extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Text('Static text'),  // Never rebuilds
        Consumer(
          builder: (context, ref, child) {
            final count = ref.watch(counterProvider);
            return Text('$count');  // Only this rebuilds
          },
        ),
      ],
    );
  }
}
```

---

## ✅ YOUR CHALLENGE: Shopping Cart with Riverpod

Convert the Provider shopping cart from Lesson 2 to Riverpod:
1. Use StateNotifierProvider for cart
2. FutureProvider for products
3. Computed provider for totals
4. Filter provider for categories

**Success Condition**: Full cart functionality with Riverpod! ✅

---

## Common Mistakes

❌ **Mistake 1**: Forgetting ProviderScope
```dart
void main() {
  runApp(MyApp());  // Will crash!
}
```

✅ **Fix**: Always wrap with ProviderScope
```dart
void main() {
  runApp(
    ProviderScope(child: MyApp()),
  );
}
```

❌ **Mistake 2**: Using StatelessWidget instead of ConsumerWidget
```dart
class MyWidget extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final count = ref.watch(counterProvider);  // Error: ref not defined!
    return Text('$count');
  }
}
```

✅ **Fix**: Use ConsumerWidget
```dart
class MyWidget extends ConsumerWidget {
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final count = ref.watch(counterProvider);
    return Text('$count');
  }
}
```

---

## What Did We Learn?

- ✅ Riverpod setup with ProviderScope
- ✅ ConsumerWidget for accessing state
- ✅ StateProvider for simple values
- ✅ StateNotifierProvider for complex state
- ✅ FutureProvider and StreamProvider
- ✅ Computed providers
- ✅ ref.watch vs ref.read vs ref.listen
- ✅ Complete todo app with filtering

---

## What's Next?

You've learned Riverpod basics! Next: **Advanced Riverpod patterns** - family modifiers, autoDispose, combining providers, and more!
