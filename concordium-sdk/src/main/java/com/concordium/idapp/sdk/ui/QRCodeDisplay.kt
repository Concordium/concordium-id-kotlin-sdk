package com.concordium.idapp.sdk.ui

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.concordium.sdk.common.toPixels
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

@Composable
internal fun QRCodeDisplay(
    walletConnectUri: String,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // QR Code
        val qrBitmap = generateQRCode(
            content = walletConnectUri,
            width = size.toPixels().toInt(),
            height = size.toPixels().toInt()
        )

        qrBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Wallet Connect QR Code",
                modifier = Modifier
                    .size(size)
                    .background(androidx.compose.ui.graphics.Color.White)
            )
        }
    }
}

private fun generateQRCode(
    content: String,
    width: Int,
    height: Int
): Bitmap? {
    return try {
        val hints = hashMapOf<EncodeHintType, Any>().apply {
            put(
                EncodeHintType.ERROR_CORRECTION,
                com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.H
            )
            put(EncodeHintType.MARGIN, 1)
        }

        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(
            content,
            BarcodeFormat.QR_CODE,
            width,
            height,
            hints
        )

        val bitmap = createBitmap(width, height)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            }
        }
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}