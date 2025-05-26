package com.bisbiai.app.ui.screen.auth

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bisbiai.app.BuildConfig
import com.bisbiai.app.R
import com.bisbiai.app.ui.components.FullScreenLoading
import com.bisbiai.app.ui.screen.auth.components.OnboardingCard
import com.bisbiai.app.ui.theme.spacing
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import java.security.MessageDigest
import java.util.UUID

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToHomeScreen: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.errorMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(key1 = state.isSuccessful) {
        if (state.isSuccessful) {
            onNavigateToHomeScreen()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(modifier = modifier.fillMaxSize()) {
//            OnboardingImage(
//                painterId = R.drawable.bisbi_promo
//            )
            Box(modifier
                .fillMaxSize()
                .background(MiuixTheme.colorScheme.primary))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OnboardingCard(
                    title = "Welcome to BISBI",
                    description = "BISBI bertujuan untuk memberdayakan pemuda Indonesia, khususnya rentang usia 17-25 tahun, dengan keterampilan komunikasi bahasa Inggris yang praktis, kontekstual, dan hiper-personalisasi.",
                )
                Spacer(
                    modifier = Modifier
                        .height(MaterialTheme.spacing.large)
                        .fillMaxWidth()
                        .background(MiuixTheme.colorScheme.surface)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MiuixTheme.colorScheme.surface)
                        .padding(horizontal = MaterialTheme.spacing.large)
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MiuixTheme.colorScheme.surface,
                            contentColor = MiuixTheme.colorScheme.onSurface,
                        ),
                        border = BorderStroke(1.dp, MiuixTheme.colorScheme.outline),
                        shape = RoundedCornerShape(16.dp),
                        onClick = {
                            scope.launch {
                                getGoogleIdToken(context)?.let {
                                    viewModel.onContinueWithGoogle(it)
                                }
                            }
                        },
                    ) {
                        Row(
                            modifier = Modifier.padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(id = R.drawable.logo_google),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(24.dp))
                            Text(text = "Continue with Google")
                        }
                    }
                }
                Spacer(
                    modifier = Modifier
                        .height(MaterialTheme.spacing.large)
                        .fillMaxWidth()
                        .background(MiuixTheme.colorScheme.surface)
                )
            }
        }

        if (state.isLoading) {
            FullScreenLoading()
        }
    }
}

suspend fun getGoogleIdToken(context: Context): String? {
    try {
        val credentialManager = CredentialManager.create(context)

        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.WEB_CLIENT_ID)
            .setNonce(hashedNonce)
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(
            context = context,
            request = request
        )

        val credential = result.credential

        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

        return googleIdTokenCredential.idToken
    } catch (e: CreateCredentialCancellationException) {
        //do nothing, the user chose not to save the credential
        Timber.tag("Credential").v("User cancelled the save: $e")
        return null
    } catch (e: CreateCredentialException) {
        Timber.tag("Credential").v("Credential save error: $e")
        return null
    } catch (e: GetCredentialCancellationException) {
        Timber.tag("Credential").v("User cancelled the get credential: $e")
        return null
    } catch (e: Exception) {
        Timber.tag("Credential").e("Error getting credential: $e")
        return null
    }
}