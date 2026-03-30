## 🚀 Comandos del Proyecto

### Inicializar y ejecutar

#### 1. Recompilar

```powershell
.\mvnw.cmd clean compile
```

#### 2. Ejecutar la aplicación

```powershell
.\mvnw.cmd spring-boot:run
```

#### Alternativa: build completo y JAR

```powershell
.\mvnw.cmd clean package -DskipTests
java -jar target\demo-0.0.1-SNAPSHOT.jar
```

---