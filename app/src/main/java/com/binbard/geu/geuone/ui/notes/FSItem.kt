package com.binbard.geu.geuone.ui.notes

data class FSItem(
    val name: String = "",
    var url: String? = null,
    val children: MutableSet<FSItem> = mutableSetOf(),
    val parent: FSItem? = null
){

    companion object {
        val root = FSItem("ROOT", null, mutableSetOf())

        fun add(path: String, url: String){
            val parts = path.split("/")
            var current = root
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
                    val newChild = FSItem(part, "", mutableSetOf(), current)
                    current.children.add(newChild)
                    current = newChild
                }
                currPart++
            }
            current.url = url
        }

        fun gotoPath(path: String): FSItem? {
            val parts = path.split("/")
            var current = root
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
                if(!found)
                    return null
                currPart++
            }
            return current
        }
    }

    fun add(item: FSItem){
        children.add(item)
    }

    fun isFolder(): Boolean {
        return url == ""
    }

    fun getFileName(): String {
        return if(isFolder())
            name
        else name.split("_").last().split(".").first()
    }

    fun getPath(): String {
        return if(parent == null)
            name
        else "${parent.getPath()}/$name"
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

}