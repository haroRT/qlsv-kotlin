package com.kma.qlsv.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class SubjectModel(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val credit: Int = 0,
    val description: String = "",
) : Parcelable
