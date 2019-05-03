package com.carsecurity.authorization.domain.dto

import com.carsecurity.authorization.domain.Role

/**
 * Data class used for transfer data about roles over https requests.
 */
data class RoleDTO(
        /** Identification number of role. */
        var id: Long = 0,
        /** Name of role. */
        val name: String = "",
        /** List of users username which have this role */
        val users: Set<String> = HashSet()

) {
    /**
     * Constructor which initialize this class with [Role] object.
     *
     * @param role of which will be initialized this object.
     */
    constructor(role: Role) : this(
            role.id,
            role.name,
            role.users.map { user -> user.username }.toHashSet()
    )
}