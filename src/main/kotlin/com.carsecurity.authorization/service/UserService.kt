package com.carsecurity.authorization.service

import com.carsecurity.authorization.domain.User
import java.util.*

interface UserService {
    // INSERT
    fun tryCreate(user: User): Optional<User>

    // SELECT
    fun findByUsername(username: String): Optional<User>
    fun getUsers(): List<User>
    fun getUser(id: Long): Optional<User>

    // UPDATE
    fun update(user: User): Optional<User>

    // DELETE
    fun delete(user: User)
    fun deleteById(id: Long)
}