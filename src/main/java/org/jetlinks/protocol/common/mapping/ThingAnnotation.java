package org.jetlinks.protocol.common.mapping;

import com.alibaba.fastjson.JSONObject;
import me.tenyks.core.utils.ShortCodeGenerator;
import me.tenyks.core.utils.UuidRemapFactory;
import me.tenyks.core.utils.UuidRemapShort;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections.CollectionUtils;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.DeviceMessageReply;
import org.jetlinks.core.message.event.EventMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessageReply;
import org.jetlinks.core.message.function.FunctionParameter;
import org.jetlinks.core.message.property.ReportPropertyMessage;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public abstract class ThingAnnotation {
    public static ThingAnnotation MsgId() {
        return new ThingAnnotation("messageId", null) {
            @Override
            public Object invokeGetter(ThingContext context, DeviceMessage msg, String itemKey) {
                return msg.getMessageId();
            }

            @Override
            public void invokeSetter(ThingContext context, DeviceMessage msg, String itemKey, Object itemVal) {
                if (itemVal != null) {
                    msg.messageId(itemVal.toString());
                }
            }
        };
    }

    /**
     * 映射为UINT16的消息流水号
     */
    public static ThingAnnotation MsgIdUint16() {
        return new ThingAnnotation("messageId", null) {

            @Override
            public Object invokeGetter(ThingContext context, DeviceMessage msg, String itemKey) {
                String msgId = msg.getMessageId();

                UuidRemapShort uuidRemap = UuidRemapFactory.DEF_INST.createOrGet(msg.getDeviceId());

                return uuidRemap.shrink(msgId);
            }

            @Override
            public void invokeSetter(ThingContext context, DeviceMessage msg, String itemKey, Object itemVal) {
                if (itemVal != null) {
                    Short   shortMsgId = (itemVal instanceof Short ? (Short) itemVal : ((Integer)itemVal).shortValue());
                    String  msgId = UuidRemapFactory.DEF_INST.createOrGet(msg.getDeviceId()).recovery(shortMsgId);
                    if (msgId == null) {
                        msgId = String.format("%s_%d", ShortCodeGenerator.INSTANCE.next(), shortMsgId);
                    }

                    msg.messageId(msgId);
                }
            }
        };
    }
    public static ThingAnnotation ServiceId(String thingValue) {
        return new ThingAnnotation("serviceId", thingValue) {
            @Override
            public Object invokeGetter(ThingContext context, DeviceMessage msg, String itemKey) {
                if (msg instanceof FunctionInvokeMessage) {
                    return ((FunctionInvokeMessage) msg).getFunctionId();
                } else if (msg instanceof FunctionInvokeMessageReply) {
                    return ((FunctionInvokeMessageReply) msg).getFunctionId();
                }

                return null;
            }

            @Override
            public void invokeSetter(ThingContext context, DeviceMessage msg, String itemKey, Object itemVal) {
                if (msg instanceof FunctionInvokeMessage) {
                    ((FunctionInvokeMessage) msg).functionId(thingValue);
                } else if (msg instanceof FunctionInvokeMessageReply) {
                    ((FunctionInvokeMessageReply) msg).functionId(thingValue);
                }
            }
        };
    }

    public static ThingAnnotation DeviceId() {
        return new ThingAnnotation("deviceId", null) {
            @Override
            public Object invokeGetter(ThingContext context, DeviceMessage msg, String itemKey) {
                return msg.getDeviceId();
            }

            @Override
            public void invokeSetter(ThingContext context, DeviceMessage msg, String itemKey, Object itemVal) {
                if (itemVal == null) return ;

                if (msg instanceof DeviceMessageReply) {
                    ((DeviceMessageReply) msg).deviceId(itemVal.toString());
                } else if (msg instanceof EventMessage) {
                    ((EventMessage) msg).setDeviceId(itemVal.toString());
                } else if (msg instanceof ReportPropertyMessage) {
                    ((ReportPropertyMessage) msg).setDeviceId(itemVal.toString());
                }
            }
        };
    }

    public static ThingAnnotation FuncInput() {
        return new ThingAnnotation("inputs", null) {
            @Override
            public Object invokeGetter(ThingContext context, DeviceMessage msg, String itemKey) {
                //TODO 优化性能

                if (itemKey == null) return null;

                FunctionInvokeMessage fiMsg = (FunctionInvokeMessage)msg;
                List<FunctionParameter> inputParams = fiMsg.getInputs();
                if (CollectionUtils.isEmpty(inputParams)) return null;

                for (FunctionParameter fParam : inputParams) {
                    if (itemKey.equals(fParam.getName())) {
                        return fParam.getValue();
                    }
                }

                return null;
            }

            @Override
            public void invokeSetter(ThingContext context, DeviceMessage msg, String itemKey, Object itemVal) {
                if (itemKey == null) return ;

                FunctionInvokeMessage fiMsg = (FunctionInvokeMessage)msg;
                fiMsg.addInput(itemKey, itemVal);
            }
        };
    }

    public static ThingAnnotation FuncOutput() {
        return new ThingAnnotation("output", null) {
            @Override
            public Object invokeGetter(ThingContext context, DeviceMessage msg, String itemKey) {
                FunctionInvokeMessageReply fiMsg = (FunctionInvokeMessageReply)msg;

                if (itemKey == null) return fiMsg.getOutput();

                JSONObject outputObject = (JSONObject) fiMsg.getOutput();
                if (outputObject == null) return null;

                return outputObject.get(itemKey);
            }

            @Override
            public void invokeSetter(ThingContext context, DeviceMessage msg, String itemKey, Object itemVal) {
                FunctionInvokeMessageReply fiMsg = (FunctionInvokeMessageReply)msg;

                if (itemKey == null) {
                    fiMsg.setOutput(itemVal);
                    return ;
                }

                JSONObject outputObj = (JSONObject) fiMsg.getOutput();
                if (outputObj == null) {
                    outputObj = new JSONObject();
                    fiMsg.setOutput(outputObj);
                }
                outputObj.put(itemKey, itemVal);
            }
        };
    }

    public static ThingAnnotation Event(String thingValue) {
        return new ThingAnnotation("event", thingValue) {
            @Override
            public Object invokeGetter(ThingContext context, DeviceMessage msg, String itemKey) {
                if (msg instanceof EventMessage) {
                    return ((EventMessage) msg).getEvent();
                }

                return null;
            }

            @Override
            public void invokeSetter(ThingContext context, DeviceMessage msg, String itemKey, Object itemVal) {
                if (msg instanceof EventMessage) {
                    ((EventMessage) msg).setEvent((String)itemVal);
                }
            }
        };
    }

    public static ThingAnnotation EventData() {
        return new ThingAnnotation("data", null) {
            @Override
            public Object invokeGetter(ThingContext context, DeviceMessage msg, String itemKey) {
                EventMessage eMsg = (EventMessage) msg;
                if (itemKey == null) return eMsg.getData();

                JSONObject jsonObj = (JSONObject)eMsg.getData();
                if (jsonObj == null) return null;

                return jsonObj.get(itemKey);
            }

            @Override
            public void invokeSetter(ThingContext context, DeviceMessage msg, String itemKey, Object itemVal) {
                EventMessage eMsg = (EventMessage) msg;

                if (itemKey == null) {
                    eMsg.setData(itemVal);
                    return ;
                }

                JSONObject jsonObj = (JSONObject)eMsg.getData();
                if (jsonObj == null) {
                    jsonObj = new JSONObject();
                    eMsg.setData(jsonObj);
                }

                jsonObj.put(itemKey, itemVal);
            }
        };
    }

    public static ThingAnnotation Property() {
        return new ThingAnnotation("properties", null) {
            @Override
            public void invokeSetter(ThingContext context, DeviceMessage msg, String itemKey, Object itemVal) {
                ReportPropertyMessage rpMsg = (ReportPropertyMessage) msg;

                if (rpMsg.getProperties() == null) rpMsg.setProperties(new HashMap<>());

                rpMsg.getProperties().put(itemKey, itemVal);
            }

            @Override
            public Object invokeGetter(ThingContext context, DeviceMessage msg, String itemKey) {
                ReportPropertyMessage rpMsg = (ReportPropertyMessage) msg;

                if (rpMsg.getProperties() == null) return null;

                return rpMsg.getProperties().get(itemKey);
            }
        };
    }

    private final String  thingKey;

    private final String  thingValue;

    /**
     * 赋值给物模型前做值的规一化处理，比如：数值类型转换
     */
    private Function<Object, ?> itemValNormalizeProcessor;

    protected ThingAnnotation(String thingKey, String thingValue) {
        this.thingKey = thingKey;
        this.thingValue = thingValue;
    }

    public abstract Object invokeGetter(ThingContext context, DeviceMessage msg, String itemKey);

    public abstract void invokeSetter(ThingContext context, DeviceMessage msg, String itemKey, Object itemVal);

    public void invokeSetter(ThingContext context, DeviceMessage msg) {
        invokeSetter(context, msg, thingKey, thingValue);
    }

    public void invokeSetter(ThingContext context, DeviceMessage msg, Object itemVal) {
        invokeSetter(context, msg, thingKey, itemVal);
    }

    public Object invokeGetter(ThingContext context, DeviceMessage msg) {
        return invokeGetter(context, msg, thingKey);
    }

    public String getThingKey() {
        return thingKey;
    }

    public String getThingValue() {
        return thingValue;
    }

    public Function<Object, ?> getItemValNormalizeProcessor() {
        return itemValNormalizeProcessor;
    }

    public void setItemValNormalizeProcessor(Function<Object, ?> itemValNormalizeProcessor) {
        this.itemValNormalizeProcessor = itemValNormalizeProcessor;
    }
}
