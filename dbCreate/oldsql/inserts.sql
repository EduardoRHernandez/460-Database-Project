INSERT INTO member (
  FIRSTNAME, LASTNAME, PHONE, EMAIL, DOB,
  EMGCONTACTFNAME, EMGCONTACTLNAME, EMGCONTACTPHONE
) VALUES (
  'Alice', 'Johnson', '520-555-0123', 'alice.johnson@example.com',
  TO_DATE('1995-06-14', 'YYYY-MM-DD'), 'Mark', 'Johnson', '520-555-0456'
);

INSERT INTO member (
  FIRSTNAME, LASTNAME, PHONE, EMAIL, DOB,
  EMGCONTACTFNAME, EMGCONTACTLNAME, EMGCONTACTPHONE
) VALUES (
  'Brian', 'Lee', '520-555-0345', 'brian.lee@example.com',
  TO_DATE('1988-12-22', 'YYYY-MM-DD'), 'Susan', 'Lee', '520-555-0678'
);

INSERT INTO member (
  FIRSTNAME, LASTNAME, PHONE, EMAIL, DOB,
  EMGCONTACTFNAME, EMGCONTACTLNAME, EMGCONTACTPHONE
) VALUES (
  'Carla', 'Nguyen', '520-555-0789', 'carla.nguyen@example.com',
  TO_DATE('2001-03-03', 'YYYY-MM-DD'), 'David', 'Nguyen', '520-555-0990'
);

INSERT INTO member (
  FIRSTNAME, LASTNAME, PHONE, EMAIL, DOB,
  EMGCONTACTFNAME, EMGCONTACTLNAME, EMGCONTACTPHONE
) VALUES (
  'Daniel', 'Garcia', '520-555-0112', 'daniel.garcia@example.com',
  TO_DATE('1993-08-19', 'YYYY-MM-DD'), 'Maria', 'Garcia', '520-555-0221'
);


-- Alice (memberId = 1), seasonal pass
INSERT INTO SkiPass (memberId, purchaseDate, expirationDate, totalUses, type)
VALUES (1, TO_DATE('2025-01-01', 'YYYY-MM-DD'), TO_DATE('2025-12-31', 'YYYY-MM-DD'), 20, 'season');

-- Brian (memberId = 2), 2-day pass
INSERT INTO SkiPass (memberId, purchaseDate, expirationDate, totalUses, type)
VALUES (2, TO_DATE('2025-02-15', 'YYYY-MM-DD'), TO_DATE('2025-02-16', 'YYYY-MM-DD'), 2, '2 day');

-- Carla (memberId = 3), 4-day pass
INSERT INTO SkiPass (memberId, purchaseDate, expirationDate, totalUses, type)
VALUES (3, TO_DATE('2025-03-10', 'YYYY-MM-DD'), TO_DATE('2025-03-13', 'YYYY-MM-DD'), 4, '4 day');

-- Daniel (memberId = 4), 1-day pass
INSERT INTO SkiPass (memberId, purchaseDate, expirationDate, totalUses, type)
VALUES (4, TO_DATE('2025-01-20', 'YYYY-MM-DD'), TO_DATE('2025-01-20', 'YYYY-MM-DD'), 1, '1 day');


-- Member 1 (Season Pass) - Active rental (not returned)
INSERT INTO Rental (passId, rentalDate, returnStatus)
VALUES (1, TO_DATE('2025-04-15 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'Rented');

-- Member 2 (2-Day Pass) - Returned
INSERT INTO Rental (passId, rentalDate, returnStatus)
VALUES (2, TO_DATE('2025-02-15 12:30:00', 'YYYY-MM-DD HH24:MI:SS'), 'Available');

-- Member 3 (4-Day Pass) - Returned
INSERT INTO Rental (passId, rentalDate, returnStatus)
VALUES (3, TO_DATE('2025-03-11 14:45:00', 'YYYY-MM-DD HH24:MI:SS'), 'Available');

-- Member 4 (1-Day Pass) - Active rental (not returned)
INSERT INTO Rental (passId, rentalDate, returnStatus)
VALUES (4, TO_DATE('2025-01-20 08:15:00', 'YYYY-MM-DD HH24:MI:SS'), 'Rented');

-- Equipment linked to currently active rental RID = 1
INSERT INTO Equipment (RID, eType, eSize, eStatus)
VALUES (1, 'Ski Boots', 'Large', 'Rented');

-- Equipment linked to currently active rental RID = 4
INSERT INTO Equipment (RID, eType, eSize, eStatus)
VALUES (4, 'Snowboard', 'Medium', 'Rented');

-- Equipment not currently rented (RID is NULL)
INSERT INTO Equipment (RID, eType, eSize, eStatus)
VALUES (NULL, 'Ski Poles', 'Small', 'Available');

INSERT INTO Equipment (RID, eType, eSize, eStatus)
VALUES (NULL, 'Helmet', 'Universal', 'Available');


-- Change #1 for EID = 1
INSERT INTO EquipmentChangeLog (
    EID, oldType, newType, oldSize, newSize, oldStatus, newStatus
) VALUES (
    1, 'Ski Poles', 'Ski Boots', 'Medium', 'Large', 'Available', 'Rented'
);

-- Change #2 for EID = 1
INSERT INTO EquipmentChangeLog (
    EID, oldType, newType, oldSize, newSize, oldStatus, newStatus
) VALUES (
    1, 'Ski Boots', 'Ski Boots', 'Large', 'Large', 'Rented', 'Available'
);

-- Change #3 for EID = 2
INSERT INTO EquipmentChangeLog (
    EID, oldType, newType, oldSize, newSize, oldStatus, newStatus
) VALUES (
    2, 'Snowboard', 'Snowboard', 'Medium', 'Large', 'Available', 'Rented'
);

-- Change #4 for EID = 1
INSERT INTO EquipmentChangeLog (
    EID, oldType, newType, oldSize, newSize, oldStatus, newStatus
) VALUES (
    1, 'Ski Boots', 'Ski Boots', 'Large', 'XL', 'Available', 'Available'
);



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

INSERT INTO LiftLog (passId, liftName, liftLogDate)
VALUES (1, 'Eagle Ridge Express', SYSDATE);

INSERT INTO LiftLog (passId, liftName, liftLogDate)
VALUES (1, 'Eagle Ridge Express', SYSDATE);

INSERT INTO LiftLog (passId, liftName, liftLogDate)
VALUES (1, 'Eagle Ridge Express', SYSDATE);