package com.doyoonkim.common.navigation

import android.content.Intent
import android.net.Uri

class DeeplinkHandler {
    companion object {
        fun processIntent(
            intent: Intent,
            onDestination: (uri: String) -> Unit
        ) {
            var destination = NavRoutes.Home.route

            if (intent.data != null) {
                destination = intent.data!!.navDestination()
            } else {
                val id = intent.getStringExtra("nttId")
                val url = intent.getStringExtra("contentUrl")

                if (id != null && url != null) {
                    destination = generateNoticeUri(id, url)
                }
            }

            onDestination(destination)
        }

        private fun Uri.navDestination(): String {
            return if (this.host != "service") {
                NavRoutes.Home.route
            } else {
                this.encodedPath?.substring(1) ?: NavRoutes.Home.route
            }
        }

        private fun generateNoticeUri(id: String, url: String) =
            "noticeDetail/$id/${Uri.encode(url)}/${true}"
    }
}