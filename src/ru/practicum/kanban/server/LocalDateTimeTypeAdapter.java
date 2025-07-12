package ru.practicum.kanban.server;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {

    private static final DateTimeFormatter timeFormatter
            = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
        if (localDateTime == null) {
            jsonWriter.value("");
        } else {
            jsonWriter.value(localDateTime.format(timeFormatter));
        }
    }


    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        String currerntString = jsonReader.nextString();
        if (currerntString.isBlank()) {
            return null;
        } else {
            return LocalDateTime.parse(currerntString, timeFormatter);
        }
    }
}
