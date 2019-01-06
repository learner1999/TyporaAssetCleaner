package cn.zheteng123.typoraassetcleaner;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: TyporaAssetCleaner [path]");
            return;
        }

        String path = args[1];
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("路径不存在！");
            return;
        }

        if (!file.isDirectory()) {
            if (file.getName().endsWith(".md")) {
                cleanAssets(file);
            }
            return;
        }

        File[] files = file.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            if (f.getName().endsWith(".md")) {
                cleanAssets(f);
            }
        }
    }

    /**
     * 清理指定的 .md 文件的 asset 目录
     * @param f 指定的 .md 文件
     */
    private static void cleanAssets(File f) {
        if (!f.getName().endsWith(".md")) {
            return;
        }

        String name = f.getName();
        String nameWithoutSuffix = name.substring(0, name.lastIndexOf('.'));
        String parentPath = f.getParentFile().getAbsolutePath();
        String assetPath = parentPath + "/" + nameWithoutSuffix + ".assets";

        File assetDir = new File(assetPath);
        if (!assetDir.isDirectory()) {
            return;
        }
        List<String> assetNames = fetchUsedAssets(f);
        File[] assets = assetDir.listFiles();
        if (assets == null) {
            return;
        }
        for (File asset : assets) {
            if (!assetNames.contains(asset.getName())) {
                if (!asset.delete()) {
                    System.out.println(asset.getName() + " 删除失败！");
                }
            }
        }

    }

    /**
     * 找出该文件用到的图片资源
     * @param file 指定的 .md 文件
     * @return 用到的图片资源名字
     */
    private static List<String> fetchUsedAssets(File file) {
        List<String> assetNames = new ArrayList<>();
        if (file == null || !file.exists()) {
            return assetNames;
        }

        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        String pattern = "!\\[.+\\]\\((.+)\\)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(content);
        while (m.find()) {
            String path = m.group(1);
            String[] splits = path.split("/");
            if (splits.length > 0) {
                path = splits[splits.length - 1];
            }
            assetNames.add(path);
        }
        return assetNames;
    }
}
