-- Tabla base para todos los usuarios
CREATE TABLE usuario (
                         id INT NOT NULL PRIMARY KEY,
                         clave_hash VARCHAR(255) NOT NULL,
                         salt VARCHAR(255) NOT NULL,
                         nombre VARCHAR(100) NOT NULL,
                         rol VARCHAR(20) NOT NULL
);

-- Admins
CREATE TABLE admin (
                       id INT NOT NULL,
                       created_at DATETIME NULL,
                       updated_at DATETIME NULL,
                       CONSTRAINT pk_admin PRIMARY KEY (id),
                       CONSTRAINT fk_admin_usuario FOREIGN KEY (id) REFERENCES usuario(id)
);

-- Farmaceutas
CREATE TABLE farmaceuta (
                            id INT NOT NULL,
                            created_at DATETIME NULL,
                            updated_at DATETIME NULL,
                            CONSTRAINT pk_farmaceuta PRIMARY KEY (id),
                            CONSTRAINT fk_farmaceuta_usuario FOREIGN KEY (id) REFERENCES usuario(id)
);

-- Médicos
CREATE TABLE medico (
                        id INT NOT NULL,
                        especialidad VARCHAR(100) NOT NULL,
                        created_at DATETIME NULL,
                        updated_at DATETIME NULL,
                        CONSTRAINT pk_medico PRIMARY KEY (id),
                        CONSTRAINT fk_medico_usuario FOREIGN KEY (id) REFERENCES usuario(id)
);

-- Pacientes
CREATE TABLE paciente (
                          id INT NOT NULL,
                          fecha_nacimiento DATE NOT NULL,
                          numero_telefono VARCHAR(20) NOT NULL,
                          created_at DATETIME NULL,
                          updated_at DATETIME NULL,
                          CONSTRAINT pk_paciente PRIMARY KEY (id),
                          CONSTRAINT fk_paciente_usuario FOREIGN KEY (id) REFERENCES usuario(id)
);

-- Recetas
CREATE TABLE receta (
                        id_receta INT AUTO_INCREMENT NOT NULL,
                        paciente_id INT NOT NULL,
                        estado VARCHAR(255) NOT NULL,
                        fecha_confeccion DATE NOT NULL,
                        fecha_retiro DATE NOT NULL,
                        CONSTRAINT pk_receta PRIMARY KEY (id_receta),
                        CONSTRAINT fk_receta_paciente FOREIGN KEY (pacieNte_id) REFERENCES paciente(id)
);

-- Medicamentos base
CREATE TABLE medicamento (
                             codigo VARCHAR(20) NOT NULL,
                             DTYPE VARCHAR(31) NOT NULL,
                             nombre VARCHAR(100) NOT NULL,
                             presentacion VARCHAR(100) NOT NULL,
                             CONSTRAINT pk_medicamento PRIMARY KEY (codigo)
);

-- Medicamentos prescritos (especialización de Medicamento)
CREATE TABLE medicamento_prescrito (
                                       codigo VARCHAR(20) NOT NULL,
                                       cantidad INT NOT NULL,
                                       duracion INT NOT NULL,
                                       indicaciones VARCHAR(300) NOT NULL,
                                       receta_id INT NOT NULL,
                                       CONSTRAINT pk_medicamento_prescrito PRIMARY KEY (codigo),
                                       CONSTRAINT fk_medicamento_prescrito_base FOREIGN KEY (codigo) REFERENCES medicamento(codigo),
                                       CONSTRAINT fk_medicamento_prescrito_receta FOREIGN KEY (receta_id) REFERENCES receta(id_receta)
);