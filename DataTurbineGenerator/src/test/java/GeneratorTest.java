import org.junit.Assert;
import org.junit.Test;

/**
 * Created by jirawat on 13/11/2014.
 */
public class GeneratorTest {
    @Test public void milisSecTest() {
        int samplingRate = 360;
        double time = 1.0/360;
        double timestamp = System.currentTimeMillis();
        for (int i=0; i<5; i++) {
            System.out.println(timestamp);
            timestamp += time;
        }
        Assert.assertTrue(true);
    }
}
