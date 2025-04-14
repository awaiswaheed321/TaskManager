package com.awais.taskmanager.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class TaskManagerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TaskRepository taskRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/tasks";
        taskRepository.deleteAll();
    }

    @Test
    void createTask_shouldPersistAndReturnTask() {
        TaskDto taskDto = new TaskDto();
        taskDto.setTitle("New Task");
        taskDto.setDescription("Do something");
        taskDto.setCompleted(false);

        ResponseEntity<TaskDto> response = restTemplate.postForEntity(baseUrl, taskDto, TaskDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TaskDto createdTask = response.getBody();
        assertNotNull(createdTask);
        assertNotNull(createdTask.getId());
        assertEquals("New Task", createdTask.getTitle());
        assertEquals("Do something", createdTask.getDescription());
        assertFalse(createdTask.isCompleted());

        assertEquals(1, taskRepository.count());
        var savedTask = taskRepository.findById(createdTask.getId());
        assertTrue(savedTask.isPresent());
        assertEquals("New Task", savedTask.get().getTitle());
    }

    @Test
    void getAllTasks_shouldReturnAllTasks() {
        TaskDto task1 = new TaskDto();
        task1.setTitle("Task 1");
        task1.setDescription("First task");
        task1.setCompleted(false);
        restTemplate.postForEntity(baseUrl, task1, TaskDto.class);

        TaskDto task2 = new TaskDto();
        task2.setTitle("Task 2");
        task2.setDescription("Second task");
        task2.setCompleted(true);
        restTemplate.postForEntity(baseUrl, task2, TaskDto.class);

        ResponseEntity<TaskDto[]> response = restTemplate.getForEntity(baseUrl, TaskDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TaskDto[] tasks = response.getBody();
        assertNotNull(tasks);
        assertEquals(2, tasks.length);
        assertEquals("Task 1", tasks[0].getTitle());
        assertEquals("Task 2", tasks[1].getTitle());
        assertFalse(tasks[0].isCompleted());
        assertTrue(tasks[1].isCompleted());

        assertEquals(2, taskRepository.count());
    }

    @Test
    void getTaskById_shouldReturnTask() {
        TaskDto taskDto = new TaskDto();
        taskDto.setTitle("Single Task");
        taskDto.setDescription("Details");
        taskDto.setCompleted(false);
        ResponseEntity<TaskDto> createResponse = restTemplate.postForEntity(baseUrl, taskDto, TaskDto.class);
        Long taskId = createResponse.getBody().getId();

        ResponseEntity<TaskDto> response = restTemplate.getForEntity(baseUrl + "/" + taskId, TaskDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TaskDto retrievedTask = response.getBody();
        assertNotNull(retrievedTask);
        assertEquals(taskId, retrievedTask.getId());
        assertEquals("Single Task", retrievedTask.getTitle());
        assertEquals("Details", retrievedTask.getDescription());
        assertFalse(retrievedTask.isCompleted());
    }

    @Test
    void getTaskById_shouldReturnNotFoundForInvalidId() {
        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/999",
                HttpMethod.GET,
                null,
                String.class
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Task not found with id: 999"));
    }
}
