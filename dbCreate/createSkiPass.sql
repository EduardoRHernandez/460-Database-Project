CREATE TABLE SkiPass (
    passId          NUMBER PRIMARY KEY,
    memberId        NUMBER NOT NULL REFERENCES Member(memberId),
    purchaseDate    DATE NOT NULL,
    expirationDate  DATE NOT NULL,
    totalUses       NUMBER DEFAULT 0 NOT NULL,
    type            VARCHAR2(10) NOT NULL CHECK (type IN ('1 day', '2 day', '4 day', 'season'))
);

CREATE SEQUENCE skiPass_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER skiPass_before_insert
BEFORE INSERT ON SkiPass
FOR EACH ROW
BEGIN
    SELECT skiPass_seq.NEXTVAL INTO :new.passId FROM dual;
END;
/
