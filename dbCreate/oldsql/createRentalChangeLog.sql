CREATE TABLE RentalChangeLog (
    RChangeId NUMBER PRIMARY KEY,
    RID NUMBER UNIQUE NOT NULL REFERENCES Rental(RID) ON DELETE CASCADE,
    action VARCHAR2(10) CHECK (action IN ('Update', 'Delete')),
    rentalChangeDate DATE,
    changedBy VARCHAR2(50),
    changeReason VARCHAR2(200),
    oldReturnStatus VARCHAR2(20),
    newReturnStatus VARCHAR2(20)
);


CREATE SEQUENCE rentalChangeLog_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER rentalChangeLog_before_insert
BEFORE INSERT ON RentalChangeLog
FOR EACH ROW
BEGIN
    SELECT rentalChangeLog_seq.NEXTVAL INTO :new.rChangeId FROM dual;
END;
/