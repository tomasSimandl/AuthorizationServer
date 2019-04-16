package com.carsecurity.authorization.service


import com.carsecurity.authorization.domain.User
import com.carsecurity.authorization.repository.UserRepository
import com.carsecurity.authorization.service.impl.UserServiceImpl
import com.nhaarman.mockitokotlin2.argumentCaptor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.internal.verification.Times
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

class UserServiceImplTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    private lateinit var userService: UserServiceImpl

    private val hashPassword = "hash password"

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)

        userService = UserServiceImpl(userRepository, passwordEncoder)
        doReturn(hashPassword).`when`(passwordEncoder).encode(ArgumentMatchers.any())
    }

    @Test
    fun `try create success`() {

        val user = User(username = "Emilia", password = "1234", email = "em@il.ia")
        val userReturn = user.copy(id = 10)
        val userOptional: Optional<User> = Optional.empty()

        doReturn(userOptional).`when`(userRepository).findByUsername("Emilia")
        val userCaptor = argumentCaptor<User>()
        doReturn(userReturn).`when`(userRepository).save(userCaptor.capture())

        val result = userService.tryCreate(user)

        assertEquals(userReturn, result.get())
        assertEquals(user.username, userCaptor.firstValue.username)
        assertEquals(user.email, userCaptor.firstValue.email)
        assertEquals(hashPassword, userCaptor.firstValue.password)
    }

    @Test
    fun `try create already exists`() {

        val user = User(username = "Emilia", password = "1234", email = "em@il.ia")
        doReturn(Optional.of(user)).`when`(userRepository).findByUsername("Emilia")

        val result = userService.tryCreate(user)

        verify(userRepository, Times(0)).save(ArgumentMatchers.any())
        verify(passwordEncoder, Times(0)).encode(ArgumentMatchers.any())

        assertFalse(result.isPresent)
    }

    @Test
    fun `update success`() {

        val userOld = User(id = 12, username = "Emilia", password = "1234", email = "em@il.ia")
        val userNew = User(id = 12, username = "Emilia", password = "1234", email = "emilia@mail.com")
        val userSaved = userNew.copy(password = "1234HASH")

        doReturn(Optional.of(userOld)).`when`(userRepository).findByUsername("Emilia")

        val userCaptor = argumentCaptor<User>()
        doReturn(userSaved).`when`(userRepository).save(userCaptor.capture())

        val result = userService.update(userNew)

        assertEquals(userSaved, result.get())
        assertEquals(userNew.username, userCaptor.firstValue.username)
        assertEquals(userNew.email, userCaptor.firstValue.email)
        assertEquals(hashPassword, userCaptor.firstValue.password)
    }

    @Test
    fun `update not existing user`() {

        val user = User(id = 12, username = "Emilia", password = "1234", email = "em@il.ia")
        val userOptional: Optional<User> = Optional.empty()
        doReturn(userOptional).`when`(userRepository).findByUsername("Emilia")

        val result = userService.update(user)

        verify(userRepository, Times(0)).save(ArgumentMatchers.any())
        assertFalse(result.isPresent)
    }

    @Test
    fun `update user exists but with different id`() {

        val userStored = User(id = 12, username = "Emilia", password = "1234", email = "em@il.ia")
        val userNew = User(id = 24, username = "Emilia", password = "1234", email = "emilia@mail.com")

        doReturn(Optional.of(userStored)).`when`(userRepository).findByUsername("Emilia")

        val result = userService.update(userNew)

        verify(userRepository, Times(0)).save(ArgumentMatchers.any())
        assertFalse(result.isPresent)
    }

    @Test
    fun `update without password success`() {

        val userOld = User(id = 12, username = "Emilia", password = "1234", email = "em@il.ia")
        val userNew = User(id = 12, username = "Emilia", password = "5678", email = "emilia@mail.com")

        doReturn(Optional.of(userOld)).`when`(userRepository).findByUsername("Emilia")

        val userCaptor = argumentCaptor<User>()
        doReturn(userNew).`when`(userRepository).save(userCaptor.capture())

        val result = userService.updateWithoutPassword(userNew)

        assertEquals(userNew, result.get())
        assertEquals(userOld.password, userCaptor.firstValue.password)
        assertEquals(userNew.username, userCaptor.firstValue.username)
        assertEquals(userNew.email, userCaptor.firstValue.email)
    }

    @Test
    fun `update without password not existing user`() {

        val user = User(id = 12, username = "Emilia", password = "1234", email = "em@il.ia")
        val userOptional: Optional<User> = Optional.empty()
        doReturn(userOptional).`when`(userRepository).findByUsername("Emilia")

        val result = userService.updateWithoutPassword(user)

        verify(userRepository, Times(0)).save(ArgumentMatchers.any())
        assertFalse(result.isPresent)
    }

    @Test
    fun `update without password user exists but with different id`() {

        val userStored = User(id = 12, username = "Emilia", password = "1234", email = "em@il.ia")
        val userNew = User(id = 24, username = "Emilia", password = "1234", email = "emilia@mail.com")

        doReturn(Optional.of(userStored)).`when`(userRepository).findByUsername("Emilia")

        val result = userService.updateWithoutPassword(userNew)

        verify(userRepository, Times(0)).save(ArgumentMatchers.any())
        assertFalse(result.isPresent)
    }
}