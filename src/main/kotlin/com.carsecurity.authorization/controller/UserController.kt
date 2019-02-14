package com.carsecurity.authorization.controller

import com.carsecurity.authorization.domain.User
import com.carsecurity.authorization.domain.dto.UserDTO
import com.carsecurity.authorization.service.RoleService
import com.carsecurity.authorization.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("user")
class UserController(
        private val userService: UserService,
        private val roleService: RoleService

) {
    private val logger = LoggerFactory.getLogger(javaClass)


    @GetMapping(params = ["id"])
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userDetailsServiceImpl.isOwner(principal, #userId)")
    fun getUser(@RequestParam(name = "id") userId: Long): ResponseEntity<UserDTO> {

        val userOptional = userService.getUser(userId)

        return if (userOptional.isPresent) {
            ResponseEntity.ok(UserDTO(userOptional.get()))
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    fun getUsers(): List<UserDTO> = userService.getUsers().map { user -> UserDTO(user) }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER_REGISTRATION_CLIENT')")
    fun createUser(@RequestBody userCreate: UserDTO): ResponseEntity<UserDTO> {

        userCreate.id = 0
        val userOptional = getAndCheckUser(userCreate)
        if(!userOptional.isPresent) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        val createdUserOptional = userService.tryCreate(userOptional.get())
        return if (createdUserOptional.isPresent) {
            ResponseEntity(UserDTO(createdUserOptional.get()), HttpStatus.CREATED)
        } else {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userDetailsServiceImpl.isOwner(principal, #userUpdate.id)")
    fun updateUser(@RequestBody userUpdate: UserDTO): ResponseEntity<UserDTO> {

        val userOptional = userService.getUser(userUpdate.id)
        if(!userOptional.isPresent) {
            logger.debug("Can not update user. User does not exists.")
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        val updateDbUser = getAndCheckUser(userUpdate)
        if(!updateDbUser.isPresent) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        val updatedUserOptional = userService.update(updateDbUser.get())
        return if (updatedUserOptional.isPresent) {
            ResponseEntity.ok(UserDTO(updatedUserOptional.get()))
        } else {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userDetailsServiceImpl.isOwner(principal, #userId)")
    fun deleteUser(@RequestParam(name = "id") userId: Long) {
        userService.deleteById(userId)

        // TODO (after delete user logout user (remove token))
    }

    private fun getAndCheckUser(userDTO: UserDTO): Optional<User> {

        val roles = roleService.findRolesByName(userDTO.roles.toList())

        if (
                userDTO.username.isBlank() ||
                userDTO.password.isBlank() ||
                roles.size != userDTO.roles.size
        ) {
            logger.debug("Username is empty OR password is empty OR roles does not exists." +
                    "\nUsername: ${userDTO.username}" +
                    "\nPassword: ${userDTO.password}" +
                    "\nRoles: ${userDTO.roles.joinToString()}")
            return Optional.empty()
        }

        val user = User(
                id = userDTO.id,
                username = userDTO.username,
                password = userDTO.password,
                roles = roles.toHashSet(),
                nonExpired = userDTO.nonExpired,
                nonLocked = userDTO.nonLocked,
                enabled = userDTO.enabled,
                credentialsNonExpired = userDTO.credentialsNonExpired)

        return Optional.of(user)
    }
}