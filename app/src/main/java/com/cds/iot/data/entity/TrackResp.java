package com.cds.iot.data.entity;

import java.util.List;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/10/17 16:19
 */
public class TrackResp {

    /**
     * device_id : 2
     * track_path : [{"address":"广东省深圳市福田区沙头街道泰然九路1-2号天地源盛唐大厦","location":"114.024408908421,22.530108235678","time":"1533084947000"},{"address":"广东省深圳市福田区沙头街道泰然九路1-2号天地源盛唐大厦","location":"114.024437120226,22.530124782987","time":"1533084962000"},{"address":"广东省深圳市福田区沙头街道泰然九路海松大厦","location":"114.024883355035,22.530434570313","time":"1533084977000"},{"address":"广东省深圳市福田区沙头街道泰然八路天地源盛唐大厦","location":"114.02469780816,22.530193956164","time":"1533085009000"},{"address":"广东省深圳市福田区沙头街道盛唐大厦东座天地源盛唐大厦","location":"114.024412163629,22.52997639974","time":"1533085024000"},{"address":"广东省深圳市福田区沙头街道盛唐大厦东座天地源盛唐大厦","location":"114.024356825087,22.529889865452","time":"1533085039000"},{"address":"广东省深圳市福田区沙头街道盛唐大厦东座天地源盛唐大厦","location":"114.024291992188,22.530029568143","time":"1533085101000"}]
     */

    private String device_id;
    private List<TrackPath> track_path;

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public List<TrackPath> getTrack_path() {
        return track_path;
    }

    public void setTrack_path(List<TrackPath> track_path) {
        this.track_path = track_path;
    }
}
