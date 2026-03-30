package com.lucia.api.repository.Appointment;

import com.lucia.api.model.entity.Appointment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Coincide teléfonos guardados con distinto formato (+34..., espacios) comparando solo dígitos.
     */
    @Query(
            value =
                    "SELECT * FROM appointments WHERE "
                            + "NULLIF(regexp_replace(COALESCE(contact_phone, ''), '[^0-9]', '', 'g'), '') = :digits "
                            + "ORDER BY date DESC NULLS LAST, start_time DESC NULLS LAST",
            nativeQuery = true)
    List<Appointment> findByContactPhoneDigits(@Param("digits") String digits);
}
