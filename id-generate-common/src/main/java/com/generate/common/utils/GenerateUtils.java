package com.generate.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Slf4j
public class GenerateUtils {
    /**
     * 获取已激活网卡的IP地址
     *
     * @param interfaceName 可指定网卡名称,null则获取全部
     * @return List<String>
     */
    public static List<String> getHostAddress(String interfaceName) throws SocketException {
        List<String> ipList = new ArrayList<>(5);
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            Enumeration<InetAddress> allAddress = networkInterface.getInetAddresses();
            String networkInterfaceName = networkInterface.getName();
            while (allAddress.hasMoreElements()) {
                InetAddress address = allAddress.nextElement();
                log.info("网卡名称: {},网卡IP:{}", networkInterfaceName, address.getHostAddress());
                if (address.isLoopbackAddress()) {
                    // skip the loopback addr
                    continue;
                }

                if (address instanceof Inet6Address) {
                    // skip the IPv6 addr
                    continue;
                }
                String hostAddress = address.getHostAddress();
                if(interfaceName.equalsIgnoreCase(networkInterfaceName)){
                    ipList.add(hostAddress);
                }
            }

        }
        return ipList;
    }
}
