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
fun MacroeconomicView(mainViewModel: MainViewModel, dataViewModel: DataViewModel) {
    val context = LocalContext.current

    var dataSets by remember { mutableStateOf<List<ILineDataSet>>(emptyList()) }
    var startYear by remember { mutableStateOf("") }
    var endYear by remember { mutableStateOf("") }
    var yearLabels by remember { mutableStateOf<List<String>>(emptyList()) }

    // State variables for annotation functionality
    var isAnnotateVisible by remember { mutableStateOf(false) }
    var annotationTextState by remember { mutableStateOf("") }
    val annotationText by dataViewModel.macroAnnotationTextFlow.collectAsState(initial = "")


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Macroeconomics (${mainViewModel.selectedCountry})")

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

        // Checkbox options for selecting data to display
        Column(
            modifier = Modifier.fillMaxWidth(0.8f),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start, modifier = Modifier.padding(10.dp)) {
                Checkbox(
                    checked = mainViewModel.gdpCurrentUSDChecked,
                    onCheckedChange = { mainViewModel.gdpCurrentUSDChecked = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Current GDP (USD)")
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start, modifier = Modifier.padding(10.dp)) {
                Checkbox(
                    checked = mainViewModel.fdiBOPChecked,
                    onCheckedChange = { mainViewModel.fdiBOPChecked = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "FDI Net (BoP, USD)")
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start, modifier = Modifier.padding(10.dp)) {
                Checkbox(
                    checked = mainViewModel.growthRateChecked,
                    onCheckedChange = { mainViewModel.growthRateChecked = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "GDP Growth Rate (%)")
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start, modifier = Modifier.padding(10.dp)) {
                Checkbox(
                    checked = mainViewModel.fdiInflowsChecked,
                    onCheckedChange = { mainViewModel.fdiInflowsChecked = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "FDI Inflows (%)")
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start, modifier = Modifier.padding(10.dp)) {
                Checkbox(
                    checked = mainViewModel.fdiOutflowsChecked,
                    onCheckedChange = { mainViewModel.fdiOutflowsChecked = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "FDI Outflows (%)")
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start, modifier = Modifier.padding(10.dp)) {
                Checkbox(
                    checked = mainViewModel.importExportChecked,
                    onCheckedChange = { mainViewModel.importExportChecked = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Current Account Balance (%)")
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
            val gdpGrowthRate = if (mainViewModel.growthRateChecked) {
                filterDataByYear(parseCsvData(context, "GDP Growth Rate.csv", mainViewModel.selectedCountry), startYearFloat, endYearFloat)
            } else emptyList()

            val fdiBOP = if (mainViewModel.fdiBOPChecked) {
                filterDataByYear(parseCsvData(context, "FDI BOP.csv", mainViewModel.selectedCountry), startYearFloat, endYearFloat)
            } else emptyList()

            val gdpCurrentUSDData = if (mainViewModel.gdpCurrentUSDChecked) {
                filterDataByYear(parseCsvData(context, "GDP Current USD.csv", mainViewModel.selectedCountry), startYearFloat, endYearFloat)
            } else emptyList()

            val fdiInflowsData = if (mainViewModel.fdiInflowsChecked) {
                filterDataByYear(parseCsvData(context, "FDI Net Inflows.csv", mainViewModel.selectedCountry), startYearFloat, endYearFloat)
            } else emptyList()

            val fdiOutflowsData = if (mainViewModel.fdiOutflowsChecked) {
                filterDataByYear(parseCsvData(context, "FDI Net Outflows.csv", mainViewModel.selectedCountry), startYearFloat, endYearFloat)
            } else emptyList()

            val importExportData = if (mainViewModel.importExportChecked) {
                filterDataByYear(parseCsvData(context, "Current Account Balance.csv", mainViewModel.selectedCountry), startYearFloat, endYearFloat)
            } else emptyList()

            // Collect all unique years from the filtered data
            val allYears = (gdpGrowthRate.map { it.first } + fdiBOP.map { it.first } +
                    gdpCurrentUSDData.map { it.first } + fdiInflowsData.map { it.first } +
                    fdiOutflowsData.map { it.first } + importExportData.map { it.first })
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

            if (gdpGrowthRate.isNotEmpty()) {
                val gdpGrowthRateEntries = mapDataToEntries(gdpGrowthRate)
                val gdpGrowthRateDataSet = LineDataSet(gdpGrowthRateEntries, "GDP Growth Rate (%)").apply {
                    color = android.graphics.Color.YELLOW
                    lineWidth = 2f
                    setDrawValues(false)
                    setDrawCircles(false)
                }
                newDataSets.add(gdpGrowthRateDataSet)
            }

            if (fdiBOP.isNotEmpty()) {
                val fdiBOPEntries = mapDataToEntries(fdiBOP)
                val fdiBOPDataSet = LineDataSet(fdiBOPEntries, "FDI Net (BoP, USD)").apply {
                    color = android.graphics.Color.CYAN
                    lineWidth = 2f
                    setDrawValues(false)
                    setDrawCircles(false)
                }
                newDataSets.add(fdiBOPDataSet)
            }

            if (gdpCurrentUSDData.isNotEmpty()) {
                val gdpEntries = mapDataToEntries(gdpCurrentUSDData)
                val gdpDataSet = LineDataSet(gdpEntries, "Current GDP (USD)").apply {
                    color = android.graphics.Color.BLUE
                    lineWidth = 2f
                    setDrawValues(false)
                    setDrawCircles(false)
                }
                newDataSets.add(gdpDataSet)
            }

            if (fdiInflowsData.isNotEmpty()) {
                val fdiInflowsEntries = mapDataToEntries(fdiInflowsData)
                val fdiInflowsDataSet = LineDataSet(fdiInflowsEntries, "FDI Inflows (%)").apply {
                    color = android.graphics.Color.GREEN
                    lineWidth = 2f
                    setDrawValues(false)
                    setDrawCircles(false)
                }
                newDataSets.add(fdiInflowsDataSet)
            }

            if (fdiOutflowsData.isNotEmpty()) {
                val fdiOutflowsEntries = mapDataToEntries(fdiOutflowsData)
                val fdiOutflowsDataSet = LineDataSet(fdiOutflowsEntries, "FDI Outflows (%)").apply {
                    color = android.graphics.Color.RED
                    lineWidth = 2f
                    setDrawValues(false)
                    setDrawCircles(false)
                }
                newDataSets.add(fdiOutflowsDataSet)
            }

            if (importExportData.isNotEmpty()) {
                val importExportEntries = mapDataToEntries(importExportData)
                val importExportDataSet = LineDataSet(importExportEntries, "Current Account Balance (%)").apply {
                    color = android.graphics.Color.MAGENTA
                    lineWidth = 2f
                    setDrawValues(false)
                    setDrawCircles(false)
                }
                newDataSets.add(importExportDataSet)
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
                    lineChart.description.text = "Macroeconomic Time Series Graph"
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
                    dataViewModel.saveMacroAnnotationText(annotationTextState)
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