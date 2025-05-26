package com.bisbiai.app.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.bisbiai.app.R
import com.bisbiai.app.ui.components.UserStatsCard
import kotlinx.coroutines.flow.collectLatest
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToAuthScreen: () -> Unit,
) {
    val userData by viewModel.userData.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = Unit) {
        viewModel.errorMessage.collectLatest { message ->
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message = message)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Agar bisa discroll jika ada konten lebih nanti
        ) {
            // Bagian Atas dengan Latar Belakang Primary
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MiuixTheme.colorScheme.primary) // Latar belakang primary
                    .padding(horizontal = 24.dp, vertical = 16.dp) // Padding yang cukup
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(top = innerPadding.calculateTopPadding())
                ) {
                    // Foto Profil Bulat
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(userData?.profilePictureUrl)
                            .crossfade(true)
                            .error(R.drawable.ic_launcher_foreground) // Ganti dengan drawable placeholder Anda
                            .placeholder(R.drawable.ic_launcher_background) // Ganti dengan drawable placeholder Anda
                            .build(),
                        contentDescription = "Foto Profil ${userData ?: ""}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MiuixTheme.colorScheme.surfaceVariant), // Warna background jika gambar transparan atau error
                        loading = {
                            CircularProgressIndicator(
                                modifier = Modifier.size(40.dp),
                                strokeWidth = 2.dp
                            )
                        },
                        error = {
                            // Tampilan jika ada error atau photoUrl null
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MiuixTheme.colorScheme.primaryContainer) // Atau warna placeholder lain
                            ) {
                                // Anda bisa menggunakan ikon placeholder jika mau
                                // Icon(imageVector = Icons.Default.Person, contentDescription = "Placeholder Profile", tint = MiuixTheme.colorScheme.onPrimaryContainer)
                                Text(
                                    "?",
                                    style = MiuixTheme.textStyles.title1.copy(color = MiuixTheme.colorScheme.onPrimaryContainer)
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Kolom Nama dan Email
                    Column {
                        Text(
                            text = userData?.userName ?: "Loading...",
                            style = MiuixTheme.textStyles.title3, // Gaya teks yang lebih besar untuk nama
                            fontWeight = FontWeight.Bold,
                            color = MiuixTheme.colorScheme.onPrimary // Warna teks di atas primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = userData?.email ?: "Loading...",
                            style = MiuixTheme.textStyles.headline2,
                            color = MiuixTheme.colorScheme.onPrimary.copy(alpha = 0.8f) // Sedikit transparan untuk email
                        )
                    }
                }
            }

            // Bagian Bawah (Kosong untuk saat ini)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Agar mengisi sisa ruang jika ada
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp) // Contoh penempatan di tengah
            ) {
                UserStatsCard(
                    currentLevel = 5,
                    currentXp = 140,
                    xpToNextLevel = 150,
                    dayStreak = 32
                )
            }
        }
    }
}