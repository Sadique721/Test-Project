package com.savbill.commonGateway.core.utillity.fileUtillity;


import com.savbill.commonGateway.common.service.ClientServiceSrv;
import com.savbill.commonGateway.constants.DocumentConstants;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.core.exceptions.FileNotCreatedException;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.PolyGone;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


@Component
public class FileUtility {

    private static final String MODULE = " [File Utility ] ";
    @Autowired
    private ClientServiceSrv clientService;

    private static final Logger logger = LoggerFactory.getLogger(FileUtility.class);

    public String saveFileToServer(MultipartFile argFile, String path) throws IOException {
        String SUBMODULE = MODULE + " [saveFileToServer()] ";
        Assert.notNull(path, "Path should not be empty");
        String fileName = "Test";

        int allowedFileSize = clientService.getByName(DocumentConstants.ALLOWED_DOCUMENT_SIZE) != null ? Integer.parseInt(clientService.getByName(DocumentConstants.ALLOWED_DOCUMENT_SIZE).getValue()) : 2;
        if (argFile.getSize() > ((long) allowedFileSize * 1024 * 1024))
            throw new RuntimeException("File size limit exceeds. Please provide document within " + allowedFileSize + "MB");

        if (null != argFile) {
            fileName = (null != argFile.getOriginalFilename()) ? argFile.getOriginalFilename().replace("/", "_").trim() : fileName;
        }
//        path="D:\\";
        File file = new File(path + System.currentTimeMillis() + "_" + fileName);
        File directory = new File(path);
        try {
            if (!directory.exists()) {
                directory.mkdir();
            }
            boolean isCreated = file.createNewFile();
            if (!isCreated) {
                throw new FileNotCreatedException();
            }
            if (null != argFile) {
                FileOutputStream fout = new FileOutputStream(file);
                fout.write(argFile.getBytes());
                fout.close();
            }
            return file.getName();
        } catch (IOException e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            throw new FileNotCreatedException();
        }
    }

    /** Generic method for List Of Multipart Files */
    public HashMap<String,StringBuilder> saveMultipleFiles(MultipartFile[] files, String path) throws IOException {
        StringBuilder uniqueNames = new StringBuilder();
        StringBuilder fileNames = new StringBuilder();
        HashMap<String,StringBuilder> map = new HashMap<>();
        logger.info(":::::::::::::::Inside Save Multiple Files Method::::::::::::::::::");
        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String uniqueName = saveFile(file, path);
                    if (fileNames.length() > 0) {
                        fileNames.append(",");
                    }
                    fileNames.append(file.getOriginalFilename());
                    if (uniqueNames.length() > 0) {
                        uniqueNames.append(",");
                    }
                    uniqueNames.append(uniqueName);
                    map.put("uniqueNames",uniqueNames);
                    map.put("fileNames",fileNames);
                }
            }
        }
        return map;
    }

    public String saveFile(MultipartFile argFile, String path) throws IOException {
        Assert.notNull(path, "Path should not be empty");
        String fileName = "Test";
        Path fullPath = Paths.get(path);
        File directory = fullPath.toFile();
        if (!directory.exists()) {
            boolean dirsCreated = directory.mkdirs();
            if (!dirsCreated) {
                throw new IOException("Failed to create directories: " + fullPath);
            }
        }
        if (null != argFile) {
            fileName = (null != argFile.getOriginalFilename()) ? argFile.getOriginalFilename().replace("/", "_").trim() : fileName;
        }
        File file = new File(path+ System.currentTimeMillis() + "_" + fileName);
        try (FileOutputStream fout = new FileOutputStream(file)) {
            // Write file contents
            fout.write(argFile.getBytes());
            logger.info("File Uploaded successfully on path: "+path);
        } catch (IOException e) {
            logger.error(":::::Exception While Saving File:::: " + e.getMessage(), e);
            throw new FileNotCreatedException();
        }
        return file.getName();
    }


    public String saveFileToServerForTicket(MultipartFile argFile, String path) throws IOException {
        String SUBMODULE = MODULE + " [saveFileToServerForTicket()] ";
        Assert.notNull(path, "Path should not be empty");
        String fileName = "Test";

//        int allowedFileSize = clientService.getByName(DocumentConstants.ALLOWED_TICKET_DOCUMENT_SIZE) != null ? Integer.parseInt(clientService.getByName(DocumentConstants.ALLOWED_TICKET_DOCUMENT_SIZE).getValue()) : 500;
        if (argFile.getSize() > ((long) 0 * 8 * 1000))
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "File size limit exceeds. Please provide document within " + 0 + "KB", null);
        if (null != argFile) {
            fileName = (null != argFile.getOriginalFilename()) ? argFile.getOriginalFilename().replace("/", "_").trim() : fileName;
        }
//        path="D:\\";
        File file = new File(path + System.currentTimeMillis() + "_" + fileName);

        logger.info("===================== Absolute Path :-" + file.getAbsolutePath() + " File Details : -  " + file.toString() + "=====================");
        File directory = new File(path);
        try {
            if (!directory.exists()) {
                directory.mkdir();
            }
            logger.info("=====================Directory Path :- " + directory.getPath() + "=====================");
            boolean isCreated = file.createNewFile();
            if (!isCreated) {
                throw new FileNotCreatedException();
            }

            if (null != argFile) {
                FileOutputStream fout = new FileOutputStream(file);
                fout.write(argFile.getBytes());
                fout.close();
            }
            return file.getName();
        } catch (
                IOException e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            throw new FileNotCreatedException();
        }

    }

    public boolean removeFileAtServer(String argFile, String path) {
        boolean isFileDeleted = false;
        String SUBMODULE = MODULE + " [saveFileToServer()] ";
        Assert.notNull(path, "Path should not be empty");
        try {
            File file = new File(path + argFile);
            if (null == file) {
                ApplicationLogger.logger.debug(SUBMODULE + "File not found with name" + file.getName());
            }
            if (null != file && file.delete()) {
                isFileDeleted = true;
            }
            return isFileDeleted;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public boolean removeFileAtServer(String path) {
        boolean isFileDeleted = false;
        String SUBMODULE = MODULE + " [saveFileToServer()] ";
        Assert.notNull(path, "Path should not be empty");
        try {
            File file = new File(path);
            if (null == file) {
                ApplicationLogger.logger.debug(SUBMODULE + "File not found with name" + file.getName());
            }
            if (null != file && file.delete()) {
                isFileDeleted = true;
            }
            return isFileDeleted;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public MultipartFile getFileFromArray(String fileName, MultipartFile[] files) {
        boolean isAvailable = false;
        try {
            Integer allowedFileSize = clientService.getByName(DocumentConstants.ALLOWED_DOCUMENT_SIZE) != null ? Integer.parseInt(clientService.getByName(DocumentConstants.ALLOWED_DOCUMENT_SIZE).getValue()) : 2;
            for (MultipartFile file : files) {
                if (file.getSize() > (allowedFileSize * 1024 * 1024))
                    throw new RuntimeException("File size limit exceeds. Please provide document within " + allowedFileSize + "MB");
                if (fileName.equalsIgnoreCase(file.getOriginalFilename())) {
                    isAvailable = true;
                    return file;
                }
            }
        } catch (Exception ex) {
            throw ex;
        }
        return null;
    }

    public MultipartFile getFileFromArrayForTicket(MultipartFile files) {
//        int allowedFileSize = clientService.getByName(DocumentConstants.ALLOWED_TICKET_DOCUMENT_SIZE) != null ? Integer.parseInt(clientService.getByName(DocumentConstants.ALLOWED_TICKET_DOCUMENT_SIZE).getValue()) : 500;
        if (files.getSize() > (long) 0 * 8 * 1000)
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "File size limit exceeds. Please provide document within " + 0 + "KB",null);
        else {
            return files;
        }

    }

    //List<MacSerialListDTO> list = fileUtility.readCsv(MacSerialListDTO.class, file.getInputStream());
    public <T> List<T> readCsv(Class<T> clazz, InputStream stream) throws IOException {
        try {
            CsvMapper mapper = new CsvMapper();
            CsvSchema schema = mapper.typedSchemaFor(clazz)
                    .withColumnSeparator(CsvSchema.DEFAULT_COLUMN_SEPARATOR)
                    .withHeader()
                    .withColumnReordering(true)
                    .withArrayElementSeparator(CsvSchema.DEFAULT_ARRAY_ELEMENT_SEPARATOR)
                    .withNullValue(StringUtils.EMPTY)
                    .withoutEscapeChar();
            return mapper
                    .readerFor(clazz)
                    .with(CsvParser.Feature.TRIM_SPACES)
                    .with(CsvParser.Feature.SKIP_EMPTY_LINES)
                    .with(schema)
                    .<T>readValues(stream)
                    .readAll();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Exception While reading CSV file: "+ex.getMessage());
        }
    }

    public List<PolyGone> readKml(File kmlFile) throws IOException {
        try {
            // Create a DocumentBuilderFactory object
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // Create a DocumentBuilder object
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Parse the KML file
            Document document = builder.parse(kmlFile);

            // Get the root element of the document
            Element rootElement = document.getDocumentElement();

            // Get the Placemark elements
            NodeList placemarkElements = rootElement.getElementsByTagName("Placemark");

            // Create a list to store the coordinates
            List<PolyGone> coordinates = new ArrayList<>();

            // Iterate over the Placemark elements
            for (int i = 0; i < placemarkElements.getLength(); i++) {
                // Get the Placemark element
                Element placemarkElement = (Element) placemarkElements.item(i);

                // Get the coordinates of the Placemark
                String coordinatesString = placemarkElement.getElementsByTagName("coordinates").item(0).getTextContent();

                // Split the coordinates string into an array of strings
                String[] coordinateStrings = coordinatesString.split(",");

                // Create a Coordinate object for each coordinate string
                for (String coordinateString : coordinateStrings) {
                    // Split the coordinate string into longitude and latitude
                    String[] longitudeLatitude = coordinateString.split(" ");

                    // Create a Coordinate object
                    PolyGone polyGone = new PolyGone(longitudeLatitude[0], longitudeLatitude[1]);
                    // Add the coordinate to the list
                    coordinates.add(polyGone);
                }
            }

            // Print the coordinates
            for (PolyGone coordinate : coordinates) {
                System.out.println(coordinate.getLat() + ", " + coordinate.getLng());
            }
            return coordinates;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Exception While reading CSV file: "+ex.getMessage());
        }
    }

    public List<PolyGone> readJeoJson(File jeoJson) {
        try {
            // Read the GeoJSON file
            FileReader reader = new FileReader("geojson_file.geojson");
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(reader);
            List<PolyGone> coordinates = new ArrayList<>();
            // Get the coordinates from the GeoJSON file
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonObject geometry = jsonObject.get("geometry").getAsJsonObject();
            String type = geometry.get("type").getAsString();
            if (type.equals("Point")) {
                PolyGone polyGone = new PolyGone(geometry.get("coordinates").getAsJsonArray().get(0).toString(), geometry.get("coordinates").getAsJsonArray().get(1).toString());
                coordinates.add(polyGone);
            } else if (type.equals("LineString")) {
                for (JsonElement coordinate : geometry.get("coordinates").getAsJsonArray()) {
                    PolyGone polyGone = new PolyGone(coordinate.getAsJsonArray().get(0).toString(), coordinate.getAsJsonArray().get(1).toString());
                    coordinates.add(polyGone);
                }
                System.out.println(coordinates);
            } else if (type.equals("Polygon")) {
                for (JsonElement coordinate : geometry.get("coordinates").getAsJsonArray().get(0).getAsJsonArray()) {
                    PolyGone polyGone = new PolyGone(coordinate.getAsJsonArray().get(0).toString(), coordinate.getAsJsonArray().get(1).toString());
                    coordinates.add(polyGone);
                }
                System.out.println(coordinates);
            } else {
                System.out.println("Unsupported GeoJSON type: " + type);
            }
            return coordinates;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Exception While reading GeoJson file: "+ex.getMessage());
        }
    }

    public File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
        try (OutputStream outputStream = new FileOutputStream(file)) {
            InputStream inputStream = multipartFile.getInputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        return file;
    }

    public static void export(List<PolyGone> objects, String fileName)  {
        try {
            Writer writer = new FileWriter(fileName);
            HeaderColumnNameMappingStrategy<PolyGone> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(PolyGone.class);
            StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .withMappingStrategy(strategy)
                    .build();
            beanToCsv.write(objects);
            writer.close();
        } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Unable to write data in CSV: "+ex.getMessage());
        }
    }



    public String generateCsv(List<PolyGone> polyGoneList) {
        final String CSV_HEADER = "lat,lng,polygoneName\n";
        StringBuilder csvContent = new StringBuilder();
        csvContent.append(CSV_HEADER);

        for (PolyGone polyGone : polyGoneList) {
            csvContent.append(polyGone.getLat()).append(",")
                    .append(polyGone.getLng()).append(",")
                    .append(polyGone.getPolygoneName()).append("\n");
        }

        return csvContent.toString();
    }

    /**
     * Generic Method for Delete Directory.
     * @author Kalp Shah
     * @param folderToDelete
     *
     * */
    public static void deleteDirectory(File folderToDelete) {
        if (folderToDelete.exists() && folderToDelete.isDirectory()) {
            try {
                org.apache.commons.io.FileUtils.deleteDirectory(folderToDelete);
                System.out.println("Deleted folder: " + folderToDelete.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("Error deleting folder: " + e.getMessage());
            }
        } else {
            System.out.println("Folder does not exist: " + folderToDelete.getAbsolutePath());
        }
    }

}
