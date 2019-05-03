package com.carsecurity.authorization.service

import com.carsecurity.authorization.domain.Role
import java.util.*

/**
 * Service which specified operations which can be used to communicated with database.
 * Specially with role table.
 */
interface RoleService {

    /**
     * Method create [role] in database if do not already exists.
     *
     * @param role which will be stored in database.
     * @return created role from database.
     */
    fun tryCreate(role: Role): Optional<Role>

    /**
     * Method returns role which is identified by its [name].
     *
     * @param name of role.
     * @return found role from database.
     */
    fun findByName(name: String): Optional<Role>

    /**
     * Method returns all roles witch name is in [rolesStr] list.
     *
     * @param rolesStr list of names of searching roles.
     * @return list of found roles.
     */
    fun findRolesByName(rolesStr: List<String>): List<Role>

    /**
     * Method return role which is specified by its [id].
     *
     * @param id is identification of role in database.
     * @return founded role from database.
     */
    fun findById(id: Long): Optional<Role>

    /**
     * Method return all roles from database.
     *
     * @return all roles from database.
     */
    fun getRoles(): List<Role>

    /**
     * Method update all roles attributes from [role] in database.
     *
     * @param role which will be updated in database.
     * @return updated role from database.
     */
    fun update(role: Role): Optional<Role>

    /**
     * Method delete role from database which is identified by [id] parameter.
     *
     * @param id is identification of role which will be deleted.
     */
    fun deleteById(id: Long)
}