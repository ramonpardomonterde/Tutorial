package com.ccsw.tutorial.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import com.ccsw.tutorial.client.model.ClientDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ClientIT {

    public static final String LOCALHOST = "http://localhost:";
    public static final String SERVICE_PATH = "/client";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    ParameterizedTypeReference<List<ClientDto>> responseType = new ParameterizedTypeReference<List<ClientDto>>() {
    };

    @Test
    public void findAllShouldReturnAllCliens() {

        ResponseEntity<List<ClientDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH,
                HttpMethod.GET, null, responseType);

        assertNotNull(response);
        assertEquals(3, response.getBody().size());
    }

    public static final Long NEW_CLIENT_ID = 4L;
    public static final String NEW_CLIENT_NAME = "CLI4";

    @Test
    public void saveWithoutIdShouldCreateNewClient() {

        ClientDto dto = new ClientDto();
        dto.setName(NEW_CLIENT_NAME);

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        ResponseEntity<List<ClientDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH,
                HttpMethod.GET, null, responseType);
        assertNotNull(response);
        assertEquals(4, response.getBody().size());

        ClientDto clientSeach = response.getBody().stream().filter(item -> item.getId().equals(NEW_CLIENT_ID))
                .findFirst().orElse(null);
        assertNotNull(clientSeach);
        assertEquals(NEW_CLIENT_NAME, clientSeach.getName());
    }

    public static final Long MODIFY_CLIENT_ID = 3L;

    @Test
    public void modifyWithExistIdShouldModifyClient() {

        ClientDto dto = new ClientDto();
        dto.setName(NEW_CLIENT_NAME);

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + MODIFY_CLIENT_ID, HttpMethod.PUT,
                new HttpEntity<>(dto), Void.class);

        ResponseEntity<List<ClientDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH,
                HttpMethod.GET, null, responseType);
        assertNotNull(response);
        assertEquals(3, response.getBody().size());

        ClientDto clientSearch = response.getBody().stream().filter(item -> item.getId().equals(MODIFY_CLIENT_ID))
                .findFirst().orElse(null);
        assertNotNull(clientSearch);
        assertEquals(NEW_CLIENT_NAME, clientSearch.getName());
    }

    public static final Long DELETE_CLIENT_ID = 2L;

    @Test
    public void deleteWithExistsIdShouldDeleteClient() {

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + DELETE_CLIENT_ID, HttpMethod.DELETE, null,
                Void.class);

        ResponseEntity<List<ClientDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH,
                HttpMethod.GET, null, responseType);
        assertNotNull(response);
        assertEquals(2, response.getBody().size());
    }

    @Test
    public void deleteWithNotExistsIdShouldInternalError() {

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + NEW_CLIENT_ID,
                HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void saveExistingClientShouldReturnError() {
        // Crear un cliente con el mismo nombre que ya existe
        ClientDto existingClientDto = new ClientDto();
        existingClientDto.setName("NombreExistente");
        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(existingClientDto),
                Void.class);

        // Intentar crear un nuevo cliente con el mismo nombre
        ClientDto newClientDto = new ClientDto();
        newClientDto.setName("NombreExistente");

        // Esperar una respuesta de error
        ResponseEntity<Void> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT,
                new HttpEntity<>(newClientDto), Void.class);

        // Verificar que la respuesta es un error
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}