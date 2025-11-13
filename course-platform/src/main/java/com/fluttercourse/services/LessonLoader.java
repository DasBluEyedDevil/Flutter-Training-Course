package com.fluttercourse.services;

import com.fluttercourse.models.Lesson;
import com.fluttercourse.models.Module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Loads lesson content from markdown files
 */
public class LessonLoader {
    private static final String LESSONS_BASE_PATH = "lessons";
    private List<Module> modules;

    public LessonLoader() {
        this.modules = new ArrayList<>();
        loadAllModules();
    }

    /**
     * Load all modules and lessons from the filesystem
     */
    private void loadAllModules() {
        // Define the course structure
        modules.add(createModule00());
        modules.add(createModule01());
        modules.add(createModule02());
        modules.add(createModule03());
        modules.add(createModule04());
        modules.add(createModule05());
        // Add more modules as they are created
    }

    private Module createModule00() {
        Module module = new Module("module-00", "Setup & First Steps",
            "Get the development environment ready and verify everything works", 0);

        module.addLesson(new Lesson("00-01", "Installing Flutter & Dart SDK",
            "module-00", 1, "module-00/lesson-01-installation.md"));
        module.addLesson(new Lesson("00-02", "Setting Up Your Editor",
            "module-00", 2, "module-00/lesson-02-editor-setup.md"));
        module.addLesson(new Lesson("00-03", "Running Your First Hello World",
            "module-00", 3, "module-00/lesson-03-hello-world.md"));
        module.addLesson(new Lesson("00-04", "Understanding Emulator vs Physical Device",
            "module-00", 4, "module-00/lesson-04-emulator-setup.md"));
        module.addLesson(new Lesson("00-05", "Troubleshooting Common Issues",
            "module-00", 5, "module-00/lesson-05-troubleshooting.md"));

        return module;
    }

    private Module createModule01() {
        Module module = new Module("module-01", "The Dart Language",
            "Learn programming fundamentals through interactive exercises", 1);

        module.addLesson(new Lesson("01-01", "What is Code?",
            "module-01", 1, "module-01/lesson-01-what-is-code.md"));
        module.addLesson(new Lesson("01-02", "Storing Information (Variables)",
            "module-01", 2, "module-01/lesson-02-variables.md"));
        module.addLesson(new Lesson("01-03", "Making Decisions (if/else)",
            "module-01", 3, "module-01/lesson-03-conditionals.md"));
        module.addLesson(new Lesson("01-04", "Repeating Actions (Loops)",
            "module-01", 4, "module-01/lesson-04-loops.md"));
        module.addLesson(new Lesson("01-05", "Reusable Instructions (Functions)",
            "module-01", 5, "module-01/lesson-05-functions.md"));
        module.addLesson(new Lesson("01-06", "Organizing Collections (Lists and Maps)",
            "module-01", 6, "module-01/lesson-06-lists-maps.md"));
        module.addLesson(new Lesson("01-07", "Mini-Project: Number Guessing Game",
            "module-01", 7, "module-01/lesson-07-mini-project.md"));

        return module;
    }

    private Module createModule02() {
        Module module = new Module("module-02", "Your First Flutter App",
            "Understand the basics of how a Flutter app comes to life", 2);

        module.addLesson(new Lesson("02-01", "What Happens When You Run an App?",
            "module-02", 1, "module-02/lesson-01-main-function.md"));
        module.addLesson(new Lesson("02-02", "Building Blocks (Widgets)",
            "module-02", 2, "module-02/lesson-02-widgets.md"));
        module.addLesson(new Lesson("02-03", "Displaying and Styling Text",
            "module-02", 3, "module-02/lesson-03-text-styling.md"));
        module.addLesson(new Lesson("02-04", "Displaying Images",
            "module-02", 4, "module-02/lesson-04-images.md"));
        module.addLesson(new Lesson("02-05", "The Container Widget",
            "module-02", 5, "module-02/lesson-05-containers.md"));
        module.addLesson(new Lesson("02-06", "Arranging Widgets (Column & Row)",
            "module-02", 6, "module-02/lesson-06-layouts.md"));
        module.addLesson(new Lesson("02-07", "Mini-Project: Business Card App",
            "module-02", 7, "module-02/lesson-07-mini-project.md"));

        return module;
    }

    private Module createModule03() {
        Module module = new Module("module-03", "Building Layouts",
            "Master advanced layout techniques for complex UIs", 3);

        module.addLesson(new Lesson("03-01", "Scrollable Lists (ListView)",
            "module-03", 1, "module-03/lesson-01-listview.md"));
        module.addLesson(new Lesson("03-02", "Photo Grids (GridView)",
            "module-03", 2, "module-03/lesson-02-gridview.md"));
        module.addLesson(new Lesson("03-03", "Layering Widgets (Stack)",
            "module-03", 3, "module-03/lesson-03-stack.md"));
        module.addLesson(new Lesson("03-04", "Responsive Layouts",
            "module-03", 4, "module-03/lesson-04-responsive.md"));
        module.addLesson(new Lesson("03-05", "Creating Custom Widgets",
            "module-03", 5, "module-03/lesson-05-custom-widgets.md"));
        module.addLesson(new Lesson("03-06", "Advanced Scrolling Techniques",
            "module-03", 6, "module-03/lesson-06-scrolling.md"));
        module.addLesson(new Lesson("03-07", "Mini-Project: Instagram-Style Feed",
            "module-03", 7, "module-03/lesson-07-mini-project.md"));

        return module;
    }

    private Module createModule04() {
        Module module = new Module("module-04", "User Interaction",
            "Make apps interactive with buttons, forms, and state", 4);

        module.addLesson(new Lesson("04-01", "Making Things Clickable (Buttons)",
            "module-04", 1, "module-04/lesson-01-buttons.md"));
        module.addLesson(new Lesson("04-02", "Text Input and Forms",
            "module-04", 2, "module-04/lesson-02-textfield-forms.md"));
        module.addLesson(new Lesson("04-03", "StatefulWidget and Managing State",
            "module-04", 3, "module-04/lesson-04-stateful-state.md"));

        return module;
    }

    private Module createModule05() {
        Module module = new Module("module-05", "State Management",
            "Professional state management with Provider and Riverpod", 5);

        module.addLesson(new Lesson("05-01", "Understanding State Management",
            "module-05", 1, "module-05/lesson-01-state-management-intro.md"));

        return module;
    }

    /**
     * Load the markdown content for a specific lesson
     */
    public String loadLessonContent(Lesson lesson) {
        try {
            Path lessonPath = Paths.get(LESSONS_BASE_PATH, lesson.getMarkdownFile());

            // Try to load from resources first (when packaged as JAR)
            var resource = getClass().getClassLoader()
                .getResourceAsStream(LESSONS_BASE_PATH + "/" + lesson.getMarkdownFile());

            if (resource != null) {
                return new String(resource.readAllBytes());
            }

            // Fall back to file system (during development)
            if (Files.exists(lessonPath)) {
                return Files.readString(lessonPath);
            }

            return "# Lesson Not Found\n\nThe lesson content for **" + lesson.getTitle() +
                   "** could not be loaded.\n\nExpected path: `" + lessonPath + "`";

        } catch (IOException e) {
            return "# Error Loading Lesson\n\nAn error occurred while loading this lesson:\n\n```\n" +
                   e.getMessage() + "\n```";
        }
    }

    /**
     * Get all available modules
     */
    public List<Module> getModules() {
        return modules;
    }

    /**
     * Get a specific module by ID
     */
    public Module getModule(String moduleId) {
        return modules.stream()
            .filter(m -> m.getId().equals(moduleId))
            .findFirst()
            .orElse(null);
    }

    /**
     * Get a specific lesson by ID
     */
    public Lesson getLesson(String lessonId) {
        return modules.stream()
            .flatMap(m -> m.getLessons().stream())
            .filter(l -> l.getId().equals(lessonId))
            .findFirst()
            .orElse(null);
    }

    /**
     * Get total number of lessons across all modules
     */
    public int getTotalLessonCount() {
        return modules.stream()
            .mapToInt(m -> m.getLessons().size())
            .sum();
    }
}
