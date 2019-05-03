package com.carsecurity.authorization.domain

import java.io.Serializable
import javax.persistence.*

/**
 * Entity of role which is mapped to role in database.
 */
@Entity
data class Role(

        /** Identification number of role. */
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,

        /** Name of role. */
        @Column(nullable = false, unique = true)
        val name: String = "",

        /** List of users with this role. */
        @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
        @JoinTable(
                name = "user_role",
                joinColumns = [JoinColumn(name = "role_id")],
                inverseJoinColumns = [JoinColumn(name = "user_id")]
        )
        val users: Set<User> = HashSet()

) : Serializable {

    /**
     * Return hash code of this role. Hash code is based on roles name.
     *
     * @return created hash code.
     */
    override fun hashCode(): Int {
        return name.hashCode()
    }

    /**
     * Method returns if input role is equals with this role.
     * Roles are equals when are of the same class and have the same name.
     *
     * @return true when roles are equal, false otherwise.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other === null || other !is Role) {
            return false
        }
        if (this::class != other::class) {
            return false
        }
        return name == other.name
    }
}