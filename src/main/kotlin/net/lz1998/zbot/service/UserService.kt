package net.lz1998.zbot.service

import com.fasterxml.jackson.databind.util.LRUMap
import net.lz1998.zbot.entity.User
import net.lz1998.zbot.repository.UserRepository
import net.lz1998.zbot.security.JwtUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.util.*

@Service
class UserService {
    // 不包括0，1，O，I
    private val VERIFICATION_CODE_LETTERS = "1234567890"
    private val VERIFICATION_CODE_LENGTH = 6
    private val EXPIRE_TIME = 600000L

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    lateinit var jwtUtil: JwtUtil

    val userCacheLru = LRUMap<String, UserCache>(16, 128)

    data class UserCache(
            val user: User,
            val startTime: Long = 0
    )

    var random = Random()


    fun getUser(userId: Long): User? {
        return userRepository.findUserByUserId(userId)
    }

    /**
     * 登陆
     * @param userId 用户ID，QQ
     * @param password 密码
     * @return 成功返回token，失败返回null
     */
    fun login(userId: Long, password: String): String? {
        val user: User = userRepository.findUserByUserId(userId) ?: return null

        // 账号不存在
        return if (passwordEncoder.matches(password, user.password)) {
            jwtUtil.createJWT(true, userId) // 生成token
        } else null
    }

    /**
     * 设置临时用户，存在Map中
     * @param userId 用户ID，QQ
     * @param password 用户密码，需要加密
     * @return 验证码
     */
    fun setTmpUser(userId: Long, password: String): String? {
        val encryptedPassword = passwordEncoder.encode(password)
        val user = User(userId = userId, password = encryptedPassword)
        val userCache = UserCache(user, System.currentTimeMillis())
        val verificationCode = getVerificationCode()
        userCacheLru.put(verificationCode, userCache)
        return verificationCode
    }

    /**
     * 私聊收到验证码后注册
     * @param verificationCode 验证码
     * @return 注册结果
     */
    fun register(userId: Long, verificationCode: String): String {
        if (StringUtils.isEmpty(verificationCode)) {
            return "验证码为空"
        }
        val userCache = userCacheLru[verificationCode]
        if (userCache == null || userCache.startTime + EXPIRE_TIME < System.currentTimeMillis()) {
            return "验证码错误"
        }
        val user: User = userCache.user
        // 校验是否本人注册
        if (userId == user.userId) {
            userRepository.save(user)
            return "设置成功"
        }
        return "验证码错误"
    }

    /**
     * 获取验证码
     * @return 验证码
     */
    private fun getVerificationCode(): String {
        var verificationCode = ""
        for (i in 0 until VERIFICATION_CODE_LENGTH) {
            verificationCode += VERIFICATION_CODE_LETTERS[random.nextInt(VERIFICATION_CODE_LETTERS.length)]
        }

        // 如果重复，重新生成
        if (userCacheLru.get(verificationCode) != null) {
            verificationCode = getVerificationCode()
        }
        return verificationCode
    }


}