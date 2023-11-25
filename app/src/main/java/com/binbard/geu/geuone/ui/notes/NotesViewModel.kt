package com.binbard.geu.geuone.ui.notes

import android.os.Build.VERSION_CODES.S
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request

class NotesViewModel: ViewModel() {
    private var _text = MutableLiveData<String>().apply {
        value = "The Notes"
    }
    val notesText: LiveData<String> = _text

    val notes = MutableLiveData<FSItem>().apply {
        value = FSItem.root
    }

    fun gotoPrevDir(): Boolean {
        if(notes.value?.parent != null){
            notes.value = notes.value?.parent!!
            return true
        }
        return false
    }

    init{
        fetchData()
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchData() {
        GlobalScope.launch(Dispatchers.IO) {
            val response = fetchPage("https://raw.githubusercontent.com/geu-one-static/notes/master/notes.txt")
            withContext(Dispatchers.Main) {
                parseNotes(response)
                notes.value = FSItem.root
            }
        }
    }
    private fun fetchPage(url: String): String {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            return response.body?.string() ?: ""
        }
    }

    private fun parseNotes(txtData: String){
        val lines = txtData.split("\n")
        val line1s = lines[0].split(" ")
        val pre = line1s[0]
        val post = line1s[0]

        for(line in lines){
            if(!line.endsWith(".pdf")) continue
            val url = pre + line.replace(" ","%20") + post
            FSItem.add(line, url)
        }
    }
}