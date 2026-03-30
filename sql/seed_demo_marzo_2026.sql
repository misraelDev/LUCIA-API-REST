-- =============================================================================
-- Semillas demo: 50 contactos, 50 llamadas (historial + conversaciones), 50 citas
-- Rango de fechas llamadas/citas: 01/03/2026 – 31/03/2026
-- Base: PostgreSQL (misma que usa LUCIA-API-REST / Hibernate ddl-auto)
--
-- Uso:
--   psql -h HOST -U USER -d luciadb -f sql/seed_demo_marzo_2026.sql
--
-- Limpieza de una corrida anterior (mismos patrones):
--   Ejecutar el bloque "cleanup" al final del archivo si hace falta.
-- =============================================================================

BEGIN;

-- Limpieza idempotente (órden: calls -> contacts; appointments por resumen)
DELETE FROM calls c
WHERE c.motive LIKE 'Motivo semilla MAR2026-%';

DELETE FROM appointments a
WHERE a.summary LIKE 'Cita semilla MAR2026-%';

DELETE FROM contacts ct
WHERE ct.email LIKE 'seed.mar2026.%@lucia.test';

-- Inserciones encadenadas: cada índice crea 1 contacto + 1 llamada asociada + 1 cita en marzo
DO $$
DECLARE
  i           int;
  cid         bigint;
  day_d       int;
  call_ts     timestamp without time zone;
  appt_date   date;
  start_t     time;
  end_t       time;
  statuses    text[] := ARRAY[
    'RESERVED',
    'CONFIRMED',
    'UNASSIGNED',
    'CANCELLED',
    'SCHEDULED'
  ];
  st          text;
BEGIN
  FOR i IN 1..50 LOOP
    -- Repartir sobre 31 días de marzo
    day_d := 1 + ((i - 1) % 31);
    call_ts := make_timestamp(2026, 3, day_d, 8 + (i % 12), (i * 11) % 60, 0);
    appt_date := date '2026-03-01' + (day_d - 1);

    INSERT INTO contacts (name, email, phone_number, created_at, updated_at)
    VALUES (
      'Cliente semilla MAR2026-' || i,
      'seed.mar2026.' || i || '@lucia.test',
      '+34600100' || lpad(i::text, 3, '0'),
      call_ts,
      call_ts
    )
    RETURNING id INTO cid;

    -- Historial de llamadas + datos para "Conversaciones" (transcripción en messages)
    INSERT INTO calls (
      date,
      duration,
      motive,
      contact_id,
      summary,
      intent,
      messages,
      created_at,
      updated_at
    )
    VALUES (
      call_ts,
      45 + ((i * 17) % 420),
      'Motivo semilla MAR2026-' || i || ': consulta y seguimiento',
      cid,
      'Resumen llamada semilla ' || i || ' (demo marzo 2026).',
      CASE (i % 4)
        WHEN 0 THEN 'reserva'
        WHEN 1 THEN 'informacion'
        WHEN 2 THEN 'reclamo'
        ELSE 'venta'
      END,
      '[{"role":"customer","text":"Hola, llamo desde el móvil."},' ||
      '{"role":"assistant","text":"Buenos días, soy Lucía. ¿En qué puedo ayudarle?"},' ||
      '{"role":"customer","text":"Quiero revisar mi reserva del mes."},' ||
      '{"role":"assistant","text":"Perfecto, un momento mientras localizo los datos."}]',
      call_ts,
      call_ts
    );

    st := statuses[1 + ((i - 1) % array_length(statuses, 1))];

    start_t := make_time(9 + (i % 7), (i * 13) % 60, 0);
    end_t := start_t + interval '50 minutes';

    INSERT INTO appointments (
      summary,
      start_time,
      end_time,
      date,
      status,
      description,
      location,
      contact_phone,
      created_at,
      updated_at
    )
    VALUES (
      'Cita semilla MAR2026-' || i,
      start_t,
      end_t,
      appt_date,
      st,
      'Descripción demo cita ' || i || ' para módulo Citas (marzo 2026).',
      'Sede demo ' || (1 + (i % 4)),
      '+34600100' || lpad(i::text, 3, '0'),
      call_ts,
      call_ts
    );
  END LOOP;
END $$;

COMMIT;

-- =============================================================================
-- Solo limpieza (por si no quieres volver a insertar):
-- DELETE FROM calls WHERE motive LIKE 'Motivo semilla MAR2026-%';
-- DELETE FROM appointments WHERE summary LIKE 'Cita semilla MAR2026-%';
-- DELETE FROM contacts WHERE email LIKE 'seed.mar2026.%@lucia.test';
-- =============================================================================
