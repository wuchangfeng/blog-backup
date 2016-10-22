import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by allen on 2016/9/26.
 */
public class CompileClassLoader extends ClassLoader {

    // 读取一个文件的内容
    private byte[] getBytes(String filename)
        throws IOException{
        File file = new File(filename);
        long len = file.length();
        byte[] raw = new byte[(int)len];
        try(FileInputStream fin = new FileInputStream(file)){
            // 一次读取 Class 文件的全部二进制数据
            int r = fin.read(raw);
            if(r != len)
                throw new IOException("无法读取全部文件："+r+"!="+len);
            return raw;
        }
    }

    // 定义编译 java 文件的方法
    private boolean complie(String javaFile)
        throws Exception{
        System.out.println("正在编译"+ javaFile + "...");
        // 调用系统的 javac 命令
        Process p = Runtime.getRuntime().exec("javac"+javaFile);
        try{
            p.waitFor();
        }catch (InterruptedException ie){
            System.out.println(ie);
        }

        int ret = p.exitValue();
        return ret == 0;
    }

    @Override
    protected Class<?> findClass(String name)
            throws ClassNotFoundException {
        Class clazz = null;
        String fileStub = name.replace(".","/");
        String javaFilename = fileStub + ".java";
        String classFilename = fileStub + ".class";
        File javaFile = new File(javaFilename);
        File classFile = new File(classFilename);

        if (javaFile.exists() && (!classFile.exists() || javaFile.lastModified()
        > classFile.lastModified())){

            try {
                if (!complie(javaFilename) || !classFile.exists()){
                    throw new ClassNotFoundException(
                            "classNotFoundExcetpion:" + javaFilename
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (classFile.exists()){
            try {
                byte[] raw = getBytes(classFilename);
                clazz = defineClass(name,raw,0,raw.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if( clazz == null){
            throw new ClassNotFoundException(name);
        }

        return clazz;
    }

    public static void main(String[] args)
        throws Exception{

        if(args.length < 1){
            System.out.println("缺少目标类。请按照如下格式运行 java 源文件" );
            System.out.println("java CompileClassLoader className");
        }

        String progClass = args[0];
        String[] proArgs = new String[args.length-1];
        System.arraycopy(args,1,proArgs,0,proArgs.length);
        CompileClassLoader ccl = new CompileClassLoader();
        Class<?> clazz = ccl.loadClass(progClass);

        // 获取需要运行的类的主要方法
        Method main = clazz.getMethod("main",(new String[0]).getClass());
        Object arsArray[] = {proArgs};
        main.invoke(null,arsArray);
    }
}
