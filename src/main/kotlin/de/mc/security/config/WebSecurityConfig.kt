package de.mc.security.config

import de.mc.security.persistence.InMemoryUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter


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
    }

    @Bean
    override fun userDetailsService() = InMemoryUserDetailsService()

}