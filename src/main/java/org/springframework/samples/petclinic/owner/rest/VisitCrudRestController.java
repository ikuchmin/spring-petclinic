package org.springframework.samples.petclinic.owner.rest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.owner.PetRepository;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.samples.petclinic.owner.VisitMapper;
import org.springframework.samples.petclinic.owner.VisitRepository;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetScheduleService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest/visits")
public class VisitCrudRestController {

	private final VisitRepository visitRepository;

	private final VisitMapper visitMapper;

	private final ObjectMapper objectMapper;

	public VisitCrudRestController(VisitRepository visitRepository,
								   VisitMapper visitMapper,
								   ObjectMapper objectMapper) {
		this.visitRepository = visitRepository;
		this.visitMapper = visitMapper;
		this.objectMapper = objectMapper;
	}

	@GetMapping
	public PagedModel<VisitCrudRestDto> getAll(@ModelAttribute VisitCrudRestFilter filter, Pageable pageable) {
		Specification<Visit> spec = filter.toSpecification();
		Page<Visit> visits = visitRepository.findAll(spec, pageable);
		Page<VisitCrudRestDto> visitCrudRestDtoPage = visits.map(visitMapper::toVisitCrudRestDto);
		return new PagedModel<>(visitCrudRestDtoPage);
	}

	@GetMapping("/{id}")
	public VisitCrudRestDto getOne(@PathVariable Integer id) {
		Optional<Visit> visitOptional = visitRepository.findById(id);
		return visitMapper.toVisitCrudRestDto(visitOptional.orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
	}

	@GetMapping("/by-ids")
	public List<VisitCrudRestDto> getMany(@RequestParam List<Integer> ids) {
		List<Visit> visits = visitRepository.findAllById(ids);
		return visits.stream()
			.map(visitMapper::toVisitCrudRestDto)
			.toList();
	}

	@PostMapping
	public VisitCrudRestDto create(@RequestBody @Valid VisitCrudRestDto dto) {
		Visit visit = visitMapper.toEntity(dto);
		Visit resultVisit = visitRepository.save(visit);

		return visitMapper.toVisitCrudRestDto(resultVisit);
	}

	@PatchMapping("/{id}")
	public VisitCrudRestDto patch(@PathVariable Integer id, @RequestBody JsonNode patchNode) throws IOException {
		Visit visit = visitRepository.findById(id).orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

		VisitCrudRestDto visitCrudRestDto = visitMapper.toVisitCrudRestDto(visit);
		objectMapper.readerForUpdating(visitCrudRestDto).readValue(patchNode);
		visitMapper.updateWithNull(visitCrudRestDto, visit);

		Visit resultVisit = visitRepository.save(visit);
		return visitMapper.toVisitCrudRestDto(resultVisit);
	}

	@PatchMapping
	public List<Integer> patchMany(@RequestParam @Valid List<Integer> ids, @RequestBody JsonNode patchNode) throws IOException {
		Collection<Visit> visits = visitRepository.findAllById(ids);

		for (Visit visit : visits) {
			VisitCrudRestDto visitCrudRestDto = visitMapper.toVisitCrudRestDto(visit);
			objectMapper.readerForUpdating(visitCrudRestDto).readValue(patchNode);
			visitMapper.updateWithNull(visitCrudRestDto, visit);
		}

		List<Visit> resultVisits = visitRepository.saveAll(visits);
		return resultVisits.stream()
			.map(Visit::getId)
			.toList();
	}

	@DeleteMapping("/{id}")
	public VisitCrudRestDto delete(@PathVariable Integer id) {
		Visit visit = visitRepository.findById(id).orElse(null);
		if (visit != null) {
			visitRepository.delete(visit);
		}
		return visitMapper.toVisitCrudRestDto(visit);
	}

	@DeleteMapping
	public void deleteMany(@RequestParam List<Integer> ids) {
		visitRepository.deleteAllById(ids);
	}
}
