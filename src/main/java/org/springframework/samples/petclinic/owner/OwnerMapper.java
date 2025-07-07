package org.springframework.samples.petclinic.owner;

import org.mapstruct.*;
import org.springframework.samples.petclinic.owner.rest.OwnerCrudRestDto;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface OwnerMapper {
	Owner toEntity(OwnerCrudRestDto ownerCrudRestDto);

	@Mapping(target = "petIds", expression = "java(petsToPetIds(owner.getPets()))")
	OwnerCrudRestDto toOwnerCrudRestDto(Owner owner);

	Owner updateWithNull(OwnerCrudRestDto ownerCrudRestDto, @MappingTarget Owner owner);

	default List<Integer> petsToPetIds(List<Pet> pets) {
		return pets.stream().map(Pet::getId).toList();
	}
}
