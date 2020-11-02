package io.platform.client.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import io.platform.client.model.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    boolean existsByCpf( String cpf );

}
