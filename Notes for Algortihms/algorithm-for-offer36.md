### 题目： 把一个数组最开始的若干个元素搬到数组的末尾， 我们称之数组的旋转。  输入一个递增排序的数组的一个旋转，输出旋转数组的最小元素。 例如数组{3, 4, 5, 1, 2｝为｛1 ,2, 3, 4, 5}的一个旋转，该数组的最小值为 1.

这道题最直观的解法并不难，从头到尾遍历数组一次，我们就能找出最小的元素。这种思路的时间复杂度显然是**O(n)**。但是这个思路**没有利用输入的旋转数组的特性，肯定达不到面试官的要求**。

　　我们注意到旋转之后的数组实际上可以划分为两个排序的子数组，而且前面的子数组的元素都大于或者等于后面子数组的元素。我们还注意到最小的元素刚好是这两个子数组的分界线。**在排序的数组中我们可以用二分查找法实现O(logn)的查找**。



**Step1.**和二分查找法一样，我们用两个指针分别指向数组的第一个元素和最后一个元素。

**Step2.**接着我们可以找到数组中间的元素：

　　如果该中间元素位于前面的递增子数组，那么它应该大于或者等于第一个指针指向的元素。此时数组中最小的元素应该位于该中间元素的后面。我们**可以把第一个指针指向该中间元素，这样可以缩小寻找的范围**。移动之后的第一个指针仍然位于前面的递增子数组之中。如果中间元素位于后面的递增子数组，那么它应该小于或者等于第二个指针指向的元素。此时该数组中最小的元素应该位于该中间元素的前面。

**Step3.**接下来我们再用更新之后的两个指针，重复做新一轮的查找。



``` java
public class num12 {
    
    public static int min(int[] numbers) {
        // 判断输入是否合法
        if (numbers == null || numbers.length == 0) {
            throw new RuntimeException("Invalid input.");
        }
        // 开始处理的第一个位置
        int lo = 0;
        // 开始处理的最后一个位置
        int hi = numbers.length - 1;
        // 设置初始值
        int mi = lo;
        // 确保lo在前一个排好序的部分，hi在排好序的后一个部分
        // 345  12 在这里可以注意一下循环结束的条件
        while (numbers[lo] >= numbers[hi]) {
            // 当处理范围只有两个数据时，返回后一个结果
            // 因为numbers[lo] >= numbers[hi]总是成立，后一个结果对应的是最小的值
            if (hi - lo == 1) {
                return numbers[hi];
            }
            // 取中间的位置
            mi = lo + (hi - lo) / 2;
            // 如果三个数都相等，则需要进行顺序处理，从头到尾找最小的值
            if (numbers[mi] == numbers[lo] && numbers[hi] == numbers[mi]) {
                return minInorder(numbers, lo, hi);
            }
            // 如果中间位置对应的值在前一个排好序的部分，将lo设置为新的处理位置
            if (numbers[mi] >= numbers[lo]) {
                lo = mi;
            }
            // 如果中间位置对应的值在后一个排好序的部分，将hi设置为新的处理位置
            else if (numbers[mi] <= numbers[hi]) {
                hi = mi;
            }
        }
        // 返回最终的处理结果
        return numbers[mi];
    }

    public static int minInorder(int[] numbers, int start, int end) {
        int result = numbers[start];
        for (int i = start + 1; i <= end; i++) {
            if (result > numbers[i]) {
                result = numbers[i];
            }
        }
        return result;
    }
    public static void main(String[] args) {
        // 典型输入，单调升序的数组的一个旋转
        int[] array1 = {3, 4, 5, 1, 2};
        System.out.println(min(array1));
    }
}
```

