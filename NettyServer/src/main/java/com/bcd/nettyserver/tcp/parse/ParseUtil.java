package com.bcd.nettyserver.tcp.parse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ParseUtil {
    public static double calcRPNWithDouble(List list, Map<String,Double> map){
        int stackIndex=-1;
        double[] stack=new double[list.size()];
        for (Object s : list) {
            if(s instanceof Double){
                stack[++stackIndex]=(double)s;
            }else {
                switch (s.toString()) {
                    case "+": {
                        double num2 = stack[stackIndex--];
                        double num1 = stack[stackIndex--];
                        stack[++stackIndex] = num1 + num2;
                        break;
                    }
                    case "-": {
                        double num2 = stack[stackIndex--];
                        double num1 = stack[stackIndex--];
                        stack[++stackIndex] = num1 - num2;
                        break;
                    }
                    case "*": {
                        double num2 = stack[stackIndex--];
                        double num1 = stack[stackIndex--];
                        stack[++stackIndex] = num1 * num2;
                        break;
                    }
                    case "/": {
                        double num2 = stack[stackIndex--];
                        double num1 = stack[stackIndex--];
                        stack[++stackIndex] = num1 / num2;
                        break;
                    }
                    default: {
                        Double val = map.get(s.toString());
                        stack[++stackIndex] = val;
                        break;
                    }
                }
            }
        }
        return stack[0];
    }

    public static double calcRPNWithInteger(List list, Map<String,Integer> map){
        int stackIndex=-1;
        int[] stack=new int[list.size()];
        for (Object s : list) {
            if(s instanceof Integer){
                stack[++stackIndex]=(int)s;
            }else {
                switch (s.toString()) {
                    case "+": {
                        int num2 = stack[stackIndex--];
                        int num1 = stack[stackIndex--];
                        stack[++stackIndex] = num1 + num2;
                        break;
                    }
                    case "-": {
                        int num2 = stack[stackIndex--];
                        int num1 = stack[stackIndex--];
                        stack[++stackIndex] = num1 - num2;
                        break;
                    }
                    case "*": {
                        int num2 = stack[stackIndex--];
                        int num1 = stack[stackIndex--];
                        stack[++stackIndex] = num1 * num2;
                        break;
                    }
                    case "/": {
                        int num2 = stack[stackIndex--];
                        int num1 = stack[stackIndex--];
                        stack[++stackIndex] = num1 / num2;
                        break;
                    }
                    default: {
                        Integer val = map.get(s.toString());
                        stack[++stackIndex] = val;
                        break;
                    }
                }
            }
        }
        return stack[0];
    }

    public static List doWithRpnListToInteger(List<String> rpnList){
        return rpnList.stream().map(e->{
            try {
                return Integer.parseInt(e);
            }catch (NumberFormatException ex){
                return e;
            }
        }).collect(Collectors.toList());
    }

    public static List doWithRpnListToDoubler(List<String> rpnList){
        return rpnList.stream().map(e->{
            try {
                return Double.parseDouble(e);
            }catch (NumberFormatException ex){
                return e;
            }
        }).collect(Collectors.toList());
    }
}
