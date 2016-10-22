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
