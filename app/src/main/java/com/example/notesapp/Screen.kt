package com.example.notesapp

sealed class Screen(val route: String) {
    object Notes: Screen("Notes")
    object Create: Screen("Create")
    object Display: Screen("Display")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach {
                append("/$it")
            }
        }
    }
}