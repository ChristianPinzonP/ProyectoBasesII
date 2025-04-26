--------------------------------------------------------
-- Archivo creado  - s?bado-marzo-22-2025   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Sequence SEQ_EXAMEN
--------------------------------------------------------

   CREATE SEQUENCE  "ROOT2"."SEQ_EXAMEN"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 12 NOCACHE  NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence SEQ_ID_PREGUNTA
--------------------------------------------------------

   CREATE SEQUENCE  "ROOT2"."SEQ_ID_PREGUNTA"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 301 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence SEQ_PREGUNTA
--------------------------------------------------------

   CREATE SEQUENCE  "ROOT2"."SEQ_PREGUNTA"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 61 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence SEQ_RESPUESTA
--------------------------------------------------------

   CREATE SEQUENCE  "ROOT2"."SEQ_RESPUESTA"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 61 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Table TEMA
--------------------------------------------------------
CREATE TABLE "ROOT2"."TEMA" ("ID_TEMA" NUMBER, "NOMBRE" VARCHAR2(100 BYTE))
     SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table BANCO_PREGUNTAS
--------------------------------------------------------

  CREATE TABLE "ROOT2"."BANCO_PREGUNTAS" 
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

  CREATE TABLE "ROOT2"."ESTADISTICAS_EXAMEN" 
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

  CREATE TABLE "ROOT2"."ESTADISTICAS_PREGUNTA" 
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

  CREATE TABLE "ROOT2"."EXAMEN" 
   (	"ID_EXAMEN" NUMBER DEFAULT "ROOT2"."SEQ_EXAMEN"."NEXTVAL", 
	"NOMBRE" VARCHAR2(100 BYTE), 
	"DESCRIPCION" VARCHAR2(255 BYTE), 
	"FECHA_INICIO" DATE, 
	"FECHA_FIN" DATE, 
	"TIEMPO_LIMITE" NUMBER, 
	"ID_DOCENTE" NUMBER, 
	"NUMERO_PREGUNTAS" NUMBER, 
	"MODO_SELECCION" VARCHAR2(20 BYTE), 
	"TIEMPO_POR_PREGUNTA" NUMBER,
    "ID_TEMA" NUMBER
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

  CREATE TABLE "ROOT2"."EXAMEN_PREGUNTA" 
   (	"ID_EXAMEN" NUMBER, 
	"ID_PREGUNTA" NUMBER
   ) SEGMENT CREATION DEFERRED 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table PREGUNTA
--------------------------------------------------------

  CREATE TABLE "ROOT2"."PREGUNTA" 
   (	"ID_PREGUNTA" NUMBER DEFAULT "ROOT2"."SEQ_PREGUNTA"."NEXTVAL", 
	"TEXTO" CLOB, 
	"TIPO" VARCHAR2(50 BYTE), 
	"ID_BANCO" NUMBER,
    "ID_TEMA" NUMBER
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

  CREATE TABLE "ROOT2"."PRESENTACION_EXAMEN" 
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

  CREATE TABLE "ROOT2"."RESPUESTA" 
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

  CREATE TABLE "ROOT2"."RESPUESTA_ESTUDIANTE" 
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

  CREATE TABLE "ROOT2"."USUARIO" 
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
  
-----------------------------------------
REM INSERTING INTO ROOT2.TEMA
SET DEFINE OFF;
INSERT INTO ROOT2.TEMA(ID_TEMA, NOMBRE) VALUES ('1','Bases de Datos');
INSERT INTO ROOT2.TEMA(ID_TEMA, NOMBRE) VALUES ('2','Redes');
INSERT INTO ROOT2.TEMA(ID_TEMA, NOMBRE) VALUES ('3','Ciberseguridad');
SELECT * FROM TEMA;
REM INSERTING into ROOT2.BANCO_PREGUNTAS
SET DEFINE OFF;
Insert into ROOT2.BANCO_PREGUNTAS (ID_BANCO,NOMBRE) values ('1','Banco de Preguntas General');
REM INSERTING into ROOT2.ESTADISTICAS_EXAMEN
SET DEFINE OFF;
REM INSERTING into ROOT2.ESTADISTICAS_PREGUNTA
SET DEFINE OFF;
REM INSERTING into ROOT2.EXAMEN
SET DEFINE OFF;
Insert into ROOT2.EXAMEN (ID_EXAMEN,NOMBRE,DESCRIPCION,FECHA_INICIO,FECHA_FIN,TIEMPO_LIMITE,ID_DOCENTE,NUMERO_PREGUNTAS,MODO_SELECCION,TIEMPO_POR_PREGUNTA) values ('8','Prueba 2','Prueba 2 para primi',to_date('12/03/25','DD/MM/RR'),to_date('13/03/25','DD/MM/RR'),'20','1',null,null,null);
Insert into ROOT2.EXAMEN (ID_EXAMEN,NOMBRE,DESCRIPCION,FECHA_INICIO,FECHA_FIN,TIEMPO_LIMITE,ID_DOCENTE,NUMERO_PREGUNTAS,MODO_SELECCION,TIEMPO_POR_PREGUNTA) values ('11','Examen de Prueba','Descripci?n ',to_date('11/03/25','DD/MM/RR'),to_date('15/03/25','DD/MM/RR'),'20','1',null,null,null);
Insert into ROOT2.EXAMEN (ID_EXAMEN,NOMBRE,DESCRIPCION,FECHA_INICIO,FECHA_FIN,TIEMPO_LIMITE,ID_DOCENTE,NUMERO_PREGUNTAS,MODO_SELECCION,TIEMPO_POR_PREGUNTA) values ('9','Prueba 4','Prueba para Calculo',to_date('11/03/25','DD/MM/RR'),to_date('12/03/25','DD/MM/RR'),'20','1',null,null,null);
Insert into ROOT2.EXAMEN (ID_EXAMEN,NOMBRE,DESCRIPCION,FECHA_INICIO,FECHA_FIN,TIEMPO_LIMITE,ID_DOCENTE,NUMERO_PREGUNTAS,MODO_SELECCION,TIEMPO_POR_PREGUNTA) values ('7','Prueba 3','Pueba para Bases',to_date('09/03/25','DD/MM/RR'),to_date('10/03/25','DD/MM/RR'),'15','1',null,null,null);
REM INSERTING into ROOT2.EXAMEN_PREGUNTA
SET DEFINE OFF;
REM INSERTING into ROOT2.PREGUNTA
SET DEFINE OFF;
Insert into ROOT2.PREGUNTA (ID_PREGUNTA,TIPO,ID_BANCO) values ('265','Opci?n M?ltiple','1');
Insert into ROOT2.PREGUNTA (ID_PREGUNTA,TIPO,ID_BANCO) values ('263','Respuesta Corta','1');
Insert into ROOT2.PREGUNTA (ID_PREGUNTA,TIPO,ID_BANCO) values ('281','Verdadero/Falso','1');
REM INSERTING into ROOT2.PRESENTACION_EXAMEN
SET DEFINE OFF;
REM INSERTING into ROOT2.RESPUESTA
SET DEFINE OFF;
Insert into ROOT2.RESPUESTA (ID_RESPUESTA,ID_PREGUNTA,TEXTO,ES_CORRECTA) values ('21','263','Trump','S');
Insert into ROOT2.RESPUESTA (ID_RESPUESTA,ID_PREGUNTA,TEXTO,ES_CORRECTA) values ('45','265','blanca','N');
Insert into ROOT2.RESPUESTA (ID_RESPUESTA,ID_PREGUNTA,TEXTO,ES_CORRECTA) values ('46','265','verde','S');
Insert into ROOT2.RESPUESTA (ID_RESPUESTA,ID_PREGUNTA,TEXTO,ES_CORRECTA) values ('47','265','azul','S');
Insert into ROOT2.RESPUESTA (ID_RESPUESTA,ID_PREGUNTA,TEXTO,ES_CORRECTA) values ('48','265','morada','N');
Insert into ROOT2.RESPUESTA (ID_RESPUESTA,ID_PREGUNTA,TEXTO,ES_CORRECTA) values ('51','281','Verdadero','N');
Insert into ROOT2.RESPUESTA (ID_RESPUESTA,ID_PREGUNTA,TEXTO,ES_CORRECTA) values ('52','281','Falso','S');
REM INSERTING into ROOT2.RESPUESTA_ESTUDIANTE
SET DEFINE OFF;
REM INSERTING into ROOT2.USUARIO
SET DEFINE OFF;
Insert into ROOT2.USUARIO (ID_USUARIO,NOMBRE,CORREO,CONTRASENA,TIPO_USUARIO) values ('1','Docente Prueba','docente@ejemplo.com','1234','Docente');
--------------------------------------------------------
--  DDL for Index PK_EXAMEN_PREGUNTA
--------------------------------------------------------

  CREATE UNIQUE INDEX "ROOT2"."PK_EXAMEN_PREGUNTA" ON "ROOT2"."EXAMEN_PREGUNTA" ("ID_EXAMEN", "ID_PREGUNTA") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Trigger TRG_PREGUNTA_ID
--------------------------------------------------------

CREATE OR REPLACE EDITIONABLE TRIGGER "ROOT2"."TRG_PREGUNTA_ID" 
BEFORE INSERT ON PREGUNTA
FOR EACH ROW
BEGIN
    SELECT SEQ_ID_PREGUNTA.NEXTVAL INTO :NEW.ID_PREGUNTA FROM DUAL;
END;

/
ALTER TRIGGER "ROOT2"."TRG_PREGUNTA_ID" ENABLE;
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
--  Constraints for Table TEMA
--------------------------------------------------------

  ALTER TABLE "ROOT2"."TEMA" MODIFY ("ID_TEMA" NOT NULL ENABLE);
  ALTER TABLE "ROOT2"."TEMA" ADD PRIMARY KEY ("ID_TEMA")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "USERS"  ENABLE;

--------------------------------------------------------
--  Constraints for Table PRESENTACION_EXAMEN
--------------------------------------------------------

  ALTER TABLE "ROOT2"."PRESENTACION_EXAMEN" ADD PRIMARY KEY ("ID_PRESENTACION")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table RESPUESTA_ESTUDIANTE
--------------------------------------------------------

  ALTER TABLE "ROOT2"."RESPUESTA_ESTUDIANTE" ADD PRIMARY KEY ("ID_RESPUESTA_ESTUDIANTE")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table RESPUESTA
--------------------------------------------------------

  ALTER TABLE "ROOT2"."RESPUESTA" ADD PRIMARY KEY ("ID_RESPUESTA")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table ESTADISTICAS_PREGUNTA
--------------------------------------------------------

  ALTER TABLE "ROOT2"."ESTADISTICAS_PREGUNTA" ADD PRIMARY KEY ("ID_PREGUNTA")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table PREGUNTA
--------------------------------------------------------

  ALTER TABLE "ROOT2"."PREGUNTA" MODIFY ("ID_PREGUNTA" NOT NULL ENABLE);
  ALTER TABLE "ROOT2"."PREGUNTA" ADD PRIMARY KEY ("ID_PREGUNTA")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table ESTADISTICAS_EXAMEN
--------------------------------------------------------

  ALTER TABLE "ROOT2"."ESTADISTICAS_EXAMEN" ADD PRIMARY KEY ("ID_ESTADISTICA")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table EXAMEN_PREGUNTA
--------------------------------------------------------

  ALTER TABLE "ROOT2"."EXAMEN_PREGUNTA" ADD CONSTRAINT "PK_EXAMEN_PREGUNTA" PRIMARY KEY ("ID_EXAMEN", "ID_PREGUNTA")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table EXAMEN
--------------------------------------------------------

  ALTER TABLE "ROOT2"."EXAMEN" ADD PRIMARY KEY ("ID_EXAMEN")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
  ALTER TABLE "ROOT2"."EXAMEN" ADD CHECK (modo_seleccion IN ('Manual', 'Aleatorio')) ENABLE;
--------------------------------------------------------
--  Constraints for Table USUARIO
--------------------------------------------------------

  ALTER TABLE "ROOT2"."USUARIO" ADD CHECK (tipo_usuario IN ('Docente', 'Estudiante')) ENABLE;
  ALTER TABLE "ROOT2"."USUARIO" ADD PRIMARY KEY ("ID_USUARIO")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
  ALTER TABLE "ROOT2"."USUARIO" ADD UNIQUE ("CORREO")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table BANCO_PREGUNTAS
--------------------------------------------------------

  ALTER TABLE "ROOT2"."BANCO_PREGUNTAS" ADD PRIMARY KEY ("ID_BANCO")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table ESTADISTICAS_EXAMEN
--------------------------------------------------------

  ALTER TABLE "ROOT2"."ESTADISTICAS_EXAMEN" ADD FOREIGN KEY ("ID_EXAMEN")
	  REFERENCES "ROOT2"."EXAMEN" ("ID_EXAMEN") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table ESTADISTICAS_PREGUNTA
--------------------------------------------------------

  ALTER TABLE "ROOT2"."ESTADISTICAS_PREGUNTA" ADD FOREIGN KEY ("ID_PREGUNTA")
	  REFERENCES "ROOT2"."PREGUNTA" ("ID_PREGUNTA") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table EXAMEN
--------------------------------------------------------

  ALTER TABLE "ROOT2"."EXAMEN" ADD FOREIGN KEY ("ID_DOCENTE")
	  REFERENCES "ROOT2"."USUARIO" ("ID_USUARIO") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table EXAMEN_PREGUNTA
--------------------------------------------------------

  ALTER TABLE "ROOT2"."EXAMEN_PREGUNTA" ADD FOREIGN KEY ("ID_EXAMEN")
	  REFERENCES "ROOT2"."EXAMEN" ("ID_EXAMEN") ENABLE;
  ALTER TABLE "ROOT2"."EXAMEN_PREGUNTA" ADD FOREIGN KEY ("ID_PREGUNTA")
	  REFERENCES "ROOT2"."PREGUNTA" ("ID_PREGUNTA") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table PREGUNTA
--------------------------------------------------------

  ALTER TABLE "ROOT2"."PREGUNTA" ADD FOREIGN KEY ("ID_BANCO")
	  REFERENCES "ROOT2"."BANCO_PREGUNTAS" ("ID_BANCO") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table PRESENTACION_EXAMEN
--------------------------------------------------------

  ALTER TABLE "ROOT2"."PRESENTACION_EXAMEN" ADD FOREIGN KEY ("ID_EXAMEN")
	  REFERENCES "ROOT2"."EXAMEN" ("ID_EXAMEN") ENABLE;
  ALTER TABLE "ROOT2"."PRESENTACION_EXAMEN" ADD FOREIGN KEY ("ID_ESTUDIANTE")
	  REFERENCES "ROOT2"."USUARIO" ("ID_USUARIO") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table RESPUESTA
--------------------------------------------------------

  ALTER TABLE "ROOT2"."RESPUESTA" ADD FOREIGN KEY ("ID_PREGUNTA")
	  REFERENCES "ROOT2"."PREGUNTA" ("ID_PREGUNTA") ENABLE;
      
--------------------------------------------------------
--  Ref Constraints for Table EXAMEN
--------------------------------------------------------

ALTER TABLE "ROOT2"."EXAMEN" ADD FOREIGN KEY ("ID_TEMA")
    REFERENCES "ROOT2"."TEMA" ("ID_TEMA") ENABLE;
    
--------------------------------------------------------
--  Ref Constraints for Table PREGUNTA
--------------------------------------------------------

ALTER TABLE "ROOT2"."PREGUNTA" ADD FOREIGN KEY ("ID_TEMA")
    REFERENCES "ROOT2"."TEMA" ("ID_TEMA") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table RESPUESTA_ESTUDIANTE
--------------------------------------------------------

  ALTER TABLE "ROOT2"."RESPUESTA_ESTUDIANTE" ADD FOREIGN KEY ("ID_PRESENTACION")
	  REFERENCES "ROOT2"."PRESENTACION_EXAMEN" ("ID_PRESENTACION") ENABLE;
  ALTER TABLE "ROOT2"."RESPUESTA_ESTUDIANTE" ADD FOREIGN KEY ("ID_PREGUNTA")
	  REFERENCES "ROOT2"."PREGUNTA" ("ID_PREGUNTA") ENABLE;
  ALTER TABLE "ROOT2"."RESPUESTA_ESTUDIANTE" ADD FOREIGN KEY ("ID_RESPUESTA")
	  REFERENCES "ROOT2"."RESPUESTA" ("ID_RESPUESTA") ENABLE;