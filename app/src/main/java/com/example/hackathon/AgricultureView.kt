package com.example.hackathon

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AgricultureView(mainViewModel: MainViewModel, dataViewModel: DataViewModel) {
    val context = LocalContext.current

    var dataSets by remember { mutableStateOf<List<ILineDataSet>>(emptyList()) }
    var startYear by remember { mutableStateOf("") }
    var endYear by remember { mutableStateOf("") }
    var yearLabels by remember { mutableStateOf<List<String>>(emptyList()) }

    // State variables for annotation functionality
    var isAnnotateVisible by remember { mutableStateOf(false) }
    var annotationTextState by remember { mutableStateOf("") }
    val annotationText by dataViewModel.agriAnnotationTextFlow.collectAsState(initial = "")


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Agriculture (${mainViewModel.selectedCountry})")

        Spacer(modifier = Modifier.height(16.dp))

        // Start year input
        TextField(
            value = startYear,
            onValueChange = { startYear = it },
            label = { Text("Start Year") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // End year input
        TextField(
            value = endYear,
            onValueChange = { endYear = it },
            label = { Text("End Year") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(0.8f),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            // Checkbox options for selecting data to display
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start, modifier = Modifier.padding(10.dp)) {
                Checkbox(
                    checked = mainViewModel.agricultureGrowthChecked,
                    onCheckedChange = { mainViewModel.agricultureGrowthChecked = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Agriculture (% of Growth)")
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start, modifier = Modifier.padding(10.dp)) {
                Checkbox(
                    checked = mainViewModel.agricultureContributionChecked,
                    onCheckedChange = { mainViewModel.agricultureContributionChecked = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Agriculture Contribution (% of GDP)")
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start, modifier = Modifier.padding(10.dp)) {
                Checkbox(
                    checked = mainViewModel.fertilizerPercentChecked,
                    onCheckedChange = { mainViewModel.fertilizerPercentChecked = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Fertilizer Consumption (% of production)")
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start, modifier = Modifier.padding(10.dp)) {
                Checkbox(
                    checked = mainViewModel.fertilizerKgChecked,
                    onCheckedChange = { mainViewModel.fertilizerKgChecked = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Fertilizer Consumption (Kg per arable land)")
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start, modifier = Modifier.padding(10.dp)) {
                Checkbox(
                    checked = mainViewModel.manufacturingChecked,
                    onCheckedChange = { mainViewModel.manufacturingChecked = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Manufacturing (% of GDP)")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to generate the graph
        Button(onClick = {
            val startYearFloat = startYear.toFloatOrNull()
            val endYearFloat = endYear.toFloatOrNull()

            // Clear existing datasets
            dataSets = emptyList()

            // Load data with filters based on selected years
            val agricultureGrowth = if (mainViewModel.agricultureGrowthChecked) {
                filterDataByYear(parseCsvData(context, "Agriculture (% growth).csv", mainViewModel.selectedCountry), startYearFloat, endYearFloat)
            } else emptyList()

            val agricultureContribution = if (mainViewModel.agricultureContributionChecked) {
                filterDataByYear(parseCsvData(context, "Agriculture Contribution (% GDP).csv", mainViewModel.selectedCountry), startYearFloat, endYearFloat)
            } else emptyList()

            val fertilizerProduction = if (mainViewModel.fertilizerPercentChecked) {
                filterDataByYear(parseCsvData(context, "Fertilizer (% production).csv", mainViewModel.selectedCountry), startYearFloat, endYearFloat)
            } else emptyList()

            val fertilizerKG = if (mainViewModel.fertilizerKgChecked) {
                filterDataByYear(parseCsvData(context, "Fertilizer (kg per arable land).csv", mainViewModel.selectedCountry), startYearFloat, endYearFloat)
            } else emptyList()

            val manufacturing = if (mainViewModel.manufacturingChecked) {
                filterDataByYear(parseCsvData(context, "Manufacturing (% GDP).csv", mainViewModel.selectedCountry), startYearFloat, endYearFloat)
            } else emptyList()

            // Collect all unique years from the filtered data
            val allYears = (agricultureGrowth.map { it.first } + agricultureContribution.map { it.first } +
                    fertilizerProduction.map { it.first } + fertilizerKG.map { it.first } +
                    manufacturing.map { it.first })
                .distinct()
                .sorted()

            // Update yearLabels for x-axis
            yearLabels = allYears.map { it.toInt().toString() }

            // Helper function to map each dataset's year to the shared index in `allYears`
            fun mapDataToEntries(data: List<Pair<Float, Float>>): List<Entry> {
                return data.map { pair ->
                    val yearIndex = allYears.indexOf(pair.first)  // Find the index of the year in allYears
                    Entry(yearIndex.toFloat(), pair.second)
                }
            }

            // Create datasets for each selected type and align them on the same x-axis
            val newDataSets = mutableListOf<ILineDataSet>()

            if (agricultureGrowth.isNotEmpty()) {
                val agricultureGrowthEntries = mapDataToEntries(agricultureGrowth)
                val agricultureGrowthDataSet = LineDataSet(agricultureGrowthEntries, "Agriculture (% of Growth)").apply {
                    color = android.graphics.Color.YELLOW
                    lineWidth = 2f
                    setDrawValues(false)
                    setDrawCircles(false)
                }
                newDataSets.add(agricultureGrowthDataSet)
            }

            if (agricultureContribution.isNotEmpty()) {
                val agricultureContributionEntries = mapDataToEntries(agricultureContribution)
                val agricultureContributionDataSet = LineDataSet(agricultureContributionEntries, "Agriculture Contribution (% of GDP)").apply {
                    color = android.graphics.Color.CYAN
                    lineWidth = 2f
                    setDrawValues(false)
                    setDrawCircles(false)
                }
                newDataSets.add(agricultureContributionDataSet)
            }

            if (fertilizerProduction.isNotEmpty()) {
                val fertilizerProductionEntries = mapDataToEntries(fertilizerProduction)
                val fertilizerDataSet = LineDataSet(fertilizerProductionEntries, "Fertilizer Consumption (% of production)").apply {
                    color = android.graphics.Color.BLUE
                    lineWidth = 2f
                    setDrawValues(false)
                    setDrawCircles(false)
                }
                newDataSets.add(fertilizerDataSet)
            }

            if (fertilizerKG.isNotEmpty()) {
                val fertilizedKGEntries = mapDataToEntries(fertilizerKG)
                val fertilizedKGDataSet = LineDataSet(fertilizedKGEntries, "Fertilizer Consumption (Kg per arable land)").apply {
                    color = android.graphics.Color.GREEN
                    lineWidth = 2f
                    setDrawValues(false)
                    setDrawCircles(false)
                }
                newDataSets.add(fertilizedKGDataSet)
            }

            if (manufacturing.isNotEmpty()) {
                val manufacturingEntries = mapDataToEntries(manufacturing)
                val manufacturingDataSet = LineDataSet(manufacturingEntries, "Manufacturing (% of GDP)").apply {
                    color = android.graphics.Color.RED
                    lineWidth = 2f
                    setDrawValues(false)
                    setDrawCircles(false)
                }
                newDataSets.add(manufacturingDataSet)
            }

            dataSets = newDataSets
        }) {
            Text(text = "Generate Graph")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (dataSets.isNotEmpty()) {
            AndroidView(
                factory = { ctx ->
                    val lineChart = LineChart(ctx)
                    lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                    lineChart.description.text = "Agriculture Time Series Graph"
                    lineChart
                },
                update = { lineChart ->
                    val lineData = LineData(dataSets)
                    lineChart.data = lineData

                    // Update x-axis labels
                    lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(yearLabels)
                    lineChart.xAxis.granularity = 1f

                    // Refresh the chart
                    lineChart.invalidate()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Annotate button
        Button(onClick = {
            isAnnotateVisible = true
            annotationTextState = annotationText  // Load existing annotation
        }) {
            Text(text = "Annotate")
        }

        if (isAnnotateVisible) {
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = annotationTextState,
                onValueChange = { annotationTextState = it },
                label = { Text("Your Notes") },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                dataViewModel.viewModelScope.launch {
                    dataViewModel.saveAgriAnnotationText(annotationTextState)
                    withContext(Dispatchers.Main) {
                        isAnnotateVisible = false  // Hide the annotation box after saving
                        Toast.makeText(context, "Annotation saved", Toast.LENGTH_SHORT).show()
                    }
                }
            }) {
                Text(text = "Save")
            }
        }
    }
}