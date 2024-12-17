package com.collabflow.service.mapper;

import com.collabflow.domain.AuditLog;
import com.collabflow.domain.User;
import com.collabflow.service.dto.AuditLogDTO;
import com.collabflow.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AuditLog} and its DTO {@link AuditLogDTO}.
 */
@Mapper(componentModel = "spring")
public interface AuditLogMapper extends EntityMapper<AuditLogDTO, AuditLog> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    AuditLogDTO toDto(AuditLog s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
