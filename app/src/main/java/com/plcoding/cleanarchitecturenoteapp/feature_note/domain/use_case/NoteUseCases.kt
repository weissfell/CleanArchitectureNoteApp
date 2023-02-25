package com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case

//Wrapperclass that will be injected into the viewmodel - useful in case list of usecases grow
data class NoteUseCases(
    val getAllNotes: GetAllNotes,
    val deleteNote: DeleteNote
)
