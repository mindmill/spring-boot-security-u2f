package de.mc.security.config

import com.yubico.u2f.U2F
import com.yubico.u2f.data.messages.SignResponse
import de.mc.security.persistence.IRequestStorage
import de.mc.security.persistence.InMemoryUserDetailsService
import de.mc.security.util.getLogger
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.core.Authentication


/**
 * ralfulrich on 02.09.17.
 */
class U2fAuthenticationProvider(val requestStorage: IRequestStorage) : DaoAuthenticationProvider() {

    private val log = getLogger()
    private val u2f = U2F()

    override fun authenticate(auth: Authentication): Authentication {
        log.info("authenticate ...")
        val imUserDetails = userDetailsService as InMemoryUserDetailsService
        val signData = (auth.getDetails() as U2fWebAuthenticationDetails).signData
        val user = try {
            imUserDetails.getUser(auth.name)
        } catch (e: Exception) {
            throw BadCredentialsException("Invalid username or password")
        }
        val response = SignResponse.fromJson(signData)

        // Get the challenges that we stored when starting the authentication
        val authenticateRequest = requestStorage.delete(response.requestId)
        // Verify the that the given response is valid for one of the registered
        // devices
        u2f.finishSignature(authenticateRequest, response, user.deviceRegistrations)

        val result = super.authenticate(auth)
        return UsernamePasswordAuthenticationToken(
                user, result.credentials, result.authorities)
    }


    override fun supports(authentication: Class<*>): Boolean {
        return authentication == UsernamePasswordAuthenticationToken::class.java
    }


}