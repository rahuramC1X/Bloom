package com.BloomFilter.examination.bloomTest;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class BloomFilterExample {

    public static void main(String[] args) throws IOException {
        // File containing user IDs
        String listUsersFile = "500000_users.txt";
        String additionalUsersFile = "1m_users.txt";
        File f = new File(listUsersFile);
        BufferedReader reader1 = new BufferedReader(new FileReader(listUsersFile));
        BufferedReader reader2 = new BufferedReader(new FileReader(additionalUsersFile));

        long size = f.length();
        String firstElement = reader1.readLine();
        long eachElementSize = firstElement.length(); // Assuming first line is not header
        long expectedInsertions = (size / eachElementSize) * 2;
        double falsePositiveProbability = 0.003;
        int sizeOfBooleanArray = (int) Math.ceil(-(expectedInsertions * Math.log(falsePositiveProbability)) / Math.pow(Math.log(2), 2));
        int numberOfHashFunctions = (int) Math.ceil((sizeOfBooleanArray / (double) expectedInsertions) * Math.log(2));
        System.out.println("Number of Hash function " + numberOfHashFunctions);
        System.out.println("Size of the boolean array " + sizeOfBooleanArray);
        System.out.println("Expected Insertions: " + expectedInsertions);
        BloomFilter<CharSequence> bloomFilter = BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8), expectedInsertions,falsePositiveProbability );

        bloomFilter.put(firstElement);
        int lineCount = 1;
        // Inserting user IDs from the file into the BloomFilter
        try {
            String line;
            while ((line = reader1.readLine()) != null) {
                bloomFilter.put(line);
                lineCount++;
            }
            System.out.println("User IDs have been inserted into the BloomFilter.");
            System.out.println("Total number of users in the file: " + lineCount);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } finally {
            reader1.close(); // Close the reader1 after insertion
        }

        // Check if all members of the file are in the BloomFilter and get the count

        int count = 0;
        int totalCheck = 0;
        try  {
            String line;
            reader1 = new BufferedReader(new FileReader(listUsersFile)); // Reopen reader1 for checking
            while ((line = reader1.readLine()) != null) {
                totalCheck++;
                if (bloomFilter.mightContain(line)) {
                    count++;
                }
            }
            if (count == lineCount) {
                System.out.println("All user IDs in the file are likely in the BloomFilter.");
            }
            else {
                System.out.println("Not all user IDs in the file are in the BloomFilter.");
            }
            reader2 = new BufferedReader(new FileReader(additionalUsersFile)); // Reopen reader2 for checking
            while ((line = reader2.readLine()) != null) {
                totalCheck++;
                if (bloomFilter.mightContain(line)) {
                    count++;
                }
            }
            System.out.println("Total number of userIds checked "+ totalCheck);

           if (count > lineCount) {
                double calculatedFalsePositive = (double) Math.pow((double) 1 - Math.exp((double) - numberOfHashFunctions*(lineCount)/sizeOfBooleanArray), (double) numberOfHashFunctions );
                System.out.println("UserCount: " + lineCount);
                System.out.println("CountOfUsersWhoMayExist: " + count);
                System.out.println("CalculatedFalsePositive: "+ calculatedFalsePositive);
                System.out.println("Actual false positive: " + (double)(count-lineCount)/totalCheck);
                System.out.println("Estimated extra users: " + totalCheck * calculatedFalsePositive );
                System.out.println(count - lineCount + " users are extra");
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } finally {
            reader1.close(); // Close the readers after checking
            reader2.close();
        }
    }
}
