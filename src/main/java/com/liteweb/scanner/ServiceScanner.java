package com.liteweb.scanner;

import com.liteweb.context.LiteWebContext;
import com.liteweb.register.inter.Registry;
import com.liteweb.service.BaseService;

/**
 * @author Hone
 */
public abstract class ServiceScanner {
    private static final Registry<BaseService> REGISTRY = LiteWebContext.getInstance().getServiceRegister();

    /**
     * 扫描服务
     * @param service 服务列表
     */
    public abstract void deploy(BaseService... service);

    /**
     * 服务注册器
     * @param name 服务名称
     * @param service 服务实体
     */
    void register(String name,BaseService service){
        REGISTRY.register(name,service);
    }
}
