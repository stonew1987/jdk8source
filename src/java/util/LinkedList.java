

package java.util;

import java.util.function.Consumer;

/**
 * LinkedList可以在任何位置进行高效地插入和移除操作的有序序列
 *
 * LinkedList 是一个继承于AbstractSequentialList的双向链表。它也可以被当作堆栈、队列或双端队列进行操作。
 * LinkedList 实现 List 接口，能对它进行队列操作。
 * LinkedList 实现 Deque 接口，即能将LinkedList当作双端队列使用。
 * LinkedList 实现了Cloneable接口，即覆盖了函数clone()，能克隆。
 * LinkedList 实现java.io.Serializable接口，这意味着LinkedList支持序列化，能通过序列化去传输。
 * LinkedList 是非同步的。
 * @author  Josh Bloch
 * @see     List
 * @see     ArrayList
 * @since 1.2
 */

public class LinkedList<E>
    extends AbstractSequentialList<E>
    implements List<E>, Deque<E>, Cloneable, java.io.Serializable
{
    //实现Serilizable接口时，将不需要序列化的属性前添加关键字transient，序列化对象的时候，这个属性就不会序列化到指定的目的地中。
    transient int size = 0;

    //指向首节点
    transient Node<E> first;

    //指向最后一个节点
    transient Node<E> last;

    //构建一个空列表
    public LinkedList() {
    }

    //构建一个包含集合c的列表
    public LinkedList(Collection<? extends E> c) {
        this();
        addAll(c);
    }

    /**
     * 向链表的起始位置插入节点元素
     */
    private void linkFirst(E e) {
        //获取原链表的起始节点
        final Node<E> f = first;
        //创建一个prev(上个节点)为null, next (下个节点)为 原链表的起始节点，值为e 的新节点
        final Node<E> newNode = new Node<>(null, e, f);
        //将新创建的newNode作为首节点
        first = newNode;
        //若原链表起始节点为null,则newNode也赋值给最后一个节点
        if (f == null)
            last = newNode;
        else
            //若原链表起始节点不为null,则将原链表的起始节点的prev指向newNode（新节点）
            f.prev = newNode;
        //大小+1
        size++;
        modCount++;
    }

    /**
     * 向链表的最后位置插入节点元素
     */
    void linkLast(E e) {
        //获取原链表的尾节点
        final Node<E> l = last;
        //创建一个prev(上个节点)为原链表的尾节点l, next (下个节点)为 null，值为e 的新节点
        final Node<E> newNode = new Node<>(l, e, null);
        // 链表的最后一个节点赋值成新创建的newNode
        last = newNode;
        //若原链表最后一个节点为null,则newNode也赋值给起始节点
        if (l == null)
            first = newNode;
        else
            //若原链表最后一个节点不为null,则将原链表的最后一个节点l的prev指向newNode（新创建的节点）
            l.next = newNode;
        //大小+1
        size++;
        modCount++;
    }

    /**
     * 向节点succ之前插入一个节点e
     */
    void linkBefore(E e, Node<E> succ) {
        // assert succ != null;
        //获取节点succ前一个节点
        final Node<E> pred = succ.prev;
        //创建一个prev为pred，next为succ,值为e的新节点
        final Node<E> newNode = new Node<>(pred, e, succ);
        //将succ的prev指向新创建的节点newNode
        succ.prev = newNode;
        //若succ为原链表的首节点,则将原链表的起始位置赋值为新创建的节点
        if (pred == null)
            first = newNode;
        else
            //不是首节点的，则将pred的next 指向newNode
            pred.next = newNode;
        size++;
        modCount++;
    }

    /**
     * 移除链表的首节点并返回
     */
    private E unlinkFirst(Node<E> f) {
        // assert f == first && f != null;
        // 获取 节点f的值
        final E element = f.item;
        // 获取节点的下个节点
        final Node<E> next = f.next;
        // 该节点 item next 置为null,以便gc
        f.item = null;
        f.next = null; // help GC
        // 原链表的首节点指向 next
        first = next;
        //若next 为null,即删除后的链表为空链表，last 也设置为null
        if (next == null)
            last = null;
        else
            //否则将 next 的 prev置为null
            next.prev = null;
        size--;
        modCount++;
        return element;
    }

    /**
     * 移除链表的尾节点
     */
    private E unlinkLast(Node<E> l) {
        // assert l == last && l != null;
        final E element = l.item;
        final Node<E> prev = l.prev;
        l.item = null;
        l.prev = null; // help GC
        last = prev;
        if (prev == null)
            first = null;
        else
            prev.next = null;
        size--;
        modCount++;
        return element;
    }

    /**
     * 移除Node节点值为x节点
     */
    E unlink(Node<E> x) {
        // assert x != null;
        // 获得x的节点值
        final E element = x.item;
        // 获取x节点的下个节点
        final Node<E> next = x.next;
        // 获取x节点的上个节点
        final Node<E> prev = x.prev;

        //判断移除的节点是否为链表首个节点
        if (prev == null) {
            //链表的first指向 next
            first = next;
        } else {
            //将 x节点的prev的next指向 x节点的next
            prev.next = next;
            //x节点上的prev置为null
            x.prev = null;
        }

        //移除的节点是否为链表尾节点
        if (next == null) {
            // 链表的last指向x节点中的prev
            last = prev;
        } else {
            //x的下个节点的prev 指向 x节点的prev
            next.prev = prev;
            //x节点上的next置为null
            x.next = null;
        }

        x.item = null;
        size--;
        modCount++;
        return element;
    }

    /**
     * 获取链表的第一个节点元素
     * 如果链表为空，抛异常
     *
     * peek 也是返回链表的第一个节点元素，如果链表为null,则返回null
     */
    public E getFirst() {
        final Node<E> f = first;
        if (f == null)
            throw new NoSuchElementException();
        return f.item;
    }

    /**
     * 获取链表的最后一个节点元素
     */
    public E getLast() {
        final Node<E> l = last;
        if (l == null)
            throw new NoSuchElementException();
        return l.item;
    }

    /**
     * 移除链表的第一个节点
     */
    public E removeFirst() {
        final Node<E> f = first;
        if (f == null)
            throw new NoSuchElementException();
        return unlinkFirst(f);
    }

    /**
     * 移除链表的最后一个节点
     */
    public E removeLast() {
        final Node<E> l = last;
        if (l == null)
            throw new NoSuchElementException();
        return unlinkLast(l);
    }

    /**
     * 在链表的头部添加元素
     */
    public void addFirst(E e) {
        linkFirst(e);
    }

    /**
     * 在链表的尾部添加元素
     */
    public void addLast(E e) {
        linkLast(e);
    }

    /**
     * 判断是否包含指定元素
     */
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    /**
     * 返回linkList 的大小
     */
    public int size() {
        return size;
    }

    /**
     * 在链表尾部插入节点
     */
    public boolean add(E e) {
        linkLast(e);
        return true;
    }

    /**
     * 移除元素 o
     */
    public boolean remove(Object o) {
        // 判断元素是否为null
        if (o == null) {
            // 循环遍历 链表
            for (Node<E> x = first; x != null; x = x.next) {
                if (x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        } else {
            for (Node<E> x = first; x != null; x = x.next) {
                if (o.equals(x.item)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 在链表尾部插入集合c中的所有元素
     */
    public boolean addAll(Collection<? extends E> c) {
        return addAll(size, c);
    }

    /**
     * 在index位置插入集合c中的所有元素
     */
    public boolean addAll(int index, Collection<? extends E> c) {
        //检查index 是否越界
        checkPositionIndex(index);
        // 将集合c转出Object数组
        Object[] a = c.toArray();
        //计算新加入的元素个数
        int numNew = a.length;
        if (numNew == 0)
            return false;

        // 定义index 节点的前置 和后置节点
        Node<E> pred, succ;
        //在链表尾部追加数据
        if (index == size) {
            //后置节点设置为null
            succ = null;
            //前置节点设置为原链表的last节点
            pred = last;
        } else {
            //取出index节点，作为后置节点
            succ = node(index);
            //前置节点是，index节点的前一个节点
            pred = succ.prev;
        }

        //遍历原数组，依次执行插入节点操作
        for (Object o : a) {
            @SuppressWarnings("unchecked") E e = (E) o;
            // 构建一个新的节点
            Node<E> newNode = new Node<>(pred, e, null);
            //如果前置节点是空，说明是头结点
            if (pred == null)
                first = newNode;
            else
                //否则 前置节点的后置节点设置问新节点
                pred.next = newNode;
            //步进，当前的节点为前置节点了，为下次添加节点做准备
            pred = newNode;
        }

        //循环结束后，判断，如果后置节点是null。 说明此时是在队尾append的。
        if (succ == null) {
            //则设置尾节点
            last = pred;
        } else {
            // 否则是在队中插入的节点 ，更新前置节点 后置节点
            pred.next = succ;
            //更新后置节点的前置节点
            succ.prev = pred;
        }
        // 修改数量size
        size += numNew;
        modCount++;
        return true;
    }

    /**
     * 清空链表
     */
    public void clear() {
        // Clearing all of the links between nodes is "unnecessary", but:
        // - helps a generational GC if the discarded nodes inhabit
        //   more than one generation
        // - is sure to free memory even if there is a reachable Iterator
        // 循环遍历节点 并将节点的item，next，prev置为null,以便gc
        for (Node<E> x = first; x != null; ) {
            Node<E> next = x.next;
            x.item = null;
            x.next = null;
            x.prev = null;
            x = next;
        }
        first = last = null;
        size = 0;
        modCount++;
    }



    /**
     * 根据index获取元素值
     */
    public E get(int index) {
        checkElementIndex(index);
        //返回index节点的item值
        return node(index).item;
    }

    /**
     * 修改index位置的节点元素为element，并返回原值
     */
    public E set(int index, E element) {
        // 检查index是否越界
        checkElementIndex(index);
        // 获取 原链表index位置的node节点
        Node<E> x = node(index);
        // 获取item
        E oldVal = x.item;
        // 赋值
        x.item = element;
        return oldVal;
    }

    /**
     * 在index位置前面添加element元素
     */
    public void add(int index, E element) {
        checkPositionIndex(index);
        if (index == size)
            // 插入链表的尾部
            linkLast(element);
        else
            // 插入index节点的前面
            linkBefore(element, node(index));
    }

    /**
     * 移除index 位置的 节点元素
     */
    public E remove(int index) {
        checkElementIndex(index);
        return unlink(node(index));
    }

    /**
     * 检查index, 0<= index < siz
     */
    private boolean isElementIndex(int index) {
        return index >= 0 && index < size;
    }

    /**
     * 检查index, 0<= index <= siz
     */
    private boolean isPositionIndex(int index) {
        return index >= 0 && index <= size;
    }

    /**
     *
     */
    private String outOfBoundsMsg(int index) {
        return "Index: "+index+", Size: "+size;
    }

    private void checkElementIndex(int index) {
        if (!isElementIndex(index))
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    private void checkPositionIndex(int index) {
        if (!isPositionIndex(index))
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /**
     * 返回index位置的节点
     */
    Node<E> node(int index) {
        // assert isElementIndex(index);
        //通过下标获取某个node 的时候，（增、查 ），会根据index处于前半段还是后半段 进行一个折半，以提升查询效率
        if (index < (size >> 1)) {
            Node<E> x = first;
            for (int i = 0; i < index; i++)
                x = x.next;
            return x;
        } else {
            Node<E> x = last;
            for (int i = size - 1; i > index; i--)
                x = x.prev;
            return x;
        }
    }

    // Search Operations

    /**
     * 判断指定元素在链表中的位置 -- 从链表头开始查找
     */
    public int indexOf(Object o) {
        int index = 0;
        //判断指定元素知否为null
        if (o == null) {
            //循环遍历
            for (Node<E> x = first; x != null; x = x.next) {
                if (x.item == null) // 指定元素为null 通过 == 判断
                    return index;
                index++;
            }
        } else {
            for (Node<E> x = first; x != null; x = x.next) {
                if (o.equals(x.item)) // 指定元素不为null 通过 equals 判断
                    return index;
                index++;
            }
        }
        return -1;
    }

    /**
     * 判断指定元素在链表中的最后位置 -- 从链表尾部开始查找
     */
    public int lastIndexOf(Object o) {
        int index = size;
        if (o == null) {
            for (Node<E> x = last; x != null; x = x.prev) {
                index--;
                if (x.item == null)
                    return index;
            }
        } else {
            for (Node<E> x = last; x != null; x = x.prev) {
                index--;
                if (o.equals(x.item))
                    return index;
            }
        }
        return -1;
    }


    /**
     * 返回链表中的第一个元素
     * 如链表为null,则返回null
     */
    public E peek() {
        final Node<E> f = first;
        return (f == null) ? null : f.item;
    }

    /**
     * 返回链表中的第一个元素
     * 同 getFirst
     */
    public E element() {
        return getFirst();
    }

    /**
     * 从链表头部弹出一个节点
     * 如链表为null,则返回null
     */
    public E poll() {
        final Node<E> f = first;
        return (f == null) ? null : unlinkFirst(f);
    }

    /**
     * 从链表头部移除一个节点
     * 如链表为null,抛异常
     */
    public E remove() {
        return removeFirst();
    }

    /**
     * 在链表尾部插入节点
     * 返回 boolean
     */
    public boolean offer(E e) {
        return add(e);
    }

    // Deque operations
    /**
     * 在链表头部插入节点
     * 返回 boolean
     */
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    /**
     * 在链表尾部插入节点
     * 返回 boolean
     */
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    /**
     *
     */
    public E peekFirst() {
        final Node<E> f = first;
        return (f == null) ? null : f.item;
     }

    /**
     *
     */
    public E peekLast() {
        final Node<E> l = last;
        return (l == null) ? null : l.item;
    }

    /**
     *
     */
    public E pollFirst() {
        final Node<E> f = first;
        return (f == null) ? null : unlinkFirst(f);
    }

    /**
     *
     */
    public E pollLast() {
        final Node<E> l = last;
        return (l == null) ? null : unlinkLast(l);
    }

    /**
     *
     */
    public void push(E e) {
        addFirst(e);
    }

    /**
     *
     */
    public E pop() {
        return removeFirst();
    }

    /**
     *
     */
    public boolean removeFirstOccurrence(Object o) {
        return remove(o);
    }

    /**
     *
     */
    public boolean removeLastOccurrence(Object o) {
        if (o == null) {
            for (Node<E> x = last; x != null; x = x.prev) {
                if (x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        } else {
            for (Node<E> x = last; x != null; x = x.prev) {
                if (o.equals(x.item)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     */
    public ListIterator<E> listIterator(int index) {
        checkPositionIndex(index);
        return new ListItr(index);
    }

    private class ListItr implements ListIterator<E> {
        private Node<E> lastReturned;
        private Node<E> next;
        private int nextIndex;
        private int expectedModCount = modCount;

        ListItr(int index) {
            // assert isPositionIndex(index);
            next = (index == size) ? null : node(index);
            nextIndex = index;
        }

        public boolean hasNext() {
            return nextIndex < size;
        }

        public E next() {
            checkForComodification();
            if (!hasNext())
                throw new NoSuchElementException();

            lastReturned = next;
            next = next.next;
            nextIndex++;
            return lastReturned.item;
        }

        public boolean hasPrevious() {
            return nextIndex > 0;
        }

        public E previous() {
            checkForComodification();
            if (!hasPrevious())
                throw new NoSuchElementException();

            lastReturned = next = (next == null) ? last : next.prev;
            nextIndex--;
            return lastReturned.item;
        }

        public int nextIndex() {
            return nextIndex;
        }

        public int previousIndex() {
            return nextIndex - 1;
        }

        public void remove() {
            checkForComodification();
            if (lastReturned == null)
                throw new IllegalStateException();

            Node<E> lastNext = lastReturned.next;
            unlink(lastReturned);
            if (next == lastReturned)
                next = lastNext;
            else
                nextIndex--;
            lastReturned = null;
            expectedModCount++;
        }

        public void set(E e) {
            if (lastReturned == null)
                throw new IllegalStateException();
            checkForComodification();
            lastReturned.item = e;
        }

        public void add(E e) {
            checkForComodification();
            lastReturned = null;
            if (next == null)
                linkLast(e);
            else
                linkBefore(e, next);
            nextIndex++;
            expectedModCount++;
        }

        public void forEachRemaining(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            while (modCount == expectedModCount && nextIndex < size) {
                action.accept(next.item);
                lastReturned = next;
                next = next.next;
                nextIndex++;
            }
            checkForComodification();
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    private static class Node<E> {
        //表示该节点包含的值
        E item;
        //表达当前节点的下一个节点
        Node<E> next;
        //表示当前节点的上一个节点
        Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    /**
     * @since 1.6
     */
    public Iterator<E> descendingIterator() {
        return new DescendingIterator();
    }

    /**
     * Adapter to provide descending iterators via ListItr.previous
     */
    private class DescendingIterator implements Iterator<E> {
        private final ListItr itr = new ListItr(size());
        public boolean hasNext() {
            return itr.hasPrevious();
        }
        public E next() {
            return itr.previous();
        }
        public void remove() {
            itr.remove();
        }
    }

    @SuppressWarnings("unchecked")
    private LinkedList<E> superClone() {
        try {
            return (LinkedList<E>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    /**
     * 浅克隆
     */
    public Object clone() {
        LinkedList<E> clone = superClone();

        // Put clone into "virgin" state
        clone.first = clone.last = null;
        clone.size = 0;
        clone.modCount = 0;

        // Initialize clone with our elements
        for (Node<E> x = first; x != null; x = x.next)
            clone.add(x.item);

        return clone;
    }

    /**
     * 转出Object 数组
     */
    public Object[] toArray() {
        Object[] result = new Object[size];
        int i = 0;
        for (Node<E> x = first; x != null; x = x.next)
            result[i++] = x.item;
        return result;
    }

    /**
     *
     */
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size)
            a = (T[])java.lang.reflect.Array.newInstance(
                                a.getClass().getComponentType(), size);
        int i = 0;
        Object[] result = a;
        for (Node<E> x = first; x != null; x = x.next)
            result[i++] = x.item;

        if (a.length > size)
            a[size] = null;

        return a;
    }

    private static final long serialVersionUID = 876323262645176354L;


    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException {
        // Write out any hidden serialization magic
        s.defaultWriteObject();

        // Write out size
        s.writeInt(size);

        // Write out all elements in the proper order.
        for (Node<E> x = first; x != null; x = x.next)
            s.writeObject(x.item);
    }

    /**
     * Reconstitutes this {@code LinkedList} instance from a stream
     * (that is, deserializes it).
     */
    @SuppressWarnings("unchecked")
    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {
        // Read in any hidden serialization magic
        s.defaultReadObject();

        // Read in size
        int size = s.readInt();

        // Read in all elements in the proper order.
        for (int i = 0; i < size; i++)
            linkLast((E)s.readObject());
    }

    /**
     *
     * @since 1.8
     */
    @Override
    public Spliterator<E> spliterator() {
        return new LLSpliterator<E>(this, -1, 0);
    }

    /** A customized variant of Spliterators.IteratorSpliterator */
    static final class LLSpliterator<E> implements Spliterator<E> {
        static final int BATCH_UNIT = 1 << 10;  // batch array size increment
        static final int MAX_BATCH = 1 << 25;  // max batch array size;
        final LinkedList<E> list; // null OK unless traversed
        Node<E> current;      // current node; null until initialized
        int est;              // size estimate; -1 until first needed
        int expectedModCount; // initialized when est set
        int batch;            // batch size for splits

        LLSpliterator(LinkedList<E> list, int est, int expectedModCount) {
            this.list = list;
            this.est = est;
            this.expectedModCount = expectedModCount;
        }

        final int getEst() {
            int s; // force initialization
            final LinkedList<E> lst;
            if ((s = est) < 0) {
                if ((lst = list) == null)
                    s = est = 0;
                else {
                    expectedModCount = lst.modCount;
                    current = lst.first;
                    s = est = lst.size;
                }
            }
            return s;
        }

        public long estimateSize() { return (long) getEst(); }

        public Spliterator<E> trySplit() {
            Node<E> p;
            int s = getEst();
            if (s > 1 && (p = current) != null) {
                int n = batch + BATCH_UNIT;
                if (n > s)
                    n = s;
                if (n > MAX_BATCH)
                    n = MAX_BATCH;
                Object[] a = new Object[n];
                int j = 0;
                do { a[j++] = p.item; } while ((p = p.next) != null && j < n);
                current = p;
                batch = j;
                est = s - j;
                return Spliterators.spliterator(a, 0, j, Spliterator.ORDERED);
            }
            return null;
        }

        public void forEachRemaining(Consumer<? super E> action) {
            Node<E> p; int n;
            if (action == null) throw new NullPointerException();
            if ((n = getEst()) > 0 && (p = current) != null) {
                current = null;
                est = 0;
                do {
                    E e = p.item;
                    p = p.next;
                    action.accept(e);
                } while (p != null && --n > 0);
            }
            if (list.modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }

        public boolean tryAdvance(Consumer<? super E> action) {
            Node<E> p;
            if (action == null) throw new NullPointerException();
            if (getEst() > 0 && (p = current) != null) {
                --est;
                E e = p.item;
                current = p.next;
                action.accept(e);
                if (list.modCount != expectedModCount)
                    throw new ConcurrentModificationException();
                return true;
            }
            return false;
        }

        public int characteristics() {
            return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
        }
    }

}
