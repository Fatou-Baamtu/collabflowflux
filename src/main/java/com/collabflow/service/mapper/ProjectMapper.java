package com.collabflow.service.mapper;

import com.collabflow.domain.Project;
import com.collabflow.service.dto.ProjectDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Project} and its DTO {@link ProjectDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProjectMapper extends EntityMapper<ProjectDTO, Project> {}
