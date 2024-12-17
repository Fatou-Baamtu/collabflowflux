package com.collabflow.service.impl;

import com.collabflow.repository.TaskRepository;
import com.collabflow.service.TaskService;
import com.collabflow.service.dto.TaskDTO;
import com.collabflow.service.mapper.TaskMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.collabflow.domain.Task}.
 */
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private static final Logger LOG = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    public TaskServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public Mono<TaskDTO> save(TaskDTO taskDTO) {
        LOG.debug("Request to save Task : {}", taskDTO);
        return taskRepository.save(taskMapper.toEntity(taskDTO)).map(taskMapper::toDto);
    }

    @Override
    public Mono<TaskDTO> update(TaskDTO taskDTO) {
        LOG.debug("Request to update Task : {}", taskDTO);
        return taskRepository.save(taskMapper.toEntity(taskDTO)).map(taskMapper::toDto);
    }

    @Override
    public Mono<TaskDTO> partialUpdate(TaskDTO taskDTO) {
        LOG.debug("Request to partially update Task : {}", taskDTO);

        return taskRepository
            .findById(taskDTO.getId())
            .map(existingTask -> {
                taskMapper.partialUpdate(existingTask, taskDTO);

                return existingTask;
            })
            .flatMap(taskRepository::save)
            .map(taskMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TaskDTO> findAll() {
        LOG.debug("Request to get all Tasks");
        return taskRepository.findAll().map(taskMapper::toDto);
    }

    public Mono<Long> countAll() {
        return taskRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TaskDTO> findOne(Long id) {
        LOG.debug("Request to get Task : {}", id);
        return taskRepository.findById(id).map(taskMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Task : {}", id);
        return taskRepository.deleteById(id);
    }
}
