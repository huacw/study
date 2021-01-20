package net.sea.study.algorithm;

import java.util.*;

/**
 * @Description:
 * @Author: hcw
 * @Date: 2021/1/20 15:26
 */
public class ACTrie {
    private Boolean failureStatesConstructed = false;
    //是否建立了failure表
    private Node root;

    //根结点

    /**
     * 构建ACTrie
     */
    public ACTrie() {
        this(false);
    }

    /**
     * 构建ACTrie
     *
     * @param ignoreCase 关键字是否匹配大小写
     */
    public ACTrie(boolean ignoreCase) {
        this.root = new Node(true, ignoreCase);
    }

    /**
     * 添加一个模式串
     *
     * @param keyword
     */
    public void addKeyword(String keyword) {
        if (keyword == null || keyword.length() == 0) {
            return;
        }
        Node currentState = this.root;
        for (Character character : keyword.toCharArray()) {
            currentState = currentState.insert(character);
        }
        currentState.addEmit(keyword);
    }

    /**
     * 模式匹配
     *
     * @param text 待匹配的文本
     * @return 匹配到的模式串
     */
    public Collection<Emit> parseText(String text) {
        checkForConstructedFailureStates();
        Node currentState = this.root;
        List<Emit> collectedEmits = new ArrayList<>();
        for (int position = 0; position < text.length(); position++) {
            Character character = text.charAt(position);
            currentState = currentState.nextState(character);
            Collection<String> emits = currentState.emit();
            if (emits == null || emits.isEmpty()) {
                continue;
            }
            for (String emit : emits) {
                collectedEmits.add(new Emit(position - emit.length() + 1, position, emit));
            }
        }
        return collectedEmits;
    }

    /**
     * 检查是否建立了failure表
     */
    private void checkForConstructedFailureStates() {
        if (!this.failureStatesConstructed) {
            constructFailureStates();
        }
    }

    /**
     * 建立failure表
     */
    private void constructFailureStates() {
        Queue<Node> queue = new LinkedList<>();
        // 第一步，将深度为1的节点的failure设为根节点
        //特殊处理：第二层要特殊处理，将这层中的节点的失败路径直接指向父节点(也就是根节点)。
        for (Node depthOneState : this.root.children()) {
            depthOneState.setFailure(this.root);
            queue.add(depthOneState);
        }
        this.failureStatesConstructed = true;
        // 第二步，为深度 > 1 的节点建立failure表，这是一个bfs 广度优先遍历
        /**
         * 构造失败指针的过程概括起来就一句话：设这个节点上的字母为C，沿着他父亲的失败指针走，直到走到一个节点，他的儿子中也有字母为C的节点。
         * 然后把当前节点的失败指针指向那个字母也为C的儿子。如果一直走到了root都没找到，那就把失败指针指向root。
         * 使用广度优先搜索BFS，层次遍历节点来处理，每一个节点的失败路径。　　
         */
        while (!queue.isEmpty()) {
            Node parentNode = queue.poll();
            for (Character transition : parentNode.getTransitions()) {
                Node childNode = parentNode.find(transition);
                queue.add(childNode);
                Node failNode = parentNode.getFailure().nextState(transition);
                childNode.setFailure(failNode);
                childNode.addEmit(failNode.emit());
            }
        }
    }

    private static class Node {
        private Map<Character, Node> map;
        private List<String> emits;
        //输出
        private Node failure;
        //失败中转
        private Boolean isRoot = false;
        //是否忽略大小写
        private boolean ignoreCase = false;

        //是否为根结点
        public Node() {
            map = new HashMap<>();
            emits = new ArrayList<>();
        }

        public Node(Boolean isRoot) {
            this();
            this.isRoot = isRoot;
        }

        public Node(Boolean isRoot, boolean ignoreCase) {
            this(isRoot);
            this.ignoreCase = ignoreCase;
        }

        public Node insert(Character character) {
            char key = buildNewKeyWithIgnoreCase(character);
            Node node = this.map.get(key);
            if (node == null) {
                node = new Node();
                map.put(key, node);
            }
            return node;
        }

        public void addEmit(String keyword) {
            emits.add(keyword);
        }

        public void addEmit(Collection<String> keywords) {
            emits.addAll(keywords);
        }

        /**
         * success跳转
         *
         * @param character
         * @return
         */
        public Node find(Character character) {
            return map.get(buildNewKeyWithIgnoreCase(character));
        }

        /**
         * 跳转到下一个状态
         *
         * @param transition 接受字符
         * @return 跳转结果
         */
        private Node nextState(Character transition) {
            Node state = this.find(buildNewKeyWithIgnoreCase(transition));
            // 先按success跳转
            if (state != null) {
                return state;
            }
            //如果跳转到根结点还是失败，则返回根结点
            if (this.isRoot) {
                return this;
            }
            // 跳转失败的话，按failure跳转
            return this.failure.nextState(buildNewKeyWithIgnoreCase(transition));
        }

        /**
         * 根据忽略大小写的状态构建新值
         *
         * @param transition
         * @return
         */
        private char buildNewKeyWithIgnoreCase(Character transition) {
            return ignoreCase ? Character.toLowerCase(transition) : transition;
        }

        public Collection<Node> children() {
            return this.map.values();
        }

        public void setFailure(Node node) {
            failure = node;
        }

        public Node getFailure() {
            return failure;
        }

        public Set<Character> getTransitions() {
            return map.keySet();
        }

        public Collection<String> emit() {
            return this.emits == null ? Collections.emptyList() : this.emits;
        }
    }

    private static class Emit {
        private final String keyword;
        //匹配到的模式串
        private final int start;
        private final int end;

        /**
         * 构造一个模式串匹配结果
         *
         * @param start   起点
         * @param end     终点
         * @param keyword 模式串
         */
        public Emit(final int start, final int end, final String keyword) {
            this.start = start;
            this.end = end;
            this.keyword = keyword;
        }

        /**
         * 获取对应的模式串
         *
         * @return 模式串
         */
        public String getKeyword() {
            return this.keyword;
        }

        @Override
        public String toString() {
            return super.toString() + "=" + this.keyword;
        }
    }

    public static void main(String[] args) {
        ACTrie trie = new ACTrie();
        trie.addKeyword("is");
        trie.addKeyword("This");
        trie.addKeyword("ok");
        trie.addKeyword("he");
        String text = "Hi,This is a book. Ok, That is great!";
        Collection<Emit> emits = trie.parseText(text);
        for (Emit emit : emits) {
            System.out.println(emit.start + " " + emit.end + "\t" + emit.getKeyword() + "\t实际匹配字符串：" + text.substring(emit.start, emit.end + 1));
        }
    }
}
