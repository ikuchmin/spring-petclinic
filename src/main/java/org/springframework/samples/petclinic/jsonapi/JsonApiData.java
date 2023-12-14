package org.springframework.samples.petclinic.jsonapi;

import org.springframework.samples.petclinic.vet.VetDto;

public record JsonApiData(String type, String id, VetDto attributes) {
}
