package org.springframework.samples.petclinic.jsonapi;

import java.util.List;

public record JsonApiRoot(List<JsonApiData> data, List<JsonApiIncluded> included, JsonApiMeta meta) {
}
