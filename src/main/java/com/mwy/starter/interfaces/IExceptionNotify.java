package com.mwy.starter.interfaces;

import com.mwy.starter.model.ExchangeMessage;

/**
 * @author MaWuYing
 * @desc
 * @date 2021-03-08
 **/
public interface IExceptionNotify {

    /**
     * 异常通知
     * @param message
     */
    void expsNotify(ExchangeMessage message);

    /**
     * 异常统计
     * @param message
     */
    void expsCollect(ExchangeMessage message);
}
