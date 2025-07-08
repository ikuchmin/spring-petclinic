package org.springframework.samples.petclinic.owner;

import org.mapstruct.*;
import org.springframework.samples.petclinic.owner.rest.PetCrudRestDto;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PetMapper {
	@Mapping(source = "typeId", target = "type.id")
	Pet toEntity(PetCrudRestDto petCrudRestDto);

	@Mapping(target = "visitIds", expression = "java(visitsToVisitIds(pet.getVisits()))")
	@Mapping(source = "type.id", target = "typeId")
	PetCrudRestDto toPetCrudRestDto(Pet pet);

	@Mapping(source = "typeId", target = "type")
	Pet updateWithNull(PetCrudRestDto petCrudRestDto, @MappingTarget Pet pet);

	default PetType createPetType(Integer typeId) {
		if (typeId == null) {
			return null;
		}
		PetType petType = new PetType();
		petType.setId(typeId);
		return petType;
	}

	default Set<Integer> visitsToVisitIds(Collection<Visit> visits) {
		return visits.stream().map(Visit::getId).collect(Collectors.toSet());
	}
}
