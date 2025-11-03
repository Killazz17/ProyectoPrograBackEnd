package hospital.example.WebSocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hospital.example.Domain.models.Mensaje;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MensajeriaWebSocketServer extends WebSocketServer {

    private static final int PORT = 8887;
    private final Map<String, WebSocket> usuariosConectados;
    private final Gson gson;

    public MensajeriaWebSocketServer() {
        super(new InetSocketAddress(PORT));
        this.usuariosConectados = new ConcurrentHashMap<>();
        this.gson = new Gson();
        System.out.println("Servidor WebSocket iniciado en puerto: " + PORT);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Nueva conexión desde: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String usuarioDesconectado = obtenerUsuarioPorConexion(conn);
        if (usuarioDesconectado != null) {
            usuariosConectados.remove(usuarioDesconectado);
            System.out.println("Usuario desconectado: " + usuarioDesconectado);
            notificarCambioUsuarios();
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            JsonObject json = gson.fromJson(message, JsonObject.class);
            String tipo = json.get("tipo").getAsString();

            switch (tipo) {
                case "REGISTRO":
                    registrarUsuario(conn, json);
                    break;
                case "MENSAJE":
                    enviarMensaje(json);
                    break;
                case "SOLICITAR_USUARIOS":
                    enviarListaUsuarios(conn);
                    break;
                default:
                    System.out.println("Tipo de mensaje desconocido: " + tipo);
            }
        } catch (Exception e) {
            System.err.println("Error procesando mensaje: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("Error en WebSocket: " + ex.getMessage());
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Servidor WebSocket iniciado exitosamente!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    private void registrarUsuario(WebSocket conn, JsonObject json) {
        String userId = json.get("userId").getAsString();
        String userName = json.get("userName").getAsString();

        usuariosConectados.put(userId, conn);
        System.out.println("Usuario registrado: " + userId + " (" + userName + ")");

        // Enviar confirmación al usuario
        JsonObject respuesta = new JsonObject();
        respuesta.addProperty("tipo", "REGISTRO_OK");
        respuesta.addProperty("mensaje", "Registrado exitosamente");
        conn.send(gson.toJson(respuesta));

        // Notificar a todos sobre el cambio en usuarios
        notificarCambioUsuarios();
    }

    private void enviarMensaje(JsonObject json) {
        String destinatarioId = json.get("destinatarioId").getAsString();
        WebSocket destinatarioConn = usuariosConectados.get(destinatarioId);

        if (destinatarioConn != null && destinatarioConn.isOpen()) {
            JsonObject mensajeEnvio = new JsonObject();
            mensajeEnvio.addProperty("tipo", "MENSAJE_RECIBIDO");
            mensajeEnvio.addProperty("remitenteId", json.get("remitenteId").getAsString());
            mensajeEnvio.addProperty("remitenteNombre", json.get("remitenteNombre").getAsString());
            mensajeEnvio.addProperty("contenido", json.get("contenido").getAsString());
            mensajeEnvio.addProperty("fechaEnvio", json.get("fechaEnvio").getAsString());

            destinatarioConn.send(gson.toJson(mensajeEnvio));
            System.out.println("Mensaje enviado de " + json.get("remitenteId").getAsString() +
                    " a " + destinatarioId);
        } else {
            System.out.println("Destinatario no disponible: " + destinatarioId);
        }
    }

    private void enviarListaUsuarios(WebSocket conn) {
        JsonObject respuesta = new JsonObject();
        respuesta.addProperty("tipo", "LISTA_USUARIOS");
        respuesta.add("usuarios", gson.toJsonTree(new ArrayList<>(usuariosConectados.keySet())));
        conn.send(gson.toJson(respuesta));
    }

    private void notificarCambioUsuarios() {
        JsonObject notificacion = new JsonObject();
        notificacion.addProperty("tipo", "ACTUALIZACION_USUARIOS");
        notificacion.add("usuarios", gson.toJsonTree(new ArrayList<>(usuariosConectados.keySet())));

        String mensaje = gson.toJson(notificacion);
        broadcast(mensaje);
        System.out.println("Usuarios activos notificados: " + usuariosConectados.size());
    }

    private String obtenerUsuarioPorConexion(WebSocket conn) {
        for (Map.Entry<String, WebSocket> entry : usuariosConectados.entrySet()) {
            if (entry.getValue().equals(conn)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        MensajeriaWebSocketServer server = new MensajeriaWebSocketServer();
        server.start();
        System.out.println("Servidor de mensajería en ejecución...");
    }
}