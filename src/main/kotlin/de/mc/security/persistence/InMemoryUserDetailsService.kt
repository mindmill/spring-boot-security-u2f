package de.mc.security.persistence

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
}

data class User(val name: String, val pw: String) : UserDetails {
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