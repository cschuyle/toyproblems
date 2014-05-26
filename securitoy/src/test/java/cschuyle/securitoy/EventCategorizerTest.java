package cschuyle.securitoy;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class EventCategorizerTest {

    private EventCategorizer.EventCategory detect(String inputString) throws IOException {
        return new EventCategorizer().processFile(new ByteArrayInputStream(inputString.getBytes(StandardCharsets.UTF_8)), "filename");
    }
    @Test
    public void shouldProcessJson() throws IOException {
        assertNull(detect("{\"a\":1}"));
    }

    @Test
    public void shouldDetectAlarm() throws IOException {
        assertEquals(EventCategorizer.EventCategory.ALARM, detect("{\"Type\":\"alarm\"}"));
    }

    @Test
    public void shouldDetectImg() throws IOException {
        assertEquals(EventCategorizer.EventCategory.IMG, detect("{\"Type\":\"img\"}"));
    }

    @Test
    public void shouldDetectUnknownTypes() throws IOException {
        assertEquals(null, detect("{\"Type\":\"Door\"}"));
    }

    @Test
    public void shouldGracefullyProcessGarbage() throws IOException {
        assertNull(detect("sghg2 -- , </pre> {\"Type\":\"alarm\"} 24397bn%%~"));
    }

    @Test
    public void shouldNotDetectAlarmWhenAtUnexpectedLocation() throws IOException {
        assertNull(detect("{\"notes\":[\"A\",\"B\",{\"Type\":\"alarm\"}]}"));
    }

}