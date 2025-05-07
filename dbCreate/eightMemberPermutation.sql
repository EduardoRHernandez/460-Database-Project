INSERT INTO Member (
  FIRSTNAME, LASTNAME, PHONE, EMAIL, DOB,
  EMGCONTACTFNAME, EMGCONTACTLNAME, EMGCONTACTPHONE
) VALUES (
  'Case1', 'Clear', '520-555-1001', 'case1.clear@example.com',
  TO_DATE('1990-01-01', 'YYYY-MM-DD'), 'EC', 'Clear', '520-555-0001'
);

INSERT INTO Member (
  FIRSTNAME, LASTNAME, PHONE, EMAIL, DOB,
  EMGCONTACTFNAME, EMGCONTACTLNAME, EMGCONTACTPHONE
) VALUES (
  'Case2', 'Ski', '520-555-1002', 'case2.ski@example.com',
  TO_DATE('1991-02-02', 'YYYY-MM-DD'), 'EC', 'Ski', '520-555-0002'
);


-- 1-Day Pass
INSERT INTO SkiPass (memberId, purchaseDate, expirationDate, totalUses, type)
VALUES (
  (SELECT memberId FROM Member WHERE firstName = 'Case2'),
  TO_DATE('2025-01-10', 'YYYY-MM-DD'),
  TO_DATE('2025-06-10', 'YYYY-MM-DD'),
  1,
  '1 day'
);

-- 2-Day Pass
INSERT INTO SkiPass (memberId, purchaseDate, expirationDate, totalUses, type)
VALUES (
  (SELECT memberId FROM Member WHERE firstName = 'Case2'),
  TO_DATE('2025-01-15', 'YYYY-MM-DD'),
  TO_DATE('2025-01-16', 'YYYY-MM-DD'),
  1,
  '2 day'
);

-- 4-Day Pass
INSERT INTO SkiPass (memberId, purchaseDate, expirationDate, totalUses, type)
VALUES (
  (SELECT memberId FROM Member WHERE firstName = 'Case2'),
  TO_DATE('2025-01-20', 'YYYY-MM-DD'),
  TO_DATE('2025-01-23', 'YYYY-MM-DD'),
  4,
  '4 day'
);

-- Season Pass
INSERT INTO SkiPass (memberId, purchaseDate, expirationDate, totalUses, type)
VALUES (
  (SELECT memberId FROM Member WHERE firstName = 'Case2'),
  TO_DATE('2025-01-01', 'YYYY-MM-DD'),
  TO_DATE('2025-12-31', 'YYYY-MM-DD'),
  0,
  'season'
);


INSERT INTO Member (
  FIRSTNAME, LASTNAME, PHONE, EMAIL, DOB,
  EMGCONTACTFNAME, EMGCONTACTLNAME, EMGCONTACTPHONE
) VALUES (
  'Case3', 'Rental', '520-555-1003', 'case3.rental@example.com',
  TO_DATE('1992-03-03', 'YYYY-MM-DD'), 'EC', 'Rental', '520-555-0003'
);

INSERT INTO SkiPass (memberId, purchaseDate, expirationDate, totalUses, type)
VALUES (
  (SELECT memberId FROM Member WHERE firstName = 'Case3'),
  TO_DATE('2025-01-01', 'YYYY-MM-DD'),
  TO_DATE('2025-12-31', 'YYYY-MM-DD'),
  0, 'season'
);

INSERT INTO Rental (passId, rentalDate, returnStatus)
VALUES (
  (SELECT passId FROM SkiPass WHERE memberId = (SELECT memberId FROM Member WHERE firstName = 'Case3')),
  TO_DATE('2025-04-20 10:00:00', 'YYYY-MM-DD HH24:MI:SS'),
  'Rented'
);


INSERT INTO Member (
  FIRSTNAME, LASTNAME, PHONE, EMAIL, DOB,
  EMGCONTACTFNAME, EMGCONTACTLNAME, EMGCONTACTPHONE
) VALUES (
  'Case001', 'LessonOnly', '520-000-0001', 'case001@example.com',
  TO_DATE('1990-01-04', 'YYYY-MM-DD'), 'Test', 'User', '520-111-0001'
);

INSERT INTO lessonpurchase (memberid, lessonid, totalsessions, remainingsessions, pricepersession)
VALUES (1, 1, 5, 3, 100);
