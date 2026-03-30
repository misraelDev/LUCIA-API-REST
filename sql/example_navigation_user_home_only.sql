-- Rol user: solo sección Inicio (user_home → /dashboard/user/home).
-- Sustituye el nombre por el de tu cliente / empresa (columna name, obligatoria).

INSERT INTO public.tenants (name, navigation_config, created_at, updated_at)
VALUES (
    'Usuario lucia',
    '{"user":["user_home"]}',
    NOW(),
    NOW()
);

-- O actualizar uno existente (ej. id = 1)
-- UPDATE public.tenants
-- SET navigation_config = '{"user":["user_home"]}'
-- WHERE id = 1;
