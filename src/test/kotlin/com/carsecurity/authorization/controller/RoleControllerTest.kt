package com.carsecurity.authorization.controller


import com.carsecurity.authorization.domain.Role
import com.carsecurity.authorization.domain.User
import com.carsecurity.authorization.domain.dto.RoleDTO
import com.carsecurity.authorization.service.RoleService
import com.carsecurity.authorization.service.UserService
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.internal.verification.Times
import org.springframework.http.HttpStatus
import java.util.*

class RoleControllerTest {

    @Mock
    private lateinit var userService: UserService

    @Mock
    private lateinit var roleService: RoleService

    private lateinit var roleController: RoleController

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)

        roleController = RoleController(userService, roleService)
    }

    @Test
    fun `create role success`() {
        val user1 = User(username = "user 1")
        val user2 = User(username = "user 2")
        val user3 = User(username = "user 3")
        val createRole = RoleDTO(id = 21, name = "new role", users = setOf("user 1", "user 2", "user 3"))
        val createdRole = Role(id = 1, name = "new role", users = setOf(user1, user2, user3))

        doReturn(listOf(user1, user2, user3)).`when`(userService).findAllByUsername(listOf("user 1", "user 2", "user 3"))

        val roleCaptor = argumentCaptor<Role>()
        doReturn(Optional.of(createdRole)).`when`(roleService).tryCreate(roleCaptor.capture())

        val result = roleController.createRole(createRole)

        assertEquals(HttpStatus.CREATED, result.statusCode)
        assertEquals(createdRole.name, result.body?.name)
        assertEquals(createdRole.id, result.body?.id)
        assertArrayEquals(createdRole.users.map { it.username }.sorted().toTypedArray(), result.body?.users?.sorted()?.toTypedArray())

        assertEquals(createRole.name, roleCaptor.firstValue.name)
        assertEquals(0, roleCaptor.firstValue.id)
        assertArrayEquals(createRole.users.sorted().toTypedArray(), roleCaptor.firstValue.users.map { it.username }.sorted().toTypedArray())
    }

    @Test
    fun `create role success without user`() {

        val createRole = RoleDTO(name = "new role", users = setOf())
        val createdRole = Role(id = 1, name = "new role", users = setOf())

        doReturn(listOf<Role>()).`when`(userService).findAllByUsername(listOf())

        val roleCaptor = argumentCaptor<Role>()
        doReturn(Optional.of(createdRole)).`when`(roleService).tryCreate(roleCaptor.capture())

        val result = roleController.createRole(createRole)

        assertEquals(HttpStatus.CREATED, result.statusCode)
        assertEquals(createdRole.name, result.body?.name)
        assertEquals(createdRole.id, result.body?.id)
        assertTrue(result.body?.users?.isEmpty() ?: false)
    }

    @Test
    fun `create role invalid user`() {
        val user1 = User(username = "user 1")
        val user2 = User(username = "user 2")
        val createRole = RoleDTO(name = "new role", users = setOf("user 1", "user 2", "user 3"))

        doReturn(listOf(user1, user2)).`when`(userService).findAllByUsername(listOf("user 1", "user 2", "user 3"))

        val result = roleController.createRole(createRole)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    @Test
    fun `create role can not create role in db`() {
        val user1 = User(username = "user 1")
        val user2 = User(username = "user 2")
        val createRole = RoleDTO(name = "new role", users = setOf("user 1", "user 2"))

        doReturn(listOf(user1, user2)).`when`(userService).findAllByUsername(listOf("user 1", "user 2"))

        val roleOptional: Optional<Role> = Optional.empty()
        doReturn(roleOptional).`when`(roleService).tryCreate(any())

        val result = roleController.createRole(createRole)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    @Test
    fun `update role users success`() {
        val user1 = User(username = "user 1")
        val user2 = User(username = "user 2")
        val user3 = User(username = "user 3")
        val updateRole = RoleDTO(id = 1, name = "new role", users = setOf("user 1", "user 2", "user 3"))
        val existingRole = Role(id = 1, name = "new role", users = setOf(user1))
        val updatedRole = Role(id = 1, name = "new role", users = setOf(user1, user2, user3))

        doReturn(Optional.of(existingRole)).`when`(roleService).findById(existingRole.id)
        doReturn(listOf(user1, user2, user3)).`when`(userService).findAllByUsername(listOf("user 1", "user 2", "user 3"))

        val roleCaptor = argumentCaptor<Role>()
        doReturn(Optional.of(updatedRole)).`when`(roleService).update(roleCaptor.capture())

        val result = roleController.updateRole(updateRole)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(updatedRole.name, result.body?.name)
        assertEquals(updatedRole.id, result.body?.id)
        assertArrayEquals(updatedRole.users.map { it.username }.sorted().toTypedArray(), result.body?.users?.sorted()?.toTypedArray())

        assertEquals(updateRole.name, roleCaptor.firstValue.name)
        assertEquals(updateRole.id, roleCaptor.firstValue.id)
        assertArrayEquals(updateRole.users.sorted().toTypedArray(), roleCaptor.firstValue.users.map { it.username }.sorted().toTypedArray())
    }

    @Test
    fun `update role name success`() {
        val user1 = User(username = "user 1")
        val updateRole = RoleDTO(id = 1, name = "new role name", users = setOf("user 1"))
        val existingRole = Role(id = 1, name = "new role", users = setOf(user1))
        val updatedRole = Role(id = 1, name = "new role name", users = setOf(user1))
        val roleOptional: Optional<Role> = Optional.empty()

        doReturn(Optional.of(existingRole)).`when`(roleService).findById(existingRole.id)
        doReturn(listOf(user1)).`when`(userService).findAllByUsername(listOf("user 1"))
        doReturn(roleOptional).`when`(roleService).findByName(updateRole.name)

        val roleCaptor = argumentCaptor<Role>()
        doReturn(Optional.of(updatedRole)).`when`(roleService).update(roleCaptor.capture())

        val result = roleController.updateRole(updateRole)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(updatedRole.name, result.body?.name)
        assertEquals(updatedRole.id, result.body?.id)
        assertArrayEquals(updatedRole.users.map { it.username }.sorted().toTypedArray(), result.body?.users?.sorted()?.toTypedArray())

        assertEquals(updateRole.name, roleCaptor.firstValue.name)
        assertEquals(updateRole.id, roleCaptor.firstValue.id)
        assertArrayEquals(updateRole.users.sorted().toTypedArray(), roleCaptor.firstValue.users.map { it.username }.sorted().toTypedArray())
    }

    @Test
    fun `update role not existing role`() {
        val updateRole = RoleDTO(id = 1, name = "super role name", users = setOf("user 1"))
        val roleOptional: Optional<Role> = Optional.empty()

        doReturn(roleOptional).`when`(roleService).findById(updateRole.id)

        val result = roleController.updateRole(updateRole)

        verify(roleService, Times(0)).update(any())
        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    @Test
    fun `update role existing name`() {
        val user1 = User(username = "user 1")
        val updateRole = RoleDTO(id = 1, name = "super role name", users = setOf("user 1"))
        val existingRole1 = Role(id = 1, name = "role name", users = setOf(user1))
        val existingRole2 = Role(id = 2, name = "super role name", users = setOf(user1))

        doReturn(Optional.of(existingRole1)).`when`(roleService).findById(existingRole1.id)
        doReturn(Optional.of(existingRole2)).`when`(roleService).findByName(existingRole2.name)

        val result = roleController.updateRole(updateRole)

        verify(roleService, Times(0)).update(any())
        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    @Test
    fun `update role invalid user`() {
        val user1 = User(username = "user 1")
        val user2 = User(username = "user 2")
        val updateRole = RoleDTO(id = 1, name = "new role", users = setOf("user 1", "user 2", "user 3"))
        val existingRole = Role(id = 1, name = "new role", users = setOf(user1))

        doReturn(Optional.of(existingRole)).`when`(roleService).findById(existingRole.id)
        doReturn(listOf(user1, user2)).`when`(userService).findAllByUsername(listOf("user 1", "user 2", "user 3"))

        val result = roleController.updateRole(updateRole)

        verify(roleService, Times(0)).update(any())
        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    @Test
    fun `update role can not create role in database`() {
        val user1 = User(username = "user 1")
        val user2 = User(username = "user 2")
        val updateRole = RoleDTO(id = 1, name = "new role", users = setOf("user 1", "user 2"))
        val existingRole = Role(id = 1, name = "new role", users = setOf(user1))
        val roleOptional: Optional<Role> = Optional.empty()

        doReturn(Optional.of(existingRole)).`when`(roleService).findById(existingRole.id)
        doReturn(listOf(user1, user2)).`when`(userService).findAllByUsername(listOf("user 1", "user 2"))
        doReturn(roleOptional).`when`(roleService).update(any())

        val result = roleController.updateRole(updateRole)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    @Test
    fun `delete role success`() {
        roleController.deleteRole(1)
        verify(roleService).deleteById(1)
    }
}