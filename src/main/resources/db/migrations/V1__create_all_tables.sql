CREATE TABLE usuario (
                         id INT NOT NULL PRIMARY KEY,
                         clave_hash VARCHAR(255),
                         salt VARCHAR(255),
                         nombre VARCHAR(100) NOT NULL,
                         rol VARCHAR(20) NOT NULL
);

CREATE TABLE admin (
                       id INT NOT NULL,
                       created_at DATETIME,
                       updated_at DATETIME,
                       CONSTRAINT pk_admin PRIMARY KEY (id),
                       CONSTRAINT fk_admin_usuario FOREIGN KEY (id) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE farmaceuta (
                            id INT NOT NULL,
                            created_at DATETIME,
                            updated_at DATETIME,
                            CONSTRAINT pk_farmaceuta PRIMARY KEY (id),
                            CONSTRAINT fk_farmaceuta_usuario FOREIGN KEY (id) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE medico (
                        id INT NOT NULL,
                        especialidad VARCHAR(100),
                        created_at DATETIME,
                        updated_at DATETIME,
                        CONSTRAINT pk_medico PRIMARY KEY (id),
                        CONSTRAINT fk_medico_usuario FOREIGN KEY (id) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE paciente (
                          id INT NOT NULL,
                          fecha_nacimiento DATE,
                          numero_telefono VARCHAR(20),
                          created_at DATETIME,
                          updated_at DATETIME,
                          CONSTRAINT pk_paciente PRIMARY KEY (id),
                          CONSTRAINT fk_paciente_usuario FOREIGN KEY (id) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE recetas (
                         id INT AUTO_INCREMENT NOT NULL,
                         paciente_id INT NOT NULL,
                         estado VARCHAR(50) NOT NULL,
                         fecha_confeccion DATE NOT NULL,
                         fecha_retiro DATE NOT NULL,
                         CONSTRAINT pk_recetas PRIMARY KEY (id),
                         CONSTRAINT fk_receta_paciente FOREIGN KEY (paciente_id) REFERENCES paciente(id)
);

CREATE TABLE medicamentos (
                              codigo VARCHAR(20) NOT NULL,
                              nombre VARCHAR(100) NOT NULL,
                              presentacion VARCHAR(100) NOT NULL,
                              CONSTRAINT pk_medicamentos PRIMARY KEY (codigo)
);

CREATE TABLE medicamentos_prescritos (
                                         id INT AUTO_INCREMENT NOT NULL,
                                         receta_id INT NOT NULL,
                                         medicamento_codigo VARCHAR(20) NOT NULL,
                                         cantidad INT NOT NULL,
                                         duracion INT NOT NULL,
                                         indicaciones VARCHAR(300),
                                         CONSTRAINT pk_medicamentos_prescritos PRIMARY KEY (id),
                                         CONSTRAINT fk_prescrito_receta FOREIGN KEY (receta_id) REFERENCES recetas(id) ON DELETE CASCADE,
                                         CONSTRAINT fk_prescrito_medicamento FOREIGN KEY (medicamento_codigo) REFERENCES medicamentos(codigo)
);

CREATE INDEX idx_prescritos_receta ON medicamentos_prescritos(receta_id);
CREATE INDEX idx_prescritos_medicamento ON medicamentos_prescritos(medicamento_codigo);
CREATE INDEX idx_recetas_paciente ON recetas(paciente_id);
CREATE INDEX idx_recetas_estado ON recetas(estado);