package io.platform.client.resource;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import io.platform.client.dto.ClientDTO;
import io.platform.client.model.Client;
import io.platform.client.service.ClientService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = ClientController.class)
@AutoConfigureMockMvc
public class ClientControllerTest {

	private static final LocalDate CLIENT_BIRTH = LocalDate.of(2010, 11, 1);

	private static final String CLIENT_CPF = "12345678909";

	private static final long CLIENT_ID_1L = 1L;

	private static final String CLIENT_NAME = "Client Rest Test";

	static String CLIENT_PATH = "/api/clients";

	@Autowired
	MockMvc mockMvc;

	@MockBean
	ClientService service;

	@BeforeAll
	public static void setUp() {
	}

	@Test
	@DisplayName("Should list the first client page")
	public void shoudListFirstClientPage() throws Exception {

		BDDMockito.given(service.find(Mockito.any(Client.class), Mockito.any(Pageable.class)))
				.willReturn(new PageImpl<Client>(Arrays.asList(Client.builder().build()), PageRequest.of(0, 100), 1));

		String queryString = "?page=0&size=2";

		mockMvc.perform(MockMvcRequestBuilders.get(CLIENT_PATH.concat(queryString)).accept(APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("Should create client and return 201")
	public void shouldCreateValidClientTest() throws Exception {

		ClientDTO dto = createNewClient();

		Client savedClient = Client.builder().name(CLIENT_NAME).cpf(CLIENT_CPF).id(CLIENT_ID_1L).birth(CLIENT_BIRTH)
				.build();

		BDDMockito.given(service.save(Mockito.any(Client.class))).willReturn(savedClient);

		String json = new ObjectMapper().writeValueAsString(dto);
		System.out.println(json);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(CLIENT_PATH).contentType(APPLICATION_JSON)
				.accept(APPLICATION_JSON).content(json);

		mockMvc.perform(request).andExpect(status().isCreated()).andExpect(jsonPath("id").isNotEmpty())
				.andExpect(jsonPath("name").value(dto.getName())).andExpect(jsonPath("cpf").value(dto.getCpf()))
				.andExpect(jsonPath("birth").value(dto.getBirth().toString()));

	}

	@Test
	@DisplayName("Should get client by ID")
	public void shouldGetClientById() throws Exception {

		Client client = Client.builder().id(CLIENT_ID_1L).name(CLIENT_NAME).birth(CLIENT_BIRTH).cpf(CLIENT_CPF).build();

		BDDMockito.given(service.getById(CLIENT_ID_1L)).willReturn(Optional.of(client));

		mockMvc.perform(MockMvcRequestBuilders.get(CLIENT_PATH.concat("/" + CLIENT_ID_1L)).accept(APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("id").value(CLIENT_ID_1L))
				.andExpect(jsonPath("name").value(CLIENT_NAME)).andExpect(jsonPath("cpf").value(CLIENT_CPF))
				.andExpect(jsonPath("birth").value(CLIENT_BIRTH.toString()));
	}

	@Test
	@DisplayName("Should return resource not found")
	public void clientNotFoundTest() throws Exception {

		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(CLIENT_PATH.concat("/" + CLIENT_ID_1L))
				.accept(APPLICATION_JSON);

		mockMvc.perform(request).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("Should delete a client")
	public void deleteClientTest() throws Exception {

		BDDMockito.given(service.getById(anyLong())).willReturn(Optional.of(Client.builder().id(CLIENT_ID_1L).build()));

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(CLIENT_PATH.concat("/" + 1));

		mockMvc.perform(request).andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("Should return resource not found when client doesn't exists")
	public void deleteInexistentClientTest() throws Exception {

		BDDMockito.given(service.getById(anyLong())).willReturn(Optional.empty());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(CLIENT_PATH.concat("/" + 100L));

		mockMvc.perform(request).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("Should update a client")
	public void updateClientTest() throws Exception {
		Long id = 1l;
		String json = new ObjectMapper().writeValueAsString(createNewClient());

		Client updating = Client.builder().id(CLIENT_ID_1L).name(CLIENT_NAME).cpf(CLIENT_CPF).birth(CLIENT_BIRTH)
				.build();
		BDDMockito.given(service.getById(id)).willReturn(Optional.of(updating));
		Client updated = Client.builder().id(CLIENT_ID_1L).name(CLIENT_NAME).cpf(CLIENT_CPF)
				.birth(LocalDate.of(2020, 10, 1)).build();
		BDDMockito.given(service.update(updating)).willReturn(updated);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(CLIENT_PATH.concat("/" + CLIENT_ID_1L))
				.content(json).accept(APPLICATION_JSON).contentType(APPLICATION_JSON);

		mockMvc.perform(request).andExpect(status().isOk()).andExpect(jsonPath("id").value(id))
				.andExpect(jsonPath("name").value(createNewClient().getName()))
				.andExpect(jsonPath("cpf").value(createNewClient().getCpf()))
				.andExpect(jsonPath("birth").value(LocalDate.of(2020, 10, 1).toString()));
	}

	@Test
	@DisplayName("Should update partial client")
	public void updatePartialClientTest() throws Exception {
		Long id = 1l;

		String json = "[{\"op\": \"replace\", \"path\": \"/birth\", \"value\": \"2010-01-01\"}]";

		Client updating = Client.builder().id(CLIENT_ID_1L).name(CLIENT_NAME).cpf(CLIENT_CPF).birth(CLIENT_BIRTH)
				.build();

		BDDMockito.given(service.getById(updating.getId())).willReturn(Optional.of(updating));

		Client updated = Client.builder().id(CLIENT_ID_1L).name(CLIENT_NAME).cpf(CLIENT_CPF)
				.birth(LocalDate.of(2010, 1, 1)).build();

		BDDMockito.given(service.update(Mockito.any(Client.class))).willReturn(updated);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch(CLIENT_PATH.concat("/" + CLIENT_ID_1L))
				.content(json).accept(APPLICATION_JSON).contentType("application/json-patch+json");

		mockMvc.perform(request).andExpect(status().isOk()).andExpect(jsonPath("id").value(id))
				.andExpect(jsonPath("name").value(createNewClient().getName()))
				.andExpect(jsonPath("cpf").value(createNewClient().getCpf()))
				.andExpect(jsonPath("birth").value(LocalDate.of(2010, 1, 1).toString()));
	}

	@Test
	@DisplayName("Should return 404 when try update an nonexistent client")
	public void updateNonexistentClientTest() throws Exception {

		String json = new ObjectMapper().writeValueAsString(createNewClient());
		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(CLIENT_PATH.concat("/" + 1)).content(json)
				.accept(APPLICATION_JSON).contentType(APPLICATION_JSON);

		mockMvc.perform(request).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("Should filter clients")
	public void findClientsTest() throws Exception {

		Long id = 1l;

		Client client = Client.builder().id(id).name(CLIENT_NAME).birth(CLIENT_BIRTH).build();

		BDDMockito.given(service.find(Mockito.any(Client.class), Mockito.any(Pageable.class)))
				.willReturn(new PageImpl<Client>(Arrays.asList(client), PageRequest.of(0, 100), 1));

		String queryString = String.format("?name=%s&cpf=%s&page=0&size=100", client.getName(), client.getCpf());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(CLIENT_PATH.concat(queryString))
				.accept(APPLICATION_JSON);

		mockMvc.perform(request).andExpect(status().isOk()).andExpect(jsonPath("content", Matchers.hasSize(1)))
				.andExpect(jsonPath("totalElements").value(1)).andExpect(jsonPath("pageable.pageSize").value(100))
				.andExpect(jsonPath("pageable.pageNumber").value(0));
	}

	@Test
	@DisplayName("Shouldn't create a client with invalid value")
	public void shouldCreateInvalidClientTest() throws Exception {

		String json = new ObjectMapper().writeValueAsString(new ClientDTO());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(CLIENT_PATH).contentType(APPLICATION_JSON)
				.accept(APPLICATION_JSON).content(json);

		mockMvc.perform(request).andExpect(status().isBadRequest()).andExpect(jsonPath("errors", Matchers.hasSize(2)));
	}

	@Test
	@DisplayName("Shouldn't create a client with invalid CPF")
	public void shouldntCreateClientWithInvalidCPFTest() throws Exception {
	}

	private ClientDTO createNewClient() {
		return ClientDTO.builder().name(CLIENT_NAME).id(CLIENT_ID_1L).cpf(CLIENT_CPF).birth(CLIENT_BIRTH).build();
	}

}
