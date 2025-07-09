@file:Suppress("UNNECESSARY_NOT_NULL_ASSERTION")

import org.assertj.core.api.Assertions.assertThat

val petIdsRegex = "\"petIds\":\\[(\\d+(?:,\\d+)*)\\]".toRegex()
val vetIdRegex = "\"id\":(\\d+)".toRegex()



val host = "http://localhost:8080"

val colemanPets by GET("$host/rest/owners") {
    queryParam("lastNameContains", "coleman")
} then {
    // todo: Is it really need to check code to improve exception?
    assertThat(code).isEqualTo(200)

    //val responseBody = body!!.string()

    //val contentRegex = "\"content\":\\s*\\[(.*)\\]".toRegex()
    // todo: Is it really need to check content to improve exception?
    //val content = contentRegex.find(responseBody)?.groupValues?.get(1)
    //assertThat(content).isNotBlank

    jsonPath().readList("$.content[0].petIds", Long::class.java)
        .also { assertThat(it).isNotEmpty }!!
}

val samantaAsPet by GET("$host/rest/pets/by-ids") {
    queryParam("ids", colemanPets.joinToString(","))
} then {
    // todo: Is it really need to check code to improve exception?
    assertThat(code).isEqualTo(200)

    jsonPath().readList("$[?(@.name == 'Samantha')].id", Long::class.java)
        .also { assertThat(it).isNotEmpty }.first()
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

    //val responseBody = body!!.string()

    // Assuming the response is a JSON object with a "vetId" field
    // todo: Produces NPE if no vetId found. Is it ok?
    //vetIdRegex.find(responseBody)?.groupValues?.get(1)!!
    jsonPath().readLong("$.vetId")
        .also { assertThat(it).isNotNull }
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

    val visitIdRegex = "\"id\":(\\d+)".toRegex()
    val vetIdRegex = "\"vetId\":(\\d+)".toRegex()

    val responseBody = body!!.string()

    val visitId = visitIdRegex.find(responseBody)?.groupValues?.get(1)!!
    val vetId = vetIdRegex.find(responseBody)?.groupValues?.get(1)!!

    assertThat(visitId).isNotBlank
    assertThat(vetId).isNotBlank

    visitId
}

GET("http://localhost:8080/rest/visits/{id}") {
    pathParam("id", createdVisitForSamanta)
    header("Content-Type", "application/json")
} then {

}
