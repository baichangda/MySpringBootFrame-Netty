package com.bcd.nettyserver.tcp.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class PacketSplitHandler extends ByteToMessageDecoder {

    Logger logger= LoggerFactory.getLogger(PacketSplitHandler.class);

    private byte[] header;
    //include
    private int lengthFieldStart;
    //exclude
    private int lengthFieldEnd;
    private int lengthFieldLength;

    public PacketSplitHandler(byte[] header,int lengthFieldStart,int lengthFieldLength) {
        this.header=header;
        this.lengthFieldStart=lengthFieldStart;
        this.lengthFieldLength=lengthFieldLength;
        this.lengthFieldEnd=lengthFieldStart+lengthFieldLength+1;
    }
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while(true) {
            //1、开始检测报文头(最小长度为 内容为0 再加上异或校验位)
            if (!checkReadableLength(in)) {
                break;
            }
            //2、检测报文头
            byte[] temp = new byte[header.length];
            int readLen = header.length;
            in.getBytes(0, temp);
            boolean findHeader = true;
            while (true) {
                //2.1、循环检测
                for (int i = 0; i <= header.length - 1; i++) {
                    if (header[i] != temp[i]) {
                        findHeader = false;
                        break;
                    }
                }
                //2.2、如果检测通过,则跳出循环
                if (findHeader) {
                    break;
                } else {
                    if (readLen < in.readableBytes()) {
                        //2.3、检测不通过且有可读的字节,则把检测数组向前移动一位,最后一位填充读出来的新字节
                        for (int i = 0; i <= temp.length - 2; i++) {
                            temp[i] = temp[i + 1];
                        }
                        temp[temp.length - 1] = in.getByte(readLen++);
                    } else {
                        //2.4、此时说明不包含完整头,不过有可能最后一部分包含部分头,此时保留头长度-1的报文,其他丢弃掉
                        in.readBytes(readLen - header.length + 1);
                        in.discardReadBytes();
                        break;
                    }
                }
            }
            if (!findHeader) {
                break;
            }
            //3、找到头则废弃之前的无用字节
            if (readLen > header.length) {
                in.readBytes(readLen - header.length);
                in.discardReadBytes();
            }
            //4、此时开始正式拆解报文
            //4.1、再次检测余下的长度是否满足解析需要
            if (!checkReadableLength(in)) {
                break;
            }
            //4.2、读取长度字段
            long len = getUnadjustedFrameLength(in, lengthFieldStart, lengthFieldLength);
            //4.3、读取完整的报文
            int readLength = (int) (lengthFieldEnd + len);
            if (readLength <= in.readableBytes()) {
                ByteBuf res = in.readBytes(readLength);
                in.discardReadBytes();
                //4.3.1检查异或
                boolean checkRes = checkXor(res);
                if (checkRes) {
                    out.add(res);
                }else {
                    logger.warn("xor error,[{}]", ByteBufUtil.hexDump(res));
                }
            } else {
                break;
            }
        }
    }

    private boolean checkReadableLength(ByteBuf in){
        return in.readableBytes()>=lengthFieldEnd;
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
        public Default(byte[] header, int lengthFieldStart, int lengthFieldLength) {
            super(header, lengthFieldStart, lengthFieldLength);
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
