### Convención de Commits y Ramas para MS-MERCURY-INTEGRATION

Este micro sigue la misma convención de commits que `KOREX-API-TEST`, adaptada al contexto de este proyecto.

---

### 1. Regla crítica: un feature = una rama

- **Por cada nueva funcionalidad, fix o refactor importante**:
  - Crear **una rama dedicada** desde la rama principal de trabajo (por ejemplo, `main` o la que se defina).
- No mezclar múltiples funcionalidades en la misma rama.

Ejemplos de nombres de rama:

- `feat/mercury-list-accounts`
- `fix/mercury-token-permissions`
- `docs/arquitectura-capas`

Comandos de ejemplo:

```bash
git checkout main
git pull origin main
git checkout -b feat/mercury-list-accounts
```

---

### 2. Formato obligatorio del commit

Se usa el mismo formato que en `KOREX-API-TEST`:

```text
<tipo>(<ámbito>): <título breve del cambio>

por qué: <motivo del cambio>
para qué: <objetivo o beneficio del cambio>
```

- **tipo**: `feat`, `fix`, `docs`, `refactor`, `test`, `chore`, `style`, `perf`, `build`, `ci`.
- **ámbito**: parte del sistema afectada (ej: `mercury`, `config`, `docs`, `deps`).
- **título**: en imperativo, breve, máximo 72 caracteres, sin punto final.
- Mensajes **siempre en español**.

Ejemplo para este microservicio:

```bash
git add docs/README_ARQUITECTURA_CAPAS.md
git commit -m "docs(arquitectura): documentar capas y módulos" \
  -m "por qué: hacía falta una guía clara de estructura" \
  -m "para qué: facilitar la contribución y mantenimiento del microservicio"
```

---

### 3. Tipos de commit más usados en este micro

- `feat`  → nueva funcionalidad (nuevo endpoint, nuevo módulo, etc.).
- `fix`   → corrección de bug (por ejemplo, error en llamada a Mercury).
- `docs`  → cambios en `README`, docs en `docs/`, comentarios relevantes.
- `refactor` → refactors internos sin cambiar comportamiento observable.
- `test`  → agregar o ajustar tests.
- `chore` → tareas de mantenimiento (gitignore, scripts, configuración de build).

Ejemplos:

```bash
git commit -m "feat(mercury): agregar endpoint de transacciones" \
  -m "por qué: se requiere listar transacciones por cuenta" \
  -m "para qué: permitir conciliación de depósitos con Mercury"

git commit -m "fix(config): corregir base-url de mercury" \
  -m "por qué: las llamadas devolvían 404 por ruta incorrecta" \
  -m "para qué: garantizar que los endpoints apunten a la API correcta"
```

---

### 4. Flujo recomendado para trabajar en una funcionalidad

1. **Crear rama de feature**
   ```bash
   git checkout main
   git pull origin main
   git checkout -b feat/descripcion-breve
   ```

2. **Hacer cambios y agrupar archivos por funcionalidad**
   ```bash
   git status
   git add <archivos-relacionados>
   ```

3. **Crear commit siguiendo el estándar**
   ```bash
   git commit -m "feat(mercury): agregar verificación de permisos" \
     -m "por qué: necesitamos validar qué puede hacer el token" \
     -m "para qué: detectar problemas de configuración antes de usar la API"
   ```

4. **Subir la rama de feature**
   ```bash
   git push origin feat/descripcion-breve
   ```

5. **Crear Pull Request** en GitHub desde la rama de feature hacia la rama objetivo (por ejemplo, `main`).

---

### 5. Plantillas rápidas de commit

#### Nueva funcionalidad (feat)
```bash
git add <archivos>
git commit -m "feat(<ámbito>): <descripción breve>" \
  -m "por qué: <motivo>" \
  -m "para qué: <objetivo>"
```

#### Corrección de bug (fix)
```bash
git add <archivos>
git commit -m "fix(<ámbito>): <descripción breve>" \
  -m "por qué: <motivo>" \
  -m "para qué: <objetivo>"
```

#### Documentación (docs)
```bash
git add <archivos>
git commit -m "docs(<ámbito>): <descripción breve>" \
  -m "por qué: <motivo>" \
  -m "para qué: <objetivo>"
```

---

### 6. Recordatorio

- **Un commit = un cambio lógico atómico.**
- **Una rama = una funcionalidad / fix / refactor.**
- Mensajes en **español**, claros y con **por qué** y **para qué**.

