package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.*;
import java.util.concurrent.TimeUnit;

//LRU算法
public class TokenCache {

    public static final String TOKEN_PREFIX="token_";

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(TokenCache.class);
    //缓存区初始值1000，最大是10000，一旦超过就会调用LRU算法进行清除替换。其有效期为12个小时
    private static LoadingCache<String,String> localCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS)
            .build(new CacheLoader<String, String>() {
                //默认的数据加载实现类，当调用get取值时，取不到对应key保存的值，就调用这个方法
                @Override
                public String load(String s) throws Exception{
                    return "null";
                }
            });
    public static void setKey(String key,String value){
        localCache.put(key,value);
    }

    public static String getKey(String key){
        String value=null;
        try{
            value=localCache.get(key);
            if("null".equals(value)){
                return null;
            }
            return value;
        }catch (Exception e){
            logger.error("localCache get error",e);
        }
        return null;
    }


}
