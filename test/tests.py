from utils import expect
import connection
import json
import time

c = connection.Connection()
temporary_values = {}


def test_001():
    print("Get bank information.")
    json_data = "{}"
    return_value = '[{"bankId":0,"name":"Deals","bic":"DEALFIHH"},{"bankId":1,"name":"Bear bank","bic":"BEARFIHH"},{"bankId":2,"name":"Our bank","bic":"OURBFIHH"}]'
    r = c.new_request("GET", "/accounts/getBanks", payload=json_data)
    return expect(r, code=200, return_value=return_value)


def test_002():
    print("Create new user with details.")
    json_data = '{"bankId":0,"email":"henry.master@gmail.com","password":"Hello","phoneNumber":"2451156481","username":"Henry","firstName":"Henry","lastName":"Larson"}'
    r = c.new_request("POST", "/users/createUser", payload=json_data)
    return expect(r, code=201, return_value="OK", authentication_header=True)


def test_003():
    print("Login with new details")
    json_data = '{"email":"henry.master@gmail.com","password":"Hello"}'
    return_value = '{"userId":*,"username":"Henry","firstName":"Henry","lastName":"Larson","email":"henry.master@gmail.com","phoneNumber":"2451156481","bankId":0}'
    r = c.new_request("POST", "/users/login", payload=json_data)
    return expect(r, code=200, return_value=return_value)


def test_004():
    print("Create two new accounts")
    json_data = '{"type":"Normal"}'
    return_value = '{"accountId":*,"iban":"*","balance":0}'
    r = c.new_request(
        "POST", "/accounts/createAccount", payload=json_data, authentication=True,
    )
    if not expect(r, code=201, return_value=return_value, wildcard_maximum=24):
        return False

    r = c.new_request(
        "POST", "/accounts/createAccount", payload=json_data, authentication=True
    )
    # Try to store second account iban
    temporary_values["toIban"] = json.loads(r.text).get("iban")
    return expect(r, code=201, return_value=return_value, wildcard_maximum=24)


def test_005():
    print("Get all account (there should be now two)")
    json_data = "{}"
    return_value = '[{"accountId":*,"iban":"*","balance":0,"type":"Normal"},{"accountId":*,"iban":"*","balance":0,"type":"Normal"}]'
    r = c.new_request(
        "GET", "/accounts/getAccounts", payload=json_data, authentication=True
    )
    accounts = json.loads(r.text)
    temporary_values["accountId"] = accounts[0].get("accountId")
    return expect(r, code=200, return_value=return_value, wildcard_maximum=24)


def test_006():
    print("Create new card")
    json_data = f'{{"accountId":{temporary_values["accountId"]},"spendingLimit": 0}}'
    return_value = f'{{"cardId":*,"cardNumber":*,"accountId":{temporary_values["accountId"]},"withdrawLimit":0,"spendingLimit":0,"area":""}}'
    r = c.new_request(
        "POST", "/cards/createCard", payload=json_data, authentication=True
    )
    # Try to store cardId
    temporary_values["cardId"] = json.loads(r.text).get("cardId")
    return expect(r, code=201, return_value=return_value, wildcard_maximum=22)


def test_007():
    print("Get all cards for specific account.")
    json_data = f'{{"accountId":{temporary_values["accountId"]}}}'
    return_value = '[{"cardId":*,"cardNumber":*,"accountId":*,"withdrawLimit":0,"spendingLimit":0,"area":""}]'
    r = c.new_request("GET", "/cards/getCards", payload=json_data, authentication=True)
    return expect(r, code=200, return_value=return_value, wildcard_maximum=22)


def test_008():
    print("Try to get card information from imaginary account")
    json_data = '{"accountId":99999999}'
    r = c.new_request("GET", "/cards/getCards", payload=json_data)
    return expect(r, code=401, return_value="Authentication is invalid.")


def test_009():
    print("Edit user information")
    json_data = (
        '{"email":"super@gmail.com","phoneNumber":"2452256481","lastName":"Harson"}'
    )
    return_value = '{"username":"Henry","firstName":"Henry","lastName":"Harson","email":"super@gmail.com","phoneNumber":"2452256481"}'
    r = c.new_request(
        "PATCH", "/users/updateUserDetails", payload=json_data, authentication=True
    )
    return expect(r, code=200, return_value=return_value)


def test_010():
    print("Add more balance to account")
    json_data = f'{{"balance":50000,"accountId":{temporary_values["accountId"]}}}'
    return_value = '{"accountId":*,"iban":"*","balance":50000,"type":"Normal"}'
    r = c.new_request(
        "POST", "/accounts/deposit", payload=json_data, authentication=True
    )
    return expect(r, code=200, return_value=return_value, wildcard_maximum=24)


def test_011():
    print("Get all transactions (1)")
    json_data = f'{{"accountId":{temporary_values["accountId"]}}}'
    return_value = '[{"toAccountId":*,"toAccountIban":"*","toAccountBic":"DEALFIHH","amount":50000,"time":"*","type":"Deposit"}]'
    r = c.new_request(
        "GET", "/transactions/getTransactions", payload=json_data, authentication=True
    )
    return expect(r, code=200, return_value=return_value, wildcard_maximum=30)


def test_012():
    print("Withdraw money from account")
    json_data = f'{{"cardId":{temporary_values["cardId"]},"amount":10000}}'
    return_value = '{"accountId":*,"iban":"*","balance":40000,"type":"Normal"}'
    r = c.new_request("POST", "/cards/withdraw", payload=json_data, authentication=True)
    return expect(r, code=200, return_value=return_value, wildcard_maximum=30)


def test_013():
    print("Pay with a card")
    json_data = f'{{"cardId":{temporary_values["cardId"]},"amount":10000}}'
    return_value = '{"accountId":*,"iban":"*","balance":30000,"type":"Normal"}'
    r = c.new_request("POST", "/cards/payment", payload=json_data, authentication=True)
    return expect(r, code=200, return_value=return_value, wildcard_maximum=30)


def test_014():
    print("Check once more that everything is registering to transactions.")
    json_data = f'{{"accountId":{temporary_values["accountId"]}}}'
    return_value = """[{"toAccountId":*,"toAccountIban":"*","toAccountBic":"DEALFIHH","amount":50000,"time":"*","type":"Deposit"},\
{"fromAccountId":*,"fromAccountIban":"*","fromAccountBic":"DEALFIHH","cardId":*,"cardNumber":"*","amount":10000,"time":"*","type":"Withdraw"},\
{"fromAccountId":*,"fromAccountIban":"*","fromAccountBic":"DEALFIHH","cardId":*,"cardNumber":"*","amount":10000,"time":"*","type":"Payment"}]"""
    r = c.new_request(
        "GET", "/transactions/getTransactions", payload=json_data, authentication=True
    )
    return expect(r, code=200, return_value=return_value, wildcard_maximum=30)


def test_015():
    print("Make transfer between accounts")
    json_data = f'{{"fromAccountId":{temporary_values["accountId"]},"toAccountIban":"{temporary_values["toIban"]}","amount":10000}}'
    return_value = '{"accountId":*,"iban":"*","balance":20000,"type":"Normal"}'
    r = c.new_request(
        "POST", "/accounts/transfer", payload=json_data, authentication=True
    )
    return expect(r, code=200, return_value=return_value, wildcard_maximum=30)


def test_016():
    print("Change type of the account")
    json_data = f'{{"accountId":{temporary_values["accountId"]},"type":"Credit"}}'
    return_value = '{"accountId":*,"iban":"*","balance":20000,"type":"Credit"}'
    r = c.new_request(
        "PATCH", "/accounts/updateType", payload=json_data, authentication=True
    )
    return expect(r, code=200, return_value=return_value, wildcard_maximum=24)


def test_017():
    print("Update account with card to savings type")
    json_data = f'{{"accountId":{temporary_values["accountId"]},"type":"Savings"}}'
    return_value = (
        "There is cards attached to the account. Can't update to savings type."
    )
    r = c.new_request(
        "PATCH", "/accounts/updateType", payload=json_data, authentication=True
    )
    return expect(r, code=400, return_value=return_value, wildcard_maximum=24)
