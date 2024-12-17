package com.collabflow.domain;

import com.collabflow.domain.enumeration.ActionEnum;
import com.collabflow.domain.enumeration.EntityEnum;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A AuditLog.
 */
@Table("audit_log")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuditLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("entity")
    private EntityEnum entity;

    @NotNull(message = "must not be null")
    @Column("action")
    private ActionEnum action;

    @NotNull(message = "must not be null")
    @Column("timestamp")
    private Instant timestamp;

    @NotNull(message = "must not be null")
    @Column("entity_id")
    private Long entityId;

    @org.springframework.data.annotation.Transient
    private User user;

    @Column("user_id")
    private Long userId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AuditLog id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EntityEnum getEntity() {
        return this.entity;
    }

    public AuditLog entity(EntityEnum entity) {
        this.setEntity(entity);
        return this;
    }

    public void setEntity(EntityEnum entity) {
        this.entity = entity;
    }

    public ActionEnum getAction() {
        return this.action;
    }

    public AuditLog action(ActionEnum action) {
        this.setAction(action);
        return this;
    }

    public void setAction(ActionEnum action) {
        this.action = action;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }

    public AuditLog timestamp(Instant timestamp) {
        this.setTimestamp(timestamp);
        return this;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Long getEntityId() {
        return this.entityId;
    }

    public AuditLog entityId(Long entityId) {
        this.setEntityId(entityId);
        return this;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public AuditLog user(User user) {
        this.setUser(user);
        return this;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long user) {
        this.userId = user;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditLog)) {
            return false;
        }
        return getId() != null && getId().equals(((AuditLog) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditLog{" +
            "id=" + getId() +
            ", entity='" + getEntity() + "'" +
            ", action='" + getAction() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            ", entityId=" + getEntityId() +
            "}";
    }
}
