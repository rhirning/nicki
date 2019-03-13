CREATE TABLE RPUSER.FILE_STORE (
  ID NUMBER GENERATED ALWAYS AS IDENTITY,
  NAME varchar2(1000) NOT NULL,
  DATA CLOB,
  MODIFY_TIME TIMESTAMP NOT NULL,
  CONSTRAINT FILE_STORE_PK PRIMARY KEY (ID)
);
