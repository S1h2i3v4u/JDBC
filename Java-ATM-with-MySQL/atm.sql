CREATE DATABASE atm;
USE atm;

CREATE TABLE login (
    pin INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pin INT NOT NULL,
    credited_amount DOUBLE DEFAULT 0,
    withdrawn_amount DOUBLE DEFAULT 0,
    balance DOUBLE NOT NULL,
    transaction_date DATE NOT NULL,
    transaction_time TIME NOT NULL,
    FOREIGN KEY (pin) REFERENCES login(pin)
);

SELECT * FROM login;
SELECT * FROM transactions;
