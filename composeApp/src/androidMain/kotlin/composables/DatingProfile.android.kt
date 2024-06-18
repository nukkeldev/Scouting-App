package composables

import android.net.Uri
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import defaultOnPrimary
import defaultPrimaryVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(
    photoArray: MutableList<Uri>,
    teamName: String,
    teamNumber: String,
    driveType: String,
    motorType: String,
    auto: String,
    intakePref: String,
    concerns: String,
    scout: String,
    modifier: Modifier
){
//    SwipeToDismissBox(state = SwipeToDismissBoxState()) {
        Card(colors = CardDefaults.cardColors(containerColor = Color(15, 15, 15))) {
            Column(modifier = modifier) {
                AsyncImage(model = photoArray[0], contentDescription = "Robot Image", modifier = Modifier.clip(RoundedCornerShape(7.5.dp)))
                HorizontalDivider(color = Color.Gray)
                Row {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "$teamName ",
                            fontSize = 20.sp,
                            color = defaultOnPrimary
                        )
                        Text(
                            text = teamNumber,
                            fontSize = 20.sp,
                            color = defaultPrimaryVariant,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                    }
                }
                HorizontalDivider(color = Color(150, 150, 150))
                Text(
                    text = "About Me:",
                    fontSize = 20.sp,
                    color = defaultOnPrimary
                )
                Text(
                    text= "I like to intake using $intakePref."  +
                            "I enjoy long, luxurious walks on the beach with my intense $driveType drive. "+
                            "I especially love the feeling of sand inbetween my ${motorType}s." +
                            "When I auto I go through $auto in that order." +
                            " You should generally be concerned about my $concerns.",
                    color = defaultOnPrimary
                )
                Row(modifier = Modifier.horizontalScroll(ScrollState(0))) {
                    photoArray.forEach {
                        AsyncImage(
                            model = it,
                            contentDescription = "Robot image",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(200.dp,300.dp)
                                .clip(RoundedCornerShape(7.5.dp)
                                )
                        )
                    }
                }
                Text(text = "Scout: $scout")
            }
        }
//    }
}