package com.carsecurity.authorization.domain

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.io.Serializable
import javax.persistence.*

/**
 * Database entity of user which is mapped to table in database.
 */
@Entity
@Table(name = "\"user\"")
data class User(

        /** Identification number of user. */
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,

        /** Unique username. */
        @Column(nullable = false, unique = true)
        private var username: String = "",

        /** Users password. */
        @Column(nullable = false)
        private var password: String = "",

        /** Users e-mail. */
        @Column(nullable = false)
        var email: String = "",

        /** List of users roles. */
        @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
        @JoinTable(
                name = "user_role",
                joinColumns = [JoinColumn(name = "user_id")],
                inverseJoinColumns = [JoinColumn(name = "role_id")]
        )
        val roles: Set<Role> = HashSet(),

        /** List of users authorities. */
        @Transient
        private var authorities: MutableCollection<out GrantedAuthority> = HashSet(),

        /** Identification if user is expired. */
        @Column(name = "non_expired", nullable = false)
        private val nonExpired: Boolean = true,

        /** Identification if user is locked. */
        @Column(name = "non_locked", nullable = false)
        private val nonLocked: Boolean = true,

        /** Identification if user is enabled. */
        @Column(nullable = false)
        private val enabled: Boolean = true,

        /** Identification if user has expired credentials. */
        @Column(name = "credentials_non_expired", nullable = false)
        private val credentialsNonExpired: Boolean = true

) : UserDetails, Serializable {

    /**
     * Method return username of user.
     * @return username
     */
    override fun getUsername() = username

    /**
     * Method return password of user.
     * @return password
     */
    override fun getPassword() = password

    /**
     * Return identification if user is expired.
     * @return if is account expired.
     */
    override fun isAccountNonExpired() = nonExpired

    /**
     * Return identification if user is locker.
     * @return if account is locked.
     */
    override fun isAccountNonLocked() = nonLocked

    /**
     * Return identification is user is enabled.
     * @return if user is enabled.
     */
    override fun isEnabled() = enabled

    /**
     * Return identification if are expired credentials.
     * @return if credentials are expired.
     */
    override fun isCredentialsNonExpired() = credentialsNonExpired

    /**
     * Return users authorities.
     * @return list of users authorities.
     */
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = authorities

    /**
     * Return hash code of user which is based on users id.
     * @return users hash code.
     */
    override fun hashCode(): Int {
        if (id == 0L) {
            return super.hashCode()
        }
        return id.hashCode()
    }

    /**
     * Return if input user is equals with this user.
     * Equals users are of the same class and have the same id.
     *
     * @param other user which will be compared.
     * @return true when are equals, false otherwise.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other === null || other !is User) {
            return false
        }
        if (this::class != other::class) {
            return false
        }
        return id == other.id
    }
}