package com.collabflow.service.impl;

import com.collabflow.repository.ProjectRepository;
import com.collabflow.service.ProjectService;
import com.collabflow.service.dto.ProjectDTO;
import com.collabflow.service.mapper.ProjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.collabflow.domain.Project}.
 */
@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final ProjectRepository projectRepository;

    private final ProjectMapper projectMapper;

    public ProjectServiceImpl(ProjectRepository projectRepository, ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
    }

    @Override
    public Mono<ProjectDTO> save(ProjectDTO projectDTO) {
        LOG.debug("Request to save Project : {}", projectDTO);
        return projectRepository.save(projectMapper.toEntity(projectDTO)).map(projectMapper::toDto);
    }

    @Override
    public Mono<ProjectDTO> update(ProjectDTO projectDTO) {
        LOG.debug("Request to update Project : {}", projectDTO);
        return projectRepository.save(projectMapper.toEntity(projectDTO)).map(projectMapper::toDto);
    }

    @Override
    public Mono<ProjectDTO> partialUpdate(ProjectDTO projectDTO) {
        LOG.debug("Request to partially update Project : {}", projectDTO);

        return projectRepository
            .findById(projectDTO.getId())
            .map(existingProject -> {
                projectMapper.partialUpdate(existingProject, projectDTO);

                return existingProject;
            })
            .flatMap(projectRepository::save)
            .map(projectMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ProjectDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Projects");
        return projectRepository.findAllBy(pageable).map(projectMapper::toDto);
    }

    public Mono<Long> countAll() {
        return projectRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ProjectDTO> findOne(Long id) {
        LOG.debug("Request to get Project : {}", id);
        return projectRepository.findById(id).map(projectMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Project : {}", id);
        return projectRepository.deleteById(id);
    }
}
