
import com.tod.CostomPipelineUtil


/**
 * --- 测试Json工具类 ---
 */
def conf = new File('file/costomPipelineConfig.json').getText('UTF-8');
def json = CostomPipelineUtil.getJsonPipelineConfig(conf)
print(json)