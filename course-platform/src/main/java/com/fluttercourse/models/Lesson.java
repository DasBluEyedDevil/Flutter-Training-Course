package com.fluttercourse.models;

/**
 * Represents a single lesson in the course
 */
public class Lesson {
    private String id;
    private String title;
    private String moduleId;
    private int lessonNumber;
    private String markdownFile;
    private boolean hasChallenge;
    private String challengeDescription;
    private String challengeStarterCode;
    private String challengeSolution;

    public Lesson(String id, String title, String moduleId, int lessonNumber, String markdownFile) {
        this.id = id;
        this.title = title;
        this.moduleId = moduleId;
        this.lessonNumber = lessonNumber;
        this.markdownFile = markdownFile;
        this.hasChallenge = false;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getModuleId() { return moduleId; }
    public void setModuleId(String moduleId) { this.moduleId = moduleId; }

    public int getLessonNumber() { return lessonNumber; }
    public void setLessonNumber(int lessonNumber) { this.lessonNumber = lessonNumber; }

    public String getMarkdownFile() { return markdownFile; }
    public void setMarkdownFile(String markdownFile) { this.markdownFile = markdownFile; }

    public boolean hasChallenge() { return hasChallenge; }
    public void setHasChallenge(boolean hasChallenge) { this.hasChallenge = hasChallenge; }

    public String getChallengeDescription() { return challengeDescription; }
    public void setChallengeDescription(String challengeDescription) {
        this.challengeDescription = challengeDescription;
        this.hasChallenge = true;
    }

    public String getChallengeStarterCode() { return challengeStarterCode; }
    public void setChallengeStarterCode(String challengeStarterCode) {
        this.challengeStarterCode = challengeStarterCode;
    }

    public String getChallengeSolution() { return challengeSolution; }
    public void setChallengeSolution(String challengeSolution) {
        this.challengeSolution = challengeSolution;
    }

    @Override
    public String toString() {
        return String.format("Lesson %d: %s", lessonNumber, title);
    }
}
