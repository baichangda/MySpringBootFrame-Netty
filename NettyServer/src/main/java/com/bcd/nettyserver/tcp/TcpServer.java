package com.bcd.nettyserver.tcp;

import com.bcd.base.util.StringUtil;
import com.bcd.nettyserver.tcp.parse.ParserContext;

import javax.script.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TcpServer implements Runnable{
    protected int port;
    protected ParserContext parser;

    public TcpServer(int port, ParserContext parser) {
        this.port = port;
        this.parser=parser;
    }

    public int getPort() {
        return port;
    }

    public ParserContext getParser() {
        return parser;
    }

    public static void main(String[] args) throws ScriptException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
        String eval="a+b*((c-3*f+b*c)+d/2)";
        List<String> rpn= StringUtil.parseArithmeticToRPN(eval);
        Map<String,Number> map=new HashMap<>();
        map.put("a",1d);
        map.put("b",2d);
        map.put("c",-5d);
        map.put("d",4d);
        map.put("f",1d);
        long t1=System.currentTimeMillis();
        for(int i=1;i<=10000;i++){
            SimpleBindings bindings=new SimpleBindings();
            bindings.putAll(map);
            engine.eval(eval,bindings);
        }
        long t2=System.currentTimeMillis();
        for(int i=1;i<=10000;i++){
            StringUtil.calcRPN(rpn,map);
        }
        long t3=System.currentTimeMillis();
        System.out.println(t2-t1);
        System.out.println(t3-t2);
    }
}
