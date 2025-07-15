import org.assertj.core.api.Assertions.assertThat

val jeanPets by GET("http://localhost:8080/rest/owners") {
    queryParam("lastNameContains", "colem")
} then {
    jsonPath().doRead<List<List<Long>>>("$.content[?(@.firstName == 'Jean' && @.lastName == 'Coleman')].petIds")
        .also { assertThat(it).hasSize(1) }.first()
}

data class Pet(val id: Long, val name: String, val birthDate: String)

val samantha by GET("http://localhost:8080/rest/pets/by-ids") {
    queryParam("ids", jeanPets.joinToString(","))
} then {
    jsonPath().doRead<List<Pet>>("$")
        .filter { it.name == "Samantha" && it.birthDate == "1995-09-04" }
        .also { assertThat(it).hasSize(1) }.first()
}

data class Vet(val id: Long, val firstName: String, val lastName: String)

val linda by POST("http://localhost:8080/rest/vets/schedule/appropriate") {
    queryParam("petId", samantha.id)
    header("Content-Type", "application/json")
    body(
        """
        {
            "date": "2025-07-15",
            "description": "Не может наступать на левую лапу"
        }
        """.trimIndent()
    )
} then {

    jsonPath().doRead<Vet>("$").also {
        assertThat(it.firstName).isEqualTo("Linda")
        assertThat(it.lastName).isEqualTo("Douglas")
    }
}

val createdVisit by POST("http://localhost:8080/rest/visits") {
    header("Content-Type", "application/json")
    body(
        """
        {
            "date": "2025-07-15",
            "description": "Не может наступать на левую лапу",
            "petId": ${samantha.id},
            "vetId": ${linda.id}
        }
        """.trimIndent()
    )
} then {
    jsonPath().doRead<Long>("$.id")
}

GET("http://localhost:8080/rest/visits/{id}") {
    pathParam("id", createdVisit)
} then {
    data class Visit(val id: Long, val date: String, val description: String, val petId: Long, val vetId: Long)

    jsonPath().doRead<Visit>("$")
        .also {
            assertThat(it.id).isEqualTo(createdVisit)
            assertThat(it.date).isEqualTo("2025-07-15")
            assertThat(it.description).isEqualTo("Не может наступать на левую лапу")
            assertThat(it.petId).isEqualTo(samantha.id)
            assertThat(it.vetId).isEqualTo(linda.id)
        }
}
