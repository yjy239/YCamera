package com.yjy.camera.bitmap;

import com.yjy.camera.Utils.Utils;

import java.util.Queue;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/04/20
 *     desc   :
 *     version: 1.0
 * </pre>
 */
abstract class BaseKeyPool<T extends Poolable> {
    private static final int MAX_SIZE = 20;
    private final Queue<T> keyPool = Utils.createQueue(MAX_SIZE);

    protected T get() {
        T result = keyPool.poll();
        if (result == null) {
            result = create();
        }
        return result;
    }

    public void offer(T key) {
        if (keyPool.size() < MAX_SIZE) {
            keyPool.offer(key);
        }
    }

    protected abstract T create();
}

