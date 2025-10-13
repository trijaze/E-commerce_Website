package vn.bachhoa.util;

import org.mindrot.jbcrypt.BCrypt;

public class TestPasswordHash {
    public static void main(String[] args) {
        String password = "admin123";
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        System.out.println("Password: " + password);
        System.out.println("Hash: " + hash);
        
        // Test verification
        boolean matches = BCrypt.checkpw(password, hash);
        System.out.println("Verification: " + matches);
    }
}