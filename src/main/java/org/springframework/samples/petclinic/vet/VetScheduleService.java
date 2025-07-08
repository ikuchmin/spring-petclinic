package org.springframework.samples.petclinic.vet;

import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VetScheduleService {

	private final VetRepository vetRepository;

	public VetScheduleService(VetRepository vetRepository) {
		this.vetRepository = vetRepository;
	}

	public Vet findAppropriateVet(Pet pet, Visit visit) {
		var surgeryId = 2;

		return vetRepository.findBySpecialties_IdIn(List.of(surgeryId)).stream().findAny().orElseThrow();
	}
}
