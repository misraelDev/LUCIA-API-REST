-- Ejecutar manualmente cuando quieras (los INSERT crean filas nuevas cada vez).
-- Según tenant_id del usuario, el API devuelve las secciones de navigation_config para su rol.

-- Tres tenants de ejemplo (cada registro = acceso/menú de ese cliente).

INSERT INTO public.tenants (name, navigation_config, created_at, updated_at)
VALUES (
    'Admin',
    '{"admin":["admin_home","admin_users","admin_tenants"]}',
    NOW(),
    NOW()
);

INSERT INTO public.tenants (name, navigation_config, created_at, updated_at)
VALUES (
    'Usuario',
    '{"user":["user_home","user_call_history","user_conversations","user_appointments","user_contacts"]}',
    NOW(),
    NOW()
);


INSERT INTO public.tenants (name, navigation_config, created_at, updated_at)
VALUES (
    'Vendedor',
    '{"seller":["user_home","user_call_history","user_conversations","user_appointments","user_contacts"]}',
    NOW(),
    NOW()
);
