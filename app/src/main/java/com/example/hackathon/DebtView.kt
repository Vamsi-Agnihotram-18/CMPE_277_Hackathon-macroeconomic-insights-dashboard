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
fun DebtView(mainViewModel: MainViewModel, dataViewModel: DataViewModel) {
    val context = LocalContext.current

    var dataSets by remember { mutableStateOf<List<ILineDataSet>>(emptyList()) }
    var startYear by remember { mutableStateOf("") }
    var endYear by remember { mutableStateOf("") }
    var yearLabels by remember { mutableStateOf<List<String>>(emptyList()) }

    // State variables for annotation functionality
    var isAnnotateVisible by remember { mutableStateOf(false) }
    var annotationTextState by remember { mutableStateOf("") }
    val annotationText by dataViewModel.debtAnnotationTextFlow.collectAsState(initial = "")


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Debt (${mainViewModel.selectedCountry})")

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
                    checked = mainViewModel.debtServiceChecked,
                    onCheckedChange = { mainViewModel.debtServiceChecked = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Debt Service (% of exports)")
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start, modifier = Modifier.padding(10.dp)) {
                Checkbox(
                    checked = mainViewModel.totalDebtChecked,
                    onCheckedChange = { mainViewModel.totalDebtChecked = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Total Debt (% of GNI)")
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start, modifier = Modifier.padding(10.dp)) {
                Checkbox(
                    checked = mainViewModel.totalReservesExternalChecked,
                    onCheckedChange = { mainViewModel.totalReservesExternalChecked = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Total Reserves (% of Total External Debt)")
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start, modifier = Modifier.padding(10.dp)) {
                Checkbox(
                    checked = mainViewModel.totalReservesMonthChecked,
                    onCheckedChange = { mainViewModel.totalReservesMonthChecked = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Total Reserves in Month of Import")
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start, modifier = Modifier.padding(10.dp)) {
                Checkbox(
                    checked = mainViewModel.totalReservesUSD,
                    onCheckedChange = { mainViewModel.totalReservesUSD = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Total Reserves with Gold (USD)")
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start, modifier = Modifier.padding(10.dp)) {
                Checkbox(
                    checked = mainViewModel.gniCurrentUSDChecked,
                    onCheckedChange = { mainViewModel.gniCurrentUSDChecked = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "GNI (USD)")
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
            val debtService = if (mainViewModel.debtServiceChecked) {
                filterDataByYear(parseCsvData(context, "debt service (% exports).csv", mainViewModel.selectedCountry), startYearFloat, endYearFloat)
            } else emptyList()

            val totalDebt = if (mainViewModel.totalDebtChecked) {
                filterDataByYear(parseCsvData(context, "total debt (% GNI).csv", mainViewModel.selectedCountry), startYearFloat, endYearFloat)
            } else emptyList()

            val totalReservesExternal = if (mainViewModel.totalReservesExternalChecked) {
                filterDataByYear(parseCsvData(context, "Total Reserves (% Total External Debt).csv", mainViewModel.selectedCountry), startYearFloat, endYearFloat)
            } else emptyList()

            val totalReservesMonth = if (mainViewModel.totalReservesMonthChecked) {
                filterDataByYear(parseCsvData(context, "Total Reserves in Month of Import.csv", mainViewModel.selectedCountry), startYearFloat, endYearFloat)
            } else emptyList()

            val totalReservesUsd = if (mainViewModel.totalReservesUSD) {
                filterDataByYear(parseCsvData(context, "Total Reserves with Gold (current USD).csv", mainViewModel.selectedCountry), startYearFloat, endYearFloat)
            } else emptyList()

            val gniUsd = if (mainViewModel.gniCurrentUSDChecked) {
                filterDataByYear(parseCsvData(context, "GNI (current USD).csv", mainViewModel.selectedCountry), startYearFloat, endYearFloat)
            } else emptyList()

            // Collect all unique years from the filtered data
            val allYears = (debtService.map { it.first } + totalDebt.map { it.first } +
                    totalReservesExternal.map { it.first } + totalReservesMonth.map { it.first } +
                    totalReservesUsd.map { it.first } + gniUsd.map { it.first })
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

            if (debtService.isNotEmpty()) {
                val debtServiceEntries = mapDataToEntries(debtService)
                val debtServiceDataSet = LineDataSet(debtServiceEntries, "Debt Service (% of exports)").apply {
                    color = android.graphics.Color.YELLOW
                    lineWidth = 2f
                    setDrawValues(false)
                    setDrawCircles(false)
                }
                newDataSets.add(debtServiceDataSet)
            }

            if (totalDebt.isNotEmpty()) {
                val totalDebtEntries = mapDataToEntries(totalDebt)
                val totalDebtDataSet = LineDataSet(totalDebtEntries, "Total Debt (% of GNI)").apply {
                    color = android.graphics.Color.CYAN
                    lineWidth = 2f
                    setDrawValues(false)
                    setDrawCircles(false)
                }
                newDataSets.add(totalDebtDataSet)
            }

            if (totalReservesExternal.isNotEmpty()) {
                val totalReservesExternalEntries = mapDataToEntries(totalReservesExternal)
                val totalReservesExternalDataSet = LineDataSet(totalReservesExternalEntries, "Total Reserves (% of Total External Debt)").apply {
                    color = android.graphics.Color.BLUE
                    lineWidth = 2f
                    setDrawValues(false)
                    setDrawCircles(false)
                }
                newDataSets.add(totalReservesExternalDataSet)
            }

            if (totalReservesMonth.isNotEmpty()) {
                val totalReservesMonthEntries = mapDataToEntries(totalReservesMonth)
                val totalReservesMonthDataSet = LineDataSet(totalReservesMonthEntries, "Total Reserves in Month of Import").apply {
                    color = android.graphics.Color.GREEN
                    lineWidth = 2f
                    setDrawValues(false)
                    setDrawCircles(false)
                }
                newDataSets.add(totalReservesMonthDataSet)
            }

            if (totalReservesUsd.isNotEmpty()) {
                val totalReservesUsdEntries = mapDataToEntries(totalReservesUsd)
                val totalReservesUsdDataSet = LineDataSet(totalReservesUsdEntries, "Total Reserves with Gold (USD)").apply {
                    color = android.graphics.Color.RED
                    lineWidth = 2f
                    setDrawValues(false)
                    setDrawCircles(false)
                }
                newDataSets.add(totalReservesUsdDataSet)
            }

            if (gniUsd.isNotEmpty()) {
                val gniUsdEntries = mapDataToEntries(gniUsd)
                val gniUsdDataSet = LineDataSet(gniUsdEntries, "GNI (USD)").apply {
                    color = android.graphics.Color.MAGENTA
                    lineWidth = 2f
                    setDrawValues(false)
                    setDrawCircles(false)
                }
                newDataSets.add(gniUsdDataSet)
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
                    lineChart.description.text = "Debt Time Series Graph"
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
                    dataViewModel.saveDebtAnnotationText(annotationTextState)
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