package com.collabflow.service.impl;

import com.collabflow.repository.AuditLogRepository;
import com.collabflow.service.AuditLogService;
import com.collabflow.service.dto.AuditLogDTO;
import com.collabflow.service.mapper.AuditLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.collabflow.domain.AuditLog}.
 */
@Service
@Transactional
public class AuditLogServiceImpl implements AuditLogService {

    private static final Logger LOG = LoggerFactory.getLogger(AuditLogServiceImpl.class);

    private final AuditLogRepository auditLogRepository;

    private final AuditLogMapper auditLogMapper;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository, AuditLogMapper auditLogMapper) {
        this.auditLogRepository = auditLogRepository;
        this.auditLogMapper = auditLogMapper;
    }

    @Override
    public Mono<AuditLogDTO> save(AuditLogDTO auditLogDTO) {
        LOG.debug("Request to save AuditLog : {}", auditLogDTO);
        return auditLogRepository.save(auditLogMapper.toEntity(auditLogDTO)).map(auditLogMapper::toDto);
    }

    @Override
    public Mono<AuditLogDTO> update(AuditLogDTO auditLogDTO) {
        LOG.debug("Request to update AuditLog : {}", auditLogDTO);
        return auditLogRepository.save(auditLogMapper.toEntity(auditLogDTO)).map(auditLogMapper::toDto);
    }

    @Override
    public Mono<AuditLogDTO> partialUpdate(AuditLogDTO auditLogDTO) {
        LOG.debug("Request to partially update AuditLog : {}", auditLogDTO);

        return auditLogRepository
            .findById(auditLogDTO.getId())
            .map(existingAuditLog -> {
                auditLogMapper.partialUpdate(existingAuditLog, auditLogDTO);

                return existingAuditLog;
            })
            .flatMap(auditLogRepository::save)
            .map(auditLogMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AuditLogDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all AuditLogs");
        return auditLogRepository.findAllBy(pageable).map(auditLogMapper::toDto);
    }

    public Mono<Long> countAll() {
        return auditLogRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<AuditLogDTO> findOne(Long id) {
        LOG.debug("Request to get AuditLog : {}", id);
        return auditLogRepository.findById(id).map(auditLogMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete AuditLog : {}", id);
        return auditLogRepository.deleteById(id);
    }
}
