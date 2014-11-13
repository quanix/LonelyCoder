package org.lonelycoder.core.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

@SuppressWarnings("restriction")
public final class UnsafeUtils {
    static sun.misc.Unsafe unsafe;
    public static long stringoffset;
    static {
        try {
            Class<?> clazz = Class.forName("sun.misc.Unsafe");
            Field field = clazz.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
            java.lang.reflect.Field stringValue=String.class.getDeclaredField("value");
            stringoffset=unsafe.objectFieldOffset(stringValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static boolean enable=System.getProperty("disable.unsafe")==null;

    /**
     * 获取Unsafe对象
     * @return
     */
    public final static sun.misc.Unsafe getUnsafe(){
        return unsafe;
    }

    /**
     * 不使用反射直接创造对象，注意类的构造方法不会被执行
     * @param clz
     * @return
     */
    @SuppressWarnings("unchecked")
    public final static <T> T newInstance(Class<T> clz){
        try {
            return (T) unsafe.allocateInstance(clz);
        } catch (InstantiationException e) {
            throw new IllegalStateException(e.getCause());
        }
    }


    /**
     * 用指定的ClassLoader加载二进制数据为class
     * @param className
     * @param data
     * @param i
     * @param length
     * @param classLoader
     * @return
     */
    public final static Class<?> defineClass(String className, byte[] data, int i, int length, ClassLoader classLoader) {
        if(data==null || data.length==0){
            throw new IllegalArgumentException("the input class data is empty!");
        }
        if(length<1 || i+length<data.length){
            throw new IllegalArgumentException("the input length is invalid!");
        }
        if(className==null || className.length()==0){
            throw new IllegalArgumentException("the class name is invalid!"+className);
        }
        Assert.notNull(classLoader);
//		LogUtil.debug("Unsafe load class ["+className+"] to "+classLoader);
        return unsafe.defineClass(className, data, i, length, classLoader, null);
    }

    /**
     * 返回String对象中的char[]数组。
     * 修改String 中的char[]是十分危险的，尤其是当被修改的char[]是属于常量池的String时，会发生十分难以检测的，不可预期的问题。
     * 因此这个方法用来：
     * 1、获取String中的char[]，用于读取和遍历。
     * 2、只有当非常确信String所引用的char[]不在常量池中时，可以进行修改。如果一个常量string进行substring操作，会产生的一个不完全对象的String。
     * 这个string的char[]引用上一个string的char[]，因此只有确信string对象是通过拼接等方式重新生成时，才能使用此方法
     *
     *
     * 这个方法的意义所在：
     * string.toCharArray();方法和这个很相似，但是性能上要稍微差一点。这个差别非常小。
     * 以长度为8的string来测试，运行一万次，toCharArray耗时3000us(当string比较大时可能更慢)，调用这个方法耗时约1000us(由于不用创建新对象)。
     *
     * 实际测试发现，通过toCharArray来遍历对象要比下面的方法更耗时。
     * 	for(int j=0;j<s.length();j++){
     *		char c=s.charAt(j);
     *	}
     * 而这种方式和unsafe的方式其实耗时差距不大。
     *
     * @param str
     * @return
     */
    public final static char[] getValue(String str){
        return (char[])unsafe.getObject(str, stringoffset);
    }
}
