package com.socket.org.join.ws.serv.view;

import com.socket.org.join.ws.Constants;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//import com.socket.net.asfun.jangod.template.TemplateEngine;

/**
 * @brief 模板处理器
 * @author join
 */
public class TempHandler {
//
//    /* package */ static final TemplateEngine engine;
//
//    static {
//        engine = new TemplateEngine();
//        /* 设定模板目录 */
//        engine.getConfiguration().setWorkspace(Constants.Config.SERV_TEMP_DIR);
//        /* 设定全局变量 */
//        Map<String, Object> globalBindings = new HashMap<String, Object>();
//        globalBindings.put("SERV_ROOT_DIR", Constants.Config.SERV_ROOT_DIR);
//        engine.setEngineBindings(globalBindings);
//    }

    /**
     * 渲染模板，获得html
     */
    public static String render(String tempFile, Map<String, Object> data) throws IOException {
//        return engine.process(tempFile, data);
        return  "542";
    }

}
