# Module 8, Lesson 4: Firebase Cloud Storage - File Storage

## What You'll Learn
By the end of this lesson, you'll know how to upload, download, and manage files (images, videos, documents) using Firebase Cloud Storage with progress tracking and security.

---

## Why This Matters

**Most apps need to store user files.**

- **Instagram**: Stores billions of photos and videos
- **WhatsApp**: Profile pictures, media messages, documents
- **Google Drive**: Files of all types in the cloud
- **LinkedIn**: Profile photos, resumes, company logos
- **90% of social apps** involve media upload/download

Firebase Cloud Storage provides secure, scalable file storage that integrates seamlessly with Firebase Authentication and Firestore.

---

## Real-World Analogy: The Photo Lab

### Before Cloud Storage = Physical Photo Album
- 📸 Take photo → develop film → store in album
- 📦 Album stored in your house only
- ❌ Lose the album, lose all photos
- ❌ Can't share with friends easily
- ❌ Limited by physical space

### With Cloud Storage = Online Photo Service (Google Photos)
- 📸 Take photo → automatically uploads to cloud
- ☁️ Stored on servers worldwide (safe and redundant)
- ✅ Access from any device
- ✅ Share with anyone via link
- ✅ Unlimited storage (in cloud)
- 🔐 Protected by authentication

**Firebase Storage is your app's photo lab in the cloud!**

---

## Firebase Storage Overview

### What Firebase Storage Provides

1. **File Upload/Download**
   - Images (JPEG, PNG, GIF, WebP)
   - Videos (MP4, MOV)
   - Audio files
   - Documents (PDF, DOCX)
   - Any file type

2. **Security**
   - Integration with Firebase Auth
   - Custom security rules
   - Access control per user

3. **Performance**
   - Automatic compression
   - CDN (Content Delivery Network)
   - Resume interrupted uploads/downloads

4. **Scalability**
   - Handles millions of files
   - Automatic load balancing
   - Google's infrastructure

---

## Storage Structure

Firebase Storage organizes files like a file system:

```
gs://your-app.appspot.com/
├── users/
│   ├── user123/
│   │   ├── profile.jpg
│   │   └── documents/
│   │       └── resume.pdf
│   └── user456/
│       └── profile.jpg
├── posts/
│   ├── post001.jpg
│   └── post002.mp4
└── public/
    └── app-logo.png
```

**Best practices**:
- Organize by user ID or content type
- Use consistent naming conventions
- Avoid spaces in filenames (use hyphens or underscores)

---

## Setting Up Firebase Storage

### 1. Enable Storage in Firebase Console

1. Go to https://console.firebase.google.com
2. Select your project
3. Click **"Storage"** in left sidebar
4. Click **"Get started"**
5. Choose security rules:
   - **Test mode**: Anyone can read/write (insecure!)
   - **Production mode**: Requires authentication (recommended)
6. Select location (same as Firestore for consistency)
7. Click **"Done"**

### 2. Add Package to pubspec.yaml

```yaml
dependencies:
  firebase_core: ^4.2.0
  firebase_auth: ^6.1.1
  cloud_firestore: ^6.0.3
  firebase_storage: ^13.0.4  # ← Add this
  image_picker: ^1.1.2       # For selecting images
```

Run:
```bash
flutter pub get
```

---

## Basic Storage Operations

### Create Storage Service

```dart
// lib/services/storage_service.dart
import 'package:firebase_storage/firebase_storage.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'dart:io';

class StorageService {
  final FirebaseStorage _storage = FirebaseStorage.instance;
  final FirebaseAuth _auth = FirebaseAuth.instance;

  String? get currentUserId => _auth.currentUser?.uid;

  // ========== UPLOAD ==========

  // Upload file with progress tracking
  Future<String> uploadFile({
    required File file,
    required String path,
    Function(double)? onProgress,
  }) async {
    try {
      // Create reference to the file location
      final storageRef = _storage.ref().child(path);

      // Upload the file
      final uploadTask = storageRef.putFile(file);

      // Listen to upload progress
      uploadTask.snapshotEvents.listen((TaskSnapshot snapshot) {
        final progress = snapshot.bytesTransferred / snapshot.totalBytes;
        if (onProgress != null) {
          onProgress(progress);
        }
      });

      // Wait for upload to complete
      final snapshot = await uploadTask;

      // Get download URL
      final downloadUrl = await snapshot.ref.getDownloadURL();

      return downloadUrl;
    } on FirebaseException catch (e) {
      throw _handleStorageException(e);
    }
  }

  // Upload user profile picture
  Future<String> uploadProfilePicture(File imageFile) async {
    if (currentUserId == null) throw 'User not authenticated';

    final fileName = 'profile_${DateTime.now().millisecondsSinceEpoch}.jpg';
    final path = 'users/$currentUserId/profile/$fileName';

    return uploadFile(file: imageFile, path: path);
  }

  // Upload post image
  Future<String> uploadPostImage(File imageFile) async {
    if (currentUserId == null) throw 'User not authenticated';

    final fileName = 'post_${DateTime.now().millisecondsSinceEpoch}.jpg';
    final path = 'users/$currentUserId/posts/$fileName';

    return uploadFile(file: imageFile, path: path);
  }

  // Upload document
  Future<String> uploadDocument(File file, String documentName) async {
    if (currentUserId == null) throw 'User not authenticated';

    final fileName = '${DateTime.now().millisecondsSinceEpoch}_$documentName';
    final path = 'users/$currentUserId/documents/$fileName';

    return uploadFile(file: file, path: path);
  }

  // ========== DOWNLOAD ==========

  // Get download URL for a file
  Future<String> getDownloadUrl(String path) async {
    try {
      final ref = _storage.ref().child(path);
      return await ref.getDownloadURL();
    } on FirebaseException catch (e) {
      throw _handleStorageException(e);
    }
  }

  // Download file to local storage
  Future<File> downloadFile({
    required String path,
    required String localPath,
    Function(double)? onProgress,
  }) async {
    try {
      final ref = _storage.ref().child(path);
      final file = File(localPath);

      final downloadTask = ref.writeToFile(file);

      // Listen to download progress
      downloadTask.snapshotEvents.listen((TaskSnapshot snapshot) {
        final progress = snapshot.bytesTransferred / snapshot.totalBytes;
        if (onProgress != null) {
          onProgress(progress);
        }
      });

      await downloadTask;
      return file;
    } on FirebaseException catch (e) {
      throw _handleStorageException(e);
    }
  }

  // ========== DELETE ==========

  // Delete file by path
  Future<void> deleteFile(String path) async {
    try {
      final ref = _storage.ref().child(path);
      await ref.delete();
    } on FirebaseException catch (e) {
      throw _handleStorageException(e);
    }
  }

  // Delete file by URL
  Future<void> deleteFileByUrl(String downloadUrl) async {
    try {
      final ref = _storage.refFromURL(downloadUrl);
      await ref.delete();
    } on FirebaseException catch (e) {
      throw _handleStorageException(e);
    }
  }

  // Delete user's profile picture
  Future<void> deleteProfilePicture() async {
    if (currentUserId == null) throw 'User not authenticated';

    final path = 'users/$currentUserId/profile/';
    await _deleteFolder(path);
  }

  // ========== METADATA ==========

  // Get file metadata
  Future<FullMetadata> getMetadata(String path) async {
    try {
      final ref = _storage.ref().child(path);
      return await ref.getMetadata();
    } on FirebaseException catch (e) {
      throw _handleStorageException(e);
    }
  }

  // Update file metadata
  Future<void> updateMetadata({
    required String path,
    Map<String, String>? customMetadata,
    String? contentType,
  }) async {
    try {
      final ref = _storage.ref().child(path);
      final metadata = SettableMetadata(
        customMetadata: customMetadata,
        contentType: contentType,
      );
      await ref.updateMetadata(metadata);
    } on FirebaseException catch (e) {
      throw _handleStorageException(e);
    }
  }

  // ========== LIST FILES ==========

  // List all files in a directory
  Future<List<String>> listFiles(String path) async {
    try {
      final ref = _storage.ref().child(path);
      final result = await ref.listAll();

      return result.items.map((item) => item.fullPath).toList();
    } on FirebaseException catch (e) {
      throw _handleStorageException(e);
    }
  }

  // List user's profile pictures
  Future<List<String>> listUserImages() async {
    if (currentUserId == null) throw 'User not authenticated';

    final path = 'users/$currentUserId/posts/';
    return listFiles(path);
  }

  // ========== HELPER METHODS ==========

  // Delete entire folder (recursively)
  Future<void> _deleteFolder(String path) async {
    final ref = _storage.ref().child(path);
    final result = await ref.listAll();

    // Delete all files
    for (var item in result.items) {
      await item.delete();
    }

    // Delete subfolders recursively
    for (var prefix in result.prefixes) {
      await _deleteFolder(prefix.fullPath);
    }
  }

  // Handle Storage exceptions
  String _handleStorageException(FirebaseException e) {
    switch (e.code) {
      case 'unauthorized':
        return 'You don\'t have permission to access this file.';
      case 'canceled':
        return 'Upload/download was canceled.';
      case 'unknown':
        return 'An unknown error occurred.';
      case 'object-not-found':
        return 'File not found.';
      case 'bucket-not-found':
        return 'Storage bucket not found.';
      case 'project-not-found':
        return 'Firebase project not found.';
      case 'quota-exceeded':
        return 'Storage quota exceeded.';
      case 'unauthenticated':
        return 'Please login to upload files.';
      case 'retry-limit-exceeded':
        return 'Operation timed out. Please try again.';
      default:
        return 'Storage error: ${e.message}';
    }
  }
}
```

---

## Complete Example: Image Upload App

### Profile Picture Upload Screen

```dart
// lib/screens/profile/edit_profile_screen.dart
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'dart:io';
import '../../services/storage_service.dart';
import '../../services/auth_service.dart';
import '../../services/firestore_service.dart';

class EditProfileScreen extends StatefulWidget {
  const EditProfileScreen({super.key});

  @override
  State<EditProfileScreen> createState() => _EditProfileScreenState();
}

class _EditProfileScreenState extends State<EditProfileScreen> {
  final _storageService = StorageService();
  final _authService = AuthService();
  final _imagePicker = ImagePicker();

  File? _selectedImage;
  bool _isUploading = false;
  double _uploadProgress = 0.0;
  String? _currentProfileUrl;

  @override
  void initState() {
    super.initState();
    _loadCurrentProfile();
  }

  Future<void> _loadCurrentProfile() async {
    // Load user's current profile picture URL from Firestore
    // (Implementation depends on your Firestore setup)
  }

  Future<void> _pickImage(ImageSource source) async {
    try {
      final XFile? image = await _imagePicker.pickImage(
        source: source,
        maxWidth: 1024,
        maxHeight: 1024,
        imageQuality: 85,
      );

      if (image != null) {
        setState(() {
          _selectedImage = File(image.path);
        });
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Failed to pick image: $e')),
        );
      }
    }
  }

  Future<void> _uploadProfilePicture() async {
    if (_selectedImage == null) return;

    setState(() {
      _isUploading = true;
      _uploadProgress = 0.0;
    });

    try {
      // Delete old profile picture if exists
      if (_currentProfileUrl != null) {
        try {
          await _storageService.deleteFileByUrl(_currentProfileUrl!);
        } catch (e) {
          // Ignore if file doesn't exist
        }
      }

      // Upload new profile picture
      final downloadUrl = await _storageService.uploadFile(
        file: _selectedImage!,
        path: 'users/${_storageService.currentUserId}/profile/profile.jpg',
        onProgress: (progress) {
          setState(() {
            _uploadProgress = progress;
          });
        },
      );

      // Update Firestore with new profile URL
      // await _firestoreService.updateUserProfile(downloadUrl);

      setState(() {
        _isUploading = false;
        _currentProfileUrl = downloadUrl;
        _selectedImage = null;
      });

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Profile picture updated!')),
        );
      }
    } catch (e) {
      setState(() {
        _isUploading = false;
      });

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Upload failed: $e')),
        );
      }
    }
  }

  Future<void> _showImageSourceDialog() async {
    return showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Choose Image Source'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            ListTile(
              leading: const Icon(Icons.camera_alt),
              title: const Text('Camera'),
              onTap: () {
                Navigator.pop(context);
                _pickImage(ImageSource.camera);
              },
            ),
            ListTile(
              leading: const Icon(Icons.photo_library),
              title: const Text('Gallery'),
              onTap: () {
                Navigator.pop(context);
                _pickImage(ImageSource.gallery);
              },
            ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Edit Profile Picture'),
      ),
      body: Center(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24.0),
          child: Column(
            children: [
              // Profile picture preview
              Stack(
                children: [
                  CircleAvatar(
                    radius: 80,
                    backgroundColor: Colors.grey.shade200,
                    backgroundImage: _selectedImage != null
                        ? FileImage(_selectedImage!)
                        : (_currentProfileUrl != null
                            ? NetworkImage(_currentProfileUrl!)
                            : null) as ImageProvider?,
                    child: _selectedImage == null && _currentProfileUrl == null
                        ? Icon(
                            Icons.person,
                            size: 80,
                            color: Colors.grey.shade400,
                          )
                        : null,
                  ),
                  Positioned(
                    bottom: 0,
                    right: 0,
                    child: CircleAvatar(
                      backgroundColor: Theme.of(context).primaryColor,
                      child: IconButton(
                        icon: const Icon(Icons.camera_alt, color: Colors.white),
                        onPressed: _isUploading ? null : _showImageSourceDialog,
                      ),
                    ),
                  ),
                ],
              ),

              const SizedBox(height: 32),

              // Upload progress
              if (_isUploading) ...[
                LinearProgressIndicator(value: _uploadProgress),
                const SizedBox(height: 8),
                Text(
                  'Uploading... ${(_uploadProgress * 100).toStringAsFixed(0)}%',
                  style: TextStyle(color: Colors.grey.shade600),
                ),
                const SizedBox(height: 24),
              ],

              // Upload button
              if (_selectedImage != null && !_isUploading)
                FilledButton.icon(
                  onPressed: _uploadProfilePicture,
                  icon: const Icon(Icons.cloud_upload),
                  label: const Text('Upload Profile Picture'),
                  style: FilledButton.styleFrom(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 32,
                      vertical: 16,
                    ),
                  ),
                ),

              // Delete button
              if (_currentProfileUrl != null && !_isUploading) ...[
                const SizedBox(height: 16),
                OutlinedButton.icon(
                  onPressed: () async {
                    final confirm = await showDialog<bool>(
                      context: context,
                      builder: (context) => AlertDialog(
                        title: const Text('Delete Profile Picture'),
                        content: const Text('Are you sure?'),
                        actions: [
                          TextButton(
                            onPressed: () => Navigator.pop(context, false),
                            child: const Text('Cancel'),
                          ),
                          FilledButton(
                            onPressed: () => Navigator.pop(context, true),
                            style: FilledButton.styleFrom(
                              backgroundColor: Colors.red,
                            ),
                            child: const Text('Delete'),
                          ),
                        ],
                      ),
                    );

                    if (confirm == true) {
                      try {
                        await _storageService.deleteFileByUrl(_currentProfileUrl!);
                        setState(() {
                          _currentProfileUrl = null;
                        });
                        if (mounted) {
                          ScaffoldMessenger.of(context).showSnackBar(
                            const SnackBar(content: Text('Profile picture deleted')),
                          );
                        }
                      } catch (e) {
                        if (mounted) {
                          ScaffoldMessenger.of(context).showSnackBar(
                            SnackBar(content: Text('Delete failed: $e')),
                          );
                        }
                      }
                    }
                  },
                  icon: const Icon(Icons.delete, color: Colors.red),
                  label: const Text('Delete Profile Picture'),
                  style: OutlinedButton.styleFrom(
                    foregroundColor: Colors.red,
                    side: const BorderSide(color: Colors.red),
                  ),
                ),
              ],
            ],
          ),
        ),
      ),
    );
  }
}
```

---

## Image Gallery with Firebase Storage

```dart
// lib/screens/gallery/gallery_screen.dart
import 'package:flutter/material.dart';
import '../../services/storage_service.dart';

class GalleryScreen extends StatefulWidget {
  const GalleryScreen({super.key});

  @override
  State<GalleryScreen> createState() => _GalleryScreenState();
}

class _GalleryScreenState extends State<GalleryScreen> {
  final _storageService = StorageService();
  List<String> _imagePaths = [];
  List<String> _imageUrls = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadImages();
  }

  Future<void> _loadImages() async {
    setState(() => _isLoading = true);

    try {
      // Get list of image paths
      final paths = await _storageService.listUserImages();

      // Get download URLs for each image
      final urls = await Future.wait(
        paths.map((path) => _storageService.getDownloadUrl(path)),
      );

      setState(() {
        _imagePaths = paths;
        _imageUrls = urls;
        _isLoading = false;
      });
    } catch (e) {
      setState(() => _isLoading = false);

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Failed to load images: $e')),
        );
      }
    }
  }

  Future<void> _deleteImage(int index) async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Delete Image'),
        content: const Text('Are you sure you want to delete this image?'),
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
        await _storageService.deleteFile(_imagePaths[index]);

        setState(() {
          _imagePaths.removeAt(index);
          _imageUrls.removeAt(index);
        });

        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Image deleted')),
          );
        }
      } catch (e) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('Delete failed: $e')),
          );
        }
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('My Images'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadImages,
          ),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _imageUrls.isEmpty
              ? _buildEmptyState()
              : _buildGalleryGrid(),
    );
  }

  Widget _buildEmptyState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(
            Icons.photo_library_outlined,
            size: 100,
            color: Colors.grey.shade300,
          ),
          const SizedBox(height: 16),
          Text(
            'No images yet',
            style: Theme.of(context).textTheme.headlineSmall?.copyWith(
              color: Colors.grey.shade600,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            'Upload your first image',
            style: TextStyle(color: Colors.grey.shade500),
          ),
        ],
      ),
    );
  }

  Widget _buildGalleryGrid() {
    return GridView.builder(
      padding: const EdgeInsets.all(16),
      gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
        crossAxisCount: 3,
        crossAxisSpacing: 8,
        mainAxisSpacing: 8,
      ),
      itemCount: _imageUrls.length,
      itemBuilder: (context, index) {
        return GestureDetector(
          onTap: () => _showFullImage(index),
          onLongPress: () => _deleteImage(index),
          child: ClipRRect(
            borderRadius: BorderRadius.circular(8),
            child: Image.network(
              _imageUrls[index],
              fit: BoxFit.cover,
              loadingBuilder: (context, child, loadingProgress) {
                if (loadingProgress == null) return child;
                return Center(
                  child: CircularProgressIndicator(
                    value: loadingProgress.expectedTotalBytes != null
                        ? loadingProgress.cumulativeBytesLoaded /
                            loadingProgress.expectedTotalBytes!
                        : null,
                  ),
                );
              },
              errorBuilder: (context, error, stackTrace) {
                return Container(
                  color: Colors.grey.shade200,
                  child: const Icon(Icons.error_outline),
                );
              },
            ),
          ),
        );
      },
    );
  }

  void _showFullImage(int index) {
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (_) => Scaffold(
          appBar: AppBar(
            title: const Text('Image'),
            actions: [
              IconButton(
                icon: const Icon(Icons.delete),
                onPressed: () {
                  Navigator.pop(context);
                  _deleteImage(index);
                },
              ),
            ],
          ),
          body: Center(
            child: InteractiveViewer(
              child: Image.network(_imageUrls[index]),
            ),
          ),
        ),
      ),
    );
  }
}
```

---

## Firebase Storage Security Rules

### Default Rules (Test Mode - Insecure!)

```
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if true;  // ⚠️ ANYONE CAN ACCESS!
    }
  }
}
```

### Production Rules (Secure)

```
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    // User-specific files
    match /users/{userId}/{allPaths=**} {
      // Only the user can read/write their own files
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }

    // Public files (anyone can read)
    match /public/{allPaths=**} {
      allow read: if true;
      allow write: if request.auth != null;  // Only authenticated users can write
    }

    // Posts (owner can write, anyone can read)
    match /posts/{postId} {
      allow read: if true;
      allow write: if request.auth != null;
    }

    // Validate file size (max 5MB for images)
    match /users/{userId}/profile/{fileName} {
      allow write: if request.auth != null
                   && request.auth.uid == userId
                   && request.resource.size < 5 * 1024 * 1024;  // 5MB
    }

    // Validate file type (only images)
    match /users/{userId}/images/{fileName} {
      allow write: if request.auth != null
                   && request.auth.uid == userId
                   && request.resource.contentType.matches('image/.*');
    }
  }
}
```

### Update Rules in Firebase Console

1. Go to Firebase Console → Storage
2. Click "Rules" tab
3. Paste your security rules
4. Click "Publish"

---

## Best Practices

### ✅ DO:
1. **Compress images before upload** (use image_picker maxWidth/quality)
2. **Use unique filenames** (timestamp + random string)
3. **Organize by user ID** (`users/{userId}/...`)
4. **Validate file types and sizes** in security rules
5. **Delete old files** when uploading new ones (avoid storage bloat)
6. **Show upload progress** for better UX
7. **Handle errors gracefully** (network issues, quota exceeded)
8. **Use CDN URLs** (Firebase provides these automatically)

### ❌ DON'T:
1. **Don't upload full-resolution images** (compress first!)
2. **Don't store sensitive data** in filenames
3. **Don't allow public write access** (use authentication)
4. **Don't forget to delete old files** (costs add up)
5. **Don't upload without size limits** (prevent abuse)
6. **Don't use HTTP URLs** (always HTTPS)

---

## Pricing & Limits

### Free Tier (Spark Plan)

- **Storage**: 5 GB
- **Downloads**: 1 GB/day
- **Uploads**: 1 GB/day
- **Operations**: 50k/day

**This is enough for**:
- ~2,500 high-quality images (2MB each)
- Small to medium apps
- Learning and prototyping

### Paid Tier (Blaze Plan)

**Pay-as-you-go**:
- Storage: $0.026 per GB/month
- Downloads: $0.12 per GB
- Uploads: $0.12 per GB

**Example costs**:
- 10 GB storage = ~$0.26/month
- 10 GB downloads = ~$1.20/month
- Most indie apps: $1-5/month

---

## Quiz Time! 🧠

### Question 1
Why should you delete old files when uploading new ones?

A) It's required by Firebase
B) To save storage costs and prevent quota issues
C) To make uploads faster
D) Firebase does this automatically

### Question 2
What's the correct way to organize user files?

A) All in root folder
B) By file type only
C) By user ID (users/{userId}/...)
D) By date only

### Question 3
What's the benefit of showing upload progress?

A) It's required
B) It provides user feedback, especially for large files
C) It makes uploads faster
D) It compresses files

---

## Answer Key

### Answer 1: B
**Correct**: To save storage costs and prevent quota issues

Old files consume storage (which costs money) and count toward your quota. For example, if a user updates their profile picture 10 times, you'd be storing 10 images instead of 1. Always delete the old file before uploading a new one.

### Answer 2: C
**Correct**: By user ID (users/{userId}/...)

Organizing by user ID makes it easy to implement security rules (users can only access their own files), manage per-user quotas, and delete all user data when they delete their account. It's the industry standard pattern.

### Answer 3: B
**Correct**: It provides user feedback, especially for large files

Without progress indicators, users might think the app froze when uploading a 10MB video. Progress bars (0%, 25%, 50%, 100%) reassure users that the upload is working and show how long it will take.

---

## What's Next?

You've mastered Firebase Cloud Storage! In the next lesson, we'll learn about **Firebase Security Rules** to protect your data from unauthorized access.

**Coming up in Lesson 5: Firebase Security Rules**
- Firestore security rules
- Storage security rules
- Authentication-based access control
- Testing security rules
- Production-ready security

---

## Key Takeaways

✅ Firebase Storage handles secure file storage in the cloud
✅ Organize files by user ID (users/{userId}/...)
✅ Always compress images before uploading
✅ Use uploadFile() with progress callbacks for UX
✅ Delete old files to save storage costs
✅ Implement security rules to restrict access
✅ Firebase provides CDN URLs automatically for fast downloads
✅ Monitor usage to avoid surprise bills

**You can now build apps with cloud file storage like Instagram!** 📸
