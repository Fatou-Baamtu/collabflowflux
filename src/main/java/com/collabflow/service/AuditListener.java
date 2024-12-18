package com.collabflow.service;

import com.collabflow.domain.enumeration.ActionEnum;
import com.collabflow.domain.enumeration.EntityEnum;
import com.collabflow.security.SecurityUtils;
import com.collabflow.service.dto.AuditLogDTO;
import com.collabflow.service.dto.UserDTO;
import com.collabflow.service.impl.AuditLogServiceImpl;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import java.lang.reflect.Field;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Auditable;
import org.springframework.stereotype.Component;

@Component
public class AuditListener {

    private static final Logger LOG = LoggerFactory.getLogger(AuditListener.class);

    @Autowired
    private AuditLogService auditLogService;

    private Long extractId(Object entity) {
        try {
            Field idField = entity.getClass().getDeclaredField("id"); // Suppose que le champ s'appelle "id"
            idField.setAccessible(true); // Permet d'accéder aux champs privés
            return (Long) idField.get(entity); // Retourne la valeur de l'id
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Impossible d'extraire l'ID de l'entité : " + entity.getClass().getSimpleName(), e);
        }
    }

    // Méthode générique pour l'enregistrement de l'audit
    private void logAudit(String action, Object entity) {
        LOG.debug("Request to save logAudit : {}", action, entity);

        Long entityId = extractId(entity);
        AuditLogDTO auditLogDTO = new AuditLogDTO();
        auditLogDTO.setEntity(EntityEnum.valueOf(entity.getClass().getSimpleName()));
        auditLogDTO.setEntityId(entityId);
        auditLogDTO.setAction(ActionEnum.valueOf(action));
        auditLogDTO.setTimestamp(Instant.now()); // Timestamp actuel

        // Récupérer l'utilisateur courant
        SecurityUtils.getCurrentUserLogin()
            .map(login -> {
                UserDTO userDTO = new UserDTO();
                userDTO.setLogin(login); // Assurez-vous que UserDTO a un champ "login" et un setter
                return userDTO;
            })
            .doOnNext(auditLogDTO::setUser) // Définir l'utilisateur
            .then(auditLogService.save(auditLogDTO)) // Sauvegarder le log d'audit
            .subscribe(); // Exécuter l'opération de manière asynchrone
    }

    @PrePersist
    public void onCreate(Object entity) {
        LOG.info("PrePersist called for entity: {}", entity.getClass().getSimpleName());
        logAudit("CREATED", entity);
    }

    @PreUpdate
    public void onUpdate(Object entity) {
        LOG.info("PreUpdate called for entity: {}", entity.getClass().getSimpleName());
        logAudit("UPDATED", entity);
    }

    @PreRemove
    public void onDelete(Object entity) {
        LOG.info("PreRemove called for entity: {}", entity.getClass().getSimpleName());
        logAudit("DELETED", entity);
    }
}
