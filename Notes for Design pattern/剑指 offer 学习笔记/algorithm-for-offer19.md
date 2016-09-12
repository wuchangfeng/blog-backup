### 题目：判断二叉树是不是平衡二叉树

平衡二叉树的定义：即左右子树的高度差距不超过 1



先求二叉树的深度

``` java
int TreeDepth(TreeNode *root)
{
    if(root == NULL)
    {
        return 0;
    }

    int leftDepth = TreeDepth(root->left);
    int rightDepth = TreeDepth(root->right);

    //  返回左右子树中深度最深的
    return max(leftDepth, rightDepth) + 1;
}
```

第一种解法就是对每一个节点进行递归判断,但是**这个例子容易对节点重复遍历**。完整代码如下：

``` java
/**
 * Created by allen on 2016/9/3.
 */
public class ex20 {

    private static class BinaryTreeNode {
        int val;
        BinaryTreeNode left;
        BinaryTreeNode right;
        public BinaryTreeNode() {
        }
        public BinaryTreeNode(int val) {
            this.val = val;
        }
    }

    public static int treeDepth(BinaryTreeNode root) {
        if (root == null) {
            return 0;
        }
        int left = treeDepth(root.left);
        int right = treeDepth(root.right);
        return left > right ? (left + 1) : (right + 1);
    }

    /**
     * 错误范例，就是在求深度的时候搞错了
     * @param root
     * @return
     */
    public static boolean isBalancedTree(BinaryTreeNode root){
        if (root == null)
            return true;
        int rightHeight = treeDepth(root);// 错误
        int leftHeight = treeDepth(root);// 错误
        if (Math.abs(rightHeight - leftHeight) <=1 ){
            return isBalancedTree(root.left) && isBalancedTree(root.right);
        }else{
            return false;
        }
    }

    public static boolean isBalanced(BinaryTreeNode root) {
        if (root == null) {
            return true;
        }
        int left = treeDepth(root.left);
        int right = treeDepth(root.right);
        int diff = left - right;
        if (diff > 1 || diff < -1) {
            return false;
        }
        return isBalanced(root.left) && isBalanced(root.right);
    }

    //             1
    //         /      \
    //        2        3
    //       /\       / \
    //      4  5     6   7
    //      /
    //     8
    public static void main(String[] args) {
        BinaryTreeNode n1 = new BinaryTreeNode(1);
        BinaryTreeNode n2 = new BinaryTreeNode(1);
        BinaryTreeNode n3 = new BinaryTreeNode(1);
        BinaryTreeNode n4 = new BinaryTreeNode(1);
        BinaryTreeNode n5 = new BinaryTreeNode(1);
        BinaryTreeNode n6 = new BinaryTreeNode(1);
        BinaryTreeNode n7 = new BinaryTreeNode(1);
        n1.left = n2;
        n1.right = n3;
        n2.left = n4;
        n2.right = n5;
        n5.left = n7;

        //System.out.println();
        System.out.println(isBalanced(n1));
      	// 输出 false
    }
}

```

