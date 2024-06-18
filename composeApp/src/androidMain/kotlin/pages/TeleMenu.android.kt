package pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.operation.pop
import composables.Comments
import composables.EnumerableValue
import defaultSecondary
import exportScoutingData
import nodes.AutoTeleSelectorNode
import nodes.LocalAppConfiguration
import nodes.LocalScoutingLog
import nodes.LocalScoutingLogs
import nodes.RootNode
import ScoutingLog
import nodes.tryLoadScoutingLog
import setTeam

@Composable
actual fun TeleMenu(
    backStack: BackStack<AutoTeleSelectorNode.NavTarget>,
    mainMenuBackStack: BackStack<RootNode.NavTarget>,
) {
    val context = LocalContext.current

    val scrollState = rememberScrollState(0)
    val scoutingLog = LocalScoutingLog.current
    val scoutingLogs = LocalScoutingLogs.current

    Column(
        Modifier
            .verticalScroll(state = scrollState)
            .padding(20.dp)
    ) {

        EnumerableValue(
            label = "Speaker",
            get = { scoutingLog.value.teleSpeakerNum },
            set = { scoutingLog.value = scoutingLog.value.copy(teleSpeakerNum = it) })
        EnumerableValue(
            label = "Amp",
            get = { scoutingLog.value.teleAmpNum },
            set = { scoutingLog.value = scoutingLog.value.copy(teleAmpNum = it) })
        EnumerableValue(
            label = "Passed",
            get = { scoutingLog.value.telePassed },
            set = { scoutingLog.value = scoutingLog.value.copy(telePassed = it) })
        EnumerableValue(
            label = "Trap",
            get = { scoutingLog.value.teleTrapNum },
            set = { scoutingLog.value = scoutingLog.value.copy(teleTrapNum = it) })

        Spacer(modifier = Modifier.height(30.dp))

        EnumerableValue(
            label = "S Missed",
            get = { scoutingLog.value.teleSMissed },
            set = { scoutingLog.value = scoutingLog.value.copy(teleSMissed = it) })
        EnumerableValue(
            label = "A Missed",
            get = { scoutingLog.value.teleAMissed },
            set = { scoutingLog.value = scoutingLog.value.copy(teleAMissed = it) })

        Spacer(modifier = Modifier.height(30.dp))

        EnumerableValue(
            label = "S Received",
            get = { scoutingLog.value.teleSReceived },
            set = { scoutingLog.value = scoutingLog.value.copy(teleSReceived = it) })
        EnumerableValue(
            label = "A Received",
            get = { scoutingLog.value.teleAReceived },
            set = { scoutingLog.value = scoutingLog.value.copy(teleAReceived = it) })

        Row {
            Text("Lost Comms?")
            Checkbox(
                scoutingLog.value.lostComms,
                onCheckedChange = {
                    scoutingLog.value = scoutingLog.value.copy(lostComms = it)
                })
        }


        HorizontalDivider(color = Color.Yellow, thickness = 4.dp)

        Comments({ scoutingLog.value.teleNotes ?: "" }, { scoutingLog.value = scoutingLog.value.copy(teleNotes = it) })

        Spacer(Modifier.height(15.dp))

        OutlinedButton(
            border = BorderStroke(3.dp, Color.Yellow),
            shape = RoundedCornerShape(25.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 15.dp),
            colors = ButtonDefaults.buttonColors(containerColor = defaultSecondary),
            onClick = {
                val pos = scoutingLog.value.robotStartPosition ?: return@OutlinedButton
                val match = scoutingLog.value.match ?: return@OutlinedButton

                scoutingLogs.putIfAbsent(pos, mutableMapOf())
                scoutingLogs[pos]?.set(match, scoutingLog.value.toString())

                scoutingLog.value = ScoutingLog(
                    team = scoutingLog.value.team,
                    match = match + 1,
                    robotStartPosition = pos
                )
                exportScoutingData(context.filesDir, scoutingLogs)
                tryLoadScoutingLog(scoutingLog, scoutingLogs)

                backStack.pop()
                setTeam(match, pos) { scoutingLog.value = scoutingLog.value.copy(team = it) }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Next Match", fontSize = 20.sp)
        }

        OutlinedButton(
            border = BorderStroke(2.dp, color = Color.Yellow),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = defaultSecondary),
            onClick = {
                TODO()
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                text = "Back",
                color = Color.Yellow
            )
        }
    }
}
