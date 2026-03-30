package com.lucia.api.service.File;

import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileSystemStorageService implements StorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileSystemStorageService.class);

    @Value("${media.location:mediafiles}")
    private String mediaLocation;

    private Path rootLocation;

    @Override
    @PostConstruct
    public void init() throws IOException {
        rootLocation = Paths.get(mediaLocation);
        try {
            Files.createDirectories(rootLocation);
            logger.info("Media root directory initialized at {}", rootLocation.toAbsolutePath());
        } catch (Exception e) {
            logger.warn("Could not create media directory at {}: {}. Falling back to temp directory.", 
                rootLocation, e.getMessage());
            try {
                Path tempDir = Files.createTempDirectory("mediafiles-");
                rootLocation = tempDir;
                logger.info("Using temporary media directory: {}", rootLocation.toAbsolutePath());
            } catch (Exception ex) {
                logger.error("Failed to create temporary media directory, using system temp dir. Error: {}", 
                    ex.getMessage());
                rootLocation = Paths.get(System.getProperty("java.io.tmpdir"));
            }
        }
    }

    /**
     * Guarda un archivo y retorna el nombre del archivo guardado
     */
    public String save(MultipartFile file, String filename) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("No se puede almacenar un archivo vacío.");
            }
            Path destinationFile = rootLocation.resolve(Paths.get(filename))
                .normalize()
                .toAbsolutePath();
            
            Path parentDir = destinationFile.getParent();
            if (parentDir != null) {
                Files.createDirectories(parentDir);
            }
            
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
            
            logger.debug("File saved successfully: {}", filename);
            return filename;
        } catch (IOException e) {
            logger.error("Failed to store file: {}", filename, e);
            throw new RuntimeException("No se pudo almacenar el archivo.", e);
        }
    }

    /**
     * Carga un archivo como recurso
     */
    public Resource loadAsResource(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("No se pudo leer el archivo: " + filename);
            }
        } catch (MalformedURLException e) {
            logger.error("Malformed URL for file: {}", filename, e);
            throw new RuntimeException("No se pudo leer el archivo: " + filename, e);
        }
    }

    /**
     * Elimina un archivo
     */
    public boolean delete(String filename) {
        try {
            Path file = rootLocation.resolve(filename).normalize().toAbsolutePath();
            boolean deleted = Files.deleteIfExists(file);
            
            if (deleted) {
                logger.debug("File deleted successfully: {}", filename);
            } else {
                logger.warn("File not found for deletion: {}", filename);
            }
            
            return deleted;
        } catch (Exception e) {
            logger.error("Failed to delete file: {}", filename, e);
            throw new RuntimeException("No se pudo eliminar el archivo: " + filename, e);
        }
    }

    /**
     * Verifica si un archivo existe
     */
    public boolean exists(String filename) {
        Path file = rootLocation.resolve(filename).normalize().toAbsolutePath();
        return Files.exists(file);
    }

    /**
     * Genera un nombre de archivo único agregando un índice si ya existe
     */
    public String generateUniqueFilename(String originalFilename) {
        int dot = originalFilename.lastIndexOf('.');
        String baseName = dot == -1 ? originalFilename : originalFilename.substring(0, dot);
        String extension = dot == -1 ? "" : originalFilename.substring(dot + 1);
        String newFilename = originalFilename;
        int index = 1;

        while (exists(newFilename)) {
            newFilename = extension.isEmpty() 
                ? baseName + "(" + index + ")" 
                : baseName + "(" + index + ")." + extension;
            index++;
        }
        
        return newFilename;
    }

    /**
     * Obtiene la ruta absoluta de un archivo
     */
    public Path getPath(String filename) {
        return rootLocation.resolve(filename).normalize().toAbsolutePath();
    }

    /**
     * Obtiene la ubicación raíz del almacenamiento
     */
    public Path getRootLocation() {
        return rootLocation;
    }
}