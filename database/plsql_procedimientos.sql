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
        -- Obtener el examen asociado a la presentaciÃ³n
SELECT ID_EXAMEN INTO v_id_examen
FROM PRESENTACION_EXAMEN
WHERE ID_PRESENTACION = :NEW.ID_PRESENTACION;

-- Contar cuÃ¡ntas preguntas tiene el examen
SELECT COUNT(*) INTO v_total_preguntas
FROM EXAMEN_PREGUNTA
WHERE ID_EXAMEN = v_id_examen;

-- Contar cuÃ¡ntas preguntas ha respondido el estudiante
SELECT COUNT(DISTINCT ID_PREGUNTA) INTO v_total_respondidas
FROM RESPUESTA_ESTUDIANTE
WHERE ID_PRESENTACION = :NEW.ID_PRESENTACION;

-- Si ya respondiÃ³ todas, calificamos
IF v_total_respondidas = v_total_preguntas THEN
            CALIFICAR_EXAMEN_AUTOMATICO(:NEW.ID_PRESENTACION, v_estado);
END IF;

EXCEPTION
        WHEN NO_DATA_FOUND THEN
            NULL; -- Silenciar la excepciÃ³n si no se encuentra la presentaciÃ³n aÃºn
WHEN OTHERS THEN
            NULL; -- (opcional) evita que cualquier otro error detenga la inserciÃ³n
END;
END;
/

//////////////////////////////////////////////////////////////////////////

--------------------------------------------------------
--  DDL for Procedure GENERAR_ESTADISTICAS_ESTUDIANTE
--------------------------------------------------------
CREATE OR REPLACE PROCEDURE GENERAR_ESTADISTICAS_ESTUDIANTE (
    p_id_estudiante IN NUMBER
) AS
    v_total_examenes NUMBER;
    v_correctas NUMBER;
    v_incorrectas NUMBER;
    v_promedio NUMBER;
    v_maxima NUMBER;
    v_minima NUMBER;
BEGIN
    -- Obtener estadísticas desde la vista
    SELECT 
        COUNT(DISTINCT pres.id_presentacion),
        SUM(CASE WHEN r.es_correcta = 'S' THEN 1 ELSE 0 END),
        SUM(CASE WHEN r.es_correcta = 'N' THEN 1 ELSE 0 END),
        AVG(pres.calificacion),
        MAX(pres.calificacion),
        MIN(pres.calificacion)
    INTO v_total_examenes, v_correctas, v_incorrectas, v_promedio, v_maxima, v_minima
    FROM PRESENTACION_EXAMEN pres
    JOIN RESPUESTA_ESTUDIANTE res_est ON res_est.id_presentacion = pres.id_presentacion
    JOIN RESPUESTA r ON r.id_respuesta = res_est.id_respuesta
    WHERE pres.id_estudiante = p_id_estudiante;

    -- Mostrar en consola
    DBMS_OUTPUT.PUT_LINE('Total exámenes: ' || v_total_examenes);
    DBMS_OUTPUT.PUT_LINE('Correctas: ' || v_correctas);
    DBMS_OUTPUT.PUT_LINE('Incorrectas: ' || v_incorrectas);
    DBMS_OUTPUT.PUT_LINE('Promedio: ' || ROUND(v_promedio, 2));
    DBMS_OUTPUT.PUT_LINE('Máxima: ' || v_maxima);
    DBMS_OUTPUT.PUT_LINE('Mínima: ' || v_minima);

    -- Guardar en tabla
    MERGE INTO ESTADISTICAS_ESTUDIANTE e
    USING (SELECT p_id_estudiante AS id_estudiante FROM dual) src
    ON (e.id_estudiante = src.id_estudiante)
    WHEN MATCHED THEN
        UPDATE SET 
            total_examenes_presentados = v_total_examenes,
            preguntas_correctas = v_correctas,
            preguntas_incorrectas = v_incorrectas,
            promedio_nota = ROUND(v_promedio, 2),
            max_nota = v_maxima,
            min_nota = v_minima
    WHEN NOT MATCHED THEN
        INSERT (id_estudiante, total_examenes_presentados, preguntas_correctas, preguntas_incorrectas, promedio_nota, max_nota, min_nota)
        VALUES (p_id_estudiante, v_total_examenes, v_correctas, v_incorrectas, ROUND(v_promedio, 2), v_maxima, v_minima);
END;
/

///////////////////////////////////////////////////////////////////////////////////////

-- Crear tipos si aún no existen
CREATE OR REPLACE TYPE TEMA_OBJ AS OBJECT (
    ID_TEMA  NUMBER,
    NOMBRE   VARCHAR2(255)
);
/

CREATE OR REPLACE TYPE TEMA_TABLA AS TABLE OF TEMA_OBJ;
/

-- Crear procedimiento que devuelve los temas
CREATE OR REPLACE PROCEDURE OBTENER_TEMAS(p_temas OUT TEMA_TABLA) IS
BEGIN
    SELECT TEMA_OBJ(ID_TEMA, NOMBRE)
    BULK COLLECT INTO p_temas
    FROM TEMA
    ORDER BY NOMBRE ASC;
END;
/
/////////////////////////PLSQL PREGUNTAS////////////////////////////////////
-- =============================
-- TIPOS DE OBJETO
-- =============================
CREATE OR REPLACE TYPE OPCION_RESPUESTA_OBJ AS OBJECT (
    id_respuesta NUMBER,
    texto       VARCHAR2(500),
    es_correcta VARCHAR2(1) -- 'S' o 'N'
);
/

CREATE OR REPLACE TYPE OPCION_RESPUESTA_TABLE AS TABLE OF OPCION_RESPUESTA_OBJ;
/

-- =============================
-- ESPECIFICACIÓN DEL PAQUETE
-- =============================
CREATE OR REPLACE PACKAGE PKG_PREGUNTA AS
  -- TIPOS
  TYPE T_CURSOR IS REF CURSOR;

  -- Procedimientos
  PROCEDURE agregar_pregunta(
    p_texto            IN VARCHAR2,
    p_tipo             IN VARCHAR2,
    p_id_tema          IN NUMBER,
    p_valor_nota       IN NUMBER,
    p_es_publica       IN VARCHAR2,
    p_id_docente       IN NUMBER,
    p_id_pregunta_padre IN NUMBER DEFAULT NULL,
    p_id_generado      OUT NUMBER
  );
  
  PROCEDURE obtener_preguntas_hijas (
    p_id_padre IN NUMBER,
    p_cursor   OUT SYS_REFCURSOR
  );
  
  PROCEDURE quitar_vinculo_padre(
  p_id_pregunta IN NUMBER
  );

  PROCEDURE agregar_opcion_respuesta(------
    p_id_pregunta IN NUMBER,
    p_texto       IN VARCHAR2,
    p_es_correcta IN VARCHAR2
  );

  PROCEDURE actualizar_pregunta(-----
    p_id_pregunta IN NUMBER,
    p_texto       IN VARCHAR2,
    p_tipo        IN VARCHAR2,
    p_id_tema     IN NUMBER,
    p_valor_nota  IN NUMBER,
    p_es_publica  IN VARCHAR2,
    p_id_pregunta_padre IN NUMBER DEFAULT NULL
  );

  PROCEDURE eliminar_pregunta(p_id_pregunta IN NUMBER);------

  PROCEDURE agregar_opciones_respuesta(
    p_id_pregunta IN NUMBER,
    p_opciones IN OPCION_RESPUESTA_TABLE
  );

  PROCEDURE eliminar_opciones_respuesta(
    p_id_pregunta IN NUMBER
  );

  PROCEDURE obtener_opciones_pregunta(------------
    p_id_pregunta IN NUMBER,
    p_cursor OUT SYS_REFCURSOR
  );

  -- NUEVO: Procedimiento con filtro por docente
  PROCEDURE obtener_preguntas_por_tema_docente(--------
    p_id_tema IN NUMBER,
    p_id_docente IN NUMBER,
    p_cursor OUT SYS_REFCURSOR
  );

  -- NUEVO: Procedimiento para obtener preguntas visibles para un docente específico
  PROCEDURE obtener_preguntas_visibles_docente(-----------
    p_id_docente IN NUMBER,
    p_cursor OUT SYS_REFCURSOR
  );

END PKG_PREGUNTA;
/

-- =============================
-- CUERPO DEL PAQUETE
-- =============================
CREATE OR REPLACE PACKAGE BODY PKG_PREGUNTA AS

    PROCEDURE agregar_pregunta (
        p_texto       IN VARCHAR2,
        p_tipo        IN VARCHAR2,
        p_id_tema     IN NUMBER,
        p_valor_nota  IN NUMBER,
        p_es_publica  IN VARCHAR2,
        p_id_docente  IN NUMBER,
        p_id_pregunta_padre IN NUMBER DEFAULT NULL,
        p_id_generado OUT NUMBER
    ) IS
    BEGIN
        INSERT INTO pregunta (id_pregunta, texto, tipo, id_tema, valor_nota, es_publica, id_docente, id_pregunta_padre)
        VALUES (SEQ_PREGUNTA.NEXTVAL, p_texto, p_tipo, p_id_tema, p_valor_nota, p_es_publica, p_id_docente, p_id_pregunta_padre)
        RETURNING id_pregunta INTO p_id_generado;
    END agregar_pregunta;

    PROCEDURE agregar_opcion_respuesta (
        p_id_pregunta IN NUMBER,
        p_texto       IN VARCHAR2,
        p_es_correcta IN VARCHAR2
    ) IS
    BEGIN
        INSERT INTO respuesta (id_respuesta, id_pregunta, texto, es_correcta)
        VALUES (SEQ_RESPUESTA.NEXTVAL, p_id_pregunta, p_texto, p_es_correcta);
    END agregar_opcion_respuesta;

    PROCEDURE actualizar_pregunta (
        p_id_pregunta IN NUMBER,
        p_texto       IN VARCHAR2,
        p_tipo        IN VARCHAR2,
        p_id_tema     IN NUMBER,
        p_valor_nota  IN NUMBER,
        p_es_publica  IN VARCHAR2,
        p_id_pregunta_padre IN NUMBER DEFAULT NULL
    ) IS
    BEGIN
        UPDATE pregunta
        SET texto = p_texto,
            tipo = p_tipo,
            id_tema = p_id_tema,
            valor_nota = p_valor_nota,
            es_publica = p_es_publica,
            id_pregunta_padre = p_id_pregunta_padre
        WHERE id_pregunta = p_id_pregunta;
        
        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(-20001, 'No se encontró la pregunta con ID: ' || p_id_pregunta);
        END IF;
        
        COMMIT;
    END actualizar_pregunta;

    PROCEDURE eliminar_pregunta (p_id_pregunta IN NUMBER) IS
    BEGIN
        DELETE FROM respuesta WHERE id_pregunta = p_id_pregunta;
        DELETE FROM examen_pregunta WHERE id_pregunta = p_id_pregunta;
        DELETE FROM pregunta WHERE id_pregunta = p_id_pregunta;
    END eliminar_pregunta;

    PROCEDURE obtener_opciones_pregunta (
        p_id_pregunta IN NUMBER,
        p_cursor      OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
        SELECT * FROM respuesta WHERE id_pregunta = p_id_pregunta;
    END obtener_opciones_pregunta;
    
      PROCEDURE agregar_opciones_respuesta(
    p_id_pregunta IN NUMBER,
    p_opciones IN OPCION_RESPUESTA_TABLE
  ) IS
  BEGIN
    FOR i IN 1 .. p_opciones.COUNT LOOP
      INSERT INTO respuesta (id_respuesta, id_pregunta, texto, es_correcta)
      VALUES (SEQ_RESPUESTA.NEXTVAL, p_id_pregunta, p_opciones(i).texto, p_opciones(i).es_correcta);
    END LOOP;
  END agregar_opciones_respuesta;

  PROCEDURE eliminar_opciones_respuesta(
    p_id_pregunta IN NUMBER
  ) IS
  BEGIN
    DELETE FROM respuesta WHERE id_pregunta = p_id_pregunta;
  END eliminar_opciones_respuesta;

    -- NUEVO: Procedimiento con filtro por docente y JOIN con TEMA
    PROCEDURE obtener_preguntas_por_tema_docente (
        p_id_tema    IN NUMBER,
        p_id_docente IN NUMBER,
        p_cursor     OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
        SELECT p.ID_PREGUNTA, 
               p.TEXTO, 
               p.TIPO, 
               p.ID_TEMA, 
               t.NOMBRE as NOMBRE_TEMA, 
               NVL(p.VALOR_NOTA, 0) AS VALOR_NOTA, 
               p.ES_PUBLICA, 
               p.ID_DOCENTE
        FROM PREGUNTA p 
        LEFT JOIN TEMA t ON p.ID_TEMA = t.ID_TEMA 
        WHERE p.ID_TEMA = p_id_tema 
          AND (p.ES_PUBLICA = 'S' OR p.ID_DOCENTE = p_id_docente)
        ORDER BY p.ID_PREGUNTA DESC;
    END obtener_preguntas_por_tema_docente;

    -- NUEVO: Procedimiento para obtener preguntas visibles para un docente específico
    PROCEDURE obtener_preguntas_visibles_docente (
        p_id_docente IN NUMBER,
        p_cursor     OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
        SELECT p.ID_PREGUNTA, 
               p.TEXTO, 
               p.TIPO, 
               p.ID_TEMA, 
               t.NOMBRE as NOMBRE_TEMA, 
               NVL(p.VALOR_NOTA, 0) AS VALOR_NOTA, 
               p.ES_PUBLICA, 
               p.ID_DOCENTE
        FROM PREGUNTA p 
        LEFT JOIN TEMA t ON p.ID_TEMA = t.ID_TEMA 
        WHERE p.ES_PUBLICA = 'S' OR p.ID_DOCENTE = p_id_docente
        ORDER BY p.ID_PREGUNTA DESC;
    END obtener_preguntas_visibles_docente;

    PROCEDURE obtener_preguntas_con_opciones_por_examen (
        p_id_examen IN NUMBER,
        p_resultado OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_resultado FOR
        SELECT p.*, r.*
        FROM examen_pregunta ep
        JOIN pregunta p ON ep.id_pregunta = p.id_pregunta
        LEFT JOIN respuesta r ON p.id_pregunta = r.id_pregunta
        WHERE ep.id_examen = p_id_examen;
    END obtener_preguntas_con_opciones_por_examen;
    
  PROCEDURE obtener_preguntas_hijas (
    p_id_padre IN NUMBER,
    p_cursor   OUT SYS_REFCURSOR
  ) IS
  BEGIN
    OPEN p_cursor FOR
    SELECT * FROM pregunta
    WHERE id_pregunta_padre = p_id_padre;
  END obtener_preguntas_hijas;
  
    PROCEDURE quitar_vinculo_padre (
      p_id_pregunta IN NUMBER
    ) IS
    BEGIN
      UPDATE pregunta
      SET id_pregunta_padre = NULL
      WHERE id_pregunta = p_id_pregunta;
    END quitar_vinculo_padre;




END PKG_PREGUNTA;
/

//////////////////////////////////CREAR_EXAMEN////////////////////////////

create or replace NONEDITIONABLE PROCEDURE CREAR_EXAMEN (
    p_nombre                IN VARCHAR2,
    p_descripcion           IN VARCHAR2,
    p_fecha_inicio          IN DATE,
    p_fecha_fin             IN DATE,
    p_tiempo_limite         IN NUMBER,
    p_id_docente            IN NUMBER,
    p_numero_preguntas      IN NUMBER,
    p_modo_seleccion        IN VARCHAR2,
    p_tiempo_por_pregunta   IN NUMBER,
    p_id_tema               IN NUMBER,
    p_nota_minima_aprob     IN NUMBER DEFAULT 3.0,
    p_id_grupo              IN NUMBER,
    p_estado                OUT VARCHAR2,
    p_id_generado           OUT NUMBER
)
AS
BEGIN
    INSERT INTO EXAMEN (
        ID_EXAMEN, NOMBRE, DESCRIPCION, FECHA_INICIO, FECHA_FIN,
        TIEMPO_LIMITE, ID_DOCENTE, NUMERO_PREGUNTAS, MODO_SELECCION,
        TIEMPO_POR_PREGUNTA, ID_TEMA, NOTA_MINIMA_APROBACION, ID_GRUPO
    )
    VALUES (
        SEQ_EXAMEN.NEXTVAL, p_nombre, p_descripcion, p_fecha_inicio, p_fecha_fin,
        p_tiempo_limite, p_id_docente, p_numero_preguntas, p_modo_seleccion,
        p_tiempo_por_pregunta, p_id_tema, p_nota_minima_aprob, p_id_grupo
    )
    RETURNING ID_EXAMEN INTO p_id_generado;

    p_estado := 'OK';

EXCEPTION
    WHEN OTHERS THEN
        p_estado := SQLERRM; -- Devuelve el mensaje de error exacto
        p_id_generado := NULL;
END;
/
//////////////////////////////////////////////////////////////////////////
--------------------------------------------------------
--  DDL for Procedure GENERAR_ESTADISTICAS_EXAMEN
--------------------------------------------------------
CREATE OR REPLACE PROCEDURE GENERAR_ESTADISTICAS_EXAMEN (
    p_id_examen IN NUMBER
) AS
    v_total_presentaciones NUMBER;
    v_promedio NUMBER;
    v_maxima NUMBER;
    v_minima NUMBER;
BEGIN
    SELECT COUNT(*), AVG(calificacion), MAX(calificacion), MIN(calificacion)
    INTO v_total_presentaciones, v_promedio, v_maxima, v_minima
    FROM PRESENTACION_EXAMEN
    WHERE id_examen = p_id_examen;

    DBMS_OUTPUT.PUT_LINE('Total presentaciones: ' || v_total_presentaciones);
    DBMS_OUTPUT.PUT_LINE('Promedio: ' || ROUND(v_promedio, 2));
    DBMS_OUTPUT.PUT_LINE('Máxima: ' || v_maxima);
    DBMS_OUTPUT.PUT_LINE('Mínima: ' || v_minima);

    -- Guardar en tabla
    MERGE INTO ESTADISTICAS_EXAMEN e
    USING (SELECT p_id_examen AS id_examen FROM dual) src
    ON (e.id_examen = src.id_examen)
    WHEN MATCHED THEN
        UPDATE SET 
            total_presentados = v_total_presentaciones,
            promedio_nota = ROUND(v_promedio, 2),
            max_nota = v_maxima,
            min_nota = v_minima
    WHEN NOT MATCHED THEN
        INSERT (id_estadistica, id_examen, total_presentados, promedio_nota, max_nota, min_nota)
        VALUES (SEQ_ID_PREGUNTA.NEXTVAL, p_id_examen, v_total_presentaciones, ROUND(v_promedio, 2), v_maxima, v_minima);
END;
/

--------------------------------------------------------
--  DDL for Procedure GENERAR_ESTADISTICAS_PREGUNTA
--------------------------------------------------------
CREATE OR REPLACE PROCEDURE GENERAR_ESTADISTICAS_PREGUNTA (
    p_id_pregunta IN NUMBER
) AS
    v_total_respuestas NUMBER;
    v_correctas NUMBER;
    v_incorrectas NUMBER;
    v_porcentaje NUMBER;
BEGIN
    SELECT COUNT(*),
           SUM(CASE WHEN r.es_correcta = 'S' THEN 1 ELSE 0 END),
           SUM(CASE WHEN r.es_correcta = 'N' THEN 1 ELSE 0 END)
    INTO v_total_respuestas, v_correctas, v_incorrectas
    FROM RESPUESTA_ESTUDIANTE re
    JOIN RESPUESTA r ON re.id_respuesta = r.id_respuesta
    WHERE re.id_pregunta = p_id_pregunta;

    IF v_total_respuestas > 0 THEN
        v_porcentaje := ROUND((v_correctas * 100.0) / v_total_respuestas, 2);
    ELSE
        v_porcentaje := 0;
    END IF;

    DBMS_OUTPUT.PUT_LINE('Total respuestas: ' || v_total_respuestas);
    DBMS_OUTPUT.PUT_LINE('Correctas: ' || v_correctas);
    DBMS_OUTPUT.PUT_LINE('Incorrectas: ' || v_incorrectas);
    DBMS_OUTPUT.PUT_LINE('Porcentaje de aciertos: ' || v_porcentaje);

    MERGE INTO ESTADISTICAS_PREGUNTA e
    USING (SELECT p_id_pregunta AS id_pregunta FROM dual) src
    ON (e.id_pregunta = src.id_pregunta)
    WHEN MATCHED THEN
        UPDATE SET 
            porcentaje_correctas = v_porcentaje,
            total_respuestas = v_total_respuestas,
            correctas = v_correctas,
            incorrectas = v_incorrectas
    WHEN NOT MATCHED THEN
        INSERT (id_pregunta, porcentaje_correctas, total_respuestas, correctas, incorrectas)
        VALUES (p_id_pregunta, v_porcentaje, v_total_respuestas, v_correctas, v_incorrectas);
END;
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
    DBMS_OUTPUT.PUT_LINE('Total exámenes: ' || v_total_examenes);
    DBMS_OUTPUT.PUT_LINE('Promedio: ' || ROUND(v_promedio, 2));
    DBMS_OUTPUT.PUT_LINE('Máxima: ' || v_maxima);
    DBMS_OUTPUT.PUT_LINE('Mínima: ' || v_minima);

    MERGE INTO ESTADISTICAS_GRUPO e
    USING (SELECT p_id_grupo AS id_grupo FROM dual) src
    ON (e.id_grupo = src.id_grupo)
    WHEN MATCHED THEN
        UPDATE SET 
            total_examenes_presentados = v_total_examenes,
            promedio_general = ROUND(v_promedio, 2),
            max_nota = v_maxima,
            min_nota = v_minima
    WHEN NOT MATCHED THEN
        INSERT (id_grupo, total_examenes_presentados, promedio_general, max_nota, min_nota)
        VALUES (p_id_grupo, v_total_examenes, ROUND(v_promedio, 2), v_maxima, v_minima);
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
    DBMS_OUTPUT.PUT_LINE('Promedio: ' || ROUND(v_promedio_nota, 2));
    DBMS_OUTPUT.PUT_LINE('Máxima: ' || v_max_nota);
    DBMS_OUTPUT.PUT_LINE('Mínima: ' || v_min_nota);

    MERGE INTO ESTADISTICAS_TEMA e
    USING (SELECT p_id_tema AS id_tema FROM dual) src
    ON (e.id_tema = src.id_tema)
    WHEN MATCHED THEN
        UPDATE SET 
            total_preguntas = v_total_respuestas,
            total_correctas = v_correctas,
            total_incorrectas = v_incorrectas,
            promedio_nota = ROUND(v_promedio_nota, 2),
            max_nota = v_max_nota,
            min_nota = v_min_nota
    WHEN NOT MATCHED THEN
        INSERT (id_tema, total_preguntas, total_correctas, total_incorrectas, promedio_nota, max_nota, min_nota)
        VALUES (p_id_tema, v_total_respuestas, v_correctas, v_incorrectas, ROUND(v_promedio_nota, 2), v_max_nota, v_min_nota);
END;
/
