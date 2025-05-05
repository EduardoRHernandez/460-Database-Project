CREATE TABLE Trail (
    name VARCHAR2(100) PRIMARY KEY,
    startLocation VARCHAR2(100) NOT NULL,
    endLocation VARCHAR2(100) NOT NULL,
    status VARCHAR2(20) NOT NULL CHECK (status IN ('open', 'closed')),
    category VARCHAR2(20) NOT NULL CHECK (category IN ('groomed', 'park', 'moguls', 'glade')),
    difficulty VARCHAR2(20) NOT NULL CHECK (difficulty IN ('beginner', 'intermediate', 'expert'))
);
