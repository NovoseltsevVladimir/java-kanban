package ru.practicum.kanban.server;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
        if (duration == null) {
            jsonWriter.value("");
        } else {
            long seconds = duration.toSeconds();
            jsonWriter.value(seconds);
        }
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {

        String durationString = jsonReader.nextString();
        if (durationString.isBlank()) {
            return null;
        } else {
            return Duration.ofSeconds(Integer.parseInt(durationString));
        }
    }
}
