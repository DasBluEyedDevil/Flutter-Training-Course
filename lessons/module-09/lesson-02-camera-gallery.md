# Lesson 2: Camera and Photo Gallery

## What You'll Learn
- Accessing device camera and photo gallery
- Using the image_picker package
- Handling permissions on Android and iOS
- Displaying and saving selected images
- Taking photos vs selecting from gallery
- Building a complete photo app

## Concept First: Why Camera Access Matters

### Real-World Analogy
Think of your app accessing the camera like a valet service at a hotel. The valet (your app) needs **permission** to drive your car (access the camera). Once you give permission, the valet can:
- Use your car for a specific task (take a photo)
- Return it when done (give you the image)
- But can't just take your car whenever they want (must ask each time)

Modern phones treat camera and photos as **private property** - apps must explicitly ask permission and users can revoke it anytime.

### Why This Matters
Camera and gallery access enables powerful features:

1. **Profile Pictures**: Let users personalize their accounts
2. **Content Creation**: Social media, blogging, marketplace apps
3. **Document Scanning**: Receipt capture, ID verification
4. **Visual Search**: Take a photo to search for products
5. **AR Features**: Augmented reality experiences

According to App Annie, photo/camera features increase user engagement by 35% in social apps!

---

## Setting Up

### 1. Add Dependencies

**pubspec.yaml:**
```yaml
dependencies:
  flutter:
    sdk: flutter
  image_picker: ^1.3.0  # Latest version with Android Photo Picker
  permission_handler: ^11.3.1  # For permission management
```

Run:
```bash
flutter pub get
```

### 2. Android Configuration

**android/app/src/main/AndroidManifest.xml:**
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- Required permissions -->
    <uses-permission android:name="android.permission.CAMERA"/>
    <uses-permission android:name="android.permission.READ_MEDIA_IMAGES"/>  <!-- Android 13+ -->
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
        android:maxSdkVersion="32"/>  <!-- Android 12 and below -->

    <!-- Optional: If you want to save photos to gallery -->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        android:maxSdkVersion="28"/>

    <application>
        <!-- ... -->
    </application>
</manifest>
```

### 3. iOS Configuration

**ios/Runner/Info.plist:**
```xml
<dict>
    <!-- Camera permission description -->
    <key>NSCameraUsageDescription</key>
    <string>We need access to your camera to take photos for your profile.</string>

    <!-- Photo library permission description -->
    <key>NSPhotoLibraryUsageDescription</key>
    <string>We need access to your photo library to select images.</string>

    <!-- For iOS 14+ -->
    <key>NSPhotoLibraryAddUsageDescription</key>
    <string>We need permission to save photos to your library.</string>
</dict>
```

**Important:** Customize the permission messages to explain **why** your app needs access!

---

## Basic Usage: ImagePicker

### Simple Example: Pick from Gallery

```dart
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'dart:io';

class SimpleImagePickerScreen extends StatefulWidget {
  @override
  State<SimpleImagePickerScreen> createState() => _SimpleImagePickerScreenState();
}

class _SimpleImagePickerScreenState extends State<SimpleImagePickerScreen> {
  File? _selectedImage;
  final ImagePicker _picker = ImagePicker();

  Future<void> _pickImageFromGallery() async {
    try {
      // Pick a single image from gallery
      final XFile? image = await _picker.pickImage(
        source: ImageSource.gallery,
        imageQuality: 80,  // Compress to 80% quality
        maxWidth: 1920,    // Limit size to save memory
      );

      if (image != null) {
        setState(() {
          _selectedImage = File(image.path);
        });
      }
    } catch (e) {
      print('Error picking image: $e');
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Failed to pick image: $e')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Image Picker Demo')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            // Display selected image or placeholder
            Container(
              width: 300,
              height: 300,
              decoration: BoxDecoration(
                color: Colors.grey[200],
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: Colors.grey),
              ),
              child: _selectedImage != null
                  ? ClipRRect(
                      borderRadius: BorderRadius.circular(12),
                      child: Image.file(
                        _selectedImage!,
                        fit: BoxFit.cover,
                      ),
                    )
                  : Icon(Icons.image, size: 100, color: Colors.grey),
            ),

            SizedBox(height: 20),

            ElevatedButton.icon(
              onPressed: _pickImageFromGallery,
              icon: Icon(Icons.photo_library),
              label: Text('Pick from Gallery'),
            ),
          ],
        ),
      ),
    );
  }
}
```

### Taking Photos with Camera

```dart
Future<void> _takePhoto() async {
  try {
    final XFile? photo = await _picker.pickImage(
      source: ImageSource.camera,  // Use camera instead of gallery
      imageQuality: 80,
      preferredCameraDevice: CameraDevice.rear,  // Front or rear camera
    );

    if (photo != null) {
      setState(() {
        _selectedImage = File(photo.path);
      });
    }
  } catch (e) {
    print('Error taking photo: $e');
  }
}
```

### Picking Multiple Images (Android 13+, iOS 14+)

```dart
Future<void> _pickMultipleImages() async {
  try {
    final List<XFile> images = await _picker.pickMultipleImages(
      imageQuality: 80,
      maxWidth: 1920,
    );

    if (images.isNotEmpty) {
      setState(() {
        _imageFiles = images.map((img) => File(img.path)).toList();
      });

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('${images.length} images selected')),
      );
    }
  } catch (e) {
    print('Error picking multiple images: $e');
  }
}
```

---

## Complete Example: Photo Profile Editor

```dart
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:permission_handler/permission_handler.dart';
import 'dart:io';

class PhotoProfileScreen extends StatefulWidget {
  @override
  State<PhotoProfileScreen> createState() => _PhotoProfileScreenState();
}

class _PhotoProfileScreenState extends State<PhotoProfileScreen> {
  File? _profileImage;
  final ImagePicker _picker = ImagePicker();
  bool _isLoading = false;

  // Check and request permissions
  Future<bool> _requestPermission(Permission permission) async {
    final status = await permission.status;

    if (status.isGranted) {
      return true;
    } else if (status.isDenied) {
      final result = await permission.request();
      return result.isGranted;
    } else if (status.isPermanentlyDenied) {
      // User permanently denied - open settings
      await openAppSettings();
      return false;
    }

    return false;
  }

  Future<void> _pickImageSource() async {
    // Show dialog to choose camera or gallery
    final ImageSource? source = await showModalBottomSheet<ImageSource>(
      context: context,
      builder: (context) => SafeArea(
        child: Wrap(
          children: [
            ListTile(
              leading: Icon(Icons.camera_alt, color: Colors.blue),
              title: Text('Take Photo'),
              onTap: () => Navigator.pop(context, ImageSource.camera),
            ),
            ListTile(
              leading: Icon(Icons.photo_library, color: Colors.green),
              title: Text('Choose from Gallery'),
              onTap: () => Navigator.pop(context, ImageSource.gallery),
            ),
            if (_profileImage != null)
              ListTile(
                leading: Icon(Icons.delete, color: Colors.red),
                title: Text('Remove Photo'),
                onTap: () {
                  setState(() => _profileImage = null);
                  Navigator.pop(context);
                },
              ),
          ],
        ),
      ),
    );

    if (source != null) {
      await _pickImage(source);
    }
  }

  Future<void> _pickImage(ImageSource source) async {
    setState(() => _isLoading = true);

    try {
      // Request appropriate permission
      final permission = source == ImageSource.camera
          ? Permission.camera
          : Permission.photos;

      final hasPermission = await _requestPermission(permission);

      if (!hasPermission) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Permission denied. Please enable in settings.'),
            action: SnackBarAction(
              label: 'Settings',
              onPressed: () => openAppSettings(),
            ),
          ),
        );
        return;
      }

      // Pick the image
      final XFile? image = await _picker.pickImage(
        source: source,
        imageQuality: 85,
        maxWidth: 1024,
        maxHeight: 1024,
      );

      if (image != null) {
        setState(() {
          _profileImage = File(image.path);
        });

        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Photo updated successfully!')),
        );
      }
    } catch (e) {
      print('Error: $e');
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Error: ${e.toString()}')),
      );
    } finally {
      setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Edit Profile Photo'),
        actions: [
          if (_profileImage != null)
            TextButton(
              onPressed: () {
                // Save photo (implement your save logic)
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text('Photo saved!')),
                );
              },
              child: Text('SAVE', style: TextStyle(color: Colors.white)),
            ),
        ],
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            // Profile photo display
            Stack(
              children: [
                CircleAvatar(
                  radius: 100,
                  backgroundColor: Colors.grey[300],
                  backgroundImage: _profileImage != null
                      ? FileImage(_profileImage!)
                      : null,
                  child: _profileImage == null
                      ? Icon(Icons.person, size: 80, color: Colors.grey[600])
                      : null,
                ),

                // Loading indicator
                if (_isLoading)
                  Positioned.fill(
                    child: Container(
                      decoration: BoxDecoration(
                        shape: BoxShape.circle,
                        color: Colors.black45,
                      ),
                      child: Center(
                        child: CircularProgressIndicator(color: Colors.white),
                      ),
                    ),
                  ),

                // Edit button overlay
                Positioned(
                  bottom: 0,
                  right: 0,
                  child: GestureDetector(
                    onTap: _isLoading ? null : _pickImageSource,
                    child: Container(
                      padding: EdgeInsets.all(8),
                      decoration: BoxDecoration(
                        color: Theme.of(context).primaryColor,
                        shape: BoxShape.circle,
                        border: Border.all(color: Colors.white, width: 3),
                      ),
                      child: Icon(Icons.camera_alt, color: Colors.white, size: 24),
                    ),
                  ),
                ),
              ],
            ),

            SizedBox(height: 40),

            Text(
              'Tap the camera icon to update your photo',
              style: TextStyle(color: Colors.grey[600], fontSize: 16),
            ),

            SizedBox(height: 20),

            // Alternative: Large button
            ElevatedButton.icon(
              onPressed: _isLoading ? null : _pickImageSource,
              icon: Icon(Icons.add_a_photo),
              label: Text(_profileImage == null ? 'Add Photo' : 'Change Photo'),
              style: ElevatedButton.styleFrom(
                padding: EdgeInsets.symmetric(horizontal: 32, vertical: 16),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
```

**Key Features:**
- ✅ Permission handling with fallback to settings
- ✅ Choose camera or gallery via bottom sheet
- ✅ Loading indicator during processing
- ✅ Remove photo option
- ✅ Circular avatar display
- ✅ Image compression to save memory

---

## Advanced: Multiple Image Grid

```dart
class MultiImagePickerScreen extends StatefulWidget {
  @override
  State<MultiImagePickerScreen> createState() => _MultiImagePickerScreenState();
}

class _MultiImagePickerScreenState extends State<MultiImagePickerScreen> {
  List<File> _images = [];
  final ImagePicker _picker = ImagePicker();

  Future<void> _pickMultipleImages() async {
    try {
      final List<XFile> pickedImages = await _picker.pickMultipleImages(
        imageQuality: 80,
        maxWidth: 1920,
      );

      setState(() {
        _images.addAll(pickedImages.map((img) => File(img.path)));
      });
    } catch (e) {
      print('Error: $e');
    }
  }

  void _removeImage(int index) {
    setState(() {
      _images.removeAt(index);
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Photo Gallery (${_images.length})'),
        actions: [
          if (_images.isNotEmpty)
            IconButton(
              icon: Icon(Icons.delete_sweep),
              onPressed: () {
                setState(() => _images.clear());
              },
            ),
        ],
      ),
      body: _images.isEmpty
          ? Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.photo_library_outlined, size: 100, color: Colors.grey),
                  SizedBox(height: 20),
                  Text(
                    'No images selected',
                    style: TextStyle(fontSize: 18, color: Colors.grey),
                  ),
                ],
              ),
            )
          : GridView.builder(
              padding: EdgeInsets.all(8),
              gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 3,
                crossAxisSpacing: 8,
                mainAxisSpacing: 8,
              ),
              itemCount: _images.length,
              itemBuilder: (context, index) {
                return Stack(
                  fit: StackFit.expand,
                  children: [
                    // Image
                    ClipRRect(
                      borderRadius: BorderRadius.circular(8),
                      child: Image.file(
                        _images[index],
                        fit: BoxFit.cover,
                      ),
                    ),

                    // Remove button
                    Positioned(
                      top: 4,
                      right: 4,
                      child: GestureDetector(
                        onTap: () => _removeImage(index),
                        child: Container(
                          padding: EdgeInsets.all(4),
                          decoration: BoxDecoration(
                            color: Colors.black54,
                            shape: BoxShape.circle,
                          ),
                          child: Icon(Icons.close, color: Colors.white, size: 20),
                        ),
                      ),
                    ),
                  ],
                );
              },
            ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: _pickMultipleImages,
        icon: Icon(Icons.add_photo_alternate),
        label: Text('Add Photos'),
      ),
    );
  }
}
```

---

## Platform Differences

### Android 13+ (API 33+)
- Uses the new **Android Photo Picker** (privacy-focused)
- Users can select photos without granting full storage access
- More secure and privacy-friendly

### Android 12 and Below
- Uses traditional file picker
- Requires `READ_EXTERNAL_STORAGE` permission

### iOS 14+
- Uses **PHPicker** (privacy-focused)
- Similar to Android Photo Picker
- Users can select specific photos without granting full library access

---

## Best Practices

1. **Always Compress Images**
   ```dart
   await _picker.pickImage(
     source: ImageSource.gallery,
     imageQuality: 80,  // 80% quality is usually enough
     maxWidth: 1920,    // Limit dimensions
   );
   ```

2. **Handle Permissions Gracefully**
   - Explain **why** you need permission (in Info.plist/AndroidManifest)
   - Provide fallback if permission denied
   - Guide users to settings if permanently denied

3. **Dispose of Large Images**
   ```dart
   @override
   void dispose() {
     _selectedImage?.delete();  // Clean up temp files
     super.dispose();
   }
   ```

4. **Show Loading Indicators**
   - Picking/compressing images takes time
   - Always show progress to user

5. **Validate Image Files**
   ```dart
   Future<bool> _isValidImage(File file) async {
     final bytes = await file.length();
     final maxSize = 10 * 1024 * 1024;  // 10 MB

     if (bytes > maxSize) {
       ScaffoldMessenger.of(context).showSnackBar(
         SnackBar(content: Text('Image too large! Max 10 MB')),
       );
       return false;
     }

     return true;
   }
   ```

---

## Common Issues & Solutions

**Issue 1: "Lost connection to device" when using camera**
- **Solution**: Run on physical device, not simulator
- Camera doesn't work in iOS Simulator

**Issue 2: Permission permanently denied**
- **Solution**: Guide user to app settings
  ```dart
  await openAppSettings();
  ```

**Issue 3: Large images cause memory issues**
- **Solution**: Always use `maxWidth`, `maxHeight`, `imageQuality`

**Issue 4: Images don't persist after app restart**
- **Solution**: Copy from temp directory to app documents
  ```dart
  final appDir = await getApplicationDocumentsDirectory();
  final fileName = basename(image.path);
  final savedImage = await File(image.path).copy('${appDir.path}/$fileName');
  ```

---

## Quiz

**Question 1:** Which ImageSource would you use to let users take a new photo?
A) ImageSource.gallery
B) ImageSource.camera
C) ImageSource.files
D) ImageSource.photos

**Question 2:** Why should you compress images before uploading?
A) To make them look better
B) To reduce memory usage and upload time
C) Because it's required by image_picker
D) To increase image quality

**Question 3:** What happens if a user permanently denies camera permission?
A) The app crashes
B) You can force enable it programmatically
C) You should guide them to app settings to enable it manually
D) The permission request will keep showing

---

## Exercise: Mini Photo Editor

Build a screen that:
1. Lets users pick an image from gallery or camera
2. Displays the image with filter options (grayscale, sepia, etc.)
3. Has a "Save" button that shows a success message
4. Handles all permissions properly

**Bonus Challenge:**
- Add image rotation (90° increments)
- Add crop functionality
- Save edited image to device

**Hint:** Use the `image` package for filters:
```yaml
dependencies:
  image: ^4.2.0
```

---

## Summary

You've mastered camera and gallery access in Flutter! Here's what we covered:

- **Setup**: Platform-specific permissions (Android & iOS)
- **ImagePicker**: Simple API for camera and gallery
- **Permissions**: Proper permission handling with fallbacks
- **Multiple Images**: Picking and displaying image grids
- **Best Practices**: Compression, validation, and error handling

With these skills, you can build photo-centric features for profiles, social media, marketplaces, and more!

---

## Answer Key

**Answer 1:** B) ImageSource.camera

`ImageSource.camera` opens the device camera to take a new photo. `ImageSource.gallery` opens the photo library/gallery to select existing photos.

**Answer 2:** B) To reduce memory usage and upload time

Compressing images (via `imageQuality`, `maxWidth`, `maxHeight`) reduces file size significantly, saving memory and making uploads faster. High-resolution photos can be 5-10 MB; compressed versions might be 500 KB.

**Answer 3:** C) You should guide them to app settings to enable it manually

When permanently denied, you cannot request permission again. Use `openAppSettings()` from `permission_handler` to help users navigate to settings where they can manually enable permissions.
