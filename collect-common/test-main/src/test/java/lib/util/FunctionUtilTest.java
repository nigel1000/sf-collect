package lib.util;

import com.common.collect.lib.util.FunctionUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by hznijianfeng on 2018/11/14.
 */

@Slf4j
public class FunctionUtilTest {

    public static void main(String[] args) {
        List<Object> result = FunctionUtil.batchFunctionResults(
                Arrays.asList(
                        list -> list.stream().collect(Collectors.summarizingInt(x -> x)),
                        list -> list.stream().filter(x -> x < 50).sorted().collect(Collectors.toList()),
                        list -> list.stream().collect(Collectors.groupingBy(x -> (x % 2 == 0 ? "even" : "odd"))),
                        list -> list.stream().sorted().collect(Collectors.toList()),
                        list -> list.stream().sorted().map(Math::sqrt).collect(Collectors.toMap(x -> x, y -> Math.pow(2, y)))),
                Arrays.asList(64, 49, 25, 16, 9, 4, 1, 81, 36));

        log.info("{}", result);
    }

}
