package com.zeroball0526

import com.zeroball0526.properties.PropertiesStore
import io.ktor.server.application.*
import com.zeroball0526.route.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import java.io.File
import kotlin.system.exitProcess

private val dbRoute = imageDbRoute
val customLogger = LoggerFactory.getLogger(Application::class.java)

fun main(args: Array<String>) {
    if(dbRoute.isNullOrEmpty()) throw IllegalArgumentException("DB 경로가 올바르지 않아요! setting.properties 를 다시 점검해주세요!")
    val db = File(dbRoute)

    if(!db.exists()) {
        db.mkdir()
        customLogger.info("이미지 데이터베이스 폴더를 만들었어요! 규격에 맞게 콘을 넣어주시고 다시 실행해주세요!")
        exitProcess(0)
    }else{
        imageIndexer(dbRoute)
    }

    val securityRoute = File("${dbRoute}\\SECURITY")
    fun genToken(){
        val token = Token().generateSecurityToken(32,false, dbRoute)
        customLogger.info("토큰 생성 요청 됨")
        customLogger.info("토큰 키: $token")
        customLogger.info("절대 타인에게 함부로 공유하지 마세요!")
    }

    if(!securityRoute.exists()){
        genToken()
    }else if (
        //날짜 규격은 반드시 MM-DD 을 지켜주세요! 세부적인 조건으로 설정이 필요한 경우 코드 변경이 필요합니다.
        //기본값은 6개월마다 초기화 입니다.
        Token().resetToken(true,listOf("06-01","12-01"))
    ){
        genToken()
    }

    if(PropertiesStore.getProperty("status") == "production"){
        val l = LoggerFactory.getLogger("io.ktor.server.routing.Routing") as Logger
        val l2 = LoggerFactory.getLogger("io.ktor.server.plugins.contentnegotiation.ContentNegotiation") as Logger
        l.level = Level.INFO
        l2.level = Level.INFO
    }

    embeddedServer(Netty, port = PropertiesStore.getProperty("port")?.toInt() ?:8080, module = Application::module).start( wait = true )
    //io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureHTTP()
    configureDeleteRouting()
    configureGetRouting()
    configurePostRouting()
    configurePutRouting()
}
