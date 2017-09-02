package de.mc.security.persistence

import com.yubico.u2f.data.DeviceRegistration

/**
 * @author Ralf Ulrich
 * 02.09.17
 */
interface IDeviceRegistration {
    fun saveRegistrationForUsername(registration: DeviceRegistration, username: String)
    fun findRegistrationsByUsername(username: String): MutableList<DeviceRegistration>
}