# 🎯 Flutter Training Course Platform

![Java](https://img.shields.io/badge/Java-17+-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-21-blue)
![Maven](https://img.shields.io/badge/Maven-Build-red)
![License](https://img.shields.io/badge/License-MIT-green)

A comprehensive, interactive desktop learning platform for Flutter development. This standalone Java application provides a complete Flutter training course from absolute beginner to full-stack developer, featuring embedded markdown lessons, progress tracking, and interactive learning.

## 🌟 Features

### ✅ Complete Flutter Curriculum
- **12 Modules** covering everything from setup to deployment
- **95+ Lessons** with detailed explanations and examples
- **10+ Mini-Projects** for hands-on practice
- **1 Capstone Project** to build a full-stack application

### ✅ Interactive Learning Platform
- **JavaFX GUI** with modern, intuitive interface
- **Markdown Rendering** with syntax highlighting for code
- **Progress Tracking** with persistent JSON storage
- **Navigation System** with module/lesson tree view
- **Real-time Progress Bar** showing course completion

### ✅ Pedagogical Excellence
- **Concept-First Teaching**: Analogies before jargon
- **Interactive Exercises**: Learn by doing
- **Low Cognitive Load**: Break complex topics into digestible pieces
- **Full-Stack Focus**: From Flutter UI to backend deployment

## 📸 Screenshots

*Coming soon - the application includes:*
- Clean, modern UI with dark theme
- Lesson navigation sidebar
- HTML-rendered markdown content viewer
- Progress tracking dashboard
- Next/Previous lesson navigation

## 🚀 Quick Start

### Prerequisites

- **Java 17 or higher** ([Download here](https://adoptium.net/))
- **Maven 3.6+** ([Download here](https://maven.apache.org/download.cgi))
- **Git** (for cloning the repository)

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/YourUsername/Flutter-Training-Course.git
   cd Flutter-Training-Course
   ```

2. **Navigate to the platform directory**:
   ```bash
   cd course-platform
   ```

3. **Build the project**:
   ```bash
   mvn clean install
   ```

4. **Run the application**:
   ```bash
   mvn javafx:run
   ```

   Or, after building, run the generated JAR:
   ```bash
   java -jar target/flutter-course-platform-1.0.0.jar
   ```

### Alternative: Run with Java directly

```bash
cd course-platform
mvn compile
mvn exec:java -Dexec.mainClass="com.fluttercourse.FlutterCourseApp"
```

## 📚 Course Structure

### Module 0: Setup & First Steps
- Installing Flutter & Dart SDK
- Setting up your editor (VS Code)
- Running your first "Hello World"

### Module 1: The Dart Language (Interactive)
- What is Code? (The Recipe Analogy)
- Variables: Storing Information
- Conditionals: Making Decisions
- Loops: Repeating Actions
- Functions: Reusable Instructions

### Module 2: Your First Flutter App
- The main() Function
- Understanding Widgets
- Building Layouts (Row, Column, Container)

### Modules 3-11: Advanced Topics
- State Management
- Navigation
- Networking & APIs
- Backend Integration (Firebase/Supabase)
- Database Operations
- Deployment to App Stores

### Module 12: Next Steps & Advanced Topics
- Advanced state management
- Animations
- Testing
- Performance optimization

## 🏗️ Project Architecture

```
Flutter-Training-Course/
├── course-platform/              # Java/JavaFX Application
│   ├── pom.xml                  # Maven dependencies
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/fluttercourse/
│   │   │   │       ├── FlutterCourseApp.java      # Main entry point
│   │   │   │       ├── controllers/
│   │   │   │       │   └── MainController.java    # UI controller
│   │   │   │       ├── models/
│   │   │   │       │   ├── Lesson.java           # Lesson data model
│   │   │   │       │   ├── Module.java           # Module data model
│   │   │   │       │   └── Progress.java         # Progress tracking
│   │   │   │       ├── services/
│   │   │   │       │   ├── LessonLoader.java     # Loads markdown lessons
│   │   │   │       │   ├── ProgressTracker.java  # Saves/loads progress
│   │   │   │       │   └── MarkdownRenderer.java # Renders markdown to HTML
│   │   │   └── resources/
│   │   │       └── css/
│   │   │           └── styles.css                # Application styling
│   └── data/
│       └── progress.json                         # User progress (auto-generated)
├── lessons/                      # Markdown Lesson Files
│   ├── module-00/               # Setup & Installation
│   ├── module-01/               # Dart Language Basics
│   ├── module-02/               # First Flutter App
│   └── ...
└── README.md                    # This file
```

## 🎓 Learning Methodology

This course follows a unique **"Concept First, Jargon Last"** approach:

1. **Analogies First**: Every concept is introduced with real-world analogies
   - Variables = "labeled boxes"
   - Widgets = "LEGO pieces"
   - Functions = "recipes"

2. **Technical Terms Second**: Jargon is introduced only after understanding
   ```
   "This pattern we just used is called a 'StatefulWidget'"
   ```

3. **Interactive by Default**: Every lesson includes hands-on coding challenges

4. **Low Cognitive Load**: Complex topics are broken into smallest parts

## 🛠️ Development

### Technology Stack

- **Java 17**: Core application language
- **JavaFX 21**: GUI framework
- **Maven**: Build and dependency management
- **CommonMark**: Markdown parsing and rendering
- **Gson**: JSON serialization for progress tracking

### Key Dependencies

```xml
<dependencies>
    <!-- JavaFX -->
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>21.0.1</version>
    </dependency>

    <!-- Markdown Parser -->
    <dependency>
        <groupId>org.commonmark</groupId>
        <artifactId>commonmark</artifactId>
        <version>0.21.0</version>
    </dependency>

    <!-- JSON -->
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.10.1</version>
    </dependency>
</dependencies>
```

### Building from Source

```bash
# Clone the repository
git clone https://github.com/YourUsername/Flutter-Training-Course.git
cd Flutter-Training-Course/course-platform

# Clean build
mvn clean

# Compile
mvn compile

# Run tests (when available)
mvn test

# Package as JAR
mvn package

# Run the application
mvn javafx:run
```

## 📝 Adding New Lessons

To add new lessons to the platform:

1. **Create the markdown file**:
   ```bash
   # Create in the appropriate module folder
   touch lessons/module-03/lesson-04-new-topic.md
   ```

2. **Write the lesson content** following the template:
   ```markdown
   # Module X, Lesson Y: Title

   ## Conceptual Explanation
   [Use analogies first]

   ## Technical Terms
   [Introduce jargon]

   ## Code Examples
   [Provide examples]

   ## Challenge
   [Interactive exercise]
   ```

3. **Register the lesson** in `LessonLoader.java`:
   ```java
   module.addLesson(new Lesson("03-04", "New Topic",
       "module-03", 4, "module-03/lesson-04-new-topic.md"));
   ```

4. **Rebuild and run** to see your new lesson!

## 🎯 Usage Guide

### Navigating Lessons

1. **Launch the application**
2. **Browse modules** in the left sidebar
3. **Click any lesson** to view its content
4. **Use navigation buttons** at the bottom:
   - "Previous Lesson" - Go back
   - "Next Lesson" - Continue forward
   - "Mark as Complete" - Track your progress

### Tracking Progress

- Progress is automatically saved in `data/progress.json`
- Completed lessons show a ✓ checkmark in the tree
- Progress bar shows overall completion percentage
- Your current lesson is remembered between sessions

### Keyboard Shortcuts

- `Ctrl/Cmd + Mouse Wheel`: Zoom in/out on lesson content
- Tree navigation: Arrow keys to navigate lessons

## 🤝 Contributing

Contributions are welcome! Here's how you can help:

1. **Add More Lessons**: Expand the curriculum
2. **Improve Explanations**: Make content clearer
3. **Add Features**: Code editor, quizzes, etc.
4. **Fix Bugs**: Report and fix issues
5. **Improve UI**: Enhance the visual design

### Contribution Guidelines

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Flutter Team** at Google for creating Flutter
- **JavaFX Community** for the excellent GUI framework
- **CommonMark** for markdown parsing
- All contributors who help improve this course

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/YourUsername/Flutter-Training-Course/issues)
- **Discussions**: [GitHub Discussions](https://github.com/YourUsername/Flutter-Training-Course/discussions)
- **Email**: support@fluttercourse.dev

## 🗺️ Roadmap

- [ ] Add interactive code editor for challenges
- [ ] Implement code validation system
- [ ] Add video content integration
- [ ] Create quiz system for each module
- [ ] Add dark/light theme toggle
- [ ] Export progress reports
- [ ] Multi-language support
- [ ] Cloud sync for progress

## 📊 Stats

- **Modules**: 12
- **Lessons**: 95+
- **Code Examples**: 200+
- **Exercises**: 100+
- **Estimated Time**: 12-16 weeks (self-paced)

---

**Ready to become a Flutter developer?** Clone this repo and start your journey today! 🚀

Built with ❤️ by the Flutter Training Community
