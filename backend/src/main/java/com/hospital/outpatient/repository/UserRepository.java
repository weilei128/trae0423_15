package com.hospital.outpatient.repository;

import com.hospital.outpatient.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByIdCard(String idCard);
    Optional<User> findByPhone(String phone);
    boolean existsByUsername(String username);
    boolean existsByIdCard(String idCard);
}
