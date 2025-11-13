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
        modules.add(createModule06());
        modules.add(createModule07());
        modules.add(createModule08());
        modules.add(createModule09());
        modules.add(createModule10());
        modules.add(createModule11());
        modules.add(createModule12());
        // Course complete!
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
        module.addLesson(new Lesson("04-03", "Gestures and Touch Interactions",
            "module-04", 3, "module-04/lesson-03-gestures.md"));
        module.addLesson(new Lesson("04-04", "StatefulWidget and Managing State",
            "module-04", 4, "module-04/lesson-04-stateful-state.md"));
        module.addLesson(new Lesson("04-05", "Mini-Project: Interactive Notes App",
            "module-04", 5, "module-04/lesson-05-mini-project.md"));

        return module;
    }

    private Module createModule05() {
        Module module = new Module("module-05", "State Management",
            "Professional state management with Provider and Riverpod", 5);

        module.addLesson(new Lesson("05-01", "Understanding State Management",
            "module-05", 1, "module-05/lesson-01-state-management-intro.md"));
        module.addLesson(new Lesson("05-02", "Provider Deep Dive",
            "module-05", 2, "module-05/lesson-02-provider-deep-dive.md"));
        module.addLesson(new Lesson("05-03", "Introduction to Riverpod",
            "module-05", 3, "module-05/lesson-03-riverpod-intro.md"));
        module.addLesson(new Lesson("05-04", "Advanced Riverpod Patterns",
            "module-05", 4, "module-05/lesson-04-riverpod-advanced.md"));
        module.addLesson(new Lesson("05-05", "State Management Best Practices",
            "module-05", 5, "module-05/lesson-05-best-practices.md"));
        module.addLesson(new Lesson("05-06", "Mini-Project: Task Management App",
            "module-05", 6, "module-05/lesson-06-mini-project.md"));

        return module;
    }

    private Module createModule06() {
        Module module = new Module("module-06", "Navigation & Routing",
            "Build multi-screen apps with professional navigation patterns", 6);

        module.addLesson(new Lesson("06-01", "Basic Navigation",
            "module-06", 1, "module-06/lesson-01-basic-navigation.md"));
        module.addLesson(new Lesson("06-02", "Named Routes",
            "module-06", 2, "module-06/lesson-02-named-routes.md"));
        module.addLesson(new Lesson("06-03", "Modern Navigation with GoRouter",
            "module-06", 3, "module-06/lesson-03-go-router.md"));
        module.addLesson(new Lesson("06-04", "Deep Linking",
            "module-06", 4, "module-06/lesson-04-deep-linking.md"));
        module.addLesson(new Lesson("06-05", "Bottom Navigation Bar",
            "module-06", 5, "module-06/lesson-05-bottom-navigation.md"));
        module.addLesson(new Lesson("06-06", "Tab Bars and TabBarView",
            "module-06", 6, "module-06/lesson-06-tabs.md"));
        module.addLesson(new Lesson("06-07", "Drawer Navigation",
            "module-06", 7, "module-06/lesson-07-drawer.md"));
        module.addLesson(new Lesson("06-08", "Mini-Project: Multi-Screen Navigation App",
            "module-06", 8, "module-06/lesson-08-mini-project.md"));

        return module;
    }

    private Module createModule07() {
        Module module = new Module("module-07", "Networking & API Integration",
            "Connect your app to the internet with REST APIs, authentication, and file uploads", 7);

        module.addLesson(new Lesson("07-01", "HTTP Requests and APIs",
            "module-07", 1, "module-07/lesson-01-http-requests.md"));
        module.addLesson(new Lesson("07-02", "JSON Parsing and Serialization",
            "module-07", 2, "module-07/lesson-02-json-parsing.md"));
        module.addLesson(new Lesson("07-03", "Error Handling and Loading States",
            "module-07", 3, "module-07/lesson-03-error-handling.md"));
        module.addLesson(new Lesson("07-04", "Authentication and Headers",
            "module-07", 4, "module-07/lesson-04-authentication.md"));
        module.addLesson(new Lesson("07-05", "Dio Package (Advanced HTTP Client)",
            "module-07", 5, "module-07/lesson-05-dio-package.md"));
        module.addLesson(new Lesson("07-06", "Pagination and Infinite Scroll",
            "module-07", 6, "module-07/lesson-06-pagination.md"));
        module.addLesson(new Lesson("07-07", "File Upload and Download",
            "module-07", 7, "module-07/lesson-07-file-upload-download.md"));
        module.addLesson(new Lesson("07-08", "Mini-Project: Social Media App",
            "module-07", 8, "module-07/lesson-08-mini-project.md"));

        return module;
    }

    private Module createModule08() {
        Module module = new Module("module-08", "Backend Integration - Firebase",
            "Build full-stack apps with Firebase authentication, database, storage, and real-time features", 8);

        module.addLesson(new Lesson("08-01", "Backend Services & Firebase Setup",
            "module-08", 1, "module-08/lesson-01-backend-intro-firebase-setup.md"));
        module.addLesson(new Lesson("08-02", "Firebase Authentication",
            "module-08", 2, "module-08/lesson-02-firebase-authentication.md"));
        module.addLesson(new Lesson("08-03", "Cloud Firestore Database",
            "module-08", 3, "module-08/lesson-03-cloud-firestore.md"));
        module.addLesson(new Lesson("08-04", "Cloud Storage",
            "module-08", 4, "module-08/lesson-04-cloud-storage.md"));
        module.addLesson(new Lesson("08-05", "Security Rules",
            "module-08", 5, "module-08/lesson-05-security-rules.md"));
        module.addLesson(new Lesson("08-06", "Real-Time Features",
            "module-08", 6, "module-08/lesson-06-realtime-features.md"));
        module.addLesson(new Lesson("08-07", "Push Notifications & Analytics",
            "module-08", 7, "module-08/lesson-07-push-notifications.md"));
        module.addLesson(new Lesson("08-08", "Mini-Project: Complete Firebase Social App",
            "module-08", 8, "module-08/lesson-08-mini-project.md"));

        return module;
    }

    private Module createModule09() {
        Module module = new Module("module-09", "Advanced Features",
            "Master animations, device sensors, maps, local storage, and background tasks", 9);

        module.addLesson(new Lesson("09-01", "Introduction to Animations",
            "module-09", 1, "module-09/lesson-01-animations.md"));
        module.addLesson(new Lesson("09-02", "Camera and Photo Gallery",
            "module-09", 2, "module-09/lesson-02-camera-gallery.md"));
        module.addLesson(new Lesson("09-03", "Local Storage with Hive",
            "module-09", 3, "module-09/lesson-03-local-storage-hive.md"));
        module.addLesson(new Lesson("09-04", "SQLite Database",
            "module-09", 4, "module-09/lesson-04-sqlite-database.md"));
        module.addLesson(new Lesson("09-05", "Maps and Location",
            "module-09", 5, "module-09/lesson-05-maps-location.md"));
        module.addLesson(new Lesson("09-06", "Device Features (Sensors & Biometrics)",
            "module-09", 6, "module-09/lesson-06-device-features.md"));
        module.addLesson(new Lesson("09-07", "Background Tasks & Workmanager",
            "module-09", 7, "module-09/lesson-07-background-tasks.md"));
        module.addLesson(new Lesson("09-08", "Mini-Project: Fitness Tracker App",
            "module-09", 8, "module-09/lesson-08-mini-project.md"));

        return module;
    }

    private Module createModule10() {
        Module module = new Module("module-10", "Testing",
            "Master unit testing, widget testing, mocking, and test-driven development", 10);

        module.addLesson(new Lesson("10-01", "Introduction to Testing",
            "module-10", 1, "module-10/lesson-01-introduction-to-testing.md"));
        module.addLesson(new Lesson("10-02", "Widget Testing",
            "module-10", 2, "module-10/lesson-02-widget-testing.md"));
        module.addLesson(new Lesson("10-03", "Mocking Dependencies",
            "module-10", 3, "module-10/lesson-03-mocking-dependencies.md"));

        return module;
    }

    private Module createModule11() {
        Module module = new Module("module-11", "Deployment & Publishing",
            "Prepare, build, and publish your Flutter apps to Google Play and App Store", 11);

        module.addLesson(new Lesson("11-01", "Preparing for Release",
            "module-11", 1, "module-11/lesson-01-preparing-for-release.md"));
        module.addLesson(new Lesson("11-02", "Publishing to App Stores",
            "module-11", 2, "module-11/lesson-02-publishing-to-stores.md"));

        return module;
    }

    private Module createModule12() {
        Module module = new Module("module-12", "Final Capstone Project",
            "Build a complete social marketplace app combining all skills learned", 12);

        module.addLesson(new Lesson("12-01", "Final Capstone Project - LocalBuy Social Marketplace",
            "module-12", 1, "module-12/lesson-01-final-capstone-project.md"));

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
