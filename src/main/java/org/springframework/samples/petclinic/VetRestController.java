package org.springframework.samples.petclinic;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/")
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

	@GetMapping("/{id}")
	public Optional<VetDto> findById(@PathVariable Integer id) {
		Optional<Vet> vetOptional = vetRepository.findById(id);
		VetDto vetDto = vetMapper.toDto(vetOptional.orElse(null));
		return Optional.ofNullable(vetDto);
	}
}

