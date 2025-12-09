package com.kma.qlsv.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class ScoreModel(
    val id: String = UUID.randomUUID().toString(),
    val studentId: String = "",
    val subjectId: String = "",
    val name: String = "",
    val score1: Float = 0f,
    val score2: Float = 0f,
    ) : Parcelable
