package com.triloco.ehrmachine.applicationManager;

import com.triloco.ehrmachine.applicationManager.model.User;

import java.io.*;

public class SessionManager {
    private static final String SESSION_FILE = "session.dat";
    private static User currentUser;

    static {
    File file = new File(SESSION_FILE);
    if (file.exists()) {
        loadSessionFromFile();
    } else {
        // Create empty session file with null user
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(null);  // write null user object
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentUser = null;
    }
}

    public static void login(User user, boolean rememberMe) {
        currentUser = user;

        if (rememberMe) {
            saveSession(user);
        } else {
            deleteSession();
        }
    }

    public static void logout() {
        currentUser = null;
        deleteSession();
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    // ✅ Optional reloader (manual restore)
    public static void restoreSession() {
        loadSessionFromFile();
    }

    private static void loadSessionFromFile() {
        File file = new File(SESSION_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object obj = ois.readObject();
                if (obj instanceof User) {
                    currentUser = (User) obj;
                } else {
                    System.err.println("Session file corrupted or invalid type.");
                    deleteSession();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                deleteSession();
            }
        } else {
            currentUser = null;
        }
    }

    private static void saveSession(User user) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SESSION_FILE))) {
            oos.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void deleteSession() {
        File file = new File(SESSION_FILE);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                System.err.println("Warning: Unable to delete session file.");
            }
        }
    }
}
