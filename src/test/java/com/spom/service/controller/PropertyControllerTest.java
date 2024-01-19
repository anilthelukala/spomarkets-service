package com.spom.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spom.service.common.CommonResponse;
import com.spom.service.dto.PropertyDto;
import com.spom.service.model.PropertyEntity;
import com.spom.service.service.PropertyService;
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
class PropertyControllerTest {

	@MockBean
    private PropertyService propertyService;

	@InjectMocks
    private PropertyController propertyController;


    @Autowired
    private WebTestClient webTestClient;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private String authToken;
    
    @BeforeEach
    void setup() {
        // Perform authentication and obtain the token
    	CommonResponse response = new CommonResponse();
    	response = authenticateAndGetToken();
        
        authToken=(String) response.getData().get("token");
    }

    @BeforeEach
    void setUp() throws Exception{
    	MockitoAnnotations.openMocks(this);
    }
    
    @Test
    public void testGetByPropertyId() throws Exception{
    	
    	PropertyDto property = new PropertyDto();
    	property.setId("www");
    	property.setDescription("Test Project");
    	property.setCreatedBy("test@example.com");
		String propertyId="de3";
       
		Mockito.when(propertyService.findByPropertyId(propertyId)).thenReturn(property).thenThrow();

        webTestClient.get().uri("/property/propertyId/{propertyId}",propertyId)
        .header(HttpHeaders.AUTHORIZATION, "Bearer "+authToken)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PropertyEntity.class);

        	Mockito.verify(propertyService, Mockito.times(1)).findByPropertyId(propertyId);
    }

    @Test
    public void testGetByPropertyIdError() throws Exception {
        // Mocking the service method to simulate an exception
    	PropertyDto property = new PropertyDto();
    	property.setId("123");
    	String propertyId="123";
        Mockito.when(propertyService.findByPropertyId(propertyId)).thenThrow(new RuntimeException("Simulated error"));

        // Calling the controller method
        ResponseEntity<?> responseEntity = propertyController.getByPropertyId(propertyId);

        // Verifying the result
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Internal Server Error", responseEntity.getBody());
    }
    
    @Test
    public void testSaveProperty() throws Exception {
    	
    	PropertyDto property = new PropertyDto();
    	property.setDescription("Test Case");
        Mockito.when(propertyService.saveProperty(Mockito.any())).thenReturn(property).thenThrow();


        webTestClient.post()
        .uri("/property/saveProperty")
        .header(HttpHeaders.AUTHORIZATION, "Bearer "+authToken)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(property))
        .exchange()
        .expectStatus().isOk()
        .expectBody(PropertyDto.class);

        
     
        Mockito.verify(propertyService, Mockito.times(1)).saveProperty(Mockito.any());
    }

    @Test
    public void testSavePropertyError() throws Exception {
        // Mocking the service method to simulate an exception
    	PropertyDto property = new PropertyDto();
    	property.setId("123");
    	
        Mockito.when(propertyService.saveProperty(property)).thenThrow(new RuntimeException("Simulated error"));

        // Calling the controller method
        ResponseEntity<?> responseEntity = propertyController.saveProperty(property);

        // Verifying the result
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Internal Server Error", responseEntity.getBody());
    }
    
    @Test
    public void testDeleteProperty() throws Exception {
        String propertyId = "eee";

        
        Mockito.when(propertyService.deleteProperty(propertyId)).thenReturn(Mono.just("Deleted"));

        // Perform the DELETE request and verify the response
        webTestClient.delete().uri("/property/deleteProperty/{id}", propertyId)
        .header(HttpHeaders.AUTHORIZATION, "Bearer "+authToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class);
                

        
        Mockito.verify(propertyService, times(1)).deleteProperty(propertyId);
    }
    
    
    @Test
    public void testDeletePropertyError() throws Exception {
        // Mocking the service method to simulate an exception
    	String propertyId  = "123";
        Mockito.when(propertyService.deleteProperty(propertyId)).thenThrow(new RuntimeException("Simulated error"));

        // Calling the controller method
        ResponseEntity<?> responseEntity = propertyController.deleteProperty(propertyId);

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
