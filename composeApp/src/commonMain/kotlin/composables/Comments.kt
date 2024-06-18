package composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import defaultOnPrimary

@Composable
fun Comments(get: () -> String, set: (String) -> Unit) {
    Column {
        Text("Comments", fontSize = 25.sp)
        Spacer(Modifier.height(5.dp))
        Box {
            OutlinedTextField(
                value = get(),
                placeholder = {
                    Text("Write Here")
                },
                shape = RoundedCornerShape(25.dp),
                onValueChange = { set(it) },
                modifier = Modifier
                    .size(400.dp, 75.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Cyan,
                    unfocusedBorderColor = Color.Yellow,
                    focusedContainerColor = Color(6, 9, 13),
                    unfocusedContainerColor = Color(6, 9, 13),
                    focusedTextColor = defaultOnPrimary,
                    unfocusedTextColor = defaultOnPrimary,
                    cursorColor = defaultOnPrimary
                ),
            )
        }
    }
}