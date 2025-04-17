import json

class RestAPI:
    def __init__(self, database=None):
        if database is None:
            self.database = {"users": []}
        else:
            self.database = database

    def get(self, url, payload=None):
        if url == "/users":
            if payload:
                data = json.loads(payload)
                names = data.get("users", [])
                users = [user for user in self.database["users"] if user["name"] in names]
            else:
                users = self.database["users"]
            # sort users by name
            users_sorted = sorted(users, key=lambda u: u["name"])
            return json.dumps({"users": users_sorted})
        else:
            return json.dumps({})

    def post(self, url, payload=None):
        data = json.loads(payload) if payload else {}
        if url == "/add":
            name = data["user"]
            new_user = {"name": name, "owes": {}, "owed_by": {}, "balance": 0.0}
            self.database["users"].append(new_user)
            return json.dumps(new_user)
        elif url == "/iou":
            lender_name = data["lender"]
            borrower_name = data["borrower"]
            amount = data["amount"]

            lender = next(user for user in self.database["users"] if user["name"] == lender_name)
            borrower = next(user for user in self.database["users"] if user["name"] == borrower_name)

            # Check if lender owes borrower
            if borrower_name in lender["owes"]:
                owed_amount = lender["owes"][borrower_name]
                if owed_amount > amount:
                    lender["owes"][borrower_name] -= amount
                    borrower["owed_by"][lender_name] -= amount
                    amount = 0
                elif owed_amount < amount:
                    amount -= owed_amount
                    del lender["owes"][borrower_name]
                    del borrower["owed_by"][lender_name]
                else:  # owed_amount == amount
                    del lender["owes"][borrower_name]
                    del borrower["owed_by"][lender_name]
                    amount = 0

            # If remaining amount > 0, update lender's owed_by and borrower's owes
            if amount > 0:
                lender["owed_by"].setdefault(borrower_name, 0.0)
                lender["owed_by"][borrower_name] += amount
                borrower["owes"].setdefault(lender_name, 0.0)
                borrower["owes"][lender_name] += amount

            # Update balances
            lender["balance"] = sum(lender["owed_by"].values()) - sum(lender["owes"].values())
            borrower["balance"] = sum(borrower["owed_by"].values()) - sum(borrower["owes"].values())

            # Prepare sorted user list
            users_sorted = sorted([lender, borrower], key=lambda u: u["name"])
            return json.dumps({"users": users_sorted})
        else:
            return json.dumps({})
