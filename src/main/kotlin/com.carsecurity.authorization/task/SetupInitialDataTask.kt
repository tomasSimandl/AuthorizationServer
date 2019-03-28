package com.carsecurity.authorization.task

import com.carsecurity.authorization.domain.Role
import com.carsecurity.authorization.domain.User
import com.carsecurity.authorization.service.RoleService
import com.carsecurity.authorization.service.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Class used for filling database with some mock data.
 *
 * @param userService for access data in user table.
 * @param roleService for access data in role table.
 * @param initializationMode is mode if data should be inserted in database. Take from properties.
 */
@Component
class SetupUserDataTask(
        private val userService: UserService,
        private val roleService: RoleService,
        @Value("\${spring.datasource.initialization-mode}")
        private val initializationMode: String
) : ApplicationListener<ContextRefreshedEvent> {
    /** Identification if database fill already been perform. */
    private var alreadySetup = false

    /**
     * Method insert mock users and roles to database. When [initializationMode] is set to 'never' no data are
     * inserted.
     */
    @Transactional
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        if (alreadySetup) return
        if (initializationMode == "never") return
        alreadySetup = true

        val adminRole = roleService.tryCreate(Role(name = "ROLE_ADMIN")).get()
        val userRole = roleService.tryCreate(Role(name = "ROLE_USER")).get()
        val superRole = roleService.tryCreate(Role(name = "ROLE_SUPER_ADMIN")).get()

        userService.tryCreate(User(username = "admin", password = "admin", email = "tomas.simandl@gmail.com", roles = hashSetOf(adminRole, userRole, superRole)))
        userService.tryCreate(User(username = "user", password = "123456", email = "simi159@seznam.cz", roles = hashSetOf(userRole)))
    }
}