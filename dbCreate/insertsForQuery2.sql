-- -- Lifts (6 total, covering all combinations of difficulty and status)
-- INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate)
-- VALUES ('LIFTA', 'open', TO_DATE('08:00', 'HH24:MI'), TO_DATE('16:00', 'HH24:MI'), 'beginner', SYSDATE);

-- INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate)
-- VALUES ('LIFTB', 'open', TO_DATE('09:00', 'HH24:MI'), TO_DATE('17:00', 'HH24:MI'), 'intermediate', SYSDATE);

-- INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate)
-- VALUES ('LIFTC', 'closed', TO_DATE('10:00', 'HH24:MI'), TO_DATE('18:00', 'HH24:MI'), 'expert', SYSDATE);

-- INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate)
-- VALUES ('LIFTD', 'maintenance', TO_DATE('07:30', 'HH24:MI'), TO_DATE('15:30', 'HH24:MI'), 'beginner', SYSDATE);

-- INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate)
-- VALUES ('LIFTE', 'open', TO_DATE('08:30', 'HH24:MI'), TO_DATE('16:30', 'HH24:MI'), 'intermediate', SYSDATE);

-- INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate)
-- VALUES ('LIFTZ', 'open', TO_DATE('07:00', 'HH24:MI'), TO_DATE('17:00', 'HH24:MI'), 'expert', SYSDATE);

-- Members (5 total)
INSERT INTO Member (firstName, lastName, phone, email, dob, emgContactFName, emgContactLName, emgContactPhone)
VALUES ('John', 'Doe', '555-1111', 'john.doe@example.com', TO_DATE('1990-01-01', 'YYYY-MM-DD'), 'Mary', 'Doe', '555-9999');

INSERT INTO Member (firstName, lastName, phone, email, dob, emgContactFName, emgContactLName, emgContactPhone)
VALUES ('Jane', 'Smith', '555-2222', 'jane.smith@example.com', TO_DATE('1985-02-02', 'YYYY-MM-DD'), 'Mark', 'Smith', '555-8888');

INSERT INTO Member (firstName, lastName, phone, email, dob, emgContactFName, emgContactLName, emgContactPhone)
VALUES ('Carlos', 'Rivera', '555-3333', 'c.rivera@example.com', TO_DATE('2000-03-15', 'YYYY-MM-DD'), 'Laura', 'Rivera', '555-7777');

INSERT INTO Member (firstName, lastName, phone, email, dob, emgContactFName, emgContactLName, emgContactPhone)
VALUES ('Emily', 'Nguyen', '555-4444', 'emily.n@example.com', TO_DATE('1995-07-21', 'YYYY-MM-DD'), 'Tam', 'Nguyen', '555-6666');

INSERT INTO Member (firstName, lastName, phone, email, dob, emgContactFName, emgContactLName, emgContactPhone)
VALUES ('Liam', 'Patel', '555-5555', 'liam.p@example.com', TO_DATE('1998-11-11', 'YYYY-MM-DD'), 'Asha', 'Patel', '555-5550');

-- SkiPasses (variety of types and members)
INSERT INTO SkiPass (memberId, purchaseDate, expirationDate, totalUses, type) VALUES (1, SYSDATE, SYSDATE + 5, 1, '1 day');
INSERT INTO SkiPass (memberId, purchaseDate, expirationDate, totalUses, type) VALUES (2, SYSDATE, SYSDATE + 2, 2, '2 day');
INSERT INTO SkiPass (memberId, purchaseDate, expirationDate, totalUses, type) VALUES (3, SYSDATE, SYSDATE + 4, 0, '4 day');
INSERT INTO SkiPass (memberId, purchaseDate, expirationDate, totalUses, type) VALUES (4, SYSDATE, SYSDATE + 60, 0, 'season');
INSERT INTO SkiPass (memberId, purchaseDate, expirationDate, totalUses, type) VALUES (5, SYSDATE, SYSDATE + 1, 0, '1 day');

-- Rentals (some returned, some active)
INSERT INTO Rental (passId, rentalDate, returnStatus) VALUES (1, SYSDATE - 1, 'Available');
INSERT INTO Rental (passId, rentalDate, returnStatus) VALUES (2, SYSDATE, 'Rented');
INSERT INTO Rental (passId, rentalDate, returnStatus) VALUES (3, SYSDATE, 'Rented');
INSERT INTO Rental (passId, rentalDate, returnStatus) VALUES (4, SYSDATE - 2, 'Available');
INSERT INTO Rental (passId, rentalDate, returnStatus) VALUES (5, SYSDATE, 'Rented');

-- Equipment (some rented, some available, with size and type variation)
INSERT INTO Equipment (eType, eSize, eStatus) VALUES ('Skis', 'M', 'Available');
INSERT INTO Equipment (eType, eSize, eStatus) VALUES ('Snowboard', 'L', 'Available');
INSERT INTO Equipment (RID, eType, eSize, eStatus) VALUES (2, 'Ski Boots', 'M', 'Rented');
INSERT INTO Equipment (RID, eType, eSize, eStatus) VALUES (3, 'Helmet', 'S', 'Rented');
INSERT INTO Equipment (RID, eType, eSize, eStatus) VALUES (5, 'Ski Poles', 'L', 'Rented');
INSERT INTO Equipment (eType, eSize, eStatus) VALUES ('Ski Boots', 'S', 'Lost');
INSERT INTO Equipment (eType, eSize, eStatus) VALUES ('Helmet', 'M', 'Retired');

-- -- LiftLogs (assumes liftLogId is auto-increment or you are managing IDs manually)
-- INSERT INTO LiftLog (liftLogId, passId, liftName, liftLogDate) VALUES (1, 1, 'LIFTA', SYSDATE);
-- INSERT INTO LiftLog (liftLogId, passId, liftName, liftLogDate) VALUES (2, 2, 'LIFTB', SYSDATE);
-- INSERT INTO LiftLog (liftLogId, passId, liftName, liftLogDate) VALUES (3, 3, 'LIFTC', SYSDATE - 1);
-- INSERT INTO LiftLog (liftLogId, passId, liftName, liftLogDate) VALUES (4, 4, 'LIFTE', SYSDATE - 2);
-- INSERT INTO LiftLog (liftLogId, passId, liftName, liftLogDate) VALUES (5, 5, 'LIFTZ', SYSDATE);

-- COMMIT;


-- Lift logs for various passIds
INSERT INTO LiftLog (passId, liftName, liftLogDate)
VALUES (1, 'Alpine Cabin', SYSDATE);

INSERT INTO LiftLog (passId, liftName, liftLogDate)
VALUES (1, 'Snowflake Skyway', SYSDATE - 1);

INSERT INTO LiftLog (passId, liftName, liftLogDate)
VALUES (2, 'Crystal Flyer', SYSDATE);

INSERT INTO LiftLog (passId, liftName, liftLogDate)
VALUES (2, 'Morning Cabin', SYSDATE - 2);

INSERT INTO LiftLog (passId, liftName, liftLogDate)
VALUES (3, 'Aspen Gondola', SYSDATE);

INSERT INTO LiftLog (passId, liftName, liftLogDate)
VALUES (3, 'Eagle Tram', SYSDATE - 1);

INSERT INTO LiftLog (passId, liftName, liftLogDate)
VALUES (4, 'Pine Tram', SYSDATE);

INSERT INTO LiftLog (passId, liftName, liftLogDate)
VALUES (5, 'Glacier Chair', SYSDATE - 1);

INSERT INTO LiftLog (passId, liftName, liftLogDate) VALUES (1, 'Alpine Cabin', SYSDATE);
INSERT INTO LiftLog (passId, liftName, liftLogDate) VALUES (1, 'Alpine Cabin', SYSDATE - 1);
INSERT INTO LiftLog (passId, liftName, liftLogDate) VALUES (1, 'Snowflake Skyway', SYSDATE);
INSERT INTO LiftLog (passId, liftName, liftLogDate) VALUES (1, 'Crystal Flyer', SYSDATE - 2);
INSERT INTO LiftLog (passId, liftName, liftLogDate) VALUES (1, 'Morning Cabin', SYSDATE - 2);
INSERT INTO LiftLog (passId, liftName, liftLogDate) VALUES (1, 'Aspen Gondola', SYSDATE - 3);
INSERT INTO LiftLog (passId, liftName, liftLogDate) VALUES (1, 'Eagle Tram', SYSDATE - 3);
INSERT INTO LiftLog (passId, liftName, liftLogDate) VALUES (1, 'Pine Tram', SYSDATE - 4);
INSERT INTO LiftLog (passId, liftName, liftLogDate) VALUES (1, 'Glacier Chair', SYSDATE - 4);
INSERT INTO LiftLog (passId, liftName, liftLogDate) VALUES (1, 'Crystal Flyer', SYSDATE - 5);
INSERT INTO LiftLog (passId, liftName, liftLogDate) VALUES (1, 'Alpine Cabin', SYSDATE - 6);
