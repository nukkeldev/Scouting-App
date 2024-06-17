package nodes

import AppConfiguration
import Competition
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.ui.fader.BackStackFader
import com.bumble.appyx.navigation.composable.AppyxNavigationContainer
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.node
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import pages.LoginMenu

val LocalScoutingLog = compositionLocalOf { mutableStateOf(ScoutingLog()) }
val LocalScoutingLogs = compositionLocalOf { mutableStateMapOf<Int, MutableMap<Int, String>>() }
val LocalAppConfiguration = compositionLocalOf { mutableStateOf(AppConfiguration()) }

class RootNode(
    nodeContext: NodeContext,
    private val backStack: BackStack<NavTarget> = BackStack(
        model = BackStackModel(
            initialTarget = NavTarget.LoginPage,
            savedStateMap = nodeContext.savedStateMap
        ),
        visualisation = { BackStackFader(it) }
    )
) : Node<RootNode.NavTarget>(
    appyxComponent = backStack,
    nodeContext = nodeContext
) {
    private var scoutingLog = mutableStateOf(ScoutingLog())
    private var appConfiguration = mutableStateOf(AppConfiguration())
    private var scoutingLogs = mutableStateMapOf<Int, MutableMap<Int, String>>()

    sealed class NavTarget : Parcelable {
        @Parcelize
        data object MainMenu : NavTarget()

        @Parcelize
        data object MatchScouting : NavTarget()

        @Parcelize
        data object PitsScouting : NavTarget()

        @Parcelize
        data object LoginPage : NavTarget()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun buildChildNode(navTarget: NavTarget, nodeContext: NodeContext): Node<*> =
        when (navTarget) {
            else -> node(nodeContext) {
                LoginMenu()
            }

//            NavTarget.MainMenu -> node(nodeContext) { modifier ->
//                MainMenu(
//                    nodeContext,
//                    backStack,
//                    robotStartPosition,
//                    scoutName,
//                    comp,
//                    team,
//                    modifier
//                )
//            }
//
//            NavTarget.MatchScouting -> AutoTeleSelectorNode(
//                nodeContext,
//                robotStartPosition,
//                team,
//                backStack
//            )
//
//            NavTarget.PitsScouting -> node(nodeContext) {
//                PitsScoutMenu(
//                    backStack,
//                    pitsPerson,
//                    scoutName
//                )
//            }
        }

    @Composable
    override fun Content(modifier: Modifier) {
        CompositionLocalProvider(
            LocalScoutingLog provides scoutingLog,
            LocalScoutingLogs provides scoutingLogs,
            LocalAppConfiguration provides appConfiguration
        ) {
            Column {
                AppyxNavigationContainer(
                    appyxComponent = backStack,
                    modifier = Modifier.weight(0.9f)
                )
            }
        }
    }
}

@Composable
fun tryLoadScoutingLog(match: Int, robotStartPosition: Int) {
    LocalScoutingLog.current.value =
        LocalScoutingLogs.current[robotStartPosition]?.get(match)?.let { ScoutingLog.deserialize(it) }
            ?: return
}