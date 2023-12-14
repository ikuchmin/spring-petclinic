package org.springframework.samples.petclinic.vet;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/rest/vet")
public class VetRestController {

	private final VetRepository vetRepository;

	private final VetMapper vetMapper;

	public VetRestController(VetRepository vetRepository,
							 VetMapper vetMapper) {
		this.vetRepository = vetRepository;
		this.vetMapper = vetMapper;
	}

	@GetMapping
	public Page<VetDto> findAll(Pageable pageable) throws DataAccessException {
		Page<Vet> vets = vetRepository.findAll(pageable);
		Page<VetDto> vetDtoPage = vets.map(vetMapper::toDto);
		return vetDtoPage;
	}
}

