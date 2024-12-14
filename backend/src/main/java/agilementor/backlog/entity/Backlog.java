package agilementor.backlog.entity;

import agilementor.member.entity.Member;
import agilementor.project.entity.Project;
import agilementor.sprint.entity.Sprint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Backlog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long backlogId;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "sprint_id")
    private Sprint sprint;

    @ManyToOne
    @JoinColumn(name = "story_id")
    private Story story;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member assignee;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    protected Backlog() {
    }

    public Backlog(String title, String description, Priority priority, Project project,
        Sprint sprint, Story story, Member assignee) {
        this.title = title;
        this.description = description;
        this.status = Status.TODO;
        this.priority = priority;
        this.project = project;
        this.sprint = sprint;
        this.story = story;
        this.assignee = assignee;
    }

    public Long getBacklogId() {
        return backlogId;
    }

    public Project getProject() {
        return project;
    }

    public Sprint getSprint() {
        return sprint;
    }

    public Story getStory() {
        return story;
    }

    public Member getAssignee() {
        return assignee;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }

    public void setStory(Story story) {
        this.story = story;
    }

    public void deleteAssignee() {
        this.assignee = null;
        this.status = Status.TODO;
    }

    public boolean isDone() {
        return status.equals(Status.DONE);
    }

    public boolean isNotDone() {
        return !status.equals(Status.DONE);
    }

    public void update(String title, String description, Status status, Priority priority,
        Project project, Sprint sprint, Story story, Member assignee) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.project = project;
        this.sprint = sprint;
        this.story = story;
        this.assignee = assignee;
    }
}
