package com.BloomFilter.examination.bloomTest;

import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;

public class WriteRandomUserIdsToFile {

    public static void main(String[] args) {
        int n = 4000000; // Number of random user IDs to generate
        int idLength = 32; // Length of each user ID
        String[] userIDs = new String[n];

        for (int i = 0; i < n; i++) {
            userIDs[i] = generateRandomUserId(idLength);
        }

        String fileName = "4m_users_part2.txt"; // File name to write the user IDs

        try {
            writeUserIdsToFile(fileName, userIDs);
            System.out.println("User IDs have been written to '" + fileName + "'.");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    private static String generateRandomUserId(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder userIdBuilder = new StringBuilder(length);
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            userIdBuilder.append(characters.charAt(random.nextInt(characters.length())));
        }
        return userIdBuilder.toString();
    }

    private static void writeUserIdsToFile(String fileName, String[] userIDs) throws IOException {
        try (FileWriter writer = new FileWriter(fileName)) {
            for (String userID : userIDs) {
                writer.write(userID + System.lineSeparator());
            }
        }
    }
}
