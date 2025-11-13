# Module 4, Lessons 4-5: StatefulWidget and Managing State

## The Update Problem

Right now, your apps are **static**. When you click a button, nothing changes on screen!

Try this - it WON'T work:

```dart
class CounterBroken extends StatelessWidget {
  int counter = 0;  // This won't update the UI!
  
  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Text('Count: $counter'),
        ElevatedButton(
          onPressed: () {
            counter++;  // Changes variable but UI doesn't rebuild!
            print(counter);  // Console shows it changes
          },
          child: Text('Increment'),
        ),
      ],
    );
  }
}
```

**Problem**: The screen doesn't know to rebuild!

**Solution**: **StatefulWidget** and **setState()**!

---

## Your First StatefulWidget

```dart
class Counter extends StatefulWidget {
  @override
  _CounterState createState() => _CounterState();
}

class _CounterState extends State<Counter> {
  int counter = 0;
  
  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Text('Count: $counter', style: TextStyle(fontSize: 48)),
        ElevatedButton(
          onPressed: () {
            setState(() {
              counter++;  // setState tells Flutter to rebuild!
            });
          },
          child: Text('Increment'),
        ),
      ],
    );
  }
}
```

**Now it works!** Click the button and the number updates!

---

## Understanding StatefulWidget

**Two classes work together:**

1. **Widget class** (`Counter`) - Immutable configuration
2. **State class** (`_CounterState`) - Mutable state

**Why?** Widgets rebuild often. State persists across rebuilds.

---

## The setState() Magic

```dart
setState(() {
  // Make changes here
  counter++;
  name = 'New Name';
  isVisible = !isVisible;
});
```

**What setState does:**
1. Runs the code inside  
2. Marks widget as "dirty"
3. Schedules a rebuild
4. Calls `build()` again with new values

---

## Complete Counter App

```dart
import 'package:flutter/material.dart';

void main() => runApp(MaterialApp(home: CounterApp()));

class CounterApp extends StatefulWidget {
  @override
  _CounterAppState createState() => _CounterAppState();
}

class _CounterAppState extends State<CounterApp> {
  int counter = 0;
  
  void increment() {
    setState(() {
      counter++;
    });
  }
  
  void decrement() {
    setState(() {
      counter--;
    });
  }
  
  void reset() {
    setState(() {
      counter = 0;
    });
  }
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Counter')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text('Counter Value', style: TextStyle(fontSize: 20)),
            Text('$counter', style: TextStyle(fontSize: 72, fontWeight: FontWeight.bold)),
            SizedBox(height: 30),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                FloatingActionButton(
                  onPressed: decrement,
                  child: Icon(Icons.remove),
                  heroTag: 'decrement',
                ),
                SizedBox(width: 20),
                FloatingActionButton(
                  onPressed: reset,
                  child: Icon(Icons.refresh),
                  heroTag: 'reset',
                ),
                SizedBox(width: 20),
                FloatingActionButton(
                  onPressed: increment,
                  child: Icon(Icons.add),
                  heroTag: 'increment',
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

## Todo List with StatefulWidget

```dart
class TodoList extends StatefulWidget {
  @override
  _TodoListState createState() => _TodoListState();
}

class _TodoListState extends State<TodoList> {
  List<String> todos = [];
  TextEditingController todoController = TextEditingController();
  
  void addTodo() {
    if (todoController.text.isNotEmpty) {
      setState(() {
        todos.add(todoController.text);
        todoController.clear();
      });
    }
  }
  
  void removeTodo(int index) {
    setState(() {
      todos.removeAt(index);
    });
  }
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Todo List')),
      body: Column(
        children: [
          Padding(
            padding: EdgeInsets.all(16),
            child: Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: todoController,
                    decoration: InputDecoration(
                      hintText: 'Enter todo',
                      border: OutlineInputBorder(),
                    ),
                  ),
                ),
                SizedBox(width: 10),
                ElevatedButton(
                  onPressed: addTodo,
                  child: Text('Add'),
                ),
              ],
            ),
          ),
          Expanded(
            child: ListView.builder(
              itemCount: todos.length,
              itemBuilder: (context, index) {
                return ListTile(
                  title: Text(todos[index]),
                  trailing: IconButton(
                    icon: Icon(Icons.delete, color: Colors.red),
                    onPressed: () => removeTodo(index),
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
    todoController.dispose();
    super.dispose();
  }
}
```

---

## Toggle Visibility Example

```dart
class ToggleWidget extends StatefulWidget {
  @override
  _ToggleWidgetState createState() => _ToggleWidgetState();
}

class _ToggleWidgetState extends State<ToggleWidget> {
  bool isVisible = true;
  
  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        if (isVisible)
          Container(
            width: 200,
            height: 200,
            color: Colors.blue,
            child: Center(child: Text('Visible!')),
          ),
        SizedBox(height: 20),
        ElevatedButton(
          onPressed: () {
            setState(() {
              isVisible = !isVisible;
            });
          },
          child: Text(isVisible ? 'Hide' : 'Show'),
        ),
      ],
    );
  }
}
```

---

## Lifecycle Methods

```dart
class LifecycleDemo extends StatefulWidget {
  @override
  _LifecycleDemoState createState() => _LifecycleDemoState();
}

class _LifecycleDemoState extends State<LifecycleDemo> {
  @override
  void initState() {
    super.initState();
    print('initState: Widget created');
    // Initialize data, start timers, etc.
  }
  
  @override
  void didUpdateWidget(LifecycleDemo oldWidget) {
    super.didUpdateWidget(oldWidget);
    print('didUpdateWidget: Widget updated');
  }
  
  @override
  void dispose() {
    print('dispose: Widget destroyed');
    // Clean up controllers, cancel timers, etc.
    super.dispose();
  }
  
  @override
  Widget build(BuildContext context) {
    print('build: Widget building');
    return Container();
  }
}
```

---

## ✅ YOUR CHALLENGES

### Challenge 1: Like Button
Create a button that toggles between liked (red heart) and not liked (outline heart).

### Challenge 2: Shopping Cart
Create an app with products that can be added/removed from cart. Show cart count.

### Challenge 3: Theme Switcher
Create a button that toggles between light and dark theme.

### Challenge 4: Form with Validation
Create a contact form that shows/hides error messages.

**Success Condition**: Interactive, updating UIs! ✅

---

## What Did We Learn?

- ✅ StatelessWidget for static content
- ✅ StatefulWidget for dynamic content
- ✅ setState() triggers rebuilds
- ✅ State persists across rebuilds
- ✅ Lifecycle methods (initState, dispose)
- ✅ Managing lists, toggles, counters

---

## What's Next?

**Module 5: State Management!**

setState works great for simple apps. But what about:
- Sharing data between screens?
- Complex app state?
- Better organization?

Next module: **Provider, Riverpod, and professional state management**!
