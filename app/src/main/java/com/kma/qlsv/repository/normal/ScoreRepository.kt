package com.kma.qlsv.repository.normal

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.kma.qlsv.data.ScoreModel
import com.kma.qlsv.data.ScoreWithSubject
import com.kma.qlsv.data.StudentModel
import com.kma.qlsv.data.SubjectModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScoreRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val scoresCollection = firestore.collection("scores")

    suspend fun addScore(score: ScoreModel): Result<String> {
        return try {
            val documentRef = scoresCollection.document(score.id)
            documentRef.set(score).await()
            Result.success(score.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateScore(score: ScoreModel): Result<Unit> {
        return try {
            scoresCollection.document(score.id)
                .set(score)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteScore(score: ScoreModel): Result<Unit> {
        return try {
            scoresCollection.document(score.id)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getScoreByStudent(studentModel: StudentModel): Result<List<ScoreWithSubject>> {
        return try {
            // Bước 1: Lấy tất cả điểm số của sinh viên
            val scoreSnapshot = scoresCollection
                .whereEqualTo("studentId", studentModel.id)
                .get()
                .await()

            val scores = scoreSnapshot.toObjects(ScoreModel::class.java)
            val scoreWithSubjects = mutableListOf<ScoreWithSubject>()
            val subjectTasks = mutableListOf<Task<DocumentSnapshot>>()

            // Bước 2: Tạo danh sách tác vụ truy vấn môn học
            for (score in scores) {
                val task = firestore.collection("subjects").document(score.subjectId).get()
                subjectTasks.add(task)
            }

            // Bước 3: Chờ tất cả truy vấn môn học hoàn thành
            val subjectResults = Tasks.await(Tasks.whenAllSuccess<DocumentSnapshot>(subjectTasks))

            // Bước 4: Kết hợp dữ liệu
            scores.forEachIndexed { index, score ->
                val subjectSnapshot = subjectResults[index]
                val subject = subjectSnapshot.toObject(SubjectModel::class.java) ?: SubjectModel()
                scoreWithSubjects.add(ScoreWithSubject(score, subject))
            }

            Result.success(scoreWithSubjects)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}