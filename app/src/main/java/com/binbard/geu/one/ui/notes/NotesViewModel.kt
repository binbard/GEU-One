package com.binbard.geu.one.ui.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class NotesViewModel(application: Application): AndroidViewModel(application) {
    val notesCacheHelper = NotesCacheHelper(application)

    var notesRepository: NotesRepository? = null

    private var _text = MutableLiveData<String>().apply {
        value = notesCacheHelper.getLastPath()
    }
    var rvAdapter: NotesRecyclerAdapter? = null

    val notesTitle: LiveData<String> = _text

    var notes = MutableLiveData<FSItem>().apply {
        value = FSItem("Notes", null, mutableSetOf(), null)
    }

    var thumbDownloaded = MutableLiveData<String>().apply {
        value = ""
    }

    fun gotoPrevDir(): Boolean {
        if(notes.value?.parent != null){
            notes.value = notes.value?.parent!!
            _text.value = notes.value!!.getPath()
            return true
        }
        return false
    }

    fun gotoNextDir(dir: String): Boolean{
        for (child in notes.value!!.children){
            if(child.name == dir){
                notes.value = child
                _text.value = notes.value!!.getPath()
                return true
            }
        }
        return false
    }

    fun gotoPath(path: String) {
        notes.value = notes.value!!.gotoPath(path)
    }
}