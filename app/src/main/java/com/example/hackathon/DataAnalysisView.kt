package com.example.hackathon

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun DataAnalysisView(navController: NavController, mainViewModel: MainViewModel, dataViewModel: DataViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp), // Leave space for the bottom buttons
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with the selected country and role information
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (mainViewModel.role == "macroeconomic_researcher") {
                        Image(
                            painter = painterResource(id = R.drawable.person_fill),
                            contentDescription = "Government Official",
                            colorFilter = ColorFilter.tint(Color.Red),
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.person_fill),
                            contentDescription = "Government Official",
                            colorFilter = ColorFilter.tint(Color.Blue),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Text(
                        text = "Dashboard",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    CountryFlagDropdown(mainViewModel = mainViewModel)
                }
            }

            // Macroeconomic View containing the checkboxes and the graph
            if (mainViewModel.selectedSection == "macroeconomic") {
                item {
                    MacroeconomicView(mainViewModel = mainViewModel, dataViewModel = dataViewModel)
                }
            } else if (mainViewModel.selectedSection == "agriculture") {
                item {
                    AgricultureView(mainViewModel = mainViewModel, dataViewModel = dataViewModel)
                }
            } else if (mainViewModel.selectedSection == "debt") {
                item {
                    DebtView(mainViewModel = mainViewModel, dataViewModel = dataViewModel)
                }
            } else {
                mainViewModel.selectedSection = "macroeconomic"
                item {
                    MacroeconomicView(mainViewModel = mainViewModel, dataViewModel = dataViewModel)
                }
            }
        }

        // Section selection buttons at the bottom of the screen
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 60.dp)
        ) {
            ChatButton {
                navController.navigate("chat")
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.Gray.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .background(
                            if (mainViewModel.selectedSection == "macroeconomic") Color.Blue.copy(
                                alpha = 0.2f
                            ) else Color.Transparent
                        )
                        .clickable {
                            mainViewModel.selectedSection = "macroeconomic"
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.economic),
                        contentDescription = "Macroeconomic Button",
                        modifier = Modifier.size(40.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .background(
                            if (mainViewModel.selectedSection == "agriculture") Color.Blue.copy(
                                alpha = 0.2f
                            ) else Color.Transparent
                        )
                        .clickable {
                            mainViewModel.selectedSection = "agriculture"
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.agriculture),
                        contentDescription = "Agriculture Button",
                        modifier = Modifier.size(40.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .background(
                            if (mainViewModel.selectedSection == "debt") Color.Blue.copy(
                                alpha = 0.2f
                            ) else Color.Transparent
                        )
                        .clickable {
                            mainViewModel.selectedSection = "debt"
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.debt),
                        contentDescription = "Debt Button",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun DataAnalysisViewPreview() {
    DataAnalysisView(navController = NavController(LocalContext.current), mainViewModel = MainViewModel(), dataViewModel = viewModel())
}