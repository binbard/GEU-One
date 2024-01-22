package com.binbard.geu.one.ui.notes

import android.content.Context
import com.binbard.geu.one.R
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request

class NotesRepository(context: Context,val nvm: NotesViewModel) {
    val notesUrl = context.getString(R.string.notesUrl)

    @OptIn(DelicateCoroutinesApi::class)
    fun fetchData() {
        GlobalScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                val cache = nvm.notesCacheHelper.getData()
                if(cache.isNotEmpty()){
                    parseNotes(nvm.notesCacheHelper.getData())
                    nvm.gotoPath(nvm.notesCacheHelper.getLastPath())
                    nvm.notes.value = nvm.notes.value
                }
            }
            val response = fetchPage(notesUrl)
            if(response.isEmpty()) return@launch
            withContext(Dispatchers.Main) {
                parseNotes(response)
                nvm.notes.value = nvm.notes.value
            }
            nvm.notesCacheHelper.saveData(response)
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
            nvm.notes.value!!.add(line, url)
        }
    }
}