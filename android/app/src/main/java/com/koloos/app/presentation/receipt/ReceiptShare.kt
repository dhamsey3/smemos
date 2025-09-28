package com.koloos.app.presentation.receipt

import android.content.Context
import android.content.Intent

fun shareReceipt(context: Context, message: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
        `package` = "com.whatsapp"
    }
    context.startActivity(Intent.createChooser(intent, "Share receipt"))
}
