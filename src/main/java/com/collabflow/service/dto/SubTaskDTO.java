package com.collabflow.service.dto;

import com.collabflow.domain.enumeration.Status;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.collabflow.domain.SubTask} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SubTaskDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String title;

    private String description;

    @NotNull(message = "must not be null")
    private Status status;

    private Instant dueDate;

    private UserDTO assignee;

    private TaskDTO task;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getDueDate() {
        return dueDate;
    }

    public void setDueDate(Instant dueDate) {
        this.dueDate = dueDate;
    }

    public UserDTO getAssignee() {
        return assignee;
    }

    public void setAssignee(UserDTO assignee) {
        this.assignee = assignee;
    }

    public TaskDTO getTask() {
        return task;
    }

    public void setTask(TaskDTO task) {
        this.task = task;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubTaskDTO)) {
            return false;
        }

        SubTaskDTO subTaskDTO = (SubTaskDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, subTaskDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SubTaskDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", status='" + getStatus() + "'" +
            ", dueDate='" + getDueDate() + "'" +
            ", assignee=" + getAssignee() +
            ", task=" + getTask() +
            "}";
    }
}
