package Utils;

import java.util.Random;

/**
 * Created by nami on 1/21/16.
 */
public class IdUtils {

    public static String getRandomMessageId(String prefix){
        Random random = new Random();
        int time = (int) Math.abs(System.currentTimeMillis() / 1000L);
        int randomNum = random.nextInt(time);
        return prefix + randomNum;
    }
}
