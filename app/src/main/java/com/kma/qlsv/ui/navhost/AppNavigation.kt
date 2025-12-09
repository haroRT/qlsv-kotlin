
package com.kma.qlsv.ui.navhost

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kma.qlsv.ui.screen.home.HomeScreen
import com.kma.qlsv.ui.screen.login.LoginScreen
import com.kma.qlsv.ui.screen.score_change.ScoreChange
import com.kma.qlsv.ui.screen.search.SearchScreen
import com.kma.qlsv.ui.screen.search_subject.SearchSubjectScreen
import com.kma.qlsv.ui.screen.signup.SignupScreen
import com.kma.qlsv.ui.screen.student_change.StudentChange
import com.kma.qlsv.ui.screen.student_detail.StudentDetail
import com.kma.qlsv.ui.screen.subject_change.SubjectChangeScreen

@Composable
fun AppNavigation(
    authViewModel: AuthCheckViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val startDestination = if (authViewModel.isUserLoggedIn()) "home" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(
                navController = navController
            )
        }
        composable("register") {
            SignupScreen(
                navController = navController
            )
        }
        composable("home") {
            HomeScreen(
                navController = navController
            )
        }
        composable("searchStudent") {
            SearchScreen(
                navController = navController
            )
        }
        composable(
            route = "changeStudent"
        ) {
            StudentChange(
                navController = navController,
            )
        }
        composable(route = "studentDetail"){
            StudentDetail(
                navController = navController,
            )
        }

        composable(route = "searchSubject"){
            SearchSubjectScreen(
                navController = navController,
            )
        }
        composable(route = "changeSubject"){
            SubjectChangeScreen(
                navController = navController,
            )
        }
        composable(route = "changeScore") {
            ScoreChange(
                navController = navController,
            )
        }
    }
}