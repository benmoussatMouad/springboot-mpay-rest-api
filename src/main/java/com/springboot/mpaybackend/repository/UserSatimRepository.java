package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.Merchant;
import com.springboot.mpaybackend.entity.MerchantStatus;
import com.springboot.mpaybackend.entity.UserSatim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserSatimRepository extends JpaRepository<UserSatim, Long> {
    List<UserSatim> findAllByDeletedFalse();

    @Query("SELECT m FROM UserSatim m WHERE (:id is null or m.id = :id) AND ((:firstName is null OR CONCAT('%', m.firstName, '%') LIKE CONCAT('%', :firstName, '%')) OR (:lastName is null OR CONCAT('%', m.lastName, '%') LIKE CONCAT('%', :lastName, '%'))) AND (:phone is null OR m.phone LIKE CONCAT('%', :phone, '%')) AND (m.deleted = FALSE)")
    Page<UserSatim> findByFilter(
            Pageable pageable,
            @Param("id") Long id,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("phone") String phone
    );

    Optional<UserSatim> findByIdAndDeletedFalse(Long id);

    Optional<UserSatim> findByUsernameUsernameAndDeletedFalse(String username);
}
