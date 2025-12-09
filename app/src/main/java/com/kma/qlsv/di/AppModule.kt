package com.kma.qlsv.di


import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kma.qlsv.repository.normal.ScoreRepository
import com.kma.qlsv.repository.normal.StudentRepository
import com.kma.qlsv.repository.auth.AuthRepository
import com.kma.qlsv.repository.auth.IAuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(firebaseAuth: FirebaseAuth): IAuthRepository = AuthRepository(firebaseAuth)

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore  = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideStudentRepository(firestore: FirebaseFirestore): StudentRepository = StudentRepository(firestore)

    @Provides
    @Singleton
    fun provideScoreRepository(firestore: FirebaseFirestore): ScoreRepository = ScoreRepository(firestore)

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext appContext: Context): Context = appContext
}