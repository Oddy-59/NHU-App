import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DrawerContent(
    drawerState: DrawerState,
    scope: CoroutineScope,
    navController: NavController
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val drawerWidth = screenWidth * 0.75f
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .width(drawerWidth)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        // Top section
        Text(
            text = "More Options",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        Spacer(modifier = Modifier.weight(1f)) // Push bottom items to bottom

        // Divider line - centered and 65% width approx
        Divider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            thickness = 1.dp,
            modifier = Modifier
                .width(drawerWidth * 0.75f)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 12.dp)
        )

        Text(
            text = "Login As Admin",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .clickable {
                    scope.launch { drawerState.close() }
                    navController.navigate("login") // Replace with actual route
                }
        )

        Text(
            text = "Settings",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .clickable {
                    scope.launch { drawerState.close() }
                    // Navigate to settings screen if exists
                }
        )

        // NHU Website link under Settings
        Text(
            text = "Visit NHU Website",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://namibiahockey.org"))
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                }
        )
    }
}