> Given an array of integers, find two numbers such that they add up to a specific target number.
>
> The function twoSum should return indices of the two numbers such that they add up to the target, where index1 must be less than index2. Please note that your returned answers (both index1 and index2) are not zero-based.
>
> You may assume that each input would have exactly one solution.
>
> **Input:** numbers={2, 7, 11, 15}, target=9
> **Output:** index1=1, index2=2



``` java
/**
 * Created by allen on 2016/11/1.
 */
public class leetcode2sum {
    /**
     * Input: numbers={2, 7, 11, 15}, target=9
     * Output: index1=1, index2=2
     * @param numbers
     * @param target
     * @return
     */
    public static  int[] twoSum1(int[] numbers, int target) {
        int [] res = new int[2];
        if(numbers==null||numbers.length<2)
            return res;
        HashMap<Integer,Integer> map = new HashMap<Integer,Integer>();
        for(int i = 0; i < numbers.length; i++){
            if(!map.containsKey(target-numbers[i])){
                map.put(numbers[i],i);
            }else{
                // 注意为什么加 1，因为要求输出的是 index 并且 index 从 1 开始
                res[0]= map.get(target-numbers[i])+1;
                res[1]= i+1;
                break;
            }
        }
        return res;
    }

    public static int[] twoSum2(int[] numbers, int target) {
        int [] res = new int[2];
        if(numbers==null||numbers.length<2)
            return res;
        //copy original list and sort
        int[] copylist = new int[numbers.length];
        System.arraycopy(numbers, 0, copylist, 0, numbers.length);
        Arrays.sort(copylist);

        int low = 0;
        int high = copylist.length-1;

        while(low<high){
            if(copylist[low]+copylist[high]<target)
                low++;
            else if(copylist[low]+copylist[high]>target)
                high--;
            // equal to target
            else{
                res[0]=copylist[low];
                res[1]=copylist[high];
                break;
            }
        }
        //此处也是为了让 index 从 1 开始输出
        int index1 = -1, index2 = -1;
        for(int i = 0; i < numbers.length; i++){
            if(numbers[i] == res[0]&&index1==-1)
                index1 = i+1;
            else if(numbers[i] == res[1]&&index2==-1)
                index2 = i+1;
        }
        res[0] = index1;
        res[1] = index2;
        Arrays.sort(res);
        return res;
    }
	// 测试
    public static void main(String[] args) {
        int numbers[] ={2, 7, 11, 15};
        int target=9;
        int res[] = twoSum1(numbers,target);
        for (Integer r : res)
            System.out.println(r);

        int res2[] = twoSum2(numbers,target);
        for (Integer r1 : res2)
            System.out.println(r1);
    }
}
```

