CREATE OR REPLACE PROCEDURE LOGIN_USUARIO (
    p_correo        IN  VARCHAR2,
    p_contrasena    IN  VARCHAR2,
    p_id_usuario    OUT NUMBER,
    p_nombre        OUT VARCHAR2,
    p_tipo_usuario  OUT VARCHAR2,
    p_estado        OUT VARCHAR2
)
IS
BEGIN
SELECT ID_USUARIO, NOMBRE, TIPO_USUARIO
INTO p_id_usuario, p_nombre, p_tipo_usuario
FROM USUARIO
WHERE CORREO = p_correo
  AND CONTRASENA = p_contrasena
  AND LOWER(TIPO_USUARIO) IN ('estudiante', 'docente');

p_estado := 'OK';
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_estado := 'NO_ENCONTRADO';
        p_id_usuario := NULL;
        p_nombre := NULL;
        p_tipo_usuario := NULL;
WHEN OTHERS THEN
        p_estado := 'ERROR';
        p_id_usuario := NULL;
        p_nombre := NULL;
        p_tipo_usuario := NULL;
END;
/
//////////////////////////////////////////////////
CREATE OR REPLACE PROCEDURE OBTENER_DOCENTE_COMPLETO (
    p_id_usuario      IN  NUMBER,
    p_nombre          OUT VARCHAR2,
    p_correo          OUT VARCHAR2,
    p_asignatura      OUT VARCHAR2,
    p_estado          OUT VARCHAR2
) AS
BEGIN
SELECT u.nombre, u.correo, d.asignatura
INTO p_nombre, p_correo, p_asignatura
FROM USUARIO u
         JOIN DOCENTE d ON u.id_usuario = d.id_docente
WHERE u.id_usuario = p_id_usuario;

p_estado := 'OK';
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_estado := 'NO_ENCONTRADO';
        p_nombre := NULL;
        p_correo := NULL;
        p_asignatura := NULL;
WHEN OTHERS THEN
        p_estado := 'ERROR';
        p_nombre := NULL;
        p_correo := NULL;
        p_asignatura := NULL;
END;
/
/////////////////////////////////////////////////////////////////

create or replace NONEDITIONABLE PROCEDURE OBTENER_ESTUDIANTE_COMPLETO (
    p_id_usuario      IN  NUMBER,
    p_nombre          OUT VARCHAR2,
    p_correo          OUT VARCHAR2,
    p_id_grupo        OUT NUMBER,
    p_nombre_grupo    OUT VARCHAR2,
    p_estado          OUT VARCHAR2
) AS
BEGIN

    SELECT u.nombre, u.correo, g.id_grupo, g.nombre
    INTO p_nombre, p_correo, p_id_grupo, p_nombre_grupo
    FROM USUARIO u
    JOIN ESTUDIANTE e ON u.id_usuario = e.id_estudiante
    LEFT JOIN ESTUDIANTE_GRUPO eg ON e.id_estudiante = eg.id_estudiante
    LEFT JOIN GRUPO g ON eg.id_grupo = g.id_grupo
    WHERE u.id_usuario = p_id_usuario;

    p_estado := 'OK';

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_estado := 'NO_ENCONTRADO';
        p_nombre := NULL;
        p_correo := NULL;
        p_id_grupo := NULL;
        p_nombre_grupo := NULL;
    WHEN OTHERS THEN
        p_estado := 'ERROR';
        p_nombre := NULL;
        p_correo := NULL;
        p_id_grupo := NULL;
        p_nombre_grupo := NULL;
END;
/

///////////////////7Secuencias//////////////////////////////////////////

CREATE SEQUENCE SEQ_RESPUESTA_ESTUDIANTE
    START WITH 1
    INCREMENT BY 1
    NOCACHE;

CREATE SEQUENCE SEQ_RESPUESTA
    START WITH 1
    INCREMENT BY 1
    NOCACHE;

CREATE SEQUENCE SEQ_PRESENTACION_EXAMEN
    START WITH 1
    INCREMENT BY 1
    NOCACHE;

///////////////////////////////////////////////////////////////////////////////

create or replace NONEDITIONABLE PROCEDURE CALIFICAR_EXAMEN_AUTOMATICO (
    p_id_presentacion IN NUMBER,
    p_estado OUT VARCHAR2
)
AS
    v_total_posible   NUMBER := 0;
    v_total_obtenido  NUMBER := 0;
    v_id_examen       NUMBER;
    v_calificacion    NUMBER(5,2);
BEGIN
    -- Obtener el ID del examen
    SELECT ID_EXAMEN
    INTO v_id_examen
    FROM PRESENTACION_EXAMEN
    WHERE ID_PRESENTACION = p_id_presentacion;

    -- Total de puntos posibles (suma de VALOR_NOTA de todas las preguntas del examen)
    SELECT NVL(SUM(VALOR_NOTA), 0)
    INTO v_total_posible
    FROM EXAMEN_PREGUNTA
    WHERE ID_EXAMEN = v_id_examen;

    -- Total obtenido por respuestas correctas del estudiante
    SELECT NVL(SUM(ep.VALOR_NOTA), 0)
    INTO v_total_obtenido
    FROM RESPUESTA_ESTUDIANTE re
    JOIN RESPUESTA r ON re.ID_RESPUESTA = r.ID_RESPUESTA
    JOIN EXAMEN_PREGUNTA ep ON re.ID_PREGUNTA = ep.ID_PREGUNTA AND ep.ID_EXAMEN = v_id_examen
    WHERE re.ID_PRESENTACION = p_id_presentacion
      AND r.ES_CORRECTA = 'S';

    -- Aplicar regla de 3 proporcional (nota final sobre 5)
    IF v_total_posible > 0 THEN
        v_calificacion := ROUND((v_total_obtenido * 5) / v_total_posible, 2);
    ELSE
        v_calificacion := 0;
    END IF;

    -- Guardar la nota en la tabla
    UPDATE PRESENTACION_EXAMEN
    SET CALIFICACION = v_calificacion
    WHERE ID_PRESENTACION = p_id_presentacion;

    p_estado := 'OK';

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_estado := 'NO_ENCONTRADO';
    WHEN OTHERS THEN
        p_estado := 'ERROR';
END;
/
////////////////////////////////////Disparadores//////////////////////////////

CREATE OR REPLACE NONEDITIONABLE TRIGGER TRG_CALIFICAR_AL_FINALIZAR
AFTER INSERT ON RESPUESTA_ESTUDIANTE
FOR EACH ROW
DECLARE
v_id_examen EXAMEN.ID_EXAMEN%TYPE;
    v_total_preguntas NUMBER;
    v_total_respondidas NUMBER;
    v_estado VARCHAR2(20);
BEGIN
BEGIN
        -- Obtener el examen asociado a la presentación
SELECT ID_EXAMEN INTO v_id_examen
FROM PRESENTACION_EXAMEN
WHERE ID_PRESENTACION = :NEW.ID_PRESENTACION;

-- Contar cuántas preguntas tiene el examen
SELECT COUNT(*) INTO v_total_preguntas
FROM EXAMEN_PREGUNTA
WHERE ID_EXAMEN = v_id_examen;

-- Contar cuántas preguntas ha respondido el estudiante
SELECT COUNT(DISTINCT ID_PREGUNTA) INTO v_total_respondidas
FROM RESPUESTA_ESTUDIANTE
WHERE ID_PRESENTACION = :NEW.ID_PRESENTACION;

-- Si ya respondió todas, calificamos
IF v_total_respondidas = v_total_preguntas THEN
            CALIFICAR_EXAMEN_AUTOMATICO(:NEW.ID_PRESENTACION, v_estado);
END IF;

EXCEPTION
        WHEN NO_DATA_FOUND THEN
            NULL; -- Silenciar la excepción si no se encuentra la presentación aún
WHEN OTHERS THEN
            NULL; -- (opcional) evita que cualquier otro error detenga la inserción
END;
END;
/
