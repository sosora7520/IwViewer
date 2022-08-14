package com.rerere.iwara4a.ui.screen.index

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.rerere.iwara4a.data.api.Response
import com.rerere.iwara4a.data.api.backend.Iwara4aBackendAPI
import com.rerere.iwara4a.data.api.github.GithubAPI
import com.rerere.iwara4a.data.api.oreno3d.Oreno3dApi
import com.rerere.iwara4a.data.api.oreno3d.OrenoSort
import com.rerere.iwara4a.data.api.paging.OrenoSource
import com.rerere.iwara4a.data.model.detail.video.VideoDetailFast
import com.rerere.iwara4a.data.model.github.GithubRelease
import com.rerere.iwara4a.data.model.index.MediaPreview
import com.rerere.iwara4a.data.model.index.MediaType
import com.rerere.iwara4a.data.model.session.SessionManager
import com.rerere.iwara4a.data.model.user.Self
import com.rerere.iwara4a.data.repo.MediaRepo
import com.rerere.iwara4a.data.repo.UserRepo
import com.rerere.iwara4a.sharedPreferencesOf
import com.rerere.iwara4a.ui.component.MediaQueryParam
import com.rerere.iwara4a.ui.component.PageListProvider
import com.rerere.iwara4a.ui.component.SortType
import com.rerere.iwara4a.util.DataState
import com.rerere.iwara4a.util.logError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IndexViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val mediaRepo: MediaRepo,
    private val sessionManager: SessionManager,
    private val githubAPI: GithubAPI,
    private val oreno3dApi: Oreno3dApi,
    private val backendAPI: Iwara4aBackendAPI,
    private val context: Application
) : AndroidViewModel(context) {
    var self by mutableStateOf(Self.GUEST)
    var email by mutableStateOf("")
    var loadingSelf by mutableStateOf(false)

    var updateChecker = MutableStateFlow<DataState<GithubRelease>>(DataState.Empty)

    init {
        viewModelScope.launch {
            updateChecker.value = githubAPI.getLatestRelease().toDataState()
        }
        refreshSelf()
    }

    // Recommend
    val recommendVideoList: MutableStateFlow<DataState<List<VideoDetailFast>>> =
        MutableStateFlow(DataState.Empty)

    val allRecommendTags: MutableStateFlow<DataState<List<String>>> =
        MutableStateFlow(DataState.Empty)

    fun recommendVideoList(tags: Set<String>) {
        viewModelScope.launch {
            recommendVideoList.value = DataState.Loading
            try {
                recommendVideoList.value = DataState.Success(
                    backendAPI.recommend(
                        tags = tags.joinToString(","),
                        limit = 16
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                recommendVideoList.value = DataState.Error(e.javaClass.name)
            }
        }
    }

    fun loadTags() {
        viewModelScope.launch {
            try {
                allRecommendTags.value = DataState.Loading
                allRecommendTags.value = DataState.Success(
                    backendAPI.getAllRecommendTags()
                )
            } catch (e: Exception) {
                logError("Failed to get recommend tags", e)
                allRecommendTags.value = DataState.Error(e.javaClass.name)
            }
        }
    }

    init {
        loadTags()
    }

    // Pager: 视频列表
    val videoListPrvider = object : PageListProvider<MediaPreview> {
        private var lastSuccessPage = -1
        private var lastSuccessQueryParam: MediaQueryParam? = null
        private val data = MutableStateFlow<DataState<List<MediaPreview>>>(DataState.Empty)

        override fun load(page: Int, queryParam: MediaQueryParam?) {
            if (page == lastSuccessPage && queryParam == lastSuccessQueryParam) return

            viewModelScope.launch {
                data.value = DataState.Loading
                val response = mediaRepo.getMediaList(
                    session = sessionManager.session,
                    mediaType = MediaType.VIDEO,
                    page = page - 1,
                    sortType = queryParam?.sortType ?: SortType.DATE,
                    filters = queryParam?.filters ?: hashSetOf()
                )
                when (response) {
                    is Response.Success -> {
                        data.value = DataState.Success(response.read().mediaList)
                        lastSuccessPage = page
                        lastSuccessQueryParam = queryParam
                    }
                    is Response.Failed -> {
                        data.value = DataState.Error(response.errorMessage())
                    }
                }
            }
        }

        override fun getPage(): Flow<DataState<List<MediaPreview>>> = data
        override fun getTotal(): Int {
            TODO("Not yet implemented")
        }
    }


    // Pager: 订阅列表
    val subscriptionsProvider = object : PageListProvider<MediaPreview> {
        private var lastSuccessPage = -1
        private var lastSuccessQueryParam: MediaQueryParam? = null
        private val data = MutableStateFlow<DataState<List<MediaPreview>>>(DataState.Empty)

        override fun load(page: Int, queryParam: MediaQueryParam?) {
            if (page == lastSuccessPage && queryParam == lastSuccessQueryParam) return
            viewModelScope.launch {
                data.value = DataState.Loading
                val response = mediaRepo.getSubscriptionList(
                    session = sessionManager.session,
                    page = page - 1
                )
                when (response) {
                    is Response.Success -> {
                        data.value = DataState.Success(response.read().subscriptionList)
                        lastSuccessPage = page
                        lastSuccessQueryParam = queryParam
                    }
                    is Response.Failed -> {
                        data.value = DataState.Error(response.errorMessage())
                    }
                }
            }
        }

        override fun getPage(): Flow<DataState<List<MediaPreview>>> = data
        override fun getTotal(): Int {
            TODO("Not yet implemented")
        }
    }

    // 图片列表
    val imageListProvider = object : PageListProvider<MediaPreview> {
        private var lastSuccessPage = -1
        private var lastSuccessQueryParam: MediaQueryParam? = null

        private val data = MutableStateFlow<DataState<List<MediaPreview>>>(DataState.Empty)

        override fun load(page: Int, queryParam: MediaQueryParam?) {
            if (page == lastSuccessPage && queryParam == lastSuccessQueryParam) return
            viewModelScope.launch {
                data.value = DataState.Loading
                val response = mediaRepo.getMediaList(
                    session = sessionManager.session,
                    mediaType = MediaType.IMAGE,
                    page = page - 1,
                    sortType = queryParam?.sortType ?: SortType.DATE,
                    filters = queryParam?.filters ?: hashSetOf()
                )
                when (response) {
                    is Response.Success -> {
                        data.value = DataState.Success(response.read().mediaList)

                        lastSuccessPage = page
                        lastSuccessQueryParam = queryParam
                    }
                    is Response.Failed -> {
                        data.value = DataState.Error(response.errorMessage())
                    }
                }
            }
        }

        override fun getPage(): Flow<DataState<List<MediaPreview>>> = data
        override fun getTotal(): Int {
            TODO("Not yet implemented")
        }
    }

    // 推荐
    val orenoList = OrenoSort.values().map { sort ->
        sort to Pager(
            config = PagingConfig(
                pageSize = 36,
                prefetchDistance = 8,
                initialLoadSize = 36
            )
        ) {
            OrenoSource(
                oreno3dApi = oreno3dApi,
                orenoSort = sort
            )
        }.flow.cachedIn(viewModelScope)
    }.toList()

    fun openOrenoVideo(id: Int, result: (String) -> Unit) {
        viewModelScope.launch {
            val response = oreno3dApi.getVideoIwaraId(id)
            if (response.isSuccess()) {
                result(response.read())
            } else {
                result("")
            }
        }
    }

    fun refreshSelf() = viewModelScope.launch {
        loadingSelf = true
        email = context.sharedPreferencesOf("session").getString("username", "请先登录你的账号吧")!!
        val response = userRepo.getSelf(sessionManager.session)
        if (response.isSuccess()) {
            self = response.read()
        }
        loadingSelf = false
    }
}