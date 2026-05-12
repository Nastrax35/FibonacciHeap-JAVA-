package ru.itis.SemWork.FibomacciHeap;

import java.util.ArrayList;
import java.util.List;

public class FibonacciHeap {
    public static long operationCount = 0;

    public static class Node {
        int key;
        int degree;
        Node parent;
        Node child;
        Node left;
        Node right;
        boolean mark;

        Node(int key) {
            this.key = key;
            this.left = this;
            this.right = this;
            this.degree = 0;
            this.mark = false;
        }
    }

    private Node minNode;
    private int n;

    public FibonacciHeap() {
        minNode = null;
        n = 0;
    }

    // 1. ДОБАВЛЕНИЕ (Insert)
    public Node insert(int key) {
        operationCount++;
        Node node = new Node(key);

        if (minNode != null) {
            operationCount += 4;
            node.left = minNode;
            node.right = minNode.right;
            minNode.right.left = node;
            minNode.right = node;

            operationCount++;
            if (key < minNode.key) {
                operationCount++;
                minNode = node;
            }
        } else {
            operationCount++;
            minNode = node;
        }
        operationCount++;
        n++;
        return node;
    }

    // 2. ПОИСК ПО ЗНАЧЕНИЮ (Find)
    public Node find(int key) {
        if (minNode == null) {
            operationCount++;
            return null;
        }

        // Обход всех корневых деревьев
        Node result = findInTree(minNode, key);
        if (result != null) return result;

        // Обход остальных корней
        Node current = minNode.right;
        while (current != minNode) {
            operationCount++;
            result = findInTree(current, key);
            if (result != null) return result;
            current = current.right;
        }
        operationCount++;
        return null;
    }

    private Node findInTree(Node root, int key) {
        if (root == null) return null;
        operationCount++;
        if (root.key == key) {
            return root;
        }

        // Рекурсивный поиск среди детей
        if (root.child != null) {
            Node child = root.child;
            Node start = child;
            do {
                operationCount++;
                Node found = findInTree(child, key);
                if (found != null) return found;
                child = child.right;
            } while (child != start);
        }
        return null;
    }

    // Вспомогательный метод для извлечения минимума
    public Node extractMin() {
        Node z = minNode;
        if (z != null) {
            operationCount++;
            if (z.child != null) {
                Node child = z.child;
                Node start = child;
                do {
                    operationCount += 3;
                    Node next = child.right;
                    child.parent = null;

                    // Добавляем детей в корневой список
                    child.left = minNode;
                    child.right = minNode.right;
                    minNode.right.left = child;
                    minNode.right = child;
                    child = next;
                } while (child != start);
            }

            operationCount += 4;
            // Удаляем minNode из корневого списка
            z.left.right = z.right;
            z.right.left = z.left;

            if (z == z.right) {
                operationCount++;
                minNode = null;
            } else {
                operationCount += 2;
                minNode = z.right;
                consolidate();
            }
            n--;
        }
        return z;
    }

    private void consolidate() {
        int maxDegree = ((int) (Math.log(n) / Math.log(1.618))) + 2;
        Node[] A = new Node[maxDegree + 1];

        // Собираем все корневые узлы в список
        List<Node> rootList = new ArrayList<>();
        if (minNode != null) {
            Node current = minNode;
            do {
                rootList.add(current);
                current = current.right;
            } while (current != minNode);
        }

        for (Node w : rootList) {
            Node x = w;
            int d = x.degree;
            while (d < A.length && A[d] != null) {
                Node y = A[d];
                operationCount++;
                if (x.key > y.key) {
                    Node temp = x;
                    x = y;
                    y = temp;
                }
                link(y, x);
                A[d] = null;
                d++;
                if (d >= A.length) {
                    // Расширяем массив при необходимости
                    Node[] newA = new Node[d + 1];
                    System.arraycopy(A, 0, newA, 0, A.length);
                    A = newA;
                }
            }
            if (d < A.length) {
                A[d] = x;
            }
        }

        // Восстанавливаем корневой список из A
        minNode = null;
        for (Node node : A) {
            if (node != null) {
                if (minNode == null) {
                    minNode = node;
                    node.left = node;
                    node.right = node;
                } else {
                    operationCount += 5;
                    node.left = minNode;
                    node.right = minNode.right;
                    minNode.right.left = node;
                    minNode.right = node;
                    if (node.key < minNode.key) {
                        minNode = node;
                    }
                }
            }
        }
    }

    private void link(Node y, Node x) {
        operationCount += 6;
        // Удаляем y из корневого списка
        y.left.right = y.right;
        y.right.left = y.left;
        y.parent = x;

        // Делаем y ребёнком x
        if (x.child == null) {
            x.child = y;
            y.left = y;
            y.right = y;
        } else {
            y.left = x.child;
            y.right = x.child.right;
            x.child.right.left = y;
            x.child.right = y;
        }
        x.degree++;
        y.mark = false;
    }

    // Уменьшение ключа
    public void decreaseKey(Node x, int k) {
        operationCount++;
        if (k > x.key) return;
        x.key = k;
        Node y = x.parent;
        if (y != null && x.key < y.key) {
            cut(x, y);
            cascadingCut(y);
        }
        operationCount++;
        if (x.key < minNode.key) {
            minNode = x;
        }
    }

    private void cut(Node x, Node y) {
        operationCount += 5;
        // Удаляем x из списка детей y
        x.left.right = x.right;
        x.right.left = x.left;
        y.degree--;
        if (y.child == x) {
            y.child = x.right == x ? null : x.right;
        }

        // Добавляем x в корневой список
        x.left = minNode;
        x.right = minNode.right;
        minNode.right.left = x;
        minNode.right = x;
        x.parent = null;
        x.mark = false;
    }

    private void cascadingCut(Node y) {
        Node z = y.parent;
        if (z != null) {
            if (!y.mark) {
                operationCount++;
                y.mark = true;
            } else {
                operationCount++;
                cut(y, z);
                cascadingCut(z);
            }
        }
    }

    // 3. УДАЛЕНИЕ ПРОИЗВОЛЬНОГО ЭЛЕМЕНТА ПО ЗНАЧЕНИЮ (Delete)
    public boolean delete(int key) {
        operationCount++;
        Node node = find(key);
        if (node == null) {
            return false;
        }
        decreaseKey(node, Integer.MIN_VALUE);
        extractMin();
        return true;
    }

    // Вспомогательный метод для получения размера кучи
    public int size() {
        return n;
    }

    // Вспомогательный метод для проверки, пуста ли куча
    public boolean isEmpty() {
        return minNode == null;
    }
}