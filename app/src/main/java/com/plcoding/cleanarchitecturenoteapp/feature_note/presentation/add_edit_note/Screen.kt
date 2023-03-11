package com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.add_edit_note

sealed class Screen (val route : String) {

    object NotesScreen : Screen("notes_screen")
    object AddEditNoteScreen : Screen("add_edit_note_screen")

}
