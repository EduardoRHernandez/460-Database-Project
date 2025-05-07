CREATE TABLE LessonPurchase (
    orderId NUMBER PRIMARY KEY,
    memberId NUMBER NOT NULL REFERENCES Member(memberId) on delete cascade,
    lessonId NUMBER NOT NULL REFERENCES Lesson(lessonId),
    totalSessions NUMBER(3) CHECK (totalSessions > 0),
    remainingSessions NUMBER(3) CHECK (remainingSessions >= 0),
    pricePerSession NUMBER(6,2) CHECK (pricePerSession >= 0)
);


CREATE SEQUENCE lesson_purchase_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER trg_lesson_purchase_id
BEFORE INSERT ON LessonPurchase
FOR EACH ROW
BEGIN
    :NEW.orderId := lesson_purchase_seq.NEXTVAL;
END;
/
