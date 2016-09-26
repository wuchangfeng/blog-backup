## 题目：冒泡排序

时间复杂度即每次将一个数丢最后，需要进行 n 次。显然时间复杂度为 n 的平方。

如上图，每一次将最小的数字冒上去。

![](http://ww3.sinaimg.cn/large/b10d1ea5jw1f86pz6ozynj20b5088wew.jpg)

``` java
public class num4 {

    // 冒泡排序
    public static void sort(int a[]){

        int temp;
        for (int i = 0; i < a.length ; i++) {
            for (int j = 0; j < a.length-1 ; j++) { // 想想为什么是 a.length -1 

                if (a[j] < a[j+1]){ // 这里的 j+1 容易写成 i
                    temp = a[j];
                    a[j] = a[j+1];
                    a[j+1] = temp;
                }
            }

            System.out.println("第"+i+"趟排序结果为：");
            for (int k = 0; k < a.length; k++) {
                System.out.print(a[k]);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {

        int a[] = {2,1,4,5,3,9,0,6,7};
        sort(a);
    }
}
```

