package io.platform.client.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.platform.client.model.Client;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class ClientRepositoryTest {

    private static final LocalDate CLIENT_BIRTH = LocalDate.of(2010, 11, 1);

    private static final String CLIENT_CPF = "12345678909";

    private static final String CLIENT_NAME = "Client Rest Test";

    @Autowired
    TestEntityManager em;

    @Autowired
    ClientRepository repository;

    @Test
    @DisplayName("Should save a client")
    public void saveClientTest() {

        Client client = createNewClient();

        Client savedClient = repository.save(client);

        assertThat(savedClient.getId()).isNotNull();

    }

    @Test
    @DisplayName("Should delete a client")
    public void deleteClientTest() {

        Client client = createNewClient();
        em.persist(client);
        Client foundClient = em.find(Client.class, client.getId());

        repository.delete(foundClient);

        Client deletedClient = em.find(Client.class, client.getId());
        assertThat(deletedClient).isNull();

    }


    @Test
    @DisplayName("Should return true when cpf exist")
    public void returnTrueWhenIsbnExists(){
        Client client = createNewClient();
        em.persist(client);

        boolean exists = repository.existsByCpf(CLIENT_CPF);

        assertThat(exists).isTrue();
    }


    @Test
    @DisplayName("Should return false when cpf doesn't exists")
    public void returnFalseWhenIsbnDoesntExist(){
        boolean exists = repository.existsByCpf(CLIENT_CPF);
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should retrieve a book by id.")
    public void findByIdTest(){
        Client client = createNewClient();
        em.persist(client);
        Optional<Client> foundClient = repository.findById(client.getId());
        assertThat(foundClient.isPresent()).isTrue();
    }

    public static Client createNewClient() {
        return Client.builder().name(CLIENT_NAME).cpf(CLIENT_CPF).birth(CLIENT_BIRTH).build();
    }

}
