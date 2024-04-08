package spl.cards.app.model.api.helius

import com.squareup.moshi.Json

data class Content(
    @Json(name = "\$schema") val schema: String,
    val json_uri: String,
    val files: List<File>,
    val metadata: Metadata,
    val links: Map<String, String>
)
