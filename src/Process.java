import loader.DynamicClassOverloader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by someone on 04.05.17.
 */
public class Process {
    ArrayList mainArrayList = new ArrayList();
    public void start() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, InstantiationException {
        String [] path = new String[] {"ClassesHolder/pocket1"};
        ClassLoader loader = new DynamicClassOverloader(path);
        Class clazz = null;

        clazz = loader.loadClass("TestModule");
        fillArrayByFackeValue(clazz);
//        fillArrayByFackeValue();
        showAll();
        for(Object tm: mainArrayList){
            Field f = tm.getClass().getDeclaredField("a1");
            f.setAccessible(true);
            System.out.println(f.get(tm));
        }

        try {
            reloadClass("ClassesHolder/pocket2","TestModule");
            System.out.println("Class TestModule reloaded\n");
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        showAll();

    }

    private void fillArrayByFackeValue(Class clazz) throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        for (int i = 0; i < 3; i++) {
            Object o = clazz.newInstance();
            Field f = o.getClass().getDeclaredField("a1");
            f.setAccessible(true);
            f.set(o,i);
            f = o.getClass().getDeclaredField("a2");
            f.setAccessible(true);
            f.set(o,i + "");
            f = o.getClass().getDeclaredField("a3");
            f.setAccessible(true);
            ArrayList<String> al = new ArrayList<>();
            al.add(i+10 + "");
            al.add(i+20 + "");
            al.add(i+30 + "");
            f.set(o,al);
            mainArrayList.add(o);
        }
    }

    private void fillArrayByFackeValue() throws NoSuchFieldException, IllegalAccessException {
        for (int i = 0; i < 3; i++) {
            TestModule o = new TestModule();
            Field f = o.getClass().getDeclaredField("a1");
            f.set(o,i);
            f = o.getClass().getDeclaredField("a2");
            f.set(o,i + "");
            f = o.getClass().getDeclaredField("a3");
            ArrayList<String> al = new ArrayList<>();
            al.add(i+10 + "");
            al.add(i+20 + "");
            al.add(i+30 + "");
            f.set(o,al);
            mainArrayList.add(o);
        }
    }

    private void reloadClass(String path, String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        ClassLoader loader = new DynamicClassOverloader(new String[]{path});
        Class clazz = loader.loadClass(className);
        Class oldClazz;
        Field[] oldClazzFilds = new Field[0];
        for(Object o: mainArrayList){
            if(o.getClass().getName().equals(clazz.getName())){
                oldClazz = o.getClass();
                oldClazzFilds = oldClazz.getDeclaredFields();
                break;
            }
        }
        
        if(oldClazzFilds.length==0){
            return;
        }
        Field[] clazzFilds = clazz.getDeclaredFields();
        int n = (clazzFilds.length < oldClazzFilds.length) ? clazzFilds.length : oldClazzFilds.length;
        int clazzFildsPairPosition[] = new int[n];
        int oldClazzFildsPairPosition[] = new int[n];
        int z = 0;
        for (int i = 0; i < clazzFilds.length ; i++) {
            for (int j = 0; j < oldClazzFilds.length; j++) {
                if(clazzFilds[i].getName().equals(oldClazzFilds[j].getName())){
                    clazzFildsPairPosition[z] = i;
                    oldClazzFildsPairPosition[z] = j;
                    z++;
                    break;
                }
            }
        }

        for (int j = 0; j < mainArrayList.size() ; j++) {
            Object o = mainArrayList.get(j);
            if(o.getClass().getName().equals(clazz.getName())) {
                Object newO = clazz.newInstance();
                for (int i = 0; i < z; i++) {
                    Field clazzFildPairPosition = clazzFilds[clazzFildsPairPosition[i]];
                    clazzFildPairPosition.setAccessible(true);
                    Field oldClazzFildPairPosition = oldClazzFilds[oldClazzFildsPairPosition[i]];
                    oldClazzFildPairPosition.setAccessible(true);
                    Object newValue = SmartCaster.transform(oldClazzFildPairPosition.get(o),clazzFildPairPosition.getType());
                    clazzFildPairPosition.set(newO,newValue);
                }
                mainArrayList.remove(j);
                mainArrayList.add(j,newO);
            }
        }
    }

    private void showAll() throws IllegalAccessException {
        for (Object o: mainArrayList){
            System.out.println("------------------------");
            System.out.println(o.getClass());
            System.out.println(o.getClass().getClassLoader());
            Field[] filds = o.getClass().getDeclaredFields();
            for(Field f: filds){
                f.setAccessible(true);
                System.out.println("name: " + f.getName() +" type: " + f.getType() + " value: " + f.get(o));
            }

        }


    }



}
