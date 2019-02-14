package com.carsecurity.authorization.service

import com.carsecurity.authorization.domain.Role
import java.util.*

interface RoleService {
    fun tryCreate(role: Role): Optional<Role>
    fun findByName(name: String): Optional<Role>
    fun findRolesByName(rolesStr: List<String>): List<Role>
    fun findById(id: Long): Optional<Role>
    fun getRoles(): List<Role>

    fun update(role: Role): Optional<Role>

    fun deleteById(id: Long)
}