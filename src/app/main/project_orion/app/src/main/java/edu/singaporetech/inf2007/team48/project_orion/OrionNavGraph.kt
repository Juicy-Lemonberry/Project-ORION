package edu.singaporetech.inf2007.team48.project_orion

import edu.singaporetech.inf2007.team48.project_orion.controllers.bluetooths.BluetoothViewModel
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import edu.singaporetech.inf2007.team48.project_orion.controllers.OrionViewModel
import edu.singaporetech.inf2007.team48.project_orion.controllers.OrionViewModelFactory
import edu.singaporetech.inf2007.team48.project_orion.services.api.OrionApiClient
import edu.singaporetech.inf2007.team48.project_orion.screens.addNewItem.AddNewItemScreen
import edu.singaporetech.inf2007.team48.project_orion.screens.EditChecklistItemScreen
import edu.singaporetech.inf2007.team48.project_orion.screens.editChecklist.EditChecklistScreen
import edu.singaporetech.inf2007.team48.project_orion.screens.RegisterAccountScreen
import edu.singaporetech.inf2007.team48.project_orion.screens.RovConnectingScreen
import edu.singaporetech.inf2007.team48.project_orion.screens.RovViewPortScreen
import edu.singaporetech.inf2007.team48.project_orion.screens.SearchAssetScreen
import edu.singaporetech.inf2007.team48.project_orion.screens.SystemMenuScreen
import edu.singaporetech.inf2007.team48.project_orion.screens.ViewAssetScreen
import edu.singaporetech.inf2007.team48.project_orion.screens.WelcomeScreen
import edu.singaporetech.inf2007.team48.project_orion.screens.testingpulldata
import edu.singaporetech.inf2007.team48.project_orion.controllers.bluetooths.BluetoothViewModelFactory
import edu.singaporetech.inf2007.team48.project_orion.controllers.hotspot.HotspotViewModel
import edu.singaporetech.inf2007.team48.project_orion.controllers.hotspot.HotspotViewModelFactory
import edu.singaporetech.inf2007.team48.project_orion.controllers.xbox.XboxInputViewModel
import edu.singaporetech.inf2007.team48.project_orion.controllers.udp.UdpViewModel
import edu.singaporetech.inf2007.team48.project_orion.screens.RovLoadChecklistScreen
import edu.singaporetech.inf2007.team48.project_orion.screens.RovQRCodeScreen
import edu.singaporetech.inf2007.team48.project_orion.screens.RovWelcomeScreen
import edu.singaporetech.inf2007.team48.project_orion.screens.UdpTestScreen

sealed class OrionScreens(val route: String) {
    object WelcomeScreen : OrionScreens(route = "welcome_screen")
    object SystemMenuScreen : OrionScreens(route = "system_menu_screen")
    object SearchAssetScreen : OrionScreens(route = "search_asset_screen")

    object EditChecklistScreen : OrionScreens(route = "edit_checklist_screen")
    object EditChecklistItemScreen : OrionScreens(route = "edit_checklist_item_screen")
    object RovViewPortScreen : OrionScreens(route = "rov_view_port_screen")
    object RovWelcomeScreen : OrionScreens(route = "rov_welcome_screen")
    object RovConnectingScreen : OrionScreens(route = "rov_connecting_screen")

    //    object RovQRCodeScreen : OrionScreens(route = "rov_qrcode_screen")
    object RegisterAccountScreen : OrionScreens(route = "register_account_screen")
//    object RovBluetoothConnectScreen : OrionScreens(route = "rov_bluetooth_screen")

    object testingpulldata : OrionScreens(route = "zhtestpull_screen")

    object ViewAssetScreen : OrionScreens(route = "view_asset_screen/{assetId}") {
        // Define a function to create a route with a specific assetId
        fun createRoute(assetId: String) = "view_asset_screen/$assetId"
    }

    object AddNewItemScreen : OrionScreens(route = "addnewitem_screen")
    object RovQRCodeScreen : OrionScreens(route = "rov_qr_code_screen")

    object udpTestingScreen : OrionScreens(route = "udp_testing_screen")

    object RovLoadChecklistScreen : OrionScreens(route = "rov_load_checklist_screen")

    /*TODO: Keep Adding More Screens As they get created*/
}

@Composable
fun OrionNavGraph(
    orionViewModelFactory: OrionViewModelFactory,
    hotspotViewModelFactory: HotspotViewModelFactory,
    bluetoothViewModelFactory: BluetoothViewModelFactory,
    xboxInputViewModel: XboxInputViewModel,
    udpViewModel: UdpViewModel
) {
    val orionViewModel: OrionViewModel = viewModel(factory = orionViewModelFactory)
    val hotspotViewModel: HotspotViewModel = viewModel(factory = hotspotViewModelFactory)
    val bluetoothViewModel: BluetoothViewModel = viewModel(factory = bluetoothViewModelFactory)
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = OrionScreens.WelcomeScreen.route
    ) {
        composable(OrionScreens.WelcomeScreen.route) {
            WelcomeScreen(
                navController = navController,
                xboxInputViewModel = xboxInputViewModel,
                orionViewModel = orionViewModel
            )
        }
        composable(OrionScreens.SystemMenuScreen.route) {
            SystemMenuScreen(
                navController = navController,
                xboxInputViewModel = xboxInputViewModel,
                orionViewModel = orionViewModel
            )
        }
        composable(
            route = OrionScreens.ViewAssetScreen.route,
            arguments = listOf(navArgument("assetId") {
                type = NavType.StringType
            }) // or NavType.IntType if IDs are integers
        ) { backStackEntry ->
            ViewAssetScreen(
                assetId = backStackEntry.arguments?.getString("assetId") ?: "",
                navController = navController,
                orionViewModel = orionViewModel,
                xboxInputViewModel = xboxInputViewModel,
                orionGetService = OrionApiClient.orionApi.getService,
                orionPostService = OrionApiClient.orionApi.postService
            )
        }

        composable(OrionScreens.SearchAssetScreen.route) {
            SearchAssetScreen(
                navController = navController,
                xboxInputViewModel = xboxInputViewModel,
                orionViewModel = orionViewModel,
                orionApi = OrionApiClient.orionApi.getService // Adjust based on your actual implementation
            )
        }
        composable(OrionScreens.EditChecklistScreen.route) {
            EditChecklistScreen(
                navController = navController,
                xboxInputViewModel = xboxInputViewModel,
                orionViewModel = orionViewModel,
                orionApi = OrionApiClient.orionApi.getService,
                orionApiPostService = OrionApiClient.orionApi.postService,
                orionApiServiceDelete = OrionApiClient.orionApi.deleteService
            )
        }

        composable(OrionScreens.AddNewItemScreen.route) {
            AddNewItemScreen(
                navController = navController,
                xboxInputViewModel = xboxInputViewModel,
                orionViewModel = orionViewModel,
                orionApiPostService = OrionApiClient.orionApi.postService
            )
        }

        composable(OrionScreens.EditChecklistItemScreen.route) {
            EditChecklistItemScreen(
                navController = navController,
                xboxInputViewModel = xboxInputViewModel,
                orionViewModel = orionViewModel
            )
        }
        composable(OrionScreens.RovWelcomeScreen.route) {
            RovWelcomeScreen(
                navController = navController,
                xboxInputViewModel = xboxInputViewModel,
                orionViewModel = orionViewModel
            )
        }
//        composable(OrionScreens.RovQRCodeScreen.route) {
//            RovQRCodeScreen(navController = navController, orionViewModel = orionViewModel)
//        }
        composable(OrionScreens.RovConnectingScreen.route) {
            RovConnectingScreen(
                navController = navController,
                xboxInputViewModel = xboxInputViewModel,
                orionViewModel = orionViewModel,
                bluetoothViewModel = bluetoothViewModel,
                hotspotViewModel = hotspotViewModel
            )
        }
        composable(OrionScreens.RovViewPortScreen.route) {
            RovViewPortScreen(
                navController = navController,
                xboxInputViewModel = xboxInputViewModel,
                udpViewModel = udpViewModel,
                orionViewModel = orionViewModel
            )
        }
//        composable(OrionScreens.RovBluetoothConnectScreen.route) {
//            RovBluetoothConnectScreen(navController = navController, orionViewModel = orionViewModel)
//        }
        composable(OrionScreens.RegisterAccountScreen.route) {
            RegisterAccountScreen(
                navController = navController,
                xboxInputViewModel = xboxInputViewModel,
                orionViewModel = orionViewModel
            )
        }

        composable(OrionScreens.testingpulldata.route) {
            testingpulldata(
                navController = navController,
                xboxInputViewModel = xboxInputViewModel,
                orionViewModel = orionViewModel,
                orionApi = OrionApiClient.orionApi.getService // Adjust based on your actual implementation
            )
        }

        composable(OrionScreens.RovQRCodeScreen.route) {
            RovQRCodeScreen(
                navController = navController,
                xboxInputViewModel = xboxInputViewModel,
                orionViewModel = orionViewModel
            )
        }

        composable(OrionScreens.udpTestingScreen.route) {
            UdpTestScreen(
                xboxInputViewModel = xboxInputViewModel,
                orionViewModel = orionViewModel,
                udpViewModel = udpViewModel,
                navController = navController
            )
        }

        composable(OrionScreens.RovLoadChecklistScreen.route) {
            RovLoadChecklistScreen(
                navController = navController,
                xboxInputViewModel = xboxInputViewModel,
                orionViewModel = orionViewModel,
            )
        }
    }
}