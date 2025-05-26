package com.bisbiai.app.data.remote.dto

import com.google.gson.annotations.SerializedName

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

data class KeyPhrasesItem(

	@field:SerializedName("phrase")
	val phrase: Phrase
)

data class Example(

	@field:SerializedName("en")
	val en: String,

	@field:SerializedName("id")
	val id: String
)

data class Tip(

	@field:SerializedName("en")
	val en: String,

	@field:SerializedName("id")
	val id: String
)

data class VocabularyItem(

	@field:SerializedName("term")
	val term: Term
)

data class GrammarTipsItem(

	@field:SerializedName("tip")
	val tip: Tip,

	@field:SerializedName("example")
	val example: Example
)

data class ScenarioTitle(

	@field:SerializedName("en")
	val en: String,

	@field:SerializedName("id")
	val id: String
)

data class Term(

	@field:SerializedName("en")
	val en: String,

	@field:SerializedName("id")
	val id: String
)

data class Phrase(

	@field:SerializedName("en")
	val en: String,

	@field:SerializedName("id")
	val id: String
)