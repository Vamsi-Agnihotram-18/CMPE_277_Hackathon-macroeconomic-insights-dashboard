package com.example.hackathon

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun AppLaunchView(navController: NavController, mainViewModel: MainViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.macro_logo),
            contentDescription = "Macroeconomic Researcher",
            modifier = Modifier
                .size(100.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "Welcome",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Text(
            text = "to",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Text(
            text = "Macroeconomic",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Text(
            text = "Food Security App",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 60.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Gray.copy(alpha = 0.1f))
                    .clickable {
                        mainViewModel.role = "macroeconomic_researcher"
                        navController.navigate("data_analysis")
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.person_fill),
                        contentDescription = "Macroeconomic Researcher",
                        colorFilter = ColorFilter.tint(Color.Red),
                        modifier = Modifier
                            .size(60.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Macroeconomic",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = "Researcher",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(modifier = Modifier.width(24.dp))

            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Gray.copy(alpha = 0.1f))
                    .clickable {
                        mainViewModel.role = "government_official"
                        navController.navigate("data_analysis")
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.person_fill),
                        contentDescription = "Government Official",
                        colorFilter = ColorFilter.tint(Color.Blue),
                        modifier = Modifier
                            .size(60.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Government",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = "Official",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier
            .fillMaxWidth(0.7f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ChatButton {
                navController.navigate("chat")
            }
        }
    }
}

@Preview
@Composable
fun AppLaunchViewPreview() {
    AppLaunchView(navController = NavController(LocalContext.current), mainViewModel = MainViewModel())
}

@Composable
fun ChatButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .alpha(0.9f)
            .shadow(0.5.dp, shape = CircleShape)
            .background(
                color = Color.White,
                shape = CircleShape
            )
            .border(
                shape = CircleShape,
                border = BorderStroke(0.5.dp, Color.Black.copy(alpha = 0.1f))
            ),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(64.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.chatgpt_icon),
                contentDescription = "Scan and Pay",
                tint = Color.Blue.copy(alpha = 0.6f),
                modifier = Modifier.size(55.dp)
            )
        }
    }
}