CREATE TABLE LiftLog (
    liftLogId INT PRIMARY KEY,
    passId INT NOT NULL,
    liftName VARCHAR(50) REFERENCES Lift(name),
    liftLotDate DATE
);