# Contributing to Flutter Training Course Platform

Thank you for your interest in contributing to the Flutter Training Course Platform! This document provides guidelines and instructions for contributing.

## 🌟 Ways to Contribute

### 1. Add New Lessons
- Expand existing modules with more lessons
- Create new modules for advanced topics
- Improve existing lesson content

### 2. Improve the Platform
- Add new features (code editor, quizzes, etc.)
- Improve UI/UX
- Fix bugs
- Optimize performance

### 3. Documentation
- Improve README
- Add code comments
- Create tutorials
- Translate content

## 📝 Lesson Writing Guidelines

### Structure

Every lesson should follow this structure:

```markdown
# Module X, Lesson Y: Title

## Conceptual Explanation
[Explain the concept using analogies and simple language]

## Technical Terms
[Introduce technical terminology after the concept is clear]

## Code Examples
[Provide clear, commented code examples]

## Your Challenge
[Include an interactive exercise]

## What Did We Learn?
[Summarize key takeaways]

## What's Next?
[Preview the next lesson]
```

### Core Principles

1. **Concept First, Jargon Last**
   - Always explain the *why* before the *what*
   - Use analogies before introducing technical terms
   - Example: Explain "boxes with labels" before introducing "variables"

2. **Interactive by Default**
   - Every lesson must include a hands-on challenge
   - Challenges should be achievable and clear
   - Provide hints and success conditions

3. **Low Cognitive Load**
   - Break complex topics into small, digestible pieces
   - Don't introduce multiple new concepts simultaneously
   - Use plenty of examples

4. **Progressive Difficulty**
   - Each lesson builds on previous lessons
   - Gradually increase complexity
   - Review previous concepts when introducing new ones

### Example: Good vs Bad Lesson Intro

❌ **Bad** (Jargon-first):
```markdown
## StatefulWidget

A StatefulWidget is a widget that has mutable state.
It calls setState() to notify the framework that its
internal state has changed...
```

✅ **Good** (Concept-first):
```markdown
## Making the Screen Update

Imagine you have a counter app. When you press the + button,
you want the number on the screen to change. Right now,
nothing happens! The screen doesn't know it needs to update.

We need a way to tell Flutter: "Hey, something changed!
Please redraw the screen."

[Show how to do it]

This pattern we just used is called a "StatefulWidget"...
```

## 🛠️ Development Setup

### Prerequisites
- Java 17+
- Maven 3.6+
- Git
- IDE (IntelliJ IDEA or Eclipse recommended)

### Setup Steps

1. Fork the repository
2. Clone your fork:
   ```bash
   git clone https://github.com/YOUR_USERNAME/Flutter-Training-Course.git
   cd Flutter-Training-Course
   ```

3. Build the project:
   ```bash
   cd course-platform
   mvn clean install
   ```

4. Import into your IDE as a Maven project

## 📋 Pull Request Process

1. **Create a feature branch**:
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes**:
   - Follow the coding standards
   - Add appropriate comments
   - Test your changes

3. **Commit your changes**:
   ```bash
   git add .
   git commit -m "Add feature: description of what you added"
   ```

4. **Push to your fork**:
   ```bash
   git push origin feature/your-feature-name
   ```

5. **Open a Pull Request**:
   - Provide a clear description of your changes
   - Reference any related issues
   - Include screenshots if applicable

## 🎨 Code Style Guidelines

### Java Code Style

- Use meaningful variable and method names
- Follow camelCase for variables and methods
- Follow PascalCase for class names
- Add JavaDoc comments for public methods
- Keep methods focused and concise

Example:
```java
/**
 * Loads lesson content from a markdown file
 * @param lesson The lesson to load
 * @return The markdown content as a string
 */
public String loadLessonContent(Lesson lesson) {
    // Implementation
}
```

### Markdown Style

- Use heading hierarchy correctly (h1 for title, h2 for sections)
- Include code examples with language specifiers
- Use tables for comparisons
- Include emojis sparingly for visual interest

## 🧪 Testing

Before submitting a PR:

1. **Test the application**:
   ```bash
   mvn clean install
   mvn javafx:run
   ```

2. **Verify your changes**:
   - Navigate to modified lessons
   - Check rendering is correct
   - Test any new features

3. **Check for errors**:
   - Look for console errors
   - Ensure progress tracking still works
   - Test navigation between lessons

## 📚 Adding New Modules

To add a new module:

1. **Create the lesson files**:
   ```bash
   mkdir lessons/module-XX
   touch lessons/module-XX/lesson-01-topic.md
   ```

2. **Update `LessonLoader.java`**:
   ```java
   private Module createModuleXX() {
       Module module = new Module("module-XX", "Title",
           "Description", XX);

       module.addLesson(new Lesson("XX-01", "Topic",
           "module-XX", 1, "module-XX/lesson-01-topic.md"));

       return module;
   }
   ```

3. **Register in `loadAllModules()`**:
   ```java
   private void loadAllModules() {
       modules.add(createModule00());
       // ... existing modules
       modules.add(createModuleXX());  // Add your module
   }
   ```

## 🐛 Reporting Bugs

When reporting bugs, please include:

1. **Description**: Clear description of the bug
2. **Steps to Reproduce**: Exact steps to trigger the bug
3. **Expected Behavior**: What should happen
4. **Actual Behavior**: What actually happens
5. **Environment**:
   - OS (Windows, Mac, Linux)
   - Java version
   - Maven version
6. **Screenshots**: If applicable
7. **Console Output**: Any error messages

## 💡 Feature Requests

We welcome feature requests! Please:

1. Check existing issues to avoid duplicates
2. Provide a clear use case
3. Explain the expected behavior
4. Consider implementation complexity

## 📞 Questions?

- Open a [GitHub Discussion](https://github.com/YourUsername/Flutter-Training-Course/discussions)
- Check existing issues and discussions first
- Be respectful and constructive

## 🎯 Current Priorities

We're especially looking for help with:

1. **More Lessons**: Modules 3-12 need content
2. **Code Editor**: Interactive coding environment
3. **Quiz System**: End-of-module assessments
4. **Themes**: Dark/light mode support
5. **Internationalization**: Multi-language support

## 📜 Code of Conduct

- Be respectful and inclusive
- Provide constructive feedback
- Focus on the content and code, not the person
- Help create a welcoming environment for all

## ✅ Checklist Before Submitting PR

- [ ] Code compiles without errors
- [ ] Application runs without crashes
- [ ] New lessons follow the style guide
- [ ] Code includes appropriate comments
- [ ] README updated if needed
- [ ] No merge conflicts
- [ ] PR description is clear and complete

Thank you for contributing to making Flutter education better! 🚀
