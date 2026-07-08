package com.example.klippr.redemption.util

import java.net.URLEncoder

// @author Samuel Bonifacio
/**
 * Construye la URL de la QR Server API (goqr.me) para renderizar el QR de canje (US-04).
 * El backend Render sigue siendo el único que genera [content] (token/código); esta API
 * externa solo produce la imagen PNG a partir de ese contenido.
 */
fun buildQrImageUrl(content: String, sizePx: Int = 200): String {
    val encoded = URLEncoder.encode(content, "UTF-8")
    return "https://api.qrserver.com/v1/create-qr-code/?size=${sizePx}x${sizePx}&data=$encoded"
}
