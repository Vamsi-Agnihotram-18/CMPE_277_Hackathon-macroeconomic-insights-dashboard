package com.example.hackathon

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CountryFlagDropdown(mainViewModel: MainViewModel) {
    // State for managing selected country and expanded menu
    var expanded by remember { mutableStateOf(false) }

    // Map to hold country names and their corresponding flag resources
    val countries = mapOf(
        "United States" to R.drawable.flag_usa,
        "China" to R.drawable.flag_china,
        "India" to R.drawable.flag_india
    )

    // Main Column layout
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = countries[mainViewModel.selectedCountry] ?: R.drawable.flag_usa),
            contentDescription = "${mainViewModel.selectedCountry} Flag",
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    expanded = true
                }
        )

        // Dropdown menu for selecting the country
        Box {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                countries.keys.forEach { country ->
                    DropdownMenuItem(
                        onClick = {
                        mainViewModel.selectedCountry = country
                        expanded = false
                    },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = countries[country] ?: R.drawable.flag_usa),
                                    contentDescription = "$country Flag",
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = country, fontSize = 16.sp)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun CountryFlagDropdownPreview() {
    CountryFlagDropdown(mainViewModel = MainViewModel())
}