package com.carsecurity.authorization.repository

import com.carsecurity.authorization.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Repository which is used for access users in database.
 */
@Repository
interface UserRepository : JpaRepository<User, Long> {

    /**
     * Method find user by its username.
     *
     * @param username is name of requested user.
     * @return [Optional] with requested user or empty [Optional].
     */
    fun findByUsername(username: String): Optional<User>

    /**
     * Method all users which username is in [username] list.
     *
     * @param username is list of usernames which identifies requested users in database.
     * @return list of found users.
     */
    fun findAllByUsernameIsIn(username: List<String>): List<User>
}