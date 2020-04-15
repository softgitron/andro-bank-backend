USE olio1;

DELETE FROM MasterTransfer;
DELETE FROM Card;
DELETE FROM Account;
DELETE FROM Users;

INSERT INTO olio1.Users VALUES (1, "test1", "test1", "test1","test1","test1","test1",0);
INSERT INTO olio1.Users VALUES (2, "test2", "test2", "test2","test2","test2","test2",1);
COMMIT;
SELECT * FROM Users;

INSERT INTO olio1.Account VALUES (1, 1, "Iban1", 50, "Normal");
INSERT INTO olio1.Account VALUES (2, 2, "Iban2", 100, "Normal");

INSERT INTO olio1.Card VALUES (1, "1254 1251 1567 4221", 1, 0, 0, "Normal");

INSERT INTO olio1.MasterTransfer VALUES (1, 1, 2, NULL, 5, now(), "Transfer");
INSERT INTO olio1.MasterTransfer VALUES (2, 1, 2, 1, 10, now(), "Transfer");
INSERT INTO olio1.MasterTransfer VALUES (3, NULL, 1, 1, 15, now(), "Deposit");