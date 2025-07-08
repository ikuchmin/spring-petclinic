import org.assertj.core.api.Assertions.assertThat

val host = "http://localhost:8080"

val petIds by GET("http://localhost:8080/rest/owners") {
    queryParam("lastNameContains", "coleman")
} then {
    assertThat(code).isEqualTo(200)

    val contentRegex = "\"content\":\\s*\\[(.*)\\]".toRegex()
    val petIdsRegex = "\"petIds\":\\[(\\d+(?:,\\d+)*)\\]".toRegex()

    val responseBody = body!!.string()
    val content = contentRegex.find(responseBody)?.groupValues?.get(1)

    assertThat(content).isNotBlank

    petIdsRegex.find(responseBody)?.groupValues?.get(1)
        ?.split(",")?.map { it.trim().toLong() }!!
}

data class Pet(
    val id: Int,
    val name: String,
    val birthDate: String,
    val typeId: Int,
    val visitIds: List<Int>
)

GET("http://localhost:8080/rest/pets/by-ids") {
    queryParam("ids", petIds.joinToString(","))
} then {
    assertThat(code).isEqualTo(200)

    val responseBody = body!!.string()

    //val pets = Json.decodeFromString<List<Pet>>(responseBody)

    //val petsRegexp = "\\[(\\{.*\\})+\\]".toRegex()
//    val petsRegexp = "\\[.*\\]".toRegex()
//    val pets = petsRegexp.find(responseBody)?.groupValues?.get(0)!!
    //?.split("},{")!!
    // extract Samanta id
   // println("Pets: ${pets[0]}")
}

data class VisitResponseDto(
    val id: Int,
    val date: String,
    val description: String,
    val petId: Int,
    val vetId: Int
)

POST("http://localhost:8080/rest/visits") {
    header("Content-Type", "application/json")
    body(
        """
        {
            "date": "2025-07-07",
            "description": "Grooming",
            "petId": 7
        }
        """.trimIndent()
    )
} then {
    val visitIdRegex = "\"id\":(\\d+)".toRegex()
    val vetIdRegex = "\"vetId\":(\\d+)".toRegex()

    val responseBody = body!!.string()

    val visitId = visitIdRegex.find(responseBody)?.groupValues?.get(1)
    val vetId = vetIdRegex.find(responseBody)?.groupValues?.get(1)

    assertThat(visitId).isNotBlank
    assertThat(vetId).isNotBlank

    jsonPath().read("$", VisitResponseDto::class.java)
}

GET("http://localhost:8080/rest/visits/{id}") {
    pathParam("id", "11")
}
