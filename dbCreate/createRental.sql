CREATE TABLE Rental (
    RID NUMBER PRIMARY KEY,
    passId NUMBER NOT NULL REFERENCES SkiPass(passId),
    rentalDate DATE,         -- Includes both date and time
    returnStatus VARCHAR2(20)
);
