```java
/**
 * Created by allen on 2016/8/15.
 *
 * 1 2 8 9
 * 2 4 9 12
 * 4 7 10 13
 * 6 8 11 15
 *
 *查找 7 是否在这个二维数组中 二维数组是有序的 行和列都是递增
 */
public class ex2 {

    public  static boolean solution(int[][] m,int a,int b,int number){

        // 对数组的大小是否为空判断也需要写上

        int i = 0;// 开始查询的行号
        int j = b - 1;// 开始查询的列号
        while (i >= 0 && i < a && j >= 0 && j < b){//注意等号不要少了否则不能进行

            if (number == m[i][j]){
                return true;// 找到了
            }else if (number < m[i][j]){
                j--;// 列数-- 代表向左移动
            }else{
                i++;// 行数 ++ 代表向下移动
            }
        }
        return false;
    }

    public static void main(String[] args) {

        int m[][] = { {1,2,8,9}, {2,4,9,12}, {4,7,10,13}, {6,8,11,15}};
        int a = m.length;//获取二维数组的长度
        int b = m[0].length;//获取二维数组的宽度
        if (solution(m, a, b, 7)) System.out.println("存在");
        else {
            System.out.println("不存在");
        }
    }
}
```