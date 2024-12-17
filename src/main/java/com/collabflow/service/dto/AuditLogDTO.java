package com.collabflow.service.dto;

import com.collabflow.domain.enumeration.ActionEnum;
import com.collabflow.domain.enumeration.EntityEnum;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.collabflow.domain.AuditLog} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuditLogDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private EntityEnum entity;

    @NotNull(message = "must not be null")
    private ActionEnum action;

    @NotNull(message = "must not be null")
    private Instant timestamp;

    @NotNull(message = "must not be null")
    private Long entityId;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EntityEnum getEntity() {
        return entity;
    }

    public void setEntity(EntityEnum entity) {
        this.entity = entity;
    }

    public ActionEnum getAction() {
        return action;
    }

    public void setAction(ActionEnum action) {
        this.action = action;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditLogDTO)) {
            return false;
        }

        AuditLogDTO auditLogDTO = (AuditLogDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, auditLogDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditLogDTO{" +
            "id=" + getId() +
            ", entity='" + getEntity() + "'" +
            ", action='" + getAction() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            ", entityId=" + getEntityId() +
            ", user=" + getUser() +
            "}";
    }
}
