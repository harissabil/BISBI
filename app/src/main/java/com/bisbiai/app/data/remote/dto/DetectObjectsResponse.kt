package com.bisbiai.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DetectObjectsResponse(

	@field:SerializedName("boundingBox")
	val boundingBox: BoundingBox,

	@field:SerializedName("confidence")
	val confidence: Double,

	@field:SerializedName("objectName")
	val objectName: String
)

data class BoundingBox(

	@field:SerializedName("x")
	val x: Int,

	@field:SerializedName("width")
	val width: Int,

	@field:SerializedName("y")
	val y: Int,

	@field:SerializedName("height")
	val height: Int
)
