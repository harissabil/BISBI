package com.bisbiai.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PronunciationAssessmentResponse(

	@field:SerializedName("accuracyScore")
	val accuracyScore: Any,

	@field:SerializedName("completenessScore")
	val completenessScore: Any,

	@field:SerializedName("prosodyScore")
	val prosodyScore: Any,

	@field:SerializedName("words")
	val words: List<WordsItem>,

	@field:SerializedName("fluencyScore")
	val fluencyScore: Any,

	@field:SerializedName("pronunciationScore")
	val pronunciationScore: Any,

	@field:SerializedName("recognizedText")
	val recognizedText: String
)

data class PhonemesItem(

	@field:SerializedName("accuracyScore")
	val accuracyScore: Any,

	@field:SerializedName("phoneme")
	val phoneme: String
)

data class WordsItem(

	@field:SerializedName("accuracyScore")
	val accuracyScore: Any,

	@field:SerializedName("errorType")
	val errorType: String,

	@field:SerializedName("word")
	val word: String,

	@field:SerializedName("phonemes")
	val phonemes: List<PhonemesItem>
)
