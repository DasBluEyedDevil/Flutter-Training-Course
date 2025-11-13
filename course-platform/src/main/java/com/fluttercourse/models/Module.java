package com.fluttercourse.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a module (collection of lessons) in the course
 */
public class Module {
    private String id;
    private String title;
    private String description;
    private int moduleNumber;
    private List<Lesson> lessons;

    public Module(String id, String title, String description, int moduleNumber) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.moduleNumber = moduleNumber;
        this.lessons = new ArrayList<>();
    }

    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getModuleNumber() { return moduleNumber; }
    public void setModuleNumber(int moduleNumber) { this.moduleNumber = moduleNumber; }

    public List<Lesson> getLessons() { return lessons; }
    public void setLessons(List<Lesson> lessons) { this.lessons = lessons; }

    @Override
    public String toString() {
        return String.format("Module %d: %s (%d lessons)", moduleNumber, title, lessons.size());
    }
}
