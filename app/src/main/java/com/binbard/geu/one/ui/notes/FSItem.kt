package com.binbard.geu.one.ui.notes

data class FSItem(
    var name: String = "",
    var url: String? = null,
    val children: MutableSet<FSItem> = mutableSetOf(),
    val parent: FSItem? = null
){
    fun add(path: String, url: String){
        val parts = path.split("/")
        var current = getRoot()
        var currPart = 0
        while(currPart < parts.size){
            val part = parts[currPart]
            var found = false
            for(child in current.children){
                if(child.name == part){
                    current = child
                    found = true
                    break
                }
            }
            if(!found){
                val newChild = FSItem(part, null, mutableSetOf(), current)
                current.children.add(newChild)
                current = newChild
            }
            currPart++
        }
        current.url = url
    }

    private fun getRoot(): FSItem{
        var current = this
        while(current.parent != null){
            current = current.parent!!
        }
        return current
    }

    fun gotoPath(path: String): FSItem? {
        val parts = path.split("/")
        var current = getRoot()
        var currPart = 1
        while(currPart < parts.size){
            val part = parts[currPart]
            var found = false
            for(child in current.children){
                if(child.name == part){
                    current = child
                    found = true
                    break
                }
            }
            if(!found) return null
            currPart++
        }
        return current
    }

    fun getChildFileIndex(name: String): Int {
        for((index, child) in children.withIndex()){
            if(child.getFileDisplayName() == name) return index
        }
        return -1
    }

    fun add(item: FSItem){
        children.add(item)
    }

    fun isFolder(): Boolean {
        return url == null || url == ""
    }

    fun getFileDisplayName(): String {
        return if(isFolder()) name
        else name.substringAfterLast("_").substringBeforeLast(".pdf")
    }

    fun getPath(): String {
        return if(parent == null) name
        else "${parent.getPath()}/$name"
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FSItem

        if (name != other.name) return false
        if (url != other.url) return false
        if (children != other.children) return false
        if (parent != other.parent) return false

        return true
    }

}