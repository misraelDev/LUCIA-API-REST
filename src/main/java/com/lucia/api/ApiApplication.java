package com.lucia.api;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiApplication {

	private static void loadDotenvFile(String filename) {
		try {
			// user.dir suele ser el directorio del proyecto cuando corres spring-boot:run.
			// ignoreIfMissing evita fallar si el archivo no existe (por ejemplo en el deploy).
			Dotenv dotenv = Dotenv.configure()
					.ignoreIfMissing()
					.filename(filename)
					.directory(System.getProperty("user.dir"))
					.load();

			for (DotenvEntry e : dotenv.entries()) {
				// Spring placeholder suele leer de system properties; así forzamos resolución.
				if (e.getValue() != null && !e.getValue().isBlank()) {
					System.setProperty(e.getKey(), e.getValue());
				}
			}
		} catch (Exception ignored) {
			// Si falla el dotenv, dejamos que Spring use sus valores por defecto/env del contenedor.
		}
	}

	private static void hydrateSpringDatasourceFromDbEnv() {
		// Si el deploy/entorno solo trae DB_* (como tu .env),
		// convertimos a SPRING_DATASOURCE_* para que application*.properties pueda resolver.
		String springUrl = System.getProperty("SPRING_DATASOURCE_URL");
		String dbHost = System.getProperty("DB_HOST");
		String dbPort = System.getProperty("DB_PORT");
		String dbName = System.getProperty("DB_NAME");

		if ((springUrl == null || springUrl.isBlank()) && dbHost != null && dbPort != null && dbName != null) {
			System.setProperty(
					"SPRING_DATASOURCE_URL",
					"jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName + "?sslmode=disable&ApplicationName=LuciaAPI"
			);
		}

		String springUser = System.getProperty("SPRING_DATASOURCE_USERNAME");
		if ((springUser == null || springUser.isBlank())) {
			String dbUser = System.getProperty("DB_USERNAME");
			if (dbUser != null && !dbUser.isBlank()) {
				System.setProperty("SPRING_DATASOURCE_USERNAME", dbUser);
			}
		}

		String springPass = System.getProperty("SPRING_DATASOURCE_PASSWORD");
		if ((springPass == null || springPass.isBlank())) {
			String dbPass = System.getProperty("DB_PASSWORD");
			if (dbPass != null && !dbPass.isBlank()) {
				System.setProperty("SPRING_DATASOURCE_PASSWORD", dbPass);
			}
		}
	}

	public static void main(String[] args) {
		// Prioridad: .env (base) -> .env.local (dev override)
		loadDotenvFile(".env");
		loadDotenvFile(".env.local");
		hydrateSpringDatasourceFromDbEnv();

		SpringApplication.run(ApiApplication.class, args);
	}
}
