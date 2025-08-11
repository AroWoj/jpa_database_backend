package com.voytasic.jpa_database.repository;

import com.voytasic.jpa_database.repository.entity.ActivateToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ActivateTokenRepository extends JpaRepository<ActivateToken, Long> {

    Optional<ActivateToken> findByToken(String token);

    @Transactional
    @Query(name = "DELETE FROM Token t WHERE t.user_id=?1")
    void deleteByUserId(Long id);

//    @Query(value = """
//      select t from Token t inner join User u\s
//      on t.user.id = u.id\s
//      where u.id = :id and (t.expired = false or t.revoked = false)\s
//      """)
//    List<Token> findAllValidTokenByUser(Long id);
}
