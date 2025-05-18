package com.zeroball0526.route

import com.zeroball0526.Token
import com.zeroball0526.imageDbRoute
import com.zeroball0526.imageIndexer
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.Application
import io.ktor.server.http.content.resource
import io.ktor.server.http.content.static
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.origin
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.text.isNullOrEmpty

fun Application.configurePutRouting(){
    routing{
        //카테고리 추가
        route("/addCategory"){
            put{
                val token = call.request.headers["token"]
                //토큰 권한 필요한 명령 api 접근자 ip 확인
                println("카테고리 접근 요청, 요청된 ip: ${call.request.origin.remoteAddress}")

                if(token.isNullOrEmpty() || token != Token().getToken(imageDbRoute))
                    return@put call.respond(
                        HttpStatusCode.Forbidden,
                        mapOf(
                            "code" to HttpStatusCode.Forbidden.value,
                            "message" to "토큰 값이 올바르지 않아요!"
                        )
                    )
                try{
                    val categoryName = call.receive<CategoryForm>().categoryName
                    if(File("${imageDbRoute}\\${categoryName}").exists())
                        return@put call.respond(HttpStatusCode.BadRequest,
                            mapOf(
                                "code" to HttpStatusCode.BadRequest,
                                "message" to "이미 존재하는 카테고리 이름이에요!"
                            )
                        )
                    File("${imageDbRoute}\\${categoryName}").mkdir()
                    imageIndexer(imageDbRoute)
                    return@put call.respond(
                        HttpStatusCode.OK,
                        mapOf(
                            "code" to HttpStatusCode.OK.value,
                            "message" to "카테고리를 성공적으로 추가했어요."
                        )
                    )
                }catch (e : Error){
                    e.printStackTrace()
                    return@put call.respond(HttpStatusCode.InternalServerError,
                        mapOf(
                            "code" to HttpStatusCode.InternalServerError.value,
                            "message" to "작업 중 문제가 발생했습니다. 데이터베이스를 확인해주세요!"
                        )
                    )
                }
            }
        }

        val allowedExtension = setOf("jpg","jpeg","png","gif")
        //콘추가
        put("addCons"){
            val token = call.request.headers["token"]
            //토큰 권한 필요한 명령 api 접근자 ip 확인
            println("콘 접근 요청, 요청된 ip: ${call.request.origin.remoteAddress}")

            if(token.isNullOrEmpty() || token != Token().getToken(imageDbRoute))
                return@put call.respond( HttpStatusCode.Forbidden, mapOf(
                    "code" to HttpStatusCode.Forbidden.value,
                    "message" to "토큰 값이 올바르지 않아요!"
                )
                )

            try{
                //action
                val errorData = ArrayList<String>()
                val data = call.receiveMultipart()
                var categoryName : String = ""
                var isBadForm = false

                data.forEachPart { part ->
                    when(part){
                        is PartData.FormItem -> {
                            if(part.name == "category") categoryName = part.value
                            else {
                                println("ERROR: 올바른 폼의 형식이 아닙니다!")
                                isBadForm = true
                            }
                        }
                        is PartData.FileItem -> {
                            if(categoryName.isNotEmpty() && !isBadForm){
                                //파일 확장자 무결성 검사
                                if(File("$imageDbRoute\\$categoryName\\${part.originalFileName.toString()}").exists() ||
                                    part.originalFileName?.substringAfterLast('.', missingDelimiterValue = "")?.lowercase() !in allowedExtension){
                                    //지원하는 확장자인지 중복파일인지 검사
                                    errorData.add(part.originalFileName.toString())
                                    return@forEachPart
                                }
                                val filePath : Path = Path.of("$imageDbRoute\\$categoryName", part.originalFileName)
                                part.streamProvider().use { inputStream ->
                                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING)  // 파일 복사
                                }
                            }else errorData.add(part.originalFileName.toString())

                        }
                        //error stack
                        else -> errorData.add(part.name.toString())
                    }
                    part.dispose()
                }
                imageIndexer(imageDbRoute)
                if(errorData.isNotEmpty())
                    return@put call.respond(HttpStatusCode.MultiStatus, mapOf(
                        "code" to HttpStatusCode.MultiStatus.value,
                        "message" to "일부는 처리를 완료하였으나, 일부는 처리에 실패했어요.",
                        "errorCon" to errorData
                    ))

                return@put call.respond(HttpStatusCode.OK, mapOf(
                    "code" to HttpStatusCode.OK.value,
                    "message" to "업데이트를 완료했어요."
                ))
            }catch (e : Error){
                e.printStackTrace()
                return@put call.respond(HttpStatusCode.InternalServerError, mapOf(
                    "code" to HttpStatusCode.InternalServerError.value,
                    "message" to "작업 중 문제가 발생했습니다. 데이터베이스를 확인해주세요!"
                )
                )
            }

        }
    }

}