package com.binbard.geu.geuone.ui.notes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotesViewModel: ViewModel() {
    private var _text = MutableLiveData<String>().apply {
        value = "This is notes fragment"
    }
    val notesText: LiveData<String> = _text
}