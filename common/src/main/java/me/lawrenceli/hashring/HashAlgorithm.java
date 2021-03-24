package me.lawrenceli.hashring;

/**
 * @author lawrence
 * @since 2021/3/23
 */
public interface HashAlgorithm {

    /**
     * @param key to be hashed
     * @return hash value
     */
    long hash(String key);
}
