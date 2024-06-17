package nodes

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
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
import pages.MainMenu
import pages.PitsScoutMenu
import java.lang.Integer.parseInt


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
    private var team = mutableIntStateOf(1)
    private var robotStartPosition = mutableIntStateOf(0)
    private var pitsPerson = mutableStateOf("P1")
    private var comp = mutableStateOf("")

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
            NavTarget.LoginPage -> node(nodeContext) { LoginMenu(backStack, scoutName, comp) }
            is NavTarget.MainMenu -> node(nodeContext) { modifier ->
                MainMenu(
                    nodeContext,
                    backStack,
                    robotStartPosition,
                    scoutName,
                    comp,
                    team,
                    modifier
                )
            }

            NavTarget.MatchScouting -> AutoTeleSelectorNode(
                nodeContext,
                robotStartPosition,
                team,
                backStack
            )

            NavTarget.PitsScouting -> node(nodeContext) {
                PitsScoutMenu(
                    backStack,
                    pitsPerson,
                    scoutName
                )
            }
        }

    @Composable
    override fun Content(modifier: Modifier) {
        Column {
            AppyxNavigationContainer(
                appyxComponent = backStack,
                modifier = Modifier.weight(0.9f)
            )
        }
    }
}

var scoutName = mutableStateOf("")
val matchScoutArray = HashMap<Int, HashMap<Int, String>>()

fun loadData(match: Int, team: MutableIntState, robotStartPosition: MutableIntState) {
    reset()
    if (matchScoutArray[robotStartPosition.intValue]?.get(match)?.isEmpty() == false) {
        fun intToState(i: Int) = when (i) {
            0 -> ToggleableState.Off
            1 -> ToggleableState.Indeterminate
            2 -> ToggleableState.On
            else -> ToggleableState.Off
        }

        val help =
            matchScoutArray[robotStartPosition.intValue]?.get(match)?.split('/') ?: createOutput(
                team,
                robotStartPosition
            ).split('/')
        team.intValue = parseInt(help[1])

        autoSpeakerNum.intValue = parseInt(help[3])
        autoAmpNum.intValue = parseInt(help[4])
        autoSMissed.intValue = parseInt(help[5])
        autoAMissed.intValue = parseInt(help[6])
        autoStop.intValue = parseInt(help[7])
        telePassed.intValue = parseInt(help[8])
        teleSpeakerNum.intValue = parseInt(help[9])
        teleAmpNum.intValue = parseInt(help[10])
        teleTrapNum.intValue = parseInt(help[11])
        teleSMissed.intValue = parseInt(help[12])
        teleAMissed.intValue = parseInt(help[13])
        teleSReceived.intValue = parseInt(help[14])
        teleAReceived.intValue = parseInt(help[15])
        lostComms.intValue = parseInt(help[16])

        val teleCommentsSplit = help[17].split(':')
        autos.value = teleCommentsSplit[1]
        teleNotes.value = teleCommentsSplit[2]
        scoutName.value = teleCommentsSplit[3]
        println(autos)

    }
}

fun reset() {
    autoSpeakerNum.intValue = 0
    autoAmpNum.intValue = 0
    collected.intValue = 0
    autoSMissed.intValue = 0
    autoAMissed.intValue = 0
    autos.value = ""
    lostComms.intValue = 0
    teleSpeakerNum.intValue = 0
    teleAmpNum.intValue = 0
    teleTrapNum.intValue = 0
    teleSMissed.intValue = 0
    teleAMissed.intValue = 0
    teleSReceived.intValue = 0
    teleAReceived.intValue = 0
    autoStop.intValue = 0
    telePassed.intValue = 0
    teleNotes.value = ""
}
