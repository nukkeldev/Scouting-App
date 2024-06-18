package org.tahomarobotics.scouting


import App
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.ExperimentalUnitApi
import com.bumble.appyx.navigation.integration.NodeActivity
import nodes.FILES_DIRECTORY

@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
class MainActivity : NodeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FILES_DIRECTORY = LocalContext.current.filesDir
            App(appyxIntegrationPoint)
        }
    }
}

