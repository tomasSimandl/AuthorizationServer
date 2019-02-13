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
        private val encoder: PasswordEncoder
) : UserService {
    @Transactional
    override fun tryCreate(user: User): Optional<User> {
        if (repo.findByUsername(user.username).isPresent)
            return Optional.empty()
        return Optional.of(repo.save(user.copy(password = encoder.encode(user.password))))
    }

    @Transactional
    override fun findByUsername(username: String) = repo.findByUsername(username)
}