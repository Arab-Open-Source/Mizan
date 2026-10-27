package com.eec.core.share

import java.awt.Desktop
import java.awt.FileDialog
import java.awt.Frame
import java.awt.GraphicsEnvironment
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path

actual class ShareService actual constructor() {
    actual suspend fun shareText(text: String, subject: String) {
        copyText(text)
    }

    actual suspend fun copyText(text: String) {
        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(text), null)
    }

    actual suspend fun shareFile(bytes: ByteArray, fileName: String, mimeType: String) {
        val dialog = FileDialog(null as Frame?, "حفظ الملف", FileDialog.SAVE)
        dialog.file = fileName
        dialog.isVisible = true
        val directory = dialog.directory ?: return
        val selected = dialog.file ?: return
        Files.write(Path.of(directory, selected), bytes)
    }

    actual suspend fun openLink(url: String) {
        if (!Desktop.isDesktopSupported()) {
            error("فتح الروابط غير مدعوم على هذا الجهاز")
        }
        Desktop.getDesktop().browse(URI(url))
    }

    actual fun canShareFiles(): Boolean = !GraphicsEnvironment.isHeadless()
}
