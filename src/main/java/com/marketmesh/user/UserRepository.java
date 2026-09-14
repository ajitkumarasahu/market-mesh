//package com.marketmesh.user;
package com.marketmesh.user;

/**
 * UserRepository interface for performing CRUD operations on User entities.
 * It extends JpaRepository to provide standard database operations.
 * Additional methods are defined to find users by email and check for email existence.
 */
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * UserRepository interface for performing CRUD operations on User entities.
 * if email is already exists in the database then it will return true otherwise false.
 * if email is already exists in the database then it will return user object otherwise empty.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}