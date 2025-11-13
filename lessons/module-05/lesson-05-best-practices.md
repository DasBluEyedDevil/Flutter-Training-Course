# Module 5, Lesson 5: State Management Best Practices

## The Big Picture

You've learned setState, Provider, and Riverpod. But which one should you use? **It depends!**

This lesson covers:
- When to use each approach
- Architecture patterns
- Common pitfalls
- Testing strategies
- Real-world decision making

---

## The State Management Ladder

Think of state management as a ladder - climb as high as you need:

### Level 1: setState (Local State)
**Use for:**
- Single widget state
- UI-only state (toggles, animations)
- Temporary state

**Example:** Expanding/collapsing a card, show/hide password

```dart
class ExpandableCard extends StatefulWidget {
  @override
  _ExpandableCardState createState() => _ExpandableCardState();
}

class _ExpandableCardState extends State<ExpandableCard> {
  bool isExpanded = false;  // Local to this widget only

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Column(
        children: [
          ListTile(
            title: Text('Title'),
            trailing: IconButton(
              icon: Icon(isExpanded ? Icons.expand_less : Icons.expand_more),
              onPressed: () {
                setState(() {
                  isExpanded = !isExpanded;
                });
              },
            ),
          ),
          if (isExpanded) Text('Expanded content'),
        ],
      ),
    );
  }
}
```

✅ **Perfect for this!** State doesn't need to be shared.

---

### Level 2: InheritedWidget (Prop Drilling Solution)
**Use for:**
- Passing data down the tree
- Avoiding constructor parameters through many levels

**Flutter's built-in examples:**
- `Theme.of(context)`
- `MediaQuery.of(context)`
- `Navigator.of(context)`

❌ **You probably won't create these manually** - Provider/Riverpod do this for you!

---

### Level 3: Provider (Shared State)
**Use for:**
- State shared across multiple screens
- Shopping cart, favorites, user preferences
- Medium-sized apps
- When you need ChangeNotifier patterns

```dart
class CartProvider with ChangeNotifier {
  List<Item> _items = [];

  void addItem(Item item) {
    _items.add(item);
    notifyListeners();
  }
}

// Access anywhere in the app
final cart = context.watch<CartProvider>();
```

✅ **Great for:** Medium apps, teams familiar with Provider, gradual migration

---

### Level 4: Riverpod (Modern State Management)
**Use for:**
- Large apps with complex state
- Apps requiring testability
- When you want compile-time safety
- New projects (best modern choice)

```dart
final cartProvider = StateNotifierProvider<CartNotifier, List<Item>>((ref) {
  return CartNotifier();
});

// Access from anywhere, no context needed
final cart = ref.watch(cartProvider);
```

✅ **Great for:** New apps, large codebases, teams wanting best practices

---

### Level 5: BLoC (Business Logic Component)
**Use for:**
- Enterprise apps
- Strict separation of business logic and UI
- Complex event-driven flows
- When required by company standards

```dart
class CartBloc extends Bloc<CartEvent, CartState> {
  CartBloc() : super(CartInitial()) {
    on<AddToCart>(_onAddToCart);
  }

  void _onAddToCart(AddToCart event, Emitter<CartState> emit) {
    emit(CartUpdated(items: [...state.items, event.item]));
  }
}
```

✅ **Great for:** Large enterprise apps, teams with BLoC experience, event-driven architecture

---

## Decision Tree

```
Is the state only used in ONE widget?
├─ Yes → Use setState
└─ No  → Is it shared across 2-3 widgets nearby?
    ├─ Yes → Pass via constructor OR use setState in parent
    └─ No  → Is it app-wide state (cart, auth, theme)?
        ├─ Yes → Use Provider or Riverpod
        └─ How complex is your app?
            ├─ Medium  → Provider
            ├─ Large   → Riverpod
            └─ Enterprise with strict patterns → BLoC
```

---

## Real-World Examples

### Example 1: E-Commerce App

**Problem:** Shopping cart, user auth, product catalog, favorites

**Solution:**
```dart
// Riverpod approach
final authProvider = StateNotifierProvider<AuthNotifier, AuthState>(...);
final cartProvider = StateNotifierProvider<CartNotifier, CartState>(...);
final productsProvider = FutureProvider<List<Product>>(...);
final favoritesProvider = StateNotifierProvider<FavoritesNotifier, Set<String>>(...);
```

**Why Riverpod?**
- Multiple state objects that interact
- Need testability
- FutureProvider for async data
- Clean separation of concerns

---

### Example 2: Todo App

**Problem:** List of todos, filtering, persistence

**Solution (Small app):**
```dart
// Provider approach
class TodoProvider with ChangeNotifier {
  List<Todo> _todos = [];
  TodoFilter _filter = TodoFilter.all;

  List<Todo> get filteredTodos { /* ... */ }

  void addTodo(Todo todo) {
    _todos.add(todo);
    notifyListeners();
  }
}
```

**Why Provider?**
- Simple app with one main state object
- Quick to set up
- Easy to understand for beginners

---

### Example 3: Social Media Feed

**Problem:** Posts, comments, likes, users, real-time updates

**Solution:**
```dart
// Riverpod with StreamProvider
final postsStreamProvider = StreamProvider<List<Post>>((ref) {
  return FirebaseFirestore.instance
      .collection('posts')
      .snapshots()
      .map((snapshot) => /* parse */);
});

final commentsProvider = StreamProvider.family<List<Comment>, String>((ref, postId) {
  return FirebaseFirestore.instance
      .collection('posts')
      .doc(postId)
      .collection('comments')
      .snapshots()
      .map((snapshot) => /* parse */);
});

final likesProvider = StateNotifierProvider.family<LikesNotifier, bool, String>((ref, postId) {
  return LikesNotifier(postId);
});
```

**Why Riverpod?**
- Real-time data with StreamProvider
- Parameterized providers with .family
- Complex dependencies between providers
- Automatic disposal with .autoDispose

---

## Architecture Patterns

### Pattern 1: MVVM (Model-View-ViewModel)

**Model**: Data classes
```dart
class User {
  final String id;
  final String name;
  final String email;

  User({required this.id, required this.name, required this.email});
}
```

**View**: UI widgets
```dart
class UserProfile extends ConsumerWidget {
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final userAsync = ref.watch(userViewModelProvider);
    return userAsync.when(
      data: (user) => Text(user.name),
      loading: () => CircularProgressIndicator(),
      error: (err, stack) => Text('Error'),
    );
  }
}
```

**ViewModel**: Business logic (Provider/Riverpod)
```dart
final userViewModelProvider = FutureProvider<User>((ref) async {
  final userId = ref.watch(authProvider);
  return await fetchUser(userId);
});
```

---

### Pattern 2: Repository Pattern

Separate data sources from business logic:

```dart
// Data layer
class UserRepository {
  Future<User> getUser(String id) async {
    final response = await http.get('api.example.com/users/$id');
    return User.fromJson(jsonDecode(response.body));
  }

  Future<void> updateUser(User user) async {
    await http.put('api.example.com/users/${user.id}', body: user.toJson());
  }
}

// Providers
final userRepositoryProvider = Provider<UserRepository>((ref) {
  return UserRepository();
});

final userProvider = FutureProvider.family<User, String>((ref, userId) async {
  final repository = ref.read(userRepositoryProvider);
  return await repository.getUser(userId);
});

// UI
class UserWidget extends ConsumerWidget {
  final String userId;

  UserWidget({required this.userId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final userAsync = ref.watch(userProvider(userId));
    return userAsync.when(
      data: (user) => Text(user.name),
      loading: () => CircularProgressIndicator(),
      error: (err, stack) => Text('Error: $err'),
    );
  }
}
```

**Benefits:**
- Easy to swap data sources (API → local DB)
- Testable (mock repository)
- Clean separation

---

## Common Pitfalls

### Pitfall 1: Over-Engineering

❌ **Bad**: Using Riverpod for a simple counter
```dart
final counterProvider = StateNotifierProvider<CounterNotifier, int>((ref) {
  return CounterNotifier();
});

class CounterNotifier extends StateNotifier<int> {
  CounterNotifier() : super(0);
  void increment() => state++;
}
```

✅ **Good**: Just use setState!
```dart
class Counter extends StatefulWidget {
  @override
  _CounterState createState() => _CounterState();
}

class _CounterState extends State<Counter> {
  int count = 0;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Text('$count'),
        ElevatedButton(
          onPressed: () => setState(() => count++),
          child: Text('Increment'),
        ),
      ],
    );
  }
}
```

**Rule**: Use the simplest solution that works!

---

### Pitfall 2: Not Using Computed State

❌ **Bad**: Duplicating state logic everywhere
```dart
class CartScreen extends ConsumerWidget {
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final cart = ref.watch(cartProvider);
    final total = cart.items.fold(0.0, (sum, item) => sum + item.price * item.quantity);

    return Text('Total: \$$total');
  }
}

class CheckoutScreen extends ConsumerWidget {
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final cart = ref.watch(cartProvider);
    final total = cart.items.fold(0.0, (sum, item) => sum + item.price * item.quantity);
    // Duplicated logic!

    return Text('Total: \$$total');
  }
}
```

✅ **Good**: Create a computed provider
```dart
final cartTotalProvider = Provider<double>((ref) {
  final cart = ref.watch(cartProvider);
  return cart.items.fold(0.0, (sum, item) => sum + item.price * item.quantity);
});

// Usage
final total = ref.watch(cartTotalProvider);
```

---

### Pitfall 3: Storing UI State in Global Provider

❌ **Bad**: Storing temporary UI state globally
```dart
final isMenuOpenProvider = StateProvider<bool>((ref) => false);

// This means ALL widgets see the same menu state!
```

✅ **Good**: Use local state
```dart
class MyWidget extends StatefulWidget {
  @override
  _MyWidgetState createState() => _MyWidgetState();
}

class _MyWidgetState extends State<MyWidget> {
  bool isMenuOpen = false;

  @override
  Widget build(BuildContext context) {
    // Menu state is local to this widget
  }
}
```

**Rule**: Global state for global data, local state for local UI!

---

## Testing State Management

### Testing setState
```dart
testWidgets('Counter increments', (tester) async {
  await tester.pumpWidget(MaterialApp(home: CounterWidget()));

  expect(find.text('0'), findsOneWidget);

  await tester.tap(find.byIcon(Icons.add));
  await tester.pump();

  expect(find.text('1'), findsOneWidget);
});
```

### Testing Riverpod
```dart
test('Cart adds items correctly', () {
  final container = ProviderContainer();
  addTearDown(container.dispose);

  final cartNotifier = container.read(cartProvider.notifier);

  cartNotifier.addItem(Item(name: 'Laptop', price: 999));

  expect(container.read(cartProvider).length, 1);
  expect(container.read(cartTotalProvider), 999);
});
```

**Riverpod advantage**: No widget tree needed for tests!

---

## Performance Tips

### 1. Use Consumer Wisely
```dart
// Bad: Entire Column rebuilds
class MyWidget extends ConsumerWidget {
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final count = ref.watch(counterProvider);

    return Column(
      children: [
        ExpensiveWidget(),  // Rebuilds even though it doesn't use count!
        Text('$count'),
      ],
    );
  }
}

// Good: Only Text rebuilds
class MyWidget extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        ExpensiveWidget(),  // Never rebuilds
        Consumer(
          builder: (context, ref, child) {
            final count = ref.watch(counterProvider);
            return Text('$count');
          },
        ),
      ],
    );
  }
}
```

### 2. Select Specific Fields
```dart
// Bad: Rebuilds when ANY cart field changes
final cart = ref.watch(cartProvider);

// Good: Only rebuilds when item count changes
final itemCount = ref.watch(cartProvider.select((cart) => cart.items.length));
```

---

## Migration Strategy

### Moving from setState to Provider

**Step 1**: Identify shared state
```dart
// Before: State in widget
class MyWidget extends StatefulWidget {
  // State here
}

// After: State in provider
class CartProvider with ChangeNotifier {
  // State here
}
```

**Step 2**: Wrap app with provider
```dart
void main() {
  runApp(
    ChangeNotifierProvider(
      create: (_) => CartProvider(),
      child: MyApp(),
    ),
  );
}
```

**Step 3**: Replace setState calls
```dart
// Before
setState(() {
  items.add(item);
});

// After
context.read<CartProvider>().addItem(item);
```

---

## ✅ YOUR CHALLENGE: Refactor an App

Take your Notes App from Module 4 and:
1. Identify which state is local vs global
2. Refactor global state to Riverpod
3. Keep local UI state in setState
4. Add computed providers for derived state
5. Write tests for business logic

**Success Condition**: Clean architecture with proper state separation! ✅

---

## Choosing Your Stack

### For Learning:
- Start with **setState**
- Move to **Provider** when you need shared state
- Learn **Riverpod** for modern practices

### For Small Projects (< 20 screens):
- **setState** + occasional Provider

### For Medium Projects (20-50 screens):
- **Provider** or **Riverpod**

### For Large Projects (50+ screens):
- **Riverpod** (recommended)
- **BLoC** (if team has experience)

### For Enterprise:
- **BLoC** (common in enterprise)
- **Riverpod** (modern choice)

---

## What Did We Learn?

- ✅ When to use each state management approach
- ✅ Decision tree for choosing solutions
- ✅ Architecture patterns (MVVM, Repository)
- ✅ Common pitfalls and how to avoid them
- ✅ Testing strategies
- ✅ Performance optimization
- ✅ Migration strategies

---

## What's Next?

Theory is great, but practice is better! Next: **Module 5 Mini-Project** - Build a complete app using modern state management with all the patterns you've learned!
