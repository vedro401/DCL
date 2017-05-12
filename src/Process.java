import loader.DynamicClassOverloader;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by someone on 04.05.17.
 */
public class Process {
    ArrayList mainArrayList = new ArrayList();
    SmartCaster smartCaster = new SmartCaster();
    public void start() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, InstantiationException {
        String [] path = new String[] {"ClassesHolder/pocket1"};
        ClassLoader loader = new DynamicClassOverloader(path);
        Class clazz = null;

        clazz = loader.loadClass("TestModule");
        fillArrayByTestValue(clazz);
        showAll();

        try {
            try {
                reloadClass("ClassesHolder/pocket2","TestModule","/home/someone/IdeaProjects/DCL/src/Descriptions/FieldDescription.txt",
                        "/home/someone/IdeaProjects/DCL/src/Descriptions/ConverterDependency.txt",
                        "/home/someone/IdeaProjects/DCL/ClassesHolder/Converters", new String[]{"SpecialConverter"});
            } catch (WrongFormatException e) {
                e.printStackTrace();
            }
            System.out.println("===========================\nClass TestModule reloaded\n===========================");
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        showAll();

    }

    public void reloadClass(String classPath, String className,
                            String fieldsDescriptionPath, String fieldConverterDependencyPath,
                            String converterModulesPath, String[] converterModulesNames) throws IllegalAccessException, InstantiationException, ClassNotFoundException, WrongFormatException, NoSuchFieldException {
        prepareCaster(converterModulesPath,converterModulesNames);
        HashMap fieldsDescriptionMap = getFieldsDescriptionMap(fieldsDescriptionPath);
        HashMap fieldConverterDependencyMap = getFieldConverterDependencyMap(fieldConverterDependencyPath);
        reloadClass(classPath,className, fieldsDescriptionMap, fieldConverterDependencyMap);
    }
    public void reloadClass(String classPath, String className,
                            String fieldsDescriptionPath, String fieldConverterDependencyPath) throws WrongFormatException, ClassNotFoundException, NoSuchFieldException, InstantiationException, IllegalAccessException {
        HashMap fieldsDescriptionMap = getFieldsDescriptionMap(fieldsDescriptionPath);
        HashMap fieldConverterDependencyMap = getFieldConverterDependencyMap(fieldConverterDependencyPath);
        reloadClass(classPath,className, fieldsDescriptionMap, fieldConverterDependencyMap);
    }

    private void reloadClass(String path, String className,
                             HashMap<String,Pair<String,String>> fieldsDescriptionMap,
                             HashMap<String,String> fieldConverterDependencyMap) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        HashMap<String,Pair<Field,Field>> describedFieldsMap = new HashMap();
        ClassLoader loader = new DynamicClassOverloader(new String[]{path});
        Class clazz = loader.loadClass(className);
        Class oldClazz;
        Field[] oldClazzFilds = new Field[0];
        for(Object o: mainArrayList){
            if(o.getClass().getName().equals(clazz.getName())){
                oldClazz = o.getClass();
                oldClazzFilds = oldClazz.getDeclaredFields();

                for(Map.Entry<String,Pair<String,String>> entry: fieldsDescriptionMap.entrySet()){
                    describedFieldsMap.put(entry.getKey(), new Pair(
                            oldClazz.getDeclaredField(entry.getValue().first),
                                    clazz.getDeclaredField(entry.getValue().second)));
                    describedFieldsMap.get(entry.getKey()).first.setAccessible(true);
                    describedFieldsMap.get(entry.getKey()).second.setAccessible(true);
                }

                break;
            }
        }
        if(oldClazzFilds.length==0){
            return;
        }
        Field[] clazzFields = clazz.getDeclaredFields();
        HashMap<Field,Field> fieldPair = new HashMap();
        for (int i = 0; i < clazzFields.length ; i++) {
            for (int j = 0; j < oldClazzFilds.length; j++) {
                if(clazzFields[i].getName().equals(oldClazzFilds[j].getName())){
                    fieldPair.put(oldClazzFilds[j],clazzFields[i]);
                    break;
                }
            }
        }

        for (int j = 0; j < mainArrayList.size() ; j++) {
            Object o = mainArrayList.get(j);
            try {
            if(o.getClass().getName().equals(clazz.getName())) {
                Object newO = clazz.newInstance();
                for (Map.Entry<Field,Field> pair: fieldPair.entrySet()) {
                    pair.getKey().setAccessible(true);
                    pair.getValue().setAccessible(true);
                    Object newValue = smartCaster.transform(pair.getKey().get(o),pair.getValue().getType());
                    pair.getValue().set(newO,newValue);
                }

              for(Map.Entry<String,Pair<Field,Field>> entry: describedFieldsMap.entrySet()){
                  Object newValue;
                    if(fieldConverterDependencyMap.containsKey(entry.getKey())){
                        newValue = smartCaster.transformBySpecialConverter(entry.getValue().first.get(o),
                                entry.getValue().second.getType(),fieldConverterDependencyMap.get(entry.getKey()));
                        entry.getValue().second.set(newO,newValue);
                    } else {
                        newValue = smartCaster.transform(entry.getValue().first.get(o),
                                entry.getValue().second.getType());
                        entry.getValue().second.set(newO,newValue);
                    }
              }
                mainArrayList.remove(j);
                mainArrayList.add(j,newO);
            }
            } catch (SmartCaster.TransformFailedException e) {
                e.printStackTrace();
            }
        }
    }

    private HashMap<String,String>getFieldConverterDependencyMap(String fieldConverterDependencyPath) throws WrongFormatException {
        HashMap fieldConverterDependencyMap = new HashMap<String,String>();
        String[] lines = MyFileReader.read(fieldConverterDependencyPath);
        for(String line: lines){
            String[] params = line.trim().split(" ");
            if(params.length == 2){
                fieldConverterDependencyMap.put(params[0],params[1]);
            } else {
                throw new WrongFormatException("Wrong converter dependency file format");
            }
        }
        return fieldConverterDependencyMap;
    }

    private HashMap<String,Pair<String,String>> getFieldsDescriptionMap(String fieldsDescriptionPath) throws WrongFormatException {
        HashMap fieldsDescriptionMap = new HashMap<String,Pair<String,String>>();
        String[] lines = MyFileReader.read(fieldsDescriptionPath);
        for(String line: lines){
            String[] params = line.trim().split(" ");
            if(params.length == 3){
                fieldsDescriptionMap.put(params[0],new Pair(params[1],params[2]));
            } else {
                throw new WrongFormatException("Wrong fields description file format");
            }
        }
        return fieldsDescriptionMap;
    }

    private void prepareCaster(String converterModulesPath, String[] converterModulesNames) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        ClassLoader loader = new DynamicClassOverloader(new String[]{converterModulesPath});
        for(String name: converterModulesNames){
            ConverterModule module = (ConverterModule) loader.loadClass(name).newInstance();
            smartCaster.addConverterModule(module);
        }
    }

    private void showAll() throws IllegalAccessException {
        for (Object o: mainArrayList){
            System.out.println("------------------------");
            System.out.println(o.getClass());
//            System.out.println(o.getClass().getClassLoader());
            Field[] fields = o.getClass().getDeclaredFields();
            for(Field f: fields){
                f.setAccessible(true);
                System.out.print("name: " + f.getName() +" type: " + f.getType().getName() + " value: " );
                if(f.getType().equals(byte[].class)){
                    for(byte b: (byte[])f.get(o)){
                        System.out.print(b + " ");
                    }
                    System.out.println();
                } else if(f.getType().equals(String[].class)){
                    for(String  s: (String [])f.get(o)){
                        System.out.print(s + " ");
                    }
                    System.out.println();
                } else
                    System.out.println(f.get(o).toString());
            }
        }
    }

    private void fillArrayByTestValue(Class clazz) throws IllegalAccessException, InstantiationException, NoSuchFieldException {
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

    private class Pair<F,S>{
        public Pair(F oldField, S newField) {
            this.first = oldField;
            this.second = newField;
        }
        F first;
        S second;
    }

     public class WrongFormatException extends Exception{
        public WrongFormatException(String message) {
            super(message);
        }
     }

}
