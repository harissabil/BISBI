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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.bisbiai.app.R
import com.bisbiai.app.ui.UserProgressViewModel
import com.bisbiai.app.ui.screen.profile.component.AchievementsSection
import com.bisbiai.app.ui.screen.profile.component.CompactUserStats
import kotlinx.coroutines.flow.collectLatest
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
    userProgressViewModel: UserProgressViewModel,
    onNavigateToAuthScreen: () -> Unit,
) {
    val progressData by userProgressViewModel.userStats.collectAsStateWithLifecycle() // Data dari UserProgressViewModel
    val allAchievements by userProgressViewModel.achievements.collectAsStateWithLifecycle()

    val userData by viewModel.userData.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // State untuk filter achievement, dikelola di sini karena ProfileScreen yang "memiliki" section ini
    var achievementFilter by remember { mutableStateOf(AchievementFilterType.SHOW_ALL) }

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
            modifier = modifier.fillMaxSize()
        ) {
            // Bagian Atas Terintegrasi: Profil dan Statistik
            Column( // Menggunakan Column agar bisa menumpuk info profil dan statistik
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MiuixTheme.colorScheme.primary)
                    .padding(top = innerPadding.calculateTopPadding()) // Padding dari Scaffold
                    .padding(horizontal = 24.dp, vertical = 20.dp) // Padding internal
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
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

                // Statistik Pengguna (Compact Version)
                CompactUserStats(
                    currentLevel = progressData?.level ?: 1,
                    currentXp = progressData?.currentXp ?: 0,
                    xpToNextLevel = progressData?.xpToNextLevel ?: 100,
                    dayStreak = progressData?.dayStreak ?: 0,
                    onPrimaryColor = MiuixTheme.colorScheme.onPrimary,
                    onPrimaryVariantColor = MiuixTheme.colorScheme.onPrimary.copy(alpha = 0.75f),
                    progressColor = Color.White, // Progress bar putih di atas biru
                    progressTrackColor = Color.White.copy(alpha = 0.3f) // Track lebih redup
                )
            }

            // Bagian Bawah (Kosong untuk saat ini)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
//                    .weight(1f) // Agar mengisi sisa ruang jika ada
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp) // Contoh penempatan di tengah
            ) {
                AchievementsSection(
                    achievements = allAchievements,
                    currentFilter = achievementFilter,
                    onFilterChanged = { newFilter ->
                        achievementFilter = newFilter
                    }
                )
            }
        }
    }
}