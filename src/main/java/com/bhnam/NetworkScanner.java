package com.bhnam;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class NetworkScanner {

    public ScanResult scan(String ip) {

        long start = System.currentTimeMillis();

        try {
            InetAddress address =
                    InetAddress.getByName(ip);

            boolean reachable =
                    address.isReachable(1000);

            long responseTime =
                    System.currentTimeMillis() - start;

            if (reachable) {

                String hostname;

                try {
                    hostname = address.getCanonicalHostName();
                } catch (Exception e) {
                    hostname = "-";
                }

                if (hostname.equals(ip)) {
                    hostname = "-";
                }

                String mac = getMacAddress(ip);

                return new ScanResult(
                        ip,
                        hostname,
                        mac,
                        "UP",
                        responseTime
                );
            }

            return new ScanResult(
                    ip,
                    "-",
                    "-",
                    "DOWN",
                    responseTime
            );

        } catch (Exception e) {

            return new ScanResult(
                    ip,
                    "-",
                    "-",
                    "ERROR",
                    -1
            );
        }
    }

    private String getMacAddress(String ip) {

        try {

            Process process =
                    Runtime.getRuntime().exec(
                            new String[]{
                                    "arp",
                                    "-a",
                                    ip
                            }
                    );

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    process.getInputStream()
                            )
                    );

            String line;

            while ((line = reader.readLine()) != null) {

                line = line.trim();

                if (line.contains(ip)) {

                    String[] parts =
                            line.split("\\s+");

                    for (String part : parts) {

                        if (part.matches(
                                "(?i)[0-9a-f]{2}(-[0-9a-f]{2}){5}"
                        )) {

                            return part
                                    .toUpperCase();
                        }
                    }
                }
            }

            process.waitFor();

        } catch (Exception ignored) {
        }

        return "-";
    }

    public record ScanResult(
            String ip,
            String hostname,
            String mac,
            String status,
            long responseTime
    ) {
    }
}