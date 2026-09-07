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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import com.example.pyazlens.ui.theme.PyazLensTheme

private val Purple = Color(0xFF511D50)
private val Background = Color(0xFFFCFAFD)
private val Green = Color(0xFF73C943)
private val Gray = Color(0xFF8D8790)
private val BorderGray = Color(0xFFE9E5EA)
private val Red = Color(0xFFE05252)


// ----------------------------------------------------
// Dummy data model
// ----------------------------------------------------

private data class DummyInspection(
    val id: String,
    val title: String,
    val date: String,
    val onionCount: Int,
    val qualityScore: Int,
    val grade: String,
    val defect: String
)


// ----------------------------------------------------
// Dummy inspection data
// ----------------------------------------------------

private val dummyInspections = listOf(

    DummyInspection(
        id = "142",
        title = "Red Onion #142",
        date = "Today, 10:24 AM",
        onionCount = 7,
        qualityScore = 94,
        grade = "GRADE A",
        defect = "No Defect"
    ),

    DummyInspection(
        id = "141",
        title = "Red Onion #141",
        date = "Today, 09:15 AM",
        onionCount = 12,
        qualityScore = 91,
        grade = "GRADE A",
        defect = "Sprouted"
    ),

    DummyInspection(
        id = "140",
        title = "Red Onion #140",
        date = "Yesterday, 05:42 PM",
        onionCount = 9,
        qualityScore = 82,
        grade = "GRADE B",
        defect = "Cut / Crack"
    ),

    DummyInspection(
        id = "139",
        title = "Red Onion #139",
        date = "Yesterday, 02:18 PM",
        onionCount = 15,
        qualityScore = 76,
        grade = "GRADE B",
        defect = "Misshapen"
    ),

    DummyInspection(
        id = "138",
        title = "Red Onion #138",
        date = "10 Sep 2026, 11:30 AM",
        onionCount = 8,
        qualityScore = 61,
        grade = "GRADE C",
        defect = "Rotten"
    )
)


// ----------------------------------------------------
// History Screen
// ----------------------------------------------------

@Composable
fun HistoryScreen(
    onRecordClick: () -> Unit,
    onDeleteRecord: (String) -> Unit
) {

    var searchQuery by remember {
        mutableStateOf("")
    }

    var selectedFilter by remember {
        mutableStateOf("All")
    }

    var inspections by remember {
        mutableStateOf(dummyInspections)
    }

    var recordToDelete by remember {
        mutableStateOf<DummyInspection?>(null)
    }

    val filterOptions = listOf(
        "All",
        "Grade A",
        "Grade B",
        "Grade C",
        "Defective"
    )

    // ------------------------------------------------
    // Search + filter
    // ------------------------------------------------

    val filteredList = inspections.filter { record ->

        val matchesSearch =
            record.title.contains(
                searchQuery,
                ignoreCase = true
            ) ||
                    record.id.contains(
                        searchQuery,
                        ignoreCase = true
                    )

        val matchesFilter = when (selectedFilter) {

            "Grade A" ->
                record.grade == "GRADE A"

            "Grade B" ->
                record.grade == "GRADE B"

            "Grade C" ->
                record.grade == "GRADE C"

            "Defective" ->
                record.defect != "No Defect"

            else ->
                true
        }

        matchesSearch && matchesFilter
    }


    // ------------------------------------------------
    // UI
    // ------------------------------------------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 16.dp)
    ) {

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // Header
        Text(
            text = "History Records",
            color = Purple,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )


        // Search
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
            },
            placeholder = {
                Text(
                    text = "Search by ID or batch name..."
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Purple
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
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


        // Filter chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
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

                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Purple,
                        selectedLabelColor = Color.White,
                        containerColor = Color.White,
                        labelColor = Purple
                    ),

                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )


        // ------------------------------------------------
        // Empty state
        // ------------------------------------------------

        if (filteredList.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),

                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "🔍",
                        fontSize = 42.sp
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "No inspection records found",
                        color = Gray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

        } else {

            // ------------------------------------------------
            // History list
            // ------------------------------------------------

            LazyColumn(
                modifier = Modifier.weight(1f),

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
                            onRecordClick()
                        },

                        onDelete = {
                            recordToDelete = record
                        }
                    )
                }
            }
        }
    }


    // ----------------------------------------------------
    // Delete confirmation dialog
    // ----------------------------------------------------

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
                    text = "Are you sure you want to delete ${record.title}?"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        inspections =
                            inspections.filter {
                                it.id != record.id
                            }

                        onDeleteRecord(record.id)

                        recordToDelete = null
                    }
                ) {

                    Text(
                        text = "Delete",
                        color = Red,
                        fontWeight = FontWeight.Bold
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


// ----------------------------------------------------
// History Card
// ----------------------------------------------------

@Composable
private fun HistoryItemCard(
    record: DummyInspection,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(20.dp)
            )
            .background(Color.White)
            .border(
                width = 1.dp,
                color = BorderGray,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable {
                onClick()
            }
            .padding(14.dp),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        // Onion image placeholder
        Box(
            modifier = Modifier
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
            modifier = Modifier.width(14.dp)
        )


        Column(
            modifier = Modifier.weight(1f)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = record.title,
                    color = Purple,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )


                // Grade
                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(8.dp)
                        )
                        .background(
                            when (record.grade) {

                                "GRADE A" ->
                                    Color(0xFFEAF7E5)

                                "GRADE B" ->
                                    Color(0xFFFFF3DD)

                                else ->
                                    Color(0xFFFFE8E8)
                            }
                        )
                        .padding(
                            horizontal = 8.dp,
                            vertical = 3.dp
                        )
                ) {

                    Text(
                        text = record.grade,

                        color = when (record.grade) {

                            "GRADE A" ->
                                Green

                            "GRADE B" ->
                                Color(0xFFD89425)

                            else ->
                                Red
                        },

                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }


            Spacer(
                modifier = Modifier.height(4.dp)
            )


            Text(
                text = record.date,
                color = Gray,
                fontSize = 11.sp
            )


            Spacer(
                modifier = Modifier.height(6.dp)
            )


            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = "${record.onionCount} Onions",
                    color = Purple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(
                    modifier = Modifier.width(10.dp)
                )

                Text(
                    text = "• ${record.qualityScore}/100",
                    color = Green,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = record.defect,
                    color = Gray,
                    fontSize = 11.sp
                )
            }
        }


        // Delete
        IconButton(
            onClick = onDelete
        ) {

            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color(0xFFB0BEC5),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryScreenPreview() {
    PyazLensTheme {
        HistoryScreen(
            onRecordClick = {},
            onDeleteRecord = {}
        )
    }
}