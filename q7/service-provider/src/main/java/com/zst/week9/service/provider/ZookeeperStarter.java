package com.zst.week9.service.provider;

import com.zst.week9.service.provider.zookeeper.EmbeddedZooKeeper;

public class ZookeeperStarter {
    public static void main(String[] args) {
        // 先启动作为注册中心的ZooKeeper
        new EmbeddedZooKeeper(2181, false).start();
    }
}
