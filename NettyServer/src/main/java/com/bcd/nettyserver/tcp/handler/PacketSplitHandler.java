package com.bcd.nettyserver.tcp.handler;

import com.bcd.nettyserver.tcp.info.PacketInfo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class PacketSplitHandler extends ByteToMessageDecoder {

    Logger logger= LoggerFactory.getLogger(PacketSplitHandler.class);

    private byte[] header;
    private int lengthFieldStart;
    private int lengthFieldEnd;
    private int lengthFieldLength;

    public PacketSplitHandler(byte[] header,int lengthFieldStart,int lengthFieldEnd) {
        this.header=header;
        this.lengthFieldStart=lengthFieldStart;
        this.lengthFieldEnd=lengthFieldEnd;
        this.lengthFieldLength=lengthFieldEnd-lengthFieldStart;
    }
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Object decoded = decode(ctx, in);
        if (decoded != null) {
            out.add(decoded);
        }
    }

    private boolean checkReadableLength(ByteBuf in){
        return in.readableBytes()>=(lengthFieldEnd+1);
    }

    private Object decode(ChannelHandlerContext ctx, ByteBuf in) {
        //1、开始检测报文头(最小长度为 内容为0 再加上异或校验位)
        if(!checkReadableLength(in)){
            return null;
        }
        //2、检测报文头
        byte[] temp=new byte[header.length];
        int readIndex=header.length;
        in.getBytes(0,temp);
        while(true){
            //2.1、循环检测
            boolean checkRes=true;
            for(int i=0;i<=header.length-1;i++){
                if(header[i]!=temp[i]){
                    checkRes=false;
                    break;
                }
            }
            //2.2、如果检测通过,则跳出循环
            if(checkRes){
                break;
            }else{
                if(readIndex<in.readableBytes()){
                    //2.3、检测不通过且有可读的字节,则把检测数组向前移动一位,最后一位填充读出来的新字节
                    for(int i=0;i<=temp.length-2;i++){
                        temp[i]=temp[i+1];
                    }
                    temp[temp.length-1]=in.getByte(readIndex++);
                }else{
                    //2.4、此时说明此段报文中不包含头,不过有可能最后一部分包含部分头,此时保留头长度-1的报文,其他丢弃掉
                    in.readBytes((readIndex+1)-header.length+1).discardReadBytes();
                    return null;
                }
            }
        }
        //3、找到头则废弃之前的无用字节
        in.readBytes(readIndex-header.length);
        in.discardReadBytes();
        //4、此时开始正式拆解报文
        //4.1、再次检测余下的长度是否满足解析需要
        if(!checkReadableLength(in)){
            return null;
        }
        //4.2、读取长度字段
        long len=getUnadjustedFrameLength(in, lengthFieldStart, lengthFieldLength);
        //4.3、读取完整的报文
        int readLength=(int)(lengthFieldEnd+len+1);
        if(readLength<=in.readableBytes()){
            ByteBuf res= in.readBytes(readLength);
            in.discardReadBytes();
            boolean checkRes=checkXor(res);
            if(checkRes){
                return res;
            }else{
                return null;
            }
        }else{
            return null;
        }
    }


    protected abstract long getUnadjustedFrameLength(ByteBuf buf, int offset, int length) ;

    protected boolean checkXor(ByteBuf data){
        int len=data.readableBytes();
        byte temp=data.getByte(0);
        for(int i=1;i<=len-2;i++){
            temp^=data.getByte(i);
        }
        return temp==data.getByte(len-1);
    }

    public static class Default extends PacketSplitHandler{
        public Default(byte[] header, int lengthFieldStart, int lengthFieldEnd) {
            super(header, lengthFieldStart, lengthFieldEnd);
        }

        @Override
        protected long getUnadjustedFrameLength(ByteBuf buf, int offset, int length) {
            long frameLength;
            switch (length) {
                case 1:
                    frameLength = buf.getUnsignedByte(offset);
                    break;
                case 2:
                    frameLength = buf.getUnsignedShort(offset);
                    break;
                case 3:
                    frameLength = buf.getUnsignedMedium(offset);
                    break;
                case 4:
                    frameLength = buf.getUnsignedInt(offset);
                    break;
                case 8:
                    frameLength = buf.getLong(offset);
                    break;
                default:
                    throw new DecoderException(
                            "unsupported lengthFieldLength: " + length + " (expected: 1, 2, 3, 4, or 8)");
            }
            return frameLength;
        }
    }
    
}
