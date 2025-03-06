package com.example.landmarkrecognitioner.domain

import android.graphics.Bitmap

interface LandmarkClassifier {
    fun classify(bitmap: Bitmap, rotation: Int): List<Classification>
    // gets bitmap and rotation and gives out list of classifications
}