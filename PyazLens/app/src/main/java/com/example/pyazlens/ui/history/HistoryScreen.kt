package com.example.pyazlens.ui.history

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pyazlens.data.network.HistoryInspection
import com.example.pyazlens.data.network.RetrofitClient
import com.example.pyazlens.ui.theme.PyazLensTheme
import androidx.compose.ui.tooling.preview.Preview

private val Purple = Color(0xFF511D50)
private val Background = Color(0xFFFCFAFD)
private val Green = Color(0xFF73C943)
private val Gray = Color(0xFF8D8790)
private val BorderGray = Color(0xFFE9E5EA)
private val Red = Color(0xFFE05252)


// ============================================================
// HISTORY SCREEN
// ============================================================

@Composable
fun HistoryScreen(
    userProfileId: Long,
    onRecordClick: (Int) -> Unit,
    onDeleteRecord: (Int) -> Unit
) {

    var searchQuery by remember {
        mutableStateOf("")
    }

    var selectedFilter by remember {
        mutableStateOf("All")
    }

    var inspections by remember {
        mutableStateOf<List<HistoryInspection>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var recordToDelete by remember {
        mutableStateOf<HistoryInspection?>(null)
    }

    var isDeleting by remember {
        mutableStateOf(false)
    }

    val filterOptions = listOf(
        "All",
        "Grade A",
        "Grade URS",
        "Rejected"
    )

    // ========================================================
    // LOAD HISTORY
    // ========================================================

    LaunchedEffect(userProfileId) {

        isLoading = true
        errorMessage = null

        try {

            val response =
                RetrofitClient.api.getUserInspections(
                    userProfileId
                )

            if (response.success) {

                inspections =
                    response.inspections

            } else {

                errorMessage =
                    "Unable to load inspection history."
            }

        } catch (e: Exception) {

            e.printStackTrace()

            errorMessage =
                "Connection failed: ${e.message}"

        } finally {

            isLoading = false
        }
    }

    // ========================================================
    // SEARCH + FILTER
    // ========================================================

    val filteredList =
        inspections.filter { record ->

            val inspectionId =
                record.id.toString()

            val batchId =
                record.batch_id?.toString()
                    ?: ""

            val matchesSearch =
                inspectionId.contains(
                    searchQuery,
                    ignoreCase = true
                ) ||
                        batchId.contains(
                            searchQuery,
                            ignoreCase = true
                        )

            val grade =
                record.final_grade
                    ?.trim()
                    ?.uppercase()
                    ?: ""

            val matchesFilter =
                when (selectedFilter) {

                    "Grade A" ->
                        grade == "GRADE A"

                    "Grade URS" ->
                        grade == "GRADE URS"

                    "Rejected" ->
                        grade == "REJECT" ||
                                grade == "REJECTED"

                    else ->
                        true
                }

            matchesSearch && matchesFilter
        }

    // ========================================================
    // UI
    // ========================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 16.dp)
    ) {

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // ====================================================
        // HEADER
        // ====================================================

        Text(
            text = "History Records",
            color = Purple,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // ====================================================
        // SEARCH
        // ====================================================

        OutlinedTextField(
            value = searchQuery,

            onValueChange = {
                searchQuery = it
            },

            placeholder = {
                Text(
                    text = "Search by ID or batch..."
                )
            },

            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Purple
                )
            },

            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(16.dp),

            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Purple,
                    unfocusedBorderColor = BorderGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),

            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // ====================================================
        // FILTER CHIPS
        // ====================================================

        LazyRow(
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            items(filterOptions) { filter ->

                val isSelected =
                    selectedFilter == filter

                FilterChip(

                    selected = isSelected,

                    onClick = {
                        selectedFilter = filter
                    },

                    label = {
                        Text(
                            text = filter
                        )
                    },

                    colors =
                        FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Purple,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = Purple
                        ),

                    shape =
                        RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // ====================================================
        // LOADING
        // ====================================================

        if (isLoading) {

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),

                contentAlignment =
                    Alignment.Center
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    CircularProgressIndicator(
                        color = Green
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    Text(
                        text =
                            "Loading your inspections...",

                        color = Gray,

                        fontSize = 14.sp
                    )
                }
            }

            // ====================================================
            // ERROR
            // ====================================================

        } else if (errorMessage != null) {

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),

                contentAlignment =
                    Alignment.Center
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "⚠️",
                        fontSize = 42.sp
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            errorMessage!!,

                        color = Gray,

                        fontSize = 15.sp,

                        fontWeight =
                            FontWeight.Medium
                    )
                }
            }

            // ====================================================
            // EMPTY
            // ====================================================

        } else if (filteredList.isEmpty()) {

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),

                contentAlignment =
                    Alignment.Center
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "🔍",
                        fontSize = 42.sp
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "No inspection records found",

                        color = Gray,

                        fontSize = 16.sp,

                        fontWeight =
                            FontWeight.Medium
                    )
                }
            }

            // ====================================================
            // HISTORY LIST
            // ====================================================

        } else {

            LazyColumn(

                modifier =
                    Modifier.weight(1f),

                verticalArrangement =
                    Arrangement.spacedBy(12.dp),

                contentPadding =
                    PaddingValues(
                        bottom = 20.dp
                    )
            ) {

                items(
                    items = filteredList,
                    key = {
                        it.id
                    }
                ) { record ->

                    HistoryItemCard(

                        record = record,

                        onClick = {
                            onRecordClick(record.id)
                        },

                        onDelete = {
                            recordToDelete = record
                        }
                    )
                }
            }
        }
    }

    // ========================================================
    // DELETE CONFIRMATION
    // ========================================================

    recordToDelete?.let { record ->

        AlertDialog(

            onDismissRequest = {
                recordToDelete = null
            },

            title = {
                Text(
                    text = "Delete Inspection?"
                )
            },

            text = {
                Text(
                    text =
                        "Are you sure you want to delete inspection #${record.id}?"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        onDeleteRecord(
                            record.id
                        )

                        inspections =
                            inspections.filter {
                                it.id != record.id
                            }

                        recordToDelete = null
                    }
                ) {

                    Text(
                        text = "Delete",

                        color = Red,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        recordToDelete = null
                    }
                ) {

                    Text(
                        text = "Cancel"
                    )
                }
            }
        )
    }
}


// ============================================================
// HISTORY CARD
// ============================================================

@Composable
private fun HistoryItemCard(
    record: HistoryInspection,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {

    val grade =
        record.final_grade
            ?.trim()
            ?.uppercase()
            ?: "UNKNOWN"

    val displayGrade =
        when (grade) {

            "GRADE A" ->
                "GRADE A"

            "GRADE URS" ->
                "GRADE URS"

            "REJECT",
            "REJECTED" ->
                "REJECT"

            else ->
                grade
        }

    val gradeBackground =
        when (grade) {

            "GRADE A" ->
                Color(0xFFEAF7E5)

            "GRADE URS" ->
                Color(0xFFFFF3DD)

            else ->
                Color(0xFFFFE8E8)
        }

    val gradeColor =
        when (grade) {

            "GRADE A" ->
                Green

            "GRADE URS" ->
                Color(0xFFD89425)

            else ->
                Red
        }

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(20.dp)
                )
                .background(Color.White)
                .border(
                    width = 1.dp,
                    color = BorderGray,
                    shape =
                        RoundedCornerShape(20.dp)
                )
                .clickable {
                    onClick()
                }
                .padding(14.dp),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        // ====================================================
        // IMAGE PLACEHOLDER
        // ====================================================

        Box(
            modifier =
                Modifier
                    .size(60.dp)
                    .clip(
                        RoundedCornerShape(16.dp)
                    )
                    .background(
                        Color(0xFFF4ECE8)
                    ),

            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text = "🧅",
                fontSize = 30.sp
            )
        }

        Spacer(
            modifier =
                Modifier.width(14.dp)
        )

        // ====================================================
        // DETAILS
        // ====================================================

        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text =
                        "Inspection #${record.id}",

                    color = Purple,

                    fontSize = 16.sp,

                    fontWeight =
                        FontWeight.Bold
                )

                Box(
                    modifier =
                        Modifier
                            .clip(
                                RoundedCornerShape(8.dp)
                            )
                            .background(
                                gradeBackground
                            )
                            .padding(
                                horizontal = 8.dp,
                                vertical = 3.dp
                            )
                ) {

                    Text(
                        text =
                            displayGrade,

                        color =
                            gradeColor,

                        fontSize = 9.sp,

                        fontWeight =
                            FontWeight.ExtraBold
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            Text(
                text =
                    record.created_at
                        ?: "Date unavailable",

                color = Gray,

                fontSize = 11.sp
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text =
                        "${record.total_onions} Onions",

                    color = Purple,

                    fontSize = 12.sp,

                    fontWeight =
                        FontWeight.SemiBold
                )

                Spacer(
                    modifier =
                        Modifier.width(10.dp)
                )

                Text(
                    text =
                        "• ${record.rejected_count ?: 0} Rejected",

                    color = Red,

                    fontSize = 12.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }
        }

        // ====================================================
        // DELETE
        // ====================================================

        IconButton(
            onClick = onDelete
        ) {

            Icon(
                imageVector =
                    Icons.Default.Delete,

                contentDescription =
                    "Delete",

                tint =
                    Color(0xFFB0BEC5),

                modifier =
                    Modifier.size(20.dp)
            )
        }
    }
}



// ============================================================
// PREVIEW
// ============================================================

@Preview(showBackground = true)
@Composable
private fun HistoryScreenPreview() {

    PyazLensTheme {

        HistoryScreen(
            userProfileId = 1L,
            onRecordClick = {},
            onDeleteRecord = {}
        )
    }
}