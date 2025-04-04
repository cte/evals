import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

class RestApi {

    private final Map<String, User> users = new HashMap<>();

    RestApi(User... initialUsers) {
        for (User user : initialUsers) {
            this.users.put(user.name(), user);
        }
    }

    String get(String url) {
        if (url.equals("/users")) {
            // No payload: return all users
            List<User> allUsers = new ArrayList<>(users.values());
            return formatUsersResponse(allUsers);
        }
        // Handle other potential GET endpoints if necessary, or return error
        return "{}"; // Or throw an exception for unsupported URL
    }

    String get(String url, JSONObject payload) {
        if (url.equals("/users")) {
            // Payload specifies which users to return
            JSONArray requestedNames = payload.getJSONArray("users");
            List<User> requestedUsers = StreamSupport.stream(requestedNames.spliterator(), false)
                    .map(Object::toString)
                    .filter(users::containsKey)
                    .map(users::get)
                    .collect(Collectors.toList());
            return formatUsersResponse(requestedUsers);
        }
        // Handle other potential GET endpoints if necessary, or return error
        return "{}"; // Or throw an exception for unsupported URL
    }

    String post(String url, JSONObject payload) {
        switch (url) {
            case "/add":
                return addUser(payload);
            case "/iou":
                return addIou(payload);
            default:
                // Handle other potential POST endpoints if necessary, or return error
                return "{}"; // Or throw an exception for unsupported URL
        }
    }

    private String addUser(JSONObject payload) {
        String name = payload.getString("user");
        if (users.containsKey(name)) {
            // Handle error: user already exists (optional based on requirements)
            // For this exercise, assume unique names are guaranteed by caller or overwrite
        }
        User newUser = User.builder().setName(name).build();
        users.put(name, newUser);
        return userToJson(newUser).toString();
    }

    private String addIou(JSONObject payload) {
        String lenderName = payload.getString("lender");
        String borrowerName = payload.getString("borrower");
        double amount = payload.getDouble("amount");

        User lenderOld = users.get(lenderName);
        User borrowerOld = users.get(borrowerName);

        // Calculate net change in debt
        // Positive value means borrower owes lender
        double borrowerOwesLenderAmount = 0;
        for (Iou iou : borrowerOld.owes()) {
            if (iou.name.equals(lenderName)) {
                borrowerOwesLenderAmount = iou.amount;
                break;
            }
        }

        double lenderOwesBorrowerAmount = 0;
        for (Iou iou : lenderOld.owes()) {
            if (iou.name.equals(borrowerName)) {
                lenderOwesBorrowerAmount = iou.amount;
                break;
            }
        }

        // New net amount borrower owes lender
        double newNetBorrowerOwesLender = borrowerOwesLenderAmount - lenderOwesBorrowerAmount + amount;

        // --- Build new Borrower User ---
        User.Builder borrowerNewBuilder = User.builder().setName(borrowerName);
        // Copy IOUs not involving the lender
        borrowerOld.owes().stream()
                .filter(iou -> !iou.name.equals(lenderName))
                .forEach(iou -> borrowerNewBuilder.owes(iou.name, iou.amount));
        borrowerOld.owedBy().stream()
                .filter(iou -> !iou.name.equals(lenderName))
                .forEach(iou -> borrowerNewBuilder.owedBy(iou.name, iou.amount));

        // Add the updated IOU between borrower and lender
        if (newNetBorrowerOwesLender > 1e-9) { // Use tolerance for double comparison
            borrowerNewBuilder.owes(lenderName, newNetBorrowerOwesLender);
        } else if (newNetBorrowerOwesLender < -1e-9) {
            borrowerNewBuilder.owedBy(lenderName, -newNetBorrowerOwesLender);
        }
        User borrowerNew = borrowerNewBuilder.build();

        // --- Build new Lender User ---
        User.Builder lenderNewBuilder = User.builder().setName(lenderName);
        // Copy IOUs not involving the borrower
        lenderOld.owes().stream()
                .filter(iou -> !iou.name.equals(borrowerName))
                .forEach(iou -> lenderNewBuilder.owes(iou.name, iou.amount));
        lenderOld.owedBy().stream()
                .filter(iou -> !iou.name.equals(borrowerName))
                .forEach(iou -> lenderNewBuilder.owedBy(iou.name, iou.amount));

        // Add the updated IOU between lender and borrower (mirror image of borrower's)
        if (newNetBorrowerOwesLender > 1e-9) { // Borrower owes Lender
            lenderNewBuilder.owedBy(borrowerName, newNetBorrowerOwesLender);
        } else if (newNetBorrowerOwesLender < -1e-9) { // Lender owes Borrower
            lenderNewBuilder.owes(borrowerName, -newNetBorrowerOwesLender);
        }
        User lenderNew = lenderNewBuilder.build();

        // Update the map
        users.put(lenderName, lenderNew);
        users.put(borrowerName, borrowerNew);

        // Return updated users, sorted by name
        List<User> updatedUsers = Arrays.asList(lenderNew, borrowerNew);
        return formatUsersResponse(updatedUsers);
    }


    private JSONObject userToJson(User user) {
        JSONObject jsonUser = new JSONObject();
        jsonUser.put("name", user.name());

        JSONObject owesMap = new JSONObject();
        double totalOwes = 0;
        for (Iou iou : user.owes()) {
            owesMap.put(iou.name, iou.amount);
            totalOwes += iou.amount;
        }
        jsonUser.put("owes", owesMap);

        JSONObject owedByMap = new JSONObject();
        double totalOwedBy = 0;
        for (Iou iou : user.owedBy()) {
            owedByMap.put(iou.name, iou.amount);
            totalOwedBy += iou.amount;
        }
        jsonUser.put("owedBy", owedByMap);

        // Round balance to 2 decimal places to avoid floating point issues in comparison
        double balance = Math.round((totalOwedBy - totalOwes) * 100.0) / 100.0;
        jsonUser.put("balance", balance);

        return jsonUser;
    }

    private String formatUsersResponse(List<User> userList) {
        // Sort users by name
        userList.sort(Comparator.comparing(User::name));

        // Convert users to JSON
        JSONArray usersJsonArray = new JSONArray();
        for (User user : userList) {
            usersJsonArray.put(userToJson(user));
        }

        // Format final response
        JSONObject response = new JSONObject();
        response.put("users", usersJsonArray);
        return response.toString();
    }
}