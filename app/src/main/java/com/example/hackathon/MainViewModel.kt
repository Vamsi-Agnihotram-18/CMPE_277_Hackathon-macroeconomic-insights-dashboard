package com.example.hackathon

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.io.BufferedReader
import java.io.InputStreamReader

class MainViewModel: ViewModel() {
    var role by mutableStateOf<String?>(null)
    var selectedCountry by mutableStateOf("United States")
    var selectedSection by mutableStateOf("macroeconomic")

    // Mutable states for checkboxes and year range
    var gdpCurrentUSDChecked by mutableStateOf(false)
    var fdiInflowsChecked by mutableStateOf(true)
    var fdiOutflowsChecked by mutableStateOf(true)
    var importExportChecked by mutableStateOf(true)
    var growthRateChecked by mutableStateOf(true)
    var fdiBOPChecked by mutableStateOf(false)

    var agricultureGrowthChecked by mutableStateOf(true)
    var agricultureContributionChecked by mutableStateOf(true)
    var manufacturingChecked by mutableStateOf(true)
    var fertilizerPercentChecked by mutableStateOf(false)
    var fertilizerKgChecked by mutableStateOf(false)

    var debtServiceChecked by mutableStateOf(true)
    var totalDebtChecked by mutableStateOf(true)
    var totalReservesExternalChecked by mutableStateOf(true)
    var totalReservesMonthChecked by mutableStateOf(false)
    var totalReservesUSD by mutableStateOf(false)
    var gniCurrentUSDChecked by mutableStateOf(false)
}

// Function to parse CSV data for a specific country
fun parseCsvData(context: Context, fileName: String, country: String): List<Pair<Float, Float>> {
    val data = mutableListOf<Pair<Float, Float>>()

    // Open the CSV file from assets
    val inputStream = context.assets.open(fileName)
    BufferedReader(InputStreamReader(inputStream)).use { reader ->
        val headers = reader.readLine()?.split(",") ?: return emptyList()

        // Get the index of each year's column in headers
        val yearIndices = headers.drop(4).mapIndexedNotNull { index, year ->
            year.toFloatOrNull()?.let { index + 4 to it } // Start index at 4 (after non-year columns)
        }

        // Find the row for the selected country
        reader.lineSequence().forEach { line ->
            val columns = line.split(",")
            if (columns[0] == country) {
                yearIndices.forEach { (index, year) ->
                    columns.getOrNull(index)?.toFloatOrNull()?.let { value ->
                        data.add(Pair(year, value))
                    }
                }
                return@forEach
            }
        }
    }
    return data
}

// Helper function to filter data by the selected year range
fun filterDataByYear(data: List<Pair<Float, Float>>, startYear: Float?, endYear: Float?): List<Pair<Float, Float>> {
    return data.filter { (year, _) ->
        (startYear == null || year >= startYear) && (endYear == null || year <= endYear)
    }
}