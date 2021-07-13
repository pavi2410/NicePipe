package me.pavi2410.nicepipe.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import me.pavi2410.nicepipe.R
import me.pavi2410.nicepipe.ui.screens.HomeScreen
import me.pavi2410.nicepipe.ui.screens.PlaylistsScreen
import me.pavi2410.nicepipe.ui.screens.SearchScreen
import me.pavi2410.nicepipe.ui.screens.SubsScreen
import me.pavi2410.nicepipe.ui.screens.VideoPlayerScreen
import me.pavi2410.nicepipe.ui.theme.Red500

sealed class Screen(val route: String, val icon: ImageVector) {
    object Home : Screen("home", Icons.Filled.Home)
    object Playlists : Screen("playlists", Icons.Filled.PlaylistPlay)
    object Subs : Screen("subs", Icons.Filled.Subscriptions)
    object Search : Screen("search", Icons.Filled.Search)
    object VideoPlayer : Screen("videoPlayer", Icons.Filled.PlayArrow)
}

val bottomNavLinks = listOf(
    Screen.Home,
    Screen.Playlists,
    Screen.Subs
)

@Composable
fun Nav() {
    val navController = rememberNavController()

    var showBottomAppBar by remember { mutableStateOf(true) }

    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = { if (showBottomAppBar) BottomBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.Home.route,
            Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { showBottomAppBar = true; HomeScreen(navController) }
            composable(Screen.Playlists.route) { showBottomAppBar = true; PlaylistsScreen(navController) }
            composable(Screen.Subs.route) { showBottomAppBar = true; SubsScreen(navController) }
            composable(Screen.Search.route) { showBottomAppBar = false; SearchScreen(navController) }
            composable(Screen.VideoPlayer.route) { showBottomAppBar = false; VideoPlayerScreen(navController) }
        }
    }
}

@Composable
fun TopBar(navController: NavHostController) {
    TopAppBar(
        title = {
            Text(stringResource(id = R.string.app_name))
        },
        elevation = 0.dp,
        backgroundColor = Color.White,
        actions = {
            IconButton(onClick = { navController.navigate(Screen.Search.route) {
                launchSingleTop = true
            } }) {
                Icon(imageVector = Icons.Filled.Search, contentDescription = null, tint = Color.Black)
            }
        }
    )
}

@Composable
fun BottomBar(navController: NavHostController) {
    BottomNavigation(backgroundColor = Color.White, elevation = 64.dp) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        bottomNavLinks.forEach { screen ->
            BottomNavigationItem(
                icon = { Icon(screen.icon, contentDescription = null) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                selectedContentColor = Red500,
                unselectedContentColor = Color.Black.copy(alpha = ContentAlpha.medium),
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}
