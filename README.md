JAVA多线程常用工具类编码实践，线程安全对比总结：   
Synchronized、Atomic包及其方法的原子性、同步容器、并发容器、
饿汉/懒汉单例模式的线程安全、安全发布对象、不可变量CollectionsUnmodifiable和GuavaImmutable、
模拟多个互联网用户访问WEB Server，并使用过滤器Filter和拦截器Interceptor实践threadLocal的线程封闭、
JUC中的AQS、ReentrantLock/StampedLock等、callable+线程返结果Future、线程池的使用和测试等。  

生产过程中的一些多线程并发编程的经验总结：     
1.使用本地变量：线程封闭threalocal、节省内存且便于回收。   
2.尽量避免使用静态变量，如果一定要使用，只读不可变，例如final、CollectionUnmodifiable、Immutable。   
3.使用不可变类：String，Integer，Long等，降低代码中需要同步的数量。   
4.最小化锁的作用域范围，例如synchronized需多线程读写的资源代码块，而不是synchronized整个方法。    
5.使用线程池Executor，而不是直接new Thread。   
6.使用AQS同步器(CountdownLatch、Semaphore、CyclicBarrier等)，而不是使用线程的wait和notify。    
7.使用阻塞队列BlockingQueue实现生产消费者模式，线程之间共享数据。   
8.使用并发容器而不是同步容器(同步容器内部，在每个方法加synchronzied线程安全但性能不好，而且方法间并发线程不安全)。   
9.使用Semophare控制并发数。  

## Atomic
原子性：提供了互斥访问能力，同一时刻只能有一个线程对它进行操作。   
竞争激烈时能维持常态，比Lock性能好；劣势是只能同步一个值。    
Atomic包底层基于本地接口unsafe类实现CAS：当前对象va1中获取期望值var2(工作内存) 与 底层获取当前值var5(主内存)相比较，如果相同才做操作。  
CAS存在ABA的问题，场景严格需要时，使用AtomicStampedReference，compareAndSet中增加了参数stamp做数据版本号解决。    

- AtomicIntegerTestY.java  
多线程竞争场景下，自增、自减、比较的线程安全原子变量AtomicInteger。  
AtomicInteger.getAndIncrement()增加了一个值，返回值是增加前的值。   
AtomicInteger.incrementAndGet()增加了一个值，返回值是增加后的值。   

- LongAddrTestY.java  
LongAdder与AtomicLong功能一样，区别是：   
1.自增方法是LongAdder.increment()或LongAdder.add(nL)--每次自增n。  
2.优点：高并发时LongAdder性能比AtomicLong好，因为其进行了热点分离，将Long拆解成多块区域。  
3.缺点：LongAdder计数会有不准的可能，统计或序列生成场景不能使用。  
支持atomicLong.compareAndSet(expectedValue, newValue);   

- AtomicBooleanTestY.java  
场景：需要一段代码在高并发多线程环境下，有且仅执行一次。  
AtomicBoolean.compareAndSet(false,true)方法，参数1是expectedValue，参数2是updateValue(实际值与参数1一致，则更新;不一致则不更新)。     
在高并发多线程情况下，可以保证compareAndSet的if分支内的代码只被一个线程抢到，最终只执行一次。    

- AtomicReference.java  
提供了一个可以被原子性读和写的对象引用变量
AtomicReference.compareAndSet(expect_ref,update_ref)方法，将当前引用于一个期望值(引用)进行比较：
如果相等，在该 AtomicReference 对象内部设置一个新的引用。  

- AtomicIntegerFieldUpdaterTry.java，AtomicIntegerFieldUpdaterModel    
原子性更新某个类的实例中的某个属性，该属性定义必须是public volatile int   
指定原子性更新的类名和属性名称 updater = AtomicIntegerFieldUpdater.newUpdater(className.class, fieldName)  
使用时的控制类似上面的AtomicReference，只是引入了类名和属性名，updater("fieldName").compareAndSet(className,expect_value,update_value)

## SafePublic   
发布对象：使一个对象能够被当前范围之外的代码使用。  
对象逸出：一种错误的对象发布方式。当一个对象还没被构造完成时，就被其他线程可见了。  
所以多线程并发访问的前提下，发布对象时要考虑安全发布。用单例模式实践体验下。    
- HungerySingleton.java：  
安全发布对象方法之一：在静态初始化方法中初始化一个对象引用：     
单例饿汉模式，静态初始化方法中初始化一个对象引用，单例在类装载时就创建好了，是线程安全的。  
使用场景：  
1.私有构造函数处理不多，否则类加载慢，性能有问题；  
2.对象肯定会被使用。  

- HungerySingleton2.java   
还是单例饿汉模式，还可以：  
1.初始化对象 instance()= null，而不是直接new出来。  
2.再使用静态代码块实例化单例。  

- LazySingleton.java
安全发布对象方法之一：将对象的引用保存到volatile类型域或AtomicReference对象中。  
安全发布对象方法之一：将对象的引用保存到一个由锁保护的域中。   
单例懒汉模式，比饿汉模式省资源，因为是单例在第一次使用时才创建。为确保线程安全：  
1.使用双重同步锁保护instance引用，构造单例。  
2.使用volatile修饰instance，限制CPU和JVM指令重排。  

- EnumSingleton.java  
单例的枚举实现是最佳方法，利用了enum的语法糖-编译时为代码块增加static属性。
从而JVM会保证实例化方法绝对只调用一次。  

## Immutable   
如果允许，使用不可变对象，也是一种线程安全策略。  
例如，经常使用final修饰基本变量，其实就是常量不可修改。
final修饰引用类型变量，是指的该引用类型变量不能再指向其他对象，但其指向的对象数值是可以修改。  

- CollectionsUnmodifiable.java  
Collections.unmodifiableMap(map)、Collections.unmodifiableList(list)、Collections.unmodifiableSet(set)
等方法，可以将相应的Collection转换为不可变的，如果改变会抛异常，记得catch。  

-  GuavaImmutable.java  
Guava中的ImmutableList、ImmutableSet、ImmutableMap提供.of()方法，可以定义不可变的Collection，无法增加、修改数据，
会抛出异常java.lang.UnsupportedOperationException，记得catch。  

##  ThreadLocal
线程安全手段之一是线程封闭。  
1.堆栈封闭，是不经意使用的最常用的线程封闭方法：能用局部变量不用全局变量，因为局部变量是线程封闭的，线程安全。  
2.threadlocal，也是线程封闭的方法之一，其内部维护了一个map，key是线程id，value是要封闭的对象。  
模拟多用户访问网站，编程实践一下threadlocal。  

- HttpFilter.java, RequestHolder.java     
过滤器HttpFilter implements Filter，在doFilter方法中，将ServletRequest先强制转换成HttpServletRequest，
从HttpServletRequest能获取本次请求的路径、用户等信息，
使用Thread.currentThread().getId()获取处理本次请求的线程ID，add到自定义的ThreadLocal容器RequestHolder中(支持增删查)。  
SpringBoot的启动类ConcurrencyApplication中增加FilterRegistrationBean，  
将上述自定义的HttpFilter，setFilter到FilterRegistrationBean中，
还可以使用filterRegistrationBean.addUrlPatterns，设置想拦截的url。  
过滤器Filter:依赖于servlet容器。在实现上基于函数回调，可以对几乎所有请求进行过滤，
但缺点是一个过滤器实例只能在容器初始化时调用一次。  
使用过滤器的目的是用来做一些过滤操作，获取我们想要获取的数据，
比如：在过滤器中修改字符编码；在过滤器中修改HttpServletRequest的一些参数，包括：过滤低俗文字、危险字符等。   

- HttpInterceptor.java , RequestHolder.java   
拦截器HttpInterceptor extends HandlerInterceptorAdapter：  
接口请求之前处理preHandle，从RequestHolder中获取当前线程ID进行自己的业务处理，return true继续进行流程(如果return false，就拦截，后续流程无法处理)。  
接口正常或异常请求之后处理afterCompletion，将线程ID从RequestHolder中移除。  
拦截器HandlerInterceptorAdapter:依赖于web框架，在SpringMVC中就是依赖于SpringMVC框架。
在实现上基于Java的反射机制，属于面向切面编程（AOP）的一种运用。  
由于拦截器是基于web框架的调用，因此可以使用Spring的依赖注入（DI）进行一些业务操作，
同时一个拦截器实例在一个controller生命周期之内可以多次调用。  
但缺点是只能对controller请求进行拦截，对其他的一些比如直接访问静态资源的请求则没办法进行拦截处理。  

- ThreadLocalController.java  
简单写个controller做测试。使用postman向/threadlocal/test地址发起get请求，结果是：  
1.请求流程最前端，HttpFilter中的doFilter，使用Thread.currentThread().getId()获取处理本次请求的线程ID，add到自定义的ThreadLocal容器RequestHolder中。  
2.接下来，在请求逻辑处理前，HttpInterceptor中的preHandle，从RequestHolder中获取当前线程ID进行自己的业务处理，return true继续进行流程。  
3.在请求逻辑处理后，无论正常或异常，HttpInterceptor中的afterCompletion，将线程ID从RequestHolder中移除。  

#   test  
使用CountDownLatch模拟请求总数，使用Semaphore模拟允许同时并发执行的线程数，
semaphore.acquire()和semaphore.release()包裹起来的方法代码，是模拟并发操作测试对象。
看测试对象是否线程安全。  

##  String   
- StringBuilderTestN.java， StringBufferTestY.java  
StringBuilder是线程不安全的，但其性能高，单线程或局部变量场景优先使用。  
StringBuffer是线程安全的，其内部实现使用了synchronized，性能低，多线程场景使用。      

##  Time   
- SimpleDateFormatTestN.java， JodaTimeTestY.java  
SimpleDateFormat是线程不安全的，如果以全局静态变量给多线程使用，运行时会抛出大量异常；
基于线程封闭方法，将SimpleDateFormat改为局部变量(每个线程new自己的局部变量SimpleDateFormat)使用可规避该问题。   
第三方JodaTime，JAVA8的DateTime是线程安全的，推荐使用。      

##  List   
- ArrayListTestN.java       
ArrayList不是线程安全的。  

- VectorTestY.java，VectorTestN.java，VectorLoopTestN.java   
同步容器Vector的单个方法(add/remove/get)是线程安全的，其每个方法内部实现使用了synchronized，多线程场景可使用。  
但Vector多个方法同时使用时，无法保证线程安全。例如两个线程一个remove，一个get--VectorTestN.java测试了这种情况。  
同步容器遍历读取和增删改操作需要独立开来运行，例如遍历读取时只是给要操作的数据打标记，遍历读取完成后再单独操作，因为同步容器只能保证单独的增删改操作是线程安全的。  
如果一定想要遍历读取时做增删改操作，推荐使用普通的for循环来做，此时如果使用foreach或iterator操作，会出现ConcurrentModificationException。  

- CollectionsSynchronizedListTestY.java    
同步容器CollectionsSynchronizedList的单个方法是线程安全的。    

- CopyOnWriteArrayListTestY.java   
并发容器CopyOnWriteArrayList可以保证线程安全，其先拷贝一份修改，修改成功后将引用指向新ArrayList--读写分离思想。
但有2个缺点：    
1.如果ArrayList较大，拷贝会占用内存，有可能引起GC、OOM。  
2.不支持实时读，只是保证数据的最终一致性，有可能读取到修改之前的脏数据。  
CopyOnWriteArrayList适合读多写少，List不大的场景。  

##  Set   
- HashSetTestN.java     
HashSet不是线程安全的。   

- CollectionsSynchronizedSetTestY.java    
同步容器CollectionsSynchronizedSet的单个方法是线程安全的。  

- CopyOnWriteArraySetTestY.java  
并发容器CopyOnWriteArraySet是线程安全的，使用参见上面对CopyOnWriteArrayList的描述。  

- ConcurrentSkipListSetTestY.java  
并发容器ConcurrentSkipListSet单次操作是线程安全的，批量操作不是线程安全的需要加锁，不支持null，对应线程不安全的TreeSet。   

##  Map   
- HashMapTestN.java   
HashMap是线程不安全的。  

- HashTableTestY.java    
同步容器HashTable是线程安全的(注意：HashTable的key，value不能为null)，其内部实现了Map接口，使用了synchronized，性能低，多线程场景使用。 

- CollectionsSynchronizedHashMapTestY.java   
同步容器CollectionsSynchronizedHashMap的单个方法是线程安全的。  

- ConcurrentHashMapTestY.java   
并发容器ConcurrentHashMap是线程安全的，不支持null，性能极佳，需要仔细研究。  

- ConcurrentSkipListMapTestY.java   
并发容器ConcurrentSkipListMap也是线程安全的，不支持null，对应线程不安全的TreeMap，性能比ConcurrentHashMap差(约是1/4)，但其有优点：  
1.有序排列；2.支持更高的并发，存取时间与线程数多少无关。   

## AQS   
AbstractQueuedSynchronizer，并发容器中的同步器，提供了先进先出的队列，可以构建锁或其他同步装置的基础框架。  
底层使用了一个Sync queue双向链表，是队列的一种实现，head节点主要用于后续的调度。  
内部维护了一个队列来管理锁，线程会首先尝试获取锁；如果失败，就将当前线程等待状态等信息包成一个node节点，加入到AQS的同步队列Sync queue中，线程不断循环尝试获取锁。  
前提是当前head不断后进才会尝试。如果失败，阻塞自己，直到自己再被唤醒；持有锁的线程释放锁的时候，会唤醒队列中的后进线程。  

- SemaphoreTestY.java，SemaphoreTryTestY.java   
使用场景：控制并发量。  
并发量threadTotal：final Semaphore semaphore = new Semaphore(threadTotal);  
获得N个许可：semaphore.acquire(N);  
释放N个许可，这些许可都做完再做其他的：semaphore.release(N);  
尝试在总共5秒内拿到2个许可就做，拿不到不做：if(semaphore.tryAcquire(2,5000, TimeUnit.MILLISECONDS));    
默认不保证线程能够公平地可从信号量中获得许可，Semaphore semaphore = new Semaphore(1, true);强制公平会影响到并发性能，不推荐使用。  

- CountDownLatchTestY.java  
CountDownLatch用于一个或多个线程等待一系列指定操作的完成。  
并发原子计数器--不能被重置。  
总共执行REQ_TOTAL次：final CountDownLatch countDownLatch = new CountDownLatch(REQ_TOTAL);  
多个线程等待，直到某个线程执行完其业务逻辑，让计数器减1，触发多个线程争抢CountDownLatch，只会有1个线程抢到：countDownLatch.countDown();  
最终，等countDownLatch一直减为0再执行await后面的逻辑处理：countDownLatch.await();   
可以设置等待时间，例如执行任务总共只给10毫秒完成，超过时间不等待：countDownLatch.await(10, TimeUnit.MILLISECONDS);   

- CyclicBarrierTestY.java   
CyclicBarrier是一个所有线程必须等待的一个栅栏，直到所有线程都到达，然后所有线程才可以继续做其他事情。
CyclicBarrier是一个Runnable实例，可以在CyclicBarrier的构造方法中将Runnable栅栏行动传给它。  
CyclicBarrier barrier = new CyclicBarrier(2, barrierAction);    
Runnable barrierAction = ... ;   
调用 CyclicBarrier 对象的 await() 方法，多个线程互相等待，都OK后，执行barrierAction。   
CyclicBarrier和CountDownLatch的区别：    
(1)CyclicBarrier的某个线程运行到某个点上之后，该线程即停止运行，直到所有的线程都到达了这个点，所有线程才重新运行；
CountDownLatch则不是，某线程运行到某个点上之后，只是给某个数值-1而已，该线程继续运行。  
(2)CyclicBarrier只能唤起一个任务，CountDownLatch可以唤起多个任务。  
(3)CyclicBarrier可重用，CountDownLatch不可重用，计数值为0该CountDownLatch就不可再用了。  

## Lock  
Synchronized是JVM实现的不可中断锁，JVM自动释放所以不会死锁。  
ReentrantLock是JDK实现的可中断锁， 需要手工声明加锁，务必记得要在finally中unlock释放锁，避免死锁。 
Synchronized经过优化使用偏向锁后性能与ReentrantLock已经差不多，官方推荐Synchronized；竞争线程少的场景，可考虑Synchronized。  
功能方面，Synchronized使用更加便利。  
ReentrantLock细粒度，是一种自旋锁(循环调用CAS，在用户态就解决问题，避免线程进入内核态的阻塞状态)。  
使用ReentrantLock的场景(3个独有功能，其他情况可考虑使用Synchronized)：
1.ReentrantLock可指定使用公平锁还是非公平锁，Synchronized只能支持非公平锁。  
2.ReentrantLock可以分组唤醒指定线程；Synchronized只能随机唤醒一个线程或者唤醒全部线程。   
3.ReentrantLock提供了能中断等待锁的线程的方法：lock.lockInterruptibly() --当前线程未中断，执行获取锁定尝试；当前线程已被中断，抛出异常。  

- SynchronizedTest.java：    
semaphore.acquire()和semaphore.release()包裹起来的add()方法代码模拟并发量，对public static int变量自增操作。
add()方法增加同步锁synchronized修饰，则线程安全，否则线程不安全。   

- SynchronizedActionScope.java
研究了下Synchronized作用域的区别，写了4段代码：  
1.Synchronized修饰代码块，只作用于调用它的当前对象。所以多线程同时各自安全执行该代码块，且互不影响。    
2.Synchronized修饰方法，只作用于调用它的当前对象。所以多线程同时各自安全执行该方法，且互不影响。  
PS：父类方法的Synchronized是不继承给子类的，因为Synchronized不属于方法声明的一部分。  
3.Synchronized修饰静态方法，作用于调用它的所有对象。所以多线程中的一个线程先安全执行完该方法，另外一个线程再安全执行该方法。  
4.Synchronized修饰类，作用于调用它的所有对象。所以多线程中的一个线程先安全执行完该类实例化的对象，另外一个线程再安全执行该类实例化的对象。    

- ReentrantLockTestY.java    
直接加锁ReentrantLock.lock();如果使用tryLock()或tryLock(long,TimeUnit)表示如果没锁定才加锁。  
然后try业务逻辑；最后务必记得要在finally中释放锁unlock，避免死锁--finally{ReentrantLock.unlock();}  

- ReentrantReadWriteLockTry.java   
读取操作前加readLock，写操作前加writeLock。最后务必记得要在finally中释放锁unlock，避免死锁。     
如果没有读或者写lock时，写操作才能获取锁，进行写操作。       
是一种悲观锁，如果是读多写少场景，写会很难抢到，写饥饿。使用场景不多。  

- StampedLockDemo.java    
乐观锁，读源码发现有个例子，拿出来进行了逐行仔细研究：  
1.写操作：  
写操作前加写锁时，生成新的数据版本号stamp，long stamp = sl.writeLock();      
写操作结束后，务必要释放对应stamp的写锁，finally {sl.unlockWrite(stamp);}    
2.读操作：   
首先尝试获取读乐观锁，long stamp = sl.tryOptimisticRead();   
检查读乐观锁获取的stamp--是否其他线程已加写操作锁，if(!sl.validate(stamp)){}    
如果没有，接下来加一个读悲观锁防其他线程写操作，stamp = sl.readLock();     
读操作结束后，务必要解对应stamp的读锁，finally {sl.unlockRead(stamp);}                  
3.更新操作：    
直接获取读悲观锁，stamp = sl.readLock();    
尝试将读锁转换为写锁，long ws = sl.tryConvertToWriteLock(stamp);     
判断转换为写锁是否成功，如果成功，锁的stamp替换为写锁的stamp，if(ws != 0L){stamp = ws;}     
如果转换为写锁失败，显式释放读锁，显式尝试加上更加严格的写锁，else {sl.unlockRead(stamp); stamp = sl.writeLock();}    
最终务必要释放所有的读锁和写锁，finally {sl.unlock(stamp);}    

-  StampedLockTestY.java
原理和源码样例学习完，实践一下StampedLock看看，线程安全，适用于读多写少场景，乐观锁在此场景下性能很高。    

- ReentrantLockCondition.java  
Condition是个线程间通信的工具类，可实现线程间任务切换，从ReentrantLock实例中可取出Condition：Condition condition = reentrantLock.newCondition();  
线程1：reentrantLock.lock()后， 执行condition.await()，会从AQS等待队列中移除(释放锁)，并加入Condition的等待队列中，等待其他线程发出signal&&释放锁后再被唤醒。     
线程2：线程2reentrantLock.lock()获取到锁，并执行完业务处理，再执行condition.signalAll()，
通知Condition等待队列中的所有线程。此时，线程1被从Condition等待队列中移除，放入AQS等待队列，但此时线程1未被唤醒。
线程2释放锁reentrantLock.unlock()，线程1在AQS中被唤醒，开始业务处理，最后线程1释放锁线程1reentrantLock.unlock()释放锁。  

## CallableFuture
Callable接口类似Runnable接口，Callable接口可以获取线程返回结果，功能更强大。  
Future接口可以获得其他线程Callable的返回值，阻塞等待获得。  
FutureTask类的父类是RunnableFuture， RunnableFuture继承了Runnable和Callable这2个接口。  
使用场景，有一个复杂逻辑需要计算并返回值，但不是马上需要--异步获取返回值。线程A使用Callable复杂逻辑计算，
线程B忙其他事情，等线程B想要结果时，使用future阻塞等待get线程A的Callable结果。  

- FutureTry.java    
线程A实现Callable接口，static class MyCallable implements Callable<String>  
在Callable接口中重写call方法，做复杂业务逻辑处理，public String call() throws Exception   
注意Callable<>的类型要与call()的返回值类型一致，例如都是String。   
main方法主线程线程池submit提交MyCallable任务(submit有返回值,execute无返回值)，并设置Future接口用于届时获得MyCallable线程的返回值，Future<String> future = executorService.submit(new MyCallable());   
main方法需要得到线程A执行结果时，尝试查看并阻塞等待myCallabe执行结果，String result = future.get();  

- FutureTaskTry.java    
FutureTask和Future类似，区别是：定义线程获取结果任务时，FutureTask比使用Future更灵活，不一定是new Callable，也可以new Runable,result。  
FutureTask<String> futureTask = new FutureTask<String>(new Callable<String>(){}    

## ForkJoin    
- ForkJoinTaskTry.java    
Fork/Join类似Map/Reduce，当有重型大任务时，采用分而治之的思想，自行编写task拆分策略，拆分成子任务交给fork执行，
每个子任务执行结果执行join获取，然后汇总。  
使用ForkJoinPool线程池，submit上述task，返回future结果。  
PS：多线程fork执行任务时，其内部使用了双端窃取算法，先完成任务的线程1不闲着，窃取未完成任务的线程2的任务执行。
同时为避免冲突，未完成任务的线程2从队列头部执行任务，完成任务来帮忙的线程1从队列尾部执行任务。  

## ThreadPoolExecutor   
如果线程数<=corePoolSize，直接创建新的线程处理任务，即使线程池中有空闲线程。   
如果 corePoolSize < 线程数 <= maximumPoolSize时，只有当workQueue满时才创建新的线程，如果workQueue不满，此时先放在workQueue中临时等待。  
corePoolSize中的线程如果空闲，不会立即销毁，而是等待keepAliveTime后再销毁。    
workQueue满，且没有空闲的线程时，需要使用rejectHandler。有4种处理策略：  
1.AbortPolicy：默认值，不做新提交的这次任务，抛出异常。   
2.DiscardPolicy：不做新提交的这次任务，直接丢弃无异常抛出。   
3.CallerRunsPolicy：用调用者所在线程执行任务。   
4.DiscardOldestPolicy：丢弃最早的任务(队列中最前面的任务)。   
  
线程池常用方法：  
execute()：提交任务给线程池执行。   
submit()：提交任务给线程池执行，且能返回结果，execute+Future。    
shutdown()：不再接受新任务，将线程池中已有任务都执行完，再关闭线程池，线程池进入shutdown状态。     
shutdownNow()：不再接受新任务，线程池中已有任务不再执行，立即关闭线程池，线程池进入stop状态。 
getTaskCount()：线程池已执行和未执行的总任务总数。  
getCompletedTaskCount()：线程池已完成的任务总数。 
getPoolSize()：线程池中当前的线程数量。  
getActiveCount：线程池正在执行任务的活跃线程数。  
getQueue().size()：线程池队列中等待执行的任务数目。   

实际生产项目中，我们直接使用ThreadPoolExecutor，下面都是ThreadPoolExecutor的封装类，生产上一般不用。     
- NewCachedThreadPoolTry.java  
Executors.newCachedThreadPool创建一个缓存线程池，如果线程池长度超过处理需要，可以灵活回收空闲线程；如果没有可回收的线程，就新建线程。   

- NewFixedThreadPoolTry.java  
Executors.newFixedThreadPool创建一个定长线程池，通过nThreads参数，可以控制线程的最大并发数，超出的线程会在队列中等待。  

- NewSingleThreadExecutorTry.java  
Executors.newSingleThreadExecutor创建单线程线程池，所有任务按指定顺序(先入先出、优先级等)执行。  

- NewScheduledThreadPoolTry.java  
Executors.newScheduledThreadPool创建一个定长线程池，通过corePoolSize参数指定核心线程数，支持定时和周期性任务执行。  
executorService.schedule可以指定延迟多久执行。  
executorService.scheduleAtFixedRate可以指定首次延迟多久后，每隔多久执行一次，类似Timer定时器。  

## DeadLock   
死锁有4个必要条件：  
1.环路等待条件；      
2.请求和保持条件：线程已占用某个资源(保持)，又请求占用其他资源；     
3.互斥条件：排他性，在某个时间，某个资源只能被一个线程使用，其他请求线程只能等待；  
4.不剥夺条件：该资源只能占用资源的线程自己释放，其他线程释放不了。   
  
- DeadLockDemo.java   
写个简单的死锁体会下4个必要条件，实战中务必避免出现死锁：    
通过设置flag，让thread0先执行，锁定o1,sleep 1秒。  
通过设置flag，让thread1后执行，锁定o2，sleep 1秒。  
thread0 sleep结束后，需要再锁定o2才能继续执行，但此时o2已经被thread1锁定，thread0获取不到，于是thread0等待。  
thread1 sleep结束后，需要再锁定o1才能继续执行，但此时o1已经被thread0锁定，thread1获取不到，于是thread1等待。  
于是thread0和thread1互相等待，都需要得到对方资源才能继续运行，从而死锁。  
解决死锁方案：  
1.锁定资源按同一顺序，例如加锁的顺序统一为先o1再o2。  
2.设置加锁超时时间，超过时间释放锁;例如不使用synchronized，用ReentrantLock.tryLock(long timeout, TimeUnit unit)。  
3.编写死锁检测代码，存储所有线程加锁记录，然后分析。    
4.如果发现死锁，释放所有锁，设置随机线程优先级，再次重试。  
PS：并发的地方留高级别日志，便于排查问题。  



