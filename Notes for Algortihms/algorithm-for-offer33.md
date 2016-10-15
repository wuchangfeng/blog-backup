### 题目：请实现一个函数，把字符串中的每个空格替换成"%20"，例如“We are happy.”，则输出“We%20are%20happy.”。

常见做法是从字符串前面往后面遍历，遇到空格进行替换。但是这样做，每次遇到一个空格后面的字符都要重复的往后面进行移动，存在一定的重复性工作。进而量化处对于字符串长度为 n ，其时间复杂度为 o( n * n)。算法的训练过程中不允许出现这样的效率。转换思路从后往前去替换，这样，所有重复的字符就只需要移动一次了，前提是首先计算出新生的字符串的长度。

``` java
/**
 * Created by allen on 2016/10/15.
 */
public class num8 {

    /**
     * @param string     要转换的字符数组
     * @param usedLength 已经字符数组中已经使用的长度
     * @return 转换后使用的字符长度，-1表示处理失败
     */
    public static int replaceBlank(char[] string, int usedLength) {
        // 判断输入是否合法
        if (string == null || string.length < usedLength) {
            return -1;
        }
        // 统计字符数组中的空白字符数
        int whiteCount = 0;
        for (int i = 0; i < usedLength; i++) {
            if (string[i] == ' ') {
                whiteCount++;
            }
        }
        // 计算转换后的字符长度是多少,本来已经有一个空格，一个空格替换成三个字符
        // 故加上2即可
        int targetLength = whiteCount * 2 + usedLength;
        int tmp = targetLength; // 保存长度结果用于返回
        if (targetLength > string.length) { // 如果转换后的长度大于数组的最大长度，直接返回失败
            return -1;
        }
        // 如果没有空白字符就不用处理
        if (whiteCount == 0) {
            return usedLength;
        }
        usedLength--; // 从后向前，第一个开始处理的字符
        targetLength--; // 处理后的字符放置的位置
        // 字符中有空白字符，一直处理到所有的空白字符处理完
        while (usedLength >= 0 && usedLength < targetLength) {
            // 如是当前字符是空白字符，进行"%20"替换
            if (string[usedLength] == ' ') {
                string[targetLength--] = '0';
                string[targetLength--] = '2';
                string[targetLength--] = '%';
            } else { // 否则移动字符
                string[targetLength--] = string[usedLength];
            }
            usedLength--;
        }
        return tmp;
    }
    public static void main(String[] args) {
        char[] string = new char[50];
        string[0] = ' ';
        string[1] = 'e';
        string[2] = ' ';
        string[3] = ' ';
        string[4] = 'r';
        string[5] = 'e';
        string[6] = ' ';
        string[7] = ' ';
        string[8] = 'a';
        string[9] = ' ';
        string[10] = 'p';
        string[11] = ' ';
        int length = replaceBlank(string, 12);
        // 这种输出方式要注意一下
        System.out.println(new String(string, 0, length));
    }
}

```

