package com.zeroball0526

import com.zeroball0526.properties.PropertiesStore
import java.io.BufferedReader
import java.io.FileReader
import java.io.File
import java.security.SecureRandom
import java.time.format.DateTimeFormatter
import java.time.LocalDate


class Token{
    /**
     * 랜덤 토큰을 생성 또는 재생성하고 SECURITY 파일에 저장하고 값으로 불러옵니다
     * @param length 자릿수
     * @param isUpperCase token 문자 대문자여부
     * @param route SECURITY 파일 저장 위치(별도 격리를 원할 때)
     * @return 생성된 Token값
     */
    fun generateSecurityToken(length: Int, isUpperCase: Boolean, route: String): String {
        val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

        val random = SecureRandom()
        val sb = StringBuilder(length)

        val result : String?

        for(i in 0..<length){
            val value = random.nextInt(characters.length)
            sb.append(characters[value])
        }

        try{
            result = if(isUpperCase) sb.toString() else sb.toString().lowercase()
            File("${route}\\SECURITY").writeText(result)
        }catch (e : Error){
            customLogger.error("Token - 토큰을 생성하는 도중 오류가 발생했습니다!")
            e.printStackTrace()
            return false.toString()
        }

        return result
    }

    /**
     * 토큰 초기화 주기인지 확인을 시켜줍니다. (기본 값은 MM-DD 기준으로 비교합니다)
     * @param doActive 작동시킬지 여부
     * @param activeDate 초기시킬 날짜
     * @return Boolean
     */
    fun resetToken(doActive: Boolean, activeDate: List<String>): Boolean{
        if(!doActive)
            return false

        val formatter =  DateTimeFormatter.ofPattern("MM-dd")
        val currentTime = LocalDate.now().format(formatter)

        for (reservedDate in activeDate){
            if (reservedDate == currentTime)
                return true
        }
        return false
    }

    /**
     * 저장된 토큰 값을 읽어옵니다.
     * @param route 토큰파일 위치 주소
     * @return token
     */
    fun getToken(route: String): String? {
        val securityFile = File("${route}\\SECURITY")

        if(!securityFile.exists()){
            Exception("SECURITY FILE이 존재하지 않습니다! 데이터베이스를 체크해주세요!")
            return null
        }

        val result = BufferedReader(FileReader(securityFile, Charsets.UTF_8)).readLines()[0]
        return result
    }

    /**
     * 입력받은 토큰이 유효한지 검증합니다.
     * @param inputToken 검증할 토큰 값
     * @param tokenRoute 토큰 저장 위치
     * @return boolean
     */
    fun isValidToken(inputToken : String,tokenRoute : String): Boolean {
        val propVal = PropertiesStore.getProperty("useToken").toBoolean()
        if(propVal == false) {
            customLogger.info("Token - 경고! 토큰기능이 비활성화 되어 있어요! 이것이 무슨 위험인지 잘 아실거라 믿어요.")
            customLogger.info("Token - 디버깅이 아니다면 토큰 기능 활성화를 권장합니다. 자세한 설정은 setting.properties 파일을 확인해주세요.")
        }
        return !((inputToken.isNotEmpty() && inputToken == getToken(tokenRoute)) || !propVal)
    }

}
