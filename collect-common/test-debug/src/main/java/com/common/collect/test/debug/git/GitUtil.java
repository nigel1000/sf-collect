package com.common.collect.test.debug.git;

import com.common.collect.lib.util.EmptyUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by hznijianfeng on 2020/4/29.
 */

@Slf4j
public class GitUtil {

    public static List<String> getLocalBranches(@NonNull String baseDir) {
        try {
            List<String> branches = new ArrayList<>();
            // cmd.exe /k 执行完dir命令后不关闭命令窗口
            // cmd.exe /c 执行完dir命令后关闭命令窗口
            String cmd = "cmd.exe /c cd " + baseDir + " & git branch ";
            Process proc = process(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                branches.add(line.replace("*", "").trim());
            }
            br.close();
            return branches;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static List<String> getRemoteBranches(@NonNull String baseDir) {
        try {
            List<String> branches = new ArrayList<>();
            String cmd = "cmd.exe /c cd " + baseDir + " & git branch -r";
            Process proc = process(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("origin/HEAD")) {
                    continue;
                }
                branches.add(line.trim());
            }
            br.close();
            return branches;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void removeLocalBranch(@NonNull String baseDir, String branchName) {
        try {
            String cmd = "cmd.exe /c cd " + baseDir + " & git branch -D " + branchName;
            process(cmd);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void removeRemoteBranch(@NonNull String baseDir, String branchName) {
        try {
            // git push origin :
            // git push origin --delete
            String cmd = "cmd.exe /c cd " + baseDir + " & git push origin --delete " + branchName.replace("origin/", "");
            process(cmd);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static boolean hasMergeB2A(@NonNull String baseDir, String A, String B) {
        try {
            String cmd = "cmd.exe /c cd " + baseDir + " & git rev-parse " + B;
            Process proc = process(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8));
            String needMergedBranchCommitId;
            while ((needMergedBranchCommitId = br.readLine()) != null) {
                br.close();
                needMergedBranchCommitId = needMergedBranchCommitId.trim();
                break;
            }
            cmd = "cmd.exe /c cd " + baseDir + " & git merge-base " + B + " " + A;
            proc = process(cmd);
            br = new BufferedReader(new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8));
            String commonBranchCommitId;
            while ((commonBranchCommitId = br.readLine()) != null) {
                br.close();
                commonBranchCommitId = commonBranchCommitId.trim();
                break;
            }
            if (EmptyUtil.isEmpty(B) || EmptyUtil.isEmpty(commonBranchCommitId)) {
                return false;
            }
            return Objects.equals(needMergedBranchCommitId, commonBranchCommitId);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String logLastCommitTime(@NonNull String baseDir, String branchName) {
        try {
            String cmd = "cmd.exe /c cd " + baseDir + " & git show --pretty=format:\"%Cgreen%ci %cn\" " + branchName;
            Process proc = process(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                br.close();
                return line.trim();
            }
            //分支没有提交记录
            return null;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Process process(@NonNull String cmd) {
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            log.debug(cmd);
            return proc;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
