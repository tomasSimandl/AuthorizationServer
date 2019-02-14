package com.carsecurity.authorization.service

import com.carsecurity.authorization.domain.User
import com.carsecurity.authorization.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserServiceImpl(
        private val repo: UserRepository,
        private val passwordEncoder: PasswordEncoder
) : UserService {


    @Transactional
    override fun tryCreate(user: User): Optional<User> {
        return if (repo.findByUsername(user.username).isPresent) {
            Optional.empty()
        } else {
            Optional.of(repo.save(user.copy(password = passwordEncoder.encode(user.password))))
        }
    }


    @Transactional
    override fun findByUsername(username: String) = repo.findByUsername(username)

    @Transactional
    override fun findAllByUsername(username: List<String>): List<User> = repo.findAllByUsernameIsIn(username)

    @Transactional
    override fun getUsers(): List<User> = repo.findAll()

    @Transactional
    override fun getUser(id: Long): Optional<User> = repo.findById(id)


    @Transactional
    override fun update(user: User): Optional<User> {
        val dbUserOptional = repo.findByUsername(user.username)

        if (dbUserOptional.isPresent && dbUserOptional.get().id != user.id) {
            return Optional.empty()
        }

        return Optional.of(repo.save(user.copy(password = passwordEncoder.encode(user.password))))
    }


    @Transactional
    override fun delete(user: User) = repo.delete(user)

    @Transactional
    override fun deleteById(id: Long) = repo.deleteById(id)
}