# Module 7, Lesson 7: File Upload and Download

## What You'll Learn
By the end of this lesson, you'll understand how to upload and download files (images, videos, documents) with progress tracking, just like WhatsApp, Instagram, and Google Drive.

---

## Why This Matters

**File upload/download is essential for modern apps.**

- **WhatsApp**: Upload images, videos, documents
- **Instagram**: Upload photos and videos with stories
- **Google Drive**: Upload and download any file type
- **LinkedIn**: Upload profile pictures and resumes
- **90% of social apps** involve file uploads

In this lesson, you'll learn the same techniques used by every major app that handles media.

---

## Real-World Analogy: The Post Office

### Sending a Package (Upload)
Imagine mailing a birthday gift to your friend:
1. **Pick the item** (select file from device)
2. **Package it** (prepare file for sending)
3. **Take to post office** (upload to server)
4. **Track the package** (progress: 20%, 50%, 100%)
5. **Receive confirmation** (upload complete!)

### Receiving a Package (Download)
Getting a package from a friend:
1. **Get notification** ("You have a package!")
2. **Pick up from post office** (download from server)
3. **Track delivery** (downloading: 30%, 60%, 100%)
4. **Unpack and save** (save file to device)

**Flutter file upload/download works exactly the same way!**

---

## Types of Files You Can Handle

### 1. Images
- Profile pictures
- Photo sharing (Instagram-style)
- Product images (e-commerce)

### 2. Videos
- Short-form video (TikTok-style)
- Video messages (WhatsApp-style)

### 3. Documents
- PDFs (resumes, contracts)
- Word documents
- Excel spreadsheets

### 4. Any File Type
- Audio files
- Zip archives
- Custom formats

---

## Setting Up File Picker Packages

### Install Required Packages

```yaml
# pubspec.yaml
dependencies:
  flutter:
    sdk: flutter
  dio: ^5.7.0
  image_picker: ^1.1.2      # For images from camera/gallery
  file_picker: ^10.1.2      # For any file type
  path_provider: ^2.1.5     # For getting device directories
  permission_handler: ^11.3.1 # For permissions
```

Run:
```bash
flutter pub get
```

### Platform-Specific Setup

#### Android (android/app/src/main/AndroidManifest.xml)

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- Permissions -->
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.CAMERA"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>

    <application>
        <!-- ... -->
    </application>
</manifest>
```

#### iOS (ios/Runner/Info.plist)

```xml
<dict>
    <!-- Camera Permission -->
    <key>NSCameraUsageDescription</key>
    <string>We need camera access to take photos</string>

    <!-- Photo Library Permission -->
    <key>NSPhotoLibraryUsageDescription</key>
    <string>We need photo library access to select photos</string>

    <!-- ... -->
</dict>
```

---

## Part 1: Picking Images (image_picker)

### Basic Image Picker

```dart
// lib/services/image_picker_service.dart
import 'package:image_picker/image_picker.dart';
import 'dart:io';

class ImagePickerService {
  final ImagePicker _picker = ImagePicker();

  // Pick single image from gallery
  Future<File?> pickImageFromGallery() async {
    try {
      final XFile? image = await _picker.pickImage(
        source: ImageSource.gallery,
        maxWidth: 1920,    // Resize to max width
        maxHeight: 1080,   // Resize to max height
        imageQuality: 85,  // Compress (0-100, lower = smaller file)
      );

      if (image != null) {
        return File(image.path);
      }
      return null;
    } catch (e) {
      print('Error picking image: $e');
      return null;
    }
  }

  // Pick single image from camera
  Future<File?> pickImageFromCamera() async {
    try {
      final XFile? image = await _picker.pickImage(
        source: ImageSource.camera,
        maxWidth: 1920,
        maxHeight: 1080,
        imageQuality: 85,
      );

      if (image != null) {
        return File(image.path);
      }
      return null;
    } catch (e) {
      print('Error taking photo: $e');
      return null;
    }
  }

  // Pick multiple images from gallery
  Future<List<File>> pickMultipleImages() async {
    try {
      final List<XFile> images = await _picker.pickMultiImage(
        maxWidth: 1920,
        maxHeight: 1080,
        imageQuality: 85,
      );

      return images.map((xFile) => File(xFile.path)).toList();
    } catch (e) {
      print('Error picking multiple images: $e');
      return [];
    }
  }

  // Pick video from gallery
  Future<File?> pickVideo() async {
    try {
      final XFile? video = await _picker.pickVideo(
        source: ImageSource.gallery,
        maxDuration: const Duration(seconds: 60), // Max 60 seconds
      );

      if (video != null) {
        return File(video.path);
      }
      return null;
    } catch (e) {
      print('Error picking video: $e');
      return null;
    }
  }
}
```

### Usage Example:

```dart
final imagePickerService = ImagePickerService();

// Pick from gallery
File? image = await imagePickerService.pickImageFromGallery();
if (image != null) {
  print('Image selected: ${image.path}');
}

// Pick from camera
File? photo = await imagePickerService.pickImageFromCamera();

// Pick multiple
List<File> images = await imagePickerService.pickMultipleImages();
print('Selected ${images.length} images');
```

---

## Part 2: Picking Any File (file_picker)

```dart
// lib/services/file_picker_service.dart
import 'package:file_picker/file_picker.dart';
import 'dart:io';

class FilePickerService {
  // Pick any file
  Future<File?> pickFile() async {
    try {
      FilePickerResult? result = await FilePicker.platform.pickFiles();

      if (result != null && result.files.single.path != null) {
        return File(result.files.single.path!);
      }
      return null;
    } catch (e) {
      print('Error picking file: $e');
      return null;
    }
  }

  // Pick specific file types
  Future<File?> pickPDF() async {
    try {
      FilePickerResult? result = await FilePicker.platform.pickFiles(
        type: FileType.custom,
        allowedExtensions: ['pdf'],
      );

      if (result != null && result.files.single.path != null) {
        return File(result.files.single.path!);
      }
      return null;
    } catch (e) {
      print('Error picking PDF: $e');
      return null;
    }
  }

  // Pick images (alternative to image_picker)
  Future<File?> pickImage() async {
    try {
      FilePickerResult? result = await FilePicker.platform.pickFiles(
        type: FileType.image,
      );

      if (result != null && result.files.single.path != null) {
        return File(result.files.single.path!);
      }
      return null;
    } catch (e) {
      print('Error picking image: $e');
      return null;
    }
  }

  // Pick multiple files
  Future<List<File>> pickMultipleFiles() async {
    try {
      FilePickerResult? result = await FilePicker.platform.pickFiles(
        allowMultiple: true,
      );

      if (result != null) {
        return result.paths
            .where((path) => path != null)
            .map((path) => File(path!))
            .toList();
      }
      return [];
    } catch (e) {
      print('Error picking multiple files: $e');
      return [];
    }
  }
}
```

---

## Part 3: Uploading Files with Dio

### Upload Service with Progress Tracking

```dart
// lib/services/upload_service.dart
import 'package:dio/dio.dart';
import 'dart:io';

class UploadService {
  final Dio _dio = Dio(
    BaseOptions(
      baseUrl: 'https://api.example.com',
      connectTimeout: const Duration(seconds: 30),
      receiveTimeout: const Duration(seconds: 30),
    ),
  );

  // Upload single image
  Future<Map<String, dynamic>> uploadImage(
    File imageFile, {
    Function(int sent, int total)? onProgress,
  }) async {
    try {
      // Create form data
      final fileName = imageFile.path.split('/').last;
      final formData = FormData.fromMap({
        'image': await MultipartFile.fromFile(
          imageFile.path,
          filename: fileName,
        ),
        'description': 'Profile photo', // Optional metadata
      });

      // Upload with progress tracking
      final response = await _dio.post(
        '/upload/image',
        data: formData,
        onSendProgress: (sent, total) {
          if (onProgress != null) {
            onProgress(sent, total);
          }
          print('Upload progress: ${(sent / total * 100).toStringAsFixed(0)}%');
        },
      );

      return response.data;
    } on DioException catch (e) {
      throw _handleError(e);
    }
  }

  // Upload multiple images
  Future<Map<String, dynamic>> uploadMultipleImages(
    List<File> imageFiles, {
    Function(int sent, int total)? onProgress,
  }) async {
    try {
      final formData = FormData();

      // Add multiple files
      for (var i = 0; i < imageFiles.length; i++) {
        final fileName = imageFiles[i].path.split('/').last;
        formData.files.add(
          MapEntry(
            'images[$i]', // Key for each image
            await MultipartFile.fromFile(
              imageFiles[i].path,
              filename: fileName,
            ),
          ),
        );
      }

      final response = await _dio.post(
        '/upload/images',
        data: formData,
        onSendProgress: (sent, total) {
          if (onProgress != null) {
            onProgress(sent, total);
          }
        },
      );

      return response.data;
    } on DioException catch (e) {
      throw _handleError(e);
    }
  }

  // Upload any file (PDF, document, etc.)
  Future<Map<String, dynamic>> uploadFile(
    File file, {
    required String fileType, // 'pdf', 'document', etc.
    Function(int sent, int total)? onProgress,
  }) async {
    try {
      final fileName = file.path.split('/').last;
      final formData = FormData.fromMap({
        'file': await MultipartFile.fromFile(
          file.path,
          filename: fileName,
        ),
        'type': fileType,
      });

      final response = await _dio.post(
        '/upload/file',
        data: formData,
        onSendProgress: (sent, total) {
          if (onProgress != null) {
            onProgress(sent, total);
          }
        },
      );

      return response.data;
    } on DioException catch (e) {
      throw _handleError(e);
    }
  }

  String _handleError(DioException e) {
    switch (e.type) {
      case DioExceptionType.connectionTimeout:
      case DioExceptionType.sendTimeout:
        return 'Upload timeout. Check your internet connection.';
      case DioExceptionType.badResponse:
        return 'Server error: ${e.response?.statusCode}';
      default:
        return 'Upload failed: ${e.message}';
    }
  }
}
```

---

## Part 4: Downloading Files with Progress Tracking

```dart
// lib/services/download_service.dart
import 'package:dio/dio.dart';
import 'package:path_provider/path_provider.dart';
import 'dart:io';

class DownloadService {
  final Dio _dio = Dio();

  // Download file with progress tracking
  Future<File> downloadFile(
    String url,
    String fileName, {
    Function(int received, int total)? onProgress,
  }) async {
    try {
      // Get device's download directory
      final directory = await getApplicationDocumentsDirectory();
      final savePath = '${directory.path}/$fileName';

      await _dio.download(
        url,
        savePath,
        onReceiveProgress: (received, total) {
          if (total != -1) {
            if (onProgress != null) {
              onProgress(received, total);
            }
            print(
              'Download progress: ${(received / total * 100).toStringAsFixed(0)}%',
            );
          }
        },
      );

      return File(savePath);
    } catch (e) {
      throw Exception('Download failed: $e');
    }
  }

  // Download image
  Future<File> downloadImage(
    String url, {
    Function(int received, int total)? onProgress,
  }) async {
    final fileName = 'image_${DateTime.now().millisecondsSinceEpoch}.jpg';
    return downloadFile(url, fileName, onProgress: onProgress);
  }

  // Download PDF
  Future<File> downloadPDF(
    String url,
    String fileName, {
    Function(int received, int total)? onProgress,
  }) async {
    if (!fileName.endsWith('.pdf')) {
      fileName = '$fileName.pdf';
    }
    return downloadFile(url, fileName, onProgress: onProgress);
  }
}
```

---

## Complete Example: Image Upload App

Let's build a complete app with image selection and upload:

```dart
// lib/screens/image_upload_screen.dart
import 'package:flutter/material.dart';
import 'dart:io';
import '../services/image_picker_service.dart';
import '../services/upload_service.dart';

class ImageUploadScreen extends StatefulWidget {
  const ImageUploadScreen({super.key});

  @override
  State<ImageUploadScreen> createState() => _ImageUploadScreenState();
}

class _ImageUploadScreenState extends State<ImageUploadScreen> {
  final _imagePickerService = ImagePickerService();
  final _uploadService = UploadService();

  File? _selectedImage;
  bool _isUploading = false;
  double _uploadProgress = 0.0;
  String? _uploadedImageUrl;

  Future<void> _pickImageFromGallery() async {
    final image = await _imagePickerService.pickImageFromGallery();
    if (image != null) {
      setState(() {
        _selectedImage = image;
        _uploadedImageUrl = null;
      });
    }
  }

  Future<void> _pickImageFromCamera() async {
    final image = await _imagePickerService.pickImageFromCamera();
    if (image != null) {
      setState(() {
        _selectedImage = image;
        _uploadedImageUrl = null;
      });
    }
  }

  Future<void> _uploadImage() async {
    if (_selectedImage == null) return;

    setState(() {
      _isUploading = true;
      _uploadProgress = 0.0;
    });

    try {
      final result = await _uploadService.uploadImage(
        _selectedImage!,
        onProgress: (sent, total) {
          setState(() {
            _uploadProgress = sent / total;
          });
        },
      );

      setState(() {
        _isUploading = false;
        _uploadedImageUrl = result['url']; // Assuming server returns URL
      });

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Image uploaded successfully!')),
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
        title: const Text('Select Image Source'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            ListTile(
              leading: const Icon(Icons.photo_library),
              title: const Text('Gallery'),
              onTap: () {
                Navigator.pop(context);
                _pickImageFromGallery();
              },
            ),
            ListTile(
              leading: const Icon(Icons.camera_alt),
              title: const Text('Camera'),
              onTap: () {
                Navigator.pop(context);
                _pickImageFromCamera();
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
        title: const Text('Image Upload'),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            // Image preview
            if (_selectedImage != null)
              Container(
                height: 300,
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(12),
                  border: Border.all(color: Colors.grey.shade300),
                ),
                child: ClipRRect(
                  borderRadius: BorderRadius.circular(12),
                  child: Image.file(
                    _selectedImage!,
                    fit: BoxFit.cover,
                  ),
                ),
              )
            else
              Container(
                height: 300,
                decoration: BoxDecoration(
                  color: Colors.grey.shade100,
                  borderRadius: BorderRadius.circular(12),
                  border: Border.all(color: Colors.grey.shade300),
                ),
                child: Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Icon(
                        Icons.image_outlined,
                        size: 80,
                        color: Colors.grey.shade400,
                      ),
                      const SizedBox(height: 16),
                      Text(
                        'No image selected',
                        style: TextStyle(
                          color: Colors.grey.shade600,
                          fontSize: 16,
                        ),
                      ),
                    ],
                  ),
                ),
              ),

            const SizedBox(height: 24),

            // Select image button
            OutlinedButton.icon(
              onPressed: _isUploading ? null : _showImageSourceDialog,
              icon: const Icon(Icons.add_photo_alternate),
              label: const Text('Select Image'),
              style: OutlinedButton.styleFrom(
                padding: const EdgeInsets.symmetric(vertical: 16),
              ),
            ),

            const SizedBox(height: 16),

            // Upload button
            FilledButton.icon(
              onPressed: (_selectedImage != null && !_isUploading)
                  ? _uploadImage
                  : null,
              icon: _isUploading
                  ? const SizedBox(
                      width: 20,
                      height: 20,
                      child: CircularProgressIndicator(
                        strokeWidth: 2,
                        color: Colors.white,
                      ),
                    )
                  : const Icon(Icons.cloud_upload),
              label: Text(_isUploading ? 'Uploading...' : 'Upload Image'),
              style: FilledButton.styleFrom(
                padding: const EdgeInsets.symmetric(vertical: 16),
              ),
            ),

            // Upload progress
            if (_isUploading) ...[
              const SizedBox(height: 16),
              LinearProgressIndicator(value: _uploadProgress),
              const SizedBox(height: 8),
              Text(
                '${(_uploadProgress * 100).toStringAsFixed(0)}% uploaded',
                textAlign: TextAlign.center,
                style: TextStyle(color: Colors.grey.shade600),
              ),
            ],

            // Success message
            if (_uploadedImageUrl != null) ...[
              const SizedBox(height: 24),
              Container(
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: Colors.green.shade50,
                  borderRadius: BorderRadius.circular(8),
                  border: Border.all(color: Colors.green.shade200),
                ),
                child: Row(
                  children: [
                    Icon(Icons.check_circle, color: Colors.green.shade700),
                    const SizedBox(width: 12),
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            'Upload successful!',
                            style: TextStyle(
                              color: Colors.green.shade900,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                          const SizedBox(height: 4),
                          Text(
                            _uploadedImageUrl!,
                            style: TextStyle(
                              color: Colors.green.shade700,
                              fontSize: 12,
                            ),
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }
}
```

---

## Multiple Image Upload Example

```dart
// lib/screens/multiple_image_upload_screen.dart
import 'package:flutter/material.dart';
import 'dart:io';
import '../services/image_picker_service.dart';
import '../services/upload_service.dart';

class MultipleImageUploadScreen extends StatefulWidget {
  const MultipleImageUploadScreen({super.key});

  @override
  State<MultipleImageUploadScreen> createState() =>
      _MultipleImageUploadScreenState();
}

class _MultipleImageUploadScreenState extends State<MultipleImageUploadScreen> {
  final _imagePickerService = ImagePickerService();
  final _uploadService = UploadService();

  final List<File> _selectedImages = [];
  bool _isUploading = false;
  double _uploadProgress = 0.0;

  Future<void> _pickMultipleImages() async {
    final images = await _imagePickerService.pickMultipleImages();
    if (images.isNotEmpty) {
      setState(() {
        _selectedImages.addAll(images);
      });
    }
  }

  void _removeImage(int index) {
    setState(() {
      _selectedImages.removeAt(index);
    });
  }

  Future<void> _uploadImages() async {
    if (_selectedImages.isEmpty) return;

    setState(() {
      _isUploading = true;
      _uploadProgress = 0.0;
    });

    try {
      await _uploadService.uploadMultipleImages(
        _selectedImages,
        onProgress: (sent, total) {
          setState(() {
            _uploadProgress = sent / total;
          });
        },
      );

      setState(() {
        _isUploading = false;
        _selectedImages.clear();
      });

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('${_selectedImages.length} images uploaded!'),
          ),
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

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Upload (${_selectedImages.length} images)'),
        actions: [
          if (_selectedImages.isNotEmpty && !_isUploading)
            IconButton(
              icon: const Icon(Icons.delete_outline),
              tooltip: 'Clear all',
              onPressed: () {
                setState(() {
                  _selectedImages.clear();
                });
              },
            ),
        ],
      ),
      body: Column(
        children: [
          // Image grid
          Expanded(
            child: _selectedImages.isEmpty
                ? Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(
                          Icons.photo_library_outlined,
                          size: 80,
                          color: Colors.grey.shade400,
                        ),
                        const SizedBox(height: 16),
                        Text(
                          'No images selected',
                          style: TextStyle(
                            color: Colors.grey.shade600,
                            fontSize: 16,
                          ),
                        ),
                      ],
                    ),
                  )
                : GridView.builder(
                    padding: const EdgeInsets.all(16),
                    gridDelegate:
                        const SliverGridDelegateWithFixedCrossAxisCount(
                      crossAxisCount: 3,
                      crossAxisSpacing: 8,
                      mainAxisSpacing: 8,
                    ),
                    itemCount: _selectedImages.length,
                    itemBuilder: (context, index) {
                      return Stack(
                        fit: StackFit.expand,
                        children: [
                          ClipRRect(
                            borderRadius: BorderRadius.circular(8),
                            child: Image.file(
                              _selectedImages[index],
                              fit: BoxFit.cover,
                            ),
                          ),
                          if (!_isUploading)
                            Positioned(
                              top: 4,
                              right: 4,
                              child: GestureDetector(
                                onTap: () => _removeImage(index),
                                child: Container(
                                  padding: const EdgeInsets.all(4),
                                  decoration: BoxDecoration(
                                    color: Colors.red,
                                    shape: BoxShape.circle,
                                  ),
                                  child: const Icon(
                                    Icons.close,
                                    color: Colors.white,
                                    size: 16,
                                  ),
                                ),
                              ),
                            ),
                        ],
                      );
                    },
                  ),
          ),

          // Upload progress
          if (_isUploading) ...[
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 24),
              child: Column(
                children: [
                  LinearProgressIndicator(value: _uploadProgress),
                  const SizedBox(height: 8),
                  Text(
                    'Uploading ${_selectedImages.length} images... ${(_uploadProgress * 100).toStringAsFixed(0)}%',
                    style: TextStyle(color: Colors.grey.shade600),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),
          ],

          // Action buttons
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: Row(
              children: [
                Expanded(
                  child: OutlinedButton.icon(
                    onPressed: _isUploading ? null : _pickMultipleImages,
                    icon: const Icon(Icons.add_photo_alternate),
                    label: const Text('Add Images'),
                    style: OutlinedButton.styleFrom(
                      padding: const EdgeInsets.symmetric(vertical: 16),
                    ),
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: FilledButton.icon(
                    onPressed: (_selectedImages.isNotEmpty && !_isUploading)
                        ? _uploadImages
                        : null,
                    icon: _isUploading
                        ? const SizedBox(
                            width: 20,
                            height: 20,
                            child: CircularProgressIndicator(
                              strokeWidth: 2,
                              color: Colors.white,
                            ),
                          )
                        : const Icon(Icons.cloud_upload),
                    label: const Text('Upload'),
                    style: FilledButton.styleFrom(
                      padding: const EdgeInsets.symmetric(vertical: 16),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
```

---

## Best Practices

### ✅ DO:
1. **Compress images before upload** (use maxWidth, maxHeight, imageQuality)
2. **Show upload progress** (users need feedback on long uploads)
3. **Handle permission denials gracefully**
4. **Set appropriate timeouts** (30-60 seconds for file uploads)
5. **Validate file types** before upload (check extensions)
6. **Limit file sizes** (e.g., max 10MB per image)
7. **Show file names and sizes** in the UI
8. **Allow users to cancel uploads**

### ❌ DON'T:
1. **Don't upload full-resolution images** (wastes bandwidth and storage)
2. **Don't upload without showing progress** (bad UX for large files)
3. **Don't forget to handle errors** (network issues, server errors)
4. **Don't upload to HTTP endpoints** (always use HTTPS for security)
5. **Don't store uploaded files in app memory** (use temporary directories)

---

## Testing File Upload (Without Real Backend)

You can test file uploads using these free services:

### 1. File.io (Temporary Upload)
```dart
final response = await dio.post(
  'https://file.io',
  data: formData,
  onSendProgress: (sent, total) {
    print('Progress: ${(sent / total * 100).toStringAsFixed(0)}%');
  },
);
// Returns: {"success":true,"key":"...","link":"..."}
```

### 2. Imgur (Image Upload)
Sign up for free API key at https://api.imgur.com/oauth2/addclient

```dart
final dio = Dio();
dio.options.headers['Authorization'] = 'Client-ID YOUR_CLIENT_ID';

final formData = FormData.fromMap({
  'image': await MultipartFile.fromFile(imageFile.path),
});

final response = await dio.post(
  'https://api.imgur.com/3/image',
  data: formData,
);
// Returns image URL
```

---

## Quiz Time! 🧠

Test your understanding:

### Question 1
Why should you compress images before uploading?

A) To make the app faster
B) To reduce bandwidth usage, server storage, and upload time
C) Because Flutter requires it
D) To improve image quality

### Question 2
What's the difference between image_picker and file_picker?

A) They're the same
B) image_picker is for images/videos only, file_picker handles any file type
C) file_picker is faster
D) image_picker only works on Android

### Question 3
Why is progress tracking important for file uploads?

A) It's required by the API
B) It provides user feedback, especially for large files, so they know the upload isn't stuck
C) It makes uploads faster
D) It's only needed for videos

---

## Answer Key

### Answer 1: B
**Correct**: To reduce bandwidth usage, server storage, and upload time

Full-resolution images can be 5-10MB each. By compressing to 85% quality and resizing to 1920x1080, you can reduce this to 500KB-1MB without noticeable quality loss. This saves bandwidth, speeds up uploads, and reduces server storage costs.

### Answer 2: B
**Correct**: image_picker is for images/videos only, file_picker handles any file type

image_picker is optimized for images and videos with built-in camera support and image quality settings. file_picker is more general-purpose and can handle PDFs, documents, ZIP files, and any other file type.

### Answer 3: B
**Correct**: It provides user feedback, especially for large files, so they know the upload isn't stuck

Without progress tracking, users might think the app froze when uploading a large file (e.g., 50MB video). Progress indicators (20%, 50%, 100%) reassure users that the upload is working and show how long it will take.

---

## What's Next?

You've learned how to upload and download files with progress tracking. In the next lesson, we'll build a **Mini-Project** that combines everything from Module 7!

**Coming up in Lesson 8: Mini-Project - Complete Social Media App**
- Combine all networking concepts
- User authentication
- Feed with pagination
- Image upload (post creation)
- Comments and likes
- Complete production-ready app

---

## Key Takeaways

✅ image_picker handles images/videos from camera/gallery
✅ file_picker handles any file type (PDFs, documents, etc.)
✅ Always compress images before upload (maxWidth, maxHeight, imageQuality)
✅ Use Dio's FormData and MultipartFile for uploads
✅ Track progress with onSendProgress and onReceiveProgress callbacks
✅ Show progress indicators for better UX (especially for large files)
✅ Handle errors gracefully (network issues, permission denials)
✅ Set appropriate timeouts (30-60 seconds for file uploads)

**You're now ready to build apps with file upload/download like Instagram and WhatsApp!** 🎉
