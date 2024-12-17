package com.collabflow.service.mapper;

import static com.collabflow.domain.TaskAsserts.*;
import static com.collabflow.domain.TaskTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskMapperTest {

    private TaskMapper taskMapper;

    @BeforeEach
    void setUp() {
        taskMapper = new TaskMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTaskSample1();
        var actual = taskMapper.toEntity(taskMapper.toDto(expected));
        assertTaskAllPropertiesEquals(expected, actual);
    }
}
