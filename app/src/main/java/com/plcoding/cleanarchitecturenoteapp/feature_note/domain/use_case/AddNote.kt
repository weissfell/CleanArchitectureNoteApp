package com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case

import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.InvalidNoteException
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.repository.NoteRepository

class AddNote(
    private val repository: NoteRepository
) {

    @Throws(InvalidNoteException::class)
    suspend operator fun invoke(note: Note) {

        if(note.title.isBlank()){
            //error handling - return errorcode or throw specifically made exception class
            throw InvalidNoteException("The title is empty!")
        }
        if(note.content.isBlank()){
            throw InvalidNoteException("The content is blank!")
        }

        repository.insertNote(note)
    }
}