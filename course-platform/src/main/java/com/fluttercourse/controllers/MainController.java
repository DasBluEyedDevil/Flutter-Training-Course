package com.fluttercourse.controllers;

import com.fluttercourse.models.Lesson;
import com.fluttercourse.models.Module;
import com.fluttercourse.services.LessonLoader;
import com.fluttercourse.services.MarkdownRenderer;
import com.fluttercourse.services.ProgressTracker;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;

/**
 * Main controller for the course platform UI
 */
public class MainController {
    private BorderPane mainView;
    private LessonLoader lessonLoader;
    private ProgressTracker progressTracker;
    private MarkdownRenderer markdownRenderer;
    private WebView contentView;
    private TreeView<String> lessonTree;
    private ProgressBar progressBar;
    private Label progressLabel;
    private Lesson currentLesson;

    public MainController() {
        this.lessonLoader = new LessonLoader();
        this.progressTracker = new ProgressTracker();
        this.markdownRenderer = new MarkdownRenderer();
        initializeUI();
    }

    private void initializeUI() {
        mainView = new BorderPane();

        // Top: Title bar and progress
        mainView.setTop(createTopBar());

        // Left: Module and lesson navigation
        mainView.setLeft(createNavigationPane());

        // Center: Lesson content viewer
        mainView.setCenter(createContentPane());

        // Bottom: Lesson controls
        mainView.setBottom(createBottomBar());

        // Load first lesson if available
        loadFirstLesson();
    }

    private VBox createTopBar() {
        VBox topBar = new VBox(10);
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: #2c3e50;");

        // Title
        Label titleLabel = new Label("🎯 Flutter Training Course Platform");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Progress section
        HBox progressBox = new HBox(10);
        progressBox.setAlignment(Pos.CENTER_LEFT);

        progressLabel = new Label("Progress: 0%");
        progressLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);
        progressBar.setStyle("-fx-accent: #3498db;");

        progressBox.getChildren().addAll(progressLabel, progressBar);

        topBar.getChildren().addAll(titleLabel, progressBox);
        updateProgress();

        return topBar;
    }

    private ScrollPane createNavigationPane() {
        VBox navContainer = new VBox(10);
        navContainer.setPadding(new Insets(10));
        navContainer.setStyle("-fx-background-color: #ecf0f1;");
        navContainer.setPrefWidth(300);

        Label navTitle = new Label("📚 Course Modules");
        navTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Create tree view for modules and lessons
        TreeItem<String> rootItem = new TreeItem<>("Flutter Course");
        rootItem.setExpanded(true);

        for (Module module : lessonLoader.getModules()) {
            TreeItem<String> moduleItem = new TreeItem<>(
                String.format("Module %d: %s", module.getModuleNumber(), module.getTitle())
            );
            moduleItem.setExpanded(true);

            for (Lesson lesson : module.getLessons()) {
                String lessonText = String.format("Lesson %d: %s",
                    lesson.getLessonNumber(), lesson.getTitle());

                // Add checkmark if completed
                if (progressTracker.isLessonCompleted(lesson.getId())) {
                    lessonText = "✓ " + lessonText;
                }

                TreeItem<String> lessonItem = new TreeItem<>(lessonText);
                lessonItem.setValue(lesson.getId()); // Store lesson ID
                moduleItem.getChildren().add(lessonItem);
            }

            rootItem.getChildren().add(moduleItem);
        }

        lessonTree = new TreeView<>(rootItem);
        lessonTree.setShowRoot(false);
        lessonTree.setPrefHeight(600);

        // Handle lesson selection
        lessonTree.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.isLeaf()) {
                String lessonId = newVal.getValue();
                loadLesson(lessonId);
            }
        });

        navContainer.getChildren().addAll(navTitle, new Separator(), lessonTree);

        ScrollPane scrollPane = new ScrollPane(navContainer);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private VBox createContentPane() {
        VBox contentContainer = new VBox(10);
        contentContainer.setPadding(new Insets(20));

        // WebView for displaying HTML-rendered markdown
        contentView = new WebView();
        contentView.setPrefHeight(600);

        VBox.setVgrow(contentView, Priority.ALWAYS);
        contentContainer.getChildren().add(contentView);

        return contentContainer;
    }

    private HBox createBottomBar() {
        HBox bottomBar = new HBox(15);
        bottomBar.setPadding(new Insets(15));
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        bottomBar.setStyle("-fx-background-color: #ecf0f1;");

        Button markCompleteBtn = new Button("✓ Mark as Complete");
        markCompleteBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                                "-fx-font-size: 14px; -fx-padding: 10px 20px;");
        markCompleteBtn.setOnAction(e -> markCurrentLessonComplete());

        Button previousBtn = new Button("← Previous Lesson");
        previousBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10px 20px;");
        previousBtn.setOnAction(e -> navigateToPreviousLesson());

        Button nextBtn = new Button("Next Lesson →");
        nextBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                        "-fx-font-size: 14px; -fx-padding: 10px 20px;");
        nextBtn.setOnAction(e -> navigateToNextLesson());

        bottomBar.getChildren().addAll(markCompleteBtn, previousBtn, nextBtn);

        return bottomBar;
    }

    private void loadLesson(String lessonId) {
        Lesson lesson = lessonLoader.getLesson(lessonId);
        if (lesson != null) {
            currentLesson = lesson;
            String markdownContent = lessonLoader.loadLessonContent(lesson);
            String html = markdownRenderer.renderToHtml(markdownContent);
            contentView.getEngine().loadContent(html);

            // Update current lesson in progress
            progressTracker.setCurrentLesson(lessonId);
        }
    }

    private void loadFirstLesson() {
        var modules = lessonLoader.getModules();
        if (!modules.isEmpty() && !modules.get(0).getLessons().isEmpty()) {
            Lesson firstLesson = modules.get(0).getLessons().get(0);
            loadLesson(firstLesson.getId());

            // Select in tree
            selectLessonInTree(firstLesson.getId());
        }
    }

    private void selectLessonInTree(String lessonId) {
        for (TreeItem<String> moduleItem : lessonTree.getRoot().getChildren()) {
            for (TreeItem<String> lessonItem : moduleItem.getChildren()) {
                if (lessonItem.getValue().equals(lessonId)) {
                    lessonTree.getSelectionModel().select(lessonItem);
                    return;
                }
            }
        }
    }

    private void markCurrentLessonComplete() {
        if (currentLesson != null) {
            progressTracker.markLessonComplete(currentLesson.getId());
            updateProgress();
            refreshLessonTree();

            // Show confirmation
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Lesson Complete!");
            alert.setHeaderText("Great job! 🎉");
            alert.setContentText("You've completed: " + currentLesson.getTitle());
            alert.showAndWait();

            // Auto-navigate to next lesson
            navigateToNextLesson();
        }
    }

    private void navigateToNextLesson() {
        if (currentLesson == null) return;

        Lesson nextLesson = findNextLesson(currentLesson);
        if (nextLesson != null) {
            loadLesson(nextLesson.getId());
            selectLessonInTree(nextLesson.getId());
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Course Complete!");
            alert.setHeaderText("Congratulations! 🎓");
            alert.setContentText("You've reached the end of the available lessons!");
            alert.showAndWait();
        }
    }

    private void navigateToPreviousLesson() {
        if (currentLesson == null) return;

        Lesson previousLesson = findPreviousLesson(currentLesson);
        if (previousLesson != null) {
            loadLesson(previousLesson.getId());
            selectLessonInTree(previousLesson.getId());
        }
    }

    private Lesson findNextLesson(Lesson current) {
        var modules = lessonLoader.getModules();
        boolean foundCurrent = false;

        for (Module module : modules) {
            for (Lesson lesson : module.getLessons()) {
                if (foundCurrent) {
                    return lesson;
                }
                if (lesson.getId().equals(current.getId())) {
                    foundCurrent = true;
                }
            }
        }
        return null;
    }

    private Lesson findPreviousLesson(Lesson current) {
        var modules = lessonLoader.getModules();
        Lesson previous = null;

        for (Module module : modules) {
            for (Lesson lesson : module.getLessons()) {
                if (lesson.getId().equals(current.getId())) {
                    return previous;
                }
                previous = lesson;
            }
        }
        return null;
    }

    private void updateProgress() {
        int totalLessons = lessonLoader.getTotalLessonCount();
        double progressPercent = progressTracker.getProgressPercentage(totalLessons);

        progressBar.setProgress(progressPercent / 100.0);
        progressLabel.setText(String.format("Progress: %.0f%% (%d/%d lessons)",
            progressPercent,
            progressTracker.getProgress().getTotalLessonsCompleted(),
            totalLessons));
    }

    private void refreshLessonTree() {
        // Rebuild tree to show updated completion status
        TreeItem<String> selectedItem = lessonTree.getSelectionModel().getSelectedItem();
        createNavigationPane(); // This will rebuild the tree
        if (selectedItem != null) {
            selectLessonInTree(selectedItem.getValue());
        }
    }

    public BorderPane getView() {
        return mainView;
    }
}
