package org.springframework.samples.petclinic.owner.rest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerMapper;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest/owners")
public class OwnerCrudRestController {

	private final OwnerRepository ownerRepository;

	private final OwnerMapper ownerMapper;

	private final ObjectMapper objectMapper;

	public OwnerCrudRestController(OwnerRepository ownerRepository,
                                   OwnerMapper ownerMapper,
                                   ObjectMapper objectMapper) {
		this.ownerRepository = ownerRepository;
		this.ownerMapper = ownerMapper;
		this.objectMapper = objectMapper;
	}

	@GetMapping
	public PagedModel<OwnerCrudRestDto> getAll(@ModelAttribute OwnerCrudRestFilter filter, Pageable pageable) {
		Specification<Owner> spec = filter.toSpecification();
		Page<Owner> owners = ownerRepository.findAll(spec, pageable);
		Page<OwnerCrudRestDto> ownerCrudRestDtoPage = owners.map(ownerMapper::toOwnerCrudRestDto);
		return new PagedModel<>(ownerCrudRestDtoPage);
	}

	@GetMapping("/{id}")
	public OwnerCrudRestDto getOne(@PathVariable Integer id) {
		Optional<Owner> ownerOptional = ownerRepository.findById(id);
		return ownerMapper.toOwnerCrudRestDto(ownerOptional.orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
	}

	@GetMapping("/by-ids")
	public List<OwnerCrudRestDto> getMany(@RequestParam List<Integer> ids) {
		List<Owner> owners = ownerRepository.findAllById(ids);
		return owners.stream()
			.map(ownerMapper::toOwnerCrudRestDto)
			.toList();
	}

	@PostMapping
	public OwnerCrudRestDto create(@RequestBody @Valid OwnerCrudRestDto dto) {
		Owner owner = ownerMapper.toEntity(dto);
		Owner resultOwner = ownerRepository.save(owner);
		return ownerMapper.toOwnerCrudRestDto(resultOwner);
	}

	@PatchMapping("/{id}")
	public OwnerCrudRestDto patch(@PathVariable Integer id, @RequestBody JsonNode patchNode) throws IOException {
		Owner owner = ownerRepository.findById(id).orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

		OwnerCrudRestDto ownerCrudRestDto = ownerMapper.toOwnerCrudRestDto(owner);
		objectMapper.readerForUpdating(ownerCrudRestDto).readValue(patchNode);
		ownerMapper.updateWithNull(ownerCrudRestDto, owner);

		Owner resultOwner = ownerRepository.save(owner);
		return ownerMapper.toOwnerCrudRestDto(resultOwner);
	}

	@PatchMapping
	public List<Integer> patchMany(@RequestParam @Valid List<Integer> ids, @RequestBody JsonNode patchNode) throws IOException {
		Collection<Owner> owners = ownerRepository.findAllById(ids);

		for (Owner owner : owners) {
			OwnerCrudRestDto ownerCrudRestDto = ownerMapper.toOwnerCrudRestDto(owner);
			objectMapper.readerForUpdating(ownerCrudRestDto).readValue(patchNode);
			ownerMapper.updateWithNull(ownerCrudRestDto, owner);
		}

		List<Owner> resultOwners = ownerRepository.saveAll(owners);
		return resultOwners.stream()
			.map(Owner::getId)
			.toList();
	}

	@DeleteMapping("/{id}")
	public OwnerCrudRestDto delete(@PathVariable Integer id) {
		Owner owner = ownerRepository.findById(id).orElse(null);
		if (owner != null) {
			ownerRepository.delete(owner);
		}
		return ownerMapper.toOwnerCrudRestDto(owner);
	}

	@DeleteMapping
	public void deleteMany(@RequestParam List<Integer> ids) {
		ownerRepository.deleteAllById(ids);
	}
}
