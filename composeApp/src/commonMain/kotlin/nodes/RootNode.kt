package nodes

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
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
import data.AppConfiguration
import data.ScoutingLogs
import data.overwriteString
import matchData
import pages.LoginMenu
import pages.MainMenu
import teamData
import java.io.File

lateinit var FILES_DIRECTORY: File

val LocalScoutingLogs: ProvidableCompositionLocal<MutableState<ScoutingLogs>> =
    compositionLocalOf { error("No default can be provided for scouting logs. Please use in the context of a provider.") }
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
    private var appConfiguration = mutableStateOf(AppConfiguration())
    private var scoutingLogs = mutableStateOf(ScoutingLogs(File(FILES_DIRECTORY, ScoutingLogs.FILE_NAME)))

    init {
        File(FILES_DIRECTORY, "match_data.json").overwriteString(matchData?.toString())
        File(FILES_DIRECTORY, "team_data.json").overwriteString(teamData?.toString())
    }

    sealed class NavTarget : Parcelable {
        @Parcelize
        data object MainMenu : NavTarget()

        @Parcelize
        data object MatchScouting : NavTarget()

        @Parcelize
        data object LoginPage : NavTarget()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun buildChildNode(navTarget: NavTarget, nodeContext: NodeContext): Node<*> =
        when (navTarget) {
            NavTarget.LoginPage -> node(nodeContext) {
                LoginMenu(backStack)
            }

            NavTarget.MainMenu -> node(nodeContext) { modifier ->
                MainMenu(
                    backStack,
                    modifier
                )
            }

            NavTarget.MatchScouting -> AutoTeleSelectorNode(
                nodeContext,
                backStack
            )
        }

    @Composable
    override fun Content(modifier: Modifier) {
        CompositionLocalProvider(
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