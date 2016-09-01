### 题目：栈的压入弹出序列



输入两个整数序列，第一个序列表示栈的压入顺序，请判断第二个序列是否为该栈的弹出顺序。假设压入栈的所有数字均不相等。例如序列1,2,3,4,5是某栈的压入顺序，序列4，5,3,2,1是该压栈序列对应的一个弹出序列，但4,3,5,1,2就不可能是该压栈序列的弹出序列。

上面的例子先将 1，2，3，4 压入栈，然后把 4 弹出来，然后再压入 5 然后就是题目所描述的顺序。

解决这个问题就是建立一个辅助栈，把输入的第一个序列中的数字一次压入付竹展，并按照第二个序列的顺序依次从该栈中弹出数字。

如果下一个弹出的数字刚好是栈顶数字，那么直接弹出。如果下一个弹出的数字不在栈顶，我们把**压栈序列**中还没有入栈的数字压入辅助栈，**直到把下一个需要弹出的数字压入栈顶为止**。如果所有的数字都压入栈了仍然没有找到下一个弹出的数字，那么该序列可能不是一个弹出序列。

``` java
public class ex18 {

    public static boolean IsPopOrder(int[] pushA, int[] popA) {

        if(pushA.length == 0 || popA.length == 0){
            return false;
        }

        Stack<Integer> stack = new Stack<Integer>();
        int j = 0;
        // popA 是原序列 PushA 是弹出栈
        for (int i = 0; i < popA.length; i++) {
            stack.push(pushA[i]);
            // 注意下 peek()
            while (j < popA.length && stack.peek() == popA[j]) {
                stack.pop();
                j++;
            }
        }
        //return false;
        return stack.empty() == true;
    }

    public static void main(String[] args) {

        int a[] = {1,2,3};
        int b[] = {3,1,2};
        System.out.println(IsPopOrder(a,b));

    }
}
```



