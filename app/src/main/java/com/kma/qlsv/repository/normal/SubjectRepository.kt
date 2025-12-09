package com.kma.qlsv.repository.normal

import com.google.firebase.firestore.FirebaseFirestore
import com.kma.qlsv.data.SubjectModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val subjectsCollection = firestore.collection("subjects")

    suspend fun addSubject(subject: SubjectModel): Result<String> {
        return try {
            val documentRef = subjectsCollection.document(subject.id)
            documentRef.set(subject).await()
            Result.success(subject.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSubject(subject: SubjectModel): Result<Unit> {
        return try {
            subjectsCollection.document(subject.id)
                .set(subject)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteSubject(subjectId: String): Result<Unit> {
        return try {
            subjectsCollection.document(subjectId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSubject(subjectId: String): Result<SubjectModel?> {
        return try {
            val documentSnapshot = subjectsCollection.document(subjectId)
                .get()
                .await()
            
            if (documentSnapshot.exists()) {
                val subject = documentSnapshot.toObject(SubjectModel::class.java)
                Result.success(subject)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllSubjects(): Result<List<SubjectModel>> {
        return try {
            val querySnapshot = subjectsCollection
                .get()
                .await()
            
            val subjects = querySnapshot.documents.mapNotNull { document ->
                document.toObject(SubjectModel::class.java)
            }
            Result.success(subjects)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSubjectsByName(name: String): Result<List<SubjectModel>> {
        return try {
            val querySnapshot = subjectsCollection
                .whereEqualTo("name", name)
                .get()
                .await()
            
            val subjects = querySnapshot.documents.mapNotNull { document ->
                document.toObject(SubjectModel::class.java)
            }
            Result.success(subjects)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}