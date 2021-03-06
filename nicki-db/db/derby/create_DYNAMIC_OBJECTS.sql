DROP TABLE APP.DYNAMIC_OBJECTS;

CREATE TABLE APP.DYNAMIC_OBJECTS (
  UNIQUE_ID bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
  ENTRY_TYPE varchar(100) NOT NULL,
  ID varchar(100) NOT NULL,
  FROM_TIME timestamp NOT NULL,
  TO_TIME timestamp,
  ATTRIBUTE varchar(100) NOT NULL,
  CONTENT varchar(2000),
  CONSTRAINT DYNAMIC_OBJECTS_PK PRIMARY KEY (UNIQUE_ID)
);
