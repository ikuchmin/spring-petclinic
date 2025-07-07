package org.springframework.samples.petclinic.vet.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetMapper;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest/vets")
public class VetCrudRestController {

	private final VetRepository vetRepository;

	private final VetMapper vetMapper;

	private final ObjectMapper objectMapper;

	public VetCrudRestController(VetRepository vetRepository,
								 VetMapper vetMapper,
								 ObjectMapper objectMapper) {
		this.vetRepository = vetRepository;
		this.vetMapper = vetMapper;
		this.objectMapper = objectMapper;
	}

	@GetMapping
	public PagedModel<VetCrudRestDto> getAll(@ModelAttribute VetCrudRestFilter filter, Pageable pageable) {
		Specification<Vet> spec = filter.toSpecification();
		Page<Vet> vets = vetRepository.findAll(spec, pageable);
		Page<VetCrudRestDto> vetCrudRestDtoPage = vets.map(vetMapper::toVetCrudRestDto);
		return new PagedModel<>(vetCrudRestDtoPage);
	}

	@GetMapping("/{id}")
	public VetCrudRestDto getOne(@PathVariable Integer id) {
		Optional<Vet> vetOptional = vetRepository.findById(id);
		return vetMapper.toVetCrudRestDto(vetOptional.orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
	}

	@GetMapping("/by-ids")
	public List<VetCrudRestDto> getMany(@RequestParam List<Integer> ids) {
		List<Vet> vets = vetRepository.findAllById(ids);
		return vets.stream()
			.map(vetMapper::toVetCrudRestDto)
			.toList();
	}

	@PostMapping
	public VetCrudRestDto create(@RequestBody @Valid VetCrudRestDto dto) {
		Vet vet = vetMapper.toEntity(dto);
		Vet resultVet = vetRepository.save(vet);
		return vetMapper.toVetCrudRestDto(resultVet);
	}

	@PatchMapping("/{id}")
	public VetCrudRestDto patch(@PathVariable Integer id, @RequestBody JsonNode patchNode) throws IOException {
		Vet vet = vetRepository.findById(id).orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

		VetCrudRestDto vetCrudRestDto = vetMapper.toVetCrudRestDto(vet);
		objectMapper.readerForUpdating(vetCrudRestDto).readValue(patchNode);
		vetMapper.updateWithNull(vetCrudRestDto, vet);

		Vet resultVet = vetRepository.save(vet);
		return vetMapper.toVetCrudRestDto(resultVet);
	}

	@PatchMapping
	public List<Integer> patchMany(@RequestParam List<Integer> ids, @RequestBody JsonNode patchNode) throws IOException {
		Collection<Vet> vets = vetRepository.findAllById(ids);

		for (Vet vet : vets) {
			VetCrudRestDto vetCrudRestDto = vetMapper.toVetCrudRestDto(vet);
			objectMapper.readerForUpdating(vetCrudRestDto).readValue(patchNode);
			vetMapper.updateWithNull(vetCrudRestDto, vet);
		}

		List<Vet> resultVets = vetRepository.saveAll(vets);
		return resultVets.stream()
			.map(Vet::getId)
			.toList();
	}

	@DeleteMapping("/{id}")
	public VetCrudRestDto delete(@PathVariable Integer id) {
		Vet vet = vetRepository.findById(id).orElse(null);
		if (vet != null) {
			vetRepository.delete(vet);
		}
		return vetMapper.toVetCrudRestDto(vet);
	}

	@DeleteMapping
	public void deleteMany(@RequestParam List<Integer> ids) {
		vetRepository.deleteAllById(ids);
	}
}
