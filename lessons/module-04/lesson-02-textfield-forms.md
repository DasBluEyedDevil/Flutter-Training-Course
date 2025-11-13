# Module 4, Lessons 2-3: Text Input and Forms

## Getting User Input

Buttons trigger actions, but how do we get TEXT from users?
- Login forms (username/password)
- Search bars
- Comments and messages
- Registration forms

**TextField** is the answer!

---

## Your First TextField

```dart
TextField(
  decoration: InputDecoration(
    labelText: 'Enter your name',
    border: OutlineInputBorder(),
  ),
)
```

---

## Reading TextField Value

Use a **TextEditingController**:

```dart
class TextFieldDemo extends StatefulWidget {
  @override
  _TextFieldDemoState createState() => _TextFieldDemoState();
}

class _TextFieldDemoState extends State<TextFieldDemo> {
  TextEditingController nameController = TextEditingController();
  
  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        TextField(
          controller: nameController,
          decoration: InputDecoration(labelText: 'Name'),
        ),
        ElevatedButton(
          onPressed: () {
            String name = nameController.text;
            print('Name: $name');
          },
          child: Text('Submit'),
        ),
      ],
    );
  }
  
  @override
  void dispose() {
    nameController.dispose();  // Clean up!
    super.dispose();
  }
}
```

---

## TextField Styling

```dart
TextField(
  decoration: InputDecoration(
    labelText: 'Email',
    hintText: 'Enter your email',
    prefixIcon: Icon(Icons.email),
    suffixIcon: Icon(Icons.check_circle, color: Colors.green),
    border: OutlineInputBorder(
      borderRadius: BorderRadius.circular(10),
    ),
  ),
)
```

---

## Input Types

```dart
// Number keyboard
TextField(
  keyboardType: TextInputType.number,
  decoration: InputDecoration(labelText: 'Age'),
)

// Email keyboard
TextField(
  keyboardType: TextInputType.emailAddress,
  decoration: InputDecoration(labelText: 'Email'),
)

// Phone keyboard
TextField(
  keyboardType: TextInputType.phone,
  decoration: InputDecoration(labelText: 'Phone'),
)

// Multiline
TextField(
  maxLines: 3,
  decoration: InputDecoration(labelText: 'Bio'),
)
```

---

## Password Field

```dart
class PasswordField extends StatefulWidget {
  @override
  _PasswordFieldState createState() => _PasswordFieldState();
}

class _PasswordFieldState extends State<PasswordField> {
  bool obscureText = true;
  
  @override
  Widget build(BuildContext context) {
    return TextField(
      obscureText: obscureText,
      decoration: InputDecoration(
        labelText: 'Password',
        suffixIcon: IconButton(
          icon: Icon(obscureText ? Icons.visibility : Icons.visibility_off),
          onPressed: () {
            setState(() {
              obscureText = !obscureText;
            });
          },
        ),
      ),
    );
  }
}
```

---

## Form Validation

```dart
class LoginForm extends StatefulWidget {
  @override
  _LoginFormState createState() => _LoginFormState();
}

class _LoginFormState extends State<LoginForm> {
  final _formKey = GlobalKey<FormState>();
  String email = '';
  String password = '';
  
  @override
  Widget build(BuildContext context) {
    return Form(
      key: _formKey,
      child: Column(
        children: [
          TextFormField(
            decoration: InputDecoration(labelText: 'Email'),
            validator: (value) {
              if (value == null || value.isEmpty) {
                return 'Please enter email';
              }
              if (!value.contains('@')) {
                return 'Invalid email';
              }
              return null;  // Valid
            },
            onSaved: (value) => email = value!,
          ),
          TextFormField(
            decoration: InputDecoration(labelText: 'Password'),
            obscureText: true,
            validator: (value) {
              if (value == null || value.isEmpty) {
                return 'Please enter password';
              }
              if (value.length < 6) {
                return 'Password must be at least 6 characters';
              }
              return null;
            },
            onSaved: (value) => password = value!,
          ),
          ElevatedButton(
            onPressed: () {
              if (_formKey.currentState!.validate()) {
                _formKey.currentState!.save();
                print('Email: $email, Password: $password');
                // Login logic here
              }
            },
            child: Text('Login'),
          ),
        ],
      ),
    );
  }
}
```

---

## Complete Registration Form

```dart
class RegistrationForm extends StatefulWidget {
  @override
  _RegistrationFormState createState() => _RegistrationFormState();
}

class _RegistrationFormState extends State<RegistrationForm> {
  final _formKey = GlobalKey<FormState>();
  final nameController = TextEditingController();
  final emailController = TextEditingController();
  final passwordController = TextEditingController();
  final confirmPasswordController = TextEditingController();
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Register')),
      body: Padding(
        padding: EdgeInsets.all(16),
        child: Form(
          key: _formKey,
          child: ListView(
            children: [
              TextFormField(
                controller: nameController,
                decoration: InputDecoration(
                  labelText: 'Full Name',
                  prefixIcon: Icon(Icons.person),
                ),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Please enter your name';
                  }
                  return null;
                },
              ),
              SizedBox(height: 16),
              TextFormField(
                controller: emailController,
                keyboardType: TextInputType.emailAddress,
                decoration: InputDecoration(
                  labelText: 'Email',
                  prefixIcon: Icon(Icons.email),
                ),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Please enter email';
                  }
                  if (!value.contains('@')) {
                    return 'Invalid email format';
                  }
                  return null;
                },
              ),
              SizedBox(height: 16),
              TextFormField(
                controller: passwordController,
                obscureText: true,
                decoration: InputDecoration(
                  labelText: 'Password',
                  prefixIcon: Icon(Icons.lock),
                ),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Please enter password';
                  }
                  if (value.length < 8) {
                    return 'Password must be at least 8 characters';
                  }
                  return null;
                },
              ),
              SizedBox(height: 16),
              TextFormField(
                controller: confirmPasswordController,
                obscureText: true,
                decoration: InputDecoration(
                  labelText: 'Confirm Password',
                  prefixIcon: Icon(Icons.lock_outline),
                ),
                validator: (value) {
                  if (value != passwordController.text) {
                    return 'Passwords do not match';
                  }
                  return null;
                },
              ),
              SizedBox(height: 24),
              ElevatedButton(
                onPressed: () {
                  if (_formKey.currentState!.validate()) {
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(content: Text('Registration Successful!')),
                    );
                  }
                },
                child: Text('Register'),
                style: ElevatedButton.styleFrom(
                  minimumSize: Size(double.infinity, 50),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
  
  @override
  void dispose() {
    nameController.dispose();
    emailController.dispose();
    passwordController.dispose();
    confirmPasswordController.dispose();
    super.dispose();
  }
}
```

---

## ✅ YOUR CHALLENGES

### Challenge 1: Contact Form
Create a form with name, email, message fields and submit button.

### Challenge 2: Calculator Input
Create two number TextFields and display their sum.

### Challenge 3: Search Bar
Create a search TextField with search icon that filters a list.

**Success Condition**: Working forms with validation! ✅

---

## What's Next?

You can now get input from users! But how do we make the UI UPDATE when data changes? Next: **StatefulWidget and setState**!
