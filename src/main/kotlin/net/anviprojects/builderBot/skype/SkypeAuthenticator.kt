package net.anviprojects.builderBot.skype

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.microsoft.bot.connector.authentication.CredentialProvider
import com.microsoft.bot.connector.authentication.CredentialProviderImpl
import com.microsoft.bot.connector.authentication.JwtTokenValidation
import com.microsoft.bot.connector.authentication.MicrosoftAppCredentials
import com.microsoft.bot.schema.models.Activity
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

//TODO рассмотреть вариант вынесения кэша отдельно, т.к. если приложение рестартнется - оно получит другой токен, с которым
// скайп может отказаться работать нормально. Но стоит для начала проверить эту теорию
class SkypeAuthenticator(appId : String, appSecret : String) {

    private var authCache : Cache<String, String>
    var credentials : MicrosoftAppCredentials
    var credentialProvider : CredentialProvider
    private val TOKEN = "token"

    init {
        this.credentials = MicrosoftAppCredentials(appId, appSecret)
        this.credentialProvider = CredentialProviderImpl(appId, appSecret)
        this.authCache = CacheBuilder.newBuilder()
                .expireAfterWrite(3600, TimeUnit.SECONDS)
                .maximumSize(1)
                .build()
    }

    fun getToken(key : String = TOKEN) = this.authCache.getIfPresent(key)

    fun setToken(key : String = TOKEN, value : String) {
        if (authCache.size() < 1) {
            this.authCache.put(key, value)
        }
    }

    fun authenticateRequest(activity : Activity) {
        JwtTokenValidation.authenticateRequest(activity, getToken(), this.credentialProvider)
    }
}