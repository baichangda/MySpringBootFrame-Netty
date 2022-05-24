package com.bcd.protocol.gb32960.parser;

import com.bcd.support_parser.impl.gb32960.Parser_gb32960;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ParserConfig {

    @Bean("parser_gb32960")
    public Parser_gb32960 parser_gb32960() {
        final Parser_gb32960 parser_gb32960 = new Parser_gb32960(false);
        parser_gb32960.init();
        return parser_gb32960;
    }

    public static void main(String[] args) {
        Parser_gb32960 parser = new Parser_gb32960(false);
        parser.init();
        parser.performanceTest("232302fe4c534a4532343036364a4732323935383901011f14090812110c01020101000000031fec0e8026bb45021013dd0000050007381f0701d8cc8c06013e0f20010c0f1701054e01074c070000000000000000000801010e8126bb00600001600f1b0f1b0f190f1a0f1a0f1a0f1a0f180f1a0f1a0f1b0f170f1b0f1b0f1b0f1c0f190f1a0f1a0f1b0f1a0f1a0f1a0f1a0f1a0f1a0f1a0f190f1a0f190f1b0f1a0f1b0f1a0f190f1a0f1b0f1b0f1a0f1b0f1a0f1d0f1a0f1a0f1b0f1c0f1d0f1d0f1c0f1b0f1b0f1c0f1a0f1d0f1d0f1c0f1d0f1d0f1b0f1b0f1a0f200f1d0f1a0f1a0f1a0f1a0f1b0f1b0f1b0f1a0f1c0f1b0f1a0f1a0f1a0f1b0f1e0f1d0f1b0f1c0f1d0f1d0f1d0f1d0f1c0f1b0f1b0f1d0f1c0f1c0f1c0f1c0f1d0f1b0f1d09010100104d4d4d4d4e4d4c4c4d4c4d4d4d4d4d4c2a",
                1, true);
    }
}
