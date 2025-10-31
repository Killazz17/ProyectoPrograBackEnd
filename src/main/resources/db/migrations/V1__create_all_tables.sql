-- Tabla base para todos los usuarios
CREATE TABLE usuario (
                         id INT NOT NULL PRIMARY KEY,
                         clave_hash VARCHAR(255),
                         salt VARCHAR(255),
                         nombre VARCHAR(100) NOT NULL,
                         rol VARCHAR(20) NOT NULL
);

-- Admins
CREATE TABLE admin (
                       id INT NOT NULL,
                       created_at DATETIME,
                       updated_at DATETIME,
                       CONSTRAINT pk_admin PRIMARY KEY (id),
                       CONSTRAINT fk_admin_usuario FOREIGN KEY (id) REFERENCES usuario(id) ON DELETE CASCADE
);

-- Farmaceutas
CREATE TABLE farmaceuta (
                            id INT NOT NULL,
                            created_at DATETIME,
                            updated_at DATETIME,
                            CONSTRAINT pk_farmaceuta PRIMARY KEY (id),
                            CONSTRAINT fk_farmaceuta_usuario FOREIGN KEY (id) REFERENCES usuario(id) ON DELETE CASCADE
);

-- Médicos
CREATE TABLE medico (
                        id INT NOT NULL,
                        especialidad VARCHAR(100),
                        created_at DATETIME,
                        updated_at DATETIME,
                        CONSTRAINT pk_medico PRIMARY KEY (id),
                        CONSTRAINT fk_medico_usuario FOREIGN KEY (id) REFERENCES usuario(id) ON DELETE CASCADE
);

-- Pacientes
CREATE TABLE paciente (
                          id INT NOT NULL,
                          fecha_nacimiento DATE,
                          numero_telefono VARCHAR(20),
                          created_at DATETIME,
                          updated_at DATETIME,
                          CONSTRAINT pk_paciente PRIMARY KEY (id),
                          CONSTRAINT fk_paciente_usuario FOREIGN KEY (id) REFERENCES usuario(id) ON DELETE CASCADE
);

-- Recetas (nombre corregido a "recetas")
CREATE TABLE recetas (
                         id INT AUTO_INCREMENT NOT NULL,
                         paciente_id INT NOT NULL,
                         estado VARCHAR(50) NOT NULL,
                         fecha_confeccion DATE NOT NULL,
                         fecha_retiro DATE NOT NULL,
                         CONSTRAINT pk_recetas PRIMARY KEY (id),
                         CONSTRAINT fk_receta_paciente FOREIGN KEY (paciente_id) REFERENCES paciente(id)
);

-- Medicamentos (nombre corregido a "medicamentos")
CREATE TABLE medicamentos (
                              codigo VARCHAR(20) NOT NULL,
                              tipo VARCHAR(50) NOT NULL,
                              nombre VARCHAR(100) NOT NULL,
                              presentacion VARCHAR(100) NOT NULL,
    -- Campos solo para MedicamentoPrescrito
                              cantidad INT,
                              duracion INT,
                              indicaciones VARCHAR(300),
                              receta_id INT,
                              CONSTRAINT pk_medicamentos PRIMARY KEY (codigo),
                              CONSTRAINT fk_medicamento_receta FOREIGN KEY (receta_id) REFERENCES recetas(id)
);

-- Índices para mejorar performance
CREATE INDEX idx_medicamentos_receta ON medicamentos(receta_id);
CREATE INDEX idx_recetas_paciente ON recetas(paciente_id);
CREATE INDEX idx_recetas_estado ON recetas(estado);