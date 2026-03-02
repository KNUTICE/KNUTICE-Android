package com.doyoonkim.common.navigation

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri

class DeeplinkHandler {
    companion object {

        fun processDeeplink(
            intent: Intent,
            onReady: (host: String, destination: String) -> Unit
        ) {
            // Check intent has a data or not.
            // Uri via data -> FCM notification received while app is in foreground.
            // Bookmark URI
            if (intent.data != null) {
                // use uri from intent.data directly.
                with(intent.data) {
                    val host = this?.host ?: return      // Unknown Host; Cancel Navigation

                    // Temporary Handling
                    if (host == "reading-room") {
                        // Bypass Deeplink-based resolver.
                        onReady(host, NavRoutes.CarrelStatus.route)
                        return
                    }

                    // Query Validation
                    if (this.encodedQuery.isNullOrBlank()) return    // Unknown/empty path; Cancel Navigation

                    Log.d("Handler", "23")
                    val destination = "$host?${this.encodedQuery}"
                    onReady(host, destination)
                }
            } else {
                // Intent.data is empty -> uri sent via Intent
                // FCM notification received while app is in background
                // Access deeplink (key: deeplink)
                val receivedUri = intent.getStringExtra("deeplink")
                // Validation
                if (receivedUri.isNullOrBlank()) return     // No Uri Found; Cancel Navigation
                // Host Validation
                val host = receivedUri.toUri().host
                if (host.isNullOrBlank()) return        // Unknown Host. Cancel Navigation

                when (host) {
                    "notice" -> {
                        // Take nttId and ContentUrl to create nav destination
                        val id = intent.getStringExtra("nttId") ?: return       // Unable to find nttId, Cancel Navigation
                        val url = intent.getStringExtra("contentUrl") ?: return     // Unable to find contentUrl. Cancel Navigation
                        // Create Navigation Uri
                        val destination = generateNoticeUri(id, url)
                        onReady(host, destination)
                    }
                    else -> {
                        val path = receivedUri.toUri().encodedQuery
                        if (path.isNullOrBlank()) return        // Path not found: Cancel Navigation
                        onReady(host, "$host?$path")
                    }
                }
            }

        }

        private fun generateNoticeUri(id: String, url: String) =
            "notice?nttId=$id&contentUrl=${Uri.encode(url)}&FabVisible=true"
    }
}