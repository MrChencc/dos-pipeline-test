
import com.tod.CustomPipelineUtil


/**
 * --- 测试Json工具类 ---
 */
def conf = new File('file/customPipelineConfig.json').getText('UTF-8');
def json = CustomPipelineUtil.getJsonPipelineConfig(conf)
print(json)