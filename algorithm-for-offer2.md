### 题目: 二分查找

```java
public class ex3{
public static void find(int l,int h,int object,int a[]){
    int n = (l+h)/2;
    if(a[n]==object)
        System.out.print("位置在:"+n);
    else if(a[n]>object){
        h = n - 1;
        find(0,h,2,a);
    }
    else{
        find(n,h,2,a);
    }
}
public static void main(String[] args) {
    int a[] = {2,3,7,9,11,13};
    find(0,a.length-1,2,a);
}
}
```
