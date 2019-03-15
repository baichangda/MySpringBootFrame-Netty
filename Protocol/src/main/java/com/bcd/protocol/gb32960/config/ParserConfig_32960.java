package com.bcd.protocol.gb32960.config;

import com.bcd.nettyserver.tcp.parse.Parser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParserConfig_32960 {
    @Bean("parser_32960")
    public Parser parser(){
        return new Parser();
    }
}
