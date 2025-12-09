package com.kma.qlsv.repository.auth

import com.google.firebase.auth.FirebaseUser

interface IAuthRepository {
    suspend fun login(email: String, password: String): Result<FirebaseUser?>
    suspend fun register(email: String, password: String): Result<FirebaseUser?>
    fun getCurrentUser(): FirebaseUser?
    fun logout()
}