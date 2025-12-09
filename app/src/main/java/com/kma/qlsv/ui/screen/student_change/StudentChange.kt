package com.kma.qlsv.ui.screen.student_change

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kma.qlsv.R
import com.kma.qlsv.data.StudentModel
import com.kma.qlsv.ui.theme.AppTheme
import com.kma.qlsv.ui.theme.Primary
import com.kma.qlsv.ui.theme.Background
import com.kma.qlsv.ui.theme.Surface
import com.kma.qlsv.ui.theme.OnSurface
import com.kma.qlsv.ui.theme.OnSurfaceVariant
import com.kma.qlsv.utils.formatDate

@Composable
fun StudentChange(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: StudentChangeViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_qlsv))


    // State for input fields
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var fullName by remember { mutableStateOf(uiState.student?.name ?: "") }
    var birthDate by remember { mutableStateOf( TextFieldValue("${uiState.student?.birthday}")) }
    var hometown by remember { mutableStateOf(uiState.student?.address ?: "") }
    var email by remember { mutableStateOf(uiState.student?.email ?: "") }

    LaunchedEffect(Unit) {
        val student = navController.previousBackStackEntry
            ?.savedStateHandle
            ?.remove<StudentModel>("student")
        viewModel.initData(student)
    }

    LaunchedEffect(uiState.student) {
        fullName = uiState.student?.name ?: ""
        val initialDate = uiState.student?.birthday?.formatDate() ?: ""
        birthDate = TextFieldValue(
            text = initialDate,
            selection = TextRange(initialDate.length) // Đặt con trỏ ở cuối
        )
        hometown = uiState.student?.address ?: ""
        email = uiState.student?.email ?: ""
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
                        text = if(viewModel.getType() == TYPE.CREATE) "Thêm mới sinh viên" else "Cập nhật thông tin",
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

            // Form Content - matching React Native design
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            placeholder = {
                                Text(
                                    text = "Họ tên *",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 15.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.outline,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedContainerColor = Surface,
                                unfocusedContainerColor = Surface
                            )
                        )

                        OutlinedTextField(
                            value = birthDate,
                            onValueChange = { newValue ->
                                val cleanInput = newValue.text.filter { it.isDigit() || it == '/' }
                                    .replace("[^0-9/]".toRegex(), "")
                                if (cleanInput.length <= 10) {
                                    val formatted = cleanInput.formatDate()
                                    val newCursorPosition = formatted.length
                                    birthDate = TextFieldValue(
                                        text = formatted,
                                        selection = TextRange(newCursorPosition)
                                    )
                                }
                            },
                            placeholder = {
                                Text(
                                    text = "Ngày sinh (DD/MM/YYYY) *",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 15.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.outline,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedContainerColor = Surface,
                                unfocusedContainerColor = Surface
                            )
                        )

                        OutlinedTextField(
                            value = hometown,
                            onValueChange = { hometown = it },
                            placeholder = {
                                Text(
                                    text = "Địa chỉ",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 15.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.outline,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedContainerColor = Surface,
                                unfocusedContainerColor = Surface
                            )
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = {
                                Text(
                                    text = "Email *",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 15.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.outline,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedContainerColor = Surface,
                                unfocusedContainerColor = Surface
                            )
                        )

                        // Phone field
                        OutlinedTextField(
                            value = uiState.student?.phone ?: "",
                            onValueChange = { },
                            placeholder = {
                                Text(
                                    text = "Số điện thoại",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.outline,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedContainerColor = Surface,
                                unfocusedContainerColor = Surface
                            ),
                        )
                        
                        // Save button - matching React Native design
                        Button(
                            onClick = {
                                viewModel.updateName(fullName)
                                viewModel.updateBirthDay(birthDate.text)
                                viewModel.updateAddress(hometown)
                                viewModel.updateEmail(email)
                                viewModel.handleStudent()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Primary
                            )
                        ) {
                            Text(
                                text = if (viewModel.getType() == TYPE.CREATE) "Lưu" else "Cập nhật",
                                style = MaterialTheme.typography.labelLarge,
                                color = Surface,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
        when (uiState.status) {
            STATUS.SUCCESS -> {
                if (viewModel.getType() == TYPE.CREATE){
                    Toast.makeText(context, "Thêm sinh viên thành công", Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(context, "Cập nhật thông tin sinh viên thành công", Toast.LENGTH_LONG).show()
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
                Log.d("hao", "lottie: ")
            }
            STATUS.IDLE -> {

            }
        }
    }
}
