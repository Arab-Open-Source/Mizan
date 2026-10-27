package com.eec.core.share

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object AndroidShareContext {
    private var applicationContext: Context? = null

    fun initialize(context: Context) {
        applicationContext = context.applicationContext
    }

    internal fun requireContext(): Context =
        applicationContext ?: error("AndroidShareContext.initialize must run before sharing")
}

actual class ShareService actual constructor() {
    actual suspend fun shareText(text: String, subject: String) {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        startChooser(send)
    }

    actual suspend fun copyText(text: String) {
        val context = AndroidShareContext.requireContext()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return
        clipboard.setPrimaryClip(ClipData.newPlainText(CLIP_LABEL, text))
    }

    actual suspend fun shareFile(bytes: ByteArray, fileName: String, mimeType: String) {
        val context = AndroidShareContext.requireContext()
        val directory = File(context.cacheDir, SHARED_DIRECTORY).apply { mkdirs() }
        val file = File(directory, fileName)
        file.writeBytes(bytes)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val send = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startChooser(send)
    }

    actual suspend fun openLink(url: String) {
        val context = AndroidShareContext.requireContext()
        val view = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(view)
    }

    actual fun canShareFiles(): Boolean = true

    private fun startChooser(send: Intent) {
        val context = AndroidShareContext.requireContext()
        val chooser = Intent.createChooser(send, null).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(chooser)
    }
}

private const val CLIP_LABEL: String = "Mizan"
private const val SHARED_DIRECTORY: String = "shared"
