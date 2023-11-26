package com.binbard.geu.geuone.ui.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request

class NotesViewModel(application: Application): AndroidViewModel(application) {
    private val notesCacheHelper = NotesCacheHelper(application)

    private var _text = MutableLiveData<String>().apply {
        value = notesCacheHelper.getLastPath()
    }
    val notesTitle: LiveData<String> = _text

    var notes = MutableLiveData<FSItem>().apply {
        value = FSItem("Notes", "", mutableSetOf(), null)
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

    fun notesCount(): Int{
        if(notes.value == null) return 0
        return notes.value!!.children.size
    }

    init{
        fetchData()
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchData() {
        GlobalScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                val cache = notesCacheHelper.getData()
                if(cache.isNotEmpty()){
                    parseNotes(notesCacheHelper.getData())
                    gotoPath(notesCacheHelper.getLastPath())
                    notes.value = notes.value
                }
            }
            val response = fetchPage("https://raw.githubusercontent.com/geu-one-static/notes/master/notes.txt")
            if(response.isEmpty()) return@launch
            withContext(Dispatchers.Main) {
                parseNotes(response)
                notes.value = notes.value
            }
            notesCacheHelper.saveData(response)
        }
    }
    private fun fetchPage(url: String): String {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        try{
            client.newCall(request).execute().use { response ->
                return response.body?.string() ?: ""
            }
        } catch(e: Exception){
            return ""
        }
    }

    private fun parseNotes(txtData: String){
        if(txtData.isEmpty()) return
        val lines = txtData.split("\n")
        val line1s = lines[0].split(" ")
        val pre = line1s[0]
        val post = line1s[1]

        for(line in lines){
            if(!line.endsWith(".pdf")) continue
            val url = pre + line.replace(" ","%20") + post
            notes.value!!.add(line, url)
        }
    }
}