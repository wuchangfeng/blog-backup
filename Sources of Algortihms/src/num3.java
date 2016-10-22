/**
 * Created by allen on 2016/9/25.
 * 动态规划解决问题的实例一
 */
public class num3 {

    public static int solution(int a[]){

        int last = 0;
        int now = 0;
        int sum = 0;

        for (int i = 0; i < a.length; i++) {

            if (last <= 0)
                now = a[i];
            else
                now = last + a[i];

            if(sum < now) // 留一个临时变量来保存最大值
                sum = now;

            last = now;
        }

        return sum;
    }
    public static void main(String[] args) {

        int a[] = {1, -2, 3, 10, -4, 7, 2, -5};
        int r = solution(a);
        System.out.println(r);
    }
}
