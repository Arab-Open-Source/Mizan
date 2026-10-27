package com.eec.core.share

import kotlin.js.ExperimentalWasmJsInterop

@OptIn(ExperimentalWasmJsInterop::class)
actual class ShareService actual constructor() {
    actual suspend fun shareText(text: String, subject: String) {
        if (!shareWithNavigator(text = text, subject = subject)) {
            copyText(text)
        }
    }

    actual suspend fun copyText(text: String) {
        copyToClipboard(text)
    }

    actual suspend fun shareFile(bytes: ByteArray, fileName: String, mimeType: String) {
        shareFileBytes(base64 = bytes.toBase64(), fileName = fileName, mimeType = mimeType)
    }

    actual suspend fun openLink(url: String) {
        openInNewTab(url)
    }

    actual fun canShareFiles(): Boolean = true
}

private fun ByteArray.toBase64(): String = buildString {
    var index = 0
    val length = this@toBase64.size
    while (index < length) {
        val first = this@toBase64[index].toInt() and 0xFF
        val second = if (index + 1 < length) this@toBase64[index + 1].toInt() and 0xFF else 0
        val third = if (index + 2 < length) this@toBase64[index + 2].toInt() and 0xFF else 0

        append(BASE64[first shr 2])
        append(BASE64[((first and 0x03) shl 4) or (second shr 4)])
        append(if (index + 1 < length) BASE64[((second and 0x0F) shl 2) or (third shr 6)] else '=')
        append(if (index + 2 < length) BASE64[third and 0x3F] else '=')

        index += 3
    }
}

private const val BASE64: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"

@JsFun(
    "(text, subject) => {" +
        " if (typeof navigator !== 'undefined' && typeof navigator.share === 'function') {" +
        " navigator.share({ text: text, title: subject }).catch(() => {}); return true; }" +
        " return false; }",
)
@OptIn(ExperimentalWasmJsInterop::class)
private external fun shareWithNavigator(text: String, subject: String): Boolean

@JsFun(
    "(text) => { if (typeof navigator !== 'undefined' && navigator.clipboard && navigator.clipboard.writeText) {" +
        " navigator.clipboard.writeText(text).catch(() => {}); } }",
)
@OptIn(ExperimentalWasmJsInterop::class)
private external fun copyToClipboard(text: String)

@JsFun(
    "(base64, fileName, mimeType) => {" +
        " const raw = atob(base64);" +
        " const bytes = new Uint8Array(raw.length);" +
        " for (let i = 0; i < raw.length; i++) { bytes[i] = raw.charCodeAt(i); }" +
        " const blob = new Blob([bytes], { type: mimeType });" +
        " const download = () => {" +
        " const url = URL.createObjectURL(blob);" +
        " const anchor = document.createElement('a');" +
        " anchor.href = url; anchor.download = fileName;" +
        " document.body.appendChild(anchor); anchor.click(); anchor.remove();" +
        " URL.revokeObjectURL(url); };" +
        " const file = new File([blob], fileName, { type: mimeType });" +
        " if (typeof navigator !== 'undefined' && navigator.canShare && navigator.share &&" +
        " navigator.canShare({ files: [file] })) {" +
        " navigator.share({ files: [file], title: fileName }).catch(download);" +
        " } else { download(); } }",
)
@OptIn(ExperimentalWasmJsInterop::class)
private external fun shareFileBytes(base64: String, fileName: String, mimeType: String)

@JsFun("(url) => { window.open(url, '_blank', 'noopener'); }")
@OptIn(ExperimentalWasmJsInterop::class)
private external fun openInNewTab(url: String)
