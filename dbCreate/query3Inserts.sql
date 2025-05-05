INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Alpine Express', 'Summit Station', 'Base Lodge', 'open', 'groomed', 'intermediate');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Blue Ridge Run', 'Mid Mountain', 'West Valley', 'open', 'groomed', 'intermediate');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Cedar Lane', 'North Peak', 'Village Center', 'open', 'moguls', 'intermediate');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Diamond Edge', 'East Summit', 'Base Area', 'closed', 'groomed', 'expert');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Eagle Pass', 'Ridge Top', 'Mid Station', 'open', 'park', 'intermediate');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Forest Glide', 'West Peak', 'Lodge Area', 'open', 'glade', 'intermediate');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Golden Trail', 'Panorama Ridge', 'Valley Base', 'open', 'groomed', 'beginner');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Highland Path', 'Mountain Top', 'Northern Base', 'open', 'groomed', 'intermediate');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Icicle Run', 'North Face', 'Eastern Lodge', 'closed', 'moguls', 'intermediate');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Juniper Way', 'Southern Peak', 'Base Camp', 'open', 'park', 'intermediate');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Kestrel Drop', 'Eagle Summit', 'Lower Valley', 'open', 'glade', 'expert');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Lookout Trail', 'Observation Point', 'Main Base', 'open', 'groomed', 'intermediate');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Moose Meadow', 'Northern Ridge', 'Western Base', 'open', 'park', 'beginner');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Nordic Pass', 'Eastern Summit', 'Lodge Center', 'open', 'groomed', 'intermediate');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Owl Hollow', 'Night Ridge', 'Southern Base', 'open', 'glade', 'intermediate');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Pine Valley', 'Forest Peak', 'Central Lodge', 'open', 'groomed', 'intermediate');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Quicksilver', 'High Point', 'Main Center', 'closed', 'moguls', 'expert');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Ridgeline', 'Upper Mountain', 'Base Terminal', 'open', 'groomed', 'intermediate');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Snowbird Pass', 'Peak View', 'Lower Terminal', 'open', 'moguls', 'intermediate');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Timber Trail', 'Forest Edge', 'Main Lodge', 'open', 'glade', 'intermediate');

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Alpine Express', 'open', TO_DATE('08:00', 'HH24:MI'), TO_DATE('16:30', 'HH24:MI'), 'intermediate', TO_DATE('2024-12-01', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Blue Ridge Run', 'open', TO_DATE('08:30', 'HH24:MI'), TO_DATE('16:00', 'HH24:MI'), 'intermediate', TO_DATE('2024-12-05', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Cedar Lane', 'open', TO_DATE('09:00', 'HH24:MI'), TO_DATE('16:30', 'HH24:MI'), 'intermediate', TO_DATE('2024-12-10', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Diamond Edge', 'closed', TO_DATE('08:00', 'HH24:MI'), TO_DATE('16:00', 'HH24:MI'), 'expert', TO_DATE('2024-12-15', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Eagle Pass', 'open', TO_DATE('08:30', 'HH24:MI'), TO_DATE('16:30', 'HH24:MI'), 'intermediate', TO_DATE('2024-12-01', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Forest Glide', 'open', TO_DATE('09:00', 'HH24:MI'), TO_DATE('16:00', 'HH24:MI'), 'intermediate', TO_DATE('2024-12-05', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Golden Trail', 'open', TO_DATE('08:00', 'HH24:MI'), TO_DATE('16:30', 'HH24:MI'), 'beginner', TO_DATE('2024-11-25', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Highland Path', 'open', TO_DATE('08:30', 'HH24:MI'), TO_DATE('16:00', 'HH24:MI'), 'intermediate', TO_DATE('2024-12-10', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Icicle Run', 'closed', TO_DATE('09:00', 'HH24:MI'), TO_DATE('16:30', 'HH24:MI'), 'intermediate', TO_DATE('2024-12-15', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Juniper Way', 'open', TO_DATE('08:00', 'HH24:MI'), TO_DATE('16:00', 'HH24:MI'), 'intermediate', TO_DATE('2024-12-01', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Kestrel Drop', 'open', TO_DATE('08:30', 'HH24:MI'), TO_DATE('16:30', 'HH24:MI'), 'expert', TO_DATE('2024-12-20', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Lookout Trail', 'open', TO_DATE('09:00', 'HH24:MI'), TO_DATE('16:00', 'HH24:MI'), 'intermediate', TO_DATE('2024-12-05', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Moose Meadow', 'open', TO_DATE('08:00', 'HH24:MI'), TO_DATE('16:30', 'HH24:MI'), 'beginner', TO_DATE('2024-11-25', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Nordic Pass', 'open', TO_DATE('08:30', 'HH24:MI'), TO_DATE('16:00', 'HH24:MI'), 'intermediate', TO_DATE('2024-12-10', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Owl Hollow', 'open', TO_DATE('09:00', 'HH24:MI'), TO_DATE('16:30', 'HH24:MI'), 'intermediate', TO_DATE('2024-12-15', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Pine Valley', 'open', TO_DATE('08:00', 'HH24:MI'), TO_DATE('16:00', 'HH24:MI'), 'intermediate', TO_DATE('2024-12-01', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Quicksilver', 'closed', TO_DATE('08:30', 'HH24:MI'), TO_DATE('16:30', 'HH24:MI'), 'expert', TO_DATE('2024-12-20', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Ridgeline', 'open', TO_DATE('09:00', 'HH24:MI'), TO_DATE('16:00', 'HH24:MI'), 'intermediate', TO_DATE('2024-12-05', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Snowbird Pass', 'open', TO_DATE('08:00', 'HH24:MI'), TO_DATE('16:30', 'HH24:MI'), 'intermediate', TO_DATE('2024-12-10', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Timber Trail', 'open', TO_DATE('08:30', 'HH24:MI'), TO_DATE('16:00', 'HH24:MI'), 'intermediate', TO_DATE('2024-12-15', 'YYYY-MM-DD'));
