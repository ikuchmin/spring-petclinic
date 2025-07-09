@file:Suppress("UNNECESSARY_NOT_NULL_ASSERTION")

import org.assertj.core.api.Assertions.assertThat

val host = "http://localhost:8080"

val colemanPets by GET("$host/rest/owners") {
    queryParam("lastNameContains", "colem")
} then {
    // todo: Is it really need to check code to improve exception?
    assertThat(code).isEqualTo(200)

    data class Owner(val id: Long, val firstName: String, val lastName: String,
                     val telephone: String,  val petIds: List<Long>)

    jsonPath().readList("$.content", Owner::class.java)
        .also { assertThat(it).isNotEmpty } // todo: to improve exception
        .find { it.firstName == "Jean" && it.lastName == "Coleman" }!!
        .also {
            assertThat(it.id).isEqualTo(6)
            assertThat(it.telephone).isEqualTo("6085552654")
        }.petIds
}

val samantaAsPet by GET("$host/rest/pets/by-ids") {
    queryParam("ids", colemanPets.joinToString(","))
} then {
    // todo: Is it really need to check code to improve exception?
    assertThat(code).isEqualTo(200)

    jsonPath().readList("$[?(@.name == 'Samantha')].id", Long::class.java)
        .also { assertThat(it).hasSize(1) } // todo: to improve exception
        .first().also { assertThat(it).isEqualTo(7) }
}

val vetForSamanta by POST("$host/rest/vets/schedule/appropriate") {
    queryParam("petId", samantaAsPet)
    header("Content-Type", "application/json")
    body(
        """
        {
            "date": "2025-07-21",
            "description": "Grooming"
        }
        """.trimIndent()
    )
} then {
    // todo: Is it really need to check code to improve exception?
    assertThat(code).isEqualTo(200)

    data class Vet(val id: Long, val firstName: String, val lastName: String, val specialtyIds: List<String>)

    jsonPath().read("$", Vet::class.java)
        .also {
            assertThat(it.id).isEqualTo(3)
            assertThat(it.firstName).isEqualTo("Linda")
            assertThat(it.lastName).isEqualTo("Douglas")
        }.id
}

val createdVisitForSamanta by POST("$host/rest/visits") {
    header("Content-Type", "application/json")
    body(
        """
        {
            "date": "2025-07-07",
            "description": "Grooming",
            "petId": 7,
            "vetId": $vetForSamanta
        }
        """.trimIndent()
    )
} then {
    jsonPath().readLong("$.id")
        .also { assertThat(it).isNotNull }
}

GET("$host/rest/visits/{id}") {
    pathParam("id", createdVisitForSamanta)
    header("Content-Type", "application/json")
} then {
    data class Visit(val id: Int, val date: String, val description: String,
                     val petId: Int, val vetId: Int)

    jsonPath().read("$", Visit::class.java)
        .also {
            assertThat(it.id).isEqualTo(createdVisitForSamanta)
            assertThat(it.date).isEqualTo("2025-07-07")
            assertThat(it.description).isEqualTo("Grooming")
            assertThat(it.petId).isEqualTo(7)
            assertThat(it.vetId).isEqualTo(vetForSamanta)
        }
}
