package com.ont.player.sample.network;

import android.text.TextUtils;

import com.ont.player.sample.def.IApiListener;
import com.ont.player.sample.def.IDataListener;
import com.ont.player.sample.def.IRequestDef;
import com.ont.player.sample.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by betali on 2018/8/1.
 */

public class PicRequest extends OntPlayerRequest {
    private final String TAG = "PicRequest";

    String device_id;
    String channel_id;
    IDataListener request_listener;

    public PicRequest(String api_key) {
        super(api_key);
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public void setRequest_listener(IDataListener request_listener) {
        this.request_listener = request_listener;
    }

    @Override
    String getRequestType() {

        return IRequestDef.IRequestTypeDef.GET;
    }

    @Override
    String getRequestUrl() {

        return String.format(IRequestDef.IRequestUrlDef.API_URL + "/ipc/picture/get_list?device_id=%s&channel_id=%s", device_id, channel_id);
    }

    @Override
    String getRequestBody() {

        return null;
    }

    @Override
    void initListener() {

        api_listner = new IApiListener() {
            @Override
            public void onComplete(boolean success, String response) {

                LogUtil.i(TAG, "response:" + response);

                if (!success) {

                    request_listener.onComplete(IRequestDef.IRequestResultDef.ERR_NETWORK, 0, response);
                    return;
                }

                if(TextUtils.isEmpty(response)){

                    if(null != request_listener) {
                        request_listener.onComplete(IRequestDef.IRequestResultDef.ERR_NULL, 0, "返回空");
                    }
                    return;
                }
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if(null != jsonObject){

                        if(0 != jsonObject.optInt("errno")){

                            if(null != request_listener) {

                                request_listener.onComplete(jsonObject.optInt("errno"), 0, "指令发送失败:" + jsonObject.optString("error"));
                            }
                        }else {

                            String data = jsonObject.optString("data");
                            if(null != request_listener) {

                                request_listener.onComplete(IRequestDef.IRequestResultDef.ERR_OK, 0, data);
                            }
                        }
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        };
    }
}
