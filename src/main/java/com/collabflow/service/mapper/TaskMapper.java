package com.collabflow.service.mapper;

import com.collabflow.domain.Project;
import com.collabflow.domain.Task;
import com.collabflow.domain.User;
import com.collabflow.service.dto.ProjectDTO;
import com.collabflow.service.dto.TaskDTO;
import com.collabflow.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Task} and its DTO {@link TaskDTO}.
 */
@Mapper(componentModel = "spring")
public interface TaskMapper extends EntityMapper<TaskDTO, Task> {
    @Mapping(target = "assignee", source = "assignee", qualifiedByName = "userId")
    @Mapping(target = "project", source = "project", qualifiedByName = "projectId")
    TaskDTO toDto(Task s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("projectId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProjectDTO toDtoProjectId(Project project);
}
