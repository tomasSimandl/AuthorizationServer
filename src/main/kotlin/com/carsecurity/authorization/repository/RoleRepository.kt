package com.carsecurity.authorization.repository

import com.carsecurity.authorization.domain.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Repository used for access roles in database.
 */
@Repository
interface RoleRepository : JpaRepository<Role, Long> {
    /**
     * Method find role by its name.
     *
     * @param name of requested role.
     * @return [Optional] with found role or empty [Optional]
     */
    fun findByName(name: String): Optional<Role>
}