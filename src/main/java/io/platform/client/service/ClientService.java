package io.platform.client.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.platform.client.model.Client;


public interface ClientService {

	Client save(Client entity);

	Optional<Client> getById(Long id);

	void delete(Client client);

	Client update(Client client);

	Page<Client> find( Client filter, Pageable pageRequest );

}
