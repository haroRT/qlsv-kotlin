package com.kma.qlsv.ui.screen.search

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kma.qlsv.R
import com.kma.qlsv.data.StudentModel
import com.kma.qlsv.ui.screen.student_change.STATUS
import com.kma.qlsv.ui.screen.student_change.TYPE
import com.kma.qlsv.ui.theme.AppTheme
import com.kma.qlsv.ui.theme.Primary
import com.kma.qlsv.ui.theme.Background
import com.kma.qlsv.ui.theme.Surface
import com.kma.qlsv.ui.theme.OnSurface
import com.kma.qlsv.ui.theme.OnSurfaceVariant

@Composable
fun SearchScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_qlsv))
    var showDialog by remember { mutableStateOf(false) }
    var selectedStudent by remember { mutableStateOf<StudentModel?>(null) }
    var searchText by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAllStudent()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column {
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
                            tint = MaterialTheme.colorScheme.surface
                        )
                    }
                    
                    Text(
                        text = "Quản lý sinh viên",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.surface,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    )
                }
            }

            // Search Bar - matching React Native design  
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        if (searchText.isEmpty()) {
                            Text(
                                text = "Tìm kiếm theo tên hoặc mã sinh viên...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        androidx.compose.foundation.text.BasicTextField(
                            value = searchText,
                            onValueChange = {
                                searchText = it
                                viewModel.searchByIdAndName(it)
                            },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Main content
            LazyColumn(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (uiState.listSearch.isEmpty() && searchText == "") {
                    items(uiState.listStudent.size) {
                        ItemStudent(
                            modifier = Modifier.padding(vertical = 4.dp),
                            student = uiState.listStudent[it],
                            navController,
                            onDelete = { student ->
                                showDialog = true
                                selectedStudent = student
                            },
                            viewModel = viewModel
                        )
                    }
                } else {
                    if (uiState.listSearch.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AsyncImage(
                                    model = R.drawable.img_not_found_data,
                                    contentDescription = "not found data",
                                    modifier = Modifier.size(160.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Không tìm thấy dữ liệu",
                                    fontSize = 20.sp
                                )
                            }
                        }
                    } else {
                        items(uiState.listSearch.size) {
                            ItemStudent(
                                modifier = Modifier.padding(vertical = 4.dp),
                                student = uiState.listSearch[it],
                                navController,
                                onDelete = { student ->
                                    showDialog = true
                                    selectedStudent = student
                                },
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }

            // Button at the bottom
            if (viewModel.isAdmin()){
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    onClick = {
                        navController.navigate("changeStudent")
                    },
                    shape = RoundedCornerShape(50.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Primary
                    )
                ) {
                    Text(
                        text = "Thêm mới sinh viên",
                        style = MaterialTheme.typography.labelLarge,
                        color = Surface,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        when (uiState.status) {
            STATUS.SUCCESS -> {

            }

            STATUS.FAILURE -> {
                Toast.makeText(context, "Đã có lỗi xảy ra", Toast.LENGTH_LONG).show()
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
                            selectedStudent?.let {
                                viewModel.deleteStudent(context, it)
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
    }
}

@Composable
fun ItemStudent(
    modifier: Modifier = Modifier,
    student: StudentModel,
    navController: NavController,
    onDelete: (StudentModel) -> Unit,
    viewModel: SearchViewModel
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable {
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("student", student)
                navController.navigate("studentDetail")
            },
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
            // Student Avatar - circular background like React Native
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Background, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_user),
                    contentDescription = "Student",
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = student.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = OnSurface,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ID: ${student.id}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            }
            
            if (viewModel.isAdmin()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("student", student)
                            navController.navigate("changeStudent")
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit),
                            contentDescription = "Edit",
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = {
                            onDelete.invoke(student)
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_delete),
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

@Preview(showBackground = true)
@Composable
fun ItemStudentPreview() {
    AppTheme {
        ItemStudent(
            student = StudentModel(
                id = "123456",
                name = "Nguyễn Văn A"
            ),
            navController = rememberNavController(),
            onDelete = { student ->

            },
            viewModel = viewModel()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    AppTheme {
        SearchScreen(
            navController = rememberNavController()
        )
    }
}