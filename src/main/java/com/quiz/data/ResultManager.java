package com.quiz.data;

import com.quiz.model.Result;
import com.quiz.util.Constants;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Handles persisting quiz results to data/results.csv.
 * Creates the file and adds a CSV header if needed, then
 * appends one row per completed quiz session.
 */
public class ResultManager {

    /**
     * Saves the given Result to the CSV file.
     * Creates the file (and parent directory) if they don't exist.
     *
     * @param result the completed quiz result
     * @throws IOException if writing fails
     */
    public void save(Result result) throws IOException {
        File file = new File(Constants.RESULTS_FILE);

        // Ensure parent directory exists
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        boolean needsHeader = !file.exists() || file.length() == 0;

        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(file, true))) {   // append = true

            if (needsHeader) {
                bw.write(Constants.RESULTS_HEADER);
                bw.newLine();
            }

            bw.write(result.toCsvRow());
            bw.newLine();
        }
    }

    /**
     * Returns the full content of results.csv as a String,
     * or an empty string if the file doesn't exist yet.
     */
    public String readAll() throws IOException {
        File file = new File(Constants.RESULTS_FILE);
        if (!file.exists()) return "";
        return new String(Files.readAllBytes(Paths.get(Constants.RESULTS_FILE)));
    }
}
