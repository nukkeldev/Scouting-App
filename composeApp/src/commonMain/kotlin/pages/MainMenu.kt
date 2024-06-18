package pages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.backstack.BackStack
import nodes.RootNode

@RequiresApi(Build.VERSION_CODES.O)
@Composable
expect fun MainMenu(
    backStack: BackStack<RootNode.NavTarget>,
    modifier: Modifier,
)
