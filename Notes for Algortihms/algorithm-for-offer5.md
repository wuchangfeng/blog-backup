### 题目：查找字符串中第一个出现的不重复的字符

思路用的是 Java 中的 HassMap 中的 key 和 value 来解决。一次性写入进 hashmap.

这种方法简直太好。

[遍历hashMap 的四种方法](http://blog.csdn.net/tjcyjd/article/details/11111401)

``` java
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by allen on 2016/8/17.
 */
public class ex6{

    public static void main(String[] args) {
        System.out.println(get("alibaba"));
        System.out.println(get("taobao"));
        System.out.println(get("aabbccd"));
        System.out.println(get("ddbbdd"));
        System.out.println(get("ahsdkdhask"));
    }
    
    public static Character get(String s) {
        // 判断边界条件
        if (s == null || s.length() < 1) {
            // 抛出异常
            throw new IllegalArgumentException("should not be null or empty");
        }
        Map<Character, Integer> map = new LinkedHashMap<Character, Integer>();
        int len = s.length();

        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);// 获取字符
            Integer already = map.get(c);//获取指定 key 处对应着的 value

            // 如果 already 为 null 表示没有存入 map
            // 如果 already 不为 null 表示该 key 已经存过进 map 一次
            if(already == null){
                already = 1;
            }else{
                already = already + 1;
            }
            // 三目运算符
            //already = (already == null) ? 0 : already;
            // s.charAt(i) 代表着 key 如果字符重复那么久 ++value
            //map.put(s.charAt(i), ++already);
            map.put(s.charAt(i), already);
        }
        // 来遍历 HashMap
        Set<Map.Entry<Character, Integer>> entries = map.entrySet();
        for (Map.Entry<Character, Integer> entry : entries) {
            if (entry.getValue() == 1) {// 如果 value 中的 value 为 1 返回该位置的 key
                return entry.getKey();
            }
        }
        return null;
    }
}

```



