package nodes

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
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
import pages.AutoMenu
import pages.TeleMenu

class AutoTeleSelectorNode(
    nodeContext: NodeContext,
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
                    mainMenuBackStack
                )
            }

            NavTarget.TeleScouting -> node(nodeContext) {
                TeleMenu(
                    backStack,
                    mainMenuBackStack
                )
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

