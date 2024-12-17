package com.collabflow.service.impl;

import com.collabflow.repository.SubTaskRepository;
import com.collabflow.service.SubTaskService;
import com.collabflow.service.dto.SubTaskDTO;
import com.collabflow.service.mapper.SubTaskMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.collabflow.domain.SubTask}.
 */
@Service
@Transactional
public class SubTaskServiceImpl implements SubTaskService {

    private static final Logger LOG = LoggerFactory.getLogger(SubTaskServiceImpl.class);

    private final SubTaskRepository subTaskRepository;

    private final SubTaskMapper subTaskMapper;

    public SubTaskServiceImpl(SubTaskRepository subTaskRepository, SubTaskMapper subTaskMapper) {
        this.subTaskRepository = subTaskRepository;
        this.subTaskMapper = subTaskMapper;
    }

    @Override
    public Mono<SubTaskDTO> save(SubTaskDTO subTaskDTO) {
        LOG.debug("Request to save SubTask : {}", subTaskDTO);
        return subTaskRepository.save(subTaskMapper.toEntity(subTaskDTO)).map(subTaskMapper::toDto);
    }

    @Override
    public Mono<SubTaskDTO> update(SubTaskDTO subTaskDTO) {
        LOG.debug("Request to update SubTask : {}", subTaskDTO);
        return subTaskRepository.save(subTaskMapper.toEntity(subTaskDTO)).map(subTaskMapper::toDto);
    }

    @Override
    public Mono<SubTaskDTO> partialUpdate(SubTaskDTO subTaskDTO) {
        LOG.debug("Request to partially update SubTask : {}", subTaskDTO);

        return subTaskRepository
            .findById(subTaskDTO.getId())
            .map(existingSubTask -> {
                subTaskMapper.partialUpdate(existingSubTask, subTaskDTO);

                return existingSubTask;
            })
            .flatMap(subTaskRepository::save)
            .map(subTaskMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SubTaskDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all SubTasks");
        return subTaskRepository.findAllBy(pageable).map(subTaskMapper::toDto);
    }

    public Mono<Long> countAll() {
        return subTaskRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<SubTaskDTO> findOne(Long id) {
        LOG.debug("Request to get SubTask : {}", id);
        return subTaskRepository.findById(id).map(subTaskMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete SubTask : {}", id);
        return subTaskRepository.deleteById(id);
    }
}
