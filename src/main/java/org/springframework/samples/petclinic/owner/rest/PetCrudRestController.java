package org.springframework.samples.petclinic.owner.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetMapper;
import org.springframework.samples.petclinic.owner.PetRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest/pets")
public class PetCrudRestController {

	private final PetRepository petRepository;

	private final PetMapper petMapper;

	private final ObjectMapper objectMapper;

	public PetCrudRestController(PetRepository petRepository,
								 PetMapper petMapper,
								 ObjectMapper objectMapper) {
		this.petRepository = petRepository;
		this.petMapper = petMapper;
		this.objectMapper = objectMapper;
	}

	@GetMapping
	public PagedModel<PetCrudRestDto> getAll(@ModelAttribute PetCrudRestFilter filter, Pageable pageable) {
		Specification<Pet> spec = filter.toSpecification();
		Page<Pet> pets = petRepository.findAll(spec, pageable);
		Page<PetCrudRestDto> petCrudRestDtoPage = pets.map(petMapper::toPetCrudRestDto);
		return new PagedModel<>(petCrudRestDtoPage);
	}

	@GetMapping("/{id}")
	public PetCrudRestDto getOne(@PathVariable Integer id) {
		Optional<Pet> petOptional = petRepository.findById(id);
		return petMapper.toPetCrudRestDto(petOptional.orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
	}

	@GetMapping("/by-ids")
	public List<PetCrudRestDto> getMany(@RequestParam List<Integer> ids) {
		List<Pet> pets = petRepository.findAllById(ids);
		return pets.stream()
			.map(petMapper::toPetCrudRestDto)
			.toList();
	}

	@PostMapping
	public PetCrudRestDto create(@RequestBody @Valid PetCrudRestDto dto) {
		Pet pet = petMapper.toEntity(dto);
		Pet resultPet = petRepository.save(pet);
		return petMapper.toPetCrudRestDto(resultPet);
	}

	@PatchMapping("/{id}")
	public PetCrudRestDto patch(@PathVariable Integer id, @RequestBody JsonNode patchNode) throws IOException {
		Pet pet = petRepository.findById(id).orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

		PetCrudRestDto petCrudRestDto = petMapper.toPetCrudRestDto(pet);
		objectMapper.readerForUpdating(petCrudRestDto).readValue(patchNode);
		petMapper.updateWithNull(petCrudRestDto, pet);

		Pet resultPet = petRepository.save(pet);
		return petMapper.toPetCrudRestDto(resultPet);
	}

	@PatchMapping
	public List<Integer> patchMany(@RequestParam List<Integer> ids, @RequestBody JsonNode patchNode) throws IOException {
		Collection<Pet> pets = petRepository.findAllById(ids);

		for (Pet pet : pets) {
			PetCrudRestDto petCrudRestDto = petMapper.toPetCrudRestDto(pet);
			objectMapper.readerForUpdating(petCrudRestDto).readValue(patchNode);
			petMapper.updateWithNull(petCrudRestDto, pet);
		}

		List<Pet> resultPets = petRepository.saveAll(pets);
		return resultPets.stream()
			.map(Pet::getId)
			.toList();
	}

	@DeleteMapping("/{id}")
	public PetCrudRestDto delete(@PathVariable Integer id) {
		Pet pet = petRepository.findById(id).orElse(null);
		if (pet != null) {
			petRepository.delete(pet);
		}
		return petMapper.toPetCrudRestDto(pet);
	}

	@DeleteMapping
	public void deleteMany(@RequestParam List<Integer> ids) {
		petRepository.deleteAllById(ids);
	}
}
