import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nhu_app.R
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
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "More options",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 20.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        // Static list with icons
        // Static list with icons and themed text
        val items = listOf(
            "Officials" to R.drawable.ic_officials,
            "Indoor Hockey" to R.drawable.ic_indoor,
            "Outdoor Hockey" to R.drawable.ic_outdoor,
            "National Teams" to R.drawable.ic_national,
            "Tournaments" to R.drawable.ic_tournaments,
            "About" to R.drawable.ic_about,
            "Contact Us" to R.drawable.ic_contact
        )

        items.forEach { (label, iconRes) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = label,
                    modifier = Modifier
                        .size(29.dp)
                        .padding(end = 12.dp)
                )
                Text(
                    text = label,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f)) // Push bottom items to bottom

        HorizontalDivider(
            modifier = Modifier
                .width(drawerWidth * 0.75f)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 12.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )

        // Login As Admin
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp)
                .clickable {
                    scope.launch { drawerState.close() }
                    navController.navigate("login")
                }
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_admin),
                contentDescription = "Login",
                modifier = Modifier
                    .size(35.dp)
                    .padding(end = 12.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
            Text(
                text = "Login As Admin",
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

// Settings
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_settings),
                contentDescription = "Settings",
                modifier = Modifier
                    .size(33.dp)
                    .padding(end = 12.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
            Text(
                text = "Settings",
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Social icons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val socialLinks = listOf(
                R.drawable.ic_facebook to "https://facebook.com/NamibiaHockey",
                R.drawable.ic_instagram to "https://instagram.com/namibiahockeyunion",
                R.drawable.ic_x to "https://x.com/namibiahockey",
                R.drawable.ic_website to "https://namibiahockey.org"
            )

            socialLinks.forEach { (iconRes, url) ->
                IconButton(onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                    scope.launch { drawerState.close() }
                }) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}