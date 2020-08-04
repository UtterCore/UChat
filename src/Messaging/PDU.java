package Messaging;

import org.json.simple.JSONObject;

public abstract class PDU {
    public int type;

    public abstract JSONObject toJSON();
}
