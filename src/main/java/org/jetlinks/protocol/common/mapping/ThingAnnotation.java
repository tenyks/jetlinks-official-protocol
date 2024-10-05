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
import org.jetlinks.core.message.event.ThingEventMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessageReply;
import org.jetlinks.core.message.function.FunctionParameter;
import org.jetlinks.core.message.property.ReportPropertyMessage;
import org.jetlinks.core.message.request.DeviceRequestMessage;
import org.jetlinks.core.message.request.DeviceRequestMessageReply;
import reactor.util.function.Tuple2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                } else if (msg instanceof DeviceRequestMessage<?>) {
                    return ((DeviceRequestMessage<?>) msg).getFunctionId();
                } else if (msg instanceof DeviceRequestMessageReply) {
                    return ((DeviceRequestMessageReply) msg).getFunctionId();
                } else if (msg instanceof ThingEventMessage) {
                    return ((ThingEventMessage) msg).getEvent();
                }

                return null;
            }

            @Override
            public void invokeSetter(ThingContext context, DeviceMessage msg, String itemKey, Object itemVal) {
                if (msg instanceof FunctionInvokeMessage) {
                    ((FunctionInvokeMessage) msg).functionId(thingValue);
                } else if (msg instanceof FunctionInvokeMessageReply) {
                    ((FunctionInvokeMessageReply) msg).functionId(thingValue);
                } else if (msg instanceof DeviceRequestMessage<?>) {
                    ((DeviceRequestMessage<?>) msg).functionId(thingValue);
                } else if (msg instanceof DeviceRequestMessageReply) {
                    ((DeviceRequestMessageReply) msg).functionId(thingValue);
                } else if (msg instanceof ThingEventMessage) {
                    ((ThingEventMessage) msg).event(thingValue);
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

    public static ThingAnnotation FuncInput(final ThingValueNormalization<Byte> norm) {
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
                        return norm.apply(fParam.getValue());
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


    public static ThingAnnotation DevReqInput() {
        return new ThingAnnotation("inputs", null) {
            @Override
            public Object invokeGetter(ThingContext context, DeviceMessage msg, String itemKey) {
                if (itemKey == null) return null;

                DeviceRequestMessage<?> fiMsg = (DeviceRequestMessage<?>)msg;

                return fiMsg.getInput(itemKey);
            }

            @Override
            public void invokeSetter(ThingContext context, DeviceMessage msg, String itemKey, Object itemVal) {
                if (itemKey == null) return ;

                DeviceRequestMessage<?> fiMsg = (DeviceRequestMessage<?>)msg;
                fiMsg.addInput(itemKey, itemVal);
            }
        };
    }

    public static ThingAnnotation DevReqInput(final ThingValueNormalization<Integer> norm) {
        return new ThingAnnotation("inputs", null) {
            @Override
            public Object invokeGetter(ThingContext context, DeviceMessage msg, String itemKey) {
                if (itemKey == null) return null;

                DeviceRequestMessage<?> fiMsg = (DeviceRequestMessage<?>)msg;

                return norm.apply(fiMsg.getInput(itemKey));
            }

            @Override
            public void invokeSetter(ThingContext context, DeviceMessage msg, String itemKey, Object itemVal) {
                if (itemKey == null) return ;

                DeviceRequestMessage<?> fiMsg = (DeviceRequestMessage<?>)msg;
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

    public static <T> ThingAnnotation FuncOutput(ThingValueNormalization<T> norm) {
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
                outputObj.put(itemKey, norm.apply(itemVal));
            }
        };
    }

    public static ThingAnnotation FuncOutput(ThingItemMapping<String> itemMapping) {
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

                itemMapping.bind(context.getStructInstance());
                List<Tuple2<String, String>> items = itemMapping.apply(itemKey, itemVal);
                for (Tuple2<String, String> item : items) {
                    outputObj.put(item.getT1(), item.getT2());
                }
            }
        };
    }


    public static ThingAnnotation DevReqReplyOutput() {
        return new ThingAnnotation("outputs", null) {
            @Override
            public Object invokeGetter(ThingContext context, DeviceMessage msg, String itemKey) {
                DeviceRequestMessageReply fiMsg = (DeviceRequestMessageReply)msg;

                if (itemKey == null) return fiMsg.getOutputs();

                return fiMsg.getOutput(itemKey);
            }

            @Override
            public void invokeSetter(ThingContext context, DeviceMessage msg, String itemKey, Object itemVal) {
                DeviceRequestMessageReply fiMsg = (DeviceRequestMessageReply)msg;

                JSONObject outputObj = fiMsg.getOutputs();
                if (outputObj == null) {
                    outputObj = new JSONObject();
                    fiMsg.setOutputs(outputObj);
                }
                outputObj.put(itemKey, itemVal);
            }
        };
    }

    public static <T> ThingAnnotation DevReqReplyOutput(ThingValueNormalization<T> norm) {
        return new ThingAnnotation("outputs", null) {
            @Override
            public Object invokeGetter(ThingContext context, DeviceMessage msg, String itemKey) {
                DeviceRequestMessageReply fiMsg = (DeviceRequestMessageReply)msg;

                if (itemKey == null) return fiMsg.getOutputs();

                JSONObject outputObject = fiMsg.getOutputs();
                if (outputObject == null) return null;

                return outputObject.get(itemKey);
            }

            @Override
            public void invokeSetter(ThingContext context, DeviceMessage msg, String itemKey, Object itemVal) {
                DeviceRequestMessageReply fiMsg = (DeviceRequestMessageReply)msg;

                JSONObject outputObj = fiMsg.getOutputs();
                if (outputObj == null) {
                    outputObj = new JSONObject();
                    fiMsg.setOutputs(outputObj);
                }
                outputObj.put(itemKey, norm.apply(itemVal));
            }
        };
    }

    public static ThingAnnotation DevReqReplyOutput(ThingItemMapping<String> itemMapping) {
        return new ThingAnnotation("outputs", null) {
            @Override
            public Object invokeGetter(ThingContext context, DeviceMessage msg, String itemKey) {
                DeviceRequestMessageReply fiMsg = (DeviceRequestMessageReply)msg;

                if (itemKey == null) return fiMsg.getOutputs();

                JSONObject outputObject = fiMsg.getOutputs();
                if (outputObject == null) return null;

                return outputObject.get(itemKey);
            }

            @Override
            public void invokeSetter(ThingContext context, DeviceMessage msg, String itemKey, Object itemVal) {
                DeviceRequestMessageReply fiMsg = (DeviceRequestMessageReply)msg;

                JSONObject outputObj = fiMsg.getOutputs();
                if (outputObj == null) {
                    outputObj = new JSONObject();
                    fiMsg.setOutputs(outputObj);
                }

                itemMapping.bind(context.getStructInstance());
                List<Tuple2<String, String>> items = itemMapping.apply(itemKey, itemVal);
                for (Tuple2<String, String> item : items) {
                    outputObj.put(item.getT1(), item.getT2());
                }
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

    public static <T> ThingAnnotation EventData(ThingValueNormalization<T> norm) {
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

                jsonObj.put(itemKey, norm.apply(itemVal));
            }
        };
    }

    public static ThingAnnotation EventData(ThingItemMapping<String> itemMapping) {
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

                itemMapping.bind(context.getStructInstance());
                List<Tuple2<String, String>> items = itemMapping.apply(itemKey, itemVal);
                for (Tuple2<String, String> item : items) {
                    jsonObj.put(item.getT1(), item.getT2());
                }
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

    public static <T> ThingAnnotation Property(ThingValueNormalization<T> normalization) {
        return new ThingAnnotation("properties", null) {
            @Override
            public void invokeSetter(ThingContext context, DeviceMessage msg, String itemKey, Object itemVal) {
                ReportPropertyMessage rpMsg = (ReportPropertyMessage) msg;

                if (rpMsg.getProperties() == null) rpMsg.setProperties(new HashMap<>());

                rpMsg.getProperties().put(itemKey, normalization.apply(itemVal));
            }

            @Override
            public Object invokeGetter(ThingContext context, DeviceMessage msg, String itemKey) {
                ReportPropertyMessage rpMsg = (ReportPropertyMessage) msg;

                if (rpMsg.getProperties() == null) return null;

                return rpMsg.getProperties().get(itemKey);
            }
        };
    }

    public static ThingAnnotation Property(ThingItemMapping<String> itemMapping) {
        return new ThingAnnotation("properties", null) {
            @Override
            public void invokeSetter(ThingContext context, DeviceMessage msg, String itemKey, Object itemVal) {
                ReportPropertyMessage rpMsg = (ReportPropertyMessage) msg;

                if (rpMsg.getProperties() == null) rpMsg.setProperties(new HashMap<>());

                Map<String, Object> properties = rpMsg.getProperties();
                itemMapping.bind(context.getStructInstance());
                List<Tuple2<String, String>> items = itemMapping.apply(itemKey, itemVal);
                for (Tuple2<String, String> item : items) {
                    properties.put(item.getT1(), item.getT2());
                }
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

    private boolean required = false;

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

    public boolean isRequired() {
        return required;
    }

    public ThingAnnotation setRequired(boolean required) {
        this.required = required;
        return this;
    }
}
