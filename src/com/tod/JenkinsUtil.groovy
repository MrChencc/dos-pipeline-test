package com.tod

/**
 * 用来替代Jenkins不能实现的功能
 */
class JenkinsUtil {
    static boolean isMap(def obj) {
        try {
            obj.keySet()
            return true;
        } catch (Throwable ignore) {
            return false;
        }
    }

    /**
     * 将Map递归的转化为可以序列化的HashMap，用作Jenkins的传值
     * @param oldVal
     * @return
     */
    static def tryTransMap(def oldVal) {
        // jenkins 不让用instanceof
        if (isMap(oldVal)) {
            return oldVal
        }
        String[] mapKey = ((Map)oldVal).keySet()
        Map newMap = new HashMap();
        for (String mKey : mapKey) {
            newMap.put(mKey, tryTransMap(oldVal.get(mKey)))
        }
        return newMap
    }
}
