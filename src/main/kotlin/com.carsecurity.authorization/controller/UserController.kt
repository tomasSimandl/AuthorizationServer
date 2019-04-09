package com.carsecurity.authorization.controller

import com.carsecurity.authorization.domain.User
import com.carsecurity.authorization.domain.dto.UserDTO
import com.carsecurity.authorization.service.RoleService
import com.carsecurity.authorization.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * This controller is used for access and manage users in database.
 *
 * @param userService is service for access users in database.
 * @param roleService is service for access roles in database.
 * @param passwordEncoder is encoder used for hashing passwords.
 */
@RestController
@RequestMapping("user")
class UserController(
        private val userService: UserService,
        private val roleService: RoleService,
        private val passwordEncoder: PasswordEncoder

) {
    /** Logger of this class. */
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Method get and return requested user from database. For access login user must be admin or be the user.
     *
     * @param userId identification number of user.
     * @return user which was found in database.
     */
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

    /**
     * Method get and return requeseted user from database. For access login user must be admin or be the user.
     *
     * @param username is username of requested user.
     * @return user which was found in database.
     */
    @GetMapping(params = ["username"])
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userDetailsServiceImpl.isOwner(principal, #username)")
    fun getUserByUsername(@RequestParam(name = "username") username: String): ResponseEntity<UserDTO> {

        val userOptional = userService.findByUsername(username)

        return if (userOptional.isPresent) {
            ResponseEntity.ok(UserDTO(userOptional.get()))
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Method return list of all users in database. For this resource have access only user with ADMIN role.
     *
     * @return list of [UserDTO] which was found in database.
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    fun getUsers(): List<UserDTO> = userService.getUsers().map { user -> UserDTO(user) }

    /**
     * Method creates new user in database. Users can be created by user with ADMIN role or client with
     * USER_REGISTRATION role.
     *
     * @param userCreate user which will be created in database.
     * @return user which was created in database.
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER_REGISTRATION_CLIENT')")
    fun createUser(@RequestBody userCreate: UserDTO): ResponseEntity<UserDTO> {

        userCreate.id = 0
        val userOptional = getAndCheckUser(userCreate)
        if (!userOptional.isPresent) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        val createdUserOptional = userService.tryCreate(userOptional.get())
        return if (createdUserOptional.isPresent) {
            ResponseEntity(UserDTO(createdUserOptional.get()), HttpStatus.CREATED)
        } else {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    /**
     * Method update user in database. This operation can perform only user with role ADMIN and user can update own
     * account.
     *
     * @param userUpdate is user which will be updated in database.
     * @return updated user from database.
     */
    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userDetailsServiceImpl.isOwner(principal, #userUpdate.id)")
    fun updateUser(@RequestBody userUpdate: UserDTO): ResponseEntity<UserDTO> {

        val userOptional = userService.getUser(userUpdate.id)
        if (!userOptional.isPresent) {
            logger.debug("Can not update user. User does not exists.")
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        val updateDbUser = getAndCheckUser(userUpdate)
        if (!updateDbUser.isPresent) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        val updatedUserOptional = userService.update(updateDbUser.get())
        return if (updatedUserOptional.isPresent) {
            ResponseEntity.ok(UserDTO(updatedUserOptional.get()))
        } else {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    /**
     * Method update only users email in database. This operation can perform only user with role ADMIN and user can
     * update own account.
     *
     * @param email new email which will be stored in database.
     * @param userId of user which will be updated in database.
     * @return user from database which was updated.
     */
    @PutMapping(params = ["id", "email"])
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userDetailsServiceImpl.isOwner(principal, #userId)")
    fun updateUsersEmail(
            @RequestParam(name = "id") userId: Long,
            @RequestParam(name = "email") email: String
    ): ResponseEntity<UserDTO> {

        val userOptional = userService.getUser(userId)
        if (!userOptional.isPresent) {
            logger.debug("Can not update user. User does not exists.")
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        userOptional.get().email = email
        val updatedUserOptional = userService.updateWithoutPassword(userOptional.get())
        return if (updatedUserOptional.isPresent) {
            ResponseEntity.ok(UserDTO(updatedUserOptional.get()))
        } else {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    /**
     * Method update only users password in database. This operation can perform only user with role ADMIN and user can
     * update own account.
     *
     * @param oldPassword old password which will be updated.
     * @param newPassword new users password.
     * @param userId of user which will be updated in database.
     * @return user from database which was updated.
     */
    @PutMapping(params = ["id", "old_password", "new_password"])
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userDetailsServiceImpl.isOwner(principal, #userId)")
    fun updateUsersPassword(
            @RequestParam(name = "id") userId: Long,
            @RequestParam(name = "old_password") oldPassword: String,
            @RequestParam(name = "new_password") newPassword: String
    ): ResponseEntity<UserDTO> {

        val userOptional = userService.getUser(userId)
        if (!userOptional.isPresent) {
            logger.debug("Can not update user. User does not exists.")
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        if (!passwordEncoder.matches(oldPassword, userOptional.get().password)) {
            logger.debug("Can not update users password. Old password did not match.")
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }

        val newUser = userOptional.get().copy(password = newPassword)
        val updatedUserOptional = userService.update(newUser)
        return if (updatedUserOptional.isPresent) {
            ResponseEntity.ok(UserDTO(updatedUserOptional.get()))
        } else {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    /**
     * Method delete user in database. This operation can perform only user with role ADMIN and user can update
     * own account.
     *
     * @param userId identification of user which will be deleted.
     */
    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userDetailsServiceImpl.isOwner(principal, #userId)")
    fun deleteUser(@RequestParam(name = "id") userId: Long) {
        userService.deleteById(userId)

        // TODO (after delete user logout user (remove token))
    }

    /**
     * Method check input [userDTO] and when everything is OK return user created form [userDTO] which can be stored
     * in database.
     * @param userDTO is user of which will be created database user.
     * @return empty [Optional] when input user is invalid or [User] when everything is OK.
     */
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