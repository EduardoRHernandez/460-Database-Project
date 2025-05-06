CREATE TABLE LiftLog (
    liftLogId     NUMBER PRIMARY KEY,
    passId        NUMBER NOT NULL REFERENCES SkiPass(passId) ON DELETE CASCADE,
    liftName      VARCHAR2(50) REFERENCES Lift(name),
    liftLogDate   DATE
);

CREATE SEQUENCE liftlog_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER liftlog_before_insert
BEFORE INSERT ON LiftLog
FOR EACH ROW
BEGIN
    SELECT liftlog_seq.NEXTVAL INTO :new.liftLogId FROM dual;
END;
/

CREATE OR REPLACE TRIGGER liftlog_after_insert
AFTER INSERT ON LiftLog
FOR EACH ROW
BEGIN
    UPDATE SkiPass
    SET totalUses = totalUses + 1
    WHERE passId = :new.passId;
END;
/
