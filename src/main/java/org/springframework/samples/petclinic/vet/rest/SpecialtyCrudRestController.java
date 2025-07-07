package org.springframework.samples.petclinic.vet.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.vet.Specialty;
import org.springframework.samples.petclinic.vet.SpecialtyMapper;
import org.springframework.samples.petclinic.vet.SpecialtyRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest/specialties")
public class SpecialtyCrudRestController {

	private final SpecialtyRepository specialtyRepository;

	private final SpecialtyMapper specialtyMapper;

	private final ObjectMapper objectMapper;

	public SpecialtyCrudRestController(SpecialtyRepository specialtyRepository,
									   SpecialtyMapper specialtyMapper,
									   ObjectMapper objectMapper) {
		this.specialtyRepository = specialtyRepository;
		this.specialtyMapper = specialtyMapper;
		this.objectMapper = objectMapper;
	}

	@GetMapping
	public PagedModel<SpecialtyCrudRestDto> getAll(@ModelAttribute SpecialtyCrudRestFilter filter, Pageable pageable) {
		Specification<Specialty> spec = filter.toSpecification();
		Page<Specialty> specialties = specialtyRepository.findAll(spec, pageable);
		Page<SpecialtyCrudRestDto> specialtyCrudRestDtoPage = specialties.map(specialtyMapper::toSpecialtyCrudRestDto);
		return new PagedModel<>(specialtyCrudRestDtoPage);
	}

	@GetMapping("/{id}")
	public SpecialtyCrudRestDto getOne(@PathVariable Integer id) {
		Optional<Specialty> specialtyOptional = specialtyRepository.findById(id);
		return specialtyMapper.toSpecialtyCrudRestDto(specialtyOptional.orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
	}

	@GetMapping("/by-ids")
	public List<SpecialtyCrudRestDto> getMany(@RequestParam List<Integer> ids) {
		List<Specialty> specialties = specialtyRepository.findAllById(ids);
		return specialties.stream()
			.map(specialtyMapper::toSpecialtyCrudRestDto)
			.toList();
	}

	@PostMapping
	public SpecialtyCrudRestDto create(@RequestBody @Valid SpecialtyCrudRestDto dto) {
		Specialty specialty = specialtyMapper.toEntity(dto);
		Specialty resultSpecialty = specialtyRepository.save(specialty);
		return specialtyMapper.toSpecialtyCrudRestDto(resultSpecialty);
	}

	@PatchMapping("/{id}")
	public SpecialtyCrudRestDto patch(@PathVariable Integer id, @RequestBody JsonNode patchNode) throws IOException {
		Specialty specialty = specialtyRepository.findById(id).orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

		SpecialtyCrudRestDto specialtyCrudRestDto = specialtyMapper.toSpecialtyCrudRestDto(specialty);
		objectMapper.readerForUpdating(specialtyCrudRestDto).readValue(patchNode);
		specialtyMapper.updateWithNull(specialtyCrudRestDto, specialty);

		Specialty resultSpecialty = specialtyRepository.save(specialty);
		return specialtyMapper.toSpecialtyCrudRestDto(resultSpecialty);
	}

	@PatchMapping
	public List<Integer> patchMany(@RequestParam List<Integer> ids, @RequestBody JsonNode patchNode) throws IOException {
		Collection<Specialty> specialties = specialtyRepository.findAllById(ids);

		for (Specialty specialty : specialties) {
			SpecialtyCrudRestDto specialtyCrudRestDto = specialtyMapper.toSpecialtyCrudRestDto(specialty);
			objectMapper.readerForUpdating(specialtyCrudRestDto).readValue(patchNode);
			specialtyMapper.updateWithNull(specialtyCrudRestDto, specialty);
		}

		List<Specialty> resultSpecialties = specialtyRepository.saveAll(specialties);
		return resultSpecialties.stream()
			.map(Specialty::getId)
			.toList();
	}

	@DeleteMapping("/{id}")
	public SpecialtyCrudRestDto delete(@PathVariable Integer id) {
		Specialty specialty = specialtyRepository.findById(id).orElse(null);
		if (specialty != null) {
			specialtyRepository.delete(specialty);
		}
		return specialtyMapper.toSpecialtyCrudRestDto(specialty);
	}

	@DeleteMapping
	public void deleteMany(@RequestParam List<Integer> ids) {
		specialtyRepository.deleteAllById(ids);
	}
}
