package com.example.pyazlens.ui.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pyazlens.data.language.AppStrings
import com.example.pyazlens.data.network.HistoryInspection
import com.example.pyazlens.data.network.RetrofitClient
import com.example.pyazlens.ui.theme.PyazBackground
import com.example.pyazlens.ui.theme.PyazCardBorder
import com.example.pyazlens.ui.theme.PyazGradeA
import com.example.pyazlens.ui.theme.PyazGray
import com.example.pyazlens.ui.theme.PyazGreen
import com.example.pyazlens.ui.theme.PyazLensTheme
import com.example.pyazlens.ui.theme.PyazPurple
import com.example.pyazlens.ui.theme.PyazReject
import com.example.pyazlens.ui.theme.PyazURS
import java.text.SimpleDateFormat
import java.util.Locale
private enum class FilterState {
    NEUTRAL,
    INCLUDE,
    EXCLUDE
}
@Composable
fun HistoryScreen(
    userProfileId: Long,
    currentLanguage: String = "en",
    onRecordClick: (Int) -> Unit,
    onDeleteRecord: (Int) -> Unit
) {
    val strings = AppStrings.getStrings(currentLanguage)

    var searchQuery by remember { mutableStateOf("") }
    var filterStates by remember {
        mutableStateOf<Map<String, FilterState>>(emptyMap())
    }
    var inspections by remember { mutableStateOf<List<HistoryInspection>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var recordToDelete by remember { mutableStateOf<HistoryInspection?>(null) }

    val filterOptions = listOf(
        strings.filterGradeA,
        strings.filterGradeUrs,
        strings.filterRejected
    )

    LaunchedEffect(userProfileId) {
        isLoading = true
        errorMessage = null
        try {
            val response = RetrofitClient.api.getUserInspections(userProfileId)
            if (response.success) {
                inspections = response.inspections
            } else {
                errorMessage = strings.noHistoryFound
            }
        } catch (e: Exception) {
            e.printStackTrace()
            errorMessage = "${strings.connectionFailed}: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    val filteredList = inspections.filter { record ->
        val inspectionId = record.id.toString()
        val batchId = record.batch_id?.toString() ?: ""
        val matchesSearch = inspectionId.contains(searchQuery, ignoreCase = true) ||
                batchId.contains(searchQuery, ignoreCase = true)

        val grade = record.final_grade?.trim()?.uppercase() ?: ""
        val matchesFilter = filterOptions.all { filter ->

            val state = filterStates[filter] ?: FilterState.NEUTRAL

            val contains = when (filter) {
                strings.filterGradeA ->
                    (record.grade_a_percentage ?: 0.0) > 0.0

                strings.filterGradeUrs ->
                    (record.urs_percentage ?: 0.0) > 0.0

                strings.filterRejected ->
                    (record.rejected_percentage ?: 0.0) > 0.0

                else -> false
            }

            when (state) {
                FilterState.NEUTRAL -> true
                FilterState.INCLUDE -> contains
                FilterState.EXCLUDE -> !contains
            }
        }

        matchesSearch && matchesFilter
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PyazBackground)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = PyazPurple,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = strings.historyRecordsTitle,
                    color = PyazPurple,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF3E8F5))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "${filteredList.size} Records",
                    color = PyazPurple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SEARCH BAR
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(text = strings.searchPlaceholder, fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = PyazPurple,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = PyazGray)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PyazPurple,
                unfocusedBorderColor = PyazCardBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // FILTER CHIPS
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filterOptions) { filter ->

                val state = filterStates[filter] ?: FilterState.NEUTRAL

                val isSelected = state != FilterState.NEUTRAL

                val chipText = when (state) {
                    FilterState.NEUTRAL -> filter
                    FilterState.INCLUDE -> "✓ $filter"
                    FilterState.EXCLUDE -> "✕ $filter"
                }

                val chipColor = when (state) {
                    FilterState.NEUTRAL -> PyazPurple
                    FilterState.INCLUDE -> PyazGradeA
                    FilterState.EXCLUDE -> PyazReject
                }

                FilterChip(
                    selected = isSelected,

                    onClick = {
                        val nextState = when (state) {
                            FilterState.NEUTRAL -> FilterState.INCLUDE
                            FilterState.INCLUDE -> FilterState.EXCLUDE
                            FilterState.EXCLUDE -> FilterState.NEUTRAL
                        }

                        filterStates = if (nextState == FilterState.NEUTRAL) {
                            filterStates - filter
                        } else {
                            filterStates + (filter to nextState)
                        }
                    },

                    label = {
                        Text(
                            text = chipText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },

                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = when (state) {
                            FilterState.INCLUDE -> Color(0xFFEAF7E5)
                            FilterState.EXCLUDE -> Color(0xFFFFE8E8)
                            FilterState.NEUTRAL -> Color.White
                        },

                        selectedLabelColor = chipColor,

                        containerColor = Color.White,
                        labelColor = PyazPurple
                    ),

                    shape = RoundedCornerShape(10.dp),

                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = when (state) {
                            FilterState.INCLUDE -> PyazGradeA
                            FilterState.EXCLUDE -> PyazReject
                            FilterState.NEUTRAL -> PyazCardBorder
                        },

                        selectedBorderColor = chipColor,
                        enabled = true,
                        selected = isSelected
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // CONTENT
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(color = PyazPurple, strokeWidth = 3.dp, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = strings.loadingInspections, color = PyazGray, fontSize = 14.sp)
                }
            }
        } else if (errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "⚠️", fontSize = 40.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        color = PyazGray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🔍", fontSize = 40.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = strings.noHistoryFound,
                        color = PyazGray,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                itemsIndexed(
                    items = filteredList,
                    key = { _, record -> record.id }
                ) { _, record ->

                    val inspectionNumber =
                        inspections.size - inspections.indexOfFirst { it.id == record.id }

                    HistoryItemCard(
                        record = record,
                        inspectionNumber = inspectionNumber,
                        currentLanguage = currentLanguage,
                        onClick = { onRecordClick(record.id) },
                        onDelete = { recordToDelete = record }
                    )
                }
            }
        }
    }

    // DELETE CONFIRMATION DIALOG
    recordToDelete?.let { record ->
        AlertDialog(
            onDismissRequest = { recordToDelete = null },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            title = { Text(text = strings.deleteTitle, color = PyazPurple, fontWeight = FontWeight.Bold) },
            text = {
                val deleteText = try {
                    String.format(strings.deleteConfirmText, record.id)
                } catch (_: Exception) {
                    "Delete inspection #${record.id}?"
                }
                Text(text = deleteText, color = Color.DarkGray, fontSize = 14.sp)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteRecord(record.id)
                        inspections = inspections.filter { it.id != record.id }
                        recordToDelete = null
                    }
                ) {
                    Text(
                        text = strings.deleteBtn,
                        color = PyazReject,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { recordToDelete = null }) {
                    Text(text = strings.cancelBtn, color = PyazGray)
                }
            }
        )
    }
}

@Composable
fun HistoryItemCard(
    record: HistoryInspection,
    inspectionNumber: Int,
    currentLanguage: String = "en",
    onClick: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val strings = AppStrings.getStrings(currentLanguage)

    val grade = record.final_grade?.trim()?.uppercase() ?: "UNKNOWN"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, PyazCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF6F0F8)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🧅", fontSize = 26.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Inspection #$inspectionNumber",
                        color = PyazPurple,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = formatInspectionDate(record.created_at),
                    color = PyazGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Column {
                    val countText = try {
                        String.format(strings.onionsDetected, record.total_onions)
                    } catch (_: Exception) {
                        "${record.total_onions} Onions"
                    }

                    Text(
                        text = countText,
                        color = PyazPurple,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        // Grade A
                        Text(
                            text = "● ${record.grade_a_percentage?.toInt() ?: 0}% A",
                            color = PyazGradeA,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // URS
                        Text(
                            text = "● ${record.urs_percentage?.toInt() ?: 0}% URS",
                            color = PyazURS,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // Rejected
                        Text(
                            text = "● ${record.rejected_percentage?.toInt() ?: 0}% Reject",
                            color = PyazReject,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Actions
            Row(verticalAlignment = Alignment.CenterVertically) {

                onDelete?.let {
                    IconButton(
                        onClick = it,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = strings.deleteBtn,
                            tint = Color(0xFFD94A4A),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = PyazGray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private fun formatInspectionDate(dateString: String?): String {
    if (dateString.isNullOrBlank()) {
        return "DATE UNKNOWN"
    }

    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val outputFormat = SimpleDateFormat("dd MMM yyyy • HH:mm", Locale.US)
        val date = inputFormat.parse(dateString.substringBefore("."))
        if (date != null) {
            outputFormat.format(date).uppercase()
        } else {
            dateString
        }
    } catch (_: Exception) {
        dateString
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    PyazLensTheme {
        HistoryScreen(
            userProfileId = 1L,
            currentLanguage = "en",
            onRecordClick = {},
            onDeleteRecord = {}
        )
    }
}
