package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByUserUsernameAndDeletedFalse(String username);

    Optional<Client> findByPhone(String phone);

    Boolean existsByPhoneAndDeletedFalse(String phone);

    Boolean existsByUserUsernameAndDeletedFalse(String usernameOrEmail);

    @Query("SELECT m FROM Client m, ClientCard c WHERE (:id is null or m.id = :id) AND ((:name is null OR CONCAT('%', m.firstName, '%') LIKE CONCAT('%', :name, '%')) OR (:name is null OR CONCAT('%', m.lastName, '%') LIKE CONCAT('%', :name, '%'))) AND (:phone is null OR m.phone LIKE CONCAT('%', :phone, '%'))  AND (m.deleted = FALSE) AND ((:pan is null OR CONCAT('%', c.cardFirst6Numbers, '%') LIKE CONCAT('%', :pan, '%')) OR (:pan is null OR CONCAT('%', c.cardLast4Numbers, '%') LIKE CONCAT('%', :pan, '%')))")
    Page<Client> findAllByFilter(PageRequest of, String name, String phone, String pan, Long id);
}
