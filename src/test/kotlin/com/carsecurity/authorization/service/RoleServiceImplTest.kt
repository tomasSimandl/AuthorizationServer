package com.carsecurity.authorization.service


import com.carsecurity.authorization.domain.Role
import com.carsecurity.authorization.repository.RoleRepository
import com.carsecurity.authorization.service.impl.RoleServiceImpl
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.MockitoAnnotations
import java.util.*

class RoleServiceImplTest {

    @Mock
    private lateinit var roleRepository: RoleRepository

    private lateinit var roleService: RoleServiceImpl

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)

        roleService = RoleServiceImpl(roleRepository)
    }

    @Test
    fun `try create success`() {

        val role = Role(name = "first role")
        val createdRole = Role(1, "first role")
        val roleOptional: Optional<Role> = Optional.empty()

        doReturn(roleOptional).`when`(roleRepository).findByName("first role")
        val roleCaptor = argumentCaptor<Role>()
        doReturn(createdRole).`when`(roleRepository).save(roleCaptor.capture())

        val result = roleService.tryCreate(role)

        assertEquals(role.name, roleCaptor.firstValue.name)
        assertTrue(roleCaptor.firstValue.users.isEmpty())
        assertEquals(createdRole, result.get())
    }

    @Test
    fun `try create already exists`() {

        val role = Role(1, "first role")
        doReturn(Optional.of(role)).`when`(roleRepository).findByName(any())

        val result = roleService.tryCreate(role)
        assertFalse(result.isPresent)
    }
}