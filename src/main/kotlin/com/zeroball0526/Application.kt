package com.zeroball0526

import io.ktor.server.application.*
import com.zeroball0526.route.*
import java.io.File
import kotlin.system.exitProcess

private val dbRoute = "${System.getProperty("user.dir")}\\conbot-image"

fun main(args: Array<String>) {
    val db = File(dbRoute)

    if(!db.exists()) {
        db.mkdir()
        println("이미지 데이터베이스 폴더를 만들었어요! 규격에 맞게 콘을 넣어주시고 다시 실행해주세요!")
        exitProcess(0)
    }else{
        imageIndexer(dbRoute)
    }

    val securityRoute = File("${dbRoute}\\SECURITY")

    if(!securityRoute.exists()){
        val token = Token().generateSecurityToken(32,false, dbRoute)
        println("토큰 생성 요청 됨")
        println("토큰 키: $token")
        println("절대 타인에게 함부로 공유하지 마세요!")
    }else if (
        //날짜 규격은 반드시 MM-DD 을 지켜주세요! 세부적인 조건으로 설정이 필요한 경우 코드 변경이 필요합니다.
        //기본값은 6개월마다 초기화 입니다.
        Token().resetToken(true,listOf("06-01","12-01"))
    ){
        val token = Token().generateSecurityToken(32,false, dbRoute)
        println("토큰 재생성 요청 됨")
        println("토큰 키: ${token}")
        println("절대 타인에게 함부로 공유하지 마세요!")
    }

    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureHTTP()
    configureDeleteRouting()
    configureGetRouting()
    configurePostRouting()
    configurePutRouting()
}
