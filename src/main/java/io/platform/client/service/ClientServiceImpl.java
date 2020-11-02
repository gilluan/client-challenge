package io.platform.client.service;

import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.platform.client.model.Client;
import io.platform.client.repository.ClientRepository;
import io.platform.exception.BusinessException;

@Service
public class ClientServiceImpl implements ClientService {

    private ClientRepository repository;

    public ClientServiceImpl(ClientRepository repository) {
        this.repository = repository;
    }

    @Override
    public Client save(Client entity) {
        if(repository.existsByCpf(entity.getCpf())) {
           throw new BusinessException("Cpf already exists.");
        }
        return this.repository.save(entity);
    }

    @Override
    public Optional<Client> getById(Long id) {
        return this.repository.findById(id);
    }

    @Override
    public void delete(Client client) {
        if (client == null || client.getId() == null) {
            throw new IllegalArgumentException("Client id cannot be null");
        }
        this.repository.delete(client);
    }

    @Override
    public Client update(Client client) {
        if (client == null || client.getId() == null) {
            throw new IllegalArgumentException("Client id cannot be null");
        }
        return this.repository.save(client);
    }

    @Override
    public Page<Client> find(Client filter, Pageable pageRequest) {
        Example<Client> example = Example.of(filter, ExampleMatcher.matching().withIgnoreCase().withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return repository.findAll(example, pageRequest);
    }

}
