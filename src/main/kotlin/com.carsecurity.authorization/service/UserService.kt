package com.carsecurity.authorization.service

import com.carsecurity.authorization.domain.User
import java.util.*

/**
 * Service which specified operations which can be used to communicated with database. Specially with user table.
 */
interface UserService {
    // INSERT
    /**
     * Method create [user] in database if do not already exists.
     *
     * @param user which will be stored in database.
     * @return created user from database.
     */
    fun tryCreate(user: User): Optional<User>

    // SELECT
    /**
     * Method returns user which is identified by his [username].
     *
     * @param username of user.
     * @return found user from database.
     */
    fun findByUsername(username: String): Optional<User>

    /**
     * Method returns all users witch username are in [username] list.
     *
     * @param username list of username of searching users.
     * @return list of found users.
     */
    fun findAllByUsername(username: List<String>): List<User>

    /**
     * Method return all users from database.
     *
     * @return all users from database.
     */
    fun getUsers(): List<User>

    /**
     * Method return user which is specified by his [id].
     *
     * @param id is identification of user.
     * @return founded user from database.
     */
    fun getUser(id: Long): Optional<User>

    // UPDATE
    /**
     * Method update all users attributes from [user] in database.
     *
     * @param user which will be updated in database.
     * @return updated user from database.
     */
    fun update(user: User): Optional<User>

    /**
     * Method update users attributes without password which will be unchanged.
     *
     * @param user which will be updated in database.
     * @return updated user from database.
     */
    fun updateWithoutPassword(user: User): Optional<User>

    // DELETE
    /**
     * Method delete input [user] from database.
     *
     * @param user which will be deleted from database.
     */
    fun delete(user: User)

    /**
     * Method delete user from database which is identified by [id] parameter.
     *
     * @param id is identification of user.
     */
    fun deleteById(id: Long)
}