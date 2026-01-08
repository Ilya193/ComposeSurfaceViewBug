package ru.ikom.composesurfaceviewbug

import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val layout = true

        if (layout) {
            setContentView(R.layout.activity_main)

            val composeView = findViewById<ComposeView>(R.id.compose_view)

            composeView.apply {
                setContent {
                    ComposeSurfaceView()
                }
            }
        }
        else {
            setContent {
                ComposeSurfaceView()
            }
        }
    }
}
sealed interface Content {
    data object ContentA : Content
    data object ContentB : Content
}

@Composable
fun ComposeSurfaceView() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val content = remember { mutableStateOf<Content>(Content.ContentA) }

        when (content.value) {
            Content.ContentA -> ContentA(
                onOpenB = { content.value = Content.ContentB }
            )
            Content.ContentB -> ContentB(
                onBack = { content.value = Content.ContentA }
            )
        }
    }
}

@Composable
fun ContentA(
    onOpenB: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Yellow)
            .clickable(
                interactionSource = null,
                indication = null,
                onClick = onOpenB
            )
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "ContentA"
        )
    }
}

@Composable
fun ContentB(
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = null,
                indication = null,
                onClick = onBack
            )
    ) {
        val callback = remember {
            object : SurfaceHolder.Callback {
                override fun surfaceChanged(
                    holder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int
                ) {
                    drawRed(holder)
                }

                override fun surfaceCreated(holder: SurfaceHolder) {
                    drawRed(holder)
                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {

                }

                private fun drawRed(holder: SurfaceHolder) {
                    val canvas = holder.lockCanvas()
                    try {
                        canvas.drawColor(android.graphics.Color.RED)
                    } finally {
                        holder.unlockCanvasAndPost(canvas)
                    }
                }
            }
        }

        AndroidView(
            factory = { context ->
                SurfaceView(context).apply {
                    holder.addCallback(callback)
                }
            },
            onRelease = { view ->
                view.holder.removeCallback(callback)
            }
        )

        /*Button(
            modifier = Modifier
                .align(Alignment.CenterStart),
            onClick = onBack
        ) {
            Text(text = "back")
        }*/
    }
}