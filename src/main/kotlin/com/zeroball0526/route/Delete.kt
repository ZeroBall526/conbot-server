package com.zeroball0526.route

import com.zeroball0526.Token
import com.zeroball0526.imageDbRoute
import com.zeroball0526.imageIndexer
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.plugins.origin
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.routing
import java.io.File
import kotlin.text.isNullOrEmpty

fun Application.configureDeleteRouting(){
    routing {
        delete("/deleteCons") {
            val token = call.request.headers["token"]
            val form = call.receive<DeleteConForm>()
            println("요청된 ip: ${call.request.origin.remoteAddress}")

            if (token.isNullOrEmpty() || token != Token().getToken(imageDbRoute)) {
                println(form.conName)
                return@delete call.respond(
                    HttpStatusCode.Forbidden, mapOf(
                        "code" to HttpStatusCode.Forbidden.value,
                        "message" to "토큰 값이 올바르지 않아요!"
                    )
                )
            }

            //do action
            val errorCon = ArrayList<String>()
            try {
                val categoryName = form.category
                form.conName.forEach { con ->
                    val fileDir = File("${imageDbRoute}\\${categoryName}").listFiles { _, name -> name.startsWith(con) }
                    if (!fileDir.isNullOrEmpty()) errorCon.add(con)
                    //TODO : 파일 확장자까지 프론트가 전달하도록 개선
                    fileDir.forEach {
                        try {
                            if(!it.delete()){
                                errorCon.add(it.name)
                            }
                        } catch (e: Error) {
                            e.printStackTrace()
                            errorCon.add(it.name)
                        }
                    }
                }
                imageIndexer(imageDbRoute)
            } catch (e: Error) {
                e.printStackTrace()
                return@delete call.respond(
                    HttpStatusCode.InternalServerError, mapOf(
                        "code" to HttpStatusCode.InternalServerError.value,
                        "message" to "작업 중 문제가 발생했습니다. 데이터베이스를 확인해주세요!"
                    )
                )
            }
            // 삭제 처리 실패한 콘이 있을시
            if (errorCon.isNotEmpty()) {
                return@delete call.respond(
                    HttpStatusCode.MultiStatus, mapOf(
                        "code" to HttpStatusCode.MultiStatus.value, "message" to "일부 작업은 완료했으나 일부 콘 삭제를 실패했어요",
                        "errorCon" to errorCon
                    )
                )
            }
            return@delete call.respond(
                HttpStatusCode.OK, mapOf("code" to HttpStatusCode.OK.value, "message" to "콘을 삭제했어요")
            )

        }

        delete("/deleteCategory") {
            val token = call.request.headers["token"]
            //토큰 권한 필요한 명령 api 접근자 ip 확인
            println("요청된 ip: ${call.request.origin.remoteAddress}")

            if (token.isNullOrEmpty() || token != Token().getToken(imageDbRoute))
                return@delete call.respond( HttpStatusCode.Forbidden, mapOf(
                    "code" to HttpStatusCode.Forbidden.value,
                    "message" to "토큰 값이 올바르지 않아요!"
                ))

            try {
                val categoryName = call.receive<CategoryForm>().categoryName
                val folderDir = File("${imageDbRoute}\\${categoryName}")

                //폴더 검증
                if(!folderDir.isDirectory && !folderDir.exists())
                    error("${categoryName}은 존재하지 않는 카테고리거나, 카테고리가 아니에요!")

                //카테고리 내 콘이 있는지 있는지 확인
                if(folderDir.listFiles().size > 0){
                    return@delete call.respond(HttpStatusCode.BadRequest, mapOf(
                        "code" to HttpStatusCode.Forbidden.value,
                        "message" to "카테고리 내에 콘들이 존재해요. 카테고리 안에 콘이 없어야 삭제가 가능해요."
                    )
                    )
                }

                //폴더(카테고리) 삭제
                val result = folderDir.deleteRecursively()

                if(!result)
                    error("$categoryName 카테고리 삭제도중 문제가 발생했습니다!")

                imageIndexer(imageDbRoute)
                return@delete call.respond( HttpStatusCode.OK, mapOf(
                    "code" to HttpStatusCode.OK.value,
                    "message" to "$categoryName 카테고리를 성공적으로 삭제했어요."
                )
                )
            } catch (e: Error) {
                e.printStackTrace()
                return@delete call.respond( HttpStatusCode.InternalServerError, mapOf(
                    "code" to HttpStatusCode.InternalServerError.value,
                    "message" to "작업 중 문제가 발생했습니다. 데이터베이스를 확인해주세요!"
                )
                )
            }
        }
    }
}