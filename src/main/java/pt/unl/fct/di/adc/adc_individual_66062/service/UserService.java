package pt.unl.fct.di.adc.adc_individual_66062.service;

import pt.unl.fct.di.adc.adc_individual_66062.model.User;
import pt.unl.fct.di.adc.adc_individual_66062.model.Token;
import pt.unl.fct.di.adc.adc_individual_66062.repository.UserRepository;
import pt.unl.fct.di.adc.adc_individual_66062.repository.TokenRepository;
import pt.unl.fct.di.adc.adc_individual_66062.authentication.JWTToken;

import java.util.*;

public class UserService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public UserService() {
        this.userRepository = new UserRepository();
        this.tokenRepository = new TokenRepository();
    }

    public Map<String, Object> createAccount(String username, String password,
                                             String confirmation, String phone,
                                             String address, String role) {
        Map<String, Object> response = new HashMap<>();

        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                !password.equals(confirmation)) {
            response.put("status", 9906);
            response.put("data", "Invalid input or password mismatch");
            return response;
        }

        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            response.put("status", 9901);
            response.put("data", "Username already exists");
            return response;
        }

        User user = new User(username, password, phone, address, role);
        userRepository.save(user);

        Map<String, String> data = new HashMap<>();
        data.put("username", user.getUsername());
        data.put("role", user.getRole());
        response.put("status", "success");
        response.put("data", data);

        return response;
    }

    public String login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (!userOpt.isPresent()) {
            return null;
        }

        User user = userOpt.get();
        if (!user.getPassword().equals(password)) {
            return null;
        }

        Token token = new Token(user.getUsername(), user.getRole());
        tokenRepository.save(token);

        return JWTToken.create(user.getUsername(), user.getRole());
    }

    public Map<String, Object> showUsers(String requestingUsername, String requestingRole) {
        Map<String, Object> response = new HashMap<>();

        if (!requestingRole.equals("ADMIN") && !requestingRole.equals("BOFFICER")) {
            response.put("status", 9905);
            response.put("data", "Not authorized to view users");
            return response;
        }

        List<User> users = userRepository.findAll();
        List<Map<String, Object>> userList = new ArrayList<>();

        for (User user : users) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("username", user.getUsername());
            userInfo.put("role", user.getRole());
            userInfo.put("phone", user.getPhone());
            userInfo.put("address", user.getAddress());
            userList.add(userInfo);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("users", userList);

        response.put("status", "success");
        response.put("data", data);

        return response;
    }

    public Map<String, Object> deleteAccount(String requestingUsername, String requestingRole,
                                             String usernameToDelete) {
        Map<String, Object> response = new HashMap<>();

        if (!requestingRole.equals("ADMIN")) {
            response.put("status", 9905);
            response.put("data", "Only ADMIN can delete accounts");
            return response;
        }

        Optional<User> userOpt = userRepository.findByUsername(usernameToDelete);
        if (!userOpt.isPresent()) {
            response.put("status", 9902);
            response.put("data", "User not found");
            return response;
        }

        userRepository.delete(usernameToDelete);
        tokenRepository.deleteAllForUser(usernameToDelete);

        Map<String, String> data = new HashMap<>();
        data.put("message", "Account deleted successfully");

        response.put("status", "success");
        response.put("data", data);

        return response;
    }

    public Map<String, Object> modifyAccountAttributes(String requestingUsername, String requestingRole,
                                                       String usernameToModify, String newPhone,
                                                       String newAddress) {
        Map<String, Object> response = new HashMap<>();

        Optional<User> userOpt = userRepository.findByUsername(usernameToModify);
        if (!userOpt.isPresent()) {
            response.put("status", 9902);
            response.put("data", "User not found");
            return response;
        }

        User user = userOpt.get();

        boolean authorized = false;
        if (requestingRole.equals("ADMIN")) {
            authorized = true;
        } else if (requestingRole.equals("BOFFICER") &&
                (user.getRole().equals("USER") ||
                        requestingUsername.equals(usernameToModify))) {
            authorized = true;
        } else if (requestingRole.equals("USER") &&
                requestingUsername.equals(usernameToModify)) {
            authorized = true;
        }

        if (!authorized) {
            response.put("status", 9905);
            response.put("data", "Not authorized to modify this account");
            return response;
        }

        if (newPhone != null && !newPhone.trim().isEmpty()) {
            user.setPhone(newPhone);
        }
        if (newAddress != null && !newAddress.trim().isEmpty()) {
            user.setAddress(newAddress);
        }

        userRepository.update(user);

        Map<String, String> data = new HashMap<>();
        data.put("message", "Updated successfully");

        response.put("status", "success");
        response.put("data", data);

        return response;
    }

    public Map<String, Object> showAuthenticatedSessions(String requestingUsername, String requestingRole) {
        Map<String, Object> response = new HashMap<>();

        if (!requestingRole.equals("ADMIN")) {
            response.put("status", 9905);
            response.put("data", "Only ADMIN can view all sessions");
            return response;
        }

        List<Token> allTokens = tokenRepository.findAll();

        List<Map<String, Object>> sessions = new ArrayList<>();
        for (Token t : allTokens) {
            Map<String, Object> session = new HashMap<>();
            session.put("tokenId", t.getTokenId());
            session.put("username", t.getUsername());
            session.put("role", t.getRole());
            session.put("expiresAt", t.getExpiresAt());
            sessions.add(session);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("sessions", sessions);

        response.put("status", "success");
        response.put("data", data);

        return response;
    }

    public Map<String, Object> showUserRole(String requestingUsername, String requestingRole,
                                            String usernameToCheck) {
        Map<String, Object> response = new HashMap<>();

        if (!requestingRole.equals("ADMIN") && !requestingRole.equals("BOFFICER")) {
            response.put("status", 9905);
            response.put("data", "Not authorized to view user roles");
            return response;
        }

        Optional<User> userOpt = userRepository.findByUsername(usernameToCheck);
        if (!userOpt.isPresent()) {
            response.put("status", 9902);
            response.put("data", "User not found");
            return response;
        }

        User user = userOpt.get();

        Map<String, Object> data = new HashMap<>();
        data.put("username", user.getUsername());
        data.put("role", user.getRole());

        response.put("status", "success");
        response.put("data", data);

        return response;
    }

    public Map<String, Object> changeUserRole(String requestingUsername, String requestingRole,
                                              String usernameToChange, String newRole) {
        Map<String, Object> response = new HashMap<>();

        if (!requestingRole.equals("ADMIN")) {
            response.put("status", 9905);
            response.put("data", "Only ADMIN can change user roles");
            return response;
        }

        Optional<User> userOpt = userRepository.findByUsername(usernameToChange);
        if (!userOpt.isPresent()) {
            response.put("status", 9902);
            response.put("data", "User not found");
            return response;
        }

        User user = userOpt.get();
        user.setRole(newRole);
        userRepository.update(user);

        Map<String, String> data = new HashMap<>();
        data.put("message", "Role updated successfully");

        response.put("status", "success");
        response.put("data", data);

        return response;
    }

    public Map<String, Object> changeUserPassword(String username, String currentPassword,
                                                  String newPassword, String confirmation) {
        Map<String, Object> response = new HashMap<>();

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (!userOpt.isPresent()) {
            response.put("status", 9902);
            response.put("data", "User not found");
            return response;
        }

        User user = userOpt.get();

        if (!user.getPassword().equals(currentPassword)) {
            response.put("status", 9900);
            response.put("data", "Current password is incorrect");
            return response;
        }

        if (!newPassword.equals(confirmation)) {
            response.put("status", 9906);
            response.put("data", "New password and confirmation do not match");
            return response;
        }

        user.setPassword(newPassword);
        userRepository.update(user);

        Map<String, String> data = new HashMap<>();
        data.put("message", "Password changed successfully");

        response.put("status", "success");
        response.put("data", data);

        return response;
    }

    public Map<String, Object> logout(String username) {
        Map<String, Object> response = new HashMap<>();

        tokenRepository.deleteAllForUser(username);

        Map<String, String> data = new HashMap<>();
        data.put("message", "Logged out successfully");

        response.put("status", "success");
        response.put("data", data);

        return response;
    }
}