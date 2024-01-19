package com.spom.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spom.service.common.CommonResponse;
import com.spom.service.dto.PropertyFractionalisationDto;
import com.spom.service.model.PropertyEntity;
import com.spom.service.model.PropertyFractionalisationEntity;
import com.spom.service.service.PropertyFractionalisationService;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;

@SpringBootTest
@AutoConfigureMockMvc
@SpringJUnitConfig
@ExtendWith(MockitoExtension.class)
class PropertyFractionalisationControllerTest {
    @MockBean
    private PropertyFractionalisationService PropertyFractionalisationService;

    @InjectMocks
    private PropertyFractionalisationController propertyFractionalisationController;

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
    public void testFindByProjectId() throws Exception {

        PropertyFractionalisationDto propertyFractionalisation = new PropertyFractionalisationDto();
        propertyFractionalisation.setId("uu");
        propertyFractionalisation.setBlockSize("88");
        propertyFractionalisation.setCreatedBy("test@example.com");
        String projectId = "333";

        Mockito.when(PropertyFractionalisationService.findByProjecId(projectId)).thenReturn(propertyFractionalisation)
                .thenThrow();

        webTestClient.get().uri("/fractionalisation/projectId/{projectId}", projectId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken).exchange().expectStatus().isOk()
                .expectBodyList(PropertyEntity.class);

        Mockito.verify(PropertyFractionalisationService, Mockito.times(1)).findByProjecId(projectId);
    }

    @Test
    public void testFindByProjectIdError() throws Exception {
        // Mocking the service method to simulate an exception
        PropertyFractionalisationDto propertyFractionalisationEntity = new PropertyFractionalisationDto();
        propertyFractionalisationEntity.setId("123");
        propertyFractionalisationEntity.setBlockSize("ee3");

        String projectId = "123";
        Mockito.when(PropertyFractionalisationService.findByProjecId(projectId))
                .thenThrow(new RuntimeException("Simulated error"));

        // Calling the controller method
        ResponseEntity<?> responseEntity = propertyFractionalisationController.getByProjecId(projectId);

        // Verifying the result
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Internal Server Error", responseEntity.getBody());
    }

    @Test
    public void testSavePropertyFractionalisation() throws Exception {

        PropertyFractionalisationDto propertyFractionalisationEntity = new PropertyFractionalisationDto();
        propertyFractionalisationEntity.setBlockSize("ee3");
        Mockito.when(PropertyFractionalisationService.savePropertyFractionalisation(Mockito.any()))
                .thenReturn(propertyFractionalisationEntity).thenThrow();

        webTestClient.post().uri("/fractionalisation/savePropertyFractionalisation")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(propertyFractionalisationEntity)).exchange().expectStatus()
                .isOk().expectBody(PropertyFractionalisationEntity.class);

        Mockito.verify(PropertyFractionalisationService, Mockito.times(1)).savePropertyFractionalisation(Mockito.any());
    }

    @Test
    public void testSavePropertyFractionalisationError() throws Exception {
        // Mocking the service method to simulate an exception
        PropertyFractionalisationDto propertyFractionalisationEntity = new PropertyFractionalisationDto();
        propertyFractionalisationEntity.setId("123");
        propertyFractionalisationEntity.setBlockSize("ee3");
        Mockito.when(PropertyFractionalisationService.savePropertyFractionalisation(propertyFractionalisationEntity))
                .thenThrow(new RuntimeException("Simulated error"));

        // Calling the controller method
        ResponseEntity<?> responseEntity = propertyFractionalisationController
                .savePropertyFractionalisation(propertyFractionalisationEntity);

        // Verifying the result
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Internal Server Error", responseEntity.getBody());
    }

    @Test
    public void testDeletePropertyFractionalisation() throws Exception {
        String id = "222";

        Mockito.when(PropertyFractionalisationService.deletePropertyFractionalisation(id))
                .thenReturn(Mono.just("Deleted"));

        // Perform the DELETE request and verify the response
        webTestClient.delete().uri("/fractionalisation/deletePropertyFractionalisation/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken).exchange().expectStatus().isOk()
                .expectBody(String.class);

        Mockito.verify(PropertyFractionalisationService, times(1)).deletePropertyFractionalisation(id);
    }

    @Test
    public void testDeletePropertyFractionalisationError() throws Exception {
        // Mocking the service method to simulate an exception
        String id = "123";
        Mockito.when(PropertyFractionalisationService.deletePropertyFractionalisation(id))
                .thenThrow(new RuntimeException("Simulated error"));

        // Calling the controller method
        ResponseEntity<?> responseEntity = propertyFractionalisationController.deletePropertyFractionalisation(id);

        // Verifying the result
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Internal Server Error", responseEntity.getBody());
    }

    private CommonResponse authenticateAndGetToken() {
        // Perform authentication and retrieve the token
        // Example: Use webTestClient to authenticate and extract the token
        // You may need to adjust this based on your actual authentication mechanism

        return webTestClient.post().uri("/auth/authorization").contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{ \"username\": \"anilkoh@klt.comk\", \"password\": \"password\" }").exchange()
                .expectStatus().isOk().expectBody(CommonResponse.class).returnResult().getResponseBody();
    }

}
