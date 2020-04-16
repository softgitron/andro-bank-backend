import mysql.connector
import os


def init():
    if os.environ.get("MYSQL_PASSWORD") == None:
        password = "YOUR PASSWORD HERE"
    else:
        password = os.environ.get("MYSQL_PASSWORD")

    mydb = mysql.connector.connect(
        host="localhost",
        user="olio1admin",
        passwd=password,
        auth_plugin="mysql_native_password",
    )

    return mydb


def empty(mydb):
    mycursor = mydb.cursor()

    mycursor.execute("USE olio1;", None)
    # Delete all
    mycursor.execute("DELETE FROM FutureTransfer;", None)
    mycursor.execute("DELETE FROM MasterTransfer;", None)
    mycursor.execute("DELETE FROM Card;", None)
    mycursor.execute("DELETE FROM Account;", None)
    mycursor.execute("DELETE FROM Users;", None)

    mydb.commit()
