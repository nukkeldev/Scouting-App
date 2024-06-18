package pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.operation.push
import composables.EnumerableValue
import defaultOnPrimary
import defaultSecondary
import nodes.AutoTeleSelectorNode
import nodes.LocalAppConfiguration
import nodes.LocalScoutingLogs
import nodes.RootNode

@Composable
actual fun AutoMenu(
    backStack: BackStack<AutoTeleSelectorNode.NavTarget>,
    mainMenuBackStack: BackStack<RootNode.NavTarget>,
) {
    val scrollState = rememberScrollState(0)
    val scoutingLogs = LocalScoutingLogs.current.value
    val scoutingLog = scoutingLogs.currentLog
    val scoutingLogValue = scoutingLog.value!!
    val scoutName = LocalAppConfiguration.current.value.scoutName()

    Column(
        Modifier
            .verticalScroll(state = scrollState)
            .padding(20.dp)
    ) {
        EnumerableValue(
            label = "Speaker",
            get = { scoutingLogValue.autoSpeakerNum },
            set = { scoutingLog.value = scoutingLogValue.copy(autoSpeakerNum = it) })
        EnumerableValue(
            label = "Amp",
            get = { scoutingLogValue.autoAmpNum },
            set = { scoutingLog.value = scoutingLogValue.copy(autoAmpNum = it) })

        Spacer(modifier = Modifier.height(10.dp))

        EnumerableValue(
            label = "S Missed",
            get = { scoutingLogValue.autoSMissed },
            set = { scoutingLog.value = scoutingLogValue.copy(autoSMissed = it) })
        EnumerableValue(
            label = "A Missed",
            get = { scoutingLogValue.autoAMissed },
            set = { scoutingLog.value = scoutingLogValue.copy(autoAMissed = it) })

        OutlinedTextField(
            value = scoutingLogValue.autos,
            onValueChange = { scoutingLog.value = scoutingLogValue.copy(autos = it) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Cyan,
                unfocusedBorderColor = Color.Yellow,
                focusedContainerColor = Color(6, 9, 13),
                unfocusedContainerColor = Color(6, 9, 13),
                focusedTextColor = defaultOnPrimary,
                unfocusedTextColor = defaultOnPrimary,
                cursorColor = defaultOnPrimary
            ),
            shape = RoundedCornerShape(15.dp),
            placeholder = { Text("Autos", color = Color.White) },
            modifier = Modifier
                .fillMaxWidth(9f / 10f)
                .align(Alignment.CenterHorizontally)
                .height(70.dp)
        )

        Row() {
            Text(
                text = "Auto Stop ⚠\uFE0F",
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Checkbox(
                scoutingLogValue.autoStop,
                colors = CheckboxDefaults.colors(checkedColor = Color.Cyan),
                onCheckedChange = {
                    scoutingLog.value = scoutingLogValue.copy(autoStop = it)
                }
            )
        }

        Spacer(Modifier.height(5.dp))

        OutlinedButton(
            border = BorderStroke(2.dp, color = Color.Yellow),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = defaultSecondary),
            onClick = {
                backStack.push(AutoTeleSelectorNode.NavTarget.TeleScouting)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Tele",
                color = Color.Yellow,
                fontSize = 35.sp
            )
        }

        Spacer(Modifier.height(25.dp))

        OutlinedButton(
            border = BorderStroke(2.dp, color = Color.Yellow),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = defaultSecondary),
            onClick = { scoutingLogs.saveCurrentLog() },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                text = "Back",
                color = Color.Yellow
            )
        }
        Text(scoutName)
    }
}