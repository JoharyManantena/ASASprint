package prom16.fonction;

import prom16.annotation.FileValidator;
import jakarta.servlet.http.Part;

public class FileHandler {

    @FileValidator(maxSize = 5242880, allowedTypes = {"image/png", "image/jpeg"})
    public static void validateFile(Part part) throws Exception {
        // String fileName = extractFileName(part);
        // Call to reflect validation logic
        // Reflect.validateFileWithAnnotation(part, fileName, FileHandler.class.getMethod("validateFile", Part.class));
    }

    public static String extractFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        String[] items = contentDisposition.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf("=") + 2, s.length() - 1);
            }
        }
        return "";
    }
    
    // Example of custom exceptions for better error handling
    public static class FileSizeExceededException extends Exception {
        public FileSizeExceededException(String message) {
            super(message);
        }
    }

    public static class FileTypeNotAllowedException extends Exception {
        public FileTypeNotAllowedException(String message) {
            super(message);
        }
    }
}
