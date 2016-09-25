## 题目：求一个字符流中第一个不重复出现的字符，如 google 中 第一个不重复出现的为 l

这个题目其实很简单，我们之前有了利用 hashMap 来进行高效的数据存储的思想。将每一个字符作为 key 存入到 map 中，出现了一次我们就 +1.。。。但是，最后做了几个测试，发现测试 “google” 的时候输出的是 e 。按道理应该是 l 。找了很久，才意识到是最开始的数据结构不应该选择 hashMap 因为它不能保证数据插入的顺序性，所有遍历的时候有时候 e 在前面有时候 l 在前面，显然他们都满足 value 为1 的情况。而后果断换成了 LinkedHashMap。

```java

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by allen on 2016/9/24.
 */
class solution {

    // 注意这里用 LinkedHashMap 来替代 HashMap
    private Map<Character,Integer> map = new LinkedHashMap<>();

    public  void insert(char c){

        if (!map.containsKey(c)){
            map.put(c,1);
        }else{
            map.put(c,2);
        }
    }

    public  char firstApperenceOnce(String data){

        // 数据的异常
        if (data == null)
            throw  new IllegalArgumentException(data);

        for (int i = 0; i < data.length() ; i++) {
            insert(data.charAt(i));
        }

        for (Character c : map.keySet()){
            if(map.get(c) == 1){
                return c;
            }
            //System.out.println(c+":"+map.get(c));
        }
        return '#';
    }

}

public class num2{

    public static void main(String[] args) {

        solution s = new solution();
        System.out.println(s.firstApperenceOnce("google"));
    }
}
```