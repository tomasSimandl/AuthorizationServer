package com.carsecurity.authorization.service.impl

import com.carsecurity.authorization.domain.User
import com.carsecurity.authorization.repository.UserRepository
import com.carsecurity.authorization.service.UserService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Implementation of service which specified operations which can be used to communicated with database.
 * Specially with user table.
 *
 * @param repo is user repository used for access user table in database.
 * @param passwordEncoder is encoder used for hashing passwords.
 */
@Service
class UserServiceImpl(
        private val repo: UserRepository,
        private val passwordEncoder: PasswordEncoder
) : UserService {

    /**
     * Method create [user] in database if do not already exists.
     *
     * @param user which will be stored in database.
     * @return created user from database.
     */
    @Transactional
    override fun tryCreate(user: User): Optional<User> {
        return if (repo.findByUsername(user.username).isPresent) {
            Optional.empty()
        } else {
            Optional.of(repo.save(user.copy(password = passwordEncoder.encode(user.password))))
        }
    }

    /**
     * Method returns user which is identified by his [username].
     *
     * @param username of user.
     * @return found user from database.
     */
    @Transactional
    override fun findByUsername(username: String) = repo.findByUsername(username)

    /**
     * Method returns all users witch username are in [username] list.
     *
     * @param username list of username of searching users.
     * @return list of found users.
     */
    @Transactional
    override fun findAllByUsername(username: List<String>): List<User> = repo.findAllByUsernameIsIn(username)

    /**
     * Method return all users from database.
     *
     * @return all users from database.
     */
    @Transactional
    override fun getUsers(): List<User> = repo.findAll()

    /**
     * Method return user which is specified by his [id].
     *
     * @param id is identification of user.
     * @return founded user from database.
     */
    @Transactional
    override fun getUser(id: Long): Optional<User> = repo.findById(id)

    /**
     * Method update all users attributes from [user] in database.
     *
     * @param user which will be updated in database.
     * @return updated user from database.
     */
    @Transactional
    override fun update(user: User): Optional<User> {
        val dbUserOptional = repo.findByUsername(user.username)

        if (dbUserOptional.isPresent && dbUserOptional.get().id != user.id) {
            return Optional.empty()
        }

        return Optional.of(repo.save(user.copy(password = passwordEncoder.encode(user.password))))
    }

    /**
     * Method update users attributes without password which will be unchanged.
     *
     * @param user which will be updated in database.
     * @return updated user from database.
     */
    @Transactional
    override fun updateWithoutPassword(user: User): Optional<User> {
        val dbUserOptional = repo.findByUsername(user.username)

        if (dbUserOptional.isPresent && dbUserOptional.get().id != user.id) {
            return Optional.empty()
        }

        return Optional.of(repo.save(user))
    }

    /**
     * Method delete input [user] from database.
     *
     * @param user which will be deleted from database.
     */
    @Transactional
    override fun delete(user: User) = repo.delete(user)

    /**
     * Method delete user from database which is identified by [id] parameter.
     *
     * @param id is identification of user.
     */
    @Transactional
    override fun deleteById(id: Long) = repo.deleteById(id)
}