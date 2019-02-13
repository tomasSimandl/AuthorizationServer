package com.carsecurity.authorization.config

import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

//class CorsFilterConfig : GenericFilterBean() {
//    override fun doFilter(servletRequest: ServletRequest?, servletResponse: ServletResponse?, chain: FilterChain) {
//        val response = servletResponse as HttpServletResponse
//        val request = servletRequest as HttpServletRequest
//
//        response.setHeader("Access-Control-Allow-Origin", "*")
//        response.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
//        response.setHeader("Access-Control-Allow-Headers", "Authorization,Content-Type")
//        response.setHeader("Access-Control-Allow-Credentials", true.toString())
//
//        if ("OPTIONS".equals(request.method, ignoreCase = true)) {
//            response.status = HttpServletResponse.SC_OK
//        } else {
//            chain.doFilter(servletRequest, servletResponse)
//        }
//    }
//}