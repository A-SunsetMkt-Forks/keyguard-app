package com.artemchep.keyguard.feature.filepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.artemchep.keyguard.desktop.util.showFilePicker
import com.artemchep.keyguard.platform.leParseUri
import com.artemchep.keyguard.ui.CollectedEffect
import io.github.vinceglb.filekit.dialogs.FileKitType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
actual fun FilePickerEffect(
    flow: Flow<FilePickerIntent<*>>,
) {
    val state = remember {
        MutableStateFlow<FilePickerIntent<*>?>(null)
    }

    CollectedEffect(flow) { intent ->
        state.value = intent

        when (intent) {
            is FilePickerIntent.OpenDocument -> {
                val mimeTypes = intent.mimeTypes

                val extensions = mimeTypes
                    .mapNotNull { mimeType ->
                        when (mimeType) {
                            "text/plain" -> "txt"
                            "text/wordlist" -> "wordlist"
                            "image/png" -> "png"
                            "image/jpg" -> "jpg"
                            else -> null
                        }
                    }
                    .toSet()
                    .takeIf { it.isNotEmpty() }
                val type = FileKitType.File(extensions = extensions)
                showFilePicker(
                    type = type,
                ) { file ->
                    val info = FilePickerIntent.OpenDocument.Ifo(
                        uri = leParseUri(file),
                        name = file.name,
                        size = file.length(),
                    )
                    intent.onResult(info)
                }
            }
        }
    }
}
