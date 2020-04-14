from utils import expect
import connection
import json
import time

c = connection.Connection()


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
    json_data = "{}"
    return_value = '{"accountId":*,"iban":"*","balance":0}'
    r = c.new_request(
        "POST", "/accounts/createAccount", payload=json_data, authentication=True,
    )
    if not expect(r, code=201, return_value=return_value, wildcard_maximum=24):
        return False

    r = c.new_request(
        "POST", "/accounts/createAccount", payload=json_data, authentication=True
    )
    return expect(r, code=201, return_value=return_value, wildcard_maximum=24)


def test_005():
    print("Get all account (there should be now two)")
    json_data = "{}"
    return_value = '[{"accountId":*,"iban":"*","balance":0},{"accountId":*,"iban":"*","balance":0}]'
    r = c.new_request(
        "POST", "/accounts/getAccounts", payload=json_data, authentication=True
    )
    return expect(r, code=200, return_value=return_value, wildcard_maximum=24)


def test_006():
    print("Create new card")
    json_data = "{}"
    # Get id for the first account
    r = c.new_request(
        "POST", "/accounts/getAccounts", payload=json_data, authentication=True
    )
    accounts = json.loads(r.text)
    account_id = accounts[0].get("accountId")
    json_data = f'{{"accountId":{account_id},"spendingLimit": 6}}'
    return_value = f'{{"cardId":*,"accountId":{account_id},"withdrawLimit":0,"spendingLimit":6,"area":""}}'
    r = c.new_request(
        "POST", "/cards/createCard", payload=json_data, authentication=True
    )
    return expect(r, code=201, return_value=return_value)


def test_007():
    print("Get all cards for specific account.")
    json_data = "{}"
    # Get id for the first account
    r = c.new_request(
        "POST", "/accounts/getAccounts", payload=json_data, authentication=True
    )
    accounts = json.loads(r.text)
    account_id = accounts[0].get("accountId")
    json_data = f'{{"accountId":{account_id}}}'
    return_value = '[{"cardId":*,"withdrawLimit":0,"spendingLimit":6,"area":""}]'
    r = c.new_request("GET", "/cards/getCards", payload=json_data, authentication=True)
    return expect(r, code=200, return_value=return_value)


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
        "POST", "/users/updateUserDetails", payload=json_data, authentication=True
    )
    return expect(r, code=200, return_value=return_value)
