## 题目：给五张扑克牌，判断是不是顺子。大小王可以替代任意数。

其中大小王在程序中当成 0, 其他的按顺序上升。

``` java
public class num5 {

    public static boolean isContinue(int a[]){

        if (a == null || a.length != 5)
            return false;
        // 利用内置函数进行排序
        Arrays.sort(a);
        // 数组中 0 的个数
        int NumofZero = 0;
        // 数组中前后两个数之间额差距
        int NumofGap = 0;

        // 找出数组中 0 的个数
        for (int i = 0; i < a.length; i++) {
            if (a[i] == 0)
                NumofZero ++;
        }

        // 因为已经排序好了，0 总是在前面,Small 表示第一个非 0 元素的位置
        int Small = NumofZero;
        int Big = NumofZero + 1;

        while (Big < a.length){
            // 有对子存在了，就不可能是顺子了
            if (a[Small] == a[Big])
                return false;
            // 找出每一对数字之间的差距 如 3 和 6 之间的差距为 4和5 6-3 = 3 所以需要在减掉一
            NumofGap += (a[Big] - a[Small] - 1);
            Small = Big;
            Big++;
        }

        // 这里可以写成三目或者直接 return
        if (NumofGap > NumofZero)
            return false;
        else
            return true;
    }

    public static void main(String[] args) {
        int[] numbers1 = {1, 3, 2, 6, 4};
        System.out.println(isContinue(numbers1));
    }
}
```

