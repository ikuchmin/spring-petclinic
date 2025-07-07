package org.springframework.samples.petclinic.owner;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.samples.petclinic.owner.rest.VisitCrudRestDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface VisitMapper {
	Visit toEntity(VisitCrudRestDto visitCrudRestDto);

	VisitCrudRestDto toVisitCrudRestDto(Visit visit);

	Visit updateWithNull(VisitCrudRestDto visitCrudRestDto, @MappingTarget Visit visit);
}
