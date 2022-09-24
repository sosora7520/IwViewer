package com.rerere.iwara4a.ui.screen.search

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.rerere.iwara4a.R
import com.rerere.iwara4a.ui.component.*
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.util.stringResource
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(searchViewModel: SearchViewModel = hiltViewModel()) {
    val navController = LocalNavController.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarScrollState())
    val pagerState = rememberPagerState()
    val state = PageListState
    Scaffold(
        topBar = {
            Md3TopBar(
                navigationIcon = {
                    BackIcon()
                },
                title = {
                    Text(stringResource(R.string.search))
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            TabComp(pagerState)
            HorizontalPager(
                count = 2,
                state = pagerState,
                userScrollEnabled = true
            ) {
                when (it) {
                    0 -> {
                        val pageList = rememberPageListPage()
                        Column {
                            SearchBar(searchViewModel,
                                queryParam = pageList.queryParam,
                                onChangeFiler = {
                                    pageList.queryParam = MediaQueryParam(
                                        pageList.queryParam.sortType,
                                        it
                                    )
                                },
                                onSearch = {
                                    searchViewModel.provider.load(pageList.page, pageList.queryParam)
                                }
                            )
                            PageList(
                                state = pageList,
                                provider = searchViewModel.provider,
                                supportQueryParam = true
                            ) { list ->
                                LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                                    items(list) {
                                        MediaPreviewCard(navController, it)
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        ComposeWebview(
                            link = "https://oreno3d.com/",
                            session = null,
                            onTitleChange = {}
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TabComp(pagerState: PagerState) {
    val scope = rememberCoroutineScope()
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = {
            TabRowDefaults.Indicator(Modifier.pagerTabIndicatorOffset(pagerState, it))
        }
    ) {
        Tab(
            selected = pagerState.currentPage == 0,
            onClick = {
                scope.launch {
                    pagerState.scrollToPage(0)
                }
            },
            text = {
                Text(text = "本站搜索")
            }
        )
        Tab(
            selected = pagerState.currentPage == 1,
            onClick = {
                scope.launch {
                    pagerState.scrollToPage(1)
                }
            },
            text = {
                Text(text = "Oreno3d搜索")
            }
        )
    }
}

@Composable
private fun SearchBar(searchViewModel: SearchViewModel,
                      queryParam: MediaQueryParam,
                      onChangeFiler: (MutableSet<String>) -> Unit,
                      onSearch: () -> Unit
){
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    Card(modifier = Modifier.padding(8.dp)) {
        Row(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = searchViewModel.query,
                    onValueChange = { searchViewModel.query = it.replace("\n", "") },
                    maxLines = 1,
                    placeholder = {
                        Text(text = stringResource(id = R.string.screen_search_bar_placeholder))
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    trailingIcon = {
                        if (searchViewModel.query.isNotEmpty()) {
                            IconButton(onClick = { searchViewModel.query = "" }) {
                                Icon(Icons.Outlined.Close, null)
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (searchViewModel.query.isBlank()) {
                                Toast.makeText(
                                    context,
                                    context.stringResource(id = R.string.screen_search_bar_empty),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                focusManager.clearFocus()
                                onSearch()
                            }
                        }
                    )
                )
            }
            IconButton(onClick = {
                if (searchViewModel.query.isBlank()) {
                    Toast.makeText(
                        context,
                        context.stringResource(id = R.string.screen_search_bar_empty),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    focusManager.clearFocus()
                    onSearch()
                }
            }) {
                Icon(Icons.Outlined.Search, null)
            }
        }
    }
    Column {
        MEDIA_FILTERS.subList(1,2).fastForEach { filter ->
            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(10.dp,0.dp),
                mainAxisSpacing = 8.dp,
                crossAxisSpacing = (-10).dp,
                mainAxisAlignment =  FlowMainAxisAlignment.Start,
                crossAxisAlignment = FlowCrossAxisAlignment.Start
            ) {
                filter.value.fastForEach { value ->
                    val code = "${filter.type}:$value"
                    FilterChip(
                        selected = queryParam.filters.contains(code),
                        onClick = {
                            val filters = queryParam.filters.toMutableSet()
                            if (!filters.contains(code)) {
                                filters.add(code)
                            } else {
                                filters.remove(code)
                            }
                            onChangeFiler(filters)
                            onSearch()
                        },
                        label = {
                            Text(text = (filter.type to value).filterName())
                        }
                    )
                }
            }
        }
    }
}