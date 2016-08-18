```java
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn1,btn2;
    TextView tv1;

    private Handler uiHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1 = (Button) findViewById(R.id.btn1);
        tv1 = (TextView) findViewById(R.id.tv1);
        btn1.setOnClickListener(this);
        System.out.println("Main thread id " + Thread.currentThread().getId());
    }

    @Override
    public void onClick(View view) {
        DownloadThread downloadThread = new DownloadThread();
        downloadThread.start();
    }

    class DownloadThread extends Thread{
        @Override
        public void run() {
            try{
                System.out.println("DownloadThread id " + Thread.currentThread().getId());
                System.out.println("开始下载文件");
                //此处让线程DownloadThread休眠5秒中，模拟文件的耗时过程
                Thread.sleep(5000);
                System.out.println("文件下载完成");
                //文件下载完成后更新UI
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Runnable thread id " + Thread.currentThread().getId());
                        MainActivity.this.tv1.setText("文件下载完成");
                    }
                };
                uiHandler.post(runnable);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        }
}
```



```java
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn1,btn2;
    TextView tv1;

    private Handler uiHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    System.out.println("handleMessage thread id " + Thread.currentThread().getId());
                    System.out.println("msg.arg1:" + msg.arg1);
                    System.out.println("msg.arg2:" + msg.arg2);
                    MainActivity.this.tv1.setText("文件下载完成");
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1 = (Button) findViewById(R.id.btn1);
        tv1 = (TextView) findViewById(R.id.tv1);
        btn1.setOnClickListener(this);
        System.out.println("Main thread id " + Thread.currentThread().getId());
    }

    @Override
    public void onClick(View view) {
        DownloadThread downloadThread = new DownloadThread();
        downloadThread.start();
    }

    class DownloadThread extends Thread{
        @Override
        public void run() {
            try{
                System.out.println("DownloadThread id " + Thread.currentThread().getId());
                System.out.println("开始下载文件");
                //此处让线程DownloadThread休眠5秒中，模拟文件的耗时过程
                Thread.sleep(5000);
                System.out.println("文件下载完成");
                //文件下载完成后更新UI
                Message msg = new Message();
                //虽然Message的构造函数式public的，我们也可以通过以下两种方式通过循环对象获取Message
                //msg = Message.obtain(uiHandler);
                //msg = uiHandler.obtainMessage();

                //what是我们自定义的一个Message的识别码，以便于在Handler的handleMessage方法中根据what识别
                //出不同的Message，以便我们做出不同的处理操作
                msg.what = 1;

                //我们可以通过arg1和arg2给Message传入简单的数据
                msg.arg1 = 123;
                msg.arg2 = 321;
                //我们也可以通过给obj赋值Object类型传递向Message传入任意数据
                //msg.obj = null;
                //我们还可以通过setData方法和getData方法向Message中写入和读取Bundle类型的数据
                //msg.setData(null);
                //Bundle data = msg.getData();
                //将该Message发送给对应的Handler
                uiHandler.sendMessage(msg);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        }
}
```

