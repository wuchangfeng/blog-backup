> Given an array *S* of *n* integers, are there elements *a*, *b*, *c* in *S* such that *a* + *b* + *c* = 0? Find all unique triplets in the array which gives the sum of zero.
>
> **Note:**
>
> - Elements in a triplet (*a*,*b*,*c*) must be in non-descending order. (ie, *a* ≤ *b* ≤ *c*)
> - The solution set must not contain duplicate triplets.
>
> ```
>     For example, given array S = {-1 0 1 2 -1 -4},
>
>     A solution set is:
>     (-1, 0, 1)
>     (-1, -1, 2)
> ```

在数组中找出三个不同的数，使他们和为 0，并输出。重点是不能有重复的数字，其实核心还是跟 twosum 一样的，固定好一个数字，然后进行 twosum。

``` java
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by allen on 2016/11/1.
 */
public class leetcode3sum {
   
    public static ArrayList<ArrayList<Integer>> threeSum(int[] num) {

        ArrayList<ArrayList<Integer>> rst = new ArrayList<ArrayList<Integer>>();
        if(num == null || num.length < 3) {
            return rst;
        }
        // 对输入的数组进行排序
        Arrays.sort(num);
      	// num.length -2 是为了 三个数相加
        for (int i = 0; i < num.length - 2; i++) {
            // 找出第一个重复的
            if (i != 0 && num[i] == num[i - 1]) {
                continue; // to skip duplicate numbers; e.g [0,0,0,0]
            }

            int left = i + 1;
            int right = num.length - 1;
            // 固定好 num[i] 之后进行 twosum 运算
            while (left < right) {
                int sum = num[left] + num[right] + num[i];
                if (sum == 0) {
                    ArrayList<Integer> tmp = new ArrayList<Integer>();
                    tmp.add(num[i]);
                    tmp.add(num[left]);
                    tmp.add(num[right]);
                    rst.add(tmp);
                    left++;
                    right--;
                    while (left < right && num[left] == num[left - 1]) { // to skip duplicates
                        left++;
                    }
                    while (left < right && num[right] == num[right + 1]) { // to skip duplicates
                        right--;
                    }
                // 由于从小到大排序，sum < 0 left 要向右边移动才能保证 sum 再增大    
                } else if (sum < 0) {
                    left++;
                } else {
                    right--;
                }
            }
        }
        return rst;
    }

    public static void main(String[] args) {

        int a[] = {-1, 0 ,1 ,2 ,-1 ,-4};
        ArrayList<ArrayList<Integer>> rst = threeSum(a);

        for (ArrayList<Integer> tmp : rst)
            System.out.println(tmp);
    }
}
```

