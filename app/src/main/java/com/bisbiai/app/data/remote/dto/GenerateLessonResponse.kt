package com.bisbiai.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class GenerateLessonResponse(

	@field:SerializedName("vocabulary")
	val vocabulary: List<VocabularyItem>,

	@field:SerializedName("keyPhrases")
	val keyPhrases: List<KeyPhrasesItem>,

	@field:SerializedName("scenarioTitle")
	val scenarioTitle: ScenarioTitle,

	@field:SerializedName("grammarTips")
	val grammarTips: List<GrammarTipsItem>
)

@Serializable
data class KeyPhrasesItem(

	@field:SerializedName("phrase")
	val phrase: Phrase
)

@Serializable
data class Example(

	@field:SerializedName("en")
	val en: String,

	@field:SerializedName("id")
	val id: String
)

@Serializable
data class Tip(

	@field:SerializedName("en")
	val en: String,

	@field:SerializedName("id")
	val id: String
)

@Serializable
data class VocabularyItem(

	@field:SerializedName("term")
	val term: Term
)

@Serializable
data class GrammarTipsItem(

	@field:SerializedName("tip")
	val tip: Tip,

	@field:SerializedName("example")
	val example: Example
)

@Serializable
data class ScenarioTitle(

	@field:SerializedName("en")
	val en: String,

	@field:SerializedName("id")
	val id: String
)

@Serializable
data class Term(

	@field:SerializedName("en")
	val en: String,

	@field:SerializedName("id")
	val id: String
)

@Serializable
data class Phrase(

	@field:SerializedName("en")
	val en: String,

	@field:SerializedName("id")
	val id: String
)