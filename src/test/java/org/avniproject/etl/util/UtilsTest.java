package org.avniproject.etl.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilsTest {

    @Test
    public void shouldGetS3ThumbnailUrl() {
        String input = "https://s3.ap-south-1.amazonaws.com/staging-user-media/mt/e70a306c-27c4-43cf-9aaa-37c8d3153dca.jpeg";
        String expectedOutput = "https://s3.ap-south-1.amazonaws.com/staging-user-media/mt/thumbnails/e70a306c-27c4-43cf-9aaa-37c8d3153dca.jpeg";

        String actualOutput = Utils.getThumbnailUrl(input);

        assertEquals(expectedOutput, actualOutput);
    }
}
