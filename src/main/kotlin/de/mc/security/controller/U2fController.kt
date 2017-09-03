/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mc.security.controller

import com.yubico.u2f.U2F
import com.yubico.u2f.data.DeviceRegistration
import com.yubico.u2f.data.messages.RegisterResponse
import de.mc.security.persistence.IRequestStorage
import de.mc.security.persistence.InMemoryUserDetailsService
import de.mc.security.persistence.User
import de.mc.security.util.getLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

/**
 * @author Rob Winch
 */
@Controller
class U2fController @Autowired constructor(val requestStorage: IRequestStorage,
                                           val userDetailsService: InMemoryUserDetailsService,
                                           val u2f: U2F) {

    private val log = getLogger()
    internal val SERVER_ADDRESS = "https://localhost:8443"

    @RequestMapping("/u2f/register/challenge", method = arrayOf(RequestMethod.POST))
    fun registerForm(@RequestParam("username") username: String, @RequestParam("password") password: String, model: MutableMap<String, Any>): String {
        try {
            val user = userDetailsService.getUser(username)
            if (user.password != password) throw BadCredentialsException("Password does not match")
        } catch (e: UsernameNotFoundException) {
            userDetailsService.addUser(User(username, password))
        }
        val registerRequestData = u2f.startRegistration(SERVER_ADDRESS, getRegistrations(username))
        requestStorage.save(registerRequestData)
        log.info("challenge start for $username")
        model.put("username", username)
        model.put("password", password)
        model.put("data", registerRequestData.toJson())
        return "/signup"
    }


    @RequestMapping(value = "/u2f/register/response", method = arrayOf(RequestMethod.POST))
    fun registerToken(@RequestParam("username") username: String, @RequestParam("password") password: String, @RequestParam("tokenResponse") response: String): String {
        val registerResponse = RegisterResponse.fromJson(response)
        val registerRequestData = requestStorage.deleteRegistration(registerResponse.requestId)
        val registration = u2f.finishRegistration(registerRequestData, registerResponse)
        log.info("response finished for $username")
        userDetailsService.getUser(username).deviceRegistrations.add(registration)
        return "redirect:/login"
    }

    @RequestMapping("/u2f/authenticate/challenge", method = arrayOf(RequestMethod.POST))
    fun authenticateForm(@RequestParam("username") username: String, @RequestParam("password") password: String, model: MutableMap<String, Any>): String {
        val registrations = try {
            getRegistrations(username)
        } catch (e: Exception) {
            throw BadCredentialsException("Username or Password not ok")
        }


        // Generate a challenge for each U2F device that this user has
        // registered
        val requestData = u2f.startSignature(SERVER_ADDRESS, registrations)

        // Store the challenges for future reference
        requestStorage.save(requestData)

        // Return an HTML page containing the challenges
        model.put("username", username)
        model.put("password", password)
        model.put("data", requestData.toJson())
        return "/login"
    }


    private fun getRegistrations(username: String): List<DeviceRegistration> {
        return userDetailsService.getUser(username).deviceRegistrations
    }

}
