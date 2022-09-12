package com.rerere.iwara4a.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.rerere.iwara4a.R
import com.rerere.iwara4a.data.model.index.MediaPreview
import com.rerere.iwara4a.data.model.index.MediaType
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.ui.modifier.nsfw
import me.rerere.slantedtext.SlantedMode
import me.rerere.slantedtext.SlantedText

@Composable
fun MediaPreviewCard(navController: NavController = LocalNavController.current, mediaPreview: MediaPreview) {
    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .semantics {
                 contentDescription = "Media Preview Card"
            },
        onClick = {
            if (mediaPreview.type == MediaType.VIDEO) {
                navController.navigate("video/${mediaPreview.mediaId}")
            } else if (mediaPreview.type == MediaType.IMAGE) {
                navController.navigate("image/${mediaPreview.mediaId}")
            }
        }
    ) {
        SlantedText(
            visible = mediaPreview.private,
            text = "私有",
            textSize = 20.sp,
            thickness = 25.dp,
            padding = 20.dp,
            slantedMode = SlantedMode.TOP_LEFT,
            backGroundColor = MaterialTheme.colorScheme.primary,
            textColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16 / 9f)
                        .nsfw(),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    model = mediaPreview.previewPic
                )


                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Icon(
                            modifier = Modifier.size(15.dp),
                            painter =  when(mediaPreview.type){
                                MediaType.VIDEO -> painterResource(R.drawable.play_icon)
                                MediaType.IMAGE -> painterResource(R.drawable.view_icon)
                            },
                            contentDescription = null
                        )
                        Text(text = mediaPreview.watchs, fontSize = 13.sp)
                        Icon(
                            modifier = Modifier.size(15.dp),
                            painter = painterResource(R.drawable.like_icon),
                            contentDescription = null
                        )
                        Text(text = mediaPreview.likes, fontSize = 13.sp)
                        Spacer(Modifier.weight(1f))
                        Icon(
                            modifier = Modifier.size(20.dp),
                            painter =  when(mediaPreview.type){
                                MediaType.VIDEO -> painterResource(R.drawable.video_icon)
                                MediaType.IMAGE -> painterResource(R.drawable.image_icon)
                            },
                            contentDescription = null
                        )
                    }

                    Text(
                        text = mediaPreview.title.trim(),
                        maxLines = 1,
                        fontWeight = FontWeight.Medium
                    )
                    if (mediaPreview.author.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = mediaPreview.author, maxLines = 1, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}