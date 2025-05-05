-- Populate Lift table
INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) VALUES ('LIFTA', 'open', TO_DATE('08:00', 'HH24:MI'), TO_DATE('16:00', 'HH24:MI'), 'beginner', SYSDATE);
INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) VALUES ('LIFTB', 'open', TO_DATE('09:00', 'HH24:MI'), TO_DATE('17:00', 'HH24:MI'), 'intermediate', SYSDATE);
INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) VALUES ('LIFTC', 'closed', TO_DATE('10:00', 'HH24:MI'), TO_DATE('18:00', 'HH24:MI'), 'expert', SYSDATE);

-- Populate Member table (with emergency contacts)
INSERT INTO Member (firstName, lastName, phone, email, dob, emgContactFName, emgContactLName, emgContactPhone)
VALUES ('John', 'Doe', '555-1111', 'john.doe@example.com', TO_DATE('1990-01-01', 'YYYY-MM-DD'), 'Mary', 'Doe', '555-9999');

INSERT INTO Member (firstName, lastName, phone, email, dob, emgContactFName, emgContactLName, emgContactPhone)
VALUES ('Jane', 'Smith', '555-2222', 'jane.smith@example.com', TO_DATE('1985-02-02', 'YYYY-MM-DD'), 'Mark', 'Smith', '555-8888');

-- Populate SkiPass table
INSERT INTO SkiPass (memberId, purchaseDate, expirationDate, totalUses, type)
VALUES (1, SYSDATE, SYSDATE + 30, 0, '1 day');

INSERT INTO SkiPass (memberId, purchaseDate, expirationDate, totalUses, type)
VALUES (2, SYSDATE, SYSDATE + 30, 0, '2 day');

-- Populate Rental table
INSERT INTO Rental (RID, passId, rentalDate, returnStatus)
VALUES (1, 1, SYSDATE, 'returned');

INSERT INTO Rental (RID, passId, rentalDate, returnStatus)
VALUES (2, 2, SYSDATE, 'rented');

-- Populate Equipment table
INSERT INTO Equipment (RID, eType, eSize, eStatus)
VALUES (1, 'Skis', 'M', 'available');

INSERT INTO Equipment (RID, eType, eSize, eStatus)
VALUES (2, 'Snowboard', 'L', 'available');

-- Populate LiftLog table
INSERT INTO LiftLog (liftLogId, passId, liftName, liftLotDate)
VALUES (1, 1, 'LIFTA', SYSDATE);

INSERT INTO LiftLog (liftLogId, passId, liftName, liftLotDate)
VALUES (2, 2, 'LIFTB', SYSDATE);

COMMIT;
