package com.carsecurity.authorization.task

import com.carsecurity.authorization.domain.Role
import com.carsecurity.authorization.domain.User
import com.carsecurity.authorization.service.RoleService
import com.carsecurity.authorization.service.UserService
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class SetupUserDataTask(
        private val userService: UserService,
        private val roleService: RoleService
) : ApplicationListener<ContextRefreshedEvent> {
    private var alreadySetup = false

    @Transactional
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        if (alreadySetup) return
        alreadySetup = true

        val adminRole = roleService.tryCreate(Role(name = "ROLE_ADMIN")).get()
        val userRole = roleService.tryCreate(Role(name = "ROLE_USER")).get()
        val superRole = roleService.tryCreate(Role(name = "ROLE_SUPER_ADMIN")).get()

        userService.tryCreate(User(username = "admin", password = "admin", roles = hashSetOf(adminRole, userRole, superRole)))
        userService.tryCreate(User(username = "user", password = "123456", roles = hashSetOf(userRole)))
    }
}