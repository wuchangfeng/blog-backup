### 题目：用两个栈模拟一个队列



``` java
import java.util.Stack;

/**
 * Created by allen on 2016/8/31.
 */
public class ex16 {


    public static class LikeQueue<T>{
        // 压入栈
        Stack<T> stack1 = new Stack<T>();
        // 弹出栈
        Stack<T> stack2 = new Stack<T>();

        public LikeQueue() {
        }

        public void addNode(T t){
            stack1.add(t);
        }

        public T deleteNode(){
            if (stack2.isEmpty()){
                while (!stack1.isEmpty())
                    stack2.add(stack1.pop());
            }
            if (stack2.isEmpty()){
                // 抛出异常
                throw new RuntimeException("hello error");
            }
            // 最终都是从 stack2 中弹出数据
            return stack2.pop();
        }
    }


    public static void main(String[] args) {
        LikeQueue likeQueue = new LikeQueue();
        likeQueue.addNode(3);
        likeQueue.addNode(4);
        likeQueue.addNode(5);
        System.out.println("开始删除了,按照队列的特性，返回的结果是 3,4,5");
        System.out.println(likeQueue.deleteNode());
        System.out.println(likeQueue.deleteNode());
        System.out.println(likeQueue.deleteNode());
    }
}

```

