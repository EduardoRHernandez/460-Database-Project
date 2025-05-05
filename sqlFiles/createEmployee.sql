CREATE TABLE Employee (
    employeeId NUMBER PRIMARY KEY,
    firstName VARCHAR2(30),
    lastName VARCHAR2(30),
    dob DATE,
    position VARCHAR2(30),
    startDate DATE,
    monthlySalary NUMBER(8,2),
    email VARCHAR2(100),
    phone VARCHAR2(20)
);

CREATE SEQUENCE employee_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER trg_employee_id
BEFORE INSERT ON Employee
FOR EACH ROW
BEGIN
    :NEW.employeeId := employee_seq.NEXTVAL;
END;
/
