package composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState

@Composable
expect fun EnumerableValue(
    label: String, get: () -> Int, set: (Int) -> Unit
)