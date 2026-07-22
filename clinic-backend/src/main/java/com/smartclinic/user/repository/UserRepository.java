package com.smartclinic.user.repository;

import com.smartclinic.user.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserName(String userName);

    boolean existsByUserName(String userName);

    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.userName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "(:keyword IS NULL OR u.phone LIKE CONCAT('%', :keyword, '%'))")
    Page<User> search(@Param("keyword") String keyword, Pageable pageable);
}
