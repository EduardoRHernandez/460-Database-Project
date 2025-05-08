-- Populate Lift table (avoid duplicate lift names)
INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate)
VALUES ('LiftD', 'open', TO_DATE('08:00', 'HH24:MI'), TO_DATE('16:00', 'HH24:MI'), 'beginner', SYSDATE);

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate)
VALUES ('LiftE', 'open', TO_DATE('09:00', 'HH24:MI'), TO_DATE('17:00', 'HH24:MI'), 'intermediate', SYSDATE);

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate)
VALUES ('LiftF', 'closed', TO_DATE('10:00', 'HH24:MI'), TO_DATE('18:00', 'HH24:MI'), 'expert', SYSDATE);

-- Populate Member table (assuming your sequence/trigger auto-assigns memberId)
INSERT INTO Member (firstName, lastName, phone, email, dob, emgContactFName, emgContactLName, emgContactPhone)
VALUES ('John', 'Doe', '555-1111', 'john.doe@example.com', TO_DATE('1990-01-01', 'YYYY-MM-DD'), 'Mary', 'Doe', '555-9999');

INSERT INTO Member (firstName, lastName, phone, email, dob, emgContactFName, emgContactLName, emgContactPhone)
VALUES ('Jane', 'Smith', '555-2222', 'jane.smith@example.com', TO_DATE('1985-02-02', 'YYYY-MM-DD'), 'Mark', 'Smith', '555-8888');

-- Populate SkiPass table (assuming skiPass_seq and trigger auto-assign passId)
INSERT INTO SkiPass (memberId, purchaseDate, expirationDate, totalUses, type)
VALUES (1, SYSDATE, SYSDATE + 30, 0, '1 day');

INSERT INTO SkiPass (memberId, purchaseDate, expirationDate, totalUses, type)
VALUES (2, SYSDATE, SYSDATE + 30, 0, '2 day');

-- Populate Rental table (let rental_seq auto-assign RID, use valid returnStatus)
INSERT INTO Rental (passId, rentalDate, returnStatus)
VALUES (1, SYSDATE, 'Rented');

INSERT INTO Rental (passId, rentalDate, returnStatus)
VALUES (2, SYSDATE, 'Available');

-- Populate Equipment table (equipment_seq auto-assigns EID)
INSERT INTO Equipment (RID, eType, eSize, eStatus)
VALUES (1, 'Skis', 'Medium', 'Rented');

INSERT INTO Equipment (RID, eType, eSize, eStatus)
VALUES (2, 'Snowboard', 'Large', 'Available');

-- Populate LiftLog table (assuming liftlog_seq and trigger auto-assign liftLogId)
INSERT INTO LiftLog (passId, liftName, liftLogDate)
VALUES (1, 'LiftD', SYSDATE);

INSERT INTO LiftLog (passId, liftName, liftLogDate)
VALUES (2, 'LiftE', SYSDATE);

COMMIT;