### 题目：数组中有一个数字出现的次数超过数组长度的一半，请找出这个数字

例子说明：

如输入一个长度为 9 的数组｛ 1, 2, 3, 2, 2, 2, 5, 4, 2｝。由于数字 2 在数组中出现了 5 次，超过数组长度的一半，因此输出 2 。

**参考链接**：http://wiki.jikexueyuan.com/project/for-offer/question-twenty-nine.html

#### 解法二：根据数组组特点找出O(n)的算法

数组中有一个数字出现的次数超过数组长度的一半，也就是说它出现的次数比其他所有数字出现次数的和还要多。因此我们可以考虑在遍历数组的时候保存两个值： 一个是数组中的一个数字， 一个是次数。当我们遍历到下个数字的时候，如果下一个数字和我们之前保存的数字相同，则次数加 1；如果下一个数字和我们之前保存的数字不同，则次数减 1 。如果次数为0，我们需要保存下一个数字，并把次数设为 1 。由于我们要找的数字出现的次数比其他所有数字出现的次数之和还要多，**那么要找的数字肯定是最后一次把次数设为 1 时对应的数字**。





``` java
public class num11 {
    public static int moreThanHalfNum(int[] numbers) {
        // 输入校验
        if (numbers == null || numbers.length < 1) {
            throw new IllegalArgumentException("array length must large than 0");
        }
        // 用于记录出现次数大于数组一半的数
        int result = numbers[0];
        // 于当前记录的数不同的数的个数
        int count = 1;
        // 从第二个数开始向后找
        for (int i = 1; i < numbers.length; i++) {
            // 如果记数为0
            if (count == 0) {
                // 重新记录一个数，假设它是出现次数大于数组一半的
                result = numbers[i];
                // 记录统计值
                count = 1;
            }
            // 如果记录的值与统计值相等，记数值增加
            else if (result == numbers[i]) {
                count++;
            }
            // 如果不相同就减少，相互抵消
            else {
                count--;
            }
        }
        // 最后的result可能是出现次数大于数组一半长度的值
        // 统计result的出现次数,注意这里置于0的原因
        count = 0;
        for (int number : numbers) {
            if (result == number) {
                count++;
            }
        }
        // 如果出现次数大于数组的一半就返回对应的值
        if (count > numbers.length / 2) {
            return result;
        }
        // 否则输入异常
        else {
            throw new IllegalArgumentException("invalid input");
        }
    }
    public static void main(String[] args) {
        // 存在出现次数超过数组长度一半的数字
        int numbers[] = {1, 2, 3, 2, 2, 2, 5, 4, 2};
        System.out.println(moreThanHalfNum(numbers));
    }
}
```

