package nl.mwsoft.www.chatster.modelLayer.helper.util.uuid;


import java.util.UUID;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;

public class ChatsterUUIDUtil {

    public ChatsterUUIDUtil() {
    }

    public String createUUID() {
        return UUID.randomUUID().toString().replace(ConstantRegistry.CHATSTER_DASH,ConstantRegistry.CHATSTER_EMPTY_STRING);
    }
}
