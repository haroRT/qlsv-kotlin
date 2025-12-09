package com.kma.qlsv.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class ScoreWithSubject(
    val score: ScoreModel? = ScoreModel(),
    val subject: SubjectModel? = SubjectModel()
) : Parcelable