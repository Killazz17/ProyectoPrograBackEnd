package hospital.example.Utilities;
/**
 * Enumeración que define los diferentes roles de usuario en el sistema hospitalario.
 * Cada usuario del sistema debe tener asignado uno de estos roles para determinar
 * sus permisos y funcionalidades disponibles.
 */
public enum Rol {
    ADMINISTRADOR,  // Usuario con permisos completos para gestionar el sistema
    MEDICO,         // Personal médico que puede prescribir medicamentos
    FARMACEUTA,     // Personal de farmacia que prepara y entrega medicamentos
    PACIENTE        // Usuarios que reciben atención médica y medicamentos
}
