package com.example.templates

import io.ktor.server.html.*
import io.ktor.util.*
import kotlinx.html.HTML
import kotlinx.html.head
import kotlinx.html.*

class LayoutTemplate : Template<HTML> {
    val content = Placeholder<FlowContent>()
    override fun HTML.apply() {
        head {
            link(rel="stylesheet",
                href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.0/dist/css/bootstrap.min.css") {
                integrity = "sha384-gH2yIJqKdNHPEq0n4Mqa/HGKIhSkIHeL5AyhkYV8i59U5AR6csBvApHHNl/vI1Bx"
                attributes["crossorigin"] = "anonymous"
            }
            link(rel="stylesheet", href="https://cdn.datatables.net/1.12.1/css/dataTables.bootstrap5.min.css")

            script(src = "/webjars/jquery/jquery.min.js") {}
            script(src = "/webjars/bootstrap/bootstrap.min.js") {}
            script(src="//cdn.datatables.net/1.12.1/js/jquery.dataTables.min.js") {}
            script(src="https://cdn.datatables.net/1.12.1/js/dataTables.bootstrap5.min.js") {}
            script(src="/static/app.js") {}
            //link(rel = "stylesheet", href = "/webjars/bootstrap/bootstrap.min.css", type = "text/css")
        }
        body {
            header {
                nav("navbar navbar-expand navbar-light bg-light") {
                    div("container-fluid") {
                        a(classes = "navbar-brand", href = "/") {
                            +"WHAT"
                        }
                        div("collapse navbar-collapse") {
                            id = "navbarContent"
                            ul("navbar-nav") {
                                li("nav-item") {
                                    a("/pc", classes = "nav-link") {
                                        +"PC"
                                    }
                                }
                                li("nav-item") {
                                    a("/laptop", classes = "nav-link") {
                                        +"노트북"
                                    }
                                }
                                li("nav-item") {
                                    a("/monitor", classes = "nav-link") {
                                        +"모니터"
                                    }
                                }
                            }
                        }
                    }
                }
            }
            div("container-fluid") {
                id = "app"
                insert(content)
            }
        }
    }
}