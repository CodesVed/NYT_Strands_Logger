package com.example.strandslogger.navigation

import kotlinx.serialization.Serializable

sealed class Navigation {
    @Serializable
    object Main
}

sealed class Routes {
    @Serializable
    object History

    @Serializable
    object AddEntry
}