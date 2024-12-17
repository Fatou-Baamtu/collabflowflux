package com.collabflow.service.mapper;

import com.collabflow.domain.SubTask;
import com.collabflow.domain.Task;
import com.collabflow.domain.User;
import com.collabflow.service.dto.SubTaskDTO;
import com.collabflow.service.dto.TaskDTO;
import com.collabflow.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SubTask} and its DTO {@link SubTaskDTO}.
 */
@Mapper(componentModel = "spring")
public interface SubTaskMapper extends EntityMapper<SubTaskDTO, SubTask> {
    @Mapping(target = "assignee", source = "assignee", qualifiedByName = "userId")
    @Mapping(target = "task", source = "task", qualifiedByName = "taskId")
    SubTaskDTO toDto(SubTask s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("taskId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TaskDTO toDtoTaskId(Task task);
}
