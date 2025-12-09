package com.kma.qlsv.repository.normal

import com.google.firebase.firestore.FirebaseFirestore
import com.kma.qlsv.data.StudentModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val studentsCollection = firestore.collection("students")

    suspend fun addStudent(student: StudentModel): Result<String> {
        return try {
            val documentRef = studentsCollection.document(student.id)
            documentRef.set(student).await()
            Result.success(student.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStudent(student: StudentModel): Result<Unit> {
        return try {
            studentsCollection.document(student.id)
                .set(student)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteStudent(studentId: String): Result<Unit> {
        return try {
            studentsCollection.document(studentId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStudent(studentId: String): Result<StudentModel?> {
        return try {
            val documentSnapshot = studentsCollection.document(studentId)
                .get()
                .await()
            
            if (documentSnapshot.exists()) {
                val student = documentSnapshot.toObject(StudentModel::class.java)
                Result.success(student)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllStudents(): Result<List<StudentModel>> {
        return try {
            val querySnapshot = studentsCollection
                .get()
                .await()
            
            val students = querySnapshot.documents.mapNotNull { document ->
                document.toObject(StudentModel::class.java)
            }
            Result.success(students)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStudentByEmail(email: String): Result<List<StudentModel>> {
        return try {
            val querySnapshot = studentsCollection
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .await()

            val student = querySnapshot.documents.firstOrNull()
                ?.toObject(StudentModel::class.java)
            val list = student?.let { listOf(it) } ?: listOf()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStudentsByClass(classId: String): Result<List<StudentModel>> {
        return try {
            val querySnapshot = studentsCollection
                .whereEqualTo("classId", classId)
                .get()
                .await()
            
            val students = querySnapshot.documents.mapNotNull { document ->
                document.toObject(StudentModel::class.java)
            }
            Result.success(students)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}