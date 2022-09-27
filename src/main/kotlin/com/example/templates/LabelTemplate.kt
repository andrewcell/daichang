package com.example.templates

import com.example.Equipment
import com.example.Monitor
import com.example.PC
import io.ktor.server.html.*
import kotlinx.html.*

class LabelTemplate(val list: List<Equipment>) : Template<HTML> {
    override fun HTML.apply() {
        head {
            link(rel="stylesheet", href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.0/css/all.min.css") {
                attributes["integrity"] =
                    "sha512-xh6O/CkQoPOWDdYTDqeRdPCVd1SpvCA9XXcUnZS2FmJNp1coAFzvtCN9BmamE+4aHK8yyUHUSCcJHgXloTyT2A=="
                attributes["crossorigin"] = "anonymous"
                attributes["referrerpolicy"] = "no-referrer"
            }
            link(rel="stylesheet", href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.0/css/fontawesome.min.css") {
                attributes["integrity"] =
                    "sha512-RvQxwf+3zJuNwl4e0sZjQeX7kUa3o82bDETpgVCH2RiwYSZVDdFJ7N/woNigN/ldyOOoKw8584jM4plQdt8bhA=="
                attributes["crossorigin"] = "anonymous"
                attributes["referrerpolicy"] = "no-referrer"
            }
            link(rel="stylesheet", href = "https://use.fontawesome.com/releases/v5.15.4/css/fontawesome.css") {
                attributes["integrity"] = "sha384-jLKHWM3JRmfMU0A5x5AkjWkw/EYfGUAGagvnfryNV3F9VqM98XiIH7VBGVoxVSc7"
                attributes["crossorigin"] = "anonymous"
            }
            link(rel="preconnect", href="https://fonts.googleapis.com")
            link(rel="preconnect", href="https://fonts.gstatic.com") {
                attributes["crossorigin"]
            }
            link(href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100;300;400;500;700;900&display=swap", rel="stylesheet")
            link(rel = "stylesheet", href = "/static/table.css")
        }
        body {
            style = "font-family: \"Noto Sans KR\", sans-serif;"
            script {
                +"window.print();"
            }
            div {
                style = "width: 21cm;"

                list.forEachIndexed { index, it ->
                    if (it is PC) {
                        insert(PCLabelTableTemplate(it)) { }
                    } else if (it is Monitor) {
                        insert(MonitorTableTemplate(it)) { }
                    }
                    if ((index + 1) % 6 == 0) {
                        div {
                            style = "page-break-after:always;"
                        }
                    }
                }
            }
        }
    }
}