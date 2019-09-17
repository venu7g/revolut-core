--This script is used for unit test cases, DO NOT CHANGE!


DROP TABLE IF EXISTS account;

CREATE TABLE account (accountId LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
balance DECIMAL(19,4)
);

CREATE UNIQUE INDEX idx_acc on account(accountId,balance);

INSERT INTO Account (balance) VALUES (100.0000);
INSERT INTO Account (balance) VALUES (200.0000);
INSERT INTO Account (balance) VALUES (500.0000);
INSERT INTO Account (balance) VALUES (500.0000);
INSERT INTO Account (balance) VALUES (500.0000);
INSERT INTO Account (balance) VALUES (500.0000);