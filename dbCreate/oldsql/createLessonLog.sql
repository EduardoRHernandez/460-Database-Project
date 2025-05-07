CREATE TABLE LessonLog (
    lessonLogId NUMBER PRIMARY KEY,
    orderId NUMBER NOT NULL REFERENCES LessonPurchase(orderId) on delete cascade,
    useDate DATE
);

CREATE SEQUENCE lesson_log_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER trg_lesson_log_id
BEFORE INSERT ON LessonLog
FOR EACH ROW
BEGIN
    :NEW.lessonLogId := lesson_log_seq.NEXTVAL;
END;
/
