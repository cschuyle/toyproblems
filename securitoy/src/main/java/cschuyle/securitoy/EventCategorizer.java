package cschuyle.securitoy;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * Categorizes events (which are received as files).  A file may belong to one or zero event categories.
 */
public class EventCategorizer {

    /**
     * The categories of events.
     */
    public enum EventCategory {
        ALARM,
        IMG
    }

    private final JsonFactory jsonFactory = new JsonFactory();

    /**
     *
     * @param file - path to a file to process
     * @return the category (might be null) which the message is judged to be in.  To be in a category, the file
     *   must be parseable JSON and must have "Type":"alarm" or "Type":"img" at the top level of the JSON object.
     * @throws IOException
     */
    public EventCategory processFile(Path file) throws IOException {
        return processFile(new FileInputStream(file.toFile()), file.toString());
    }

    public EventCategory processFile(InputStream iStream, String filename) throws IOException {
        State state = null;

        long level = 0;
        try(JsonParser jp = jsonFactory.createParser(iStream)) {
            JsonToken token = jp.nextToken();
            while (token != null) {
                if(token == JsonToken.START_OBJECT) {
                    ++ level;
                }
                else if(token == JsonToken.END_OBJECT) {
                    -- level;
                }
                else if (token == JsonToken.FIELD_NAME && jp.getCurrentName().equals("Type") && level == 1) {
                    state = State.TYPE;
                }
                else if (state == State.TYPE && token == JsonToken.VALUE_STRING) {
                    String value = jp.getText();
                    if (value.equals("alarm")) {
                        return EventCategory.ALARM;
                    }
                    if (value.equals("img")) {
                        return EventCategory.IMG;
                    }
                } else {
                    state = null;
                }
                token = jp.nextToken();
            }
        } catch(JsonParseException e) {
            System.out.println("WARNING: File '" + filename + "' could not be processed.");
        }
        return null;
    }

    // OK, not much of a set of states ... the idea is the "parser" uses a state machine driven by the Jackson stream.
    private enum State {
        TYPE
    }
}
