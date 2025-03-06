package com.example.landmarkrecognitioner.domain

data class Classification(
    val name : String,  // Name of classification
    val score: Float    // Probability of recognition
)
