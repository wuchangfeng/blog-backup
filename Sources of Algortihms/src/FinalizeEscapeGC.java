/**
 * Created by allen on 2016/9/24.
 */
public class FinalizeEscapeGC {

    public static FinalizeEscapeGC SAVE_HOOK = null;

    public void isAlive(){
        System.out.println("yes i am still live");
    }

    // 留个心眼，这个方法每个对象只会被系统自动调用一次
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("finalize method called");
        FinalizeEscapeGC.SAVE_HOOK = this;
    }

    public static void main(String[] args) throws Throwable {

        SAVE_HOOK = new FinalizeEscapeGC();

        // 对象第一次自我拯救成功
        SAVE_HOOK = null;
        System.gc();
        Thread.sleep(500);
        if (SAVE_HOOK != null){
            SAVE_HOOK.isAlive();
        }else{
            System.out.println("no,i am dead");
        }

        // 对象第二次拯救自己失败
        SAVE_HOOK = null;
        System.gc();
        Thread.sleep(500);
        if (SAVE_HOOK != null){
            SAVE_HOOK.isAlive();
        }else{
            System.out.println("no,i am dead");
        }
    }
}
