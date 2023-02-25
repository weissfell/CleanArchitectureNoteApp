package com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case

import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.repository.NoteRepository
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.util.NoteOrder
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.util.OrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllNotes(
    private val repository: NoteRepository
) {
    //IMPORTANT: repository is of the interface, not the implementation
    //this will make it replaceable for testing etc.

    operator fun invoke(
        noteOrder: NoteOrder = NoteOrder.Date(OrderType.Descending)
    ): Flow<List<Note>> {
        //by invoke we can use this later on as a methode

        //what follows is the logic by which we want to sort the notes...
        return repository.getAllNotes().map {
            when (noteOrder.orderType) {
                is OrderType.Ascending -> {
                    when(noteOrder){
                        is NoteOrder.Title -> it.sortedBy { it.title.lowercase() }
                        is NoteOrder.Date -> it.sortedBy { it.timestamp }
                        is NoteOrder.Color -> it.sortedBy { it.color }
                    }
                }
                is OrderType.Descending -> {
                    when(noteOrder){
                        is NoteOrder.Title -> it.sortedByDescending { it.title.lowercase() }
                        is NoteOrder.Date -> it.sortedByDescending { it.timestamp }
                        is NoteOrder.Color -> it.sortedByDescending { it.color }
                    }
                }
            }
        }
    }
}