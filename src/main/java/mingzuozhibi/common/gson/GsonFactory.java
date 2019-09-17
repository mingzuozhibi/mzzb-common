package mingzuozhibi.common.gson;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import javax.persistence.Version;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

public abstract class GsonFactory {

    private static List<Class<? extends Annotation>> shouldSkips = new LinkedList<>();

    static {
        shouldSkips.add(Version.class);
        shouldSkips.add(Ignore.class);
    }

    public static Gson createGson() {
        GsonBuilder gson = new GsonBuilder();
        gson.setExclusionStrategies(new ExclusionStrategy() {

            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                for (Class<? extends Annotation> annotation : shouldSkips) {
                    if (f.getAnnotation(annotation) != null) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        });
        gson.registerTypeAdapter(Instant.class, new TypeAdapter<Instant>() {
            @Override
            public void write(JsonWriter out, Instant instant) throws IOException {
                if (instant != null) {
                    out.value(instant.toEpochMilli());
                }
            }

            @Override
            public Instant read(JsonReader in) throws IOException {
                if (in.hasNext()) {
                    return Instant.ofEpochMilli(in.nextLong());
                }
                return null;
            }
        });
        return gson.create();
    }

}
