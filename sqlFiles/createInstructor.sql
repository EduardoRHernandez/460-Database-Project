CREATE TABLE Instructor (
    instructorId NUMBER PRIMARY KEY REFERENCES Employee(employeeId),
    certification VARCHAR2(10) 
        CHECK (certification IN ('Level I', 'Level II', 'Level III'))
);
