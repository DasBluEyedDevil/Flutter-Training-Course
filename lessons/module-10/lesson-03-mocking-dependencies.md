# Lesson 3: Mocking Dependencies

## What You'll Learn
- Why and when to mock dependencies
- Using Mocktail for mocking (v1.0.4)
- Mocking API calls and services
- Verifying method calls
- Stubbing return values
- Testing error scenarios

## Concept First: What is Mocking?

### Real-World Analogy
Think of mocking like **using a crash test dummy** instead of a real person:
- **Testing a car crash?** Use a dummy (don't crash a real person!)
- **Testing your code?** Use a mock (don't call real APIs!)

Mocks are **fake objects** that simulate real dependencies for testing.

### Why Mock?

1. **Speed**: No network delays or database queries
2. **Reliability**: Tests don't fail due to external services
3. **Control**: Test error scenarios easily
4. **Isolation**: Test your code, not external code
5. **Cost**: No API rate limits or charges

**Example**: Testing a weather app shouldn't require actual weather API calls!

---

## When to Mock vs Not Mock

### ✅ DO Mock:
- External APIs (REST, GraphQL)
- Databases
- File systems
- Third-party services
- Time/date functions
- Random number generators

### ❌ DON'T Mock:
- Simple data classes (models)
- Pure functions (no side effects)
- Flutter framework widgets
- Your own business logic (test it for real!)

**Rule of Thumb:** Mock **boundaries** (edges of your app), test **internals** (your code) for real.

---

## Setting Up Mocktail

### Installation

**pubspec.yaml:**
```yaml
dev_dependencies:
  flutter_test:
    sdk: flutter
  mocktail: ^1.0.4  # No code generation needed!
```

```bash
flutter pub get
```

**Why Mocktail over Mockito?**
- ✅ No code generation (no build_runner)
- ✅ Better null safety support
- ✅ Cleaner API
- ✅ Less boilerplate

---

## Basic Mocking Example

### Creating a Mock

**lib/services/weather_service.dart:**
```dart
abstract class WeatherService {
  Future<double> getTemperature(String city);
  Future<List<String>> getForecast(String city, int days);
}

class RealWeatherService implements WeatherService {
  @override
  Future<double> getTemperature(String city) async {
    // Real API call
    final response = await http.get(Uri.parse('api.weather.com/$city'));
    return jsonDecode(response.body)['temp'];
  }

  @override
  Future<List<String>> getForecast(String city, int days) async {
    // Real API call
    return ['Sunny', 'Rainy', 'Cloudy'];
  }
}
```

**test/services/weather_service_test.dart:**
```dart
import 'package:flutter_test/flutter_test.dart';
import 'package:mocktail/mocktail.dart';
import 'package:my_app/services/weather_service.dart';

// 1. Create a mock class
class MockWeatherService extends Mock implements WeatherService {}

void main() {
  group('WeatherService', () {
    late MockWeatherService mockWeatherService;

    setUp(() {
      mockWeatherService = MockWeatherService();
    });

    test('getTemperature returns mocked value', () async {
      // 2. Stub the method (define what it returns)
      when(() => mockWeatherService.getTemperature('London'))
          .thenAnswer((_) async => 22.5);

      // 3. Call the mocked method
      final temp = await mockWeatherService.getTemperature('London');

      // 4. Verify the result
      expect(temp, 22.5);

      // 5. Verify the method was called
      verify(() => mockWeatherService.getTemperature('London')).called(1);
    });

    test('getForecast returns list', () async {
      // Stub with a list
      when(() => mockWeatherService.getForecast('Paris', 3))
          .thenAnswer((_) async => ['Sunny', 'Cloudy', 'Rainy']);

      final forecast = await mockWeatherService.getForecast('Paris', 3);

      expect(forecast, ['Sunny', 'Cloudy', 'Rainy']);
      expect(forecast, hasLength(3));
    });
  });
}
```

---

## Stubbing Methods

### Different Ways to Stub

```dart
class MockUserRepository extends Mock implements UserRepository {}

void main() {
  late MockUserRepository mockRepo;

  setUp(() {
    mockRepo = MockUserRepository();
  });

  // 1. Simple return value
  test('stub with simple value', () {
    when(() => mockRepo.getUserCount()).thenReturn(42);
    expect(mockRepo.getUserCount(), 42);
  });

  // 2. Async/Future return
  test('stub async method', () async {
    when(() => mockRepo.getUser(1))
        .thenAnswer((_) async => User(id: 1, name: 'Alice'));

    final user = await mockRepo.getUser(1);
    expect(user.name, 'Alice');
  });

  // 3. Throwing errors
  test('stub to throw exception', () {
    when(() => mockRepo.getUser(999))
        .thenThrow(UserNotFoundException('User not found'));

    expect(
      () => mockRepo.getUser(999),
      throwsA(isA<UserNotFoundException>()),
    );
  });

  // 4. Different returns for different arguments
  test('stub based on arguments', () async {
    when(() => mockRepo.getUser(1))
        .thenAnswer((_) async => User(id: 1, name: 'Alice'));
    when(() => mockRepo.getUser(2))
        .thenAnswer((_) async => User(id: 2, name: 'Bob'));

    final alice = await mockRepo.getUser(1);
    final bob = await mockRepo.getUser(2);

    expect(alice.name, 'Alice');
    expect(bob.name, 'Bob');
  });

  // 5. Using argument matchers
  test('stub with any argument', () async {
    when(() => mockRepo.getUser(any()))
        .thenAnswer((_) async => User(id: 0, name: 'Default'));

    final user1 = await mockRepo.getUser(1);
    final user2 = await mockRepo.getUser(999);

    expect(user1.name, 'Default');
    expect(user2.name, 'Default');
  });

  // 6. Sequential returns (different value each call)
  test('return different values sequentially', () {
    when(() => mockRepo.getUserCount())
        .thenReturn(1)
        .thenReturn(2)
        .thenReturn(3);

    expect(mockRepo.getUserCount(), 1);
    expect(mockRepo.getUserCount(), 2);
    expect(mockRepo.getUserCount(), 3);
  });
}
```

---

## Verification

### Verifying Method Calls

```dart
test('verify method was called', () async {
  when(() => mockRepo.saveUser(any())).thenAnswer((_) async => true);

  await mockRepo.saveUser(User(id: 1, name: 'Alice'));

  // Verify it was called exactly once
  verify(() => mockRepo.saveUser(any())).called(1);
});

test('verify method was never called', () {
  verifyNever(() => mockRepo.deleteUser(any()));
});

test('verify method was called multiple times', () {
  mockRepo.getUserCount();
  mockRepo.getUserCount();
  mockRepo.getUserCount();

  verify(() => mockRepo.getUserCount()).called(3);
});

test('verify method was called at least once', () {
  mockRepo.getUserCount();
  mockRepo.getUserCount();

  verify(() => mockRepo.getUserCount()).called(greaterThan(0));
});

test('verify method call with specific argument', () async {
  when(() => mockRepo.getUser(any())).thenAnswer((_) async => User(id: 1, name: 'Alice'));

  await mockRepo.getUser(42);

  verify(() => mockRepo.getUser(42)).called(1);
  verifyNever(() => mockRepo.getUser(99));
});

test('verify call order', () {
  mockRepo.getUserCount();
  mockRepo.saveUser(User(id: 1, name: 'Alice'));
  mockRepo.getUserCount();

  verifyInOrder([
    () => mockRepo.getUserCount(),
    () => mockRepo.saveUser(any()),
    () => mockRepo.getUserCount(),
  ]);
});
```

---

## Argument Matchers

```dart
test('any() matches any argument', () {
  when(() => mockRepo.getUser(any())).thenAnswer((_) async => mockUser);

  mockRepo.getUser(1);
  mockRepo.getUser(999);

  verify(() => mockRepo.getUser(any())).called(2);
});

test('any(named:) for named parameters', () {
  when(() => mockRepo.search(
    query: any(named: 'query'),
    limit: any(named: 'limit'),
  )).thenAnswer((_) async => []);

  mockRepo.search(query: 'test', limit: 10);

  verify(() => mockRepo.search(
    query: 'test',
    limit: any(named: 'limit'),
  )).called(1);
});

test('captureAny() to inspect arguments', () async {
  when(() => mockRepo.saveUser(any())).thenAnswer((_) async => true);

  await mockRepo.saveUser(User(id: 1, name: 'Alice'));

  final captured = verify(() => mockRepo.saveUser(captureAny())).captured;
  expect(captured.length, 1);
  expect(captured.first.name, 'Alice');
});
```

---

## Complete Example: Testing a Login Screen

**lib/services/auth_service.dart:**
```dart
abstract class AuthService {
  Future<User> login(String email, String password);
  Future<void> logout();
}

class User {
  final String id;
  final String email;
  final String name;

  User({required this.id, required this.email, required this.name});
}
```

**lib/viewmodels/login_viewmodel.dart:**
```dart
class LoginViewModel {
  final AuthService authService;

  LoginViewModel(this.authService);

  String? errorMessage;
  bool isLoading = false;
  User? currentUser;

  Future<bool> login(String email, String password) async {
    errorMessage = null;
    isLoading = true;

    try {
      currentUser = await authService.login(email, password);
      isLoading = false;
      return true;
    } on InvalidCredentialsException {
      errorMessage = 'Invalid email or password';
      isLoading = false;
      return false;
    } on NetworkException {
      errorMessage = 'Network error. Please try again.';
      isLoading = false;
      return false;
    } catch (e) {
      errorMessage = 'An unexpected error occurred';
      isLoading = false;
      return false;
    }
  }

  Future<void> logout() async {
    await authService.logout();
    currentUser = null;
  }
}
```

**test/viewmodels/login_viewmodel_test.dart:**
```dart
import 'package:flutter_test/flutter_test.dart';
import 'package:mocktail/mocktail.dart';
import 'package:my_app/services/auth_service.dart';
import 'package:my_app/viewmodels/login_viewmodel.dart';

class MockAuthService extends Mock implements AuthService {}

void main() {
  group('LoginViewModel', () {
    late MockAuthService mockAuthService;
    late LoginViewModel viewModel;

    setUp(() {
      mockAuthService = MockAuthService();
      viewModel = LoginViewModel(mockAuthService);
    });

    test('successful login sets currentUser and returns true', () async {
      // Arrange
      final mockUser = User(
        id: '123',
        email: 'test@example.com',
        name: 'Test User',
      );

      when(() => mockAuthService.login('test@example.com', 'password123'))
          .thenAnswer((_) async => mockUser);

      // Act
      final result = await viewModel.login('test@example.com', 'password123');

      // Assert
      expect(result, true);
      expect(viewModel.currentUser, mockUser);
      expect(viewModel.errorMessage, null);
      expect(viewModel.isLoading, false);

      verify(() => mockAuthService.login('test@example.com', 'password123'))
          .called(1);
    });

    test('failed login with invalid credentials shows error', () async {
      // Arrange
      when(() => mockAuthService.login(any(), any()))
          .thenThrow(InvalidCredentialsException());

      // Act
      final result = await viewModel.login('wrong@example.com', 'wrongpass');

      // Assert
      expect(result, false);
      expect(viewModel.currentUser, null);
      expect(viewModel.errorMessage, 'Invalid email or password');
      expect(viewModel.isLoading, false);
    });

    test('network error shows appropriate message', () async {
      // Arrange
      when(() => mockAuthService.login(any(), any()))
          .thenThrow(NetworkException());

      // Act
      final result = await viewModel.login('test@example.com', 'password123');

      // Assert
      expect(result, false);
      expect(viewModel.errorMessage, 'Network error. Please try again.');
    });

    test('unexpected error shows generic message', () async {
      // Arrange
      when(() => mockAuthService.login(any(), any()))
          .thenThrow(Exception('Something went wrong'));

      // Act
      final result = await viewModel.login('test@example.com', 'password123');

      // Assert
      expect(result, false);
      expect(viewModel.errorMessage, 'An unexpected error occurred');
    });

    test('logout clears currentUser', () async {
      // Arrange
      viewModel.currentUser = User(
        id: '123',
        email: 'test@example.com',
        name: 'Test User',
      );

      when(() => mockAuthService.logout()).thenAnswer((_) async {});

      // Act
      await viewModel.logout();

      // Assert
      expect(viewModel.currentUser, null);
      verify(() => mockAuthService.logout()).called(1);
    });

    test('isLoading is true during login', () async {
      // Arrange
      when(() => mockAuthService.login(any(), any()))
          .thenAnswer((_) async {
            // Simulate delay
            await Future.delayed(Duration(milliseconds: 100));
            return User(id: '123', email: 'test@example.com', name: 'Test');
          });

      // Act
      final loginFuture = viewModel.login('test@example.com', 'password123');

      // Check immediately (should be loading)
      expect(viewModel.isLoading, true);

      // Wait for completion
      await loginFuture;

      // Should no longer be loading
      expect(viewModel.isLoading, false);
    });
  });
}
```

---

## Mocking HTTP Calls

### Using Mocktail with Dio/HTTP

```dart
import 'package:http/http.dart' as http;
import 'package:mocktail/mocktail.dart';

class MockHttpClient extends Mock implements http.Client {}

void main() {
  setUpAll(() {
    // Register fallback values for Uri type
    registerFallbackValue(Uri());
  });

  test('fetch user data from API', () async {
    final mockClient = MockHttpClient();

    // Stub the get method
    when(() => mockClient.get(any())).thenAnswer(
      (_) async => http.Response(
        '{"id": 1, "name": "Alice"}',
        200,
      ),
    );

    final userService = UserService(mockClient);
    final user = await userService.getUser(1);

    expect(user.name, 'Alice');
    verify(() => mockClient.get(Uri.parse('https://api.example.com/users/1')))
        .called(1);
  });

  test('handles 404 error', () async {
    final mockClient = MockHttpClient();

    when(() => mockClient.get(any())).thenAnswer(
      (_) async => http.Response('Not Found', 404),
    );

    final userService = UserService(mockClient);

    expect(
      () => userService.getUser(999),
      throwsA(isA<UserNotFoundException>()),
    );
  });
}
```

---

## Best Practices

1. **Mock Interfaces, Not Implementations**
   ```dart
   // ✅ Good
   abstract class AuthService { /* ... */ }
   class MockAuthService extends Mock implements AuthService {}

   // ❌ Bad
   class RealAuthService { /* ... */ }
   class MockAuthService extends Mock implements RealAuthService {}
   ```

2. **Use `setUpAll()` for Fallback Values**
   ```dart
   setUpAll(() {
     registerFallbackValue(Uri());
     registerFallbackValue(User(id: '0', email: '', name: ''));
   });
   ```

3. **Verify Important Interactions**
   ```dart
   // Verify critical side effects
   verify(() => mockRepo.saveToDatabase(any())).called(1);

   // Don't over-verify
   // verifyNever(() => mockRepo.log(any()));  // Unnecessary
   ```

4. **Use Descriptive Test Names**
   ```dart
   test('login with valid credentials sets currentUser')
   test('login with network error shows error message')
   ```

5. **Reset Mocks Between Tests**
   ```dart
   setUp(() {
     mockService = MockService();  // Create fresh mock
     reset(mockService);  // Or reset existing mock
   });
   ```

---

## Quiz

**Question 1:** What's the main advantage of Mocktail over Mockito?
A) It's faster
B) No code generation needed
C) Better for integration tests
D) Works on iOS only

**Question 2:** When should you use `thenAnswer()` instead of `thenReturn()`?
A) For async methods returning Future
B) For sync methods
C) Only for error cases
D) Never, they're the same

**Question 3:** What does `verify().called(1)` check?
A) The method returned 1
B) The method was called exactly once
C) The method was called with argument 1
D) The method took 1 second

---

## Exercise: Mock a Weather App

Create a weather app service and test it:

```dart
abstract class WeatherApi {
  Future<Weather> getCurrentWeather(String city);
  Future<List<Weather>> getForecast(String city, int days);
}

class Weather {
  final double temperature;
  final String condition;
  final DateTime date;

  Weather(this.temperature, this.condition, this.date);
}
```

Write tests for:
1. Successful weather fetch
2. City not found (404 error)
3. Network timeout
4. Forecast returns correct number of days
5. Verify API is called with correct city name

---

## Summary

You've mastered mocking in Flutter! Here's what we covered:

- **Why Mock**: Speed, reliability, control, isolation
- **Mocktail Setup**: No code generation needed
- **Stubbing**: `when().thenReturn()` and `when().thenAnswer()`
- **Verification**: `verify().called()` and `verifyNever()`
- **Argument Matchers**: `any()`, `captureAny()`
- **Complete Example**: Login viewmodel with error handling

Mocking lets you test your code in isolation without external dependencies!

---

## Answer Key

**Answer 1:** B) No code generation needed

Mocktail's main advantage is that it doesn't require `build_runner` or code generation. You just extend `Mock` and implement your interface. This makes tests cleaner and faster to write.

**Answer 2:** A) For async methods returning Future

Use `thenAnswer()` for async methods because it takes a callback that can return a Future. Use `thenReturn()` for synchronous methods that return values immediately.

**Answer 3:** B) The method was called exactly once

`verify().called(1)` verifies that the method was invoked exactly one time during the test. Use `.called(n)` for n times, `greaterThan(n)` for at least n times, or `.never()` for zero times.
