package com.alex.yang.alexmaptagscompose.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alex.yang.alexmaptagscompose.ui.theme.AlexMapTagsComposeTheme

/**
 * Created by AlexYang on 2025/12/10.
 *
 *
 */
@Composable
fun AddMarkerDialog(
    visible: Boolean,
    onConfirm: (name: String, description: String) -> Unit,
    onDismiss: () -> Unit
) {
    if (!visible) return

    val nameState: TextFieldState = rememberTextFieldState()
    val descState: TextFieldState = rememberTextFieldState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "新增標記") },
        text = {
            Column {
                // 名稱
                OutlinedTextField(
                    label = { Text(text = "名稱") },
                    placeholder = { Text(text = "請輸入標記名字") },
                    state = nameState,
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 描述
                OutlinedTextField(
                    label = { Text(text = "描述") },
                    placeholder = { Text(text = "請輸入標記描述") },
                    state = descState,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val name = nameState.text.toString()
                    val description = descState.text.toString()

                    if (name.isNotBlank()) {
                        onConfirm(name, description)
                    } else {
                        onConfirm(name, description)
                    }
                }
            ) {
                Text(text = "完成")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "取消")
            }
        }
    )
}

@Preview(
    showBackground = true,
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light Mode"
)
@Preview(
    showBackground = true,
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
fun AddMarkerDialogPreview() {
    AlexMapTagsComposeTheme {
        AddMarkerDialog(
            visible = true,
            onConfirm = { name, description -> },
            onDismiss = {}
        )
    }
}