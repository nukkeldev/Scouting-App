package nodes

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
import pages.AutoMenu
import pages.AutoTeleSelectorMenu
import pages.TeleMenu

class AutoTeleSelectorNode(
    nodeContext: NodeContext,
    private var robotStartPosition: MutableIntState,
    private val team: MutableIntState,
    private val mainMenuBackStack: BackStack<RootNode.NavTarget>,
    private val backStack: BackStack<NavTarget> = BackStack(
        model = BackStackModel(
            initialTarget = NavTarget.AutoScouting,
            savedStateMap = nodeContext.savedStateMap
        ),
        visualisation = { BackStackFader(it) }
    )
) : Node<AutoTeleSelectorNode.NavTarget>(
    appyxComponent = backStack,
    nodeContext
) {
    private val selectAuto = mutableStateOf(false)

    sealed class NavTarget {
        data object AutoScouting : NavTarget()
        data object TeleScouting : NavTarget()
    }

    override fun buildChildNode(navTarget: NavTarget, nodeContext: NodeContext): Node<*> =
        when (navTarget) {
            NavTarget.AutoScouting -> node(nodeContext) {
                AutoMenu(
                    backStack,
                    mainMenuBackStack,
                    selectAuto,
                    match,
                    team,
                    robotStartPosition
                )
            }

            NavTarget.TeleScouting -> node(nodeContext) {
                TeleMenu(
                    backStack,
                    mainMenuBackStack,
                    selectAuto,
                    match,
                    team,
                    robotStartPosition
                )
            }
        }

    @Composable
    override fun Content(modifier: Modifier) {
        Column {
            AutoTeleSelectorMenu(
                match,
                team,
                robotStartPosition,
                selectAuto,
                backStack,
                mainMenuBackStack
            )
            AppyxNavigationContainer(
                appyxComponent = backStack,
                modifier = Modifier.weight(0.9f)
            )
        }
    }
}


val match = mutableStateOf("1")
val autoSpeakerNum = mutableIntStateOf(0)
val autoAmpNum = mutableIntStateOf(0)
val collected = mutableIntStateOf(0)
val autoSMissed = mutableIntStateOf(0)
val autoAMissed = mutableIntStateOf(0)
var autos = mutableStateOf("")
val teleSpeakerNum = mutableIntStateOf(0)
val teleAmpNum = mutableIntStateOf(0)
val telePassed = mutableIntStateOf(0)
val teleTrapNum = mutableIntStateOf(0)
val teleSMissed = mutableIntStateOf(0)
val teleAMissed = mutableIntStateOf(0)
val teleSReceived = mutableIntStateOf(0)
val teleAReceived = mutableIntStateOf(0)
var lostComms = mutableIntStateOf(0)
val autoStop = mutableIntStateOf(0)
var teleNotes = mutableStateOf("")


fun createOutput(team: MutableIntState, robotStartPosition: MutableIntState): String {
    fun stateToInt(state: ToggleableState) = when (state) {
        ToggleableState.Off -> 0
        ToggleableState.Indeterminate -> 1
        ToggleableState.On -> 2
    }
    if (autos.value.isEmpty()) {
        autos.value = " "
    }
    autos.value = autos.value.replace(":", "")
    if (teleNotes.value.isEmpty()) {
        teleNotes.value = "No Comments"
    }
    teleNotes.value = teleNotes.value.replace(":", "")
    val teleNotesFinal = "autopath:${autos.value}:${teleNotes.value}:${scoutName.value}"
    return delimString(
        "/",
        match.value,
        team.intValue.toString(),
        robotStartPosition.intValue.toString(),
        autoSpeakerNum.intValue.toString(),
        autoAmpNum.intValue.toString(),
        autoSMissed.intValue.toString(),
        autoAMissed.intValue.toString(),
        autoStop.intValue.toString(),
        telePassed.intValue.toString(),
        teleSpeakerNum.intValue.toString(),
        teleAmpNum.intValue.toString(),
        teleTrapNum.intValue.toString(),
        teleSMissed.intValue.toString(),
        teleAMissed.intValue.toString(),
        teleSReceived.intValue.toString(),
        teleAReceived.intValue.toString(),
        lostComms.intValue.toString(),
        teleNotesFinal
    )
}

private fun delimString(delimiter: String, vararg inputs: String): String {
    val endString = StringBuilder()
    inputs.forEach { endString.append(it + delimiter) }
    endString.deleteAt(endString.lastIndex)
    return endString.toString()
}