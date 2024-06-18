package pages

import android.content.Context
import android.hardware.usb.UsbManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.operation.push
import compKey
import defaultSecondary
import getCurrentTheme
import getJsonFromMatchHash
import getLastSynced
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import matchData
import nodes.LocalAppConfiguration
import nodes.LocalScoutingLog
import nodes.LocalScoutingLogs
import nodes.RootNode
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import scouting_app.composeapp.generated.resources.Res
import scouting_app.composeapp.generated.resources.checkmark
import scouting_app.composeapp.generated.resources.crossmark
import sendData
import sendDataUSB
import setTeam
import syncTeamsAndMatches
import teamData

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun MainMenu(
    backStack: BackStack<RootNode.NavTarget>,
    modifier: Modifier,
) {
    val context = LocalContext.current
    var selectedPlacement by remember { mutableStateOf(false) }
    var teamSyncedResource: DrawableResource by remember { mutableStateOf(if (teamData == null) Res.drawable.crossmark else Res.drawable.checkmark) }
    var matchSyncedResource: DrawableResource by remember { mutableStateOf(if (matchData == null) Res.drawable.crossmark else Res.drawable.checkmark) }
    var serverDialogOpen by remember { mutableStateOf(false) }

    var ipAddressErrorDialog by remember { mutableStateOf(false) }
    var deviceListOpen by remember { mutableStateOf(false) }
    val manager = context.getSystemService(Context.USB_SERVICE) as UsbManager

    var setEventCode by remember { mutableStateOf(false) }
    var tempCompKey by remember { mutableStateOf(compKey) }

    val deviceList = manager.deviceList
    val scoutingLog = LocalScoutingLog.current
    val scoutingLogs = LocalScoutingLogs.current

    val appConfiguration = LocalAppConfiguration.current

    Column(modifier = Modifier.verticalScroll(ScrollState(0))) {
        DropdownMenu(expanded = deviceListOpen, onDismissRequest = { deviceListOpen = false }) {
            deviceList.forEach { (name, _) ->
                Log.i("USB", name)
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = { sendDataUSB(name, manager, getJsonFromMatchHash(scoutingLogs)) })
            }
        }
        if (setEventCode) {
            Dialog(onDismissRequest = {
                setEventCode = false
                compKey = tempCompKey
            }) {
                Column {
                    Text("Enter new event code")
                    TextField(tempCompKey, { tempCompKey = it })
                }
            }
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { backStack.push(RootNode.NavTarget.LoginPage) }, modifier = Modifier
                    .scale(0.75f)
                    .align(Alignment.CenterStart)
            ) {
                Text(text = "Login", color = getCurrentTheme().onPrimary)
            }

            Text(
                text = "Bear Metal Scout App",
                fontSize = 25.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        HorizontalDivider(color = getCurrentTheme().onSurface, thickness = 2.dp)
        Text(
            text = "Hello ${appConfiguration.value.scoutName()}",
            color = getCurrentTheme().onPrimary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        OutlinedButton(
            border = BorderStroke(3.dp, Color.Yellow),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = defaultSecondary),
            contentPadding = PaddingValues(horizontal = 60.dp, vertical = 5.dp),
            onClick = {
                syncTeamsAndMatches(false, context.filesDir)
                selectedPlacement = true
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 50.dp, vertical = 50.dp),
        ) {
            Text(
                text = "Match",
                color = getCurrentTheme().onPrimary,
                fontSize = 35.sp
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .offset((-100).dp, (-50).dp)
        ) {
            DropdownMenu(
                expanded = selectedPlacement,
                onDismissRequest = { selectedPlacement = false },
                modifier = Modifier
                    .size(200.dp, 332.dp)
                    .background(color = Color(0, 0, 0))
            ) {
                val matchScouting = { pos: Int ->
                    scoutingLog.value = scoutingLog.value.copy(robotStartPosition = pos)
                    backStack.push(RootNode.NavTarget.MatchScouting)

                    setTeam(scoutingLog.value.match, scoutingLog.value.robotStartPosition) {
                        scoutingLog.value = scoutingLog.value.copy(team = it)
                    }
                }

                (1..3).forEach {
                    Row {
                        DropdownMenuItem(
                            onClick = {
                                matchScouting(it - 1)
                            },
                            modifier = Modifier
                                .border(BorderStroke(color = Color.Yellow, width = 3.dp))
                                .size(100.dp, 100.dp)
                                .background(color = Color(60, 30, 30)),
                            text = { Text("R$it", fontSize = 22.sp, color = Color.White) }
                        )
                        DropdownMenuItem(
                            onClick = {
                                matchScouting(it + 2)
                            },
                            modifier = Modifier
                                .border(BorderStroke(color = Color.Yellow, width = 3.dp))
                                .size(100.dp, 100.dp)
                                .background(color = Color(30, 30, 60)),
                            text = { Text("B$it", fontSize = 22.sp, color = Color.White) }
                        )
                    }
                }
            }
//            OutlinedButton(
//                border = BorderStroke(3.dp, Color.Yellow),
//                shape = RoundedCornerShape(25.dp),
//                contentPadding = PaddingValues(horizontal = 80.dp, vertical = 5.dp),
//                colors = ButtonDefaults.buttonColors(containerColor = defaultSecondary),
//                onClick = {
//                    backStack.push(RootNode.NavTarget.PitsScouting)
//                },
//                modifier = Modifier
//                    .align(Alignment.Center)
//                    .padding(horizontal = 50.dp, vertical = 50.dp),
//
//                ) {
//                Text(
//                    text = "Pits",
//                    color = getCurrentTheme().onPrimary,
//                    fontSize = 35.sp
//                )
//            }

            OutlinedButton(
                border = BorderStroke(3.dp, Color.Yellow),
                shape = RoundedCornerShape(25.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 15.dp),
                colors = ButtonDefaults.buttonColors(containerColor = defaultSecondary),
                onClick = {
                    val scope = CoroutineScope(Dispatchers.Default)
                    scope.launch {
                        syncTeamsAndMatches(true, context.filesDir)
                        teamSyncedResource =
                            if (teamData == null) Res.drawable.crossmark else Res.drawable.checkmark
                        matchSyncedResource =
                            if (matchData == null) Res.drawable.crossmark else Res.drawable.checkmark
                    }
                },
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 50.dp, vertical = 50.dp),
            ) {
                Column {
                    Text(
                        text = "Sync",
                        color = getCurrentTheme().onPrimary,
                        fontSize = 35.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Text(
                        text = "Last synced ${getLastSynced()}",
                        fontSize = 12.sp,
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(Modifier.fillMaxWidth(1f / 2f)) {
                        Text("Robot List")

                        Image(
                            painterResource(teamSyncedResource),
                            contentDescription = "status",
                            modifier = Modifier
                                .size(30.dp)
                                .align(Alignment.CenterEnd)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(Modifier.fillMaxWidth(1f / 2f)) {
                        Text("Match List")

                        Image(
                            painterResource(matchSyncedResource),
                            contentDescription = "status",
                            modifier = Modifier
                                .size(30.dp)
                                .align(Alignment.CenterEnd)
                        )
                    }
                }
            }

            OutlinedButton(
                border = BorderStroke(3.dp, Color.Yellow),
                shape = RoundedCornerShape(25.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 15.dp),
                colors = ButtonDefaults.buttonColors(containerColor = defaultSecondary),
                onClick = {
                    serverDialogOpen = true
                    //deviceListOpen = true
                }
            ) {
                Text("Export")
            }

            Box(modifier = Modifier.fillMaxSize()) {
                OutlinedButton(
                    border = BorderStroke(3.dp, Color.Yellow),
                    shape = RoundedCornerShape(25.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 15.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = defaultSecondary),
                    onClick = {
                        setEventCode = true
                        teamSyncedResource = Res.drawable.crossmark
                        matchSyncedResource = Res.drawable.crossmark
                    },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Text("Set custom event key", fontSize = 9.sp)
                }
            }
            Text(tempCompKey)

            if (serverDialogOpen) {
                Dialog(
                    onDismissRequest = {
                        serverDialogOpen = false
                        if (ipAddress.value.matches(Regex("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}\$"))) {
                            CoroutineScope(Dispatchers.Default).launch {
                                sendData(ipAddress.value, getJsonFromMatchHash(scoutingLogs))
                            }
                        } else
                            ipAddressErrorDialog = true

                    }
                ) {
                    Column {
                        Text("Set Device Address", fontSize = 24.sp)
                        TextField(
                            ipAddress.value,
                            onValueChange = { ipAddress.value = it }
                        )
                    }
                }
            }

            if (ipAddressErrorDialog) {
                BasicAlertDialog(
                    onDismissRequest = { ipAddressErrorDialog = false }
                ) {
                    Text("Ip Address was entered incorrectly, check address and enter again")
                }
            }
        }
    }
}

var ipAddress = mutableStateOf("127.0.0.1")
val openError = mutableStateOf(false)