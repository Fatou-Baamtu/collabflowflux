package com.collabflow.service.mapper;

import com.collabflow.domain.Comment;
import com.collabflow.domain.Task;
import com.collabflow.service.dto.CommentDTO;
import com.collabflow.service.dto.TaskDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Comment} and its DTO {@link CommentDTO}.
 */
@Mapper(componentModel = "spring")
public interface CommentMapper extends EntityMapper<CommentDTO, Comment> {
    @Mapping(target = "task", source = "task", qualifiedByName = "taskId")
    CommentDTO toDto(Comment s);

    @Named("taskId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TaskDTO toDtoTaskId(Task task);
}
