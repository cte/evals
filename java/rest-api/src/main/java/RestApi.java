import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

class RestApi {

    private final Map<String, User> users = new HashMap<>();

    RestApi(User... initialUsers) {
        for (User user : initialUsers) {
            users.put(user.name(), user);
        }
    }

    String get(String url) {
        if (url.equals("/users")) {
            return usersToJson(new ArrayList<>(users.values()));
        }
        throw new UnsupportedOperationException("Unknown GET endpoint: " + url);
    }

    String get(String url, JSONObject payload) {
        if (url.equals("/users")) {
            List<User> filtered = new ArrayList<>();
            JSONArray names = payload.optJSONArray("users");
            if (names != null) {
                for (int i = 0; i < names.length(); i++) {
                    String name = names.getString(i);
                    User user = users.get(name);
                    if (user != null) {
                        filtered.add(user);
                    }
                }
            }
            return usersToJson(filtered);
        }
        throw new UnsupportedOperationException("Unknown GET endpoint: " + url);
    }

    String post(String url, JSONObject payload) {
        switch (url) {
            case "/add":
                return addUser(payload);
            case "/iou":
                return postIou(payload);
            default:
                throw new UnsupportedOperationException("Unknown POST endpoint: " + url);
        }
    }

    private String addUser(JSONObject payload) {
        String name = payload.getString("user");
        User user = User.builder().setName(name).build();
        users.put(name, user);
        JSONObject result = userToJson(user);
        return result.toString();
    }

    private String postIou(JSONObject payload) {
        String lenderName = payload.getString("lender");
        String borrowerName = payload.getString("borrower");
        double amount = payload.getDouble("amount");

        User lender = users.get(lenderName);
        User borrower = users.get(borrowerName);

        if (lender == null || borrower == null) {
            throw new IllegalArgumentException("Lender or borrower does not exist");
        }

        Map<String, Double> lenderOwes = listToMap(lender.owes());
        Map<String, Double> lenderOwedBy = listToMap(lender.owedBy());
        Map<String, Double> borrowerOwes = listToMap(borrower.owes());
        Map<String, Double> borrowerOwedBy = listToMap(borrower.owedBy());

        // Check if lender owes borrower
        double lenderOwesBorrower = lenderOwes.getOrDefault(borrowerName, 0.0);
        if (lenderOwesBorrower > 0) {
            if (lenderOwesBorrower >= amount) {
                lenderOwes.put(borrowerName, round(lenderOwesBorrower - amount));
                borrowerOwedBy.put(lenderName, round(lenderOwesBorrower - amount));
                amount = 0.0;
            } else {
                amount = round(amount - lenderOwesBorrower);
                lenderOwes.remove(borrowerName);
                borrowerOwedBy.remove(lenderName);
            }
        }

        if (amount > 0) {
            // Borrower owes lender more
            double borrowerOwesLender = borrowerOwes.getOrDefault(lenderName, 0.0);
            borrowerOwes.put(lenderName, round(borrowerOwesLender + amount));
            lenderOwedBy.put(borrowerName, round(lenderOwedBy.getOrDefault(borrowerName, 0.0) + amount));
        }

        users.put(lenderName, buildUser(lenderName, lenderOwes, lenderOwedBy));
        users.put(borrowerName, buildUser(borrowerName, borrowerOwes, borrowerOwedBy));

        List<User> resultUsers = Arrays.asList(users.get(lenderName), users.get(borrowerName));
        resultUsers.sort(Comparator.comparing(User::name));
        return usersToJson(resultUsers);
    }

    private User buildUser(String name, Map<String, Double> owesMap, Map<String, Double> owedByMap) {
        User.Builder builder = User.builder().setName(name);
        for (Map.Entry<String, Double> e : owesMap.entrySet()) {
            if (e.getValue() > 0.0) {
                builder.owes(e.getKey(), e.getValue());
            }
        }
        for (Map.Entry<String, Double> e : owedByMap.entrySet()) {
            if (e.getValue() > 0.0) {
                builder.owedBy(e.getKey(), e.getValue());
            }
        }
        return builder.build();
    }

    private Map<String, Double> listToMap(List<Iou> list) {
        Map<String, Double> map = new HashMap<>();
        for (Iou iou : list) {
            map.put(iou.name, iou.amount);
        }
        return map;
    }

    private String usersToJson(List<User> userList) {
        JSONArray usersArray = new JSONArray();
        userList.sort(Comparator.comparing(User::name));
        for (User user : userList) {
            usersArray.put(userToJson(user));
        }
        JSONObject result = new JSONObject();
        result.put("users", usersArray);
        return result.toString();
    }

    private JSONObject userToJson(User user) {
        JSONObject json = new JSONObject();
        json.put("name", user.name());
        json.put("owes", iouListToJson(user.owes()));
        json.put("owedBy", iouListToJson(user.owedBy()));
        double balance = 0.0;
        for (Iou iou : user.owedBy()) {
            balance += iou.amount;
        }
        for (Iou iou : user.owes()) {
            balance -= iou.amount;
        }
        json.put("balance", round(balance));
        return json;
    }

    private JSONObject iouListToJson(List<Iou> list) {
        JSONObject json = new JSONObject();
        for (Iou iou : list) {
            json.put(iou.name, round(iou.amount));
        }
        return json;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}