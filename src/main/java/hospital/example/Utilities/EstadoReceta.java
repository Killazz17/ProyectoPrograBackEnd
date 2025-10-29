package hospital.example.Utilities;

/**
 * Enumeración que define los posibles estados de una receta médica.
 * Una receta pasa por diferentes etapas desde su creación hasta su entrega final.
 */
public enum EstadoReceta {
    confeccionada,  // Receta creada por el médico pero aún no procesada
    proceso,        // Receta siendo preparada por el farmaceuta
    lista,          // Receta lista para ser retirada por el paciente
    entregada       // Receta ya entregada al paciente
}
