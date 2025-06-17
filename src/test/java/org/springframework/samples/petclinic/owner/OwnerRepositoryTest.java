package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OwnerRepositoryTest {

	@Autowired
	private OwnerRepository ownerRepository;

	@Autowired
	private VetRepository vetRepository;


	@Test
	void checkAssignVetSendingMessage() {
		var ownerId = 6;
		Vet carter = vetRepository.getReferenceById(1);

		Owner coleman = ownerRepository.findById(ownerId).orElseThrow();
		Pet max = coleman.getPet(8);
		Visit visit = max.getVisits().stream().findFirst().orElseThrow();

		// visit.setVet(carter);

		// trigger event
		ownerRepository.save(coleman);
	}
}
