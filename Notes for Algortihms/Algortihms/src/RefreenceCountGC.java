import java.util.Base64;
import java.util.Objects;

/**
 * Created by allen on 2016/9/24.
 */
public class RefreenceCountGC {

    public Object instance = null;
    private static final int _1MB = 1024*1024;

    // 占用内存，以方便查看是否被回收过
    private byte[] bigSize = new byte[2*_1MB];

    public static void main(String[] args) {

        RefreenceCountGC objA = new RefreenceCountGC();
        RefreenceCountGC objB = new RefreenceCountGC();

        objA.instance = objB;
        objB.instance = objA;

        objA = null;
        objB = null;

        System.gc();

    }
}
