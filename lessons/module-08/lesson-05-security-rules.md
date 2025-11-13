# Module 8, Lesson 5: Firebase Security Rules

## What You'll Learn
By the end of this lesson, you'll understand how to protect your Firebase data with security rules, prevent unauthorized access, and build production-ready secure applications.

---

## Why This Matters

**Security rules are the MOST IMPORTANT part of Firebase.**

- **Without security rules**, anyone can read/write your entire database
- **Data breaches** happen when developers forget to set rules
- **Firebase projects get hacked** every day due to weak security
- **Security rules** are your firewall between users and data
- **Production apps** MUST have proper security rules

**Real example**: In 2020, millions of Firebase databases were exposed online because developers used test mode rules in production.

---

## Real-World Analogy: The Bank Vault

### Without Security Rules = No Locks
- 🏦 Bank with no locks on vault
- 💰 Anyone can walk in and take money
- 📁 Anyone can see everyone's account balances
- ❌ **This is test mode!**

### With Security Rules = Multi-Layer Security
- 🔐 **Locks** (authentication required)
- 👮 **Guards** (authorization checks)
- 🎫 **ID verification** (user owns the data)
- 📹 **Cameras** (audit logs)
- ✅ **This is production mode!**

**Security rules are your bank vault's locks and guards.**

---

## Types of Firebase Security Rules

Firebase has security rules for two services:

1. **Firestore Security Rules** (database)
2. **Storage Security Rules** (files)

Both use similar syntax but protect different resources.

---

## Part 1: Firestore Security Rules

### Basic Structure

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Rules go here
    match /collection/{document} {
      allow read, write: if <condition>;
    }
  }
}
```

### The Four Operations

```javascript
allow read;   // = get + list
allow write;  // = create + update + delete

// Or be specific:
allow get;      // Read single document
allow list;     // Read multiple documents (query)
allow create;   // Create new document
allow update;   // Update existing document
allow delete;   // Delete document
```

---

## Common Security Patterns

### 1. Public Read, Authenticated Write

**Use case**: Blog posts, public content

```javascript
match /posts/{postId} {
  allow read: if true;                    // Anyone can read
  allow create: if request.auth != null;  // Only logged-in users can create
  allow update, delete: if request.auth != null
                        && request.auth.uid == resource.data.userId;
}
```

### 2. User-Specific Data (Most Common!)

**Use case**: User profiles, private data

```javascript
match /users/{userId} {
  // Users can only read/write their own data
  allow read, write: if request.auth != null
                     && request.auth.uid == userId;
}
```

### 3. Role-Based Access

**Use case**: Admin panels, moderation

```javascript
match /users/{userId} {
  allow read: if request.auth != null;
  allow write: if request.auth != null
               && (request.auth.uid == userId
                   || get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'admin');
}
```

### 4. Validate Data Types

**Use case**: Prevent invalid data

```javascript
match /posts/{postId} {
  allow create: if request.auth != null
                && request.resource.data.title is string
                && request.resource.data.title.size() > 0
                && request.resource.data.title.size() <= 100
                && request.resource.data.likes is int
                && request.resource.data.createdAt == request.time;
}
```

### 5. Subcollections

**Use case**: Comments on posts, nested data

```javascript
match /posts/{postId} {
  allow read: if true;
  allow write: if request.auth != null;

  match /comments/{commentId} {
    allow read: if true;
    allow create: if request.auth != null;
    allow update, delete: if request.auth != null
                           && request.auth.uid == resource.data.userId;
  }
}
```

---

## Complete Firestore Rules Example

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    // Helper function: Check if user is logged in
    function isSignedIn() {
      return request.auth != null;
    }

    // Helper function: Check if user owns the document
    function isOwner(userId) {
      return isSignedIn() && request.auth.uid == userId;
    }

    // Helper function: Check if user is admin
    function isAdmin() {
      return isSignedIn()
             && get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'admin';
    }

    // Helper function: Validate string field
    function validString(field, minLen, maxLen) {
      return request.resource.data[field] is string
             && request.resource.data[field].size() >= minLen
             && request.resource.data[field].size() <= maxLen;
    }

    // ========== USERS COLLECTION ==========
    match /users/{userId} {
      // Anyone can read user profiles (for mentions, etc.)
      allow read: if true;

      // Users can create their own profile on signup
      allow create: if isOwner(userId)
                    && validString('name', 1, 50)
                    && validString('email', 5, 100);

      // Users can update only their own profile
      allow update: if isOwner(userId)
                    && validString('name', 1, 50);

      // Only admins can delete users
      allow delete: if isAdmin();
    }

    // ========== POSTS COLLECTION ==========
    match /posts/{postId} {
      // Anyone can read posts
      allow read: if true;

      // Logged-in users can create posts
      allow create: if isSignedIn()
                    && validString('title', 1, 100)
                    && validString('content', 1, 10000)
                    && request.resource.data.userId == request.auth.uid
                    && request.resource.data.createdAt == request.time
                    && request.resource.data.likes == 0;

      // Only post owner can update
      allow update: if isOwner(resource.data.userId)
                    && validString('title', 1, 100)
                    && validString('content', 1, 10000)
                    // Prevent changing userId or createdAt
                    && request.resource.data.userId == resource.data.userId
                    && request.resource.data.createdAt == resource.data.createdAt;

      // Owner or admin can delete
      allow delete: if isOwner(resource.data.userId) || isAdmin();

      // ========== COMMENTS SUBCOLLECTION ==========
      match /comments/{commentId} {
        allow read: if true;
        allow create: if isSignedIn()
                      && validString('text', 1, 1000)
                      && request.resource.data.userId == request.auth.uid;
        allow update: if isOwner(resource.data.userId);
        allow delete: if isOwner(resource.data.userId) || isAdmin();
      }
    }

    // ========== PRIVATE MESSAGES ==========
    match /messages/{messageId} {
      // Only sender and recipient can read
      allow read: if isSignedIn()
                  && (request.auth.uid == resource.data.senderId
                      || request.auth.uid == resource.data.recipientId);

      // Only authenticated users can send messages
      allow create: if isSignedIn()
                    && request.resource.data.senderId == request.auth.uid
                    && validString('text', 1, 1000);

      // No updates or deletes
      allow update, delete: if false;
    }

    // ========== ADMIN ONLY COLLECTION ==========
    match /admin/{document=**} {
      allow read, write: if isAdmin();
    }
  }
}
```

---

## Part 2: Storage Security Rules

### Basic Structure

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    // Rules go here
    match /path/{fileName} {
      allow read, write: if <condition>;
    }
  }
}
```

### Common Storage Patterns

#### 1. User-Specific Files

```javascript
match /users/{userId}/{allPaths=**} {
  // Users can only access their own files
  allow read, write: if request.auth != null
                     && request.auth.uid == userId;
}
```

#### 2. File Size Limits

```javascript
match /users/{userId}/profile/{fileName} {
  // Max 5MB for profile pictures
  allow write: if request.auth != null
               && request.auth.uid == userId
               && request.resource.size < 5 * 1024 * 1024;
}
```

#### 3. File Type Validation

```javascript
match /users/{userId}/images/{fileName} {
  // Only allow image uploads
  allow write: if request.auth != null
               && request.auth.uid == userId
               && request.resource.contentType.matches('image/.*');
}
```

#### 4. Public Read, Authenticated Write

```javascript
match /public/{allPaths=**} {
  allow read: if true;
  allow write: if request.auth != null;
}
```

---

## Complete Storage Rules Example

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {

    // Helper functions
    function isSignedIn() {
      return request.auth != null;
    }

    function isOwner(userId) {
      return isSignedIn() && request.auth.uid == userId;
    }

    function isImage() {
      return request.resource.contentType.matches('image/.*');
    }

    function isVideo() {
      return request.resource.contentType.matches('video/.*');
    }

    function validSize(maxSizeMB) {
      return request.resource.size < maxSizeMB * 1024 * 1024;
    }

    // ========== USER PROFILE PICTURES ==========
    match /users/{userId}/profile/{fileName} {
      allow read: if true;  // Public read
      allow write: if isOwner(userId)
                   && isImage()
                   && validSize(5);  // 5MB max
    }

    // ========== USER POST IMAGES ==========
    match /users/{userId}/posts/{fileName} {
      allow read: if true;
      allow write: if isOwner(userId)
                   && (isImage() || isVideo())
                   && validSize(10);  // 10MB max
    }

    // ========== USER DOCUMENTS ==========
    match /users/{userId}/documents/{fileName} {
      // Private documents
      allow read, write: if isOwner(userId)
                         && validSize(20);  // 20MB max
    }

    // ========== PUBLIC FILES ==========
    match /public/{allPaths=**} {
      allow read: if true;
      allow write: if isSignedIn() && validSize(1);  // 1MB max
    }

    // ========== TEMPORARY UPLOADS ==========
    match /temp/{userId}/{allPaths=**} {
      // Temporary upload area (cleanup with Cloud Functions)
      allow read, write: if isOwner(userId)
                         && validSize(50);  // 50MB max
    }
  }
}
```

---

## Testing Security Rules

### 1. Firebase Console Rules Playground

1. Go to Firebase Console → Firestore → Rules
2. Click **"Rules Playground"** tab
3. Simulate requests with different auth states

**Example test**:
```
Location: /users/user123
Method: get
Auth: Authenticated as user123
Result: ✓ Allow (user can read own data)

Location: /users/user456
Method: get
Auth: Authenticated as user123
Result: ✗ Deny (cannot read other user's data)
```

### 2. Firebase Emulator Suite (Local Testing)

```bash
# Install Firebase CLI
npm install -g firebase-tools

# Initialize emulators
firebase init emulators

# Start emulators
firebase emulators:start
```

Then in your Flutter app:

```dart
// main.dart
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_storage/firebase_storage.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );

  // Use emulators in debug mode
  if (kDebugMode) {
    FirebaseFirestore.instance.useFirestoreEmulator('localhost', 8080);
    FirebaseStorage.instance.useStorageEmulator('localhost', 9199);
  }

  runApp(const MyApp());
}
```

---

## Common Security Mistakes

### ❌ Mistake 1: Test Mode in Production

```javascript
// NEVER DO THIS IN PRODUCTION!
match /{document=**} {
  allow read, write: if true;
}
```

**Problem**: Anyone can read/write your entire database!

### ❌ Mistake 2: Relying on Client-Side Checks

```dart
// ❌ BAD: Checking on client doesn't prevent malicious users
if (user.isAdmin) {
  await firestore.collection('admin').doc('settings').set(data);
}
```

**Problem**: Hackers can modify your app code and bypass this check.

**Solution**: Enforce in security rules!

```javascript
// ✅ GOOD: Server-side enforcement
match /admin/{document} {
  allow write: if request.auth.token.admin == true;
}
```

### ❌ Mistake 3: Not Validating Data

```javascript
// ❌ BAD: No validation
match /posts/{postId} {
  allow write: if request.auth != null;
}
```

**Problem**: Users can write invalid data (empty titles, negative numbers, etc.)

**Solution**: Validate everything!

```javascript
// ✅ GOOD: Strict validation
match /posts/{postId} {
  allow write: if request.auth != null
               && request.resource.data.title is string
               && request.resource.data.title.size() > 0;
}
```

---

## Security Rules Best Practices

### ✅ DO:

1. **Start restrictive, gradually allow** (deny by default)
2. **Validate all data types and sizes**
3. **Prevent users from changing critical fields** (userId, createdAt)
4. **Use helper functions** for reusable logic
5. **Test rules thoroughly** before deploying
6. **Log and monitor** rule violations
7. **Review rules regularly** as your app evolves

### ❌ DON'T:

1. **Don't use test mode** in production
2. **Don't trust client-side validation**
3. **Don't allow unlimited file sizes**
4. **Don't forget subcollection rules**
5. **Don't expose sensitive data** in public reads
6. **Don't allow users to read all users** (privacy issue)

---

## Production-Ready Rules Checklist

Before launching your app, verify:

- [ ] **No `if true` rules** except for truly public data
- [ ] **All write operations require authentication**
- [ ] **Users can only access their own data**
- [ ] **Data validation on all fields**
- [ ] **File size limits enforced**
- [ ] **File type validation for uploads**
- [ ] **Admin actions require admin role**
- [ ] **Rules tested with emulator**
- [ ] **No sensitive data in public reads**
- [ ] **Subcollections have appropriate rules**

---

## Monitoring Security

### View Rule Violations

1. Go to Firebase Console → Firestore → Usage
2. Check "Denied requests" graph
3. High denial rate might indicate:
   - **Attack attempt** (good - rules working!)
   - **Bug in your app** (bad - fix your code)
   - **Overly restrictive rules** (bad - adjust rules)

### Set Up Alerts

1. Firebase Console → Project Settings → Integrations
2. Enable Cloud Functions alerts
3. Monitor for:
   - Unusual traffic spikes
   - High error rates
   - Storage quota nearing limit

---

## Quiz Time! 🧠

### Question 1
Why must security rules be enforced on the server, not the client?

A) It's faster
B) Hackers can modify client code to bypass client-side checks
C) It's easier to code
D) Firebase requires it

### Question 2
What's wrong with this rule: `allow write: if true;`?

A) Syntax error
B) It allows anyone (including unauthenticated users) to write data
C) It's too slow
D) Nothing, it's fine

### Question 3
Why validate data types in security rules?

A) To make queries faster
B) To prevent invalid data that could break your app
C) Firebase requires it
D) To reduce storage costs

---

## Answer Key

### Answer 1: B
**Correct**: Hackers can modify client code to bypass client-side checks

Since Flutter apps run on the user's device, hackers can decompile your app, modify the code, and bypass any client-side security checks. Security rules run on Firebase servers (which hackers can't access), making them the only reliable security layer.

### Answer 2: B
**Correct**: It allows anyone (including unauthenticated users) to write data

`allow write: if true` means "allow anyone to write data, no questions asked." This is extremely dangerous in production - anyone could delete your entire database, inject malicious data, or fill your storage quota.

### Answer 3: B
**Correct**: To prevent invalid data that could break your app

Without validation, users could write `{ title: 123, likes: "hello", createdAt: null }` which would break your app when it tries to display a string title or count numeric likes. Validation ensures data matches your expected schema.

---

## What's Next?

You've mastered Firebase security! In the next lesson, we'll explore **Real-Time Features** - building apps that update instantly across all devices.

**Coming up in Lesson 6: Real-Time Features**
- Real-time listeners
- Presence detection (online/offline)
- Live collaboration features
- Chat app example

---

## Key Takeaways

✅ Security rules are your firewall between users and data
✅ Always enforce security on the server (never trust client code)
✅ Start with deny-all, gradually allow specific operations
✅ Validate all data (types, sizes, required fields)
✅ Test rules thoroughly before production
✅ Users should only access their own data (userId matching)
✅ File uploads need size and type validation
✅ Monitor rule violations to detect attacks and bugs

**Your app is now production-ready and secure!** 🔐
