package org.jetlinks.protocol.common.mapping;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.DeviceMessageReply;
import org.jetlinks.core.message.event.EventMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessageReply;
import org.jetlinks.core.message.function.FunctionParameter;
import org.jetlinks.core.message.property.ReportPropertyMessage;

import java.util.List;

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

    private String  thingKey;

    private String  thingValue;

    protected ThingAnnotation(String thingKey, String thingValue) {
        this.thingKey = thingKey;
        this.thingValue = thingValue;
    }

    public Object invokeGetter(ThingContext context, DeviceMessage msg, String itemKey) {
        return null;
    }

    public void invokeSetter(ThingContext context, DeviceMessage msg, String itemKey, Object itemVal) {

    }

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
}
