package com.taskmanager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

public class Principal {
    
    private static final String ARCHIVO_TAREAS = "tareas.json";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Scanner entrada = new Scanner(System.in);
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new AdaptadorFecha())
            .create();
    
    private static List<Tarea> tareas = new ArrayList<>();
    private static int siguienteId = 1;

    public static void main(String[] args) {
        cargarTareas();
        System.out.println("\n========================================");
        System.out.println("    GESTOR DE TAREAS - v1.0");
        System.out.println("    Por [Orlando Romero]");
        System.out.println("========================================");

        boolean continuar = true;
        while (continuar) {
            mostrarMenu();
            int opcion = leerNumero("Seleccione una opcion");

            switch (opcion) {
                case 1: crearTarea(); break;
                case 2: listarTareas(); break;
                case 3: verDetalle(); break;
                case 4: actualizarTarea(); break;
                case 5: eliminarTarea(); break;
                case 6: completarTarea(); break;
                case 7: cambiarEstado(); break;
                case 8: filtrarPorEstado(); break;
                case 9: cambiarPrioridad(); break;
                case 10: filtrarPorPrioridad(); break;
                case 0:
                    System.out.println("\nHasta luego!");
                    continuar = false;
                    break;
                default:
                    System.out.println("Opcion no valida");
            }

            if (continuar && opcion != 0) {
                System.out.println("\nPresione Enter para continuar...");
                entrada.nextLine();
            }
        }
        entrada.close();
    }

    private static void crearTarea() {
        System.out.println("\n--- CREAR NUEVA TAREA ---");
        
        String titulo = leer("Titulo");
        if (titulo.isEmpty()) {
            System.out.println("El titulo no puede estar vacio");
            return;
        }

        String descripcion = leer("Descripcion");
        Prioridad prioridad = seleccionarPrioridad();
        
        Tarea tarea = new Tarea(generarId(), titulo, descripcion, Estado.PENDIENTE, prioridad);
        tareas.add(tarea);
        guardarTareas();
        
        System.out.println("Tarea creada con ID: " + tarea.id);
    }

    private static void listarTareas() {
        if (tareas.isEmpty()) {
            System.out.println("\nNo hay tareas registradas");
            return;
        }

        System.out.println("\n========================================");
        System.out.println("           LISTA DE TAREAS");
        System.out.println("========================================");
        
        for (Tarea t : tareas) {
            System.out.printf("[%s] %s%n", t.id, t.titulo);
            System.out.printf("    Estado: %s | Prioridad: %s%n", 
                t.estado.nombre, t.prioridad.nombre);
            System.out.println("----------------------------------------");
        }
        
        System.out.println("Total de tareas: " + tareas.size());
    }

    private static void verDetalle() {
        Tarea t = buscarTarea();
        if (t == null) return;

        System.out.println("\n========================================");
        System.out.println("         DETALLES DE LA TAREA");
        System.out.println("========================================");
        System.out.println("ID:          " + t.id);
        System.out.println("Titulo:      " + t.titulo);
        System.out.println("Descripcion: " + t.descripcion);
        System.out.println("Estado:      " + t.estado.nombre);
        System.out.println("Prioridad:   " + t.prioridad.nombre);
        System.out.println("Creada:      " + t.creada.format(FORMATO_FECHA));
        System.out.println("Actualizada: " + t.actualizada.format(FORMATO_FECHA));
        System.out.println("========================================");
    }

    private static void actualizarTarea() {
        Tarea t = buscarTarea();
        if (t == null) return;

        System.out.println("\nTarea actual: " + t.titulo);
        
        String nuevoTitulo = leer("Nuevo titulo (Enter para mantener)");
        String nuevaDesc = leer("Nueva descripcion (Enter para mantener)");
        
        if (!nuevoTitulo.isEmpty()) t.titulo = nuevoTitulo;
        if (!nuevaDesc.isEmpty()) t.descripcion = nuevaDesc;
        
        t.actualizada = LocalDateTime.now();
        guardarTareas();
        System.out.println("Tarea actualizada correctamente");
    }

    private static void eliminarTarea() {
        Tarea t = buscarTarea();
        if (t == null) return;

        System.out.println("\nTarea: " + t.titulo);
        String confirmar = leer("¿Desea eliminar esta tarea? (s/n)");
        
        if (confirmar.equalsIgnoreCase("s")) {
            tareas.remove(t);
            guardarTareas();
            System.out.println("Tarea eliminada correctamente");
        } else {
            System.out.println("Operacion cancelada");
        }
    }

    private static void completarTarea() {
        Tarea t = buscarTarea();
        if (t != null) {
            t.estado = Estado.COMPLETADA;
            t.actualizada = LocalDateTime.now();
            guardarTareas();
            System.out.println("Tarea marcada como completada");
        }
    }

    private static void cambiarEstado() {
        Tarea t = buscarTarea();
        if (t == null) return;

        t.estado = seleccionarEstado();
        t.actualizada = LocalDateTime.now();
        guardarTareas();
        System.out.println("Estado actualizado correctamente");
    }

    private static void filtrarPorEstado() {
        Estado estado = seleccionarEstado();
        List<Tarea> filtradas = tareas.stream()
                .filter(t -> t.estado == estado)
                .collect(Collectors.toList());
        
        System.out.println("\nTareas con estado: " + estado.nombre);
        mostrarLista(filtradas);
    }

    private static void cambiarPrioridad() {
        Tarea t = buscarTarea();
        if (t == null) return;

        t.prioridad = seleccionarPrioridad();
        t.actualizada = LocalDateTime.now();
        guardarTareas();
        System.out.println("Prioridad actualizada correctamente");
    }

    private static void filtrarPorPrioridad() {
        Prioridad prioridad = seleccionarPrioridad();
        List<Tarea> filtradas = tareas.stream()
                .filter(t -> t.prioridad == prioridad)
                .collect(Collectors.toList());
        
        System.out.println("\nTareas con prioridad: " + prioridad.nombre);
        mostrarLista(filtradas);
    }

    private static Tarea buscarTarea() {
        String id = leer("Ingrese el ID de la tarea");
        for (Tarea t : tareas) {
            if (t.id.startsWith(id)) return t;
        }
        System.out.println("No se encontro la tarea con ese ID");
        return null;
    }

    private static void mostrarLista(List<Tarea> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay tareas que mostrar");
            return;
        }
        System.out.println("----------------------------------------");
        for (Tarea t : lista) {
            System.out.printf("[%s] %s - %s - %s%n", 
                t.id, t.titulo, t.estado.nombre, t.prioridad.nombre);
        }
        System.out.println("----------------------------------------");
    }

    private static Estado seleccionarEstado() {
        System.out.println("\nEstados disponibles:");
        System.out.println("1. Pendiente");
        System.out.println("2. En Progreso");
        System.out.println("3. Completada");
        
        int opcion = leerNumero("Seleccione el estado");
        switch (opcion) {
            case 1: return Estado.PENDIENTE;
            case 2: return Estado.EN_PROGRESO;
            case 3: return Estado.COMPLETADA;
            default: 
                System.out.println("Opcion invalida, se usara Pendiente");
                return Estado.PENDIENTE;
        }
    }

    private static Prioridad seleccionarPrioridad() {
        System.out.println("\nPrioridades disponibles:");
        System.out.println("1. Baja");
        System.out.println("2. Media");
        System.out.println("3. Alta");
        System.out.println("4. Urgente");
        
        int opcion = leerNumero("Seleccione la prioridad");
        switch (opcion) {
            case 1: return Prioridad.BAJA;
            case 2: return Prioridad.MEDIA;
            case 3: return Prioridad.ALTA;
            case 4: return Prioridad.URGENTE;
            default:
                System.out.println("Opcion invalida, se usara Media");
                return Prioridad.MEDIA;
        }
    }

    private static String leer(String mensaje) {
        System.out.print(mensaje + ": ");
        return entrada.nextLine().trim();
    }

    private static int leerNumero(String mensaje) {
        while (true) {
            try {
                return Integer.parseInt(leer(mensaje));
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingrese un numero valido");
            }
        }
    }

    private static String generarId() {
        return String.format("%06d", siguienteId++);
    }

    private static void cargarTareas() {
        File archivo = new File(ARCHIVO_TAREAS);
        if (!archivo.exists()) return;

        try (Reader lector = new FileReader(archivo)) {
            Type tipo = new TypeToken<ArrayList<Tarea>>(){}.getType();
            List<Tarea> cargadas = gson.fromJson(lector, tipo);
            if (cargadas != null) {
                tareas = cargadas;
                calcularSiguienteId();
            }
        } catch (IOException e) {
            System.out.println("Error al cargar las tareas: " + e.getMessage());
        }
    }

    private static void guardarTareas() {
        try (Writer escritor = new FileWriter(ARCHIVO_TAREAS)) {
            gson.toJson(tareas, escritor);
        } catch (IOException e) {
            System.out.println("Error al guardar las tareas: " + e.getMessage());
        }
    }

    private static void calcularSiguienteId() {
        int idMaximo = 0;
        for (Tarea t : tareas) {
            try {
                int id = Integer.parseInt(t.id);
                if (id > idMaximo) idMaximo = id;
            } catch (NumberFormatException e) {
                // Ignorar IDs no numericos
            }
        }
        siguienteId = idMaximo + 1;
    }

    private static void mostrarMenu() {
        System.out.println("\n========================================");
        System.out.println("            MENU PRINCIPAL");
        System.out.println("========================================");
        System.out.println(" 1.  Crear tarea");
        System.out.println(" 2.  Listar tareas");
        System.out.println(" 3.  Ver detalle de tarea");
        System.out.println(" 4.  Actualizar tarea");
        System.out.println(" 5.  Eliminar tarea");
        System.out.println(" 6.  Completar tarea");
        System.out.println(" 7.  Cambiar estado");
        System.out.println(" 8.  Filtrar por estado");
        System.out.println(" 9.  Cambiar prioridad");
        System.out.println(" 10. Filtrar por prioridad");
        System.out.println(" 0.  Salir");
        System.out.println("========================================");
    }

    static class Tarea {
        String id;
        String titulo;
        String descripcion;
        Estado estado;
        Prioridad prioridad;
        LocalDateTime creada;
        LocalDateTime actualizada;

        Tarea(String id, String titulo, String descripcion, Estado estado, Prioridad prioridad) {
            this.id = id;
            this.titulo = titulo;
            this.descripcion = descripcion;
            this.estado = estado;
            this.prioridad = prioridad;
            this.creada = LocalDateTime.now();
            this.actualizada = LocalDateTime.now();
        }
    }

    enum Estado {
        PENDIENTE("Pendiente"),
        EN_PROGRESO("En Progreso"),
        COMPLETADA("Completada");

        String nombre;

        Estado(String nombre) {
            this.nombre = nombre;
        }
    }

    enum Prioridad {
        BAJA("Baja"),
        MEDIA("Media"),
        ALTA("Alta"),
        URGENTE("Urgente");

        String nombre;

        Prioridad(String nombre) {
            this.nombre = nombre;
        }
    }

    static class AdaptadorFecha implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        private static final DateTimeFormatter formateador = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public JsonElement serialize(LocalDateTime fecha, Type tipo, JsonSerializationContext contexto) {
            return new JsonPrimitive(fecha.format(formateador));
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type tipo, JsonDeserializationContext contexto) {
            return LocalDateTime.parse(json.getAsString(), formateador);
        }
    }
}
