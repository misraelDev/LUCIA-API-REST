package com.lucia.api.repository.Call;

import com.lucia.api.model.entity.Call;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CallRepository extends JpaRepository<Call, Long> {

    @Query("SELECT c FROM Call c LEFT JOIN FETCH c.contact WHERE c.id = :id")
    Optional<Call> findByIdWithContact(@Param("id") Long id);

    Page<Call> findByDateBetween(
            LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Call> findByDateGreaterThanEqual(LocalDateTime start, Pageable pageable);

    Page<Call> findByDateLessThanEqual(LocalDateTime end, Pageable pageable);
}
