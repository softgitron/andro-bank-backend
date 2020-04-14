import re
from termcolor import colored


def expect(
    request, code=200, return_value="", authentication_header=None, wildcard_maximum=16
):
    # Prepare wildcards
    pattern = convert_to_pattern(return_value, wildcard_maximum)

    # Do authentication header check only if required
    if authentication_header:
        if not "X-Auth-Token" in request.headers:
            print(colored("Authentication header test failed", "red"))
            return False
    if request.status_code != code:
        print(colored("Return code is not expected", "red"))
        print(f"Got '{request.status_code}', expected '{code}'")
        return False

    # Test strings separated by wildcard
    if not re.match(pattern, request.text):
        print(colored("Return value is not expected", "red"))
        print(f"Got '{request.text}', expected '{return_value}'")
        return False

    return True


def convert_to_pattern(string, wildcard_maximum):
    pattern = r"^"
    for letter in string:
        if letter == "*":
            pattern += f".{{,{wildcard_maximum}}}"
        elif letter in (
            ".",
            "^",
            "$",
            "+",
            "?",
            "{",
            "}",
            "[",
            "]",
            "\\",
            "(",
            ")",
            "|",
        ):
            pattern += "\\" + letter
        else:
            pattern += letter
    pattern += "$"
    return pattern
