package com.zeroball0526.route

import com.zeroball0526.Token
import com.zeroball0526.customLogger
import com.zeroball0526.imageDbRoute
import com.zeroball0526.imageIndexer
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.plugins.origin
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun Application.configurePostRouting(){
    routing {
        //재인덱싱
        post("/update"){
            val token = call.request.headers["token"]
            //토큰 권한 필요한 명령 api 접근자 ip 확인
            customLogger.info("접근 - 인덱스 업데이트 요청, 요청된 ip: ${call.request.origin.remoteAddress}")


            if(Token().isValidToken(token ?:"",imageDbRoute)) {
                customLogger.warn("${call.request.uri} 인증 거부 - 사유: 잘못된 토큰, 요청된 ip: ${call.request.origin.remoteAddress}")
                return@post call.respond(HttpStatusCode.Forbidden, "토큰 값이 불일치해요! 다시 확인해주세요!")
            }

            try{
                customLogger.info("업데이트 - 재인덱싱 요청됨, 요청된 ip: ${call.request.origin.remoteAddress}")
                imageIndexer(imageDbRoute)
                return@post call.respond( HttpStatusCode.OK, mapOf(
                    "code" to HttpStatusCode.OK.value,
                    "message" to "데이터베이스를 성공적으로 재인덱싱 했어요!"
                )
                )
            }catch (e : Error){
                customLogger.warn("${call.request.uri} - 데이터베이스 작업 중 오류가 발생했습니다!")
                e.printStackTrace()
                return@post call.respond(HttpStatusCode.InternalServerError, "인덱싱 중 문제가 발생했습니다. 데이터베이스를 확인해주세요!")
            }
        }

    }
}