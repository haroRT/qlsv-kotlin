package com.kma.qlsv.ui.screen.score_change

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kma.qlsv.R
import com.kma.qlsv.data.ScoreModel
import com.kma.qlsv.data.ScoreWithSubject
import com.kma.qlsv.data.StudentModel
import com.kma.qlsv.data.SubjectModel
import com.kma.qlsv.ui.theme.Primary
import com.kma.qlsv.ui.theme.Background
import com.kma.qlsv.ui.theme.Surface
import com.kma.qlsv.ui.theme.OnSurface
import com.kma.qlsv.ui.theme.OnSurfaceVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreChange(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ScoreChangeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_qlsv))

    // State for input fields
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var fullName by remember { mutableStateOf(uiState.student?.name ?: "") }
    var subjectName by remember { mutableStateOf(uiState.scoreWithSubject?.score?.name ?: "") }

    // List of options for the dropdown
    var options by remember { mutableStateOf(listOf(SubjectModel())) }
    // State to track if the dropdown is expanded
    var expanded by remember { mutableStateOf(false) }
    // State to store the selected option
    var selectedOption by remember { mutableStateOf(SubjectModel()) }
    var scoreValue1 by remember { mutableStateOf("") }
    var scoreValue2 by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val scoreWithSubject = navController.previousBackStackEntry
            ?.savedStateHandle
            ?.remove<ScoreWithSubject>("scoreWithSubject")
        val student = navController.previousBackStackEntry
            ?.savedStateHandle
            ?.remove<StudentModel>("student")
        viewModel.initData(student, scoreWithSubject)
        viewModel.getListSubject()
    }

    LaunchedEffect(uiState.listSubject) {
        options = uiState.listSubject
    }

    LaunchedEffect(uiState.scoreWithSubject, uiState.student) {
        fullName = uiState.student?.name ?: ""
        selectedOption = uiState.scoreWithSubject?.subject ?: SubjectModel()
        if (uiState.scoreWithSubject?.score != null){
            scoreValue1 = uiState.scoreWithSubject?.score?.score1.toString()
            scoreValue2 = uiState.scoreWithSubject?.score?.score2.toString()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
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
                        text = if(viewModel.getType() == TYPE.CREATE) "Add Score" else "Edit Score",
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
                                    text = "Student Name *",
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
                            ),
                            enabled = false
                        )

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedOption.name,
                                onValueChange = { },
                                readOnly = true,
                                placeholder = {
                                    Text(
                                        text = "Select Subject *",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 15.dp)
                                    .menuAnchor(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    focusedContainerColor = Surface,
                                    unfocusedContainerColor = Surface
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                options.forEach { selectionOption ->
                                    DropdownMenuItem(
                                        text = { Text(selectionOption.name) },
                                        onClick = {
                                            selectedOption = selectionOption
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = scoreValue1,
                            onValueChange = { newValue ->
                                val regex = Regex("^[0-9]*\\.?[0-9]*$")
                                if (newValue.matches(regex)) {
                                    scoreValue1 = newValue
                                }
                            },
                            placeholder = {
                                Text(
                                    text = "Component Score",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
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
                            value = scoreValue2,
                            onValueChange = { newValue ->
                                val regex = Regex("^[0-9]*\\.?[0-9]*$")
                                if (newValue.matches(regex)) {
                                    scoreValue2 = newValue
                                }
                            },
                            placeholder = {
                                Text(
                                    text = "Final Score",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.outline,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedContainerColor = Surface,
                                unfocusedContainerColor = Surface
                            )
                        )

                        // Save button - matching React Native design
                        Button(
                            onClick = {
                                viewModel.updateData(fullName, scoreValue1.toFloat(), scoreValue2.toFloat(), selectedOption)
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
                                text = if (viewModel.getType() == TYPE.CREATE) "Save" else "Update",
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
                    Toast.makeText(context, "Nhập điểm thành công", Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(context, "Cập nhật điểm thành công", Toast.LENGTH_LONG).show()
                }
            }
            STATUS.FAILURE -> {
                Toast.makeText(context, "Lỗi: mạng mất kết nối", Toast.LENGTH_LONG).show()
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
