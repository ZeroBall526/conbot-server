package com.zeroball0526.properties

import java.io.File
import java.io.FileInputStream
import java.util.Properties

object PropertiesStore{

    private val filePath = File("${System.getProperty("user.dir")}\\setting.properties")

    val setting : Properties by lazy {
        val prop = Properties()
        if(!filePath.exists()) createSetting()
        //설정 파일 적제
        prop.load(FileInputStream(filePath))
        prop
    }

    fun defaultSetting() : Properties {
        val default = Properties()
        default.setProperty("port","8080")
        default.setProperty("useToken","true")
        default.setProperty("status","production")
        default.setProperty("db.route","${System.getProperty("user.dir")}\\conbot-image".split("\\").joinToString("\\\\"))
        return default
    }

    private fun createSetting(){
        // 새 설정 파일 제작
        println("새 설정 파일 생성중...")
        CreateProperties(defaultSetting(),filePath).addComment("콘봇-서버 설정 파일입니다! 잘 모르겠다면 절대 건드지마세요!")
        println("새 설정 파일 작성 완료!")
    }

    fun addOption (opt:Map <String,Any>){
        for((key,value) in opt){
            setting.setProperty(key,value.toString())
        }
    }

    fun getProperty(key : String): String? = setting.getProperty(key)
}