import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.bumble.appyx.navigation.integration.IntegrationPoint
import com.bumble.appyx.navigation.integration.NodeHost
import com.bumble.appyx.navigation.platform.AndroidLifecycle
import nodes.RootNode

@Composable
fun App(integrationPoint: IntegrationPoint) {
    MaterialTheme(
        colorScheme = defaultScheme
    ) {
        Surface(color = MaterialTheme.colorScheme.background) {
            NodeHost(
                lifecycle = AndroidLifecycle(LocalLifecycleOwner.current.lifecycle),
                integrationPoint = integrationPoint,
            ) {
                RootNode(
                    nodeContext = it,
                )
            }
        }
    }
}