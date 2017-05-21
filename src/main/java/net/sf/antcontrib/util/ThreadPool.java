/*
 * Copyright (c) 2001-2004 Ant-Contrib project.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.antcontrib.util;

/**
 * @author <a href="mailto:mattinger@yahoo.com">Matthew Inger</a>
 */
public class ThreadPool {
    /**
     * Field maxActive.
     */
    private final int maxActive;

    /**
     * Field active.
     */
    private int active;

    /**
     * Constructor for ThreadPool.
     *
     * @param maxActive int
     */
    public ThreadPool(int maxActive) {
        super();
        this.maxActive = maxActive;
        this.active = 0;
    }

    /**
     * Method returnThread.
     *
     * @param thread ThreadPoolThread
     */
    public void returnThread(ThreadPoolThread thread) {
        synchronized (this) {
            active--;
            notify();
        }
    }

    /**
     * Method borrowThread.
     *
     * @return ThreadPoolThread
     * @throws InterruptedException if a thread is interrupted
     */
    public ThreadPoolThread borrowThread() throws InterruptedException {
        synchronized (this) {
            if (maxActive > 0 && active >= maxActive) {
                wait();
            }

            active++;
            return new ThreadPoolThread(this);
        }
    }
}
