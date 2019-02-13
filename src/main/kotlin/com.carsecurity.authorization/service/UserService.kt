package com.carsecurity.authorization.service

import com.carsecurity.authorization.domain.User
import java.util.*

interface UserService {
    fun tryCreate(user: User): Optional<User>
    fun findByUsername(username: String): Optional<User>
}