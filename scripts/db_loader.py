import os
import psycopg2 as pg
from dotenv import load_dotenv
from pathlib import Path
from urllib.parse import urlparse
import argparse

dotenv_path = Path("../.env")
load_dotenv(dotenv_path=dotenv_path)

services = {
    "users": "USERSERVICE",
    "courses": "COURSESERVICE",
    "exercises": "EXERCISESERVICE"
}
BACKUP_DIRECTORY = "../backup"


def connect(hostname, port, database, user, password):
    try:
        con = pg.connect(host=hostname, port=port, database=database, password=password, user=user)
        return con, con.cursor()
    except Exception as e:
        print(e)
        return None

def append_data(cursor, table, columns, data):
    nl = "\n"
    values = ["("+ ", ".join([f"'{cell}'" for cell in row]) + ")" for row in data]
    cursor.execute(f"""
            INSERT INTO {table} ({", ".join(columns)})
            VALUES
                {f",{nl}".join(values)}
        """)
    print("DONE")

def parse_csv(input_name):
    with open(input_name) as f:
        lines = f.read().splitlines()
        data = []
        for line in lines[1:]:
            data.append(line.split(','))
        return lines[0].split(','), data

def delete_all_data(cursor, table):
    cursor.execute(f"DELETE FROM {table}")

parser = argparse.ArgumentParser(description='Backup Avogador databases.')
parser.add_argument("-R", "--replace", default=False, action=argparse.BooleanOptionalAction, help="Replaces all data in the table, deleting old data")
parser.add_argument("-d", "--database", required=True, choices=services.keys(), help="Backup all tables in the specified services")
parser.add_argument("-t", "--table", required=True, help="Space separated pairs <service>:<table>. Example, exercises:trials")
parser.add_argument("-i", "--input", required=True, help="CSV file containing the data to be loaded")
options = parser.parse_args()

service = options.database
service_env = services[service]
user = os.getenv(service_env + "_DATASOURCE_USERNAME")
password = os.getenv(service_env + "_DATASOURCE_PASSWORD")
parsed_url = urlparse(os.getenv(service_env + "_DATASOURCE_URL")[5:])  # the splice excludes the protocol
path = parsed_url.path[1:]
hostname = parsed_url.hostname
port = parsed_url.port

connection, cursor = connect(hostname, port, path, user, password)

columns, data = parse_csv(options.input)
if (options.replace):
    print("This will delete all current data inside the table. Type 'DELETE' to confirm")
    if input() != "DELETE":
        print("Aborted")
        exit()
    delete_all_data(cursor, options.table)

append_data(cursor, options.table, columns, data)
connection.commit()

cursor.close()
connection.close()
