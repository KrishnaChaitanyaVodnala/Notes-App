package com.example.notesapp

sealed class Screen(val route: String) {
    object notes: Screen("Notes")
    object create: Screen("Create")
    object display: Screen("Display")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach {
                append("/$it")
            }
        }
    }
}