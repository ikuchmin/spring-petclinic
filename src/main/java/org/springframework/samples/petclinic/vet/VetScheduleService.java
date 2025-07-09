package org.springframework.samples.petclinic.vet;

import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.samples.petclinic.owner.VisitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class VetScheduleService {

	public static final int SURGERY_ID = 2;

	private final VetRepository vetRepository;

	private final VisitRepository visitRepository;

	public VetScheduleService(VetRepository vetRepository,
							  VisitRepository visitRepository) {
		this.vetRepository = vetRepository;
		this.visitRepository = visitRepository;
	}

	@Transactional
	public Vet findAppropriateVet(Pet pet, Visit visit) {
		List<Visit> visits = visitRepository.findWithVetByPet(pet);

		if (visits.isEmpty() || visits.stream().noneMatch(v -> v.getVet() != null)) {
			return vetRepository.findBySpecialties_IdIn(Collections.singleton(SURGERY_ID))
				.stream().findAny().orElseThrow();
		}

		return visits.stream()
			.filter(v -> v.getVet() != null)
			.max(Comparator.comparing(Visit::getDate))
			.orElseThrow().getVet();
	}
}
