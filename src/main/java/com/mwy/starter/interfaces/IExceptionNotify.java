package com.mwy.starter.interfaces;

import com.mwy.starter.model.ExchangeMessage;

/**
 * @author MaWuYing
 * @desc
 * @date 2021-03-08
 **/
public interface IExceptionNotify {

    void expsNotify(ExchangeMessage message);

    void expsCollect(ExchangeMessage message);
}
