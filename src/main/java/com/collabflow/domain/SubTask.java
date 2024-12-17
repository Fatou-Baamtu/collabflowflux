package com.collabflow.domain;

import com.collabflow.domain.enumeration.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A SubTask.
 */
@Table("sub_task")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SubTask implements Serializable {

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

    @org.springframework.data.annotation.Transient
    private User assignee;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "subTasks", "comments", "assignee", "project" }, allowSetters = true)
    private Task task;

    @Column("assignee_id")
    private Long assigneeId;

    @Column("task_id")
    private Long taskId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SubTask id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public SubTask title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public SubTask description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return this.status;
    }

    public SubTask status(Status status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getDueDate() {
        return this.dueDate;
    }

    public SubTask dueDate(Instant dueDate) {
        this.setDueDate(dueDate);
        return this;
    }

    public void setDueDate(Instant dueDate) {
        this.dueDate = dueDate;
    }

    public User getAssignee() {
        return this.assignee;
    }

    public void setAssignee(User user) {
        this.assignee = user;
        this.assigneeId = user != null ? user.getId() : null;
    }

    public SubTask assignee(User user) {
        this.setAssignee(user);
        return this;
    }

    public Task getTask() {
        return this.task;
    }

    public void setTask(Task task) {
        this.task = task;
        this.taskId = task != null ? task.getId() : null;
    }

    public SubTask task(Task task) {
        this.setTask(task);
        return this;
    }

    public Long getAssigneeId() {
        return this.assigneeId;
    }

    public void setAssigneeId(Long user) {
        this.assigneeId = user;
    }

    public Long getTaskId() {
        return this.taskId;
    }

    public void setTaskId(Long task) {
        this.taskId = task;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubTask)) {
            return false;
        }
        return getId() != null && getId().equals(((SubTask) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SubTask{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", status='" + getStatus() + "'" +
            ", dueDate='" + getDueDate() + "'" +
            "}";
    }
}
