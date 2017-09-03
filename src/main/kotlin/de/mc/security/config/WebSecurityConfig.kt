package de.mc.security.config

import com.yubico.u2f.U2F
import de.mc.security.persistence.IRequestStorage
import de.mc.security.persistence.InMemoryRequestStorage
import de.mc.security.persistence.InMemoryUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService


/**
 * @author Ralf Ulrich
 * 02.09.17
 */
@Configuration
@EnableWebSecurity
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http
                .authorizeRequests()
                .antMatchers("/img/**", "/webjars/**").permitAll()
                .antMatchers("/signup.html").permitAll()
                .antMatchers("/u2f/register/*").permitAll()
                .antMatchers("/u2f/authenticate/*").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().successHandler({ req, res, auth -> res.sendRedirect("/home") })
                .authenticationDetailsSource({ context -> U2fWebAuthenticationDetails(context) })
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .permitAll()
    }

    @Autowired
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService())
        auth.authenticationProvider(daoAuthenticationProvider())
    }

    @Bean
    fun daoAuthenticationProvider(): DaoAuthenticationProvider =
            U2fAuthenticationProvider(requestStorage()).apply {
                setUserDetailsService(userDetailsService())
            }

    @Bean
    fun requestStorage(): IRequestStorage = InMemoryRequestStorage()

    @Bean
    override fun userDetailsService(): UserDetailsService = InMemoryUserDetailsService()

    @Bean
    fun u2f() = U2F()
}