package collect.debug.docs.demo;

import com.common.collect.container.docs.JsonComment;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by hznijianfeng on 2019/5/22.
 */

@Data
public class RetDemo implements Serializable {

    private int id;
    @JsonComment(desc = "姓名")
    private String name;
    @JsonComment(desc = "1:未审核，2:已审核", required = true)
    private int status = 2;

}
