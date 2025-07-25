package com.diploma.work.ui.feature.articles

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.diploma.work.data.models.*
import com.diploma.work.ui.components.ErrorCard
import com.diploma.work.ui.components.LoadingCard
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticlesScreen(
    modifier: Modifier = Modifier,
    onOpenDrawer: () -> Unit = {},
    viewModel: ArticlesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showFilters by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.refreshUserPreferences()
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Articles") },
            navigationIcon = {
                IconButton(onClick = onOpenDrawer) {
                    Icon(Icons.Default.Menu, contentDescription = "Open menu")
                }
            },
            actions = {                IconButton(onClick = { showFilters = !showFilters }) {
                    Icon(
                        Icons.Default.Tune,
                        contentDescription = "Filters",
                        tint = if (uiState.searchQuery.isNotBlank() ||
                                  uiState.selectedTimePeriod != TimePeriod.WEEK ||
                                  uiState.selectedSortType != SortType.RELEVANCE) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }
        )

        SearchBar(
            query = uiState.searchQuery,
            onQueryChange = viewModel::setSearchQuery,
            onSearch = { viewModel.loadArticles() },
            placeholder = "Search articles...")
        if (showFilters) {
            FiltersPanel(
                uiState = uiState,
                onTimePeriodChange = viewModel::setTimePeriod,
                onSortByChange = viewModel::setSortBy,
                onSourceChange = viewModel::toggleSource,
                onClearFilters = viewModel::clearFilters,
                onApplyUserPreferences = viewModel::applyUserPreferencesToFilters
            )
        }
        
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refreshArticles() },
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                uiState.isLoading && uiState.articles.isEmpty() -> {
                    LoadingCard(
                        message = "Loading articles...",
                        modifier = Modifier.fillMaxSize()
                    )
                }            uiState.error != null -> {
                    ErrorCard(
                        error = uiState.error!!,
                        onRetry = { viewModel.loadArticles() }
                    )
                }uiState.articles.isEmpty() -> {
                    EmptyStateCard()
                }            else -> {
                    ArticlesList(
                        articles = uiState.articles,
                        onArticleClick = { /* TODO: Navigate to article detail */ },
                        onLoadMore = { viewModel.loadMoreArticles() },
                        isLoading = uiState.isLoading,
                        hasMore = uiState.hasMore
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search")
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        singleLine = true
    )
}

@Composable
private fun FiltersPanel(
    uiState: ArticlesUiState,
    onTimePeriodChange: (TimePeriod) -> Unit,
    onSortByChange: (SortType) -> Unit,
    onSourceChange: (String) -> Unit,
    onClearFilters: () -> Unit,
    onApplyUserPreferences: () -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Filters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row {                    TextButton(onClick = onApplyUserPreferences) {
                        Text("My Interests")
                    }
                    TextButton(onClick = onClearFilters) {
                        Text("Clear All")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            FilterSection(title = "Time Period") {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(TimePeriod.entries) { period ->
                        FilterChip(
                            selected = uiState.selectedTimePeriod == period,
                            onClick = { onTimePeriodChange(period) },
                            label = { Text(period.displayName) }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            FilterSection(title = "Sort By") {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(SortType.entries) { sortType ->
                        FilterChip(
                            selected = uiState.selectedSortType == sortType,
                            onClick = { onSortByChange(sortType) },
                            label = { Text(sortType.displayName) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun ArticlesList(
    articles: List<Article>,
    onArticleClick: (Article) -> Unit,
    onLoadMore: () -> Unit = {},
    isLoading: Boolean = false,
    hasMore: Boolean = true
) {
    val listState = rememberLazyListState()
    
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val lastVisibleItem = visibleItems.lastOrNull()
                if (lastVisibleItem != null && 
                    lastVisibleItem.index >= articles.size - 5 &&
                    hasMore && 
                    !isLoading) {
                    onLoadMore()
                }
            }
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(articles) { article ->
            ArticleCard(
                article = article,
                onClick = { onArticleClick(article) }
            )
        }
        
        if (isLoading && articles.isNotEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun ArticleCard(
    article: Article,
    onClick: () -> Unit
) {
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                if (article.url.isNotEmpty()) {
                    uriHandler.openUri(article.url)
                } else {
                    onClick()
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = article.rssSourceName,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (article.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = article.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = article.publishedAt
                        .atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("MMM dd")),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EmptyStateCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Article,
                contentDescription = "No articles",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No articles found",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Try adjusting your filters or check back later for new content",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private val TimePeriod.displayName: String
    get() = when (this) {
        TimePeriod.DAY -> "24 Hours"
        TimePeriod.WEEK -> "Week"
        TimePeriod.MONTH -> "Month"
        TimePeriod.QUARTER -> "3 Months"
        TimePeriod.YEAR -> "Year"
        TimePeriod.ALL -> "All Time"
    }

private val SortType.displayName: String
    get() = when (this) {
        SortType.RELEVANCE -> "Relevance"
        SortType.DATE_DESC -> "Newest First"
        SortType.DATE_ASC -> "Oldest First"
        SortType.RATING_DESC -> "Highest Rated"
        SortType.RATING_ASC -> "Lowest Rated"
    }

private val ArticleDirection.displayName: String
    get() = when (this) {
        ArticleDirection.BACKEND -> "Backend"
        ArticleDirection.FRONTEND -> "Frontend"
        ArticleDirection.DEVOPS -> "DevOps"
        ArticleDirection.DATA_SCIENCE -> "Data Science"
        ArticleDirection.UNSPECIFIED -> "All"
    }


