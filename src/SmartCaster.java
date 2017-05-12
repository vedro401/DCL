import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by someone on 04.05.17.
 */
public class SmartCaster{
    private HashMap<String, ConverterModule> modules = new HashMap<>();

    public Object transformBySpecialConverter(Object inputDate, Class resultType, String converterName) throws TransformFailedException {
        for(Map.Entry<String,ConverterModule> entry: modules.entrySet()){
            if(entry.getValue().getClass().getName().equals(converterName)){
                return entry.getValue().transform(inputDate);
            }
        }
        throw new TransformFailedException("No such converter \"" + converterName + "\"");
    }

  public Object transform(Object inputDate, Class resultType) throws TransformFailedException {

      String requiredSpecialization = inputDate.getClass().getName() + resultType.getName();

      if(modules.containsKey(requiredSpecialization))
        return modules.get(requiredSpecialization).transform(inputDate);

      Class oc = inputDate.getClass();

//      if(oc.equals(resultType)){
//        return inputDate;
//      }
      if(oc.equals(String.class)){
          //cast to chars
          //cat to something else
            if(resultType.equals(byte[].class)){
                return inputDate.toString().getBytes();
            }
          if(resultType.equals(Integer.class)){;
                return inputDate.toString().hashCode();
          }
      }
      if (oc.equals(Integer.class)){
          if(resultType.equals(Integer.class)) {
              return inputDate;
          }
          if(resultType.equals(byte[].class)){
              byte[] bb = ((Integer)inputDate).toString().getBytes();
              return bb;
          }
      }
      if(oc.equals(ArrayList.class)){
          ArrayList oal = ((ArrayList)inputDate);

          if(resultType.equals(String[].class)){
              String[] sa = (String[]) oal.toArray(new String[oal.size()]);
              return sa;
          }
      }

      throw new TransformFailedException("No converter which can transform " + inputDate.getClass().getName() + " to " +
      resultType.getName());
  }

  public void addConverterModule(ConverterModule module){
      Method cmTransform = module.getClass().getMethods()[0];
      modules.put(cmTransform.getParameterTypes()[0].getName() + cmTransform.getReturnType().getName(),module);
  }

    public class TransformFailedException extends Exception{
        public TransformFailedException(String message) {
            super(message);
        }
    }
}
