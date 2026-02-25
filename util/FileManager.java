package com.taskmanager.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.taskmanager.model.Task;

public class FileManager {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();


      //Guarda la lista de tareas en un archivo JSON
    public static void saveTasks(List<Task> tasks, String filePath) {
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(tasks, writer);
        } catch (IOException e) {
            System.err.println("Error al guardar tareas: " + e.getMessage());
        }
    }

     //Carga la lista de tareas desde un archivo JSON
    public static List<Task> loadTasks(String filePath) {
        File file = new File(filePath);
        
        // Si el archivo no existe, retornar lista vacía
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(filePath)) {
            Type taskListType = new TypeToken<ArrayList<Task>>(){}.getType();
            List<Task> tasks = gson.fromJson(reader, taskListType);
            return tasks != null ? tasks : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Error al cargar tareas: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
