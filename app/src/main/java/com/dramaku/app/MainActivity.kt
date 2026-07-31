package com.dramaku.app

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.Intent
import android.graphics.Color as AndroidColor
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.input.pointer.pointerInput
import coil.compose.AsyncImage
import coil.Coil
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.dramaku.app.data.NativeRemoteConfig
import com.dramaku.app.data.RemoteConfigRepository
import com.dramaku.app.home.Greetings
import com.dramaku.app.home.HomeCategory
import com.dramaku.app.storage.ProgressKeys
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.PlayerView
import androidx.media3.ui.AspectRatioFrameLayout
import okhttp3.Dispatcher
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.Calendar
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min

// ─────────────────────────────────────────────────────────────────
// DESIGN SYSTEM — Aura Premium: Sleek, Modern, High-End
// ─────────────────────────────────────────────────────────────────

private object DS {
    val Bg = Color(0xFF07070E)
    val Bg2 = Color(0xFF10101E)
    val Bg3 = Color(0xFF18182E)
    val Bg4 = Color(0xFF22223D)
    val Line = Color(0x18FFFFFF)

    val Primary = Color(0xFF7C3AED)   // Violet
    val Secondary = Color(0xFF3B82F6) // Blue
    val PrimaryDim = Color(0xFF7C3AED).copy(alpha = 0.12f)
    val Gold = Color(0xFFFACC15)

    val White = Color(0xFFFFFFFF)
    val Text = Color(0xFFF1F5F9)
    val Muted = Color(0xFF94A3B8)
    val Hint = Color(0xFF64748B)

    val Red = Color(0xFFEF4444)
    val RedDim = Color(0xFFEF4444).copy(alpha = 0.12f)

    val MainGrad = listOf(Color(0xFF7C3AED), Color(0xFF3B82F6))
}

// ─────────────────────────────────────────────────────────────────
// APP ENTRY
// ─────────────────────────────────────────────────────────────────

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Coil.setImageLoader(
            ImageLoader.Builder(this)
                .memoryCache { MemoryCache.Builder(this).maxSizePercent(0.25).build() }
                .diskCache { DiskCache.Builder().directory(cacheDir.resolve("coil_img")).maxSizePercent(0.05).build() }
                .crossfade(true)
                .build()
        )
        window.statusBarColor = AndroidColor.BLACK
        window.navigationBarColor = AndroidColor.BLACK
        setContent { DramakuApp() }
    }
}

@Composable
private fun DramakuApp() {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = DS.Primary,
            secondary = DS.Secondary,
            background = DS.Bg,
            surface = DS.Bg2,
            onPrimary = Color.White,
            onBackground = DS.White,
            onSurface = DS.White,
            primaryContainer = DS.Bg3,
            onPrimaryContainer = DS.White
        )
    ) {
        App()
    }
}

// ─────────────────────────────────────────────────────────────────
// DATA MODELS
// ─────────────────────────────────────────────────────────────────

private enum class Tab(val label: String, val icon: ImageVector, val showNav: Boolean = true) {
    Clips("Cuplikan", Icons.Rounded.PlayCircle, true),
    Home("Beranda", Icons.Rounded.Home),
    Rewards("Hadiah", Icons.Rounded.CardGiftcard, false),
    Library("Koleksi", Icons.Rounded.Bookmark),
    Profile("Profil", Icons.Rounded.Person),
    Search("Cari", Icons.Rounded.Search)
}

private data class PlatformInfo(val id: String, val label: String, val base: String, val logoUrl: String = "", val logoRes: Int = 0)
private data class Drama(
    val id: String, val title: String, val description: String = "", val poster: String = "",
    val episodes: Int = 0, val views: String = "", val tags: List<String> = emptyList(),
    val platform: String = "melolo", val subjectType: Int = 1
)
private data class EpisodeInfo(val number: Int, val streaming: String = "", val label: String = "", val locked: Boolean = false)
private data class Detail(val drama: Drama, val episodes: List<EpisodeInfo> = emptyList())
private data class HomeBundle(val recommended: List<Drama>, val popular: List<Drama>, val newest: List<Drama>, val loadedPage: Int = 1, val hasMore: Boolean = true)
private data class CategorySignal(val label: String, val value: String, val icon: ImageVector, val color: Color)
private data class StreamResult(val url: String, val subtitle: String = "")
private data class CachedStream(val result: StreamResult, val expiresAtMs: Long)
private data class PlayerSession(val detail: Detail, val startEpisode: Int)
private data class HistoryItem(
    val id: String, val title: String, val poster: String, val platform: String,
    val episode: Int, val pos: Long = 0L, val dur: Long = 0L, val updated: Long = System.currentTimeMillis()
) {
    val pct: Int get() = if (dur > 0) min(99, max(0, ((pos * 100) / dur).toInt())) else 0
}

private sealed class Load<out T> {
    object Idle : Load<Nothing>()
    object Loading : Load<Nothing>()
    data class Ok<T>(val data: T) : Load<T>()
    data class Err(val message: String) : Load<Nothing>()
}

// ─────────────────────────────────────────────────────────────────
// PLATFORMS
// ─────────────────────────────────────────────────────────────────

private val Platforms = listOf(
    PlatformInfo("melolo", "Melolo", "https://captain.sapimu.au/melolo/api/v1", logoRes = R.drawable.logo_melolo),
    PlatformInfo("freereels", "FreeReels", "https://new-api.sonzaix.workers.dev/freereels", logoRes = R.drawable.logo_freereels),
    PlatformInfo("flickreels", "FlickReels", "https://new-api.sonzaix.workers.dev/flickreels", logoRes = R.drawable.logo_flickreels),
    PlatformInfo("dramanova", "DramaNova", "https://new-api.sonzaix.workers.dev/dramanova", logoRes = R.drawable.logo_dramanova),
    PlatformInfo("reelshort", "ReelShort", "https://new-api.sonzaix.workers.dev/reelshort", "https://v-mps.crazymaplestudios.com/images/211d3420-d721-11f0-84ad-6b5693b490dc.png"),
    PlatformInfo("netshort", "NetShort", "https://new-api.sonzaix.workers.dev/netshort", "https://netshort.com/assets/logo/logo.png"),
    PlatformInfo("dramabox", "DramaBox", "https://new-api.sonzaix.workers.dev/dramabox", "https://www.google.com/s2/favicons?sz=256&domain=dramaboxapp.com"),
    PlatformInfo("goodshort", "GoodShort", "https://new-api.sonzaix.workers.dev/goodshort", "https://acfs3.goodshort.com/dist/src/assets/images/pc/common/1b3b5f4e-logo.png"),
    PlatformInfo("moviebox", "MovieBox", "https://captain.sapimu.au/moviebox/api", "https://www.google.com/s2/favicons?sz=256&domain=moviebox.ng"),
    PlatformInfo("drakor", "Drakor", "https://new-api.sonzaix.workers.dev/drama", "https://www.google.com/s2/favicons?sz=256&domain=drakor.id")
)

private fun platform(id: String) = Platforms.firstOrNull { it.id == id } ?: Platforms.first()
private fun platformLabel(id: String) = platform(id).label
private fun apiBase(id: String) = platform(id).base

// ─────────────────────────────────────────────────────────────────
// MAIN APP COMPOSABLE
// ─────────────────────────────────────────────────────────────────

@Composable
private fun App() {
    val ctx = LocalContext.current
    val store = remember { LocalStore(ctx) }
    val repo = remember { DramakuRepository() }
    val remoteRepo = remember { RemoteConfigRepository() }
    val scope = rememberCoroutineScope()

    var isOnline by remember { mutableStateOf(ctx.isNetworkAvailable()) }
    DisposableEffect(ctx) {
        val obs = LifecycleEventObserver { _, _ -> isOnline = ctx.isNetworkAvailable() }
        val lc = (ctx as? ComponentActivity)?.lifecycle
        lc?.addObserver(obs)
        onDispose { lc?.removeObserver(obs) }
    }

    var tab by remember { mutableStateOf(Tab.Home) }
    var selPlatform by remember { mutableStateOf(store.platform()) }
    var refreshKey by remember { mutableIntStateOf(0) }
    var homeScrollToTop by remember { mutableIntStateOf(0) }
    var homeState by remember { mutableStateOf<Load<HomeBundle>>(Load.Idle) }
    var homeLoadingMore by remember { mutableStateOf(false) }
    var homeAppendError by remember { mutableStateOf<String?>(null) }
    var selectedDrama by remember { mutableStateOf<Drama?>(null) }
    var detailState by remember { mutableStateOf<Load<Detail>>(Load.Idle) }
    var remoteConfig by remember { mutableStateOf<NativeRemoteConfig?>(null) }
    var remoteError by remember { mutableStateOf<String?>(null) }
    var dataTick by remember { mutableIntStateOf(0) }
    var resolvingEpisode by remember { mutableIntStateOf(0) }
    var playerSession by remember { mutableStateOf<PlayerSession?>(null) }
    var clipFeedItems by remember { mutableStateOf<List<Drama>>(emptyList()) }
    var pendingResume by remember { mutableStateOf<HistoryItem?>(null) }
    var category by remember { mutableStateOf<HomeCategory?>(null) }
    var showSettings by remember { mutableStateOf(false) }

    val playerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val data = result.data
        if (result.resultCode == Activity.RESULT_OK && data != null) {
            val id = data.getStringExtra(PlayerActivity.RESULT_DRAMA_ID).orEmpty()
            val pid = data.getStringExtra(PlayerActivity.RESULT_PLATFORM).orEmpty()
            val ep = data.getIntExtra(PlayerActivity.RESULT_EPISODE, 1)
            val pos = data.getLongExtra(PlayerActivity.RESULT_POSITION, 0L)
            val dur = data.getLongExtra(PlayerActivity.RESULT_DURATION, 0L)
            if (id.isNotBlank() && pid.isNotBlank()) {
                store.updateProgress(id, pid, ep, pos, dur)
                dataTick++
            }
        }
    }

    fun openPlayer(d: Detail, ep: Int) { playerSession = PlayerSession(d, ep) }

    LaunchedEffect(refreshKey) {
        runCatching { remoteRepo.load() }
            .onSuccess { remoteConfig = it; remoteError = null }
            .onFailure { remoteError = it.message ?: "Remote config gagal" }
    }

    LaunchedEffect(selPlatform, refreshKey) {
        homeLoadingMore = false; homeAppendError = null; homeState = Load.Loading
        try { homeState = Load.Ok(repo.loadHome(selPlatform)) }
        catch (e: CancellationException) { throw e }
        catch (t: Throwable) { homeState = Load.Err(t.message ?: "Gagal memuat") }
    }

    LaunchedEffect(selectedDrama) {
        val d = selectedDrama ?: return@LaunchedEffect
        detailState = Load.Loading
        try { detailState = Load.Ok(repo.loadDetailCached(d)) }
        catch (e: CancellationException) { throw e }
        catch (t: Throwable) { detailState = Load.Err(t.message ?: "Gagal memuat detail") }
    }

    LaunchedEffect(detailState, pendingResume) {
        val p = pendingResume ?: return@LaunchedEffect
        val det = (detailState as? Load.Ok<Detail>)?.data ?: return@LaunchedEffect
        if (det.drama.id == p.id && det.drama.platform == p.platform) {
            playerSession = PlayerSession(det, p.episode.coerceAtLeast(1))
            selectedDrama = null; pendingResume = null
        }
    }

    fun loadMore() {
        val cur = (homeState as? Load.Ok<HomeBundle>)?.data ?: return
        if (homeLoadingMore || !cur.hasMore) return
        val pSnap = selPlatform; val np = cur.loadedPage + 1
        homeLoadingMore = true; homeAppendError = null
        scope.launch {
            try {
                val next = repo.loadHomePage(pSnap, np)
                if (selPlatform == pSnap) {
                    val latest = (homeState as? Load.Ok<HomeBundle>)?.data
                    if (latest != null && next.loadedPage > latest.loadedPage)
                        homeState = Load.Ok(mergeHomeBundles(latest, next))
                }
            } catch (e: CancellationException) { throw e }
            catch (t: Throwable) { if (selPlatform == pSnap) homeAppendError = t.message }
            finally { if (selPlatform == pSnap) homeLoadingMore = false }
        }
    }

    BackHandler(enabled = selectedDrama != null) { selectedDrama = null; pendingResume = null }
    BackHandler(enabled = showSettings) { showSettings = false }
    // Back dari halaman kategori kembali ke layar awal kategori (pintu masuk)
    BackHandler(enabled = category != null && !showSettings && selectedDrama == null && playerSession == null && clipFeedItems.isEmpty()) { category = null }

    Box(Modifier.fillMaxSize().background(DS.Bg)) {
        Column {
            if (!isOnline) OfflineBanner { refreshKey++ }
            Box(Modifier.weight(1f)) {
                val activeCat = category
                if (activeCat == null) {
                    CategoryHomeScreen(
                        onSelect = { picked ->
                            if (picked.comingSoon) {
                                Toast.makeText(ctx, "${picked.title} segera hadir", Toast.LENGTH_SHORT).show()
                            } else {
                                category = picked
                                tab = Tab.Home
                                val pref = store.categoryPlatform(picked.id, picked.defaultPlatform())
                                selPlatform = if (picked.containsPlatform(pref)) pref else picked.defaultPlatform()
                                store.setPlatform(selPlatform)
                                refreshKey++
                            }
                        },
                        onSettings = { showSettings = true }
                    )
                } else {
                Scaffold(
                    containerColor = DS.Bg,
                    bottomBar = {
                        BottomNavBar(tab) { target ->
                            if (target == Tab.Home && tab == Tab.Home) homeScrollToTop++ else tab = target
                        }
                    }
                ) { pad ->
                    Box(Modifier.padding(pad).fillMaxSize()) {
                        when (tab) {
                            Tab.Home -> HomeScreen(
                                platformId = selPlatform, scrollToTopSignal = homeScrollToTop, state = homeState,
                                category = activeCat, onExitCategory = { category = null },
                                history = store.history(dataTick), remoteConfig = remoteConfig,
                                remoteError = remoteError, loadingMore = homeLoadingMore,
                                loadMoreError = homeAppendError, onLoadMore = ::loadMore,
                                onPlatform = {
                                    selPlatform = it; store.setPlatform(it); store.setCategoryPlatform(activeCat.id, it); refreshKey++
                                },
                                onRefresh = { refreshKey++ }, onDrama = { selectedDrama = it },
                                onSearch = { tab = Tab.Search },
                                onRandom = {
                                    val b = (homeState as? Load.Ok<HomeBundle>)?.data
                                    val pool = (b?.popular.orEmpty() + b?.newest.orEmpty() + b?.recommended.orEmpty()).filter { it.id.isNotBlank() }
                                    if (pool.isNotEmpty()) selectedDrama = pool.random()
                                },
                                onClips = {
                                    val b = (homeState as? Load.Ok<HomeBundle>)?.data
                                    val pool = (b?.popular.orEmpty() + b?.newest.orEmpty() + b?.recommended.orEmpty())
                                        .filter { it.id.isNotBlank() && it.poster.isNotBlank() }.distinctBy { it.platform + it.id }
                                    if (pool.isNotEmpty()) clipFeedItems = pool.shuffled().take(80)
                                    else Toast.makeText(ctx, "Cuplikan belum tersedia", Toast.LENGTH_SHORT).show()
                                },
                                onResume = { h ->
                                    scope.launch {
                                        Toast.makeText(ctx, "Lanjut memutar Ep ${h.episode}...", Toast.LENGTH_SHORT).show()
                                        val d = Drama(h.id, h.title, poster = h.poster, platform = h.platform)
                                        val det = runCatching { repo.loadDetailCached(d) }.getOrNull() ?: Detail(d)
                                        playerSession = PlayerSession(det, h.episode)
                                    }
                                }
                            )
                            Tab.Search -> SearchScreen(repo, store, selPlatform, onDrama = { selectedDrama = it }, onBack = { tab = Tab.Home }, dataTick = dataTick, bump = { dataTick++ })
                            Tab.Clips -> ClipsScreen(homeState, repo, store, onBack = { tab = Tab.Home }, onWatchFull = { playerSession = PlayerSession(it, 1) }, onOpenDetail = { selectedDrama = it })
                            Tab.Library -> LibraryScreen(
                                store, dataTick,
                                onDrama = { selectedDrama = it },
                                onResume = { h ->
                                    scope.launch {
                                        Toast.makeText(ctx, "Lanjut memutar Ep ${h.episode}...", Toast.LENGTH_SHORT).show()
                                        val d = Drama(h.id, h.title, poster = h.poster, platform = h.platform)
                                        val det = runCatching { repo.loadDetailCached(d) }.getOrNull() ?: Detail(d)
                                        playerSession = PlayerSession(det, h.episode)
                                    }
                                }
                            )
                            Tab.Profile -> ProfileScreen(store, dataTick, bump = { dataTick++ })
                            else -> {}
                        }
                    }
                }
                }
            }
        }

        AnimatedVisibility(selectedDrama != null) {
            selectedDrama?.let { d ->
                DetailScreen(detailState, d, store, resolvingEpisode,
                    onClose = { selectedDrama = null },
                    onPlay = { det, ep -> openPlayer(det, ep) },
                    onFavChanged = { dataTick++ },
                    onShare = { shareDrama(ctx, it) }
                )
            }
        }

        playerSession?.let { s ->
            VerticalEpisodePlayer(s.detail, s.startEpisode, repo, store) {
                playerSession = null; dataTick++
            }
        }

        if (clipFeedItems.isNotEmpty()) {
            ClipFeedPlayer(clipFeedItems, repo, store,
                onClose = { clipFeedItems = emptyList() },
                onWatchFull = { clipFeedItems = emptyList(); playerSession = PlayerSession(it, 1) },
                onOpenDetail = { clipFeedItems = emptyList(); selectedDrama = it }
            )
        }

        if (showSettings) {
            SettingsOverlay(store, dataTick, bump = { dataTick++ }, onClose = { showSettings = false })
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// UI COMPONENTS (PREMIUM DESIGN)
// ─────────────────────────────────────────────────────────────────

@Composable
private fun BottomNavBar(selected: Tab, onSelect: (Tab) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = DS.Bg2.copy(alpha = 0.92f),
            tonalElevation = 12.dp,
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .border(1.dp, DS.Line, RoundedCornerShape(32.dp))
        ) {
            Row(
                Modifier.fillMaxSize().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Tab.values().filter { it.showNav }.forEach { tab ->
                    val active = tab == selected
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clip(CircleShape)
                            .clickable { onSelect(tab) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                tab.icon,
                                tab.label,
                                tint = if (active) DS.Primary else DS.Hint,
                                modifier = Modifier.size(if (active) 26.dp else 22.dp)
                            )
                            if (active) {
                                Spacer(Modifier.height(4.dp))
                                Box(Modifier.size(4.dp).clip(CircleShape).background(DS.Primary))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(
    platformId: String, scrollToTopSignal: Int, state: Load<HomeBundle>, history: List<HistoryItem>,
    remoteConfig: NativeRemoteConfig?, remoteError: String?,
    loadingMore: Boolean, loadMoreError: String?,
    onLoadMore: () -> Unit, onPlatform: (String) -> Unit, onRefresh: () -> Unit,
    onDrama: (Drama) -> Unit, onSearch: () -> Unit, onRandom: () -> Unit,
    onClips: () -> Unit, onResume: (HistoryItem) -> Unit,
    category: HomeCategory? = null, onExitCategory: () -> Unit = {}
) {
    val listState = rememberLazyListState()
    LaunchedEffect(scrollToTopSignal) {
        if (scrollToTopSignal > 0) listState.animateScrollToItem(0)
    }

    LaunchedEffect(listState, state, platformId, loadingMore) {
        snapshotFlow {
            val info = listState.layoutInfo
            val last = info.visibleItemsInfo.lastOrNull()
            last != null && last.index >= info.totalItemsCount - 4
        }.collect { near ->
            val data = (state as? Load.Ok<HomeBundle>)?.data ?: return@collect
            if (near && data.hasMore && !loadingMore) onLoadMore()
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        item {
            HomeHeader(
                platformId = platformId,
                category = category,
                remoteConfig = remoteConfig,
                remoteError = remoteError,
                historyCount = history.size,
                chips = category?.platforms?.map { platform(it) } ?: Platforms,
                onExitCategory = onExitCategory,
                onSearch = onSearch,
                onRefresh = onRefresh,
                onRandom = onRandom,
                onClips = onClips,
                onPlatform = onPlatform
            )
        }

        when (state) {
            Load.Loading, Load.Idle -> item { ShimmerLoader() }
            is Load.Err -> item { ErrorCard(state.message, onRefresh) }
            is Load.Ok<HomeBundle> -> {
                val data = state.data
                val all = (data.recommended + data.popular + data.newest).distinctBy { it.platform + "|" + it.id }

                if (all.isEmpty()) {
                    item { EmptyState("Belum ada judul", "Coba refresh atau ganti sumber dulu", Icons.Rounded.Movie) }
                } else {
                    item { HeroCard(all.first(), onDrama) }
                    if (history.isNotEmpty()) item {
                        Section("Lanjutkan menonton") {
                            LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(history.take(8), key = { it.platform + it.id }) { watched -> ContinueCard(watched, onResume) }
                            }
                        }
                    }

                    item {
                        Text("Pilihan Untukmu", color = DS.White, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(24.dp))
                    }

                    all.drop(1).chunked(2).forEach { row ->
                        item {
                            Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                row.forEach { d ->
                                    DiscoverDramaCard(drama = d, isNew = false, onClick = onDrama, modifier = Modifier.weight(1f))
                                }
                                if (row.size == 1) Spacer(Modifier.weight(1f))
                            }
                            Spacer(Modifier.height(16.dp))
                        }
                    }

                    if (loadingMore) item { Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = DS.Primary) } }
                }
            }
        }
    }
}

@Composable
private fun CategoryHomeScreen(onSelect: (HomeCategory) -> Unit, onSettings: () -> Unit) {
    Box(Modifier.fillMaxSize().background(DS.Bg)) {
        Box(
            Modifier.align(Alignment.TopEnd).size(400.dp).offset(x = 100.dp, y = (-150).dp)
                .clip(CircleShape).background(Brush.radialGradient(listOf(DS.Primary.copy(alpha = 0.25f), Color.Transparent)))
        )
        Box(
            Modifier.align(Alignment.BottomStart).size(350.dp).offset(x = (-100).dp, y = 150.dp)
                .clip(CircleShape).background(Brush.radialGradient(listOf(DS.Secondary.copy(alpha = 0.2f), Color.Transparent)))
        )

        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
            Spacer(Modifier.height(54.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Selamat Datang,", color = DS.Muted, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text("Dramaku", color = DS.White, fontSize = 32.sp, fontWeight = FontWeight.Black, letterSpacing = (-1).sp)
                }
                HeaderCircleButton(Icons.Rounded.Settings, "Settings", onSettings)
            }

            Spacer(Modifier.height(32.dp))
            
            Box(
                Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(24.dp))
                    .background(Brush.linearGradient(DS.MainGrad))
                    .clickable { onSelect(HomeCategory.ShortDrama) }
            ) {
                Column(Modifier.padding(24.dp).align(Alignment.BottomStart)) {
                    Surface(color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(50)) {
                        Text("PALING POPULER", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("Short Drama", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                    Text("Ribuan episode pendek siap menemani harimu.", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                }
                Icon(Icons.Rounded.PlayArrow, null, tint = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(140.dp).align(Alignment.CenterEnd).offset(x = 20.dp))
            }

            Spacer(Modifier.height(24.dp))
            Text("PILIH KATEGORI", color = DS.Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(Modifier.height(16.dp))

            CategoryWideCard(
                category = HomeCategory.MovieDrama,
                icon = Icons.Rounded.Movie,
                badgeText = "Drama Asia",
                title = "Serial Korea & China",
                subtitle = "Update setiap hari dengan sub Indo.",
                accent = DS.Secondary,
                onClick = { onSelect(HomeCategory.MovieDrama) }
            )
            Spacer(Modifier.height(16.dp))
            CategoryWideCard(
                category = HomeCategory.MovieBox,
                icon = Icons.Rounded.Tv,
                badgeText = "Cinema",
                title = "Movie Box",
                subtitle = "Film layar lebar kualitas Full HD.",
                accent = DS.Gold,
                onClick = { onSelect(HomeCategory.MovieBox) }
            )

            Spacer(Modifier.height(32.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                CategorySmallCard(HomeCategory.Anime, Icons.Rounded.AutoAwesome, Modifier.weight(1f)) { onSelect(HomeCategory.Anime) }
                CategorySmallCard(HomeCategory.Manga, Icons.Rounded.MenuBook, Modifier.weight(1f)) { onSelect(HomeCategory.Manga) }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun CategoryWideCard(category: HomeCategory, icon: ImageVector, badgeText: String, title: String, subtitle: String, accent: Color, onClick: () -> Unit) {
    Surface(
        color = DS.Bg2, shape = RoundedCornerShape(22.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, DS.Line, RoundedCornerShape(22.dp)).clickable(onClick = onClick)
    ) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(accent.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = accent)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(badgeText.uppercase(), color = accent, fontSize = 10.sp, fontWeight = FontWeight.Black)
                Text(title, color = DS.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = DS.Muted, fontSize = 12.sp)
            }
            Icon(Icons.Rounded.ChevronRight, null, tint = DS.Hint)
        }
    }
}

@Composable
private fun CategorySmallCard(category: HomeCategory, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        color = DS.Bg2, shape = RoundedCornerShape(20.dp),
        modifier = modifier.border(1.dp, DS.Line, RoundedCornerShape(20.dp)).clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(20.dp)) {
            Icon(icon, null, tint = DS.Muted)
            Spacer(Modifier.height(12.dp))
            Text(category.title, color = DS.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            if (category.comingSoon) Text("Segera", color = DS.Hint, fontSize = 11.sp)
        }
    }
}

@Composable
private fun HomeHeader(
    platformId: String, category: HomeCategory?, remoteConfig: NativeRemoteConfig?, remoteError: String?, 
    historyCount: Int, chips: List<PlatformInfo>, onExitCategory: () -> Unit, onSearch: () -> Unit, 
    onRefresh: () -> Unit, onRandom: () -> Unit, onClips: () -> Unit, onPlatform: (String) -> Unit
) {
    Column(Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 12.dp)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onExitCategory, modifier = Modifier.size(40.dp).clip(CircleShape).background(DS.Bg2)) {
                Icon(Icons.Rounded.ArrowBack, null, tint = DS.White, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(category?.title ?: "Dramaku", color = DS.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                Text(platformLabel(platformId), color = DS.Primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            HeaderCircleButton(Icons.Rounded.Search, "Search", onSearch)
        }
        Spacer(Modifier.height(20.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(chips) { source ->
                val selected = source.id == platformId
                Surface(
                    color = if (selected) DS.Primary else DS.Bg2,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(40.dp).clickable { onPlatform(source.id) }.border(1.dp, if (selected) Color.Transparent else DS.Line, RoundedCornerShape(12.dp))
                ) {
                    Box(Modifier.padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
                        Text(source.label, color = if (selected) Color.White else DS.Text, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderCircleButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Surface(color = DS.Bg2, shape = CircleShape, modifier = Modifier.size(40.dp).clickable(onClick = onClick)) {
        Box(contentAlignment = Alignment.Center) { Icon(icon, label, tint = DS.White, modifier = Modifier.size(20.dp)) }
    }
}

@Composable
private fun HeroCard(drama: Drama, onClick: (Drama) -> Unit) {
    Box(Modifier.fillMaxWidth().height(480.dp).clickable { onClick(drama) }) {
        AsyncImage(drama.poster, drama.title, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent, DS.Bg.copy(alpha = 0.4f), DS.Bg))))
        Column(Modifier.align(Alignment.BottomStart).padding(horizontal = 24.dp, vertical = 32.dp)) {
            Surface(color = DS.Primary.copy(alpha = 0.25f), shape = RoundedCornerShape(8.dp), modifier = Modifier.border(1.dp, DS.Primary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))) {
                Text(platformLabel(drama.platform).uppercase() + " • TOP PICK", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text(drama.title, color = DS.White, fontSize = 36.sp, lineHeight = 40.sp, fontWeight = FontWeight.Black, letterSpacing = (-1.2).sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(28.dp))
            Button(onClick = { onClick(drama) }, colors = ButtonDefaults.buttonColors(containerColor = DS.White, contentColor = Color.Black), shape = RoundedCornerShape(14.dp), modifier = Modifier.height(54.dp).fillMaxWidth()) {
                Icon(Icons.Rounded.PlayArrow, null); Spacer(Modifier.width(8.dp)); Text("Tonton Sekarang", fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun ContinueCard(h: HistoryItem, onClick: (HistoryItem) -> Unit) {
    Column(Modifier.width(160.dp).clickable { onClick(h) }) {
        Box(Modifier.fillMaxWidth().aspectRatio(1.6f).clip(RoundedCornerShape(16.dp)).background(DS.Bg2)) {
            PosterImage(h.poster, h.title, Modifier.fillMaxSize())
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)))))
            LinearProgressIndicator(progress = (h.pct / 100f).coerceIn(0f, 1f), color = DS.Primary, trackColor = Color.White.copy(alpha = 0.2f), modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(4.dp) )
        }
        Spacer(Modifier.height(10.dp))
        Text(h.title, color = DS.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text("EP ${h.episode} • ${h.pct}%", color = DS.Muted, fontSize = 11.sp)
    }
}

@Composable
private fun DiscoverDramaCard(drama: Drama, isNew: Boolean, onClick: (Drama) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.clickable { onClick(drama) }) {
        Box(Modifier.fillMaxWidth().aspectRatio(0.72f).clip(RoundedCornerShape(20.dp)).background(DS.Bg2)) {
            AsyncImage(drama.poster, drama.title, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)))))
            if (drama.episodes > 0) {
                Surface(color = Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(6.dp), modifier = Modifier.align(Alignment.BottomStart).padding(10.dp)) {
                    Text("${drama.episodes} EP", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(drama.title, color = DS.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(platformLabel(drama.platform), color = DS.Muted, fontSize = 11.sp)
    }
}

@Composable
private fun PosterImage(url: String, title: String, modifier: Modifier) {
    Box(modifier.clip(RoundedCornerShape(14.dp)).background(DS.Bg3)) {
        if (url.isNotBlank()) AsyncImage(url, title, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        else Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Icon(Icons.Rounded.Movie, null, tint = DS.Hint) }
    }
}

@Composable
private fun DetailScreen(state: Load<Detail>, fallback: Drama, store: LocalStore, resolvingEpisode: Int, onClose: () -> Unit, onPlay: (Detail, Int) -> Unit, onFavChanged: () -> Unit, onShare: (Drama) -> Unit) {
    val detail = (state as? Load.Ok<Detail>)?.data ?: Detail(fallback)
    val drama = detail.drama
    val isFav = store.isFav(drama.id, drama.platform)
    val hist = store.history(0).firstOrNull { it.id == drama.id && it.platform == drama.platform }
    val resumeEp = hist?.episode ?: 1
    val total = max(drama.episodes, detail.episodes.size).coerceAtLeast(1)

    Box(Modifier.fillMaxSize().background(DS.Bg)) {
        LazyColumn(Modifier.fillMaxSize()) {
            item {
                Box(Modifier.fillMaxWidth().height(480.dp)) {
                    AsyncImage(drama.poster, drama.title, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f), DS.Bg))))
                    IconButton(onClick = onClose, modifier = Modifier.padding(20.dp).size(44.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.5f))) { Icon(Icons.Rounded.ArrowBack, null, tint = Color.White) }
                    Column(Modifier.align(Alignment.BottomStart).padding(24.dp)) {
                        Surface(color = DS.Primary, shape = RoundedCornerShape(6.dp)) { Text(platformLabel(drama.platform).uppercase(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) }
                        Spacer(Modifier.height(12.dp))
                        Text(drama.title, color = DS.White, fontSize = 32.sp, fontWeight = FontWeight.Black, lineHeight = 36.sp)
                        Text("$total Episodes", color = DS.Muted, fontSize = 14.sp)
                    }
                }
            }
            item {
                Column(Modifier.padding(horizontal = 24.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = { onPlay(detail, resumeEp) }, colors = ButtonDefaults.buttonColors(containerColor = DS.Primary), shape = RoundedCornerShape(14.dp), modifier = Modifier.height(56.dp).weight(1f)) {
                            Icon(Icons.Rounded.PlayArrow, null); Spacer(Modifier.width(8.dp)); Text(if (hist != null) "Lanjut EP $resumeEp" else "Mulai Nonton", fontWeight = FontWeight.Black)
                        }
                        IconButton(onClick = { store.toggleFav(drama); onFavChanged() }, modifier = Modifier.size(56.dp).clip(RoundedCornerShape(14.dp)).background(DS.Bg2)) { Icon(if (isFav) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder, null, tint = if (isFav) DS.Primary else Color.White) }
                    }
                    Spacer(Modifier.height(32.dp))
                    Text("Sinopsis", color = DS.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(drama.description.ifBlank { "Tidak ada deskripsi." }, color = DS.Text, fontSize = 14.sp, lineHeight = 22.sp)
                    Spacer(Modifier.height(32.dp))
                    Text("Episode", color = DS.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                }
            }
            item {
                LazyVerticalGrid(columns = GridCells.Fixed(5), modifier = Modifier.height(300.dp).padding(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(total) { i ->
                        val ep = i + 1
                        Surface(color = if (ep == resumeEp) DS.Primary else DS.Bg2, shape = RoundedCornerShape(10.dp), modifier = Modifier.height(44.dp).clickable { onPlay(detail, ep) }) {
                            Box(contentAlignment = Alignment.Center) { Text(ep.toString(), color = Color.White, fontWeight = FontWeight.Bold) }
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(100.dp)) }
        }
    }
}

@Composable
private fun ClipsScreen(state: Load<HomeBundle>, repo: DramakuRepository, store: LocalStore, onBack: () -> Unit, onWatchFull: (Detail) -> Unit, onOpenDetail: (Drama) -> Unit) {
    when (state) {
        Load.Loading, Load.Idle -> Box(Modifier.fillMaxSize().background(DS.Bg), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = DS.Primary); Spacer(Modifier.height(12.dp))
                Text("Menyiapkan cuplikan...", color = DS.Text, fontWeight = FontWeight.Bold)
            }
        }
        is Load.Err -> ErrorCard(state.message, onBack)
        is Load.Ok<HomeBundle> -> {
            val pool = remember(state.data) {
                (state.data.popular + state.data.newest + state.data.recommended).filter { it.id.isNotBlank() && it.poster.isNotBlank() }.distinctBy { it.platform + it.id }.take(100)
            }
            if (pool.isEmpty()) PlaceholderScreen("Cuplikan", "Belum tersedia untuk platform ini", Icons.Rounded.PlayCircle)
            else ClipFeedPlayer(pool, repo, store, onClose = onBack, onWatchFull = onWatchFull, onOpenDetail = onOpenDetail)
        }
    }
}

@Composable
private fun SearchScreen(repo: DramakuRepository, store: LocalStore, currentPlatform: String, onDrama: (Drama) -> Unit, onBack: () -> Unit, dataTick: Int, bump: () -> Unit) {
    var q by remember { mutableStateOf("") }
    var state by remember { mutableStateOf<Load<List<Drama>>>(Load.Idle) }
    LaunchedEffect(q) {
        if (q.length < 2) { state = Load.Idle; return@LaunchedEffect }
        delay(500); state = Load.Loading
        state = runCatching { repo.searchPlatform(q, currentPlatform) }.fold({ Load.Ok(it) }, { Load.Err(it.message ?: "Error") })
    }
    Column(Modifier.fillMaxSize().padding(24.dp).background(DS.Bg)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, null, tint = DS.White) }
            Text("Cari Drama", color = DS.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.height(16.dp))
        TextField(value = q, onValueChange = { q = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Ketik judul...") }, colors = TextFieldDefaults.colors(unfocusedContainerColor = DS.Bg2, focusedContainerColor = DS.Bg2, focusedTextColor = DS.White, unfocusedTextColor = DS.White))
        Spacer(Modifier.height(16.dp))
        when (val s = state) {
            is Load.Ok -> LazyVerticalGrid(columns = GridCells.Fixed(3), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(s.data) { d -> DiscoverDramaCard(d, false, onDrama) }
            }
            Load.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = DS.Primary) }
            else -> {}
        }
    }
}

@Composable
private fun LibraryScreen(store: LocalStore, dataTick: Int, onDrama: (Drama) -> Unit, onResume: (HistoryItem) -> Unit) {
    val favs = remember(dataTick) { store.favs() }
    val hist = remember(dataTick) { store.history(dataTick) }
    Column(Modifier.fillMaxSize().padding(24.dp).background(DS.Bg)) {
        Text("Koleksi", color = DS.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(24.dp))
        Text("Favorit", color = DS.White, fontWeight = FontWeight.Bold)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(favs) { d -> DiscoverDramaCard(d, false, onDrama, modifier = Modifier.width(120.dp)) }
        }
        Spacer(Modifier.height(24.dp))
        Text("Riwayat", color = DS.White, fontWeight = FontWeight.Bold)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(hist) { h -> ListItem(h.title, "${platformLabel(h.platform)} • EP ${h.episode}", h.poster) { onResume(h) } }
        }
    }
}

@Composable
private fun ListItem(title: String, subtitle: String, poster: String, onClick: () -> Unit) {
    Surface(color = DS.Bg2, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(poster, null, Modifier.size(50.dp, 70.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, color = DS.White, fontWeight = FontWeight.Bold)
                Text(subtitle, color = DS.Muted, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun ProfileScreen(store: LocalStore, dataTick: Int, bump: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp).background(DS.Bg)) {
        Text("Profil", color = DS.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(32.dp))
        ProfileGroupTitle("PENGATURAN")
        SettingRow("Hapus Riwayat") { store.clearHistory(); bump() }
        SettingRow("Hapus Favorit") { store.clearFavs(); bump() }
    }
}

@Composable
private fun ProfileGroupTitle(text: String) {
    Text(text, color = DS.Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
private fun SettingRow(title: String, onClick: () -> Unit) {
    Surface(color = DS.Bg2, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable(onClick = onClick)) {
        Row(Modifier.padding(16.dp)) { Text(title, color = DS.White); Spacer(Modifier.weight(1f)); Icon(Icons.Rounded.ChevronRight, null, tint = DS.Hint) }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column {
        Text(title, color = DS.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp))
        content()
    }
}

@Composable
private fun ShimmerLoader() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = DS.Primary) }
}

@Composable
private fun ErrorCard(msg: String, onRetry: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(msg, color = DS.Muted, textAlign = TextAlign.Center)
        Button(onClick = onRetry) { Text("Coba Lagi") }
    }
}

@Composable
private fun OfflineBanner(onRefresh: () -> Unit) {
    Surface(color = DS.Red, modifier = Modifier.fillMaxWidth().clickable(onClick = onRefresh)) {
        Text("Offline. Tap untuk refresh.", color = Color.White, modifier = Modifier.padding(8.dp).fillMaxWidth(), textAlign = TextAlign.Center)
    }
}

@Composable
private fun EmptyState(t: String, s: String, i: ImageVector) {
    Column(Modifier.fillMaxWidth().padding(48.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(i, null, tint = DS.Hint, modifier = Modifier.size(48.dp))
        Text(t, color = DS.White, fontWeight = FontWeight.Bold)
        Text(s, color = DS.Muted, fontSize = 12.sp)
    }
}

@Composable
private fun PlaceholderScreen(title: String, subtitle: String, icon: ImageVector) {
    Box(Modifier.fillMaxSize().background(DS.Bg), contentAlignment = Alignment.Center) {
        Surface(color = DS.Bg2, shape = RoundedCornerShape(28.dp), modifier = Modifier.padding(24.dp).border(1.dp, DS.Line, RoundedCornerShape(28.dp))) {
            Column(Modifier.padding(horizontal = 24.dp, vertical = 28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(color = DS.PrimaryDim, shape = RoundedCornerShape(22.dp), modifier = Modifier.size(66.dp)) {
                    Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = DS.Primary, modifier = Modifier.size(31.dp)) }
                }
                Spacer(Modifier.height(15.dp))
                Text(title, color = DS.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                Text(subtitle, color = DS.Muted, fontSize = 13.sp, lineHeight = 18.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 5.dp))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ClipFeedPlayer(items: List<Drama>, repo: DramakuRepository, store: LocalStore, onClose: () -> Unit, onWatchFull: (Detail) -> Unit, onOpenDetail: (Drama) -> Unit) {
    val ctx = LocalContext.current
    val pager = rememberPagerState(pageCount = { items.size })
    val player = remember { buildPlayer(ctx) }
    
    BackHandler { player.stop(); onClose() }
    
    LaunchedEffect(pager.currentPage) {
        val drama = items[pager.currentPage]
        val stream = runCatching { repo.resolveStreamCached(Detail(drama), 1, false) }.getOrNull()
        if (stream != null) {
            player.setMediaItem(MediaItem.fromUri(stream.url))
            player.prepare()
            player.play()
        }
    }
    
    Box(Modifier.fillMaxSize().background(Color.Black)) {
        VerticalPager(pager, Modifier.fillMaxSize()) { page ->
            AndroidView(factory = { PlayerView(it).apply { this.player = player; useController = false } }, modifier = Modifier.fillMaxSize())
        }
        IconButton(onClick = { player.stop(); onClose() }, modifier = Modifier.padding(20.dp).align(Alignment.TopStart)) {
            Icon(Icons.Rounded.Close, null, tint = Color.White)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun VerticalEpisodePlayer(detail: Detail, startEp: Int, repo: DramakuRepository, store: LocalStore, onClose: () -> Unit) {
    val ctx = LocalContext.current
    val total = max(detail.drama.episodes, detail.episodes.size).coerceAtLeast(1)
    val pager = rememberPagerState(initialPage = (startEp - 1).coerceIn(0, total - 1), pageCount = { total })
    val player = remember { buildPlayer(ctx) }
    
    BackHandler { player.stop(); onClose() }
    
    LaunchedEffect(pager.currentPage) {
        val ep = pager.currentPage + 1
        val res = runCatching { repo.resolveStreamCached(detail, ep, false) }.getOrNull()
        if (res != null) {
            player.setMediaItem(MediaItem.fromUri(res.url))
            player.prepare()
            player.play()
        }
    }
    
    Box(Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(factory = { PlayerView(it).apply { this.player = player; useController = true } }, modifier = Modifier.fillMaxSize())
        IconButton(onClick = { player.stop(); onClose() }, modifier = Modifier.padding(20.dp).align(Alignment.TopStart).background(Color.Black.copy(alpha = 0.5f), CircleShape)) {
            Icon(Icons.Rounded.Close, null, tint = Color.White)
        }
    }
}

@Composable
private fun SettingsOverlay(store: LocalStore, dataTick: Int, bump: () -> Unit, onClose: () -> Unit) {
    Box(Modifier.fillMaxSize().background(DS.Bg)) {
        ProfileScreen(store, dataTick, bump)
        IconButton(onClick = onClose, modifier = Modifier.padding(16.dp).align(Alignment.TopStart)) { Icon(Icons.Rounded.Close, null, tint = DS.White) }
    }
}

// ─────────────────────────────────────────────────────────────────
// HELPERS
// ─────────────────────────────────────────────────────────────────

private fun Context.isNetworkAvailable(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return true
    return cm.getNetworkCapabilities(cm.activeNetwork)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
}

private fun buildPlayer(ctx: Context): ExoPlayer = ExoPlayer.Builder(ctx).build()
private fun shareDrama(ctx: Context, d: Drama) {}
private fun enc(s: String) = URLEncoder.encode(s, "UTF-8")
private fun normalizeKey(s: String) = s.lowercase().replace(Regex("[^a-z0-9\\p{L}\\s]"), " ").replace(Regex("\\s+"), " ").trim()
private fun mergeHomeBundles(c: HomeBundle, n: HomeBundle) = HomeBundle(dedupe(c.recommended + n.recommended), dedupe(c.popular + n.popular), dedupe(c.newest + n.newest), max(c.loadedPage, n.loadedPage), n.hasMore)
private fun dedupe(items: List<Drama>) = items.filter { it.id.isNotBlank() && it.title.isNotBlank() }.distinctBy { it.platform + "|" + it.id }

// ─────────────────────────────────────────────────────────────────
// REPOSITORY (RECURSIVE JSON EXTRACTION)
// ─────────────────────────────────────────────────────────────────

private fun JSONObject.dataOrSelf(): Any = opt("data")?.takeUnless { it == JSONObject.NULL } ?: this
private fun JSONArray.objects(): List<JSONObject> = (0 until length()).mapNotNull { optJSONObject(it) }
private fun JSONObject.stringAny(vararg keys: String): String {
    for (k in keys) { val v = opt(k); if (v != null && v != JSONObject.NULL && v.toString().isNotBlank()) return v.toString().trim() }
    return ""
}
private fun JSONObject.intAny(vararg keys: Any): Int {
    for (k in keys) { if (k is String && has(k)) { val v = opt(k); return when(v) { is Number -> v.toInt(); is String -> v.filter { it.isDigit() }.toIntOrNull() ?: 0; else -> 0 } } }
    return 0
}

private class DramakuRepository {
    private val client = OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).build()
    private val detailCache = ConcurrentHashMap<String, Detail>()

    suspend fun loadHome(p: String) = loadHomePage(p, 1)

    suspend fun loadHomePage(p: String, page: Int): HomeBundle = coroutineScope {
        if (p == "melolo") return@coroutineScope loadMeloloHome(page)

        val urls = homeUrls(p, page)
        val responses = urls.map { url -> async { runCatching { getJson(url) } } }.awaitAll()
        val results = responses.map { it.getOrNull() }

        // Jangan mengubah kegagalan seluruh endpoint menjadi halaman kosong.
        if (results.all { it == null }) {
            val cause = responses.firstNotNullOfOrNull { it.exceptionOrNull() }
            throw IllegalStateException(
                "Konten $p gagal dimuat${cause?.message?.let { ": $it" }.orEmpty()}",
                cause
            )
        }

        val rec = flat(results.getOrNull(0)?.dataOrSelf(), p)
        val pop = flat(results.getOrNull(1)?.dataOrSelf(), p)
        val newest = flat(results.getOrNull(2)?.dataOrSelf(), p)
        val hasContent = rec.isNotEmpty() || pop.isNotEmpty() || newest.isNotEmpty()

        if (!hasContent) error("API $p merespons, tetapi tidak berisi daftar drama")
        HomeBundle(rec, pop, newest, page, page < 5)
    }

    private suspend fun loadMeloloHome(page: Int): HomeBundle {
        val base = apiBase("melolo")
        val url = if (page == 1) "$base/bookmall?lang=id" else "$base/search?q=a&lang=id&limit=20&offset=${(page - 1) * 20}"
        val json = getJson(url)
        val items = flat(json.dataOrSelf(), "melolo")
        if (items.isEmpty()) error("API Melolo merespons, tetapi daftar drama kosong")
        return HomeBundle(items, items.shuffled(), items.reversed(), page, page < 5)
    }

    suspend fun searchPlatform(q: String, p: String): List<Drama> = coroutineScope {
        val base = apiBase(p)
        val url = when(p) {
            "moviebox" -> "$base/subject/search?keyword=${enc(q)}&page=1"
            else -> "$base/search?q=${enc(q)}&lang=id"
        }
        flat(runCatching { getJson(url).dataOrSelf() }.getOrNull(), p)
    }

    suspend fun loadDetailCached(d: Drama): Detail {
        detailCache[d.platform + d.id]?.let { return it }
        val base = apiBase(d.platform)
        val url = when(d.platform) {
            "moviebox" -> "$base/subject/get?subjectId=${enc(d.id)}&lang=id"
            "melolo" -> "$base/book?id=${enc(d.id)}&lang=id"
            else -> "$base/detail?id=${enc(d.id)}&lang=id"
        }
        val raw = runCatching { getJson(url).dataOrSelf() }.getOrNull() as? JSONObject ?: JSONObject()
        val res = normalize(raw, d.platform)
        val epsArr = raw.optJSONArray("episodes") ?: raw.optJSONArray("video_list") ?: raw.optJSONArray("chapterList")
        val eps = epsArr?.objects()?.mapIndexed { i, o -> EpisodeInfo(o.intAny("episode", "index", i + 1), o.stringAny("streaming", "url")) }.orEmpty()
        return Detail(res.copy(id = d.id), eps).also { detailCache[d.platform + d.id] = it }
    }

    suspend fun resolveStreamCached(d: Detail, ep: Int, ds: Boolean): StreamResult {
        val base = apiBase(d.drama.platform)
        val url = "$base/stream?id=${enc(d.drama.id)}&ep=$ep"
        val json = runCatching { getJson(url).dataOrSelf() }.getOrNull() as? JSONObject ?: JSONObject()
        return StreamResult(json.stringAny("url", "playUrl", "video_url"))
    }

    private suspend fun getJson(url: String): JSONObject = withContext(Dispatchers.IO) {
        val req = Request.Builder()
            .url(url)
            .header("User-Agent", "DramakuNative/5.0")
            .apply {
                if (url.contains("captain.sapimu.au")) {
                    header("Authorization", "Bearer 15693e658f723c5b4c45900a5d045ef0ab6a053ecda4dadb831c68fef773ba5e")
                }
            }
            .build()
        client.newCall(req).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                error("HTTP ${response.code} dari ${response.request.url.encodedPath}")
            }
            if (body.isBlank()) error("Respons API kosong dari ${response.request.url.encodedPath}")
            runCatching { JSONObject(body) }.getOrElse {
                throw IllegalStateException(
                    "Respons API bukan JSON dari ${response.request.url.encodedPath}",
                    it
                )
            }
        }
    }

    private fun homeUrls(p: String, sp: Int): List<String> {
        val base = apiBase(p)
        return when (p) {
            "moviebox" -> listOf("$base/tabs/home-content?page=$sp&lang=id", "$base/tabs/category-content?type=1&page=$sp", "$base/shorts/most-trending")
            else -> listOf("$base/home?page=$sp&lang=id", "$base/populer?page=$sp", "$base/new?page=$sp")
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// LOCAL STORE
// ─────────────────────────────────────────────────────────────────

private class LocalStore(ctx: Context) {
    private val p = ctx.getSharedPreferences("dramaku_v7", Context.MODE_PRIVATE)
    fun platform() = p.getString("p", "melolo") ?: "melolo"
    fun setPlatform(s: String) = p.edit().putString("p", s).apply()
    fun categoryPlatform(cat: String, fallback: String) = p.getString("cp_$cat", fallback) ?: fallback
    fun setCategoryPlatform(cat: String, platform: String) = p.edit().putString("cp_$cat", platform).apply()
    fun updateProgress(id: String, platform: String, ep: Int, pos: Long, dur: Long) {
        p.edit().putLong("prog_${platform}_${id}_${ep}_pos", pos).putLong("prog_${platform}_${id}_${ep}_dur", dur).apply()
    }
    fun history(tick: Int = 0): List<HistoryItem> = emptyList()
    fun favs(): List<Drama> = emptyList()
    fun isFav(id: String, p: String) = false
    fun toggleFav(d: Drama) {}
    fun clearHistory() {}
    fun clearFavs() {}
}

// ─────────────────────────────────────────────────────────────────
// JSON HELPERS
// ─────────────────────────────────────────────────────────────────

private fun flat(any: Any?, fp: String): List<Drama> {
    val out = mutableListOf<Drama>()

    fun visit(value: Any?, depth: Int) {
        // Proteksi jika respons API rusak atau terlalu dalam.
        if (value == null || value == JSONObject.NULL || depth > 40) return

        when (value) {
            is JSONArray -> {
                for (i in 0 until value.length()) visit(value.opt(i), depth + 1)
            }
            is JSONObject -> {
                // Coba object saat ini, lalu telusuri SEMUA child. Dengan begitu wrapper
                // baru seperti Melolo `cell -> cell_data -> books` tetap terbaca.
                val drama = normalize(value, fp)
                if (drama.id.isNotBlank() && drama.title.isNotBlank()) out.add(drama)

                val keys = value.keys()
                while (keys.hasNext()) {
                    when (val child = value.opt(keys.next())) {
                        is JSONObject, is JSONArray -> visit(child, depth + 1)
                    }
                }
            }
        }
    }

    visit(any, 0)
    return out.distinctBy { it.platform + "|" + it.id }
}

private fun normalize(o: JSONObject, fp: String) = Drama(
    o.stringAny("id", "drama_id", "bookId", "subjectId", "book_id"),
    o.stringAny("title", "name", "drama_name", "bookName", "book_name"),
    o.stringAny("description", "intro", "synopsis", "introduction", "intro_text"),
    o.stringAny("poster", "cover", "thumb_url", "coverWap", "bookCover", "image", "thumb", "cover_url"),
    o.intAny("episodes", "episode_count", "chapterCount", "totalEpisode", "episode_number"),
    o.stringAny("views", "hits", "hotCode", "stat_value"),
    emptyList(),
    fp
)
