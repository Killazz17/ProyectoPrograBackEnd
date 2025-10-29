package hospital.example.Utilities;

/**
 * Enumeración que define los tipos de cambios que pueden ocurrir en los datos.
 * Se utiliza en el patrón Observer para notificar qué tipo de operación se realizó
 * sobre una entidad en el sistema.
 */
public enum ChangeType {
    CREATED,   // Se creó un nuevo registro
    UPDATED,   // Se modificó un registro existente
    DELETED    // Se eliminó un registro
}
