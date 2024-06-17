package nodes

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
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
import org.json.JSONObject
import pages.AutoMenu
import pages.AutoTeleSelectorMenu
import pages.TeleMenu

class AutoTeleSelectorNode(
    nodeContext: NodeContext,
    private var scoutingLog: MutableState<ScoutingLog>,
    private val scoutName: String,
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
    sealed class NavTarget : Parcelable {
        @Parcelize
        data object AutoScouting : NavTarget()

        @Parcelize
        data object TeleScouting : NavTarget()
    }

    override fun buildChildNode(navTarget: NavTarget, nodeContext: NodeContext): Node<*> =
        when (navTarget) {
            NavTarget.AutoScouting -> node(nodeContext) {
                AutoMenu(
                    backStack,
                    mainMenuBackStack,
                    scoutingLog,
                    scoutName
                ) { clear ->
                    TODO()
                }
            }

            NavTarget.TeleScouting -> node(nodeContext) {
//                TeleMenu(
//                    backStack,
//                    mainMenuBackStack,
//                    selectAuto,
//                    match,
//                    team,
//                    robotStartPosition
//                )
            }
        }

    @Composable
    override fun Content(modifier: Modifier) {
        Column {
//            AutoTeleSelectorMenu(
//                match,
//                team,
//                robotStartPosition,
//                selectAuto,
//                backStack,
//                mainMenuBackStack
//            )
            AppyxNavigationContainer(
                appyxComponent = backStack,
                modifier = Modifier.weight(0.9f)
            )
        }
    }
}

data class ScoutingLog(
    val match: Int? = null,
    val team: Int? = null,
    val robotStartPosition: Int? = null,

    val autos: String? = null,
    val teleNotes: String? = null,

    val autoSpeakerNum: Int = 0,
    val autoAmpNum: Int = 0,
    val collected: Int = 0,
    val autoSMissed: Int = 0,
    val autoAMissed: Int = 0,
    val teleSpeakerNum: Int = 0,
    val teleAmpNum: Int = 0,
    val telePassed: Int = 0,
    val teleTrapNum: Int = 0,
    val teleSMissed: Int = 0,
    val teleAMissed: Int = 0,
    val teleSReceived: Int = 0,
    val teleAReceived: Int = 0,
    val lostComms: Int = 0,
    val autoStop: Boolean = false,
) {
    override fun toString(): String {
        return listOf(
            match ?: "",
            team ?: "",
            robotStartPosition ?: "",
            autos ?: "",
            teleNotes ?: "",
            autoSpeakerNum,
            autoAmpNum,
            collected,
            autoSMissed,
            autoAMissed,
            teleSpeakerNum,
            teleAmpNum,
            telePassed,
            teleTrapNum,
            teleSMissed,
            teleAMissed,
            teleSReceived,
            teleAReceived,
            lostComms,
            autoStop,
        ).joinToString("\n") { s -> s.toString().replace("\n", "") }
    }

    companion object {
        fun deserialize(data: String): ScoutingLog? {
            val values = data.lines()
            if (values.size != 20) return null;

            return ScoutingLog(
                match = values[0].toIntOrNull(),
                team = values[1].toIntOrNull(),
                robotStartPosition = values[2].toIntOrNull(),
                autos = values[3],
                teleNotes = values[4],
                autoSpeakerNum = values[5].toIntOrNull() ?: return null,
                autoAmpNum = values[6].toIntOrNull() ?: return null,
                collected = values[7].toIntOrNull() ?: return null,
                autoSMissed = values[8].toIntOrNull() ?: return null,
                autoAMissed = values[9].toIntOrNull() ?: return null,
                teleSpeakerNum = values[10].toIntOrNull() ?: return null,
                teleAmpNum = values[11].toIntOrNull() ?: return null,
                telePassed = values[12].toIntOrNull() ?: return null,
                teleTrapNum = values[13].toIntOrNull() ?: return null,
                teleSMissed = values[14].toIntOrNull() ?: return null,
                teleAMissed = values[15].toIntOrNull() ?: return null,
                teleSReceived = values[16].toIntOrNull() ?: return null,
                teleAReceived = values[17].toIntOrNull() ?: return null,
                lostComms = values[18].toIntOrNull() ?: return null,
                autoStop = values[19].toBooleanStrictOrNull() ?: return null,
            )
        }
    }
}