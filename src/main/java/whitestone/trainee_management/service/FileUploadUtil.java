//package whitestone.trainee_management.service;
//
//import org.docx4j.Docx4J;
//import org.docx4j.openpackaging.exceptions.Docx4JException;
//import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.*;
//
//public class FileUploadUtil {
//
//    public static String saveFile(MultipartFile file, String uploadDir) throws Exception {
//
//        // ---- 1️ Create absolute path OUTSIDE Tomcat temp ----
//        String basePath = System.getProperty("user.dir");   // Project root
//        String finalPath = basePath + File.separator + uploadDir;
//
//        File directory = new File(finalPath);
//
//        // ---- 2️ Ensure directory exists ----
//        if (!directory.exists()) {
//            boolean created = directory.mkdirs();
//            System.out.println("UPLOAD DIRECTORY CREATED: " + created 
//                + " | PATH = " + directory.getAbsolutePath());
//        }
//
//        // ---- 3️ Clean filename ----
//        String originalName = file.getOriginalFilename();
//        originalName = originalName.replaceAll("\\s+", "_");
//
//        String extension = originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
//
//        // ---- 4️ Convert DOC/DOCX to PDF ----
//        if (extension.equals("doc") || extension.equals("docx")) {
//            String pdfName = originalName.replace("." + extension, ".pdf");
//            File pdfFile = new File(directory, pdfName);
//
//            convertToPdf(file.getInputStream(), pdfFile);
////            System.out.println("Uploaddeddddd");
//            return pdfFile.getAbsolutePath();
//        }
//
//        // ---- 5️ PDF direct save ----
//        if (extension.equals("pdf")) {
//
//            File dest = new File(directory, originalName);
//            file.transferTo(dest);
//
//            System.out.println("FILE SAVED AT: " + dest.getAbsolutePath());
//            return dest.getAbsolutePath();
//        }
//
//        throw new IllegalArgumentException("Invalid file format. Only DOC, DOCX, PDF allowed.");
//    }
//
//    // ----  DOC → PDF conversion ----
//    private static void convertToPdf(InputStream inputStream, File pdfFile)
//            throws Docx4JException, IOException {
//
//        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(inputStream);
//
//        try (FileOutputStream os = new FileOutputStream(pdfFile)) {
//            Docx4J.toPDF(wordMLPackage, os);
//        }
//    }
//}



package whitestone.trainee_management.service;

import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class FileUploadUtil {

    public static String saveFile(MultipartFile file, String uploadDir) throws Exception {

        // ---- Absolute base path ----
        String basePath = System.getProperty("user.dir");
        String absoluteUploadPath = basePath + File.separator + uploadDir;

        File directory = new File(absoluteUploadPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // ----  Clean filename ----
        String originalName = file.getOriginalFilename();
        originalName = originalName.replaceAll("\\s+", "_");

        String extension =
                originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase();

        // ----  DOC/DOCX → PDF ----
        if (extension.equals("doc") || extension.equals("docx")) {

            String pdfName = originalName.replace("." + extension, ".pdf");
            File pdfFile = new File(directory, pdfName);

            convertToPdf(file.getInputStream(), pdfFile);

            // RETURN RELATIVE PATH
            return uploadDir + "" + pdfName;
        }

        // ---- PDF direct save ----
        if (extension.equals("pdf")) {

            File dest = new File(directory, originalName);
            file.transferTo(dest);

            //  RETURN RELATIVE PATH
            return uploadDir + "" + originalName;
        }

        throw new IllegalArgumentException("Only DOC, DOCX, PDF allowed");
    }

    // ----  DOC → PDF ----
    private static void convertToPdf(InputStream inputStream, File pdfFile) throws Exception {

        WordprocessingMLPackage wordMLPackage =
                WordprocessingMLPackage.load(inputStream);

        try (FileOutputStream os = new FileOutputStream(pdfFile)) {
            Docx4J.toPDF(wordMLPackage, os);
        }
    }
}