package com.tod

/**
 * 用来替代Jenkins不能实现的功能
 */
class JenkinsUtil {
    /**
     * 传入对象是Map
     * @param obj 需判断的对象
     * @return ?是
     */
    static boolean isMap(def obj) {
        try {
            Map tryMap = (Map) obj;
            tryMap.keySet()
            return true;
        } catch (Throwable ignore) {
        }
        return false;
    }

    /**
     * 传入对象是List
     * @param obj 需判断的对象
     * @return ?是
     */
    static boolean isList(def obj) {
        try {
            List tryList = (List) obj
            if (tryList.size() >= 0) {
                return true;
            }
        } catch (Throwable ignore) {
        }
        return false;
    }

    /**
     * 将Map递归的转化为可以序列化的HashMap，用作Jenkins的传值
     * @param oldVal
     * @return
     */
    static def tryTransMap(def oldVal) {
        // jenkins 不让用instanceof
        if (!isMap(oldVal)) {
            return oldVal
        }
        String[] mapKey = ((Map) oldVal).keySet()
        Map newMap = new HashMap();
        for (String mKey : mapKey) {
            newMap.put(mKey, tryTransMap(oldVal.get(mKey)))
        }
        return newMap
    }
}
