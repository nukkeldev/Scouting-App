package pages

import Competition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode
import compKey
import getCurrentTheme
import nodes.LocalAppConfiguration
import nodes.RootNode
import org.jetbrains.compose.resources.painterResource
import scouting_app.composeapp.generated.resources.Res
import scouting_app.composeapp.generated.resources.logo

@Composable
fun LoginMenu() {
    var competitionDropdown by remember { mutableStateOf(false) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var selectedComp by remember { mutableStateOf(Competition.BonneyLake) }

    val appConfiguration = LocalAppConfiguration.current

    Column {
        Image(
            painter = painterResource(Res.drawable.logo),
            contentDescription = "Bear Metal Logo",
            modifier = Modifier.height(64.dp)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            val colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = getCurrentTheme().background,
                unfocusedTextColor = getCurrentTheme().onPrimary,
                focusedContainerColor = getCurrentTheme().background,
                focusedTextColor = getCurrentTheme().onPrimary,
                cursorColor = getCurrentTheme().onSecondary,
                focusedBorderColor = Color.Cyan,
                unfocusedBorderColor = getCurrentTheme().secondary
            )

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                placeholder = { Text("First Name") },
                shape = RoundedCornerShape(15.dp),
                colors = colors
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                placeholder = { Text("Last Name") },
                shape = RoundedCornerShape(15.dp),
                colors = colors
            )
        }
        Box(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = { competitionDropdown = true },
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(3.dp, color = getCurrentTheme().primaryVariant),
                colors = ButtonDefaults.buttonColors(containerColor = getCurrentTheme().primary)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Competition: ${selectedComp.displayName}",
                        color = getCurrentTheme().onPrimary,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    Text(
                        text = "V",
                        color = getCurrentTheme().onPrimary,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }
            DropdownMenu(
                expanded = competitionDropdown,
                onDismissRequest = { competitionDropdown = false; },
                modifier = Modifier.background(color = getCurrentTheme().onSurface)
            ) {
                Competition.entries.forEach { comp ->
                    DropdownMenuItem(
                        onClick = {
                            selectedComp = comp; competitionDropdown = false; compKey =
                            comp.tbaKey
                        },
                        text = {
                            Text(
                                text = comp.displayName,
                                color = getCurrentTheme().onPrimary,
                                modifier = Modifier.background(color = getCurrentTheme().onSurface)
                            )
                        }
                    )
                }
            }
        }

        HorizontalDivider(
            color = getCurrentTheme().primaryVariant,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(9f / 10f)
                .align(Alignment.End)
        ) {
            OutlinedButton(
                onClick = {
                    appConfiguration.value = appConfiguration.value.copy(
                        scout = firstName to lastName,
                        competition = selectedComp
                    )
                },
                border = BorderStroke(color = getCurrentTheme().primaryVariant, width = 2.dp),
                colors = ButtonDefaults.buttonColors(containerColor = getCurrentTheme().primary),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text(
                    text = "Submit",
                    color = getCurrentTheme().onPrimary
                )
            }
        }
    }
}
