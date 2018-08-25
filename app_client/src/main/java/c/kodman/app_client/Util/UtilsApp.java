package c.kodman.app_client.Util;

public class UtilsApp {

    public static int setSpinner(String []data,String value)
    {
        for(int i=0;i<data.length;i++)
        {
            if(data[i].equals(value))
                return i;
        }
        return -1;
    }
}
