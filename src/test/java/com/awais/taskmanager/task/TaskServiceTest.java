package com.awais.taskmanager.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskDto taskDto;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Description");
        task.setCompleted(false);

        taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setTitle("Test Task");
        taskDto.setDescription("Description");
        taskDto.setCompleted(false);
    }

    @Test
    void createTask_shouldSaveAndReturnTaskDto() {
        when(taskMapper.toEntity(any(TaskDto.class))).thenReturn(task);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toDto(any(Task.class))).thenReturn(taskDto);

        TaskDto result = taskService.createTask(taskDto);

        assertNotNull(result);
        assertEquals(taskDto.getId(), result.getId());
        assertEquals(taskDto.getTitle(), result.getTitle());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void getAllTasks_shouldReturnTaskDtoList() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task));
        when(taskMapper.toDto(any(Task.class))).thenReturn(taskDto);

        var result = taskService.getAllTasks();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(taskDto.getTitle(), result.get(0).getTitle());
        verify(taskRepository).findAll();
    }

    @Test
    void getTaskById_shouldReturnTaskDto() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toDto(any(Task.class))).thenReturn(taskDto);

        TaskDto result = taskService.getTaskById(1L);

        assertNotNull(result);
        assertEquals(taskDto.getId(), result.getId());
        verify(taskRepository).findById(1L);
    }

    @Test
    void getTaskById_shouldThrowExceptionIfNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.getTaskById(1L));
        verify(taskRepository).findById(1L);
    }
}
