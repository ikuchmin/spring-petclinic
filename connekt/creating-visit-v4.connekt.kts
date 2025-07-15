import org.assertj.core.api.Assertions.assertThat

val clientSecret: String by env

val goauth by oauth(
    authorizeEndpoint = "http://localhost:9081/realms/petclinic/protocol/openid-connect/auth",
    clientId = "sb",
    clientSecret = clientSecret,
    scope = "openid",
    tokenEndpoint = "http://localhost:9081/realms/petclinic/protocol/openid-connect/token",
    redirectUri = "http://localhost:8085/callback"
)

val host = "http://localhost:8080"

val jeanPets by GET("$host/rest/owners") {
    bearerAuth(goauth.accessToken)
    queryParam("lastNameContains", "colem")
} then {
    assertThat(code).isEqualTo(200)

    jsonPath().doRead<List<List<Long>>>("$.content[?(@.firstName == 'Jean' && @.lastName == 'Coleman')].petIds")
        .also { assertThat(it).hasSize(1) }.first()
}

data class Pet(val id: Long, val name: String, val birthDate: String) // todo: liveTemplate

val samantha by GET("$host/rest/pets/by-ids") {
    queryParam("ids", jeanPets.joinToString(","))
} then {
    jsonPath().doRead<List<Pet>>("$")
        .filter { it.name == "Samantha" && it.birthDate == "1995-09-04" }
        .also { assertThat(it).hasSize(1) }.first()
}

data class Vet(val id: Long, val firstName: String, val lastName: String)

val linda by POST("$host/rest/vets/schedule/appropriate") {
    queryParam("petId", samantha.id)
    header("Content-Type", "application/json")
    body(
        """
        {
            "date": "2025-07-15",
            "description": "Не может наступить на левую ногу"
        }
        """.trimIndent()
    )
} then {
    jsonPath().doRead<Vet>("$")
        .also {
            assertThat(it.firstName).isEqualTo("Linda")
            assertThat(it.lastName).isEqualTo("Douglas")
        }
}


val createdVisit by POST("$host/rest/visits") {
    header("Content-Type", "application/json")
    body(
        """
        {
            "date": "2025-07-15",
            "description": "Не может наступить на левую ногу",
            "petId": ${samantha.id},
            "vetId": ${linda.id}
        }
        """.trimIndent()
    )
} then {
    jsonPath().doRead<Long>("$.id")
}

GET("$host/rest/visits/{id}") {
    pathParam("id", createdVisit)
} then {
    data class Visit(val id: Long, val date: String, val description: String, val petId: Long, val vetId: Long)

    jsonPath().doRead<Visit>("$")
        .also {
            assertThat(it.id).isEqualTo(createdVisit)
            assertThat(it.date).isEqualTo("2025-07-15")
            assertThat(it.description).isEqualTo("Не может наступить на левую ногу")
            assertThat(it.petId).isEqualTo(samantha.id)
            assertThat(it.vetId).isEqualTo(linda.id)
        }
}




