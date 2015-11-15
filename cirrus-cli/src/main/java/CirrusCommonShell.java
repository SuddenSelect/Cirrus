import asg.cliche.InputConverter;
import pl.mmajewski.cirrus.common.model.Host;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Created by Maciej Majewski on 15/11/15.
 */
public class CirrusCommonShell {

    public static final InputConverter[] CLI_INPUT_CONVERTERS = {
            new InputConverter() {
                public Object convertInput(String original, Class toClass)
                        throws Exception {

                    if (toClass.equals(LocalDateTime.class)) {
                        return LocalDateTime.parse(original);

                    } else if(toClass.equals(InetAddress.class)) {
                        return InetAddress.getByName(original);

                    } else if(toClass.equals(Integer.class)) {
                        return Integer.parseInt(original);

                    } else if(toClass.equals(String.class)) {
                        return original;

                    } else {
                        return null;
                    }
                }
            },
    };

    protected String listClassSetters(Class c){

        StringBuilder sb = new StringBuilder(c.getSimpleName());
        sb.append(" : \n");
        for(Method method : c.getMethods()){
            if(method.getName().startsWith("set")){
                sb.append("-> ");
                sb.append(method.getName().substring(3));
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    protected String listCollection(Collection collection){
        StringBuilder sb = new StringBuilder();
        for(Object object : collection){
            sb.append("+ ");
            sb.append(object.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    protected void set(Object object, String property, Class valueClass, String value) throws Exception {
        object.getClass().getMethod("set"+property,valueClass).invoke(object,CLI_INPUT_CONVERTERS[0].convertInput(value,valueClass));
    }

    protected void set(Object object, String property, Class valueClass, Collection value) throws Exception {
        object.getClass().getMethod("set"+property,valueClass).invoke(object,value);
    }
}
