# Module 5, Mini-Project: Task Management App with Riverpod

## Project Overview

Build a **complete task management app** with Riverpod that demonstrates:
- ✅ Multiple providers with dependencies
- ✅ Family and autoDispose modifiers
- ✅ Repository pattern
- ✅ Computed state
- ✅ AsyncValue handling
- ✅ Real-world architecture

**Features:**
- User authentication
- Multiple projects
- Tasks with categories
- Filtering and sorting
- Statistics dashboard
- Pull to refresh
- Offline support

---

## Architecture

```
lib/
├── main.dart
├── models/
│   ├── user.dart
│   ├── project.dart
│   └── task.dart
├── providers/
│   ├── auth_provider.dart
│   ├── projects_provider.dart
│   ├── tasks_provider.dart
│   └── stats_provider.dart
├── repositories/
│   ├── auth_repository.dart
│   └── task_repository.dart
├── screens/
│   ├── login_screen.dart
│   ├── home_screen.dart
│   ├── project_detail_screen.dart
│   └── task_detail_screen.dart
└── widgets/
    ├── project_card.dart
    └── task_tile.dart
```

---

## Step 1: Models

```dart
// lib/models/user.dart
class User {
  final String id;
  final String name;
  final String email;

  User({required this.id, required this.name, required this.email});

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      id: json['id'],
      name: json['name'],
      email: json['email'],
    );
  }

  Map<String, dynamic> toJson() {
    return {'id': id, 'name': name, 'email': email};
  }
}

// lib/models/project.dart
class Project {
  final String id;
  final String name;
  final String description;
  final String userId;
  final DateTime createdAt;

  Project({
    required this.id,
    required this.name,
    required this.description,
    required this.userId,
    required this.createdAt,
  });

  factory Project.fromJson(Map<String, dynamic> json) {
    return Project(
      id: json['id'],
      name: json['name'],
      description: json['description'],
      userId: json['userId'],
      createdAt: DateTime.parse(json['createdAt']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'description': description,
      'userId': userId,
      'createdAt': createdAt.toIso8601String(),
    };
  }
}

// lib/models/task.dart
enum TaskStatus { todo, inProgress, done }
enum TaskPriority { low, medium, high }

class Task {
  final String id;
  final String title;
  final String description;
  final String projectId;
  final TaskStatus status;
  final TaskPriority priority;
  final DateTime createdAt;
  final DateTime? dueDate;

  Task({
    required this.id,
    required this.title,
    required this.description,
    required this.projectId,
    required this.status,
    required this.priority,
    required this.createdAt,
    this.dueDate,
  });

  Task copyWith({
    String? id,
    String? title,
    String? description,
    String? projectId,
    TaskStatus? status,
    TaskPriority? priority,
    DateTime? createdAt,
    DateTime? dueDate,
  }) {
    return Task(
      id: id ?? this.id,
      title: title ?? this.title,
      description: description ?? this.description,
      projectId: projectId ?? this.projectId,
      status: status ?? this.status,
      priority: priority ?? this.priority,
      createdAt: createdAt ?? this.createdAt,
      dueDate: dueDate ?? this.dueDate,
    );
  }

  factory Task.fromJson(Map<String, dynamic> json) {
    return Task(
      id: json['id'],
      title: json['title'],
      description: json['description'],
      projectId: json['projectId'],
      status: TaskStatus.values.byName(json['status']),
      priority: TaskPriority.values.byName(json['priority']),
      createdAt: DateTime.parse(json['createdAt']),
      dueDate: json['dueDate'] != null ? DateTime.parse(json['dueDate']) : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'title': title,
      'description': description,
      'projectId': projectId,
      'status': status.name,
      'priority': priority.name,
      'createdAt': createdAt.toIso8601String(),
      'dueDate': dueDate?.toIso8601String(),
    };
  }
}
```

---

## Step 2: Repositories

```dart
// lib/repositories/auth_repository.dart
class AuthRepository {
  User? _currentUser;

  Future<User> login(String email, String password) async {
    await Future.delayed(Duration(seconds: 1));  // Simulate API call

    if (email.isEmpty || password.isEmpty) {
      throw Exception('Invalid credentials');
    }

    _currentUser = User(
      id: 'user_${DateTime.now().millisecondsSinceEpoch}',
      name: email.split('@')[0],
      email: email,
    );

    return _currentUser!;
  }

  Future<void> logout() async {
    await Future.delayed(Duration(milliseconds: 500));
    _currentUser = null;
  }

  User? getCurrentUser() => _currentUser;
}

// lib/repositories/task_repository.dart
class TaskRepository {
  final List<Project> _projects = [];
  final List<Task> _tasks = [];

  Future<List<Project>> getProjects(String userId) async {
    await Future.delayed(Duration(milliseconds: 500));
    return _projects.where((p) => p.userId == userId).toList();
  }

  Future<Project> createProject(Project project) async {
    await Future.delayed(Duration(milliseconds: 300));
    _projects.add(project);
    return project;
  }

  Future<void> deleteProject(String projectId) async {
    await Future.delayed(Duration(milliseconds: 300));
    _projects.removeWhere((p) => p.id == projectId);
    _tasks.removeWhere((t) => t.projectId == projectId);
  }

  Future<List<Task>> getTasksForProject(String projectId) async {
    await Future.delayed(Duration(milliseconds: 400));
    return _tasks.where((t) => t.projectId == projectId).toList();
  }

  Future<Task> createTask(Task task) async {
    await Future.delayed(Duration(milliseconds: 300));
    _tasks.add(task);
    return task;
  }

  Future<Task> updateTask(Task task) async {
    await Future.delayed(Duration(milliseconds: 300));
    final index = _tasks.indexWhere((t) => t.id == task.id);
    if (index != -1) {
      _tasks[index] = task;
    }
    return task;
  }

  Future<void> deleteTask(String taskId) async {
    await Future.delayed(Duration(milliseconds: 300));
    _tasks.removeWhere((t) => t.id == taskId);
  }
}
```

---

## Step 3: Providers

```dart
// lib/providers/auth_provider.dart
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../models/user.dart';
import '../repositories/auth_repository.dart';

final authRepositoryProvider = Provider<AuthRepository>((ref) {
  return AuthRepository();
});

final authStateProvider = StateNotifierProvider<AuthStateNotifier, AsyncValue<User?>>((ref) {
  final repository = ref.read(authRepositoryProvider);
  return AuthStateNotifier(repository);
});

class AuthStateNotifier extends StateNotifier<AsyncValue<User?>> {
  final AuthRepository _repository;

  AuthStateNotifier(this._repository) : super(const AsyncValue.data(null)) {
    _checkAuth();
  }

  void _checkAuth() {
    final user = _repository.getCurrentUser();
    state = AsyncValue.data(user);
  }

  Future<void> login(String email, String password) async {
    state = const AsyncValue.loading();
    try {
      final user = await _repository.login(email, password);
      state = AsyncValue.data(user);
    } catch (e, stack) {
      state = AsyncValue.error(e, stack);
    }
  }

  Future<void> logout() async {
    await _repository.logout();
    state = const AsyncValue.data(null);
  }
}

// lib/providers/projects_provider.dart
final taskRepositoryProvider = Provider<TaskRepository>((ref) {
  return TaskRepository();
});

final projectsProvider = FutureProvider.autoDispose<List<Project>>((ref) async {
  final authAsync = ref.watch(authStateProvider);
  final repository = ref.read(taskRepositoryProvider);

  return authAsync.when(
    data: (user) async {
      if (user == null) return [];
      return await repository.getProjects(user.id);
    },
    loading: () => [],
    error: (_, __) => [],
  );
});

final projectProvider = FutureProvider.autoDispose.family<Project?, String>((ref, projectId) async {
  final projects = await ref.watch(projectsProvider.future);
  return projects.where((p) => p.id == projectId).firstOrNull;
});

// lib/providers/tasks_provider.dart
final tasksProvider = FutureProvider.autoDispose.family<List<Task>, String>((ref, projectId) async {
  final repository = ref.read(taskRepositoryProvider);
  return await repository.getTasksForProject(projectId);
});

enum TaskFilter { all, todo, inProgress, done }

final taskFilterProvider = StateProvider.autoDispose<TaskFilter>((ref) => TaskFilter.all);

final filteredTasksProvider = Provider.autoDispose.family<AsyncValue<List<Task>>, String>((ref, projectId) {
  final tasksAsync = ref.watch(tasksProvider(projectId));
  final filter = ref.watch(taskFilterProvider);

  return tasksAsync.whenData((tasks) {
    switch (filter) {
      case TaskFilter.todo:
        return tasks.where((t) => t.status == TaskStatus.todo).toList();
      case TaskFilter.inProgress:
        return tasks.where((t) => t.status == TaskStatus.inProgress).toList();
      case TaskFilter.done:
        return tasks.where((t) => t.status == TaskStatus.done).toList();
      case TaskFilter.all:
      default:
        return tasks;
    }
  });
});

// lib/providers/stats_provider.dart
final projectStatsProvider = Provider.autoDispose.family<AsyncValue<Map<String, int>>, String>((ref, projectId) {
  final tasksAsync = ref.watch(tasksProvider(projectId));

  return tasksAsync.whenData((tasks) {
    return {
      'total': tasks.length,
      'todo': tasks.where((t) => t.status == TaskStatus.todo).length,
      'inProgress': tasks.where((t) => t.status == TaskStatus.inProgress).length,
      'done': tasks.where((t) => t.status == TaskStatus.done).length,
      'high': tasks.where((t) => t.priority == TaskPriority.high).length,
    };
  });
});
```

---

## Step 4: Login Screen

```dart
// lib/screens/login_screen.dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../providers/auth_provider.dart';
import 'home_screen.dart';

class LoginScreen extends ConsumerStatefulWidget {
  @override
  _LoginScreenState createState() => _LoginScreenState();
}

class _LoginScreenState extends ConsumerState<LoginScreen> {
  final emailController = TextEditingController();
  final passwordController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    ref.listen<AsyncValue<User?>>(authStateProvider, (previous, next) {
      next.whenData((user) {
        if (user != null) {
          Navigator.pushReplacement(
            context,
            MaterialPageRoute(builder: (_) => HomeScreen()),
          );
        }
      });
    });

    final authState = ref.watch(authStateProvider);

    return Scaffold(
      appBar: AppBar(title: Text('Login')),
      body: Padding(
        padding: EdgeInsets.all(16),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            TextField(
              controller: emailController,
              decoration: InputDecoration(
                labelText: 'Email',
                border: OutlineInputBorder(),
              ),
              keyboardType: TextInputType.emailAddress,
            ),
            SizedBox(height: 16),
            TextField(
              controller: passwordController,
              decoration: InputDecoration(
                labelText: 'Password',
                border: OutlineInputBorder(),
              ),
              obscureText: true,
            ),
            SizedBox(height: 24),
            authState.isLoading
                ? CircularProgressIndicator()
                : SizedBox(
                    width: double.infinity,
                    child: ElevatedButton(
                      onPressed: () {
                        ref.read(authStateProvider.notifier).login(
                              emailController.text,
                              passwordController.text,
                            );
                      },
                      child: Text('Login'),
                      style: ElevatedButton.styleFrom(
                        padding: EdgeInsets.all(16),
                      ),
                    ),
                  ),
            if (authState.hasError)
              Padding(
                padding: EdgeInsets.only(top: 16),
                child: Text(
                  'Error: ${authState.error}',
                  style: TextStyle(color: Colors.red),
                ),
              ),
          ],
        ),
      ),
    );
  }

  @override
  void dispose() {
    emailController.dispose();
    passwordController.dispose();
    super.dispose();
  }
}
```

---

## Step 5: Home Screen (Project List)

```dart
// lib/screens/home_screen.dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../providers/auth_provider.dart';
import '../providers/projects_provider.dart';
import '../models/project.dart';
import '../repositories/task_repository.dart';
import 'project_detail_screen.dart';
import 'login_screen.dart';

class HomeScreen extends ConsumerWidget {
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final projectsAsync = ref.watch(projectsProvider);
    final authAsync = ref.watch(authStateProvider);

    return Scaffold(
      appBar: AppBar(
        title: Text('Projects'),
        actions: [
          authAsync.whenData((user) {
            return user != null
                ? IconButton(
                    icon: Icon(Icons.logout),
                    onPressed: () async {
                      await ref.read(authStateProvider.notifier).logout();
                      Navigator.pushReplacement(
                        context,
                        MaterialPageRoute(builder: (_) => LoginScreen()),
                      );
                    },
                  )
                : SizedBox.shrink();
          }).value ?? SizedBox.shrink(),
        ],
      ),
      body: projectsAsync.when(
        data: (projects) {
          if (projects.isEmpty) {
            return Center(
              child: Text('No projects yet. Tap + to create one!'),
            );
          }

          return RefreshIndicator(
            onRefresh: () async {
              ref.invalidate(projectsProvider);
            },
            child: ListView.builder(
              itemCount: projects.length,
              itemBuilder: (context, index) {
                final project = projects[index];
                return ProjectCard(project: project);
              },
            ),
          );
        },
        loading: () => Center(child: CircularProgressIndicator()),
        error: (err, stack) => Center(child: Text('Error: $err')),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => _showCreateProjectDialog(context, ref),
        child: Icon(Icons.add),
      ),
    );
  }

  void _showCreateProjectDialog(BuildContext context, WidgetRef ref) {
    final nameController = TextEditingController();
    final descController = TextEditingController();

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text('New Project'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(
              controller: nameController,
              decoration: InputDecoration(labelText: 'Project Name'),
            ),
            SizedBox(height: 8),
            TextField(
              controller: descController,
              decoration: InputDecoration(labelText: 'Description'),
              maxLines: 3,
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: Text('Cancel'),
          ),
          ElevatedButton(
            onPressed: () async {
              final authAsync = ref.read(authStateProvider);
              final user = authAsync.value;

              if (user != null && nameController.text.isNotEmpty) {
                final project = Project(
                  id: 'proj_${DateTime.now().millisecondsSinceEpoch}',
                  name: nameController.text,
                  description: descController.text,
                  userId: user.id,
                  createdAt: DateTime.now(),
                );

                await ref.read(taskRepositoryProvider).createProject(project);
                ref.invalidate(projectsProvider);

                Navigator.pop(context);
              }
            },
            child: Text('Create'),
          ),
        ],
      ),
    );
  }
}

class ProjectCard extends ConsumerWidget {
  final Project project;

  ProjectCard({required this.project});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final statsAsync = ref.watch(projectStatsProvider(project.id));

    return Card(
      margin: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: InkWell(
        onTap: () {
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (_) => ProjectDetailScreen(projectId: project.id),
            ),
          );
        },
        child: Padding(
          padding: EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Expanded(
                    child: Text(
                      project.name,
                      style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
                    ),
                  ),
                  statsAsync.when(
                    data: (stats) => Container(
                      padding: EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                      decoration: BoxDecoration(
                        color: Colors.blue,
                        borderRadius: BorderRadius.circular(20),
                      ),
                      child: Text(
                        '${stats['done']}/${stats['total']}',
                        style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
                      ),
                    ),
                    loading: () => SizedBox(
                      width: 20,
                      height: 20,
                      child: CircularProgressIndicator(strokeWidth: 2),
                    ),
                    error: (_, __) => SizedBox.shrink(),
                  ),
                ],
              ),
              SizedBox(height: 8),
              Text(
                project.description,
                style: TextStyle(color: Colors.grey[600]),
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
              ),
              SizedBox(height: 12),
              statsAsync.when(
                data: (stats) => Row(
                  children: [
                    _StatChip(label: 'To Do', count: stats['todo'] ?? 0, color: Colors.grey),
                    SizedBox(width: 8),
                    _StatChip(label: 'In Progress', count: stats['inProgress'] ?? 0, color: Colors.orange),
                    SizedBox(width: 8),
                    _StatChip(label: 'Done', count: stats['done'] ?? 0, color: Colors.green),
                  ],
                ),
                loading: () => SizedBox.shrink(),
                error: (_, __) => SizedBox.shrink(),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _StatChip extends StatelessWidget {
  final String label;
  final int count;
  final Color color;

  _StatChip({required this.label, required this.count, required this.color});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      decoration: BoxDecoration(
        color: color.withOpacity(0.1),
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: color),
      ),
      child: Text(
        '$label: $count',
        style: TextStyle(fontSize: 12, color: color, fontWeight: FontWeight.bold),
      ),
    );
  }
}
```

Due to length, I'll create a separate continuation message with the remaining screens...

---

## What This Project Teaches

**Riverpod Concepts:**
- ✅ StateNotifierProvider for auth
- ✅ FutureProvider for async data
- ✅ .family for parameterized providers
- ✅ .autoDispose for memory management
- ✅ Computed providers (stats, filtering)
- ✅ Provider dependencies (auth → projects → tasks)
- ✅ ref.listen for navigation
- ✅ ref.invalidate for refreshing

**Architecture:**
- ✅ Repository pattern
- ✅ Clean separation of concerns
- ✅ Model-Provider-View layers
- ✅ Reusable widgets

**Flutter Features:**
- ✅ Navigation
- ✅ Forms and validation
- ✅ Pull to refresh
- ✅ Dialogs
- ✅ Lists and cards

---

## Enhancement Ideas

1. **Persistence**: Add SharedPreferences or SQLite
2. **Search**: Add task search functionality
3. **Calendar View**: Show tasks by due date
4. **Notifications**: Remind about due tasks
5. **Collaboration**: Share projects with other users
6. **Themes**: Dark mode support
7. **Analytics**: Charts showing progress over time

---

## Success Condition

Build this app and you've mastered:
- ✅ Complex Riverpod patterns
- ✅ Production-ready architecture
- ✅ State management best practices
- ✅ Real-world Flutter development

**Congratulations! You're ready for Module 6: Navigation & Routing!** 🎉
