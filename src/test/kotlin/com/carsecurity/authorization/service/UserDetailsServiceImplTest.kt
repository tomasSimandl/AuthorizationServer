package com.carsecurity.authorization.service


import com.carsecurity.authorization.domain.Role
import com.carsecurity.authorization.domain.User
import com.carsecurity.authorization.repository.UserRepository
import com.carsecurity.authorization.service.impl.UserDetailsServiceImpl
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.MockitoAnnotations
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.util.*

class UserDetailsServiceImplTest {

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var userDetailsService: UserDetailsServiceImpl

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)

        userDetailsService = UserDetailsServiceImpl(userRepository)
    }

    @Test
    fun `load user by username success`() {

        val role1 = Role(1, "first role")
        val role2 = Role(2, "second role")
        val user = User(username = "Emilia", password = "1234", email = "emilia@mail.com", roles = setOf(role1, role2),
                nonLocked = true, nonExpired = true, credentialsNonExpired = true, enabled = true)

        doReturn(Optional.of(user)).`when`(userRepository).findByUsername("Emilia")

        val result = userDetailsService.loadUserByUsername("Emilia")

        assertEquals(user.username, result.username)
        assertEquals(user.isAccountNonExpired, result.isAccountNonExpired)
        assertEquals(user.isAccountNonLocked, result.isAccountNonLocked)
        assertEquals(user.isCredentialsNonExpired, result.isCredentialsNonExpired)
        assertEquals(user.isEnabled, result.isEnabled)
        assertEquals(user.password, result.password)
        assertEquals(2, result.authorities.size)
        assertEquals(role1.name, result.authorities.first().authority)
        assertEquals(role2.name, result.authorities.last().authority)
    }

    @Test
    fun `load user by username no roles`() {

        val user = User(username = "Emilia", password = "1234", email = "emilia@mail.com", roles = emptySet(),
                nonLocked = true, nonExpired = true, credentialsNonExpired = true, enabled = true)

        doReturn(Optional.of(user)).`when`(userRepository).findByUsername("Emilia")

        val result = userDetailsService.loadUserByUsername("Emilia")

        assertEquals(user.username, result.username)
        assertEquals(user.isAccountNonExpired, result.isAccountNonExpired)
        assertEquals(user.isAccountNonLocked, result.isAccountNonLocked)
        assertEquals(user.isCredentialsNonExpired, result.isCredentialsNonExpired)
        assertEquals(user.isEnabled, result.isEnabled)
        assertEquals(user.password, result.password)
        assertTrue(result.authorities.isEmpty())
    }

    @Test
    fun `load user by username invalid username`() {

        val userOptional: Optional<User> = Optional.empty()
        doReturn(userOptional).`when`(userRepository).findByUsername("Emilia")

        try {
            userDetailsService.loadUserByUsername("Emilia")
            fail()
        } catch (e: UsernameNotFoundException) {
            return
        }
    }

    @Test
    fun `is owner by id success`() {
        val user = User(id = 11, username = "Alžběta")

        val result = userDetailsService.isOwner(user, 11)
        assertTrue(result)
    }

    @Test
    fun `is owner by id wrong id`() {
        val user = User(id = 11, username = "Alžběta")

        val result = userDetailsService.isOwner(user, 111)
        assertFalse(result)
    }

    @Test
    fun `is owner by id wrong instance`() {
        val user = org.springframework.security.core.userdetails.User("Alžběta", "1234", emptyList())

        val result = userDetailsService.isOwner(user, 11)
        assertFalse(result)
    }

    @Test
    fun `is owner by name success`() {
        val user = User(id = 11, username = "Alžběta")

        val result = userDetailsService.isOwner(user, "Alžběta")
        assertTrue(result)
    }

    @Test
    fun `is owner by name wrong name`() {
        val user = User(id = 11, username = "Alžběta")

        val result = userDetailsService.isOwner(user, "Victoria")
        assertFalse(result)
    }

    @Test
    fun `is owner by name wrong instance`() {
        val user = org.springframework.security.core.userdetails.User("Alžběta", "1234", emptyList())

        val result = userDetailsService.isOwner(user, "Alžběta")
        assertFalse(result)
    }
}