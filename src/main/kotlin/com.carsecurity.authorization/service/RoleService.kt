package com.carsecurity.authorization.service

import com.carsecurity.authorization.domain.Role
import java.util.*

interface RoleService {
    fun tryCreate(role: Role): Optional<Role>
    fun findByName(name: String): Optional<Role>
}