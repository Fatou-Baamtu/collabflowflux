package com.collabflow.domain;

import com.collabflow.domain.enumeration.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Task.
 */
@Table("task")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("title")
    private String title;

    @Column("description")
    private String description;

    @NotNull(message = "must not be null")
    @Column("status")
    private Status status;

    @Column("due_date")
    private Instant dueDate;

    @Column("is_completed")
    private Boolean isCompleted;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "assignee", "task" }, allowSetters = true)
    private Set<SubTask> subTasks = new HashSet<>();

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "task" }, allowSetters = true)
    private Set<Comment> comments = new HashSet<>();

    @org.springframework.data.annotation.Transient
    private User assignee;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "tasks" }, allowSetters = true)
    private Project project;

    @Column("assignee_id")
    private Long assigneeId;

    @Column("project_id")
    private Long projectId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Task id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Task title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public Task description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return this.status;
    }

    public Task status(Status status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getDueDate() {
        return this.dueDate;
    }

    public Task dueDate(Instant dueDate) {
        this.setDueDate(dueDate);
        return this;
    }

    public void setDueDate(Instant dueDate) {
        this.dueDate = dueDate;
    }

    public Boolean getIsCompleted() {
        return this.isCompleted;
    }

    public Task isCompleted(Boolean isCompleted) {
        this.setIsCompleted(isCompleted);
        return this;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Set<SubTask> getSubTasks() {
        return this.subTasks;
    }

    public void setSubTasks(Set<SubTask> subTasks) {
        if (this.subTasks != null) {
            this.subTasks.forEach(i -> i.setTask(null));
        }
        if (subTasks != null) {
            subTasks.forEach(i -> i.setTask(this));
        }
        this.subTasks = subTasks;
    }

    public Task subTasks(Set<SubTask> subTasks) {
        this.setSubTasks(subTasks);
        return this;
    }

    public Task addSubTasks(SubTask subTask) {
        this.subTasks.add(subTask);
        subTask.setTask(this);
        return this;
    }

    public Task removeSubTasks(SubTask subTask) {
        this.subTasks.remove(subTask);
        subTask.setTask(null);
        return this;
    }

    public Set<Comment> getComments() {
        return this.comments;
    }

    public void setComments(Set<Comment> comments) {
        if (this.comments != null) {
            this.comments.forEach(i -> i.setTask(null));
        }
        if (comments != null) {
            comments.forEach(i -> i.setTask(this));
        }
        this.comments = comments;
    }

    public Task comments(Set<Comment> comments) {
        this.setComments(comments);
        return this;
    }

    public Task addComments(Comment comment) {
        this.comments.add(comment);
        comment.setTask(this);
        return this;
    }

    public Task removeComments(Comment comment) {
        this.comments.remove(comment);
        comment.setTask(null);
        return this;
    }

    public User getAssignee() {
        return this.assignee;
    }

    public void setAssignee(User user) {
        this.assignee = user;
        this.assigneeId = user != null ? user.getId() : null;
    }

    public Task assignee(User user) {
        this.setAssignee(user);
        return this;
    }

    public Project getProject() {
        return this.project;
    }

    public void setProject(Project project) {
        this.project = project;
        this.projectId = project != null ? project.getId() : null;
    }

    public Task project(Project project) {
        this.setProject(project);
        return this;
    }

    public Long getAssigneeId() {
        return this.assigneeId;
    }

    public void setAssigneeId(Long user) {
        this.assigneeId = user;
    }

    public Long getProjectId() {
        return this.projectId;
    }

    public void setProjectId(Long project) {
        this.projectId = project;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task)) {
            return false;
        }
        return getId() != null && getId().equals(((Task) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Task{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", status='" + getStatus() + "'" +
            ", dueDate='" + getDueDate() + "'" +
            ", isCompleted='" + getIsCompleted() + "'" +
            "}";
    }
}
