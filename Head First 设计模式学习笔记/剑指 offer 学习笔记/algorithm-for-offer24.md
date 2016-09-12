## 题目：判断二叉树是不是对称的

思路理解：其实核心还是递归啊，不停的左边跟右边比较。对称的去比较。可以先联想三个节点的例子，慢慢扩展开来。

``` java
public class ex24 {

    public static class BinaryTreeNode {
        int val;
        BinaryTreeNode left;
        BinaryTreeNode right;
        public BinaryTreeNode() {
        }
        public BinaryTreeNode(int val) {
            this.val = val;
        }
    }

    private static boolean isSame(BinaryTreeNode right, BinaryTreeNode left){

        if (right != null && left != null){// 同时都不是 null
            if (isSame(right.right,left.left)&&isSame(left.right,right.left))
                return true;
            else
                return false;
        }else if(right == null && left == null){// 同时都为 null
            return true;
        }else{ // 其他情况
            return false;
        }
    }

    public static void main(String[] args) {
        BinaryTreeNode n1 = new BinaryTreeNode(5);
        BinaryTreeNode n2 = new BinaryTreeNode(2);
        BinaryTreeNode n3 = new BinaryTreeNode(2);
        BinaryTreeNode n4 = new BinaryTreeNode(3);
        BinaryTreeNode n5 = new BinaryTreeNode(4);
        BinaryTreeNode n6 = new BinaryTreeNode(4);
        BinaryTreeNode n7 = new BinaryTreeNode(2);
        n1.left = n2;
        n1.right = n3;
        n2.left = n4;
        n2.right = n5;
        n3.left = n6;
        n3.right = n7;
        // 默认 root 不是 null 了
        System.out.println(isSame(n1.right,n1.left));
    }
}

```

