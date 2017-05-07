import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by someone on 04.05.17.
 */
public class SmartCaster{
  public static Object transform(Object o, Class c){

      Class oc = o.getClass();

      if(oc.equals(c)){
          //TODO ArrayList protection
        return o;
      }
      if(oc.equals(String.class)){
          //cast to chars
          //cat to something else
            if(c.equals(byte[].class)){
                return o.toString().getBytes();
            }
      }
      if (oc.equals(int.class)){
          if(c.equals(Integer.class)) {
              return o;
          }
      }
      if(oc.equals(ArrayList.class)){
          ArrayList oal = ((ArrayList)o);

          if(c.equals(String[].class)){
              String[] sa = (String[]) oal.toArray(new String[oal.size()]);
              return sa;
          }
      }
    return null;
  }
}
