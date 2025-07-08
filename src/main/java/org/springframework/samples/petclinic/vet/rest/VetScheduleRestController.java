package org.springframework.samples.petclinic.vet.rest;

import jakarta.validation.Valid;
import org.springframework.samples.petclinic.owner.*;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetMapper;
import org.springframework.samples.petclinic.vet.VetScheduleService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/vets/schedule")
public class VetScheduleRestController {

	private final VetScheduleService vetScheduleService;

	private final VetMapper vetMapper;

	private final PetRepository petRepository;

	private final VisitRepository visitRepository;

	private final VisitMapper visitMapper;

	public VetScheduleRestController(VetScheduleService vetScheduleService,
									 VetMapper vetMapper,
									 PetRepository petRepository,
									 VisitRepository visitRepository,
									 VisitMapper visitMapper) {
		this.vetScheduleService = vetScheduleService;
		this.vetMapper = vetMapper;
		this.petRepository = petRepository;
		this.visitRepository = visitRepository;
		this.visitMapper = visitMapper;
	}

	@PostMapping("/appropriate")
	public VetCrudRestDto findAppropriateVet(@RequestParam Integer petId,
											 @RequestBody @Valid VisitAppropriateDto visitAppropriateDto) {
		Pet pet = petRepository.getReferenceById(petId);
		Visit visit = visitMapper.toEntity(visitAppropriateDto);
		Vet vet = vetScheduleService.findAppropriateVet(pet, visit);
		return vetMapper.toVetCrudRestDto(vet);
	}
}

