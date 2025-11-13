package com.fluttercourse.services;

import com.fluttercourse.models.Progress;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

/**
 * Manages saving and loading user progress
 */
public class ProgressTracker {
    private static final String PROGRESS_FILE = "data/progress.json";
    private Progress progress;
    private Gson gson;

    public ProgressTracker() {
        this.gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
        this.progress = loadProgress();
    }

    /**
     * Load progress from JSON file
     */
    private Progress loadProgress() {
        try {
            Path progressPath = Paths.get(PROGRESS_FILE);
            if (Files.exists(progressPath)) {
                String json = Files.readString(progressPath);
                return gson.fromJson(json, Progress.class);
            }
        } catch (IOException e) {
            System.err.println("Error loading progress: " + e.getMessage());
        }
        return new Progress(); // Return new progress if file doesn't exist
    }

    /**
     * Save progress to JSON file
     */
    public void saveProgress() {
        try {
            Path progressPath = Paths.get(PROGRESS_FILE);
            Files.createDirectories(progressPath.getParent());

            String json = gson.toJson(progress);
            Files.writeString(progressPath, json);
        } catch (IOException e) {
            System.err.println("Error saving progress: " + e.getMessage());
        }
    }

    /**
     * Mark a lesson as completed
     */
    public void markLessonComplete(String lessonId) {
        progress.markLessonComplete(lessonId);
        saveProgress();
    }

    /**
     * Check if a lesson is completed
     */
    public boolean isLessonCompleted(String lessonId) {
        return progress.isLessonCompleted(lessonId);
    }

    /**
     * Set the current lesson
     */
    public void setCurrentLesson(String lessonId) {
        progress.setCurrentLessonId(lessonId);
        saveProgress();
    }

    /**
     * Get the current lesson ID
     */
    public String getCurrentLessonId() {
        return progress.getCurrentLessonId();
    }

    /**
     * Get overall progress percentage
     */
    public double getProgressPercentage(int totalLessons) {
        return progress.getProgressPercentage(totalLessons);
    }

    /**
     * Get the progress object
     */
    public Progress getProgress() {
        return progress;
    }

    // Custom adapter for LocalDateTime serialization
    private static class LocalDateTimeAdapter extends com.google.gson.TypeAdapter<LocalDateTime> {
        @Override
        public void write(com.google.gson.stream.JsonWriter out, LocalDateTime value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.toString());
            }
        }

        @Override
        public LocalDateTime read(com.google.gson.stream.JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return LocalDateTime.parse(in.nextString());
        }
    }
}
