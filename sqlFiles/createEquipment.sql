CREATE TABLE Equipment (
    EID NUMBER PRIMARY KEY,
    RID NUMBER REFERENCES Rental(RID),
    eType VARCHAR2(30) CHECK (
        eType IN ('Ski Boots', 'Ski Poles', 'Skis', 'Snowboard', 'Helmet')
    ),
    eSize VARCHAR2(20),
    eStatus VARCHAR2(20) CHECK (
        eStatus IN ('Available', 'Rented')
    )
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
