package com.kma.qlsv.ui.screen.student_detail

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kma.qlsv.R
import com.kma.qlsv.data.ScoreModel
import com.kma.qlsv.data.ScoreWithSubject
import com.kma.qlsv.data.StudentModel
import com.kma.qlsv.ui.theme.Primary
import com.kma.qlsv.ui.theme.Background
import com.kma.qlsv.ui.theme.Surface
import com.kma.qlsv.ui.theme.OnSurface
import com.kma.qlsv.ui.theme.OnSurfaceVariant

@Composable
fun StudentDetail(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: StudentDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var selectedScore by remember { mutableStateOf<ScoreModel?>(null) }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_qlsv))

    var searchText by remember { mutableStateOf("") }

    // State for input fields
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        val student = navController.previousBackStackEntry
            ?.savedStateHandle
            ?.get<StudentModel>("student")
        viewModel.initData(student)
    }

    LaunchedEffect(uiState.student) {
        viewModel.getScoreByStudent()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Header - matching React Native design
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(top = 16.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Surface
                        )
                    }
                    
                    Text(
                        text = "Thông tin sinh viên",
                        style = MaterialTheme.typography.titleMedium,
                        color = Surface,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        textAlign = TextAlign.Center
                    )
                    
                    // Spacer for balance
                    Spacer(modifier = Modifier.width(40.dp))
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Student Info Card - matching React Native design
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        StudentInfoRow("ID:", uiState.student?.id ?: "")
                        StudentInfoRow("Họ tên:", uiState.student?.name ?: "")
                        StudentInfoRow("Ngày sinh:", uiState.student?.birthday ?: "")
                        StudentInfoRow("Email:", uiState.student?.email ?: "")
                        StudentInfoRow("Số điện thoại:", uiState.student?.phone ?: "N/A")
                        StudentInfoRow("Địa chỉ:", uiState.student?.address ?: "N/A")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                
                // Scores Section
                Text(
                    text = "Bảng điểm",
                    style = MaterialTheme.typography.titleSmall,
                    color = OnSurface,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 15.dp)
                )
                
                // Search Bar - matching React Native design
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 15.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = "Search",
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            if (searchText.isEmpty()) {
                                Text(
                                    text = "Tìm kiếm môn học...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = OnSurfaceVariant
                                )
                            }
                            androidx.compose.foundation.text.BasicTextField(
                                value = searchText,
                                onValueChange = { searchText = it },
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    color = OnSurface
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Scores List
                if (uiState.listScore.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_book),
                                contentDescription = "No scores",
                                tint = OnSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No scores found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.listScore.forEach { scoreWithSubject ->
                            ItemScore(
                                scoreWithSubject = scoreWithSubject,
                                studentModel = uiState.student,
                                navController = navController,
                                onDelete = { score ->
                                    showDialog = true
                                    selectedScore = score
                                },
                                viewModel = viewModel
                            )
                        }
                    }
                }

            }
        }
        
        // FAB for Admin - matching React Native design
        if (viewModel.isAdmin()) {
            FloatingActionButton(
                onClick = {
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("student", uiState.student)
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("score", null)
                    navController.navigate("changeScore")
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp),
                containerColor = Primary,
                contentColor = Surface
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Score"
                )
            }
        }
        
        if (showDialog) {
            AlertDialog(
                containerColor = Color.White,
                onDismissRequest = { showDialog = false }, // Khi bấm ra ngoài hoặc back
                title = {
                    Text(
                        color = Color.Black,
                        text = "Xác nhận xoá"
                    )
                },
                text = {
                    Text(
                        color = Color.Black,
                        text = "Bạn có chắc chắn muốn tiếp tục không?"
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            selectedScore?.let {
                                viewModel.deleteScore(context, it)
                            }
                        }
                    ) {
                        Text("Đồng ý")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                        }
                    ) {
                        Text("Hủy")
                    }
                }
            )
        }

        when (uiState.status) {
            STATUS.SUCCESS -> {
                if (viewModel.getType() == TYPE.CREATE) {
                    Toast.makeText(context, "Thêm sinh viên thành công", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(
                        context,
                        "Cập nhật thông tin sinh viên thành công",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            STATUS.FAILURE -> {
                Toast.makeText(context, "Lỗi: Email đã tồn tại", Toast.LENGTH_LONG).show()
            }

            STATUS.LOADING -> {
                LottieAnimation(
                    composition = composition,
                    modifier = Modifier.fillMaxSize(),
                    iterations = LottieConstants.IterateForever // Lặp vô hạn
                )
            }

            STATUS.IDLE -> {

            }
        }
    }
}

@Composable
fun ItemScore(
    modifier: Modifier = Modifier,
    scoreWithSubject: ScoreWithSubject,
    studentModel: StudentModel?,
    navController: NavController,
    viewModel: StudentDetailViewModel,
    onDelete: (ScoreModel) -> Unit
) {
    val finalScore = scoreWithSubject.score?.let { score ->
        (score.score1 * 0.3f) + (score.score2 * 0.7f)
    } ?: 0f
    
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = scoreWithSubject.subject?.name ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = OnSurface,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Điểm 1: ${scoreWithSubject.score?.score1?.let { "%.1f".format(it) } ?: "0.0"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                    Text(
                        text = "Điểm 2: ${scoreWithSubject.score?.score2?.let { "%.1f".format(it) } ?: "0.0"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                    Text(
                        text = "Tổng: %.1f".format(finalScore),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            if (viewModel.isAdmin()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("student", studentModel)
                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("scoreWithSubject", scoreWithSubject)
                            navController.navigate("changeScore")
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_edit),
                            contentDescription = "Edit",
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = {
                            scoreWithSubject.score?.let { onDelete.invoke(it) }
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_delete),
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StudentInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurface
        )
    }
}
