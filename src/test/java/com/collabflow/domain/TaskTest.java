package com.collabflow.domain;

import static com.collabflow.domain.CommentTestSamples.*;
import static com.collabflow.domain.ProjectTestSamples.*;
import static com.collabflow.domain.SubTaskTestSamples.*;
import static com.collabflow.domain.TaskTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.collabflow.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Task.class);
        Task task1 = getTaskSample1();
        Task task2 = new Task();
        assertThat(task1).isNotEqualTo(task2);

        task2.setId(task1.getId());
        assertThat(task1).isEqualTo(task2);

        task2 = getTaskSample2();
        assertThat(task1).isNotEqualTo(task2);
    }

    @Test
    void subTasksTest() {
        Task task = getTaskRandomSampleGenerator();
        SubTask subTaskBack = getSubTaskRandomSampleGenerator();

        task.addSubTasks(subTaskBack);
        assertThat(task.getSubTasks()).containsOnly(subTaskBack);
        assertThat(subTaskBack.getTask()).isEqualTo(task);

        task.removeSubTasks(subTaskBack);
        assertThat(task.getSubTasks()).doesNotContain(subTaskBack);
        assertThat(subTaskBack.getTask()).isNull();

        task.subTasks(new HashSet<>(Set.of(subTaskBack)));
        assertThat(task.getSubTasks()).containsOnly(subTaskBack);
        assertThat(subTaskBack.getTask()).isEqualTo(task);

        task.setSubTasks(new HashSet<>());
        assertThat(task.getSubTasks()).doesNotContain(subTaskBack);
        assertThat(subTaskBack.getTask()).isNull();
    }

    @Test
    void commentsTest() {
        Task task = getTaskRandomSampleGenerator();
        Comment commentBack = getCommentRandomSampleGenerator();

        task.addComments(commentBack);
        assertThat(task.getComments()).containsOnly(commentBack);
        assertThat(commentBack.getTask()).isEqualTo(task);

        task.removeComments(commentBack);
        assertThat(task.getComments()).doesNotContain(commentBack);
        assertThat(commentBack.getTask()).isNull();

        task.comments(new HashSet<>(Set.of(commentBack)));
        assertThat(task.getComments()).containsOnly(commentBack);
        assertThat(commentBack.getTask()).isEqualTo(task);

        task.setComments(new HashSet<>());
        assertThat(task.getComments()).doesNotContain(commentBack);
        assertThat(commentBack.getTask()).isNull();
    }

    @Test
    void projectTest() {
        Task task = getTaskRandomSampleGenerator();
        Project projectBack = getProjectRandomSampleGenerator();

        task.setProject(projectBack);
        assertThat(task.getProject()).isEqualTo(projectBack);

        task.project(null);
        assertThat(task.getProject()).isNull();
    }
}
