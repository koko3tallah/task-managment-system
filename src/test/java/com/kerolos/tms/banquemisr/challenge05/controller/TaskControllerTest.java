package com.kerolos.tms.banquemisr.challenge05.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kerolos.tms.banquemisr.challenge05.data.dto.TaskRequest;
import com.kerolos.tms.banquemisr.challenge05.data.dto.TaskResponse;
import com.kerolos.tms.banquemisr.challenge05.data.enums.TaskPriority;
import com.kerolos.tms.banquemisr.challenge05.data.enums.TaskStatus;
import com.kerolos.tms.banquemisr.challenge05.service.TaskService;
import com.kerolos.tms.banquemisr.challenge05.util.JsonUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.OffsetDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest()
@ContextConfiguration(classes = TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    ObjectMapper objectMapper = JsonUtils.getJacksonObjectMapper();

    @Test
    public void createTaskShouldReturnCreated() throws Exception {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTitle("title");
        taskRequest.setDescription("description");
        taskRequest.setPriority(TaskPriority.LOW);
        taskRequest.setStatus(TaskStatus.TODO);
        taskRequest.setDueDate(OffsetDateTime.MAX);
        TaskResponse taskResponse = new TaskResponse();

        when(taskService.createTask(any(TaskRequest.class))).thenReturn(taskResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/" + TaskController.PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getTaskByIdShouldReturnOk() throws Exception {
        Long taskId = 1L;
        TaskResponse taskResponse = new TaskResponse();

        when(taskService.getTaskById(taskId)).thenReturn(taskResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/" + TaskController.PATH + "/" + taskId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getAllUserTasksShouldReturnOk() throws Exception {
        when(taskService.getAllUserTasks()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/" + TaskController.PATH + "/user"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getAllTasksShouldReturnOk() throws Exception {
        when(taskService.getAllTasks()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/" + TaskController.PATH)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void updateTaskShouldReturnOk() throws Exception {
        Long taskId = 1L;
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTitle("title");
        taskRequest.setDescription("description");
        taskRequest.setPriority(TaskPriority.LOW);
        taskRequest.setStatus(TaskStatus.TODO);
        taskRequest.setDueDate(OffsetDateTime.MAX);
        TaskResponse taskResponse = new TaskResponse();

        when(taskService.updateTask(eq(taskId), any(TaskRequest.class))).thenReturn(taskResponse);

        mockMvc.perform(MockMvcRequestBuilders.put("/" + TaskController.PATH + "/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void deleteTaskShouldReturnNoContent() throws Exception {
        Long taskId = 1L;

        doNothing().when(taskService).deleteTask(taskId);

        mockMvc.perform(delete("/" + TaskController.PATH + "/" + taskId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void searchTasksShouldReturnOk() throws Exception {
        when(taskService.searchTasks(any(), any(), any(), any(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/" + TaskController.PATH + "/search")
                        .param("title", "test")
                        .param("description", "test")
                        .param("MockMvcResultMatchers.status", "TODO")
                        .param("priority", "HIGH")
                        .param("dueDate", "2024-10-10T10:00:00Z")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }
}