package com.carsecurity.authorization.domain

import java.io.Serializable
import javax.persistence.*

@Entity
data class Role(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,

        @Column(nullable = false, unique = true)
        val name: String = "",

        @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
        val users: Set<User> = HashSet()

) : Serializable {
    override fun hashCode(): Int {
        return name.hashCode()
    }

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