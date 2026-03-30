package com.lucia.api.service.File;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Interface para servicios de almacenamiento de archivos
 */
public interface StorageService {

    /**
     * Inicializa el almacenamiento creando directorios necesarios
     * @throws IOException si hay un error al crear los directorios
     */
    void init() throws IOException;

    /**
     * Guarda un archivo en el almacenamiento
     * @param file el archivo a guardar
     * @param filename el nombre con el que se guardará el archivo
     * @return el nombre del archivo guardado
     */
    String save(MultipartFile file, String filename);

    /**
     * Carga un archivo como recurso
     * @param filename el nombre del archivo a cargar
     * @return el recurso del archivo
     */
    Resource loadAsResource(String filename);

    /**
     * Elimina un archivo del almacenamiento
     * @param filename el nombre del archivo a eliminar
     * @return true si el archivo fue eliminado, false si no existía
     */
    boolean delete(String filename);

    /**
     * Verifica si un archivo existe en el almacenamiento
     * @param filename el nombre del archivo a verificar
     * @return true si el archivo existe, false en caso contrario
     */
    boolean exists(String filename);

    /**
     * Genera un nombre de archivo único basado en el nombre original
     * Si el archivo ya existe, agrega un índice numérico
     * @param originalFilename el nombre original del archivo
     * @return un nombre de archivo único
     */
    String generateUniqueFilename(String originalFilename);

    /**
     * Obtiene la ruta absoluta de un archivo en el almacenamiento
     * @param filename el nombre del archivo
     * @return la ruta del archivo
     */
    Path getPath(String filename);

    /**
     * Obtiene la ubicación raíz del almacenamiento
     * @return la ruta raíz del almacenamiento
     */
    Path getRootLocation();
}