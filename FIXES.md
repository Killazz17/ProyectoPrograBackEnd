# Correcciones Realizadas

## Problema Original
El código tenía errores de configuración que causaban que las entidades JPA no coincidieran con el esquema de la base de datos definido en las migraciones SQL.

## Errores Corregidos

### 1. Error de Sintaxis SQL (V1__create_all_tables.sql)
- **Línea 57**: Corregido typo en el nombre de la columna
  - Antes: `FOREIGN KEY (pacieNte_id)`
  - Después: `FOREIGN KEY (paciente_id)`

### 2. Entidad Receta (Receta.java)
- **Línea 10**: Corregido nombre de tabla para coincidir con SQL
  - Antes: `@Table(name = "recetas")`
  - Después: `@Table(name = "receta")`
  
- **Línea 15**: Agregada anotación para mapear correctamente el ID
  - Agregado: `@Column(name = "id_receta")`

### 3. Entidad Medicamento (Medicamento.java)
- **Línea 7**: Corregido nombre de columna discriminador
  - Antes: `@DiscriminatorColumn(name = "tipo")`
  - Después: `@DiscriminatorColumn(name = "DTYPE")`
  
- **Línea 8**: Corregido nombre de tabla para coincidir con SQL
  - Antes: `@Table(name = "medicamentos")`
  - Después: `@Table(name = "medicamento")`

## Estado Actual
✅ El código compila sin errores  
✅ Las anotaciones JPA coinciden con el esquema SQL  
✅ Se mantiene la estructura original del proyecto

## Para Ejecutar
1. Asegurarse de tener MySQL corriendo en `localhost:3306`
2. Crear la base de datos: `CREATE DATABASE Proyecto2;`
3. Ejecutar Flyway para crear las tablas: `mvn flyway:migrate`
4. Ejecutar la aplicación: `mvn exec:java -Dexec.mainClass="hospital.example.Main"`
