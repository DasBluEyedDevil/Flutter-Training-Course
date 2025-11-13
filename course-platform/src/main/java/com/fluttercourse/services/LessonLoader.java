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

        return module;
    }

    private Module createModule02() {
        Module module = new Module("module-02", "Your First Flutter App",
            "Understand the basics of how a Flutter app comes to life", 2);

        module.addLesson(new Lesson("02-01", "What Happens When You Run an App?",
            "module-02", 1, "module-02/lesson-01-main-function.md"));

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
