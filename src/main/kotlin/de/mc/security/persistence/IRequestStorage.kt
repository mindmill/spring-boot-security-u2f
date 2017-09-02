package de.mc.security.persistence

import com.yubico.u2f.data.messages.RegisterRequestData
import com.yubico.u2f.data.messages.SignRequestData

/**
 * @author Ralf Ulrich
 * 02.09.17
 */
interface IRequestStorage {
    fun save(request: RegisterRequestData)
    fun deleteRegistration(id: String): RegisterRequestData?
    fun save(request: SignRequestData)
    fun delete(id: String): SignRequestData?
}