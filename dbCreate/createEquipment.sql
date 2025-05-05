CREATE TABLE Equipment (
    EID NUMBER PRIMARY KEY,                      -- Unique equipment ID
    RID NUMBER REFERENCES Rental(RID),  -- Associated rental
    eType VARCHAR2(30),
    eSize VARCHAR2(20),
    eStatus VARCHAR2(20)
);


CREATE SEQUENCE equipment_seq
    START WITH 1
    INCREMENT BY 1;


CREATE OR REPLACE TRIGGER trg_equipment_id
BEFORE INSERT ON Equipment
FOR EACH ROW
BEGIN
    :NEW.EID := equipment_seq.NEXTVAL;
END;
/
