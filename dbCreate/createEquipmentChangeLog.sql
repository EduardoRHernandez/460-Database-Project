CREATE TABLE EquipmentChangeLog (
    EChangeId NUMBER PRIMARY KEY,
    EID NUMBER NOT NULL REFERENCES Equipment(EID)  ON DELETE CASCADE,
    oldType VARCHAR2(30) 
        CHECK (oldType IN ('Ski Boots', 'Ski Poles', 'Skis', 'Snowboard', 'Helmet')),
    newType VARCHAR2(30) 
        CHECK (newType IN ('Ski Boots', 'Ski Poles', 'Skis', 'Snowboard', 'Helmet')),
    oldSize VARCHAR2(20),
    newSize VARCHAR2(20),
    oldStatus VARCHAR2(20) 
        CHECK (oldStatus IN ('Available', 'Rented')),
    newStatus VARCHAR2(20) 
        CHECK (newStatus IN ('Available', 'Rented')),
    changeDate DATE DEFAULT SYSDATE
);



CREATE SEQUENCE echangelog_seq
START WITH 9001
INCREMENT BY 1;


CREATE OR REPLACE TRIGGER trg_echange_id
BEFORE INSERT ON EquipmentChangeLog
FOR EACH ROW
WHEN (NEW.EChangeId IS NULL)
BEGIN
    SELECT echangelog_seq.NEXTVAL INTO :NEW.EChangeId FROM dual;
END;
/
