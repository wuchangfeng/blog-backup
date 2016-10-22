import java.util.ArrayList;
import java.util.List;

/**
 * Created by allen on 2016/9/26.
 */
public class HeapOOMDemo {

    static class OOMObject{

    }

    public static void main(String[] args) {

        List<OOMObject> list = new ArrayList<>();
        while (true){
            list.add(new OOMObject());
        }

    }

    /**
    protected synchronized class<?> loadClass(String name,boolean resolve)
            throws ClassNotFoundException{

        Class c = findLoadedClass(name);
    }*/
}
