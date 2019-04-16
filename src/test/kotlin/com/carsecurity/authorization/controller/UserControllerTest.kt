package com.carsecurity.authorization.controller


import com.carsecurity.authorization.domain.Role
import com.carsecurity.authorization.domain.User
import com.carsecurity.authorization.domain.dto.UserDTO
import com.carsecurity.authorization.service.RoleService
import com.carsecurity.authorization.service.UserService
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.MockitoAnnotations
import org.mockito.internal.verification.Times
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

class UserControllerTest {

    @Mock
    private lateinit var userService: UserService

    @Mock
    private lateinit var roleService: RoleService

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    private lateinit var userController: UserController

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)

        userController = UserController(userService, roleService, passwordEncoder)
    }

    @Test
    fun `get user success`() {
        val user = User(id = 123, username = "Emil")

        doReturn(Optional.of(user)).`when`(userService).getUser(123)

        val result = userController.getUser(123)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(user.id, result.body?.id)
        assertEquals(user.username, result.body?.username)
    }

    @Test
    fun `get user not existing user`() {
        val userOptional: Optional<User> = Optional.empty()
        doReturn(userOptional).`when`(userService).getUser(123)

        val result = userController.getUser(123)
        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
    }

    @Test
    fun `get user by username success`() {
        val user = User(id = 123, username = "Emil")

        doReturn(Optional.of(user)).`when`(userService).findByUsername(user.username)

        val result = userController.getUserByUsername(user.username)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(user.id, result.body?.id)
        assertEquals(user.username, result.body?.username)
    }

    @Test
    fun `get user by username not existing user`() {
        val userOptional: Optional<User> = Optional.empty()
        doReturn(userOptional).`when`(userService).findByUsername("Pavel")

        val result = userController.getUserByUsername("Pavel")
        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
    }

    @Test
    fun `get users success`() {
        val user1 = User(id = 123, username = "Emil")
        val user2 = User(id = 321, username = "Lime")

        doReturn(listOf(user1, user2)).`when`(userService).getUsers()

        val result = userController.getUsers()

        assertEquals(2, result.size)
        assertEquals(user1.username, result.first().username)
        assertEquals(user1.id, result.first().id)
        assertEquals(user2.username, result.last().username)
        assertEquals(user2.id, result.last().id)
    }

    @Test
    fun `create user success`() {
        val createUser = UserDTO(123, "Emil", "12345678", "emil@mail.com", emptySet(), false, false, false, false)
        val createdUser = User(2, "Emil", "12345678", "emil@mail.com", emptySet(), mutableSetOf(), false, false, false, false)

        doReturn(emptyList<Role>()).`when`(roleService).findRolesByName(emptyList())
        val userCaptor = argumentCaptor<User>()
        doReturn(Optional.of(createdUser)).`when`(userService).tryCreate(userCaptor.capture())

        val result = userController.createUser(createUser)

        assertEquals(HttpStatus.CREATED, result.statusCode)
        compareUsers(createdUser, result.body!!)
        compareUsers(createUser.copy(id = 0), userCaptor.firstValue)
    }

    @Test
    fun `create user can not create user in database`() {
        val createUser = UserDTO(123, "Emil", "12345678", "emil@mail.com", emptySet(), false, false, false, false)
        val userOptional: Optional<User> = Optional.empty()

        doReturn(emptyList<Role>()).`when`(roleService).findRolesByName(emptyList())
        doReturn(userOptional).`when`(userService).tryCreate(any())

        val result = userController.createUser(createUser)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        verify(userService).tryCreate(any())
    }

    @Test
    fun `create user short password`() {
        val createUser = UserDTO(123, "Emil", "123456", "emil@mail.com", emptySet(), false, false, false, false)

        doReturn(emptyList<Role>()).`when`(roleService).findRolesByName(emptyList())

        val result = userController.createUser(createUser)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        verify(userService, Times(0)).tryCreate(any())
    }

    @Test
    fun `create user blank username`() {
        val createUser = UserDTO(123, "     ", "12345678", "emil@mail.com", emptySet(), false, false, false, false)

        doReturn(emptyList<Role>()).`when`(roleService).findRolesByName(emptyList())

        val result = userController.createUser(createUser)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        verify(userService, Times(0)).tryCreate(any())
    }

    @Test
    fun `create user blank password`() {
        val createUser = UserDTO(123, "Emil", " ", "emil@mail.com", emptySet(), false, false, false, false)

        doReturn(emptyList<Role>()).`when`(roleService).findRolesByName(emptyList())

        val result = userController.createUser(createUser)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        verify(userService, Times(0)).tryCreate(any())
    }

    @Test
    fun `create user invalid role`() {
        val createUser = UserDTO(123, "Emil", "12345678", "emil@mail.com", setOf("role 1"), false, false, false, false)

        doReturn(emptyList<Role>()).`when`(roleService).findRolesByName(listOf("role1"))

        val result = userController.createUser(createUser)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        verify(userService, Times(0)).tryCreate(any())
    }

    @Test
    fun `update user success`() {
        val role = Role(1, "role 1")
        val updateUser = UserDTO(2, "Emil", "12345678", "emil@mail.com", setOf("role 1"), false, true, false, true)
        val existingUser = User(2, "Emil", "12345678", "emil@mail.com", emptySet(), mutableSetOf(), false, false, false, false)
        val updatedUser = User(2, "Emil", "12345678", "emil@mail.com", setOf(role), mutableSetOf(), false, true, false, true)

        doReturn(Optional.of(existingUser)).`when`(userService).getUser(2)
        doReturn(listOf(role)).`when`(roleService).findRolesByName(listOf("role 1"))
        val userCaptor = argumentCaptor<User>()
        doReturn(Optional.of(updatedUser)).`when`(userService).update(userCaptor.capture())

        val result = userController.updateUser(updateUser)

        assertEquals(HttpStatus.OK, result.statusCode)
        compareUsers(updatedUser, result.body!!)
        compareUsers(updateUser, userCaptor.firstValue)
    }

    @Test
    fun `update user not existing user`() {
        val updateUser = UserDTO(2, "Emil", "12345678", "emil@mail.com", setOf("role 1"), false, true, false, true)
        val userOptional: Optional<User> = Optional.empty()

        doReturn(userOptional).`when`(userService).getUser(2)

        val result = userController.updateUser(updateUser)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        verify(userService, Times(0)).update(any())
    }

    @Test
    fun `update user blank username`() {
        val role = Role(1, "role 1")
        val updateUser = UserDTO(2, "", "12345678", "emil@mail.com", setOf("role 1"), false, true, false, true)
        val existingUser = User(2, "Emil", "12345678", "emil@mail.com", emptySet(), mutableSetOf(), false, false, false, false)

        doReturn(Optional.of(existingUser)).`when`(userService).getUser(2)
        doReturn(listOf(role)).`when`(roleService).findRolesByName(listOf("role 1"))

        val result = userController.updateUser(updateUser)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        verify(userService, Times(0)).update(any())
    }

    @Test
    fun `update user blank password`() {
        val role = Role(1, "role 1")
        val updateUser = UserDTO(2, "Emil", "   ", "emil@mail.com", setOf("role 1"), false, true, false, true)
        val existingUser = User(2, "Emil", "12345678", "emil@mail.com", emptySet(), mutableSetOf(), false, false, false, false)

        doReturn(Optional.of(existingUser)).`when`(userService).getUser(2)
        doReturn(listOf(role)).`when`(roleService).findRolesByName(listOf("role 1"))

        val result = userController.updateUser(updateUser)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        verify(userService, Times(0)).update(any())
    }

    @Test
    fun `update user short password`() {
        val role = Role(1, "role 1")
        val updateUser = UserDTO(2, "Emil", "abcdefg", "emil@mail.com", setOf("role 1"), false, true, false, true)
        val existingUser = User(2, "Emil", "12345678", "emil@mail.com", emptySet(), mutableSetOf(), false, false, false, false)

        doReturn(Optional.of(existingUser)).`when`(userService).getUser(2)
        doReturn(listOf(role)).`when`(roleService).findRolesByName(listOf("role 1"))

        val result = userController.updateUser(updateUser)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        verify(userService, Times(0)).update(any())
    }

    @Test
    fun `update user invalid role`() {
        val updateUser = UserDTO(2, "Emil", "12345678", "emil@mail.com", setOf("role 1"), false, true, false, true)
        val existingUser = User(2, "Emil", "12345678", "emil@mail.com", emptySet(), mutableSetOf(), false, false, false, false)

        doReturn(Optional.of(existingUser)).`when`(userService).getUser(2)
        doReturn(emptyList<Role>()).`when`(roleService).findRolesByName(listOf("role 1"))

        val result = userController.updateUser(updateUser)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        verify(userService, Times(0)).update(any())
    }

    @Test
    fun `update user can not update in database`() {
        val updateUser = UserDTO(2, "Emil", "12345678", "emil@mail.com", emptySet(), false, true, false, true)
        val existingUser = User(2, "Emil", "12345678", "emil@mail.com", emptySet(), mutableSetOf(), false, false, false, false)
        val userOptional: Optional<User> = Optional.empty()

        doReturn(Optional.of(existingUser)).`when`(userService).getUser(2)
        doReturn(emptyList<Role>()).`when`(roleService).findRolesByName(listOf("role 1"))
        doReturn(userOptional).`when`(userService).update(any())

        val result = userController.updateUser(updateUser)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        verify(userService).update(any())
    }

    @Test
    fun `update users email success`() {
        val existingUser = User(2, "Emil", "12345678", "emil@mail.com", emptySet(), mutableSetOf(), true, true, true, true)
        val updatedUser = existingUser.copy(email = "new@mail.com")

        doReturn(Optional.of(existingUser)).`when`(userService).getUser(2)
        doReturn(emptyList<Role>()).`when`(roleService).findRolesByName(listOf("role 1"))
        val userCaptor = argumentCaptor<User>()
        doReturn(Optional.of(updatedUser)).`when`(userService).updateWithoutPassword(userCaptor.capture())

        val result = userController.updateUsersEmail(2, "new@mail.com")

        assertEquals(HttpStatus.OK, result.statusCode)
        compareUsers(updatedUser, result.body!!)
        compareUsers(existingUser.copy(email = "new@mail.com"), userCaptor.firstValue)
    }

    @Test
    fun `update users email not existing user`() {
        val userOptional: Optional<User> = Optional.empty()

        doReturn(userOptional).`when`(userService).getUser(2)

        val result = userController.updateUsersEmail(2, "new@mail.com")

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        verify(userService, Times(0)).updateWithoutPassword(any())
    }

    @Test
    fun `update users can not update in database`() {
        val existingUser = User(2, "Emil", "12345678", "emil@mail.com", emptySet(), mutableSetOf(), true, true, true, true)
        val userOptional: Optional<User> = Optional.empty()

        doReturn(Optional.of(existingUser)).`when`(userService).getUser(2)
        doReturn(emptyList<Role>()).`when`(roleService).findRolesByName(listOf("role 1"))
        doReturn(userOptional).`when`(userService).updateWithoutPassword(any())

        val result = userController.updateUsersEmail(2, "new@mail.com")

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        verify(userService).updateWithoutPassword(any())
    }

    @Test
    fun `update users password success`() {
        val existingUser = User(2, "Emil", "12345678", "emil@mail.com", emptySet(), mutableSetOf(), true, true, true, true)
        val updatedUser = existingUser.copy(password = "87654321")

        doReturn(Optional.of(existingUser)).`when`(userService).getUser(2)
        doReturn(true).`when`(passwordEncoder).matches("12345678", "12345678")

        val userCaptor = argumentCaptor<User>()
        doReturn(Optional.of(updatedUser)).`when`(userService).update(userCaptor.capture())

        val result = userController.updateUsersPassword(2, "12345678", "87654321")

        assertEquals(HttpStatus.OK, result.statusCode)
        compareUsers(updatedUser, result.body!!)
        compareUsers(existingUser.copy(password = "87654321"), userCaptor.firstValue)
    }

    @Test
    fun `update users password not existing user`() {
        val userOptional: Optional<User> = Optional.empty()

        doReturn(userOptional).`when`(userService).getUser(2)

        val result = userController.updateUsersPassword(2, "123456789", "87654321")

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        verify(userService, Times(0)).update(any())
    }

    @Test
    fun `update users password not match passwords`() {
        val existingUser = User(2, "Emil", "12345678", "emil@mail.com", emptySet(), mutableSetOf(), true, true, true, true)

        doReturn(Optional.of(existingUser)).`when`(userService).getUser(2)
        doReturn(false).`when`(passwordEncoder).matches(any(), any())

        val result = userController.updateUsersPassword(2, "abcdefgh", "87654321")

        assertEquals(HttpStatus.UNAUTHORIZED, result.statusCode)
        verify(userService, Times(0)).update(any())
    }

    @Test
    fun `update users password short password`() {
        val existingUser = User(2, "Emil", "12345678", "emil@mail.com", emptySet(), mutableSetOf(), true, true, true, true)

        doReturn(Optional.of(existingUser)).`when`(userService).getUser(2)
        doReturn(true).`when`(passwordEncoder).matches(any(), any())

        val result = userController.updateUsersPassword(2, "12345678", "abcd")

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        verify(userService, Times(0)).update(any())
    }

    @Test
    fun `update users password can not update in database`() {
        val existingUser = User(2, "Emil", "12345678", "emil@mail.com", emptySet(), mutableSetOf(), true, true, true, true)
        val userOptional: Optional<User> = Optional.empty()

        doReturn(Optional.of(existingUser)).`when`(userService).getUser(2)
        doReturn(true).`when`(passwordEncoder).matches("12345678", "12345678")
        doReturn(userOptional).`when`(userService).update(any())

        val result = userController.updateUsersPassword(2, "12345678", "87654321")

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        verify(userService).update(any())
    }

    @Test
    fun `delete user success`() {
        userController.deleteUser(32)
        verify(userService).deleteById(32)
    }

    private fun compareUsers(expected: User, actual: UserDTO) {
        assertEquals(expected.id, actual.id)
        assertEquals(expected.username, actual.username)
        assertEquals(expected.password, actual.password)
        assertEquals(expected.email, actual.email)
        assertEquals(expected.isAccountNonExpired, actual.nonExpired)
        assertEquals(expected.isAccountNonLocked, actual.nonLocked)
        assertEquals(expected.isEnabled, actual.enabled)
        assertEquals(expected.isCredentialsNonExpired, actual.credentialsNonExpired)
        assertEquals(expected.roles.size, actual.roles.size)
    }

    private fun compareUsers(expected: UserDTO, actual: User) {
        assertEquals(expected.id, actual.id)
        assertEquals(expected.username, actual.username)
        assertEquals(expected.password, actual.password)
        assertEquals(expected.email, actual.email)
        assertEquals(expected.nonExpired, actual.isAccountNonExpired)
        assertEquals(expected.nonLocked, actual.isAccountNonLocked)
        assertEquals(expected.enabled, actual.isEnabled)
        assertEquals(expected.credentialsNonExpired, actual.isCredentialsNonExpired)
        assertEquals(expected.roles.size, actual.roles.size)
    }

    private fun compareUsers(expected: User, actual: User) {
        assertEquals(expected.id, actual.id)
        assertEquals(expected.username, actual.username)
        assertEquals(expected.password, actual.password)
        assertEquals(expected.email, actual.email)
        assertEquals(expected.isAccountNonExpired, actual.isAccountNonExpired)
        assertEquals(expected.isAccountNonLocked, actual.isAccountNonLocked)
        assertEquals(expected.isEnabled, actual.isEnabled)
        assertEquals(expected.isCredentialsNonExpired, actual.isCredentialsNonExpired)
        assertEquals(expected.roles.size, actual.roles.size)
    }
}