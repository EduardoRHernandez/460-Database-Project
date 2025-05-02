CREATE TABLE EquipmentChangeLog (
    EChangeId NUMBER PRIMARY KEY,
    EID NUMBER UNIQUE NOT NULL REFERENCES Equipment(EID),
    oldType VARCHAR2(30),
    newType VARCHAR2(30),
    oldSize VARCHAR2(20),
    newSize VARCHAR2(20),
    oldStatus VARCHAR2(20),
    newStatus VARCHAR2(20)
);
