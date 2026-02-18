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
                    Log.d("Handler", "21")
                    val host = this?.host ?: return      // Unknown Host; Cancel Navigation
                    Log.d("Handler", "22")
                    // Query Validation
                    if (this.encodedQuery.isNullOrBlank()) return    // Unknown/empty path; Cancel Navigation

                    Log.d("Handler", "23")
                    val destination = "$host?${this.encodedQuery}"
                    Log.d("Handler", "Resolved Destination: $destination")
                    onReady(host, destination)
                }
            } else {
                // Intent.data is empty -> uri sent via Intent
                // FCM notification received while app is in background
                // Access deeplink (key: deeplink)
                val receivedUri = intent.getStringExtra("deeplink")
                Log.d("Handler", "37")
                // Validation
                if (receivedUri.isNullOrBlank()) return     // No Uri Found; Cancel Navigation
                Log.d("Handler", "40")
                // Host Validation
                val host = receivedUri.toUri().host
                if (host.isNullOrBlank()) return        // Unknown Host. Cancel Navigation

                when (host) {
                    "notice" -> {
                        Log.d("Handler", "Host is Notice; $host")

                        // Take nttId and ContentUrl to create nav destination
                        val id = intent.getStringExtra("nttId") ?: return       // Unable to find nttId, Cancel Navigation
                        val url = intent.getStringExtra("contentUrl") ?: return     // Unable to find contentUrl. Cancel Navigation
                        Log.d("Handler", "52")
                        // Create Navigation Uri
                        val destination = generateNoticeUri(id, url)
                        onReady(host, destination)
                    }
                    else -> {
                        Log.d("Handler", "Host is $host")
                        val path = receivedUri.toUri().encodedQuery
                        Log.d("Handler", "60")
                        if (path.isNullOrBlank()) return        // Path not found: Cancel Navigation
                        onReady(host, "$host?$path")
                    }
                }
            }

        }

        fun processIntent(
            intent: Intent,
            onDestination: (service: String, uri: String) -> Unit
        ) {
            var destination = NavRoutes.Home.route

            val type = intent.getStringExtra("type")
            Log.d("Handler", "Type: $type")

            // Widget Intent Processing
            if (type != null) {
                destination = NavRoutes.Settings.route
                onDestination(type, destination)
                return
            }


            if (intent.data != null) {
                destination = intent.data!!.navDestination()
            } else {
                val id = intent.getStringExtra("nttId")
                val url = intent.getStringExtra("contentUrl")
                Log.d("Handler", "${intent.getStringExtra("deeplink")}")

                if (id != null && url != null) {
                    destination = generateNoticeUri(id, url)
                }
            }

            val service = destination.split("/")[0]
            Log.d("Handler", "${destination}")
            onDestination(service, destination)
        }

        private fun Uri.navDestination(): String {
            return if (this.host != "service") {
                NavRoutes.Home.route
            } else {
                this.encodedPath?.substring(1) ?: NavRoutes.Home.route
            }
        }

        private fun generateNoticeUri(id: String, url: String) =
            "notice?nttId=$id&contentUrl=${Uri.encode(url)}&FabVisible=true"
    }
}