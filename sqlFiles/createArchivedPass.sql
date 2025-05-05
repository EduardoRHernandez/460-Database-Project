CREATE TABLE ArchivedPass (
    passId          NUMBER PRIMARY KEY,
    memberId        NUMBER NOT NULL REFERENCES Member(memberId),
    purchaseDate    DATE NOT NULL,
    expirationDate  DATE NOT NULL,
    totalUses       NUMBER NOT NULL,
    type            VARCHAR2(20) NOT NULL,
    archiveDate     DATE NOT NULL,
    archiveReason   VARCHAR2(255) NOT NULL
);
