### 题目：求两个有序数组的交集

```java
public class ex5{
public static void print2(int a[],int b[],int M,int N){
    int result[] = new int[100];
    int i = 0;
    int k = 0;
    int z = 0;
    while(i < M && k < N)
    {
        if(a[i] == b[k])
        {
            result[z] = a[i];
            i++;
            k++;
            z++;
        }
        else if(a[i] > b[k])
        {
            i++;
        }
        else
        {
            k++;
        }
    }
    for(i = 0; i < z; i++)
    {
        System.out.print(result[i]+",");
    }
}

public static void main(String[] args) {

    int a[] = {9,6,5,3,2};
    int b[] = {10,9,6,3,1};
    //System.out.println(a.length-1);
    print2(a,b,a.length,b.length);
}
}
```
