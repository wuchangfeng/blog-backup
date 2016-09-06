## 题目：找出二叉搜索树的第 K 个节点

二叉搜索树是有序的，中序遍历有序输出。但是这个程序测试有点问题，值得再仔细研读。



``` java
/**
 * Created by allen on 2016/9/6.
 */

class BinaryTreeNode {
    int val;
    BinaryTreeNode left;
    BinaryTreeNode right;
    public BinaryTreeNode() {
    }
    public BinaryTreeNode(int val) {
        this.val = val;
    }
}

class solution{
    // 用来记录遍历的数目
    int count = 0;
    public BinaryTreeNode findTheK(BinaryTreeNode root,int k){
        // 这些情况不用考虑
        if (root == null || k <= 0)
            return null;

        BinaryTreeNode targetNode = null;
        // 中序遍历来搜索
        if (root.left != null)
            targetNode = findTheK(root.left,k);

        count ++;
        if(targetNode == null) {
            if (k == count)
                targetNode = root;
                return targetNode;
        }
        // 右字数不为 null 并且还没有找到第 k 个节点时候继续遍历
        if (root.right != null && targetNode == null)
            targetNode = findTheK(root.right,k);

        return targetNode;
    }

}
public class ex22 {
    //             5
    //         /      \
    //        3        7
    //       /\       / \
    //      2  4      6  8
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
        solution s = new solution();
        System.out.println(s.findTheK(n1,1).val);
    }
}

```

