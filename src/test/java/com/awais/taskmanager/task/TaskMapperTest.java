package com.awais.taskmanager.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskMapperTest {

    private TaskMapper taskMapper;

    @BeforeEach
    void setUp() {
        taskMapper = new TaskMapper();
    }

    @Test
    void toDto_shouldMapTaskToTaskDto() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Description");
        task.setCompleted(false);

        TaskDto taskDto = taskMapper.toDto(task);

        assertNotNull(taskDto);
        assertEquals(task.getId(), taskDto.getId());
        assertEquals(task.getTitle(), taskDto.getTitle());
        assertEquals(task.getDescription(), taskDto.getDescription());
        assertEquals(task.isCompleted(), taskDto.isCompleted());
    }

    @Test
    void toEntity_shouldMapTaskDtoToTask() {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setTitle("Test Task");
        taskDto.setDescription("Description");
        taskDto.setCompleted(false);

        Task task = taskMapper.toEntity(taskDto);

        assertNotNull(task);
        assertEquals(taskDto.getId(), task.getId());
        assertEquals(taskDto.getTitle(), task.getTitle());
        assertEquals(taskDto.getDescription(), task.getDescription());
        assertEquals(taskDto.isCompleted(), task.isCompleted());
    }
}
