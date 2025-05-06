CREATE TABLE Lift (
    name VARCHAR2(100) PRIMARY KEY,
    status VARCHAR2(20) NOT NULL CHECK (status IN ('open', 'closed', 'maintenance')),
    openTime DATE NOT NULL, -- DATE stores time
    closeTime DATE NOT NULL,
    difficulty VARCHAR2(20) NOT NULL CHECK (difficulty IN ('beginner', 'intermediate', 'expert')),
    seasonOpenDate DATE NOT NULL
);

