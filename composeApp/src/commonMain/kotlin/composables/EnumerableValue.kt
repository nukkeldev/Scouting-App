package composables

import androidx.compose.runtime.Composable

@Composable
expect fun EnumerableValue(
    label: String, get: () -> Int, set: (Int) -> Unit
)