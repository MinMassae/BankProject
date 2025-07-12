-- 1. 会員テーブル (members)
CREATE TABLE members (
                         no INT PRIMARY KEY AUTO_INCREMENT,
                         id VARCHAR(50) NOT NULL UNIQUE,
                         password VARCHAR(100) NOT NULL,
                         name VARCHAR(50),
                         created_at DATETIME
);

-- 2. 口座テーブル (account)
CREATE TABLE account (
                         no INT PRIMARY KEY AUTO_INCREMENT,
                         members_no INT NOT NULL,
                         account_number VARCHAR(30) NOT NULL UNIQUE,
                         balance DECIMAL(12, 2) DEFAULT 0,
                         job VARCHAR(30) NOT NULL,
                         created_at DATETIME,
                         FOREIGN KEY (members_no) REFERENCES members(no)
);

-- 3. 入出金履歴テーブル (account_history)
CREATE TABLE account_history (
                                 no INT PRIMARY KEY AUTO_INCREMENT,
                                 account_no INT NOT NULL,
                                 type VARCHAR(2), -- 入金 / 出金
                                 amount DECIMAL(12, 2),
                                 balance_after DECIMAL(12, 2),
                                 created_at DATETIME,
                                 FOREIGN KEY (account_no) REFERENCES account(no)
);

-- 4. 振込履歴テーブル (transfer_history)
CREATE TABLE transfer_history (
                                  no INT PRIMARY KEY AUTO_INCREMENT,
                                  from_account_no INT NOT NULL,
                                  to_account_no INT NOT NULL,
                                  amount DECIMAL(12, 2) NOT NULL,
                                  created_at DATETIME,
                                  FOREIGN KEY (from_account_no) REFERENCES account(no),
                                  FOREIGN KEY (to_account_no) REFERENCES account(no)
);

-- 5. 利息履歴テーブル (interest_history)
CREATE TABLE interest_history (
                                  no INT PRIMARY KEY AUTO_INCREMENT,
                                  account_no INT NOT NULL,
                                  interest_amount DECIMAL(12, 2),
                                  calculated_at DATETIME,
                                  FOREIGN KEY (account_no) REFERENCES account(no)
);