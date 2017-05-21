import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by someone on 18.05.17.
 */
public class GeneratedConverter implements ConverterModule {

    HashMap<String, Pair<Field, Field>> describedFieldsMap;
    HashMap<String,Pair<String,String>> fieldsDescriptionMap;
    HashMap<Field,Field> fieldPairs;

    HashMap<String,HashMap<String,Pair<String,String>>> fieldsDescriptionMaps;
    HashMap<String,String> fieldConverterDependencyMap;
    Class resultClass;
    Class inputClass;
    SmartCaster smartCaster;

    public GeneratedConverter(HashMap<String,HashMap<String,Pair<String,String>>> fieldsDescriptionMaps,
                              HashMap<String, String> fieldConverterDependencyMap,
                              Class iClass,
                              Class rClass,
                              SmartCaster smartCaster) throws NoSuchFieldException {
        this.fieldsDescriptionMaps = fieldsDescriptionMaps;
        this.fieldConverterDependencyMap = fieldConverterDependencyMap;
        this.resultClass = rClass;
        this.inputClass = iClass;
        this.fieldsDescriptionMap = fieldsDescriptionMaps.get(iClass.getName());
        this.smartCaster = smartCaster;
        init();

    }

    @Override
    public Object transform(Object i)  {
        Object newO = null;
        try {
            newO = resultClass.newInstance();
        } catch (InstantiationException e) {
            if(!resultClass.isPrimitive())
            try {
                Constructor c = resultClass.getDeclaredConstructors()[0];
                c.setAccessible(true);
                newO = c.newInstance(smartCaster.customer);
            } catch (InstantiationException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (InvocationTargetException e1) {
                e1.printStackTrace();
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        smartCaster.customer = newO;
        for(Map.Entry<Field,Field>  entry: fieldPairs.entrySet()){
            try {
                Object newValue = smartCaster.transform(entry.getKey().get(i), entry.getValue().getType());
                Class classs = newO.getClass();
                entry.getValue().set(newO, newValue);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (SmartCaster.TransformFailedException e) {
                e.printStackTrace();
            }
        }


        return newO;
    }

    private void init() throws NoSuchFieldException {
        Field oldField;
        Field newField;
        Pattern p = Pattern.compile("this\\$\\d+");
        if(fieldsDescriptionMap != null) {
            for (Map.Entry<String, Pair<String, String>> entry : fieldsDescriptionMap.entrySet()) {
                oldField = inputClass.getDeclaredField(entry.getValue().first);
                oldField.setAccessible(true);
                newField = inputClass.getDeclaredField(entry.getValue().second);
                newField.setAccessible(true);
                describedFieldsMap.put(entry.getKey(), new Pair<Field, Field>(oldField, newField));
                if (!smartCaster.canYouTransformIt(oldField, newField) &&
                        fieldConverterDependencyMap.get(entry.getKey()) == null) {
                    GeneratedConverter generatedConverter =
                            new GeneratedConverter(fieldsDescriptionMaps,fieldConverterDependencyMap,
                                    oldField.getType(),newField.getType(),smartCaster);
                    smartCaster.addConverterModule(generatedConverter,
                            oldField.getType().getName() + newField.getType().getName());
                }
            }
        }

        Field[] clazzFields = resultClass.getDeclaredFields();
        Field[] oldClazzFields = inputClass.getDeclaredFields();
        fieldPairs = new HashMap();
        for (int i = 0; i < clazzFields.length ; i++) {
            if(p.matcher(clazzFields[i].getName()).matches()){
                break;
            }
            for (int j = 0; j < oldClazzFields.length; j++) {
                if(clazzFields[i].getName().equals(oldClazzFields[j].getName())){
                    oldClazzFields[j].setAccessible(true);
                    clazzFields[i].setAccessible(true);
                    fieldPairs.put(oldClazzFields[j],clazzFields[i]);
                    if (!smartCaster.canYouTransformIt(oldClazzFields[j], clazzFields[i]) ) {
                        GeneratedConverter generatedConverter =
                                new GeneratedConverter(fieldsDescriptionMaps,fieldConverterDependencyMap,
                                        oldClazzFields[j].getType(),clazzFields[i].getType(),smartCaster);
                        smartCaster.addConverterModule(generatedConverter,
                                oldClazzFields[j].getType().getName() + clazzFields[i].getType().getName());
                    }
                    break;
                }
            }
        }
    }
}
