package loader;

import java.io.*;

public class Test
{
    public static void main(String[] argv) throws Exception
    {
//        for (;;) {
//            ClassLoader loader= new DynamicClassOverloader(new String[] {"ClassesHolder"});
//            // текущий каталог "." будет единственным каталогом поиска
//            Class clazz= Class.forName("TestModule2",true,loader);
//            Object object= clazz.newInstance();
//            System.out.println(object);
//            new BufferedReader(new InputStreamReader(System.in)).readLine();
//        }
        Class clazz;
        ClassLoader loader= new DynamicClassOverloader(new String[] {"ClassesHolder"});
//        clazz = Class.forName("TestModule2",true,loader);
        clazz = loader.loadClass("TestModule2");
        Object object= clazz.newInstance();
        System.out.println(object);
        new BufferedReader(new InputStreamReader(System.in)).readLine();
//        ClassLoader loader2= new DynamicClassOverloader(new String[] {"ClassesHolder"});
        clazz = Class.forName("TestModule2",true,loader);
//        clazz = loader.loadClass("TestModule2");
        object = clazz.newInstance();
        System.out.println(object);

    }



}
