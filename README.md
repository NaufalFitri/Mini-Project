Mini-Project — build & run

Quick steps to clean/build/run locally (Windows PowerShell):

1) Prepare JDBC driver (optional if you don't use the database at runtime)
- Download MySQL Connector/J (mysql-connector-j-<version>.jar) from MySQL website.
- Place the jar in the project `libs/` folder: `Mini-Project\libs\mysql-connector-j-8.x.x.jar`

2) Build and run from PowerShell (project root):

```powershell
# compile only
.\build_and_run.ps1

# compile and run (will include jars in libs/ on classpath)
.\build_and_run.ps1 -CompileAndRun
```

3) If you use an IDE (Eclipse / VS Code):
- Ensure `src/` is configured as source folder.
- In VS Code: run `Java: Clean the Java language server workspace`, then restart the window.
- If IDE shows "Unresolved compilation problems" after that, open the Problems view to fix other errors.
- To add the JDBC driver in IDE run configuration: add the jar to the runtime classpath / project classpath.

4) Notes on common problems:
- "Database cannot be resolved to a type" can be an IDE cache problem; cleaning the Java language server or Eclipse workspace usually fixes it.
- `ClassNotFoundException: com.mysql.cj.jdbc.Driver` means the MySQL connector JAR is missing from classpath at runtime. Put the jar into `libs/` or add as dependency in your IDE.

If you want, I can also create a Maven `pom.xml` to manage dependencies automatically.