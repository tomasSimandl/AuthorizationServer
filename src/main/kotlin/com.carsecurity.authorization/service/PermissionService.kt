package com.carsecurity.authorization.service

import com.carsecurity.authorization.domain.Permission
import java.util.*

interface PermissionService {
    fun tryCreate(permission: Permission): Optional<Permission>
    fun findByName(name: String): Optional<Permission>
}