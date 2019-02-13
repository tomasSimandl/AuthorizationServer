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


        val adminRole = createRoleIfNotFound("ROLE_ADMIN")
        val userRole = createRoleIfNotFound("ROLE_USER")

        createUserIfNotFound("user1", "abcd1234", hashSetOf(adminRole, userRole))
        createUserIfNotFound("user2", "123456", hashSetOf(userRole))
    }

    @Transactional
    fun createRoleIfNotFound(name: String): Role {
        val role = Role(name = name)
        var retrieval = roleService.tryCreate(role)
        if (!retrieval.isPresent)
            retrieval = roleService.findByName(name)
        return retrieval.get()
    }

    @Transactional
    fun createUserIfNotFound(username: String, password: String, roles: Set<Role>): User {
        val user = User(username = username, password = password, roles = roles)
        var retrieval = userService.tryCreate(user)
        if (!retrieval.isPresent)
            retrieval = userService.findByUsername(username)
        return retrieval.get()
    }
}