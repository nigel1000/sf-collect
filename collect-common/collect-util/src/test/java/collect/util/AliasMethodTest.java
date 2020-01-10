package collect.util;

import com.common.collect.util.AliasMethod;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by hznijianfeng on 2020/1/10.
 */

public class AliasMethodTest {

    public static void main(String[] args) {
        List<Integer> nums = Arrays.asList(100, 300, 200, 340, 480);
        Double total = nums.stream().mapToDouble((t) -> t).sum();
        List<Double> probabilities = nums.stream().map(num -> num / total).collect(Collectors.toList());
        AliasMethod method = new AliasMethod(probabilities);

        for (int i = 0; i < 10; i++) {
            System.out.println(nums.get(method.next()));
        }
    }

}
