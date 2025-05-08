
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


-- Expanded and detailed Lift entries
INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate)
VALUES ('Eagle Ridge Express', 'open', TO_DATE('08:00', 'HH24:MI'), TO_DATE('16:00', 'HH24:MI'), 'intermediate', TO_DATE('2024-12-01', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate)
VALUES ('Glacier Summit Lift', 'maintenance', TO_DATE('09:00', 'HH24:MI'), TO_DATE('17:00', 'HH24:MI'), 'expert', TO_DATE('2024-11-25', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate)
VALUES ('Pine Valley Chair', 'closed', TO_DATE('07:30', 'HH24:MI'), TO_DATE('15:30', 'HH24:MI'), 'beginner', TO_DATE('2024-12-10', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate)
VALUES ('Snowfall Connector', 'open', TO_DATE('08:15', 'HH24:MI'), TO_DATE('16:15', 'HH24:MI'), 'intermediate', TO_DATE('2024-12-05', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate)
VALUES ('Bear Claw Express', 'open', TO_DATE('08:00', 'HH24:MI'), TO_DATE('16:00', 'HH24:MI'), 'expert', TO_DATE('2024-11-30', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate)
VALUES ('Morning Glory Lift', 'open', TO_DATE('07:45', 'HH24:MI'), TO_DATE('16:30', 'HH24:MI'), 'beginner', TO_DATE('2024-12-03', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate)
VALUES ('Crystal Bowl Tram', 'maintenance', TO_DATE('09:30', 'HH24:MI'), TO_DATE('17:30', 'HH24:MI'), 'expert', TO_DATE('2024-11-28', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate)
VALUES ('Aspen Run Chair', 'open', TO_DATE('08:30', 'HH24:MI'), TO_DATE('16:45', 'HH24:MI'), 'intermediate', TO_DATE('2024-12-07', 'YYYY-MM-DD'));


-- Expanded LiftLog entries
INSERT INTO LiftLog (passId, liftName, liftLogDate)
VALUES (1, 'Eagle Ridge Express', SYSDATE);

INSERT INTO LiftLog (passId, liftName, liftLogDate)
VALUES (1, 'Snowfall Connector', SYSDATE - 1);

INSERT INTO LiftLog (passId, liftName, liftLogDate)
VALUES (2, 'Bear Claw Express', SYSDATE);

INSERT INTO LiftLog (passId, liftName, liftLogDate)
VALUES (2, 'Morning Glory Lift', SYSDATE - 2);

INSERT INTO LiftLog (passId, liftName, liftLogDate)
VALUES (3, 'Aspen Run Chair', SYSDATE);

INSERT INTO LiftLog (passId, liftName, liftLogDate)
VALUES (3, 'Crystal Bowl Tram', SYSDATE - 1);

INSERT INTO LiftLog (passId, liftName, liftLogDate)
VALUES (4, 'Pine Valley Chair', SYSDATE);

INSERT INTO LiftLog (passId, liftName, liftLogDate)
VALUES (5, 'Glacier Summit Lift', SYSDATE - 1);


-- Lift logs for passId = 1 across multiple lifts and days
INSERT INTO LiftLog (passId, liftName, liftLogDate) VALUES (1, 'Eagle Ridge Express', SYSDATE);
INSERT INTO LiftLog (passId, liftName, liftLogDate) VALUES (1, 'Eagle Ridge Express', SYSDATE - 1);
INSERT INTO LiftLog (passId, liftName, liftLogDate) VALUES (1, 'Snowfall Connector', SYSDATE);
INSERT INTO LiftLog (passId, liftName, liftLogDate) VALUES (1, 'Bear Claw Express', SYSDATE - 2);
INSERT INTO LiftLog (passId, liftName, liftLogDate) VALUES (1, 'Morning Glory Lift', SYSDATE - 2);
INSERT INTO LiftLog (passId, liftName, liftLogDate) VALUES (1, 'Aspen Run Chair', SYSDATE - 3);
INSERT INTO LiftLog (passId, liftName, liftLogDate) VALUES (1, 'Crystal Bowl Tram', SYSDATE - 3);
INSERT INTO LiftLog (passId, liftName, liftLogDate) VALUES (1, 'Pine Valley Chair', SYSDATE - 4);
INSERT INTO LiftLog (passId, liftName, liftLogDate) VALUES (1, 'Glacier Summit Lift', SYSDATE - 4);
INSERT INTO LiftLog (passId, liftName, liftLogDate) VALUES (1, 'Bear Claw Express', SYSDATE - 5);
INSERT INTO LiftLog (passId, liftName, liftLogDate) VALUES (1, 'Eagle Ridge Express', SYSDATE - 6);

COMMIT;