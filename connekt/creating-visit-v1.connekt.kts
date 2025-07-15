import org.assertj.core.api.Assertions.assertThat

val colemanPets by GET("http://localhost:8080/rest/owners") {
    queryParam("lastNameContains", "colem")
} then {
    assertThat(code).isEqualTo(200)

    data class Owner(val id: Long, val firstName: String, val lastName: String,
                     val telephone: String, val petIds: List<Long>)

    jsonPath().readList("$.content", Owner::class.java).
    also { assertThat(it).isNotEmpty }
        .find { it.firstName == "Jean" && it.lastName == "Coleman" }!!
        .also {
            assertThat(it.id).isEqualTo(6)
            assertThat(it.telephone).isEqualTo("6085552654")
        }.petIds
}

val samanthaAsPet by GET("http://localhost:8080/rest/pets/by-ids") {
    queryParam("ids", colemanPets.joinToString(","))
} then {
    assertThat(code).isEqualTo(200)

    data class Pet(val id: Long, val name: String, val birthDate: String)

    jsonPath().readList("$", Pet::class.java)
        .find { it.name == "Samantha" && it.birthDate == "1995-09-04" }!!
        .also { assertThat(it.id).isEqualTo(7) }
        .id
}

val vetForSamantha by POST("http://localhost:8080/rest/vets/schedule/appropriate") {
    queryParam("petId", "$samanthaAsPet")
    header("Content-Type", "application/json")
    body(
        """
        {
            "date": "2025-07-22",
            "description": "Болит лапа, не может на нее наступать"
        }
        """.trimIndent()
    )
} then {
    assertThat(code).isEqualTo(200)

    data class Vet(val id: Long, val firstName: String, val lastName: String)

    jsonPath().read("$", Vet::class.java)
        .also {
            assertThat(it.id).isEqualTo(3)
            assertThat(it.firstName).isEqualTo("Linda")
            assertThat(it.lastName).isEqualTo("Douglas")
        }.id
}

val createdVisit by POST("http://localhost:8080/rest/visits") {
    header("Content-Type", "application/json")
    body(
        """
        {
            "date": "2025-07-22",
            "description": "Болит лапа, не может на нее наступать",
            "petId": $samanthaAsPet,
            "vetId": $vetForSamantha
        }
        """.trimIndent()
    )
} then {
    assertThat(code).isEqualTo(200)

    jsonPath().readLong("$.id")
        .also { assertThat(it).isNotNull }
}


GET("http://localhost:8080/rest/visits/{id}") {
    pathParam("id", "$createdVisit")
} then {
    assertThat(code).isEqualTo(200)

    data class Visit(val id: Long, val date: String, val description: String,
                     val petId: Long, val vetId: Long)

    jsonPath().read("$", Visit::class.java)
        .also {
            assertThat(it.id).isEqualTo(createdVisit)
            assertThat(it.date).isEqualTo("2025-07-22")
            assertThat(it.description).isEqualTo("Болит лапа, не может на нее наступать")
            assertThat(it.petId).isEqualTo(samanthaAsPet)
            assertThat(it.vetId).isEqualTo(vetForSamantha)
        }
}
