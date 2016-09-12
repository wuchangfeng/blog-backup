### 题目：将一个数组中的偶数全部置于奇数之前



![](http://ww2.sinaimg.cn/large/b10d1ea5jw1f7agpcw7x0j20js07sdi9.jpg)

``` java
/**
 * Created by allen on 2016/8/29.
 */
public class ex15 {

    public static int Reorder(int a[],int length){
        //鲁棒性的考虑
        if (a == null || length < 0)
            return 0;

        int i = 0;
        int j = length - 1;

        while (i < j){
            // a[i] % 2 == 0 可以把这个抽离出来
            while ((a[i] % 2 == 0) && i < j)
                i++;

            while ((a[j] % 2 != 0) && i < j )
                j--;

            if (i < j){
                int temp = a[i];
                a[i] = a[j];
                a[j] = temp;
            }
        }
        return 1;
    }

    public static void main(String[] args) {
         int a[] = {1,3,4,5,8,9};
         int length = a.length;
        Reorder(a,length);

        for (int i = 0; i < a.length;i ++)
            System.out.print(a[i]);
    }
}

```

