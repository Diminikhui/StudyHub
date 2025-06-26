import argparse
import re
from tinydb import TinyDB
from tinydb.table import Document

def validate_type(value):
    if re.match(r"^\+7 \d{3} \d{3} \d{2} \d{2}$", value):
        return "phone"
    elif re.match(r"^\d{2}\.\d{2}\.\d{4}$", value) or re.match(r"^\d{4}-\d{2}-\d{2}$", value):
        return "date"
    elif re.match(r"^[^@\s]+@[^@\s]+\.[a-zA-Z0-9]+$", value):
        return "email"
    else:
        return "text"

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("get_tpl")
    args, unknown = parser.parse_known_args()

    user_fields = {}
    for arg in unknown:
        if arg.startswith("--"):
            key, value = arg[2:].split("=", 1)
            user_fields[key] = value

    db = TinyDB("DB.json")
    templates = db.all()

    user_types = {k: validate_type(v) for k, v in user_fields.items()}

    for tpl in templates:
        tpl_fields = {k: v for k, v in tpl.items() if k != "name"}
        if all(k in user_types and user_types[k] == tpl_fields[k] for k in tpl_fields):
            print(tpl["name"])
            return

    print("{")
    for k, v in user_types.items():
        print(f'  "{k}": "{v}",')
    print("}")

if __name__ == "__main__":
    main()