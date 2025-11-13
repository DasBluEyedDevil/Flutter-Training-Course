package com.fluttercourse.models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Tracks user progress through the course
 */
public class Progress {
    private Set<String> completedLessons;
    private Map<String, LocalDateTime> completionDates;
    private String currentLessonId;
    private int totalLessonsCompleted;

    public Progress() {
        this.completedLessons = new HashSet<>();
        this.completionDates = new HashMap<>();
        this.totalLessonsCompleted = 0;
    }

    public void markLessonComplete(String lessonId) {
        if (!completedLessons.contains(lessonId)) {
            completedLessons.add(lessonId);
            completionDates.put(lessonId, LocalDateTime.now());
            totalLessonsCompleted++;
        }
    }

    public boolean isLessonCompleted(String lessonId) {
        return completedLessons.contains(lessonId);
    }

    public LocalDateTime getCompletionDate(String lessonId) {
        return completionDates.get(lessonId);
    }

    public double getProgressPercentage(int totalLessons) {
        if (totalLessons == 0) return 0.0;
        return (totalLessonsCompleted * 100.0) / totalLessons;
    }

    // Getters and Setters
    public Set<String> getCompletedLessons() { return completedLessons; }
    public void setCompletedLessons(Set<String> completedLessons) {
        this.completedLessons = completedLessons;
    }

    public Map<String, LocalDateTime> getCompletionDates() { return completionDates; }
    public void setCompletionDates(Map<String, LocalDateTime> completionDates) {
        this.completionDates = completionDates;
    }

    public String getCurrentLessonId() { return currentLessonId; }
    public void setCurrentLessonId(String currentLessonId) {
        this.currentLessonId = currentLessonId;
    }

    public int getTotalLessonsCompleted() { return totalLessonsCompleted; }
    public void setTotalLessonsCompleted(int totalLessonsCompleted) {
        this.totalLessonsCompleted = totalLessonsCompleted;
    }
}
