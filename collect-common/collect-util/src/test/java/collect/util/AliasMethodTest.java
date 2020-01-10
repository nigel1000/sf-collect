package collect.util;

import com.common.collect.util.AliasMethod;

import java.util.Arrays;
import java.util.List;

/**
 * Created by hznijianfeng on 2020/1/10.
 */

public class AliasMethodTest {

    public static void main(String[] args) {
        List<Double> weights = Arrays.asList(100d, 300d, 200d, 340d, 480d);
        AliasMethod method = AliasMethod.fromWeights(weights);

        for (int i = 0; i < 10; i++) {
            System.out.println(weights.get(method.next()));
        }
    }

}
