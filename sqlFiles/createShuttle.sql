CREATE TABLE Shuttle (
    name                 VARCHAR2(100) PRIMARY KEY,
    departingProperty    VARCHAR2(100) NOT NULL REFERENCES Property(name),
    destinationProperty  VARCHAR2(100) NOT NULL REFERENCES Property(name)
);