package utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import temp.UpdateSheet;

public class CustomerExecutionSchedulerHelper {

    private static final String FS = Constant.FILE_SEPERATOR;

    private static final String SCHEDULER_FILE_PATH =
            Constant.BASE_PATH + FS + "TestData" + FS + "input" + FS + "scheduler";

    // ---------------------------------------------------
    // Utility method to build full file path
    // ---------------------------------------------------
    private Path getFullPath(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("Scheduler file name cannot be null or empty");
        }
        return Paths.get(SCHEDULER_FILE_PATH, fileName);
    }

    // ---------------------------------------------------
    // Clear existing file (create if not exists)
    // ---------------------------------------------------
    public void clearExistingFile(String fileName) {
        Path filePath = getFullPath(fileName);

        try {
            Files.createDirectories(filePath.getParent());
            Files.write(filePath,
                    new byte[0],
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Failed to clear scheduler file: " + fileName);
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------
    // Write UpdateSheet data to file (key=value)
    // ---------------------------------------------------
    public void setCustomerExecutionSchedulerData(UpdateSheet us, String schedulerFile) {

        if (us == null || us.getRowList() == null || us.getRowList().isEmpty()) {
            return;
        }

        Path filePath = getFullPath(schedulerFile);

        try {
            Files.createDirectories(filePath.getParent());

            try (BufferedWriter bw = Files.newBufferedWriter(
                    filePath,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {

                for (Map.Entry<String, String> entry : us.getRowList().entrySet()) {
                    bw.write(entry.getKey() + "=" + entry.getValue());
                    bw.newLine();
                }
            }

        } catch (IOException e) {
            System.err.println("Failed to write scheduler file: " + schedulerFile);
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------
    // Read scheduler data from file
    // ---------------------------------------------------
    public UpdateSheet getCustomerExecutionSchedulerData(String schedulerFile, String activeSheetName) {

        Path filePath = getFullPath(schedulerFile);

        UpdateSheet updateSheet = new UpdateSheet();
        updateSheet.setActiveSheetName(activeSheetName);

        if (!Files.exists(filePath)) {
            return updateSheet;
        }

        try (BufferedReader br = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {

            String line;
            while ((line = br.readLine()) != null) {

                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                // Split only on FIRST '='
                int idx = line.indexOf('=');
                if (idx <= 0) {
                    continue; // invalid line
                }

                String rowIndex = line.substring(0, idx).trim();
                String columnAndValue = line.substring(idx + 1).trim();

                if (!rowIndex.isEmpty()) {
                    updateSheet.setRowList(rowIndex, columnAndValue);
                }
            }

        } catch (IOException e) {
            System.err.println("Failed to read scheduler file: " + schedulerFile);
            e.printStackTrace();
        }

        return updateSheet;
    }
}
