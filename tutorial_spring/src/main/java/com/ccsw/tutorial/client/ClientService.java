package com.ccsw.tutorial.client;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;

public interface ClientService {
    /**
     * Recupera un {@link Client} a través de su ID
     *
     * @param id PK de la entidad
     * @return {@link Client}
     */
    Client get(Long id);

    /**
     * Método para recuperar todos los {@link Client}
     *
     * @return {@link List} de {@link Client}
     */
    List<Client> findAll();

    /**
     * Método para crear o actualizar un {@link Client}
     *
     * @param id  PK de la entidad
     * @param dto datos de la entidad
     */
    void save(Long id, ClientDto dto) throws DataIntegrityViolationException;

    /**
     * Método para borrar un {@link Client}
     *
     * @param id PK de la entidad
     */
    void delete(Long id) throws Exception;
}
