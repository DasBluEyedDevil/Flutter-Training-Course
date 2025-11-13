# 🚀 Quick Start Guide

## What Was Built?

A complete **Flutter Training Course Platform** - a standalone Java desktop application that provides:

✅ **Interactive GUI** - JavaFX-based interface with navigation and progress tracking
✅ **95+ Planned Lessons** - Comprehensive curriculum from beginner to full-stack
✅ **7 Sample Lessons** - Fully written lessons to demonstrate the platform
✅ **Progress Tracking** - Automatic save/load of lesson completion
✅ **Markdown Rendering** - Beautiful HTML rendering with syntax highlighting

## 📂 What's Included?

### Sample Lessons Created:

**Module 0: Setup & First Steps**
1. Installing Flutter & Dart SDK
2. Setting Up Your Editor (VS Code)
3. Running Your First "Hello World"

**Module 1: The Dart Language**
1. What is Code? (The Recipe Analogy)
2. Storing Information (Variables)
3. Making Decisions (if/else)

**Module 2: Your First Flutter App**
1. What Happens When You Run an App?

### Application Features:

- 🎨 **Modern UI**: Clean interface with navigation tree
- 📊 **Progress Dashboard**: See your completion percentage
- 📝 **Lesson Viewer**: WebView renders markdown as styled HTML
- ⏭️ **Navigation**: Previous/Next buttons, mark as complete
- 💾 **Auto-Save**: Progress saved automatically to JSON

## ⚡ Run It Now!

### Prerequisites:
- Java 17 or higher
- Maven 3.6+

### Steps:

```bash
# 1. Navigate to the platform
cd course-platform

# 2. Build the project
mvn clean install

# 3. Run the application
mvn javafx:run
```

That's it! The application will launch and show Module 0, Lesson 1.

## 🎯 How to Use

1. **Browse Modules**: Left sidebar shows all modules and lessons
2. **Click Any Lesson**: Content loads in the main viewer
3. **Read & Learn**: Scroll through the lesson content
4. **Complete**: Click "Mark as Complete" when done
5. **Navigate**: Use Previous/Next buttons or click another lesson

## 📝 Adding Your Own Lessons

### 1. Create a Markdown File

```bash
touch lessons/module-01/lesson-04-loops.md
```

### 2. Write Your Content

Follow the template in `CONTRIBUTING.md` - use analogies first, then technical terms!

### 3. Register the Lesson

Edit `course-platform/src/main/java/com/fluttercourse/services/LessonLoader.java`:

```java
private Module createModule01() {
    // ... existing lessons ...

    module.addLesson(new Lesson("01-04", "Loops",
        "module-01", 4, "module-01/lesson-04-loops.md"));

    return module;
}
```

### 4. Rebuild & Run

```bash
mvn clean install
mvn javafx:run
```

Your new lesson appears in the navigation tree!

## 🎓 Pedagogical Approach

This course follows **"Concept First, Jargon Last"**:

### ❌ Don't Do This:
```
"A StatefulWidget is a widget that has mutable state..."
```

### ✅ Do This:
```
"Imagine a counter. When you press +, you want the number
to change on screen. We need to tell Flutter to update!

[Show how]

This is called a 'StatefulWidget'..."
```

## 📁 Project Structure

```
Flutter-Training-Course/
├── course-platform/          # Java application
│   ├── src/main/java/       # Source code
│   ├── src/main/resources/  # CSS, images
│   └── pom.xml              # Maven config
├── lessons/                  # Markdown lessons
│   ├── module-00/
│   ├── module-01/
│   └── module-02/
├── README.md                 # Full documentation
├── CONTRIBUTING.md           # Contribution guide
└── QUICK_START.md           # This file!
```

## 🔧 Troubleshooting

### "Module javafx.controls not found"
You need Java 17+ with JavaFX. Install from: https://adoptium.net/

### "mvn command not found"
Install Maven: https://maven.apache.org/install.html

### Lessons not showing?
Check that markdown files exist in `lessons/` and are registered in `LessonLoader.java`

### Progress not saving?
The app creates `course-platform/data/progress.json` automatically on first run

## 🎉 What's Next?

1. **Expand the Curriculum**: Write more lessons for Modules 3-12
2. **Add Features**:
   - Interactive code editor
   - Quizzes
   - Video integration
   - Dark theme
3. **Share**: Deploy the JAR and share with learners!

## 📞 Need Help?

- Check `README.md` for full documentation
- Review `CONTRIBUTING.md` for lesson-writing guidelines
- Check the sample lessons for examples

---

**You now have a complete, working Flutter training platform!** 🚀

Add content, customize the UI, and start teaching! The foundation is solid and ready to grow.
