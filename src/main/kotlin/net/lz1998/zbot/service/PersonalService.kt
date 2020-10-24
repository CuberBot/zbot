package net.lz1998.zbot.service

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.lz1998.zbot.config.ZbotConfig
import net.lz1998.zbot.entity.WcaUser
import net.lz1998.zbot.repository.WcaUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


@Serializable
data class AccessTokenReq(
        @SerialName("code") val code: String,
        @SerialName("client_id") val clientId: String,
        @SerialName("client_secret") val clientSecret: String,
        @SerialName("redirect_uri") val redirectUri: String,
        @SerialName("grant_type") val grantType: String
)

@Serializable
data class AccessTokenResp(
        @SerialName("access_token") val accessToken: String = "",
        @SerialName("token_type") val tokenType: String = ""
)


@Serializable
data class MeResp(
        val me: Me? = null
)

@Serializable
data class Me(
        @SerialName("wca_id") val wcaId: String = "",
        @SerialName("name") val name: String = "",
        @SerialName("gender") val gender: String = ""
)

@Service
class PersonalService {
    @Autowired
    lateinit var restTemplate: RestTemplate
    val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }

    @Autowired
    lateinit var wcaUserRepository: WcaUserRepository
    fun setWcaCode(code: String, redirectUri: String): WcaUser? {
        val accessTokenResp = getToken(code, redirectUri) ?: return null
        return getWcaUserByToken(accessTokenResp.accessToken)
    }

    fun getToken(code: String, redirectUri: String): AccessTokenResp? {
        // 获取wca token
        val accessTokenReq = AccessTokenReq(code, ZbotConfig.wcaClientId, ZbotConfig.wcaClientSecret, redirectUri, "authorization_code")
        val reqStr = json.encodeToString(AccessTokenReq.serializer(), accessTokenReq)
        val postHeaders = HttpHeaders()
        postHeaders.contentType = MediaType.APPLICATION_JSON
        val request = HttpEntity<String>(reqStr, postHeaders)
        val accessTokenRespStr = restTemplate.postForObject("https://www.worldcubeassociation.org/oauth/token", request, String::class.java)
                ?: return null
        return json.decodeFromString(AccessTokenResp.serializer(), accessTokenRespStr)
    }

    fun getWcaUserByToken(accessToken: String): WcaUser? {
        val headers = HttpHeaders()
        headers.setBearerAuth(accessToken)
        val entity = HttpEntity<String>(null, headers)
        val meRespStr = restTemplate.exchange("https://www.worldcubeassociation.org/api/v0/me", HttpMethod.GET, entity, String::class.java).body
                ?: return null
        val meResp = json.decodeFromString(MeResp.serializer(), meRespStr)
        val userId = SecurityContextHolder.getContext().authentication.principal as Long
        val wcaUser = WcaUser(
                userId = userId,
                wcaId = meResp.me?.wcaId ?: "",
                name = meResp.me?.name ?: "",
                gender = meResp.me?.gender ?: "",
                open = true,
                defaultAttend = true,
                enabled = true
        )
        if (wcaUser.wcaId.isNotEmpty()) {
            wcaUserRepository.save(wcaUser)
            return wcaUser
        }
        return null
    }

    fun getWcaUser(userId: Long): WcaUser? {
        return wcaUserRepository.findWcaUserByUserId(userId)
    }

    fun updateWcaUser(userId: Long, open: Boolean, defaultAttend: Boolean) {
        val wcaUser = wcaUserRepository.findWcaUserByUserId(userId) ?: return
        wcaUser.defaultAttend = defaultAttend
        wcaUser.open = open
        wcaUserRepository.save(wcaUser)
    }
}