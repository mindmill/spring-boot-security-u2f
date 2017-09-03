package de.mc.security.config

import org.springframework.security.web.authentication.WebAuthenticationDetails
import javax.servlet.http.HttpServletRequest

/**
 * @author Ralf Ulrich
 * 02.09.17
 */
class U2fWebAuthenticationDetails(val context: HttpServletRequest , val signData: String? = context.getParameter("tokenResponse")): WebAuthenticationDetails(context)