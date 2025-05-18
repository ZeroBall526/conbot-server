package com.zeroball0526

import java.io.File


fun main(){
    val con = File("${System.getProperty("user.dir")}\\conbot-image\\이유식콘\\83327906_p0_master1200.jpg")

    val fileName = con.name.split(".")
    println(fileName)
    println(if(fileName[1] == "gif") "cg" else "c")

    /*
    val conAddress = con.path.split("\\")
    println(conAddress)
     */
}
