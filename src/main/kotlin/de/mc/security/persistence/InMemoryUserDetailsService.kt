package de.mc.security.persistence

import com.yubico.u2f.data.DeviceRegistration
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Ralf Ulrich
 * 02.09.17
 */
class InMemoryUserDetailsService : UserDetailsService {

    private val userMap = ConcurrentHashMap<String, User>()

    init {
        userMap.put("user", User("user", "password"))
    }

    override fun loadUserByUsername(username: String): UserDetails {
        return userMap[username] ?: throw UsernameNotFoundException("User $username not found")
    }

    fun addUser(user: User) {
        userMap.put(user.username, user)
    }

    fun getUser(username: String): User{
        return loadUserByUsername(username) as User
    }
}

data class User(val name: String, val pw: String, val deviceRegistrations: MutableList<DeviceRegistration> = mutableListOf()) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = mutableListOf(SimpleGrantedAuthority("ROLE_user"))
    override fun isEnabled() = true
    override fun getUsername() = name
    override fun isCredentialsNonExpired() = true
    override fun getPassword() = pw
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun toString(): String {
        return "User($name)"
    }
}