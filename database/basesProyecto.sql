--------------------------------------------------------
-- Archivo creado  - martes-mayo-20-2025   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Sequence SEQ_EXAMEN
--------------------------------------------------------

   CREATE SEQUENCE  "ROOT2"."SEQ_EXAMEN"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 13 NOCACHE  NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence SEQ_GRUPO
--------------------------------------------------------

   CREATE SEQUENCE  "ROOT2"."SEQ_GRUPO"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence SEQ_ID_PREGUNTA
--------------------------------------------------------

   CREATE SEQUENCE  "ROOT2"."SEQ_ID_PREGUNTA"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 381 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence SEQ_PREGUNTA
--------------------------------------------------------

   CREATE SEQUENCE  "ROOT2"."SEQ_PREGUNTA"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 101 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence SEQ_PRESENTACION_EXAMEN
--------------------------------------------------------

   CREATE SEQUENCE  "ROOT2"."SEQ_PRESENTACION_EXAMEN"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 3 NOCACHE  NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence SEQ_RESPUESTA
--------------------------------------------------------

   CREATE SEQUENCE  "ROOT2"."SEQ_RESPUESTA"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 101 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence SEQ_RESPUESTA_ESTUDIANTE
--------------------------------------------------------

   CREATE SEQUENCE  "ROOT2"."SEQ_RESPUESTA_ESTUDIANTE"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence SEQ_TEMA
--------------------------------------------------------

   CREATE SEQUENCE  "ROOT2"."SEQ_TEMA"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Table CURSO
--------------------------------------------------------

  CREATE TABLE "ROOT2"."CURSO" 
   (	"ID_CURSO" NUMBER, 
	"NOMBRE" VARCHAR2(100 BYTE), 
	"DESCRIPCION" VARCHAR2(100 BYTE)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table DOCENTE
--------------------------------------------------------

  CREATE TABLE "ROOT2"."DOCENTE" 
   (	"ID_DOCENTE" NUMBER, 
	"ASIGNATURA" VARCHAR2(100 BYTE)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table DOCENTE_GRUPO
--------------------------------------------------------

  CREATE TABLE "ROOT2"."DOCENTE_GRUPO" 
   (	"ID_DOCENTE" NUMBER, 
	"ID_GRUPO" NUMBER
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table ESTADISTICAS_ESTUDIANTE
--------------------------------------------------------

  CREATE TABLE "ROOT2"."ESTADISTICAS_ESTUDIANTE" 
   (	"ID_ESTUDIANTE" NUMBER(*,0), 
	"TOTAL_EXAMENES_PRESENTADOS" NUMBER(*,0), 
	"PREGUNTAS_CORRECTAS" NUMBER(*,0), 
	"PREGUNTAS_INCORRECTAS" NUMBER(*,0), 
	"PROMEDIO_NOTA" NUMBER(5,2), 
	"MAX_NOTA" NUMBER(5,2), 
	"MIN_NOTA" NUMBER(5,2)
   ) SEGMENT CREATION DEFERRED 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
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
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table ESTADISTICAS_GRUPO
--------------------------------------------------------

  CREATE TABLE "ROOT2"."ESTADISTICAS_GRUPO" 
   (	"ID_GRUPO" NUMBER(*,0), 
	"TOTAL_EXAMENES_PRESENTADOS" NUMBER(*,0), 
	"PROMEDIO_GENERAL" NUMBER(5,2), 
	"MAX_NOTA" NUMBER(5,2), 
	"MIN_NOTA" NUMBER(5,2)
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
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table ESTADISTICAS_TEMA
--------------------------------------------------------

  CREATE TABLE "ROOT2"."ESTADISTICAS_TEMA" 
   (	"ID_TEMA" NUMBER(*,0), 
	"TOTAL_PREGUNTAS" NUMBER(*,0), 
	"TOTAL_CORRECTAS" NUMBER(*,0), 
	"TOTAL_INCORRECTAS" NUMBER(*,0), 
	"PROMEDIO_NOTA" NUMBER(5,2), 
	"MAX_NOTA" NUMBER(5,2), 
	"MIN_NOTA" NUMBER(5,2)
   ) SEGMENT CREATION DEFERRED 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table ESTUDIANTE
--------------------------------------------------------

  CREATE TABLE "ROOT2"."ESTUDIANTE" 
   (	"ID_ESTUDIANTE" NUMBER
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table ESTUDIANTE_GRUPO
--------------------------------------------------------

  CREATE TABLE "ROOT2"."ESTUDIANTE_GRUPO" 
   (	"ID_ESTUDIANTE" NUMBER, 
	"ID_GRUPO" NUMBER
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
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
	"ID_TEMA" NUMBER, 
	"ES_SIN_TIEMPO" NUMBER(1,0) DEFAULT 0, 
	"NUM_PREGUNTAS_ALEATORIAS" NUMBER, 
	"PESO_CURSO" NUMBER(5,2), 
	"NOTA_MINIMA_APROBACION" NUMBER(2,1) DEFAULT 3.0, 
	"ID_GRUPO" NUMBER
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
	"ID_PREGUNTA" NUMBER, 
	"VALOR_NOTA" NUMBER(3,2)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table GRUPO
--------------------------------------------------------

  CREATE TABLE "ROOT2"."GRUPO" 
   (	"ID_GRUPO" NUMBER, 
	"NOMBRE" VARCHAR2(100 BYTE), 
	"ID_CURSO" NUMBER
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table HORARIO
--------------------------------------------------------

  CREATE TABLE "ROOT2"."HORARIO" 
   (	"ID_HORARIO" NUMBER, 
	"ID_GRUPO" NUMBER, 
	"DIA_SEMANA" VARCHAR2(15 BYTE), 
	"HORA_INICIO" DATE, 
	"HORA_FIN" DATE, 
	"LUGAR" VARCHAR2(100 BYTE)
   ) SEGMENT CREATION DEFERRED 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table PLAN_ESTUDIO
--------------------------------------------------------

  CREATE TABLE "ROOT2"."PLAN_ESTUDIO" 
   (	"ID_PLAN" NUMBER, 
	"ID_CURSO" NUMBER, 
	"VERSION_PLAN" VARCHAR2(10 BYTE), 
	"DESCRIPCION" VARCHAR2(200 BYTE)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table PREGUNTA
--------------------------------------------------------

  CREATE TABLE "ROOT2"."PREGUNTA" 
   (	"ID_PREGUNTA" NUMBER DEFAULT "ROOT2"."SEQ_PREGUNTA"."NEXTVAL", 
	"TEXTO" CLOB, 
	"TIPO" VARCHAR2(50 BYTE), 
	"ID_TEMA" NUMBER, 
	"ES_PUBLICA" CHAR(1 BYTE) DEFAULT 'N', 
	"ID_PREGUNTA_PADRE" NUMBER, 
	"VALOR_NOTA" NUMBER(3,2)
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
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
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
--  DDL for Table TEMA
--------------------------------------------------------

  CREATE TABLE "ROOT2"."TEMA" 
   (	"ID_TEMA" NUMBER, 
	"NOMBRE" VARCHAR2(100 BYTE), 
	"ID_UNIDAD" NUMBER
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table UNIDAD
--------------------------------------------------------

  CREATE TABLE "ROOT2"."UNIDAD" 
   (	"ID_UNIDAD" NUMBER, 
	"ID_PLAN" NUMBER, 
	"NOMBRE" VARCHAR2(100 BYTE), 
	"DESCRIPCION" VARCHAR2(200 BYTE), 
	"ORDEN" NUMBER
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
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
--------------------------------------------------------
--  DDL for View VISTA_ESTADISTICAS_ESTUDIANTE
--------------------------------------------------------

  CREATE OR REPLACE FORCE EDITIONABLE VIEW "ROOT2"."VISTA_ESTADISTICAS_ESTUDIANTE" ("ID_ESTUDIANTE", "NOMBRE", "TOTAL_EXAMENES_PRESENTADOS", "TOTAL_RESPUESTAS", "PREGUNTAS_CORRECTAS", "PREGUNTAS_INCORRECTAS", "PROMEDIO_NOTA", "MAX_NOTA", "MIN_NOTA") AS 
  SELECT 
    e.id_estudiante AS id_estudiante,
    u.nombre AS nombre,
    COUNT(DISTINCT pres.id_presentacion) AS total_examenes_presentados,
    COUNT(res_est.id_respuesta_estudiante) AS total_respuestas,
    SUM(CASE WHEN r.es_correcta = 1 THEN 1 ELSE 0 END) AS preguntas_correctas,
    SUM(CASE WHEN r.es_correcta = 0 THEN 1 ELSE 0 END) AS preguntas_incorrectas,
    ROUND(AVG(pres.calificacion), 2) AS promedio_nota,
    MAX(pres.calificacion) AS max_nota,
    MIN(pres.calificacion) AS min_nota
FROM 
    ESTUDIANTE e
JOIN 
    USUARIO u ON u.id_usuario = e.id_estudiante
JOIN 
    PRESENTACION_EXAMEN pres ON pres.id_estudiante = e.id_estudiante
LEFT JOIN 
    RESPUESTA_ESTUDIANTE res_est ON res_est.id_presentacion = pres.id_presentacion
LEFT JOIN 
    RESPUESTA r ON r.id_respuesta = res_est.id_respuesta
GROUP BY 
    e.id_estudiante, u.nombre
;
--------------------------------------------------------
--  DDL for View VISTA_ESTADISTICAS_EXAMEN
--------------------------------------------------------

  CREATE OR REPLACE FORCE EDITIONABLE VIEW "ROOT2"."VISTA_ESTADISTICAS_EXAMEN" ("ID_EXAMEN", "NOMBRE", "TOTAL_PRESENTACIONES", "PROMEDIO_NOTA", "MAX_NOTA", "MIN_NOTA") AS 
  SELECT 
    e.id_examen,
    e.nombre,
    COUNT(pres.id_presentacion) AS total_presentaciones,
    ROUND(AVG(pres.calificacion), 2) AS promedio_nota,
    MAX(pres.calificacion) AS max_nota,
    MIN(pres.calificacion) AS min_nota
FROM 
    EXAMEN e
LEFT JOIN 
    PRESENTACION_EXAMEN pres ON pres.id_examen = e.id_examen
GROUP BY 
    e.id_examen, e.nombre
;
--------------------------------------------------------
--  DDL for View VISTA_ESTADISTICAS_GRUPO
--------------------------------------------------------

  CREATE OR REPLACE FORCE EDITIONABLE VIEW "ROOT2"."VISTA_ESTADISTICAS_GRUPO" ("ID_GRUPO", "GRUPO", "TOTAL_EXAMENES_PRESENTADOS", "PROMEDIO_GENERAL", "MAX_NOTA", "MIN_NOTA") AS 
  SELECT 
    g.id_grupo,
    g.nombre AS grupo,
    COUNT(DISTINCT pres.id_presentacion) AS total_examenes_presentados,
    ROUND(AVG(pres.calificacion), 2) AS promedio_general,
    MAX(pres.calificacion) AS max_nota,
    MIN(pres.calificacion) AS min_nota
FROM 
    GRUPO g
JOIN 
    ESTUDIANTE_GRUPO eg ON eg.id_grupo = g.id_grupo
JOIN 
    PRESENTACION_EXAMEN pres ON pres.id_estudiante = eg.id_estudiante
GROUP BY 
    g.id_grupo, g.nombre
;
--------------------------------------------------------
--  DDL for View VISTA_ESTADISTICAS_PREGUNTA
--------------------------------------------------------

  CREATE OR REPLACE FORCE EDITIONABLE VIEW "ROOT2"."VISTA_ESTADISTICAS_PREGUNTA" ("ID_PREGUNTA", "PREGUNTA", "TEMA", "TOTAL_RESPUESTAS", "TOTAL_CORRECTAS", "TOTAL_INCORRECTAS", "PORCENTAJE_ACIERTOS") AS 
  SELECT 
    p.id_pregunta,
    DBMS_LOB.SUBSTR(p.texto, 4000, 1) AS pregunta,
    t.nombre AS tema,
    COUNT(re.id_respuesta_estudiante) AS total_respuestas,
    SUM(CASE WHEN r.es_correcta = 1 THEN 1 ELSE 0 END) AS total_correctas,
    SUM(CASE WHEN r.es_correcta = 0 THEN 1 ELSE 0 END) AS total_incorrectas,
    ROUND(
        CASE 
            WHEN COUNT(re.id_respuesta_estudiante) = 0 THEN 0
            ELSE 
                (SUM(CASE WHEN r.es_correcta = 1 THEN 1 ELSE 0 END) * 100.0) / COUNT(re.id_respuesta_estudiante)
        END
    , 2) AS porcentaje_aciertos
FROM 
    PREGUNTA p
JOIN 
    TEMA t ON t.id_tema = p.id_tema
LEFT JOIN 
    RESPUESTA_ESTUDIANTE re ON re.id_pregunta = p.id_pregunta
LEFT JOIN 
    RESPUESTA r ON r.id_respuesta = re.id_respuesta
GROUP BY 
    p.id_pregunta, DBMS_LOB.SUBSTR(p.texto, 4000, 1), t.nombre
;
--------------------------------------------------------
--  DDL for View VISTA_ESTADISTICAS_TEMA
--------------------------------------------------------

  CREATE OR REPLACE FORCE EDITIONABLE VIEW "ROOT2"."VISTA_ESTADISTICAS_TEMA" ("ID_TEMA", "TEMA", "TOTAL_RESPUESTAS", "TOTAL_CORRECTAS", "TOTAL_INCORRECTAS", "PROMEDIO_NOTA", "MAX_NOTA", "MIN_NOTA") AS 
  SELECT 
    t.id_tema,
    t.nombre AS tema,
    COUNT(re.id_respuesta_estudiante) AS total_respuestas,
    SUM(CASE WHEN pe.es_correcta = 1 THEN 1 ELSE 0 END) AS total_correctas,
    SUM(CASE WHEN NOT pe.es_correcta = 0 THEN 1 ELSE 0 END) AS total_incorrectas,
    ROUND(AVG(pres.calificacion), 2) AS promedio_nota,
    MAX(pres.calificacion) AS max_nota,
    MIN(pres.calificacion) AS min_nota
FROM 
    TEMA t
JOIN 
    PREGUNTA p ON p.id_tema = t.id_tema
JOIN 
    RESPUESTA pe ON pe.id_pregunta = p.id_pregunta
JOIN 
    RESPUESTA_ESTUDIANTE re ON re.id_respuesta = pe.id_respuesta
JOIN
    PRESENTACION_EXAMEN pres ON pres.id_presentacion = re.id_presentacion
GROUP BY 
    t.id_tema, t.nombre
;
REM INSERTING into ROOT2.CURSO
SET DEFINE OFF;
Insert into ROOT2.CURSO (ID_CURSO,NOMBRE,DESCRIPCION) values ('1','Bases de Datos 2','Asignatura de 6to semestre');
Insert into ROOT2.CURSO (ID_CURSO,NOMBRE,DESCRIPCION) values ('2','Infraestructura de TI','Asignatura de 7mo semestre');
Insert into ROOT2.CURSO (ID_CURSO,NOMBRE,DESCRIPCION) values ('3','Ingeniería de Software','Curso de desarrollo de software');
Insert into ROOT2.CURSO (ID_CURSO,NOMBRE,DESCRIPCION) values ('4','Sistemas Distribuidos','Curso sobre arquitecturas distribuidas');
REM INSERTING into ROOT2.DOCENTE
SET DEFINE OFF;
Insert into ROOT2.DOCENTE (ID_DOCENTE,ASIGNATURA) values ('1','Programación');
Insert into ROOT2.DOCENTE (ID_DOCENTE,ASIGNATURA) values ('5','Ingeniería de Software');
Insert into ROOT2.DOCENTE (ID_DOCENTE,ASIGNATURA) values ('6','Sistemas Distribuidos');
REM INSERTING into ROOT2.DOCENTE_GRUPO
SET DEFINE OFF;
Insert into ROOT2.DOCENTE_GRUPO (ID_DOCENTE,ID_GRUPO) values ('1','1');
Insert into ROOT2.DOCENTE_GRUPO (ID_DOCENTE,ID_GRUPO) values ('5','3');
Insert into ROOT2.DOCENTE_GRUPO (ID_DOCENTE,ID_GRUPO) values ('6','2');
REM INSERTING into ROOT2.ESTADISTICAS_ESTUDIANTE
SET DEFINE OFF;
REM INSERTING into ROOT2.ESTADISTICAS_EXAMEN
SET DEFINE OFF;
REM INSERTING into ROOT2.ESTADISTICAS_GRUPO
SET DEFINE OFF;
REM INSERTING into ROOT2.ESTADISTICAS_PREGUNTA
SET DEFINE OFF;
REM INSERTING into ROOT2.ESTADISTICAS_TEMA
SET DEFINE OFF;
REM INSERTING into ROOT2.ESTUDIANTE
SET DEFINE OFF;
Insert into ROOT2.ESTUDIANTE (ID_ESTUDIANTE) values ('2');
Insert into ROOT2.ESTUDIANTE (ID_ESTUDIANTE) values ('7');
Insert into ROOT2.ESTUDIANTE (ID_ESTUDIANTE) values ('8');
Insert into ROOT2.ESTUDIANTE (ID_ESTUDIANTE) values ('9');
REM INSERTING into ROOT2.ESTUDIANTE_GRUPO
SET DEFINE OFF;
Insert into ROOT2.ESTUDIANTE_GRUPO (ID_ESTUDIANTE,ID_GRUPO) values ('2','1');
Insert into ROOT2.ESTUDIANTE_GRUPO (ID_ESTUDIANTE,ID_GRUPO) values ('7','3');
Insert into ROOT2.ESTUDIANTE_GRUPO (ID_ESTUDIANTE,ID_GRUPO) values ('8','2');
Insert into ROOT2.ESTUDIANTE_GRUPO (ID_ESTUDIANTE,ID_GRUPO) values ('9','3');
REM INSERTING into ROOT2.EXAMEN
SET DEFINE OFF;
Insert into ROOT2.EXAMEN (ID_EXAMEN,NOMBRE,DESCRIPCION,FECHA_INICIO,FECHA_FIN,TIEMPO_LIMITE,ID_DOCENTE,NUMERO_PREGUNTAS,MODO_SELECCION,TIEMPO_POR_PREGUNTA,ID_TEMA,ES_SIN_TIEMPO,NUM_PREGUNTAS_ALEATORIAS,PESO_CURSO,NOTA_MINIMA_APROBACION,ID_GRUPO) values ('18','Redes','Prueba para redes',to_date('20/05/25','DD/MM/RR'),to_date('25/05/25','DD/MM/RR'),'40','1','10','Aleatorio','2','2','0',null,null,'3','1');
Insert into ROOT2.EXAMEN (ID_EXAMEN,NOMBRE,DESCRIPCION,FECHA_INICIO,FECHA_FIN,TIEMPO_LIMITE,ID_DOCENTE,NUMERO_PREGUNTAS,MODO_SELECCION,TIEMPO_POR_PREGUNTA,ID_TEMA,ES_SIN_TIEMPO,NUM_PREGUNTAS_ALEATORIAS,PESO_CURSO,NOTA_MINIMA_APROBACION,ID_GRUPO) values ('13','Bases de Datos','Pueba para Bases',to_date('09/03/25','DD/MM/RR'),to_date('10/03/25','DD/MM/RR'),'15','1','8','Aleatorio','3,75','3','0',null,null,'3','1');
Insert into ROOT2.EXAMEN (ID_EXAMEN,NOMBRE,DESCRIPCION,FECHA_INICIO,FECHA_FIN,TIEMPO_LIMITE,ID_DOCENTE,NUMERO_PREGUNTAS,MODO_SELECCION,TIEMPO_POR_PREGUNTA,ID_TEMA,ES_SIN_TIEMPO,NUM_PREGUNTAS_ALEATORIAS,PESO_CURSO,NOTA_MINIMA_APROBACION,ID_GRUPO) values ('16','Ciberseguridad','Prueba para Ciberseguridad',to_date('21/04/25','DD/MM/RR'),to_date('21/04/25','DD/MM/RR'),'30','1','15','Aleatorio','1','1','0',null,null,'3',null);
Insert into ROOT2.EXAMEN (ID_EXAMEN,NOMBRE,DESCRIPCION,FECHA_INICIO,FECHA_FIN,TIEMPO_LIMITE,ID_DOCENTE,NUMERO_PREGUNTAS,MODO_SELECCION,TIEMPO_POR_PREGUNTA,ID_TEMA,ES_SIN_TIEMPO,NUM_PREGUNTAS_ALEATORIAS,PESO_CURSO,NOTA_MINIMA_APROBACION,ID_GRUPO) values ('17','Ciberseguridad','Prueba para Ciberseguridad',to_date('21/04/25','DD/MM/RR'),to_date('21/04/25','DD/MM/RR'),'30','1','10','Aleatorio','3','1','0',null,null,'3',null);
Insert into ROOT2.EXAMEN (ID_EXAMEN,NOMBRE,DESCRIPCION,FECHA_INICIO,FECHA_FIN,TIEMPO_LIMITE,ID_DOCENTE,NUMERO_PREGUNTAS,MODO_SELECCION,TIEMPO_POR_PREGUNTA,ID_TEMA,ES_SIN_TIEMPO,NUM_PREGUNTAS_ALEATORIAS,PESO_CURSO,NOTA_MINIMA_APROBACION,ID_GRUPO) values ('12','Prueba sin docente','a ver si falla',to_date('07/05/25','DD/MM/RR'),to_date('14/05/25','DD/MM/RR'),'34','1',null,null,null,'3','0',null,null,'3',null);
REM INSERTING into ROOT2.EXAMEN_PREGUNTA
SET DEFINE OFF;
Insert into ROOT2.EXAMEN_PREGUNTA (ID_EXAMEN,ID_PREGUNTA,VALOR_NOTA) values ('18','501','3');
Insert into ROOT2.EXAMEN_PREGUNTA (ID_EXAMEN,ID_PREGUNTA,VALOR_NOTA) values ('18','361','3');
Insert into ROOT2.EXAMEN_PREGUNTA (ID_EXAMEN,ID_PREGUNTA,VALOR_NOTA) values ('18','322','3');
Insert into ROOT2.EXAMEN_PREGUNTA (ID_EXAMEN,ID_PREGUNTA,VALOR_NOTA) values ('18','81','3');
Insert into ROOT2.EXAMEN_PREGUNTA (ID_EXAMEN,ID_PREGUNTA,VALOR_NOTA) values ('18','345','0,6');
Insert into ROOT2.EXAMEN_PREGUNTA (ID_EXAMEN,ID_PREGUNTA,VALOR_NOTA) values ('16','482','0,3');
Insert into ROOT2.EXAMEN_PREGUNTA (ID_EXAMEN,ID_PREGUNTA,VALOR_NOTA) values ('13','402',null);
Insert into ROOT2.EXAMEN_PREGUNTA (ID_EXAMEN,ID_PREGUNTA,VALOR_NOTA) values ('16','405',null);
Insert into ROOT2.EXAMEN_PREGUNTA (ID_EXAMEN,ID_PREGUNTA,VALOR_NOTA) values ('13','461',null);
Insert into ROOT2.EXAMEN_PREGUNTA (ID_EXAMEN,ID_PREGUNTA,VALOR_NOTA) values ('17','364','0,5');
REM INSERTING into ROOT2.GRUPO
SET DEFINE OFF;
Insert into ROOT2.GRUPO (ID_GRUPO,NOMBRE,ID_CURSO) values ('1','Grupo A','1');
Insert into ROOT2.GRUPO (ID_GRUPO,NOMBRE,ID_CURSO) values ('2','Grupo B','2');
Insert into ROOT2.GRUPO (ID_GRUPO,NOMBRE,ID_CURSO) values ('3','Grupo C','3');
REM INSERTING into ROOT2.HORARIO
SET DEFINE OFF;
REM INSERTING into ROOT2.PLAN_ESTUDIO
SET DEFINE OFF;
Insert into ROOT2.PLAN_ESTUDIO (ID_PLAN,ID_CURSO,VERSION_PLAN,DESCRIPCION) values ('1','1','Plan 2025','Plan de Estudios');
Insert into ROOT2.PLAN_ESTUDIO (ID_PLAN,ID_CURSO,VERSION_PLAN,DESCRIPCION) values ('2','2','Plan 2025','Plan de Estudios');
Insert into ROOT2.PLAN_ESTUDIO (ID_PLAN,ID_CURSO,VERSION_PLAN,DESCRIPCION) values ('3','3','Plan 2025','Plan de ingeniería de software');
Insert into ROOT2.PLAN_ESTUDIO (ID_PLAN,ID_CURSO,VERSION_PLAN,DESCRIPCION) values ('4','4','Plan 2025','Plan de sistemas distribuidos');
REM INSERTING into ROOT2.PREGUNTA
SET DEFINE OFF;
Insert into ROOT2.PREGUNTA (ID_PREGUNTA,TIPO,ID_TEMA,ES_PUBLICA,ID_PREGUNTA_PADRE,VALOR_NOTA) values ('81','Opción Múltiple','2','N',null,'3');
Insert into ROOT2.PREGUNTA (ID_PREGUNTA,TIPO,ID_TEMA,ES_PUBLICA,ID_PREGUNTA_PADRE,VALOR_NOTA) values ('405','Respuesta Corta','1','N',null,null);
Insert into ROOT2.PREGUNTA (ID_PREGUNTA,TIPO,ID_TEMA,ES_PUBLICA,ID_PREGUNTA_PADRE,VALOR_NOTA) values ('461','Respuesta Corta','3','N',null,null);
Insert into ROOT2.PREGUNTA (ID_PREGUNTA,TIPO,ID_TEMA,ES_PUBLICA,ID_PREGUNTA_PADRE,VALOR_NOTA) values ('501','Opción Múltiple','2','N',null,'3');
Insert into ROOT2.PREGUNTA (ID_PREGUNTA,TIPO,ID_TEMA,ES_PUBLICA,ID_PREGUNTA_PADRE,VALOR_NOTA) values ('482','Opción Múltiple','1','N',null,null);
Insert into ROOT2.PREGUNTA (ID_PREGUNTA,TIPO,ID_TEMA,ES_PUBLICA,ID_PREGUNTA_PADRE,VALOR_NOTA) values ('361','Respuesta Corta','2','N',null,null);
Insert into ROOT2.PREGUNTA (ID_PREGUNTA,TIPO,ID_TEMA,ES_PUBLICA,ID_PREGUNTA_PADRE,VALOR_NOTA) values ('402','Opción Múltiple','3','N',null,null);
Insert into ROOT2.PREGUNTA (ID_PREGUNTA,TIPO,ID_TEMA,ES_PUBLICA,ID_PREGUNTA_PADRE,VALOR_NOTA) values ('321','Respuesta Corta','3','N',null,null);
Insert into ROOT2.PREGUNTA (ID_PREGUNTA,TIPO,ID_TEMA,ES_PUBLICA,ID_PREGUNTA_PADRE,VALOR_NOTA) values ('322','Respuesta Corta','2','N',null,'3');
Insert into ROOT2.PREGUNTA (ID_PREGUNTA,TIPO,ID_TEMA,ES_PUBLICA,ID_PREGUNTA_PADRE,VALOR_NOTA) values ('341','Respuesta Corta','1','N',null,null);
Insert into ROOT2.PREGUNTA (ID_PREGUNTA,TIPO,ID_TEMA,ES_PUBLICA,ID_PREGUNTA_PADRE,VALOR_NOTA) values ('342','Respuesta Corta','3','N',null,null);
Insert into ROOT2.PREGUNTA (ID_PREGUNTA,TIPO,ID_TEMA,ES_PUBLICA,ID_PREGUNTA_PADRE,VALOR_NOTA) values ('344','OpciÃ³n MÃºltiple','1','N',null,null);
Insert into ROOT2.PREGUNTA (ID_PREGUNTA,TIPO,ID_TEMA,ES_PUBLICA,ID_PREGUNTA_PADRE,VALOR_NOTA) values ('345','Respuesta Corta','2','N',null,null);
Insert into ROOT2.PREGUNTA (ID_PREGUNTA,TIPO,ID_TEMA,ES_PUBLICA,ID_PREGUNTA_PADRE,VALOR_NOTA) values ('346','OpciÃ³n MÃºltiple','3','N',null,null);
Insert into ROOT2.PREGUNTA (ID_PREGUNTA,TIPO,ID_TEMA,ES_PUBLICA,ID_PREGUNTA_PADRE,VALOR_NOTA) values ('347','Respuesta Corta','3','N',null,null);
Insert into ROOT2.PREGUNTA (ID_PREGUNTA,TIPO,ID_TEMA,ES_PUBLICA,ID_PREGUNTA_PADRE,VALOR_NOTA) values ('348','Respuesta Corta','2','N',null,null);
Insert into ROOT2.PREGUNTA (ID_PREGUNTA,TIPO,ID_TEMA,ES_PUBLICA,ID_PREGUNTA_PADRE,VALOR_NOTA) values ('362','Respuesta Corta','3','N',null,null);
Insert into ROOT2.PREGUNTA (ID_PREGUNTA,TIPO,ID_TEMA,ES_PUBLICA,ID_PREGUNTA_PADRE,VALOR_NOTA) values ('363','OpciÃ³n MÃºltiple','2','N',null,null);
Insert into ROOT2.PREGUNTA (ID_PREGUNTA,TIPO,ID_TEMA,ES_PUBLICA,ID_PREGUNTA_PADRE,VALOR_NOTA) values ('364','OpciÃ³n MÃºltiple','1','N',null,null);
Insert into ROOT2.PREGUNTA (ID_PREGUNTA,TIPO,ID_TEMA,ES_PUBLICA,ID_PREGUNTA_PADRE,VALOR_NOTA) values ('365','Respuesta Corta','2','N',null,null);
Insert into ROOT2.PREGUNTA (ID_PREGUNTA,TIPO,ID_TEMA,ES_PUBLICA,ID_PREGUNTA_PADRE,VALOR_NOTA) values ('366','OpciÃ³n MÃºltiple','3','N',null,null);
Insert into ROOT2.PREGUNTA (ID_PREGUNTA,TIPO,ID_TEMA,ES_PUBLICA,ID_PREGUNTA_PADRE,VALOR_NOTA) values ('367','Respuesta Corta','3','N',null,null);
Insert into ROOT2.PREGUNTA (ID_PREGUNTA,TIPO,ID_TEMA,ES_PUBLICA,ID_PREGUNTA_PADRE,VALOR_NOTA) values ('368','Respuesta Corta','2','N',null,null);
REM INSERTING into ROOT2.PRESENTACION_EXAMEN
SET DEFINE OFF;
Insert into ROOT2.PRESENTACION_EXAMEN (ID_PRESENTACION,ID_EXAMEN,ID_ESTUDIANTE,FECHA_PRESENTACION,CALIFICACION) values ('2','18','2',to_date('20/05/25','DD/MM/RR'),null);
Insert into ROOT2.PRESENTACION_EXAMEN (ID_PRESENTACION,ID_EXAMEN,ID_ESTUDIANTE,FECHA_PRESENTACION,CALIFICACION) values ('1','18','2',to_date('20/05/25','DD/MM/RR'),null);
REM INSERTING into ROOT2.RESPUESTA
SET DEFINE OFF;
Insert into ROOT2.RESPUESTA (ID_RESPUESTA,ID_PREGUNTA,TEXTO,ES_CORRECTA) values ('86','501','1234567890','S');
Insert into ROOT2.RESPUESTA (ID_RESPUESTA,ID_PREGUNTA,TEXTO,ES_CORRECTA) values ('87','501','uytrew','N');
Insert into ROOT2.RESPUESTA (ID_RESPUESTA,ID_PREGUNTA,TEXTO,ES_CORRECTA) values ('88','501','345ytgfds','N');
Insert into ROOT2.RESPUESTA (ID_RESPUESTA,ID_PREGUNTA,TEXTO,ES_CORRECTA) values ('89','501','dfghyu787y','N');
Insert into ROOT2.RESPUESTA (ID_RESPUESTA,ID_PREGUNTA,TEXTO,ES_CORRECTA) values ('281','321','sí','S');
Insert into ROOT2.RESPUESTA (ID_RESPUESTA,ID_PREGUNTA,TEXTO,ES_CORRECTA) values ('241','461','mnbvcvb','S');
Insert into ROOT2.RESPUESTA (ID_RESPUESTA,ID_PREGUNTA,TEXTO,ES_CORRECTA) values ('263','405','Falló','S');
Insert into ROOT2.RESPUESTA (ID_RESPUESTA,ID_PREGUNTA,TEXTO,ES_CORRECTA) values ('90','81','sí','S');
Insert into ROOT2.RESPUESTA (ID_RESPUESTA,ID_PREGUNTA,TEXTO,ES_CORRECTA) values ('85','322','1','S');
Insert into ROOT2.RESPUESTA (ID_RESPUESTA,ID_PREGUNTA,TEXTO,ES_CORRECTA) values ('91','81','no','N');
Insert into ROOT2.RESPUESTA (ID_RESPUESTA,ID_PREGUNTA,TEXTO,ES_CORRECTA) values ('92','81','23454','N');
Insert into ROOT2.RESPUESTA (ID_RESPUESTA,ID_PREGUNTA,TEXTO,ES_CORRECTA) values ('93','81','345trfds','N');
REM INSERTING into ROOT2.RESPUESTA_ESTUDIANTE
SET DEFINE OFF;
REM INSERTING into ROOT2.TEMA
SET DEFINE OFF;
Insert into ROOT2.TEMA (ID_TEMA,NOMBRE,ID_UNIDAD) values ('4','Sistemas Operativos','1');
Insert into ROOT2.TEMA (ID_TEMA,NOMBRE,ID_UNIDAD) values ('5','Conectividad','2');
Insert into ROOT2.TEMA (ID_TEMA,NOMBRE,ID_UNIDAD) values ('1','Ciberseguridad','1');
Insert into ROOT2.TEMA (ID_TEMA,NOMBRE,ID_UNIDAD) values ('2','Redes','2');
Insert into ROOT2.TEMA (ID_TEMA,NOMBRE,ID_UNIDAD) values ('3','Bases de Datos','1');
Insert into ROOT2.TEMA (ID_TEMA,NOMBRE,ID_UNIDAD) values ('6','Patrones de Diseño','3');
Insert into ROOT2.TEMA (ID_TEMA,NOMBRE,ID_UNIDAD) values ('7','Middleware','4');
REM INSERTING into ROOT2.UNIDAD
SET DEFINE OFF;
Insert into ROOT2.UNIDAD (ID_UNIDAD,ID_PLAN,NOMBRE,DESCRIPCION,ORDEN) values ('1','1','Unidad 1 - Fundamentos','desc1','1');
Insert into ROOT2.UNIDAD (ID_UNIDAD,ID_PLAN,NOMBRE,DESCRIPCION,ORDEN) values ('2','2','Unidad 2 - Redes Básicas','desc2','2');
Insert into ROOT2.UNIDAD (ID_UNIDAD,ID_PLAN,NOMBRE,DESCRIPCION,ORDEN) values ('3','3','Unidad 1 - Requisitos','Ingeniería de requisitos','1');
Insert into ROOT2.UNIDAD (ID_UNIDAD,ID_PLAN,NOMBRE,DESCRIPCION,ORDEN) values ('4','4','Unidad 1 - Introducción a Distribuidos','Conceptos básicos','1');
REM INSERTING into ROOT2.USUARIO
SET DEFINE OFF;
Insert into ROOT2.USUARIO (ID_USUARIO,NOMBRE,CORREO,CONTRASENA,TIPO_USUARIO) values ('1','Docente Prueba','docente@ejemplo.com','1234','Docente');
Insert into ROOT2.USUARIO (ID_USUARIO,NOMBRE,CORREO,CONTRASENA,TIPO_USUARIO) values ('2','Estudiante Prueba','estudiante@ejemplo.com','5678','Estudiante');
Insert into ROOT2.USUARIO (ID_USUARIO,NOMBRE,CORREO,CONTRASENA,TIPO_USUARIO) values ('3','Jorve Ivan Triviño','trivinio@ejemplo.com','1234','Docente');
Insert into ROOT2.USUARIO (ID_USUARIO,NOMBRE,CORREO,CONTRASENA,TIPO_USUARIO) values ('4','Alisson Campos Marin','alisson@ejemplo.com','5678','Estudiante');
Insert into ROOT2.USUARIO (ID_USUARIO,NOMBRE,CORREO,CONTRASENA,TIPO_USUARIO) values ('5','Carlos Ruiz','cruiz@ejemplo.com','abcd','Docente');
Insert into ROOT2.USUARIO (ID_USUARIO,NOMBRE,CORREO,CONTRASENA,TIPO_USUARIO) values ('6','Lucía Mendoza','lmendoza@ejemplo.com','abcd','Docente');
Insert into ROOT2.USUARIO (ID_USUARIO,NOMBRE,CORREO,CONTRASENA,TIPO_USUARIO) values ('7','Pedro Londoño','plondonio@ejemplo.com','5678','Estudiante');
Insert into ROOT2.USUARIO (ID_USUARIO,NOMBRE,CORREO,CONTRASENA,TIPO_USUARIO) values ('8','María Fernanda','mfernanda@ejemplo.com','5678','Estudiante');
Insert into ROOT2.USUARIO (ID_USUARIO,NOMBRE,CORREO,CONTRASENA,TIPO_USUARIO) values ('9','Andrés Parra','aparra@ejemplo.com','5678','Estudiante');
--------------------------------------------------------
--  DDL for Index PK_EXAMEN_PREGUNTA
--------------------------------------------------------

  CREATE UNIQUE INDEX "ROOT2"."PK_EXAMEN_PREGUNTA" ON "ROOT2"."EXAMEN_PREGUNTA" ("ID_EXAMEN", "ID_PREGUNTA") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Trigger TRG_CALIFICAR_AL_FINALIZAR
--------------------------------------------------------

  CREATE OR REPLACE NONEDITIONABLE TRIGGER "ROOT2"."TRG_CALIFICAR_AL_FINALIZAR" 
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
ALTER TRIGGER "ROOT2"."TRG_CALIFICAR_AL_FINALIZAR" ENABLE;
--------------------------------------------------------
--  DDL for Procedure CALIFICAR_EXAMEN_AUTOMATICO
--------------------------------------------------------
set define off;

  CREATE OR REPLACE NONEDITIONABLE PROCEDURE "ROOT2"."CALIFICAR_EXAMEN_AUTOMATICO" (
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
--  DDL for Procedure LOGIN_USUARIO
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "ROOT2"."LOGIN_USUARIO" (
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
--------------------------------------------------------
--  DDL for Procedure OBTENER_DOCENTE_COMPLETO
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "ROOT2"."OBTENER_DOCENTE_COMPLETO" (
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
--------------------------------------------------------
--  DDL for Procedure OBTENER_ESTUDIANTE_COMPLETO
--------------------------------------------------------
set define off;

  CREATE OR REPLACE NONEDITIONABLE PROCEDURE "ROOT2"."OBTENER_ESTUDIANTE_COMPLETO" (
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
--------------------------------------------------------
--  Constraints for Table ESTADISTICAS_ESTUDIANTE
--------------------------------------------------------

  ALTER TABLE "ROOT2"."ESTADISTICAS_ESTUDIANTE" ADD PRIMARY KEY ("ID_ESTUDIANTE")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table DOCENTE
--------------------------------------------------------

  ALTER TABLE "ROOT2"."DOCENTE" ADD PRIMARY KEY ("ID_DOCENTE")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table CURSO
--------------------------------------------------------

  ALTER TABLE "ROOT2"."CURSO" MODIFY ("NOMBRE" NOT NULL ENABLE);
  ALTER TABLE "ROOT2"."CURSO" ADD PRIMARY KEY ("ID_CURSO")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table TEMA
--------------------------------------------------------

  ALTER TABLE "ROOT2"."TEMA" MODIFY ("NOMBRE" NOT NULL ENABLE);
  ALTER TABLE "ROOT2"."TEMA" ADD PRIMARY KEY ("ID_TEMA")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
  ALTER TABLE "ROOT2"."TEMA" MODIFY ("ID_UNIDAD" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table HORARIO
--------------------------------------------------------

  ALTER TABLE "ROOT2"."HORARIO" MODIFY ("ID_GRUPO" NOT NULL ENABLE);
  ALTER TABLE "ROOT2"."HORARIO" ADD PRIMARY KEY ("ID_HORARIO")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table PLAN_ESTUDIO
--------------------------------------------------------

  ALTER TABLE "ROOT2"."PLAN_ESTUDIO" MODIFY ("ID_CURSO" NOT NULL ENABLE);
  ALTER TABLE "ROOT2"."PLAN_ESTUDIO" ADD PRIMARY KEY ("ID_PLAN")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table PRESENTACION_EXAMEN
--------------------------------------------------------

  ALTER TABLE "ROOT2"."PRESENTACION_EXAMEN" ADD PRIMARY KEY ("ID_PRESENTACION")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table ESTADISTICAS_TEMA
--------------------------------------------------------

  ALTER TABLE "ROOT2"."ESTADISTICAS_TEMA" ADD PRIMARY KEY ("ID_TEMA")
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
  ALTER TABLE "ROOT2"."EXAMEN" ADD CHECK (modo_seleccion IN ('Manual', 'Aleatorio')) ENABLE;
  ALTER TABLE "ROOT2"."EXAMEN" MODIFY ("ID_TEMA" NOT NULL ENABLE);
  ALTER TABLE "ROOT2"."EXAMEN" ADD CHECK (modo_seleccion IN ('Manual', 'Aleatorio')) ENABLE;
  ALTER TABLE "ROOT2"."EXAMEN" ADD CHECK (modo_seleccion IN ('Manual', 'Aleatorio')) ENABLE;
  ALTER TABLE "ROOT2"."EXAMEN" ADD CHECK (modo_seleccion IN ('Manual', 'Aleatorio')) ENABLE;
  ALTER TABLE "ROOT2"."EXAMEN" ADD CHECK (modo_seleccion IN ('Manual', 'Aleatorio')) ENABLE;
--------------------------------------------------------
--  Constraints for Table ESTADISTICAS_PREGUNTA
--------------------------------------------------------

  ALTER TABLE "ROOT2"."ESTADISTICAS_PREGUNTA" ADD PRIMARY KEY ("ID_PREGUNTA")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table ESTUDIANTE_GRUPO
--------------------------------------------------------

  ALTER TABLE "ROOT2"."ESTUDIANTE_GRUPO" MODIFY ("ID_ESTUDIANTE" NOT NULL ENABLE);
  ALTER TABLE "ROOT2"."ESTUDIANTE_GRUPO" MODIFY ("ID_GRUPO" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table DOCENTE_GRUPO
--------------------------------------------------------

  ALTER TABLE "ROOT2"."DOCENTE_GRUPO" ADD PRIMARY KEY ("ID_DOCENTE", "ID_GRUPO")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table ESTADISTICAS_GRUPO
--------------------------------------------------------

  ALTER TABLE "ROOT2"."ESTADISTICAS_GRUPO" ADD PRIMARY KEY ("ID_GRUPO")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table PREGUNTA
--------------------------------------------------------

  ALTER TABLE "ROOT2"."PREGUNTA" MODIFY ("ID_TEMA" NOT NULL ENABLE);
  ALTER TABLE "ROOT2"."PREGUNTA" MODIFY ("ID_PREGUNTA" NOT NULL ENABLE);
  ALTER TABLE "ROOT2"."PREGUNTA" ADD PRIMARY KEY ("ID_PREGUNTA")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
  ALTER TABLE "ROOT2"."PREGUNTA" ADD CHECK (es_publica IN ('S', 'N')) ENABLE;
  ALTER TABLE "ROOT2"."PREGUNTA" ADD CHECK (es_publica IN ('S', 'N')) ENABLE;
  ALTER TABLE "ROOT2"."PREGUNTA" ADD CHECK (es_publica IN ('S', 'N')) ENABLE;
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
--  Constraints for Table EXAMEN_PREGUNTA
--------------------------------------------------------

  ALTER TABLE "ROOT2"."EXAMEN_PREGUNTA" ADD CONSTRAINT "PK_EXAMEN_PREGUNTA" PRIMARY KEY ("ID_EXAMEN", "ID_PREGUNTA")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table RESPUESTA_ESTUDIANTE
--------------------------------------------------------

  ALTER TABLE "ROOT2"."RESPUESTA_ESTUDIANTE" ADD PRIMARY KEY ("ID_RESPUESTA_ESTUDIANTE")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table USUARIO
--------------------------------------------------------

  ALTER TABLE "ROOT2"."USUARIO" ADD PRIMARY KEY ("ID_USUARIO")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table GRUPO
--------------------------------------------------------

  ALTER TABLE "ROOT2"."GRUPO" ADD PRIMARY KEY ("ID_GRUPO")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
  ALTER TABLE "ROOT2"."GRUPO" MODIFY ("NOMBRE" NOT NULL ENABLE);
  ALTER TABLE "ROOT2"."GRUPO" MODIFY ("ID_CURSO" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table ESTUDIANTE
--------------------------------------------------------

  ALTER TABLE "ROOT2"."ESTUDIANTE" ADD PRIMARY KEY ("ID_ESTUDIANTE")
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
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table UNIDAD
--------------------------------------------------------

  ALTER TABLE "ROOT2"."UNIDAD" MODIFY ("ID_PLAN" NOT NULL ENABLE);
  ALTER TABLE "ROOT2"."UNIDAD" ADD PRIMARY KEY ("ID_UNIDAD")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table DOCENTE
--------------------------------------------------------

  ALTER TABLE "ROOT2"."DOCENTE" ADD CONSTRAINT "FK_DOCENTE_USUARIO" FOREIGN KEY ("ID_DOCENTE")
	  REFERENCES "ROOT2"."USUARIO" ("ID_USUARIO") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table DOCENTE_GRUPO
--------------------------------------------------------

  ALTER TABLE "ROOT2"."DOCENTE_GRUPO" ADD CONSTRAINT "FK_DOCENTEGRUPO_DOCENTE" FOREIGN KEY ("ID_DOCENTE")
	  REFERENCES "ROOT2"."DOCENTE" ("ID_DOCENTE") ENABLE;
  ALTER TABLE "ROOT2"."DOCENTE_GRUPO" ADD CONSTRAINT "FK_DOCENTEGRUPO_GRUPO" FOREIGN KEY ("ID_GRUPO")
	  REFERENCES "ROOT2"."GRUPO" ("ID_GRUPO") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table ESTADISTICAS_ESTUDIANTE
--------------------------------------------------------

  ALTER TABLE "ROOT2"."ESTADISTICAS_ESTUDIANTE" ADD FOREIGN KEY ("ID_ESTUDIANTE")
	  REFERENCES "ROOT2"."ESTUDIANTE" ("ID_ESTUDIANTE") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table ESTADISTICAS_EXAMEN
--------------------------------------------------------

  ALTER TABLE "ROOT2"."ESTADISTICAS_EXAMEN" ADD FOREIGN KEY ("ID_EXAMEN")
	  REFERENCES "ROOT2"."EXAMEN" ("ID_EXAMEN") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table ESTADISTICAS_GRUPO
--------------------------------------------------------

  ALTER TABLE "ROOT2"."ESTADISTICAS_GRUPO" ADD FOREIGN KEY ("ID_GRUPO")
	  REFERENCES "ROOT2"."GRUPO" ("ID_GRUPO") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table ESTADISTICAS_PREGUNTA
--------------------------------------------------------

  ALTER TABLE "ROOT2"."ESTADISTICAS_PREGUNTA" ADD FOREIGN KEY ("ID_PREGUNTA")
	  REFERENCES "ROOT2"."PREGUNTA" ("ID_PREGUNTA") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table ESTADISTICAS_TEMA
--------------------------------------------------------

  ALTER TABLE "ROOT2"."ESTADISTICAS_TEMA" ADD FOREIGN KEY ("ID_TEMA")
	  REFERENCES "ROOT2"."TEMA" ("ID_TEMA") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table ESTUDIANTE
--------------------------------------------------------

  ALTER TABLE "ROOT2"."ESTUDIANTE" ADD CONSTRAINT "FK_ESTUDIANTE_USUARIO" FOREIGN KEY ("ID_ESTUDIANTE")
	  REFERENCES "ROOT2"."USUARIO" ("ID_USUARIO") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table ESTUDIANTE_GRUPO
--------------------------------------------------------

  ALTER TABLE "ROOT2"."ESTUDIANTE_GRUPO" ADD CONSTRAINT "ESTUDIANTE_GRUPO_FK" FOREIGN KEY ("ID_ESTUDIANTE")
	  REFERENCES "ROOT2"."ESTUDIANTE" ("ID_ESTUDIANTE") ENABLE;
  ALTER TABLE "ROOT2"."ESTUDIANTE_GRUPO" ADD CONSTRAINT "ESTUDIANTE_GRUPO_FK2" FOREIGN KEY ("ID_GRUPO")
	  REFERENCES "ROOT2"."GRUPO" ("ID_GRUPO") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table EXAMEN
--------------------------------------------------------

  ALTER TABLE "ROOT2"."EXAMEN" ADD FOREIGN KEY ("ID_DOCENTE")
	  REFERENCES "ROOT2"."USUARIO" ("ID_USUARIO") ENABLE;
  ALTER TABLE "ROOT2"."EXAMEN" ADD CONSTRAINT "FK_EXAMEN_TEMA" FOREIGN KEY ("ID_TEMA")
	  REFERENCES "ROOT2"."TEMA" ("ID_TEMA") ENABLE;
  ALTER TABLE "ROOT2"."EXAMEN" ADD CONSTRAINT "FK_EXAMEN_DOCENTE" FOREIGN KEY ("ID_DOCENTE")
	  REFERENCES "ROOT2"."DOCENTE" ("ID_DOCENTE") ENABLE;
  ALTER TABLE "ROOT2"."EXAMEN" ADD CONSTRAINT "FK_EXAMEN_GRUPO" FOREIGN KEY ("ID_GRUPO")
	  REFERENCES "ROOT2"."GRUPO" ("ID_GRUPO") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table EXAMEN_PREGUNTA
--------------------------------------------------------

  ALTER TABLE "ROOT2"."EXAMEN_PREGUNTA" ADD FOREIGN KEY ("ID_EXAMEN")
	  REFERENCES "ROOT2"."EXAMEN" ("ID_EXAMEN") ENABLE;
  ALTER TABLE "ROOT2"."EXAMEN_PREGUNTA" ADD FOREIGN KEY ("ID_PREGUNTA")
	  REFERENCES "ROOT2"."PREGUNTA" ("ID_PREGUNTA") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table GRUPO
--------------------------------------------------------

  ALTER TABLE "ROOT2"."GRUPO" ADD FOREIGN KEY ("ID_CURSO")
	  REFERENCES "ROOT2"."CURSO" ("ID_CURSO") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table HORARIO
--------------------------------------------------------

  ALTER TABLE "ROOT2"."HORARIO" ADD FOREIGN KEY ("ID_GRUPO")
	  REFERENCES "ROOT2"."GRUPO" ("ID_GRUPO") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table PLAN_ESTUDIO
--------------------------------------------------------

  ALTER TABLE "ROOT2"."PLAN_ESTUDIO" ADD FOREIGN KEY ("ID_CURSO")
	  REFERENCES "ROOT2"."CURSO" ("ID_CURSO") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table PREGUNTA
--------------------------------------------------------

  ALTER TABLE "ROOT2"."PREGUNTA" ADD FOREIGN KEY ("ID_PREGUNTA_PADRE")
	  REFERENCES "ROOT2"."PREGUNTA" ("ID_PREGUNTA") ENABLE;
  ALTER TABLE "ROOT2"."PREGUNTA" ADD CONSTRAINT "FK_PREGUNTA_TEMA" FOREIGN KEY ("ID_TEMA")
	  REFERENCES "ROOT2"."TEMA" ("ID_TEMA") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table PRESENTACION_EXAMEN
--------------------------------------------------------

  ALTER TABLE "ROOT2"."PRESENTACION_EXAMEN" ADD FOREIGN KEY ("ID_EXAMEN")
	  REFERENCES "ROOT2"."EXAMEN" ("ID_EXAMEN") ENABLE;
  ALTER TABLE "ROOT2"."PRESENTACION_EXAMEN" ADD FOREIGN KEY ("ID_ESTUDIANTE")
	  REFERENCES "ROOT2"."USUARIO" ("ID_USUARIO") ENABLE;
  ALTER TABLE "ROOT2"."PRESENTACION_EXAMEN" ADD CONSTRAINT "FK_PRESENTACION_ESTUDIANTE" FOREIGN KEY ("ID_ESTUDIANTE")
	  REFERENCES "ROOT2"."ESTUDIANTE" ("ID_ESTUDIANTE") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table RESPUESTA
--------------------------------------------------------

  ALTER TABLE "ROOT2"."RESPUESTA" ADD FOREIGN KEY ("ID_PREGUNTA")
	  REFERENCES "ROOT2"."PREGUNTA" ("ID_PREGUNTA") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table RESPUESTA_ESTUDIANTE
--------------------------------------------------------

  ALTER TABLE "ROOT2"."RESPUESTA_ESTUDIANTE" ADD FOREIGN KEY ("ID_PRESENTACION")
	  REFERENCES "ROOT2"."PRESENTACION_EXAMEN" ("ID_PRESENTACION") ENABLE;
  ALTER TABLE "ROOT2"."RESPUESTA_ESTUDIANTE" ADD FOREIGN KEY ("ID_PREGUNTA")
	  REFERENCES "ROOT2"."PREGUNTA" ("ID_PREGUNTA") ENABLE;
  ALTER TABLE "ROOT2"."RESPUESTA_ESTUDIANTE" ADD FOREIGN KEY ("ID_RESPUESTA")
	  REFERENCES "ROOT2"."RESPUESTA" ("ID_RESPUESTA") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table TEMA
--------------------------------------------------------

  ALTER TABLE "ROOT2"."TEMA" ADD CONSTRAINT "FK_TEMA_UNIDAD" FOREIGN KEY ("ID_UNIDAD")
	  REFERENCES "ROOT2"."UNIDAD" ("ID_UNIDAD") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table UNIDAD
--------------------------------------------------------

  ALTER TABLE "ROOT2"."UNIDAD" ADD FOREIGN KEY ("ID_PLAN")
	  REFERENCES "ROOT2"."PLAN_ESTUDIO" ("ID_PLAN") ENABLE;
