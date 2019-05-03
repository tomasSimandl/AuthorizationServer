package com.carsecurity.authorization.service.impl

import com.carsecurity.authorization.domain.Role
import com.carsecurity.authorization.repository.RoleRepository
import com.carsecurity.authorization.service.RoleService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Implementation of service which specified operations which can be used to communicated with database.
 * Specially with role table.
 *
 * @param repo is role repository used for access role table in database.
 */
@Service
class RoleServiceImpl(
        private val repo: RoleRepository
) : RoleService {

    /**
     * Method create [role] in database if do not already exists.
     *
     * @param role which will be stored in database.
     * @return created role from database.
     */
    @Transactional
    override fun tryCreate(role: Role): Optional<Role> {
        if (repo.findByName(role.name).isPresent)
            return Optional.empty()
        return Optional.of(repo.save(role))
    }

    /**
     * Method returns role which is identified by its [name].
     *
     * @param name of role.
     * @return found role from database.
     */
    @Transactional
    override fun findByName(name: String) = repo.findByName(name)

    /**
     * Method returns all roles witch name is in [rolesStr] list.
     *
     * @param rolesStr list of names of searching roles.
     * @return list of found roles.
     */
    @Transactional
    override fun findRolesByName(rolesStr: List<String>): List<Role> {

        val roles = repo.findAll()
        return roles.filter { role -> rolesStr.contains(role.name) }
    }

    /**
     * Method return role which is specified by its [id].
     *
     * @param id is identification of role in database.
     * @return founded role from database.
     */
    @Transactional
    override fun findById(id: Long): Optional<Role> = repo.findById(id)

    /**
     * Method return all roles from database.
     *
     * @return all roles from database.
     */
    @Transactional
    override fun getRoles(): List<Role> = repo.findAll()

    /**
     * Method update all roles attributes from [role] in database.
     *
     * @param role which will be updated in database.
     * @return updated role from database.
     */
    @Transactional
    override fun update(role: Role): Optional<Role> = Optional.of(repo.save(role))

    /**
     * Method delete role from database which is identified by [id] parameter.
     *
     * @param id is identification of role which will be deleted.
     */
    @Transactional
    override fun deleteById(id: Long) = repo.deleteById(id)
}