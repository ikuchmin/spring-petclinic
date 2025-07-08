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

    val responseBody = body!!.string()

    //val contentRegex = "\"content\":\\s*\\[(.*)\\]".toRegex()
    // todo: Is it really need to check content to improve exception?
    //val content = contentRegex.find(responseBody)?.groupValues?.get(1)
    //assertThat(content).isNotBlank

    // todo: Produces NPE if no petIds found. Is it ok?
    petIdsRegex.find(responseBody)?.groupValues?.get(1)
        ?.split(",")?.map { it.trim().toLong() }!!
}

val samantaAsPet by GET("$host/rest/pets/by-ids") {
    queryParam("ids", colemanPets.joinToString(","))
} then {
    // todo: Is it really need to check code to improve exception?
    assertThat(code).isEqualTo(200)

    val responseBody = body!!.string()

    // todo: Extract pets from response body and find Samanta's id
    //val pets = Json.decodeFromString<List<Pet>>(responseBody)

    //val petsRegexp = "\\[(\\{.*\\})+\\]".toRegex()
//    val petsRegexp = "\\[.*\\]".toRegex()
//    val pets = petsRegexp.find(responseBody)?.groupValues?.get(0)!!
    //?.split("},{")!!
    // extract Samanta id
    // println("Pets: ${pets[0]}")
    val samantaId = 7
    samantaId
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

    val responseBody = body!!.string()

    // Assuming the response is a JSON object with a "vetId" field
    // todo: Produces NPE if no vetId found. Is it ok?
    vetIdRegex.find(responseBody)?.groupValues?.get(1)!!
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
