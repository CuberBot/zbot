package net.lz1998.zbot.controller

import com.volcengine.model.request.TranslateImageRequest
import com.volcengine.service.translate.impl.TranslateServiceImpl
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URL
import java.util.*

@RestController
@RequestMapping("/translate")
class TranslateController {
    val translateService = TranslateServiceImpl.getInstance()!!
    val base64Encoder = Base64.getEncoder()
    val base64Decoder = Base64.getDecoder()


    @RequestMapping("/image", produces = ["image/png"])
    fun image(sourceLanguage: String, targetLanguage: String, url: String): ByteArray? {
        val bytes = URL(url).openStream().readBytes()

        val req = TranslateImageRequest()
        req.image = base64Encoder.encodeToString(bytes)
        req.sourceLanguage = sourceLanguage
        req.targetLanguage = targetLanguage
        val resp = translateService.translateImage(req)
        return base64Decoder.decode(resp.translateImage)
    }
}