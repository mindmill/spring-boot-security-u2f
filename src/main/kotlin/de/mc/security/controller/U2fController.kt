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
import com.yubico.u2f.data.messages.SignResponse
import de.mc.security.persistence.IDeviceRegistration
import de.mc.security.persistence.IRequestStorage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import java.security.Principal

/**
 * @author Rob Winch
 */
@Controller
class U2fController @Autowired constructor(val requestStorage: IRequestStorage,
                                           val devices: IDeviceRegistration,
                                           val u2f: U2F) {

    internal val SERVER_ADDRESS = "https://localhost:8443"

    @RequestMapping("/u2f/register")
    fun registerForm(principal: Principal, model: MutableMap<String, Any>): String {
        val username = principal.name
        val registerRequestData = u2f.startRegistration(SERVER_ADDRESS, getRegistrations(username))
        requestStorage.save(registerRequestData)
        model.put("data", registerRequestData.toJson())
        return "u2f/register"
    }


    @RequestMapping(value = "/u2f/register", method = arrayOf(RequestMethod.POST))
    fun register(principal: Principal, @RequestParam("tokenResponse") response: String): String {
        val username = principal.name
        val registerResponse = RegisterResponse.fromJson(response)
        val registerRequestData = requestStorage.deleteRegistration(registerResponse.requestId)
        val registration = u2f.finishRegistration(registerRequestData, registerResponse)
        devices.saveRegistrationForUsername(registration, username)
        return "redirect:/u2f/authenticate"
    }

    @RequestMapping("/u2f/authenticate")
    fun authenticateForm(principal: Principal, model: MutableMap<String, Any>): String {
        val username = principal.name
        // Generate a challenge for each U2F device that this user has
        // registered
        val requestData = u2f.startSignature(SERVER_ADDRESS, getRegistrations(username))

        // Store the challenges for future reference
        requestStorage.save(requestData)

        // Return an HTML page containing the challenges
        model.put("data", requestData.toJson())
        return "u2f/authenticate"
    }

    @RequestMapping(value = "/u2f/authenticate", method = arrayOf(RequestMethod.POST))
    fun authenticate(@RequestParam tokenResponse: String, principal: Principal): String {
        val response = SignResponse.fromJson(tokenResponse)
        val username = principal.name

        // Get the challenges that we stored when starting the authentication
        val authenticateRequest = requestStorage.delete(response.requestId)
        // Verify the that the given response is valid for one of the registered
        // devices
        u2f.finishSignature(authenticateRequest, response, getRegistrations(username))
        return "u2f/success"
    }

    private fun getRegistrations(username: String): List<DeviceRegistration> {
        return devices.findRegistrationsByUsername(username)
    }

}
