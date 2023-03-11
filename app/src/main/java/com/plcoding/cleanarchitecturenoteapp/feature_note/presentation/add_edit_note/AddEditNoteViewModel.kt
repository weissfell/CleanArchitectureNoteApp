package com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.add_edit_note

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.InvalidNoteException
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    /**
     * this wont bne wrapped in a single state object unlike NotesViewModel. Why? We hav emultiple textfields in our screen .
     * Entering a letter into one of the textfields would then lead to the entire UI/screen being recomposed (instead of one textfield
     *
     *
     */

    //also needed: state if hint is visible or not - Wrapper class NoteTextFieldState!
    private val _noteTitle = mutableStateOf(
        NoteTextFieldState(
            hint = "enter title.."
        )
    )
    val noteTitle: State<NoteTextFieldState> = _noteTitle

    private val _noteContent = mutableStateOf(
        NoteTextFieldState(
            hint = "Enter content.."
        )
    )
    val noteContent: State<NoteTextFieldState> = _noteContent


    private val _noteColor = mutableStateOf<Int>(Note.noteColors.random().toArgb())
    val noteColor: State<Int> = _noteColor

    //what is eventFlow? if we use normal compose state then these host states... but some things are more like events not state
    //no native Jetpack Compose Events exist for this yet
    //this way we can send one time used events into the SharedFlow and observe it
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentNoteId : Int? = null

    init {

        /**
         * user clicks on existing note - how do we get the id of the note he clicked on?
         * NavigationArguments! They can be injected to Hilt... (see: savedStateHandle)
         */

        savedStateHandle.get<Int>("noteId")?.let{
            if(it != -1){ //default we will use for new note
                viewModelScope.launch{
                    noteUseCases.getNote(it)?.also{ note ->
                        currentNoteId = note.id
                        _noteTitle.value = noteTitle.value.copy(
                            text = note.title,
                            isHintVisible = false
                        )
                        _noteContent.value = noteContent.value.copy(
                            text = note.content,
                            isHintVisible = false
                        )
                        _noteColor.value = noteColor.value
                    }
                }
            }
        }
    }

    fun onEvent(event: AddEditNoteEvent) {
        when (event) {
            is AddEditNoteEvent.EnteredTitle -> {
                _noteTitle.value = noteTitle.value.copy(
                    text = event.value
                )
            }
            is AddEditNoteEvent.ChangeTitleFocus -> {
                _noteTitle.value = noteTitle.value.copy(
                    isHintVisible = !event.focusState.isFocused && noteTitle.value.text.isBlank()
                )
            }
            is AddEditNoteEvent.EnteredContent -> {
                _noteContent.value = noteContent.value.copy(
                    text = event.value
                )
            }
            is AddEditNoteEvent.ChangeContentFocus -> {
                _noteContent.value = noteContent.value.copy(
                    isHintVisible = !event.focusState.isFocused && noteContent.value.text.isBlank()
                )
            }
            is AddEditNoteEvent.ChangeColor -> {
                _noteColor.value = event.color
            }
            is AddEditNoteEvent.SaveNote -> {
                viewModelScope.launch {
                    try {
                        noteUseCases.addNote(
                            Note(
                                title = noteTitle.value.text,
                                content = noteContent.value.text,
                                timestamp = System.currentTimeMillis(),
                                color = noteColor.value,
                                id = currentNoteId //id can be duplicated - that would mean we edit an existing note. Possible thanks to conflictStrategy in DAO!
                            )

                        )
                        _eventFlow.emit(UiEvent.SaveNote) //here we call a new SaveNote event - can later react to it!
                    } catch(e: InvalidNoteException){
                        _eventFlow.emit(UiEvent.ShowSnackbar(
                            msg = e.message ?: "Unknown error..."
                        ))
                    }
                }
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val msg: String) : UiEvent()
        object SaveNote : UiEvent() //navigate back after save
    }

}