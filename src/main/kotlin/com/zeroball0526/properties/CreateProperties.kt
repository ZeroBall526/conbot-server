package com.zeroball0526.properties

import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Properties

class CreateProperties(private val prop : Properties, private val file : File){
    init{
        val formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy")
        val nowTime = LocalDateTime.now(ZoneId.of("Asia/Seoul")).atZone(ZoneId.of("Asia/Seoul")).format(formatter)

        if(!file.exists()) file.createNewFile()


        file.printWriter().use { writer ->
            writer.println("#$nowTime")
            for(setting in prop){
                writer.println(setting)
            }
        }
    }

    fun addComment(msg : String){
        val data = file.readLines()
        val changeData = mutableListOf<String>()
        println(msg)

        changeData.add("#$msg")
        changeData.addAll(data)

        file.bufferedWriter(Charsets.UTF_8).use { writer ->
            changeData.forEach{ writer.appendLine(it) }
        }
    }
}