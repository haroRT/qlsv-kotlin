package com.kma.qlsv.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class ClassModel(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
) : Parcelable
