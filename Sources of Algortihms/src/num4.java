/**
 * Created by allen on 2016/9/26.
 */
public class num4 {

    // 冒泡排序
    public static void sort(int a[]){

        int temp;
        for (int i = 0; i < a.length ; i++) {
            for (int j = 0; j < a.length-1 ; j++) { // 想想为什么是 a.length -1

                if (a[j] < a[j+1]){ // 这里的 j+1 容易写成 i
                    temp = a[j];
                    a[j] = a[j+1];
                    a[j+1] = temp;
                }
            }

            System.out.println("第"+i+"趟排序结果为：");
            for (int k = 0; k < a.length; k++) {
                System.out.print(a[k]);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {

        int a[] = {2,1,4,5,3,9,0,6,7};
        sort(a);
    }
}
