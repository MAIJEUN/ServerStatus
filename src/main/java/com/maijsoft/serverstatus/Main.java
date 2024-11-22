package com.maijsoft.serverstatus;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements CommandExecutor {

    private final SystemInfo systemInfo = new SystemInfo();

    @Override
    public void onEnable() {
        this.getCommand("status").setExecutor(this);
        getLogger().info("ServerStatusPlugin 활성화!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ServerStatusPlugin 비활성화!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("status")) {
            sender.sendMessage("§a[서버 정보]");
            sender.sendMessage("  §7서버 버전: §f" + Bukkit.getBukkitVersion());
            sender.sendMessage("  §7Paper 버전: §f" + Bukkit.getVersion());

            sender.sendMessage("\n§a[서버 상태]");

            // TPS
            double[] recentTps = Bukkit.getTPS();
            sender.sendMessage("  §7TPS: §f" +
                    String.format("%.2f, %.2f, %.2f", recentTps[0], recentTps[1], recentTps[2]));

            // CPU 사용량
            double cpuLoad = getSystemCpuLoad();
            sender.sendMessage("  §7CPU 사용량: §f" + String.format("%.2f%%", cpuLoad));

            // 메모리 상태
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory() / (1024 * 1024);
            long totalMemory = runtime.totalMemory() / (1024 * 1024);
            long freeMemory = runtime.freeMemory() / (1024 * 1024);
            long usedMemory = totalMemory - freeMemory;

            sender.sendMessage("  §7최대 메모리: §f" + maxMemory + " MB");
            sender.sendMessage("  §7할당된 메모리: §f" + totalMemory + " MB");
            sender.sendMessage("  §7메모리 사용량: §f" + usedMemory + " MB");

            // 월드별 정보
            double highestLoadScore = 0;
            String highestLoadWorld = "";
            for (World world : Bukkit.getWorlds()) {
                sender.sendMessage("\n§b[월드 '" + world.getName() + "' 정보]");

                int entityCount = world.getEntities().size();
                int loadedChunks = world.getLoadedChunks().length;

                // 추정 메모리 사용량 및 부하 점수 계산
                long estimatedWorldMemory = (entityCount * 500 + loadedChunks * 100) / 1024; // 추정치
                double worldLoadScore = entityCount * 1.5 + loadedChunks * 1.0;

                sender.sendMessage("  §7엔티티 수: §f" + entityCount);
                sender.sendMessage("  §7로딩된 청크 수: §f" + loadedChunks);
                sender.sendMessage("  §7추정 메모리 사용량: §f" + estimatedWorldMemory + " MB");
                sender.sendMessage("  §7부하 점수: §f" + String.format("%.2f", worldLoadScore));

                // 가장 부하가 큰 월드 탐색
                if (worldLoadScore > highestLoadScore) {
                    highestLoadScore = worldLoadScore;
                    highestLoadWorld = world.getName();
                }
            }

            // 가장 부하가 큰 월드 출력
            sender.sendMessage("\n§a[서버 상태]");
            sender.sendMessage("  §7가장 부하가 큰 월드: §f" + (highestLoadWorld.isEmpty() ? "없음" : highestLoadWorld));
            return true;
        }
        return false;
    }

    // OSHI를 사용한 CPU 사용량 측정
    private double getSystemCpuLoad() {
        CentralProcessor processor = systemInfo.getHardware().getProcessor();
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        try {
            Thread.sleep(100); // 100ms 대기 후 측정
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long[] currTicks = processor.getSystemCpuLoadTicks();
        return processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
    }
}
