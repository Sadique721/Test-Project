package com.diameter.commons;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FileUtil {
  private static final int EOF = -1;
  
  private static final int ONE_KB = 1024;
  
  private static final long ONE_MB = 1048576L;
  
  private static final long FILE_COPY_BUFFER_SIZE = 31457280L;
  
  public static String formAbsolutePath(String path, String basePath) {
    File file = new File(path);
    if (!file.isAbsolute())
      path = basePath + File.separator + path; 
    return path;
  }
  
  public static void createDirectories(String path) throws IllegalArgumentException {
    File file = new File(path);
    if (file.isFile())
      throw new IllegalArgumentException("Invalid path : " + path + ", path points to file and is not a directory."); 
    if (!file.exists())
      file.mkdirs(); 
  }
  
  public static void copyDirectoryToDirectory(File srcDir, File destDir) throws IOException {
    if (srcDir == null)
      throw new NullPointerException("Source must not be null"); 
    if (srcDir.exists() && !srcDir.isDirectory())
      throw new IllegalArgumentException("Source '" + destDir + "' is not a directory"); 
    if (destDir == null)
      throw new NullPointerException("Destination must not be null"); 
    if (destDir.exists() && !destDir.isDirectory())
      throw new IllegalArgumentException("Destination '" + destDir + "' is not a directory"); 
    copyDirectory(srcDir, new File(destDir, srcDir.getName()), true);
  }
  
  public static void copyDirectory(File srcDir, File destDir, boolean preserveFileDate) throws IOException {
    copyDirectory(srcDir, destDir, null, preserveFileDate);
  }
  
  public static void copyDirectory(File srcDir, File destDir, FileFilter filter, boolean preserveFileDate) throws IOException {
    if (srcDir == null)
      throw new NullPointerException("Source must not be null"); 
    if (destDir == null)
      throw new NullPointerException("Destination must not be null"); 
    if (!srcDir.exists())
      throw new FileNotFoundException("Source '" + srcDir + "' does not exist"); 
    if (!srcDir.isDirectory())
      throw new IOException("Source '" + srcDir + "' exists but is not a directory"); 
    if (srcDir.getCanonicalPath().equals(destDir.getCanonicalPath()))
      throw new IOException("Source '" + srcDir + "' and destination '" + destDir + "' are the same"); 
    List<String> exclusionList = null;
    if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath())) {
      File[] srcFiles = (filter == null) ? srcDir.listFiles() : srcDir.listFiles(filter);
      if (srcFiles != null && srcFiles.length > 0) {
        exclusionList = new ArrayList<>(srcFiles.length);
        for (File srcFile : srcFiles) {
          File copiedFile = new File(destDir, srcFile.getName());
          exclusionList.add(copiedFile.getCanonicalPath());
        } 
      } 
    } 
    doCopyDirectory(srcDir, destDir, filter, preserveFileDate, exclusionList);
  }
  
  private static void doCopyDirectory(File srcDir, File destDir, FileFilter filter, boolean preserveFileDate, List<String> exclusionList) throws IOException {
    File[] srcFiles = (filter == null) ? srcDir.listFiles() : srcDir.listFiles(filter);
    if (srcFiles == null)
      throw new IOException("Failed to list contents of " + srcDir); 
    if (destDir.exists()) {
      if (!destDir.isDirectory())
        throw new IOException("Destination '" + destDir + "' exists but is not a directory"); 
    } else if (!destDir.mkdirs() && !destDir.isDirectory()) {
      throw new IOException("Destination '" + destDir + "' directory cannot be created");
    } 
    if (!destDir.canWrite())
      throw new IOException("Destination '" + destDir + "' cannot be written to"); 
    for (File srcFile : srcFiles) {
      File dstFile = new File(destDir, srcFile.getName());
      if (exclusionList == null || !exclusionList.contains(srcFile.getCanonicalPath()))
        if (srcFile.isDirectory()) {
          doCopyDirectory(srcFile, dstFile, filter, preserveFileDate, exclusionList);
        } else {
          doCopyFile(srcFile, dstFile, preserveFileDate);
        }  
    } 
    if (preserveFileDate)
      destDir.setLastModified(srcDir.lastModified()); 
  }
  
  private static void doCopyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
    if (destFile.exists() && destFile.isDirectory())
      throw new IOException("Destination '" + destFile + "' exists but is a directory"); 
    FileInputStream fis = null;
    FileOutputStream fos = null;
    FileChannel input = null;
    FileChannel output = null;
    try {
      fis = new FileInputStream(srcFile);
      fos = new FileOutputStream(destFile);
      input = fis.getChannel();
      output = fos.getChannel();
      long size = input.size();
      long pos = 0L;
      long count = 0L;
      while (pos < size) {
        count = (size - pos > 31457280L) ? 31457280L : (size - pos);
        pos += output.transferFrom(input, pos, count);
      } 
    } finally {
      closeQuietly(output);
      closeQuietly(fos);
      closeQuietly(input);
      closeQuietly(fis);
    } 
    if (srcFile.length() != destFile.length())
      throw new IOException("Failed to copy full contents from '" + srcFile + "' to '" + destFile + "'"); 
    if (preserveFileDate)
      destFile.setLastModified(srcFile.lastModified()); 
  }
  
  public static void closeQuietly(Closeable closeable) {
    try {
      if (closeable != null)
        closeable.close(); 
    } catch (IOException iOException) {}
  }
  
  public static List<File> getRecursiveFileFromPath(String path) throws IOException {
    File file = new File(path);
    if (!file.exists())
      throw new FileNotFoundException(path + ""); 
    List<File> files = new ArrayList<>();
    if (file.isDirectory())
      for (File childFile : file.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
              return !pathname.isHidden();
            }
          })) {
        if (childFile.isDirectory()) {
          files.addAll(getRecursiveFileFromPath(childFile.getAbsolutePath()));
        } else {
          files.add(childFile);
        } 
      }  
    return files;
  }
  
  public static byte[] readBytesFully(String filePath) throws IOException {
    if (filePath == null)
      throw new NullPointerException("filePath must not be null"); 
    File file = new File(filePath);
    if (!file.exists())
      throw new IllegalArgumentException("File: " + file.getAbsolutePath() + " does not exist"); 
    if (file.isDirectory())
      throw new IllegalArgumentException(file.getAbsolutePath() + " is a directory"); 
    if (!file.canRead())
      throw new IllegalArgumentException("File: " + file.getAbsolutePath() + " can not be read"); 
    if (file.length() > 2147483647L)
      throw new OutOfMemoryError("File: " + file.getAbsolutePath() + " size too large."); 
    InputStream fileStream = null;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      fileStream = new FileInputStream(file);
      byte[] buffer = new byte[1024];
      while (true) {
        int i = fileStream.read(buffer);
        if (i == -1)
          break; 
        out.write(buffer, 0, i);
      } 
      return out.toByteArray();
    } finally {
      closeQuietly(fileStream);
      closeQuietly(out);
    } 
  }
  
  public static void cleanDirectory(File file, Predicate<File> predicate) throws IOException {
    checkForDirectoryPreconditions(file);
    File[] files = file.listFiles();
    if (files == null)
      throw new IOException("Failed to list contents of " + file); 
    IOException exception = null;
    for (int i = 0; i < files.length; i++) {
      File f = files[i];
      if (predicate.apply(f))
        try {
          forceDelete(f);
        } catch (IOException ioe) {
          exception = ioe;
        }  
    } 
    if (null != exception)
      throw exception; 
  }
  
  public static void cleanDirectory(File file) throws IOException {
    checkForDirectoryPreconditions(file);
    File[] files = file.listFiles();
    if (files == null)
      throw new IOException("Failed to list contents of " + file); 
    IOException exception = null;
    for (int i = 0; i < files.length; i++) {
      File f = files[i];
      try {
        forceDelete(f);
      } catch (IOException ioe) {
        exception = ioe;
      } 
    } 
    if (null != exception)
      throw exception; 
  }
  
  private static void checkForDirectoryPreconditions(File file) throws IOException {
    if (!file.exists()) {
      String message = file + " does not exist";
      throw new IllegalArgumentException(message);
    } 
    if (!file.isDirectory()) {
      String message = file + " is not a directory";
      throw new IllegalArgumentException(message);
    } 
  }
  
  private static void forceDelete(File file) throws IOException {
    if (file.isDirectory()) {
      deleteDirectory(file);
    } else {
      boolean filePresent = file.exists();
      if (!file.delete()) {
        if (!filePresent)
          throw new FileNotFoundException("File does not exist: " + file); 
        String message = "Unable to delete file: " + file;
        throw new IOException(message);
      } 
    } 
  }
  
  public static void deleteDirectory(File directory) throws IOException {
    if (!directory.exists())
      return; 
    cleanDirectory(directory);
    if (!directory.delete()) {
      String message = "Unable to delete directory " + directory + ".";
      throw new IOException(message);
    } 
  }
  
  public static List<File> listFiles(File file, FileFilter filter) {
    ArrayList<File> files = new ArrayList<>();
    if (!file.exists())
      return files; 
    if (file.isFile()) {
      if (filter.accept(file))
        files.add(file); 
    } else {
      for (File innerFile : file.listFiles())
        files.addAll(listFiles(innerFile, filter)); 
    } 
    return files;
  }
  
  public static void move(File from, File to) throws IOException {
    Preconditions.checkNotNull(from, "from is null");
    Preconditions.checkNotNull(to, "to is null");
    Preconditions.checkArgument(!from.equals(to), "Source " + from + " and destination " + to + " must be different");
    if (!from.renameTo(to)) {
      doCopyFile(from, to, false);
      if (!from.delete()) {
        if (!to.delete())
          throw new IOException("Unable to delete " + to); 
        throw new IOException("Unable to delete " + from);
      } 
    } 
  }
  
  public static class ExtensionFilter implements FileFilter {
    private String extension;
    
    public ExtensionFilter(String extension) {
      this.extension = extension;
    }
    
    public boolean accept(File pathname) {
      return pathname.getName().endsWith("." + this.extension);
    }
  }
  
  public static class WithoutExtensionFilter implements FileFilter {
    public boolean accept(File pathname) {
      return !pathname.getName().contains(".");
    }
  }
  
  public static class LastDateModifiedComparator implements Comparator<File> {
    public int compare(File file1, File file2) {
      return (int)(file1.lastModified() - file2.lastModified());
    }
  }
}
