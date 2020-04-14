#!/bin/python

from termcolor import colored
import requests
import databse_functions
from inspect import getmembers, isfunction
import tests


def main():
    reset_database()
    test_number = 0
    tests_successfull = 0
    tests_failed = 0
    # https://stackoverflow.com/questions/139180/how-to-list-all-functions-in-a-python-module
    test_list = [o for o in getmembers(tests) if isfunction(o[1])]
    for test in test_list:
        # Check that it is test
        if not test[0].startswith("test"):
            continue
        # Get the function
        test = test[1]
        test_number += 1

        error = None
        try:
            results = test()
        except requests.exceptions.ConnectionError as e:
            error = e
            print(colored("Unexpected connection error.", "red"))
        except Exception as e:
            error = e
            print(colored("Unknown error occurred.", "red"))
        finally:
            if error:
                print(colored(error, "red"))
                tests_failed += 1
                print(colored(f"Test #{test_number} failed", "red"))
                continue

        if results:
            tests_successfull += 1
            print(colored(f"Test #{test_number} succeeded", "green"))
        else:
            tests_failed += 1
            print(colored(f"Test #{test_number} failed", "red"))
        print()


def reset_database():
    connection = databse_functions.init()
    databse_functions.empty(connection)
    connection.close()


main()
