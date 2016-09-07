## 题目：用递归方式来打印出树的三种遍历结果

``` java
/**
 * Created by allen on 2016/9/7.
 */
// 二叉树的三种遍历方式
//             5
//         /      \
//        3        7
//       /\       / \
//      2  4      6  8
// 先序遍历结果为：5324768
// 中序遍历结果为：2345678
// 后序遍历结果为：2436875
public class ex23 {

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

    public static void preorder(BinaryTreeNode root){
        if (root != null){
            System.out.print(root.val);
            preorder(root.left);
            preorder(root.right);
        }
    }

    public static void midorder(BinaryTreeNode root){
        if (root != null){
            midorder(root.left);
            System.out.print(root.val);
            midorder(root.right);
        }

    }

    public static void lastorder(BinaryTreeNode root){
        if (root!= null){
            lastorder(root.left);
            lastorder(root.right);
            System.out.print(root.val);
        }
    }

    public static void main(String[] args) {
        BinaryTreeNode n1 = new BinaryTreeNode(5);
        BinaryTreeNode n2 = new BinaryTreeNode(3);
        BinaryTreeNode n3 = new BinaryTreeNode(7);
        BinaryTreeNode n4 = new BinaryTreeNode(2);
        BinaryTreeNode n5 = new BinaryTreeNode(4);
        BinaryTreeNode n6 = new BinaryTreeNode(6);
        BinaryTreeNode n7 = new BinaryTreeNode(8);
        n1.left = n2;
        n1.right = n3;
        n2.left = n4;
        n2.right = n5;
        n3.left = n6;
        n3.right = n7;
        System.out.println("先序遍历:");
        preorder(n1);
        System.out.println("中序遍历");
        midorder(n1);
        System.out.println("后续遍历");
        lastorder(n1);
    }
}

```

