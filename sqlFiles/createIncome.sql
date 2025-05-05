CREATE TABLE Income (
    propertyName  VARCHAR2(100) NOT NULL REFERENCES Property(name),
    incomeDate          DATE NOT NULL,
    amount        NUMBER NOT NULL,
    PRIMARY KEY (propertyName, incomeDate)
);
