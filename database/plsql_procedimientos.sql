--------------------------------------------------------
-- Archivo creado  - jueves-mayo-29-2025   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Package PKG_DOCENTE
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE PACKAGE "ROOT2"."PKG_DOCENTE" AS

    PROCEDURE OBTENER_DOCENTE_COMPLETO (
        p_id_usuario      IN  NUMBER,
        p_nombre          OUT VARCHAR2,
        p_correo          OUT VARCHAR2,
        p_asignatura      OUT VARCHAR2,
        p_estado          OUT VARCHAR2
    );
    
    -- Procedimiento para obtener docente por ID
    PROCEDURE OBTENER_DOCENTE_POR_ID(
        p_id_docente IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    );

END PKG_DOCENTE;

/
--------------------------------------------------------
--  DDL for Package PKG_EXAMEN
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE PACKAGE "ROOT2"."PKG_EXAMEN" AS
    
    -- Cursor para obtener exámenes por docente
    PROCEDURE OBTENER_EXAMENES_POR_DOCENTE(
        p_id_docente IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    );

    -- Procedimiento para agregar examen
    PROCEDURE AGREGAR_EXAMEN(
        p_nombre IN VARCHAR2,
        p_descripcion IN VARCHAR2,
        p_fecha_inicio IN DATE,
        p_fecha_fin IN DATE,
        p_tiempo_limite IN NUMBER,
        p_id_docente IN NUMBER,
        p_id_tema IN NUMBER,
        p_id_grupo IN NUMBER,
        p_modo_seleccion IN VARCHAR2,
        p_tiempo_por_pregunta IN NUMBER,
        p_numero_preguntas IN NUMBER,
        p_intentos_permitidos IN NUMBER,
        p_resultado OUT NUMBER
    );

    -- Procedimiento para editar examen
    PROCEDURE EDITAR_EXAMEN(
        p_id_examen IN NUMBER,
        p_nombre IN VARCHAR2,
        p_descripcion IN VARCHAR2,
        p_fecha_inicio IN DATE,
        p_fecha_fin IN DATE,
        p_tiempo_limite IN NUMBER,
        p_id_docente IN NUMBER,
        p_id_tema IN NUMBER,
        p_id_grupo IN NUMBER,
        p_numero_preguntas IN NUMBER,
        p_modo_seleccion IN VARCHAR2,
        p_tiempo_por_pregunta IN NUMBER,
        p_intentos_permitidos IN NUMBER,
        p_resultado OUT NUMBER
    );

    -- Cursor para obtener exámenes por grupo
    PROCEDURE OBTENER_EXAMENES_POR_GRUPO(
        p_id_grupo IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    );

    -- Procedimiento para eliminar examen
    PROCEDURE ELIMINAR_EXAMEN(
        p_id_examen IN NUMBER,
        p_resultado OUT NUMBER
    );

END PKG_EXAMEN;

/
--------------------------------------------------------
--  DDL for Package PKG_EXAMEN_PREGUNTA
--------------------------------------------------------

  CREATE OR REPLACE NONEDITIONABLE PACKAGE "ROOT2"."PKG_EXAMEN_PREGUNTA" AS

    -- Asignar pregunta a examen con validación de coherencia de tema
    PROCEDURE ASIGNAR_PREGUNTA_A_EXAMEN (
        p_id_examen     IN NUMBER,
        p_id_pregunta   IN NUMBER,
        p_valor_nota    IN NUMBER,
        p_resultado     OUT VARCHAR2
    );

    -- Eliminar pregunta de examen
    PROCEDURE ELIMINAR_PREGUNTA_DE_EXAMEN (
        p_id_examen     IN NUMBER,
        p_id_pregunta   IN NUMBER,
        p_resultado     OUT VARCHAR2
    );

    -- Obtener preguntas de un examen
    PROCEDURE OBTENER_PREGUNTAS_DE_EXAMEN (
        p_id_examen IN NUMBER,
        p_cursor    OUT SYS_REFCURSOR
    );

    -- Validar coherencia de tema entre examen y pregunta
    FUNCTION VALIDAR_COHERENCIA_TEMA_EXAMEN_PREGUNTA (
        p_id_examen   IN NUMBER,
        p_id_pregunta IN NUMBER
    ) RETURN VARCHAR2;

    -- Obtener el total de nota de un examen
    FUNCTION OBTENER_TOTAL_NOTA_EXAMEN (
        p_id_examen IN NUMBER
    ) RETURN NUMBER;

    -- Contar preguntas de un examen
    FUNCTION CONTAR_PREGUNTAS_EXAMEN (
        p_id_examen IN NUMBER
    ) RETURN NUMBER;

    -- Validar si una pregunta ya está asignada a un examen
    FUNCTION PREGUNTA_YA_ASIGNADA (
        p_id_examen   IN NUMBER,
        p_id_pregunta IN NUMBER
    ) RETURN VARCHAR2;

    -- Obtener valor de nota de una pregunta en un examen específico
    FUNCTION OBTENER_VALOR_NOTA_PREGUNTA_EXAMEN (
        p_id_examen   IN NUMBER,
        p_id_pregunta IN NUMBER
    ) RETURN NUMBER;

    -- Actualizar valor de nota de una pregunta en un examen
    PROCEDURE ACTUALIZAR_VALOR_NOTA_PREGUNTA (
        p_id_examen   IN NUMBER,
        p_id_pregunta IN NUMBER,
        p_valor_nota  IN NUMBER,
        p_resultado   OUT VARCHAR2
    );

END PKG_EXAMEN_PREGUNTA;


/
--------------------------------------------------------
--  DDL for Package PKG_GRUPO
--------------------------------------------------------

  CREATE OR REPLACE NONEDITIONABLE PACKAGE "ROOT2"."PKG_GRUPO" AS
    
    -- Tipo para cursor de grupos
    TYPE t_grupo_record IS RECORD (
        id_grupo NUMBER,
        nombre VARCHAR2(200)
    );

    TYPE t_grupos_cursor IS REF CURSOR RETURN t_grupo_record;

    -- FUNCIÓN PARA OBTENER GRUPOS POR DOCENTE
    FUNCTION OBTENER_GRUPOS_POR_DOCENTE (
        p_id_docente IN NUMBER
    ) RETURN t_grupos_cursor;

END PKG_GRUPO;

/
--------------------------------------------------------
--  DDL for Package PKG_PREGUNTA
--------------------------------------------------------

  CREATE OR REPLACE NONEDITIONABLE PACKAGE "ROOT2"."PKG_PREGUNTA" AS
  TYPE T_CURSOR IS REF CURSOR;

  PROCEDURE agregar_pregunta(
    p_texto             IN VARCHAR2,
    p_tipo              IN VARCHAR2,
    p_id_tema           IN NUMBER,
    p_valor_nota        IN NUMBER,
    p_es_publica        IN VARCHAR2,
    p_id_docente        IN NUMBER,
    p_id_pregunta_padre IN NUMBER DEFAULT NULL,
    p_id_generado       OUT NUMBER
  );

  PROCEDURE obtener_preguntas_hijas (
    p_id_padre IN NUMBER,
    p_cursor   OUT SYS_REFCURSOR
  );

  PROCEDURE quitar_vinculo_padre(
    p_id_pregunta IN NUMBER
  );

  PROCEDURE agregar_opcion_respuesta(
    p_id_pregunta IN NUMBER,
    p_texto       IN VARCHAR2,
    p_es_correcta IN VARCHAR2
  );

  PROCEDURE actualizar_pregunta(
    p_id_pregunta       IN NUMBER,
    p_texto             IN VARCHAR2,
    p_tipo              IN VARCHAR2,
    p_id_tema           IN NUMBER,
    p_valor_nota        IN NUMBER,
    p_es_publica        IN VARCHAR2,
    p_id_pregunta_padre IN NUMBER DEFAULT NULL
  );

  PROCEDURE eliminar_pregunta(
    p_id_pregunta IN NUMBER
  );

  PROCEDURE agregar_opciones_respuesta(
    p_id_pregunta IN NUMBER,
    p_opciones    IN OPCION_RESPUESTA_TABLE
  );

  PROCEDURE eliminar_opciones_respuesta(
    p_id_pregunta IN NUMBER
  );

  PROCEDURE obtener_opciones_pregunta(
    p_id_pregunta IN NUMBER,
    p_cursor      OUT SYS_REFCURSOR
  );

  PROCEDURE obtener_preguntas_por_tema_docente(
    p_id_tema    IN NUMBER,
    p_id_docente IN NUMBER,
    p_cursor     OUT SYS_REFCURSOR
  );

  PROCEDURE obtener_preguntas_visibles_docente(
    p_id_docente IN NUMBER,
    p_cursor     OUT SYS_REFCURSOR
  );

  PROCEDURE obtener_preguntas_candidatas_padre(
    p_id_docente IN NUMBER,
    p_cursor     OUT SYS_REFCURSOR
  );

  PROCEDURE obtener_preguntas_con_opciones_por_examen(
    p_id_examen IN NUMBER,
    p_resultado OUT SYS_REFCURSOR
  );
  
  PROCEDURE OBTENER_TEMA_PREGUNTA(
    p_id_pregunta IN NUMBER,
    p_id_tema OUT NUMBER
  );
  
  PROCEDURE VALIDAR_TEMA_HIJA_PADRE(
    p_id_pregunta_padre IN NUMBER,
    p_id_tema_hija IN NUMBER,
    p_resultado OUT VARCHAR2
  );
  
  PROCEDURE obtener_preguntas_por_tema_y_disponibles (
    p_id_tema    IN NUMBER,
    p_id_docente IN NUMBER,
    p_id_examen  IN NUMBER,
    p_cursor     OUT SYS_REFCURSOR
  );
  
  -- Verifica si una pregunta está siendo utilizada en exámenes presentados
  FUNCTION pregunta_esta_en_examen_presentado(
    p_id_pregunta IN NUMBER
  ) RETURN VARCHAR2;

  -- Obtiene información de los exámenes que usan una pregunta específica
  PROCEDURE obtener_examenes_que_usan_pregunta(
    p_id_pregunta IN NUMBER,
    p_cursor      OUT SYS_REFCURSOR
  );  

END PKG_PREGUNTA;

/
--------------------------------------------------------
--  DDL for Package PKG_PRESENTACION_EXAMEN
--------------------------------------------------------

  CREATE OR REPLACE NONEDITIONABLE PACKAGE "ROOT2"."PKG_PRESENTACION_EXAMEN" AS
    PROCEDURE REGISTRAR_RESPUESTA (
        p_id_presentacion   IN PRESENTACION_EXAMEN.ID_PRESENTACION%TYPE,
        p_id_pregunta       IN PREGUNTA.ID_PREGUNTA%TYPE,
        p_id_opcion         IN RESPUESTA.ID_RESPUESTA%TYPE
    );

    PROCEDURE CALIFICAR_EXAMEN_AUTOMATICO (
        p_id_presentacion IN NUMBER,
        p_estado OUT VARCHAR2
    );

    PROCEDURE FINALIZAR_EXAMEN (
        p_id_presentacion IN NUMBER,
        p_estado OUT VARCHAR2
    );

    PROCEDURE CREAR_PRESENTACION_EXAMEN (
        p_id_examen     IN EXAMEN.ID_EXAMEN%TYPE,
        p_id_estudiante IN ESTUDIANTE.ID_ESTUDIANTE%TYPE
    );

    -- NUEVA FUNCIÓN AGREGADA
    FUNCTION OBTENER_CALIFICACION (
        p_id_presentacion IN NUMBER
    ) RETURN NUMBER;

    -- FUNCIÓN PARA CONTAR INTENTOS REALIZADOS
    FUNCTION CONTAR_INTENTOS_REALIZADOS (
        p_id_estudiante IN NUMBER,
        p_id_examen IN NUMBER
    ) RETURN NUMBER;

    -- FUNCIÓN PARA OBTENER INTENTOS PERMITIDOS
    FUNCTION OBTENER_INTENTOS_PERMITIDOS (
        p_id_examen IN NUMBER
    ) RETURN NUMBER;

    -- FUNCIÓN PARA VERIFICAR SI EXAMEN TIENE PRESENTACIONES
    FUNCTION EXAMEN_TIENE_PRESENTACIONES (
        p_id_examen IN NUMBER
    ) RETURN NUMBER;

    -- FUNCIÓN PARA OBTENER ESTADÍSTICAS DE PRESENTACIONES
    FUNCTION OBTENER_ESTADISTICAS_PRESENTACIONES (
        p_id_examen IN NUMBER,
        p_total OUT NUMBER,
        p_finalizados OUT NUMBER,
        p_en_progreso OUT NUMBER
    ) RETURN VARCHAR2;

END PKG_PRESENTACION_EXAMEN;

/
--------------------------------------------------------
--  DDL for Package Body PKG_DOCENTE
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE PACKAGE BODY "ROOT2"."PKG_DOCENTE" AS

    PROCEDURE OBTENER_DOCENTE_COMPLETO (
        p_id_usuario      IN  NUMBER,
        p_nombre          OUT VARCHAR2,
        p_correo          OUT VARCHAR2,
        p_asignatura      OUT VARCHAR2,
        p_estado          OUT VARCHAR2
    ) AS
    BEGIN
        -- Corregir el JOIN: usar el campo correcto de relación
        SELECT u.nombre, u.correo, d.asignatura
        INTO p_nombre, p_correo, p_asignatura
        FROM USUARIO u
        JOIN DOCENTE d ON u.id_usuario = d.id_docente  -- Corrección aquí
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
            -- Opcional: Log del error real
            DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
    END;

    -- Implementación: Obtener docente por ID
    PROCEDURE OBTENER_DOCENTE_POR_ID(
        p_id_docente IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT d.id_docente,
                   d.asignatura,
                   u.nombre,
                   u.correo,
                   u.contrasena,
                   u.tipo_usuario
            FROM DOCENTE d 
            JOIN USUARIO u ON d.id_docente = u.id_usuario 
            WHERE d.id_docente = p_id_docente;

    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Error al obtener docente por ID: ' || SQLERRM);
            -- En caso de error, devolver cursor vacío
            OPEN p_cursor FOR
                SELECT NULL as id_docente,
                       NULL as id_usuario,
                       NULL as asignatura,
                       NULL as nombre,
                       NULL as correo,
                       NULL as contrasena,
                       NULL as tipo_usuario
                FROM DUAL
                WHERE 1 = 0; -- Esta condición nunca será verdadera, por lo que devuelve 0 filas
    END OBTENER_DOCENTE_POR_ID;

END PKG_DOCENTE;

/
--------------------------------------------------------
--  DDL for Package Body PKG_EXAMEN
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE PACKAGE BODY "ROOT2"."PKG_EXAMEN" AS

    -- Implementación: Obtener exámenes por docente
    PROCEDURE OBTENER_EXAMENES_POR_DOCENTE(
        p_id_docente IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT e.id_examen, 
                   e.nombre, 
                   e.descripcion, 
                   e.fecha_inicio, 
                   e.fecha_fin,
                   e.tiempo_limite, 
                   e.id_docente, 
                   e.id_tema, 
                   e.id_grupo, 
                   t.NOMBRE AS nombre_tema, 
                   g.NOMBRE AS nombre_grupo,
                   e.numero_preguntas, 
                   e.modo_seleccion, 
                   e.tiempo_por_pregunta, 
                   e.intentos_permitidos
            FROM EXAMEN e 
            LEFT JOIN TEMA t ON e.id_tema = t.id_tema 
            LEFT JOIN GRUPO g ON e.id_grupo = g.id_grupo 
            WHERE e.id_docente = p_id_docente 
            ORDER BY e.id_examen DESC;
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Error al obtener exámenes por docente: ' || SQLERRM);
            RAISE;
    END OBTENER_EXAMENES_POR_DOCENTE;

    -- Implementación: Agregar examen
    PROCEDURE AGREGAR_EXAMEN(
        p_nombre IN VARCHAR2,
        p_descripcion IN VARCHAR2,
        p_fecha_inicio IN DATE,
        p_fecha_fin IN DATE,
        p_tiempo_limite IN NUMBER,
        p_id_docente IN NUMBER,
        p_id_tema IN NUMBER,
        p_id_grupo IN NUMBER,
        p_modo_seleccion IN VARCHAR2,
        p_tiempo_por_pregunta IN NUMBER,
        p_numero_preguntas IN NUMBER,
        p_intentos_permitidos IN NUMBER,
        p_resultado OUT NUMBER
    ) AS
    BEGIN
        -- Validaciones básicas
        IF p_nombre IS NULL OR LENGTH(TRIM(p_nombre)) = 0 THEN
            p_resultado := 0;
            RETURN;
        END IF;

        IF p_fecha_inicio > p_fecha_fin THEN
            p_resultado := 0;
            RETURN;
        END IF;

        -- Insertar el examen
        INSERT INTO EXAMEN (
            NOMBRE, DESCRIPCION, FECHA_INICIO, FECHA_FIN, TIEMPO_LIMITE, 
            ID_DOCENTE, ID_TEMA, ID_GRUPO, MODO_SELECCION, TIEMPO_POR_PREGUNTA, 
            NUMERO_PREGUNTAS, INTENTOS_PERMITIDOS
        ) VALUES (
            p_nombre, p_descripcion, p_fecha_inicio, p_fecha_fin, p_tiempo_limite,
            p_id_docente, p_id_tema, p_id_grupo, p_modo_seleccion, p_tiempo_por_pregunta,
            p_numero_preguntas, p_intentos_permitidos
        );

        p_resultado := SQL%ROWCOUNT;
        COMMIT;

    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            p_resultado := 0;
            DBMS_OUTPUT.PUT_LINE('Error al agregar examen: ' || SQLERRM);
    END AGREGAR_EXAMEN;

    -- Implementación: Editar examen
    PROCEDURE EDITAR_EXAMEN(
        p_id_examen IN NUMBER,
        p_nombre IN VARCHAR2,
        p_descripcion IN VARCHAR2,
        p_fecha_inicio IN DATE,
        p_fecha_fin IN DATE,
        p_tiempo_limite IN NUMBER,
        p_id_docente IN NUMBER,
        p_id_tema IN NUMBER,
        p_id_grupo IN NUMBER,
        p_numero_preguntas IN NUMBER,
        p_modo_seleccion IN VARCHAR2,
        p_tiempo_por_pregunta IN NUMBER,
        p_intentos_permitidos IN NUMBER,
        p_resultado OUT NUMBER
    ) AS
    BEGIN
        -- Validaciones básicas
        IF p_nombre IS NULL OR LENGTH(TRIM(p_nombre)) = 0 THEN
            p_resultado := 0;
            RETURN;
        END IF;

        IF p_fecha_inicio > p_fecha_fin THEN
            p_resultado := 0;
            RETURN;
        END IF;

        -- Actualizar el examen
        UPDATE EXAMEN 
        SET nombre = p_nombre,
            descripcion = p_descripcion,
            fecha_inicio = p_fecha_inicio,
            fecha_fin = p_fecha_fin,
            tiempo_limite = p_tiempo_limite,
            id_docente = p_id_docente,
            id_tema = p_id_tema,
            id_grupo = p_id_grupo,
            numero_preguntas = p_numero_preguntas,
            modo_seleccion = p_modo_seleccion,
            tiempo_por_pregunta = p_tiempo_por_pregunta,
            intentos_permitidos = p_intentos_permitidos
        WHERE id_examen = p_id_examen;

        p_resultado := SQL%ROWCOUNT;
        COMMIT;

    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            p_resultado := 0;
            DBMS_OUTPUT.PUT_LINE('Error al editar examen: ' || SQLERRM);
    END EDITAR_EXAMEN;

    -- Implementación: Obtener exámenes por grupo
    PROCEDURE OBTENER_EXAMENES_POR_GRUPO(
        p_id_grupo IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT id_examen, 
                   nombre, 
                   descripcion, 
                   fecha_inicio, 
                   fecha_fin, 
                   tiempo_limite, 
                   id_docente, 
                   id_tema, 
                   id_grupo 
            FROM EXAMEN 
            WHERE id_grupo = p_id_grupo
            ORDER BY id_examen;
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Error al obtener exámenes por grupo: ' || SQLERRM);
            RAISE;
    END OBTENER_EXAMENES_POR_GRUPO;

    -- Implementación: Eliminar examen
    PROCEDURE ELIMINAR_EXAMEN(
        p_id_examen IN NUMBER,
        p_resultado OUT NUMBER
    ) AS
    BEGIN
        -- Verificar si el examen existe
        DECLARE
            v_count NUMBER;
        BEGIN
            SELECT COUNT(*) INTO v_count 
            FROM EXAMEN 
            WHERE id_examen = p_id_examen;

            IF v_count = 0 THEN
                p_resultado := 0;
                RETURN;
            END IF;
        END;

        -- Iniciar transacción
        SAVEPOINT sp_eliminar_examen;

        -- Eliminar relaciones en EXAMEN_PREGUNTA
        DELETE FROM EXAMEN_PREGUNTA 
        WHERE id_examen = p_id_examen;

        -- Eliminar el examen
        DELETE FROM EXAMEN 
        WHERE id_examen = p_id_examen;

        p_resultado := SQL%ROWCOUNT;
        COMMIT;

    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK TO sp_eliminar_examen;
            p_resultado := 0;
            DBMS_OUTPUT.PUT_LINE('Error al eliminar examen: ' || SQLERRM);
    END ELIMINAR_EXAMEN;

END PKG_EXAMEN;

/
--------------------------------------------------------
--  DDL for Package Body PKG_EXAMEN_PREGUNTA
--------------------------------------------------------

  CREATE OR REPLACE NONEDITIONABLE PACKAGE BODY "ROOT2"."PKG_EXAMEN_PREGUNTA" AS

    PROCEDURE ASIGNAR_PREGUNTA_A_EXAMEN (
        p_id_examen     IN NUMBER,
        p_id_pregunta   IN NUMBER,
        p_valor_nota    IN NUMBER,
        p_resultado     OUT VARCHAR2
    ) AS
        v_coherencia VARCHAR2(1);
        v_ya_asignada VARCHAR2(1);
    BEGIN
        -- Validar coherencia de tema
        v_coherencia := VALIDAR_COHERENCIA_TEMA_EXAMEN_PREGUNTA(p_id_examen, p_id_pregunta);
        
        IF v_coherencia = 'N' THEN
            p_resultado := 'ERROR_TEMA_INCOHERENTE';
            RETURN;
        END IF;

        -- Validar si ya está asignada
        v_ya_asignada := PREGUNTA_YA_ASIGNADA(p_id_examen, p_id_pregunta);
        
        IF v_ya_asignada = 'S' THEN
            p_resultado := 'ERROR_PREGUNTA_YA_ASIGNADA';
            RETURN;
        END IF;

        -- Insertar la asignación
        INSERT INTO EXAMEN_PREGUNTA (
            ID_EXAMEN,
            ID_PREGUNTA,
            VALOR_NOTA
        ) VALUES (
            p_id_examen,
            p_id_pregunta,
            p_valor_nota
        );

        COMMIT;
        p_resultado := 'OK';

    EXCEPTION
        WHEN DUP_VAL_ON_INDEX THEN
            p_resultado := 'ERROR_DUPLICADO';
            ROLLBACK;
        WHEN OTHERS THEN
            p_resultado := 'ERROR_GENERAL';
            ROLLBACK;
    END ASIGNAR_PREGUNTA_A_EXAMEN;

    PROCEDURE ELIMINAR_PREGUNTA_DE_EXAMEN (
        p_id_examen     IN NUMBER,
        p_id_pregunta   IN NUMBER,
        p_resultado     OUT VARCHAR2
    ) AS
        v_count NUMBER;
    BEGIN
        -- Verificar que la asignación existe
        SELECT COUNT(*)
        INTO v_count
        FROM EXAMEN_PREGUNTA
        WHERE ID_EXAMEN = p_id_examen AND ID_PREGUNTA = p_id_pregunta;

        IF v_count = 0 THEN
            p_resultado := 'ERROR_NO_ENCONTRADO';
            RETURN;
        END IF;

        -- Eliminar la asignación
        DELETE FROM EXAMEN_PREGUNTA
        WHERE ID_EXAMEN = p_id_examen AND ID_PREGUNTA = p_id_pregunta;

        COMMIT;
        p_resultado := 'OK';

    EXCEPTION
        WHEN OTHERS THEN
            p_resultado := 'ERROR_GENERAL';
            ROLLBACK;
    END ELIMINAR_PREGUNTA_DE_EXAMEN;

    PROCEDURE OBTENER_PREGUNTAS_DE_EXAMEN (
        p_id_examen IN NUMBER,
        p_cursor    OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
        SELECT 
            p.ID_PREGUNTA,
            p.TEXTO,
            p.TIPO,
            p.ID_TEMA,
            t.NOMBRE AS NOMBRE_TEMA,
            ep.VALOR_NOTA,
            p.ES_PUBLICA,
            p.ID_DOCENTE,
            p.ID_PREGUNTA_PADRE
        FROM PREGUNTA p
        JOIN EXAMEN_PREGUNTA ep ON ep.ID_PREGUNTA = p.ID_PREGUNTA
        LEFT JOIN TEMA t ON p.ID_TEMA = t.ID_TEMA
        WHERE ep.ID_EXAMEN = p_id_examen
        ORDER BY p.ID_PREGUNTA;

    EXCEPTION
        WHEN OTHERS THEN
            IF p_cursor%ISOPEN THEN
                CLOSE p_cursor;
            END IF;
            RAISE_APPLICATION_ERROR(-20001, 'Error al obtener preguntas del examen: ' || SQLERRM);
    END OBTENER_PREGUNTAS_DE_EXAMEN;

    FUNCTION VALIDAR_COHERENCIA_TEMA_EXAMEN_PREGUNTA (
        p_id_examen   IN NUMBER,
        p_id_pregunta IN NUMBER
    ) RETURN VARCHAR2 AS
        v_count NUMBER;
    BEGIN
        SELECT COUNT(*)
        INTO v_count
        FROM EXAMEN e
        JOIN PREGUNTA p ON e.ID_TEMA = p.ID_TEMA
        WHERE e.ID_EXAMEN = p_id_examen AND p.ID_PREGUNTA = p_id_pregunta;

        IF v_count > 0 THEN
            RETURN 'S';
        ELSE
            RETURN 'N';
        END IF;

    EXCEPTION
        WHEN OTHERS THEN
            RETURN 'N';
    END VALIDAR_COHERENCIA_TEMA_EXAMEN_PREGUNTA;

    FUNCTION OBTENER_TOTAL_NOTA_EXAMEN (
        p_id_examen IN NUMBER
    ) RETURN NUMBER AS
        v_total NUMBER := 0;
    BEGIN
        SELECT NVL(SUM(VALOR_NOTA), 0)
        INTO v_total
        FROM EXAMEN_PREGUNTA
        WHERE ID_EXAMEN = p_id_examen;

        RETURN v_total;

    EXCEPTION
        WHEN OTHERS THEN
            RETURN 0;
    END OBTENER_TOTAL_NOTA_EXAMEN;

    FUNCTION CONTAR_PREGUNTAS_EXAMEN (
        p_id_examen IN NUMBER
    ) RETURN NUMBER AS
        v_count NUMBER := 0;
    BEGIN
        SELECT COUNT(*)
        INTO v_count
        FROM EXAMEN_PREGUNTA
        WHERE ID_EXAMEN = p_id_examen;

        RETURN v_count;

    EXCEPTION
        WHEN OTHERS THEN
            RETURN 0;
    END CONTAR_PREGUNTAS_EXAMEN;

    FUNCTION PREGUNTA_YA_ASIGNADA (
        p_id_examen   IN NUMBER,
        p_id_pregunta IN NUMBER
    ) RETURN VARCHAR2 AS
        v_count NUMBER;
    BEGIN
        SELECT COUNT(*)
        INTO v_count
        FROM EXAMEN_PREGUNTA
        WHERE ID_EXAMEN = p_id_examen AND ID_PREGUNTA = p_id_pregunta;

        IF v_count > 0 THEN
            RETURN 'S';
        ELSE
            RETURN 'N';
        END IF;

    EXCEPTION
        WHEN OTHERS THEN
            RETURN 'N';
    END PREGUNTA_YA_ASIGNADA;

    FUNCTION OBTENER_VALOR_NOTA_PREGUNTA_EXAMEN (
        p_id_examen   IN NUMBER,
        p_id_pregunta IN NUMBER
    ) RETURN NUMBER AS
        v_valor_nota NUMBER;
    BEGIN
        SELECT VALOR_NOTA
        INTO v_valor_nota
        FROM EXAMEN_PREGUNTA
        WHERE ID_EXAMEN = p_id_examen AND ID_PREGUNTA = p_id_pregunta;

        RETURN v_valor_nota;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN -1;
        WHEN OTHERS THEN
            RETURN -1;
    END OBTENER_VALOR_NOTA_PREGUNTA_EXAMEN;

    PROCEDURE ACTUALIZAR_VALOR_NOTA_PREGUNTA (
        p_id_examen   IN NUMBER,
        p_id_pregunta IN NUMBER,
        p_valor_nota  IN NUMBER,
        p_resultado   OUT VARCHAR2
    ) AS
        v_count NUMBER;
    BEGIN
        -- Verificar que la asignación existe
        SELECT COUNT(*)
        INTO v_count
        FROM EXAMEN_PREGUNTA
        WHERE ID_EXAMEN = p_id_examen AND ID_PREGUNTA = p_id_pregunta;

        IF v_count = 0 THEN
            p_resultado := 'ERROR_NO_ENCONTRADO';
            RETURN;
        END IF;

        -- Actualizar el valor de la nota
        UPDATE EXAMEN_PREGUNTA
        SET VALOR_NOTA = p_valor_nota
        WHERE ID_EXAMEN = p_id_examen AND ID_PREGUNTA = p_id_pregunta;

        COMMIT;
        p_resultado := 'OK';

    EXCEPTION
        WHEN OTHERS THEN
            p_resultado := 'ERROR_GENERAL';
            ROLLBACK;
    END ACTUALIZAR_VALOR_NOTA_PREGUNTA;

END PKG_EXAMEN_PREGUNTA;

/
--------------------------------------------------------
--  DDL for Package Body PKG_GRUPO
--------------------------------------------------------

  CREATE OR REPLACE NONEDITIONABLE PACKAGE BODY "ROOT2"."PKG_GRUPO" AS

    -- IMPLEMENTACIÓN OBTENER GRUPOS POR DOCENTE
    FUNCTION OBTENER_GRUPOS_POR_DOCENTE (
        p_id_docente IN NUMBER
    ) RETURN t_grupos_cursor
    IS
        v_cursor t_grupos_cursor;
    BEGIN
        OPEN v_cursor FOR
            SELECT g.ID_GRUPO, g.NOMBRE
            FROM GRUPO g
            JOIN DOCENTE_GRUPO dg ON g.ID_GRUPO = dg.ID_GRUPO
            WHERE dg.ID_DOCENTE = p_id_docente
            ORDER BY g.NOMBRE;

        RETURN v_cursor;

    EXCEPTION
        WHEN OTHERS THEN
            IF v_cursor%ISOPEN THEN
                CLOSE v_cursor;
            END IF;
            RAISE;
    END OBTENER_GRUPOS_POR_DOCENTE;

END PKG_GRUPO;

/
--------------------------------------------------------
--  DDL for Package Body PKG_PREGUNTA
--------------------------------------------------------

  CREATE OR REPLACE NONEDITIONABLE PACKAGE BODY "ROOT2"."PKG_PREGUNTA" AS

  PROCEDURE agregar_pregunta (
    p_texto             IN VARCHAR2,
    p_tipo              IN VARCHAR2,
    p_id_tema           IN NUMBER,
    p_valor_nota        IN NUMBER,
    p_es_publica        IN VARCHAR2,
    p_id_docente        IN NUMBER,
    p_id_pregunta_padre IN NUMBER DEFAULT NULL,
    p_id_generado       OUT NUMBER
  ) IS
  BEGIN
    INSERT INTO pregunta (
      id_pregunta, texto, tipo, id_tema, valor_nota,
      es_publica, id_docente, id_pregunta_padre
    )
    VALUES (
      SEQ_PREGUNTA.NEXTVAL, p_texto, p_tipo, p_id_tema,
      p_valor_nota, p_es_publica, p_id_docente, p_id_pregunta_padre
    )
    RETURNING id_pregunta INTO p_id_generado;
  END agregar_pregunta;

  PROCEDURE agregar_opcion_respuesta (
    p_id_pregunta IN NUMBER,
    p_texto       IN VARCHAR2,
    p_es_correcta IN VARCHAR2
  ) IS
  BEGIN
    INSERT INTO respuesta (
      id_respuesta, id_pregunta, texto, es_correcta
    )
    VALUES (
      SEQ_RESPUESTA.NEXTVAL, p_id_pregunta, p_texto, p_es_correcta
    );
  END agregar_opcion_respuesta;

  PROCEDURE actualizar_pregunta (
    p_id_pregunta       IN NUMBER,
    p_texto             IN VARCHAR2,
    p_tipo              IN VARCHAR2,
    p_id_tema           IN NUMBER,
    p_valor_nota        IN NUMBER,
    p_es_publica        IN VARCHAR2,
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

  PROCEDURE agregar_opciones_respuesta(
    p_id_pregunta IN NUMBER,
    p_opciones    IN OPCION_RESPUESTA_TABLE
  ) IS
  BEGIN
    FOR i IN 1 .. p_opciones.COUNT LOOP
      INSERT INTO respuesta (
        id_respuesta, id_pregunta, texto, es_correcta
      )
      VALUES (
        SEQ_RESPUESTA.NEXTVAL,
        p_id_pregunta,
        p_opciones(i).texto,
        p_opciones(i).es_correcta
      );
    END LOOP;
  END agregar_opciones_respuesta;

  PROCEDURE eliminar_opciones_respuesta(
    p_id_pregunta IN NUMBER
  ) IS
  BEGIN
    DELETE FROM respuesta WHERE id_pregunta = p_id_pregunta;
  END eliminar_opciones_respuesta;

  PROCEDURE obtener_opciones_pregunta (
    p_id_pregunta IN NUMBER,
    p_cursor      OUT SYS_REFCURSOR
  ) IS
  BEGIN
    OPEN p_cursor FOR
    SELECT * FROM respuesta WHERE id_pregunta = p_id_pregunta;
  END obtener_opciones_pregunta;

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
           t.NOMBRE AS NOMBRE_TEMA, 
           NVL(p.VALOR_NOTA, 0) AS VALOR_NOTA, 
           p.ES_PUBLICA, 
           p.ID_DOCENTE,
           p.ID_PREGUNTA_PADRE --------
    FROM PREGUNTA p 
    LEFT JOIN TEMA t ON p.ID_TEMA = t.ID_TEMA 
    WHERE p.ID_TEMA = p_id_tema 
      AND (p.ES_PUBLICA = 'S' OR p.ID_DOCENTE = p_id_docente)
    ORDER BY p.ID_PREGUNTA DESC;
  END obtener_preguntas_por_tema_docente;

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
           t.NOMBRE AS NOMBRE_TEMA, 
           NVL(p.VALOR_NOTA, 0) AS VALOR_NOTA, 
           p.ES_PUBLICA, 
           p.ID_DOCENTE,
           p.ID_PREGUNTA_PADRE 
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
    SELECT 
      p.ID_PREGUNTA     AS P_ID_PREGUNTA,
      p.TEXTO           AS P_TEXTO,
      p.TIPO            AS P_TIPO,
      p.ID_TEMA         AS P_ID_TEMA,
      p.ID_PREGUNTA_PADRE AS P_ID_PREGUNTA_PADRE,-----
      p.VALOR_NOTA      AS P_VALOR_NOTA,
      r.ID_RESPUESTA    AS R_ID_RESPUESTA,
      r.TEXTO           AS R_TEXTO,
      r.ES_CORRECTA     AS R_ES_CORRECTA
    FROM examen_pregunta ep
    JOIN pregunta p ON ep.id_pregunta = p.id_pregunta
    LEFT JOIN respuesta r ON p.id_pregunta = r.id_pregunta
    WHERE ep.id_examen = p_id_examen
    ORDER BY p.ID_PREGUNTA, r.ID_RESPUESTA;
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

  PROCEDURE obtener_preguntas_candidatas_padre (
    p_id_docente IN NUMBER,
    p_cursor     OUT SYS_REFCURSOR
  ) IS
  BEGIN
    OPEN p_cursor FOR
    SELECT p.ID_PREGUNTA, 
           p.TEXTO, 
           p.TIPO, 
           p.ID_TEMA, 
           t.NOMBRE AS NOMBRE_TEMA, 
           NVL(p.VALOR_NOTA, 0) AS VALOR_NOTA, 
           p.ES_PUBLICA, 
           p.ID_DOCENTE,
           p.ID_PREGUNTA_PADRE 
    FROM PREGUNTA p 
    LEFT JOIN TEMA t ON p.ID_TEMA = t.ID_TEMA 
    WHERE (p.ES_PUBLICA = 'S' OR p.ID_DOCENTE = p_id_docente)
      AND p.ID_PREGUNTA_PADRE IS NULL  -- Solo preguntas que NO son hijas
    ORDER BY p.ID_PREGUNTA DESC;
  END obtener_preguntas_candidatas_padre;
  
  PROCEDURE OBTENER_TEMA_PREGUNTA(
    p_id_pregunta IN NUMBER,
    p_id_tema OUT NUMBER
  ) IS
  BEGIN
    SELECT ID_TEMA 
    INTO p_id_tema
    FROM PREGUNTA 
    WHERE ID_PREGUNTA = p_id_pregunta;
    
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_id_tema := -1;
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20001, 'Error al obtener tema de pregunta: ' || SQLERRM);
    END OBTENER_TEMA_PREGUNTA;
    
    PROCEDURE VALIDAR_TEMA_HIJA_PADRE(
        p_id_pregunta_padre IN NUMBER,
        p_id_tema_hija IN NUMBER,
        p_resultado OUT VARCHAR2
    ) IS
        v_tema_padre NUMBER;
  BEGIN
    -- Obtener el tema de la pregunta padre
    SELECT ID_TEMA 
    INTO v_tema_padre
    FROM PREGUNTA 
    WHERE ID_PREGUNTA = p_id_pregunta_padre;
    
    -- Comparar temas
    IF v_tema_padre = p_id_tema_hija THEN
        p_resultado := 'VALIDO';
    ELSE
        p_resultado := 'INVALIDO';
    END IF;
    
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_resultado := 'PADRE_NO_ENCONTRADO';
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20002, 'Error al validar tema hija-padre: ' || SQLERRM);
    END VALIDAR_TEMA_HIJA_PADRE;

  PROCEDURE obtener_preguntas_por_tema_y_disponibles (
    p_id_tema    IN NUMBER,
    p_id_docente IN NUMBER,
    p_id_examen  IN NUMBER,
    p_cursor     OUT SYS_REFCURSOR
  ) IS
  BEGIN
    OPEN p_cursor FOR
    SELECT p.ID_PREGUNTA, 
           p.TEXTO, 
           p.TIPO, 
           p.ID_TEMA, 
           t.NOMBRE AS NOMBRE_TEMA, 
           NVL(p.VALOR_NOTA, 0) AS VALOR_NOTA, 
           p.ES_PUBLICA, 
           p.ID_DOCENTE,
           p.ID_PREGUNTA_PADRE
    FROM PREGUNTA p
    LEFT JOIN TEMA t ON p.ID_TEMA = t.ID_TEMA
    WHERE p.ID_TEMA = p_id_tema
      AND (p.ES_PUBLICA = 'S' OR p.ID_DOCENTE = p_id_docente)
      AND p.ID_PREGUNTA NOT IN (
        SELECT ID_PREGUNTA
        FROM EXAMEN_PREGUNTA
        WHERE ID_EXAMEN = p_id_examen
      )
    ORDER BY p.ID_PREGUNTA DESC;
  END obtener_preguntas_por_tema_y_disponibles;
  
-- Agregar estas implementaciones al PACKAGE BODY PKG_PREGUNTA (antes del END final)

  FUNCTION pregunta_esta_en_examen_presentado(
    p_id_pregunta IN NUMBER
  ) RETURN VARCHAR2 IS
    v_count NUMBER := 0;
  BEGIN
    SELECT COUNT(*)
    INTO v_count
    FROM EXAMEN_PREGUNTA ep
    JOIN PRESENTACION_EXAMEN pe ON ep.ID_EXAMEN = pe.ID_EXAMEN
    WHERE ep.ID_PREGUNTA = p_id_pregunta 
      AND pe.ESTADO IN ('FINALIZADO', 'EN_PROGRESO');
    
    -- Log para debug (opcional)
    DBMS_OUTPUT.PUT_LINE('>> Pregunta ' || p_id_pregunta || ' está en ' || v_count || ' presentaciones');
    
    IF v_count > 0 THEN
      RETURN 'S';
    ELSE
      RETURN 'N';
    END IF;
    
  EXCEPTION
    WHEN OTHERS THEN
      RAISE_APPLICATION_ERROR(-20003, 'Error al verificar pregunta en examen presentado: ' || SQLERRM);
  END pregunta_esta_en_examen_presentado;

  PROCEDURE obtener_examenes_que_usan_pregunta(
    p_id_pregunta IN NUMBER,
    p_cursor      OUT SYS_REFCURSOR
  ) IS
  BEGIN
    OPEN p_cursor FOR
    SELECT DISTINCT 
      e.ID_EXAMEN,
      e.NOMBRE,
      e.FECHA_INICIO,
      e.FECHA_FIN,
      COUNT(pe.ID_PRESENTACION) AS TOTAL_PRESENTACIONES
    FROM EXAMEN e
    JOIN EXAMEN_PREGUNTA ep ON e.ID_EXAMEN = ep.ID_EXAMEN
    LEFT JOIN PRESENTACION_EXAMEN pe ON e.ID_EXAMEN = pe.ID_EXAMEN
    WHERE ep.ID_PREGUNTA = p_id_pregunta
    GROUP BY e.ID_EXAMEN, e.NOMBRE, e.FECHA_INICIO, e.FECHA_FIN
    ORDER BY e.FECHA_INICIO DESC;
    
  EXCEPTION
    WHEN OTHERS THEN
      RAISE_APPLICATION_ERROR(-20004, 'Error al obtener exámenes que usan pregunta: ' || SQLERRM);
  END obtener_examenes_que_usan_pregunta;  
  
END PKG_PREGUNTA;

/
--------------------------------------------------------
--  DDL for Package Body PKG_PRESENTACION_EXAMEN
--------------------------------------------------------

  CREATE OR REPLACE NONEDITIONABLE PACKAGE BODY "ROOT2"."PKG_PRESENTACION_EXAMEN" AS

    PROCEDURE REGISTRAR_RESPUESTA (
        p_id_presentacion   IN PRESENTACION_EXAMEN.ID_PRESENTACION%TYPE,
        p_id_pregunta       IN PREGUNTA.ID_PREGUNTA%TYPE,
        p_id_opcion         IN RESPUESTA.ID_RESPUESTA%TYPE
    ) AS
        v_fecha_fin DATE;
    BEGIN
        SELECT FECHA_HORA_FIN
        INTO v_fecha_fin
        FROM PRESENTACION_EXAMEN
        WHERE ID_PRESENTACION = p_id_presentacion;

        IF SYSDATE > v_fecha_fin THEN
            RAISE_APPLICATION_ERROR(-20003, 'El tiempo para responder el examen ha finalizado.');
        END IF;

        INSERT INTO RESPUESTA_ESTUDIANTE (
            ID_RESPUESTA_ESTUDIANTE,
            ID_PRESENTACION,
            ID_PREGUNTA,
            ID_RESPUESTA
        )
        VALUES (
            SEQ_RESPUESTA_ESTUDIANTE.NEXTVAL,
            p_id_presentacion,
            p_id_pregunta,
            p_id_opcion
        );

        COMMIT;
    END REGISTRAR_RESPUESTA;

    PROCEDURE CALIFICAR_EXAMEN_AUTOMATICO (
        p_id_presentacion IN NUMBER,
        p_estado OUT VARCHAR2
    )
    IS
        v_id_examen       NUMBER;
        v_total_posible   NUMBER := 0;
        v_total_obtenido  NUMBER := 0;
        v_calificacion    NUMBER(5,2);
    BEGIN
        SELECT ID_EXAMEN
        INTO v_id_examen
        FROM PRESENTACION_EXAMEN
        WHERE ID_PRESENTACION = p_id_presentacion;

        FOR pr IN (
            SELECT ep.ID_PREGUNTA, p.TIPO, ep.VALOR_NOTA
            FROM EXAMEN_PREGUNTA ep
            JOIN PREGUNTA p ON p.ID_PREGUNTA = ep.ID_PREGUNTA
            WHERE ep.ID_EXAMEN = v_id_examen
        ) LOOP

            IF pr.TIPO = 'Compuesta' THEN
                DECLARE
                    v_suma_hijas NUMBER := 0;
                BEGIN
                    SELECT SUM(ep.VALOR_NOTA)
                    INTO v_suma_hijas
                    FROM EXAMEN_PREGUNTA ep
                    JOIN PREGUNTA ph ON ep.ID_PREGUNTA = ph.ID_PREGUNTA
                    WHERE ep.ID_EXAMEN = v_id_examen
                      AND ph.ID_PREGUNTA_PADRE = pr.ID_PREGUNTA;

                    FOR hija IN (
                        SELECT ep.ID_PREGUNTA, ep.VALOR_NOTA
                        FROM EXAMEN_PREGUNTA ep
                        JOIN PREGUNTA ph ON ep.ID_PREGUNTA = ph.ID_PREGUNTA
                        WHERE ep.ID_EXAMEN = v_id_examen
                          AND ph.ID_PREGUNTA_PADRE = pr.ID_PREGUNTA
                    ) LOOP
                        DECLARE
                            v_es_correcta VARCHAR2(1);
                            v_id_resp NUMBER;
                            v_nota_ponderada NUMBER := hija.VALOR_NOTA;
                        BEGIN
                            IF v_suma_hijas > pr.VALOR_NOTA THEN
                                v_nota_ponderada := (hija.VALOR_NOTA / v_suma_hijas) * pr.VALOR_NOTA;
                            END IF;

                            SELECT ID_RESPUESTA INTO v_id_resp
                            FROM RESPUESTA_ESTUDIANTE
                            WHERE ID_PRESENTACION = p_id_presentacion
                              AND ID_PREGUNTA = hija.ID_PREGUNTA;

                            SELECT ES_CORRECTA INTO v_es_correcta
                            FROM RESPUESTA
                            WHERE ID_RESPUESTA = v_id_resp;

                            v_total_posible := v_total_posible + v_nota_ponderada;
                            IF v_es_correcta = 'S' THEN
                                v_total_obtenido := v_total_obtenido + v_nota_ponderada;
                            END IF;

                        EXCEPTION
                            WHEN NO_DATA_FOUND THEN
                                NULL;
                        END;
                    END LOOP;
                END;

            ELSE
                DECLARE
                    v_es_correcta VARCHAR2(1);
                    v_id_resp NUMBER;
                BEGIN
                    v_total_posible := v_total_posible + pr.VALOR_NOTA;

                    SELECT ID_RESPUESTA INTO v_id_resp
                    FROM RESPUESTA_ESTUDIANTE
                    WHERE ID_PRESENTACION = p_id_presentacion
                      AND ID_PREGUNTA = pr.ID_PREGUNTA;

                    SELECT ES_CORRECTA INTO v_es_correcta
                    FROM RESPUESTA
                    WHERE ID_RESPUESTA = v_id_resp;

                    IF v_es_correcta = 'S' THEN
                        v_total_obtenido := v_total_obtenido + pr.VALOR_NOTA;
                    END IF;

                EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                        NULL;
                END;
            END IF;
        END LOOP;

        IF v_total_posible > 0 THEN
            v_calificacion := ROUND((v_total_obtenido * 5) / v_total_posible, 2);
        ELSE
            v_calificacion := 0;
        END IF;

        UPDATE PRESENTACION_EXAMEN
        SET CALIFICACION = v_calificacion,
            ESTADO = 'FINALIZADO'
        WHERE ID_PRESENTACION = p_id_presentacion;

        p_estado := 'OK';

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_estado := 'NO_ENCONTRADO';
        WHEN OTHERS THEN
            p_estado := 'ERROR';
    END CALIFICAR_EXAMEN_AUTOMATICO;

    PROCEDURE FINALIZAR_EXAMEN (
        p_id_presentacion IN NUMBER,
        p_estado OUT VARCHAR2
    )
    AS
    BEGIN
        UPDATE PRESENTACION_EXAMEN
        SET FECHA_HORA_FIN = SYSDATE
        WHERE ID_PRESENTACION = p_id_presentacion;

        CALIFICAR_EXAMEN_AUTOMATICO(p_id_presentacion, p_estado);
        COMMIT;

    EXCEPTION
        WHEN OTHERS THEN
            p_estado := 'ERROR_FINALIZAR';
            ROLLBACK;
    END FINALIZAR_EXAMEN;

    PROCEDURE CREAR_PRESENTACION_EXAMEN (
        p_id_examen     IN EXAMEN.ID_EXAMEN%TYPE,
        p_id_estudiante IN ESTUDIANTE.ID_ESTUDIANTE%TYPE
    ) AS
        v_id_grupo             EXAMEN.ID_GRUPO%TYPE;
        v_fecha_inicio         EXAMEN.FECHA_INICIO%TYPE;
        v_fecha_fin            EXAMEN.FECHA_FIN%TYPE;
        v_tiempo_limite        EXAMEN.TIEMPO_LIMITE%TYPE;
        v_estudiante_en_grupo  NUMBER;
    BEGIN
        -- Obtener datos del examen
        SELECT ID_GRUPO, FECHA_INICIO, FECHA_FIN, TIEMPO_LIMITE
        INTO v_id_grupo, v_fecha_inicio, v_fecha_fin, v_tiempo_limite
        FROM EXAMEN
        WHERE ID_EXAMEN = p_id_examen;

        -- Validar que el estudiante pertenece al grupo
        SELECT COUNT(*)
        INTO v_estudiante_en_grupo
        FROM ESTUDIANTE_GRUPO
        WHERE ID_ESTUDIANTE = p_id_estudiante AND ID_GRUPO = v_id_grupo;

        IF v_estudiante_en_grupo = 0 THEN
            RAISE_APPLICATION_ERROR(-20001, 'El estudiante no pertenece al grupo de este examen.');
        END IF;

        -- Validar rango de fechas
        IF SYSDATE < v_fecha_inicio OR SYSDATE > v_fecha_fin THEN
            RAISE_APPLICATION_ERROR(-20002, 'No se puede presentar el examen fuera del rango de fechas permitido.');
        END IF;

        -- Insertar la presentación del examen
        INSERT INTO PRESENTACION_EXAMEN (
            ID_PRESENTACION,
            ID_EXAMEN,
            ID_ESTUDIANTE,
            FECHA_HORA_INICIO,
            FECHA_HORA_FIN
        ) VALUES (
            SEQ_PRESENTACION_EXAMEN.NEXTVAL,
            p_id_examen,
            p_id_estudiante,
            SYSDATE,
            SYSDATE + v_tiempo_limite / 1440
        );

        COMMIT;
    END CREAR_PRESENTACION_EXAMEN;

    -- NUEVA IMPLEMENTACIÓN
    FUNCTION OBTENER_CALIFICACION (
        p_id_presentacion IN NUMBER
    ) RETURN NUMBER
    IS
        v_calificacion NUMBER(5,2);
    BEGIN
        SELECT CALIFICACION
        INTO v_calificacion
        FROM PRESENTACION_EXAMEN
        WHERE ID_PRESENTACION = p_id_presentacion;
        
        RETURN v_calificacion;
        
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN -1;
        WHEN OTHERS THEN
            RETURN -1;
    END OBTENER_CALIFICACION;

    -- IMPLEMENTACIÓN CONTAR INTENTOS REALIZADOS
    FUNCTION CONTAR_INTENTOS_REALIZADOS (
        p_id_estudiante IN NUMBER,
        p_id_examen IN NUMBER
    ) RETURN NUMBER
    IS
        v_total NUMBER;
    BEGIN
        SELECT COUNT(*)
        INTO v_total
        FROM PRESENTACION_EXAMEN
        WHERE ID_ESTUDIANTE = p_id_estudiante 
          AND ID_EXAMEN = p_id_examen 
          AND UPPER(ESTADO) = 'FINALIZADO';
        
        RETURN v_total;
        
    EXCEPTION
        WHEN OTHERS THEN
            RETURN 0;
    END CONTAR_INTENTOS_REALIZADOS;

    -- IMPLEMENTACIÓN OBTENER INTENTOS PERMITIDOS
    FUNCTION OBTENER_INTENTOS_PERMITIDOS (
        p_id_examen IN NUMBER
    ) RETURN NUMBER
    IS
        v_intentos_permitidos NUMBER;
    BEGIN
        SELECT INTENTOS_PERMITIDOS
        INTO v_intentos_permitidos
        FROM EXAMEN
        WHERE ID_EXAMEN = p_id_examen;
        
        RETURN v_intentos_permitidos;
        
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN 1;
        WHEN OTHERS THEN
            RETURN 1;
    END OBTENER_INTENTOS_PERMITIDOS;

    -- IMPLEMENTACIÓN EXAMEN TIENE PRESENTACIONES
    FUNCTION EXAMEN_TIENE_PRESENTACIONES (
        p_id_examen IN NUMBER
    ) RETURN NUMBER
    IS
        v_count NUMBER;
    BEGIN
        SELECT COUNT(*)
        INTO v_count
        FROM PRESENTACION_EXAMEN
        WHERE ID_EXAMEN = p_id_examen;
        
        RETURN v_count;
        
    EXCEPTION
        WHEN OTHERS THEN
            RETURN 0;
    END EXAMEN_TIENE_PRESENTACIONES;

    -- IMPLEMENTACIÓN OBTENER ESTADÍSTICAS DE PRESENTACIONES
    FUNCTION OBTENER_ESTADISTICAS_PRESENTACIONES (
        p_id_examen IN NUMBER,
        p_total OUT NUMBER,
        p_finalizados OUT NUMBER,
        p_en_progreso OUT NUMBER
    ) RETURN VARCHAR2
    IS
    BEGIN
        SELECT 
            COUNT(*) as TOTAL,
            COUNT(CASE WHEN UPPER(ESTADO) = 'FINALIZADO' THEN 1 END) as FINALIZADOS,
            COUNT(CASE WHEN UPPER(ESTADO) = 'EN_PROGRESO' THEN 1 END) as EN_PROGRESO
        INTO p_total, p_finalizados, p_en_progreso
        FROM PRESENTACION_EXAMEN 
        WHERE ID_EXAMEN = p_id_examen;
        
        RETURN 'OK';
        
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_total := 0;
            p_finalizados := 0;
            p_en_progreso := 0;
            RETURN 'NO_DATA';
        WHEN OTHERS THEN
            p_total := 0;
            p_finalizados := 0;
            p_en_progreso := 0;
            RETURN 'ERROR';
    END OBTENER_ESTADISTICAS_PRESENTACIONES;

END PKG_PRESENTACION_EXAMEN;

/
--------------------------------------------------------------------------------------------

create or replace NONEDITIONABLE PROCEDURE         "AGREGAR_OPCIONES_RESPUESTA" (
  p_id_pregunta IN NUMBER,
  p_opciones    IN SYS.ODCIVARCHAR2LIST
) IS
  v_id_respuesta NUMBER;
BEGIN
  FOR i IN 1 .. p_opciones.COUNT LOOP
    SELECT SEQ_RESPUESTA.NEXTVAL INTO v_id_respuesta FROM DUAL;

    INSERT INTO RESPUESTA (
      ID_RESPUESTA,
      ID_PREGUNTA,
      TEXTO,
      ES_CORRECTA
    ) VALUES (
      v_id_respuesta,
      p_id_pregunta,
      p_opciones(i),
      'N'
    );
  END LOOP;
END;

/
---------------------------------------------------------------------

create or replace PROCEDURE GENERAR_ESTADISTICAS_ESTUDIANTE (
    p_id_estudiante IN NUMBER
) AS
    v_total_examenes NUMBER := 0;
    v_correctas NUMBER := 0;
    v_incorrectas NUMBER := 0;
    v_promedio NUMBER := 0;
    v_maxima NUMBER := 0;
    v_minima NUMBER := 0;
BEGIN
    -- Obtener estadísticas desde la vista
    SELECT 
        COUNT(DISTINCT pres.id_presentacion),
        SUM(CASE WHEN r.es_correcta = 'S' THEN 1 ELSE 0 END),
        SUM(CASE WHEN r.es_correcta = 'N' THEN 1 ELSE 0 END),
        NVL(AVG(pres.calificacion), 0),
        NVL(MAX(pres.calificacion), 0),
        NVL(MIN(pres.calificacion), 0)
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

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
        RAISE;
END;
/
---------------------------------------------------------

create or replace PROCEDURE GENERAR_ESTADISTICAS_EXAMEN (
    p_id_examen IN NUMBER
) AS
    v_total_presentaciones NUMBER := 0;
    v_promedio NUMBER := 0;
    v_maxima NUMBER := 0;
    v_minima NUMBER := 0;
BEGIN
    SELECT 
        COUNT(*), 
        NVL(AVG(calificacion), 0), 
        NVL(MAX(calificacion), 0), 
        NVL(MIN(calificacion), 0)
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

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
        RAISE;
END;
/

---------------------------------------------------------------------------------------

create or replace PROCEDURE GENERAR_ESTADISTICAS_GRUPO (
    p_id_grupo IN NUMBER
) AS
    v_total_examenes NUMBER := 0;
    v_promedio NUMBER := 0;
    v_maxima NUMBER := 0;
    v_minima NUMBER := 0;
BEGIN
    SELECT COUNT(DISTINCT pres.id_presentacion),
           NVL(AVG(pres.calificacion), 0),
           NVL(MAX(pres.calificacion), 0),
           NVL(MIN(pres.calificacion), 0)
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

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
        RAISE;
END;
/
----------------------------------------------------------------------------

create or replace PROCEDURE GENERAR_ESTADISTICAS_PREGUNTA (
    p_id_pregunta IN NUMBER
) AS
    v_total_respuestas NUMBER := 0;
    v_correctas NUMBER := 0;
    v_incorrectas NUMBER := 0;
    v_porcentaje_correctas NUMBER := 0;
BEGIN
    -- Obtener estadísticas de la pregunta
    SELECT NVL(COUNT(*), 0), 
           NVL(SUM(CASE WHEN r.es_correcta = 'S' THEN 1 ELSE 0 END), 0), 
           NVL(SUM(CASE WHEN r.es_correcta = 'N' THEN 1 ELSE 0 END), 0)
    INTO v_total_respuestas, v_correctas, v_incorrectas
    FROM RESPUESTA_ESTUDIANTE re
    JOIN RESPUESTA r ON re.id_respuesta = r.id_respuesta
    WHERE re.id_pregunta = p_id_pregunta;

    -- Calcular porcentaje de aciertos
    IF v_total_respuestas > 0 THEN
        v_porcentaje_correctas := ROUND((v_correctas / v_total_respuestas) * 100, 2);
    ELSE
        v_porcentaje_correctas := 0;
    END IF;

    -- Mostrar en consola
    DBMS_OUTPUT.PUT_LINE('Total respuestas: ' || v_total_respuestas);
    DBMS_OUTPUT.PUT_LINE('Respuestas correctas: ' || v_correctas);
    DBMS_OUTPUT.PUT_LINE('Respuestas incorrectas: ' || v_incorrectas);
    DBMS_OUTPUT.PUT_LINE('Porcentaje de aciertos: ' || v_porcentaje_correctas || '%');

    -- Guardar en tabla de estadísticas
    MERGE INTO ESTADISTICAS_PREGUNTA ep
    USING (SELECT p_id_pregunta AS id_pregunta FROM dual) src
    ON (ep.id_pregunta = src.id_pregunta)
    WHEN MATCHED THEN
        UPDATE SET 
            total_respuestas = v_total_respuestas,
            correctas = v_correctas,
            incorrectas = v_incorrectas,
            porcentaje_correctas = v_porcentaje_correctas
    WHEN NOT MATCHED THEN
        INSERT (id_pregunta, total_respuestas, correctas, incorrectas, porcentaje_correctas)
        VALUES (p_id_pregunta, v_total_respuestas, v_correctas, v_incorrectas, v_porcentaje_correctas);

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
        RAISE;
END;
/
----------------------------------------------------------------

create or replace PROCEDURE GENERAR_ESTADISTICAS_TEMA (
    p_id_tema IN NUMBER
) AS
    v_total_respuestas NUMBER := 0;
    v_correctas NUMBER := 0;
    v_incorrectas NUMBER := 0;
    v_promedio_nota NUMBER := 0;
    v_max_nota NUMBER := 0;
    v_min_nota NUMBER := 0;
BEGIN
    SELECT COUNT(re.id_respuesta_estudiante),
           SUM(CASE WHEN r.es_correcta = 'S' THEN 1 ELSE 0 END),
           SUM(CASE WHEN r.es_correcta = 'N' THEN 1 ELSE 0 END),
           NVL(AVG(pres.calificacion), 0),
           NVL(MAX(pres.calificacion), 0),
           NVL(MIN(pres.calificacion), 0)
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

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
        RAISE;
END;
/
----------------------------------------------------------------------------

create or replace NONEDITIONABLE PROCEDURE         "LOGIN_USUARIO" (
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
------------------------------------------------------------

create or replace NONEDITIONABLE PROCEDURE         "OBTENER_ESTUDIANTE_COMPLETO" (
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
----------------------------------------------------------------------

create or replace NONEDITIONABLE PROCEDURE         "OBTENER_TEMAS" (p_temas OUT TEMA_TABLA) IS
BEGIN
    SELECT TEMA_OBJ(ID_TEMA, NOMBRE)
    BULK COLLECT INTO p_temas
    FROM TEMA
    ORDER BY NOMBRE ASC;
END;
/
-----------------------------------------------------------------------

create or replace NONEDITIONABLE PROCEDURE         "REGISTRAR_PRESENTACION" (
    p_id_examen      IN PRESENTACION_EXAMEN.ID_EXAMEN%TYPE,
    p_id_estudiante  IN PRESENTACION_EXAMEN.ID_ESTUDIANTE%TYPE,
    p_id_presentacion OUT PRESENTACION_EXAMEN.ID_PRESENTACION%TYPE
)
AS
    v_intentos        NUMBER := 0;
    v_max_intentos    NUMBER := 1;
BEGIN
    -- Verificar cu?ntos intentos ya tiene el estudiante para este examen
    SELECT COUNT(*) INTO v_intentos
    FROM PRESENTACION_EXAMEN
    WHERE ID_EXAMEN = p_id_examen AND ID_ESTUDIANTE = p_id_estudiante;

    -- Obtener la cantidad m?xima de intentos permitidos para el examen
    SELECT INTENTOS_PERMITIDOS INTO v_max_intentos
    FROM EXAMEN
    WHERE ID_EXAMEN = p_id_examen;

    IF v_intentos >= v_max_intentos THEN
        RAISE_APPLICATION_ERROR(-20005, 'Ya alcanz? el n?mero m?ximo de intentos permitidos para este examen.');
    END IF;

    -- Insertar nuevo intento de presentaci?n
    INSERT INTO PRESENTACION_EXAMEN (
        ID_PRESENTACION,
        ID_EXAMEN,
        ID_ESTUDIANTE,
        FECHA_PRESENTACION
    ) VALUES (
        SEQ_PRESENTACION_EXAMEN.NEXTVAL,
        p_id_examen,
        p_id_estudiante,
        SYSDATE
    ) RETURNING ID_PRESENTACION INTO p_id_presentacion;

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END REGISTRAR_PRESENTACION;

/
---------------------------------------------------------------------------------------

create or replace NONEDITIONABLE PROCEDURE         "REGISTRAR_RESPUESTA" (
    p_id_presentacion   IN PRESENTACION_EXAMEN.ID_PRESENTACION%TYPE,
    p_id_pregunta       IN PREGUNTA.ID_PREGUNTA%TYPE,
    p_id_opcion         IN RESPUESTA.ID_RESPUESTA%TYPE
) AS
    v_fecha_fin DATE;
BEGIN
    -- Obtener la fecha l?mite de la presentaci?n
    SELECT FECHA_HORA_FIN
    INTO v_fecha_fin
    FROM PRESENTACION_EXAMEN
    WHERE ID_PRESENTACION = p_id_presentacion;

    -- Validar tiempo
    IF SYSDATE > v_fecha_fin THEN
        RAISE_APPLICATION_ERROR(-20003, 'El tiempo para responder el examen ha finalizado.');
    END IF;

    -- Insertar la respuesta con ID generado
    INSERT INTO RESPUESTA_ESTUDIANTE (
        ID_RESPUESTA_ESTUDIANTE,
        ID_PRESENTACION,
        ID_PREGUNTA,
        ID_RESPUESTA
    )
    VALUES (
        SEQ_RESPUESTA_ESTUDIANTE.NEXTVAL,
        p_id_presentacion,
        p_id_pregunta,
        p_id_opcion
    );

    COMMIT;
END;

/
---------------------------------------------------------------------------

create or replace NONEDITIONABLE TRIGGER "ROOT2"."TRG_BI_RESPUESTA_ESTUDIANTE" 
BEFORE INSERT ON RESPUESTA_ESTUDIANTE
FOR EACH ROW
BEGIN
    IF :NEW.ID_RESPUESTA_ESTUDIANTE IS NULL THEN
        :NEW.ID_RESPUESTA_ESTUDIANTE := SEQ_RESPUESTA_ESTUDIANTE.NEXTVAL;
    END IF;
END;
/
----------------------------------------------------------------------------------

create or replace NONEDITIONABLE TRIGGER "ROOT2"."TRG_CALIFICAR_AL_FINALIZAR" 
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
            -- CORRECCIÓN: Llamar al procedimiento dentro del package
            PKG_PRESENTACION_EXAMEN.CALIFICAR_EXAMEN_AUTOMATICO(:NEW.ID_PRESENTACION, v_estado);
        END IF;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            NULL; -- Silenciar la excepción si no se encuentra la presentación aún
        WHEN OTHERS THEN
            NULL; -- (opcional) evita que cualquier otro error detenga la inserción
    END;
END;
/
--------------------------------------------------------------------------------------------------------

create or replace TRIGGER "ROOT2"."TRG_PREGUNTA_ID" 
BEFORE INSERT ON PREGUNTA
FOR EACH ROW
BEGIN
    SELECT SEQ_ID_PREGUNTA.NEXTVAL INTO :NEW.ID_PREGUNTA FROM DUAL;
END;
/
-------------------------------------------------------------------------------

--------------------------------------------------------
--  DDL for Sequence SEQ_EXAMEN
--------------------------------------------------------

   CREATE SEQUENCE  "ROOT2"."SEQ_EXAMEN"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 31 NOCACHE  NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence SEQ_GRUPO
--------------------------------------------------------

   CREATE SEQUENCE  "ROOT2"."SEQ_GRUPO"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 5 NOCACHE  NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence SEQ_ID_PREGUNTA
--------------------------------------------------------

   CREATE SEQUENCE  "ROOT2"."SEQ_ID_PREGUNTA"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 541 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence SEQ_PREGUNTA
--------------------------------------------------------

   CREATE SEQUENCE  "ROOT2"."SEQ_PREGUNTA"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 241 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence SEQ_PRESENTACION_EXAMEN
--------------------------------------------------------

   CREATE SEQUENCE  "ROOT2"."SEQ_PRESENTACION_EXAMEN"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 63 NOCACHE  NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence SEQ_RESPUESTA
--------------------------------------------------------

   CREATE SEQUENCE  "ROOT2"."SEQ_RESPUESTA"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 361 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence SEQ_RESPUESTA_ESTUDIANTE
--------------------------------------------------------

   CREATE SEQUENCE  "ROOT2"."SEQ_RESPUESTA_ESTUDIANTE"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 147 NOCACHE  NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence SEQ_TEMA
--------------------------------------------------------

   CREATE SEQUENCE  "ROOT2"."SEQ_TEMA"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
