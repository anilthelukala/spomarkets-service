package com.spom.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spom.service.common.CommonResponse;
import com.spom.service.dto.ProjectDto;
import com.spom.service.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;

@SpringBootTest
@AutoConfigureMockMvc
@SpringJUnitConfig
@ExtendWith(MockitoExtension.class)
//@WebFluxTest(ProjectController.class)
class ProjectControllerTest {

    @MockBean
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;


    @Autowired
    private WebTestClient webTestClient;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private String authToken;

    @BeforeEach
    void setup() {
        // Perform authentication and obtain the token
        CommonResponse response = new CommonResponse();
        response = authenticateAndGetToken();

        authToken = (String) response.getData().get("token");
    }

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllProjects() throws Exception {

        ProjectDto project = new ProjectDto();
        project.setId("1111eeee");
        project.setSummary("Test Project");
        project.setCreatedBy("test@example.com");

        List<ProjectDto> projects = new ArrayList<>();
        projects.add(project);
        Mockito.when(projectService.findProject()).thenReturn(projects);

        webTestClient.get().uri("/project/getAllProjects")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProjectDto.class);

        Mockito.verify(projectService, Mockito.times(1)).findProject();
    }

    @Test
    public void testGetAllProjectsError() throws Exception {
        // Mocking the service method to simulate an exception
        ProjectDto project = new ProjectDto();
        project.setId("123");
        Mockito.when(projectService.findProject()).thenThrow(new RuntimeException("Simulated error"));

        // Calling the controller method
        ResponseEntity<?> responseEntity = projectController.getProject();

        // Verifying the result
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Internal Server Error", responseEntity.getBody());
    }

    @Test
    public void testSaveProject() throws Exception {

        ProjectDto project = new ProjectDto();
        project.setSummary("Test Case");
        Mockito.when(projectService.saveProject(Mockito.any())).thenReturn(project);

        webTestClient.post()
                .uri("/project/saveProject")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(project))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProjectDto.class);


        Mockito.verify(projectService, Mockito.times(1)).saveProject(Mockito.any());
    }

    @Test
    public void testSaveProjectError() throws Exception {
        // Mocking the service method to simulate an exception
        ProjectDto project = new ProjectDto();
        project.setId("123");
        Mockito.when(projectService.saveProject(project)).thenThrow(new RuntimeException("Simulated error"));

        // Calling the controller method
        ResponseEntity<?> responseEntity = projectController.saveProject(project);

        // Verifying the result
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Internal Server Error", responseEntity.getBody());
    }

    @Test
    public void testDeleteProject() throws Exception {
        String projectId = "ee333";

        Mockito.when(projectService.deleteProject(projectId)).thenReturn(Mono.just("Project deleted successfully"));

        webTestClient.delete().uri("/project/deleteProject/{id}", projectId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class);

        Mockito.verify(projectService, times(1)).deleteProject(projectId);
    }


    @Test
    public void testDeleteProjectError() throws Exception {
        // Mocking the service method to simulate an exception
        String projectId = "123";
        Mockito.when(projectService.deleteProject(projectId)).thenThrow(new RuntimeException("Simulated error"));

        // Calling the controller method
        ResponseEntity<?> responseEntity = projectController.deleteProject(projectId);

        // Verifying the result
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Internal Server Error", responseEntity.getBody());
    }


    private CommonResponse authenticateAndGetToken() {
        // Perform authentication and retrieve the token
        // Example: Use webTestClient to authenticate and extract the token
        // You may need to adjust this based on your actual authentication mechanism

        return webTestClient.post()
                .uri("/auth/authorization")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{ \"username\": \"anilkoh@klt.comk\", \"password\": \"password\" }")
                .exchange()
                .expectStatus().isOk()
                .expectBody(CommonResponse.class)
                .returnResult()
                .getResponseBody();
    }

}

