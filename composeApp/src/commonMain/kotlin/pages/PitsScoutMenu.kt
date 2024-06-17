package pages

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.bumble.appyx.components.backstack.BackStack
import nodes.RootNode

@SuppressLint("NewApi")
@RequiresApi(Build.VERSION_CODES.P)
@Composable
expect fun PitsScoutMenu(
    backStack: BackStack<RootNode.NavTarget>,
    pitsPerson: MutableState<String>,
    scoutName: MutableState<String>
)
