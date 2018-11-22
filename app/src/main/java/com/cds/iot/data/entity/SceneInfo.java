package com.cds.iot.data.entity;

import java.io.Serializable;
import java.util.List;

public class SceneInfo implements Serializable {

    private List<Scenes> scenes;
    private List<BindScenes> bind_scenes;
    private List<Banner> banners;

    public List<Scenes> getScenes() {
        return scenes;
    }

    public void setScenes(List<Scenes> scenes) {
        this.scenes = scenes;
    }

    public List<BindScenes> getBind_scenes() {
        return bind_scenes;
    }

    public void setBind_scenes(List<BindScenes> bind_scenes) {
        this.bind_scenes = bind_scenes;
    }

    public List<Banner> getBanners() {
        return banners;
    }

    public void setBanners(List<Banner> banners) {
        this.banners = banners;
    }
}
