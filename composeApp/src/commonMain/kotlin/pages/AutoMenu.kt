package pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import com.bumble.appyx.components.backstack.BackStack
import nodes.AutoTeleSelectorNode
import nodes.RootNode
import nodes.ScoutingLog

@Composable
expect fun AutoMenu(
    backStack: BackStack<AutoTeleSelectorNode.NavTarget>,
    mainMenuBackStack: BackStack<RootNode.NavTarget>,
    scoutingLog: MutableState<ScoutingLog>,
    scoutName: String,
    saveLog: (clear: Boolean) -> Unit
)