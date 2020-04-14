
/* IF OBJECT_ID('olio1.Transfer', 'U') IS NOT NULL
DROP TABLE olio1.Transfer
GO
IF OBJECT_ID('olio1.CardPayment', 'U') IS NOT NULL
DROP TABLE olio1.CardPayment
GO
IF OBJECT_ID('olio1.DepWit', 'U') IS NOT NULL
DROP TABLE olio1.DepWit
GO
IF OBJECT_ID('olio1.Card', 'U') IS NOT NULL
DROP TABLE olio1.Card
GO
IF OBJECT_ID('olio1.Account', 'U') IS NOT NULL
DROP TABLE olio1.Account
GO
IF OBJECT_ID('olio1.Users', 'U') IS NOT NULL
DROP TABLE olio1.Users
GO
IF OBJECT_ID('olio1.Bank', 'U') IS NOT NULL
DROP TABLE olio1.Bank
GO */

CREATE TABLE olio1.Bank
(
    bankId INT NOT NULL PRIMARY KEY,
    name VARCHAR(15) NOT NULL,
    bic VARCHAR(10) NOT NULL
);

CREATE TABLE olio1.Users
(
    userId INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    userName VARCHAR(15) NOT NULL,
    firstName VARCHAR(15) NOT NULL,
    lastName VARCHAR(15) NOT NULL,
    email VARCHAR(30) NOT NULL UNIQUE,
    phoneNumber VARCHAR(12) NOT NULL,
    password CHAR(60) NOT NULL,
    bankId INT NOT NULL,
    FOREIGN KEY (bankId) 
        REFERENCES olio1.Bank (bankId) 
        ON DELETE CASCADE 
);

CREATE TABLE olio1.Account
(
    accountId INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    userId INT NOT NULL,
    iban VARCHAR(30) NOT NULL,
    balance INT,
    FOREIGN KEY (userId)
        REFERENCES olio1.Users (userId)
        ON DELETE CASCADE
);

CREATE TABLE olio1.Card
(
    cardId INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    accountId INT NOT NULL,
    withdraw_limit INT,
    spending_limit INT,
    area VARCHAR(15),
    FOREIGN KEY (accountId)
        REFERENCES olio1.Account (accountId)
        ON DELETE CASCADE
);

CREATE TABLE olio1.MasterTransfer
(
    transferId INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    fromId INT NOT NULL,
    toId INT NOT NULL,
    amount INT NOT NULL,
    date DATE NOT NULL,
    type ENUM ('Transfer', 'DepWit', 'Payment'),
    FOREIGN KEY (fromId)
        REFERENCES olio1.Account (accountId)
        ON DELETE NO ACTION,
    FOREIGN KEY (toId)
        REFERENCES olio1.Account (accountId)
        ON DELETE CASCADE
);

/*CREATE TABLE olio1.DepWit
(
    depwitId INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    cardId INT NOT NULL,
    amount INT NOT NULL,
    date DATE NOT NULL,
    location VARCHAR(30),
    FOREIGN KEY (cardId)
        REFERENCES olio1.Card (cardId)
        ON DELETE CASCADE
);

CREATE TABLE olio1.CardPayment
(
    cardPaymentId INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    cardId INT NOT NULL,
    amount INT NOT NULL,
    date DATE NOT NULL,
    receiver VARCHAR(30),
    FOREIGN KEY (cardId)
        REFERENCES olio1.Card (cardId)
        ON DELETE CASCADE
);*/

/* Default banks. */
INSERT INTO olio1.Bank VALUES (0, "Deals", "DEALFIHH");
INSERT INTO olio1.Bank VALUES (1, "Bear bank", "BEARFIHH");
INSERT INTO olio1.Bank VALUES (2, "Our bank", "OURBFIHH");

/* CREATE TABLE olio1.Payment
(
    paymentId INT NOT NULL PRIMARY KEY,
    fromId INT NOT NULL,
    toId INT NOT NULL,
    amount INT NOT NULL,
    date DATE NOT NULL,
    FOREIGN KEY (fromId)
        REFERENCES olio1.Account (accountId)
        ON DELETE CASCADE,
    FOREIGN KEY (toId)
        REFERENCES olio1.Account (accountId)
        ON DELETE CASCADE
);
GO */
