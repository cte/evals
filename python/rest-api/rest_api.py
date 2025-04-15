import json
from collections import defaultdict

class RestAPI:
    def __init__(self, database=None):
        # Initialize the database. Use a dictionary keyed by user name for efficiency.
        self.database = {}
        if database and "users" in database:
            for user_data in database["users"]:
                user_name = user_data["name"]
                # Ensure all necessary fields exist, initializing if needed
                self.database[user_name] = {
                    "name": user_name,
                    "owes": user_data.get("owes", {}),
                    "owed_by": user_data.get("owed_by", {}),
                }
                # Calculate and store the initial balance for each user
                self.database[user_name]["balance"] = self._calculate_balance(self.database[user_name])

    def _calculate_balance(self, user_data):
        """Helper function to calculate a user's balance."""
        total_owed_by = sum(user_data.get("owed_by", {}).values())
        total_owes = sum(user_data.get("owes", {}).values())
        # Use round to avoid floating point precision issues
        return round(total_owed_by - total_owes, 2)

    def get(self, url, payload=None):
        """Handles GET requests to the API."""
        if url == "/users":
            payload_dict = json.loads(payload) if payload else {}
            user_names_to_get = payload_dict.get("users")

            if user_names_to_get:
                # Retrieve specific users requested in the payload
                result_users = []
                for name in sorted(user_names_to_get): # Sort requested names alphabetically
                    if name in self.database:
                        user_data = self.database[name]
                        # Ensure balance is up-to-date before returning
                        user_data["balance"] = self._calculate_balance(user_data)
                        result_users.append(user_data)
                return json.dumps({"users": result_users})
            else:
                # Retrieve all users if no specific users are requested
                all_users = []
                # Sort all user names alphabetically
                for name in sorted(self.database.keys()):
                     user_data = self.database[name]
                     # Ensure balance is up-to-date
                     user_data["balance"] = self._calculate_balance(user_data)
                     all_users.append(user_data)
                return json.dumps({"users": all_users})
        else:
            # Handle invalid GET URLs
            # Consider returning a more specific HTTP status code in a real API (e.g., 404)
            return json.dumps({"error": "Invalid GET URL"})


    def post(self, url, payload=None):
        """Handles POST requests to the API."""
        if payload is None:
             # Payload is generally required for POST operations in this API
             return json.dumps({"error": "Payload required"})

        # Allow both JSON strings and dictionaries as payload input
        payload_dict = json.loads(payload) if isinstance(payload, str) else payload

        if url == "/add":
            # --- Add User Logic ---
            user_name = payload_dict.get("user")
            if not user_name:
                return json.dumps({"error": "User name required"})
            # Prevent adding duplicate users
            if user_name in self.database:
                 return json.dumps({"error": f"User '{user_name}' already exists"})

            # Create the new user structure
            new_user = {
                "name": user_name,
                "owes": {},
                "owed_by": {},
                "balance": 0.0 # New users start with a balance of 0
            }
            self.database[user_name] = new_user
            # Return the newly created user object as per the specification
            return json.dumps(new_user)

        elif url == "/iou":
            # --- Create IOU Logic ---
            lender_name = payload_dict.get("lender")
            borrower_name = payload_dict.get("borrower")
            amount = payload_dict.get("amount")

            # Validate required fields
            if not all([lender_name, borrower_name, amount is not None]):
                 return json.dumps({"error": "Missing fields for IOU"})
            # Ensure both lender and borrower exist
            if lender_name not in self.database or borrower_name not in self.database:
                 return json.dumps({"error": "Lender or borrower not found"})

            # Handle case where lender and borrower are the same (no transaction needed)
            if lender_name == borrower_name:
                 # Return the user's current state as per spec (list containing the user)
                 user_data = self.database[lender_name]
                 user_data["balance"] = self._calculate_balance(user_data) # Ensure balance is current
                 # Spec requires a list of users, even if only one is involved/returned
                 return json.dumps({"users": [user_data]}) # Sorted not needed for single item


            lender = self.database[lender_name]
            borrower = self.database[borrower_name]
            try:
                amount = float(amount) # Ensure amount is a number
                if amount < 0:
                    return json.dumps({"error": "IOU amount cannot be negative"})
            except ValueError:
                return json.dumps({"error": "Invalid amount format"})


            # --- Complex IOU Settlement Logic ---
            # This logic handles settling debts between the lender and borrower correctly.

            # Step 1: Check if borrower already owes lender. If so, reduce that debt first.
            debt_borrower_owes_lender = borrower.get("owes", {}).get(lender_name, 0)
            if debt_borrower_owes_lender > 0:
                reduction = min(amount, debt_borrower_owes_lender)
                borrower["owes"][lender_name] -= reduction
                lender["owed_by"][borrower_name] -= reduction
                amount -= reduction # Decrease the amount left to process

                # Clean up zero balances in owes/owed_by dictionaries
                if borrower["owes"][lender_name] == 0: del borrower["owes"][lender_name]
                if lender["owed_by"][borrower_name] == 0: del lender["owed_by"][borrower_name]

            # Step 2: If amount still > 0, check if lender owes borrower. Reduce that debt.
            if amount > 0:
                debt_lender_owes_borrower = lender.get("owes", {}).get(borrower_name, 0)
                if debt_lender_owes_borrower > 0:
                    reduction = min(amount, debt_lender_owes_borrower)
                    lender["owes"][borrower_name] -= reduction
                    borrower["owed_by"][lender_name] -= reduction
                    amount -= reduction # Decrease the amount left to process

                    # Clean up zero balances
                    if lender["owes"][borrower_name] == 0: del lender["owes"][borrower_name]
                    if borrower["owed_by"][lender_name] == 0: del borrower["owed_by"][lender_name]

            # Step 3: If amount still > 0, create/increase debt from borrower to lender.
            if amount > 0:
                # Use setdefault to initialize dicts if they don't exist
                borrower.setdefault("owes", {})[lender_name] = borrower.get("owes", {}).get(lender_name, 0) + amount
                lender.setdefault("owed_by", {})[borrower_name] = lender.get("owed_by", {}).get(borrower_name, 0) + amount

            # --- End Complex IOU Settlement Logic ---


            # Update balances for both users involved
            lender["balance"] = self._calculate_balance(lender)
            borrower["balance"] = self._calculate_balance(borrower)

            # Return the updated user objects for lender and borrower, sorted by name
            updated_users = sorted([lender, borrower], key=lambda x: x['name'])
            return json.dumps({"users": updated_users})

        else:
             # Handle invalid POST URLs
             return json.dumps({"error": "Invalid POST URL"})
