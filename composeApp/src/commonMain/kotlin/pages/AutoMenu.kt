package pages

import androidx.compose.runtime.Composable
import com.bumble.appyx.components.backstack.BackStack
import nodes.AutoTeleSelectorNode
import nodes.RootNode

@Composable
expect fun AutoMenu(
    backStack: BackStack<AutoTeleSelectorNode.NavTarget>,
    mainMenuBackStack: BackStack<RootNode.NavTarget>,
)