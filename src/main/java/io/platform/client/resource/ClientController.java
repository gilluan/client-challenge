package io.platform.client.resource;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.platform.client.dto.ClientDTO;
import io.platform.client.exceptions.ApiErrors;
import io.platform.client.model.Client;
import io.platform.client.service.ClientService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/clients")
public class ClientController {

	@Autowired
	private ClientService service;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private ObjectMapper objectMapper;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ClientDTO create(@RequestBody
	@Valid
	ClientDTO dto) {
		log.info("creating new client");
		Client savedClient = service.save(mapRequest(dto));

		log.info("client saved with id: {}", savedClient.getId());
		return mapper.map(savedClient, ClientDTO.class);
	}

	@GetMapping("{id}")
	public ClientDTO get(@PathVariable Long id) {
		log.info(" obtaining details for client id: {} ", id);
		return service.getById(id).map(this::mapResponse)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		log.info(" deleting client of id: {} ", id);
		Client client = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		service.delete(client);
	}

	@PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
	public ClientDTO updatePartialClient(@PathVariable Long id, @RequestBody JsonPatch patch) {
		return service.getById(id).map(client -> {
			client = service.update(applyPatchToCustomer(patch, client));
			return mapResponse(client);
		}).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	@PutMapping("{id}")
	public ClientDTO update(@PathVariable Long id, @RequestBody @Valid ClientDTO dto) {
		log.info(" updating client of id: {} ", id);
		return service.getById(id).map(client -> {
			client.setBirth(dto.getBirth());
			client.setName(dto.getName());
			client.setCpf(dto.getCpf());
			client = service.update(client);
			return mapResponse(client);
		}).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	@GetMapping
	public Page<ClientDTO> find(ClientDTO dto, Pageable pageRequest) {
		Client filter = mapRequest(dto);
		Page<Client> result = service.find(filter, pageRequest);
		List<ClientDTO> list = result.getContent().stream().map(this::mapResponse).collect(Collectors.toList());
		return new PageImpl<ClientDTO>(list, pageRequest, result.getTotalElements());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrors handleExceptions(MethodArgumentNotValidException ex) {
		BindingResult bindingResult = ex.getBindingResult();
		return new ApiErrors(bindingResult);
	}

	private Client mapRequest(ClientDTO dto) {
		return mapper.map(dto, Client.class);
	}

	private ClientDTO mapResponse(Client model) {
		return mapper.map(model, ClientDTO.class);
	}

	private Client applyPatchToCustomer(JsonPatch patch, Client target) {
		try {
			JsonNode patched = patch.apply(objectMapper.convertValue(target, JsonNode.class));
			return objectMapper.treeToValue(patched, Client.class);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}
}
