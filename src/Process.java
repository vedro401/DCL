import loader.DynamicClassOverloader;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by someone on 04.05.17.
 */
public class Process {
    ArrayList mainArrayList = new ArrayList();
    SmartCaster smartCaster = new SmartCaster();

    public void startTest() throws IllegalAccessException, InstantiationException, ClassNotFoundException, WrongFormatException, IOException, NoSuchFieldException {
        String [] path = new String[] {"/home/someone/IdeaProjects/DCL/ClassesHolder/pocket1"};

        String testModuleName = "EzTestModule";
//        String testModuleName = "HareModule";

        ClassLoader loader = new DynamicClassOverloader(path);
        Class clazz = loader.loadClass(testModuleName);
//


        Scanner scanner = new Scanner(System.in);
        Scanner scanner2 = new Scanner(System.in);
        int count;
        boolean flag = true;
        System.out.println("Process started");

        while (flag){
            switch (scanner.nextLine()){
                case "more":
                    System.out.println("How much?");
                    count = scanner2.nextInt();
                    for (int i = 0; i < count; i++) {
                        mainArrayList.add(clazz.newInstance());
                    }
                    System.out.println("Now there are " +  mainArrayList.size() + " objects");
                    break;
                case "reload":
                    reloadClass("/home/someone/IdeaProjects/DCL/ClassesHolder/pocket2",
                            testModuleName,
                            "/home/someone/IdeaProjects/DCL/Descriptions/Void.txt",
                            "/home/someone/IdeaProjects/DCL/Descriptions/Void.txt",
                            null, new String[]{"SpecialConverter"});
                    break;
                case "clear":
                    mainArrayList.clear();
                    System.out.println("cleared");
                    break;
                case "exit":
                    flag = false;
                    break;
                case "check":
                    for (int i = 0; i < mainArrayList.size(); i++) {
                        if(mainArrayList.get(i) == null){
                            System.out.println("element " + i + " is null. Bastard!");
                        }
                    }
                    System.out.println("checked");
                    break;
                default:
                    System.out.println("What?");
            }
        }


    }


    public void start(){
        String [] path = new String[] {"/home/someone/IdeaProjects/DCL/ClassesHolder/pocket1"};
        ClassLoader loader = new DynamicClassOverloader(path);
        Class clazz = null;


        try {

            clazz = loader.loadClass("HareModule");
            mainArrayList.add(clazz.newInstance());
//            fillArrayByTestValue(clazz);
            showAll();
            reloadClass("/home/someone/IdeaProjects/DCL/ClassesHolder/pocket2",
                    "HareModule",
                    "/home/someone/IdeaProjects/DCL/Descriptions/Void.txt",
                        "/home/someone/IdeaProjects/DCL/Descriptions/Void.txt",
                        null, new String[]{"SpecialConverter"});
            System.out.println("===========================\nClass "+ clazz.getName() +" reloaded\n===========================");
            showAll();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (WrongFormatException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void reloadClass(String classPath, String className,
                            String fieldsDescriptionPaths, String fieldConverterDependencyPaths,
                            String converterModulesPath, String[] converterModulesNames) throws IllegalAccessException, InstantiationException, ClassNotFoundException, WrongFormatException, NoSuchFieldException, IOException {
        prepareConverterModules(converterModulesPath,converterModulesNames);
        long time = System.currentTimeMillis();
        HashMap<String,HashMap<String,Pair<String,String>>> fieldsDescriptionMaps = getFieldsDescriptionMaps(fieldsDescriptionPaths);


//        HashMap fieldsDescriptionMap = getFieldsDescriptionMaps(fieldsDescriptionPaths);
        HashMap fieldConverterDependencyMap = getFieldConverterDependencyMap(fieldConverterDependencyPaths);
//            reloadClass(classPath,className, fieldsDescriptionMaps, fieldConverterDependencyMap);
        ClassLoader loader = new DynamicClassOverloader(new String[]{classPath});
        Class clazz = loader.loadClass(className);
        Class oldClazz = null;
        for(Object o: mainArrayList) {
            if (o.getClass().getName().equals(clazz.getName())) {
                oldClazz = o.getClass();
                GeneratedConverter generatedConverter =
                        new GeneratedConverter(fieldsDescriptionMaps,fieldConverterDependencyMap,
                                oldClazz,clazz,smartCaster);
                smartCaster.addConverterModule(generatedConverter);
                smartCaster.addConverterModule(generatedConverter, oldClazz.getName() + clazz.getName());
                break;
            }
        }
        if(oldClazz == null){
            return;
        }
        int pc = mainArrayList.size()/100;
        int c = pc;
        long[] forEachTime = new long[mainArrayList.size()];
        for (int i = 0; i < 10; i++) {
            System.out.print(".........!");
        }
        System.out.println();
//        long lastTime = System.currentTimeMillis();
        long clearStartTime = System.currentTimeMillis();
        Class oc;
        Object o;
        for (int j = 0; j < mainArrayList.size() ; j++) {
            o = mainArrayList.get(j);
            oc = o.getClass();
            if(oc.getName().equals(clazz.getName()) && !oc.getClassLoader().equals(clazz.getClassLoader())) {
                try {
                    Object newO = smartCaster.transform(o,clazz);
                    mainArrayList.remove(j);
                    mainArrayList.add(j,newO);
                } catch (SmartCaster.TransformFailedException e) {
                    e.printStackTrace();
                    return;
                }
            }
            forEachTime[j] = System.currentTimeMillis();
            if(j >= c-1){
                System.out.print("|");
//                System.out.println(System.currentTimeMillis() - lastTime);
//                lastTime = System.currentTimeMillis();
                c += pc;
            }
        }
        smartCaster = new SmartCaster();
        System.out.println();
        System.out.println("\nDone in " + (System.currentTimeMillis() - time) + "\n");
        outStatistic(forEachTime,clearStartTime);

    }

    public void reloadClassMultiThread(String classPath, String className,
                            String fieldsDescriptionPaths, String fieldConverterDependencyPaths,
                            String converterModulesPath, String[] converterModulesNames) throws IllegalAccessException, InstantiationException, ClassNotFoundException, WrongFormatException, NoSuchFieldException, IOException {
        prepareConverterModules(converterModulesPath,converterModulesNames);
        long time = System.currentTimeMillis();
        HashMap<String,HashMap<String,Pair<String,String>>> fieldsDescriptionMaps = getFieldsDescriptionMaps(fieldsDescriptionPaths);


//        HashMap fieldsDescriptionMap = getFieldsDescriptionMaps(fieldsDescriptionPaths);
        HashMap fieldConverterDependencyMap = getFieldConverterDependencyMap(fieldConverterDependencyPaths);
//            reloadClass(classPath,className, fieldsDescriptionMaps, fieldConverterDependencyMap);
        ClassLoader loader = new DynamicClassOverloader(new String[]{classPath});
        Class clazz = loader.loadClass(className);
        Class oldClazz = null;
        for(Object o: mainArrayList) {
            if (o.getClass().getName().equals(clazz.getName())) {
                oldClazz = o.getClass();
                GeneratedConverter generatedConverter =
                        new GeneratedConverter(fieldsDescriptionMaps,fieldConverterDependencyMap,
                                oldClazz,clazz,smartCaster);
                smartCaster.addConverterModule(generatedConverter, oldClazz.getName() + clazz.getName());
                break;
            }
        }
        if(oldClazz == null){
            return;
        }

        long[] forEachTime = new long[mainArrayList.size()];
        for (int i = 0; i < 10; i++) {
            System.out.print(".........!");
        }
        System.out.println();
//        long lastTime = System.currentTimeMillis();
        long clearStartTime = System.currentTimeMillis();

        int procNum = Runtime.getRuntime().availableProcessors();

        ExecutingThread[] threads = new ExecutingThread[procNum];
        int h = mainArrayList.size()/procNum;

        for (int j = 0; j < procNum; j++) {
            if(j+1 >= procNum){
                threads[j] = new ExecutingThread(j * h, mainArrayList.size(), clazz, smartCaster,j);
                threads[j].start();
            } else {
                threads[j] = new ExecutingThread(j * h, (j + 1) * h, clazz, smartCaster,j);
                threads[j].start();
            }
        }
        for (int j = 0; j < Runtime.getRuntime().availableProcessors() ; j++) {
            try {
                threads[j].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        smartCaster = new SmartCaster();
        System.out.println();
        System.out.println("\nDone in " + (System.currentTimeMillis() - time) + "\n");
//        outStatistic(forEachTime,clearStartTime);

    }

    private class ExecutingThread extends Thread{
        int start, end;
        Class clazz;
        SmartCaster smartCaster;
        int num;

        public ExecutingThread(int start, int end, Class clazz, SmartCaster smartCaster, int num) {
            this.start = start;
            this.end = end;
            this.clazz = clazz;
            this.num = num;
            try {
                this.smartCaster = (SmartCaster) smartCaster.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run() {
            Class oc;
            Object o;
            int pc = mainArrayList.size()/100;
            int c = start + pc;
            for (int j = start; j < end; j++) {
                o = mainArrayList.get(j);
                oc = o.getClass();
                if(oc.getName().equals(clazz.getName()) && !oc.getClassLoader().equals(clazz.getClassLoader())) {
                    try {
                        Object newO = smartCaster.transform(o,clazz);
//                        mainArrayList.remove(j);
                        mainArrayList.add(j,newO);
                    } catch (SmartCaster.TransformFailedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
//                forEachTime[j] = System.currentTimeMillis();
                if(j >= c-1){
                    System.out.print(num);
////                System.out.println(System.currentTimeMillis() - lastTime);
////                lastTime = System.currentTimeMillis();
                    c += pc;
                }
            }

        }
    }



    private void outStatistic(long[] forEachTime, long clearStartTime){
        int pc = forEachTime.length/100;
        long [] forEachCalcTime = new long[forEachTime.length];
        forEachCalcTime[0] = forEachTime[pc] - clearStartTime;
        for (int i = 1; i < forEachTime.length; i++) {
            forEachCalcTime[i] = forEachTime[i] - forEachTime[i-1];
        }
        System.out.println("clear start time: " + clearStartTime);
        System.out.println("_________________________________________");
        System.out.println(("calculation time: " + (forEachTime[pc] - clearStartTime) ));
        System.out.println("time: " + forEachTime[pc]);
//        System.out.println("Average time for one object: " + (forEachTime[0] - clearStartTime)/forEachTime.length );
        System.out.println("_________________________________________");


        for (int i = 2*pc-1; i < forEachCalcTime.length; i+=pc) {
            System.out.println("calculation time: " + (forEachTime[i] - forEachTime[i-pc]) );
            System.out.println("time: " + forEachTime[i]);
//            System.out.println("Average time for one object: " + (forEachTime[i] - forEachTime[i-pc])/pc);
            System.out.println("_________________________________________");
        }
        long ll = 0;
        for(long l: forEachCalcTime){
            ll += l;
        }
        System.out.println("Average time for 100 object: "  + ll/(forEachCalcTime.length/100));
    }

//    public void reloadClass(String classPath, String className,
//                            String fieldsDescriptionPath, String fieldConverterDependencyPath) throws WrongFormatException, ClassNotFoundException, NoSuchFieldException, InstantiationException, IllegalAccessException, IOException {
//        HashMap fieldsDescriptionMap = getFieldsDescriptionMaps(fieldsDescriptionPath);
//        HashMap fieldConverterDependencyMap = getFieldConverterDependencyMap(fieldConverterDependencyPath);
//        reloadClass(classPath,className, fieldsDescriptionMap, fieldConverterDependencyMap);
//    }

    private void reloadClass(String path, String className,
                             HashMap<String,HashMap<String,Pair<String,String>>> fieldsDescriptionMaps,
                             HashMap<String,String> fieldConverterDependencyMap) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        HashMap<String,Pair<Field,Field>> describedFieldsMap = new HashMap();
        ClassLoader loader = new DynamicClassOverloader(new String[]{path});
        Class clazz = loader.loadClass(className);
        Class oldClazz;
        Field[] oldClazzFields = new Field[0];


        for(Object o: mainArrayList){
            if(o.getClass().getName().equals(clazz.getName())){
                oldClazz = o.getClass();
                oldClazzFields = oldClazz.getDeclaredFields();

                if(fieldsDescriptionMaps.containsKey(clazz.getName()))
                for(Map.Entry<String,Pair<String,String>> entry: fieldsDescriptionMaps.get(clazz.getName()).entrySet()){
                    describedFieldsMap.put(entry.getKey(), new Pair(
                            oldClazz.getDeclaredField(entry.getValue().first),
                                    clazz.getDeclaredField(entry.getValue().second)));
                    describedFieldsMap.get(entry.getKey()).first.setAccessible(true);
                    describedFieldsMap.get(entry.getKey()).second.setAccessible(true);
                }

                break;
            }
        }
        if(oldClazzFields.length==0){
            return;
        }
        Field[] clazzFields = clazz.getDeclaredFields();
        HashMap<Field,Field> fieldPair = new HashMap();
        for (int i = 0; i < clazzFields.length ; i++) {
            for (int j = 0; j < oldClazzFields.length; j++) {
                if(clazzFields[i].getName().equals(oldClazzFields[j].getName())){
                    fieldPair.put(oldClazzFields[j],clazzFields[i]);
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
                                entry.getValue().second.getType(),
                                fieldConverterDependencyMap.get(entry.getKey()));
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

    private HashMap<String,String>getFieldConverterDependencyMap(String fieldConverterDependencyPath) throws WrongFormatException, IOException {
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

    private HashMap<String,HashMap<String,Pair<String,String>>> getFieldsDescriptionMaps(String fieldsDescriptionPath) throws WrongFormatException, IOException {
        HashMap<String,HashMap<String,Pair<String,String>>> fieldsDescriptionMaps = new HashMap();
        String[] lines = MyFileReader.read(fieldsDescriptionPath);
        String key = "";
        for(String line: lines){
            String[] params = line.trim().split(" ");
            if(params.length == 3){
                fieldsDescriptionMaps.get(key).put(params[0],new Pair(params[1],params[2]));
            } else if(params.length == 1) {
                key = params[0];
                fieldsDescriptionMaps.put(key,new HashMap<>());
            } else {
                throw new WrongFormatException("Wrong fields description file format");
            }
        }
        return fieldsDescriptionMaps;
    }

    private void prepareConverterModules(String converterModulesPath, String[] converterModulesNames) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if(converterModulesPath == null) {
            return;
        }
        ClassLoader loader = new DynamicClassOverloader(new String[]{converterModulesPath});
        for(String name: converterModulesNames){
            ConverterModule module = (ConverterModule) loader.loadClass(name).newInstance();
            smartCaster.addConverterModule(module);
        }
    }

    private void showAll() throws IllegalAccessException {
        for (Object o: mainArrayList){
//            System.out.println("------------------------");
//            System.out.println(o.getClass());
////            System.out.println(o.getClass().getClassLoader());
//            Field[] fields = o.getClass().getDeclaredFields();
//            for(Field f: fields){
//                f.setAccessible(true);
//                System.out.print("name: " + f.getName() +" type: " + f.getType().getName() + " value: " );
//                if(f.getType().equals(byte[].class)){
//                    for(byte b: (byte[])f.get(o)){
//                        System.out.print(b + " ");
//                    }
//                    System.out.println();
//                } else if(f.getType().equals(String[].class)){
//                    for(String  s: (String [])f.get(o)){
//                        System.out.print(s + " ");
//                    }
//                    System.out.println();
//                } else
//                    System.out.println(f.get(o).toString());
//            }
        showSomething(o,"_");
        }
    }

    private void showSomething(Object o, String indentation) throws IllegalAccessException {
        Pattern p = Pattern.compile("this\\$\\d+");
        System.out.println(indentation + "__________________________________");
//        System.out.println("Class: " + o.getClass().getName());
//        System.out.println("-------------Fields---------------");
        Field[] fields = o.getClass().getDeclaredFields();
        for(Field f: fields){
            if(!p.matcher(f.getName()).matches()) {
                f.setAccessible(true);
                if (f.getType().isPrimitive() || f.getType().equals(String.class)) {
                    System.out.println(indentation + "name: " + f.getName()
                            + "\n" + indentation + "type: " + f.getType().getName()
                            +  "\n" + indentation +"value: " + f.get(o) +
                            "\n" + indentation + "_____________");
                } else {
                    System.out.println(indentation + "name: " + f.getName()
                            + "\n" + indentation + "type: " + f.getType().getName()
                            + "\n" + indentation + "fields: ");
                    showSomething(f.get(o), indentation + "____");
                }
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





     public class WrongFormatException extends Exception{
        public WrongFormatException(String message) {
            super(message);
        }
     }

}
