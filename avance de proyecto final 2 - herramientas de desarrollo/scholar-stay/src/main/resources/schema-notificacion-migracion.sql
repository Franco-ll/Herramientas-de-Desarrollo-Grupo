
UPDATE notificaciones
SET priority = 'INFO'
WHERE priority IS NULL;

-- Verificar que no queden registros sin prioridad
SELECT COUNT(*) AS registros_sin_prioridad
FROM notificaciones
WHERE priority IS NULL;

