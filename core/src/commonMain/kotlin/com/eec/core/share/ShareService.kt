package com.eec.core.share

expect class ShareService() {
    suspend fun shareText(text: String, subject: String)

    suspend fun copyText(text: String)

    suspend fun shareFile(bytes: ByteArray, fileName: String, mimeType: String)

    suspend fun openLink(url: String)

    fun canShareFiles(): Boolean
}
