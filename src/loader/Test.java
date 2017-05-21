package loader;

import java.io.*;
import java.lang.reflect.Field;

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

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String [] path = new String[] {"ClassesHolder/pocket"};
        String addPath;
        Class clazz = String.class;
        Class clazz2;
//        ClassLoader loader;
//        clazz = Class.forName("TestModule2",true,loader);
        while (true) {
            clazz2 = clazz;
            addPath = br.readLine();
            path[0] ="ClassesHolder/pocket" + addPath;
            ClassLoader loader = new DynamicClassOverloader(path);
            clazz = loader.loadClass("res.TestModule");
            Object object = clazz.newInstance();
            System.out.println(path[0]);

            System.out.println(object);
            System.out.println(object.getClass());
            System.out.println(clazz.getName().equals(clazz2.getName()));

            Field[] publicFields = clazz.getDeclaredFields();
            for (Field field : publicFields) {
                Class fieldType = field.getType();
                System.out.println("Имя: " + field.getName());
                System.out.println("Тип: " + fieldType.getName());
            }
        }

//        new BufferedReader(new InputStreamReader(System.in)).readLine();
//        ClassLoader loader2= new DynamicClassOverloader(new String[] {"ClassesHolder"});
//        clazz = Class.forName("TestClass",true,loader);
////        clazz = loader.loadClass("TestModule2");
//        object = clazz.newInstance();
//        System.out.println(object);



    }



}
