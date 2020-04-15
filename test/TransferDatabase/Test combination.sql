SELECT fromAccountId, fromAccount.iban, fromBank.bic, toAccountId, toAccount.iban, toBank.bic, MasterTransfer.cardId, Card.cardNumber, amount, time, MasterTransfer.type FROM MasterTransfer
LEFT JOIN Account AS fromAccount ON fromAccount.accountId = fromAccountId
LEFT JOIN Account AS toAccount ON toAccount.accountId = toAccountId
LEFT JOIN Users AS fromUser ON fromUser.userId = fromAccount.userId
LEFT JOIN Users AS toUser ON toUser.userId = toAccount.userId
LEFT JOIN Bank AS fromBank ON fromBank.bankId = fromUser.bankId
LEFT JOIN Bank AS toBank ON toBank.bankId = toUser.bankId
LEFT JOIN Card ON Card.cardId = MasterTransfer.cardId
WHERE fromAccountId = 1 OR toAccountId = 1 ORDER BY MasterTransfer.time;