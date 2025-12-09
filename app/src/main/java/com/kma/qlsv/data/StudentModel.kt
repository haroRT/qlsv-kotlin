package com.kma.qlsv.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class StudentModel(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val birthday: String = "",
    val address: String = "",
    val email: String = "",
    val phone: String = "",
    val classId: String? = null
) : Parcelable
