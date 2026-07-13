package com.example.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.util.Localization
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun ImmigrationGuidesScreen(
    viewModel: PermessoViewModel,
    onBack: () -> Unit,
    selectedLanguageCode: String,
    modifier: Modifier = Modifier
) {
    val activeGuideId by viewModel.activeGuideId.collectAsStateWithLifecycle()

    if (activeGuideId != null) {
        val guide = remember(activeGuideId) {
            ImmigrationGuidesCatalog.guides.firstOrNull { it.id == activeGuideId }
        }
        if (guide != null) {
            GuideDetailScreen(
                guide = guide,
                viewModel = viewModel,
                selectedLanguageCode = selectedLanguageCode,
                onBack = {
                    viewModel.clearAiResponse()
                    viewModel.activeGuideId.value = null
                }
            )
        } else {
            viewModel.activeGuideId.value = null
        }
    } else {
        GuidesListScreen(
            viewModel = viewModel,
            selectedLanguageCode = selectedLanguageCode,
            onBack = onBack
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuidesListScreen(
    viewModel: PermessoViewModel,
    selectedLanguageCode: String,
    onBack: () -> Unit
) {
    val searchQuery by viewModel.guideSearchQuery.collectAsStateWithLifecycle()
    val bookmarkedIds by viewModel.bookmarkedGuideIds.collectAsStateWithLifecycle()
    val credentials by viewModel.credentials.collectAsStateWithLifecycle()

    var selectedCategory by remember { mutableStateOf<String?>(null) }

    // Categories definition
    val categories = listOf(
        CategoryFilterItem("work", "work", "🛂", "Lavoro", "Work", "عمل"),
        CategoryFilterItem("study", "school", "🎓", "Studio", "Study", "دراسة"),
        CategoryFilterItem("long_term", "star", "🏆", "Lungo Periodo", "Long-Term", "طويل الأجل"),
        CategoryFilterItem("renewal", "refresh", "🔄", "Rinnovo", "Renewal", "تجديد"),
        CategoryFilterItem("family_reunification", "group", "👨‍👩‍👧‍👦", "Ricongiungimento", "Reunification", "لم شمل"),
        CategoryFilterItem("family_cohesion", "home", "🏠", "Coesione", "Cohesion", "تماسك أسري"),
        CategoryFilterItem("asylum", "shield", "🛡️", "Asilo", "Asylum", "لجوء"),
        CategoryFilterItem("eu_family", "public", "🇪🇺", "Familiare UE", "EU Family", "عائلة أوروبية")
    )

    // Advanced search filter (matches documents, deadlines, categories, and titles)
    val filteredGuides = remember(searchQuery, selectedCategory) {
        ImmigrationGuidesCatalog.guides.filter { guide ->
            val matchesCategory = selectedCategory == null || guide.category == selectedCategory
            val matchesSearch = if (searchQuery.isBlank()) true else {
                val q = searchQuery.lowercase()
                guide.title.get(selectedLanguageCode).lowercase().contains(q) ||
                        guide.description.get(selectedLanguageCode).lowercase().contains(q) ||
                        guide.requiredOffices.any { it.lowercase().contains(q) } ||
                        guide.documents.any { it.name.get(selectedLanguageCode).lowercase().contains(q) || it.description.get(selectedLanguageCode).lowercase().contains(q) } ||
                        guide.deadlines.any { it.period.get(selectedLanguageCode).lowercase().contains(q) || it.action.get(selectedLanguageCode).lowercase().contains(q) } ||
                        guide.category.lowercase().contains(q)
            }
            matchesCategory && matchesSearch
        }
    }

    val localCreds = credentials
    val suggestionGuide = remember(localCreds) {
        if (localCreds != null && (localCreds.numeroAssicurata.isNotBlank() || localCreds.numeroPratica.isNotBlank())) {
            ImmigrationGuidesCatalog.guides.firstOrNull { it.id == "renewal" }
        } else {
            ImmigrationGuidesCatalog.guides.firstOrNull { it.id == "work_permit" }
        }
    }

    val bookmarkedGuides = remember(bookmarkedIds) {
        ImmigrationGuidesCatalog.guides.filter { bookmarkedIds.contains(it.id) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = Localization.string("guides_screen_title", selectedLanguageCode),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("guides_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.guideSearchQuery.value = it },
                    placeholder = {
                        Text(
                            text = Localization.string("guides_search_placeholder", selectedLanguageCode),
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.guideSearchQuery.value = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("guides_search_input")
                )
            }

            // Categories horizontal filter
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null },
                            label = { Text("All") },
                            leadingIcon = {
                                Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = if (isSelected) null else cat.id },
                            label = {
                                Text(
                                    text = if (selectedLanguageCode == "it") cat.it else if (selectedLanguageCode == "ar") cat.ar else cat.en
                                )
                            },
                            leadingIcon = {
                                Text(cat.emoji, fontSize = 14.sp)
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("filter_chip_${cat.id}")
                        )
                    }
                }
            }

            // Smart Context-Aware Suggestion Banner
            if (suggestionGuide != null && searchQuery.isBlank() && selectedCategory == null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clickable { viewModel.activeGuideId.value = suggestionGuide.id }
                            .testTag("guide_suggestion_card"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TipsAndUpdates,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = Localization.string("guides_suggestions", selectedLanguageCode),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = suggestionGuide.title.get(selectedLanguageCode),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = suggestionGuide.description.get(selectedLanguageCode),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Bookmarks section
            if (bookmarkedGuides.isNotEmpty() && searchQuery.isBlank() && selectedCategory == null) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = Localization.string("guides_bookmarks_title", selectedLanguageCode),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(bookmarkedGuides) { guide ->
                                Card(
                                    modifier = Modifier
                                        .width(280.dp)
                                        .clickable { viewModel.activeGuideId.value = guide.id }
                                        .testTag("bookmark_guide_${guide.id}"),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    ),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CategoryBadge(category = guide.category, selectedLanguageCode = selectedLanguageCode)
                                            IconButton(
                                                onClick = { viewModel.toggleGuideBookmark(guide.id) },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Bookmark,
                                                    contentDescription = "Remove Bookmark",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = guide.title.get(selectedLanguageCode),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Text(
                                            text = guide.description.get(selectedLanguageCode),
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // All Guides section header
            item {
                Text(
                    text = Localization.string("guides_all_title", selectedLanguageCode),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Guides matching results
            if (filteredGuides.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FindInPage,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = Localization.string("guides_no_results", selectedLanguageCode),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(filteredGuides) { guide ->
                    val isBookmarked = bookmarkedIds.contains(guide.id)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clickable { viewModel.activeGuideId.value = guide.id }
                            .testTag("guide_card_${guide.id}"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CategoryBadge(category = guide.category, selectedLanguageCode = selectedLanguageCode)
                                IconButton(
                                    onClick = { viewModel.toggleGuideBookmark(guide.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = "Bookmark",
                                        tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                    )
                                }
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = guide.title.get(selectedLanguageCode),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = guide.description.get(selectedLanguageCode),
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                MetaItem(label = "Difficulty", value = guide.difficulty.get(selectedLanguageCode), icon = Icons.Default.BarChart)
                                MetaItem(label = "Duration", value = guide.estimatedDuration.get(selectedLanguageCode), icon = Icons.Default.Schedule)
                                MetaItem(label = "Cost", value = guide.estimatedCosts.get(selectedLanguageCode), icon = Icons.Default.Payments)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideDetailScreen(
    guide: ImmigrationGuide,
    viewModel: PermessoViewModel,
    selectedLanguageCode: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val bookmarkedIds by viewModel.bookmarkedGuideIds.collectAsStateWithLifecycle()
    val isBookmarked = bookmarkedIds.contains(guide.id)

    // Interactive checklist logic synced with database
    val dbChecklistStates by viewModel.getGuideChecklistStates(guide.id).collectAsStateWithLifecycle(initialValue = emptyList())
    val checkedDocIds = remember(dbChecklistStates) {
        dbChecklistStates.filter { it.isChecked }.map { it.documentId }.toSet()
    }

    val totalDocs = guide.documents.size
    val completedDocs = checkedDocIds.size
    val progress = if (totalDocs > 0) completedDocs.toFloat() / totalDocs else 0f

    // Smart Recommendations: Related Guides
    val relatedGuides = remember(guide) {
        ImmigrationGuidesCatalog.guides.filter { guide.recommendations.contains(it.id) }
    }

    // 1. Next Action Calculation
    val nextDoc = remember(guide.documents, checkedDocIds) {
        guide.documents.firstOrNull { !checkedDocIds.contains(it.id) }
    }

    // 2. Province Selector States
    val provinces = listOf("Milano", "Roma", "Torino", "Bologna", "Napoli", "Firenze", "Venezia", "Palermo")
    val selectedProvince by viewModel.selectedProvince.collectAsStateWithLifecycle()
    var provinceMenuExpanded by remember { mutableStateOf(false) }

    // 3. Ask AI States
    val aiResponse by viewModel.aiResponse.collectAsStateWithLifecycle()
    val aiLoading by viewModel.aiLoading.collectAsStateWithLifecycle()
    var userQuery by remember { mutableStateOf("") }

    // JSON formatted guide info for AI context injection
    val guideJson = remember(guide) {
        JSONObject().apply {
            put("title", guide.title.get("en"))
            put("category", guide.category)
            put("overview", guide.overview.whoIsItFor.get("en"))
            put("requirements", JSONArray(guide.overview.mainRequirements.map { it.get("en") }))
            put("documents", JSONArray(guide.documents.map { "${it.name.get("en")}: ${it.description.get("en")}" }))
            put("timeline", JSONArray(guide.timeline.map { "${it.stepNumber}. ${it.title.get("en")}: ${it.description.get("en")}" }))
            put("faqs", JSONArray(guide.faqs.map { "${it.question.get("en")} -> ${it.answer.get("en")}" }))
        }.toString()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = guide.title.get(selectedLanguageCode),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleGuideBookmark(guide.id) }) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 48.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Hero Gradient Banner & Trust Badges
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.tertiaryContainer
                                    )
                                )
                            )
                            .padding(20.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            CategoryBadge(category = guide.category, selectedLanguageCode = selectedLanguageCode)
                            Text(
                                text = guide.title.get(selectedLanguageCode),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Trust indicators row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TrustIndicatorChip(text = Localization.string("guides_trust_verified", selectedLanguageCode), icon = Icons.Default.Verified)
                        TrustIndicatorChip(text = Localization.string("guides_trust_official", selectedLanguageCode), icon = Icons.Default.Gavel)
                        TrustIndicatorChip(text = Localization.string("guides_trust_updated", selectedLanguageCode), icon = Icons.Default.Refresh)
                    }
                }
            }

            // Guide Versioning Info block
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Text(Localization.string("guides_last_reviewed", selectedLanguageCode), fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                            Text(guide.lastReviewed.get(selectedLanguageCode), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(MaterialTheme.colorScheme.outlineVariant))
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Text(Localization.string("guides_version", selectedLanguageCode), fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                            Text(guide.guideVersion, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(MaterialTheme.colorScheme.outlineVariant))
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Text(Localization.string("guides_last_updated", selectedLanguageCode), fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                            Text(guide.lastUpdated.get(selectedLanguageCode), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Province Selector & Local Notice block
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = Localization.string("guides_select_province_guide", selectedLanguageCode),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Box {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                                .clickable { provinceMenuExpanded = true }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = selectedProvince, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                        }

                        DropdownMenu(
                            expanded = provinceMenuExpanded,
                            onDismissRequest = { provinceMenuExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            provinces.forEach { prov ->
                                DropdownMenuItem(
                                    text = { Text(prov, fontWeight = if (prov == selectedProvince) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = {
                                        viewModel.selectProvince(prov)
                                        provinceMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Local note or standard procedure notice
                    val activeNote = remember(guide, selectedProvince) {
                        guide.provinceNotes.firstOrNull { it.provinceCode.equals(selectedProvince, ignoreCase = true) }
                    }

                    if (activeNote != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.15f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                                Column {
                                    Text(
                                        text = "${Localization.string("guides_local_notice", selectedLanguageCode)}: $selectedProvince",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = activeNote.note.get(selectedLanguageCode),
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline
                                )
                                Text(
                                    text = Localization.string("guides_standard_procedure", selectedLanguageCode),
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Metadata Grid Card with Difficulty & Period
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.weight(1f)) {
                                val difficultyVal = guide.difficulty.get(selectedLanguageCode)
                                val diffColor = when {
                                    difficultyVal.lowercase() in listOf("facile", "easy") -> Color(0xFF4CAF50)
                                    difficultyVal.lowercase() in listOf("medio", "medium", "moyen") -> Color(0xFFFF9800)
                                    else -> Color(0xFFF44336)
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.BarChart, contentDescription = null, tint = diffColor)
                                    Column {
                                        Text(text = "Difficulty", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                                        Text(text = difficultyVal, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = diffColor)
                                    }
                                }
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                MetaItem(label = "Duration", value = guide.estimatedDuration.get(selectedLanguageCode), icon = Icons.Default.Schedule)
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.weight(1f)) {
                                MetaItem(label = "Costs", value = guide.estimatedCosts.get(selectedLanguageCode), icon = Icons.Default.Payments)
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                MetaItem(label = "Validity", value = guide.validityPeriod.get(selectedLanguageCode), icon = Icons.Default.VerifiedUser)
                            }
                        }
                    }
                }
            }

            // Interactive Next Action Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (nextDoc != null) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f)
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (nextDoc != null) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        else MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (nextDoc != null) Icons.Default.PlayArrow else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (nextDoc != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                                )
                                Text(
                                    text = Localization.string("guides_next_action", selectedLanguageCode),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (nextDoc != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }

                            Text(
                                text = "${completedDocs}/${totalDocs} (${(progress * 100).toInt()}%)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        if (nextDoc != null) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = nextDoc.name.get(selectedLanguageCode),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = nextDoc.description.get(selectedLanguageCode),
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Button(
                                onClick = { viewModel.toggleGuideDocument(guide.id, nextDoc.id, true) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("next_action_complete_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = Localization.string("guides_what_to_do", selectedLanguageCode), fontSize = 13.sp)
                            }
                        } else {
                            Text(
                                text = Localization.string("guides_success_completed", selectedLanguageCode),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            }

            // Guide Warnings & Legal alerts block
            if (guide.warnings.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        guide.warnings.forEach { warning ->
                            val isDanger = warning.severity == "danger"
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isDanger) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (isDanger) MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
                                    else MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = if (isDanger) Icons.Default.Error else Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = if (isDanger) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                                    )
                                    Column {
                                        Text(
                                            text = warning.title.get(selectedLanguageCode),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDanger) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = warning.description.get(selectedLanguageCode),
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 1: Overview
            item {
                SectionCard(
                    title = Localization.string("guides_sec_overview", selectedLanguageCode),
                    icon = Icons.Default.Info
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OverviewBlock(
                            title = "Who it is for",
                            text = guide.overview.whoIsItFor.get(selectedLanguageCode)
                        )
                        OverviewBlock(
                            title = "Who can apply",
                            text = guide.overview.whoCanApply.get(selectedLanguageCode)
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Core Requirements:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        guide.overview.mainRequirements.forEach { req ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("•", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(
                                    text = req.get(selectedLanguageCode),
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // SECTION 2: Interactive Next Steps Timeline with metrics card
            item {
                val timelineDetails = guide.estimatedTimelineDetails
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SectionCard(
                        title = Localization.string("guides_sec_timeline", selectedLanguageCode),
                        icon = Icons.Default.Timeline
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            guide.timeline.forEachIndexed { idx, step ->
                                TimelineItemRow(
                                    step = step,
                                    isLast = idx == guide.timeline.size - 1,
                                    selectedLanguageCode = selectedLanguageCode
                                )
                            }
                        }
                    }

                    // Visual Timeline Metrics card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.AccessTime, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Text(Localization.string("guides_duration", selectedLanguageCode), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(Localization.string("guides_timeline_average", selectedLanguageCode), fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                Text(timelineDetails.averageTime.get(selectedLanguageCode), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(Localization.string("guides_timeline_fast", selectedLanguageCode), fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                Text(timelineDetails.fastTrackOption.get(selectedLanguageCode), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(Localization.string("guides_timeline_delays", selectedLanguageCode), fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                timelineDetails.delayReasons.forEach { reason ->
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.Top) {
                                        Text("•", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                                        Text(reason.get(selectedLanguageCode), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            // Gauge relative speed bar
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(Localization.string("guides_timeline_relative", selectedLanguageCode), fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)

                                val barColor = when {
                                    timelineDetails.relativeSpeedPercent < 0.4f -> Color(0xFF4CAF50) // Fast (Green)
                                    timelineDetails.relativeSpeedPercent < 0.7f -> Color(0xFFFF9800) // Moderate (Orange)
                                    else -> Color(0xFFF44336) // Slow (Red)
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(timelineDetails.relativeSpeedPercent)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(barColor)
                                    )
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Fast", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                                    Text("Medium", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                                    Text("Slow", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 3: Required Documents (checklist with DB syncing)
            item {
                SectionCard(
                    title = Localization.string("guides_sec_documents", selectedLanguageCode),
                    icon = Icons.Default.FactCheck
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Completion: $completedDocs of $totalDocs",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${(progress * 100).toInt()}%",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primaryContainer
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        guide.documents.forEach { doc ->
                            val isChecked = checkedDocIds.contains(doc.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isChecked) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                                        else Color.Transparent
                                    )
                                    .clickable {
                                        viewModel.toggleGuideDocument(guide.id, doc.id, !isChecked)
                                    }
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = {
                                        viewModel.toggleGuideDocument(guide.id, doc.id, it)
                                    },
                                    modifier = Modifier.testTag("checkbox_${doc.id}")
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = doc.name.get(selectedLanguageCode),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = doc.description.get(selectedLanguageCode),
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 4: Ask AI Assistant Chat Interface
            item {
                SectionCard(
                    title = Localization.string("guides_ask_ai", selectedLanguageCode),
                    icon = Icons.Default.AutoAwesome
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = Localization.string("guides_ask_ai_desc", selectedLanguageCode),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // AI prompt chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(guide.aiPrompts) { prompt ->
                                SuggestionChip(
                                    onClick = {
                                        userQuery = prompt.get(selectedLanguageCode)
                                        viewModel.askGuideAssistant(userQuery, guideJson)
                                    },
                                    label = { Text(prompt.get(selectedLanguageCode), fontSize = 11.sp) },
                                    modifier = Modifier.testTag("ai_prompt_chip_${prompt.get("en").take(15)}")
                                )
                            }
                        }

                        // Query Input Field
                        OutlinedTextField(
                            value = userQuery,
                            onValueChange = { userQuery = it },
                            placeholder = { Text(Localization.string("guides_ask_ai_placeholder", selectedLanguageCode), fontSize = 13.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ai_query_input"),
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        if (userQuery.isNotBlank() && !aiLoading) {
                                            viewModel.askGuideAssistant(userQuery, guideJson)
                                        }
                                    },
                                    enabled = userQuery.isNotBlank() && !aiLoading
                                ) {
                                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.primary)
                                }
                            },
                            singleLine = true
                        )

                        // Loading & Response card
                        if (aiLoading) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                    Text(text = Localization.string("guides_ask_ai_loading", selectedLanguageCode), fontSize = 13.sp)
                                }
                            }
                        } else if (aiResponse != null) {
                            val responseText = aiResponse ?: ""
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "Soggiorno AI 🤖", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            IconButton(
                                                onClick = {
                                                    clipboardManager.setText(AnnotatedString(responseText))
                                                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                                            }
                                            IconButton(onClick = { viewModel.clearAiResponse() }, modifier = Modifier.size(24.dp)) {
                                                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                    Text(text = responseText, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 5: Important Deadlines
            item {
                SectionCard(
                    title = Localization.string("guides_sec_deadlines", selectedLanguageCode),
                    icon = Icons.Default.Warning
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        guide.deadlines.forEach { deadline ->
                            val isDanger = deadline.severity == "danger"
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isDanger) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (isDanger) MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
                                    else MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isDanger) Icons.Default.Error else Icons.Default.Info,
                                        contentDescription = null,
                                        tint = if (isDanger) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                                    )
                                    Column {
                                        Text(
                                            text = deadline.period.get(selectedLanguageCode),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDanger) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                        Text(
                                            text = deadline.action.get(selectedLanguageCode),
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 6: Where to Start
            item {
                SectionCard(
                    title = Localization.string("guides_sec_where_start", selectedLanguageCode),
                    icon = Icons.Default.Storefront
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = guide.whereToStart.mainOffice.get(selectedLanguageCode),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = guide.whereToStart.explanation.get(selectedLanguageCode),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // SECTION 7: Cost Breakdown
            item {
                SectionCard(
                    title = Localization.string("guides_sec_costs", selectedLanguageCode),
                    icon = Icons.Default.Receipt
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        guide.costBreakdown.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.name.get(selectedLanguageCode),
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = item.amount,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // SECTION 8: Common Mistakes
            item {
                SectionCard(
                    title = Localization.string("guides_sec_mistakes", selectedLanguageCode),
                    icon = Icons.Default.Gavel
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        guide.commonMistakes.forEach { mistake ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.05f)),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Cancel, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                        Text(
                                            text = mistake.title.get(selectedLanguageCode),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                    Text(
                                        text = mistake.explanation.get(selectedLanguageCode),
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 9: Legislative references block (Expandable)
            item {
                SectionCard(
                    title = Localization.string("guides_legal_references", selectedLanguageCode),
                    icon = Icons.Default.Gavel
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        guide.legalReferences.forEach { legal ->
                            var expanded by remember { mutableStateOf(false) }
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expanded = !expanded },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = legal.law.get(selectedLanguageCode),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = legal.article.get(selectedLanguageCode),
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Icon(
                                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.outline
                                        )
                                    }

                                    AnimatedVisibility(
                                        visible = expanded,
                                        enter = fadeIn() + expandVertically(),
                                        exit = fadeOut() + shrinkVertically()
                                    ) {
                                        Column {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "Circolare / Circular: ${legal.circular.get(selectedLanguageCode)}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = legal.explanation.get(selectedLanguageCode),
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 10: Community Tips Section with Thumbs-up voting
            item {
                SectionCard(
                    title = Localization.string("guides_community_tips", selectedLanguageCode),
                    icon = Icons.Default.Group
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        guide.communityTips.forEach { tip ->
                            var helpfulCount by remember { mutableStateOf(tip.initialHelpfulCount) }
                            var isVoted by remember { mutableStateOf(false) }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                            Text(
                                                text = tip.author.get(selectedLanguageCode),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        // Vote helpful button
                                        IconButton(
                                            onClick = {
                                                if (!isVoted) {
                                                    helpfulCount++
                                                    isVoted = true
                                                }
                                            },
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (isVoted) Icons.Default.ThumbUp else Icons.Default.ThumbUp,
                                                    contentDescription = "Upvote",
                                                    tint = if (isVoted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Text(
                                                    text = Localization.string("guides_helpful_votes", selectedLanguageCode).format(helpfulCount),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isVoted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        text = tip.content.get(selectedLanguageCode),
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 11: Frequently Asked Questions (accordion layout)
            item {
                SectionCard(
                    title = Localization.string("guides_sec_faqs", selectedLanguageCode),
                    icon = Icons.Default.QuestionAnswer
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        guide.faqs.forEach { faq ->
                            var expanded by remember { mutableStateOf(false) }
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expanded = !expanded },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = faq.question.get(selectedLanguageCode),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Icon(
                                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.outline
                                        )
                                    }

                                    AnimatedVisibility(
                                        visible = expanded,
                                        enter = fadeIn() + expandVertically(),
                                        exit = fadeOut() + shrinkVertically()
                                    ) {
                                        Column {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = faq.answer.get(selectedLanguageCode),
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 12: Helpful Contacts
            item {
                SectionCard(
                    title = Localization.string("guides_sec_contacts", selectedLanguageCode),
                    icon = Icons.Default.ContactPhone
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        guide.helpfulContacts.forEach { contact ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = contact.name.get(selectedLanguageCode),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = contact.value,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (contact.type == "phone") {
                                        IconButton(
                                            onClick = {
                                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.value}"))
                                                context.startActivity(intent)
                                            },
                                            colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                        ) {
                                            Icon(Icons.Default.Phone, contentDescription = "Call", tint = MaterialTheme.colorScheme.primary)
                                        }
                                    } else if (contact.type == "web") {
                                        IconButton(
                                            onClick = {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(contact.value))
                                                context.startActivity(intent)
                                            },
                                            colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                        ) {
                                            Icon(Icons.Default.OpenInNew, contentDescription = "Website", tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 13: Official Resources & Clickable Official Badges
            item {
                SectionCard(
                    title = Localization.string("guides_sec_resources", selectedLanguageCode),
                    icon = Icons.Default.Launch
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        guide.officialResources.forEach { res ->
                            val agencyType = res.agency ?: "Official"
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(res.url))
                                        context.startActivity(intent)
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Link, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Column {
                                            Text(
                                                text = res.name.get(selectedLanguageCode),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = res.url,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.outline,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }

                                    // Professional Agency badge
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.secondaryContainer)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = agencyType,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Smart Recommendations: Related Guides
            if (relatedGuides.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = Localization.string("guides_related_guides", selectedLanguageCode),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(relatedGuides) { rGuide ->
                                Card(
                                    modifier = Modifier
                                        .width(260.dp)
                                        .clickable { viewModel.activeGuideId.value = rGuide.id }
                                        .testTag("related_guide_${rGuide.id}"),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        CategoryBadge(category = rGuide.category, selectedLanguageCode = selectedLanguageCode)
                                        Text(text = rGuide.title.get(selectedLanguageCode), fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(text = rGuide.description.get(selectedLanguageCode), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // MANDATORY LEGAL DISCLAIMER
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Text(
                        text = Localization.string("guides_disclaimer", selectedLanguageCode),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(14.dp),
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}

@Composable
fun TrustIndicatorChip(
    text: String,
    icon: ImageVector
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(12.dp))
            Text(
                text = text,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun SectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            content()
        }
    }
}

@Composable
fun TimelineItemRow(
    step: GuideTimelineStep,
    isLast: Boolean,
    selectedLanguageCode: String
) {
    val timelineColor = MaterialTheme.colorScheme.primary
    val dividerColor = MaterialTheme.colorScheme.outlineVariant

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Left timeline column with node and line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(36.dp)
        ) {
            // Circle node
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(timelineColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = step.stepNumber.toString(),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Connection line
            if (!isLast) {
                Canvas(
                    modifier = Modifier
                        .width(2.dp)
                        .height(60.dp)
                ) {
                    drawLine(
                        color = dividerColor,
                        start = Offset(size.width / 2, 0f),
                        end = Offset(size.width / 2, size.height),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Right content column
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 20.dp)
        ) {
            Text(
                text = step.title.get(selectedLanguageCode),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = step.description.get(selectedLanguageCode),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CategoryBadge(
    category: String,
    selectedLanguageCode: String
) {
    val text = when (category) {
        "work" -> "Work"
        "study" -> "Study"
        "family" -> "Family"
        "long_term" -> "Long-Term"
        "renewal" -> "Renewal"
        "family_reunification" -> "Reunification"
        "family_cohesion" -> "Cohesion"
        "asylum" -> "Asylum"
        "eu_family" -> "EU Family"
        else -> category.replaceFirstChar { it.uppercase() }
    }
    val containerColor = when (category) {
        "work" -> MaterialTheme.colorScheme.primaryContainer
        "study" -> MaterialTheme.colorScheme.secondaryContainer
        "family", "family_reunification", "family_cohesion", "eu_family" -> MaterialTheme.colorScheme.tertiaryContainer
        "asylum" -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when (category) {
        "work" -> MaterialTheme.colorScheme.onPrimaryContainer
        "study" -> MaterialTheme.colorScheme.onSecondaryContainer
        "family", "family_reunification", "family_cohesion", "eu_family" -> MaterialTheme.colorScheme.onTertiaryContainer
        "asylum" -> MaterialTheme.colorScheme.onErrorContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}

@Composable
fun MetaItem(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
        Column {
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun OverviewBlock(
    title: String,
    text: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = text,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

data class CategoryFilterItem(
    val id: String,
    val iconName: String,
    val emoji: String,
    val it: String,
    val en: String,
    val ar: String
)
