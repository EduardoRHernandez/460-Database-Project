CREATE TABLE Member (
	memberId     	NUMBER PRIMARY KEY,
	firstName    	VARCHAR2(50) NOT NULL,
	lastName     	VARCHAR2(50) NOT NULL,
	phone        	VARCHAR2(20) NOT NULL,
	email        	VARCHAR2(100) NOT NULL,
	dob          	DATE NOT NULL,
	emgContactFName  VARCHAR2(50) NOT NULL,
	emgContactLName  VARCHAR2(50) NOT NULL,
	emgContactPhone  VARCHAR2(20) NOT NULL
);

CREATE SEQUENCE member_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER member_before_insert
BEFORE INSERT ON Member
FOR EACH ROW
BEGIN
	SELECT member_seq.NEXTVAL INTO :new.memberId FROM dual;
END;
/