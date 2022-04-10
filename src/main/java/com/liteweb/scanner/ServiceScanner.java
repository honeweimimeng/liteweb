package com.liteweb.scanner;

import com.liteweb.context.LiteWebContext;
import com.liteweb.register.inter.Registry;
import com.liteweb.service.BaseService;

public abstract class ServiceScanner {
    private static final Registry<BaseService> registry= LiteWebContext.getInstance().getServiceRegister();

    public abstract void Deploy(BaseService... service);

    /**
     * 服务注册器
     * @param name 服务名称
     * @param service 服务实体
     */
    void register(String name,BaseService service){
        registry.Register(name,service);
    }
}
