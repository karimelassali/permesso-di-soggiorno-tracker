package com.example

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel5, sdk = [34])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun app_screenshot() {
    composeTestRule.setContent {
      MyApplicationTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            // Header
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "Benvenuto, Karim",
                  fontSize = 24.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                  text = "Track your Permesso di Soggiorno",
                  fontSize = 14.sp,
                  color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
              }
              Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
              )
            }

            // Status Card
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(24.dp),
              colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
              )
            ) {
              Column(modifier = Modifier.padding(20.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "STATUS: IN TRATTAZIONE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                  )
                  Icon(
                    imageVector = Icons.Default.Autorenew,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                  )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = "Your application is currently being processed by the Questura.",
                  fontSize = 15.sp,
                  color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                  text = "Last checked: Today, 10:15 AM",
                  fontSize = 12.sp,
                  color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                )
              }
            }

            // Bento Grid Items
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
              Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                  containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                  )
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                    text = "Guides",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                  )
                  Text(
                    text = "8 Articles",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                  )
                }
              }

              Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                  containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                  )
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                    text = "Sofia AI",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                  )
                  Text(
                    text = "Immigration Assistant",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                  )
                }
              }
            }
          }
        }
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
