CREATE TABLE Lesson (
    lessonId NUMBER PRIMARY KEY,
    instructorId NUMBER NOT NULL REFERENCES Instructor(instructorId),
    ageType VARCHAR2(10) CHECK (ageType IN ('Youth', 'Adult')),
    lessonType VARCHAR2(10) CHECK (lessonType IN ('Private', 'Group')),
    durationType VARCHAR2(10) CHECK (durationType IN ('Half', 'Full')),
    startTime VARCHAR2(10) CHECK (startTime IN ('Morning', 'Midday'))
);

CREATE SEQUENCE lesson_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER trg_lesson_id
BEFORE INSERT ON Lesson
FOR EACH ROW
BEGIN
    :NEW.lessonId := lesson_seq.NEXTVAL;
END;
/
