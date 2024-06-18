package com.dede.android_eggs.views.crash

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.BuildConfig
import com.dede.android_eggs.R
import com.dede.android_eggs.util.ThemeUtils
import com.dede.android_eggs.util.copy
import com.dede.android_eggs.views.crash.GlobalExceptionHandler.Companion.getUncaughtException
import com.dede.android_eggs.views.main.EasterEggsActivity
import com.dede.android_eggs.views.theme.AppTheme
import kotlin.system.exitProcess

class CrashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.tryApplyOLEDTheme(this)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val tr = getUncaughtException(intent)
        if (tr == null) {
            finish()
            return
        }

        val title = "[Bug] App Crash: %s".format(tr::class.java.simpleName)
        val body = buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(
                    "Device: %s (%s - %s), SDK: %s (%d), App: %s (%d)".format(
                        Build.MODEL, Build.BRAND, Build.DEVICE,
                        Build.VERSION.RELEASE, Build.VERSION.SDK_INT,
                        BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE,
                    )
                )
            }
            append("\n\n")
            append(Log.getStackTraceString(tr))
        }
        setContent {
            AppTheme {
                Surface {
                    CrashScreen(title, body)
                }
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true)
private fun CrashScreen(
    title: String = "[Bug] App Crash: java.lang.IllegalStateException: xxx",
    body: AnnotatedString? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Rounded.SmartToy,
                contentDescription = null,
                modifier = Modifier
                    .statusBarsPadding()
                    .size(42.dp),
                tint = colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(id = R.string.app_name),
                style = typography.titleMedium
            )
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                onClick = {
                    expanded = !expanded
                },
                shape = shapes.extraLarge,
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.9f)
                    .navigationBarsPadding()
                    .animateContentSize()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (expanded) {
                        Text(
                            text = body as AnnotatedString,
                            modifier = Modifier
                                .weight(1f)
                                .padding(16.dp),
                            style = typography.bodySmall,
                        )
                    } else {
                        Text(
                            text = title,
                            modifier = Modifier
                                .weight(1f)
                                .padding(16.dp),
                            maxLines = 1,
                            style = typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    if (!expanded) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        IconButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 12.dp, end = 12.dp)
                .statusBarsPadding(),
            onClick = {
                exitProcess(0)
            },
        ) {
            Icon(
                imageVector = Icons.Rounded.PowerSettingsNew,
                contentDescription = null
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
                .navigationBarsPadding()
                .fillMaxWidth(fraction = 0.8f),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            FloatingActionButton(
                onClick = {
                    context.startActivity(
                        Intent(context, EasterEggsActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    )
                },
                shape = FloatingActionButtonDefaults.largeShape
            ) {
                Icon(
                    imageVector = Icons.Rounded.RestartAlt,
                    contentDescription = null
                )
            }
            FloatingActionButton(
                onClick = {
                    context.copy(title + "\n\n" + body)
                },
                shape = FloatingActionButtonDefaults.largeShape
            ) {
                Icon(
                    imageVector = Icons.Rounded.ContentCopy,
                    contentDescription = null
                )
            }
            FloatingActionButton(
                onClick = {
                    val uri = Uri.parse(context.getString(R.string.url_github_issues))
                        .buildUpon()
                        .appendPath("new")
                        .appendQueryParameter("title", title)
                        .appendQueryParameter("body", "```\n$body\n```")
                        .build()
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    } catch (_: ActivityNotFoundException) {
                    }
                    context.copy(title + "\n\n" + body)
                },
                shape = FloatingActionButtonDefaults.largeShape
            ) {
                Icon(
                    imageVector = Icons.Rounded.BugReport,
                    contentDescription = null
                )
            }
        }
    }
}