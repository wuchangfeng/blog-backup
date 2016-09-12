### 题目：求一个数组连续和最大的值

思路一：最开始，大部分人估计是将数组所能有的组合列举出来，累加，然后比较最大值。自然一个长度为n的数组，所能有的组合有 n*(n+1)/2 种。如 -1，2，3则能有-1，2，,3，-12，-123，23六种。但是计算出所有数组的和最快也要  n * n 时间。能不能一次遍历就好？



代码如下：

```java
/**
 * Created by allen on 2016/8/20.
 */
public class ex9 {

    public static int solution(int a[] ,int length){

        if (a == null ||length < 0)
            return -1;

        int CurrentSum = 0;//当前和
        int CurrentGrate = 0;//当前最大值
        for (int i = 0; i < length;i++){
            // 若当前和 < 0,抛弃之
            if (CurrentSum < 0){
                CurrentSum = a[i];
            // 累加
            }else{
                CurrentSum += a[i];
            }
            // 如果当前和大于最大值
            if (CurrentSum > CurrentGrate)
                CurrentGrate = CurrentSum;
        }


        return CurrentGrate;
    }

    public static void main(String[] args) {
        // 1 -2 3 10 -4 7 2 -5
        int a[] ={1,-2,3,10,-4,7,2,-5};
        System.out.println(solution(a,a.length));
    }
}

```

