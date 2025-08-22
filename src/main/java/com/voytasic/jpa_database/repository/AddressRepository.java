package com.voytasic.jpa_database.repository;

import com.voytasic.jpa_database.repository.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    @Transactional
    @Query(name = "DELETE FROM address a WHERE a.user_id=?1")
    void deleteByUserId(Long id);


}
