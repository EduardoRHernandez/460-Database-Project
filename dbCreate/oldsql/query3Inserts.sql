INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Alpine Cabin', 'open', TO_DATE('08:00', 'HH24:MI'), TO_DATE('16:00', 'HH24:MI'), 'expert', TO_DATE('2024-12-01', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Aspen Cabin', 'open', TO_DATE('08:00', 'HH24:MI'), TO_DATE('16:00', 'HH24:MI'), 'intermediate', TO_DATE('2024-12-03', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Aspen Gondola', 'open', TO_DATE('08:00', 'HH24:MI'), TO_DATE('16:00', 'HH24:MI'), 'beginner', TO_DATE('2024-12-07', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Aspen Link', 'open', TO_DATE('08:00', 'HH24:MI'), TO_DATE('16:00', 'HH24:MI'), 'expert', TO_DATE('2024-12-11', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Crystal Flyer', 'closed', TO_DATE('08:00', 'HH24:MI'), TO_DATE('16:00', 'HH24:MI'), 'intermediate', TO_DATE('2024-12-12', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Eagle Tram', 'open', TO_DATE('08:00', 'HH24:MI'), TO_DATE('16:00', 'HH24:MI'), 'expert', TO_DATE('2024-12-17', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('East Flyer', 'open', TO_DATE('08:00', 'HH24:MI'), TO_DATE('16:00', 'HH24:MI'), 'intermediate', TO_DATE('2024-12-21', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('East Link', 'open', TO_DATE('08:00', 'HH24:MI'), TO_DATE('16:00', 'HH24:MI'), 'expert', TO_DATE('2024-12-22', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Glacier Chair', 'open', TO_DATE('08:00', 'HH24:MI'), TO_DATE('16:00', 'HH24:MI'), 'intermediate', TO_DATE('2024-12-24', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Morning Cabin', 'open', TO_DATE('08:00', 'HH24:MI'), TO_DATE('16:00', 'HH24:MI'), 'intermediate', TO_DATE('2024-12-29', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Pine Tram', 'open', TO_DATE('08:00', 'HH24:MI'), TO_DATE('16:00', 'HH24:MI'), 'beginner', TO_DATE('2025-01-01', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Ridge Flyer', 'open', TO_DATE('08:00', 'HH24:MI'), TO_DATE('16:00', 'HH24:MI'), 'intermediate', TO_DATE('2025-01-06', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('Snowflake Skyway', 'open', TO_DATE('08:00', 'HH24:MI'), TO_DATE('16:00', 'HH24:MI'), 'beginner', TO_DATE('2025-01-09', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('South Quad', 'open', TO_DATE('08:00', 'HH24:MI'), TO_DATE('16:00', 'HH24:MI'), 'expert', TO_DATE('2025-01-10', 'YYYY-MM-DD'));

INSERT INTO Lift (name, status, openTime, closeTime, difficulty, seasonOpenDate) 
VALUES ('South Tram', 'open', TO_DATE('08:00', 'HH24:MI'), TO_DATE('16:00', 'HH24:MI'), 'intermediate', TO_DATE('2025-01-13', 'YYYY-MM-DD'));


INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Sunset Run', 'Morning Cabin', 'Snowflake Skyway', 'open', 'groomed', 'intermediate');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Storm Glade', 'East Flyer', 'Crystal Flyer', 'open', 'moguls', 'beginner');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Glade Traverse', 'Ridge Flyer', 'Eagle Tram', 'open', 'glade', 'expert');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Ridge Drop', 'Glacier Chair', 'East Flyer', 'open', 'moguls', 'expert');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Bear Loop', 'Morning Cabin', 'East Link', 'open', 'glade', 'intermediate');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Crystal Glade', 'Pine Tram', 'Morning Cabin', 'open', 'glade', 'expert');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Eagle Path', 'Eagle Tram', 'Morning Cabin', 'open', 'groomed', 'intermediate');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Willow Chute', 'Aspen Cabin', 'Aspen Gondola', 'open', 'park', 'beginner');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Storm Run', 'Aspen Link', 'Morning Cabin', 'open', 'groomed', 'intermediate');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Sunset Glade', 'Aspen Gondola', 'Snowflake Skyway', 'open', 'glade', 'intermediate');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Ridge Traverse', 'South Tram', 'Aspen Link', 'open', 'groomed', 'intermediate');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Crystal Loop', 'Alpine Cabin', 'South Tram', 'open', 'park', 'beginner');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Pine Drop', 'Aspen Link', 'Ridge Flyer', 'open', 'moguls', 'intermediate');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Glade Run', 'Pine Tram', 'South Quad', 'open', 'glade', 'beginner');

INSERT INTO Trail (name, startLocation, endLocation, status, category, difficulty) 
VALUES ('Bear Traverse', 'Aspen Gondola', 'Aspen Link', 'open', 'groomed', 'intermediate');
