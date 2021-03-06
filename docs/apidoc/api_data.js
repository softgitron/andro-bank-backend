define({ "api": [
  {
    "type": "post",
    "url": "/accounts/createAccount",
    "title": "Create new account",
    "version": "1.0.0",
    "name": "createAccount",
    "group": "Account",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "x-access-token",
            "description": "<p>authentication token of the session.</p>"
          }
        ]
      }
    },
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "String",
            "size": "6..7",
            "optional": true,
            "field": "type",
            "defaultValue": "Normal",
            "description": "<p>Type of the account that will be created</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "    HTTP/1.1 201 OK\n{\"accountId\":1,\"iban\":\"FI02 4597 4268 1567 54\",\"balance\":0,\"type\":\"Normal\"}",
          "type": "json"
        }
      ]
    },
    "filename": "server/src/main/java/com/server/routes/AccountRouter.java",
    "groupTitle": "Account"
  },
  {
    "type": "post",
    "url": "/accounts/deposit",
    "title": "Deposit money to account",
    "version": "1.0.0",
    "name": "deposit",
    "group": "Account",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "x-access-token",
            "description": "<p>authentication token of the session.</p>"
          }
        ]
      }
    },
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "Number",
            "size": "0..",
            "optional": false,
            "field": "accountId",
            "description": "<p>Account where money will be saved</p>"
          },
          {
            "group": "Parameter",
            "type": "Number",
            "size": "1..",
            "optional": false,
            "field": "balance",
            "description": "<p>How much will be added to account</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "    HTTP/1.1 200 OK\n{\"accountId\":1,\"iban\":\"FI02 4597 4268 1567 54\",\"balance\":0,\"type\":\"Normal\"}",
          "type": "json"
        }
      ]
    },
    "filename": "server/src/main/java/com/server/routes/AccountRouter.java",
    "groupTitle": "Account"
  },
  {
    "type": "post",
    "url": "/accounts/futureTransfer",
    "title": "Add new future transfer or periodic transfer",
    "version": "1.0.0",
    "name": "futureTransfer",
    "group": "Account",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "x-access-token",
            "description": "<p>authentication token of the session.</p>"
          }
        ]
      }
    },
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "Number",
            "size": "0..",
            "optional": false,
            "field": "fromAccountId",
            "description": "<p>From whitch account</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "size": "22",
            "optional": false,
            "field": "toAccountIban",
            "description": "<p>To what account</p>"
          },
          {
            "group": "Parameter",
            "type": "Number",
            "size": "1..",
            "optional": false,
            "field": "amount",
            "description": "<p>Amount that should be tranferred</p>"
          },
          {
            "group": "Parameter",
            "type": "Number",
            "size": "1..",
            "optional": true,
            "field": "atInterval",
            "defaultValue": "null",
            "description": "<p>How often in minutes transaction should occur</p>"
          },
          {
            "group": "Parameter",
            "type": "Number",
            "size": "1..",
            "optional": true,
            "field": "times",
            "defaultValue": "null",
            "description": "<p>How many times trnsfer should occur</p>"
          },
          {
            "group": "Parameter",
            "type": "Number",
            "size": "1..",
            "optional": false,
            "field": "atTime",
            "description": "<p>When transaction should occur (for the first time)</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "    HTTP/1.1 201 OK\nOK",
          "type": "json"
        }
      ]
    },
    "filename": "server/src/main/java/com/server/routes/AccountRouter.java",
    "groupTitle": "Account"
  },
  {
    "type": "get",
    "url": "/accounts/getAccounts",
    "title": "Gets all accounts",
    "version": "1.0.0",
    "name": "getAccounts",
    "group": "Account",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "x-access-token",
            "description": "<p>authentication token of the session.</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "    HTTP/1.1 200 OK\n[{\"accountId\":1,\"iban\":\"FI02 4597 4268 1567 54\",\"balance\":0,\"type\":\"Normal\"},\n{\"accountId\":2,\"iban\":\"FI58 4897 1864 8648 45\",\"balance\":0,\"type\":\"Savings\"}]",
          "type": "json"
        }
      ]
    },
    "filename": "server/src/main/java/com/server/routes/AccountRouter.java",
    "groupTitle": "Account"
  },
  {
    "type": "post",
    "url": "/accounts/transfer",
    "title": "Transfer from one account to another",
    "version": "1.0.0",
    "name": "transfer",
    "group": "Account",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "x-access-token",
            "description": "<p>authentication token of the session.</p>"
          }
        ]
      }
    },
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "Number",
            "size": "0..",
            "optional": false,
            "field": "fromAccountId",
            "description": "<p>From whitch account</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "size": "22",
            "optional": false,
            "field": "toAccountIban",
            "description": "<p>To what account</p>"
          },
          {
            "group": "Parameter",
            "type": "Number",
            "size": "0..",
            "optional": false,
            "field": "amount",
            "description": "<p>Amount that should be tranferred</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "    HTTP/1.1 200 OK\n{\"accountId\":1,\"iban\":\"FI02 4597 4268 1567 54\",\"balance\":100,\"type\":\"Normal\"}",
          "type": "json"
        }
      ]
    },
    "filename": "server/src/main/java/com/server/routes/AccountRouter.java",
    "groupTitle": "Account"
  },
  {
    "type": "patch",
    "url": "/accounts/updateType",
    "title": "Update type of the account",
    "version": "1.0.0",
    "name": "updateType",
    "group": "Account",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "x-access-token",
            "description": "<p>authentication token of the session.</p>"
          }
        ]
      }
    },
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "Number",
            "size": "0..",
            "optional": false,
            "field": "accountId",
            "description": "<p>From whitch account</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "size": "6..7",
            "optional": false,
            "field": "type",
            "description": "<p>To what account</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "    HTTP/1.1 200 OK\n{\"accountId\":1,\"iban\":\"FI02 4597 4268 1567 54\",\"balance\":100,\"type\":\"Credit\"}",
          "type": "json"
        }
      ]
    },
    "filename": "server/src/main/java/com/server/routes/AccountRouter.java",
    "groupTitle": "Account"
  },
  {
    "type": "get",
    "url": "/accounts/getBanks",
    "title": "Get all available banks",
    "version": "1.0.0",
    "name": "getBanks",
    "group": "Bank",
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "    HTTP/1.1 200 OK\n[{\"bankId\":0,\"name\":\"Deals\",\"bic\":\"DEALFIHH\"},\n{\"bankId\":1,\"name\":\"Bear bank\",\"bic\":\"BEARFIHH\"},\n{\"bankId\":2,\"name\":\"Our bank\",\"bic\":\"OURBFIHH\"}]",
          "type": "json"
        }
      ]
    },
    "filename": "server/src/main/java/com/server/routes/AccountRouter.java",
    "groupTitle": "Bank"
  },
  {
    "type": "post",
    "url": "/cards/createCards",
    "title": "Create new card for the account",
    "version": "1.0.0",
    "name": "createCards",
    "group": "Card",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "x-access-token",
            "description": "<p>authentication token of the session.</p>"
          }
        ]
      }
    },
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "Number",
            "size": "0..",
            "optional": false,
            "field": "accountId",
            "description": "<p>Id of the account where card is attached to</p>"
          },
          {
            "group": "Parameter",
            "type": "Number",
            "size": "0..",
            "optional": true,
            "field": "withdrawLimit",
            "defaultValue": "0",
            "description": "<p>Limit of the withdrawals (0 means no limit)</p>"
          },
          {
            "group": "Parameter",
            "type": "Number",
            "size": "0..",
            "optional": true,
            "field": "paymnetLimit",
            "defaultValue": "0",
            "description": "<p>Limit of the payments (0 means no limit)</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": true,
            "field": "area",
            "description": "<p>Area where payments can be only processed (&quot;&quot; means no limit)</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "    HTTP/1.1 201 OK\n{\"cardId\":1,\"cardNumber\":\"1025 5879 5483 2858\",\"accountId\":1,\"withdrawLimit\":0,\"spendingLimit\":0,\"area\":\"\"}",
          "type": "json"
        }
      ]
    },
    "filename": "server/src/main/java/com/server/routes/CardRouter.java",
    "groupTitle": "Card"
  },
  {
    "type": "post",
    "url": "/cards/getCards",
    "title": "Get cards that are attached to account",
    "version": "1.0.0",
    "name": "getCards",
    "group": "Card",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "x-access-token",
            "description": "<p>authentication token of the session.</p>"
          }
        ]
      }
    },
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "Number",
            "size": "0..",
            "optional": false,
            "field": "accountId",
            "description": "<p>Id of the account where cards are attached to.</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "    HTTP/1.1 200 OK\n[{\"cardId\":1,\"cardNumber\":\"1025 5879 5483 2858\",\"accountId\":1,\"withdrawLimit\":0,\"spendingLimit\":0,\"area\":\"\"}]",
          "type": "json"
        }
      ]
    },
    "filename": "server/src/main/java/com/server/routes/CardRouter.java",
    "groupTitle": "Card"
  },
  {
    "type": "post",
    "url": "/cards/payment",
    "title": "Make payment using card",
    "version": "1.0.0",
    "name": "payment",
    "group": "Card",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "x-access-token",
            "description": "<p>authentication token of the session.</p>"
          }
        ]
      }
    },
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "Number",
            "size": "0..",
            "optional": false,
            "field": "cardId",
            "description": "<p>Id of the card that is used.</p>"
          },
          {
            "group": "Parameter",
            "type": "Number",
            "size": "0..",
            "optional": false,
            "field": "amount",
            "description": "<p>Amount that is payed from the account.</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "    HTTP/1.1 200 OK\n{\"accountId\":1,\"iban\":\"FI02 4597 4268 1567 54\",\"balance\":100,\"type\":\"Credit\"}",
          "type": "json"
        }
      ]
    },
    "filename": "server/src/main/java/com/server/routes/CardRouter.java",
    "groupTitle": "Card"
  },
  {
    "type": "post",
    "url": "/cards/updateCard",
    "title": "Update details of the card",
    "version": "1.0.0",
    "name": "updateCard",
    "group": "Card",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "x-access-token",
            "description": "<p>authentication token of the session.</p>"
          }
        ]
      }
    },
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "Number",
            "size": "0..",
            "optional": false,
            "field": "cardId",
            "description": "<p>Id of the card that is being updated</p>"
          },
          {
            "group": "Parameter",
            "type": "Number",
            "size": "0..",
            "optional": true,
            "field": "withdrawLimit",
            "defaultValue": "0",
            "description": "<p>Limit of the withdrawals (0 means no limit)</p>"
          },
          {
            "group": "Parameter",
            "type": "Number",
            "size": "0..",
            "optional": true,
            "field": "paymnetLimit",
            "defaultValue": "0",
            "description": "<p>Limit of the payments (0 means no limit)</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": true,
            "field": "area",
            "description": "<p>Area where payments can be only processed (&quot;&quot; means no limit)</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "    HTTP/1.1 200 OK\n{\"cardId\":1,\"cardNumber\":\"1025 5879 5483 2858\",\"accountId\":1,\"withdrawLimit\":0,\"spendingLimit\":0,\"area\":\"\"}",
          "type": "json"
        }
      ]
    },
    "filename": "server/src/main/java/com/server/routes/CardRouter.java",
    "groupTitle": "Card"
  },
  {
    "type": "post",
    "url": "/cards/withdraw",
    "title": "Make withdraw using card",
    "version": "1.0.0",
    "name": "withdraw",
    "group": "Card",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "x-access-token",
            "description": "<p>authentication token of the session.</p>"
          }
        ]
      }
    },
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "Number",
            "size": "0..",
            "optional": false,
            "field": "cardId",
            "description": "<p>Id of the card that is used.</p>"
          },
          {
            "group": "Parameter",
            "type": "Number",
            "size": "0..",
            "optional": false,
            "field": "amount",
            "description": "<p>Amount that is withdrawn from the account.</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "    HTTP/1.1 200 OK\n{\"accountId\":1,\"iban\":\"FI02 4597 4268 1567 54\",\"balance\":100,\"type\":\"Credit\"}",
          "type": "json"
        }
      ]
    },
    "filename": "server/src/main/java/com/server/routes/CardRouter.java",
    "groupTitle": "Card"
  },
  {
    "type": "delete",
    "url": "/transactions/deleteFutureTransaction",
    "title": "Remove future transaction",
    "version": "1.0.0",
    "name": "deleteFutureTransaction",
    "group": "Transaction",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "x-access-token",
            "description": "<p>authentication token of the session.</p>"
          }
        ]
      }
    },
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "Number",
            "size": "0..",
            "optional": false,
            "field": "futureTransferId",
            "description": "<p>Id of the future transaction to be removed</p>"
          },
          {
            "group": "Parameter",
            "type": "Number",
            "size": "0..",
            "optional": false,
            "field": "fromAccountId",
            "description": "<p>ID account that future transaction is related to</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "    HTTP/1.1 200 OK\nOK",
          "type": "json"
        }
      ]
    },
    "filename": "server/src/main/java/com/server/routes/TransactionRouter.java",
    "groupTitle": "Transaction"
  },
  {
    "type": "post",
    "url": "/transactions/getFutureTransactions",
    "title": "Get future or periodic transactions",
    "version": "1.0.0",
    "name": "getFutureTransactions",
    "group": "Transaction",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "x-access-token",
            "description": "<p>authentication token of the session.</p>"
          }
        ]
      }
    },
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "Number",
            "size": "0..",
            "optional": false,
            "field": "accountId",
            "description": "<p>Id of the account where futureTransactions are retrieved</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "    HTTP/1.1 200 OK\n[{\"toAccountId\":1,\"toAccountIban\":\"FI24 5864 8568 9554 87\",\"toAccountBic\":\"DEALFIHH\",\n\"amount\":50000,\"time\":\"1.5.2020 15:41 PM\",\"type\":\"Deposit\"},\n{\"fromAccountId\":2,\"fromAccountIban\":\"FI58 9348 5687 5324 67\",\"fromAccountBic\":\"DEALFIHH\",\n\"cardId\":1,\"cardNumber\":\"1254 8658 9425 7896\",\"amount\":10000,\"time\":1588338156,\"type\":\"Withdraw\"},\n{\"fromAccountId\":3,\"fromAccountIban\":\"FI98 2357 8654 1598 65\",\"fromAccountBic\":\"DEALFIHH\",\n\"cardId\":1,\"cardNumber\":\"1254 8658 9425 7896\",\"amount\":10000,\"time\":1588338156,\"type\":\"Payment\"}]",
          "type": "json"
        }
      ]
    },
    "filename": "server/src/main/java/com/server/routes/TransactionRouter.java",
    "groupTitle": "Transaction"
  },
  {
    "type": "post",
    "url": "/transactions/getTransactions",
    "title": "Get transactions of the account",
    "version": "1.0.0",
    "name": "getTransactions",
    "group": "Transaction",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "x-access-token",
            "description": "<p>authentication token of the session.</p>"
          }
        ]
      }
    },
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "Number",
            "size": "0..",
            "optional": false,
            "field": "accountId",
            "description": "<p>Id of the account where transactions are retrieved</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "    HTTP/1.1 200 OK\n[{\"toAccountId\":1,\"toAccountIban\":\"FI24 5864 8568 9554 87\",\"toAccountBic\":\"DEALFIHH\",\n\"amount\":50000,\"time\":\"1.5.2020 15:41 PM\",\"type\":\"Deposit\"},\n{\"fromAccountId\":2,\"fromAccountIban\":\"FI58 9348 5687 5324 67\",\"fromAccountBic\":\"DEALFIHH\",\n\"cardId\":1,\"cardNumber\":\"1254 8658 9425 7896\",\"amount\":10000,\"time\":1588338156,\"type\":\"Withdraw\"},\n{\"fromAccountId\":3,\"fromAccountIban\":\"FI98 2357 8654 1598 65\",\"fromAccountBic\":\"DEALFIHH\",\n\"cardId\":1,\"cardNumber\":\"1254 8658 9425 7896\",\"amount\":10000,\"time\":1588338156,\"type\":\"Payment\"}]",
          "type": "json"
        }
      ]
    },
    "filename": "server/src/main/java/com/server/routes/TransactionRouter.java",
    "groupTitle": "Transaction"
  },
  {
    "type": "post",
    "url": "/users/createUser",
    "title": "Create new user to specific bank",
    "version": "1.0.0",
    "name": "createUser",
    "group": "User",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "x-access-token",
            "description": "<p>authentication token of the session.</p>"
          }
        ]
      }
    },
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "Integer",
            "size": "0..",
            "optional": false,
            "field": "bankId",
            "description": "<p>Id of the bank where account is created</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "size": "3..",
            "optional": false,
            "field": "username",
            "description": "<p>Username of the new user</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "size": "3..",
            "optional": false,
            "field": "firstName",
            "description": "<p>First name of the new user</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "size": "3..",
            "optional": false,
            "field": "lastName",
            "description": "<p>Last name of the new user</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "size": "6..",
            "optional": false,
            "field": "email",
            "description": "<p>Email of the new user</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "size": "6..",
            "optional": false,
            "field": "phoneNumber",
            "description": "<p>Phonenumber of the new user</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "size": "12..",
            "optional": false,
            "field": "password",
            "description": "<p>Password of the new user</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "    HTTP/1.1 201 OK\n{\"username\":\"Henry\",\"firstName\":\"Henry\",\"lastName\":\"Harson\",\"email\":\"super@gmail.com\",\"phoneNumber\":\"2452256481\"}",
          "type": "json"
        }
      ]
    },
    "filename": "server/src/main/java/com/server/routes/UserRouter.java",
    "groupTitle": "User"
  },
  {
    "type": "post",
    "url": "/users/login",
    "title": "Login",
    "version": "1.0.0",
    "name": "login",
    "group": "User",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "Number",
            "size": "0..",
            "optional": false,
            "field": "bankId",
            "description": "<p>Id of the bank</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "size": "3..",
            "optional": false,
            "field": "email",
            "description": "<p>Username of the new user</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "size": "12..",
            "optional": false,
            "field": "password",
            "description": "<p>Password of the new user</p>"
          }
        ]
      }
    },
    "header": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n{x-access-token: \"eyhniuhnhuiohaw==hjhuihuehiuguj==\"}",
          "type": "json"
        }
      ]
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "    HTTP/1.1 201 OK\n{\"username\":\"Henry\",\"firstName\":\"Henry\",\"lastName\":\"Harson\",\"email\":\"super@gmail.com\",\"phoneNumber\":\"2452256481\"}",
          "type": "json"
        }
      ]
    },
    "filename": "server/src/main/java/com/server/routes/UserRouter.java",
    "groupTitle": "User"
  },
  {
    "type": "patch",
    "url": "/users/updateUserDetails",
    "title": "Update details of the user",
    "version": "1.0.0",
    "name": "updateUserDetails",
    "group": "User",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "x-access-token",
            "description": "<p>authentication token of the session.</p>"
          }
        ]
      }
    },
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "String",
            "size": "3..",
            "optional": true,
            "field": "username",
            "description": "<p>Username of the updated user</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "size": "3..",
            "optional": true,
            "field": "firstName",
            "description": "<p>First name of the updated user</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "size": "3..",
            "optional": true,
            "field": "lastName",
            "description": "<p>Last name of the updated user</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "size": "6..",
            "optional": true,
            "field": "email",
            "description": "<p>Email of the updated user</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "size": "6..",
            "optional": true,
            "field": "phoneNumber",
            "description": "<p>Phonenumber of the updated user</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "size": "12..",
            "optional": true,
            "field": "password",
            "description": "<p>Password of the updated user</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "    HTTP/1.1 201 OK\n{\"username\":\"Henry\",\"firstName\":\"Henry\",\"lastName\":\"Harson\",\"email\":\"super@gmail.com\",\"phoneNumber\":\"2452256481\"}",
          "type": "json"
        }
      ]
    },
    "filename": "server/src/main/java/com/server/routes/UserRouter.java",
    "groupTitle": "User"
  }
] });
