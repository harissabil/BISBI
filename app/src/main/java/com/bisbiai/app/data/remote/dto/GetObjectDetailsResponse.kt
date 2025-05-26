package com.bisbiai.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class GetObjectDetailsResponse(

	@field:SerializedName("relatedAdjectives")
	val relatedAdjectives: List<RelatedAdjectivesItem>,

	@field:SerializedName("objectName")
	val objectName: ObjectName,

	@field:SerializedName("description")
	val description: Description,

	@field:SerializedName("exampleSentences")
	val exampleSentences: List<ExampleSentencesItem>
)

@Serializable
data class Description(

	@field:SerializedName("en")
	val en: String,

	@field:SerializedName("id")
	val id: String
)

@Serializable
data class ExampleSentencesItem(

	@field:SerializedName("en")
	val en: String,

	@field:SerializedName("id")
	val id: String
)

@Serializable
data class ObjectName(

	@field:SerializedName("en")
	val en: String,

	@field:SerializedName("id")
	val id: String
)

@Serializable
data class RelatedAdjectivesItem(

	@field:SerializedName("en")
	val en: String,

	@field:SerializedName("id")
	val id: String
)
