package com.collabflow.service.mapper;

import static com.collabflow.domain.SubTaskAsserts.*;
import static com.collabflow.domain.SubTaskTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubTaskMapperTest {

    private SubTaskMapper subTaskMapper;

    @BeforeEach
    void setUp() {
        subTaskMapper = new SubTaskMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSubTaskSample1();
        var actual = subTaskMapper.toEntity(subTaskMapper.toDto(expected));
        assertSubTaskAllPropertiesEquals(expected, actual);
    }
}
