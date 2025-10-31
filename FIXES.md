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

### 4. Inicialización de Datos (Main.java)
- **Líneas 50-82**: Agregado asignación manual de IDs para todos los usuarios
  - Los usuarios necesitan IDs asignados manualmente porque la entidad Usuario no tiene `@GeneratedValue`
  - Admins: IDs 1-5
  - Farmaceutas: IDs 6-10
  - Médicos: IDs 11-15
  - Pacientes: IDs 16-20

- **Líneas 50-83**: Agregado asignación de roles para todos los usuarios
  - Cada usuario requiere un `rol` asignado (campo NOT NULL en la base de datos)
  - Admins: `Rol.ADMINISTRADOR`
  - Farmaceutas: `Rol.FARMACEUTA`
  - Médicos: `Rol.MEDICO`
  - Pacientes: `Rol.PACIENTE`

- **Líneas 50-83**: Corregido orden de operaciones para guardar usuarios
  - Removido llamadas redundantes a `save()` antes de `asignarClaveHasheada()`
  - `asignarClaveHasheada()` ahora maneja el guardado completo del usuario con todos los campos requeridos
  - Esto evita errores de "Column 'clave_hash' cannot be null"

## Estado Actual
✅ El código compila sin errores  
✅ Las anotaciones JPA coinciden con el esquema SQL  
✅ Se mantiene la estructura original del proyecto

## Para Ejecutar
1. Asegurarse de tener MySQL corriendo en `localhost:3306`
2. Crear la base de datos: `CREATE DATABASE Proyecto2;`
3. Ejecutar Flyway para crear las tablas: `mvn flyway:migrate`
4. Ejecutar la aplicación: `mvn exec:java -Dexec.mainClass="hospital.example.Main"`
