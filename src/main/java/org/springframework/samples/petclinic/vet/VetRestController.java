package org.springframework.samples.petclinic.vet;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.jsonapi.JsonApiData;
import org.springframework.samples.petclinic.jsonapi.JsonApiMeta;
import org.springframework.samples.petclinic.jsonapi.JsonApiRoot;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/vet")
public class VetRestController {

	private final VetRepository vetRepository;

	private final VetMapper vetMapper;

	public VetRestController(VetRepository vetRepository,
							 VetMapper vetMapper) {
		this.vetRepository = vetRepository;
		this.vetMapper = vetMapper;
	}

	@GetMapping
	public JsonApiRoot findAll(@RequestParam Map<String, Object> params) throws DataAccessException {

		Object pageNumber = params.get("page[number]");
		if (pageNumber == null) {
			pageNumber = params.get("page%5Bnumber%5D");
		}


		Object paramSize = params.get("page[size]");
		if (paramSize == null) {
			paramSize = params.get("page%5Bsize%5D");
		}

		PageRequest pageRequest = PageRequest.of(Integer.valueOf((String) pageNumber) - 1, Integer.valueOf((String) paramSize));
		Page<Vet> vets = vetRepository.findAll(pageRequest);
		var vetDtoPage = vets.map(vetMapper::toDto).map(v -> new JsonApiData("vet", String.valueOf(v.id()), v)).stream().toList();

		return new JsonApiRoot(vetDtoPage, new ArrayList<>(), new JsonApiMeta());
	}
}

