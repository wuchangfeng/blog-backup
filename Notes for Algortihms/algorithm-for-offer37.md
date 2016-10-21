### 题目：输入数字 n，按顺序打印出从1到最大的n位十进制数，比如输入3，则打印出1、2、3一直打印到最大的 3 位数即 999.

此题目看似简单，一般写法会先求出最大的数，比如上述的例子，求出 1000，然后依次打印 1 到 999.这样做法在 n 比较小的时候，是没有什么问题的，但是当 n 比较大时候，使用整形或者长整形都会产生溢出呢？

首先我们要考虑用什么来表示大数，通常的做法，有用数组或者字符串，下面我们引入用字符串表示大数字的方法;

> 用数组表示大数就是：数组内的每一个成员只表示大数里的一位。
> 如数组int a[100]表示：123456789
> a[0]为符号位，值为1时为正数，值为-1为负数（值可以自己定义）
> a[1]=1;
> a[2]=2;
> a[3]=3;~~~;a[9]=9;
> 最后定义一个结束位a[10]=-999;(-999是结束的标志，可以自己定义)
> 如果要用大数进行运算则要重写四则运算法则。
> 重写加减法较为简单，乘除法则较为复杂，需要花大量时间设计。
> 一般来说存储使用字符串数组如：char a[100]，运算时则转化为
> 数值数组：int a[100]。[]中的100为数组长度，大数长度必须在100-2的范围内
> 才能正确表示。数组长度由自己定义

如题目中，我们输 3 ，自然想让其打印 1、2、3.....999.

``` java
/**
 * Created by allen on 2016/10/21.
 */
public class num13 {

    public static void printOneToNthDigits(int n) {
        // 输入的数字不能为小于1
        if (n < 1) {
            System.out.println("");
        }
        // 创建一个数组用于打存放值
        int[] arr = new int[n];
        printOneToNthDigits(0, arr);
    }

    public static void printOneToNthDigits(int n, int[] arr) {
        // 说明所有的数据排列选择已经处理完了
        if (n >= arr.length) {
            // 可以输入数组的值
            printArray(arr);
        } else {
            // 对
            for (int i = 0; i <= 9; i++) {
                arr[n] = i;
                printOneToNthDigits(n + 1, arr);
            }
        }
    }

    public static void printArray(int[] arr) {
        // 找第一个非0的元素
        int index = 0;
        while (index < arr.length && arr[index] == 0) {
            index++;
        }
        // 从第一个非0值到开始输出到最后的元素。
        for (int i = index; i < arr.length; i++) {
            // 注意这里我们并没换行。数组表示的大数连在一起
            System.out.print(arr[i]);
        }
        // 条件成立说明数组中有非零元素，所以需要换行
        if (index < arr.length) {
            System.out.println();
        }
    }
    public static void main(String[] args) {
        printOneToNthDigits(2);
    }
}

```

