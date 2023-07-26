package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.Client;
import com.springboot.mpaybackend.entity.ClientCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<ClientCard, Long> {

    List<ClientCard> findAllByDeletedFalse();

    List<ClientCard> findByClientIdAndDeletedFalse(Long id);

    List<ClientCard> findByClientUserUsernameAndDeletedFalse(String username);
}
