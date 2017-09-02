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
package de.mc.security.persistence

import com.yubico.u2f.data.DeviceRegistration
import de.mc.security.util.getLogger
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Rob Winch
 */
@Component
class InMemoryDeviceRegistration : IDeviceRegistration {

    private val log = getLogger()
    private val usernameToRegistration = ConcurrentHashMap<String, MutableList<DeviceRegistration>>()

    override fun saveRegistrationForUsername(registration: DeviceRegistration, username: String) {
        log.info("save registration for $username : $registration")
        findRegistrationsByUsername(username).add(registration)
    }

    override fun findRegistrationsByUsername(username: String): MutableList<DeviceRegistration> {
        log.info("find registrations for $username")
        return usernameToRegistration.getOrPut(username, { mutableListOf() })
    }
}
