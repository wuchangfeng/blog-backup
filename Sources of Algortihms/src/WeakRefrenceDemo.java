import java.lang.ref.WeakReference;

/**
 * Created by allen on 2016/9/24.
 */
public class WeakRefrenceDemo {

    public static void main(String[] args) {

        String s = new String("hello");
        WeakReference<String> wrf = new WeakReference<String>(s);

        s = null;// 置 null

        System.out.println(wrf.get());//WeakReference
        System.gc();//强制gc
        System.runFinalization();
        System.out.println(wrf.get());//null
    }
}
