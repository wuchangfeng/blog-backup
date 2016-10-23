### 题目：判断树的子结构，输入 A 和 B，判断 B 是不是 A 的子结构(子树)。

这一节的重点还是树的操作以及代码的鲁棒性。当树 A 或者 树 B 为 null 的时候一定要做相应的处理，负责程序就很容易崩溃。



``` java
    public static boolean hasSubtree(BinaryTreeNode root1, BinaryTreeNode root2) {
        // 只要两个对象是同一个就返回true
        // 【注意此处与书本上的不同，书本上的没有这一步】
        if (root1 == root2) {
            return true;
        }
        // 只要树B的根结点点为空就返回true
        if (root2 == null) {
            return true;
        }
        // 树B的根结点不为空，如果树A的根结点为空就返回false
        if (root1 == null) {
            return false;
        }
        // 记录匹配结果
        boolean result = false;
        // 如果结点的值相等就，调用匹配方法
        if (root1.value == root2.value) {
            result = match(root1, root2);
        }
        // 如果匹配就直接返回结果
        if (result) {
            return true;
        }
        // 如果不匹配就找树A的左子结点和右子结点进行判断
        return hasSubtree(root1.left, root2) || hasSubtree(root1.right, root2);
    }
```



上述代码的作用就是找到 B 与 A 相同的根节点以方便进行下一步匹配，如果找到了继续 match 下去，找不到了的话，继续找 A 的子树上，一直到找到为止。

找到之后，我们从该 root 节点进行依次匹配的工作。如下函数：

``` java
public static boolean match(BinaryTreeNode root1, BinaryTreeNode root2) {
        // 注意递归。只要两个对象是同一个就返回true
        if (root1 == root2) {
            return true;
        }
        // 递归到只要树B的根结点点为空就返回true
        if (root2 == null) {
            return true;
        }
        // 树B的根结点不为空，如果树A的根结点为空就返回false
        if (root1 == null) {
            return false;
        }
        // 如果两个结点的值相等，则分别判断其左子结点和右子结点
        if (root1.value == root2.value) {
            return match(root1.left, root2.left) && match(root1.right, root2.right);
        }
        // 结点值不相等返回false
        return false;
    }
```

当然少不了测试的案例，按照之前的说法，这里的测试应该更为重要。输入不同 root 的情况。