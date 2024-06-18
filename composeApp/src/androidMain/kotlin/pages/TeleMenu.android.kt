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
import nodes.AutoTeleSelectorNode
import nodes.LocalScoutingLogs
import nodes.RootNode
import setTeam

@Composable
actual fun TeleMenu(
    backStack: BackStack<AutoTeleSelectorNode.NavTarget>,
    mainMenuBackStack: BackStack<RootNode.NavTarget>,
) {
    val context = LocalContext.current

    val scrollState = rememberScrollState(0)
    val scoutingLogs = LocalScoutingLogs.current
    val scoutingLog = scoutingLogs.value.currentLog
    val scoutingLogValue = scoutingLog.value!!

    Column(
        Modifier
            .verticalScroll(state = scrollState)
            .padding(20.dp)
    ) {

        EnumerableValue(
            label = "Speaker",
            get = { scoutingLogValue.teleSpeakerNum },
            set = { scoutingLog.value = scoutingLogValue.copy(teleSpeakerNum = it) })
        EnumerableValue(
            label = "Amp",
            get = { scoutingLogValue.teleAmpNum },
            set = { scoutingLog.value = scoutingLogValue.copy(teleAmpNum = it) })
        EnumerableValue(
            label = "Passed",
            get = { scoutingLogValue.telePassed },
            set = { scoutingLog.value = scoutingLogValue.copy(telePassed = it) })
        EnumerableValue(
            label = "Trap",
            get = { scoutingLogValue.teleTrapNum },
            set = { scoutingLog.value = scoutingLogValue.copy(teleTrapNum = it) })

        Spacer(modifier = Modifier.height(30.dp))

        EnumerableValue(
            label = "S Missed",
            get = { scoutingLogValue.teleSMissed },
            set = { scoutingLog.value = scoutingLogValue.copy(teleSMissed = it) })
        EnumerableValue(
            label = "A Missed",
            get = { scoutingLogValue.teleAMissed },
            set = { scoutingLog.value = scoutingLogValue.copy(teleAMissed = it) })

        Spacer(modifier = Modifier.height(30.dp))

        EnumerableValue(
            label = "S Received",
            get = { scoutingLogValue.teleSReceived },
            set = { scoutingLog.value = scoutingLogValue.copy(teleSReceived = it) })
        EnumerableValue(
            label = "A Received",
            get = { scoutingLogValue.teleAReceived },
            set = { scoutingLog.value = scoutingLogValue.copy(teleAReceived = it) })

        Row {
            Text("Lost Comms?")
            Checkbox(
                scoutingLogValue.lostComms,
                onCheckedChange = {
                    scoutingLog.value = scoutingLogValue.copy(lostComms = it)
                })
        }


        HorizontalDivider(color = Color.Yellow, thickness = 4.dp)

        Comments({ scoutingLogValue.teleNotes }, { scoutingLog.value = scoutingLogValue.copy(teleNotes = it) })

        Spacer(Modifier.height(15.dp))

        OutlinedButton(
            border = BorderStroke(3.dp, Color.Yellow),
            shape = RoundedCornerShape(25.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 15.dp),
            colors = ButtonDefaults.buttonColors(containerColor = defaultSecondary),
            onClick = {
                val match = scoutingLogValue.match
                val pos = scoutingLogValue.position

                scoutingLogs.value.setCurrentLog(match, pos)
                scoutingLogs.value.exportToFile(context.filesDir)

                backStack.pop()
                setTeam(match, pos) { scoutingLog.value = scoutingLogValue.copy(team = it) }
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
