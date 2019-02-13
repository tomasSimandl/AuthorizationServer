package com.carsecurity.authorization.repository

import com.carsecurity.authorization.domain.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
@Transactional(propagation = Propagation.MANDATORY)
interface RoleRepository: JpaRepository<Role, Long> {
    fun findByName(name: String): Optional<Role>
}