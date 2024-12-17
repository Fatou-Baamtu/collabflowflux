package com.collabflow.domain;

import static com.collabflow.domain.SubTaskTestSamples.*;
import static com.collabflow.domain.TaskTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.collabflow.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SubTaskTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SubTask.class);
        SubTask subTask1 = getSubTaskSample1();
        SubTask subTask2 = new SubTask();
        assertThat(subTask1).isNotEqualTo(subTask2);

        subTask2.setId(subTask1.getId());
        assertThat(subTask1).isEqualTo(subTask2);

        subTask2 = getSubTaskSample2();
        assertThat(subTask1).isNotEqualTo(subTask2);
    }

    @Test
    void taskTest() {
        SubTask subTask = getSubTaskRandomSampleGenerator();
        Task taskBack = getTaskRandomSampleGenerator();

        subTask.setTask(taskBack);
        assertThat(subTask.getTask()).isEqualTo(taskBack);

        subTask.task(null);
        assertThat(subTask.getTask()).isNull();
    }
}
