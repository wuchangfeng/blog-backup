> Given an array S of n integers, find three integers in S such that the sum is closest to a given number,
>
> target. Return the sum of the three integers. You may assume that each input would have exactly one solution.
> For example, given array S = {-1 2 1 -4}, and target = 1.
>
> The sum that is closest to the target is 2. (-1 + 2 + 1 = 2).

``` java
import java.util.Arrays;
/**
 * Created by allen on 2016/11/2.
 */
public class leetcode3sumcloest {

    /**
     *
     * @param a 指定数组
     * @param target 数组三个元素距离目标和
     * @return 最接近目标的数值
     */
    public static int solution(int a[],int target){
        // 用来赞存最近接目标值的变量
        int tar = Integer.MAX_VALUE;
        int res = 0;
        // 给数组从小到大排序
        Arrays.sort(a);
        for (int i = 0; i < a.length-1; i++) {

            int j = i + 1;
            int k = a.length - 1;

            while (j < k){

                int sum = a[i] + a[j] + a[k];
                if (sum == target) {
                    return sum;
                }
                else{
                    int df = Math.abs(target - sum);
                    if (df < tar){
                        tar = df;
                        // 注意这里 res 每次重新赋值为 sum
                        res = sum;
                    }
                    if (sum > target)
                        k --;
                    else
                        j ++;
                }
            }
        }
        return res;
    }

    public static void main(String[] args) {

        int a[] = {-1,2,1,-4};
        int target = 1;
        int r = solution(a,target);
        System.out.println(r);
    }
}
```

