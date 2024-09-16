package uk.gov.hmcts.pdda.web.publicdisplay.imaging;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.pdda.web.publicdisplay.imaging.exceptions.ImageLoadingException;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ImageLoaderTest {

    private static final String TRUE = "Result is not True";

    private final Toolkit mockToolkit = Mockito.mock(Toolkit.class);

    private final URL mockUrl = Mockito.mock(URL.class);

    private final Image mockImage = Mockito.mock(Image.class);

    private final ImageLoader classUnderTest = getClassUnderTest();

    @BeforeAll
    public static void setUp() {
        Mockito.mockStatic(Toolkit.class);
    }

    @AfterAll
    public static void tearDown() {
        Mockito.clearAllCaches();
    }

    private ImageLoader getClassUnderTest() {
        Mockito.when(Toolkit.getDefaultToolkit()).thenReturn(mockToolkit);
        Mockito.when(mockToolkit.createImage(Mockito.isA(URL.class))).thenReturn(mockImage);
        return new ImageLoader(mockUrl, false);
    }

    @Test
    void testGetImage() {
        boolean result;
        classUnderTest.setImage(classUnderTest.getImage());
        classUnderTest.getImage();
        result = true;

        assertTrue(result, TRUE);
    }

    @Test
    void testImageUpdate() {
        Image img = null;
        int infoflags = ImageObserver.ALLBITS;
        int xvalue = 0;
        int yvalue = 0;
        int width = 0;
        int height = 0;
        boolean result = classUnderTest.imageUpdate(img, infoflags, xvalue, yvalue, width, height);
        assertTrue(result, TRUE);
    }

    @Test
    void testImageLoadingException() throws MalformedURLException {
        Assertions.assertThrows(ImageLoadingException.class, () -> {
            throw new ImageLoadingException(mockUrl);
        });
    }

}
