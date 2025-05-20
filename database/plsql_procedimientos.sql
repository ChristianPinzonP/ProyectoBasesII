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
         LEFT JOIN (
    SELECT eg.id_estudiante, g.id_grupo, g.nombre
    FROM ESTUDIANTE_GRUPO eg
             JOIN GRUPO g ON eg.id_grupo = g.id_grupo
    WHERE eg.estado = 'ACTIVO'
) g ON e.id_estudiante = g.id_estudiante
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
        -- Obtener el examen asociado a la presentaci贸n
SELECT ID_EXAMEN INTO v_id_examen
FROM PRESENTACION_EXAMEN
WHERE ID_PRESENTACION = :NEW.ID_PRESENTACION;

-- Contar cu谩ntas preguntas tiene el examen
SELECT COUNT(*) INTO v_total_preguntas
FROM EXAMEN_PREGUNTA
WHERE ID_EXAMEN = v_id_examen;

-- Contar cu谩ntas preguntas ha respondido el estudiante
SELECT COUNT(DISTINCT ID_PREGUNTA) INTO v_total_respondidas
FROM RESPUESTA_ESTUDIANTE
WHERE ID_PRESENTACION = :NEW.ID_PRESENTACION;

-- Si ya respondi贸 todas, calificamos
IF v_total_respondidas = v_total_preguntas THEN
            CALIFICAR_EXAMEN_AUTOMATICO(:NEW.ID_PRESENTACION, v_estado);
END IF;

EXCEPTION
        WHEN NO_DATA_FOUND THEN
            NULL; -- Silenciar la excepci贸n si no se encuentra la presentaci贸n a煤n
WHEN OTHERS THEN
            NULL; -- (opcional) evita que cualquier otro error detenga la inserci贸n
END;
END;
/

//////////////////////////////////////////////////////////////////////////

--------------------------------------------------------
--  DDL for Procedure GENERAR_ESTADISTICAS_ESTUDIANTE
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "ROOT2"."GENERAR_ESTADISTICAS_ESTUDIANTE" (
    p_id_estudiante IN NUMBER
) AS
    v_total_examenes NUMBER;
    v_promedio NUMBER;
    v_maxima NUMBER;
    v_minima NUMBER;
BEGIN
    SELECT COUNT(*), AVG(calificacion), MAX(calificacion), MIN(calificacion)
    INTO v_total_examenes, v_promedio, v_maxima, v_minima
    FROM Presentacion_Examen
    WHERE id_estudiante = p_id_estudiante;

    DBMS_OUTPUT.PUT_LINE('Total ex?menes presentados: ' || v_total_examenes);
    DBMS_OUTPUT.PUT_LINE('Promedio de calificaciones: ' || v_promedio);
    DBMS_OUTPUT.PUT_LINE('Calificaci?n m?xima: ' || v_maxima);
    DBMS_OUTPUT.PUT_LINE('Calificaci?n m?nima: ' || v_minima);
END Generar_Estadisticas_Estudiante;




/
///////////////////////////////////////////////////////////////////////////////////////
--------------------------------------------------------
--  DDL for Procedure GENERAR_ESTADISTICAS_EXAMEN
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "ROOT2"."GENERAR_ESTADISTICAS_EXAMEN" (
    p_id_examen IN NUMBER
) AS
    v_total_presentaciones NUMBER;
    v_promedio NUMBER;
    v_maxima NUMBER;
    v_minima NUMBER;
BEGIN
    SELECT COUNT(*), AVG(calificacion), MAX(calificacion), MIN(calificacion)
    INTO v_total_presentaciones, v_promedio, v_maxima, v_minima
    FROM Presentacion_Examen
    WHERE id_examen = p_id_examen;

    DBMS_OUTPUT.PUT_LINE('Total presentaciones: ' || v_total_presentaciones);
    DBMS_OUTPUT.PUT_LINE('Promedio calificaciones: ' || v_promedio);
    DBMS_OUTPUT.PUT_LINE('Nota m?xima: ' || v_maxima);
    DBMS_OUTPUT.PUT_LINE('Nota m?nima: ' || v_minima);
END Generar_Estadisticas_Examen;




/
--------------------------------------------------------
--  DDL for Procedure GENERAR_ESTADISTICAS_PREGUNTA
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "ROOT2"."GENERAR_ESTADISTICAS_PREGUNTA" (
    p_id_pregunta IN NUMBER
) AS
    v_total_respuestas NUMBER;
    v_correctas NUMBER;
    v_incorrectas NUMBER;
BEGIN
    SELECT COUNT(*), 
           SUM(CASE WHEN r.es_correcta = 'S' THEN 1 ELSE 0 END), 
           SUM(CASE WHEN r.es_correcta = 'N' THEN 1 ELSE 0 END)
    INTO v_total_respuestas, v_correctas, v_incorrectas
    FROM Respuesta_Estudiante re
    JOIN Respuesta r ON re.id_respuesta = r.id_respuesta
    WHERE re.id_pregunta = p_id_pregunta;

    DBMS_OUTPUT.PUT_LINE('Total respuestas: ' || v_total_respuestas);
    DBMS_OUTPUT.PUT_LINE('Respuestas correctas: ' || v_correctas);
    DBMS_OUTPUT.PUT_LINE('Respuestas incorrectas: ' || v_incorrectas);
END Generar_Estadisticas_Pregunta;




/

--------------------------------------------------------
--  DDL for Procedure GENERAR_ESTADISTICAS_GRUPO
--------------------------------------------------------

CREATE OR REPLACE PROCEDURE GENERAR_ESTADISTICAS_GRUPO (
    p_id_grupo IN NUMBER
) AS
    v_total_examenes NUMBER;
    v_promedio NUMBER;
    v_maxima NUMBER;
    v_minima NUMBER;
BEGIN
    SELECT COUNT(DISTINCT pres.id_presentacion),
           AVG(pres.calificacion),
           MAX(pres.calificacion),
           MIN(pres.calificacion)
    INTO v_total_examenes, v_promedio, v_maxima, v_minima
    FROM GRUPO g
    JOIN ESTUDIANTE_GRUPO eg ON eg.id_grupo = g.id_grupo
    JOIN PRESENTACION_EXAMEN pres ON pres.id_estudiante = eg.id_estudiante
    WHERE g.id_grupo = p_id_grupo;

    DBMS_OUTPUT.PUT_LINE('Grupo ID: ' || p_id_grupo);
    DBMS_OUTPUT.PUT_LINE('Total ex醡enes presentados: ' || v_total_examenes);
    DBMS_OUTPUT.PUT_LINE('Promedio general: ' || ROUND(v_promedio, 2));
    DBMS_OUTPUT.PUT_LINE('Nota m醲ima: ' || v_maxima);
    DBMS_OUTPUT.PUT_LINE('Nota m韓ima: ' || v_minima);
END;
/

--------------------------------------------------------
--  DDL for Procedure GENERAR_ESTADISTICAS_TEMA
--------------------------------------------------------

CREATE OR REPLACE PROCEDURE GENERAR_ESTADISTICAS_TEMA (
    p_id_tema IN NUMBER
) AS
    v_total_respuestas NUMBER;
    v_correctas NUMBER;
    v_incorrectas NUMBER;
    v_promedio_nota NUMBER;
    v_max_nota NUMBER;
    v_min_nota NUMBER;
BEGIN
    SELECT COUNT(re.id_respuesta_estudiante),
           SUM(CASE WHEN r.es_correcta = 'S' THEN 1 ELSE 0 END),
           SUM(CASE WHEN r.es_correcta = 'N' THEN 1 ELSE 0 END),
           AVG(pres.calificacion),
           MAX(pres.calificacion),
           MIN(pres.calificacion)
    INTO v_total_respuestas, v_correctas, v_incorrectas,
         v_promedio_nota, v_max_nota, v_min_nota
    FROM TEMA t
    JOIN PREGUNTA p ON p.id_tema = t.id_tema
    JOIN RESPUESTA r ON r.id_pregunta = p.id_pregunta
    JOIN RESPUESTA_ESTUDIANTE re ON re.id_respuesta = r.id_respuesta
    JOIN PRESENTACION_EXAMEN pres ON pres.id_presentacion = re.id_presentacion
    WHERE t.id_tema = p_id_tema;

    DBMS_OUTPUT.PUT_LINE('Tema ID: ' || p_id_tema);
    DBMS_OUTPUT.PUT_LINE('Total respuestas: ' || v_total_respuestas);
    DBMS_OUTPUT.PUT_LINE('Correctas: ' || v_correctas);
    DBMS_OUTPUT.PUT_LINE('Incorrectas: ' || v_incorrectas);
    DBMS_OUTPUT.PUT_LINE('Promedio nota: ' || ROUND(v_promedio_nota, 2));
    DBMS_OUTPUT.PUT_LINE('Nota m醲ima: ' || v_max_nota);
    DBMS_OUTPUT.PUT_LINE('Nota m韓ima: ' || v_min_nota);
END;
/
