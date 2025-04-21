--------------------------------------------------------
-- Archivo creado  - sábado-marzo-22-2025   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Sequence SEQ_EXAMEN
--------------------------------------------------------

   CREATE SEQUENCE  "ROOT"."SEQ_EXAMEN"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 12 NOCACHE  NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence SEQ_ID_PREGUNTA
--------------------------------------------------------

   CREATE SEQUENCE  "ROOT"."SEQ_ID_PREGUNTA"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 301 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence SEQ_PREGUNTA
--------------------------------------------------------

   CREATE SEQUENCE  "ROOT"."SEQ_PREGUNTA"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 61 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence SEQ_RESPUESTA
--------------------------------------------------------

   CREATE SEQUENCE  "ROOT"."SEQ_RESPUESTA"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 61 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Table BANCO_PREGUNTAS
--------------------------------------------------------

  CREATE TABLE "ROOT"."BANCO_PREGUNTAS" 
   (	"ID_BANCO" NUMBER, 
	"NOMBRE" VARCHAR2(100 BYTE)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table ESTADISTICAS_EXAMEN
--------------------------------------------------------

  CREATE TABLE "ROOT"."ESTADISTICAS_EXAMEN" 
   (	"ID_ESTADISTICA" NUMBER, 
	"ID_EXAMEN" NUMBER, 
	"TOTAL_PRESENTADOS" NUMBER, 
	"PROMEDIO_NOTA" NUMBER, 
	"MAX_NOTA" NUMBER, 
	"MIN_NOTA" NUMBER
   ) SEGMENT CREATION DEFERRED 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table ESTADISTICAS_PREGUNTA
--------------------------------------------------------

  CREATE TABLE "ROOT"."ESTADISTICAS_PREGUNTA" 
   (	"ID_PREGUNTA" NUMBER, 
	"PORCENTAJE_CORRECTAS" NUMBER, 
	"TOTAL_RESPUESTAS" NUMBER, 
	"CORRECTAS" NUMBER, 
	"INCORRECTAS" NUMBER
   ) SEGMENT CREATION DEFERRED 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table EXAMEN
--------------------------------------------------------

  CREATE TABLE "ROOT"."EXAMEN" 
   (	"ID_EXAMEN" NUMBER DEFAULT "ROOT"."SEQ_EXAMEN"."NEXTVAL", 
	"NOMBRE" VARCHAR2(100 BYTE), 
	"DESCRIPCION" VARCHAR2(255 BYTE), 
	"FECHA_INICIO" DATE, 
	"FECHA_FIN" DATE, 
	"TIEMPO_LIMITE" NUMBER, 
	"ID_DOCENTE" NUMBER, 
	"NUMERO_PREGUNTAS" NUMBER, 
	"MODO_SELECCION" VARCHAR2(20 BYTE), 
	"TIEMPO_POR_PREGUNTA" NUMBER
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table EXAMEN_PREGUNTA
--------------------------------------------------------

  CREATE TABLE "ROOT"."EXAMEN_PREGUNTA" 
   (	"ID_EXAMEN" NUMBER, 
	"ID_PREGUNTA" NUMBER
   ) SEGMENT CREATION DEFERRED 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table PREGUNTA
--------------------------------------------------------

  CREATE TABLE "ROOT"."PREGUNTA" 
   (	"ID_PREGUNTA" NUMBER DEFAULT "ROOT"."SEQ_PREGUNTA"."NEXTVAL", 
	"TEXTO" CLOB, 
	"TIPO" VARCHAR2(50 BYTE), 
	"ID_BANCO" NUMBER
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" 
 LOB ("TEXTO") STORE AS SECUREFILE (
  TABLESPACE "USERS" ENABLE STORAGE IN ROW CHUNK 8192
  NOCACHE LOGGING  NOCOMPRESS  KEEP_DUPLICATES 
  STORAGE(INITIAL 106496 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)) ;
--------------------------------------------------------
--  DDL for Table PRESENTACION_EXAMEN
--------------------------------------------------------

  CREATE TABLE "ROOT"."PRESENTACION_EXAMEN" 
   (	"ID_PRESENTACION" NUMBER, 
	"ID_EXAMEN" NUMBER, 
	"ID_ESTUDIANTE" NUMBER, 
	"FECHA_PRESENTACION" DATE, 
	"CALIFICACION" NUMBER
   ) SEGMENT CREATION DEFERRED 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table RESPUESTA
--------------------------------------------------------

  CREATE TABLE "ROOT"."RESPUESTA" 
   (	"ID_RESPUESTA" NUMBER, 
	"ID_PREGUNTA" NUMBER, 
	"TEXTO" VARCHAR2(255 BYTE), 
	"ES_CORRECTA" VARCHAR2(1 BYTE)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table RESPUESTA_ESTUDIANTE
--------------------------------------------------------

  CREATE TABLE "ROOT"."RESPUESTA_ESTUDIANTE" 
   (	"ID_RESPUESTA_ESTUDIANTE" NUMBER, 
	"ID_PRESENTACION" NUMBER, 
	"ID_PREGUNTA" NUMBER, 
	"ID_RESPUESTA" NUMBER
   ) SEGMENT CREATION DEFERRED 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table USUARIO
--------------------------------------------------------

  CREATE TABLE "ROOT"."USUARIO" 
   (	"ID_USUARIO" NUMBER, 
	"NOMBRE" VARCHAR2(100 BYTE), 
	"CORREO" VARCHAR2(100 BYTE), 
	"CONTRASENA" VARCHAR2(100 BYTE), 
	"TIPO_USUARIO" VARCHAR2(20 BYTE)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index PK_EXAMEN_PREGUNTA
--------------------------------------------------------

  CREATE UNIQUE INDEX "ROOT"."PK_EXAMEN_PREGUNTA" ON "ROOT"."EXAMEN_PREGUNTA" ("ID_EXAMEN", "ID_PREGUNTA") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Trigger TRG_PREGUNTA_ID
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE TRIGGER "ROOT"."TRG_PREGUNTA_ID" 
BEFORE INSERT ON PREGUNTA
FOR EACH ROW
BEGIN
    SELECT SEQ_ID_PREGUNTA.NEXTVAL INTO :NEW.ID_PREGUNTA FROM DUAL;
END;

/
ALTER TRIGGER "ROOT"."TRG_PREGUNTA_ID" ENABLE;
--------------------------------------------------------
--  DDL for Procedure GENERAR_ESTADISTICAS_ESTUDIANTE
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "ROOT"."GENERAR_ESTADISTICAS_ESTUDIANTE" (
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

    DBMS_OUTPUT.PUT_LINE('Total exámenes presentados: ' || v_total_examenes);
    DBMS_OUTPUT.PUT_LINE('Promedio de calificaciones: ' || v_promedio);
    DBMS_OUTPUT.PUT_LINE('Calificación máxima: ' || v_maxima);
    DBMS_OUTPUT.PUT_LINE('Calificación mínima: ' || v_minima);
END Generar_Estadisticas_Estudiante;

/
--------------------------------------------------------
--  DDL for Procedure GENERAR_ESTADISTICAS_EXAMEN
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "ROOT"."GENERAR_ESTADISTICAS_EXAMEN" (
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
    DBMS_OUTPUT.PUT_LINE('Nota máxima: ' || v_maxima);
    DBMS_OUTPUT.PUT_LINE('Nota mínima: ' || v_minima);
END Generar_Estadisticas_Examen;

/
--------------------------------------------------------
--  DDL for Procedure GENERAR_ESTADISTICAS_PREGUNTA
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "ROOT"."GENERAR_ESTADISTICAS_PREGUNTA" (
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
--  Constraints for Table PRESENTACION_EXAMEN
--------------------------------------------------------

  ALTER TABLE "ROOT"."PRESENTACION_EXAMEN" ADD PRIMARY KEY ("ID_PRESENTACION")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table RESPUESTA_ESTUDIANTE
--------------------------------------------------------

  ALTER TABLE "ROOT"."RESPUESTA_ESTUDIANTE" ADD PRIMARY KEY ("ID_RESPUESTA_ESTUDIANTE")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table RESPUESTA
--------------------------------------------------------

  ALTER TABLE "ROOT"."RESPUESTA" ADD PRIMARY KEY ("ID_RESPUESTA")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table ESTADISTICAS_PREGUNTA
--------------------------------------------------------

  ALTER TABLE "ROOT"."ESTADISTICAS_PREGUNTA" ADD PRIMARY KEY ("ID_PREGUNTA")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table PREGUNTA
--------------------------------------------------------

  ALTER TABLE "ROOT"."PREGUNTA" MODIFY ("ID_PREGUNTA" NOT NULL ENABLE);
  ALTER TABLE "ROOT"."PREGUNTA" ADD PRIMARY KEY ("ID_PREGUNTA")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table ESTADISTICAS_EXAMEN
--------------------------------------------------------

  ALTER TABLE "ROOT"."ESTADISTICAS_EXAMEN" ADD PRIMARY KEY ("ID_ESTADISTICA")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table EXAMEN_PREGUNTA
--------------------------------------------------------

  ALTER TABLE "ROOT"."EXAMEN_PREGUNTA" ADD CONSTRAINT "PK_EXAMEN_PREGUNTA" PRIMARY KEY ("ID_EXAMEN", "ID_PREGUNTA")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table EXAMEN
--------------------------------------------------------

  ALTER TABLE "ROOT"."EXAMEN" ADD PRIMARY KEY ("ID_EXAMEN")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
  ALTER TABLE "ROOT"."EXAMEN" ADD CHECK (modo_seleccion IN ('Manual', 'Aleatorio')) ENABLE;
--------------------------------------------------------
--  Constraints for Table USUARIO
--------------------------------------------------------

  ALTER TABLE "ROOT"."USUARIO" ADD CHECK (tipo_usuario IN ('Docente', 'Estudiante')) ENABLE;
  ALTER TABLE "ROOT"."USUARIO" ADD PRIMARY KEY ("ID_USUARIO")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
  ALTER TABLE "ROOT"."USUARIO" ADD UNIQUE ("CORREO")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table BANCO_PREGUNTAS
--------------------------------------------------------

  ALTER TABLE "ROOT"."BANCO_PREGUNTAS" ADD PRIMARY KEY ("ID_BANCO")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table ESTADISTICAS_EXAMEN
--------------------------------------------------------

  ALTER TABLE "ROOT"."ESTADISTICAS_EXAMEN" ADD FOREIGN KEY ("ID_EXAMEN")
	  REFERENCES "ROOT"."EXAMEN" ("ID_EXAMEN") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table ESTADISTICAS_PREGUNTA
--------------------------------------------------------

  ALTER TABLE "ROOT"."ESTADISTICAS_PREGUNTA" ADD FOREIGN KEY ("ID_PREGUNTA")
	  REFERENCES "ROOT"."PREGUNTA" ("ID_PREGUNTA") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table EXAMEN
--------------------------------------------------------

  ALTER TABLE "ROOT"."EXAMEN" ADD FOREIGN KEY ("ID_DOCENTE")
	  REFERENCES "ROOT"."USUARIO" ("ID_USUARIO") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table EXAMEN_PREGUNTA
--------------------------------------------------------

  ALTER TABLE "ROOT"."EXAMEN_PREGUNTA" ADD FOREIGN KEY ("ID_EXAMEN")
	  REFERENCES "ROOT"."EXAMEN" ("ID_EXAMEN") ENABLE;
  ALTER TABLE "ROOT"."EXAMEN_PREGUNTA" ADD FOREIGN KEY ("ID_PREGUNTA")
	  REFERENCES "ROOT"."PREGUNTA" ("ID_PREGUNTA") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table PREGUNTA
--------------------------------------------------------

  ALTER TABLE "ROOT"."PREGUNTA" ADD FOREIGN KEY ("ID_BANCO")
	  REFERENCES "ROOT"."BANCO_PREGUNTAS" ("ID_BANCO") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table PRESENTACION_EXAMEN
--------------------------------------------------------

  ALTER TABLE "ROOT"."PRESENTACION_EXAMEN" ADD FOREIGN KEY ("ID_EXAMEN")
	  REFERENCES "ROOT"."EXAMEN" ("ID_EXAMEN") ENABLE;
  ALTER TABLE "ROOT"."PRESENTACION_EXAMEN" ADD FOREIGN KEY ("ID_ESTUDIANTE")
	  REFERENCES "ROOT"."USUARIO" ("ID_USUARIO") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table RESPUESTA
--------------------------------------------------------

  ALTER TABLE "ROOT"."RESPUESTA" ADD FOREIGN KEY ("ID_PREGUNTA")
	  REFERENCES "ROOT"."PREGUNTA" ("ID_PREGUNTA") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table RESPUESTA_ESTUDIANTE
--------------------------------------------------------

  ALTER TABLE "ROOT"."RESPUESTA_ESTUDIANTE" ADD FOREIGN KEY ("ID_PRESENTACION")
	  REFERENCES "ROOT"."PRESENTACION_EXAMEN" ("ID_PRESENTACION") ENABLE;
  ALTER TABLE "ROOT"."RESPUESTA_ESTUDIANTE" ADD FOREIGN KEY ("ID_PREGUNTA")
	  REFERENCES "ROOT"."PREGUNTA" ("ID_PREGUNTA") ENABLE;
  ALTER TABLE "ROOT"."RESPUESTA_ESTUDIANTE" ADD FOREIGN KEY ("ID_RESPUESTA")
	  REFERENCES "ROOT"."RESPUESTA" ("ID_RESPUESTA") ENABLE;
