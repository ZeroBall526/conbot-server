package com.zeroball0526

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

val imageDbRoute = "${System.getProperty("user.dir")}\\conbot-image"

private class jsonTemplete{
    var name : String? = null
    var type : String? = null
    var address : String? = null
}

fun imageIndexer(route : String){
    //TODO : 이미지 인덱싱 구현
    val path = File(route)
    val listFolder = path.listFiles()

    val categoryJson = JsonObject()
    val category = JsonArray()

    val indexJson = JsonObject()
    //기존 인덱싱 파일 예외 처리
    for(i in listFolder){
        if(i.name == "category" || i.name == "index.json" || i.name == "SECURITY")
            continue

        /*
        //testCode
        println("${i.name} 카테고리 인덱싱 시작!")
         */
        val categoryArray = JsonArray()
        category.add(i.name)
        //템플릿 + 하위 폴더에 있는 콘 파일들 불러온 후 인데싱
        for(con in File(i.path).listFiles()){
            /*
            //testCode
            println("${con.name} 인덱싱중...")
             */

            val conFile = jsonTemplete()
            val fileName = con.name.split(".")

            //인덱싱
            conFile.name = fileName[0]
            conFile.type = if(fileName[fileName.size-1] == "gif") "cg" else "c"
            val conAddress = con.path.split("\\")
            conFile.address = "image/${conAddress[conAddress.size-2]}/${conAddress[conAddress.size-1]}"

            //conList에 업로드
            categoryArray.add(Gson().toJsonTree(conFile).asJsonObject)
        }
        //json에 해당 카테고리 + 콘 업데이트
        indexJson.add(i.name,categoryArray)
    }
    categoryJson.add("categories",category)
    try{
        File("${route}\\index.json").writeText(indexJson.toString())
        File("${route}\\category").writeText(categoryJson.toString())
        println("인덱싱 완료")
    }catch (e: Error){
        println("콘 정보를 인덱싱 중 문제가 발생했어요!")
        e.printStackTrace()
    }
}

fun categoryCon(dbRoute : String,category: String): String?{
    val index = BufferedReader(FileReader(File("${dbRoute}\\index.json"), Charsets.UTF_8)).readLines()[0]

    try{
        val parser = JsonParser.parseString(index) as JsonObject
        val category : JsonArray = parser.get(category) as JsonArray

        return category.toString()
    }catch (e: NullPointerException){
        println("$category 카테고리 검색 실패!")
        return null
    }
}