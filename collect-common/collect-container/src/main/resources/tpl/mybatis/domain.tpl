package ${package};

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * Created by ${author} on ${date}.
 *
 * ${tableComment}
 */

@Data
@NoArgsConstructor
public class ${className} implements Serializable {

    private static final long serialVersionUID = 1L;

<#list fieldInfos as vo>
    /**
     * ${vo.memo}
     */
    private ${vo.type} ${vo.name};
    
</#list>

}