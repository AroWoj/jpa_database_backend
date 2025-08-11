package com.voytasic.jpa_database.repository;

import com.voytasic.jpa_database.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

//    @Override
//    @Query(value = "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.addresses")
//    List<User> findAll();

    Optional<User> findByEmail(String email);
}
