package com.zeroball0526.route

import com.zeroball0526.categoryCon
import com.zeroball0526.imageDbRoute
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import kotlin.text.lowercase
import kotlin.text.split

fun Application.configureGetRouting() {
    routing {
        //페이지 대응
        staticResources("/", "static")

        //인덱싱 값 전체 얻기
        get("/index"){
            val resultFile = File("${imageDbRoute}\\index.json")

            if(!resultFile.exists())
                return@get call.respond(HttpStatusCode.NotFound,"index.json 파일이 생성되지 않았거나 손상되었어요, 데이터베이스 파일을 재생성 해주세요!")

            call.respondText(
                text = BufferedReader(FileReader(resultFile, Charsets.UTF_8)).readLines()[0],
                contentType = ContentType.Application.Json
            )
        }
        //인덱싱 카테고리에 해당하는 json 값 얻기
        get("/index/{category}"){
            val category = call.parameters["category"] ?: return@get call.respond(HttpStatusCode.BadRequest,"제출할 양식이 충족되지 않았어요! 다시 확인해주세요.(Hint: 카테고리 양식 오류)")

            val result = categoryCon(imageDbRoute,category)

            if(result != null){
                return@get call.respondText(
                    text = result,
                    contentType = ContentType.Application.Json
                )
            }else return@get call.respond(HttpStatusCode.NotFound,"검색하신 카테고리를 찾을수 없어요!")
        }
        //인덱싱 된 카테고리 얻기
        get("/indexCategory"){
            val resultFile = File("${imageDbRoute}\\category")

            if(!resultFile.exists())
                return@get call.respond(HttpStatusCode.NotFound,"category 파일이 생성되지 않았거나 손상되었어요, 데이터베이스 파일을 재생성 해주세요!")

            call.respondText(
                text = BufferedReader(FileReader(resultFile, Charsets.UTF_8)).readLines()[0],
                contentType = ContentType.Application.Json
            )
        }
        //카테고리만 입력시 오류표현
        get("/image/{category}"){
            return@get call.respond(HttpStatusCode.BadRequest,"제출할 양식이 충족되지 않았어요! 다시 확인해주세요.(Hint: 콘 양식이 작성되지 않음)")
        }
        //콘 불러오기
        get("/image/{category}/{con}"){
            val category = call.parameters["category"] ?: return@get call.respond(HttpStatusCode.BadRequest,"제출할 양식이 충족되지 않았어요! 다시 확인해주세요.(Hint: 카테고리 양식 오류)")
            val con = call.parameters["con"] ?: return@get call.respond(HttpStatusCode.BadRequest,"제출할 양식이 충족되지 않았어요! 다시 확인해주세요.(Hint: 콘 양식 오류)")

            val conDir = File("${imageDbRoute}\\${category}\\${con}")
            val contentType : ContentType = when(con.split(".")[con.split(".").size-1].lowercase()){
                "png" -> ContentType.Image.PNG
                "jpg","jpeg" -> ContentType.Image.JPEG
                "gif" -> ContentType.Image.GIF
                "svg" -> ContentType.Image.SVG
                else -> return@get call.respond(HttpStatusCode.NoContent,"올바르지 않는 이미지 형식! 제출양식 또는 데이터베이스를 점검해주세요.(Hint: 양식에 문제가 있거나, 불러오려는 이미지 확장자가 지원되지 않는 확장자에요)")
            }

            if(!conDir.exists())
                return@get call.respond(HttpStatusCode.NotFound,"찾으시는 콘이 존재하지 않아요, 다시 한번 요청을 확인해 주세요")

            call.response.header(
                HttpHeaders.ContentDisposition, "inline"
            )
            call.respondBytes(
                bytes = conDir.readBytes(),
                contentType=contentType,
                status = HttpStatusCode.OK
            )
            return@get
        }
    }
}