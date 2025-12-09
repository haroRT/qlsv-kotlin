package com.kma.qlsv.ui.screen.login

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.recaptcha.Recaptcha
import com.google.android.recaptcha.RecaptchaAction
import com.google.android.recaptcha.RecaptchaClient
import com.google.android.recaptcha.RecaptchaException
import com.google.firebase.auth.FirebaseUser
import com.kma.qlsv.repository.auth.AuthRepository
import com.kma.qlsv.utils.SharedPref
import com.kma.qlsv.utils.SharedPrefNoneEncrypt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

data class UiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val user: FirebaseUser? = null
)

@HiltViewModel
class LoginVM @Inject constructor(
    private val authRepository: AuthRepository,
    private val pref: SharedPref,
    private val application: Application,
    private val prefNoneEncrypt: SharedPrefNoneEncrypt
) : ViewModel() {

    init {
        initializeRecaptchaClient()
    }
    private lateinit var recaptchaClient: RecaptchaClient
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun checkRecaptcha(email: String, password: String){
        viewModelScope.launch(Dispatchers.IO) {
            recaptchaClient
                .execute(RecaptchaAction.LOGIN)
                .onSuccess { token ->
                    Log.d("hao", "checkRecaptcha: ${token}")
                    sendTokenToServer(token){
                        login(email, password)
                    }
                }
                .onFailure { exception ->
                    Log.d("hao", "checkRecaptcha: ${exception.message}")

                }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            delay(3000)
            val result = authRepository.login(email, password)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        user = result.getOrNull(),
                        isSuccess = true,
                        isLoading = false
                    )
                }
                pref.isAdmin = authRepository.isAdmin()
                prefNoneEncrypt.isAdmin = authRepository.isAdmin()
            }
            else{
                _uiState.update {
                    it.copy(
                        isSuccess = false,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun initializeRecaptchaClient() {
        viewModelScope.launch {
            try {
                recaptchaClient = Recaptcha.fetchClient(application, "6LcOGPQrAAAAAApNWKUScjgvf-s7ffKKZEPNRc9-")
            } catch(e: RecaptchaException) {
                // Handle errors ...
                // See "Handle errors" section
            }
        }
    }

    private fun sendTokenToServer(token: String, onSuccess: () -> Unit) {
        val url = "https://recaptcha-express.onrender.com/verify-recaptcha"

        val jsonBody = """{"token":"$token"}"""

        val client = OkHttpClient()
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = RequestBody.create(mediaType, jsonBody)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("reCAPTCHA", "Không gửi được token lên server", e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    Log.d("reCAPTCHA", "Server phản hồi: ${response.body?.string()}")
                    if (response.isSuccessful) onSuccess()
                }
            }
        })
    }
}