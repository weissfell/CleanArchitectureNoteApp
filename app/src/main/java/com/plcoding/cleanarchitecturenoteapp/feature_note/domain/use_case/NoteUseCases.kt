package com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case

import com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.notes.GetNote

//Wrapperclass that will be injected into the viewmodel - useful in case list of usecases grow
data class NoteUseCases(
    val getAllNotes: GetAllNotes,
    val deleteNote: DeleteNote,
    val addNote: AddNote,
    val getNote : GetNote
)
