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
        return con.cursor()
    except Exception as e:
        print(e)
        return None


def get_schema(cursor):
    cursor.execute("""
        SELECT table_name 
        FROM information_schema.tables
        WHERE table_schema = 'public'
    """)
    schema = cursor.fetchall()
    return [table[0] for table in schema]


def get_table_structure(cursor, table):
    cursor.execute(f"""
            SELECT COLUMN_NAME
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_NAME='{table}'
            ORDER BY ORDINAL_POSITION
        """)
    data = cursor.fetchall()
    return [column[0] for column in data]


def get_table_data(cursor, table):
    cursor.execute(f"""
        SELECT *
        FROM {table}
    """)
    data = cursor.fetchall()
    return data


parser = argparse.ArgumentParser(description='Backup Avogador databases.')
parser.add_argument("-a", "--all", default=False, action=argparse.BooleanOptionalAction, help="All tables in all databases")
parser.add_argument("-d", "--databases", nargs="+", choices=services.keys(), default=[], help="Backup all tables in the specified services")
parser.add_argument("-t", "--tables", nargs="+", default=[], help="Space separated pairs <service>:<table>. Example, exercises:trials")
options = parser.parse_args()
selected_tables_dbs = set([table.split(':')[0] for table in options.tables])

for service, service_env in services.items():
    if not options.all and service not in options.databases and service not in selected_tables_dbs:
        continue

    user = os.getenv(service_env + "_DATASOURCE_USERNAME")
    password = os.getenv(service_env + "_DATASOURCE_PASSWORD")
    parsed_url = urlparse(os.getenv(service_env + "_DATASOURCE_URL")[5:])  # the splice excludes the protocol
    path = parsed_url.path[1:]
    hostname = parsed_url.hostname
    port = parsed_url.port

    cursor = connect(hostname, port, path, user, password)
    tables = get_schema(cursor)
    for table in tables:
        if f"{service}:{table}" not in options.tables and service not in options.databases and not options.all:
            continue

        print(f"Backing up {service}:{table}")
        structure = get_table_structure(cursor, table)
        data = get_table_data(cursor, table)

        filename = f"{BACKUP_DIRECTORY}/{service_env.lower()}/{table}.csv"
        os.makedirs(os.path.dirname(filename), exist_ok=True)
        with open(filename, "w") as f:
            f.write(",".join(structure) + '\n')
            for row in data:
                f.write(",".join([str(cell) for cell in row]) + '\n')
