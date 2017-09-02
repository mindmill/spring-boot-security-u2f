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

import com.yubico.u2f.data.messages.RegisterRequestData
import com.yubico.u2f.data.messages.SignRequestData
import de.mc.security.util.getLogger
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Rob Winch
 */
@Component
class InMemoryRequestStorage : IRequestStorage {

    private val log = getLogger()
    private val idToData = ConcurrentHashMap<String, SignRequestData>()
    private val idToRegisterData = ConcurrentHashMap<String, RegisterRequestData>()

    override fun save(request: RegisterRequestData) {
        log.info("save register request $request")
        idToRegisterData.put(request.requestId, request)
    }

    override fun deleteRegistration(id: String): RegisterRequestData? {
        log.info("delete registration $id")
        return idToRegisterData.remove(id)
    }

    override fun save(request: SignRequestData) {
        log.info("save sign request $request")
        idToData.put(request.requestId, request)
    }

    override fun delete(id: String): SignRequestData? {
        log.info("delete sign request $id")
        return idToData.remove(id)
    }
}
