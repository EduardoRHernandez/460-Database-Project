CREATE TABLE SkiPassManualChangeLog (
    PassChangeId    NUMBER PRIMARY KEY,
    passId          NUMBER NOT NULL REFERENCES SkiPass(passId),
    oldTotalUses    NUMBER NOT NULL,
    newTotalUses    NUMBER NOT NULL,
    changeDate      DATE NOT NULL
);


CREATE SEQUENCE passChange_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER passChange_before_insert
BEFORE INSERT ON SkiPassManualChangeLog
FOR EACH ROW
BEGIN
    SELECT passChange_seq.NEXTVAL INTO :new.PassChangeId FROM dual;
END;
/

