package com.carsecurity.authorization.service

import com.carsecurity.authorization.domain.Role
import com.carsecurity.authorization.repository.RoleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class RoleServiceImpl(
        private val repo: RoleRepository
) : RoleService {

    @Transactional
    override fun tryCreate(role: Role): Optional<Role> {
        if (repo.findByName(role.name).isPresent)
            return Optional.empty()
        return Optional.of(repo.save(role))
    }

    @Transactional
    override fun findByName(name: String) = repo.findByName(name)

    @Transactional
    override fun findRolesByName(rolesStr: List<String>): List<Role> {

        val roles = repo.findAll()
        return roles.filter { role -> rolesStr.contains(role.name) }
    }
}